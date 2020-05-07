package ServeurJeu.Configuration;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * @author Jean-Fran�ois Fournier
 */
public class GestionnaireMessages {
	
	private static XMLConfiguration objConfigMessages;
	private static String strFichierConfig;
	
	/* Il faut initialiser la classe lorsque le serveur d�marre sinon les
	 * appels � la fonction message() �choueront
	 */
	public static void initialiser()
	{
		GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();
		strFichierConfig = config.obtenirString("controleurjeu.messages.fichierxml");
		
		try
		{
			objConfigMessages = new XMLConfiguration(strFichierConfig);
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static String message(String nomMessage)
	{
		String strMessage = objConfigMessages.getString(nomMessage);
		
		// V�rifier si on a trouv� le message dont le nom est le param�tre
		// nomMessage, si non, il faut sp�cifier l'erreur
		if (strMessage == null)
		{
			return "ERREUR: Message <" + nomMessage + "> non trouv� dans le fichier " + 
			    strFichierConfig;
		}
        else
		{
			return strMessage;
        }

	}
}
