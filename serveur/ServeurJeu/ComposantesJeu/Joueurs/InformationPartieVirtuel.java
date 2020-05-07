package ServeurJeu.ComposantesJeu.Joueurs;

import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.objParametreIAMagasin;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.objParametreIAMinijeu;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.objParametreIAPiece;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.ptDxDy;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tDeplacementMoyen;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tNbJetonsMagasinAleatoire;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tNbJetonsMagasinBase;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tNbJetonsMinijeuAleatoire;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tNbJetonsMinijeuBase;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tNombrePointsMaximalChoixFinal;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tParametresIAObjetUtilisable;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tPourcentageChoix;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tPourcentageChoixAlternatifFinal;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tPourcentageChoixObjetLivre;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tPourcentageReponse;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tPourcentageReponseObjetLivre;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tTempsReflexionAleatoire;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.tTempsReflexionBase;
import static ServeurJeu.ComposantesJeu.Joueurs.ParametreIA.ttPointsRegionPiece;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import ClassesRetourFonctions.RetourVerifierReponseEtMettreAJourPlateauJeu;
import ClassesUtilitaires.UtilitaireNombres;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Cases.CaseSpeciale;
import ServeurJeu.ComposantesJeu.Objets.Objet;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Banane;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Brainiac;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Livre;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;
import ServeurJeu.ComposantesJeu.Objets.Pieces.Piece;
import ServeurJeu.ComposantesJeu.Tables.Table;

/**
 * 
 * @author Oloieri lilian
 *
 */

public class InformationPartieVirtuel extends InformationPartie implements ActivePlayer {

	// D�claration d'une variable qui va contenir le pointage de la
	// partie du joueur virtuel
	private int intPointage;

	private int intArgent;

	// D�claration de la position du joueur virtuel dans le plateau de jeu
	private Point objPositionJoueur;

	private int idDessin; 

	// object that describe and manipulate 
	// the Braniac state of the player
	private PlayerBrainiacState brainiacState;

	// code of the clothes color in the player's picture
	private int clothesColor;

	// D�claration d'une variable qui contient le nombre de fois
	// que le joueur virtuel a jou� � un mini-jeu
	private int intNbMiniJeuJoues;

	// D�claration d'une variable qui contient le nombre de fois
	// que le joueur virtuel a visit� un magasin
	private int intNbMagasinVisites;

	// D�claration d'un tableau contenant les temps o� le joueur
	// virtuel peut jouer � un mini-jeu (lorsque le temps arrive,
	// cela donne un jeton au joueur virtuel, lorsqu'il croisera 
	// une case de mini-jeu, il y jouera)
	private int tJetonsMiniJeu[];

	// D�claration d'un tableau contenant les temps o� le joueur
	// virtuel peut se servir d'un magasin (lorsque le temps arrive,
	// cela donne un jeton au joueur virtuel, lorsqu'il croisera un
	// magasin, il ira pour peut-�tre acheter un objet)
	private int tJetonsMagasins[];

	// Cette liste va contenir les magasins d�j� visit�s
	// par le joueur virtuel, pour emp�cher qu'il les visite
	// � plus d'une reprise
	private LinkedList<Magasin> lstMagasinsVisites;

	// Tableau contenant une r�f�rence vers le plateau de jeu
	private Case objttPlateauJeu[][];

	// Obtenir le nombre de lignes et de colonnes du plateau de jeu
	private int intNbLignes;
	private int intNbColonnes;

	// to not get twice bonus 
	private boolean wasOnFinishLine;

	// Cette matrice contiendra les valeurs indiquants quelles cases ont
	// �t� parcourue par l'algorithme
	private boolean matriceParcourue[][];

	// Cette matrice contiendra, pour chaque case enfil�e, de quelle case
	// celle-ci a �t� enfil�e. Cela nous permettra de trouver le chemin
	// emprunt� par l'algorithme.
	private Point matricePrec[][];

	// D�claration d'une matrice qui contiendra un pointage pour chaque
	// case du plateau de jeu, ce qui permettra de choisir le meilleur
	// coup � jouer
	private int matPoints[][];


	// player with max points to use Banana
	private String playerToUseBanana;
	private boolean estHumain;

	// D�claration d'une r�f�rence vers un joueur humain correspondant � cet
	// objet d'information de partie
	private final JoueurVirtuel objJoueurVirtuel;

	// D�claration d'une liste d'objets utilisables ramass�s par le joueur
	// virtuel
	private HashMap<Integer, ObjetUtilisable> lstObjetsUtilisablesRamasses;

	// Cette variable contient la case cibl�e par la joueur virtuel.
	// Il tentera de s'y rendre. Cette case sera choisie selon 
	// sa valeur en points et le type de joueur virtuel, en g�n�ral,
	// cette case poss�de une pi�ce, un objet ou un magasin.
	private Point objPositionFinaleVisee;

	// Cette variable conserve la raison pour laquelle le joueur
	// virtuel tente d'atteindre la position finale. Ceci est utile
	// pour d�tecter si, par exemple, l'objet que le joueur virtuel
	// voulait prendre n'existe plus.
	private int intRaisonPositionFinale;

	// relative time the last change of points or get the finish line
	private int pointsFinalTime;

	// Cette variable contient le niveau de difficult� du joueur virtuel
	private final int intNiveauDifficulte;

	// Constante pour la compilation conditionnelle
	private static final boolean ccDebug = false;

	// D�claration d'un objet random pour g�n�rer des nombres al�atoires
	private final Random objRandom = new Random();

	private PlayerBananaState bananaState;   

	public InformationPartieVirtuel(JoueurVirtuel objJoueurVirtuel, Table table, int idPersonnage) 
	{

		super(table, objJoueurVirtuel);
		this.objJoueurVirtuel = objJoueurVirtuel;

		// Banana state
		this.bananaState = new PlayerBananaState(objJoueurVirtuel);
		// Braniac state
		this.brainiacState = new PlayerBrainiacState(objJoueurVirtuel);

		if (idPersonnage == -1)
		{
			// Choisir un id de personnage al�atoirement
			intIdPersonnage = genererNbAleatoire(ParametreIA.NOMBRE_PERSONNAGE_ID) + 1;
		}
		else
		{
			// Affecter le id personnage pour ce joueur
			intIdPersonnage = idPersonnage;
		}

		// Cr�er la liste des objets utilisables qui ont �t� ramass�s
		lstObjetsUtilisablesRamasses = new HashMap<Integer, ObjetUtilisable>();

		// Tableau contenant une r�f�rence vers le plateau de jeu
		objttPlateauJeu = objTable.obtenirPlateauJeuCourant();

		// Obtenir le nombre de lignes et de colonnes du plateau de jeu
		intNbLignes = objttPlateauJeu.length;
		intNbColonnes = objttPlateauJeu[0].length;

		// to not get twice bonus
		setWasOnFinish(false);

		// Initialiser les matrices
		matriceParcourue = new boolean[intNbLignes][intNbColonnes];
		matricePrec = new Point[intNbLignes][intNbColonnes];
		matPoints = new int[intNbLignes][intNbColonnes];

		// D�terminer les temps des jetons des minijeus
		determinerJetonsMiniJeu();

		// Au d�part, le joueur virtuel n'a jou� aucun mini-jeu
		//intNbMiniJeuJoues = 0;

		// D�terminer les temps des jetons pour les magasins
		determinerJetonsMagasins();

		// Au d�part, le joueur virtuel n'a pas visit� de magasin
		//intNbMagasinVisites = 0;

		// Cr�er une liste de magasin d�j� visit� vide
		lstMagasinsVisites = new LinkedList<Magasin>();

		// Cr�ation du profil du joueur virtuel
		// to have virtual players of all difficulty levels
		intNiveauDifficulte = objRandom.nextInt(4);		

	}

	public void setClothesColor(int color) {
		this.clothesColor = color;
	}


	public int getClothesColor() {
		return clothesColor;
	}

	public HashMap<Integer, ObjetUtilisable> obtenirListeObjetsRamasses()
	{
		return lstObjetsUtilisablesRamasses;
	}



	public int obtenirIdPersonnage()
	{
		return intIdPersonnage;
	}	

	/* Cette fonction permet d'obtenir le pointage du joueur virtuel
	 */
	public int obtenirPointage()
	{
		return intPointage;
	}

	/* Cette fonction permet d'obtenir l'argent du joueur virtuel
	 */
	public int obtenirArgent()
	{
		return intArgent;
	}

	public void definirPointage(int valeur)
	{
		intPointage = valeur;
	}

	public void addPoints(int value)
	{
		intPointage += value;
	}

	public void definirArgent(int valeur)
	{
		intArgent = valeur;
	}

	public void addMoney(int value)
	{
		intArgent += value;
	}

	public void definirPositionJoueurVirtuel(Point pos)
	{
		objPositionJoueur = new Point(pos.x, pos.y);
	}

	public Point obtenirPositionJoueur()
	{
		return objPositionJoueur;
	}	

	protected void enleverObjet(int uidObjet)
	{
		for(ObjetUtilisable objObjet : lstObjetsUtilisablesRamasses.values()){

			if (objObjet.obtenirUniqueId() == uidObjet)
			{
				lstObjetsUtilisablesRamasses.remove(objObjet.obtenirId());
				break;
			}

		}
	}


	/* Cette fonction calcule le nombre d'objets que le joueur
	 * virtuel poss�de du type uidObjet
	 */
	protected int nombreObjetsPossedes(int uidObjet)
	{
		int intNbObjets = 0;

		for(ObjetUtilisable objObjet : lstObjetsUtilisablesRamasses.values()){

			if (objObjet.obtenirUniqueId() == uidObjet)
			{
				intNbObjets++;
			}
		}        

		return intNbObjets;
	}

	private RetourVerifierReponseEtMettreAJourPlateauJeu 
	verifierReponseEtMettreAJourPlateauJeu(Point objPositionDesiree) {

		// D�claration de l'objet de retour
		RetourVerifierReponseEtMettreAJourPlateauJeu objRetour = null;

		int bonus = 0;
		Point positionJoueur = obtenirPositionJoueur();
		int deplacementJoueur = 0;


		// Si la position en x est diff�rente de celle d�sir�e, alors
		// c'est qu'il y a eu un d�placement sur l'axe des x
		if (positionJoueur.x != objPositionDesiree.x) {
			deplacementJoueur = Math.abs(objPositionDesiree.x - positionJoueur.x);
		} // Si la position en y est diff�rente de celle d�sir�e, alors
		// c'est qu'il y a eu un d�placement sur l'axe des y
		else if (positionJoueur.y != objPositionDesiree.y) {
			deplacementJoueur = Math.abs(objPositionDesiree.y - positionJoueur.y);
		}

		if (deplacementJoueur == 1 && getBananaState().isUnderBananaEffects()) {
			addPoints(-1);
		}

		// On appelle jamais cette fonction si le joueur virtuel rate
		// la question
		boolean bolReponseEstBonne = true;

		// D�claration d'une r�f�rence vers l'objet ramass�
		ObjetUtilisable objObjetRamasse = null;

		// D�claration d'une r�f�rence vers l'objet subi
		ObjetUtilisable objObjetSubi = null;

		String collision = "";

		// D�claration d'une r�f�rence vers le magasin recontr�
		Magasin objMagasinRencontre = null;


		// Calculer le nouveau pointage du joueur
		addPoints(getPointsByMove(deplacementJoueur));

		// Si la case de destination est une case de couleur, alors on
		// v�rifie l'objet qu'il y a dessus et si c'est un objet utilisable,
		// alors on l'enl�ve et on le donne au joueur, sinon si c'est une
		// pi�ce on l'enl�ve et on met � jour le pointage du joueur, sinon
		// on ne fait rien
		Case objCaseDestination = null;
		if(objTable.getCase(objPositionDesiree.x, objPositionDesiree.y) != null)
			objCaseDestination = objTable.getCase(objPositionDesiree.x, objPositionDesiree.y);
		if (objCaseDestination != null && objCaseDestination instanceof CaseCouleur) {
			// Faire la r�f�rence vers la case de couleur
			CaseCouleur objCaseCouleurDestination = (CaseCouleur)objCaseDestination;

			// S'il y a un objet sur la case, alors on va faire l'action
			// tout d�pendant de l'objet (pi�ce, objet utilisable ou autre)
			if (objCaseCouleurDestination.obtenirObjetCase() != null) {
				// Si l'objet est un objet utilisable, alors on l'ajoute �
				// la liste des objets utilisables du joueur
				if (objCaseCouleurDestination.obtenirObjetCase() instanceof ObjetUtilisable) {

					if (objCaseCouleurDestination.obtenirObjetCase() instanceof Brainiac) {

						getBrainiacState().putTheOneBrainiac();
						objTable.preparerEvenementUtiliserObjet(objJoueurVirtuel.obtenirNom(), objJoueurVirtuel.obtenirNom(), "Brainiac", "");

						// Enlever l'objet de la case du plateau de jeu
						objCaseCouleurDestination.definirObjetCase(null);

						// On va dire aux clients qu'il y a eu collision avec cet objet
						collision = "Brainiac";

					} else {
						// Faire la r�f�rence vers l'objet utilisable
						ObjetUtilisable objObjetUtilisable = (ObjetUtilisable)objCaseCouleurDestination.obtenirObjetCase();

						// Garder la r�f�rence vers l'objet utilisable pour l'ajouter � l'objet de retour
						objObjetRamasse = objObjetUtilisable;

						// Ajouter l'objet ramass� dans la liste des objets du joueur courant
						lstObjetsUtilisablesRamasses.put(new Integer(objObjetUtilisable.obtenirId()), objObjetUtilisable);

						// Enlever l'objet de la case du plateau de jeu
						objCaseCouleurDestination.definirObjetCase(null);

						// On va dire aux clients qu'il y a eu collision avec cet objet
						collision = objObjetUtilisable.obtenirTypeObjet();
					}

				} else if (objCaseCouleurDestination.obtenirObjetCase() instanceof Piece) {

					// Faire la r�f�rence vers la pi�ce
					Piece objPiece = (Piece)objCaseCouleurDestination.obtenirObjetCase();

					// Mettre � jour l'argent du joueur
					addMoney(objPiece.obtenirMonnaie());

					// Enlever la pi�ce de la case du plateau de jeu
					objCaseCouleurDestination.definirObjetCase(null);

					collision = "piece";

					// TODO: Il faut peut-�tre lancer un algo qui va placer
					// 		 les pi�ces sur le plateau de jeu s'il n'y en n'a
					//		 plus

				} else if (objCaseCouleurDestination.obtenirObjetCase() instanceof Magasin) {
					// D�finir la collision
					collision = "magasin";

					// D�finir la r�f�rence vers le magasin rencontr�
					objMagasinRencontre = (Magasin)objCaseCouleurDestination.obtenirObjetCase();
				}
			}

			// S'il y a un objet � subir sur la case, alors on va faire une
			// certaine action (TODO: � compl�ter)
			if (objCaseCouleurDestination.obtenirObjetArme() != null) {
				// Faire la r�f�rence vers l'objet utilisable
				ObjetUtilisable objObjetUtilisable = (ObjetUtilisable)objCaseCouleurDestination.obtenirObjetArme();

				// Garder la r�f�rence vers l'objet utilisable � subir
				objObjetSubi = objObjetUtilisable;

				//TODO: Faire une certaine action au joueur

				// Enlever l'objet subi de la case
				objCaseCouleurDestination.definirObjetArme(null);
			}
		}

		//***********************************
		//for gametype tourmnament - bonus for finish line


		if (getWasOnFinish()) {
			bonus = objTable.verifyFinishAndSetBonus(objPositionDesiree);
			addPoints(bonus);
			if(bonus > 0) setWasOnFinish(true);
		}

		// Cr�er l'objet de retour
		objRetour = new RetourVerifierReponseEtMettreAJourPlateauJeu(bolReponseEstBonne, obtenirPointage(), obtenirArgent(), bonus);
		objRetour.definirObjetRamasse(objObjetRamasse);
		objRetour.definirObjetSubi(objObjetSubi);
		objRetour.definirNouvellePosition(objPositionDesiree);
		objRetour.definirCollision(collision);
		objRetour.definirMagasin(objMagasinRencontre);

		synchronized (objTable.obtenirListeJoueurs()) {
			// Pr�parer l'�v�nement de deplacement de personnage.
			// Cette fonction va passer les joueurs et cr�er un
			// InformationDestination pour chacun et ajouter l'�v�nement
			// dans la file de gestion d'�v�nements
			objTable.preparerEvenementJoueurDeplacePersonnage(objJoueurVirtuel, collision, 
					positionJoueur, objPositionDesiree, obtenirPointage(), obtenirArgent(), bonus, "");
		}

		definirPositionJoueurVirtuel(objPositionDesiree);
		setPointsFinalTime(objTable.obtenirTempsRestant());

		return objRetour;
	}

	/*
	protected void destruction() {
		this.brainiacState.destruction();
		this.brainiacState = null;
		this.bananaState.destruction();
		this.bananaState = null;		
	}*/

	/**
	 * 
	 * @return boolean -  true if we have a banana for tossing
	 */
	protected boolean controlHaveBanana() {

		for(ObjetUtilisable objObjet:lstObjetsUtilisablesRamasses.values()){
			if (objObjet instanceof Banane)
			{
				//System.out.print("Virtuel - Banana \n");
				return true;
			}
		}
		return false;
	}

	/**
	 * Method called after each step
	 * 
	 */
	protected void virtuelUseBanana() {

		//first control if we have Banana
		// if yes analyse if do it or not
		if(controlHaveBanana()) analyseIfDoIt();
	}

	/**
	 * Method used to analyse and decide if use Banana
	 * if true is called the method that apply Banana
	 */
	protected void analyseIfDoIt(){

		// first decision factor is the time
		// at the end of the game the points for the time is maximum
		int gameTime = objTable.getRelativeTime();

		// after - analyse the diffculty level
		// minus to decrese the probability to use Banana if 
		// the harder level 
		int diffLevel = - intNiveauDifficulte * 5;

		// emotions factor - use random
		int emoFactor = genererNbAleatoire(150);

		// the more Banana we have - more points to use it
		int bananaNumber = this.nombreObjetsPossedes(Objet.UID_OU_BANANE) * 10;

		// then find the player to use Banana and analyse it
		// the difference in the points and is the humain or virtual player
		int playerFactor = findPlayer();


		int total = gameTime + diffLevel + emoFactor + bananaNumber + playerFactor;

		//System.out.println("Banana!!!!!!!!!!!!!! " + total + " gameTime " + gameTime + " playerFactor " + playerFactor +
		//		" difflevel " + diffLevel + " emofactor " + emoFactor + " bananaNumber " + bananaNumber);

		if(total > 150)
		{
			objTable.preparerEvenementUtiliserObjet(objJoueurVirtuel.obtenirNom(), this.playerToUseBanana, "Banane", "");
			if(estHumain){
				objTable.obtenirJoueurHumainParSonNom(this.playerToUseBanana).obtenirPartieCourante().getBananaState().startBanana();

			}else
				objTable.obtenirJoueurVirtuelParSonNom(this.playerToUseBanana).obtenirPartieCourante().getBananaState().startBanana();

			this.enleverObjet(Objet.UID_OU_BANANE);
			//System.out.println("Banana!!!!!!!!!!!!!!");
		}	



	}// end method


	/**
	 * Method to get the player with max points
	 */
	protected int findPlayer()
	{
		int valPoints = 0; // points to return
		int max1 = 0;
		int max2 = 0;
		String max1User = "";
		String max2User = "";
		boolean estHumain1 = false;
		boolean estHumain2 = false;
		boolean isInBanana1 = false;
		boolean isInBanana2 = false;

		synchronized (objTable.obtenirListeJoueurs())
		{
			ArrayList<JoueurVirtuel> listeJoueursVirtuels = objTable.obtenirListeJoueursVirtuels();

			// On trouve les deux joueurs humains les plus susceptibles d'�tre affect�s
			for (JoueurHumain j : objTable.obtenirListeJoueurs().values())
			{
				if(j.obtenirPartieCourante().obtenirPointage() >= max1)
				{
					max2 = max1;
					max2User = max1User;
					estHumain2 = estHumain1;
					max1 = j.obtenirPartieCourante().obtenirPointage();
					max1User = j.obtenirNom();
					estHumain1 = true;
					isInBanana1 = j.obtenirPartieCourante().getBananaState().isUnderBananaEffects();
				}
				else if(j.obtenirPartieCourante().obtenirPointage() >= max2)
				{
					max2 = j.obtenirPartieCourante().obtenirPointage();
					max2User = j.obtenirNom();
					estHumain2 = true;
					isInBanana2 = j.obtenirPartieCourante().getBananaState().isUnderBananaEffects();
				}
			}

			if(listeJoueursVirtuels != null)
				for(JoueurVirtuel j : listeJoueursVirtuels)
				{
					//JoueurVirtuel j = (JoueurVirtuel)listeJoueursVirtuels.get(i);
					if(j.obtenirPartieCourante().obtenirPointage() >= max1)
					{
						max2 = max1;
						max2User = max1User;
						estHumain2 = estHumain1;
						max1 = j.obtenirPartieCourante().obtenirPointage();
						max1User = j.obtenirNom();
						estHumain1 = false;
						isInBanana1 = j.obtenirPartieCourante().getBananaState().isUnderBananaEffects();
					}
					else if(j.obtenirPartieCourante().obtenirPointage() >= max2)
					{
						max2 = j.obtenirPartieCourante().obtenirPointage();
						max2User = j.obtenirNom();
						estHumain2 = false;
						isInBanana1 = j.obtenirPartieCourante().getBananaState().isUnderBananaEffects();
					}
				}


			if(max1User.equals(objJoueurVirtuel.obtenirNom()))
			{
				// Celui qui utilise la banane est le 1er, alors on fait glisser le 2�me
				estHumain = estHumain2;
				playerToUseBanana = max2User;
				if(estHumain) valPoints = ((max2 - intPointage) * 50 / (max2 + 2) )+ 10;
				else valPoints = ((max2 - intPointage) * 50/ (max2 + 2)) ;

				//System.out.println((max2 - intPointage) * 50/ (max2 + 2) + " : ici banane");

			}
			else
			{
				// Celui qui utilise la banane n'est pas le 1er, alors on fait glisser le 1er
				estHumain = estHumain1;
				playerToUseBanana = max1User;
				if(estHumain) valPoints = ((max1 - intPointage) * 50  / (max1 + 2)) + 10;
				else valPoints = ((max1 - intPointage)* 50 / (max1 + 2)) ;

				//System.out.println((max1 - intPointage) * 50/ (max1 + 2) + " : ici banane");
			}
		}

		return valPoints;
	}

	/*
	 * Cette fonction s'occupe de d�placer le joueur virtuel s'il a bien r�pondu
	 * � la question, met � jour le plateau de jeu, envoie les �v�nements aux autres joueurs
	 * et modifie le pointage, l'argent et la position du joueur virtuel
	 */
	private void deplacerJoueurVirtuelEtMajPlateau(Point objNouvellePosition)
	{

		RetourVerifierReponseEtMettreAJourPlateauJeu objRetour = null;
		Case objCaseDestination = null;
		ObjetUtilisable objObjetRamasse = null;

		if(objJoueurVirtuel.getBoolStopThread() == false){
			objRetour = verifierReponseEtMettreAJourPlateauJeu(objNouvellePosition);
			objObjetRamasse = objRetour.obtenirObjetRamasse();
		}
		if(objJoueurVirtuel.getBoolStopThread() == false){
			objCaseDestination = objttPlateauJeu[objNouvellePosition.x][objNouvellePosition.y];//Table().getCase(objNouvellePosition.x, objNouvellePosition.y);
		}
		if (ccDebug)
		{
			System.out.println("Nouvelle position: "  + objPositionJoueur.x + "," + 
					objPositionJoueur.y);
		}

		// Si le joueur virtuel a atteint le WinTheGame, on arr�te la partie
		//if(this.obtenirTable().getObjSalle().getGameType().equals("Tournament") && objNouvellePosition.equals(this.obtenirTable().obtenirPositionWinTheGame())) this.obtenirTable().arreterPartie(this.obtenirNom());

		if (objCaseDestination instanceof CaseSpeciale)
		{
			// �mulation du mini-jeu

			// Pour l'instant, il n'y a qu'un type de mini-jeu, donc
			// on ne lui fait jouer que balle-au-mur
			int intTypeMiniJeu = ParametreIA.MINIJEU_BALLE_AU_MUR;

			// On d�termine le temps que va passer le joueur � jouer
			int intTempsJeu = determinerTempsJeuMiniJeu(intTypeMiniJeu);

			// On s'assure que ce temps ne d�passe pas le temps restant
			if (intTempsJeu > (objTable.obtenirTempsRestant() - ParametreIA.TEMPS_SURETE_MINIJEU_FIN_PARTIE))
			{
				intTempsJeu = objTable.obtenirTempsRestant() - ParametreIA.TEMPS_SURETE_MINIJEU_FIN_PARTIE;
			}

			if (intTempsJeu < 0)
			{
				intTempsJeu = 0;
			}

			// On d�termine le nombre de points que le joueur virtuel
			// fera selon le temps pris pour jouer
			int intPointsJeu = determinerPointsJeuMiniJeu(intTypeMiniJeu, intTempsJeu);

			//---------------------------------------
			if (ccDebug)
			{
				System.out.println("D�but du mini jeu");
				System.out.println("Temps="+intTempsJeu);
				System.out.println("Points="+intPointsJeu);
			}
			//----------------------------------------

			// On fait une pause pour le laisser jouer
			objJoueurVirtuel.pause(intTempsJeu * 1000);

			// On incr�mente les points du joueur virtuel
			intPointage += intPointsJeu;

			// Pr�parer un �v�nement pour les autres joueurs de la table
			// pour qu'il se tienne � jour du pointage de ce joueur
			objTable.preparerEvenementMAJPointage(objJoueurVirtuel, intPointage);

			// On incr�mente le compteur de mini-jeu
			intNbMiniJeuJoues++;
		}
		else if (objCaseDestination instanceof CaseCouleur &&
				((CaseCouleur)objCaseDestination).obtenirObjetCase() instanceof Magasin)
		{

			// Temps de pause pour que le joueur virtuel
			// pense � ce qu'il ach�te
			int intTempsReflexion = 0;

			// Pour d�cision de l'objet
			int intPlusGrand = -9999999;
			int intIndicePlusGrand = -1;

			// D�cision d'acheter quelque chose ou non
			boolean bolDecision;

			// Aller chercher une r�f�rence vers le magasin
			Magasin objMagasin = (Magasin)((CaseCouleur)objCaseDestination).obtenirObjetCase();

			// Aller chercher une r�f�rence vers la liste des objets du magasin
			ArrayList<ObjetUtilisable> lstObjetsMagasins = objMagasin.obtenirListeObjetsUtilisables();
			ArrayList<ObjetUtilisable> lstCopieObjetsMagasins = new ArrayList<ObjetUtilisable>();

			synchronized (lstObjetsMagasins)
			{
				// Faire une copie de la liste des objets du magasin
				//(Vector)objMagasin.obtenirListeObjetsUtilisables().clone();

				// Copier tous les objets du magasin
				for (int i = 0; i < lstObjetsMagasins.size(); i++)
				{
					// Aller chercher l'objet du magasin
					ObjetUtilisable objObjet = (ObjetUtilisable)lstObjetsMagasins.get(i);

					// Cr�er une copie
					ObjetUtilisable objCopieObjet = new ObjetUtilisable(objObjet.obtenirId(),
							objObjet.estVisible(), objObjet.obtenirUniqueId(),
							objObjet.obtenirPrix(), objObjet.obtenirPeutEtreArme(),
							objObjet.obtenirEstLimite(), objObjet.obtenirTypeObjet());

					// Ajouter la copie � notre liste
					lstCopieObjetsMagasins.add(objCopieObjet);
				}
			}

			/**********************************
			 *
			 * Maintenant, on fait les calculs sur la copie de la liste
			 * d'objets. Il est possible qu'un ou que des joueurs
			 * ach�tent les objets pendant ce temps. Si l'objet choisit
			 * n'y est plus apr�s les calculs, le joueur
			 * virtuel va passer son tour et n'ach�tera rien
			 *
			 **********************************/

			// Si le magasin ne poss�de aucun item, si le joueur
			// virtuel a atteint sa limite d'objets ou si le magasin
			// est dans la liste des magasins � ne pas visiter, alors le
			// temps de r�flexion est de 0 et la d�cision est de ne
			// rien acheter
			if (lstCopieObjetsMagasins.size() >= 0 && !lstMagasinsVisites.contains(objMagasin))
			{
				intTempsReflexion = obtenirTempsReflexionAchat();

				// Pour chaque objet de la liste, on va attribuer un pointage
				int tPointageObjets[] = new int[lstCopieObjetsMagasins.size()];

				for (int i = 0; i < lstCopieObjetsMagasins.size(); i ++)
				{
					// Aller chercher l'objet
					ObjetUtilisable objObjetAVendre = (ObjetUtilisable) lstCopieObjetsMagasins.get(i);

					// Si le joueur virtuel n'a pas assez d'argent pour acheter
					// l'objet, alors on donne un pointage tr�s bas
					if (intArgent < objObjetAVendre.obtenirPrix())
					{
						tPointageObjets[i] = -9999999;
					}

					// Attribuer des points � l'objet selon le nombre
					// d'objets de ce type d�j� en possession
					else
					{
						//tPointageObjets[i] = -9999999;
						//TODO: r�gler �a
						tPointageObjets[i] = tParametresIAObjetUtilisable[objObjetAVendre.obtenirUniqueId()].intValeurPoints - 
						tParametresIAObjetUtilisable[objObjetAVendre.obtenirUniqueId()].intPointsEnleverQuantite * 
						nombreObjetsPossedes(objObjetAVendre.obtenirUniqueId());
					}

				}

				// Choisir l'objet
				for (int i = 0; i < tPointageObjets.length; i ++)
				{
					if (tPointageObjets[i] > intPlusGrand)
					{
						intPlusGrand = tPointageObjets[i];
						intIndicePlusGrand = i;
					}
				}

				if (intIndicePlusGrand >= 0 && intIndicePlusGrand < tPointageObjets.length)
				{
					bolDecision = true;
				}
				else
				{
					bolDecision = false;
				}

			}
			else
			{
				intTempsReflexion = 0;
				bolDecision = false;
			}

			// On incr�ment le compteur de magasin visit�s
			intNbMagasinVisites++;

			// Ajouter ce magasin � la liste des magasins d�j� visit�s
			if (!lstMagasinsVisites.contains(objMagasin))
			{
				lstMagasinsVisites.add(objMagasin);
			}

			//---------------------------------------
			if (ccDebug)
			{
				System.out.println("***************** Magasin visite");
			}
			//----------------------------------------

			objJoueurVirtuel.pause(intTempsReflexion * 1000);

			if (bolDecision && !objJoueurVirtuel.getBoolStopThread())
			{
				// Aller chercher, dans la copie, l'indice de l'objet � acheter
				int intObjetId = ((ObjetUtilisable)lstCopieObjetsMagasins.get(intIndicePlusGrand)).obtenirId();

				// Permet de savoir si l'achat a eu lieu
				boolean bolAchatOk;

				// Va contenir l'objet 
				ObjetUtilisable objObjet = null;

				// V�rifier si l'objet existe encore
				synchronized(objMagasin)
				{
					if (objMagasin.objetExiste(intObjetId))
					{
						// Aller chercher l'objet choisit
						objObjet = (ObjetUtilisable)lstObjetsMagasins.get(intIndicePlusGrand);

						// Acheter l'objet
						objObjet = objMagasin.acheterObjet(objObjet.obtenirId(), objTable.obtenirProchainIdObjet());

						// On indique que l'achat a eu lieu puis on sort de la s.c.
						bolAchatOk = true;
					}
					else
					{
						bolAchatOk = false;
					}
				}

				if (bolAchatOk)
				{
					// Ajouter l'objet dans la liste
					lstObjetsUtilisablesRamasses.put(new Integer(objObjet.obtenirId()), objObjet);

					// D�frayer les co�ts
					intArgent -= objObjet.obtenirPrix();

					//---------------------------------------
					if (ccDebug)
					{
						System.out.println("***************** Objet achet�: " + objObjet.obtenirTypeObjet());
						System.out.println("***************** Cout: " + objObjet.obtenirPrix());
						System.out.println("***************** Prochain id: " + objTable.obtenirProchainIdObjet());

						System.out.print("***** Liste objets dans le magasin apr�s achat:");
						for (int i = 0; i < objMagasin.obtenirListeObjetsUtilisables().size(); i++)
						{
							System.out.print(((ObjetUtilisable)objMagasin.obtenirListeObjetsUtilisables().get(i)).obtenirTypeObjet() + 
									"(" + ((ObjetUtilisable)objMagasin.obtenirListeObjetsUtilisables().get(i)).obtenirId() + "),");
						}
						System.out.println("");
					}
					//---------------------------------------

					// Pr�parer un �v�nement pour les autres joueurs de la table
					// pour qu'il se tienne � jour de l'argent de ce joueur
					objTable.preparerEvenementMAJArgent(objJoueurVirtuel, intArgent);

				}
				else
				{
					if (ccDebug)
					{
						System.out.println("Objet envol� apr�s r�flexion (" + objJoueurVirtuel.obtenirNom() +
								", " + objPositionJoueur.x + "-" + objPositionJoueur.y + 
								", " + System.currentTimeMillis() + ")");
					}
				}

			}

		}

		if (objObjetRamasse instanceof Livre)
		{
			//---------------------------------------
			if (ccDebug)
			{
				System.out.println("Objet ramasse: Livre");
			}
			//---------------------------------------
		}

		if (objRetour.obtenirCollision().equals("piece"))
		{
			//---------------------------------------
			if (ccDebug)
			{
				System.out.println("Objet ramasse: Piece");
			}
			//---------------------------------------
		}

	}

	/* Cette fonction permet d'obtenir un tableau qui contient les pourcentages de
	 * choix de d�placement pour chaque grandeur de d�placement. Ces pourcentages
	 * sont bas�s sur le niveau de difficult� du joueur virtuel
	 */
	private int[] obtenirPourcentageChoix()
	{

		int tTableauSource[][];

		// D�terminer dans quel tableau on va chercher les pourcentages
		// de choix. Si le joueur poss�de l'objet Livre,
		// il va choisir des choix plus difficile car l'objet va l'aider
		if (nombreObjetsPossedes(Objet.UID_OU_LIVRE) > 0)
		{
			tTableauSource = tPourcentageChoixObjetLivre;
		}
		else
		{
			tTableauSource = tPourcentageChoix;
		}

		int intPourcentageCase[] = new int[ParametreIA.DEPLACEMENT_MAX];
		for (int i = 0; i < ParametreIA.DEPLACEMENT_MAX; i++)
		{
			intPourcentageCase[i] = tTableauSource[intNiveauDifficulte][i];
		}

		return intPourcentageCase;

	}

	/* Cette fonction d�termine le temps que jouera le joueur virtuel
	 * au mini-jeu. Ce temps permettra de conna�tre le nombre de points
	 * qu'il fera pendant le jeu.
	 */
	private int determinerTempsJeuMiniJeu(int intTypeMiniJeu)
	{
		// Un temps minimal que l'on donne au joueur pour ce jeu
		int intMinimum = 0;

		// Un temps maximal que l'on donne au joueur pour ce jeu
		int intMaximum = 0;

		// La valeur moyenne de la loi normale
		double dblMoyenne = 0.0;

		// La variance de la loi normale
		double dblVariance = 0.0;

		// La valeur � retourner
		int intTemps;

		switch(intTypeMiniJeu)
		{
		case ParametreIA.MINIJEU_BALLE_AU_MUR:

			switch (intNiveauDifficulte)
			{
			case ParametreIA.DIFFICULTE_FACILE:
				intMinimum = 24;
				intMaximum = 86;
				dblMoyenne = 47.00;
				dblVariance = 100.00;
				break;

			case ParametreIA.DIFFICULTE_MOYEN:
				intMinimum = 71;
				intMaximum = 135;
				dblMoyenne = 31.00;
				dblVariance = 225.00;
				break;

			case ParametreIA.DIFFICULTE_DIFFICILE: 
				intMinimum = 91;
				intMaximum = 190;
				dblMoyenne = 39.50;
				dblVariance = 225.00;
				break;
			}
			break;
		}


		// Maintenant, faire le calcul
		intTemps = intMinimum + UtilitaireNombres.genererNbAleatoireLoiNormale(dblMoyenne, dblVariance);

		if (intTemps > intMaximum)
		{
			intTemps = intMaximum;
		}
		else if (intTemps < intMinimum)
		{
			intTemps = intMinimum;
		}

		return intTemps;

	}

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
	 * @return the braniacState
	 */
	public PlayerBrainiacState getBrainiacState() {
		return brainiacState;
	}


	/**
	 * @param braniacState the braniacState to set
	 */
	public void setBraniacState(PlayerBrainiacState braniacState) {
		this.brainiacState = braniacState;
	}


	/**
	 * @return the bananaState
	 */
	public PlayerBananaState getBananaState() {
		return bananaState;
	}


	/**
	 * @param bananaState the bananaState to set
	 */
	public void setBananaState(PlayerBananaState bananaState) {
		this.bananaState = bananaState;
	}

	/* Cette fonction trouve le chemin le plus court entre deux points et
	 * le retourne sous forme de Vector. Le chemin retourn� est en ordre inverse
	 * (l'indice 0 correspondra au point d'arriv�e)
	 *
	 * @param: Point depart: Point de d�part du chemin
	 * @param: Point arrivee: Point d'arriv�e du chemin
	 */
	public ArrayList<Point> trouverCheminPlusCourt(Point depart, Point arrivee)
	{
		// Liste des points � traiter pour l'algorithme de recherche de chemin
		ArrayList<Point> lstPointsATraiter = new ArrayList<Point>();

		// Le chemin r�sultat que l'on retourne � la fonction appelante
		ArrayList<Point> lstResultat;

		// Point temporaire qui sert dans l'algorithme de recherche
		Point ptPosTemp = new Point();

		// Point d�fil� de la liste des points � traiter
		Point ptPosDefile;

		// Cette variable nous indiquera si l'algorithme a trouv� un chemin
		boolean bolCheminTrouve = false;

		// Variable pour boucler dans le tableau ptDxDy[]
		int dxIndex = 0;

		// Ce tableau servira � enfiler les cases de fa�ons al�atoire, ce qui
		// permettra de peut-�tre trouver diff�rents chemin
		int tRandom[] = {0,1,2,3};

		// Sert pour brasser tRandom
		int indiceA;
		int indiceB;
		int indiceNombreMelange;
		int valeurTemp;

		// On va faire 3 m�langes, ce sera suffisant
		for (indiceNombreMelange = 1; indiceNombreMelange <= 3; indiceNombreMelange++)
		{
			// Brasser al�atoirement le tableau al�atoire
			indiceA = genererNbAleatoire(4);
			indiceB = genererNbAleatoire(4); 

			// Permutter les deux valeurs
			valeurTemp = tRandom[indiceA];
			tRandom[indiceA] = tRandom[indiceB];
			tRandom[indiceB] = valeurTemp;
		}

		// Initialiser les objets pour la recherche de chemin
		for (int i = 0; i < intNbLignes; i++)
		{
			for (int j = 0; j < intNbColonnes; j++)
			{
				// On met chaque indice de la matrice des cases parcourues � false
				matriceParcourue[i][j] = false;

				// Chaque case pr�c�dente sera le point -1,-1
				matricePrec[i][j] = new Point(-1,-1);
			}
		}       

		// Enfiler notre position de d�part
		lstPointsATraiter.add(depart);
		matriceParcourue[depart.x][depart.y] = true;

		// On va boucler jusqu'� ce qu'il ne reste plus rien ou jusqu'�
		// ce qu'on arrive � l'arriv�e
		while (lstPointsATraiter.size() > 0 && bolCheminTrouve == false)
		{
			// D�filer une position
			ptPosDefile = (Point) lstPointsATraiter.get(0);
			lstPointsATraiter.remove(0);

			// V�rifier si on vient d'atteindre l'arriv�e
			if (ptPosDefile.x == arrivee.x && ptPosDefile.y == arrivee.y)
			{
				bolCheminTrouve = true;
				break;
			}


			// Enfiler les 4 cases accessibles depuis cette position  
			for (dxIndex = 0; dxIndex < 4; dxIndex++)
			{
				ptPosTemp.x = ptPosDefile.x + ptDxDy[tRandom[dxIndex]].x;
				ptPosTemp.y = ptPosDefile.y + ptDxDy[tRandom[dxIndex]].y;

				if (ptPosTemp.y >= 0 &&
						ptPosTemp.y < intNbColonnes && 
						ptPosTemp.x >= 0 &&
						ptPosTemp.x < intNbLignes &&
						matriceParcourue[ptPosTemp.x][ptPosTemp.y] == false &&
						objttPlateauJeu[ptPosTemp.x][ptPosTemp.y] != null)
				{
					// Ajouter la nouvelle case accessible
					lstPointsATraiter.add(new Point(ptPosTemp.x, ptPosTemp.y));

					// Indiquer que cette case est trait�e pour ne pas
					// l'enfiler � nouveau
					matriceParcourue[ptPosTemp.x][ptPosTemp.y] = true;

					// Conserver les traces pour savoir de quel case on a enfil�
					matricePrec[ptPosTemp.x][ptPosTemp.y].x = ptPosDefile.x;
					matricePrec[ptPosTemp.x][ptPosTemp.y].y = ptPosDefile.y;

				}
			}


		}

		if (bolCheminTrouve == true)
		{
			// Pr�parer le chemin de retour
			lstResultat = new ArrayList<Point>();

			// On part de l'arriv�e puis on retrace jusqu'au d�part
			ptPosTemp = arrivee;

			// Ajouter chaque case indiqu� dans matricePrec[] jusqu'� la
			// position de d�part
			while (ptPosTemp.x != depart.x || ptPosTemp.y != depart.y)
			{
				lstResultat.add(new Point(ptPosTemp.x, ptPosTemp.y));
				ptPosTemp = matricePrec[ptPosTemp.x][ptPosTemp.y];
			}

			// Ajouter la position de d�part
			lstResultat.add(new Point(depart.x, depart.y));

		}
		else
		{
			// Si on n'a pas trouv� de chemin, on retourne null
			lstResultat = null;
		}

		return lstResultat;

	}

	/* Cette fonction calcule les points pour un chemin. Les points sont bas�s sur
	 * le nombre de pi�ces que le chemin contient et aussi le type de case
	 * que le chemin contient au cas o� le joueur virtuel pr�f�rerait certaines cases.
	 */
	private int calculerPointsChemin(ArrayList<Point> lstPositions, Case objttPlateauJeu[][])
	{
		Point ptTemp;
		int intPoints = 0;

		boolean considererMiniJeu = determinerPretAJouerMiniJeu();
		boolean considererMagasin = determinerPretAVisiterMagasin();

		for (int i = 0; i < lstPositions.size() - 1; i++)
		{

			ptTemp = (Point) lstPositions.get(i);

			int intPointsCase = 0;

			if (objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseCouleur)
			{
				if (((CaseCouleur) objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof Piece)
				{
					// Piece sur la case
					intPointsCase = objParametreIAPiece.intPointsChemin;
				}
				else if (((CaseCouleur) objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof Magasin)
				{
					if (considererMagasin == true)
					{
						// Magasin sur la case
						intPointsCase = objParametreIAMagasin.intPointsChemin;
					}
				}

				else if (((CaseCouleur) objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof ObjetUtilisable)
				{
					ObjetUtilisable objObjet = (ObjetUtilisable)((CaseCouleur)objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase();
					if (objObjet.estVisible() && determinerPretARamasserObjet(objObjet.obtenirUniqueId()))
					{
						// Objet r�ponse sur la case
						intPointsCase = tParametresIAObjetUtilisable[objObjet.obtenirUniqueId()].intPointsChemin;
						intPointsCase -= tParametresIAObjetUtilisable[objObjet.obtenirUniqueId()].intPointsEnleverDistance * (nombreObjetsPossedes(objObjet.obtenirUniqueId()));
						if (intPointsCase < 0)
						{
							intPointsCase = 0;
						}
					}
				}

			}
			else if (objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseSpeciale)
			{
				if (considererMiniJeu == true)
				{
					// Mini-jeu sur la case
					intPointsCase = objParametreIAMinijeu.intPointsChemin;
				}
			}

			intPoints += intPointsCase;

		}

		return intPoints;
	}

	/*
	 * Cette fonction trouve une case interm�diaire qui permettra au joueur virtuel
	 * de progresser vers sa mission qu'est celle de se rendre � la case finale vis�e.
	 */
	private Point trouverPositionIntermediaire()
	{
		// Si on est d�j� sur le WinTheGame et qu'on a le pointage requis, restons l�!!
		// Peut-�tre que les joueurs virtuels essaient ensuite de se d�placer
		// mais le serveur refusera alors ils resteront vraiment l�
		if(this.obtenirPositionJoueur().equals(this.obtenirTable().getPositionPointFinish())) return this.obtenirPositionJoueur();

		// Variable contenant la position � retourner � la fonction appelante
		Point objPositionTrouvee;

		ArrayList<Point> lstPositions[] = new ArrayList[5];
		ArrayList<Point> lstPositionsTrouvees;
		int tPoints[] = new int[5];
		int intPlusGrand = -1;

		// Recherche de plusiuers chemins pour se rendre � la position finale
		for (int i = 0; i < 5; i++)
		{
			lstPositions[i] = trouverCheminPlusCourt(objPositionJoueur, objPositionFinaleVisee);

			// V�rifier si on a trouv� un chemin
			if (i == 0  && lstPositions[0] == null)
			{
				return new Point(objPositionJoueur.x, objPositionJoueur.y);
			}

			// On va calculer les points pour ce chemin
			tPoints[i] = calculerPointsChemin(lstPositions[i], objttPlateauJeu);

			// Trouver le plus grand chemin
			if (intPlusGrand == -1 || tPoints[i] > tPoints[intPlusGrand])
			{
				intPlusGrand = i;            	
			}
		}

		// Choisir le meilleur chemin
		lstPositionsTrouvees = lstPositions[intPlusGrand];

		if (ccDebug)
		{
			System.out.print("Chemin : ");
			for (int i = lstPositionsTrouvees.size()-1 ; i >=0 ; i--)
			{
				if (i < lstPositionsTrouvees.size()-1)
				{
					System.out.print(", ");
				}

				System.out.print("(" + ((Point)lstPositionsTrouvees.get(i)).x +"-" +
						((Point)lstPositionsTrouvees.get(i)).y + ")");
			}
			System.out.println("");
		}

		// Valeur du point de d�part (�gale � objPositionJoueur en principe)
		Point ptDepart = (Point) lstPositionsTrouvees.get(lstPositionsTrouvees.size() - 1);

		// Point temporaire qui nous permettra de parcourir la liste et trouver
		// o� le joueur virtuel avancera
		Point ptTemp;

		// Obtenir les pourcentages de choix pour les cases selon le niveau
		// de difficult�, on va modifier ces pourcentages par la suite car il peut
		// y avoir des trous qu'on veut �viter, des pi�ces que l'on veut ramasser ou
		// bien une case finale que l'on ne veut pas d�passer
		int intPourcentageCase[] = obtenirPourcentageChoix();
		int iIndiceTableau = 0;

		boolean bolConsidererMiniJeu = determinerPretAJouerMiniJeu();
		boolean bolConsidererMagasin = determinerPretAVisiterMagasin();

		// On part du d�but du chemin jusqu'� la fin et on trouve le premier croche
		for (int i = lstPositionsTrouvees.size() - 2; i >= 0 ; i--)
		{
			ptTemp = (Point) lstPositionsTrouvees.get(i);

			iIndiceTableau++;       

			// S'il y a un mini-jeu ici et que le joueur n'a pas de jeton
			// pour y jouer, alors on va mettre � 0 les possiblit�s
			// de choisir cette case
			if (bolConsidererMiniJeu == false && objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseSpeciale)
			{
				traiterCaseEliminerDansLigne(intPourcentageCase, iIndiceTableau -1);
			}

			// On v�rifie si le premier "croche" est ici
			if (ptTemp.x != ptDepart.x && ptTemp.y != ptDepart.y)
			{
				// Le premier "croche" est � ptTemp, c'est donc le d�placement
				// maximal que le joueur virtuel pourra faire
				traiterPieceTrouveeDansLigne(intPourcentageCase, iIndiceTableau - 2);
				break;
			}

			// S'il y a une pi�ce sur cette case, alors on s'assure que
			// le joueur virtuel ne la d�passera pas
			if (objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseCouleur)
			{
				if (((CaseCouleur) objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof Piece)   
				{
					traiterPieceTrouveeDansLigne(intPourcentageCase, iIndiceTableau - 1);
					break;    
				}
			}

			// S'il y a un mini-jeu et que le joueur � un jeton pour
			// un mini-jeu, on s'assure de ne pas d�passer cette case
			else if (objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseSpeciale && 
					bolConsidererMiniJeu == true)
			{
				traiterPieceTrouveeDansLigne(intPourcentageCase, iIndiceTableau - 1);
				break; 
			}

			// S'il y a un magasin et que le joueur � un jeton pour
			// le visiter, on s'assure de ne pas d�passer cette case
			else if (objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseCouleur && 
					((CaseCouleur)objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof Magasin &&
					bolConsidererMagasin == true)
			{
				traiterPieceTrouveeDansLigne(intPourcentageCase, iIndiceTableau - 1);
				break; 
			}

			// S'il y a un objet visible, alors on s'assurer de ne pas d�passer cette case
			else if (objttPlateauJeu[ptTemp.x][ptTemp.y] instanceof CaseCouleur &&
					((CaseCouleur)objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof ObjetUtilisable &&
					determinerPretARamasserObjet(((ObjetUtilisable)((CaseCouleur)objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase()).obtenirUniqueId()))
			{
				traiterPieceTrouveeDansLigne(intPourcentageCase, iIndiceTableau - 1);
				break; 
			}


			if (iIndiceTableau > ParametreIA.DEPLACEMENT_MAX-1)
			{
				break;
			}  

		}

		// Si on est pr�s de la position finale, on s'assure de ne pas la d�passer
		if (lstPositionsTrouvees.size() <= ParametreIA.DEPLACEMENT_MAX)
		{
			traiterPieceTrouveeDansLigne(intPourcentageCase, lstPositionsTrouvees.size() - 2); 
		}

		// Effectuer le choix
		int intPourcentageAleatoire;

		// On g�n�re un nombre entre 1 et 100
		intPourcentageAleatoire = genererNbAleatoire(100)+1;

		int intValeurAccumulee = 0;
		int intDecision = 0;

		// On d�termine � quel d�cision cela appartient
		for (int i = 0 ; i <= ParametreIA.DEPLACEMENT_MAX-1 ; i++)
		{
			intValeurAccumulee += intPourcentageCase[i];
			if (intPourcentageAleatoire <= intValeurAccumulee)
			{
				intDecision = i + 1;
				break;
			}
		}

		// On peut donc retourner la case choisie par le joueur virtuel
		ptTemp = (Point)lstPositionsTrouvees.get(lstPositionsTrouvees.size() - 1 - intDecision);
		objPositionTrouvee = new Point(ptTemp.x, ptTemp.y);


		//--------------------------------
		if (ccDebug)
		{
			int intTempsEcoule = objTable.obtenirTempsTotal() * 60 - objTable.obtenirTempsRestant();
			System.out.println("Temps ecoule:" + intTempsEcoule);
			System.out.println("Magasins: " + intNbMagasinVisites + "/" + 
					obtenirNombreJetonsDisponibles(tJetonsMagasins, intTempsEcoule));
			System.out.println("Minijeu: " + intNbMiniJeuJoues + "/" + 
					obtenirNombreJetonsDisponibles(tJetonsMiniJeu, intTempsEcoule));
			System.out.println("Pointage: " + intPointage);

			System.out.print("Liste objets: ");
			if (lstObjetsUtilisablesRamasses.size() > 0)
			{

				Set<Map.Entry<Integer,ObjetUtilisable>> lstEnsembleObjets = lstObjetsUtilisablesRamasses.entrySet();
				Iterator<Entry<Integer, ObjetUtilisable>> objIterateurListeObjets = lstEnsembleObjets.iterator();
				int i = 0;

				while (objIterateurListeObjets.hasNext())
				{
					ObjetUtilisable objObjet = (ObjetUtilisable)(((Map.Entry<Integer,ObjetUtilisable>)(objIterateurListeObjets.next())).getValue());

					if (i > 0)
					{
						System.out.print(", ");
					}

					if (objObjet instanceof Livre)
					{
						System.out.print("Livre");
					}
					System.out.print("(" + objObjet.obtenirId() + ")");
					i++;
				}
				System.out.println("");
			}
			else
			{
				System.out.println("Aucun objet");
			}

			System.out.println("Position du joueur: " + objPositionJoueur.x + "," + 
					objPositionJoueur.y);        
			System.out.println("Position trouvee: " + objPositionTrouvee.x + "," + 
					objPositionTrouvee.y);           
			System.out.println("Position a atteindre: " + objPositionFinaleVisee.x + "," + 
					objPositionFinaleVisee.y);
		}
		//--------------------------------

		return objPositionTrouvee;

	}

	/* Cette fonction permet d'attribuer de l'importance � une case en
	 * lui attribuant des points
	 *
	 * ptCase: La case � traiter
	 *
	 * pointCase: Le pointage � ajouter � la case
	 *
	 * pointAleatoire: on ajoute entre [0, pointAleatoire[ au pointage, 
	 *                 ce qui ajoute un �l�ment al�atoire
	 *
	 * limitDistance: On enl�ve un nombre de points par coup de distance,
	 *                limit� � limitDistance, ce qui permet d'attirer le
	 *                joueur virtuel vers des cases importantes m�me si
	 *                elles sont tr�s loin (emp�che les points n�gatifs)
	 *
	 * pointDistance: Nombre de points qu'on d�cr�mente par coup de distance
	 *
	 * ttPointsRegion: Un tableau contenant les informations pour 
	 *                 ajouter un nombre de points aux cases adjacentes, 
	 *                 ce qui permet de valoriser les regroupements de cases
	 *                 importantes
	 *
	 * deplacementMoyen: Le d�placement moyen du joueur virtuel, trouv�
	 *                   selon son niveau de difficult�
	 * 
	 * dblFacteurAdj: Un facteur d'ajustement pour les points ajout�s
	 *                via ttPointsRegion, normalement � 1, on le met
	 *                entre 0 et 1 si on veut rendre moins important la
	 *                case, et � 0 si on ne veut pas ajouter de points
	 *                aux cases adjacentes
	 */ 
	private void attribuerImportanceCase(Point ptCase, int pointCase, 
			int pointAleatoire, int limitDistance, int pointDistance, 
			int[][] ttPointsRegion, double deplacementMoyen,
			double dblFacteurAdj)
	{

		int x = ptCase.x;
		int y = ptCase.y;

		// Point en cours d'analyse
		Point ptTemp = ptCase;

		// Autre point en cours d'analyse
		Point ptTemp2 = new Point(0,0);

		// Chemin entre le joueur et une case importante analys�e
		ArrayList<Point> lstChemin;

		// Cette variable contiendra le nombre de coups estim� pour se rendre
		// � la case en cours d'analyse
		double dblDistance;

		// D�placement moyen, contient le nombre de cases que l'on peut
		// s'attendre � franchir par coup (prend en compte niveau de
		// difficult�)
		double dblDeplacementMoyen = deplacementMoyen;

		// Ce facteur d'�loignement sert � consid�rer l'�loingement
		// de la case lorsqu'on attribue des points aux cases adjacentes
		double dblFacteurEloignement = 1.0; 

		// Cette case augmente d'environ pointCase +pointAleatoire/2 
		// le pointage
		matPoints[x][y] += pointCase + genererNbAleatoire(pointAleatoire);

		// On va trouver le chemin le plus court
		lstChemin = trouverCheminPlusCourt(objPositionJoueur, ptTemp);

		if (lstChemin == null)
		{
			// Une case inaccessible ne doit pas �tre choisie
			matPoints[x][y] = ParametreIA.POINTS_IGNORER_CASE;
		}
		else
		{
			// On a un chemin qui contient chaque case, maintenant, on
			// va trouver, pour ce chemin, le nombre de coups estim�
			// pour le parcourir, et ce, en prenant en compte le niveau
			// de difficult�
			// TODO:Prendre en compte nombre de croches
			dblDistance = lstChemin.size() / dblDeplacementMoyen;


			// Pour permettre de quand m�me prioriser les cases
			// lointaines, on va limiter le nombre de coups
			// ce qui enl�vera un maximum de points pour une case lointaine
			// et �vitera les pointages n�gatifs
			if (dblDistance > limitDistance)
			{
				dblDistance = limitDistance;
			}

			// Plus la case est loin, plus son pointage diminue
			// On enl�ve pointDistance points par coup
			matPoints[x][y] -= (int) (pointDistance * dblDistance + .5);

			// Calculer le facteur d'�loignement pour les cases adjacents
			dblFacteurEloignement = ((double)pointCase - pointDistance * dblDistance) / pointCase;

			if (dblFacteurEloignement <= 0)
			{
				dblFacteurEloignement = ParametreIA.FACTEUR_AJUSTEMENT_MIN;
			}

			// Cette case �tant accessible, on va augmenter les
			// points des cases aux alentours pour attirer
			// le joueur virtuel vers des regroupements de cases
			// importantes
			int intBorneI = ttPointsRegion.length - 1;
			int intBorneJ = ttPointsRegion[0].length - 1;

			for (int i = -intBorneI; i <= intBorneI; i++)
			{
				for (int j = -intBorneJ; j <= intBorneJ; j++)
				{
					ptTemp2.x = x + i;
					ptTemp2.y = y + j;

					if (ptTemp2.x >= 0 && ptTemp2.x < intNbLignes &&
							ptTemp2.y >=0 && ptTemp2.y < intNbColonnes &&
							objttPlateauJeu[ptTemp2.x][ptTemp2.y] != null)  
					{
						matPoints[ptTemp2.x][ptTemp2.y] += ttPointsRegion[intBorneI - Math.abs(i)][Math.abs(j)] * dblFacteurAdj * dblFacteurEloignement;
					}  
				} 
			}
		}

	}

	/*
	 * Cette fonction trouve une position finale que le joueur virtuel va tenter
	 * d'atteindre. C'est ici que la personnalit� du joueur peut influencer la d�cision.
	 * Par la suite, le joueur virtuel devra choisir des cases interm�diaires pour se
	 * rendre � la case finale, cela peut �tre imm�diat au prochain coup.
	 */
	private Point trouverPositionFinaleVisee()
	{

		// Position trouv�e par l'algorithme
		Point objPositionTrouvee = null;

		// Cette variable contiendra le nombre de coups estim� pour se rendre
		// � la case en cours d'analyse
		//double dblDistance;

		// Point en cours d'analyse
		Point ptTemp = new Point(0,0);

		// Autre point en cours d'analyse
		//Point ptTemp2 = new Point(0,0);

		// Chemin entre le joueur et une case importante analys�e
		//Vector<Point> lstChemin;

		// D�placement moyen, contient le nombre de cases que l'on peut
		// s'attendre � franchir par coup (prend en compte niveau de
		// difficult�)
		double dblDeplacementMoyen = tDeplacementMoyen[intNiveauDifficulte];

		// Ce tableau contiendra les cases les plus int�ressantes
		Point tPlusGrand[] = new Point[ParametreIA.NOMBRE_CHOIX_ALTERNATIF];

		// Variable qui indiquera � l'algorithme s'il faut consid�rer
		// les minis-jeu. On consid�re les minis-jeu s'il y a un jeton de
		// disponible
		boolean bolConsidererMiniJeu = determinerPretAJouerMiniJeu();

		// Variable qui indiquera � l'algorithme s'il faut consid�rer
		// les magasins. 
		boolean bolConsidererMagasin = determinerPretAVisiterMagasin();

		//Variables pour calcul des points pour objets et pi�ces
		int intPointsObjet;
		int intPointsEnleverNb;
		int intPointsEnleverDistance;
		int intPointsAleat;
		int intDistanceMax;

		// Variable qui contiendra apr�s calcul les points pour une case
		int intPointsCase;

		// Facteur d'ajustement pour les regroupements de pi�ces
		double dblFacteurAdj;

		// Initialiser la matrice
		for (int x = 0; x < intNbLignes; x++)
		{
			for (int y = 0; y < intNbColonnes; y++)
			{
				// Pointage de d�part (environ 0)
				matPoints[x][y] = ParametreIA.POINTS_BASE_CASE_COULEUR + 
				genererNbAleatoire(ParametreIA.POINTS_ALEATOIRE_CASE_COULEUR);
			}
		}

		// Parcourir toutes les cases du plateau et leur attribuer
		// un pointage
		for (int x = 0; x < intNbLignes; x++)
		{
			for (int y = 0; y < intNbColonnes; y++)
			{
				ptTemp.x = x;
				ptTemp.y = y;

				if (objPositionJoueur.x == x && objPositionJoueur.y == y)
				{
					// La position courante du joueur ne doit pas �tre choisie
					matPoints[x][y] = ParametreIA.POINTS_IGNORER_CASE;
				}
				else
				{

					// Modification du pointage de la case
					if (objttPlateauJeu[x][y] == null)
					{
						// Une case nulle ne doit pas �tre chosie
						matPoints[x][y] = ParametreIA.POINTS_IGNORER_CASE;
					}


					// Objets
					else if (objttPlateauJeu[x][y] instanceof CaseCouleur && 
							((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase() instanceof ObjetUtilisable)
					{
						// Ici, d�pendamment de l'objet, on peut lui attribuer
						// un pointage diff�rent.
						// TODO: Prendre en compte objets d�j� ramass�s
						//       pour par exemple diminuer les pointages
						//       pour les objets qu'on poss�de d�j�

						// Obtenir une r�f�rence � l'objet sur la case
						ObjetUtilisable objObjet = (ObjetUtilisable)((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase();

						// Le joueur virtuel ne voit pas les objets invisibles
						if (objObjet.estVisible() && determinerPretARamasserObjet(objObjet.obtenirUniqueId()))
						{
							// Aller chercher l'UID de l'objet
							int uidObjet = objObjet.obtenirUniqueId();

							// Aller chercher les param�tres pour cet objet
							intPointsObjet = tParametresIAObjetUtilisable[uidObjet].intValeurPoints;
							intPointsEnleverNb = tParametresIAObjetUtilisable[uidObjet].intPointsEnleverQuantite;
							intPointsEnleverDistance = tParametresIAObjetUtilisable[uidObjet].intPointsEnleverDistance;
							intPointsAleat = tParametresIAObjetUtilisable[uidObjet].intValeurAleatoire;
							intDistanceMax = tParametresIAObjetUtilisable[uidObjet].intMaxDistance;


							// Plus on poss�de d'objet, moins cette
							// case est importante
							intPointsCase = intPointsObjet - intPointsEnleverNb * nombreObjetsPossedes(uidObjet);

							// On va aussi diminuer l'influence sur les
							// cases autour de celle-ci
							dblFacteurAdj = intPointsCase / intPointsObjet;
							if (dblFacteurAdj <= 0.00)
							{
								dblFacteurAdj = ParametreIA.FACTEUR_AJUSTEMENT_MIN; 
							}

							// Attribuer les points pour l'objet Livre
							attribuerImportanceCase(ptTemp, intPointsCase, intPointsAleat, 
									intDistanceMax, intPointsEnleverDistance, 
									ttPointsRegionPiece, 
									dblDeplacementMoyen, dblFacteurAdj);


						}
					}


					// Pi�ces
					else if (objttPlateauJeu[x][y] instanceof CaseCouleur && 
							((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase() instanceof Piece)
					{

						intPointsObjet = objParametreIAPiece.intValeurPoints;
						intPointsEnleverDistance = objParametreIAPiece.intPointsEnleverDistance;
						intPointsAleat = objParametreIAPiece.intValeurAleatoire;
						intDistanceMax = objParametreIAPiece.intMaxDistance;

						//objParametreIAObjetPiece;
						attribuerImportanceCase(ptTemp, intPointsObjet, intPointsAleat, 
								intDistanceMax, intPointsEnleverDistance, 
								ttPointsRegionPiece, 
								dblDeplacementMoyen, 1.0);

					}

					// Magasins
					else if(objttPlateauJeu[x][y] instanceof CaseCouleur && 
							((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase() instanceof Magasin &&
							bolConsidererMagasin == true &&
							!lstMagasinsVisites.contains((Magasin)(((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase())))
					{
						// Plus le joueur virtuel poss�de d'objets, moins il est
						// important d'aller visiter un magasin
						intPointsCase = objParametreIAMagasin.intValeurPoints;

						// On enl�ve les points par objets poss�d�s
						intPointsCase -= lstObjetsUtilisablesRamasses.size() * ParametreIA.PTS_ENLEVER_MAGASIN_NB_OBJETS;

						dblFacteurAdj = intPointsCase / objParametreIAMagasin.intValeurPoints;
						if (dblFacteurAdj <= 0.00)
						{
							dblFacteurAdj = ParametreIA.FACTEUR_AJUSTEMENT_MIN; 
						}

						attribuerImportanceCase(ptTemp, intPointsCase, 
								objParametreIAMagasin.intValeurAleatoire, 
								objParametreIAMagasin.intMaxDistance, 
								objParametreIAMagasin.intPointsEnleverDistance, 
								ttPointsRegionPiece, 
								dblDeplacementMoyen, dblFacteurAdj);
					}

					// Mini-jeu
					else if(objttPlateauJeu[x][y] instanceof CaseSpeciale)
					{
						// Attribuer un pointage � la case de MiniJeu
						if (bolConsidererMiniJeu == true)
						{
							attribuerImportanceCase(ptTemp, 
									objParametreIAMinijeu.intValeurPoints, 
									objParametreIAMinijeu.intValeurAleatoire, 
									objParametreIAMinijeu.intMaxDistance, 
									objParametreIAMinijeu.intPointsEnleverDistance, 
									ttPointsRegionPiece, 
									dblDeplacementMoyen, 1.0);
						}
						else
						{
							// On veut s'assurer que le joueur virtuel
							// ne tombe pas sur cette case
							matPoints[x][y] = ParametreIA.POINTS_IGNORER_CASE;
						}
					}
				}


			}
		}

		// On va maintenant trouver les meilleurs d�placements
		for (int x = 0; x < intNbLignes; x++)
		{
			for (int y = 0; y < intNbColonnes; y++)
			{
				// Gestion de la liste des 5 plus grands
				// On ajoute la case qu'on est en train de parcourir dans la liste
				// des 5 plus grands pointage si elle est digne d'y �tre
				for (int i = 0; i < ParametreIA.NOMBRE_CHOIX_ALTERNATIF; i++)
				{
					if (tPlusGrand[i] == null)
					{
						tPlusGrand[i] = new Point(x, y);
						break;
					}
					else if (matPoints[x][y] > matPoints[tPlusGrand[i].x][tPlusGrand[i].y])
					{
						// Tout d�caler vers la droite
						for (int j = ParametreIA.NOMBRE_CHOIX_ALTERNATIF-1; j > i; j--)
						{
							if (tPlusGrand[j-1] != null)
							{
								if (tPlusGrand[j] == null)
								{
									tPlusGrand[j] = new Point(tPlusGrand[j-1].x, tPlusGrand[j-1].y);
								}
								else
								{
									tPlusGrand[j].x = tPlusGrand[j - 1].x;
									tPlusGrand[j].y = tPlusGrand[j - 1].y;
								}
							}
						}

						// Ins�rer notre �l�ment
						tPlusGrand[i].x = x;
						tPlusGrand[i].y = y;

						break;
					}
				}   
			}
		}

		// Maintenant, on rend le joueur virtuel faillible et on fait en sorte
		// qu'il ne choisisse pas toujours le meilleur choix
		int intDifferenceMax = tNombrePointsMaximalChoixFinal[intNiveauDifficulte];

		// Nombre de choix possible qui ne d�passe pas la limite de intDifferenceMax
		int intNombreChoix = 1;

		// Valeur maximum pour g�n�rer la valeur al�atoire
		int intValeurMax;

		// Valeur al�atoire permettant d'effectuer le choix
		int intValeurAleatoire;

		// Tableau contenant le pourcentage des choix alternatifs
		int tPourcentageChoix[] = obtenirPourcentageChoixAlternatifFinal();

		// La d�cision selon le r�sultat al�atoire
		int intDecision = 0;

		// Valeur accumul�e pour trouver la d�cision correspondante
		int intValeurAccumulee = 0;

		// On doit trouver le nombre de choix possible pour le joueur virtuel
		// selon la diff�rence maximum calcul�e (qui tient compte du niveau
		// de difficult�)
		intValeurMax = tPourcentageChoix[0];
		for (int i = 1; i < ParametreIA.NOMBRE_CHOIX_ALTERNATIF; i ++)
		{
			if (matPoints[tPlusGrand[i].x][tPlusGrand[i].y] < 0 || 
					matPoints[tPlusGrand[i].x][tPlusGrand[i].y] < 
					matPoints[tPlusGrand[0].x][tPlusGrand[0].y] - intDifferenceMax)
			{
				// Ce choix est en-dessous de la limite permise pour
				// ce niveau de difficult�
				intNombreChoix = i;
				break;
			}
			else
			{
				intValeurMax += tPourcentageChoix[i];
			}
		}

		// On va chercher un nombre entre 1 et la valeur max inclusivement
		intValeurAleatoire = genererNbAleatoire(intValeurMax) + 1;

		// Ce nombre correspond � notre choix
		for (int i = 0; i < intNombreChoix; i++)
		{
			intValeurAccumulee += tPourcentageChoix[i];
			if (intValeurAleatoire <= intValeurAccumulee)
			{
				intDecision = i;
			}
		}

		// D�terminer la raison
		intRaisonPositionFinale = ParametreIA.RAISON_AUCUNE;
		if (objttPlateauJeu[tPlusGrand[intDecision].x][tPlusGrand[intDecision].y] instanceof CaseCouleur)
		{
			if (((CaseCouleur)objttPlateauJeu[tPlusGrand[intDecision].x][tPlusGrand[intDecision].y]).obtenirObjetCase() instanceof Piece)
			{
				intRaisonPositionFinale = ParametreIA.RAISON_PIECE;
			}

			else if (((CaseCouleur)objttPlateauJeu[tPlusGrand[intDecision].x][tPlusGrand[intDecision].y]).obtenirObjetCase() instanceof Magasin)
			{
				intRaisonPositionFinale = ParametreIA.RAISON_MAGASIN;
			}

			if (((CaseCouleur)objttPlateauJeu[tPlusGrand[intDecision].x][tPlusGrand[intDecision].y]).obtenirObjetCase() instanceof ObjetUtilisable)
			{
				ObjetUtilisable objObjet = (ObjetUtilisable)((CaseCouleur)objttPlateauJeu[tPlusGrand[intDecision].x][tPlusGrand[intDecision].y]).obtenirObjetCase();

				if (objObjet.estVisible())
				{
					intRaisonPositionFinale = ParametreIA.RAISON_OBJET;
				}
			}
		}
		else if (objttPlateauJeu[tPlusGrand[intDecision].x][tPlusGrand[intDecision].y] instanceof CaseSpeciale)
		{
			intRaisonPositionFinale = ParametreIA.RAISON_MINIJEU;
		}

		// Retourner la position trouv�e
		objPositionTrouvee = new Point(tPlusGrand[intDecision].x, tPlusGrand[intDecision].y);
		return objPositionTrouvee;
	}



	// it is not absolutely correct because PositionWinTheGame is a array, but it is not so important 
	public int obtenirDistanceAuWinTheGame()
	{
		Point objPoint = objTable.getPositionPointFinish();
		return Math.abs(objPositionJoueur.x - objPoint.x) + Math.abs(objPositionJoueur.y - objPoint.y);
	}


	public void setWasOnFinish(boolean bool) {
		this.wasOnFinishLine = bool;
	}


	public boolean getWasOnFinish() {
		return wasOnFinishLine;
	}

	/**
	 * @param  positionFinale  the pointsFinalTime to set
	 */
	public void setPointsFinalTime(int positionFinale) {
		this.pointsFinalTime = positionFinale;
	}


	/**
	 * @return the pointsFinalTime
	 */
	public int getPointsFinalTime() {
		return pointsFinalTime;
	}

	/* Cette fonction permet d'obtenir le temps de r�flexion d'un joueur
	 * virtuel pour penser � son achat dans un magasin. Ce temps est bas�
	 * sur le niveau de difficult� du joueur virtuel et comprend un �l�ment
	 * al�atoire.
	 */
	private int obtenirTempsReflexionAchat()
	{

		return tTempsReflexionBase[ParametreIA.TYPE_REFLEXION_ACHAT][intNiveauDifficulte] + 
		genererNbAleatoire(tTempsReflexionAleatoire[ParametreIA.TYPE_REFLEXION_ACHAT][intNiveauDifficulte]);

	}

	/* Cette fonction permet d'obtenir le temps de r�flexion d'un joueur
	 * virtuel pour planifier son prochain coup. Ce temps est bas� sur le niveau
	 * de difficult� du joueur virtuel et comprend un �l�ment al�atoire.
	 */
	private int obtenirTempsReflexionCoup()
	{
		return tTempsReflexionBase[ParametreIA.TYPE_REFLEXION_COUP][intNiveauDifficulte] + 
		genererNbAleatoire(tTempsReflexionAleatoire[ParametreIA.TYPE_REFLEXION_COUP][intNiveauDifficulte]);

	}


	/* Cette fonction permet d'obtenir le temps de r�flexion d'un joueur
	 * virtuel lorsqu'il r�pond � une question. Ce temps est bas� sur le niveau
	 * de difficult� du joueur virtuel et comprend un �l�ment al�atoire.
	 */
	private int obtenirTempsReflexionReponse()
	{
		return tTempsReflexionBase[ParametreIA.TYPE_REFLEXION_REPONSE][intNiveauDifficulte] + 
		genererNbAleatoire(tTempsReflexionAleatoire[ParametreIA.TYPE_REFLEXION_REPONSE][intNiveauDifficulte]);
	}   

	/* Cette fonction retourne le temps en secondes que dure un d�placement
	 * de joueur selon le nombre de cases du d�placement.
	 */
	private int obtenirTempsDeplacement(int nombreCase)
	{
		if (nombreCase < 4)
		{
			return nombreCase;
		}
		else
		{
			return nombreCase - 1;
		}
	}

	/* Cette fonction retourne un tableau contenant les pourcentages pour les 
	 * choix alternatifs de positions finales selon le niveau de difficult�
	 */
	private int[] obtenirPourcentageChoixAlternatifFinal()
	{
		int intPourcentageChoix[] = new int[ParametreIA.NOMBRE_CHOIX_ALTERNATIF];

		for (int i = 0; i < ParametreIA.NOMBRE_CHOIX_ALTERNATIF; i++)
		{
			intPourcentageChoix[i] = tPourcentageChoixAlternatifFinal[intNiveauDifficulte][i];
		}
		return intPourcentageChoix;
	}

	/* Cette fonction permet de savoir si c'est le temps de calculer 
	 * une nouvelle position finale vis�e par le joueur virtuel. On
	 * fait cela dans les circonstances suivantes:
	 *
	 * - Aucune position encore trouv�e (d�but)
	 * - Le joueur a atteint la position qu'il visait
	 * - L'�tat de la case vis�e a chang� (l'objet a disparu)
	 */
	private boolean reviserPositionFinaleVisee()
	{
		// V�rifier si aucune position trouv�e
		if (objPositionFinaleVisee == null)
		{
			return true;
		}

		// V�rifier si on a atteint la position pr�c�damment vis�e
		if (objPositionJoueur.x == objPositionFinaleVisee.x &&
				objPositionJoueur.y == objPositionFinaleVisee.y)
		{
			return true;
		}

		// Aller chercher le plateau de jeu
		//Case objttPlateauJeu[][] = uCourant();

		// V�rifier si l'�tat de la case a chang�
		switch (intRaisonPositionFinale)
		{
		case ParametreIA.RAISON_AUCUNE: 

			// Aucune raison = erreur en g�n�ral, donc on va recalculer
			// une position finale
			return true;


		case ParametreIA.RAISON_PIECE:

			// V�rifier si la pi�ce a �t� captur�e
			if(objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y] instanceof CaseCouleur)
			{
				if (((CaseCouleur)objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y]).obtenirObjetCase() == null)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		case ParametreIA.RAISON_MINIJEU:

			// V�rifier si encore pr�t pour un minijeu
			return !determinerPretAJouerMiniJeu();

		case ParametreIA.RAISON_MAGASIN:

			// V�rifier si encore pr�t � visiter un magasin
			return !determinerPretAVisiterMagasin();

		case ParametreIA.RAISON_OBJET:

			// V�rifier si l'objet a �t� captur� et si encore pr�t
			// � ramasser l'objet
			if(objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y] instanceof CaseCouleur)
			{
				if(((CaseCouleur)objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y]).obtenirObjetCase() instanceof ObjetUtilisable)
				{
					if (((CaseCouleur)objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y]).obtenirObjetCase() == null ||
							determinerPretARamasserObjet(((ObjetUtilisable)((CaseCouleur)objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y]).obtenirObjetCase()).obtenirUniqueId()) == false)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}

		}

		// Dans les autres cas, ce n'est pas n�cessaire de rechercher
		// tout de suite (on attend que le joueur virtuel atteigne la
		// position finale avant de recalculer une position)
		return false;

	}// end method


	/* Cette fonction d�termine le nombre de points que fera un
	 * joueur virtuel en jouant � un mini-jeu d�pendamment du temps qu'il
	 * y met
	 */
	private int determinerPointsJeuMiniJeu(int intTypeMiniJeu, int intTempsMiniJeu)
	{
		// Le temps pour les calculs
		double dblTempsCalcul = 0.0;

		// Le pointage obtenu
		int intPointsJeu = 0;

		// Un petit d�lai au d�but de la partie
		double dblDelaiDepart = 0.0;

		// Un d�lai additionnel, pour ball-au-mur, correspond au temps du
		// dernier coup qui lui, ne donne pas de points
		double dblDelaiAdditionnel = 0.0;

		switch(intTypeMiniJeu)
		{
		case ParametreIA.MINIJEU_BALLE_AU_MUR:

			// D�lai de d�part de 0 � 2 secondes
			dblDelaiDepart = genererNbAleatoire(3);

			// Trouver le temps de jeu pour le calcul
			dblTempsCalcul = (double)intTempsMiniJeu - dblDelaiDepart;

			// Trouver le temps additionnel
			if (dblTempsCalcul > 125.0)
			{
				dblDelaiAdditionnel = 0.5;
			}
			else if (dblTempsCalcul > 68.0)
			{
				dblDelaiAdditionnel = 1.0;
			}
			else if (dblTempsCalcul > 50.0)
			{
				dblDelaiAdditionnel = 2.0;
			}
			else if (dblTempsCalcul > 32)
			{
				dblDelaiAdditionnel = 3.0;
			}
			else if (dblTempsCalcul > 20)
			{
				dblDelaiAdditionnel = 4.0;
			}
			else
			{
				dblDelaiAdditionnel = 5.0;
			}

			// Modifier le temps de calcul selon le temps additionnel
			dblTempsCalcul -= dblDelaiAdditionnel; 

			if (dblTempsCalcul < 0.0)
			{
				dblTempsCalcul = 0.0;
			}   	    

			// D�pendamment du temps pris � jouer, trouver le
			// nombre de points que fera le joueur
			if (dblTempsCalcul >= 125.5)
			{
				intPointsJeu = (int)(79 + (dblTempsCalcul - 125) * 2);
			}
			else if (dblTempsCalcul >= 69.0)
			{
				intPointsJeu = (int)(22 + (dblTempsCalcul - 68));
			}
			else if (dblTempsCalcul >= 52.0)
			{
				intPointsJeu = (int)(13 + (dblTempsCalcul - 50) * 0.5);
			}
			else if (dblTempsCalcul >= 35)
			{
				intPointsJeu = (int)(7 + (dblTempsCalcul - 32) / 3);
			}
			else if (dblTempsCalcul >= 24)
			{
				intPointsJeu = (int)(4 + (dblTempsCalcul - 20) * 0.25);
			}
			else
			{
				intPointsJeu = (int)(dblTempsCalcul * 0.2);
			}

			break;
		}

		if (intPointsJeu < 0)
		{
			intPointsJeu = 0;
		}

		return intPointsJeu;
	}




	// Pour tous les minijeus
	private int obtenirTempsSureteMiniJeu()
	{
		// Temps � la fin de la partie o� le joueur ne doit pas
		// d�buter un mini-jeu
		int intTempsSurete = 0;

		switch (intNiveauDifficulte)
		{
		case ParametreIA.DIFFICULTE_FACILE:
			intTempsSurete = 80;
			break;

		case ParametreIA.DIFFICULTE_MOYEN:
			intTempsSurete = 150;
			break;

		case ParametreIA.DIFFICULTE_DIFFICILE:
			intTempsSurete = 240;
			break;
		}	

		return intTempsSurete;
	}

	/* Cette fonction va remplir le tableau tJetonsMagasins[] qui
	 * permettra aux joueurs virtuels de se servir des magasins, mais
	 * en limitant le nombre de fois qu'il s'en servira au cours
	 * de la partie
	 */
	private void determinerJetonsMagasins()
	{
		// Nombre de visite � l'heure
		int intNombreMagasins = 0;

		// Maintenant, on trouve le nombre de magasins que le joueur
		// virtuel visitera dans les prochaines 60 minutes
		intNombreMagasins = tNbJetonsMagasinBase[intNiveauDifficulte] + 
		genererNbAleatoire(tNbJetonsMagasinAleatoire[intNiveauDifficulte]);

		// Obtenir le temps de la partie en minutes
		int intTempsPartie = objTable.obtenirTempsTotal();

		// Obtenir un temps pour les calculs
		int intTempsCalcul = (((int)(intTempsPartie / 61)) + 1) * 60;

		// Obtenir le nombre de jetons � g�n�rer
		int intNombreJetons = intTempsCalcul * intNombreMagasins / 60;

		// Initialiser le tableau pour contenir les jetons
		tJetonsMagasins = new int[intNombreJetons];

		// Generer tous les jetons
		for (int i = 0; i < intNombreJetons; i++)
		{
			tJetonsMagasins[i] = genererNbAleatoire(intTempsCalcul * 60);
		}

		int intTempsSurete = ParametreIA.TEMPS_SURETE_MAGASIN;
		int intTempsMax = intTempsPartie * 60 - intTempsSurete;

		// D�placer en arri�re les jetons dans les derni�res X secondes
		for (int i = 0; i < intNombreJetons; i++)
		{
			if (tJetonsMagasins[i] >= intTempsMax && tJetonsMagasins[i] < intTempsPartie * 60)
			{
				tJetonsMagasins[i] -= intTempsSurete * 2;
			}
		}

		if (ccDebug)
		{  
			System.out.print("Jetons magasins: ");
			for (int i = 0; i < intNombreJetons ; i++)
			{
				if (i > 0)
				{
					System.out.print(", ");
				}
				System.out.print(tJetonsMagasins[i]);
			}
			System.out.println("");
		}
	}

	/* Cette fonction va remplir le tableau tJetonsMiniJeu[] qui
	 * permettra aux joueurs virtuels de jouer � des mini-jeux, mais
	 * en limitant le nombre de fois qu'il jouera au cours de la partie
	 */    
	private void determinerJetonsMiniJeu()
	{

		// Nombre de mini-jeu � l'heure
		int intNombreMiniJeu = 0;

		// Maintenant, on trouve le nombre de partie de minijeu que le
		// joueur virtuel fera pour les prochaines 60 minutes	
		intNombreMiniJeu = tNbJetonsMinijeuBase[intNiveauDifficulte] + 
		genererNbAleatoire(tNbJetonsMinijeuAleatoire[intNiveauDifficulte]);

		// Obtenir le temps de la partie en minutes
		int intTempsPartie = objTable.obtenirTempsTotal();

		// Obtenir un temps pour les calculs
		int intTempsCalcul = (((int)(intTempsPartie / 61)) + 1) * 60;

		// Obtenir le nombre de jetons � g�n�rer
		int intNombreJetons = intTempsCalcul * intNombreMiniJeu / 60;

		// Initialiser le tableau pour contenir les jetons
		tJetonsMiniJeu = new int[intNombreJetons];

		// Generer tous les jetons
		for (int i = 0; i < intNombreJetons; i++)
		{
			tJetonsMiniJeu[i] = genererNbAleatoire(intTempsCalcul * 60);
		}

		int intTempsSurete = obtenirTempsSureteMiniJeu();
		int intTempsMax = intTempsPartie * 60 - intTempsSurete;

		// D�placer en arri�re les jetons dans les derni�res X secondes
		for (int i = 0; i < intNombreJetons; i++)
		{
			if (tJetonsMiniJeu[i] >= intTempsMax && tJetonsMiniJeu[i] < intTempsPartie * 60)
			{
				tJetonsMiniJeu[i] -= intTempsSurete * 2;
			}
		}

		if (ccDebug)
		{
			System.out.print("Jetons minijeu: ");
			for (int i = 0; i < intNombreJetons ; i++)
			{
				if (i > 0)
				{
					System.out.print(", ");
				}
				System.out.print(tJetonsMiniJeu[i]);
			}
			System.out.println("");
		}
	}

	/* Cette fonction retourne le nombre de jetons disponibles
	 * d'un tableau de jetons en fonction du temps �coul�
	 */ 
	private int obtenirNombreJetonsDisponibles(int tTableauJetons[], int intTempsEcoule)
	{
		int intNombreJetonsDisponibles = 0;
		for (int i = 0; i < tTableauJetons.length; i++)
		{
			if (tTableauJetons[i] <= intTempsEcoule)
			{
				intNombreJetonsDisponibles++;
			}
		}

		return intNombreJetonsDisponibles;	
	}

	/* Cette fonction permet � l'algorithme de recherche de position
	 * de d�terminer s'il faut consid�rer les cases avec des
	 * objets comme des cases importantes, puisque leur utilisation
	 * risque d'�tre limit� dans le temps, alors en fin de partie, on
	 * essaye de ne pas ramasser d'objets.
	 */
	private boolean determinerPretARamasserObjet(int uidObjet)
	{
		// V�rifier s'il reste assez de temps et que le joueur a de la place
		if (uidObjet>0) return false; //TODO: r�gler �a
		if (objTable.obtenirTempsRestant() <= tParametresIAObjetUtilisable[uidObjet].intTempsSureteRamasser ||
				nombreObjetsPossedes(uidObjet) >= tParametresIAObjetUtilisable[uidObjet].intQuantiteMax)
		{
			return false;
		}	
		else
		{
			return true;
		}
	}

	/* Cette fonction permet � l'algorithme de recherche de position
	 * finale de d�terminer s'il doit consid�rer les cases magasins
	 * comme des cases importantes. On prend en compte le tableau
	 * tJetonsMagasins[], intNbMagasinVisites, le temps de la partie
	 * ainsi que les items que le joueur virtuel poss�de
	 */
	private boolean determinerPretAVisiterMagasin()
	{

		int intNombreJetonsDisponibles = 0;
		int intTempsEcoule = objTable.obtenirTempsTotal() * 60 - objTable.obtenirTempsRestant();

		// V�rifier d'abord s'il reste assez de temps
		if (objTable.obtenirTempsRestant() <= ParametreIA.TEMPS_SURETE_MAGASIN * 2)
		{
			return false;
		}


		// Il faut au moins 1 dollar pour acheter ne serait-ce qu'un objet
		if (obtenirArgent() < 1)
		{
			return false;
		}

		intNombreJetonsDisponibles = obtenirNombreJetonsDisponibles(tJetonsMagasins, intTempsEcoule);

		// Enlever les magasins d�j� jou�es
		intNombreJetonsDisponibles -= intNbMagasinVisites;

		// S'il y a des jetons disponibles, on permet au joueur
		// virtuel de se d�placer vers un magasin
		if (intNombreJetonsDisponibles > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/* Cette fonction permet � l'algorithme de recherche de position
	 * finale de d�terminer s'il doit consid�rer les cases minis-jeu
	 * comme des cases importantes. Pour ce faire, il faut prendre en
	 * consid�ration le tableau tJetonsMiniJeu[], le nombre de minis-jeu
	 * d�j� jou�s par le joueur virtuel dans cette partie et aussi le
	 * temps de la partie.
	 *
	 * Tout �a a pour but de limiter le nombre de mini-jeu que le
	 * joueur virtuel va jouer, faire en sorte que ce soit diff�rent
	 * � chaque partie et aussi espacer les parties pour ne pas que le
	 * joueur virtuel "bloque" lorsqu'il arrive sur une case mini-jeu,
	 * fait un va-et-vient et joue sans cesse sur cette case.
	 */
	private boolean determinerPretAJouerMiniJeu()
	{

		int intNombreJetonsDisponibles = 0;
		int intTempsEcoule = objTable.obtenirTempsTotal() * 60 - objTable.obtenirTempsRestant();

		// V�rifier d'abord s'il reste assez de temps
		if (objTable.obtenirTempsRestant() <= obtenirTempsSureteMiniJeu() * 2)
		{
			return false;
		}

		intNombreJetonsDisponibles = obtenirNombreJetonsDisponibles(tJetonsMiniJeu, intTempsEcoule);

		// Enlever les parties d�j� jou�es
		intNombreJetonsDisponibles -= intNbMiniJeuJoues;

		// S'il y a des jetons disponibles, on permet au joueur
		// virtuel de jouer sur une case mini-jeu
		if (intNombreJetonsDisponibles > 0)
		{
			return true;
		}
		else
		{
			return false;
		}


	}

	/* 
	 * Fonction de service utilis�e dans l'algorithme de recherche de
	 * position qui permet de modifier les pourcentages du choix � faire.
	 * La fonction prend un tableau de longueur X et un indice du tableau. 
	 * De indice + 1 � X - 1, on ajoute les valeurs � tableau[indice]
	 * puis on met � z�ro ces indices
	 */
	private void traiterPieceTrouveeDansLigne(int tPourcentageCase[], int indice)
	{
		int x;
		if (indice + 1 <= ParametreIA.DEPLACEMENT_MAX - 1 && indice >= 0)
		{
			for(x = indice + 1; x <= ParametreIA.DEPLACEMENT_MAX - 1; x++)
			{
				tPourcentageCase[indice] += tPourcentageCase[x];
				tPourcentageCase[x] = 0;
			}

		}
	}

	/*
	 * Cette fonction pr�pare l'�v�nement indiquant que le joueur virtuel se d�place

	private void preparerEvenementJoueurVirtuelDeplacePersonnage( String collision, Point objNouvellePosition, int nouveauPointage )
    {
        objTable.preparerEvenementJoueurDeplacePersonnage(objJoueurVirtuel.obtenirNom(), collision, objPositionJoueur, 
            objNouvellePosition, nouveauPointage);
    } */

	/* Cette fonction de service utilis�e dans l'algorithme de recherche
	 * de position permet d'�liminer une case du choix, si par exemple,
	 * on ne veut pas diriger le joueur virtuel sur un minijeu, on
	 * enl�ve cette case du choix
	 */
	private void traiterCaseEliminerDansLigne(int intPourcentageCase[], int indiceCase)
	{
		// On va �liminer la case dans les pourcentages puis
		// remettre le tout sur 100
		int intDenominateur = 0;

		for (int i = 0; i < ParametreIA.DEPLACEMENT_MAX; i++)
		{
			if (i != indiceCase)
			{
				intDenominateur += intPourcentageCase[i];
			}
		}

		// On �linie la case ici
		intPourcentageCase[indiceCase] = 0;

		// On repond�re car le total n'est plus 100
		for (int i = 0; i < ParametreIA.DEPLACEMENT_MAX; i++)
		{
			if (i != indiceCase)
			{
				intPourcentageCase[i] = intPourcentageCase[i] * 100 / intDenominateur;
			}
		}

		// On s'assure que le total est bien de 100
		int intTotal = 0;
		for (int i = 0; i < ParametreIA.DEPLACEMENT_MAX; i++)
		{
			intTotal += intPourcentageCase[i];
		}

		if (intTotal < 100)
		{
			// Ajouter au prochaine indice != 0 ce qui manque
			for (int j = 0; j < ParametreIA.DEPLACEMENT_MAX; j++)
			{
				if (intPourcentageCase[j] > 0)
				{
					intPourcentageCase[j] += 100 - intTotal;
					break;
				}
			}
		}
		else if (intTotal > 100)
		{
			// Enlever au prochaine indice != 0 ce qui a de trop
			for (int j = 0; j < ParametreIA.DEPLACEMENT_MAX; j++)
			{
				if (intPourcentageCase[j] > 0)
				{
					intPourcentageCase[j] -= intTotal - 100;
					break;
				}
			}	
		}

	}

	/**
	 * @param objPositionIntermediaire
	 * @return 
	 * @return
	 */
	protected  void analyseVirtualNextStep(Point objPositionIntermediaire) {


		// Cette variable indique si le joueur virtuel a r�pondu correctement
		// � la question
		boolean bolQuestionReussie;

		// Cette variable contient le temps de r�flexion pour r�pondre �
		// la question
		int intTempsReflexionQuestion;

		// Cette variable contient le temps de r�flexion pour choisir
		// le prochain coup � jouer
		int intTempsReflexionCoup;

		// Cette variable contient le temps de pause pour le d�placement
		// du personnage
		int intTempsDeplacement;

		// La grandeur de d�placement demand� par le joueur virtuel
		int intGrandeurDeplacement;

		// Le pourcentage de r�ussite � la question
		int intPourcentageReussite;
		//System.out.println("objJoueurVirtuel.getBoolStopThread() = " + objJoueurVirtuel.getBoolStopThread()); 

		// D�terminer le temps de r�flexion pour le prochain coup
		intTempsReflexionCoup = obtenirTempsReflexionCoup();

		// Pause pour moment de r�flexion de d�cision
		objJoueurVirtuel.pause(intTempsReflexionCoup * 1000);


		if(objJoueurVirtuel.getBoolStopThread() == false && objTable.obtenirTempsRestant() > 0){ 
			// try to use Banana - first control if we have it 
			virtuelUseBanana();
		}	

		if(objJoueurVirtuel.getBoolStopThread() == false && objTable.obtenirTempsRestant() > 0){ 
			// Trouver une case int�ressante � atteindre
			objPositionFinaleVisee = this.obtenirTable().getPositionPointFinish();


			if (reviserPositionFinaleVisee() == true )
			{
				int essais = 0;
				do
				{
					objPositionFinaleVisee = trouverPositionFinaleVisee();
					essais++;
				}while(essais < 50 &&  this.obtenirTable().checkPositionPointsFinish(objPositionFinaleVisee));

			}
		}
		// On trouve une position entre le joueur virtuel et son objectif
		if(objJoueurVirtuel.getBoolStopThread() == false && objTable.obtenirTempsRestant() > 0){ 
			objPositionIntermediaire = trouverPositionIntermediaire();
		}
		// S'il y a erreur de recherche ou si le joueur virtuel est pris
		// on ne le fait pas bouger
		if (objJoueurVirtuel.getBoolStopThread() == false && objPositionIntermediaire != null && 
				(objPositionIntermediaire.x != objPositionJoueur.x || objPositionIntermediaire.y != objPositionJoueur.y ))
		{
			// Calculer la grandeur du d�placement demand�
			intGrandeurDeplacement = obtenirPointage(objPositionJoueur, objPositionIntermediaire);

			// V�rifier si on utilise un objet livre
			boolean bolUtiliserLivre = nombreObjetsPossedes(Objet.UID_OU_LIVRE) > 0;

			// Aller chercher le pourcentage de r�ussite � la question
			intPourcentageReussite = tPourcentageReponse[intNiveauDifficulte][intGrandeurDeplacement-1];

			if (bolUtiliserLivre == true)
			{
				if (ccDebug)
				{
					System.out.println("Utilise objet: Livre");
				}

				// Enlever un objet livre des objets du joueur
				enleverObjet(Objet.UID_OU_LIVRE);

			}

			// V�rifier si c'est une question � choix de r�ponse
			boolean bolQuestionChoixDeReponse = (genererNbAleatoire(100)+1 <= ParametreIA.RATIO_CHOIX_DE_REPONSE);
			if (bolQuestionChoixDeReponse)
			{						
				// Augmenter les chances de r�ussites utilisant le
				// tableau de % de r�ponse lorsqu'il reste des charges
				// � l'objet et si cette question est � choix de r�ponse
				intPourcentageReussite = tPourcentageReponseObjetLivre[intNiveauDifficulte][intGrandeurDeplacement-1];
			}

			//if Banana is used on this Virtual Player 
			if(bananaState.isUnderBananaEffects())
				intPourcentageReussite -= 10;

			//if Braniac is used on this Virtual Player 
			if(brainiacState.isInBrainiac())
				intPourcentageReussite += 10;

			// D�terminer si le joueur virtuel r�pondra � la question
			bolQuestionReussie = (genererNbAleatoire(100)+1 <= intPourcentageReussite);

			// D�terminer le temps de r�ponse � la question
			intTempsReflexionQuestion = obtenirTempsReflexionReponse();

			//if Banana is used on this Virtual Player 
			if(bananaState.isUnderBananaEffects())
				intTempsReflexionQuestion = intTempsReflexionQuestion + 4;

			//if Braniac is used on this Virtual Player 
			if(brainiacState.isInBrainiac())
				intTempsReflexionQuestion = intTempsReflexionQuestion - 4;

			// Pause pour moment de r�flexion de r�ponse
			objJoueurVirtuel.pause(intTempsReflexionQuestion * 1000);	

			// Faire d�placer le personnage si le joueur virtuel a
			// r�ussi � r�pondre � la question
			if (bolQuestionReussie == true && objJoueurVirtuel.getBoolStopThread() == false && objTable.obtenirTempsRestant() > 0)
			{

				deplacerJoueurVirtuelEtMajPlateau(objPositionIntermediaire);

				// Obtenir le temps que le d�placement dure
				intTempsDeplacement = obtenirTempsDeplacement(obtenirPointage(objPositionJoueur, objPositionIntermediaire));

				// Pause pour laisser le personnage se d�placer
				objJoueurVirtuel.pause(intTempsDeplacement * 1000);
			}
			else
			{
				if (ccDebug)
				{
					System.out.println("Question rat�e");
				}

				// Pause pour r�troaction
				objJoueurVirtuel.pause(ParametreIA.TEMPS_RETROACTION);
			}

		}		
	}


	private int genererNbAleatoire(int nb)
	{
		return objRandom.nextInt(nb);
	}

	/* 
	 * Cette fonction retourne le pointage d'un d�placement
	 *
	 */
	public int obtenirPointage(Point ptFrom, Point ptTo)
	{
		if (ptFrom.x == ptTo.x)
		{
			return Math.abs(ptFrom.y - ptTo.y);
		}
		else
		{
			return Math.abs(ptFrom.x - ptTo.x);
		}
	}

	public void setOnBanana()
	{
		//getBrainiacState().setOffBrainiac();
	}

	public void setOffBanana()
	{
		getBananaState().setOffBanana();
	}

	public void setOffBrainiac()
	{
		getBrainiacState().setOffBrainiac();
	}

	public void setOnBrainiac() {
		setOffBanana();		
	}

} // end class
