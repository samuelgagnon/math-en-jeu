package ServeurJeu.ComposantesJeu.Tables;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import Enumerations.GameType;
import Enumerations.RetourFonctions.ResultatDemarrerPartie;
import ServeurJeu.ComposantesJeu.Salle;
import ServeurJeu.ComposantesJeu.GenerateurPartie.GenerateurPartieTournament;
import ServeurJeu.ComposantesJeu.Joueurs.InformationPartieHumain;
import ServeurJeu.ComposantesJeu.Joueurs.Joueur;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.StatisticsPlayer;
import ServeurJeu.Evenements.Evenement;
import ServeurJeu.Evenements.InformationDestination;
import ServeurJeu.Temps.Minuterie;

/**
 * 
 *
 */
public class TableTournament extends Table {

	protected ConcurrentHashMap<String, JoueurHumain> lstDiffusion;
	public TableTournament(Salle salleParente, int noTable,
			JoueurHumain joueur, int tempsPartie, String name, int intNbLines,
			int intNbColumns, GameType gamesType) {
		super(salleParente, noTable, joueur, tempsPartie, name, intNbLines,
				intNbColumns, gamesType);

		lstDiffusion = new ConcurrentHashMap<String, JoueurHumain>();

	}

	public void creation(int intNbLines, int intNbColumns) {
		objGestionnaireBD.chargerReglesTable(objRegles, gameType, objSalle.getRoomId());
		MAX_NB_PLAYERS = objRegles.getMaxNbPlayers();
		///System.out.println("We test Colors in the table  : " );

		this.setColors();
		this.setIdPersos();

		this.gameFactory = new GenerateurPartieTournament();

		gameFactory.setNbLines(intNbLines);
		gameFactory.setNbColumns(intNbColumns);

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
		if(!joueur.obtenirNom().equals(master.obtenirNom()))
		{
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
	}


	/**
	 *
	 * @param joueur
	 * @param doitGenererNoCommandeRetour
	 */
	public void entrerTable(JoueurHumain joueur, boolean doitGenererNoCommandeRetour) {

		lstDiffusion.put(joueur.obtenirNom(), joueur);

		// Le joueur est maintenant entr� dans la table courante (il faut
		// cr�er un objet InformationPartie qui va pointer sur la table
		// courante)
		joueur.definirPartieCourante(new InformationPartieHumain(joueur, this));
		colors.add(joueur.obtenirPartieCourante().resetColor()); 

		// Si on doit g�n�rer le num�ro de commande de retour, alors
		// on le g�n�re, sinon on ne fait rien
		if (doitGenererNoCommandeRetour == true) {
			// G�n�rer un nouveau num�ro de commande qui sera
			// retourn� au client
			joueur.obtenirProtocoleJoueur().genererNumeroReponse();
		}

		// Emp�cher d'autres thread de toucher � la liste des joueurs de
		// cette salle pendant qu'on parcourt tous les joueurs de la salle
		// pour leur envoyer un �v�nement
		synchronized (getObjSalle().obtenirListeJoueurs()) {
			// Pr�parer l'�v�nement de nouveau joueur dans la table.
			// Cette fonction va passer les joueurs et cr�er un
			// InformationDestination pour chacun et ajouter l'�v�nement
			// dans la file de gestion d'�v�nements
			//preparerEvenementJoueurEntreTable(joueur);
		}


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

		if(joueur.equals(master))
		{
			lstDiffusion.put(joueur.obtenirNom(), joueur);		
		}else
		{
			// to get back perso's clothes color 
			//returned to the list when get out from game
			//joueur.obtenirPartieCourante().setClothesColor(colors.getLast());

			// to not be without move cases, in case if the server is in waiting an answer
			joueur.obtenirPartieCourante().cancelPosedQuestion();

			// Emp�cher d'autres thread de toucher � la liste des tables de
			// cette salle pendant que le joueur entre dans cette table
			synchronized (getObjSalle().obtenirListeTables()) {			
				addPlayerInListe(joueur);
				preparerEvenementJoueurRejoindrePartie(joueur);
			}			
		}		

		// Si on doit g�n�rer le num�ro de commande de retour, alors
		// on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
		// faux)
		if (doitGenererNoCommandeRetour == true) {
			// G�n�rer un nouveau num�ro de commande qui sera
			// retourn� au client
			joueur.obtenirProtocoleJoueur().genererNumeroReponse();

		}

		synchronized (lstJoueursDeconnectes) {
			lstJoueursDeconnectes.remove(joueur);
		}    
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

		objtPositionsJoueurs = this.getGameFactory().genererPositionJoueurs(this, nbJoueur, lstPointsCaseLibre);
		// Cr�ation d'une nouvelle liste
		Joueur[] lstJoueursParticipants = new Joueur[nbJoueur];

		// Obtenir un it�rateur pour l'ensemble contenant les personnages
		Iterator<JoueurHumain> objIterateurListeJoueurs = lstJoueursEnAttente.values().iterator();

		int position = 0;

		// Passer toutes les positions des joueurs et les d�finir
		for (int i = 0; i < objtPositionsJoueurs.length; i++) {


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
			position++;
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
		objControleurJeu.obtenirGestionnaireTemps().ajouterTache(objMinuterie, tempsStep);

		// Obtenir la date � ce moment pr�cis
		objDateDebutPartie = new Date();

	}// end method

	public void arreterPartie(String joueurGagnant) {

		// bolEstArretee permet de savoir si cette fonction a d�j� �t� appel�e
		// de plus, bolEstArretee et bolEstCommencee permettent de conna�tre
		// l'�tat de la partie
		if (bolEstArretee == false) {
			objTacheSynchroniser.enleverObservateur(this);
			try{
				objControleurJeu.obtenirGestionnaireTemps().enleverTache(objMinuterie);
			}catch (IllegalStateException ex){
				objControleurJeu.setNewTimer();
				objLogger.error("Une erreur est survenue: objControleurJeu.setNewTimer() : ", ex );
			}

			// to discard Banana or Brainiac tasks
			//objGestionnaireTemps.stopIt();

			// S'il y a au moins un joueur qui a compl�t� la partie,
			// alors on ajoute les informations de cette partie dans la BD
			if (lstJoueurs.size() > 0) {
				// Sert � d�terminer le meilleur score pour cette partie
				int meilleurPointage = 0;

				TreeSet<StatisticsPlayer> ourResults = new TreeSet<StatisticsPlayer>();



				// Parcours des joueurs pour trouver le meilleur pointage
				int cleJoueurGagnant = 0; //0 veut dire un joueur virtuel gagne.
				for (JoueurHumain objJoueurHumain: lstJoueurs.values()) {
					InformationPartieHumain infoPartie = objJoueurHumain.obtenirPartieCourante();
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

				preparerEvenementPartieTerminee(ourResults, joueurGagnant);

				// Parcours des joueurs pour mise � jour de la BD et
				// pour ajouter les infos de la partie compl�t�e
				for (JoueurHumain joueur: lstJoueurs.values()) {
					joueur.obtenirPartieCourante().getObjGestionnaireBD().mettreAJourJoueur(intTempsTotal);
					// if the game was with the permission to use user's money from DB
					if (joueur.obtenirPartieCourante().obtenirTable().getRegles().isBolMoneyPermit()) {
						joueur.obtenirPartieCourante().getObjGestionnaireBD().setNewPlayersMoney();
					}
					boolean estGagnant = (joueur.obtenirCleJoueur() == cleJoueurGagnant);
					objGestionnaireBD.ajouterInfosJoueurPartieTerminee(clePartie, joueur, estGagnant);
					//if(joueur.getRole() > 1)
					//joueur.obtenirPartieCourante().writeInfo();

				}

			}

			// wipeout players from the table
			if (!lstJoueurs.isEmpty()) {

				lstJoueurs.clear();
				lstDiffusion.clear();
			}

			// Enlever les joueurs d�connect�s de cette table de la
			// liste des joueurs d�connect�s du serveur pour �viter
			// qu'ils ne se reconnectent et tentent de rejoindre une partie termin�e
			for (String name : lstJoueursDeconnectes.keySet()) {
				objControleurJeu.enleverJoueurDeconnecte(name);
			}

			// Enlever les joueurs d�connect�s de cette table
			lstJoueursDeconnectes.clear();
			lstJoueursDeconnectes = new ConcurrentHashMap<String, JoueurHumain>();
			
			// Arr�ter la partie
			bolEstArretee = true;
			//System.out.println("table - etape 1 " + lstJoueurs.size());
			// Si jamais les joueurs humains sont tous d�connect�s, alors
			// il faut d�truire la table ici
			if (lstJoueurs.isEmpty()) {
				// D�truire la table courante et envoyer les �v�nements
				// appropri�s
				//System.out.println("table - etape - is empty");
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

	protected void addPlayerInListe(JoueurHumain joueur)
	{
		// Ajouter ce nouveau joueur dans la liste des joueurs de cette table
		lstJoueurs.put(joueur.obtenirNom(), joueur);
		lstDiffusion.put(joueur.obtenirNom(), joueur);
	}

	protected void getOutPlayerFromListe(JoueurHumain player)
	{

		// Enlever le joueur de la liste des joueurs de cette table
		getBackOneIdPersonnage(player.obtenirPartieCourante().obtenirIdPersonnage());
		lstJoueurs.remove(player.obtenirNom());
		lstJoueursEnAttente.remove(player.obtenirNom());
		lstDiffusion.remove(player.obtenirNom());
		colors.add(player.obtenirPartieCourante().resetColor()); 

	}

	/**
	 * Sent event to players exept players that is motive for the event
	 * @param evenement
	 * @param eventHero
	 */
	protected void broadcastEvent(Evenement event, Joueur eventHero)
	{

		// Passer tous les joueurs de la table et leur envoyer un �v�nement
		for (JoueurHumain objJoueur: lstDiffusion.values()) {
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

		for (JoueurHumain objJoueur: lstDiffusion.values()) {
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
		if(checkPositionPointsFinish(point))
		{
			return obtenirTempsRestant();
		}
		return 0;		
	}

	public void verifyStopCondition()
	{
		// if all the humains is on the finish line we stop the game
		if (isAllTheHumainsOnTheFinish())
		{
			arreterPartie(""); 
		}		
	}
}
