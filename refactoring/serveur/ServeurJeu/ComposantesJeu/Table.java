package ServeurJeu.ComposantesJeu;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.Point;
import java.util.Date;

import ServeurJeu.BD.GestionnaireBD;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurVirtuel;
import ServeurJeu.ComposantesJeu.ReglesJeu.Regles;
import ServeurJeu.Evenements.EvenementJoueurDeplacePersonnage;
import ServeurJeu.Evenements.EvenementJoueurEntreTable;
import ServeurJeu.Evenements.EvenementJoueurQuitteTable;
import ServeurJeu.Evenements.EvenementJoueurDemarrePartie;
import ServeurJeu.Evenements.EvenementPartieDemarree;
import ServeurJeu.Evenements.EvenementMAJPointage;
import ServeurJeu.Evenements.EvenementMAJArgent;
import ServeurJeu.Evenements.GestionnaireEvenements;
import ServeurJeu.Evenements.InformationDestination;
import ClassesUtilitaires.GenerateurPartie;
import Enumerations.RetourFonctions.ResultatDemarrerPartie;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.Configuration.GestionnaireConfiguration;
import ServeurJeu.Temps.*;
import ServeurJeu.Evenements.EvenementSynchroniserTemps;
import ServeurJeu.Evenements.EvenementPartieTerminee;
import ServeurJeu.ControleurJeu;
import ServeurJeu.ComposantesJeu.Joueurs.ParametreIA;
import ClassesUtilitaires.IntObj;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.Evenements.EvenementDeplacementWinTheGame;
import ServeurJeu.Evenements.EvenementMessageChat;
import ServeurJeu.Evenements.EvenementUtiliserObjet;
import java.util.Random;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Table implements ObservateurSynchroniser, ObservateurMinuterie
{
  // D�claration d'une r�f�rence vers le gestionnaire d'�v�nements
  private GestionnaireEvenements objGestionnaireEvenements;
  
        // On d�clare la classe qui permettra les d�placements du WinTheGame
        private WinTheGame winTheGame;
        
  // D�claration d'une r�f�rence vers le contr�leur de jeu
  //private ControleurJeu objControleurJeu;
  
  // D�claration d'une r�f�rence vers le gestionnaire de bases de donn�es
  //private GestionnaireBD objGestionnaireBD;
  
  // D�claration d'une r�f�rence vers la salle parente dans laquelle se 
  // trouve cette table 
  private Salle objSalle;
  
  // Cette variable va contenir le num�ro de la table
  private int intNoTable;
  
  // D�claration d'une constante qui d�finit le nombre maximal de joueurs 
  // dans une table
  private int _MAX_NB_JOUEURS;
  
  private int intNbJoueurDemande; 
  
  // Cette variable va contenir le nom d'utilisateur du cr�ateur de cette table
  private String strNomUtilisateurCreateur;
  
  // D�claration d'une variable qui va garder le temps total d�fini pour 
  // cette table
  private int intTempsTotal;
  
  // D�claration d'une variable qui va garder le temps restant (au d�part 
  // il vaut la m�me chose que intTempsTotal)
  //private int intTempsRestant;
  
  // Cet objet est une liste des joueurs qui sont pr�sentement sur cette table
  private TreeMap lstJoueurs;
  
  // Cet objet est une liste des joueurs qui attendent de joueur une partie
  private TreeMap lstJoueursEnAttente;
  
  // D�claration d'une variable qui va permettre de savoir si la partie est 
  // commenc�e ou non
  private boolean bolEstCommencee;
     
  // D�claration d'une variable qui va permettre d'arr�ter la partie en laissant
  // l'�tat de la partie � "commenc�e" tant que les joueurs sont � l'�cran des pointages
  private boolean bolEstArretee;
  
  // D�claration d'un tableau � 2 dimensions qui va contenir les informations 
  // sur les cases du jeu
  private Case[][] objttPlateauJeu;
  
  // Cet objet permet de d�terminer les r�gles de jeu pour cette table
  private Regles objRegles;
  
  private GestionnaireTemps objGestionnaireTemps;
  private TacheSynchroniser objTacheSynchroniser;
  private Minuterie objMinuterie;
        
        // Position qui dit o� se trouve le WinTheGame
        private Point positionWinTheGame;
        
        // Defines what kind of game the players want to play (see config for details)
        private String butDuJeu;
  
    // Cet objet est une liste des joueurs virtuels qui jouent sur cette table
    private Vector lstJoueursVirtuels;
    
    // Cette variable indique le nombre de joueurs virtuels sur la table
    private int intNombreJoueursVirtuels;
  
    // Cette liste contient le nom des joueurs qui ont �t� d�connect�s
    // dans cette table, ce qui nous permettra, lorsqu'une partie se termine, de
    // faire la mise � jour de la liste des joueurs d�connect�s du gestionnaire
    // de communication
    private Vector lstJoueursDeconnectes;
      
    private Date objDateDebutPartie;
    
    // D�claration d'une variable qui permettra de cr�er des id pour les objets
    // On va initialis� cette variable lorsque le plateau de jeu sera cr��
    private IntObj objProchainIdObjet;
    
  public Table(Salle salleParente, int noTable, String nomUtilisateurCreateur, 
               int tempsPartie, Regles reglesTable,
               GestionnaireTemps gestionnaireTemps, 
               TacheSynchroniser tacheSynchroniser,
               String butDuJeu) {
    
    super();
    positionWinTheGame = new Point(-1, -1);
    this.butDuJeu = butDuJeu;
    
    GestionnaireConfiguration config = GestionnaireConfiguration.getInstance();
    _MAX_NB_JOUEURS = config.obtenirNombreEntier( "table.max-nb-joueurs" );
    
    // Faire la r�f�rence vers le gestionnaire d'�v�nements et le 
    // gestionnaire de base de donn�es
    objGestionnaireEvenements = new GestionnaireEvenements();
    
    // Garder en m�moire la r�f�rence vers la salle parente, le num�ro de 
    // la table, le nom d'utilisateur du cr�ateur de la table et le temps
    // total d'une partie
    objSalle = salleParente;
    intNoTable = noTable;
    strNomUtilisateurCreateur = nomUtilisateurCreateur;
    intTempsTotal = tempsPartie;
        if(!this.butDuJeu.equals("original")) winTheGame = new WinTheGame(this);
        // intTempsRestant = tempsPartie;
    
    // Cr�er une nouvelle liste de joueurs
    lstJoueurs = new TreeMap();
    lstJoueursEnAttente = new TreeMap();
    
    // Au d�part, aucune partie ne se joue sur la table
    bolEstCommencee = false;
    bolEstArretee = true;
    intNbJoueurDemande = _MAX_NB_JOUEURS;//TODO intNbJoueurDemande = intNbJoueur; validation avec MAX_NB_JOUEURS
    
    // D�finir les r�gles de jeu pour la salle courante
    objRegles = reglesTable;
    
    // Initialiser le plateau de jeu � null
    objttPlateauJeu = null;
    
    objGestionnaireTemps = gestionnaireTemps;
    objTacheSynchroniser = tacheSynchroniser;
    
    // Au d�part, on consid�re qu'il n'y a que des joueurs humains.
    // Lorsque l'on d�marrera une partie dans laPartieCommence(), on cr�era
    // autant de joueurs virtuels que intNombreJoueursVirtuels (qui devra donc
    // �tre affect� du bon nombre au pr�alable)
    intNombreJoueursVirtuels = 0;
    lstJoueursVirtuels = null;
    
    // Cette liste sera modifi� si jamais un joueur est d�connect�
    lstJoueursDeconnectes = new Vector();
    
    // Cr�er un thread pour le GestionnaireEvenements
    Thread threadEvenements = new Thread(objGestionnaireEvenements);
    
    // D�marrer le thread du gestionnaire d'�v�nements
    threadEvenements.start();
  }
  
  /**
   * Constructeur de la classe Table qui permet d'initialiser les membres 
   * priv�s de la table.
   *
   * @param Salle salleParente : La salle dans laquelle se trouve cette table
   * @param GestionnaireBD gestionnaireBD : Le gestionnaire de base de donn�es
   * @param int noTable : Le num�ro de la table
   * @param String nomUtilisateurCreateur : Le nom d'utilisateur du cr�ateur
   *                      de la table
   * @param int tempsPartie : Le temps de la partie en minute
   * @param Regles reglesTable : Les r�gles pour une partie sur cette table
   */
  /*
  public Table(GestionnaireBD gestionnaireBD, 
         Salle salleParente, int noTable, String nomUtilisateurCreateur, 
         int tempsPartie, Regles reglesTable,
         GestionnaireTemps gestionnaireTemps, 
         TacheSynchroniser tacheSynchroniser,
         ControleurJeu controleurJeu, String butDuJeu) 
  {
    super();
                positionWinTheGame = new Point(-1, -1);
                this.butDuJeu = butDuJeu;
                
    GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();
    _MAX_NB_JOUEURS = config.obtenirNombreEntier( "table.max-nb-joueurs" );
    
    // Faire la r�f�rence vers le gestionnaire d'�v�nements et le 
    // gestionnaire de base de donn�es
    objGestionnaireEvenements = new GestionnaireEvenements();
    objGestionnaireBD = gestionnaireBD;
    
    // Garder en m�moire la r�f�rence vers la salle parente, le num�ro de 
    // la table, le nom d'utilisateur du cr�ateur de la table et le temps
    // total d'une partie
    objSalle = salleParente;
    intNoTable = noTable;
    strNomUtilisateurCreateur = nomUtilisateurCreateur;
    intTempsTotal = tempsPartie;
                if(!this.butDuJeu.equals("original")) winTheGame = new WinTheGame(this);
                // intTempsRestant = tempsPartie;
    
    // Cr�er une nouvelle liste de joueurs
    lstJoueurs = new TreeMap();
    lstJoueursEnAttente = new TreeMap();
    
    // Au d�part, aucune partie ne se joue sur la table
    bolEstCommencee = false;
    bolEstArretee = true;
    intNbJoueurDemande = _MAX_NB_JOUEURS;//TODO intNbJoueurDemande = intNbJoueur; validation avec MAX_NB_JOUEURS
    
    // D�finir les r�gles de jeu pour la salle courante
    objRegles = reglesTable;
    
    // Initialiser le plateau de jeu � null
    objttPlateauJeu = null;
    
    objGestionnaireTemps = gestionnaireTemps;
    objTacheSynchroniser = tacheSynchroniser;

        // Au d�part, on consid�re qu'il n'y a que des joueurs humains.
        // Lorsque l'on d�marrera une partie dans laPartieCommence(), on cr�era
        // autant de joueurs virtuels que intNombreJoueursVirtuels (qui devra donc
        // �tre affect� du bon nombre au pr�alable)
        intNombreJoueursVirtuels = 0;
        lstJoueursVirtuels = null;
        
        // Cette liste sera modifi� si jamais un joueur est d�connect�
        lstJoueursDeconnectes = new Vector();
        
        
        // Faire la r�f�rence vers le controleu jeu
        objControleurJeu = controleurJeu;
        
        // Cr�er un thread pour le GestionnaireEvenements
    Thread threadEvenements = new Thread(objGestionnaireEvenements);
    
    // D�marrer le thread du gestionnaire d'�v�nements
    threadEvenements.start();

  }
  */
  
  public void creation()
  {

  }
  
  public void destruction()
  {
    arreterPartie("");
                
                // On doit aussi arr�ter le thread du WinTheGame si n�cessaire
                if(winTheGame.thread.isAlive())
                {
                    winTheGame.arreter();
                }
  }
  
  /**
   * Cette fonction permet au joueur d'entrer dans la table courante. 
   * On suppose que le joueur n'est pas dans une autre table, que la table 
   * courante n'est pas compl�te et qu'il n'y a pas de parties en cours. 
   * Cette fonction va avoir pour effet de connecter le joueur dans la table 
   * courante.
   * 
   * @param JoueurHumain joueur : Le joueur demandant d'entrer dans la table
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *                g�n�rer un num�ro de commande pour le retour de
   *                l'appel de fonction
   * @param TreeMap listePersonnageJoueurs : La liste des joueurs dont la cl� 
   *                est le nom d'utilisateur du joueur et le contenu 
   *                est le Id du personnage choisi
   * @throws NullPointerException : Si la liste listePersonnageJoueurs est nulle
   * 
   * Synchronisme : Cette fonction est synchronis�e pour �viter que deux 
   *          joueurs puissent entrer ou quitter la table en m�me temps.
   *          On n'a pas � s'inqui�ter que le joueur soit modifi�
   *          pendant le temps qu'on ex�cute cette fonction. De plus
   *          on n'a pas � rev�rifier que la table existe bien (car
   *          elle ne peut �tre supprim�e en m�me temps qu'un joueur 
   *          entre dans la table), qu'elle n'est pas compl�te ou 
   *          qu'une partie est en cours (car toutes les fonctions 
   *          permettant de changer �a sont synchronis�es).
   */
  public void entrerTable(JoueurHumain joueur, boolean doitGenererNoCommandeRetour, TreeMap listePersonnageJoueurs)  throws NullPointerException
  {
      // Emp�cher d'autres thread de toucher � la liste des joueurs de 
      // cette table pendant l'ajout du nouveau joueur dans cette table
      synchronized (lstJoueurs)
      {
        // Remplir la liste des personnages choisis
        remplirListePersonnageJoueurs(listePersonnageJoueurs);
        
      // Ajouter ce nouveau joueur dans la liste des joueurs de cette table
      lstJoueurs.put(joueur.obtenirNomUtilisateur(), joueur);
      
      // Le joueur est maintenant entr� dans la table courante (il faut
      // cr�er un objet InformationPartie qui va pointer sur la table
      // courante)
      //joueur.definirPartieCourante(new InformationPartie(objGestionnaireEvenements, objGestionnaireBD, joueur, this));
      joueur.definirPartieCourante(new InformationPartie(objGestionnaireEvenements, joueur, this));
      
      // Si on doit g�n�rer le num�ro de commande de retour, alors
      // on le g�n�re, sinon on ne fait rien
      if (doitGenererNoCommandeRetour == true)
      {
        // G�n�rer un nouveau num�ro de commande qui sera 
          // retourn� au client
          joueur.obtenirProtocoleJoueur().genererNumeroReponse();             
      }

      // Emp�cher d'autres thread de toucher � la liste des joueurs de 
        // cette salle pendant qu'on parcourt tous les joueurs de la salle
      // pour leur envoyer un �v�nement
        synchronized (objSalle.obtenirListeJoueurs())
        {
        // Pr�parer l'�v�nement de nouveau joueur dans la table. 
        // Cette fonction va passer les joueurs et cr�er un 
        // InformationDestination pour chacun et ajouter l'�v�nement 
        // dans la file de gestion d'�v�nements
        preparerEvenementJoueurEntreTable(joueur.obtenirNomUtilisateur());          
        }
      }
  }

  /**
   * Cette m�thode permet au joueur pass� en param�tres de quitter la table. 
   * On suppose que le joueur est dans la table.
   * 
   * @param JoueurHumain joueur : Le joueur demandant de quitter la table
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *                g�n�rer un num�ro de commande pour le retour de
   *                l'appel de fonction
   * 
   * Synchronisme : Cette fonction est synchronis�e sur la liste des tables
   *          puis sur la liste des joueurs de cette table, car il se
   *          peut qu'on doive d�truire la table si c'est le dernier
   *          joueur et qu'on va modifier la liste des joueurs de cette
   *          table, car le joueur quitte la table. Cela �vite que des
   *          joueurs entrent ou quittent une table en m�me temps.
   *          On n'a pas � s'inqui�ter que le joueur soit modifi�
   *          pendant le temps qu'on ex�cute cette fonction. Si on 
   *          inverserait les synchronisations, �a pourrait cr�er un 
   *          deadlock avec les personnes entrant dans la salle.
   */
  public void quitterTable(JoueurHumain joueur, boolean doitGenererNoCommandeRetour, boolean detruirePartieCourante)
  {
      // Emp�cher d'autres thread de toucher � la liste des tables de 
      // cette salle pendant que le joueur quitte cette table
      synchronized (objSalle.obtenirListeTables())
      {
        // Emp�cher d'autres thread de toucher � la liste des joueurs de 
        // cette table pendant que le joueur quitte cette table
        synchronized (lstJoueurs)
        {
          // Enlever le joueur de la liste des joueurs de cette table
        lstJoueurs.remove(joueur.obtenirNomUtilisateur());
        
        // Le joueur est maintenant dans aucune table
        if (detruirePartieCourante == true)
        {
          joueur.definirPartieCourante(null);
        }
        
        // Si on doit g�n�rer le num�ro de commande de retour, alors
        // on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
        // faux)
        if (doitGenererNoCommandeRetour == true)
        {
          // G�n�rer un nouveau num�ro de commande qui sera 
            // retourn� au client
            joueur.obtenirProtocoleJoueur().genererNumeroReponse();             
        }

        // Emp�cher d'autres thread de toucher � la liste des joueurs de 
          // cette salle pendant qu'on parcourt tous les joueurs de la salle
        // pour leur envoyer un �v�nement
          synchronized (objSalle.obtenirListeJoueurs())
          {
          // Pr�parer l'�v�nement qu'un joueur a quitt� la table. 
          // Cette fonction va passer les joueurs et cr�er un 
          // InformationDestination pour chacun et ajouter l'�v�nement 
          // dans la file de gestion d'�v�nements
          preparerEvenementJoueurQuitteTable(joueur.obtenirNomUtilisateur());
          }

          // S'il ne reste aucun joueur dans la table et que la partie
          // est termin�e, alors on doit d�truire la table
          if (lstJoueurs.size() == 0 && bolEstArretee == true)
          {
            //Arreter le gestionnaire de temps
            //objGestionnaireTemps.arreterGestionnaireTemps();
            // D�truire la table courante et envoyer les �v�nements 
            // appropri�s
            objSalle.detruireTable(this);
          }
        }
    }
  }
  
  /**
   * Cette m�thode permet au joueur pass� en param�tres de d�marrer la partie. 
   * On suppose que le joueur est dans la table.
   * 
   * @param JoueurHumain joueur : Le joueur demandant de d�marrer la partie
   * @param int idPersonnage : Le num�ro Id du personnage choisi par le joueur
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *                g�n�rer un num�ro de commande pour le retour de
   *                l'appel de fonction
   * @return String : Succes : si le joueur est maintenant en attente
   *          DejaEnAttente : si le joueur �tait d�j� en attente
   *          PartieEnCours : si une partie �tait en cours
   * 
   * Synchronisme : Cette fonction est synchronis�e sur la liste des joueurs 
   *          en attente, car il se peut qu'on ajouter ou retirer des
   *          joueurs de la liste en attente en m�me temps. On n'a pas
   *          � s'inqui�ter que le m�me joueur soit mis dans la liste 
   *          des joueurs en attente par un autre thread.
   */
  public String demarrerPartie(JoueurHumain joueur, int idPersonnage, boolean doitGenererNoCommandeRetour)
  {
    // Cette variable va permettre de savoir si le joueur est maintenant
    // attente ou non
    String strResultatDemarrerPartie;
    
      // Emp�cher d'autres thread de toucher � la liste des joueurs en attente 
      // de cette table pendant que le joueur tente de d�marrer la partie
      synchronized (lstJoueursEnAttente)
      {
        // Si une partie est en cours alors on va retourner PartieEnCours
        if (bolEstCommencee == true)
        {
          strResultatDemarrerPartie = ResultatDemarrerPartie.PartieEnCours;
        }
        // Sinon si le joueur est d�j� en attente, alors on va retourner 
        // DejaEnAttente
        else if (lstJoueursEnAttente.containsKey(joueur.obtenirNomUtilisateur()) == true)
        {
          strResultatDemarrerPartie = ResultatDemarrerPartie.DejaEnAttente;
        }
        else
        {
          // La commande s'est effectu�e avec succ�s
          strResultatDemarrerPartie = ResultatDemarrerPartie.Succes;
          
          // Ajouter le joueur dans la liste des joueurs en attente
        lstJoueursEnAttente.put(joueur.obtenirNomUtilisateur(), joueur);
        
        // Garder en m�moire le Id du personnage choisi par le joueur
        joueur.obtenirPartieCourante().definirIdPersonnage(idPersonnage);
        
          // Si on doit g�n�rer le num�ro de commande de retour, alors
        // on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
        // faux)
        if (doitGenererNoCommandeRetour == true)
        {
          // G�n�rer un nouveau num�ro de commande qui sera 
            // retourn� au client
            joueur.obtenirProtocoleJoueur().genererNumeroReponse();             
        }
        
        // Emp�cher d'autres thread de toucher � la liste des joueurs de 
          // cette table pendant qu'on parcourt tous les joueurs de la table
        // pour leur envoyer un �v�nement
          synchronized (lstJoueurs)
          {
          // Pr�parer l'�v�nement de joueur en attente. 
          // Cette fonction va passer les joueurs et cr�er un 
          // InformationDestination pour chacun et ajouter l'�v�nement 
          // dans la file de gestion d'�v�nements
          preparerEvenementJoueurDemarrePartie(joueur.obtenirNomUtilisateur(), idPersonnage);         
          }
        
        // Si le nombre de joueurs en attente est maintenant le nombre 
        // de joueurs que �a prend pour joueur au jeu, alors on lance 
        // un �v�nement qui indique que la partie est commenc�e
        if (lstJoueursEnAttente.size() == intNbJoueurDemande)
        {
          laPartieCommence("Aucun");      
        }
        }
    }
      
      return strResultatDemarrerPartie;
  }
  
  public String demarrerMaintenant(JoueurHumain joueur, int idPersonnage, boolean doitGenererNoCommandeRetour, String strParamJoueurVirtuel)
  {
    // Lorsqu'on fait d�marr� maintenant, le nombre de joueurs sur la
    // table devient le nombre de joueurs demand�, lorsqu'ils auront tous
    // fait OK, la partie d�marrera
    intNbJoueurDemande = lstJoueurs.size();
    
    String strResultatDemarrerPartie;
    synchronized (lstJoueursEnAttente)
      {
            // Si une partie est en cours alors on va retourner PartieEnCours
        if (bolEstCommencee == true)
        {
          strResultatDemarrerPartie = ResultatDemarrerPartie.PartieEnCours;
        }
        //TODO si joueur pas en attente?????
        else
        {
          // La commande s'est effectu�e avec succ�s
          strResultatDemarrerPartie = ResultatDemarrerPartie.Succes;
          
          // Ajouter le joueur dans la liste des joueurs en attente
        //lstJoueursEnAttente.put(joueur.obtenirNomUtilisateur(), joueur);
        
        // Garder en m�moire le Id du personnage choisi par le joueur
        joueur.obtenirPartieCourante().definirIdPersonnage(idPersonnage);
        
          // Si on doit g�n�rer le num�ro de commande de retour, alors
        // on le g�n�re, sinon on ne fait rien (�a se peut que ce soit
        // faux)
        if (doitGenererNoCommandeRetour == true)
        {
          // G�n�rer un nouveau num�ro de commande qui sera 
            // retourn� au client
            joueur.obtenirProtocoleJoueur().genererNumeroReponse();             
        }
        
        // Si le nombre de joueurs en attente est maintenant le nombre 
        // de joueurs que �a prend pour joueur au jeu, alors on lance 
        // un �v�nement qui indique que la partie est commenc�e
        if (lstJoueursEnAttente.size() == intNbJoueurDemande)
        {
          laPartieCommence(strParamJoueurVirtuel);      
        }
        }
      }
    return strResultatDemarrerPartie;
  }
    
    /* Cette fonction permet d'obtenir un tableau contenant intNombreJoueurs
     * noms de joueurs virtuels diff�rents
     */
  private String[] obtenirNomsJoueursVirtuels(int intNombreJoueurs)
  {
    // Obtenir une r�f�rence vers l'objet ParametreIA contenant
    // la banque de noms
    ControleurJeu objControleurJeu = ControleurJeu.getInstance();
    
    ParametreIA objParametreIA = objControleurJeu.obtenirParametreIA();
    
    // Obtenir le nombre de noms dans la banque
    int intQuantiteBanque = objParametreIA.tBanqueNomsJoueurVirtuels.length;
    
    // D�claration d'un tableau pour m�langer les indices de noms
    int tIndexNom[] = new int[intQuantiteBanque];
    
    // Permet d'�changer des indices du tableau pour m�langer
    int intTemp;
    int intA;
    int intB;
    
    // Pr�parer le tableau pour le m�lange
    for (int i = 0; i < tIndexNom.length; i++)
    {
      tIndexNom[i] = i;
    }
    
    // M�langer les noms
    for (int i = 0; i < intNombreJoueurs; i++)
    {
      intA = i;
      intB = objControleurJeu.genererNbAleatoire(intQuantiteBanque);
        
        intTemp = tIndexNom[intA];
        tIndexNom[intA] = tIndexNom[intB];
        tIndexNom[intB] = intTemp;
    }

       // Cr�er le tableau de retour
       String tRetour[] = new String[intNombreJoueurs];
       
       // Choisir au hasard o� aller chercher les indices
       int intDepart = objControleurJeu.genererNbAleatoire(intQuantiteBanque);
       
       // Remplir le tableau avec les valeurs trouv�es
       for (int i = 0; i < intNombreJoueurs; i++)
       {
           tRetour[i] = new String(objParametreIA.tBanqueNomsJoueurVirtuels[(i + intDepart) % intQuantiteBanque]);
       }
       
       return tRetour;
  }
  
  private void laPartieCommence(String strParamJoueurVirtuel)
  {
    
    ControleurJeu objControleurJeu = ControleurJeu.getInstance();
    
        // Cr�er une nouvelle liste qui va garder les points des 
    // cases libres (n'ayant pas d'objets dessus)
    Vector lstPointsCaseLibre = new Vector();
    
    // Cr�er un tableau de points qui va contenir la position 
    // des joueurs
    Point[] objtPositionsJoueurs;
    
    // Cr�ation d'une nouvelle liste dont la cl� est le nom 
    // d'utilisateur du joueur et le contenu est un point 
    // repr�sentant la position du joueur
    TreeMap lstPositionsJoueurs = new TreeMap();
        
                // Contient les noms des joueurs virtuels
                String tNomsJoueursVirtuels[] = null;

                // Contiendra le dernier ID des objets
                objProchainIdObjet = new IntObj();
        
    //TODO: Peut-�tre devoir synchroniser cette partie, il 
    //      faut voir avec les autres bouts de code qui 
    //    v�rifient si la partie est commenc�e (c'est OK 
    //    pour entrerTable)
    // Changer l'�tat de la table pour dire que maintenant une 
    // partie est commenc�e
    bolEstCommencee = true;
    
    // Change l'�tat de la table pour dire que la partie
    // n'est pas arr�t�e (note: bolEstCommencee restera � true
    // pendant que les joueurs sont � l'�cran de pointage)
    bolEstArretee = false;
    
    // G�n�rer le plateau de jeu selon les r�gles de la table et 
    // garder le plateau en m�moire dans la table
    objttPlateauJeu = GenerateurPartie.genererPlateauJeu(objRegles, intTempsTotal, lstPointsCaseLibre, objProchainIdObjet, butDuJeu);

                // D�finir le prochain id pour les objets
                objProchainIdObjet.intValue++;
        
    // Obtenir la position des joueurs de cette table
    int nbJoueur = lstJoueursEnAttente.size(); //TODO a v�rifier
    
    // Contient le niveau de difficult� que le joueur d�sire pour
    // les joueurs virtuels
    // on obtient la difficult� par d�faut � partir du fichier de configuration
    GestionnaireConfiguration config = GestionnaireConfiguration.getInstance();
    
    int intDifficulteJoueurVirtuel = config.obtenirNombreEntier("joueurs-virtuels.difficulte_defaut");
    //int intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_MOYEN;
    
    // Obtenir le nombre de joueurs virtuel requis
    // V�rifier d'abord le param�tre envoyer par le joueur
    if (strParamJoueurVirtuel.equals("Aucun"))
    {
      intNombreJoueursVirtuels = 0;
    }
    else
    {
      // Le joueur veut des joueurs virtuels
      if (strParamJoueurVirtuel.equals("Facile"))
      {
        intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_FACILE;
      }
      else if(strParamJoueurVirtuel.equals("Intermediaire"))
      {
        intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_MOYEN;
      }
      else if(strParamJoueurVirtuel.equals("Difficile"))
      {
        intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_DIFFICILE;
      }
      else if(strParamJoueurVirtuel.equals("TresDifficile"))
      {
        intDifficulteJoueurVirtuel = ParametreIA.DIFFICULTE_TRES_DIFFICILE;
      }
      
      // D�terminer combien de joueurs on veut
      intNombreJoueursVirtuels = 4 - lstJoueursEnAttente.size();
      if (intNombreJoueursVirtuels < 0 || intNombreJoueursVirtuels >=4)
      {
        intNombreJoueursVirtuels = 0;
      }
    }
    
    // Aller chercher les positions de d�part pour les joueurs humains et virtuels
        objtPositionsJoueurs = GenerateurPartie.genererPositionJoueurs(nbJoueur + intNombreJoueursVirtuels, lstPointsCaseLibre);
    
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // lstJoueursEnAttente (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueursEnAttente.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les personnages
    Iterator objIterateurListeJoueurs = lstEnsembleJoueurs.iterator();

    // S'il y a des joueurs virtuels, alors on va cr�er une nouvelle liste
    // qui contiendra ces joueurs
    if (intNombreJoueursVirtuels > 0)
    {
        lstJoueursVirtuels = new Vector();
        
        // Aller trouver les noms des joueurs virtuels
        tNomsJoueursVirtuels = obtenirNomsJoueursVirtuels(intNombreJoueursVirtuels);
        
    }
    
    // Cette variable permettra d'affecter aux joueurs virtuels des id
    // de personnage diff�rents de ceux des joueurs humains
    int intIdPersonnage = 1;

    // Passer toutes les positions des joueurs et les d�finir
    for (int i = 0; i < objtPositionsJoueurs.length; i++)
    {
        // On doit affecter certains positions aux joueurs humains et d'autres aux joueurs
        // virtuels. La grandeur de objtPositionsJoueurs est nbJoueur + intNombreJoueursVirtuels
        if (i < nbJoueur)
        {
            
          // Comme les positions sont g�n�r�es al�atoirement, on 
          // se fou un peu duquel on va d�finir la position en 
          // premier, on va donc passer simplement la liste des 
          // joueurs
          // Cr�er une r�f�rence vers le joueur courant 
            // dans la liste (pas besoin de v�rifier s'il y en a un 
          // prochain, car on a g�n�r� la position des joueurs 
          // selon cette liste
          JoueurHumain objJoueur = (JoueurHumain) (((Map.Entry)(objIterateurListeJoueurs.next())).getValue());
          
          // D�finir la position du joueur courant
          objJoueur.obtenirPartieCourante().definirPositionJoueur(objtPositionsJoueurs[i]);
          
          // Ajouter la position du joueur dans la liste
          lstPositionsJoueurs.put(objJoueur.obtenirNomUtilisateur(), objtPositionsJoueurs[i]);
        }
        else
        {
            // On se rendra ici seulement si intNombreJoueursVirtuels > 0
            // C'est ici qu'on cr�e les joueurs virtuels, ils vont commencer
            // � jouer plus loin
          
                // Ajouter un joueur virtuel dans la table

            // Utiliser le prochaine id de personnage libre
            while (!idPersonnageEstLibre(intIdPersonnage))
            {
              // Incr�menter le id du personnage en esp�rant en trouver un autre
              intIdPersonnage++;
            }
            
            // Cr�� le joueur virtuel selon le niveau de difficult� d�sir�
                JoueurVirtuel objJoueurVirtuel = new JoueurVirtuel(tNomsJoueursVirtuels[i - nbJoueur], 
                    intDifficulteJoueurVirtuel, this, objGestionnaireEvenements, objControleurJeu, intIdPersonnage);
                
                // D�finir sa position
                objJoueurVirtuel.definirPositionJoueurVirtuel(objtPositionsJoueurs[i]);
                
                // Ajouter le joueur virtuel � la liste
                lstJoueursVirtuels.add(objJoueurVirtuel);
                
                // Ajouter le joueur virtuel � la liste des positions, liste qui sera envoy�e
                // aux joueurs humains
                lstPositionsJoueurs.put(objJoueurVirtuel.obtenirNom(), objtPositionsJoueurs[i]);
                
                // Pour le prochain joueur virtuel
                intIdPersonnage++;
                
        }
    }
    
    // On peut maintenant vider la liste des joueurs en attente
    // car elle ne nous sert plus � rien
    lstJoueursEnAttente.clear();
    
    
    // Maintenant pour tous les joueurs, s'il y a des joueurs
    // virtuels de pr�sents, on leur envoit un message comme
    // quoi les joueurs virtuels sont pr�ts
    if (intNombreJoueursVirtuels > 0)
    {
        synchronized (lstJoueurs)
        {
            for (int i = 0; i < lstJoueursVirtuels.size(); i++)
            {
          // Pr�parer l'�v�nement de joueur en attente. 
          // Cette fonction va passer les joueurs et cr�er un 
          // InformationDestination pour chacun et ajouter l'�v�nement 
          // dans la file de gestion d'�v�nements
          JoueurVirtuel objJoueurVirtuel = (JoueurVirtuel) lstJoueursVirtuels.get(i);
          
          preparerEvenementJoueurEntreTable(objJoueurVirtuel.obtenirNom());
          preparerEvenementJoueurDemarrePartie(objJoueurVirtuel.obtenirNom(), objJoueurVirtuel.obtenirIdPersonnage());          
          }
        }
      }
    
    
    // Emp�cher d'autres thread de toucher � la liste des joueurs de 
      // cette table pendant qu'on parcourt tous les joueurs de la table
    // pour leur envoyer un �v�nement
      synchronized (lstJoueurs)
      {
      // Pr�parer l'�v�nement que la partie est commenc�e. 
      // Cette fonction va passer les joueurs et cr�er un 
      // InformationDestination pour chacun et ajouter l'�v�nement 
      // dans la file de gestion d'�v�nements
      preparerEvenementPartieDemarree(lstPositionsJoueurs);
      }
      
      int tempsStep = 1;
      objTacheSynchroniser.ajouterObservateur( this );
      objMinuterie = new Minuterie( intTempsTotal * 60, tempsStep );
      objMinuterie.ajouterObservateur( this );
      objGestionnaireTemps.ajouterTache( objMinuterie, tempsStep );
      
      // Obtenir la date � ce moment pr�cis
      objDateDebutPartie = new Date();
      
      // D�marrer tous les joueurs virtuels 
      if (intNombreJoueursVirtuels > 0)
      {
          for (int i = 0; i < lstJoueursVirtuels.size(); i++)
          {
                Thread threadJoueurVirtuel = new Thread((JoueurVirtuel) lstJoueursVirtuels.get(i));
                threadJoueurVirtuel.start();
            }
            
            // On trouve une position initiale au WinTheGame et on part son thread si n�cessaire
            if(!butDuJeu.equals("original"))
            {
                definirNouvellePositionWinTheGame();
                winTheGame.demarrer();
            }
        }
        
  }
  
  public void arreterPartie(String joueurGagnant)
  {
    GestionnaireBD lBd = new GestionnaireBD(ControleurJeu.getInstance().getConnection());
    ControleurJeu objControleurJeu = ControleurJeu.getInstance();
    
      // bolEstArretee permet de savoir si cette fonction a d�j� �t� appel�e
      // de plus, bolEstArretee et bolEstCommencee permettent de conna�tre 
      // l'�tat de la partie
    if(bolEstArretee == false)
    {
      // Arr�ter la partie
      bolEstArretee = true;
      objTacheSynchroniser.enleverObservateur(this);
      objGestionnaireTemps.enleverTache(objMinuterie);
      objMinuterie = null;

      // S'il y a au moins un joueur qui a compl�t� la partie,
      // alors on ajoute les informations de cette partie dans la BD
      if(lstJoueurs.size() > 0)
      {       
        // Ajouter la partie dans la BD
        int clePartie = lBd.ajouterInfosPartiePartieTerminee(objDateDebutPartie, intTempsTotal);
  
            // Sert � d�terminer si le joueur a gagn�
            boolean bolGagnant;
    
            // Sert � d�terminer le meilleur score pour cette partie
        int meilleurPointage = 0;
              
        // Parcours des joueurs virtuels pour trouver le meilleur pointage
        if (lstJoueursVirtuels != null)
        {
          for (int i = 0; i < lstJoueursVirtuels.size(); i++)
          {
            JoueurVirtuel objJoueurVirtuel = (JoueurVirtuel) lstJoueursVirtuels.get(i);
              if (objJoueurVirtuel.obtenirPointage() > meilleurPointage)
              {
                meilleurPointage = objJoueurVirtuel.obtenirPointage();
              }
          }
        }
          
        synchronized (lstJoueurs)
          {
            // Parcours des joueurs pour trouver le meilleur pointage
          Iterator iteratorJoueursHumains = lstJoueurs.values().iterator();
          while (iteratorJoueursHumains.hasNext())
          {
            JoueurHumain objJoueurHumain = (JoueurHumain)iteratorJoueursHumains.next();
            if (objJoueurHumain.obtenirPartieCourante().obtenirPointage() > meilleurPointage)
            {
              meilleurPointage = objJoueurHumain.obtenirPartieCourante().obtenirPointage();
            }
          }
            
          preparerEvenementPartieTerminee(joueurGagnant);
          
          // Parcours des joueurs pour mise � jour de la BD et
          // pour ajouter les infos de la partie compl�t�e
          Iterator it = lstJoueurs.values().iterator();
          while(it.hasNext())
          {
            // Mettre a jour les donn�es des joueurs
            JoueurHumain joueur = (JoueurHumain)it.next();
            lBd.mettreAJourJoueur(joueur, intTempsTotal);
            
                                                // Si un joueur a atteint le WinTheGame, joueurGagnant contiendra le nom de ce joueur
                                                if(joueurGagnant.equals(""))
                                                {
                                                    // V�rififer si ce joueur a gagn� par les points
                                                    if (joueur.obtenirPartieCourante().obtenirPointage() == meilleurPointage)
                                                    {
                                                            bolGagnant = true;
                                                    }
                                                    else
                                                    {
                                                            bolGagnant = false;
                                                    }
                                                }
                                                else
                                                {
                                                    if(joueurGagnant.equals(joueur.obtenirNomUtilisateur())) bolGagnant = true;
                                                    else bolGagnant = false;
                                                }
            
            // Ajouter l'information pour cette partie et ce joueur
                                                lBd.ajouterInfosJoueurPartieTerminee(clePartie, joueur.obtenirCleJoueur(), 
                joueur.obtenirPartieCourante().obtenirPointage(), bolGagnant);
            
            
          }
          }
        }
        
        // Arr�ter les threads des joueurs virtuels
            if (intNombreJoueursVirtuels > 0)
        {
            for (int i = 0; i < lstJoueursVirtuels.size(); i++)
            {
                    ((JoueurVirtuel)lstJoueursVirtuels.get(i)).arreterThread();
            }
        }
        
        // Enlever les joueurs d�connect�s de cette table de la
        // liste des joueurs d�connect�s du serveur pour �viter
        // qu'ils ne se reconnectent et tentent de rejoindre une partie termin�e
        for (int i = 0; i < lstJoueursDeconnectes.size(); i++)
        {
          objControleurJeu.enleverJoueurDeconnecte((String) lstJoueursDeconnectes.get(i));
        }
        
        // Enlever les joueurs d�connect�s de cette table
        lstJoueursDeconnectes = new Vector();
        
        // Si jamais les joueurs humains sont tous d�connect�s, alors
        // il faut d�truire la table ici
        if (lstJoueurs.size() == 0)
        {
          // D�truire la table courante et envoyer les �v�nements 
          // appropri�s
          objSalle.detruireTable(this);
        }
    }
  }
  
  /**
   * Cette fonction permet de retourner le num�ro de la table courante.
   * 
   * @return int : Le num�ro de la table
   */
  public int obtenirNoTable()
  {
    return intNoTable;
  }
  
  /**
   * Cette fonction permet de retourner la liste des joueurs. La vraie liste
   * est retourn�e.
   * 
   * @return TreeMap : La liste des joueurs se trouvant dans la table courante
   * 
   * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle doit
   *          l'�tre par l'appelant de cette fonction tout d�pendant
   *          du traitement qu'elle doit faire
   */
  public TreeMap obtenirListeJoueurs()
  {
    return lstJoueurs;
  }
  
  /**
   * Cette fonction permet de retourner la liste des joueurs qui sont en 
   * attente de jouer une partie. La vraie liste est retourn�e.
   * 
   * @return TreeMap : La liste des joueurs en attente se trouvant dans la 
   *           table courante
   * 
   * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle doit
   *          l'�tre par l'appelant de cette fonction tout d�pendant
   *          du traitement qu'elle doit faire
   */
  public TreeMap obtenirListeJoueursEnAttente()
  {
    return lstJoueursEnAttente;
  }
  
  /**
   * Cette fonction permet de retourner le temps total des parties de cette 
   * table.
   * 
   * @return int : Le temps total des parties de cette table
   */
  public int obtenirTempsTotal()
  {
    return intTempsTotal;
  }
  
  /**
   * Cette fonction permet de d�terminer si la table est compl�te ou non 
   * (elle est compl�te si le nombre de joueurs dans cette table �gale le 
   * nombre de joueurs maximum par table).
   * 
   * @return boolean : true si la table est compl�te
   *           false sinon
   * 
   * Synchronisme : Cette fonction est synchronis�e car il peut s'ajouter de
   *          nouveaux joueurs ou d'autres peuvent quitter pendant la 
   *          v�rification.
   */
  public boolean estComplete()
  {
      // Emp�cher d'autres Thread de toucher � la liste des joueurs de cette
      // table pendant qu'on fait la v�rification (un TreeMap n'est pas 
      // synchronis�)
      synchronized (lstJoueurs)
      {
      // Si la taille de la liste de joueurs �gale le nombre maximal de 
      // joueurs alors la table est compl�te, sinon elle ne l'est pas
      return (lstJoueurs.size() == intNbJoueurDemande);         
      }
  }
  
  /**
   * Cette fonction permet de d�terminer si une partie est commenc�e ou non.
   * 
   * @return boolean : true s'il y a une partie en cours
   *           false sinon
   */
  public boolean estCommencee()
  {
    return bolEstCommencee;         
  }
  
  /**
   * Cette fonction retourne les r�gles pour la table courante.
   * 
   * @return Regles : Les r�gles pour la table courante
   */
  public Regles obtenirRegles()
  {
    return objRegles;
  }
  
  /**
   * Cette fonction retourne le plateau de jeu courant.
   * 
   * @return Case[][] : Le plateau de jeu courant,
   *            null s'il n'y a pas de partie en cours
   */
  public Case[][] obtenirPlateauJeuCourant()
  {
    return objttPlateauJeu;
  }
  
  /**
   * Cette m�thode permet de remplir la liste des personnages des joueurs 
   * o� les cl�s seront le nom d'utilisateur du joueur et le contenu le 
   * num�ro du personnage. On suppose que le joueur courant n'est pas 
   * encore dans la liste.
   *  
   * @param TreeMap listePersonnageJoueurs : La liste des personnages 
   *                       pour chaque joueur
   * @throws NullPointerException : Si la liste des personnages est � nulle
   */
  private void remplirListePersonnageJoueurs(TreeMap listePersonnageJoueurs) throws NullPointerException
  {
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la table (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la table et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Ajouter le joueur dans la liste des personnages (il se peut que 
      // le joueur n'aie pas encore de personnages, alors le id est 0)
      listePersonnageJoueurs.put(objJoueur.obtenirNomUtilisateur(), new Integer(objJoueur.obtenirPartieCourante().obtenirIdPersonnage()));
    }
    
    // D�claration d'un compteur
    int i = 1;
    
    // Boucler tant qu'on n'a pas atteint le nombre maximal de 
    // joueurs moins le joueur courant car on ne le met pas dans la liste
    while (listePersonnageJoueurs.size() < _MAX_NB_JOUEURS - 1)
    {
      // On ajoute un joueur inconnu ayant le personnage 0
      listePersonnageJoueurs.put("Inconnu" + Integer.toString(i), new Integer(0));
      
      i++;
    }
  }

  /**
   * Cette m�thode permet de pr�parer l'�v�nement de l'entr�e d'un joueur 
   * dans la table courante. Cette m�thode va passer tous les joueurs 
   * de la salle courante et pour ceux devant �tre avertis (tous sauf le 
   * joueur courant pass� en param�tre), on va obtenir un num�ro de commande, 
   * on va cr�er un InformationDestination et on va ajouter l'�v�nement dans 
   * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel 
   * de cette fonction, la liste des joueurs est synchronis�e.
   * 
   * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
   *                  vient d'entrer dans la table
   * 
   * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
   *          par l'appelant (entrerTable).
   */
  private void preparerEvenementJoueurEntreTable(String nomUtilisateur)
  {
      // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs qu'un joueur est entr� dans la table
      EvenementJoueurEntreTable joueurEntreTable = new EvenementJoueurEntreTable(intNoTable, nomUtilisateur);
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la salle (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = objSalle.obtenirListeJoueurs().entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la salle et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Si le nom d'utilisateur du joueur courant n'est pas celui
      // qui vient d'entrer dans la table, alors on peut envoyer un 
      // �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
          // Obtenir un num�ro de commande pour le joueur courant, cr�er 
          // un InformationDestination et l'ajouter � l'�v�nement
          joueurEntreTable.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                        objJoueur.obtenirProtocoleJoueur()));
      }
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(joueurEntreTable);
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
   *                  vient de quitter la table
   * 
   * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
   *          par l'appelant (quitterTable).
   */
  private void preparerEvenementJoueurQuitteTable(String nomUtilisateur)
  {
      // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs qu'un joueur a quitt� la table
      EvenementJoueurQuitteTable joueurQuitteTable = new EvenementJoueurQuitteTable(intNoTable, nomUtilisateur);
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la salle (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = objSalle.obtenirListeJoueurs().entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la salle et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Si le nom d'utilisateur du joueur courant n'est pas celui
      // qui vient de quitter la table, alors on peut envoyer un 
      // �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
          // Obtenir un num�ro de commande pour le joueur courant, cr�er 
          // un InformationDestination et l'ajouter � l'�v�nement
          joueurQuitteTable.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                        objJoueur.obtenirProtocoleJoueur()));
      }
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(joueurQuitteTable);
  }
  
  /**
   * Cette m�thode permet de pr�parer l'�v�nement du d�marrage d'une partie 
   * de la table courante. Cette m�thode va passer tous les joueurs 
   * de la table courante et pour ceux devant �tre avertis (tous sauf le 
   * joueur courant pass� en param�tre), on va obtenir un num�ro de commande, 
   * on va cr�er un InformationDestination et on va ajouter l'�v�nement dans 
   * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel 
   * de cette fonction, la liste des joueurs est synchronis�e.
   * 
   * @param String nomUtilisateur : Le nom d'utilisateur du joueur qui
   *                  vient de d�marrer la partie
   * @param int idPersonnage : Le num�ro Id du personnage choisi par le joueur 
   * 
   * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
   *          par l'appelant (demarrerPartie).
   */
  private void preparerEvenementJoueurDemarrePartie(String nomUtilisateur, int idPersonnage)
  {
      // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs qu'un joueur d�marr� une partie
      EvenementJoueurDemarrePartie joueurDemarrePartie = new EvenementJoueurDemarrePartie(nomUtilisateur, idPersonnage);
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la table (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la table et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Si le nom d'utilisateur du joueur courant n'est pas celui
      // qui vient de d�marrer la partie, alors on peut envoyer un 
      // �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
          // Obtenir un num�ro de commande pour le joueur courant, cr�er 
          // un InformationDestination et l'ajouter � l'�v�nement
        joueurDemarrePartie.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                                     objJoueur.obtenirProtocoleJoueur()));
      }
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(joueurDemarrePartie);
  }
  
  /**
   * Cette m�thode permet de pr�parer l'�v�nement du d�marrage de partie 
   * de la table courante. Cette m�thode va passer tous les joueurs 
   * de la table courante et on va obtenir un num�ro de commande, on va 
   * cr�er un InformationDestination et on va ajouter l'�v�nement dans 
   * la file d'�v�nements du gestionnaire d'�v�nements. Lors de l'appel 
   * de cette fonction, la liste des joueurs est synchronis�e.
   * 
   * @param TreeMap : La liste contenant les positions des joueurs
   * 
   * Synchronisme : Cette fonction n'est pas synchronis�e ici, mais elle l'est
   *          par l'appelant (demarrerPartie).
   */
  private void preparerEvenementPartieDemarree(TreeMap listePositionJoueurs)
  {
      // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs de la table qu'un joueur a d�marr� une partie
      EvenementPartieDemarree partieDemarree = new EvenementPartieDemarree(intTempsTotal, listePositionJoueurs, objttPlateauJeu, this);
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la table (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la salle et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());

        // Obtenir un num�ro de commande pour le joueur courant, cr�er 
        // un InformationDestination et l'ajouter � l'�v�nement de la 
      // table
      partieDemarree.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                      objJoueur.obtenirProtocoleJoueur()));       
    }
    
    // Ajouter les nouveaux �v�nements cr��s dans la liste d'�v�nements 
    // � traiter
    objGestionnaireEvenements.ajouterEvenement(partieDemarree);
  }
  
  public void preparerEvenementMAJPointage(String nomUtilisateur, int nouveauPointage)
  {
    // Cr�er un nouveal �v�nement qui va permettre d'envoyer l'�v�nment
    // aux joueurs pour signifier une modification du pointage
    EvenementMAJPointage majPointage = new EvenementMAJPointage(nomUtilisateur, nouveauPointage);
    
    // Cr�er un ensemble contenant tous les tuples de la liste des joueurs
    // de la table
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passser tous les joueurs de la table et leur envoyer l'�v�nement
    // NOTE: On omet d'envoyer au joueur nomUtilisateur �tant donn�
    //       qu'il connait d�j� son pointage
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Si le nom d'utilisateur du joueur n'est pas nomUtilisateur, alors
      // on peut envoyer un �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
        // Obtenir un num�ro de commande pour le joueur courant, cr�er
        // un InformationDestination et l'ajouter � l'�v�nement
        majPointage.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                                     objJoueur.obtenirProtocoleJoueur()));                                         
      }
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(majPointage);
  }

  public void preparerEvenementMAJArgent(String nomUtilisateur, int nouvelArgent)
  {
    // Cr�er un nouveal �v�nement qui va permettre d'envoyer l'�v�nment
    // aux joueurs pour signifier une modification de l'argent
    EvenementMAJArgent majArgent = new EvenementMAJArgent(nomUtilisateur, nouvelArgent);
    
    // Cr�er un ensemble contenant tous les tuples de la liste des joueurs
    // de la table
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passser tous les joueurs de la table et leur envoyer l'�v�nement
    // NOTE: On omet d'envoyer au joueur nomUtilisateur �tant donn�
    //       qu'il connait d�j� son argent
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Si le nom d'utilisateur du joueur n'est pas nomUtilisateur, alors
      // on peut envoyer un �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
        // Obtenir un num�ro de commande pour le joueur courant, cr�er
        // un InformationDestination et l'ajouter � l'�v�nement
        majArgent.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(), objJoueur.obtenirProtocoleJoueur()));
      }
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(majArgent);
  }
        
  public void preparerEvenementUtiliserObjet(String joueurQuiUtilise, String joueurAffecte, String objetUtilise, String autresInformations)
  {
                // M�me chose que la fonction pr�c�dente, mais envoie plut�t les informations quant � l'utilisation d'un objet dont tous devront �tre au courant
    EvenementUtiliserObjet utiliserObjet = new EvenementUtiliserObjet(joueurQuiUtilise, joueurAffecte, objetUtilise, autresInformations);
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    while (objIterateurListe.hasNext() == true)
    {
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
                        utiliserObjet.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),objJoueur.obtenirProtocoleJoueur()));
    }
    objGestionnaireEvenements.ajouterEvenement(utiliserObjet);
  }
        
        public void preparerEvenementMessageChat(String joueurQuiEnvoieLeMessage, String messageAEnvoyer)
  {
                // M�me chose que la fonction pr�c�dente, mais envoie plut�t un message de la part d'un joueur � tous les joueurs de la table
    EvenementMessageChat messageChat = new EvenementMessageChat(joueurQuiEnvoieLeMessage, messageAEnvoyer);
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    while (objIterateurListe.hasNext() == true)
    {
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
                        messageChat.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),objJoueur.obtenirProtocoleJoueur()));
    }
    objGestionnaireEvenements.ajouterEvenement(messageChat);
  }

  public void preparerEvenementDeplacementWinTheGame()
  {
                definirNouvellePositionWinTheGame();
                
    EvenementDeplacementWinTheGame deplacementWTG = new EvenementDeplacementWinTheGame(positionWinTheGame.x, positionWinTheGame.y);
    
    // Cr�er un ensemble contenant tous les tuples de la liste des joueurs de la table
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passser tous les joueurs de la table et leur envoyer l'�v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Obtenir un num�ro de commande pour le joueur courant, cr�er
      // un InformationDestination et l'ajouter � l'�v�nement
      deplacementWTG.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(), objJoueur.obtenirProtocoleJoueur()));
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(deplacementWTG);
  }

  public void preparerEvenementJoueurDeplacePersonnage( String nomUtilisateur, String collision, 
      Point anciennePosition, Point positionJoueur, int nouveauPointage, int nouvelArgent, String objetUtilise)
  {
      // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs qu'un joueur d�marr� une partie
    
    EvenementJoueurDeplacePersonnage joueurDeplacePersonnage = new EvenementJoueurDeplacePersonnage( nomUtilisateur, 
        anciennePosition, positionJoueur, collision, nouveauPointage, nouvelArgent);
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la table (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la table et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
      
      // Si le nom d'utilisateur du joueur courant n'est pas celui
      // qui vient de d�marrer la partie, alors on peut envoyer un 
      // �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false || objetUtilise.equals("Banane"))
      {
          // Obtenir un num�ro de commande pour le joueur courant, cr�er 
          // un InformationDestination et l'ajouter � l'�v�nement
        joueurDeplacePersonnage.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                                     objJoueur.obtenirProtocoleJoueur()));
      }
    }
    
    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(joueurDeplacePersonnage);
  }
  
  private void preparerEvenementSynchroniser()
  {
    //Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs de la table
      EvenementSynchroniserTemps synchroniser = new EvenementSynchroniserTemps( objMinuterie.obtenirTempsActuel() );
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la table (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la salle et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());

        // Obtenir un num�ro de commande pour le joueur courant, cr�er 
        // un InformationDestination et l'ajouter � l'�v�nement de la 
      // table
      synchroniser.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                      objJoueur.obtenirProtocoleJoueur()));       
    }
    
    // Ajouter les nouveaux �v�nements cr��s dans la liste d'�v�nements 
    // � traiter
    objGestionnaireEvenements.ajouterEvenement(synchroniser);
  }
  
  private void preparerEvenementPartieTerminee(String joueurGagnant)
  {
            // joueurGagnant r�f�re � la personne qui a atteint le WinTheGame (s'il y a lieu)
            
            // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
      // aux joueurs de la table
      EvenementPartieTerminee partieTerminee = new EvenementPartieTerminee(lstJoueurs, lstJoueursVirtuels, joueurGagnant);
      
    // Cr�er un ensemble contenant tous les tuples de la liste 
    // des joueurs de la table (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueurs.entrySet();
    
    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
    
    // Passer tous les joueurs de la salle et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());

        // Obtenir un num�ro de commande pour le joueur courant, cr�er 
        // un InformationDestination et l'ajouter � l'�v�nement de la 
      // table
      partieTerminee.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
                                      objJoueur.obtenirProtocoleJoueur()));       
    }
    
    // Ajouter les nouveaux �v�nements cr��s dans la liste d'�v�nements 
    // � traiter
    objGestionnaireEvenements.ajouterEvenement(partieTerminee);
  }
  
  public void tempsEcoule()
  {
    arreterPartie("");
  }

  public int getObservateurMinuterieId()
  {
    return obtenirNoTable();
  }
  
  public void synchronise()
  {
    synchronized (lstJoueurs)
      {
      preparerEvenementSynchroniser();
      }
  }
  
  public int getObservateurSynchroniserId()
  {
    return obtenirNoTable();
  }
  
  public boolean estArretee()
  {
    return bolEstArretee;
  }
  
  public int obtenirTempsRestant()
  {
      if (objMinuterie == null)
      {
        return intTempsTotal;
      }
      else
      {
        return objMinuterie.obtenirTempsActuel();
      }
    
  }

    /* Cette fonction permet de d�finir le nombre de joueurs virtuels que l'on
     * veut pour cette table
     * @param: nb -> Nouveau nombre de joueurs virtuels
     */ 
  public void setNombreJoueursVirtuels(int nb)
  {
     intNombreJoueursVirtuels = nb;
  }
  
  /* Cette fonction permet d'obtenir le nombre de joueurs virtuels pour 
   * cette table
   */
  public int getNombreJoueursVirtuels()
  {
     return intNombreJoueursVirtuels;
  }
  
  public Vector obtenirListeJoueursVirtuels()
  {
     return lstJoueursVirtuels;
  }
  
  /*
   * Lorsqu'un joueur est d�connect� d'une partie en cours, on appelle
   * cette fonction qui se charge de conserver les r�f�rences vers
   * les informations pour ce joueur
   */
  public void ajouterJoueurDeconnecte(JoueurHumain joueurHumain)
  {
    lstJoueursDeconnectes.add(joueurHumain.obtenirNomUtilisateur().toLowerCase());
  }
  
  public Vector obtenirListeJoueursDeconnectes()
  {
    return lstJoueursDeconnectes;
  }
  
  public IntObj obtenirProchainIdObjet()
  {
    return objProchainIdObjet;
  }
  
  /* 
   * Aller chercher dans la liste des joueurs sur cette table
   * les ID des personnages choisi et v�rifier si le id intID est
   * d�j� choisi
   *
   * Cette fonction v�rifie dans la liste des joueurs et non dans
   * la liste des joueurs en attente
   */
  private boolean idPersonnageEstLibre(int intID)
  {
        // Pr�paration pour parcourir la liste des joueurs
        Set lstEnsembleJoueurs = lstJoueurs.entrySet();
        Iterator objIterateurListeJoueurs = lstEnsembleJoueurs.iterator();

        // Parcourir la liste des joueurs et v�rifier le id
        while(objIterateurListeJoueurs.hasNext() == true)
        {
          // Aller chercher l'objet JoueurHumain
          JoueurHumain objJoueurHumain = (JoueurHumain)(((Map.Entry)(objIterateurListeJoueurs.next())).getValue());
           
          // V�rifier le id
          if (objJoueurHumain.obtenirPartieCourante().obtenirIdPersonnage() == intID)
          {
            // D�j� utilis�
              return false;
          }           
        }
         
        // Si on se rend ici, on a parcouru tous les joueurs et on n'a pas
        // trouv� ce id de personnage, donc le id est libre
        return true;
  }
  
  /* 
   * Aller chercher dans la liste des joueurs en attente
   * les ID des personnages choisi et v�rifier si le id intID est
   * d�j� choisi
   *
   * Cette fonction v�rifie dans la liste des joueurs en attente
   * la liste des joueurs (doit donc �tre utilis� avant que la partie commence)
   *
   */     
  public boolean idPersonnageEstLibreEnAttente(int intID)
  {
    synchronized (lstJoueursEnAttente)
    {
      // Pr�paration pour parcourir la liste des joueurs
      Set lstEnsembleJoueurs = lstJoueursEnAttente.entrySet();
      Iterator objIterateurListeJoueurs = lstEnsembleJoueurs.iterator();
      
      // Parcourir la liste des joueurs et v�rifier le id
      while(objIterateurListeJoueurs.hasNext() == true)
      {
        // Aller chercher l'objet JoueurHumain
        JoueurHumain objJoueurHumain = (JoueurHumain)(((Map.Entry)(objIterateurListeJoueurs.next())).getValue());
        
        // V�rifier le id
        if (objJoueurHumain.obtenirPartieCourante().obtenirIdPersonnage() == intID)
        {
          // D�j� utilis�
          return false;
        }           
      }
      

    }
    
    // Si on se rend ici, on a parcouru tous les joueurs et on n'a pas
    // trouv� ce id de personnage, donc le id est libre
    return true;    
  }
        
  public JoueurHumain obtenirJoueurHumainParSonNom(String username)
  {
            Set nomsJoueursHumains = lstJoueurs.entrySet();
            Iterator objIterateurListeJoueurs = nomsJoueursHumains.iterator();
            while(objIterateurListeJoueurs.hasNext() == true)
            {
                JoueurHumain j = (JoueurHumain)(((Map.Entry)(objIterateurListeJoueurs.next())).getValue());
                if(username.equals(j.obtenirNomUtilisateur())) return j;
            }
            return (JoueurHumain)null;
  }
        
  public JoueurVirtuel obtenirJoueurVirtuelParSonNom(String username)
  {
            for(int i=0; i<lstJoueursVirtuels.size(); i++)
            {
                JoueurVirtuel j = (JoueurVirtuel)lstJoueursVirtuels.get(i);
                if(username.equals(j.obtenirNom())) return j;
            }
            return (JoueurVirtuel)null;
  }
        
        // Cette m�thode permettra de dire si un joueur a gagn� la partie en
        // ayant accumul� assez de points et en ayant rejoint le WinTheGame
        public boolean aRejointLeWinTheGame(int pointageDuJoueur, Point positionDuJoueur)
        {
            return (peutAllerSurLeWinTheGame(pointageDuJoueur) && (positionDuJoueur.equals(positionWinTheGame)));
        }
        
        public Point obtenirPositionWinTheGame()
        {
            return positionWinTheGame;
        }
        
        public String obtenirButDuJeu()
        {
            return butDuJeu;
        }
        
        public boolean peutAllerSurLeWinTheGame(int pointage)
        {
            if(butDuJeu.equals("winTheGameWithoutScore")) return true;
            else return pointage >= pointageRequisPourAllerSurLeWinTheGame();
        }
        
        public int pointageRequisPourAllerSurLeWinTheGame()
        {
            if(this.butDuJeu=="winTheGameWithoutScore") return 0;
            else return intTempsTotal*5;
        }
        
        public void definirNouvellePositionWinTheGame()
        {
            Random objRandom = new Random();
            boolean pasTrouve = true;
            int grandeurDeplacement = 3;
            int nbEssaisI = 0;
            int nbEssaisJ = 0;
            int maxEssais = 9000;
            
            // On obtient les positions des 4 joueurs afin de ne pas d�placer le WinTheGame
            // sur un joueur, ou encore sur une case o� un joueur voulait aller
            Point positionsJoueurs[] = new Point[4];
            Point positionsJoueursDesirees[] = new Point[4];
            {
                for(int k=0; k<4; k++)
                {
                    positionsJoueurs[k] = new Point(0, 0);
                    positionsJoueursDesirees[k] = new Point(0, 0);
                }
                int i=0;
                {
                    Set nomsJoueursHumains = lstJoueurs.entrySet();
                    Iterator objIterateurListeJoueurs = nomsJoueursHumains.iterator();
                    while(objIterateurListeJoueurs.hasNext() == true)
                    {
                        JoueurHumain j = (JoueurHumain)(((Map.Entry)(objIterateurListeJoueurs.next())).getValue());
                        positionsJoueurs[i] = j.obtenirPartieCourante().obtenirPositionJoueur();
                        positionsJoueursDesirees[i] = j.obtenirPartieCourante().obtenirPositionJoueurDesiree();
                        if(positionsJoueursDesirees[i] == null) positionsJoueursDesirees[i] = positionsJoueurs[i];
                        i++;
                    }
                }
                {   
                    for(int k=0; k<lstJoueursVirtuels.size(); k++)
                    {
                        JoueurVirtuel j = (JoueurVirtuel)lstJoueursVirtuels.get(k);
                        positionsJoueurs[i] = j.obtenirPositionJoueur();
                        positionsJoueursDesirees[i] = positionsJoueurs[i];
                        i++;
                    }
                }
            }
            
            // On commence par regarder si les cases pas trop loin sont OK si on n'est pas en -1 -1
            if(positionWinTheGame.x != -1 && positionWinTheGame.y != -1)
            {
                for(int i=positionWinTheGame.x-(objRandom.nextInt(grandeurDeplacement+1)-grandeurDeplacement/2); pasTrouve && nbEssaisI<maxEssais; i = positionWinTheGame.x-(objRandom.nextInt(grandeurDeplacement+1)-grandeurDeplacement/2))
                {
                    //System.out.println("i: " + Integer.toString(i));
                    nbEssaisJ = 0;
                    objRandom.setSeed(System.currentTimeMillis());
                    if(i>=0 && i<objttPlateauJeu.length) for(int j=positionWinTheGame.y-(objRandom.nextInt(grandeurDeplacement+1)-grandeurDeplacement/2); pasTrouve && i >= 0 && nbEssaisJ < maxEssais; j = positionWinTheGame.y-(objRandom.nextInt(grandeurDeplacement+1)-grandeurDeplacement/2))
                    {
                        //System.out.println("   j: " + Integer.toString(j));
                        objRandom.setSeed(System.currentTimeMillis());
                        // Est-ce que la case existe? Est-ce que c'est une case couleur?
                        if(j>=0 && j<objttPlateauJeu[i].length) if(j >= 0 && objttPlateauJeu[i][j] != null && objttPlateauJeu[i][j] instanceof CaseCouleur)
                        {
                            CaseCouleur caseTemp = (CaseCouleur)objttPlateauJeu[i][j];
                            // Est-ce qu'il n'y a rien dessus?
                            if(caseTemp.obtenirObjetArme() == null && caseTemp.obtenirObjetCase() == null)
                            {
                                // Est-ce que c'est la m�me case? Est-ce dans les limites?
                                if(i != positionWinTheGame.x && j != positionWinTheGame.y && i >= 0 && j >= 0 && i < objttPlateauJeu.length && j < objttPlateauJeu[i].length)
                                {
                                    // Est-ce qu'un joueur est sur cette case ou veut s'y d�placer?
                                    boolean presence = false;
                                    for(int k=0; k<4 && !presence; k++) if((new Point(i, j)).equals(positionsJoueurs[k]) || (new Point(i, j)).equals(positionsJoueursDesirees[k])) presence = true;
                                    if(!presence)
                                    {
                                        // Tout est OK, on d�place le WinTheGame
                                        pasTrouve = false;
                                        positionWinTheGame.move(i, j);
                                    }
                                }
                            }
                        }
                        nbEssaisJ++;
                    }
                    nbEssaisI++;
                }
            }
            
            // Sinon, on prend la case qui minimise la distance maximale � chacun des joueurs
            if(pasTrouve)
            {
                int meilleureDistMax = 666;
                int meilleurI = positionWinTheGame.x;
                int meilleurJ = positionWinTheGame.y;
                for(int i=0; i < objttPlateauJeu.length; i++)
                {
                    for(int j=0; j < objttPlateauJeu[i].length; j++)
                    {
                        // Est-ce que la case existe? Est-ce que c'est une case couleur?
                        if(objttPlateauJeu[i][j] != null && objttPlateauJeu[i][j] instanceof CaseCouleur)
                        {
                            CaseCouleur caseTemp = (CaseCouleur)objttPlateauJeu[i][j];
                            // Est-ce qu'il n'y a rien dessus?
                            if(caseTemp.obtenirObjetArme() == null && caseTemp.obtenirObjetCase() == null)
                            {
                                // Est-ce qu'un joueur est sur cette case ou veut s'y d�placer?
                                boolean presence = false;
                                for(int k=0; k<4 && !presence; k++) if((new Point(i, j)).equals(positionsJoueurs[k]) || (new Point(i, j)).equals(positionsJoueursDesirees[k])) presence = true;
                                if(!presence)
                                {
                                    // On regarde la distance maximale aux joueurs
                                    int[] distances = {0,0,0,0};
                                    for(int z=0; z<positionsJoueurs.length; z++) distances[z] = Math.abs(i - positionsJoueurs[z].x) + Math.abs(j - positionsJoueurs[z].y);
                                    int distMax = Math.max(Math.max(distances[0], distances[1]), Math.max(distances[2], distances[3]));
                                    if(distMax <= meilleureDistMax)
                                    {
                                        meilleurI = i;
                                        meilleurJ = j;
                                        meilleureDistMax = distMax;
                                    }
                                }
                            }
                        }
                    }
                }
                // Tout est OK, on d�place le WinTheGame
                positionWinTheGame.move(meilleurI, meilleurJ);
            }
        }
}
