package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementTableDetruite extends Evenement
{
	// D�claration d'une variable qui va garder le num�ro de la table qui a 
	// �t� d�truite
	private int intNoTable;

	/**
	 * Constructeur de la classe EvenementTableDetruite qui permet 
	 * d'initialiser le num�ro de la table. 
	 */
	public EvenementTableDetruite(int noTable)
	{
		// D�finir le num�ro de la table qui a �t� cr��e
		intNoTable = noTable;
		generateXML();
	}

	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er le noeud du param�tre
		Element objNoeudParametre = objDocumentXML.createElement("parametre");

		// Cr�er un noeud contenant le num�ro de la table du noeud param�tre
		Text objNoeudTexte = objDocumentXML.createTextNode(Integer.toString(intNoTable));

		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "TableDetruite");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre
		objNoeudParametre.setAttribute("type", "NoTable");

		// Ajouter le noeud texte au noeud du param�tre
		objNoeudParametre.appendChild(objNoeudTexte);

		// Ajouter le noeud param�tre au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametre);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);

	}
}