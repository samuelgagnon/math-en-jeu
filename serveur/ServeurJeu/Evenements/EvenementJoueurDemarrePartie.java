package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementJoueurDemarrePartie extends Evenement
{
	// D�claration d'une variable qui va garder le nom d'utilisateur du 
	// joueur qui a d�marr� la partie
	private String strNomUtilisateur;

	// D�claration d'une variable qui va garder le num�ro Id du personnage 
	private int intIdPersonnage;     

	/**
	 * Constructeur de la classe EvenementJoueurDemarrePartie qui permet 
	 * d'initialiser le num�ro Id du personnage et le nom d'utilisateur du 
	 * joueur qui vient de d�marrer la partie. 
	 */
	public EvenementJoueurDemarrePartie(String nomUtilisateur, int idPersonnage)
	{
		// D�finir le num�ro Id du personnage et le nom d'utilisateur du joueur 
		// qui a d�marr� la partie
		intIdPersonnage = idPersonnage;
		strNomUtilisateur = nomUtilisateur;
		generateXML();
	}

	
	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er les noeuds de param�tre
		Element objNoeudParametreIdPersonnage = objDocumentXML.createElement("parametre");
		Element objNoeudParametreNomUtilisateur = objDocumentXML.createElement("parametre");

		// Cr�er des noeuds texte contenant le num�ro Id du personnage et le 
		// nom d'utilisateur des noeuds param�tre
		Text objNoeudTexteIdPersonnage = objDocumentXML.createTextNode(Integer.toString(intIdPersonnage));
		Text objNoeudTexteNomUtilisateur = objDocumentXML.createTextNode(strNomUtilisateur);

		// D�finir les attributs du noeud de commande
		//objNoeudCommande.setAttribute("no", Integer.toString(1));
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "JoueurDemarrePartie");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre
		objNoeudParametreIdPersonnage.setAttribute("type", "IdPersonnage");
		objNoeudParametreNomUtilisateur.setAttribute("type", "NomUtilisateur");

		// Ajouter les noeuds texte aux noeuds des param�tres
		objNoeudParametreIdPersonnage.appendChild(objNoeudTexteIdPersonnage);
		objNoeudParametreNomUtilisateur.appendChild(objNoeudTexteNomUtilisateur);

		// Ajouter les noeuds param�tres au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametreNomUtilisateur);
		objNoeudCommande.appendChild(objNoeudParametreIdPersonnage);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);
	}
}
