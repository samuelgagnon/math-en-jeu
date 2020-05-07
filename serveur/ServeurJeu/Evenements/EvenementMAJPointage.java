package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Jean-Fran�ois Fournier
 */

public class EvenementMAJPointage extends Evenement
{
	// D�claration d'une variable qui va garder le nom d'utilisateur du
	// joueur dont le pointage est mis � jour
	private String strNomUtilisateur;

	// D�claration d'une variable qui va garder le nouveau pointage
	private int intPointage;

	/**
	 * Constructeur de la classe EvenementMAJPointage qui permet d'initialiser
	 * le nom d'utilisateur du joueur et son nouveau pointage 
	 */
	public EvenementMAJPointage(String nomUtilisateur, int nouveauPointage)
	{
		// D�finir le nom d'utilisateur du joueur
		strNomUtilisateur = nomUtilisateur;

		// D�finir le nouveau pointage du joueur
		intPointage = nouveauPointage;

		generateXML();
	}


	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er les noeuds des param�tres
		Element objNoeudParametreNom = objDocumentXML.createElement("parametre");
		Element objNoeudParametrePointage = objDocumentXML.createElement("parametre");

		// Cr�er un noeud contenant le nom d'utilisateur du noeud param�tre
		Text objNoeudTexteNom = objDocumentXML.createTextNode(strNomUtilisateur);
		Text objNoeudTextePointage = objDocumentXML.createTextNode(Integer.toString(intPointage));

		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "MAJPointage");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre pour le nom de l'utilisateur
		objNoeudParametreNom.setAttribute("type", "NomUtilisateur");

		// Ajouter le noeud texte avec le nom de l'utilisateur au noeud du param�tre
		objNoeudParametreNom.appendChild(objNoeudTexteNom);

		// On ajoute un attribut type qui va contenir le type
		// du param�tre pour le pointage
		objNoeudParametrePointage.setAttribute("type", "Pointage");

		// Ajouter le noeud texte avec le pointage au noeud du param�tre
		objNoeudParametrePointage.appendChild(objNoeudTextePointage);

		// Ajouter les noeuds param�tres au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametreNom);
		objNoeudCommande.appendChild(objNoeudParametrePointage);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);

	}
}
