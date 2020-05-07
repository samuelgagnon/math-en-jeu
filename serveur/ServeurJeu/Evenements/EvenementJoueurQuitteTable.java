package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementJoueurQuitteTable extends Evenement
{
	// D�claration d'une variable qui va garder le num�ro de la table que 
	// le joueur a quitt�
	private int intNoTable;

	// D�claration d'une variable qui va garder le nom d'utilisateur du 
	// joueur qui a quitt� la table
	private String strNomUtilisateur;

	/**
	 * Constructeur de la classe EvenementJoueurQuitteTable qui permet 
	 * d'initialiser le num�ro de la table et le nom d'utilisateur du 
	 * joueur qui a quitt� la table. 
	 */
	public EvenementJoueurQuitteTable(int noTable, String nomUtilisateur)
	{
		// D�finir le num�ro de la table et le nom d'utilisateur du joueur 
		// qui a quitt� la table
		intNoTable = noTable;
		strNomUtilisateur = nomUtilisateur;
		generateXML();
	}


	private void generateXML()
	{
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
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "JoueurQuitteTable");

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

	}
}
