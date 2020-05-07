package ServeurJeu.ComposantesJeu.Joueurs;

import java.awt.Point;
import ServeurJeu.ComposantesJeu.Tables.Table;

/**
 * Master it's a creator and the observer of the game, 
 * but not participate actively. It must assure the synchronised 
 * observation of the game, but must be not observable by ordinary 
 * players.  
 * 
 * @author Oloieri Lilian
 *
 */

public class InformationPartieMaster extends InformationPartie {

	// D�claration d'une r�f�rence vers le gestionnaire de bases de donn�es
	//private final GestionnaireBDJoueur objGestionnaireBD;
	
	// D�claration d'une r�f�rence vers un joueur correspondant � cet
	// objet d'information de partie
	private final Joueur objJoueur;
	
	private int idDessin;
	
	// D�claration d'une position du joueur dans le plateau de jeu
	private Point objPositionJoueur;
	
	
	/**
	 * Constructeur de la classe InformationPartie qui permet d'initialiser
	 * les propri�t�s de la partie et de faire la r�f�rence vers la table.
	 * @param gestionnaireEv
	 * @param gestionnaireBD
	 * @param joueur
	 * @param tableCourante
	 */
	public InformationPartieMaster(Joueur joueur, Table tableCourante) {

		super(tableCourante, joueur);
		
		// Faire la r�f�rence vers le gestionnaire de base de donn�es
		//objGestionnaireBD = new GestionnaireBDJoueur(joueur);

		// Faire la r�f�rence vers le gestionnaire d'evenements
		//objGestionnaireEv = gestionnaireEv;

		// Faire la r�f�rence vers le joueur humain courant
		objJoueur = joueur;
		
	}// fin constructeur
	
	
	/**
	 * @param idDessin the idDessin to set
	 */
	public void setIdDessin(int idDessin) {
		this.idDessin = idDessin;
	}

	/**
	 * @return the idDessin
	 */
	public int getIdDessin() {
		return idDessin;
	}


	/**
	 * @return the objGestionnaireBD
		public GestionnaireBDJoueur getObjGestionnaireBD() {
		return objGestionnaireBD;
	} */

	
}
