
/**
 * Author:    David Breton, dbreton@cs.sfu.ca

Copyright: You can do whatever you want with this application.
           The author is not liable for any loss caused by the
           use of this application.

Date:      November 2009

Version:   1.1  * Changed to permit update questions instead erasing them  jan 2013
	       0.8  * Now using Keywords instead of Subject,Category
           0.7  * Fix a bug that ignored MC5 type when creating flash files
           0.6  * A new answer type is recognized: MC5 :-((((((((
                * The selection process is now handled by the SmacUI interface
                  so that the interaction with the user is more flexible.
           0.5  * Added database interaction and config file
           0.4  * Fix parser bug when %Type=MC but %Answer > %Choices
           0.3  * Fix parser bug when comments appear after the last %End tag.
           0.2  * Fix parser bug that did not require last %End tag.
                * Made a lookup table of allowable subjects->categories
*/
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.sql.PooledConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class LatexToMeJ implements SmacUI
{
	public static final String VERSION = "1.1";
	private static final int SERVER_NAME=0;
	private static final int DB_NAME=1;
	private static final int USER=2;
	private static final int PASSWORD=3;
	//private static final int QUESTION_MASK = 0x1;
	// private static final int FEEDBACK_MASK = 0x2;
	private static final String LATEX_HEADER=
		"\\documentclass{article}\n" +
		"\\usepackage{amsfonts,amsmath,amssymb,amsthm,cancel,color,enumerate,fixltx2e,graphicx,hyperref,multirow,pstricks,pstricks-add,pst-math,pst-xkey,textcomp,wasysym,wrapfig}\n" +
		"\\usepackage[T1]{fontenc}\n" +
		"\\usepackage[french]{babel}\n" +
		"\\usepackage{ae,aecompl}\n" +
		"\\setlength{\\topmargin}{-2cm}\n" +
		"\\setlength{\\oddsidemargin}{-1cm}\n" +
		"\\setlength{\\parindent}{0pt}\n" +
		"\\pagestyle{empty}\n" +
		"\n" +
		"\\newenvironment{mej-enumerate}{\n" +
		"  \\begin{enumerate}[(A)]\n"+
		"    \\setlength{\\itemsep}{5pt}\n" +
		"    \\setlength{\\parskip}{0pt}\n" +
		"    \\setlength{\\parsep}{0pt}\n" +
		"}{\\end{enumerate}}\n" +
		"\\usepackage[utf8]{inputenc}\n" +
		"\\begin{document}\n" +
		"\\shorthandoff{:}\n";

	private static final Tag[] CHOICES = new Tag[]{
		Tag.ChoiceA,Tag.ChoiceB,Tag.ChoiceC,Tag.ChoiceD,
		Tag.ChoiceE,Tag.ChoiceF,Tag.ChoiceG,Tag.ChoiceH
	};

	private static final String LATEX_FOOTER = "\\begin{flushright}\\textcolor{white}{.}\\end{flushright}\\end{document}";	
	private static final String configFilename = "latextomej.ini";  
	private final Properties config;
	private final MysqlConnectionPoolDataSource mysqlDataSource;
	private final Map<Integer, ArrayList<String>> userInfoMap = new HashMap<Integer, ArrayList<String>>();
	private final Map<String, Integer> sourceMap = new HashMap<String, Integer>();
	private final Map<String, Integer> titleMap = new HashMap<String, Integer>();
	private SmacUI ui;
	private boolean useFirstUserMatch;
	// in current version it not overwrite but update the question in BD
	// did to keep statistics 
	private boolean overwriteFlashFiles;
	private boolean createZip;
	private final String flashFolderName;
	private final String tex2swfFolderName;
	private final String tex2swfTmpFolderName;
	private final String tex2swfBaseDir;
	private final String zipFilename;

	private final XMLWriter questionsWriter;
	//As we go through the questions we build a set of confirmed users, this way we often avoid looking into the set of all DB users (which can be very large)
	private final TreeMap<String,Integer> confirmedUsers;

	public LatexToMeJ() throws Exception
	{
		this(null, new String[]{"./", "tmp/", "flash/", "tmp/", "questions"}, null, false, false, true);
		this.ui = this;
	}
	public LatexToMeJ(String[] data, String[] tex2swfData, SmacUI ui, boolean alwaysUseFirstUser, boolean overwriteFlashFiles, boolean createZip) throws Exception
	{
		this.ui = ui;
		this.useFirstUserMatch = alwaysUseFirstUser;
		this.overwriteFlashFiles = overwriteFlashFiles;
		this.createZip = createZip;
		config = new Properties();
		
		try
		{
			InputStream conf = LatexToMeJ.class.getResourceAsStream(configFilename);
			config.load(new InputStreamReader(new BufferedInputStream(conf), "UTF-8"));
		} catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		//config.load(new InputStreamReader(new BufferedInputStream(LatexToMeJ.class.getResourceAsStream(configFilename)), "UTF-8"));
		//config.load(new InputStreamReader(new BufferedInputStream(new FileInputStream(configFilename)), "UTF-8"));
		if (data == null){
			data = new String[]{getProperty("db.mej.server"), getProperty("db.mej.name"), getProperty("db.mej.user"), getProperty("db.mej.password")};
		}else{
			ui.outputMessage("/rserver: " + data[0]);
		}
		//ui.outputMessage(getProperty("db.mej.server"));
		
		mysqlDataSource = createDataSource(data); //this does not attempt to connect, it merely sets the connection parameters.

		tex2swfFolderName = tex2swfData[0];
		tex2swfTmpFolderName = tex2swfData[1];
		flashFolderName = tex2swfData[2];
		tex2swfBaseDir = tex2swfData[3];
		zipFilename = tex2swfData[4];

		questionsWriter = new XMLWriter(ui);
		confirmedUsers = new TreeMap<String, Integer>();

		setUserMap();
		setSourceMap();
		setTitleMap();

	}

	private String getProperty(String name) throws Exception
	{
		String prop = config.getProperty(name);
		if (prop == null) throw new Exception("Configuration file property called: '" + name + "'");
		return prop.trim();
	}

	private MysqlConnectionPoolDataSource createDataSource(String[] data)
	{
		//MysqlDataSource ds = new MysqlDataSource();
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setServerName(data[SERVER_NAME]);
		ds.setDatabaseName(data[DB_NAME]);
		ds.setUser(data[USER]);
		ds.setPassword(data[PASSWORD]);
		ds.setEncoding("UTF-8");
		return ds;
	}


	private int executeUpdate(PreparedStatement stmt, String errorHint) throws Exception
	{
		try
		{
			return stmt.executeUpdate();
		}
		catch(SQLException sqle)
		{
			throw new Exception(errorHint + "\n" +
					"The offending query was:\n" + stmt + "\n" +
					"---Technical SQL error message----------\n" +
					"SQLException: " + sqle.getMessage() + "\n" +
					"SQLState: " + sqle.getSQLState() + "\n" + 
					"VendorError: " + sqle.getErrorCode() + "\n");
		}
	}

	private int executeUpdate(Statement stmt, String stSQL, String errorHint) throws Exception
	{
		try
		{
			return stmt.executeUpdate(stSQL);
		}
		catch(SQLException sqle)
		{
			throw new Exception(errorHint + "\n" +
					"The offending query was:\n" + stmt + "\n" +
					"---Technical SQL error message----------\n" +
					"SQLException: " + sqle.getMessage() + "\n" +
					"SQLState: " + sqle.getSQLState() + "\n" + 
					"VendorError: " + sqle.getErrorCode() + "\n");
			
		}
	}


	private int[] executeBatch(PreparedStatement stmt, String errorHint) throws Exception
	{
		try
		{
			return stmt.executeBatch();
		}
		catch(SQLException sqle)
		{
			throw new Exception(errorHint + "\n" +
					"The offending query was:\n" + stmt + "\n" +
					"---Technical SQL error message----------\n" +
					"SQLException: " + sqle.getMessage() + "\n" +
					"SQLState: " + sqle.getSQLState() + "\n" + 
					"VendorError: " + sqle.getErrorCode() + "\n");
		}
	}

	public static String shortLanguage(int language_id)
	{
		switch (language_id)
		{
		case 1: return "fr";
		case 2: return "en";
		default: return ""+language_id;
		}
	}
	private static String trueString(int language_id)
	{
		switch (language_id)
		{
		case 1: return "Vrai";
		case 2: return "True";
		default: return "True";
		}
	}
	private static String falseString(int language_id)
	{
		switch (language_id)
		{
		case 1: return "Faux";
		case 2: return "False";
		default: return "False";
		}
	}


	//This method looks in the 'user' table of the mej db and creates the following map:
	//   user_id  -----> [user_id, name, last_name, username]
	//   (integer)       (array of strings)
	//The user_id is guaranteed to be unique, so is the username.
	//The name and last_name are varchar(64), they can contain weird unicode characters.
	private void setUserMap() throws SQLException
	{
		Connection conn = null;
		userInfoMap.clear();
		try
		{
			conn = mysqlDataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT DISTINCT jos_comprofiler.id, firstname, lastname, jos_users.username " +
			"FROM jos_comprofiler, jos_users where jos_comprofiler.id = jos_users.id ORDER BY id;");
			while (rs.next())
			{
				int user_id = rs.getInt("id");
				ArrayList<String> userNames = new ArrayList<String>();
				userNames.add(rs.getString("id"));
				userNames.add(rs.getString("firstname"));
				userNames.add(rs.getString("lastname"));
				userNames.add(rs.getString("username"));
				userInfoMap.put(user_id, userNames);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException sqle)
		{
			try {conn.close();}catch(Exception e){};
			throw sqle;
		}
	}

	//This method looks in the 'source' table of the mej db and creates the following map:
	//   name    ---> source_id
	//   (string)     (integer)
	//The source_id is guaranteed to be unique, the name isn't (but should).  If the db
	//contains the same name more than once, only the first one will be stored in the map.
	//
	//The 'source' table was created for questions coming from math contests so that we could
	//credit the original source.
	//This program needs a map because the 'source' is frequently misspelled.  When a 'source'
	//isn't present in the map, the user will be presented with a list of possible spellings
	//and the option to create a new entry in the DB.
	private void setSourceMap() throws SQLException
	{
		Connection conn = null;
		sourceMap.clear();
		try
		{
			conn = mysqlDataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM source ORDER BY name");
			while (rs.next()){
				sourceMap.put(rs.getString("name"), rs.getInt("source_id"));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException sqle)
		{
			try {conn.close();}catch(Exception e){};
			throw sqle;
		}
	}

	//This method looks in the 'title' table of the mej db and creates the following map:
	//   name    ---> title_id
	//   (string)     (integer)
	//The title_id is guaranteed to be unique, the name isn't (but should).  If the db
	//contains the same name more than once, only the first one will be stored in the map.
	//
	//The 'title' table is used for two purposes :-(  For questions coming from math contest
	//it stores a more specific description of the exam in which the question was taken.
	//For other questions, it is a short 'descriptive' category for the question.
	//This program needs a map because the 'title' is frequently misspelled.  When a 'title'
	//isn't present in the map, the user will be presented with a list of possible spellings
	//and the option to create a new entry in the DB.
	private void setTitleMap() throws SQLException
	{
		Connection conn = null;
		titleMap.clear();
		try
		{
			conn = mysqlDataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM title ORDER BY name");
			while (rs.next()){
				titleMap.put(rs.getString("name"), rs.getInt("title_id"));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException sqle)
		{
			try {conn.close();}catch(Exception e){};
			throw sqle;
		}
	}

	private String createAvailableUserNameFrom(String baseString) throws Exception
	{
		int suffix = 2;
		Connection conn = null;
		try
		{
			conn = mysqlDataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT username FROM jos_users WHERE username like '" + baseString + "%'");
			TreeSet<Integer> usedSuffix = new TreeSet<Integer>();
			boolean baseStringAvailable = true;
			while (rs.next())
			{
				String username = rs.getString("username");
				if (username.equals(baseString)) baseStringAvailable = false;
				try
				{
					int i = Integer.parseInt(username.substring(baseString.length()));
					if (i>=suffix)
						usedSuffix.add(i);
				}
				catch(Exception e) {}
			}
			
			rs.close();
			stmt.close();
			conn.close();

			if (baseStringAvailable) return baseString;

			while(usedSuffix.contains(suffix))
				suffix++;

			return baseString + "" + suffix;

		} catch (SQLException sqle)
		{
			try {conn.close();}catch(Exception e){};
			throw sqle;
		}
	}

	//  !!!!!! to be changed 
	//The 'question_info' table of the MeJ DB requires a valid 'user' for its 'user_id' column.
	//If we try to add a question for which no valid 'user' exist (see getUserMap()), we will
	//create a new user and add it to the DB.
	//The new user will be created with the following parameters
	//  id = next one available
	//  firstname,lastname,username = taken from .tex file.  If only a username is specified we set firstname=lastname=username
	//  password = 7e891ec30f7954c5  (which is the hash for: "qcreator")
	//  province,country,email,email confirmation key = "N/A"
	//  inscription_date = today
	//  question_group_id = 2
	//  role_id = 2 (which means 'admin')
	//  language_id = computed from the %Language tag in the .tex file
	private int addUser(String name, int language_id, PreparedStatement pstmtInsertUser, PreparedStatement pstmtInsertJosCompro, PreparedStatement pstmtInsertJosCor, PreparedStatement pstmtInsertJosCorMap) throws Exception
	{
		StringTokenizer st;
		boolean usingComma = false;
		if (name.indexOf(",") >= 0)
		{
			st = new StringTokenizer(" " + name + " ",",");
			usingComma = true;
		}
		else
			st = new StringTokenizer(name);

		String username="";
		String firstname="";
		String lastname="";
		switch (st.countTokens())
		{
		case 0:
			throw new Exception(
					"You must specify a name when using the %Creator or %Translator tags");
		case 1:
			username = createAvailableUserNameFrom(st.nextToken().trim());
			firstname = username;
			lastname = username;
			break;
		case 2:
			if (!usingComma) {
				firstname = st.nextToken().trim();
				lastname = st.nextToken().trim();
				username = createAvailableUserNameFrom(firstname + " "
						+ lastname);
			} else {
				lastname = st.nextToken().trim();
				firstname = st.nextToken().trim();
				username = firstname + " " + lastname;
				username = createAvailableUserNameFrom(username.replaceAll(" ",
						"_"));
			}
			break;
		default:
			throw new Exception(
					"The name you supplied: '"
							+ name
							+ "' has more than 2 parts.  If your name contains a space you must use the format: Last name, First name");
		}

		pstmtInsertUser.setString(1, firstname);
		pstmtInsertUser.setString(2, username);
		pstmtInsertUser.setString(3, "N/A");
		pstmtInsertUser.setString(4, "cc513cd6325e7f050cf84a13b14d151f:DltwlsZGcKKpsqk4Xzzxmb0hl2V47dFr"); //password 
		pstmtInsertUser.setString(5, "Registered"); //usertype
		pstmtInsertUser.setInt(6, 18); //gid
		pstmtInsertUser.setDate(7, new java.sql.Date(System.currentTimeMillis())); //registeredDate
		pstmtInsertUser.setDate(8, new java.sql.Date(System.currentTimeMillis())); //lastvisitDate
		pstmtInsertUser.setString(9, ""); // activation
		pstmtInsertUser.setString(10, "language=\ntimezone=-8\n\n"); // params

		executeUpdate(pstmtInsertUser,"An SQL error 1 occured when trying to add a user to the DB.");

		ResultSet rs = pstmtInsertUser.getGeneratedKeys();
		rs.next();
		int userId = rs.getInt(1);
		rs.close();

		// second insert in jos_comprofiler

		pstmtInsertJosCompro.setInt(1, userId);
		pstmtInsertJosCompro.setInt(2, userId);
		pstmtInsertJosCompro.setString(3, firstname);
		pstmtInsertJosCompro.setString(4, lastname);
		pstmtInsertJosCompro.setString(5, "24.37.67.127");
		pstmtInsertJosCompro.setString(6, "");
		pstmtInsertJosCompro.setInt(7, 1);
		pstmtInsertJosCompro.setString(8, "Grade6");
		pstmtInsertJosCompro.setString(9, "Male");
		pstmtInsertJosCompro.setString(10, "UBC");

		executeUpdate(pstmtInsertJosCompro,"An SQL error 2 occured when trying to add a user to the DB.");

		//third insert for users
		pstmtInsertJosCor.setString(1, "users");
		pstmtInsertJosCor.setString(2, "" + userId);
		pstmtInsertJosCor.setString(3, firstname + " " + lastname);

		executeUpdate(pstmtInsertJosCor,"An SQL error 3 occured when trying to add a user to the DB.");
		ResultSet rscor = pstmtInsertJosCor.getGeneratedKeys();
		rscor.next();
		int corId = rscor.getInt(1);
		rscor.close();

		// last inserts
		pstmtInsertJosCorMap.setInt(1, 18);
		pstmtInsertJosCorMap.setString(2, "");
		pstmtInsertJosCorMap.setInt(3, corId);

		executeUpdate(pstmtInsertJosCorMap,"An SQL error 4 occured when trying to add a user to the DB.");

		ui.outputMessage("A new user with username='"+username+"' was added to the DB." + "\n");
		return userId;
	}

	//Look for user in the userInfoMap.  The input is the string taken from either the %Creator or the %Traductor tag.
	//1) If the string contains no ',' we assume it represents a username
	//2) If the string contains a ',' we assume that the part before the ',' is the first name and the part after the ',' is the last name
	//If no match is found, an empty list is returned.  Otherwise a list of all matches is returned.
	//If the list is not empty it can contain more than one element because the 'name' and 'last_name' are not necessarily unique.
	//However if a 'username' is specified the list will have at most one element.
	private ArrayList<Map.Entry<Integer, ArrayList<String>>> findUser(String name) throws Exception
	{
		ArrayList<Map.Entry<Integer, ArrayList<String>>> matches = new ArrayList<Map.Entry<Integer, ArrayList<String>>>();
		StringTokenizer st;
		boolean usingComma = false;
		if (name.indexOf(",") >= 0)
		{
			st = new StringTokenizer(" " + name + " ",",");
			usingComma = true;
		}
		else
			st = new StringTokenizer(name);

		String username="";
		String firstname="";
		String lastname="";
		switch (st.countTokens())
		{
		case 0:
			throw new Exception("You must specify a name when using the %Creator or %Translator tags");
		case 1:
			username = st.nextToken().trim();
			break;
		case 2:
			if (!usingComma)
			{
				firstname = st.nextToken().trim();
				lastname = st.nextToken().trim();
			}
			else
			{
				lastname = st.nextToken().trim();
				firstname = st.nextToken().trim();
			}
			break;
		default:
			throw new Exception("The name you supplied: '"+name+"' has more than 2 parts.  If your name contains a space you must use the format: Last name, First name");

		}

		for (Map.Entry<Integer, ArrayList<String>> userInfo : userInfoMap.entrySet())
		{
			ArrayList<String> names = userInfo.getValue();
			if (username.length() > 0)
			{
				if (names.get(3).equals(username))
				{
					matches.clear();
					matches.add(userInfo);
					return matches;
				}
			}
			else if (names.get(1).trim().equalsIgnoreCase(firstname) && names.get(2).trim().equalsIgnoreCase(lastname))
				matches.add(userInfo);
		}

		ui.outputMessage("INFO: end find user... \n");

		return matches;
	}


	//This method creates the flash files for the specified question.
	//The parameters are the question_id and language_id together with the question itself.
	//Two files will be created
	//   1) Q-qid-lang.swf        where qid=question_id, lang=2-letter abbreviation for the language
	//   2) Q-qid-F-lang.swf      where qid=question_id, lang=2-letter abbreviation for the language
	//The method just calls the 'tex2swf' script like this:
	//   {script_dir}/tex2swf.sh {tmp_dir}/$1 {flash_dir} {base_dir}     where $1 the file name shown in 1) and 2)
	//This means that
	//    o) the script must be in {script_dir}                             (see variable tex2swfFolderName)
	//    o) the flash files will be copied to {flash_dir}                  (see variable flashFolderName)
	//    o) all intermediate files will be written to {tmp_dir}            (see variable tex2swfTmpFolderName)
	//    o) all graphic files must be in {base_dir}                        (see variable tex2swfBaseDir)
	public void createFlashFiles(int question_id, int language_id, String questionShortFilename, String feedbackShortFilename, Map<Tag, String> question) throws FlashException, Exception
	{
		//////////////////////////////////////////////////////////
		/// Generate the feedback swf
		//Check if a flash movie with the same name already exists.
		File flashFile = new File(flashFolderName + feedbackShortFilename + ".swf");
		if (flashFile.exists() && !overwriteFlashFiles)
			throw new FlashException("File " + flashFolderName + feedbackShortFilename + ".swf" + " exists and overwrite is set to false, the Flash movie will not be created.");
		//Build the .tex file from the %Ftext tag
		PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(tex2swfTmpFolderName + feedbackShortFilename + ".tex")));
		fout.println(LATEX_HEADER);
		fout.println(question.get(Tag.Ftext));
		fout.println(LATEX_FOOTER);
		fout.close();

		//Convert .tex to .swf using the tex2swf.sh script
		String commandToExecute = tex2swfFolderName+"tex2swf.sh " +                    //script name
		tex2swfTmpFolderName+feedbackShortFilename+".tex " +                    //file to convert
		tex2swfTmpFolderName + " " +                                       //tmp dir
		flashFolderName+ " " +                                       //flash dir
		tex2swfBaseDir;                                         //root of grahics dir subtree
		Runtime.getRuntime().exec(commandToExecute).waitFor();

		//Check if the script was sucessful.
		flashFile = new File(flashFolderName+feedbackShortFilename+".swf");
		if (!flashFile.exists())
			throw new FlashException("File " + flashFolderName+feedbackShortFilename+".swf could not be created automatically.\n" +
					"See " + tex2swfTmpFolderName+"mej_convert.log for more details.\n" +
					"The failing command was: '" +commandToExecute + "'");

		//////////////////////////////////////////////////////////
		/// Generate the question+answer swf
		// questionShortFilename = "Q-"+question_id+"-"+shortLanguage(language_id);

		//Check if a flash movie with the same name already exists.
		flashFile = new File(flashFolderName+questionShortFilename+".swf");
		if (flashFile.exists() && !overwriteFlashFiles)
			throw new FlashException("File " + flashFolderName+questionShortFilename+".swf" + " exists and overwrite is set to false, the Flash movie will not be created.");

		//Build the .tex file from the %Qtext (and if appropriate %ChoiceX) tags
		fout = new PrintWriter(new BufferedWriter(new FileWriter(tex2swfTmpFolderName+questionShortFilename+".tex")));
		fout.println(LATEX_HEADER);
		fout.println(question.get(Tag.Qtext));
		fout.println();//insert a blank line between the question and answers
		String answerType = question.get(Tag.Type);
		if (answerType.startsWith("MC"))
		{
			fout.println("\\begin{mej-enumerate}");
			for (Tag t : CHOICES)
				if (question.get(t) != null)
					fout.println("\\item " + question.get(t));
			fout.println("\\end{mej-enumerate}");
		}
		else if (answerType.equals("TF"))
		{
			fout.println("\\begin{mej-enumerate}");
			fout.println("\\item " + trueString(language_id));
			fout.println("\\item " + falseString(language_id));
			fout.println("\\end{mej-enumerate}");
		}
		fout.println();
		fout.println(LATEX_FOOTER);
		fout.close();

		//Convert .tex to .swf using the tex2swf.sh script
		commandToExecute = tex2swfFolderName + "tex2swf.sh " +                    //script name
		tex2swfTmpFolderName + questionShortFilename + ".tex " + //file to convert
		tex2swfTmpFolderName + " " +                         //tmp dir
		flashFolderName + " " +                               //flash dir
		tex2swfBaseDir;                                      //root of grahics dir subtree
		Runtime.getRuntime().exec(commandToExecute).waitFor();

		//Check if the script was sucessful.
		flashFile = new File(flashFolderName+questionShortFilename+".swf");
		if (!flashFile.exists())
			throw new FlashException("File " + flashFolderName+questionShortFilename+".swf could not be created automatically.\n" +
					"See " + tex2swfTmpFolderName+"mej_convert.log for more details.\n" +
					"The failing command was: '" +commandToExecute + "'");
		ui.outputMessage("INFO: Finishing to add new flash question...\n");

	}


	/**
	 * Used for testing porposes in LatexToMej main
	 * @param sqlSelectQuery
	 */
	private void getQuestionsFromDB(String sqlSelectQuery)
	{
		Connection conn = null;
		Connection conn2 = null;
		try
		{
			conn = mysqlDataSource.getConnection();
			conn2 = mysqlDataSource.getConnection();
			Statement stmt = conn.createStatement();
			PreparedStatement pstmt = conn2.prepareStatement("SELECT answer_latex FROM answer,answer_info WHERE answer.answer_id=answer_info.answer_id && answer.question_id = ? ORDER BY answer.label");
			ResultSet rs = stmt.executeQuery(sqlSelectQuery);
			Map<Tag, String> question = new TreeMap<Tag, String>();
			while (rs.next())
			{
				int atid = rs.getInt("question.answer_type_id");
				int qid = rs.getInt("question_id");
				int lid = rs.getInt("language_id");
				String qlatex = rs.getString("question_latex");
				String flatex = rs.getString("feedback_latex");
				String qflash = rs.getString("question_flash_file");
				String fflash = rs.getString("feedback_flash_file");
				qflash = qflash.substring(0,qflash.indexOf(".swf"));
				fflash = fflash.substring(0,fflash.indexOf(".swf"));
				
				ArrayList<String> alatex = new ArrayList<String>();
				if (atid==1 || atid==4)
				{
					pstmt.setInt(1,qid);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next())
						alatex.add(rs2.getString("answer_latex"));
					rs2.close();
				}
				String type;
				switch (atid) {
				case 1: type = "MC"; break;
				case 2: type = "TF"; break;
				case 3: type = "SA"; break;
				case 5: type = "MD"; break;
				case 4: 
				default: type = "MC5"; break;
				}
				question.clear();
				question.put(Tag.Qtext, qlatex);
				question.put(Tag.Ftext, flatex);
				question.put(Tag.Type, type);
				for (int i=0; i<alatex.size(); i++)
					question.put(CHOICES[i],alatex.get(i));

				try
				{
					createFlashFiles(qid, lid, qflash, fflash, question);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println("error while creating flash file for " + qid);
				}
				rs.close();
			}
			conn.close();
			conn2.close();
		} catch (SQLException sqle)
		{
			sqle.printStackTrace();
			try {conn.close();}catch(Exception e){};

		}
	}

	private int getSourceId(String sourceString, PreparedStatement pstmtSelectSource, PreparedStatement pstmtInsertSource) throws Exception
	{
		//The question has no source. (%Source is not a mandatory tag)
		if (sourceString == null)
			return 0;

		//We have already encountered this source, look up its id in the map.
		Integer source_id = sourceMap.get(sourceString);
		if (source_id != null) return source_id;

		//We haven't seen this source string before (the map is initialized with all sources found in the DB).
		//Build an option dialog
		String msg = "No exact match for source: " + sourceString + ", is it in this list?" + "\n";
		ArrayList<SmacOptionData> options = new ArrayList<SmacOptionData>();
		ResultSet rs = pstmtSelectSource.executeQuery();
		while (rs.next())
			options.add(new SmacOptionData(rs.getString("name"), rs.getInt("source_id")));
		options.add(new SmacOptionData("It's not in the list, create a new one.", -1));
		rs.close();

		//Present the dialog to the user until a valid choice is made.
		do
		{
			int selection = ui.selectOption(msg, options);          
			switch(selection)
			{
			case Integer.MIN_VALUE: //something went wrong, ask again
				source_id = -1;
				break;
			case -1:  //the user did not find the source in the list, we try to add the source to the DB
				pstmtInsertSource.setString(1,sourceString);
				executeUpdate(pstmtInsertSource, "An error occured when trying to insert a source into the DB");
				rs = pstmtInsertSource.getGeneratedKeys();
				rs.next();
				source_id = rs.getInt(1);
				rs.close();
				ui.outputMessage("A new source was created with this name: " + sourceString + "\n");
				break;
			default: //the user found the source in the list
				source_id = selection;
			}
		} while (source_id == -1);

		//Add an entry to the sourceMap with the now known source_id
		sourceMap.put(sourceString, source_id);

		return source_id;

	}

	private int getTitleId(String titleString, PreparedStatement pstmtInsertTitle) throws Exception
	{
		Integer title_id = titleMap.get(titleString);

		//We have already encountered this title, look up its id in the map.
		if (title_id != null) return title_id;

		//We haven't seen this source string before add it to the DB
		pstmtInsertTitle.setString(1, titleString);
		executeUpdate(pstmtInsertTitle, "An error occured when trying to insert a title into the DB");
		ResultSet rs = pstmtInsertTitle.getGeneratedKeys();
		rs.next();
		title_id = rs.getInt(1);
		rs.close();
		ui.outputMessage("A new title was created with this name: " + titleString + "\n");

		//Add an entry to the titleMap with the now known title_id
		titleMap.put(titleString, title_id);

		return title_id;
	}

	private int getUserId(String userString, int language_id) throws Exception
	{
		//First look in the set of confirmed user mappings
		Integer user_id = confirmedUsers.get(userString);
		if (user_id != null) return user_id;

		//Next look in a much bigger set: The set of all users present in the DB when the GUI was initialized
		ArrayList<Map.Entry<Integer,ArrayList<String>>> allMatchingDBUsers = findUser(userString);

		Connection dbConnection = mysqlDataSource.getConnection();

		PreparedStatement pstmtInsertUser = dbConnection.prepareStatement("INSERT INTO jos_users (name, username, email, password, usertype, gid, registerDate, lastvisitDate, activation, params) VALUES(?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtInsertJosCompro = dbConnection.prepareStatement("INSERT INTO jos_comprofiler (id, user_id, firstname, lastname, registeripaddr, cbactivation, acceptedterms, cb_gradelevel, cb_gender, cb_school)  VALUES(?,?,?,?,?,?,?,?,?,?)");
		PreparedStatement pstmtInsertJosCor = dbConnection.prepareStatement("INSERT INTO jos_core_acl_aro (section_value, value, name) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtInsertJosCorMap = dbConnection.prepareStatement("INSERT INTO jos_core_acl_groups_aro_map (group_id, section_value, aro_id) VALUES(?,?,?)");
		
		//The user still couldn't be identified, create a new one.  Add it to the DB _and_ the set of confirmed user mappings
		if (allMatchingDBUsers.size() == 0)
		{
			user_id = addUser(userString, language_id, pstmtInsertUser,  pstmtInsertJosCompro, pstmtInsertJosCor, pstmtInsertJosCorMap );
			confirmedUsers.put(userString, user_id);
			return user_id;

			//JOptionPane.showMessageDialog(ui,"It's not in the list, create a new user for this creator - " + userString, "Error", JOptionPane.ERROR_MESSAGE);
			//ui.outputMessage("It's not in the list, create a new user for this creator - " + userString);
			//throw new Exception("The name you supplied: '" + userString + "' is not present in DB");
		}

		//At least one match was found and we are told to always use the first match
		if (allMatchingDBUsers.size() > 0 && useFirstUserMatch)
		{
			user_id = allMatchingDBUsers.get(0).getKey();
			confirmedUsers.put(userString, user_id);
			return user_id;
		}

		//At least one match was found.  We ask the user if the 'true' creator/translator is in the list, even if the list contains a single entry.

		//Build an option dialog
		String msg = "Match(es) were found in the DB for user: " + userString + " which one do you mean?\n";
		ArrayList<SmacOptionData> options = new ArrayList<SmacOptionData>();
		for (int i=0; i<allMatchingDBUsers.size(); i++)
			options.add(new SmacOptionData(allMatchingDBUsers.get(i).getValue().toString(), allMatchingDBUsers.get(i).getKey()));
		options.add(new SmacOptionData("It's not in the list, create a new user for this creator", -1));

		//Keep asking for the 'true' creator/translator until a valid option is chosen.
		do
		{
			int selection = ui.selectOption(msg, options);
			switch (selection)
			{
			case Integer.MIN_VALUE: //Something went wrong during the selection.
				user_id = -1;
				break;
			case -1: //The selection said: create a new user
				user_id = addUser(userString, language_id, pstmtInsertUser,  pstmtInsertJosCompro, pstmtInsertJosCor, pstmtInsertJosCorMap);
				break;
			default:
				user_id = selection;
			}
		} while (user_id == -1);

		//Add an entry to the confirmedUsers with the now known user_id
		confirmedUsers.put(userString, user_id);
		dbConnection.close();
		return user_id;
	}

	/**
	 * 
	 * @param allQuestions
	 * @param overwriteDBEntries
	 * @throws Exception
	 */
	public void insertInDB(Collection<Map<Tag, String>> allQuestions, boolean overwriteDBEntries) throws Exception
	{   		
		Connection dbConnection = mysqlDataSource.getConnection();
		ui.outputMessage("\rProcessing questions - overwriteDBEntries : " + overwriteDBEntries);

		//PreparedStatments are build to avoid creating the same statments over and over for each question
		PreparedStatement pstmtDeleteQuestion = dbConnection.prepareStatement("DELETE FROM question WHERE question_id=?");
		PreparedStatement pstmtInsertQuestion = dbConnection.prepareStatement("INSERT INTO question (answer_type_id, source_id, title_id, label, creator_id) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtInsertQuestionWithId = dbConnection.prepareStatement("INSERT INTO question (question_id, answer_type_id, source_id, title_id, label, creator_id) VALUES(?,?,?,?,?,?)");
		PreparedStatement pstmtCheckQuestionExists = dbConnection.prepareStatement("SELECT question_id FROM question WHERE question_id=? && answer_type_id=? && source_id=? && title_id=? && label=? && creator_id=?");
		PreparedStatement pstmtReplaceQuestionInfo = dbConnection.prepareStatement("REPLACE INTO question_info" +
				"(question_id, language_id, question_latex, question_flash_file," + 
				"feedback_latex, feedback_flash_file, is_valid, user_id, creation_date, last_modified, is_animated) " +
		"VALUES(?,?,?,?,?,?,?,?,?,?,?)");
		PreparedStatement pstmtSelectAnswer = dbConnection.prepareStatement("SELECT answer_id FROM answer WHERE question_id=? && label=?");
		PreparedStatement pstmtSelectAnswerByQuestionId = dbConnection.prepareStatement("SELECT answer_id FROM answer WHERE question_id=?");
		PreparedStatement pstmtInsertAnswer = dbConnection.prepareStatement("INSERT INTO answer (question_id, is_right, label) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtInsertAnswerInfo = dbConnection.prepareStatement("INSERT INTO answer_info (answer_id, question_id, language_id, answer_latex, answer_flash_file) VALUES(?,?,?,?,?)");
		PreparedStatement pstmtDeleteAnswerInfo = dbConnection.prepareStatement("DELETE FROM answer_info WHERE question_id=? && language_id=?");
		PreparedStatement pstmtInsertQuestionsKeywords = dbConnection.prepareStatement("INSERT INTO questions_keywords (question_id, keyword_id) VALUES(?,?)");
		PreparedStatement pstmtInsertQuestionLevel = dbConnection.prepareStatement("INSERT INTO question_level (question_id, level_id, value) VALUES(?,?,?)");
		PreparedStatement pstmtInsertSource = dbConnection.prepareStatement("INSERT INTO source (name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtSelectAllSources = dbConnection.prepareStatement("SELECT source_id, name FROM source ORDER BY name");
		PreparedStatement pstmtInsertTitle = dbConnection.prepareStatement("INSERT INTO title (name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);

		PreparedStatement pstmtSelectQuestionWithoutSource = dbConnection.prepareStatement("SELECT question_id FROM question WHERE creator_id=? && label=?");
		PreparedStatement pstmtSelectQuestionWithSource = dbConnection.prepareStatement("SELECT question_id FROM question WHERE source_id=? && title_id=? && label=?");
		PreparedStatement pstmtSelectQuestionInfo = dbConnection.prepareStatement("SELECT * FROM question_info WHERE question_id=? && language_id=?");

		ResultSet rs;
		//As we go through the questions we build a set of confirmed users, this way we often avoid looking into the set of all DB users (which can be very large)
		TreeMap<String,Integer> confirmedUsers = new TreeMap<String, Integer>();

		int num=1;
		for (Map<Tag, String> q : allQuestions)
		{
			ui.outputMessage("Processing question " + (num++) + " of " + allQuestions.size() + " /n");
			try
			{
				int question_id = -1;
				String creatorString = q.get(Tag.Creator);
				String translatorString = q.get(Tag.Translator);
				boolean isTranslation = (translatorString != null);
				int language_id = Integer.parseInt(getProperty("language_id." + q.get(Tag.Language)));
				int creator_id = getUserId(creatorString, language_id);
				int translator_id = (!isTranslation) ? creator_id : getUserId(translatorString, language_id);

				if (creator_id == 0 || translator_id == 0)
				{
					ui.outputMessage("WARNING: Creator " + creatorString + " or translator " + translatorString + " not exists in DB, skipping question " + num + ".\n");
					continue;
				}

				int answer_type_id = Integer.parseInt(getProperty("answer_type_id."+q.get(Tag.Type)));
				int source_id = getSourceId(q.get(Tag.Source), pstmtSelectAllSources, pstmtInsertSource);
				int title_id = getTitleId(q.get(Tag.Title), pstmtInsertTitle);
				String label = q.get(Tag.Question);
				ArrayList<Integer> keywordIds = new ArrayList<Integer>();
				for (String keyword : q.get(Tag.Keywords).split(","))
					keywordIds.add(Integer.parseInt(getProperty("keyword_id." + keyword)));
				if (source_id == 0)
				{
					pstmtSelectQuestionWithoutSource.setInt(1,creator_id);
					pstmtSelectQuestionWithoutSource.setString(2,label);
					rs = pstmtSelectQuestionWithoutSource.executeQuery();
					if (rs.next())
						question_id = rs.getInt("question.question_id");
					rs.close();
				}
				else
				{
					pstmtSelectQuestionWithSource.setInt(1,source_id);
					pstmtSelectQuestionWithSource.setInt(2,title_id);
					pstmtSelectQuestionWithSource.setString(3,label);
					rs = pstmtSelectQuestionWithSource.executeQuery();
					if (rs.next())
						question_id = rs.getInt("question.question_id");
					rs.close();
				}

				if (question_id != -1 && !overwriteDBEntries)
				{
					ui.outputMessage("WARNING: Question " + question_id + " exists in DB and 'overwriteDBEntries' is set to false, skipping question.\n");
					continue;
				}
				if (question_id == -1 && isTranslation)
				{
					ui.outputMessage("WARNING: Original question must exists in DB before translations are added, skipping question. " + question_id + " is translation - " + "\n");
					continue;
				}

				if (question_id == -1)
				{          
					//Create a new row in the 'question' table of the DB ...
					pstmtInsertQuestion.setInt(1, answer_type_id);
					pstmtInsertQuestion.setInt(2, source_id);
					pstmtInsertQuestion.setInt(3, title_id);
					pstmtInsertQuestion.setString(4, label);
					pstmtInsertQuestion.setInt(5, creator_id);
					executeUpdate(pstmtInsertQuestion, "An SQL error occured when trying to add a question to the DB.");
					//... and get the id that was created for it
					rs = pstmtInsertQuestion.getGeneratedKeys();
					rs.next();
					question_id = rs.getInt(1);
					rs.close();
				}
				else if (isTranslation)
				{
					pstmtCheckQuestionExists.setInt(1, question_id);
					pstmtCheckQuestionExists.setInt(2, answer_type_id);
					pstmtCheckQuestionExists.setInt(3, source_id);
					pstmtCheckQuestionExists.setInt(4, title_id);
					pstmtCheckQuestionExists.setString(5, label);
					pstmtCheckQuestionExists.setInt(6, creator_id);
					rs = pstmtCheckQuestionExists.executeQuery();
					if (!rs.next())
					{
						ui.outputMessage("WARNING: Trying to add a translation but some of its fields value conflict with the data already in the DB, skipping question\n");
						rs.close();
						continue;
					}
					rs.close();
				}
				else
				{
					pstmtDeleteQuestion.setInt(1, question_id); //deletes all related rows of all tables thanks to InnoDB
					executeUpdate(pstmtDeleteQuestion, "An SQL error occured when trying to delete question " + question_id + " from the DB.");
					pstmtInsertQuestionWithId.setInt(1, question_id);
					pstmtInsertQuestionWithId.setInt(2, answer_type_id);
					pstmtInsertQuestionWithId.setInt(3, source_id);
					pstmtInsertQuestionWithId.setInt(4, title_id);
					pstmtInsertQuestionWithId.setString(5, label);
					pstmtInsertQuestionWithId.setInt(6, creator_id);
					executeUpdate(pstmtInsertQuestionWithId, "An SQL error occured when trying to add a question to the DB.");

				}

				String question_flash_file_prefix = "Q-" + question_id + "-" + shortLanguage(language_id);
				String feedback_flash_file_prefix = "Q-" + question_id + "-F-" + shortLanguage(language_id);

				//Create a new row in the 'question_info' table of the DB
				pstmtReplaceQuestionInfo.setInt(1, question_id);
				pstmtReplaceQuestionInfo.setInt(2, language_id);
				pstmtReplaceQuestionInfo.setString(3, q.get(Tag.Qtext));
				pstmtReplaceQuestionInfo.setString(4, question_flash_file_prefix+".swf");
				pstmtReplaceQuestionInfo.setString(5, q.get(Tag.Ftext));
				pstmtReplaceQuestionInfo.setString(6, feedback_flash_file_prefix+".swf");
				pstmtReplaceQuestionInfo.setInt(7, 1);
				pstmtReplaceQuestionInfo.setInt(8, translator_id); //translator_id == creator_id when questions are NOT translations
				pstmtReplaceQuestionInfo.setDate(9, new java.sql.Date(System.currentTimeMillis()));
				pstmtReplaceQuestionInfo.setDate(10, null);
				pstmtReplaceQuestionInfo.setInt(11, 0);
				executeUpdate(pstmtReplaceQuestionInfo, "An SQL error occured when trying to replace a question_info to the DB.");
				
				if (isTranslation)
				{
					pstmtDeleteAnswerInfo.setInt(1, question_id);
					pstmtDeleteAnswerInfo.setInt(2, language_id);
					executeUpdate(pstmtDeleteAnswerInfo, "An SQL error occured when trying to delete answer_info associate with question_id " + question_id + " and language_id " + language_id);
				}
				int answer_id = -1;
				//Create row(s) in the 'answer' and 'answer_info' table of the DB
				switch(answer_type_id)
				{
				case 1: //Multiple choices
				case 4: //Multiple choices with 5 answers
					int numchoices = Integer.parseInt(q.get(Tag.Choices));
					String[] answerLabels = new String[] {"a","b","c","d","e","f","g","h"};
					Tag[] choiceTag = new Tag[]{Tag.ChoiceA,Tag.ChoiceB,Tag.ChoiceC,Tag.ChoiceD,
							Tag.ChoiceE,Tag.ChoiceF,Tag.ChoiceG,Tag.ChoiceH};
					String rightAnswer = q.get(Tag.Answer);
					for (int i=0; i<numchoices; i++)
					{
						String answer_flash_file = "";
						answer_id = -1;
						if (!isTranslation)
						{
							int is_right = rightAnswer.equalsIgnoreCase(answerLabels[i])?1:0;
							pstmtInsertAnswer.setInt(1,question_id);
							pstmtInsertAnswer.setInt(2,is_right);
							pstmtInsertAnswer.setString(3,answerLabels[i]);
							executeUpdate(pstmtInsertAnswer, "An SQL error occured when trying to add a MC answer to the DB.");
							rs = pstmtInsertAnswer.getGeneratedKeys();
							rs.next();
							answer_id = rs.getInt(1);
							rs.close();
						}
						else
						{
							pstmtSelectAnswer.setInt(1, question_id);
							pstmtSelectAnswer.setString(2, answerLabels[i]);
							rs = pstmtSelectAnswer.executeQuery();
							if (rs.next())
								answer_id = rs.getInt("answer_id");
							rs.close();
						}
						if (answer_id == -1)
							throw new Exception("Could not find the DB entry in table 'answer' for MC question " + question_id + " with label='" + answerLabels[i] + "'");

						pstmtInsertAnswerInfo.setInt(1, answer_id);
						pstmtInsertAnswerInfo.setInt(2, question_id);
						pstmtInsertAnswerInfo.setInt(3, language_id);
						pstmtInsertAnswerInfo.setString(4, q.get(choiceTag[i]));
						pstmtInsertAnswerInfo.setString(5, answer_flash_file);
						executeUpdate(pstmtInsertAnswerInfo, "An SQL error occured when trying to add a MC answer_info to the DB.");
					}
					break;
				case 2: //True/false
					if (!isTranslation)
					{
						pstmtInsertAnswer.setInt(1,question_id);
						pstmtInsertAnswer.setInt(2,q.get(Tag.Answer).equals("T")?1:0);
						pstmtInsertAnswer.setString(3,"");
						executeUpdate(pstmtInsertAnswer, "An SQL error occured when trying to add a TF answer to the DB.");
					}
					break;

				case 3: //Short answer
					if (!isTranslation)
					{
						pstmtInsertAnswer.setInt(1,question_id);
						pstmtInsertAnswer.setInt(2,1);
						pstmtInsertAnswer.setString(3,"");
						executeUpdate(pstmtInsertAnswer, "An SQL error occured when trying to add a SA answer to the DB.");
						rs = pstmtInsertAnswer.getGeneratedKeys();
						rs.next();
						answer_id = rs.getInt(1);
						rs.close();
					}
					else
					{
						pstmtSelectAnswerByQuestionId.setInt(1, question_id);
						rs = pstmtSelectAnswerByQuestionId.executeQuery();
						if (rs.next())
							answer_id = rs.getInt("answer_id");
						rs.close();
					}
					if (answer_id == -1)
						throw new Exception("Could not find the DB entry in table 'answer' for SA question " + question_id);

					pstmtInsertAnswerInfo.setInt(1, answer_id);
					pstmtInsertAnswerInfo.setInt(2, question_id);
					pstmtInsertAnswerInfo.setInt(3, language_id);
					pstmtInsertAnswerInfo.setString(4, q.get(Tag.Answer));
					pstmtInsertAnswerInfo.setString(5, "");
					executeUpdate(pstmtInsertAnswerInfo, "An SQL error occured when trying to add a SA answer_info to the DB.");
					break;
				}

				if (isTranslation)
					continue;

				//Add the keywords ids to the 'questions_keywords' table of the DB
				for (Integer keyword_id : keywordIds)
				{
					pstmtInsertQuestionsKeywords.setInt(1, question_id);
					pstmtInsertQuestionsKeywords.setInt(2, keyword_id);
					pstmtInsertQuestionsKeywords.addBatch();
				}
				executeBatch(pstmtInsertQuestionsKeywords, "An SQL error occured when trying to add questions keywords to the DB.");


				String qdiff = q.get(Tag.Qdifficulty);
				if (qdiff == null)
					qdiff = getProperty("rdiff."+q.get(Tag.Rdifficulty));
				StringTokenizer st = new StringTokenizer(qdiff, ",");
				int level_id=0;
				while (st.hasMoreTokens())
				{
					level_id++;
					int value = Integer.parseInt(st.nextToken());
					if (value == 0)
						continue;
					pstmtInsertQuestionLevel.setInt(1, question_id);
					pstmtInsertQuestionLevel.setInt(2, level_id);
					pstmtInsertQuestionLevel.setInt(3, value);
					executeUpdate(pstmtInsertQuestionLevel, "An SQL error occured when trying to add a question level to the DB.");
				}
			}
			catch (Exception e)
			{
				ui.outputMessage(e.getMessage() + " in insertDB \n");
				break;
			}
		}		
	}

	private int getFirstFreeQuestionId()
	{
		Connection dbConnection;
		int last_id = 0;
		try {
			dbConnection = mysqlDataSource.getConnection();
			Statement stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(question_id) AS id from question;");
			if (rs.next())
			{
				last_id = rs.getInt("id");				
			}
			rs.close();
			stmt.close();
			dbConnection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		return last_id + 1;
	}

	//Returns the id of the option selected by the user, possible return value are:
	//   Integer.MIN_VALUE: something went wrong
	//   -1               : the user did not find a match in the list
	//   anything else    : the id (from the MeJ DB) of the option selected by the user
	public int selectOption(String msg, ArrayList<SmacOptionData> options)
	{
		int id;
		try
		{
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(msg);
			for (int i=0; i<options.size(); i++)
				System.out.println(i + ") " + options.get(i).optionString);
			System.out.print("> ");
			int value = Integer.parseInt(cin.readLine());
			id = value == -1 ? -1 : options.get(value).optionId;
		}
		catch (Exception e)
		{
			id = Integer.MIN_VALUE;
			ui.outputMessage("\rError reading your input: " + e.getMessage());
		}
		return id;
	}

	public void outputMessage(String message)
	{
		System.out.println(message);
	}

	public static void main(String[] args)
	{
		String sqlSelectQuery = "SELECT question_info.*,question.answer_type_id FROM question_info,question WHERE question.question_id = question_info.question_id && language_id = 1";
		LatexToMeJ ltm = null;
		try
		{
			ltm = new LatexToMeJ();
			ltm.getQuestionsFromDB(sqlSelectQuery);
		}
		catch (Exception e)
		{
			if (ltm != null)
				ltm.outputMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	// don't used now, before for random file id
	public  String encodeQuestionNumber(int qid)
	{
		StringBuffer keyBuffer = new StringBuffer("");
		Random rand = new Random();
		keyBuffer.setLength(0);
		for(int x = 0; x < 2; x++)
		{
			keyBuffer.append(Integer.toHexString(rand.nextInt(126) + 48));
			keyBuffer.append(Integer.toHexString(rand.nextInt(333) + 65));
			keyBuffer.append(Integer.toHexString(rand.nextInt(777) + 97));
		}

		keyBuffer.append(Integer.toHexString(qid));
		keyBuffer.append(Integer.toHexString(rand.nextInt(126) + 65));
		keyBuffer.append(Integer.toHexString(rand.nextInt(126) + 97));

		return keyBuffer.toString();

	}

	public int findMatchAndExportInDB(Map<Tag, String> q, boolean overwriteDBEntries) throws SQLException
	{	
		Connection dbConnection = mysqlDataSource.getConnection();
		ui.outputMessage("Processing question - condition - overwriteDBEntries : " + overwriteDBEntries + "\n");

		//PreparedStatments are build to avoid creating the same statments over and over for each question
		PreparedStatement pstmtDeleteQuestion = dbConnection.prepareStatement("DELETE FROM question WHERE question_id=?");
		PreparedStatement pstmtInsertQuestion = dbConnection.prepareStatement("INSERT INTO question (answer_type_id, source_id, title_id, label, creator_id) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtInsertQuestionWithId = dbConnection.prepareStatement("INSERT INTO question (question_id, answer_type_id, source_id, title_id, label, creator_id) VALUES(?,?,?,?,?,?)");
		PreparedStatement pstmtCheckQuestionExists = dbConnection.prepareStatement("SELECT question_id FROM question WHERE question_id=? && answer_type_id=? && source_id=? && title_id=? && label=? && creator_id=?");
		PreparedStatement pstmtReplaceQuestionInfo = dbConnection.prepareStatement("REPLACE INTO question_info" +
				"(question_id, language_id, question_latex, question_flash_file," + 
				"feedback_latex, feedback_flash_file, is_valid, user_id, creation_date, last_modified, is_animated) " +
		"VALUES(?,?,?,?,?,?,?,?,?,?,?)");
		
		PreparedStatement pstmtAddQuestionInfo = dbConnection.prepareStatement("REPLACE INTO question_info" +
				"(question_id, language_id, question_latex, question_flash_file," + 
				"feedback_latex, feedback_flash_file, is_valid, user_id, creation_date, last_modified, is_animated) " +
		"VALUES(?,?,?,?,?,?,?,?,?,?,?)");


		PreparedStatement pstmtSelectAnswer = dbConnection.prepareStatement("SELECT answer_id FROM answer WHERE question_id=? && label=?");
		PreparedStatement pstmtSelectAnswerByQuestionId = dbConnection.prepareStatement("SELECT answer_id FROM answer WHERE question_id=?");
		PreparedStatement pstmtInsertAnswer = dbConnection.prepareStatement("INSERT INTO answer (question_id, is_right, label) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtInsertAnswerInfo = dbConnection.prepareStatement("INSERT INTO answer_info (answer_id, question_id, language_id, answer_latex, answer_flash_file) VALUES(?,?,?,?,?)");
		PreparedStatement pstmtDeleteAnswerInfo = dbConnection.prepareStatement("DELETE FROM answer_info WHERE question_id=? && language_id=?");
		PreparedStatement pstmtInsertQuestionsKeywords = dbConnection.prepareStatement("INSERT INTO questions_keywords (question_id, keyword_id) VALUES(?,?)");
		PreparedStatement pstmtInsertQuestionLevel = dbConnection.prepareStatement("INSERT INTO question_level (question_id, level_id, value) VALUES(?,?,?)");

		PreparedStatement pstmtInsertSource = dbConnection.prepareStatement("INSERT INTO source (name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtSelectAllSources = dbConnection.prepareStatement("SELECT source_id, name FROM source ORDER BY name");
		PreparedStatement pstmtInsertTitle = dbConnection.prepareStatement("INSERT INTO title (name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);

		PreparedStatement pstmtSelectQuestionWithoutSource = dbConnection.prepareStatement("SELECT question_id FROM question WHERE creator_id=? && label=? && title_id=?");
		PreparedStatement pstmtSelectQuestionWithSource = dbConnection.prepareStatement("SELECT question_id FROM question WHERE source_id=? && title_id=? && label=? && creator_id=?");
		PreparedStatement pstmtSelectQuestionInfo = dbConnection.prepareStatement("SELECT * FROM question_info WHERE question_id=? && language_id=?");

		ResultSet rs;		

		int question_id = -1;
		ui.outputMessage("Processing question " + q.get(Tag.PATH) + "\n");
		try
		{			
			String creatorString = q.get(Tag.Creator);
			String translatorString = q.get(Tag.Translator);
			boolean isTranslation = (translatorString != null);
			int language_id = Integer.parseInt(getProperty("language_id." + q.get(Tag.Language)));
			int creator_id = getUserId(creatorString, language_id);
			int translator_id = (!isTranslation) ? creator_id : getUserId(translatorString, language_id);

			if (creator_id == 0 || translator_id == 0)
			{
				ui.outputMessage("WARNING: Creator " + creatorString + " or translator " + translatorString + " not exists in DB, skipping question" + q.get(Tag.PATH) + ".\n");
				return question_id;
			}

			int answer_type_id = Integer.parseInt(getProperty("answer_type_id." + q.get(Tag.Type)));
			int source_id = getSourceId(q.get(Tag.Source), pstmtSelectAllSources, pstmtInsertSource);
			int title_id = getTitleId(q.get(Tag.Title), pstmtInsertTitle);
			//String label =q.get(Tag.PATH) + "/" + q.get(Tag.Question);
			String label = q.get(Tag.Question);
			ArrayList<Integer> keywordIds = new ArrayList<Integer>();
			for (String keyword : q.get(Tag.Keywords).split(","))
				keywordIds.add(Integer.parseInt(getProperty("keyword_id." + keyword)));

			String question_flash_file_prefix = "Q-" + question_id + "-" + shortLanguage(language_id);
			String feedback_flash_file_prefix = "Q-" + question_id + "-F-" + shortLanguage(language_id);

			// phase 1 - get question_id
			if (source_id == 0)
			{
				pstmtSelectQuestionWithoutSource.setInt(1,creator_id);
				pstmtSelectQuestionWithoutSource.setString(2,label);
				pstmtSelectQuestionWithoutSource.setInt(3,title_id);
				rs = pstmtSelectQuestionWithoutSource.executeQuery();
				if (rs.next())
					question_id = rs.getInt("question.question_id");
				rs.close();
			}
			else
			{
				pstmtSelectQuestionWithSource.setInt(1,source_id);
				pstmtSelectQuestionWithSource.setInt(2,title_id);
				pstmtSelectQuestionWithSource.setString(3,label);
				pstmtSelectQuestionWithSource.setInt(4,creator_id);
				rs = pstmtSelectQuestionWithSource.executeQuery();
				if (rs.next())
					question_id = rs.getInt("question.question_id");
				rs.close();
			}

			if (question_id != -1 && !overwriteDBEntries)
			{
				ui.outputMessage("WARNING: Question " + question_id + " exists in DB and 'overwriteDBEntries' is set to false, we only update question.\n");
				//updateQuestion(question_id, dbConnection);

				String strSQL = "UPDATE question SET label = '" + label + "' WHERE question_id = " + question_id + " AND title_id = " + title_id + 
						" AND creator_id = " + creator_id + ";";
				executeUpdate(dbConnection.createStatement(), strSQL, "An SQL error occured when trying to update a question - table 'question'.");
/*
				strSQL = "UPDATE question_info SET question_latex ='" + q.get(Tag.Qtext) + "'," +
				" question_flash_file = " + question_flash_file_prefix+".swf," +
				" feedback_latex = " + q.get(Tag.Ftext) + "," +
				" feedback_flash_file = " + feedback_flash_file_prefix + ".swf, " +
				" is_valid = 1, user_id = " + creator_id + ", last_modified = " + new java.sql.Date(System.currentTimeMillis()) +
				" WHERE question_id = " + question_id + " AND language_id = " + language_id + ";";
	*/			
				pstmtReplaceQuestionInfo.setInt(1, question_id);
				pstmtReplaceQuestionInfo.setInt(2, language_id);
				pstmtReplaceQuestionInfo.setString(3, q.get(Tag.Qtext));
				pstmtReplaceQuestionInfo.setString(4, question_flash_file_prefix+".swf");
				pstmtReplaceQuestionInfo.setString(5, q.get(Tag.Ftext));
				pstmtReplaceQuestionInfo.setString(6, feedback_flash_file_prefix+".swf");
				pstmtReplaceQuestionInfo.setInt(7, 1);
				pstmtReplaceQuestionInfo.setInt(8, translator_id); //translator_id == creator_id when questions are NOT translations
				pstmtReplaceQuestionInfo.setDate(9, new java.sql.Date(System.currentTimeMillis()));
				pstmtReplaceQuestionInfo.setDate(10, null);
				pstmtReplaceQuestionInfo.setInt(11, 0);
				executeUpdate(pstmtReplaceQuestionInfo, "An SQL error occured when trying to update a question_info to the DB.");
				
				//executeUpdate(dbConnection.createStatement(), strSQL, "An SQL error occured when trying to update a question - table question_info.");

				strSQL = "DELETE questions_keywords.* FROM questions_keywords WHERE question_id = " + question_id + ";";
				executeUpdate(dbConnection.createStatement(), strSQL, "An SQL error occured when trying to get out a question from table questions_keywords.");

				strSQL = "DELETE question_level.* FROM question_level WHERE question_id = " + question_id + ";";
				executeUpdate(dbConnection.createStatement(), strSQL, "An SQL error occured when trying to get out a question from table question_level.");

				strSQL = "DELETE answer.* FROM answer WHERE question_id = " + question_id + ";";
				executeUpdate(dbConnection.createStatement(), strSQL, "An SQL error occured when trying to get out a question from table answer.");

				strSQL = "DELETE answer_info.* FROM answer_info WHERE question_id = " + question_id + " AND language_id = " + language_id + ";";
				executeUpdate(dbConnection.createStatement(), strSQL, "An SQL error occured when trying to get out a question from table answer.");
				
			}

			if (question_id == -1 && isTranslation)
			{
				ui.outputMessage("WARNING: Original question must exists in DB before translations are added, skipping question. " + question_id + " is translation - " + "\n");
				return question_id;
			}

			if (question_id == -1)
			{          
				//Create a new row in the 'question' table of the DB ...
				pstmtInsertQuestion.setInt(1, answer_type_id);
				pstmtInsertQuestion.setInt(2, source_id);
				pstmtInsertQuestion.setInt(3, title_id);
				pstmtInsertQuestion.setString(4, label);
				pstmtInsertQuestion.setInt(5, creator_id);
				executeUpdate(pstmtInsertQuestion, "An SQL error occured when trying to add a question to the DB.");
				//... and get the id that was created for it
				rs = pstmtInsertQuestion.getGeneratedKeys();
				rs.next();
				question_id = rs.getInt(1);
				rs.close();
			}
			else if (isTranslation)
			{
				pstmtCheckQuestionExists.setInt(1, question_id);
				pstmtCheckQuestionExists.setInt(2, answer_type_id);
				pstmtCheckQuestionExists.setInt(3, source_id);
				pstmtCheckQuestionExists.setInt(4, title_id);
				pstmtCheckQuestionExists.setString(5, label);
				pstmtCheckQuestionExists.setInt(6, creator_id);
				rs = pstmtCheckQuestionExists.executeQuery();
				if (!rs.next())
				{
					ui.outputMessage("WARNING: Trying to add a translation but some of its fields value conflict with the data already in the DB, skipping question\n");
					rs.close();
					return -1;
				}
				rs.close();
			}
			else if(overwriteDBEntries)
			{
				pstmtDeleteQuestion.setInt(1, question_id); //deletes all related rows of all tables thanks to InnoDB
				executeUpdate(pstmtDeleteQuestion, "An SQL error occured when trying to delete question " + question_id + " from the DB.");
				pstmtInsertQuestionWithId.setInt(1, question_id);
				pstmtInsertQuestionWithId.setInt(2, answer_type_id);
				pstmtInsertQuestionWithId.setInt(3, source_id);
				pstmtInsertQuestionWithId.setInt(4, title_id);
				pstmtInsertQuestionWithId.setString(5, label);
				pstmtInsertQuestionWithId.setInt(6, creator_id);
				executeUpdate(pstmtInsertQuestionWithId, "An SQL error occured when trying to add a question to the DB.");	
				
				//Create a new row in the 'question_info' table of the DB
				pstmtAddQuestionInfo.setInt(1, question_id);
				pstmtAddQuestionInfo.setInt(2, language_id);
				pstmtAddQuestionInfo.setString(3, q.get(Tag.Qtext));
				pstmtAddQuestionInfo.setString(4, question_flash_file_prefix+".swf");
				pstmtAddQuestionInfo.setString(5, q.get(Tag.Ftext));
				pstmtAddQuestionInfo.setString(6, feedback_flash_file_prefix+".swf");
				pstmtAddQuestionInfo.setInt(7, 1);
				pstmtAddQuestionInfo.setInt(8, translator_id); //translator_id == creator_id when questions are NOT translations
				pstmtAddQuestionInfo.setDate(9, new java.sql.Date(System.currentTimeMillis()));
				pstmtAddQuestionInfo.setDate(10, null);
				pstmtAddQuestionInfo.setInt(11, 0);
				executeUpdate(pstmtAddQuestionInfo, "An SQL error occured when trying to replace a question_info to the DB.");
			}
			
			

			

			if (isTranslation)
			{
				pstmtDeleteAnswerInfo.setInt(1, question_id);
				pstmtDeleteAnswerInfo.setInt(2, language_id);
				executeUpdate(pstmtDeleteAnswerInfo, "An SQL error occured when trying to delete answer_info associate with question_id " + question_id + " and language_id " + language_id);
			}

			int answer_id = -1;
			//Create row(s) in the 'answer' and 'answer_info' table of the DB
			switch(answer_type_id)
			{
			case 1: //Multiple choices
			case 4: //Multiple choices with 5 answers
				int numchoices = Integer.parseInt(q.get(Tag.Choices));
				String[] answerLabels = new String[] {"a","b","c","d","e","f","g","h"};
				Tag[] choiceTag = new Tag[]{Tag.ChoiceA,Tag.ChoiceB,Tag.ChoiceC,Tag.ChoiceD,
						Tag.ChoiceE,Tag.ChoiceF,Tag.ChoiceG,Tag.ChoiceH};
				String rightAnswer = q.get(Tag.Answer);
				for (int i=0; i<numchoices; i++)
				{
					String answer_flash_file = "";
					answer_id = -1;
					if (!isTranslation)
					{
						int is_right = rightAnswer.equalsIgnoreCase(answerLabels[i])?1:0;
						pstmtInsertAnswer.setInt(1,question_id);
						pstmtInsertAnswer.setInt(2,is_right);
						pstmtInsertAnswer.setString(3,answerLabels[i]);
						executeUpdate(pstmtInsertAnswer, "An SQL error occured when trying to add a MC answer to the DB.");
						rs = pstmtInsertAnswer.getGeneratedKeys();
						rs.next();
						answer_id = rs.getInt(1);
						rs.close();
					}
					else
					{
						pstmtSelectAnswer.setInt(1, question_id);
						pstmtSelectAnswer.setString(2, answerLabels[i]);
						rs = pstmtSelectAnswer.executeQuery();
						if (rs.next())
							answer_id = rs.getInt("answer_id");
						rs.close();
					}
					if (answer_id == -1)
						throw new Exception("Could not find the DB entry in table 'answer' for MC question " + question_id + " with label='" + answerLabels[i] + "'");

					pstmtInsertAnswerInfo.setInt(1, answer_id);
					pstmtInsertAnswerInfo.setInt(2, question_id);
					pstmtInsertAnswerInfo.setInt(3, language_id);
					pstmtInsertAnswerInfo.setString(4, q.get(choiceTag[i]));
					pstmtInsertAnswerInfo.setString(5, answer_flash_file);
					ui.outputMessage("processing question - answer info" + question_id + " " + answer_id + " "+ language_id + " /n");
					executeUpdate(pstmtInsertAnswerInfo, "An SQL error occured when trying to add a MC answer_info to the DB.");
				}
				break;
			case 2: //True/false
				if (!isTranslation)
				{
					pstmtInsertAnswer.setInt(1,question_id);
					pstmtInsertAnswer.setInt(2,q.get(Tag.Answer).equals("T")?1:0);
					pstmtInsertAnswer.setString(3,"");
					executeUpdate(pstmtInsertAnswer, "An SQL error occured when trying to add a TF answer to the DB.");
				}
				break;

			case 3: //Short answer
				if (!isTranslation)
				{
					pstmtInsertAnswer.setInt(1,question_id);
					pstmtInsertAnswer.setInt(2,1);
					pstmtInsertAnswer.setString(3,"");
					executeUpdate(pstmtInsertAnswer, "An SQL error occured when trying to add a SA answer to the DB.");
					rs = pstmtInsertAnswer.getGeneratedKeys();
					rs.next();
					answer_id = rs.getInt(1);
					rs.close();
				}
				else
				{
					pstmtSelectAnswerByQuestionId.setInt(1, question_id);
					rs = pstmtSelectAnswerByQuestionId.executeQuery();
					if (rs.next())
						answer_id = rs.getInt("answer_id");
					rs.close();
				}
				if (answer_id == -1)
					throw new Exception("Could not find the DB entry in table 'answer' for SA question " + question_id);

				pstmtInsertAnswerInfo.setInt(1, answer_id);
				pstmtInsertAnswerInfo.setInt(2, question_id);
				pstmtInsertAnswerInfo.setInt(3, language_id);
				pstmtInsertAnswerInfo.setString(4, q.get(Tag.Answer));
				pstmtInsertAnswerInfo.setString(5, "");
				ui.outputMessage("processing question - answer info" + question_id + " " + answer_id + " "+ language_id + " /n");
				executeUpdate(pstmtInsertAnswerInfo, "An SQL error occured when trying to add a SA answer_info to the DB.");
				break;
			}

			if (isTranslation)
				return question_id;

			//Add the keywords ids to the 'questions_keywords' table of the DB
			for (Integer keyword_id : keywordIds)
			{
				pstmtInsertQuestionsKeywords.setInt(1, question_id);
				pstmtInsertQuestionsKeywords.setInt(2, keyword_id);
				pstmtInsertQuestionsKeywords.addBatch();
			}
			executeBatch(pstmtInsertQuestionsKeywords, "An SQL error occured when trying to add questions keywords to the DB.");


			String qdiff = q.get(Tag.Qdifficulty);
			if (qdiff == null)
				qdiff = getProperty("rdiff."+q.get(Tag.Rdifficulty));
			StringTokenizer st = new StringTokenizer(qdiff, ",");
			int level_id=0;
			while (st.hasMoreTokens())
			{
				level_id++;
				int value = Integer.parseInt(st.nextToken());
				if (value == 0)
					continue;
				pstmtInsertQuestionLevel.setInt(1, question_id);
				pstmtInsertQuestionLevel.setInt(2, level_id);
				pstmtInsertQuestionLevel.setInt(3, value);
				executeUpdate(pstmtInsertQuestionLevel, "An SQL error occured when trying to add a question level to the DB.");
			}

		}
		catch (Exception e)
		{
			ui.outputMessage(e.getMessage() + " in insertDB \n");
			dbConnection.close();
			return -1;
		}

		dbConnection.close();
		ui.outputMessage("Finished processing question " + question_id + " /n");
		return question_id;

	}// end method


}
