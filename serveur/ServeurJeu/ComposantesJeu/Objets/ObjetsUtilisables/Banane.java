package ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables;

import java.util.concurrent.ConcurrentHashMap;
import org.w3c.dom.Element;
import ServeurJeu.ComposantesJeu.Joueurs.InformationPartieHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;

/**
 * @author Fran�ois Gingras
 * changed Oloieri Lilian
 * last change August 2011
 */
public class Banane extends ObjetUtilisable 
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
	public static final String TYPE_OBJET = "Banane";
	
	//private static final long Seconds = 90;

	/**
	 * Constructeur de la classe Banane qui permet de d�finir les propri�t�s 
	 * propres � l'objet courant.
	 *
	 * @param in id : Le num�ro d'identification de l'objet
	 * @param boolean estVisible : Permet de savoir si l'objet doit �tre visible ou non
	 */
	public Banane(int id, boolean estVisible)
	{
		// Appeler le constructeur du parent
		super(id, estVisible, UID_OU_BANANE, PRIX, EST_LIMITE, PEUT_ETRE_ARME, TYPE_OBJET);
	}
	
	public void useObject(Element noeudCommande, String playerName, JoueurHumain objJoueurHumain)
	{
		 System.out.println("we are in objet banana");
		 InformationPartieHumain infoPartie = objJoueurHumain.obtenirPartieCourante();
		 
		 // La partie ici ne fait que s�lectionner le joueur qui sera affect�
         // Le reste se fait dans Banane.java
         noeudCommande.setAttribute("type", "Banane");
         boolean estHumain = false; //Le joueur choisi est'il humain?

         // On obtient la liste des joueurs humains, puis la liste des joueurs virtuels
         ConcurrentHashMap<String, JoueurHumain> listeJoueursHumains = infoPartie.obtenirTable().obtenirListeJoueurs();
         for (JoueurHumain objJoueur: listeJoueursHumains.values()) {
             if (objJoueur.obtenirNom().equals(playerName)) {
                 estHumain = true;
                 break;
             }
         }

         infoPartie.obtenirTable().preparerEvenementUtiliserObjet(
                 objJoueurHumain.obtenirNom(),
                 playerName,
                 "Banane",
                 "");

         //System.out.println("Protocole joueur 4189 Banane " + objJoueurHumain.obtenirNomUtilisateur() + " " + playerName);
         if (estHumain) {
             JoueurHumain joueur = infoPartie.obtenirTable().obtenirJoueurHumainParSonNom(playerName);
             if (joueur != null) {
                 joueur.obtenirPartieCourante().getBananaState().startBanana();
             }
             
         }else{
         	infoPartie.obtenirTable().obtenirJoueurVirtuelParSonNom(playerName).obtenirPartieCourante().getBananaState().startBanana();
         }


	}
	
}// end class