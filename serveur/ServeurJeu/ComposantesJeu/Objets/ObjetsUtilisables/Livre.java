package ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ServeurJeu.ComposantesJeu.Joueurs.InformationPartieHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Livre extends ObjetUtilisable 
{
	// Cette constante sp�cifie le prix de l'objet courant
	public static final int PRIX = 1;

	// Cette constante affirme que l'objet courant n'est pas limit� 
	// lorsqu'on l'ach�te (c'est-�-dire qu'un magasin n'�puise jamais 
	// son stock de cet objet)
	public static final boolean EST_LIMITE = false;

	// Cette constante affirme que l'objet courant ne peut �tre arm� 
	// et d�pos� sur une case pour qu'un autre joueur tombe dessus. Elle 
	// ne peut seulement �tre utilis�e imm�diatement par le joueur
	public static final boolean PEUT_ETRE_ARME = false;

	// Cette constante d�finit le nom de cet objet
	public static final String TYPE_OBJET = "Livre";

	/**
	 * Constructeur de la classe Livre qui permet de d�finir les propri�t�s 
	 * propres � l'objet courant.
	 *
	 * @param in id : Le num�ro d'identification de l'objet
	 * @param boolean estVisible : Permet de savoir si l'objet doit �tre 
	 * 							   visible ou non
	 */
	public Livre(int id, boolean estVisible)
	{
		// Appeler le constructeur du parent
		super(id, estVisible, UID_OU_LIVRE, PRIX, EST_LIMITE, PEUT_ETRE_ARME, TYPE_OBJET);
	}

	public void useObject(Element noeudCommande, String playerName, JoueurHumain objJoueurHumain)	
	{
		InformationPartieHumain infoPartie = objJoueurHumain.obtenirPartieCourante();
		System.out.println("we are in livre");
		// Le livre est utilis� lorsqu'un joueur se fait poser une question
		// � choix de r�ponse. Le serveur renvoie alors une mauvaise r�ponse
		// � la question, et le client fera dispara�tre ce choix de r�ponse
		// parmi les choix possibles pour le joueur.
		// On obtient une mauvaise r�ponse � la derni�re question pos�e
		String mauvaiseReponse = infoPartie.getObjBoiteQuestions().obtenirMauvaiseReponse(infoPartie.obtenirQuestionCourante());

		Document docSortie = noeudCommande.getOwnerDocument();
		// Cr�er le noeud contenant le choix de r�ponse si c'�tait une question � choix de r�ponse
		Element objNoeudParametreMauvaiseReponse = docSortie.createElement("parametre");
		objNoeudParametreMauvaiseReponse.setAttribute("type", "MauvaiseReponse");
		objNoeudParametreMauvaiseReponse.appendChild(docSortie.createTextNode(mauvaiseReponse));
		noeudCommande.setAttribute("type", "Livre");
		noeudCommande.appendChild(objNoeudParametreMauvaiseReponse);
	}

}