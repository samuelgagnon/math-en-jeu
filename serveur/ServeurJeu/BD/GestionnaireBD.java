package ServeurJeu.BD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * @author Jean-Fran�ois Brind'Amour
 * @author Oloieri Lilian
 */
public abstract class GestionnaireBD {

    public static final SimpleDateFormat mejFormatDate = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat mejFormatHeure = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat mejFormatDateHeure = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    // Objet Connection n�cessaire pour le contact avec le serveur MySQL
    protected Connection connexion;
    // Objet Statement n�cessaire pour envoyer une requ�te au serveur MySQL
    protected Statement requete;
    protected static Logger objLogger = Logger.getLogger(GestionnaireBD.class);
    protected Object DB_LOCK = new Object();
    
    
    
    /**
     * Constructeur de la classe GestionnaireBD qui permet de garder la
     * r�f�rence vers le contr�leur de jeu
     */
    public GestionnaireBD() {
        super();
       
        // get connection from the pool
        DBConnectionsPoolManager pool = DBConnectionsPoolManager.getInstance();
        connexion = pool.getConnection();
        
        // Cr�ation de l'objet "requ�te"
        createStatement();  
    }

    /**
     * Method used to release the problematic connection
     * and to take another one
     */
    protected void getNewConnection(){
    	releaseConnection();
    	DBConnectionsPoolManager pool = DBConnectionsPoolManager.getInstance();
        connexion = pool.getConnection();
		createStatement();
    }
    
       
    /**
     * Cette fonction permet de cr�er un objet requ�te
     */
    protected void createStatement() {

    	// Cr�ation de l'objet "requ�te"
    	try {
    		requete = connexion.createStatement();
    	} catch (SQLException e) {
    		// Une erreur est survenue lors de la cr�ation d'une requ�te
    		objLogger.error(GestionnaireMessages.message("bd.erreur_creer_requete"));
    		objLogger.error(GestionnaireMessages.message("bd.trace"));
    		objLogger.error(e.getMessage(), e);
    		getNewConnection();    		
    		return;
    	}
    	objLogger.info("bd statement created");
    }
    
    /**
     * 
     */
    protected void releaseConnection(){
    	try {
			requete.close();				
		} catch (SQLException e) {
			objLogger.error(GestionnaireMessages.message("bd.erreur_fermer_requete"), e);			
		}
    	DBConnectionsPoolManager pool = DBConnectionsPoolManager.getInstance();
    	pool.freeConnection(connexion);
    	connexion = null;
    	objLogger.info("bd connection released");
    }
    
   
    /**
     * Cette m�thode permet de fermer la connexion de base de donn�es qui
     * est ouverte.
     */
    public void arreterGestionnaireBD() {
    	releaseConnection();
    	DB_LOCK = null;
    }
    
    
    protected static void dbUtilCloseResultSet(ResultSet rs, String message)
    {
    	if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				objLogger.error(message, e);
			}
    	}
    }
    
    protected static void dbUtilClosePreparedStatement(PreparedStatement prep, String message)
    {
    	if(prep != null){
			try {
				prep.close();
			} catch (SQLException e) {
				objLogger.error(message, e);
			}
    	}
    }
    
    
}// end class

