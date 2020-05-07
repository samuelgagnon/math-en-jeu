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
public class EvenementJoueurEntreTable extends Evenement
{
	// D�claration d'une variable qui va garder le num�ro de la table dans 
	// laquelle le joueur est entr�
    private int intNoTable;
    
	// D�claration d'une variable qui va garder le nom d'utilisateur du 
	// joueur qui est entr� dans la table
    private String strNomUtilisateur;
    
    /**
     * Constructeur de la classe EvenementJoueurEntreTable qui permet 
     * d'initialiser le num�ro de la table et le nom d'utilisateur du 
     * joueur qui vient d'entrer dans la table. 
     */
    public EvenementJoueurEntreTable(int noTable, String nomUtilisateur)
    {
        // D�finir le num�ro de la table et le nom d'utilisateur du joueur 
    	// qui est entr�
    	intNoTable = noTable;
        strNomUtilisateur = nomUtilisateur;
    }
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement d'un joueur
	 * entrant dans la table et de le retourner.
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
			
			// Cr�er les noeuds de param�tre
			Element objNoeudParametreNoTable = objDocumentXML.createElement("parametre");
			Element objNoeudParametreNomUtilisateur = objDocumentXML.createElement("parametre");
			
			// Cr�er des noeuds texte contenant le num�ro de la table et le 
			// nom d'utilisateur des noeuds param�tre
			Text objNoeudTexteNoTable = objDocumentXML.createTextNode(Integer.toString(intNoTable));
			Text objNoeudTexteNomUtilisateur = objDocumentXML.createTextNode(strNomUtilisateur);
			
			// D�finir les attributs du noeud de commande
			objNoeudCommande.setAttribute("no", Integer.toString(information.obtenirNoCommande()));
			objNoeudCommande.setAttribute("type", "Evenement");
			objNoeudCommande.setAttribute("nom", "JoueurEntreTable");
			
			// On ajoute un attribut type qui va contenir le type
			// du param�tre
			objNoeudParametreNoTable.setAttribute("type", "NoTable");
			objNoeudParametreNomUtilisateur.setAttribute("type", "NomUtilisateur");
			
			// Ajouter les noeuds texte aux noeuds des param�tres
			objNoeudParametreNoTable.appendChild(objNoeudTexteNoTable);
			objNoeudParametreNomUtilisateur.appendChild(objNoeudTexteNomUtilisateur);
			
			// Ajouter les noeuds param�tres au noeud de commande
			objNoeudCommande.appendChild(objNoeudParametreNoTable);
			objNoeudCommande.appendChild(objNoeudParametreNomUtilisateur);
			
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
