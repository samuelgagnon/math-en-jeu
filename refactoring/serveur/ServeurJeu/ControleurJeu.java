package ServeurJeu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ClassesUtilitaires.Espion;
import Enumerations.RetourFonctions.ResultatAuthentification;
import ServeurJeu.BD.GestionnaireBD;
import ServeurJeu.Communications.GestionnaireCommunication;
import ServeurJeu.Communications.ProtocoleJoueur;
import ServeurJeu.ComposantesJeu.Langue;
import ServeurJeu.ComposantesJeu.Salle;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.ParametreIA;
import ServeurJeu.Configuration.GestionnaireConfiguration;
import ServeurJeu.Configuration.GestionnaireMessages;
import ServeurJeu.Evenements.EvenementJoueurConnecte;
import ServeurJeu.Evenements.EvenementJoueurDeconnecte;
import ServeurJeu.Evenements.GestionnaireEvenements;
import ServeurJeu.Evenements.InformationDestination;
import ServeurJeu.Monitoring.TacheLogMoniteur;
import ServeurJeu.Temps.GestionnaireTemps;
import ServeurJeu.Temps.TacheSynchroniser;



//TODO: Si un jour on doit modifier le nom d'utilisateur d'un joueur pendant 
//le jeu, il va falloir ajouter des synchronisation � chaque fois qu'on 
//fait des v�rifications avec le nom de l'utilisateur.
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
 */
public class ControleurJeu 
{

  /**
   * Static private class used for singleton in multi-threading
   * @author Maxime
   *
   */
  private static class ControlleurJeuHolder {
    private final static ControleurJeu mControleurJeu = new ControleurJeu();
  }


  // Cette modeDebug est vraie, toute reponse des joueurs sera bonne, et
  // on affichera dans la console des informations sur les communications
  public static boolean modeDebug;

  static private Logger objLogger = Logger.getLogger( ControleurJeu.class );


  // Cet objet permet de g�rer tous les �v�nements devant �tre envoy�s du
  // serveur aux clients (l'�v�nement ping n'est pas g�r� par ce gestionnaire)
  private GestionnaireEvenements objGestionnaireEvenements;


  // Cet objet est une liste des joueurs qui sont connect�s au serveur de jeu 
  // (cela inclus les joueurs dans les salles ainsi que les joueurs jouant
  // pr�sentement dans des tables de jeu)
  private TreeMap lstJoueursConnectes;


  // D�claration d'une variable pour contenir une liste des joueurs
  // qui ont �t�s d�connect�s et qui �taient en train de joueur une partie
  private TreeMap lstJoueursDeconnectes;

  // Cet objet est une liste des salles cr��es qui se trouvent dans le serveur
  // de jeu. Chaque �l�ment de cette liste a comme cl� le nom de la salle
  private TreeMap<String, Salle> lstSalles;

  // D�claration de l'objet Espion qui va inscrire des informations� proppos
  // du serveur en parall�te
  private Espion objEspion;

  // D�claration d'un objet random pour g�n�rer des nombres al�atoires
  private Random objRandom;

  // D�claration d'un objet pour conserver tous les param�tres
  // pour les joueurs virtuels
  private ParametreIA objParametreIA;

  /** The database connection */ 
  private static Connection mConnection;


  /**
   * Return the instance of this class
   * @return the only instance of this class
   */
  public static ControleurJeu getInstance() {
    return ControlleurJeuHolder.mControleurJeu;
  }


  /**
   * Constructeur de la classe ControleurJeu qui permet de cr�er le gestionnaire 
   * des communications, le gestionnaire d'�v�nements et le gestionnaire de bases 
   * de donn�es. 
   */
  private ControleurJeu() 
  {
    super();

    GestionnaireConfiguration lConfig = GestionnaireConfiguration.getInstance();

    modeDebug = lConfig.obtenirValeurBooleenne("controleurjeu.debug");

    // Initialiser la classe statique GestionnaireMessages
    GestionnaireMessages.initialiser();

    objLogger.info(GestionnaireMessages.message("controleur_jeu.serveur_demarre"));

    // Pr�parer l'objet pour cr�er les nombres al�atoires
    objRandom = new Random();

    // Cr�er une liste des joueurs
    lstJoueursConnectes = new TreeMap();

    // Cr�er une liste des joueurs d�connect�s
    lstJoueursDeconnectes = new TreeMap();

    // Cr�er une liste des salles
    lstSalles = new TreeMap();

    // Cr�er un nouveau gestionnaire d'�v�nements
    objGestionnaireEvenements = new GestionnaireEvenements();

    //create the database connection
    createDbConnexion();

    //GestionnaireBD lBd = new GestionnaireBD(mConnection);
    //lBd.loadRooms(lConfig.obtenirString(""));

  }

  /**
   * Create a connexion to the database
   *
   */
  public void createDbConnexion() {
    GestionnaireConfiguration config = GestionnaireConfiguration.getInstance();
    String driver = config.obtenirString( "gestionnairebd.jdbc-driver" );
    String hote = config.obtenirString( "gestionnairebd.hote" );
    String utilisateur = config.obtenirString( "gestionnairebd.utilisateur" );
    String motDePasse = config.obtenirString( "gestionnairebd.mot-de-passe" );

    // Garder la r�f�rence vers le contr�leur de jeu
    //objControleurJeu = controleur;

    BasicDataSource lDataSource = new BasicDataSource();
    lDataSource.setDriverClassName(driver);
    lDataSource.setUsername(utilisateur);
    lDataSource.setPassword(motDePasse);
    lDataSource.setUrl(hote);

    try {
      mConnection = lDataSource.getConnection();
      objLogger.log(Level.INFO, "Database connection created.");
    } catch (SQLException e) {
      objLogger.log(Level.FATAL, e.getMessage(), e);
    }
  }

  public void demarrer()
  {
    GestionnaireConfiguration config = GestionnaireConfiguration.getInstance();

    int intStepSynchro = config.obtenirNombreEntier( "controleurjeu.synchro.step" );
    GestionnaireTemps.getInstance().ajouterTache( TacheSynchroniser.getInstance(), intStepSynchro );

    //GestionnaireBD lBD = new GestionnaireBD(mConnection);
    //lBD.loadRooms(config.obtenirString("controleurjeu.gametype"));

    // Cr�er un thread pour le GestionnaireEvenements
    Thread threadEvenements = new Thread(objGestionnaireEvenements);

    // D�marrer le thread du gestionnaire d'�v�nements
    threadEvenements.start();

    // D�marrer l'espion qui �crit dans un fichier p�riodiquement les
    // informations du serveur
    String fichier = config.obtenirString( "controleurjeu.info.fichier-sortie" );
    int delai = config.obtenirNombreEntier( "controleurjeu.info.delai" );
    objEspion = new Espion(this, fichier, delai, ClassesUtilitaires.Espion.MODE_FICHIER_TEXTE);

    // D�marrer la thread de l'espion
    //Thread threadEspion = new Thread(objEspion);
    //threadEspion.start();

    // Cr�er une instance de la classe regroupant tous les param�tres
    // des joueurs virtuels
    objParametreIA = new ParametreIA();

    //Demarrer une tache de monitoring
    TacheLogMoniteur objTacheLogMoniteur = new TacheLogMoniteur();
    int intStepMonitor = config.obtenirNombreEntier( "controleurjeu.monitoring.step" );
    GestionnaireTemps.getInstance().ajouterTache( objTacheLogMoniteur, intStepMonitor );

    //D�marrer l'�coute des connexions clientes
    //Cette methode est la loop de l'application
    //Au retour, l'application se termine
    GestionnaireCommunication.getInstance().ecouterConnexions();
    //objGestionnaireCommunication.ecouterConnexions();
    System.out.println( "arret" );
  }

  public void arreter()
  {
    System.out.println( "Le serveur arrete..." );
    GestionnaireCommunication.getInstance().arreter();
    //objGestionnaireCommunication.arreter();
  }

  /**
   * Cette fonction permet de d�terminer si le joueur dont le nom d'utilisateur
   * est pass� en param�tre est d�j� connect� au serveur de jeu ou non.
   * 
   * @param String nomUtilisateur : Le nom d'utilisateur du joueur
   * @return false : Le joueur n'est pas connect� au serveur de jeu 
   *       true  : Le joueur est d�j� connect� au serveur de jeu
   * @synchronism Cette fonction est synchronis�e sur la liste des 
   *        joueurs connect�s. 
   */
  public boolean joueurEstConnecte(String nomUtilisateur)
  {
    // Synchroniser l'acc�s � la liste des joueurs connect�s
    synchronized (lstJoueursConnectes)
    {
      // Retourner si le joueur est d�j� connect� au serveur de jeu ou non
      return lstJoueursConnectes.containsKey(nomUtilisateur.toLowerCase());         
    }
  }

  /**
   * Cette fonction permet de valider que les informations du joueur pass�es
   * en param�tres sont correctes (elles existent et concordent). On suppose
   * que le joueur n'est pas connect� au serveur de jeu.
   * 
   * @param ProtocoleJoueur protocole : Le protocole du joueur
   * @param String nomUtilisateur : Le nom d'utilisateur du joueur
   * @param String motDePasse : Le mot de passe du joueur
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *              g�n�rer un num�ro de commande pour le retour de
   *              l'appel de fonction
   * @return JoueurNonConnu : Le nom d'utilisateur du joueur n'est pas connu par le 
   *                    serveur ou le mot de passe ne concorde pas au nom 
   *                    d'utilisateur donn�
   *       JoueurDejaConnecte : Le joueur a tent� de se connecter en m�me temps 
   *                � deux endroits diff�rents  
   *       Succes : L'authentification a r�ussie
   * @synchronism  Cette fonction est synchronis�e par rapport � la liste des
   *         joueurs connect�s car on fait un synchronized sur elle, 
   *         elle est synchronis� par rapport au joueur du protocole car 
   *         les seules fonctions qui acc�dent au protocole sont le 
   *         VerificateurConnexions (fait juste un acc�s au protocole et 
   *         non un acc�s au joueur du protocole donc c'est correct), le 
   *         protocole lui-m�me (le protocole ne traite qu'une commande 
   *         � la fois, donc on se fou que lui utilise son joueur) et la 
   *         fonction deconnecterJoueur (elle ne peut pas �tre ex�cut�e 
   *         en m�me temps que l'authentification car le protocole ne 
   *         traite qu'une commande � la fois, m�me si la demande vient 
   *         du VerificateurConnexions).
   */
  public String authentifierJoueur(ProtocoleJoueur protocole, String nomUtilisateur, 
      String motDePasse, boolean doitGenererNoCommandeRetour)
  {
    // D�claration d'une variable qui va contenir le r�sultat � retourner
    // � la fonction appelante, soit les valeurs de l'�num�ration 
    // ResultatAuthentification
    String strResultatAuthentification = ResultatAuthentification.JoueurNonConnu;

    GestionnaireBD lBD = new GestionnaireBD(mConnection);
    boolean bolResultatRecherche = lBD.joueurExiste(nomUtilisateur, motDePasse);
    // D�terminer si le joueur dont le nom d'utilisateur est pass� en 
    // param�tres existe et mettre le r�sultat dans une variable bool�enne
    //boolean bolResultatRecherche = GestionnaireBD.getInstance().joueurExiste(nomUtilisateur, motDePasse);//objGestionnaireBD.joueurExiste(nomUtilisateur, motDePasse); 

    // Si les informations de l'utilisateur sont correctes, alors le 
    // joueur est maintenant connect� au serveur de jeu
    if (bolResultatRecherche == true)
    {
      // Cr�er un nouveau joueur humain contenant les bonnes informations
      JoueurHumain objJoueurHumain = new JoueurHumain(protocole, nomUtilisateur, 
          protocole.obtenirAdresseIP(),
          protocole.obtenirPort());

      // Trouver les informations sur le joueur dans la BD et remplir le 
      // reste des champs tels que les droits
      lBD.remplirInformationsJoueur(objJoueurHumain);

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
      synchronized (lstJoueursConnectes)
      {
        // Si le joueur est d�j� pr�sentement connect�, on ne peut
        // pas finaliser la connexion du joueur
        if (joueurEstConnecte(nomUtilisateur) == true)
        {
          // On va retourner que le joueur est d�j� connect�
          strResultatAuthentification = ResultatAuthentification.JoueurDejaConnecte;
        }
        else
        {
          // D�finir la r�f�rence vers le joueur humain
          protocole.definirJoueur(objJoueurHumain);

          // Ajouter ce nouveau joueur dans la liste des joueurs connect�s
          // au serveur de jeu
          lstJoueursConnectes.put(nomUtilisateur.toLowerCase(), objJoueurHumain);

          // Si on doit g�n�rer le num�ro de commande de retour, alors
          // on le g�n�re, sinon on ne fait rien (�a devrait toujours
          // �tre vrai, donc on le g�n�re tout le temps)
          if (doitGenererNoCommandeRetour == true)
          {
            // G�n�rer un nouveau num�ro de commande qui sera 
            // retourn� au client
            protocole.genererNumeroReponse();             
          }

          // L'authentification a r�ussie
          strResultatAuthentification = ResultatAuthentification.Succes;

          // Pr�parer l'�v�nement de nouveau joueur. Cette fonction 
          // va passer les joueurs et cr�er un InformationDestination 
          // pour chacun et ajouter l'�v�nement dans la file de gestion 
          // d'�v�nements
          preparerEvenementJoueurConnecte(nomUtilisateur);
        }
      }
    }

    return strResultatAuthentification;
  }

  /**
   * Cette m�thode permet de d�connecter le joueur pass� en param�tres. Il 
   * faut enlever toute trace du joueur du serveur de jeu et en aviser les
   * autres participants se trouvant au m�me endroit que le joueur d�connect� 
   * (� une table de jeu).
   * 
   * @param JoueurHumain joueur : Le joueur humain ayant fait la demande 
   *                de d�connexion
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *                g�n�rer un num�ro de commande pour le retour de
   *                l'appel de fonction
   * @synchronism � ce niveau-ci, il n'y a pas vraiment de restrictions sur
   *        l'ordre d'arriv�e des �v�nements indiquant que le joueur
   *        a quitt� la table ou la salle. De plus, aucune autre 
   *        fonction ne peut modifier le joueur, puisque deux 
   *        fonctions d'un m�me protocole ne peuvent pas �tre 
   *        ex�cut�es en m�me temps. Cependant, pour enlever un
   *        joueur de la liste des joueurs connect�s, il faut
   *        s'assurer que personne d'autre ne va toucher � la liste
   *        des joueurs connect�s.
   */
  public void deconnecterJoueur(JoueurHumain joueur, boolean doitGenererNoCommandeRetour, boolean ajouterJoueurDeconnecte)
  {

    // Si d�connection pendant une partie, ajouterJoueurDeconnecte = true
    // On va donc ajouter ce joueur � la liste des joueurs
    // d�connect�s pour cette table et pour le contr�leur du jeu
    if (ajouterJoueurDeconnecte == true && joueur != null &&
        joueur.obtenirPartieCourante() != null &&
        joueur.obtenirPartieCourante().obtenirTable() != null &&
        joueur.obtenirPartieCourante().obtenirTable().estCommencee() == true &&
        joueur.obtenirPartieCourante().obtenirTable().estArretee() == false)
    {

      // Ajouter ce joueur � la liste des joueurs d�connect�s pour cette
      // table
      joueur.obtenirPartieCourante().obtenirTable().ajouterJoueurDeconnecte(joueur);

      // Ajouter ce joueur � la liste des joueurs d�connect�s du serveur
      ajouterJoueurDeconnecte(joueur);
    }

    // Si le joueur courant est dans une salle, alors on doit le retirer de
    // cette salle (pas besoin de faire la synchronisation sur la salle 
    // courante du joueur car elle ne peut �tre modifi�e par aucun autre
    // thread que celui courant)
    if (joueur.obtenirSalleCourante() != null)
    {
      // Le joueur courant quitte la salle dans laquelle il se trouve
      joueur.obtenirSalleCourante().quitterSalle(joueur, false, !ajouterJoueurDeconnecte);
    }


    // Emp�cher d'autres thread de venir utiliser la liste des joueurs
    // connect�s au serveur de jeu pendant qu'on d�connecte le joueur
    synchronized (lstJoueursConnectes)
    {
      // Enlever le joueur de la liste des joueurs connect�s
      lstJoueursConnectes.remove(joueur.obtenirNomUtilisateur().toLowerCase());

      // Enlever la r�f�rence du protocole du joueur vers son joueur humain 
      // (cela va avoir pour effet que le protocole du joueur va penser que
      // le joueur n'est plus connect� au serveur de jeu)
      joueur.obtenirProtocoleJoueur().definirJoueur(null);

      // Si on doit g�n�rer le num�ro de commande de retour, alors
      // on le g�n�re, sinon on ne fait rien
      if (doitGenererNoCommandeRetour == true)
      {
        // G�n�rer un nouveau num�ro de commande qui sera 
        // retourn� au client
        joueur.obtenirProtocoleJoueur().genererNumeroReponse();             
      }

      // Aviser tous les joueurs connect�s au serveur de jeu qu'un joueur
      // s'est d�connect�
      preparerEvenementJoueurDeconnecte(joueur.obtenirNomUtilisateur());        
    }
  }

  /**
   * Cette fonction permet d'obtenir la liste des joueurs connect�s au serveur
   * de jeu. La vraie liste est retourn�e.
   * 
   * @return TreeMap : La liste des joueurs connect�s au serveur de jeu 
   *                   (c'est la r�f�rence vers la liste du ControleurJeu, il 
   *                   faut donc traiter le cas du multithreading)
   * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle doit
   *        l'�tre par l'appelant de cette fonction tout d�pendant
   *        du traitement qu'elle doit faire
   */
  public TreeMap obtenirListeJoueurs()
  {
    return lstJoueursConnectes;
  }


  public TreeMap getRooms() {
    GestionnaireConfiguration config = GestionnaireConfiguration.getInstance();
    GestionnaireBD lBD = new GestionnaireBD(mConnection);
    lBD.loadRooms(config.obtenirString("controleurjeu.gametype"));
    return (TreeMap)lstSalles.clone();
  }
  
  
  public TreeMap<String, Salle> getRooms(Langue lLangue) {
    TreeMap<String, Salle> lResult = new TreeMap<String, Salle>();
    
    GestionnaireConfiguration config = GestionnaireConfiguration.getInstance();
    GestionnaireBD lBD = new GestionnaireBD(mConnection);
    lBD.loadRooms(config.obtenirString("controleurjeu.gametype"));
    

    Iterator<Salle> lIter = lstSalles.values().iterator();
    while(lIter.hasNext()) {
      Salle s = (Salle)lIter.next();
      
      lResult.put(String.valueOf(s.getId()), s);
    }
    
    return lResult;
    
  }


  /**
   * Cette fonction permet de d�terminer si la salle dont le nom est pass�
   * en param�tres existe d�j� ou non.
   * 
   * @param String nomSalle : Le nom de la salle
   * @return false : La salle n'existe pas 
   *       true  : La salle existe d�j�
   * @synchronism Cette fonction n'a pas besoin d'�tre synchronis�e car
   *        on ne peut pas ajouter ou enlever des salles par le
   *        serveur de jeu (sauf quand celui-ci d�marre, mais aucun
   *        joueur n'est connect� � ce moment-l�)
   */
  public boolean salleExiste(String nomSalle)
  {
    boolean lResult = false;
    // Retourner si la salle existe d�j� ou non
    Collection<Salle> lSalles = lstSalles.values();
    Iterator<Salle> lIter = lSalles.iterator();
    while (lIter.hasNext()) {
      Salle lSalle = lIter.next();
      if (lSalle.getNames().containsValue(nomSalle)) {
        lResult = true;
        break;
      }
    }
    return lResult;         
  }
  
  
  public Salle getRoomByName(String pName) {
    Salle lResult = null;
    Collection<Salle> lSalles = lstSalles.values();
    Iterator<Salle> lIter = lSalles.iterator();
    while (lIter.hasNext()) {
      Salle lSalle = lIter.next();
      if (lSalle.getNames().containsValue(pName)) {
        lResult = lSalle;
        break;
      }
    }
    return lResult; 
  }
  

  /**
   * Cette m�thode permet d'ajouter une nouvelle salle dans la liste des 
   * salles du contr�leur de jeu.
   * 
   * @param Salle nouvelleSalle : La nouvelle salle � ajouter dans la liste
   * @synchronism we need to synchronize the list of rooms.
   */
  public void ajouterNouvelleSalle(Salle nouvelleSalle)
  {
    // Ajouter la nouvelle salle dans la liste des salles du 
    // contr�leur de jeu
    synchronized (lstSalles) {
      lstSalles.put(String.valueOf(nouvelleSalle.getId()), nouvelleSalle); 
    }

  }

  /**
   * Cette fonction permet de valider que le mot de passe pour entrer dans la
   * salle est correct. On suppose suppose que le joueur n'est pas dans aucune
   * salle. Cette fonction va avoir pour effet de connecter le joueur dans la
   * salle dont le nom est pass� en param�tres.
   * 
   * @param JoueurHumain joueur : Le joueur demandant d'entrer dans la salle
   * @param String nomSalle : Le nom de la salle dans laquelle entrer
   * @param String motDePasse : Le mot de passe pour entrer dans la salle
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *                g�n�rer un num�ro de commande pour le retour de
   *                l'appel de fonction
   * @return false : Le mot de passe pour entrer dans la salle n'est pas
   *           le bon
   *       true  : Le joueur a r�ussi � entrer dans la salle
   * @synchronism Cette fonction n'a pas besoin d'�tre synchronis�e, car 
   *        elle ne modifie pas la liste des salles et aucune autre
   *        fonction ne le fait. Cependant, la m�thode entrerSalle
   *        de la salle devra �tre synchronis�e.
   */
  public boolean entrerSalle(JoueurHumain joueur, String nomSalle, 
      String motDePasse, boolean doitGenererNoCommandeRetour)
  {
    // On retourne le r�sultat de l'entr�e du joueur dans la salle
    return getRoomByName(nomSalle).entrerSalle(joueur, motDePasse, doitGenererNoCommandeRetour);
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
   *                  vient de se connecter au serveur de jeu
   * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle l'est
   *        par l'appelant (authentifierJoueur).
   */
  private void preparerEvenementJoueurConnecte(String nomUtilisateur)
  {
    // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
    // aux joueurs qu'un nouveau joueur s'est connect�
    EvenementJoueurConnecte joueurConnecte = new EvenementJoueurConnecte(nomUtilisateur);

    // Cr�er un ensemble contenant tous les tuples de la liste 
    // lstJoueursConnectes (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueursConnectes.entrySet();

    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();

    // Passer tous les joueurs connect�s et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());

      // Si le nom d'utilisateur du joueur courant n'est pas celui
      // qui vient de se connecter au serveur de jeu, alors on peut
      // envoyer un �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
        // Obtenir un num�ro de commande pour le joueur courant, cr�er 
        // un InformationDestination et l'ajouter � l'�v�nement
        joueurConnecte.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
            objJoueur.obtenirProtocoleJoueur()));
      }
    }

    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(joueurConnecte);
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
   *                  vient de se d�connecter du serveur de jeu
   * @synchronism Cette fonction n'est pas synchronis�e ici, mais elle l'est
   *        par l'appelant (deconnecterJoueur).
   */
  private void preparerEvenementJoueurDeconnecte(String nomUtilisateur)
  {
    // Cr�er un nouvel �v�nement qui va permettre d'envoyer l'�v�nement 
    // aux joueurs qu'un joueur s'est d�connect�
    EvenementJoueurDeconnecte joueurDeconnecte = new EvenementJoueurDeconnecte(nomUtilisateur);

    // Cr�er un ensemble contenant tous les tuples de la liste 
    // lstJoueursConnectes (chaque �l�ment est un Map.Entry)
    Set lstEnsembleJoueurs = lstJoueursConnectes.entrySet();

    // Obtenir un it�rateur pour l'ensemble contenant les joueurs
    Iterator objIterateurListe = lstEnsembleJoueurs.iterator();

    // Passer tous les joueurs connect�s et leur envoyer un �v�nement
    while (objIterateurListe.hasNext() == true)
    {
      // Cr�er une r�f�rence vers le joueur humain courant dans la liste
      JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());

      // Si le nom d'utilisateur du joueur courant n'est pas celui
      // qui vient de se d�connecter du serveur de jeu, alors on peut
      // envoyer un �v�nement � cet utilisateur
      if (objJoueur.obtenirNomUtilisateur().equals(nomUtilisateur) == false)
      {
        // Obtenir un num�ro de commande pour le joueur courant, cr�er 
        // un InformationDestination et l'ajouter � l'�v�nement
        joueurDeconnecte.ajouterInformationDestination(new InformationDestination(objJoueur.obtenirProtocoleJoueur().obtenirNumeroCommande(),
            objJoueur.obtenirProtocoleJoueur()));
      }
    }

    // Ajouter le nouvel �v�nement cr�� dans la liste d'�v�nements � traiter
    objGestionnaireEvenements.ajouterEvenement(joueurDeconnecte);
  }

  public GestionnaireEvenements obtenirGestionnaireEvenements()
  {
    return objGestionnaireEvenements;
  }


  /*
   * Cette fonction ajouter un joueur � la liste des joueurs d�connect�s. Si le
   * joueur tente de se reconnecter, il sera possible qu'il reprenne la partie
   */
  public void ajouterJoueurDeconnecte(JoueurHumain joueurHumain)
  {

    synchronized(lstJoueursDeconnectes)
    {
      lstJoueursDeconnectes.put(joueurHumain.obtenirNomUtilisateur().toLowerCase(), joueurHumain);
    }  
  }

  /*
   * Cette fonction va nous permettre de savoir si ce joueur a �t�
   * d�connect� pendant une partie.
   */
  public boolean estJoueurDeconnecte(String nomUtilisateur)
  {
    synchronized(lstJoueursDeconnectes)
    {
      return lstJoueursDeconnectes.containsKey(nomUtilisateur.toLowerCase()); 
    }

  }

  /*
   * Cette fonction retourne une r�f�rence vers un objet JoueurHumain
   * d'un joueur d�connect�. Cet objet contient toutes les informations
   * � propos de la partie qui �tait en cours
   */
  public JoueurHumain obtenirJoueurHumainJoueurDeconnecte(String nomUtilisateur)
  {
    synchronized(lstJoueursDeconnectes)
    {
      return (JoueurHumain) lstJoueursDeconnectes.get(nomUtilisateur);
    }
  }

  /*
   * Cette fonction permet d'enlever un joueur d�connect� de la liste
   * des joueurs d�connect�s, soit parce qu'il vient de se reconnecter,
   * ou car la partie qu'il avait commenc�e et qui �tait en suspend est termin�e
   */
  public void enleverJoueurDeconnecte(String nomUtilisateur)
  {
    synchronized(lstJoueursDeconnectes)
    {
      lstJoueursDeconnectes.remove(nomUtilisateur.toLowerCase());
    }
  }

  public TreeMap obtenirListeJoueursDeconnectes()
  {
    return lstJoueursDeconnectes;
  }


  public int genererNbAleatoire(int max)
  {
    return objRandom.nextInt(max);
  }

  public ParametreIA obtenirParametreIA()
  {
    return objParametreIA;
  }

  /**
   * Return the connection object
   * @return the database connection object
   */
  public Connection getConnection() {
    try {
      if (mConnection.isClosed() || mConnection == null) {
        createDbConnexion();
      }
    } catch (SQLException e) {
      objLogger.log(Level.FATAL, e.getMessage(), e);
    }
    return mConnection;
  }

  /**
   * Close the database connection object
   *
   */
  public void closeDbConnection() {
    try {
      mConnection.close();
    } catch (SQLException e) {
      objLogger.log(Level.FATAL, e.getMessage(), e);
    }
  }

}
