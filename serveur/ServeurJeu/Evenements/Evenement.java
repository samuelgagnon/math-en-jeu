package ServeurJeu.Evenements;

import java.util.ArrayList;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ServeurJeu.Configuration.GestionnaireMessages;
import ClassesUtilitaires.UtilitaireXML;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Evenement
{
	// D�claration d'une liste de InformationDestination
	protected ArrayList<InformationDestination> lstInformationDestination;
	//un document XML dans lequel on peut ajouter des noeuds
	protected Document objDocumentXML;

	public Evenement()
	{
		lstInformationDestination = new ArrayList<InformationDestination>();
	    // Appeler une fonction qui va cr�er le document XML 
		objDocumentXML = UtilitaireXML.obtenirDocumentXML();
	}

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
			InformationDestination information =  lstInformationDestination.get(i);
			
			// Envoyer l'�v�nement au joueur courant
			information.obtenirProtocoleJoueur().envoyerMessage(genererStringXML(information));			
		}

	}
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement d'un joueur
	 * et de le retourner.
	 * 
	 * @param InformationDestination information : Les informations � qui 
	 * 					envoyer l'�v�nement
	 * @return String : Le code XML de l'�v�nement � envoyer
	 */
	protected String genererStringXML(InformationDestination information)
	{

		Element objNoeudCommande = objDocumentXML.getDocumentElement();
		objNoeudCommande.setAttribute("no", Integer.toString(information.obtenirNoCommande()));

		try {
			return UtilitaireXML.transformerDocumentXMLEnString(objDocumentXML);
		}
		catch (TransformerConfigurationException tce)
		{
			System.out.println(GestionnaireMessages.message("evenement.XML_transformation"));
		}
		catch (TransformerException te)
		{
			System.out.println(GestionnaireMessages.message("evenement.XML_conversion"));
		}

		return ""; //TO DO ... 
	}
}
