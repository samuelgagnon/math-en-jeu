package ServeurJeu.BD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import ServeurJeu.ComposantesJeu.Language;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Questions.BoiteQuestions;
import ServeurJeu.ComposantesJeu.Questions.MiniDoku;
import ServeurJeu.ComposantesJeu.Questions.MultipleChoice5Question;
import ServeurJeu.ComposantesJeu.Questions.MultipleChoiceQuestion;
import ServeurJeu.ComposantesJeu.Questions.ShortAnswerQuestion;
import ServeurJeu.ComposantesJeu.Questions.TrueFalseQuestion;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * Class used to serve the JoueurHumain with data from DB 
 * @author Oloieri Lilian
 *
 */

public final class GestionnaireBDJoueur extends GestionnaireBD {

	private static final int[] pointsArray = {  200,   500,   900,  1325,  1775,  2250,  2750,  3275,  3825,  4400,
		5000,  5625,  6275,  6950,  7650,  8375,  9125,  9900, 10700, 11525, 
		12375, 13250, 14150, 15075, 16025, 17000, 18000, 19025, 20075, 21150,
		22250, 23375, 25525, 25700, 26900, 28125, 29375, 30650, 31950, 33275};
	
    // D�claration d'une r�f�rence vers le notre joueur
	private final JoueurHumain objJoueurHumain;	
	private int counter;

	/**
	 * Constructeur de la classe GestionnaireBD qui permet de garder la
	 * r�f�rence vers le joueur
	 * @param JoueurHumain.
	 */
	public GestionnaireBDJoueur(JoueurHumain joueur) {
		super();

		this.objJoueurHumain = joueur;
		counter = 0;
	}

	
	/**
	 * La fonction rempli la boiteQuestions avec des questions que correspond
	 * a niveaux scolaires du joueur
	 * This function fills a Question box with the questions of player's level
	 * for each category and player's lang
	 * @param objJoueurHumain Le joueur pour lequel la bo�te doit �tre remplie
	 */
	public void remplirBoiteQuestions(){
		
		// Pour tenir compte de la langue
		int cleLang = 1;
		BoiteQuestions boite = objJoueurHumain.obtenirPartieCourante().getObjBoiteQuestions();
		
		String URL = boite.obtenirLangue().getURLQuestionsAnswers();
		Language language = boite.obtenirLangue();
		String langue = language.getLanguage();
		if (langue.equalsIgnoreCase("fr"))
			cleLang = 1;
		else if (langue.equalsIgnoreCase("en"))
			cleLang = 2;

		// to not fill the Box with the same questions
		int roomLevel = objJoueurHumain.obtenirSalleCourante().getRoomLevelId();
		int niveau = roomLevel == 19 ? objJoueurHumain.obtenirCleNiveau() : roomLevel;
		System.out.println("level : " + niveau);
			niveau -= counter;
		counter--;

		StringBuffer writer = objJoueurHumain.obtenirPartieCourante().getBoiteQuestionsInfo();
		writer.append("ADD questions : Asked level - " + niveau + "\n");

		// it's little risk for that, but to be sure....
		if (niveau < 1)
			niveau = objJoueurHumain.obtenirCleNiveau() + 1;
		int room_id = objJoueurHumain.obtenirSalleCourante().getRoomId();
		writer.append("ADD questions : Asked level later - " + niveau + "\n");
		
		String strRequeteSQL = "SELECT  question.answer_type_id, question.question_id," +
		" question_info.question_flash_file, question_info.feedback_flash_file, question_level.value, " +
		" answer.label FROM question_info, question_level, question, answer " +
		" WHERE  question.question_id = question_level.question_id " +
		" AND question.question_id = question_info.question_id " +
		" AND question.question_id = answer.question_id " +
		" AND answer.is_right = 1 " +
		" AND question_info.language_id = " + cleLang +
		" and question_level.level_id = " + niveau +
		" AND question.question_id IN (SELECT question.question_id FROM question, questions_keywords " +
		" WHERE question.question_id = questions_keywords.question_id AND questions_keywords.keyword_id IN (SELECT rooms_keywords.keyword_id FROM rooms_keywords WHERE room_id = " + room_id + ")) " +
		" AND question.answer_type_id IN (1,4) " +
		" AND question_info.is_valid = 1 " +
		" and question_level.value > 0 " +
		" and question_info.question_flash_file is not NULL " +
		" and question_info.feedback_flash_file is not NULL ";


		remplirBoiteQuestionsMC(boite, strRequeteSQL, URL);

		String strRequeteSQL_SA = "SELECT DISTINCT a.answer_latex, qi.question_id, qi.question_flash_file, qi.feedback_flash_file, ql.value " +
		"FROM question q, question_info qi, question_level ql, answer_info a, questions_keywords " +
		"where  q.question_id = ql.question_id " +
		" AND q.question_id = qi.question_id " +
		" AND q.question_id = a.question_id " +
		" AND q.question_id = questions_keywords.question_id " +
		" AND questions_keywords.keyword_id IN (SELECT keyword_id FROM rooms_keywords WHERE room_id = " + room_id +
		") and q.answer_type_id = 3 " +
		" AND qi.language_id = " + cleLang +
		" and ql.level_id = " + niveau +
		" and ql.value > 0 " +
		" and qi.is_valid = 1 " +
		" and qi.question_flash_file is not NULL" +
		" and qi.feedback_flash_file is not NULL";

		remplirBoiteQuestionsSA(boite, strRequeteSQL_SA, URL);

		String strRequeteSQL_TF = "SELECT DISTINCT a.is_right,qi.question_id, qi.question_flash_file, qi.feedback_flash_file, ql.value " +
		" FROM question q, question_info qi, question_level ql, answer a, questions_keywords " +
		"where  q.question_id = ql.question_id " +
		" AND q.question_id = qi.question_id " +
		" AND q.question_id = a.question_id " +
		" AND q.question_id = questions_keywords.question_id " +
		" AND questions_keywords.keyword_id IN (SELECT keyword_id FROM rooms_keywords WHERE room_id = " + room_id +
		") and q.answer_type_id = 2 " +
		" AND qi.language_id = " + cleLang +
		" and ql.level_id = " + niveau +
		" and ql.value > 0 " +
		" and qi.is_valid = 1 " +
		" and qi.question_flash_file is not NULL" +
		" and qi.feedback_flash_file is not NULL";

		remplirBoiteQuestionsTF(boite, strRequeteSQL_TF, URL);

		// request for mathdoku
		String strRequeteSQL_MD = "SELECT DISTINCT qi.question_id, qi.question_flash_file, qi.feedback_flash_file, ql.value " +
		" FROM question q, question_info qi, question_level ql, questions_keywords " +
		"where  q.question_id = ql.question_id " +
		" AND q.question_id = qi.question_id " +
		" AND q.question_id = questions_keywords.question_id " +
		" AND questions_keywords.keyword_id IN (SELECT keyword_id FROM rooms_keywords WHERE room_id = " + room_id +
		") and q.answer_type_id = 5 " +
		" AND qi.language_id = " + cleLang +
		" and ql.level_id = " + niveau +
		" and ql.value > 0 " +
		" and qi.is_valid = 1 " +
		" and qi.question_flash_file is not NULL" +
		" and qi.feedback_flash_file is not NULL";

		remplirBoiteQuestionsMD(boite, strRequeteSQL_MD, URL);

		boite.getBoxSize();
		ArrayList<Integer> lastQuestions = null;
		int boxSize = boite.getBoxSize();
		int temps = objJoueurHumain.obtenirPartieCourante().obtenirTable().obtenirTempsTotal();
		int lastSize = 0;
		// we consider 5 questions for minuts  
		if(boxSize > temps * 5)
		{
			// now get out the questions from last 3 games
			lastQuestions = this.getLastGamesQuestions(objJoueurHumain, cleLang);
			lastSize = lastQuestions.size();
		}

		if(lastQuestions != null && boxSize - lastSize > temps * 3)
		{
			for(Integer id: lastQuestions)
			{
				boite.popQuestion(id);
				writer.append("Get out question : " + id + "\n");

			}
		}   

		boite.getBoxSize();

	}// fin m�thode

	private ArrayList<Integer> getLastGamesQuestions(JoueurHumain objJoueurHumain, int cleLang) {

		String strRequeteSQL = "SELECT  questions_answers " +
		" FROM game_user WHERE user_id  = " + objJoueurHumain.obtenirCleJoueur() + " ORDER BY game_id DESC LIMIT 2;";

		ArrayList<String> liste = new ArrayList<String>();
        ResultSet rs = null; 
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery(strRequeteSQL);
				while (rs.next()) {
					liste.add(rs.getString("questions_answers"));
				}
				rs.close();                
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		} catch (RuntimeException e) {
			// Ce n'est pas le bon message d'erreur mais ce n'est pas grave
			objLogger.error(GestionnaireMessages.message("bd.error_questions"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in getLastGamesQuestions");    			
		}

		ArrayList<Integer> lastQuestions = new ArrayList<Integer>();
		Integer quest = 0;
		for(String questions: liste)
		{
			StringTokenizer ids = new StringTokenizer(questions, ",");

			while (ids.hasMoreTokens()) {
				try
				{
					quest = Integer.parseInt(ids.nextToken());
					lastQuestions.add(quest);
				}catch(NumberFormatException ex)
				{
					objLogger.error(GestionnaireMessages.message("bd.error_questions"), ex);
				}

			}
		}
		return lastQuestions;

	}// end method

	// This function follows one of the two previous functions. It queries the database and
	// does the actual filling of the question box with questions of type MULTIPLE_CHOICE.
	private void remplirBoiteQuestionsMC(BoiteQuestions boiteQuestions, String strRequeteSQL, String URL) {
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery(strRequeteSQL);
				rs.setFetchSize(5);

				while (rs.next()) {

					int codeQuestion = rs.getInt("question_id");
					int typeQuestion = rs.getInt("answer_type_id");
					String question = rs.getString("question_flash_file");
					String explication = rs.getString("feedback_flash_file");
					int difficulte = rs.getInt("value");
					String reponse = getAnswerFromLabel(rs.getString("label"));
					
					if(typeQuestion == 1)
						boiteQuestions.ajouterQuestion(new MultipleChoiceQuestion(codeQuestion, typeQuestion, difficulte, URL + question, reponse, URL + explication));
					if(typeQuestion == 4)
						boiteQuestions.ajouterQuestion(new MultipleChoice5Question(codeQuestion, typeQuestion, difficulte, URL + question, reponse, URL + explication));
				
				}
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		} catch (RuntimeException e) {
			//Une erreur est survenue lors de la recherche de la prochaine question
			objLogger.error(GestionnaireMessages.message("bd.erreur_prochaine_question_MC"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in remplirBoiteQuestionsMC");    			
		}
		
	}// fin m�thode
	
	private String getAnswerFromLabel(String label)
	{
		if(label.equals("a"))
		{
			return "1";
		}else if(label.equals("b"))
		{
			return "2";
		}else if(label.equals("c"))
		{
			return "3";
		}else if(label.equals("d"))
		{
			return "4";
		}else if(label.equals("e"))
		{
			return "5";
		}
        return "error";
	}

	// This function follows one of the two previous functions. It queries the database and
	// does the actual filling of the question box with questions of type SHORT_ANSWER.
	private void remplirBoiteQuestionsSA(BoiteQuestions boiteQuestions, String strRequeteSQL, String URL) {
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery(strRequeteSQL);
				rs.setFetchSize(5);
				while (rs.next()) {
					int codeQuestion = rs.getInt("question_id");
					//int keyword_id1 = rs.getInt( "keyword_id1" );
					//int keyword_id2 = rs.getInt( "keyword_id2" );
					//int typeQuestion = 3;//rs.getString( "tag" );
					String question = rs.getString("question_flash_file");
					String reponse = rs.getString("answer_latex");
					String explication = rs.getString("feedback_flash_file");
					int difficulte = rs.getInt("value");

					//String URL = boiteQuestions.obtenirLangue().getURLQuestionsAnswers();
					
					// 3 - question type - short answer
					boiteQuestions.ajouterQuestion(new ShortAnswerQuestion(codeQuestion, 3, difficulte, URL + question, reponse, URL + explication));					
				}				
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		} catch (RuntimeException e) {
			//Une erreur est survenue lors de la recherche de la prochaine question
			objLogger.error(GestionnaireMessages.message("bd.erreur_prochaine_question_SA"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in remplirBoiteQuestionsSA");    			
		}
		
	}// fin m�thode

	// This function follows one of the two previous functions. It queries the database and
	// does the actual filling of the question box with questions of type TRUE_OR_FALSE.
	private void remplirBoiteQuestionsTF(BoiteQuestions boiteQuestions, String strRequeteSQL, String URL) {
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery(strRequeteSQL);
				rs.setFetchSize(5);
				while (rs.next()) {
					int codeQuestion = rs.getInt("question_id");
					//int keyword_id1 = rs.getInt( "keyword_id1" );
					//int keyword_id2 = rs.getInt( "keyword_id2" );
					//int typeQuestion = 2;   //rs.getString( "tag" );
					String question = rs.getString("question_flash_file");
					String reponse = rs.getString("is_right");
					String explication = rs.getString("feedback_flash_file");
					int difficulte = rs.getInt("value");
					
					//String URL = boiteQuestions.obtenirLangue().getURLQuestionsAnswers();
					// 2 - question type - true false
					boiteQuestions.ajouterQuestion(new TrueFalseQuestion(codeQuestion, 2, difficulte, URL + question, reponse, URL + explication));
				}				
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		} catch (RuntimeException e) {
			//Une erreur est survenue lors de la recherche de la prochaine question
			objLogger.error(GestionnaireMessages.message("bd.erreur_prochaine_question_TF"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in remplirBoiteQuestionsTF");    			
		}
	}// fin m�thode

	// This function follows one of the two previous functions. It queries the database and
	// does the actual filling of the question box with questions of type MINI_DOKU.
	private void remplirBoiteQuestionsMD(BoiteQuestions boiteQuestions, String strRequeteSQL, String URL) {
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery(strRequeteSQL);
				rs.setFetchSize(5);
				while (rs.next()) {
					int codeQuestion = rs.getInt("question_id");
					//int keyword_id1 = rs.getInt( "keyword_id1" );
					//int keyword_id2 = rs.getInt( "keyword_id2" );
					//int typeQuestion = 5;   //rs.getString( "tag" );
					String question = rs.getString("question_flash_file");
					//String reponse = rs.getString("is_right");
					String explication = rs.getString("feedback_flash_file");
					int difficulte = rs.getInt("value");

					//String URL = boiteQuestions.obtenirLangue().getURLQuestionsAnswers();
					
					// 5 - question type - mathdoku 
					boiteQuestions.ajouterQuestion(new MiniDoku(codeQuestion, 5, difficulte, URL + question, "", URL + explication));
				}
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		} catch (RuntimeException e) {
			//Une erreur est survenue lors de la recherche de la prochaine question
			objLogger.error(GestionnaireMessages.message("bd.erreur_prochaine_question_MD"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in remplirBoiteQuestionsMD");    			
		}
	}// fin m�thode

	/**
	 * Methode used to charge to player's money from DB for current game ***
	 * option can be disabled with
	 * @param userId
	 * @return the money available to the player
	 */
	public int getPlayersMoney(int userId) {
		int money = 0;
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery("SELECT jos_comprofiler.cb_money  FROM jos_comprofiler WHERE user_id = " + userId + ";");
				if (rs.next())
					money = rs.getInt("money");				
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete_get_money"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in getPlayersMoney");    			
		}
		return money;
	}// end methode

	/**
	 * Tells the server where to look for questions .swf in the specified
	 * language
	 * @param language the language used.
	 * @return URL of Questions-Answers on server
	 */
	public String transmitUrl(String language) {
		String url = "";
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery("SELECT language.url FROM language " +
						" WHERE language.short_name = '" + language + "';");
				while (rs.next()) {
					url = rs.getString("url");
				}
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in transmitURL");    			
		}
		return url;
	}//end methode

	// This method updates a player's information in the DB  ***
	public void mettreAJourJoueur(int tempsTotal) {
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				 rs = requete.executeQuery("SELECT cb_completedgames, cb_bestscore, cb_totaltimeplayed, cb_totalscore FROM jos_comprofiler WHERE id = '" + 
						objJoueurHumain.obtenirCleJoueur() + "';");
				if (rs.next()) {
					int partiesCompletes = rs.getInt("cb_completedgames") + 1;
					int meilleurPointage = rs.getInt("cb_bestscore");
					int pointageActuel = objJoueurHumain.obtenirPartieCourante().obtenirPointage();
					if (meilleurPointage < pointageActuel)
						meilleurPointage = pointageActuel;

					int tempsPartie = tempsTotal + rs.getInt("cb_totaltimeplayed");
					int totalScore = pointageActuel + rs.getInt("cb_totalscore");

					int image = 0;
					int i;
					for(i = 0; i < pointsArray.length; i++)
					{
						if(totalScore >= pointsArray[i])
							image = 40 - i;	
						else
							break;
					}

					// calculate points percents done to next grade
					double percents = 0;
					if(i == 0)
						percents = (double)totalScore / pointsArray[i] * 100;
					else if (i == 39)
						percents = 100;
					else 
						percents = (double)(totalScore - pointsArray[i - 1]) / (pointsArray[i] - pointsArray[i - 1]) * 100;

					//Format df = new Format("#.#");
					//df.format(percents);
					String target = String.format("%.1f", percents) + "%";

					//BigDecimal represent = new BigDecimal(percents, new MathContext(4));                    

					//mise-a-jour
					requete.executeUpdate("UPDATE jos_comprofiler SET cb_connected = " + 0 + " ,cb_completedgames = " + partiesCompletes + 
							" ,cb_bestscore = " + meilleurPointage + " , cb_totaltimeplayed = " + tempsPartie +  " , cb_nexttarget = '" + target + 
							"' , cb_totalscore = " + totalScore + " WHERE user_id = " + objJoueurHumain.obtenirCleJoueur() + ";");


					//String im = "";
					String im = "gallery/" + image + ".png";
					//mise-a-jour
					requete.executeUpdate("UPDATE jos_comprofiler SET avatar = '" + im + "' WHERE user_id = " + objJoueurHumain.obtenirCleJoueur() + ";");
				}
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in mettreAJourJoueur");    			
		}
	}

	/**
	 * Methode used to update in DB the player's money ****
	 * @param cleJoueur
	 * @param newMoney
	 */
	public void setNewPlayersMoney() {
		int cleJoueur = objJoueurHumain.obtenirCleJoueur();
		int newMoney = objJoueurHumain.obtenirPartieCourante().obtenirArgent();
		// Update the money in player's account
		String strMoney = " UPDATE jos_comprofiler SET cb_money = " + newMoney + " WHERE user_id = " + cleJoueur + ";";

		try {

			synchronized (DB_LOCK) {
				// Ajouter l'information pour ce joueur
				requete.executeUpdate(strMoney);
			}
		} catch (Exception e) {
			objLogger.error(GestionnaireMessages.message("bd.erreur_ajout_infos_update_money"), e);
			getNewConnection();    	
		}

	}//end methode


	public void remplirBoiteQuestionsWithRoomQuestions() {
		
		// Pour tenir compte de la langue
		int cleLang = 1;
		BoiteQuestions boite = objJoueurHumain.obtenirPartieCourante().getObjBoiteQuestions();
		
		String URL = boite.obtenirLangue().getURLQuestionsAnswers();
		Language language = boite.obtenirLangue();
		String langue = language.getLanguage();
		if (langue.equalsIgnoreCase("fr"))
			cleLang = 1;
		else if (langue.equalsIgnoreCase("en"))
			cleLang = 2;

		int niveau = objJoueurHumain.obtenirCleNiveau();		
		int room_id = objJoueurHumain.obtenirSalleCourante().getRoomId();
		
		String strRequeteSQL = "SELECT  question.answer_type_id, question.question_id," +
		" question_info.question_flash_file, question_info.feedback_flash_file, question_level.value, " +
		" answer.label FROM question_info, question_level, question, answer " +
		" WHERE question.question_id IN (SELECT room_question.question_id FROM room_question " +
		" WHERE room_id = " + room_id + ") " +
		" AND question.question_id = question_level.question_id " +
		" AND question.question_id = question_info.question_id " +
		" AND question.question_id = answer.question_id " +
		" AND answer.is_right = 1 " +
		" AND question_info.language_id = " + cleLang +
		" and question_level.level_id = " + niveau +		
		" AND question.answer_type_id IN (1,4) " +
		" AND question_info.is_valid = 1 " +
		" and question_level.value > 0 " +
		" and question_info.question_flash_file is not NULL " +
		" and question_info.feedback_flash_file is not NULL; ";


		remplirBoiteQuestionsMC(boite, strRequeteSQL, URL);

		String strRequeteSQL_SA = "SELECT DISTINCT a.answer_latex, qi.question_id, qi.question_flash_file, qi.feedback_flash_file, ql.value " +
		"FROM question q, question_info qi, question_level ql, answer_info a " +
		"where  q.question_id = ql.question_id " +
		" AND q.question_id = qi.question_id " +
		" AND q.question_id = a.question_id " +
		" AND q.question_id IN (SELECT room_question.question_id FROM room_question WHERE room_id = " + room_id +
		") and q.answer_type_id = 3 " +
		" AND qi.language_id = " + cleLang +
		" and ql.level_id = " + niveau +
		" and ql.value > 0 " +
		" and qi.is_valid = 1 " +
		" and qi.question_flash_file is not NULL" +
		" and qi.feedback_flash_file is not NULL;";

		remplirBoiteQuestionsSA(boite, strRequeteSQL_SA, URL);

		String strRequeteSQL_TF = "SELECT DISTINCT a.is_right,qi.question_id, qi.question_flash_file, qi.feedback_flash_file, ql.value " +
		" FROM question q, question_info qi, question_level ql, answer a " +
		"where  q.question_id = ql.question_id " +
		" AND q.question_id = qi.question_id " +
		" AND q.question_id = a.question_id " +
		" AND q.question_id IN (SELECT room_question.question_id FROM room_question WHERE room_id = " + room_id +
		") and q.answer_type_id = 2 " +
		" AND qi.language_id = " + cleLang +
		" and ql.level_id = " + niveau +
		" and ql.value > 0 " +
		" and qi.is_valid = 1 " +
		" and qi.question_flash_file is not NULL" +
		" and qi.feedback_flash_file is not NULL;";

		remplirBoiteQuestionsTF(boite, strRequeteSQL_TF, URL);

		// request for mathdoku
		String strRequeteSQL_MD = "SELECT DISTINCT qi.question_id, qi.question_flash_file, qi.feedback_flash_file, ql.value " +
		" FROM question q, question_info qi, question_level ql " +
		"where  q.question_id = ql.question_id " +
		" AND q.question_id = qi.question_id " +
		" AND q.question_id IN (SELECT room_question.question_id FROM room_question WHERE room_id = " + room_id +
		") and q.answer_type_id = 5 " +
		" AND qi.language_id = " + cleLang +
		" and ql.level_id = " + niveau +
		" and ql.value > 0 " +
		" and qi.is_valid = 1 " +
		" and qi.question_flash_file is not NULL" +
		" and qi.feedback_flash_file is not NULL;";

		remplirBoiteQuestionsMD(boite, strRequeteSQL_MD, URL);
		
		boite.getBoxSize();
		boite.getInfo();
		
	}

	/**
	 * Returns <tt>true</tt> if the room_question table contains
	 * questions for this room
	 * 
	 * @return  <tt>true<tt> if the room_question table contains
	 * questions for this room
	 */
	public boolean checkRoomQuestions() {
		
		int numberQuestions = 0;
		int room_id = objJoueurHumain.obtenirSalleCourante().getRoomId();
		
		ResultSet rs = null;
		try {
			synchronized (DB_LOCK) {
				rs = requete.executeQuery("SELECT COUNT(*) FROM room_question " +
						"  WHERE room_id  = '" + room_id + "';");
				if (rs.next()) {
					numberQuestions = rs.getInt(1);
				}				
			}
		} catch (SQLException e) {
			// Une erreur est survenue lors de l'ex�cution de la requ�te
			objLogger.error(GestionnaireMessages.message("bd.erreur_exec_requete"), e);
			getNewConnection();    	
		}finally{ 
			dbUtilCloseResultSet(rs, "Error in release ResultSet in transmitURL");    			
		}
				
		return (numberQuestions > 0);
	}

}