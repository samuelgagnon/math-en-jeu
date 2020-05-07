package ServeurJeu.Evenements;

import java.util.Vector;
import java.io.IOException;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public abstract class Evenement
{
	// D�claration d'une liste de InformationDestination
	protected Vector lstInformationDestination = new Vector();
	
	/**
	 * Cette fonction permet d'ajouter un nouveau InformationDestination � la
	 * liste d'InformationDestionation qui sert � savoir � qui envoyer 
	 * l'�v�nement courant.
	 * 
	 * @param InformationDestionation information : Un objet contenant le num�ro
	 * 						de commande ainsi que le ProtocoleJoueur du joueur
	 * 						� qui envoyer l'�v�nement courant.
	 */
	public void ajouterInformationDestination(InformationDestination information)
	{
	    // Ajouter l'InformationDestination � la fin de la liste
		lstInformationDestination.add(information);
	}
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement d'un nouveau
	 * joueur et de le retourner.
	 * 
	 * @param InformationDestination information : Les informations � qui 
	 * 					envoyer l'�v�nement
	 * @return String : Le code XML de l'�v�nement � envoyer
	 */
	protected abstract String genererCodeXML(InformationDestination information);
	
	/**
	 * Cette m�thode permet d'envoyer l'�v�nement courant � tous les joueurs
	 * se trouvant dans la liste des InformationDestination.
	 */
	public void envoyerEvenement()
	{
	    // Passer tous les InformationDestination se trouvant dans la liste de
	    // l'�v�nement courant et envoyer � chacun l'�v�nement courant
		
	    for (int i = 0; i < lstInformationDestination.size(); i++)
	    {
	        // Faire la r�f�rence vers l'objet InformationDestination courant
	        InformationDestination information = (InformationDestination) lstInformationDestination.get(i);
	        
	        try
	        {
                
                String strTemp = genererCodeXML(information);
		        
		        // Envoyer l'�v�nement au joueur courant
		        information.obtenirProtocoleJoueur().envoyerMessage(strTemp);	            
   
	        }
	        catch (IOException ioe)
	        {
				//System.out.println("L'evenement courant n'a pas pu etre envoye");
	        }
	    }
	    
	}
}
