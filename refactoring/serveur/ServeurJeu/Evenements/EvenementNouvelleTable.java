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
public class EvenementNouvelleTable extends Evenement
{
	// D�claration d'une variable qui va garder le num�ro de la table qui a 
	// �t� cr��e
    private int intNoTable;
    
    // D�claration d'une variable qui va permettre de garder le temps de la partie
    private int intTempsPartie;
    
    /**
     * Constructeur de la classe EvenementNouvelleTable qui permet 
     * d'initialiser le num�ro de la table. 
     */
    public EvenementNouvelleTable(int noTable, int tempsPartie)
    {
        // D�finir le num�ro de la table qui a �t� cr��e et le temps de la partie
    	intNoTable = noTable;
    	intTempsPartie = tempsPartie;
    }
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement d'une 
	 * nouvelle table et de le retourner.
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
			Element objNoeudParametreTempsPartie = objDocumentXML.createElement("parametre");
			
			// Cr�er des noeuds contenant le num�ro de la table du noeud 
			// param�tre ainsi que le temps de la partie
			Text objNoeudTexteNoTable = objDocumentXML.createTextNode(Integer.toString(intNoTable));
			Text objNoeudTexteTempsPartie = objDocumentXML.createTextNode(Integer.toString(intTempsPartie));
			
			// D�finir les attributs du noeud de commande
			objNoeudCommande.setAttribute("no", Integer.toString(information.obtenirNoCommande()));
			objNoeudCommande.setAttribute("type", "Evenement");
			objNoeudCommande.setAttribute("nom", "NouvelleTable");
			
			// On ajoute un attribut type qui va contenir le type
			// du param�tre
			objNoeudParametreNoTable.setAttribute("type", "NoTable");
			objNoeudParametreTempsPartie.setAttribute("type", "TempsPartie");
			
			// Ajouter les noeuds texte aux noeuds de param�tre
			objNoeudParametreNoTable.appendChild(objNoeudTexteNoTable);
			objNoeudParametreTempsPartie.appendChild(objNoeudTexteTempsPartie);
			
			// Ajouter les noeuds param�tre au noeud de commande
			objNoeudCommande.appendChild(objNoeudParametreNoTable);
			objNoeudCommande.appendChild(objNoeudParametreTempsPartie);
			
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
