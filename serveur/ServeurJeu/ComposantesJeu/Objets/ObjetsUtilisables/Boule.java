package ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Enumerations.TypeQuestion;
import ServeurJeu.ComposantesJeu.Joueurs.InformationPartieHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Questions.Question;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Boule extends ObjetUtilisable 
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
	public static final String TYPE_OBJET = "Boule";
	
	/**
	 * Constructeur de la classe qui permet de d�finir les propri�t�s 
	 * propres � l'objet courant.
	 *
	 * @param in id : Le num�ro d'identification de l'objet
	 * @param boolean estVisible : Permet de savoir si l'objet doit �tre 
	 * 							   visible ou non
	 */
	public Boule(int id, boolean estVisible)
	{
		// Appeler le constructeur du parent
		super(id, estVisible, UID_OU_BOULE, PRIX, EST_LIMITE, PEUT_ETRE_ARME, TYPE_OBJET);
	}
	
	public void useObject(Element noeudCommande, String playerName, JoueurHumain objJoueurHumain)
	{
		InformationPartieHumain infoPartie = objJoueurHumain.obtenirPartieCourante();
		 System.out.println("we are in objet boule");
		 Document docSortie = noeudCommande.getOwnerDocument();
		 
		 // La boule permettra � un joueur de changer de question si celle
         // qu'il s'est fait envoyer ne lui tente pas
         Question nouvelleQuestion = infoPartie.trouverQuestionAPoser(true);

         // On pr�pare l'envoi des informations sur la nouvelle question
         Element objNoeudParametreNouvelleQuestion = docSortie.createElement("parametre");
         objNoeudParametreNouvelleQuestion.setAttribute("type", "nouvelleQuestion");
         Element objNoeudParametreQuestion = docSortie.createElement("question");
         objNoeudParametreQuestion.setAttribute("id", Integer.toString(nouvelleQuestion.obtenirCodeQuestion()));
         objNoeudParametreQuestion.setAttribute("type", TypeQuestion.getValue(nouvelleQuestion.obtenirTypeQuestion()));
         objNoeudParametreQuestion.setAttribute("url", nouvelleQuestion.obtenirURLQuestion());
         objNoeudParametreNouvelleQuestion.appendChild(objNoeudParametreQuestion);
         noeudCommande.setAttribute("type", "Boule");
         noeudCommande.appendChild(objNoeudParametreNouvelleQuestion);

	}
	
}