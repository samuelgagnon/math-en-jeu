package ServeurJeu.Evenements;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import ClassesUtilitaires.UtilitaireXML;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementJoueurEntreSalle extends Evenement
{
	// D�claration d'une variable qui va garder le nom d'utilisateur du 
	// joueur qui est entr� dans la salle
    private String strNomUtilisateur;
    
    /**
     * Constructeur de la classe EvenementJoueurEntreSalle qui permet 
     * d'initialiser le nom d'utilisateur du joueur qui vient d'entrer. 
     */
    public EvenementJoueurEntreSalle(String nomUtilisateur)
    {
        // D�finir le nom d'utilisateur du joueur qui est entr�
        strNomUtilisateur = nomUtilisateur;
    }
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement d'un joueur
	 * entrant dans la salle et de le retourner.
	 * 
	 * @param InformationDestination information : Les informations � qui 
	 * 					envoyer l'�v�nement
	 * @return String : Le code XML de l'�v�nement � envoyer
	 */
	protected String genererCodeXML(InformationDestination information)
	{
	    // D�claration d'une variable qui va contenir le code XML � retourner
	    String strCodeXML = "";
	    
		try
		{
	        // Appeler une fonction qui va cr�er un document XML dans lequel 
		    // on peut ajouter des noeuds
	        Document objDocumentXML = UtilitaireXML.obtenirDocumentXML();

			// Cr�er le noeud de commande � retourner
			Element objNoeudCommande = objDocumentXML.createElement("commande");
			
			// Cr�er le noeud du param�tre
			Element objNoeudParametre = objDocumentXML.createElement("parametre");
			
			// Cr�er un noeud contenant le nom d'utilisateur du noeud param�tre
			Text objNoeudTexte = objDocumentXML.createTextNode(strNomUtilisateur);
			
			// D�finir les attributs du noeud de commande
			objNoeudCommande.setAttribute("no", Integer.toString(information.obtenirNoCommande()));
			objNoeudCommande.setAttribute("type", "Evenement");
			objNoeudCommande.setAttribute("nom", "JoueurEntreSalle");
			
			// On ajoute un attribut type qui va contenir le type
			// du param�tre
			objNoeudParametre.setAttribute("type", "NomUtilisateur");
			
			// Ajouter le noeud texte au noeud du param�tre
			objNoeudParametre.appendChild(objNoeudTexte);
			
			// Ajouter le noeud param�tre au noeud de commande
			objNoeudCommande.appendChild(objNoeudParametre);
			
			// Ajouter le noeud de commande au noeud racine dans le document
			objDocumentXML.appendChild(objNoeudCommande);

			// Transformer le document XML en code XML
			strCodeXML = UtilitaireXML.transformerDocumentXMLEnString(objDocumentXML);
		}
		catch (TransformerConfigurationException tce)
		{
			System.out.println(GestionnaireMessages.message("evenement.XML_transformation"));
		}
		catch (TransformerException te)
		{
			System.out.println(GestionnaireMessages.message("evenement.XML_conversion"));
		}
		
		return strCodeXML;
	}
}
