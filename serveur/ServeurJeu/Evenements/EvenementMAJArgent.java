package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Fran�ois Gingras
 */

public class EvenementMAJArgent extends Evenement
{
	// D�claration d'une variable qui va garder le nom d'utilisateur du
	// joueur dont l'argent est mis � jour
	private String strNomUtilisateur;

	// D�claration d'une variable qui va garder le nouvel argent
	private int intArgent;

	/**
	 * Constructeur de la classe EvenementMAJArgent qui permet d'initialiser
	 * le nom d'utilisateur du joueur et son nouvel argent 
	 */
	public EvenementMAJArgent(String nomUtilisateur, int nouvelArgent)
	{
		// D�finir le nom d'utilisateur du joueur
		strNomUtilisateur = nomUtilisateur;

		// D�finir le nouvel argent du joueur
		intArgent = nouvelArgent;
		generateXML();
	}

	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er les noeuds des param�tres
		Element objNoeudParametreNom = objDocumentXML.createElement("parametre");
		Element objNoeudParametreArgent = objDocumentXML.createElement("parametre");

		// Cr�er un noeud contenant le nom d'utilisateur du noeud param�tre
		Text objNoeudTexteNom = objDocumentXML.createTextNode(strNomUtilisateur);
		Text objNoeudTexteArgent = objDocumentXML.createTextNode(Integer.toString(intArgent));

		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "MAJArgent");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre pour le nom de l'utilisateur
		objNoeudParametreNom.setAttribute("type", "NomUtilisateur");

		// Ajouter le noeud texte avec le nom de l'utilisateur au noeud du param�tre
		objNoeudParametreNom.appendChild(objNoeudTexteNom);

		// On ajoute un attribut type qui va contenir le type
		// du param�tre pour l'argent'
		objNoeudParametreArgent.setAttribute("type", "Argent");

		// Ajouter le noeud texte avec l'argent au noeud du param�tre
		objNoeudParametreArgent.appendChild(objNoeudTexteArgent);

		// Ajouter les noeuds param�tres au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametreNom);
		objNoeudCommande.appendChild(objNoeudParametreArgent);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);
	}

}
