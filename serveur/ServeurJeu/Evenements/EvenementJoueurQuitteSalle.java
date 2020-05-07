package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementJoueurQuitteSalle extends Evenement
{
	// D�claration d'une variable qui va garder le nom d'utilisateur du
	// joueur qui vient de quitter la salle
	private String strNomUtilisateur;

	/**
	 * Constructeur de la classe EvenementJoueurQuitteSalle qui permet 
	 * d'initialiser le nom d'utilisateur du joueur qui vient d'entrer. 
	 */
	public EvenementJoueurQuitteSalle(String nomUtilisateur)
	{
		// D�finir le nom d'utilisateur du joueur qui a quitt�
		strNomUtilisateur = nomUtilisateur;
		generateXML();
	}


	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er le noeud du param�tre
		Element objNoeudParametre = objDocumentXML.createElement("parametre");

		// Cr�er un noeud contenant le nom d'utilisateur du noeud param�tre
		Text objNoeudTexte = objDocumentXML.createTextNode(strNomUtilisateur);

		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "JoueurQuitteSalle");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre
		objNoeudParametre.setAttribute("type", "NomUtilisateur");

		// Ajouter le noeud texte au noeud du param�tre
		objNoeudParametre.appendChild(objNoeudTexte);

		// Ajouter le noeud param�tre au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametre);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);
	}
}
