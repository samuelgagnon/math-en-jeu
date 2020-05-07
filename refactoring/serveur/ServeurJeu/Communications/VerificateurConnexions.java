package ServeurJeu.Communications;

import java.util.Vector;

import sun.security.jca.GetInstance;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class VerificateurConnexions implements Runnable
{
  
  private VerificateurConnexions() {
    //super();
    lstClientsPresents = new Vector();
    
  }
  
  private static class VerificateurConnexionsHolder {
    private final static VerificateurConnexions INSTANCE = new VerificateurConnexions();
  }
  
  // D�claration d'une r�f�rence vers le gestionnaire des communications
  @Deprecated
  private GestionnaireCommunication objGestionnaireCommunication;
  
  // Cette variable permet de savoir s'il faut arr�ter le thread ou non
  private boolean bolStopThread = false;
  
  // D�claration d'une variable qui va compter le nombre de fois que 
  // des messages ping ont �t� envoy�s (une fois rendu � 10, �a va 
  // retourner � 0)
  private int intCompteurPing = 0;
  
  // D�claration d'une liste de ProtocoleJoueur pour les clients qui 
  // ont r�pondu au ping
  private Vector lstClientsPresents;
  
  

  
  public static VerificateurConnexions getInstance() {
    return VerificateurConnexionsHolder.INSTANCE;
  }
  
  /**
   * Constructeur de la classe VerificateurConnexions qui permet de garder une 
   * r�f�rence vers le gestionnaire de communication.
   * 
   * @param GestionnaireCommunication communication : Le gestionnaire des 
   *                          communications
   */
  /*
  @Deprecated
  public VerificateurConnexions(GestionnaireCommunication communication) 
  {
    super();
  
    // Faire la r�f�rence vers le gestionnaire de communications
    objGestionnaireCommunication = communication;
    
    // Cr�er un nouveau vecteur
    lstClientsPresents = new Vector();
  }
  */
  
  
  /**
   * Cette m�thode est appel�e automatiquement par le thread du joueur et elle
   * permet de v�rifier toutes les connexion avec le serveur pour savoir si 
   * elles sont encore actives.
   */
  public void run()
  {
    GestionnaireCommunication objGestionnaireCommunication = GestionnaireCommunication.getInstance();
    
    // Boucler tant qu'il ne faut pas arr�ter le thread
    while (bolStopThread == false)
    {
      // D�claration d'une liste de ProtocoleJoueur qui contient la 
      // r�f�rence vers la liste des ProtocoleJoueur du gestionnaire de 
      // communication
      Vector lstProtocoleJoueur = objGestionnaireCommunication.obtenirListeProtocoleJoueur();
      
      // D�claration d'une liste de ProtocoleJoueur qui va contenir
      // une copie de la liste des ProtocoleJoueur du gestionnaire de 
      // communication
      Vector lstCopieProtocoleJoueur = null;
      
      // Emp�cher d'autres threads de toucher � la liste des protocoles 
      // de joueur
      synchronized (lstProtocoleJoueur)
      {
        // Faire une copie de la liste des ProtocoleJoueur
        lstCopieProtocoleJoueur = (Vector) lstProtocoleJoueur.clone();
      }

      // Passer tous les objets ProtocoleJoueur et envoyer un message ping
      // � chacun pour savoir s'il est l�
      for (int i = 0; i < lstCopieProtocoleJoueur.size(); i++)
      {
        // Envoyer un message ping au client courant
        ((ProtocoleJoueur) lstCopieProtocoleJoueur.get(i)).envoyerEvenementPing(intCompteurPing);
      }
      
      try
      {
        // Stopper le thread du v�rificateur pendant 60 secondes pour
        // laisser un moment de r�pit au CPU
        Thread.sleep(60000);
      }
      catch (InterruptedException ie) {}
      
      // Passer tous les ProtocoleJoueur et v�rifier s'ils ont 
      // r�pondus au ping. S'ils n'ont pas r�pondus, alors on va les
      // faire se fermer et s'assurer qu'ils se sont bien enlev� de 
      // la liste. On fait exception pour les joueurs en train de jouer
      // une partie, dans ce cas, on ne les d�connecte pas tant que
      // la partie est en cours
      for (int i = 0; i < lstCopieProtocoleJoueur.size(); i++)
      {
        // Faire la r�f�rence vers le ProtocoleJoueur courant
        ProtocoleJoueur protocole = (ProtocoleJoueur) lstCopieProtocoleJoueur.get(i);
        
        // Si le joueur est en train de jouer sur une table, alors
        // on attend, peut-�tre il se reconnectera et pourra
        // continuer � jouer
        // Si le joueur a ferm� le browser ou fait refresh(), il sera d�connect�
        // dans la thread du ProtocoleJoueur, donc n'appara�tra plus ici
                if (protocole.isPlaying() == false)
                {
            // Emp�cher d'autres threads de toucher � la liste des protocoles
            // de joueur ayant r�pondus au ping
            synchronized (lstClientsPresents)
            {
              // Si le protocole courant ne se trouve pas dans la liste des
              // clients qui ont r�pondus, alors on peut arr�ter le 
              // ProtocoleJoueur
              if (lstClientsPresents.contains(protocole) == false)
              {
                // Arr�ter le ProtocoleJoueur
                protocole.arreterProtocoleJoueur();
              } 
            }
        }
      }       
      
      // Incr�menter le compteur de pings
      intCompteurPing++;
      
      // Si le compteur a d�pass� 10, alors on le r�initialise � 0
      if (intCompteurPing >= 11)
      {
        intCompteurPing = 0;
      }
      
      // Vider la liste des clients
      lstClientsPresents.clear();
    }
  }
  
  /**
   * Cette m�thode permet de v�rifier si le ping re�u est valide (le num�ro
   * du ping est bien celui courant) et si c'est le cas alors le joueur est
   * encore connect� au serveur de jeu, il ne faut donc pas le d�truire.
   * 
   * @param ProtocoleJoueur protocole : le protocole du joueur renvoyant la
   *                    r�ponse � un ping envoy�
   * @param int compteurPing : le num�ro que le joueur avait re�u en 
   *               param�tres permettant d'identifier � quel
   *               moment le ping a �t� fait
   */
  public void confirmationPing(ProtocoleJoueur protocole, int compteurPing)
  {
    // Emp�cher d'autres threads de toucher � la liste des protocoles
    // de joueur ayant r�pondus au ping
    synchronized (lstClientsPresents)
    {
      // Si le compteur du ping est le m�me que celui courant, alors
      // on peut ajouter le protocole du joueur dans la liste de ceux
      // � ne pas d�truire
      if (compteurPing == intCompteurPing)
      {
        lstClientsPresents.add(protocole);
      }
    }
  }
  
  /**
   * Cette m�thode permet d'arr�ter le thread du v�rificateur de connexions.
   * Il n'est plus possible de relancer le v�rificateur par la suite.
   */
  public void arreterVerificateurConnexions()
  {
    bolStopThread = false;
  }
}