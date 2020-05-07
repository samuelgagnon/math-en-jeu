package ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables;

import org.w3c.dom.Element;

import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;


/**
 * @author Oloieri Lilian
 *
 */

public class Brainiac extends ObjetUtilisable {

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
	public static final String TYPE_OBJET = "Brainiac";
	
	//private static final long Seconds = 90;

	/**
	 * Constructeur de la classe Braniac qui permet de d�finir les propri�t�s 
	 * propres � l'objet courant.
	 *
	 * @param in id : Le num�ro d'identification de l'objet
	 * @param boolean estVisible : Permet de savoir si l'objet doit �tre visible ou non
	 */
	public Brainiac(int id, boolean estVisible)
	{
		// Appeler le constructeur du parent
		super(id, estVisible, UID_OU_BRAINIAC, PRIX, EST_LIMITE, PEUT_ETRE_ARME, TYPE_OBJET);
	}
	
	public void useObject(Element noeudCommande, String playerName, JoueurHumain objJoueurHumain)
	{
		 System.out.println("we are in objet brainiac");

	}
	
}// end class
