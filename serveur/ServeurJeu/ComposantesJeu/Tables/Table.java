package ServeurJeu.ComposantesJeu.Tables;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import ClassesUtilitaires.UtilitaireNombres;
import Enumerations.GameType;
import Enumerations.RetourFonctions.ResultatDemarrerPartie;
import ServeurJeu.ControleurJeu;
import ServeurJeu.BD.GestionnaireBDControleur;
import ServeurJeu.ComposantesJeu.Salle;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.GenerateurPartie.GenerateurPartie;
import ServeurJeu.ComposantesJeu.Joueurs.InformationPartieHumain;
import ServeurJeu.ComposantesJeu.Joueurs.InformationPartieVirtuel;
import ServeurJeu.ComposantesJeu.Joueurs.Joueur;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurVirtuel;
import ServeurJeu.ComposantesJeu.Joueurs.ParametreIA;
import ServeurJeu.ComposantesJeu.Joueurs.StatisticsPlayer;
import ServeurJeu.ComposantesJeu.ReglesJeu.Regles;
import ServeurJeu.Configuration.GestionnaireConfiguration;
import ServeurJeu.Evenements.Evenement;
import ServeurJeu.Evenements.EvenementJoueurDemarrePartie;
import ServeurJeu.Evenements.EvenementJoueurDeplacePersonnage;
import ServeurJeu.Evenements.EvenementJoueurEntreTable;
import ServeurJeu.Evenements.EvenementJoueurQuitteTable;
import ServeurJeu.Evenements.EvenementJoueurRejoindrePartie;
import ServeurJeu.Evenements.EvenementMAJArgent;
import ServeurJeu.Evenements.EvenementMAJPointage;
import ServeurJeu.Evenements.EvenementMessageChat;
import ServeurJeu.Evenements.EvenementPartieDemarree;
import ServeurJeu.Evenements.EvenementPartieTerminee;
import ServeurJeu.Evenements.EvenementSynchroniserTemps;
import ServeurJeu.Evenements.EvenementUtiliserObjet;
import ServeurJeu.Evenements.EventPlayerPictureCanceled;
import ServeurJeu.Evenements.EventPlayerSelectedPicture;
import ServeurJeu.Evenements.GestionnaireEvenements;
import ServeurJeu.Evenements.InformationDestination;
import ServeurJeu.Temps.GestionnaireTemps;
import ServeurJeu.Temps.Minuterie;
import ServeurJeu.Temps.ObservateurMinuterie;
import ServeurJeu.Temps.ObservateurSynchroniser;
import ServeurJeu.Temps.TacheSynchroniser;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Table implements ObservateurSynchroniser, ObservateurMinuterie
{
	// D�claration d'une r�f�rence vers le gestionnaire d'�v�nements
	protected GestionnaireEvenements objGestionnaireEvenements;
	// points for Finish WinTheGame
	protected ArrayList<Point> lstPointsFinish = new ArrayList<Point>();
	// D�claration d'une r�f�rence vers le contr�leur de jeu
	protected ControleurJeu objControleurJeu;
	// D�claration d'une r�f�rence vers le gestionnaire de bases de donn�es
	protected GestionnaireBDControleur objGestionnaireBD;
	// D�claration d'une r�f�rence vers la salle parente dans laquelle se
	// trouve cette table
	protected Salle objSalle;
	// Cette variable va contenir le num�ro de la table
	protected final int intNoTable;
	// D�claration d'une constante qui d�finit le nombre maximal de joueurs
	// dans une table
	protected int MAX_NB_PLAYERS;
	// Cette variable va contenir le nom d'utilisateur du cr�ateur de cette table
	//private String strNomUtilisateurCreateur;
	// D�claration d'une variable qui va garder le temps total d�fini pour
	// cette table
	protected final int intTempsTotal;
	// Cet objet est une liste des joueurs qui sont pr�sentement sur cette table
	protected ConcurrentHashMap<String, JoueurHumain> lstJoueurs;
	// Cet objet est une liste des joueurs qui attendent de joueur une partie
	protected ConcurrentHashMap<String, JoueurHumain> lstJoueursEnAttente;
	// Cette liste contient le nom des joueurs qui ont �t� d�connect�s
	// dans cette table, ce qui nous permettra, lorsqu'une partie se termine, de
	// faire la mise � jour de la liste des joueurs d�connect�s du gestionnaire
	// de communication
	protected ConcurrentHashMap<String, JoueurHumain> lstJoueursDeconnectes;
	// D�claration d'une variable qui va permettre de savoir si la partie est
	// commenc�e ou non
	protected volatile boolean bolEstCommencee;
	// D�claration d'une variable qui va permettre d'arr�ter la partie en laissant
	// l'�tat de la partie � "commenc�e" tant que les joueurs sont � l'�cran des pointages
	protected volatile boolean bolEstArretee;
	// D�claration d'un tableau � 2 dimensions qui va contenir les informations
	// sur les cases du jeu
	protected Case[][] objttPlateauJeu;
	protected GestionnaireTemps objGestionnaireTemps;
	protected TacheSynchroniser objTacheSynchroniser;
	protected Minuterie objMinuterie;
	// Cet objet est une liste des joueurs virtuels qui jouent sur cette table
	private ArrayList<JoueurVirtuel> lstJoueursVirtuels;
	// Cette variable indique le nombre de joueurs virtuels sur la table
	private int intNombreJoueursVirtuels;
	
	protected Date objDateDebutPartie;
	// D�claration d'une variable qui permettra de cr�er des id pour les objets
	// On va initialis� cette variable lorsque le plateau de jeu sera cr��
	protected volatile Integer objProchainIdObjet;
	// Name of the table
	private String tableName;

	// list of colors for the players clothes
	// after use of one color it is removed from the list
	// automaticaly - randomly is done to players
	protected LinkedList<Integer> colors;
	// list of idPerso used to calculate idPersonnage
	// limits - from 0 to 11 for now, but can be changed if
	// maxNumbersofPlayers will be changed to be higher then 12
	// when player got out from table it must return his idPerso in the list
	private final LinkedList<Integer> idPersos;
	// Contient le type de jeu (ex. mathEnJeu)
	protected final GameType gameType;
	// Cet objet permet de d�terminer les r�gles de jeu pour cette salle
	protected final Regles objRegles;
	protected GenerateurPartie gameFactory;

	protected static final Logger objLogger = Logger.getLogger(Table.class);
	protected JoueurHumain master;

	/**
	 * Constructeur de la classe Table qui permet d'initialiser les membres
	 * priv�s de la table.
	 *
	 * @param salleParente La salle dans laquelle se trouve cette table
	 * @param noTable Le num�ro de la table
	 * @param joueur
	 * @param tempsPartie Le temps de la partie en minute
	 * @param name
	 * @param intNbLines
	 * @param intNbColumns
	 * @param gameType
	 */
	public Table(Salle salleParente, int noTable, JoueurHumain joueur, int tempsPartie,
			String name, int intNbLines, int intNbColumns, GameType gameType) {
		super();


		objControleurJeu = salleParente.getObjControleurJeu();
		objGestionnaireBD = salleParente.getObjControleurJeu().obtenirGestionnaireBD();
		objGestionnaireEvenements = new GestionnaireEvenements();

		objSalle = salleParente;
		intNoTable = noTable;
		this.gameType = gameType;

		// D�finir les r�gles de jeu pour la table
		objRegles = new Regles();

		setTableName(name);       
		intTempsTotal = tempsPartie;

		// Cr�er une nouvelle liste de joueurs
		lstJoueurs = new ConcurrentHashMap<String, JoueurHumain>();
		lstJoueursEnAttente = new ConcurrentHashMap<String, JoueurHumain>();
		master = joueur;

		// Au d�part, aucune partie ne se joue sur la table
		bolEstCommencee = false;
		bolEstArretee = true;

		objGestionnaireTemps = new GestionnaireTemps();
		objTacheSynchroniser = objControleurJeu.obtenirTacheSynchroniser();

		// Au d�part, on consid�re qu'il n'y a que des joueurs humains.
		// Lorsque l'on d�marrera une partie dans laPartieCommence(), on cr�era
		// autant de joueurs virtuels que intNombreJoueursVirtuels (qui devra donc
		// �tre affect� du bon nombre au pr�alable)
		intNombreJoueursVirtuels = 0;
		lstJoueursVirtuels = null;

		// Cette liste sera modifi� si jamais un joueur est d�connect�
		lstJoueursDeconnectes = new ConcurrentHashMap<String, JoueurHumain>();

		// Cr�er un thread pour le GestionnaireEvenements
		Thread threadEvenements = new Thread(objGestionnaireEvenements, "GestEven table ");
		// D�marrer le thread du gestionnaire d'�v�nements
		threadEvenements.start();

		// fill the list of colors
		this.colors = new LinkedList<Integer>();
		this.idPersos = new LinkedList<Integer>();

		creation(intNbLines, intNbColumns); // create the gameFactory

	}

	public void creation(int intNbLines, int intNbColumns) {
		objGestionnaireBD.chargerReglesTable(objRegles, gameType, objSalle.getRoomId());
		MAX_NB_PLAYERS = objRegles.getMaxNbPlayers();
		///System.out.println("We test Colors in the table  : " );

		this.setColors();
		this.setIdPersos();

		try {
			this.gameFactory = (GenerateurPartie)Class.forName("ServeurJeu.ComposantesJeu.GenerateurPartie.GenerateurPartie" + gameType).newInstance();
		} catch (InstantiationException e) {
			objLogger.error("Une erreur est survenue dans creation de la table :  ", e );
		} catch (IllegalAccessException e) {
			objLogger.error("Une erreur est survenue dans creation de la table :  ", e );
		} catch (ClassNotFoundException e) {
			objLogger.error("Une erreur est survenue dans creation de la table :  ", e );
		}

		gameFactory.setNbLines(intNbLines);
		gameFactory.setNbColumns(intNbColumns);

	}

	
	/**
	 * Cette fonction permet au joueur d'entrer dans la table courante.
	 * On suppose que le joueur n'est pas dans une autre table, que la table
	 * courante n'est pas compl�te et qu'il n'y a pas de parties en cours.
	 * Cette fonction va avoir pour effet de connecter le joueur dans la table
	 * courante.
	 * @param joueur Le joueur demandant d'entrer dans la table
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 * @throws NullPointerException : Si la liste listePersonnageJoueurs est nulle
	 *
	 * Synchronisme Cette fonction est synchronis�e pour �viter que deux
	 *              joueurs puissent entrer ou quitter la table en m�me temps.
	 *              On n'a pas � s'inqui�ter que le joueur soit modifi�
	 * 	            pendant le temps qu'on ex�cute cette fonction. De plus
	 *              on n'a pas � rev�rifier que la table existe bien (car
	 *              elle ne peut �tre supprim�e en m�me temps qu'un joueur
	 *              entre dans la table), qu'elle n'est pas compl�te ou
	 *              qu'une partie est en cours (car toutes les fonctions
	 *              permettant de changer �a sont synchronis�es).
	 */
	public void entrerTableAutres(JoueurHumain joueur, boolean doitGenererNoCommandeRetour) throws NullPointerException {
		
		addPlayerInListe(joueur);

		// Le joueur est maintenant entr� dans la table courante (il faut
		// cr�er un objet InformationPartie qui va pointer sur la table
		// courante)
		joueur.definirPartieCourante(new InformationPartieHumain(joueur, this));
		joueur.obtenirPartieCourante().remplirBoiteQuestions();

		// Si on doit g�n�rer le num�ro de commande de retour, alors
		// on le g�n�re, sinon on ne fait rien
		if (doitGenererNoCommandeRetour == true) {
			// G�n�rer un nouveau num�ro de commande qui sera
			// retourn� au client
			joueur.obtenirProtocoleJoueur().genererNumeroReponse();
		}

		// Pr�parer l'�v�nement de nouveau joueur dans la table.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		preparerEvenementJoueurEntreTable(joueur);

	}// end methode


	protected void addPlayerInListe(JoueurHumain joueur)
	{
		// Ajouter ce nouveau joueur dans la liste des joueurs de cette table
		lstJoueurs.put(joueur.obtenirNom(), joueur);

	}

	/**
	 * Cette m�thode permet au joueur pass� en param�tres de quitter la table.
	 * On suppose que le joueur est dans la table.
	 *
	 * @param joueur Le joueur demandant de quitter la table
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 * @param detruirePartieCourante
	 *
	 * Synchronisme Cette fonction est synchronis�e sur la liste des tables
	 *              puis sur la liste des joueurs de cette table, car il se
	 *              peut qu'on doive d�truire la table si c'est le dernier
	 *              joueur et qu'on va modifier la liste des joueurs de cette
	 *              table, car le joueur quitte la table. Cela �vite que des
	 *              joueurs entrent ou quittent une table en m�me temps.
	 *              On n'a pas � s'inqui�ter que le joueur soit modifi�
	 *              pendant le temps qu'on ex�cute cette fonction. Si on
	 *              inverserait les synchronisations, �a pourrait cr�er un
	 *              deadlock avec les personnes entrant dans la salle.

	 */
	public void quitterTable(JoueurHumain joueur, boolean doitGenererNoCommandeRetour, boolean detruirePartieCourante) {

		getOutPlayerFromListe(joueur);

		// Le joueur est maintenant dans aucune table
		if (detruirePartieCourante == true) {
			//joueur.obtenirPartieCourante().destruction();
			joueur.definirPartieCourante(null);
		}

		// Si on doit g�n�rer le num�ro de commande de retour, alors
		// on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
		// faux)
		if (doitGenererNoCommandeRetour == true) {
			// G�n�rer un nouveau num�ro de commande qui sera
			// retourn� au client
			joueur.obtenirProtocoleJoueur().genererNumeroReponse();
		}

		// Pr�parer l'�v�nement qu'un joueur a quitt� la table.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		preparerEvenementJoueurQuitteTable(joueur);

		// S'il ne reste aucun joueur dans la table et que la partie
		// est termin�e, alors on doit d�truire la table
		if ((lstJoueurs.isEmpty() && bolEstArretee == true) || (joueur.obtenirNom().equals(master.obtenirNom()) && !bolEstCommencee)) {
			// D�truire la table courante et envoyer les �v�nements
			// appropri�s
			getObjSalle().detruireTable(this);

		}

	}// end method

	protected void getOutPlayerFromListe(JoueurHumain player)
	{
		// Enlever le joueur de la liste des joueurs de cette table
		getBackOneIdPersonnage(player.obtenirPartieCourante().obtenirIdPersonnage());
		lstJoueurs.remove(player.obtenirNom());
		lstJoueursEnAttente.remove(player.obtenirNom());
		colors.add(player.obtenirPartieCourante().resetColor());		
	}

	/**
	 * Cette m�thode permet au joueur pass� en param�tres de recommencer la partie.
	 * On suppose que le joueur est dans la table.
	 *
	 * @param joueur Le joueur demandant de recommencer
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 *
	 * Synchronisme Cette fonction est synchronis�e sur la liste des tables
	 * 	            puis sur la liste des joueurs de cette table, car il se
	 *              peut qu'on doive d�truire la table si c'est le dernier
	 *              joueur et qu'on va modifier la liste des joueurs de cette
	 *              table, car le joueur quitte la table. Cela �vite que des
	 *              joueurs entrent ou quittent une table en m�me temps.
	 *              On n'a pas � s'inqui�ter que le joueur soit modifi�
	 *              pendant le temps qu'on ex�cute cette fonction. Si on
	 *              inverserait les synchronisations, �a pourrait cr�er un
	 *              deadlock avec les personnes entrant dans la salle.
	 */
	public void restartGame(JoueurHumain joueur, boolean doitGenererNoCommandeRetour) {
		// to get back perso's clothes color 
		//returned to the list when get out from game
		//joueur.obtenirPartieCourante().setClothesColor(colors.getLast());

		joueur.obtenirPartieCourante().cancelPosedQuestion();

		// Emp�cher d'autres thread de toucher � la liste des tables de
		// cette salle pendant que le joueur entre dans cette table
		synchronized (getObjSalle().obtenirListeTables()) {			
			addPlayerInListe(joueur);

			// Si on doit g�n�rer le num�ro de commande de retour, alors
			// on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
			// faux)
			if (doitGenererNoCommandeRetour == true) {
				// G�n�rer un nouveau num�ro de commande qui sera
				// retourn� au client
				joueur.obtenirProtocoleJoueur().genererNumeroReponse();

			}

			preparerEvenementJoueurRejoindrePartie(joueur);
			lstJoueursDeconnectes.remove(joueur);			
		}        
	}

	/**
	 * Cette m�thode permet au joueur pass� en param�tres de d�marrer la partie.
	 * On suppose que le joueur est dans la table.
	 * @param joueur Le joueur demandant de d�marrer la partie
	 * @param idDessin Le num�ro Id du personnage choisi par le joueur
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 * Synchronisme Cette fonction est synchronis�e sur la liste des joueurs
	 *              en attente, car il se peut qu'on ajouter ou retirer des
	 *              joueurs de la liste en attente en m�me temps. On n'a pas
	 *              � s'inqui�ter que le m�me joueur soit mis dans la liste
	 *              des joueurs en attente par un autre thread.
	 * @return
	 */
	public ResultatDemarrerPartie demarrerPartie(JoueurHumain joueur, int idDessin, boolean doitGenererNoCommandeRetour) {
		// Cette variable va permettre de savoir si le joueur est maintenant
		// attente ou non
		ResultatDemarrerPartie resultatDemarrerPartie;


		// Si une partie est en cours alors on va retourner PartieEnCours
		if (bolEstCommencee == true) {
			resultatDemarrerPartie = ResultatDemarrerPartie.PartieEnCours;
		} // Sinon si le joueur est d�j� en attente, alors on va retourner
		// DejaEnAttente
		else if (lstJoueursEnAttente.containsKey(joueur.obtenirNom())) {
			resultatDemarrerPartie = ResultatDemarrerPartie.DejaEnAttente;
		} else {
			// La commande s'est effectu�e avec succ�s
			resultatDemarrerPartie = ResultatDemarrerPartie.Succes;

			putInWaitingList(joueur, idDessin);

			// Si on doit g�n�rer le num�ro de commande de retour, alors
			// on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
			// faux)
			if (doitGenererNoCommandeRetour == true) {
				// G�n�rer un nouveau num�ro de commande qui sera
				// retourn� au client
				joueur.obtenirProtocoleJoueur().genererNumeroReponse();
			}

			// Si le nombre de joueurs en attente est maintenant le nombre
			// de joueurs que �a prend pour joueur au jeu, alors on lance
			// un �v�nement qui indique que la partie est commenc�e
			if (lstJoueursEnAttente.size() == MAX_NB_PLAYERS) {
				laPartieCommence("Aucun");
			}
		}				
		return resultatDemarrerPartie;
	}


	/**
	 * @param joueur
	 * @param idDessin
	 * @return
	 */
	protected void putInWaitingList(JoueurHumain joueur, int idDessin) {
		// Ajouter le joueur dans la liste des joueurs en attente
		lstJoueursEnAttente.put(joueur.obtenirNom(), joueur);

		int idPersonnage = this.getOneIdPersonnage(idDessin);

		// Garder en m�moire le Id du personnage choisi par le joueur et son dessin
		joueur.obtenirPartieCourante().setIdDessin(idDessin);
		joueur.obtenirPartieCourante().definirIdPersonnage(idPersonnage);


		// Pr�parer l'�v�nement de joueur en attente.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		preparerEvenementJoueurDemarrePartie(joueur, idPersonnage);		
	}




	/**
	 * This method will cancel the picture used by the player
	 * (action initiate by the player). He will choose another one 
	 * @param player
	 * @param doitGenererNoCommandeRetour
	 */
	public void cancelPicture(JoueurHumain player, boolean doitGenererNoCommandeRetour)
	{
		int idPersonnage = player.obtenirPartieCourante().obtenirIdPersonnage();
		this.getBackOneIdPersonnage(idPersonnage);
		//cancel the carrent ids
		player.obtenirPartieCourante().definirIdPersonnage(0);
		player.obtenirPartieCourante().setIdDessin(0);

		// Si on doit g�n�rer le num�ro de commande de retour, alors
		// on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
		// faux)
		if (doitGenererNoCommandeRetour == true) {
			// G�n�rer un nouveau num�ro de commande qui sera
			// retourn� au client
			player.obtenirProtocoleJoueur().genererNumeroReponse();
		}

		// Pr�parer l'�v�nement de joueur en attente.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		prepareEventPlayerCanceledPicture(player, idPersonnage);

	}

	/**
	 * This method will put on the system the picture selected  by the player
	 * (action initiate by the player).  
	 * @param player
	 */
	public void setNewPicture(JoueurHumain humainPlayer, int idDessin) {
		int idPersonnage = this.getOneIdPersonnage(idDessin);
		
		// Garder en m�moire le Id du personnage choisi par le joueur et son dessin
		humainPlayer.obtenirPartieCourante().setIdDessin(idDessin);
		humainPlayer.obtenirPartieCourante().definirIdPersonnage(idPersonnage);

		// Pr�parer l'�v�nement de joueur en attente.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		prepareEventPlayerSelectedNewPicture(humainPlayer, idPersonnage);

	}

	/**
	 *
	 * @param joueur
	 * @param doitGenererNoCommandeRetour
	 * @param strParamJoueurVirtuel
	 * @return
	 */
	public ResultatDemarrerPartie demarrerMaintenant(JoueurHumain joueur, boolean doitGenererNoCommandeRetour, String strParamJoueurVirtuel) {
		// Lorsqu'on fait d�marr� maintenant, le nombre de joueurs sur la
		// table devient le nombre de joueurs demand�, lorsqu'ils auront tous
		// fait OK, la partie d�marrera

		ResultatDemarrerPartie resultatDemarrerPartie;

		// Si une partie est en cours alors on va retourner PartieEnCours
		if (bolEstCommencee == true) {
			resultatDemarrerPartie = ResultatDemarrerPartie.PartieEnCours;
		} //TODO si joueur pas en attente?????
		else {
			// La commande s'est effectu�e avec succ�s
			resultatDemarrerPartie = ResultatDemarrerPartie.Succes;

			// Si on doit g�n�rer le num�ro de commande de retour, alors
			// on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
			// faux)
			if (doitGenererNoCommandeRetour == true) {
				// G�n�rer un nouveau num�ro de commande qui sera
				// retourn� au client
				joueur.obtenirProtocoleJoueur().genererNumeroReponse();
			}

			// Si le nombre de joueurs en attente est maintenant le nombre
			// de joueurs que �a prend pour joueur au jeu, alors on lance
			// un �v�nement qui indique que la partie est commenc�e

			laPartieCommence(strParamJoueurVirtuel);

		}
		return resultatDemarrerPartie;
	}


	/* Cette fonction permet d'obtenir un tableau contenant intNombreJoueurs
	 * noms de joueurs virtuels diff�rents
	 */
	private String[] obtenirNomsJoueursVirtuels(int intNombreJoueurs) {

		// Initialiser les noms des joueurs virtuels        
		String[] tNomsTemp = GestionnaireConfiguration.obtenirInstance().obtenirString("joueurs-virtuels.noms").split("/");

		// Obtenir le nombre de noms dans la banque
		int intQuantiteBanque = tNomsTemp.length;

		// D�claration d'un tableau pour m�langer les indices de noms
		int tIndexNom[] = new int[intQuantiteBanque];

		// Permet d'�changer des indices du tableau pour m�langer
		int intTemp;
		int intA;
		int intB;

		// Pr�parer le tableau pour le m�lange
		for (int i = 0; i < tIndexNom.length; i++) {
			tIndexNom[i] = i;
		}

		// M�langer les noms
		for (int i = 0; i < intNombreJoueurs; i++) {
			intA = i;
			intB = ControleurJeu.genererNbAleatoire(intQuantiteBanque);

			intTemp = tIndexNom[intA];
			tIndexNom[intA] = tIndexNom[intB];
			tIndexNom[intB] = intTemp;
		}

		// Cr�er le tableau de retour
		String tRetour[] = new String[intNombreJoueurs];

		// Choisir au hasard o� aller chercher les indices
		int intDepart = ControleurJeu.genererNbAleatoire(intQuantiteBanque);

		// Remplir le tableau avec les valeurs trouv�es
		for (int i = 0; i < intNombreJoueurs; i++) {
			tRetour[i] = new String(tNomsTemp[(i + intDepart) % intQuantiteBanque]);
		}

		return tRetour;
	}

	/**
	 * Method used to start the game
	 * @param strParamJoueurVirtuel
	 */
	private void laPartieCommence(String strParamJoueurVirtuel) {
		// Cr�er une nouvelle liste qui va garder les points des
		// cases libres (n'ayant pas d'objets dessus)
		ArrayList<Point> lstPointsCaseLibre = new ArrayList<Point>();


		// Cr�er un tableau de points qui va contenir la position
		// des joueurs
		Point[] objtPositionsJoueurs;

		// Contient les noms des joueurs virtuels
		String tNomsJoueursVirtuels[] = null;

		// Contiendra le dernier ID des objets
		objProchainIdObjet = new Integer(0);

		//TODO: Peut-�tre devoir synchroniser cette partie, il
		//      faut voir avec les autres bouts de code qui
		// 		v�rifient si la partie est commenc�e (c'est OK
		//		pour entrerTable)
		// Changer l'�tat de la table pour dire que maintenant une
		// partie est commenc�e
		bolEstCommencee = true;

		// Change l'�tat de la table pour dire que la partie
		// n'est pas arr�t�e (note: bolEstCommencee restera � true
		// pendant que les joueurs sont � l'�cran de pointage)
		bolEstArretee = false;

		// G�n�rer le plateau de jeu selon les r�gles de la table et
		// garder le plateau en m�moire dans la table
		objttPlateauJeu = getGameFactory().genererPlateauJeu(lstPointsCaseLibre, lstPointsFinish, this);

		// D�finir le prochain id pour les objets
		objProchainIdObjet++;

		// Obtenir la position des joueurs de cette table
		int nbJoueur = lstJoueursEnAttente.size(); //TODO a v�rifier

		// Contient le niveau de difficult� que le joueur d�sire pour
		// les joueurs virtuels
		// on obtient la difficult� par d�faut � partir du fichier de configuration
		GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();

		int intDifficulteJoueurVirtuel = config.obtenirNombreEntier("joueurs-virtuels.difficulte_defaut");


		// Obtenir le nombre de joueurs virtuel requis
		// V�rifier d'abord le param�tre envoyer par le joueur
		if (strParamJoueurVirtuel.equals("Aucun")) {
			intNombreJoueursVirtuels = 0;
		} else {
			// Le joueur veut des joueurs virtuels
			if (strParamJoueurVirtuel.equals("Facile")) {
				intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_FACILE;
			} else if (strParamJoueurVirtuel.equals("Intermediaire")) {
				intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_MOYEN;
			} else if (strParamJoueurVirtuel.equals("Difficile")) {
				intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_DIFFICILE;
			} else if (strParamJoueurVirtuel.equals("TresDifficile")) {
				intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_TRES_DIFFICILE;
			}

			// D�terminer combien de joueurs virtuels on veut
			int maxNombreJoueursVirtuels = getRegles().getNbVirtualPlayers();
			if (nbJoueur < getRegles().getNbTracks()) {
				intNombreJoueursVirtuels = maxNombreJoueursVirtuels;
				while (maxNombreJoueursVirtuels + nbJoueur > getRegles().getNbTracks()) {
					intNombreJoueursVirtuels--;
					maxNombreJoueursVirtuels--;
				}
			}

		}

		objtPositionsJoueurs = this.getGameFactory().genererPositionJoueurs(this, nbJoueur + intNombreJoueursVirtuels, lstPointsCaseLibre);
		// Cr�ation d'une nouvelle liste
		Joueur[] lstJoueursParticipants = new Joueur[nbJoueur + intNombreJoueursVirtuels];

		// Obtenir un it�rateur pour l'ensemble contenant les personnages
		Iterator<JoueurHumain> objIterateurListeJoueurs = lstJoueursEnAttente.values().iterator();

		// S'il y a des joueurs virtuels, alors on va cr�er une nouvelle liste
		// qui contiendra ces joueurs
		if (intNombreJoueursVirtuels > 0) {
			lstJoueursVirtuels = new ArrayList<JoueurVirtuel>();

			// Aller trouver les noms des joueurs virtuels
			tNomsJoueursVirtuels = obtenirNomsJoueursVirtuels(intNombreJoueursVirtuels);
		}

		// Cette variable permettra d'affecter aux joueurs virtuels des id
		// de personnage diff�rents de ceux des joueurs humains
		int intIdPersonnage = 1;
		int position = 0;

		// Passer toutes les positions des joueurs et les d�finir
		for (int i = 0; i < objtPositionsJoueurs.length; i++) {

			// On doit affecter certains positions aux joueurs humains et d'autres aux joueurs
			// virtuels. La grandeur de objtPositionsJoueurs est nbJoueur + intNombreJoueursVirtuels
			if (i < nbJoueur) {

				// Comme les positions sont g�n�r�es al�atoirement, on
				// se fou un peu duquel on va d�finir la position en
				// premier, on va donc passer simplement la liste des
				// joueurs
				// Cr�er une r�f�rence vers le joueur courant
				// dans la liste (pas besoin de v�rifier s'il y en a un
				// prochain, car on a g�n�r� la position des joueurs
				// selon cette liste
				JoueurHumain objJoueur = objIterateurListeJoueurs.next();

				if (objJoueur.getRole() == 2) {
					// D�finir la position du joueur master
					objJoueur.obtenirPartieCourante().definirPositionJoueur(objtPositionsJoueurs[objtPositionsJoueurs.length - 1]);

					// Ajouter la position du master dans la liste
					//lstPositionsJoueurs.put(objJoueur.obtenirNomUtilisateur(), objtPositionsJoueurs[objtPositionsJoueurs.length - 1]);

					position--;
				} else {

					// D�finir la position du joueur courant
					objJoueur.obtenirPartieCourante().definirPositionJoueur(objtPositionsJoueurs[position]);

					// Ajouter la position du joueur dans la liste
					//lstPositionsJoueurs.put(objJoueur.obtenirNomUtilisateur(), objtPositionsJoueurs[position]);
				}

				lstJoueursParticipants[i] = objJoueur;



			} else {
				int IDdess;
				boolean weHaveThisNumber;
				// to have differents pictures for the virtual players
				do {
					weHaveThisNumber = false;
					IDdess = ControleurJeu.genererNbAleatoire(11) + 1;

					for(JoueurVirtuel joueur:lstJoueursVirtuels)
					{
						if(joueur.obtenirPartieCourante().getIdDessin() == IDdess)
							weHaveThisNumber = true;
					}
				} while (weHaveThisNumber);

				// On se rendra ici seulement si intNombreJoueursVirtuels > 0
				// C'est ici qu'on cr�e les joueurs virtuels, ils vont commencer
				// � jouer plus loin

				// Ajouter un joueur virtuel dans la table
				intIdPersonnage = 10000 + 100 * IDdess + 50 + i;

				// Utiliser le prochaine id de personnage libre
				while (!idPersonnageEstLibre(intIdPersonnage)) {
					// Incr�menter le id du personnage en esp�rant en trouver un autre
					intIdPersonnage++;
				}				

				// Cr�� le joueur virtuel selon le niveau de difficult� d�sir�
				JoueurVirtuel objJoueurVirtuel = new JoueurVirtuel(tNomsJoueursVirtuels[i - nbJoueur], this);

				objJoueurVirtuel.definirPartieCourante(new InformationPartieVirtuel(objJoueurVirtuel, this, intIdPersonnage));

				// D�finir sa position
				objJoueurVirtuel.obtenirPartieCourante().definirPositionJoueurVirtuel(objtPositionsJoueurs[position]);

				// Ajouter le joueur virtuel � la liste
				lstJoueursVirtuels.add(objJoueurVirtuel);

				objJoueurVirtuel.obtenirPartieCourante().setClothesColor(this.getOneColor());
				objJoueurVirtuel.obtenirPartieCourante().setIdDessin(IDdess);


				// Pr�parer l'�v�nement de joueur en attente.
				// Cette fonction va passer les joueurs et cr�er un
				// InformationDestination pour chacun et ajouter l'�v�nement
				// dans la file de gestion d'�v�nements
				preparerEvenementJoueurEntreTable(objJoueurVirtuel);					


				// Pour le prochain joueur virtuel
				intIdPersonnage++;

				// Ajouter le joueur virtuel � la liste des positions, liste qui sera envoy�e
				// aux joueurs humains
				lstJoueursParticipants[i] = objJoueurVirtuel;


			}
			position++;
		}

		// need to give time to virtuels to enter table - before start game
		// the enter table event is sent by GE of the room 
		// we have desynchronisation of events 
		objGestionnaireEvenements.pause();

		// Maintenant pour tous les joueurs, s'il y a des joueurs
		// virtuels de pr�sents, on leur envoit un message comme
		// quoi les joueurs virtuels sont pr�ts
		if (intNombreJoueursVirtuels > 0) {
			// separate to proper hold virtiel's creation
			// we don't have command number for events ... maybe to do

			for (int i = 0; i < lstJoueursVirtuels.size(); i++) {
				// Pr�parer l'�v�nement de joueur en attente.
				// Cette fonction va passer les joueurs et cr�er un
				// InformationDestination pour chacun et ajouter l'�v�nement
				// dans la file de gestion d'�v�nements
				JoueurVirtuel objJoueurVirtuel = lstJoueursVirtuels.get(i);
				//preparerEvenementJoueurEntreTable(objJoueurVirtuel);
				preparerEvenementJoueurDemarrePartie(objJoueurVirtuel, objJoueurVirtuel.obtenirPartieCourante().obtenirIdPersonnage());
			}

		}



		// On peut maintenant vider la liste des joueurs en attente
		// car elle ne nous sert plus � rien
		lstJoueursEnAttente.clear();

		// Pr�parer l'�v�nement que la partie est commenc�e.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		preparerEvenementPartieDemarree(lstJoueursParticipants);

		int tempsStep = 1;
		objTacheSynchroniser.ajouterObservateur(this);
		objMinuterie = new Minuterie(intTempsTotal * 60, tempsStep);
		objMinuterie.ajouterObservateur(this);
		//objControleurJeu.obtenirGestionnaireTemps().ajouterTache(objMinuterie, tempsStep);
		objGestionnaireTemps.ajouterTache(objMinuterie, tempsStep);

		// Obtenir la date � ce moment pr�cis
		objDateDebutPartie = new Date();

		// D�marrer tous les joueurs virtuels
		if (intNombreJoueursVirtuels > 0) {
			for (int i = 0; i < lstJoueursVirtuels.size(); i++) {
				Thread threadJoueurVirtuel = new Thread(lstJoueursVirtuels.get(i), "Virtuel");
				threadJoueurVirtuel.start();
			}

		}

	}// end method

	
	
	public void arreterPartie(String joueurGagnant) {
		objLogger.info(" Arreter partie - dans la table " + objControleurJeu.obtenirGestionnaireTemps().toString());
		TreeSet<StatisticsPlayer> ourResults = new TreeSet<StatisticsPlayer>();
		
		// bolEstArretee permet de savoir si cette fonction a d�j� �t� appel�e
		// de plus, bolEstArretee et bolEstCommencee permettent de conna�tre
		// l'�tat de la partie
		if (bolEstArretee == false) {	
			
			objTacheSynchroniser.enleverObservateur(this);

			// S'il y a au moins un joueur qui a compl�t� la partie,
			// alors on ajoute les informations de cette partie dans la BD
			if (lstJoueurs.size() > 0) {
								
				// Sert � d�terminer le meilleur score pour cette partie
				int meilleurPointage = 0;				

				// Parcours des joueurs virtuels pour trouver le meilleur pointage
				if (lstJoueursVirtuels != null) {
					for (int i = 0; i < lstJoueursVirtuels.size(); i++) {
						JoueurVirtuel objJoueurVirtuel = lstJoueursVirtuels.get(i);
						if (objJoueurVirtuel.obtenirPartieCourante().obtenirPointage() > meilleurPointage) {
							meilleurPointage = objJoueurVirtuel.obtenirPartieCourante().obtenirPointage();
						}
						ourResults.add(new StatisticsPlayer(objJoueurVirtuel.obtenirNom(), objJoueurVirtuel.obtenirPartieCourante().obtenirPointage(), objJoueurVirtuel.obtenirPartieCourante().getPointsFinalTime()));
					}
				}


				// Parcours des joueurs pour trouver le meilleur pointage
				int cleJoueurGagnant = 0; //0 veut dire un joueur virtuel gagne.
				for (JoueurHumain objJoueurHumain: lstJoueurs.values()) {
					InformationPartieHumain infoPartie = objJoueurHumain.obtenirPartieCourante();
					//infoPartie.getObjBoiteQuestions().getInfo();
					if (infoPartie.obtenirPointage() > meilleurPointage) {
						meilleurPointage = infoPartie.obtenirPointage();
					}

					ourResults.add(new StatisticsPlayer(objJoueurHumain.obtenirNom(), infoPartie.obtenirPointage(), infoPartie.getPointsFinalTime()));

					if (!joueurGagnant.equals("")) {
						if (objJoueurHumain.obtenirNom().equalsIgnoreCase(joueurGagnant))
							cleJoueurGagnant = objJoueurHumain.obtenirCleJoueur();
					}
					else if (ourResults.last().getUsername().equalsIgnoreCase(objJoueurHumain.obtenirNom()))
						cleJoueurGagnant = objJoueurHumain.obtenirCleJoueur();

				}
				
				// Ajouter la partie dans la BD
				int clePartie = objGestionnaireBD.ajouterInfosPartieTerminee(
						objSalle.getRoomId(), gameType, objDateDebutPartie, intTempsTotal, cleJoueurGagnant);
				
				// Parcours des joueurs pour mise � jour de la BD et
				// pour ajouter les infos de la partie compl�t�e
				for (JoueurHumain joueur: lstJoueurs.values()) {
					
					boolean estGagnant = (joueur.obtenirCleJoueur() == cleJoueurGagnant);
					//objGestionnaireBD.ajouterInfosJoueurPartieTerminee(clePartie, joueur, estGagnant);
					//if(joueur.getRole() > 1)
					//joueur.obtenirPartieCourante().writeInfo();
				}				

								
				// Parcours des joueurs pour mise � jour de la BD et
				// pour ajouter les infos de la partie compl�t�e
				for (JoueurHumain joueur: lstJoueurs.values()) {
					joueur.obtenirPartieCourante().getObjGestionnaireBD().mettreAJourJoueur(intTempsTotal);
					// if the game was with the permission to use user's money from DB
					if (joueur.obtenirPartieCourante().obtenirTable().getRegles().isBolMoneyPermit()) {
						joueur.obtenirPartieCourante().getObjGestionnaireBD().setNewPlayersMoney();
					}					

				}
				
				preparerEvenementPartieTerminee(ourResults, joueurGagnant);
			}
			
			if (intNombreJoueursVirtuels > 0) {
				synchronized(lstJoueursVirtuels){
					// Arr�ter les threads des joueurs virtuels

					int n = lstJoueursVirtuels.size();
					for (int i = 0; i < n; i++) {
						((JoueurVirtuel)lstJoueursVirtuels.get(i)).arreterThread();

					}
					lstJoueursVirtuels.clear();
				}
			}

			// wipeout players from the table
			if (!lstJoueurs.isEmpty()) {

				lstJoueurs.clear();
			}

			// Enlever les joueurs d�connect�s de cette table de la
			// liste des joueurs d�connect�s du serveur pour �viter
			// qu'ils ne se reconnectent et tentent de rejoindre une partie termin�e
			for (String name : lstJoueursDeconnectes.keySet()) {
				objControleurJeu.enleverJoueurDeconnecte(name);
			}

			// Enlever les joueurs d�connect�s de cette table
			lstJoueursDeconnectes.clear();
			lstJoueursDeconnectes = null;

			// Arr�ter la partie
			bolEstArretee = true;			
			
			try{
				objLogger.info(" Remove task - " + objControleurJeu.obtenirGestionnaireTemps().toString());
				objControleurJeu.obtenirGestionnaireTemps().enleverTache(objMinuterie);
			}catch (IllegalStateException ex){
				objControleurJeu.setNewTimer();
				objLogger.error("Une erreur est survenue: objControleurJeu.setNewTimer()", ex);
			}

			// to discard Banana or Brainiac tasks
			//objGestionnaireTemps.stopIt();
			
			// Si jamais les joueurs humains sont tous d�connect�s, alors
			// il faut d�truire la table ici
			if (lstJoueurs.isEmpty()) {
				// D�truire la table courante et envoyer les �v�nements
				// appropri�s
				getObjSalle().detruireTable(this);				
			}			
			
		}// end if bolEstArretee

		if (bolEstArretee == true) {

			objMinuterie = null;
			objGestionnaireTemps = null;
			objTacheSynchroniser = null;            

			this.objttPlateauJeu = null;
			this.gameFactory = null;
		}
	}// end method

	protected void arreter(){
		// this.objGestionnaireEvenements.arreterGestionnaireEvenements();
		this.objGestionnaireEvenements = null;
	}

	/**
	 * If all the other players than that in param is on the points of Finish line
	 * @param joueurHumain
	 * @return
	 */
	public boolean isAllTheHumainsOnTheFinish() {
		boolean isAllPlayers = true;
		int tracks = getRegles().getNbTracks();


		// Pass all players to find their position
		for (JoueurHumain objJoueurHumain: lstJoueurs.values()) {
			Point pozJoueur = objJoueurHumain.obtenirPartieCourante().obtenirPositionJoueur();
			Point objPoint = new Point(gameFactory.getNbLines() - 1, gameFactory.getNbColumns() - 1);
			Point objPointFinish = new Point();
			boolean isOn = false;
			for (int i = 0; i < tracks; i++) {
				objPointFinish.setLocation(objPoint.x, objPoint.y - i);
				if (pozJoueur.equals(objPointFinish)) {
					isOn = true;
				}
			}
			if (!isOn) {
				isAllPlayers = false;
			}

		}		
		return isAllPlayers;
	}

	/**
	 * Cette fonction permet de retourner le num�ro de la table courante.
	 *
	 * @return int : Le num�ro de la table
	 */
	public int obtenirNoTable() {
		return intNoTable;
	}

	/**
	 * Cette fonction permet de retourner la liste des joueurs. La vraie liste
	 * est retourn�e.
	 *
	 * @return TreeMap : La liste des joueurs se trouvant dans la table courante
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle doit
	 * 				  l'�tre par l'appelant de cette fonction tout d�pendant
	 * 				  du traitement qu'elle doit faire
	 */
	public ConcurrentHashMap<String, JoueurHumain> obtenirListeJoueurs() {
		return lstJoueurs;
	}

	/**
	 * Cette fonction permet de retourner la liste des joueurs qui sont en
	 * attente de jouer une partie. La vraie liste est retourn�e.
	 *
	 * @return TreeMap : La liste des joueurs en attente se trouvant dans la
	 * 					 table courante
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle doit
	 * 				  l'�tre par l'appelant de cette fonction tout d�pendant
	 * 				  du traitement qu'elle doit faire
	 */
	public ConcurrentHashMap<String, JoueurHumain> obtenirListeJoueursEnAttente() {
		return lstJoueursEnAttente;
	}

	/**
	 * Cette fonction permet de retourner le temps total des parties de cette
	 * table.
	 *
	 * @return int : Le temps total des parties de cette table
	 */
	public int obtenirTempsTotal() {
		return intTempsTotal;
	}

	/**
	 * Cette fonction permet de d�terminer si la table est compl�te ou non
	 * (elle est compl�te si le nombre de joueurs dans cette table �gale le
	 * nombre de joueurs maximum par table).
	 *
	 * @return boolean : true si la table est compl�te
	 * 					 false sinon
	 *
	 * Synchronisme : Cette fonction est synchronis�e car il peut s'ajouter de
	 * 				  nouveaux joueurs ou d'autres peuvent quitter pendant la
	 * 				  v�rification.
	 */
	public boolean estComplete() {

		// Si la taille de la liste de joueurs �gale le nombre maximal de
		// joueurs alors la table est compl�te, sinon elle ne l'est pas
		return (lstJoueurs.size() == MAX_NB_PLAYERS);		
	}

	/**
	 * Cette fonction permet de d�terminer si une partie est commenc�e ou non.
	 *
	 * @return boolean : true s'il y a une partie en cours
	 * 					 false sinon
	 */
	public boolean estCommencee() {
		return bolEstCommencee;
	}

	/**
	 * Cette fonction retourne le plateau de jeu courant.
	 *
	 * @return Case[][] : Le plateau de jeu courant,
	 * 					  null s'il n'y a pas de partie en cours
	 */
	public Case[][] obtenirPlateauJeuCourant() {
		return objttPlateauJeu;
	}

	/**
	 * Cette fonction retourne une case du plateau de jeu courant.
	 *
	 * @param playerX la coordon�e x de la position du joueur
	 * @param playerY la coordon�e y de la position du joueur
	 * @return Case La case du plateau de jeu correspondant � la position du
	 *         joueur.
	 */
	public Case getCase(int playerX, int playerY) {
		return objttPlateauJeu[playerX][playerY];
	}

	/**
	 * Cette m�thode permet de remplir la liste des personnages des joueurs
	 * ou les cl�s seront le id d'utilisateur du joueur et le contenu le
	 * num�ro du personnage. On suppose que le joueur courant n'est pas
	 * encore dans la liste.
	 *
	 * @throws NullPointerException : Si la liste des personnages est � nulle
	 */

	public JoueurHumain[] remplirListePersonnageJoueurs() throws NullPointerException {
		JoueurHumain[] humains = new JoueurHumain[lstJoueurs.size()];
		int iter = 0;
		for (JoueurHumain objJoueur: lstJoueurs.values()) {
			humains[iter++] = objJoueur;
		}
		return humains;
	}

	/**
	 * Cette m�thode permet de pr�parer l'�v�nement de l'entr�e d'un joueur
	 * dans la table courante. Cette m�thode va passer tous les joueurs
	 * de la salle courante et pour ceux devant �tre avertis (tous sauf le
	 * joueur courant pass� en param�tre), on va obtenir un num�ro de commande,
	 * on va cr�er un InformationDestination et on va ajouter l'�v�nement dans
	 * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel
	 * de cette fonction, la liste des joueurs est synchronis�e.
	 * @param colorS
	 *
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
	 * 								  vient d'entrer dans la table
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				  par l'appelant (entrerTable).
	 */
	protected void preparerEvenementJoueurEntreTable(Joueur player) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur est entr� dans la table
		EvenementJoueurEntreTable joueurEntreTable = new EvenementJoueurEntreTable(intNoTable, player);

		// Passer tous les joueurs de la salle et leur envoyer un �v�nement
		getObjSalle().broadcastEvent(joueurEntreTable, player);
		//broadcastEvent(joueurEntreTable, player);


	}

	/**
	 * Cette m�thode permet de pr�parer l'�v�nement du d�part d'un joueur
	 * de la table courante. Cette m�thode va passer tous les joueurs
	 * de la salle courante et pour ceux devant �tre avertis (tous sauf le
	 * joueur courant pass� en param�tre), on va obtenir un num�ro de commande,
	 * on va cr�er un InformationDestination et on va ajouter l'�v�nement dans
	 * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel
	 * de cette fonction, la liste des joueurs est synchronis�e.
	 *
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
	 * 								  vient de quitter la table
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				  par l'appelant (quitterTable).
	 */
	protected void preparerEvenementJoueurQuitteTable(JoueurHumain joueur) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur a quitt� la table
		EvenementJoueurQuitteTable joueurQuitteTable = new EvenementJoueurQuitteTable(intNoTable, joueur.obtenirNom());

		// Passer tous les joueurs de la salle et leur envoyer un �v�nement
		getObjSalle().broadcastEvent(joueurQuitteTable, joueur);
	}

	/**
	 * Cette m�thode permet de pr�parer l'�v�nement du d�marrage d'une partie
	 * de la table courante. Cette m�thode va passer tous les joueurs
	 * de la table courante et pour ceux devant �tre avertis (tous sauf le
	 * joueur courant pass� en param�tre), on va obtenir un num�ro de commande,
	 * on va cr�er un InformationDestination et on va ajouter l'�v�nement dans
	 * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel
	 * de cette fonction, la liste des joueurs est synchronis�e.
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
	 * 								  vient de d�marrer la partie
	 * @param int idPersonnage : Le num�ro Id du personnage choisi par le joueur
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				  par l'appelant (demarrerPartie).
	 */
	protected void preparerEvenementJoueurDemarrePartie(Joueur joueur, int idPersonnage) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur d�marr� une partie
		EvenementJoueurDemarrePartie joueurDemarrePartie = new EvenementJoueurDemarrePartie(joueur.obtenirNom(), idPersonnage);

		broadcastEvent(joueurDemarrePartie, joueur);

	}	


	/**
	 * Cette m�thode permet de pr�parer l'�v�nement du cancelation du dessin
	 * choisi par joueur avant. Cette m�thode va passer tous les joueurs
	 * de la table courante et pour ceux devant �tre avertis (tous sauf le
	 * joueur courant pass� en param�tre), on va obtenir un num�ro de commande,
	 * on va cr�er un InformationDestination et on va ajouter l'�v�nement dans
	 * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel
	 * de cette fonction, la liste des joueurs est synchronis�e.
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
	 * 								  vient de d�marrer la partie
	 * @param int idPersonnage : Le num�ro Id du personnage annuler par le joueur
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				  par l'appelant (playerCanceledPicture).
	 */
	private void prepareEventPlayerCanceledPicture(JoueurHumain player,
			int idPersonnage) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur d�marr� une partie
		EventPlayerPictureCanceled canceledPicture = new EventPlayerPictureCanceled(player.obtenirNom(), idPersonnage);

		broadcastEvent(canceledPicture, player);
	}



	private void prepareEventPlayerSelectedNewPicture(JoueurHumain player, 
			int idPersonnage) 
	{
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur d�marr� une partie
		EventPlayerSelectedPicture selectedNewPicture = new EventPlayerSelectedPicture(player.obtenirNom(), idPersonnage);

		broadcastEvent(selectedNewPicture, player);	
	}


	/**
	 * Cette m�thode permet de pr�parer l'�v�nement du d�marrage de partie
	 * de la table courante. Cette m�thode va passer tous les joueurs
	 * de la table courante et on va obtenir un num�ro de commande, on va
	 * cr�er un InformationDestination et on va ajouter l'�v�nement dans
	 * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel
	 * de cette fonction, la liste des joueurs est synchronis�e.
	 * @param playersListe
	 *
	 * @param TreeMap : La liste contenant les positions des joueurs
	 *
	 * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				  par l'appelant (demarrerPartie).
	 */
	protected void preparerEvenementPartieDemarree(Joueur[] playersListe) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs de la table qu'un joueur a d�marr� une partie
		EvenementPartieDemarree partieDemarree = new EvenementPartieDemarree(this, playersListe);

		broadcastEvent(partieDemarree);
	}

	/**
	 *
	 * @param nomUtilisateur
	 * @param nouveauPointage
	 */
	public void preparerEvenementMAJPointage(Joueur player, int nouveauPointage) {
		// Cr�er un nouveal �v�nement qui va permettre d'envoyer l'�v�nment
		// aux joueurs pour signifier une modification du pointage
		EvenementMAJPointage majPointage = new EvenementMAJPointage(player.obtenirNom(), nouveauPointage);
		broadcastEvent(majPointage, player);
	}

	/**
	 * Used to inform another players that one player is back to the game
	 * We need to give them his user name and his points
	 * @param nomUtilisateur le nom du joueur qui rejoint la partie
	 * @param idPersonnage La cl� id du personnage du joueur qui rejoint la partie
	 * @param points Le pointage du joueur qui rejoint la partie
	 */
	public void preparerEvenementJoueurRejoindrePartie(JoueurHumain player) {
		// Cr�er un nouveal �v�nement qui va permettre d'envoyer l'�v�nment
		// aux joueurs pour signifier une modification du pointage
		EvenementJoueurRejoindrePartie maPartie = new EvenementJoueurRejoindrePartie(player);
		broadcastEvent(maPartie, player);		
	}


	public void preparerEvenementMAJArgent(Joueur player, int nouvelArgent) {
		// Cr�er un nouveal �v�nement qui va permettre d'envoyer l'�v�nment
		// aux joueurs pour signifier une modification de l'argent
		EvenementMAJArgent majArgent = new EvenementMAJArgent(player.obtenirNom(), nouvelArgent);

		broadcastEvent(majArgent, player);		
	}


	/**
	 *
	 * @param joueurQuiUtilise
	 * @param joueurAffecte
	 * @param objetUtilise
	 * @param autresInformations
	 */
	public void preparerEvenementUtiliserObjet(String joueurQuiUtilise, String joueurAffecte, String objetUtilise, String autresInformations) {
		// M�me chose que la fonction pr�c�dente, mais envoie plut�t les informations quant � l'utilisation d'un objet dont tous devront �tre au courant
		EvenementUtiliserObjet utiliserObjet = new EvenementUtiliserObjet(joueurQuiUtilise, joueurAffecte, objetUtilise, autresInformations);
		broadcastEvent(utiliserObjet);		
	}


	public void preparerEvenementMessageChat(String joueurQuiEnvoieLeMessage, String messageAEnvoyer) {
		// Meme chose que la fonction pr�c�dente, mais envoie plut�t un message de la part d'un joueur � tous les joueurs de la table
		EvenementMessageChat messageChat = new EvenementMessageChat(joueurQuiEnvoieLeMessage, messageAEnvoyer);
		broadcastEvent(messageChat);
	}

	/**
	 * Method that is used to prepare event of move of the player
	 * @param nomUtilisateur
	 * @param collision
	 * @param oldPosition
	 * @param positionJoueur
	 * @param nouveauPointage
	 * @param nouvelArgent
	 * @param bonus
	 * @param objetUtilise
	 */
	public void preparerEvenementJoueurDeplacePersonnage(Joueur player, String collision,
			Point oldPosition, Point positionJoueur, int nouveauPointage, int nouvelArgent, int bonus, String objetUtilise) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur se deplace

		EvenementJoueurDeplacePersonnage joueurDeplacePerso = new EvenementJoueurDeplacePersonnage(player.obtenirNom(),
				oldPosition, positionJoueur, collision, nouveauPointage, nouvelArgent, bonus);
		broadcastEvent(joueurDeplacePerso, player);
	}

	/**
	 *
	 */
	protected void preparerEvenementSynchroniser() {
		//Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs de la table
		EvenementSynchroniserTemps synchroniser = new EvenementSynchroniserTemps(objMinuterie.obtenirTempsActuel());
		// Passer tous les joueurs de la table et leur envoyer un �v�nement
		broadcastEvent(synchroniser);
	}

	/**
	 *
	 * @param ourResults
	 * @param joueurGagnant
	 */
	protected void preparerEvenementPartieTerminee(TreeSet<StatisticsPlayer> ourResults, String joueurGagnant) {
		// joueurGagnant r�f�re � la personne qui a atteint le WinTheGame (s'il y a lieu)

		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs de la table
		EvenementPartieTerminee partieTerminee = new EvenementPartieTerminee(this, ourResults, joueurGagnant);
		// Passer tous les joueurs de la salle et leur envoyer un �v�nement
		broadcastEvent(partieTerminee);
	}

	public void tempsEcoule() {
		arreterPartie("");
	}

	public int getObservateurMinuterieId() {
		return obtenirNoTable();
	}

	public void synchronise() {

		preparerEvenementSynchroniser();

	}

	public int getObservateurSynchroniserId() {
		return obtenirNoTable();
	}

	public boolean estArretee() {
		return bolEstArretee;
	}

	public int obtenirTempsRestant() {
		if (objMinuterie == null) {
			return intTempsTotal;
		} else {
			return objMinuterie.obtenirTempsActuel();
		}

	}

	// return a percents of elapsed time
	public int getRelativeTime() {
		if (objMinuterie == null) {
			return 0;
		} else {
			return (intTempsTotal * 60 - objMinuterie.obtenirTempsActuel()) * 180 / (intTempsTotal * 60);
		}

	}

	/* Cette fonction permet de d�finir le nombre de joueurs virtuels que l'on
	 * veut pour cette table
	 * @param: nb -> Nouveau nombre de joueurs virtuels
	 */
	public void setNombreJoueursVirtuels(int nb) {
		intNombreJoueursVirtuels = nb;
	}

	/* Cette fonction permet d'obtenir le nombre de joueurs virtuels pour
	 * cette table
	 */
	public int getNombreJoueursVirtuels() {
		return intNombreJoueursVirtuels;
	}

	public ArrayList<JoueurVirtuel> obtenirListeJoueursVirtuels() {
		return lstJoueursVirtuels;
	}

	/*
	 * Lorsqu'un joueur est d�connect� d'une partie en cours, on appelle
	 * cette fonction qui se charge de conserver les r�f�rences vers
	 * les informations pour ce joueur
	 */
	public void ajouterJoueurDeconnecte(JoueurHumain joueurHumain) {
		lstJoueursDeconnectes.put(joueurHumain.obtenirNom(), joueurHumain);
	}

	
	public ConcurrentHashMap<String, JoueurHumain> obtenirListeJoueursDeconnectes() {
		return lstJoueursDeconnectes;
	}

	public Integer obtenirProchainIdObjet() {
		synchronized (objProchainIdObjet) {
			return objProchainIdObjet;
		}
	}

	/**
	 * Used to return the current valid id for the objects in
	 * Magasins(Shop) and automaticaly icrement to the new value
	 * @return
	 */
	public Integer getAndIncrementNewIdObject() {
			this.objProchainIdObjet++;
			return this.objProchainIdObjet - 1;
		
	}

	public void setObjProchainIdObjet(Integer objProchainIdObjet) {
		
			this.objProchainIdObjet = objProchainIdObjet;		
	}

	/**
	 * Aller chercher dans la liste des joueurs sur cette table
	 * les ID des personnages choisi et v�rifier si le id intID est
	 * d�j� choisi
	 *
	 * Cette fonction v�rifie dans la liste des joueurs et non dans
	 * la liste des joueurs en attente
	 */
	protected boolean idPersonnageEstLibre(int intID) {

		// Parcourir la liste des joueurs et v�rifier si le id est libre
		for (JoueurHumain objJoueurHumain: lstJoueurs.values()) {
			if (objJoueurHumain.obtenirPartieCourante().obtenirIdPersonnage() == intID) {
				return false;
			}
		}		
		// Si on se rend ici, on a parcouru tous les joueurs et on n'a pas
		// trouv� ce id de personnage, donc le id est libre
		return true;
	}

	/**
	 * Aller chercher dans la liste des joueurs en attente
	 * les ID des personnages choisi et v�rifier si le id intID est
	 * d�j� choisi
	 *
	 * Cette fonction v�rifie dans la liste des joueurs en attente
	 * la liste des joueurs (doit donc �tre utilis� avant que la partie commence)
	 *
	 * @param intID
	 * @return
	 */
	public boolean idPersonnageEstLibreEnAttente(int intID) {
		// Parcourir la liste des joueurs et v�rifier le id
		for (JoueurHumain objJoueurHumain: lstJoueursEnAttente.values()) {
			// V�rifier le id
			if (objJoueurHumain.obtenirPartieCourante().obtenirIdPersonnage() == intID) {
				// D�j� utilis�
				return false;
			}
		}

		// Si on se rend ici, on a parcouru tous les joueurs et on n'a pas
		// trouv� ce id de personnage, donc le id est libre
		return true;
	}// end method

	/**
	 * @param username
	 * @return Humain player
	 */
	public JoueurHumain obtenirJoueurHumainParSonNom(String username) {
		for (JoueurHumain j: lstJoueurs.values()) {
			if (username.equals(j.obtenirNom())) {
				return j;
			}
		}
		return null;		
	}

	/**
	 *
	 * @param username
	 * @return Virtual Player
	 */
	public JoueurVirtuel obtenirJoueurVirtuelParSonNom(String username) {
		for (int i = 0; i < lstJoueursVirtuels.size(); i++) {
			JoueurVirtuel j = (JoueurVirtuel)lstJoueursVirtuels.get(i);
			//System.out.println(username + " compare " + j.obtenirNom());
			if (username.equals(j.obtenirNom())) {
				return j;
			}
		}
		return (JoueurVirtuel)null;
	}


	public Salle getObjSalle() {
		return objSalle;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		if (tableName.trim().equals("")) {
			this.tableName = "" + this.intNoTable;
		} else {
			this.tableName = tableName;
		}
	}

	public Point getPositionPointFinish() {
		Random objRandom = new Random();

		return lstPointsFinish.get(objRandom.nextInt(lstPointsFinish.size() - 1));
	}

	public boolean checkPositionPointsFinish(Point objPoint) {
		boolean isTrue = false;
		for (int i = 0; i < lstPointsFinish.size(); i++) {
			isTrue = objPoint.equals(lstPointsFinish.get(i));
			if (isTrue) {
				return isTrue;
			}
		}

		return isTrue;
	}

	/**
	 * @return the lstPointsFinish
	 */
	public ArrayList<Point> getLstPointsFinish() {
		return lstPointsFinish;
	}

	/**
	 * @param lstPointsFinish the lstPointsFinish to set
	 */
	public void setLstPointsFinish(ArrayList<Point> lstPointsFinish) {
		this.lstPointsFinish = lstPointsFinish;
	}

	/**
	 *  set the list of colors for the user clothes
	 */
	protected void setColors() {
		for (int i = 1; i <= 12; i++) {
			colors.add(i);
		}
	}// end methode

	/**
	 * get one color from the list
	 * it is automatically eliminated from the list
	 * @return 
	 */
	public int getOneColor() {
		// default color - black or white?
		int color = 0;
		synchronized (colors) {
			// Let's choose a colors among the possible ones
			if (colors != null && colors.size() > 0) {
				int intRandom = UtilitaireNombres.genererNbAleatoire(colors.size());
				color = colors.remove(intRandom); 


			} else {
				//objLogger.error(GestionnaireMessages.message("colors_liste_empty"));
			}

			return color;
		}
	}

	/**
	 *
	 * @param joueur
	 * @param doitGenererNoCommandeRetour
	 */
	public void entrerTable(JoueurHumain joueur, boolean doitGenererNoCommandeRetour) {
		// Emp�cher d'autres thread de toucher � la liste des joueurs de
		// cette table pendant l'ajout du nouveau joueur dans cette table

		// Ajouter ce nouveau joueur dans la liste des joueurs de cette table
		lstJoueurs.put(joueur.obtenirNom(), joueur);

		// Le joueur est maintenant entr� dans la table courante (il faut
		// cr�er un objet InformationPartie qui va pointer sur la table
		// courante)
		joueur.definirPartieCourante(new InformationPartieHumain(joueur, this));
		// 0 - because it's first time that we fill the QuestionsBox
		// after we'll cut the level of questions by this number		
		joueur.obtenirPartieCourante().remplirBoiteQuestions();
		//System.out.println("start table2: " + System.currentTimeMillis());
		// Si on doit g�n�rer le num�ro de commande de retour, alors
		// on le g�n�re, sinon on ne fait rien
		if (doitGenererNoCommandeRetour == true) {
			// G�n�rer un nouveau num�ro de commande qui sera
			// retourn� au client
			joueur.obtenirProtocoleJoueur().genererNumeroReponse();
		}

		// Pr�parer l'�v�nement de nouveau joueur dans la table.
		// Cette fonction va passer les joueurs et cr�er un
		// InformationDestination pour chacun et ajouter l'�v�nement
		// dans la file de gestion d'�v�nements
		preparerEvenementJoueurEntreTable(joueur);
	}

	/**
	 * Return the max number of tlayers that can be on
	 * the table. In general and course types of the game the player
	 * number is filled with the virtuals till this constant
	 * @return
	 */
	public int getMaxNbPlayers() {
		return this.MAX_NB_PLAYERS;
	}

	/**
	 * use one id from list of idPersos and create idPersonnage
	 * idPerso is removed from the list
	 * @param idDessin
	 * @return the idPersonnage
	 */
	public int getOneIdPersonnage(int idDessin) {
		synchronized (idPersos) {
			int idPersonnage = this.idPersos.poll();

			idPersonnage += 10000 + idDessin * 100;
			return idPersonnage;
		}
	}

	/**
	 * if player leave the table he return the idPerso
	 * that is get back to the list
	 * @param idPersonnage
	 */
	public void getBackOneIdPersonnage(int idPersonnage) {
		synchronized (idPersos) {
			this.idPersos.add((idPersonnage - 10000) % 100);
		}
	}

	/**
	 *  the idPersos to set
	 */
	public void setIdPersos() {
		for (int i = 0; i < 12; i++) {
			this.idPersos.add(i);
		}
	}


	public Regles getRegles() {
		return objRegles;
	}


	public GameType getGameType() {
		return gameType;
	}

	public GenerateurPartie getGameFactory() {
		return gameFactory;
	}

	/**
	 * @return the objGestionnaireBD
	 */
	public GestionnaireBDControleur getObjGestionnaireBD() {
		return objGestionnaireBD;
	}

	public GestionnaireEvenements getObjGestionnaireEvenements() {
		return objGestionnaireEvenements;
	}

	public ControleurJeu getObjControleurJeu() {
		return objControleurJeu;
	}

	public GestionnaireTemps obtenirGestionnaireTemps()
	{
		return objGestionnaireTemps;
	}

	/**
	 * Sent event to players exept players that is motive for the event
	 * @param evenement
	 * @param eventHero
	 */
	protected void broadcastEvent(Evenement event, Joueur eventHero)
	{

		// Passer tous les joueurs de la table et leur envoyer un �v�nement
		for (JoueurHumain objJoueur: lstJoueurs.values()) {
			// Si le nom d'utilisateur du joueur courant n'est pas celui
			// qui vient de d�marrer la partie, alors on peut envoyer un
			// �v�nement � cet utilisateur
			if (!objJoueur.obtenirNom().equals(eventHero.obtenirNom())) {
				// Obtenir un num�ro de commande pour le joueur courant, cr�er
				// un InformationDestination et l'ajouter � l'�v�nement
				event.ajouterInformationDestination( new InformationDestination(
						objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
						objJoueur.obtenirProtocoleJoueur()));
			}
		}

		// Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
		objGestionnaireEvenements.ajouterEvenement(event);
	}

	/**
	 * Send an event to all players in the room liste
	 * @param event
	 */
	protected void broadcastEvent(Evenement event)
	{
		// Passer tous les joueurs de la table et leur envoyer un �v�nement
		for (JoueurHumain objJoueur: lstJoueurs.values()) {
			// Obtenir un num�ro de commande pour le joueur courant, cr�er
			// un InformationDestination et l'ajouter � l'�v�nement
			event.ajouterInformationDestination( new InformationDestination(
					objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
					objJoueur.obtenirProtocoleJoueur()));
		}


		// Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
		objGestionnaireEvenements.ajouterEvenement(event);
	}

	public int verifyFinishAndSetBonus(Point point)
	{
		return 0;		
	}

	public void verifyStopCondition()
	{
		// Do nothing in mathEnJeu type
	}

}// end class

