package ServeurJeu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import Enumerations.RetourFonctions.ResultatAuthentification;
import ServeurJeu.BD.DBConnectionsPoolManager;
import ServeurJeu.BD.GestionnaireBDControleur;
import ServeurJeu.Communications.GestionnaireCommunication;
import ServeurJeu.Communications.ProtocoleJoueur;
import ServeurJeu.ComposantesJeu.Salle;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.Evenements.EvenementJoueurDeconnecte;
import ServeurJeu.Evenements.EvenementJoueurConnecte;
import ServeurJeu.Evenements.EvenementNouvelleSalle;
import ServeurJeu.Evenements.GestionnaireEvenements;
import ServeurJeu.Evenements.InformationDestination;
import ServeurJeu.Evenements.StopServerEvent;
import ServeurJeu.Monitoring.TacheLogMoniteur;
import ServeurJeu.Temps.GestionnaireTemps;
import ServeurJeu.Temps.TacheSynchroniser;
import ServeurJeu.Configuration.GestionnaireConfiguration;
import ServeurJeu.Configuration.GestionnaireMessages;
import ServeurJeu.BD.GestionnaireBDUpdates;

/**
 * Note importante concernant le traitement des commandes par le 
 * ProtocoleJoueur : Deux fonctions d'un m�me protocole ne peuvent pas �tre
 * trait�es en m�me temps car si le ProtocoleJoueur est en train d'en traiter
 * une, alors il n'est plus � l'�coute pour en recevoir une autre. Pour en
 * traiter une autre, il doit attendre que le traitement de la premi�re soit
 * termin� et qu'elle retourne une valeur au client. Un autre protocole ne peut
 * pas TODO (pour l'instant) ex�cuter une fonction d'un autre protocole, la
 * seule chose qui peut se produire est qu'un protocole envoit des �v�nements
 * � d'autres joueurs par leur ProtocoleJoueur, mais aucune fonction n'est
 * ex�cut�e. TODO Il faut peut-�tre v�rifier les conditions pour envoyer
 * l'�v�nement � un joueur, car elles pourraient acc�der � des donn�es
 * importantes du joueur ou du protocole du joueur. M�me si le
 * VerificateurConnexions tente d'arr�ter un protocole qui est en train de
 * traiter une commande, c'est le socket du protocole qui est ferm�, et la
 * d�connexion du joueur va s'effectuer si on veut lire ou �crire sur le
 * socket. Cela veut donc dire qu'on n'a pas � valider que la m�me fonction
 * puisse �tre appel�e pour le m�me protocole et joueur.
 *  
 * @author Jean-Fran�ois Brind'Amour
 * @author Lilian Oloieri
 * 
 */
public class ControleurJeu {
	// Cette modeDebug est vraie, toute reponse des joueurs sera bonne, et
	// on affichera dans la console des informations sur les communications
	public static boolean modeDebug;
	private static Logger objLogger = Logger.getLogger(ControleurJeu.class);
	// Cet objet permet de g�rer toutes les interactions avec la base de donn�es
	private GestionnaireBDControleur objGestionnaireBD;
	// Cet objet permet de g�rer toutes les communications entre le serveur et
	// les clients (les joueurs)
	private GestionnaireCommunication objGestionnaireCommunication;
	// Cet objet permet de g�rer tous les �v�nements devant �tre envoy�s du
	// serveur aux clients (l'�v�nement ping n'est pas g�r� par ce gestionnaire)
	private GestionnaireEvenements objGestionnaireEvenements;
	private TacheSynchroniser objTacheSynchroniser;
	private GestionnaireTemps objGestionnaireTemps;
	// Cet objet est une liste des joueurs qui sont connect�s au serveur de jeu
	// (cela inclus les joueurs dans les salles ainsi que les joueurs jouant
	// pr�sentement dans des tables de jeu)
	private final HashMap<String, JoueurHumain> lstJoueursConnectes;
	// D�claration d'une variable pour contenir une liste des joueurs
	// qui ont �t�s d�connect�s et qui �taient en train de joueur une partie
	private final HashMap<String, JoueurHumain> lstJoueursDeconnectes;
	// Cet objet est une liste des salles cr��es qui se trouvent dans le serveur
	// de jeu. Chaque �l�ment de cette liste a comme cl� le id de la salle
	private HashMap<Integer, Salle> lstSalles;
	// D�claration de l'objet Espion qui va inscrire des informations � proppos
	// du serveur en parall�le
	//private Espion objEspion;

	// D�claration d'une map qui permet d'obtenir une liste de tous les
	// keywords disponible: keyword_id --> [language_id --> name]
	private TreeMap<Integer, TreeMap<Integer, String>> keywordsMap;
	// D�claration d'une map qui permet d'obtenir une liste de tous les
	// langues disponible: language_id --> [translated_language_id --> name]
	private TreeMap<Integer, TreeMap<Integer, String>> languagesMap;
	// D�claration d'une map qui permet d'obtenir une liste de tous les
	// 'game_types' disponible: game_type_id --> [name]
	private TreeMap<Integer, String> gameTypesMap;
	// Boolean to indicate if server is on or off
	//private boolean isOn;

	// thread to look for new rooms to add or old rooms to delete
	private GestionnaireBDUpdates objSpyDB;

	private static final Random objRandom = new Random();    

	/**
	 * Constructeur de la classe ControleurJeu qui permet de cr�er le gestionnaire
	 * des communications, le gestionnaire d'�v�nements et le gestionnaire de bases
	 * de donn�es.
	 */
	public ControleurJeu() {
		super();

		modeDebug = GestionnaireConfiguration.obtenirInstance().obtenirValeurBooleenne("controleurjeu.debug");
		// Initialiser la classe statique GestionnaireMessages
		GestionnaireMessages.initialiser();
		objLogger.info(GestionnaireMessages.message("controleur_jeu.serveur_demarre"));

		// Cr�er une liste des joueurs
		lstJoueursConnectes = new HashMap<String, JoueurHumain>();

		// Cr�er une liste des joueurs d�connect�s
		lstJoueursDeconnectes = new HashMap<String, JoueurHumain>();      
	}


	/**
	 * To start the controler actions
	 */
	public void demarrer() {

		// Cr�er une liste des salles
		lstSalles = new HashMap<Integer, Salle>();

		// Cr�er un nouveau gestionnaire d'�v�nements
		objGestionnaireEvenements = new GestionnaireEvenements();

		// Cr�er un nouveau gestionnaire de base de donn�es MySQL
		objGestionnaireBD = new GestionnaireBDControleur(this);

		objGestionnaireTemps = new GestionnaireTemps();
		objTacheSynchroniser = new TacheSynchroniser();

		// Cr�er un nouveau gestionnaire de communication
		objGestionnaireCommunication = new GestionnaireCommunication(this, objGestionnaireEvenements);

		// fill the list of keywords
		keywordsMap = objGestionnaireBD.getKeywordsMap();
		languagesMap = objGestionnaireBD.getLanguagesMap();
		gameTypesMap = objGestionnaireBD.getGameTypesMap();

		// Fills the rooms from DB
		objGestionnaireBD.fillRoomsList();

		// update user accounts - put to all not connected
		objGestionnaireBD.updatePlayersAccounts();

		GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();

		int intStepSynchro = config.obtenirNombreEntier("controleurjeu.synchro.step");
		objGestionnaireTemps.ajouterTache(objTacheSynchroniser, intStepSynchro);

		// Cr�er un thread pour le GestionnaireEvenements
		objGestionnaireEvenements = new GestionnaireEvenements();
		Thread threadEvenements = new Thread(objGestionnaireEvenements, "GestEvenem - Controleur");

		// D�marrer le thread du gestionnaire d'�v�nements
		threadEvenements.start();

		/***********************
        // D�marrer l'espion qui �crit dans un fichier p�riodiquement les
        // informations du serveur

        String fichier = config.obtenirString( "controleurjeu.info.fichier-sortie" );
        int delai = config.obtenirNombreEntier( "controleurjeu.info.delai" );
        objEspion = new Espion(this, fichier, delai, ClassesUtilitaires.Espion.MODE_FICHIER_TEXTE);

        // D�marrer la thread de l'espion
        Thread threadEspion = new Thread(objEspion);
        threadEspion.start();
		 *********************************/
		//Start spyDb to update periodically the rooms list
		// Add new rooms or out the olds
		int intSpyStep = config.obtenirNombreEntier("controleurjeu.monitoring.spyDB");
		objSpyDB = new GestionnaireBDUpdates(this, intSpyStep);
		//Start spy thread's
		Thread threadSpy = new Thread(objSpyDB, "SpyRooms");
		threadSpy.start();

		//Demarrer une tache de monitoring
		TacheLogMoniteur objTacheLogMoniteur = new TacheLogMoniteur();
		int intStepMonitor = config.obtenirNombreEntier("controleurjeu.monitoring.step");
		objGestionnaireTemps.ajouterTache(objTacheLogMoniteur, intStepMonitor);

		//D�marrer l'�coute des connexions clientes
		//Cette methode est la loop de l'application
		//Au retour, l'application se termine
		objGestionnaireCommunication.ecouterConnexions();


	}

	/**
	 * Prepare the players about future server stop or reset
	 */
	public void stopItLater(){
		this.sendEventServerWillBeReset();
	}

	private void sendEventServerWillBeReset() {
		GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();
		int nbSeconds = config.obtenirNombreEntier("controleurjeu.stopTimer");
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un nouveau joueur s'est connect�
		StopServerEvent stopServer = new StopServerEvent(nbSeconds);
		// Passer tous les joueurs connect�s et leur envoyer un �v�nement
		for (JoueurHumain objJoueur : lstJoueursConnectes.values()) {
			stopServer.ajouterInformationDestination(
					new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
							objJoueur.obtenirProtocoleJoueur()));
		}

		// Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
		objGestionnaireEvenements.ajouterEvenement(stopServer);
	}

	/**
	 * Stop the controler
	 */
	public void arreter() {

		for(JoueurHumain objJoueur: this.lstJoueursConnectes.values())
		{
			// Si le joueur courant est dans une salle, alors on doit le retirer de
			// cette salle (pas besoin de faire la synchronisation sur la salle
			// courante du joueur car elle ne peut �tre modifi�e par aucun autre
			// thread que celui courant)
			if (objJoueur.obtenirSalleCourante() != null) {
				// Le joueur courant quitte la salle dans laquelle il se trouve
				objJoueur.obtenirProtocoleJoueur().setBolStopThread(true);
				objJoueur.obtenirSalleCourante().quitterSalle(objJoueur, false, true);
			}

			objJoueur.obtenirProtocoleJoueur().definirJoueur(null);

		}


		for(Salle room: this.lstSalles.values())
		{
			room.destroyRoom();
		}

		this.lstSalles.clear();

		this.objSpyDB.arreterGestionnaireBD();
		objGestionnaireCommunication.arreter();
		objGestionnaireCommunication = null;
		//objGestionnaireTemps.stopIt();
		try{
			objGestionnaireTemps.enleverTache(objTacheSynchroniser);
		}catch (IllegalStateException ex){
			objLogger.error(" Synchroniser cancel ganerated timer end off... ", ex);	
		}
		objTacheSynchroniser = null;
		objGestionnaireTemps = null;
		objGestionnaireEvenements.arreterGestionnaireEvenements();
		objGestionnaireEvenements = null;

		this.objGestionnaireBD.arreterGestionnaireBD();

		// to close all connections to the DB
		// if connection is not closed implicitly ... is never closed
		// DB connection memory leak
		DBConnectionsPoolManager pool = DBConnectionsPoolManager.getInstance();
		pool.release();

	}

	/**
	 * Cette fonction permet de d�terminer si le joueur dont le nom d'utilisateur
	 * est pass� en param�tre est d�j� connect� au serveur de jeu ou non.
	 *
	 * @param nomUtilisateur Le nom d'utilisateur du joueur
	 * @return false Le joueur n'est pas connect� au serveur de jeu
	 * 	       true  Le joueur est d�j� connect� au serveur de jeu
	 * @synchronism Cette fonction est synchronis�e sur la liste des
	 *              joueurs connect�s.
	 */
	public boolean joueurEstConnecte(String nomUtilisateur) {
		// Synchroniser l'acc�s � la liste des joueurs connect�s
		synchronized (lstJoueursConnectes) {
			// Retourner si le joueur est d�j� connect� au serveur de jeu ou non
			for (String nom : lstJoueursConnectes.keySet())
			{
				if (nom.equalsIgnoreCase(nomUtilisateur))
					return true;
			}
			return false;
		}
	}

	/**
	 * Cette fonction permet de valider que les informations du joueur pass�es
	 * en param�tres sont correctes (elles existent et concordent). On suppose
	 * que le joueur n'est pas connect� au serveur de jeu.
	 *
	 * @param protocole Le protocole du joueur
	 * @param nomUtilisateur Le nom d'utilisateur du joueur (la capitalisation n'est pas importante)
	 * @param motDePasse Le mot de passe du joueur
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 * @return JoueurNonConnu Le nom d'utilisateur du joueur n'est pas connu par le
	 *            serveur ou le mot de passe ne concorde pas au nom d'utilisateur donn�
	 *         JoueurDejaConnecte Le joueur a tent� de se connecter en m�me temps
	 * 		  � deux endroits diff�rents
	 *         Succes L'authentification a r�ussie
	 * @synchronism Cette fonction est synchronis�e par rapport � la liste des
	 *              joueurs connect�s car on fait un synchronized sur elle,
	 *              elle est synchronis� par rapport au joueur du protocole car
	 *              les seules fonctions qui acc�dent au protocole sont le
	 *              VerificateurConnexions (fait juste un acc�s au protocole et
	 *              non un acc�s au joueur du protocole donc c'est correct), le
	 *              protocole lui-m�me (le protocole ne traite qu'une commande
	 *              � la fois, donc on se fou que lui utilise son joueur) et la
	 *              fonction deconnecterJoueur (elle ne peut pas �tre ex�cut�e
	 *              en m�me temps que l'authentification car le protocole ne
	 *              traite qu'une commande � la fois, m�me si la demande vient
	 *              du VerificateurConnexions).
	 */
	public ResultatAuthentification authentifierJoueur(ProtocoleJoueur protocole, String nomUtilisateur,
			String motDePasse, boolean doitGenererNoCommandeRetour) {

		//On vérifie tout d'abord si le joueur est déjà connecté, si c'est le
		//cas, le joueur doit nécessairement exister et la valeur de retour doit
		//être JoueurDejaConnecte (la méthode joueurEstConnecte obtient un lock sur
		//la liste des joueurs connectés, donc on a pas besoin de l'obtenir ici.
		if (joueurEstConnecte(nomUtilisateur))
		{    		

			return ResultatAuthentification.JoueurDejaConnecte;

		}
		// D�terminer si le joueur dont le nom d'utilisateur est pass� en
		// param�tres existe et mettre le r�sultat dans une variable bool�enne
		// la m�thode retourne le username de la BD pour que la capitalisation
		// soit correcte.
		String username = objGestionnaireBD.getUsername(nomUtilisateur, motDePasse);
		if (username == null)
			return ResultatAuthentification.JoueurNonConnu;

		// Si les informations de l'utilisateur sont correctes, alors le
		// joueur est maintenant connect� au serveur de jeu
		// Cr�er un nouveau joueur humain contenant les bonnes informations
		JoueurHumain objJoueurHumain = new JoueurHumain(protocole, username,
				protocole.obtenirAdresseIP(),
				protocole.obtenirPort());       

		// � ce moment, comme il se peut que le m�me joueur tente de se
		// connecter en m�me temps par 2 protocoles de joueur, alors si
		// �a arrive on va le v�rifier juste une fois qu'on a fait tous
		// les appels � la base de donn�es, il faut cependant s'assurer
		// que personne ne touche � la liste de joueurs pendant ce temps-l�.
		// C'est un cas qui ne devrait vraiment pas arriver souvent, car
		// normalement une erreur devrait �tre renvoy�e au client si
		// celui-ci essaie de se connecter � deux endroits en m�me temps.
		// Pour des raisons de performance, on fonctionne comme cela, car
		// chercher dans la base de donn�es peut �tre assez long
		synchronized (lstJoueursConnectes) {
			// Si le joueur est d�j� pr�sentement connect�, on ne peut
			// pas finaliser la connexion du joueur.  On doit re-vérifier
			// si le joueur est déjà connecté dans le cas peu probable
			// où le joueur aurait lancé une autre demande de connection
			// depuis l'autre vérification au début de la méthode.
			if (joueurEstConnecte(username)) {
				// On va retourner que le joueur est d�j� connect�
				return ResultatAuthentification.JoueurDejaConnecte;
			} 

			// D�finir la r�f�rence vers le joueur humain
			protocole.definirJoueur(objJoueurHumain);

			// Ajouter ce nouveau joueur dans la liste des joueurs connect�s
			// au serveur de jeu
			lstJoueursConnectes.put(username, objJoueurHumain);

			//adjust server info
			obtenirGestionnaireCommunication().miseAJourInfo();

			// Si on doit g�n�rer le num�ro de commande de retour, alors
			// on le g�n�re, sinon on ne fait rien (�a devrait toujours
			// �tre vrai, donc on le g�n�re tout le temps)
			if (doitGenererNoCommandeRetour == true) {
				// G�n�rer un nouveau num�ro de commande qui sera
				// retourn� au client
				protocole.genererNumeroReponse();
			}

			// Pr�parer l'�v�nement de nouveau joueur. Cette fonction
			// va passer les joueurs et cr�er un InformationDestination
			// pour chacun et ajouter l'�v�nement dans la file de gestion
			// d'�v�nements
			preparerEvenementJoueurConnecte(nomUtilisateur);
		}

		// Trouver les informations sur le joueur dans la BD et remplir le
		// reste des champs tels que les droits
		objGestionnaireBD.remplirInformationsJoueur(objJoueurHumain);

		return ResultatAuthentification.Succes;
	}

	/**
	 * Cette m�thode permet de d�connecter le joueur pass� en param�tres. Il
	 * faut enlever toute trace du joueur du serveur de jeu et en aviser les
	 * autres participants se trouvant au m�me endroit que le joueur d�connect�
	 * (� une table de jeu).
	 *
	 * @param joueur Le joueur humain ayant fait la demande de d�connexion
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 * @param ajouterJoueurDeconnecte Si on doit ajouter ce joueur � la liste
	 *        des joueurs d�connect�s.
	 * @synchronism � ce niveau-ci, il n'y a pas vraiment de restrictions sur
	 *              l'ordre d'arriv�e des �v�nements indiquant que le joueur
	 *              a quitt� la table ou la salle. De plus, aucune autre
	 *              fonction ne peut modifier le joueur, puisque deux
	 *              fonctions d'un m�me protocole ne peuvent pas �tre
	 *              ex�cut�es en m�me temps. Cependant, pour enlever un
	 *              joueur de la liste des joueurs connect�s, il faut
	 *              s'assurer que personne d'autre ne va toucher � la liste
	 *              des joueurs connect�s.
	 */
	public void deconnecterJoueur(JoueurHumain joueur, boolean doitGenererNoCommandeRetour, boolean ajouterJoueurDeconnecte) {

		// Si d�connection pendant une partie, ajouterJoueurDeconnecte = true
		// On va donc ajouter ce joueur � la liste des joueurs
		// d�connect�s pour cette table et pour le contr�leur du jeu
		if (ajouterJoueurDeconnecte == true && joueur != null &&
				joueur.obtenirPartieCourante() != null &&
				joueur.obtenirPartieCourante().obtenirTable() != null &&
				joueur.obtenirPartieCourante().obtenirTable().estCommencee() == true &&
				joueur.obtenirPartieCourante().obtenirTable().estArretee() == false) {

			//joueur.obtenirPartieCourante().writeInfo();
			// Ajouter ce joueur � la liste des joueurs d�connect�s pour cette
			// table

			joueur.obtenirPartieCourante().obtenirTable().ajouterJoueurDeconnecte(joueur);

			// Ajouter ce joueur � la liste des joueurs d�connect�s du serveur
			ajouterJoueurDeconnecte(joueur);
			objLogger.info("! Joueur deconnecter - mettre dans la liste deccon ");     
		}

		// Enlever le protocole du joueur courant de la liste des
		// protocoles de joueurs
		objGestionnaireCommunication.supprimerProtocoleJoueur(joueur.obtenirProtocoleJoueur());

		// Si le joueur courant est dans une salle, alors on doit le retirer de
		// cette salle (pas besoin de faire la synchronisation sur la salle
		// courante du joueur car elle ne peut �tre modifi�e par aucun autre
		// thread que celui courant)
		if (joueur.obtenirSalleCourante() != null) {
			// Le joueur courant quitte la salle dans laquelle il se trouve
			joueur.obtenirSalleCourante().quitterSalle(joueur, false, !ajouterJoueurDeconnecte);
		}

		// update in DB the connection reset to 0 table jos_comprofiler - cb_connected
		objGestionnaireBD.updatePlayerConnected(joueur.obtenirCleJoueur(), 0);

		// Emp�cher d'autres thread de venir utiliser la liste des joueurs
		// connect�s au serveur de jeu pendant qu'on d�connecte le joueur
		synchronized (lstJoueursConnectes) {
			
			objLogger.info(" Liste joueur connectes : " + lstJoueursConnectes.size());
			// Enlever le joueur de la liste des joueurs connect�s
			lstJoueursConnectes.remove(joueur.obtenirNom());
			// Enlever la r�f�rence du protocole du joueur vers son joueur humain
			// (cela va avoir pour effet que le protocole du joueur va penser que
			// le joueur n'est plus connect� au serveur de jeu)
			joueur.obtenirProtocoleJoueur().setBolStopThread(true);
			joueur.obtenirProtocoleJoueur().definirJoueur(null);

			//adjust server info
			obtenirGestionnaireCommunication().miseAJourInfo();

			// Si on doit g�n�rer le num�ro de commande de retour, alors
			// on le g�n�re, sinon on ne fait rien
			if (doitGenererNoCommandeRetour == true) {
				// G�n�rer un nouveau num�ro de commande qui sera
				// retourn� au client
				joueur.obtenirProtocoleJoueur().genererNumeroReponse();
			}

			// Aviser tous les joueurs connect�s au serveur de jeu qu'un joueur
			// s'est d�connect�
			preparerEvenementJoueurDeconnecte(joueur.obtenirNom());
			joueur.setObjProtocoleJoueur(null);
			
			objLogger.info(" Liste joueur connectes : " + lstJoueursConnectes.size());

		}
	}

	/**
	 * Cette fonction permet d'obtenir la liste des joueurs connect�s au serveur
	 * de jeu. La vraie liste est retourn�e.
	 *
	 * @return HashMap : La liste des joueurs connect�s au serveur de jeu
	 *                   (c'est la r�f�rence vers la liste du ControleurJeu, il
	 *                   faut donc traiter le cas du multithreading)
	 * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle doit
	 * 				l'�tre par l'appelant de cette fonction tout d�pendant
	 * 				du traitement qu'elle doit faire
	 */
	public HashMap<String, JoueurHumain> obtenirListeJoueurs() {
		synchronized(lstJoueursConnectes){
			return lstJoueursConnectes;
		}
	}

	/**
	 * Cette fonction permet d'obtenir la liste des salles du serveur de jeu.
	 * La vraie liste est retourn�e.
	 *
	 * @return TreeMap : La liste des salles du serveur de jeu (c'est la
	 * 				     r�f�rence vers la liste du ControleurJeu, il faut donc
	 *                   traiter le cas du multithreading)
	 * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle doit
	 * 				l'�tre par l'appelant de cette fonction tout d�pendant
	 * 				du traitement qu'elle doit faire
	 */

	public HashMap<Integer, Salle> obtenirListeSalles() {
		return lstSalles;
	}

	/**
	 * Cette fonction permet d'obtenir la liste des salles du serveur de jeu
	 * qui supporte la langue spécifiée.
	 *
	 * @param language une String de 2 caractères qui indique la langue pour
	 *                 laquelle on fait la recherche. 'fr'=français, 'en'=english
	 * @return TreeMap La liste des salles supportant la langue spécifiée

	 */
	public HashMap<Integer, Salle> obtenirListeSalles(String language) {
		HashMap<Integer, Salle> sallesSupportees = new HashMap<Integer, Salle>();
		synchronized (lstSalles) {
			// On cr�e une liste de salles vide, et on parcourt toutes les salles connues
			for (Salle salle : lstSalles.values())
				if (language.equals("") || salle.getName(language) != null)
					sallesSupportees.put(salle.getRoomId(), salle);
		}
		return sallesSupportees;
	}

	/**
	 * Cette fonction permet d'obtenir la liste des salles d'un certain type 
	 * du serveur de jeu qui supporte la langue spécifiée.
	 *
	 * @param language une String de 2 caractères qui indique la langue pour
	 *                 laquelle on fait la recherche. 'fr'=français, 'en'=english
	 * @param roomsType type demand�e 
	 * @return TreeMap La liste des salles supportant la langue spécifiée

	 */
	public HashMap<Integer, Salle> obtenirListeSalles(String langue, String roomsType) {
		HashMap<Integer, Salle> sallesSupportees = new HashMap<Integer, Salle>();
		synchronized (lstSalles) {
			// On cr�e une liste de salles vide, et on parcourt toutes les salles connues
			for (Salle salle : lstSalles.values())
				if ((langue.equals("") || salle.getName(langue) != null) &&
						salle.getRoomType().equals(roomsType))
					sallesSupportees.put(salle.getRoomId(), salle);
		}
		return sallesSupportees;
	} /// end method

	public HashMap<Integer, Salle> obtenirListeSallesCreateur(String nomUtilisateur) {
		HashMap<Integer, Salle> sallesUtilisateur = new HashMap<Integer, Salle>();
		synchronized (lstSalles) {
			// On cr�e une liste de salles vide, et on parcourt toutes les salles connues
			for (Salle salle : lstSalles.values())
				if (salle.getCreatorUsername().equals(nomUtilisateur))
					sallesUtilisateur.put(salle.getRoomId(), salle);
		}
		return sallesUtilisateur;
	}



	/**
	 * Cette fonction permet de d�terminer si la salle dont le id est pass�
	 * en param�tres existe d�j� ou non.
	 *
	 * @param idRoom Le room_id de la salle
	 * @return false La salle n'existe pas
	 *         true  La salle existe d�j�
	 */
	public boolean salleExiste(int idRoom) {
		// Retourner si la salle existe d�j� ou non
		synchronized (lstSalles) {
			return lstSalles.containsKey(idRoom);
		}
	}

	/**
	 * Get room's name by his Id and in requested language
	 * @param idRoom
	 * @param lang
	 * @return
	 */
	public String getRoomName(int idRoom, String lang) {
		// Retourner si la salle existe d�j� ou non
		synchronized (lstSalles) {
			if (lstSalles.containsKey(idRoom)) {
				return lstSalles.get(idRoom).getRoomName(lang);
			} else {
				return "Not exist";
			}
		}
	}

	/**
	 * Cette m�thode permet d'ajouter une nouvelle salle dans la liste des
	 * salles du contr�leur de jeu.
	 *
	 * @param nouvelleSalle La nouvelle salle � ajouter dans la liste
	 * @synchronism add synchronism because need to add rooms dinamicaly during
	 *              the time of life of controler
	 */
	public void ajouterNouvelleSalle(Salle nouvelleSalle) {
		// Ajouter la nouvelle salle dans la liste des salles du
		// contr�leur de jeu
		synchronized (lstSalles) {
			lstSalles.put(nouvelleSalle.getRoomId(), nouvelleSalle);
			//System.out.println(nouvelleSalle.getRoomId() + " NEW " + nouvelleSalle.toString());
		}
	}

	/**
	 * This methode is used to close the room for future games
	 * @param roomId the room_id for the room to close.
	 * TODO needs to handle the case where players are in the room
	 */
	public void closeRoom(int roomId) {
		synchronized (lstSalles) {
			lstSalles.remove(roomId);
		}
		//adjust server info
		obtenirGestionnaireCommunication().miseAJourInfo();
	}// end method

	/**
	 * This methode is used to remove the rooms from the list of Controleur if
	 * the date of expiration is arrived. In the same time on return for SpyRooms
	 * the list of ID of rooms rested in the list
	 * @return
	 */
	public ArrayList<Integer> removeOldRooms() {
		ArrayList<Integer> rooms = new ArrayList<Integer>();
		synchronized (lstSalles) {

			for(Salle salle : lstSalles.values())
			{
				//System.out.println(" salle - EndDate: "  + salle.getEndDate());
				if (salle.getEndDate() != null && salle.getEndDate().before(new Date(System.currentTimeMillis())))
					lstSalles.remove(salle);
				else
					rooms.add(salle.getRoomId());
			}
		}

		return rooms;
	}

	/**
	 * Cette fonction permet de valider que le mot de passe pour entrer dans la
	 * salle est correct. On suppose suppose que le joueur n'est pas dans aucune
	 * salle. Cette fonction va avoir pour effet de connecter le joueur dans la
	 * salle dont le nom est pass� en param�tres.
	 *
	 * @param joueur Le joueur demandant d'entrer dans la salle
	 * @param idRoom Le room_id de la salle dans laquelle entrer
	 * @param motDePasse Le mot de passe pour entrer dans la salle
	 * @param doitGenererNoCommandeRetour Permet de savoir si on doit g�n�rer un
	 *        num�ro de commande pour le retour de l'appel de fonction
	 * @return false Le mot de passe pour entrer dans la salle n'est pas bon
	 *         true  Le joueur a r�ussi � entrer dans la salle
	 */
	public boolean entrerSalle(JoueurHumain joueur, int idRoom,
			String motDePasse, boolean doitGenererNoCommandeRetour) {
		synchronized (lstSalles) {
			// On retourne le r�sultat de l'entr�e du joueur dans la salle
			return lstSalles.get(idRoom).entrerSalle(joueur, motDePasse, doitGenererNoCommandeRetour);
		}
	}

	/**
	 * Cette m�thode permet de pr�parer l'�v�nement de l'arriv�e d'un nouveau
	 * joueur. Cette m�thode va passer tous les joueurs connect�s et pour ceux
	 * devant �tre avertis (tous sauf le joueur courant pass� en param�tre),
	 * on va obtenir un num�ro de commande, on va cr�er un
	 * InformationDestination et on va ajouter l'�v�nement dans la file
	 * d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel de cette
	 * fonction, la liste des joueurs connect�s est synchronis�e.
	 *
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
	 * 								  vient de se connecter au serveur de jeu
	 * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				par l'appelant (authentifierJoueur).
	 */
	private void preparerEvenementJoueurConnecte(String nomUtilisateur) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un nouveau joueur s'est connect�
		//EvenementJoueurConnecte joueurConnecte = new EvenementJoueurConnecte(nomUtilisateur);
		EvenementJoueurConnecte joueurConnecte = new EvenementJoueurConnecte(nomUtilisateur);

		// Passer tous les joueurs connect�s et leur envoyer un �v�nement
		for (JoueurHumain objJoueur : lstJoueursConnectes.values()) {
			// Si le nom d'utilisateur du joueur courant n'est pas celui
			// qui vient de se connecter au serveur de jeu, alors on peut
			// envoyer un �v�nement � cet utilisateur
			if (!objJoueur.obtenirNom().equalsIgnoreCase(nomUtilisateur)) {
				// Obtenir un num�ro de commande pour le joueur courant, cr�er
				// un InformationDestination et l'ajouter � l'�v�nement
				joueurConnecte.ajouterInformationDestination(new InformationDestination(
						objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
						objJoueur.obtenirProtocoleJoueur()));
			}
		}


		// Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
		objGestionnaireEvenements.ajouterEvenement(joueurConnecte);
	}

	/**
	 * Cette m�thode permet de pr�parer l'�v�nement de la cr�ation d'une
	 * nouvelle salle dans le serveur apres l'ajout d'elle dans BD.
	 * Cette m�thode va passer tous les joueurs connect�s et pour ceux devant �tre avertis
	 * (tous sauf le joueur courant pass� en param�tre), on va obtenir un num�ro
	 * de commande, on va cr�er un InformationDestination et on va ajouter
	 * l'�v�nement dans la file d'�v�nements du gestionnaire d'�v�nements.
	 * Lors de l'appel de cette fonction, la liste des joueurs est
	 * synchronis�e.
	 *
	 * @param nouvelleSalle la salle pour laquelle on pr�pare l'�v�nement.
	 * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				par l'appelant ().
	 */
	public void preparerEvenementNouvelleSalle(Salle nouvelleSalle)
	{
		String createurSalle = nouvelleSalle.getCreatorUsername();

		String g_types = "";
		for (Integer gameTypeId: nouvelleSalle.getGameTypeIds()) {
			g_types += gameTypeId + ",";
		}
		if (g_types.endsWith(",")) {
			g_types = g_types.substring(0, g_types.length() - 1);
		}
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'une table a �t� cr��e
		EvenementNouvelleSalle evNouvelleSalle = new EvenementNouvelleSalle(
				nouvelleSalle.getRoomName(""),
				nouvelleSalle.protegeeParMotDePasse(),
				createurSalle,
				nouvelleSalle.getRoomDescription(""),
				nouvelleSalle.getMasterTime(),
				nouvelleSalle.getRoomId(),
				nouvelleSalle.getRoomType(),
				g_types
		);

		for (JoueurHumain objJoueur : lstJoueursConnectes.values())
		{
			//On n'envoi pas d'�v�nement au cr�ateur de la salle
			if (!objJoueur.obtenirNom().equalsIgnoreCase(createurSalle))                   
				// Obtenir un num�ro de commande pour le joueur courant, cr�er
				// un InformationDestination et l'ajouter � l'�v�nement
				evNouvelleSalle.ajouterInformationDestination(
						new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
								objJoueur.obtenirProtocoleJoueur())
				);
		}
		// Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
		objGestionnaireEvenements.ajouterEvenement(evNouvelleSalle);
	}

	/**
	 * Cette m�thode permet de pr�parer l'�v�nement de la d�connexion d'un
	 * joueur. Cette m�thode va passer tous les joueurs connect�s et pour ceux
	 * devant �tre avertis (tous sauf le joueur courant pass� en param�tre),
	 * on va obtenir un num�ro de commande, on va cr�er un
	 * InformationDestination et on va ajouter l'�v�nement dans la file
	 * d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel de cette
	 * fonction, la liste des joueurs connect�s est synchronis�e.
	 *
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
	 * 								  vient de se d�connecter du serveur de jeu
	 * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle l'est
	 * 				par l'appelant (deconnecterJoueur).
	 */
	private void preparerEvenementJoueurDeconnecte(String nomUtilisateur) {
		// Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement
		// aux joueurs qu'un joueur s'est d�connect�
		EvenementJoueurDeconnecte joueurDeconnecte = new EvenementJoueurDeconnecte(nomUtilisateur);


		// Passer tous les joueurs connect�s et leur envoyer un �v�nement
		for (JoueurHumain objJoueur : lstJoueursConnectes.values()) {
			// Si le nom d'utilisateur du joueur courant n'est pas celui
			// qui vient de se d�connecter du serveur de jeu, alors on peut
			// envoyer un �v�nement � cet utilisateur
			if (!objJoueur.obtenirNom().equalsIgnoreCase(nomUtilisateur)) {
				// Obtenir un num�ro de commande pour le joueur courant, cr�er
				// un InformationDestination et l'ajouter � l'�v�nement
				joueurDeconnecte.ajouterInformationDestination(new InformationDestination(
						objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
						objJoueur.obtenirProtocoleJoueur()));
			}
		}

		// Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
		objGestionnaireEvenements.ajouterEvenement(joueurDeconnecte);
	}

	public GestionnaireCommunication obtenirGestionnaireCommunication() {
		return objGestionnaireCommunication;
	}

	public GestionnaireEvenements obtenirGestionnaireEvenements() {
		return objGestionnaireEvenements;
	}

	public GestionnaireTemps obtenirGestionnaireTemps() {
		return objGestionnaireTemps;
	}

	public TacheSynchroniser obtenirTacheSynchroniser() {
		return objTacheSynchroniser;
	}

	public GestionnaireBDControleur obtenirGestionnaireBD() {
		return objGestionnaireBD;
	}

	/*
	 * Cette fonction ajouter un joueur � la liste des joueurs d�connect�s. Si le
	 * joueur tente de se reconnecter, il sera possible qu'il reprenne la partie
	 */
	public void ajouterJoueurDeconnecte(JoueurHumain joueurHumain) {
		synchronized (lstJoueursDeconnectes) {
			lstJoueursDeconnectes.put(joueurHumain.obtenirNom(), joueurHumain);
		}
	}

	/*
	 * Cette fonction va nous permettre de savoir si ce joueur a �t�
	 * d�connect� pendant une partie.
	 */
	public boolean estJoueurDeconnecte(String nomUtilisateur) {
		synchronized (lstJoueursDeconnectes) {
			return lstJoueursDeconnectes.containsKey(nomUtilisateur);
		}

	}

	/*
	 * Cette fonction retourne une r�f�rence vers un objet JoueurHumain
	 * d'un joueur d�connect�. Cet objet contient toutes les informations
	 * � propos de la partie qui �tait en cours
	 */
	public JoueurHumain obtenirJoueurHumainJoueurDeconnecte(String nomUtilisateur) {
		synchronized (lstJoueursDeconnectes) {
			return lstJoueursDeconnectes.get(nomUtilisateur);
		}
	}

	/*
	 * Cette fonction permet d'enlever un joueur d�connect� de la liste
	 * des joueurs d�connect�s, soit parce qu'il vient de se reconnecter,
	 * ou car la partie qu'il avait commenc�e et qui �tait en suspend est termin�e
	 */
	public void enleverJoueurDeconnecte(String nomUtilisateur) {
		synchronized (lstJoueursDeconnectes) {
			lstJoueursDeconnectes.remove(nomUtilisateur);
		}
	}

	public void clearDeconnectedPlayersList() {
		synchronized (lstJoueursDeconnectes) {
			lstJoueursDeconnectes.clear();
		}
	}

	public HashMap<String, JoueurHumain> obtenirListeJoueursDeconnectes() {
		synchronized (lstJoueursDeconnectes) {
			return lstJoueursDeconnectes;
		}
	}

	/**
	 * Find the language_id corresponding to the 2-letter language string.
	 * @param language the 2-letter language to look for ("fr","en")
	 * @return the language id corresponding to the specified language short name.
	 *         If not found, we return the id of "en", if this is still not found
	 *         the first existing language_id is returned.
	 */
	public int getLanguageId(String language) {
		Integer defaultLanguageId = null;
		for (Map.Entry<Integer,String> shortLanguageEntry : languagesMap.get(0).entrySet()) {
			if (language.equalsIgnoreCase(shortLanguageEntry.getValue()))
				return shortLanguageEntry.getKey();
			if (defaultLanguageId == null || shortLanguageEntry.getValue().equals("en"))
				defaultLanguageId = shortLanguageEntry.getKey();
		}
		return defaultLanguageId;
	}

	public String getLanguageShortName(int language_id) {
		String shortName = languagesMap.get(0).get(language_id);
		if (shortName != null)
			return shortName;
		return "en";
	}

	/**
	 * @param language
	 * @return the keywords
	 */
	public TreeMap<Integer, String> getKeywordsMap(String language) {
		return keywordsMap.get(getLanguageId(language));
	}

	public TreeMap<Integer, String> getLanguagesMap(String language) {
		return languagesMap.get(getLanguageId(language));
	}

	public TreeMap<Integer, String> getGameTypesMap() {
		return gameTypesMap;
	}

	public synchronized void setNewTimer() {
		objGestionnaireTemps = new GestionnaireTemps();
	}

	public static int genererNbAleatoire(int max)
	{
		//Random objRandom = new Random();
		return objRandom.nextInt(max);
	}


	public int getActiveTablesNumber() {
		int nb = 0;

		for(Salle room : lstSalles.values())
			nb += room.getActiveTablesNUmber();
		return nb;
	}


	public void removeOldRooms(ArrayList<Integer> rooms) {

		synchronized (lstSalles) {

			for(int salle : rooms)
			{
				lstSalles.remove(salle);                
			}
		}

	}


	public void updateRooms(ArrayList<Integer> rooms) {
		ArrayList<Integer> old = new ArrayList<Integer>();
		for(int id : rooms)
		{
			if(!salleExiste(id))
				old.add(id);
		}

		rooms.removeAll(old);		
		objGestionnaireBD.updateRooms(rooms);

	}


	public Salle getRoomById(int roomId) {
		return lstSalles.get(roomId);
	}


	public void detectErasedRooms() {
		synchronized(lstSalles){
			ArrayList<Integer> allBD = objGestionnaireBD.fillsRooms();
			ArrayList<Integer> toErase = new ArrayList<Integer>(); 
			Set<Integer> allControler = lstSalles.keySet();
			for(Integer salle:allControler){
				if(!allBD.contains(salle))
					toErase.add(salle);
			}
			for(Integer salle:toErase){
				lstSalles.remove(salle);
			}
		}
	}

}// end class

