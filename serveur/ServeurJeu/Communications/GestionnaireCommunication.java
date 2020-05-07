package ServeurJeu.Communications;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import ServeurJeu.ControleurJeu;
import ServeurJeu.BD.GestionnaireBD;
import ServeurJeu.Configuration.GestionnaireConfiguration;
import ServeurJeu.Configuration.GestionnaireMessages;
import ServeurJeu.Evenements.GestionnaireEvenements;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class GestionnaireCommunication 
{
	// D�claration d'une r�f�rence vers le contr�leur de jeu
	private final ControleurJeu objControleurJeu;
	
	// D�claration d'une liste de ProtocoleJoueur des clients connect�s au serveur
	private LinkedList<ProtocoleJoueur> lstProtocoleJoueur;
	
	// D�claration d'un objet qui va permettre de v�rifier l'�tat des connexions
	// entre le serveur et les clients
	private final VerificateurConnexions objVerificateurConnexions;
	
	// D�claration d'une r�f�rence vers le gestionnaire d'�v�nements
	private final GestionnaireEvenements objGestionnaireEvenements;
	
	// D�claration d'une r�f�rence vers le gestionnaire de bases de donn�es
	private final GestionnaireBD objGestionnaireBD;
	
	// Cette variable contient le port sur lequel le serveur va �couter et 
	// recevoir les connexions clientes
	private final int intPort;
	
	private final String bindAddress;
	
	// D�claration d'un socket pour le serveur
	private ServerSocket objSocketServeur;
	
	//private GestionnaireTemps objGestionnaireTemps;
	//private TacheSynchroniser objTacheSynchroniser;
	
	private boolean boolStopThread; 
	
	private static Logger objLogger = Logger.getLogger( GestionnaireCommunication.class );

    
	
	/**
	 * Constructeur de la classe GestionnaireCommunication qui permet d'initialiser
	 * le port d'�coute du serveur et la r�f�rence vers le contr�leur de jeu ainsi
	 * que vers le gestionnaire d'�v�nements.
	 */
	public GestionnaireCommunication(ControleurJeu controleur, GestionnaireEvenements gestionnaireEv) 
	{
		super();
		
		GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();
		intPort = config.obtenirNombreEntier( "gestionnairecommunication.port" );
		bindAddress = config.obtenirString( "gestionnairecommunication.address" );
		
		// Garder la r�f�rence vers le contr�leur de jeu
		objControleurJeu = controleur;
		
		// Garder la r�f�rence vers le GestionnaireEvenements et vers le GestionnaireBD
		objGestionnaireEvenements = gestionnaireEv;
		objGestionnaireBD = controleur.obtenirGestionnaireBD();
		
		// Cr�er une liste des ProtocoleJoueur
		lstProtocoleJoueur = new LinkedList<ProtocoleJoueur>();
		
		// Cr�er le v�rificateur de connexions
		objVerificateurConnexions = new VerificateurConnexions(this);
				
	}
	
	/**
	 * Cette m�thode permet de d�marrer l'�coute du serveur. Chaque connexion
	 * cr�e un nouveau thread pour g�rer le protocole du joueur.
	 */
	public void ecouterConnexions()
	{
		// Cr�er un thread pour le v�rificateur de connexions
		Thread threadVerificateur = new Thread(objVerificateurConnexions, "objVerificateurConnexions");
		// D�marrer le thread du v�rificateur
		threadVerificateur.start();
		
		miseAJourInfo();
		
		try
		{
			// Cr�er un socket pour le serveur qui va �couter sur le port d�finit
			// par la variable "intPort"
			boolStopThread = false;
			objSocketServeur = new ServerSocket(intPort, 50, InetAddress.getByName(bindAddress));
			//objSocketServeur.
		}
		catch (IOException e)
		{
			// L'�coute n'a pas pu �tre d�marr�e
			objLogger.error(GestionnaireMessages.message("communication.erreur_demarrage") + intPort);
			objLogger.error(GestionnaireMessages.message("communication.serveur_arrete"), e);
			boolStopThread = true;
		}
		
		// Boucler ind�finiment en �coutant sur le port "intPort" et en d�marrant un
		// nouveau thread pour chacune des connexions �tablies
		while( !boolStopThread )
		{
			try
			{
				objLogger.info(GestionnaireMessages.message("communication.attente"));
				
				// Accepter une connexion et cr�er un objet ProtocoleJoueur
				// qui va ex�cuter le protocole pour le joueur
				ProtocoleJoueur objJoueur = new ProtocoleJoueur(objControleurJeu, objVerificateurConnexions,
																objSocketServeur.accept());
				
				// Cr�er un thread pour le joueur demandant la connexion
				Thread threadJoueur = new Thread(objJoueur, "JoueurN" );
				
				// D�marrer le thread du joueur courant
				threadJoueur.start();
				
				// Ajouter le nouveau ProtocoleJoueur dans la liste 
				synchronized(lstProtocoleJoueur){
				  lstProtocoleJoueur.add( objJoueur );
				}
				miseAJourInfo();
				
			}
			catch (IOException e)
			{
				// Une erreur est survenue lors de l'acceptation de la connexion
				objLogger.error(GestionnaireMessages.message("communication.erreur_accept"));
				objLogger.error(GestionnaireMessages.message("communication.serveur_arrete"), e);
				boolStopThread = true;
			}
			

		}
	}
	
	/**
	 * this function is used to finish the work of the Gestionnaire
	 */
	public void arreter()
	{
		boolStopThread = true;
		synchronized(lstProtocoleJoueur){
			// Vider la liste des protocoles de joueurs
			for(ProtocoleJoueur protocole: this.lstProtocoleJoueur)
			{
				protocole.setBolStopThread(true);
				//protocole.arreterProtocoleJoueur();
			}
           lstProtocoleJoueur.clear();
		
		}

		try
		{
			// Fermer le socket du serveur
			if(!objSocketServeur.isClosed())
			   objSocketServeur.close();
		}
		catch (IOException e)
		{
			// Le socket du serveur est d�j� ferm�
			objLogger.error(GestionnaireMessages.message("communication.erreur_socket"), e);
		}
		catch (NullPointerException e)
		{
			// Le socket du serveur est d�j� ferm�
			objLogger.error(GestionnaireMessages.message("communication.erreur_socket"), e);
		}
				
		// Arr�ter le thread de v�rification des connexions
		objVerificateurConnexions.arreterVerificateurConnexions();
		
		// Arr�ter le thread de gestionnaire d'�v�nements
		objGestionnaireEvenements.arreterGestionnaireEvenements();
		
		// Fermer toutes les connexions ouvertes pour le gestionnaire de base de donn�es
		objGestionnaireBD.arreterGestionnaireBD();
	}
	
	/**
	 * Cette m�thode permet de supprimer le protocole joueur pass� en param�tres
	 * de la liste des ProtocoleJoueur. Cela signifie que le joueur ne sera plus
	 * connect� physiquement au serveur de jeu. Il faut s'assurer que le 
	 * ProtocoleJoueur supprim� sera d�truit ou terminera prochainement.
	 * 
	 * @param ProtocoleJoueur protocole : le protocole du joueur � supprimer de
	 * 									  la liste
	 */
	public void supprimerProtocoleJoueur(ProtocoleJoueur protocole)
	{
		// Enlever le protocole joueur de la liste des ProtocoleJoueur
		try
		{
			synchronized(lstProtocoleJoueur){
			   lstProtocoleJoueur.remove(protocole);
			}
			miseAJourInfo();
			
		}
		catch( Exception e )
		{
			objLogger.error(GestionnaireMessages.message("communication.erreur_protocole"), e);
		}
	}
	
	/**
	 * Cette fonction permet de retourner la liste des 
	 * ProtocoleJoueur des clients connect�s au serveur de jeu.
	 * 
	 * @return LinkedList : la liste des ProtocoleJoueur des clients 
	 * 					pr�sentement connect�s au serveur de jeu
	 */
	public synchronized LinkedList<ProtocoleJoueur> obtenirListeProtocoleJoueur()
	{
		return lstProtocoleJoueur;   /// synchro ????
	}
	
	public void miseAJourInfo()
	{
		synchronized(objControleurJeu.obtenirListeJoueurs()) {
			try
			{
				FileWriter writer = new FileWriter( "serveur.info" );
				writer.write("Dans la liste joueurs : " + new Integer( objControleurJeu.obtenirListeJoueurs().size()).toString() + "\n"); ///lstProtocoleJoueur.size()
				writer.write("Dans la liste tables: " + new Integer( objControleurJeu.getActiveTablesNumber()).toString()  + "\n" );
				writer.close();
				objLogger.info( GestionnaireMessages.message("communication.nb_joueurs") + lstProtocoleJoueur.size() );
			}
			catch( Exception e )
			{
				objLogger.error(GestionnaireMessages.message("communication.erreur_fichier"), e);
			}
		}
	}
	
} //end class 
