package ServeurJeu.Evenements;


import org.w3c.dom.Element;
import org.w3c.dom.Text;
import ServeurJeu.ComposantesJeu.Joueurs.Joueur;

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

	// variable for user role
	private int userRole;

	// var for color used for the player clothes
	private int clothesColor;


	/**
	 * Constructeur de la classe EvenementJoueurEntreTable qui permet 
	 * d'initialiser le num�ro de la table et le nom d'utilisateur du 
	 * joueur qui vient d'entrer dans la table. 
	 * @param colorS 
	 */
	public EvenementJoueurEntreTable(int noTable, Joueur player)
	{
		// D�finir le num�ro de la table et le nom d'utilisateur du joueur 
		// qui est entr�
		intNoTable = noTable;
		strNomUtilisateur = player.obtenirNom();
		userRole = player.getRole();
		clothesColor = player.getPlayerGameInfo().getClothesColor();
		generateXML();
	}


	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er les noeuds de param�tre
		Element objNoeudParametreNoTable = objDocumentXML.createElement("parametre");
		Element objNoeudParametreNomUtilisateur = objDocumentXML.createElement("parametre");
		Element objNoeudParametreRoleUtilisateur = objDocumentXML.createElement("parametre");
		Element objNoeudParametreCouleurUtilisateur = objDocumentXML.createElement("parametre");

		// Cr�er des noeuds texte contenant le num�ro de la table et le 
		// nom d'utilisateur des noeuds param�tre
		Text objNoeudTexteNoTable = objDocumentXML.createTextNode(Integer.toString(intNoTable));
		Text objNoeudTexteNomUtilisateur = objDocumentXML.createTextNode(strNomUtilisateur);
		Text objNoeudTexteRoleUtilisateur = objDocumentXML.createTextNode(Integer.toString(userRole));
		Text objNoeudTexteCouleurUtilisateur = objDocumentXML.createTextNode(Integer.toString(clothesColor));


		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "JoueurEntreTable");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre
		objNoeudParametreNoTable.setAttribute("type", "NoTable");
		objNoeudParametreNomUtilisateur.setAttribute("type", "NomUtilisateur");
		objNoeudParametreRoleUtilisateur.setAttribute("type", "userRole");
		objNoeudParametreCouleurUtilisateur.setAttribute("type", "userColor");

		// Ajouter les noeuds texte aux noeuds des param�tres
		objNoeudParametreNoTable.appendChild(objNoeudTexteNoTable);
		objNoeudParametreNomUtilisateur.appendChild(objNoeudTexteNomUtilisateur);
		objNoeudParametreRoleUtilisateur.appendChild(objNoeudTexteRoleUtilisateur);
		objNoeudParametreCouleurUtilisateur.appendChild(objNoeudTexteCouleurUtilisateur);

		// Ajouter les noeuds param�tres au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametreNoTable);
		objNoeudCommande.appendChild(objNoeudParametreNomUtilisateur);
		objNoeudCommande.appendChild(objNoeudParametreRoleUtilisateur);
		objNoeudCommande.appendChild(objNoeudParametreCouleurUtilisateur);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);
	}

}
