package ServeurJeu.ComposantesJeu.Joueurs;

import ClassesUtilitaires.UtilitaireNombres;
import ServeurJeu.ControleurJeu;
import ServeurJeu.ComposantesJeu.InformationPartie;
import java.awt.Point;
import java.util.TreeMap;
import ServeurJeu.ComposantesJeu.Table;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Cases.CaseSpeciale;
import ClassesRetourFonctions.RetourVerifierReponseEtMettreAJourPlateauJeu;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import ServeurJeu.Evenements.GestionnaireEvenements;
import ServeurJeu.ComposantesJeu.Objets.Objet;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import ServeurJeu.ComposantesJeu.Objets.Pieces.Piece;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import ServeurJeu.Configuration.GestionnaireMessages;
 
/**
 * @author Jean-Fran�ois Fournier
 */
public class JoueurVirtuel extends Joueur implements Runnable {
	
	// Cette variable va contenir le nom du joueur virtuel
	private String strNom;
        
        // Le joueur virtuel est-il destin� � subir une banane?
        public String vaSubirUneBanane;
	
    // D�claration d'une r�f�rence vers le gestionnaire d'evenements
	private GestionnaireEvenements objGestionnaireEv;
	
	// D�claration d'une r�f�rence vers un objet contenant tous
	// les param�tres des joueurs virtuels
	private ParametreIA objParametreIA;
	
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
	
	// Cette variable contient le niveau de difficult� du joueur virtuel
    private int intNiveauDifficulte;
	
	// Cette variable permet de savoir s'il faut arr�ter le thread ou non
	private boolean bolStopThread;
	
	// D�claration d'une r�f�rence vers la table courante
	private Table objTable;
	
    // D�claration d'une variable qui va contenir le num�ro Id du personnage 
	// du joueur virtuel
	private int intIdPersonnage;

    // D�claration d'une variable qui va contenir le pointage de la 
    // partie du joueur virtuel
	private int intPointage;
        
        private int intArgent;

	// D�claration de la position du joueur virtuel dans le plateau de jeu
	private Point objPositionJoueur;

	// D�claration d'une liste d'objets utilisables ramass�s par le joueur
	// virtuel
	private TreeMap lstObjetsUtilisablesRamasses;
	
	// D�claration d'une r�f�rence vers le controleur jeu
	private ControleurJeu objControleurJeu;
	
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
    
    // Cette variable contient le nombre maximum d'items que le joueur
    // virtuel tra�nera
    private int intNbObjetsMax;
    
    // Cette liste va contenir les magasins d�j� visit�s
    // par le joueur virtuel, pour emp�cher qu'il les visite
    // � plus d'une reprise
    private Vector lstMagasinsVisites;
    
    // Tableau contenant une r�f�rence vers le plateau de jeu
    private Case objttPlateauJeu[][];
    
    // Obtenir le nombre de lignes et de colonnes du plateau de jeu
    private int intNbLignes;
    private int intNbColonnes;
    
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
    
	// Constante pour la compilation conditionnelle
	private static final boolean ccDebug = false;
	
	// Constante pour acc�l�rer les d�placements
	// (voir fonction pause() )
	private static final boolean ccFast = false;
	
	// Objet logger pour afficher les erreurs dans le fichier log
	static private Logger objLogger = Logger.getLogger(JoueurVirtuel.class);
	
	/**
	 * Constructeur de la classe JoueurVirtuel qui permet d'initialiser les 
	 * membres priv�s du joueur virtuel
	 * 
	 * @param String nom : Nom du joueur virtuel
	 * @param Integer niveauDifficulte : Le niveau de difficult� pour ce joueur
	 *                                   virtuel
	 * @param Table tableCourante: La table sur laquelle le joueur joue
	 * @param GestionnaireEvenements gestionnaireEv: R�f�rence vers le gestionnaire
	 *        d'�v�nements pour envoyer aux joueurs humains les mouvements
	 *        du joueur virtuel

	 */
	public JoueurVirtuel(String nom, int niveauDifficulte, Table tableCourante, 
	    GestionnaireEvenements gestionnaireEv, ControleurJeu controleur, int idPersonnage)
	{
	    objControleurJeu = controleur;
	    
	    objParametreIA = objControleurJeu.obtenirParametreIA();
	    
		strNom = nom;
                vaSubirUneBanane = "";
		
		// Cette variable sera utilis�e dans la thread
		objPositionFinaleVisee = null;
		
		// Faire la r�f�rence vers le gestionnaire d'�venements
		objGestionnaireEv = gestionnaireEv;
			
		// Cette variable sert � arr�ter la thread lorsqu'� true
		bolStopThread = false;		
			
		// Faire la r�f�rence vers la table courante
		objTable = tableCourante;	
			
		
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
		
		// Initialisation du pointage
		intPointage = 0;
                intArgent = 0;
		
		// Initialisation � null de la position, le joueur virtuel n'est nul part
		objPositionJoueur = null;
		
	    // Cr�er la liste des objets utilisables qui ont �t� ramass�s
	    lstObjetsUtilisablesRamasses = new TreeMap();
		
        // Cr�ation du profil du joueur virtuel
        intNiveauDifficulte = niveauDifficulte;

        // Tableau contenant une r�f�rence vers le plateau de jeu
        objttPlateauJeu = objTable.obtenirPlateauJeuCourant();
        
        // Obtenir le nombre de lignes et de colonnes du plateau de jeu
        intNbLignes = objttPlateauJeu.length;
        intNbColonnes = objttPlateauJeu[0].length;
        
        // Initialiser les matrices
        matriceParcourue = new boolean[intNbLignes][intNbColonnes];
        matricePrec = new Point[intNbLignes][intNbColonnes];
        matPoints = new int[intNbLignes][intNbColonnes];

        // D�terminer les temps des jetons des minijeus
        determinerJetonsMiniJeu();

        // Au d�part, le joueur virtuel n'a jou� aucun mini-jeu
        intNbMiniJeuJoues = 0;
        
        // D�terminer les temps des jetons pour les magasins
        determinerJetonsMagasins();
        
        // Au d�part, le joueur virtuel n'a pas visit� de magasin
        intNbMagasinVisites = 0;

        // D�finir le nombre d'objets max par une valeur de base
        intNbObjetsMax = ParametreIA.MAX_NOMBRE_OBJETS;
        
        // Cr�er une liste de magasin d�j� visit� vide
        lstMagasinsVisites = new Vector();
	}


	/**
	 * Cette m�thode est appel�e lorsqu'une partie commence. C'est la thread
	 * qui fait jouer le joueur virtuel.
	 * 
	 */
	public void run()
	{
		try
		{	
		    // Assigner le priorit� TRACE au logger pour qu'il puisse
		    // �crire les traces des exceptions si il en arrivent
		    objLogger.setLevel((Level) Level.TRACE);
		
			// Cette variable conserve la case sur laquelle le joueur virtuel
			// tente de se d�placer
			Point objPositionIntermediaire = null;
			
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
			
			while(bolStopThread == false)
			{		
				// D�terminer le temps de r�flexion pour le prochain coup
				intTempsReflexionCoup = obtenirTempsReflexionCoup();
	
				// Pause pour moment de r�flexion de d�cision
				pause(intTempsReflexionCoup);
				
                                // Trouver une case int�ressante � atteindre
                                // Si on a assez de points pour atteindre le WinTheGame, allons-y!
                                if(!this.obtenirTable().obtenirButDuJeu().equals("original") && this.obtenirTable().peutAllerSurLeWinTheGame(this.obtenirPointage()))
                                {
                                    objPositionFinaleVisee = this.obtenirTable().obtenirPositionWinTheGame();
                                }
                                else
                                {
                                    if (reviserPositionFinaleVisee() == true)
                                    {
                                        int essais = 0;
                                        do
                                        {
                                            objPositionFinaleVisee = trouverPositionFinaleVisee();
                                            essais++;
                                        }while(!this.obtenirTable().obtenirButDuJeu().equals("original") && essais < 50 && objPositionFinaleVisee.equals(this.obtenirTable().obtenirPositionWinTheGame()));
                                    }
                                }
                                
                                // On trouve une position entre le joueur virtuel et son objectif
                                {
                                    int essais = 0;
                                    if(this.obtenirTable().peutAllerSurLeWinTheGame(this.obtenirPointage())) objPositionIntermediaire = trouverPositionIntermediaire();
                                    else
                                    {
                                        do
                                        {
                                            objPositionIntermediaire = trouverPositionIntermediaire();
                                            essais++;
                                        }while(!this.obtenirTable().obtenirButDuJeu().equals("original") && essais < 50 && objPositionIntermediaire.equals(this.obtenirTable().obtenirPositionWinTheGame()));
                                    }
                                }

				// S'il y a erreur de recherche ou si le joueur virtuel est pris
				// on ne le fait pas bouger
				if (objPositionIntermediaire.x != objPositionJoueur.x || 
				    objPositionIntermediaire.y != objPositionJoueur.y)
				{
					// Calculer la grandeur du d�placement demand�
					intGrandeurDeplacement = obtenirPointage(objPositionJoueur, objPositionIntermediaire);
					
					// V�rifier si on utilise un objet livre
					boolean bolUtiliserLivre = nombreObjetsPossedes(Objet.UID_OU_LIVRE) > 0;
					    
					// Aller chercher le pourcentage de r�ussite � la question
					intPourcentageReussite = objParametreIA.tPourcentageReponse[intNiveauDifficulte][intGrandeurDeplacement-1];
					
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
					    intPourcentageReussite = objParametreIA.tPourcentageReponseObjetLivre[intNiveauDifficulte][intGrandeurDeplacement-1];
					}
					
	    			// D�terminer si le joueur virtuel r�pondra � la question
	                bolQuestionReussie = (genererNbAleatoire(100)+1 <= intPourcentageReussite);
	    			        
	    			// D�terminer le temps de r�ponse � la question
	    			intTempsReflexionQuestion = obtenirTempsReflexionReponse();
	                
	    			// Pause pour moment de r�flexion de r�ponse
	    			pause(intTempsReflexionQuestion);	
	    					
	    			// Faire d�placer le personnage si le joueur virtuel a 
	    			// r�ussi � r�pondre � la question
	    			if (bolQuestionReussie == true)
	    			{
	    				// D�placement du joueur virtuel
                                        if(!vaSubirUneBanane.equals(""))
                                        {
                                            Banane.utiliserBanane(vaSubirUneBanane, this.obtenirPositionJoueur(), this.obtenirNom(), this.obtenirTable(), false);
                                            vaSubirUneBanane = "";
                                        }
                                        else
                                        {
                                            deplacerJoueurVirtuelEtMajPlateau(objPositionIntermediaire);
                                        }
	    				
	    				// Obtenir le temps que le d�placement dure
	    				intTempsDeplacement = obtenirTempsDeplacement(obtenirPointage(objPositionJoueur, objPositionIntermediaire));
	
	    				// Pause pour laisser le personnage se d�placer
	    				pause(intTempsDeplacement);
	    			}
	    			else
	    			{
	    				if (ccDebug)
	    				{
	    					System.out.println("Question rat�e");
	    				}
	
	    				// Pause pour r�troaction
	    				pause(ParametreIA.TEMPS_RETROACTION);
	    			}
	    			
	    	    }	
			}
		}
		catch (Exception e)
		{
			// Envoyer la trace de l'erreur dans le log
			objLogger.trace(GestionnaireMessages.message("joueur_virtuel.erreur_thread") + strNom, e);
			
			// Envoyer la trace de l'erreur � l'�cran
			e.printStackTrace();
		}
	}
	
	/* Cette fonction trouve le chemin le plus court entre deux points et
	 * le retourne sous forme de Vector. Le chemin retourn� est en ordre inverse
	 * (l'indice 0 correspondra au point d'arriv�e)
	 *
	 * @param: Point depart: Point de d�part du chemin
	 * @param: Point arrivee: Point d'arriv�e du chemin 
	 */
    public Vector trouverCheminPlusCourt(Point depart, Point arrivee)
    {
        // Liste des points � traiter pour l'algorithme de recherche de chemin
        Vector lstPointsATraiter = new Vector();
        
        // Le chemin r�sultat que l'on retourne � la fonction appelante
        Vector lstResultat;
        
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
        for (indiceNombreMelange = 1; indiceNombreMelange <= 3;indiceNombreMelange++)
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
                ptPosTemp.x = ptPosDefile.x + objParametreIA.ptDxDy[tRandom[dxIndex]].x;
                ptPosTemp.y = ptPosDefile.y + objParametreIA.ptDxDy[tRandom[dxIndex]].y;
                
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
            lstResultat = new Vector();
            
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
	private int calculerPointsChemin(Vector lstPositions, Case objttPlateauJeu[][])
	{
		Point ptTemp;
		int intPoints = 0;;
        
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
                   intPointsCase = objParametreIA.objParametreIAPiece.intPointsChemin;
               }
               else if (((CaseCouleur) objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof Magasin)
               {
                   if (considererMagasin == true)
                   {
                   	   // Magasin sur la case
                       intPointsCase = objParametreIA.objParametreIAMagasin.intPointsChemin;
                   }
               }
               
               else if (((CaseCouleur) objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase() instanceof ObjetUtilisable &&
                   lstObjetsUtilisablesRamasses.size() < intNbObjetsMax)
               {
                   ObjetUtilisable objObjet = (ObjetUtilisable)((CaseCouleur)objttPlateauJeu[ptTemp.x][ptTemp.y]).obtenirObjetCase();
                   if (objObjet.estVisible() && determinerPretARamasserObjet(objObjet.obtenirUniqueId()))
                   {
                       // Objet r�ponse sur la case
                       intPointsCase = objParametreIA.tParametresIAObjetUtilisable[objObjet.obtenirUniqueId()].intPointsChemin;
                       intPointsCase -= objParametreIA.tParametresIAObjetUtilisable[objObjet.obtenirUniqueId()].intPointsEnleverDistance * (nombreObjetsPossedes(objObjet.obtenirUniqueId()));
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
                   intPointsCase = objParametreIA.objParametreIAMinijeu.intPointsChemin;
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
            if(this.obtenirTable().peutAllerSurLeWinTheGame(this.obtenirPointage()) && this.obtenirPositionJoueur().equals(this.obtenirTable().obtenirPositionWinTheGame())) return this.obtenirPositionJoueur();
            
	    // Variable contenant la position � retourner � la fonction appelante
		Point objPositionTrouvee;

        Vector lstPositions[] = new Vector[5];
        Vector lstPositionsTrouvees;
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

		        Set lstEnsembleObjets = lstObjetsUtilisablesRamasses.entrySet();
		        Iterator objIterateurListeObjets = lstEnsembleObjets.iterator();
		        int i = 0;
		        	        
		        while (objIterateurListeObjets.hasNext())
		        {
		        	ObjetUtilisable objObjet = (ObjetUtilisable)(((Map.Entry)(objIterateurListeObjets.next())).getValue());
		        	
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
        Vector lstChemin;
        
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
        double dblDistance;
        
        // Point en cours d'analyse
        Point ptTemp = new Point(0,0);
        
        // Autre point en cours d'analyse
        Point ptTemp2 = new Point(0,0);
        
        // Chemin entre le joueur et une case importante analys�e
        Vector lstChemin;

        // D�placement moyen, contient le nombre de cases que l'on peut
        // s'attendre � franchir par coup (prend en compte niveau de
        // difficult�)
        double dblDeplacementMoyen = objParametreIA.tDeplacementMoyen[intNiveauDifficulte];
        
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
                        ((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase() instanceof ObjetUtilisable &&
                        lstObjetsUtilisablesRamasses.size() < intNbObjetsMax)
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
                    		intPointsObjet = objParametreIA.tParametresIAObjetUtilisable[uidObjet].intValeurPoints;
                    		intPointsEnleverNb = objParametreIA.tParametresIAObjetUtilisable[uidObjet].intPointsEnleverQuantite;
                    		intPointsEnleverDistance = objParametreIA.tParametresIAObjetUtilisable[uidObjet].intPointsEnleverDistance;
                    		intPointsAleat = objParametreIA.tParametresIAObjetUtilisable[uidObjet].intValeurAleatoire;
                    		intDistanceMax = objParametreIA.tParametresIAObjetUtilisable[uidObjet].intMaxDistance;


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
                		        objParametreIA.ttPointsRegionPiece, 
                		        dblDeplacementMoyen, dblFacteurAdj);
                		    

                    	}
                    }
                    
                    
                    // Pi�ces
                    else if (objttPlateauJeu[x][y] instanceof CaseCouleur && 
                        ((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase() instanceof Piece)
                    {

                        intPointsObjet = objParametreIA.objParametreIAPiece.intValeurPoints;
                        intPointsEnleverDistance = objParametreIA.objParametreIAPiece.intPointsEnleverDistance;
                        intPointsAleat = objParametreIA.objParametreIAPiece.intValeurAleatoire;
                        intDistanceMax = objParametreIA.objParametreIAPiece.intMaxDistance;
                        
                        //objParametreIAObjetPiece;
                        attribuerImportanceCase(ptTemp, intPointsObjet, intPointsAleat, 
                             intDistanceMax, intPointsEnleverDistance, 
                             objParametreIA.ttPointsRegionPiece, 
                             dblDeplacementMoyen, 1.0);
                        
                    }
                    
                    // Magasins
                    else if(objttPlateauJeu[x][y] instanceof CaseCouleur && 
                        ((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase() instanceof Magasin &&
                        bolConsidererMagasin == true &&
                        !lstMagasinsVisites.contains(((CaseCouleur)objttPlateauJeu[x][y]).obtenirObjetCase()))
                    {
                    	// Plus le joueur virtuel poss�de d'objets, moins il est
                    	// important d'aller visiter un magasin
                    	intPointsCase = objParametreIA.objParametreIAMagasin.intValeurPoints;
                    	
                    	// On enl�ve les points par objets poss�d�s
                    	intPointsCase -= lstObjetsUtilisablesRamasses.size() * ParametreIA.PTS_ENLEVER_MAGASIN_NB_OBJETS;
                    	
                    	dblFacteurAdj = intPointsCase / objParametreIA.objParametreIAMagasin.intValeurPoints;
                    	if (dblFacteurAdj <= 0.00)
                    	{
                    		dblFacteurAdj = ParametreIA.FACTEUR_AJUSTEMENT_MIN; 
                    	}
                    	
                    	attribuerImportanceCase(ptTemp, intPointsCase, 
                    	    objParametreIA.objParametreIAMagasin.intValeurAleatoire, 
                    	    objParametreIA.objParametreIAMagasin.intMaxDistance, 
                    	    objParametreIA.objParametreIAMagasin.intPointsEnleverDistance, 
                    	    objParametreIA.ttPointsRegionPiece, 
                    	    dblDeplacementMoyen, dblFacteurAdj);
                    }
                    
                    // Mini-jeu
                    else if(objttPlateauJeu[x][y] instanceof CaseSpeciale)
                    {
                    	// Attribuer un pointage � la case de MiniJeu
                    	if (bolConsidererMiniJeu == true)
                    	{
                    		attribuerImportanceCase(ptTemp, 
                    		    objParametreIA.objParametreIAMinijeu.intValeurPoints, 
                    		    objParametreIA.objParametreIAMinijeu.intValeurAleatoire, 
                    		    objParametreIA.objParametreIAMinijeu.intMaxDistance, 
                    		    objParametreIA.objParametreIAMinijeu.intPointsEnleverDistance, 
                    		    objParametreIA.ttPointsRegionPiece, 
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
        int intDifferenceMax = objParametreIA.tNombrePointsMaximalChoixFinal[intNiveauDifficulte];
        
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

      
    /*
     * Cette fonction s'occupe de d�placer le joueur virtuel s'il a bien r�pondu
     * � la question, met � jour le plateau de jeu, envoie les �v�nements aux autres joueurs
     * et modifie le pointage, l'argent et la position du joueur virtuel
     */
    private void deplacerJoueurVirtuelEtMajPlateau(Point objNouvellePosition)
    {
    	
    	String reponse = "";
    	
		RetourVerifierReponseEtMettreAJourPlateauJeu objRetour =
		    InformationPartie.verifierReponseEtMettreAJourPlateauJeu(reponse, objNouvellePosition, this);
    	
    	Case objCaseDestination = objTable.obtenirPlateauJeuCourant()[objNouvellePosition.x][objNouvellePosition.y];
        
        ObjetUtilisable objObjetRamasse = objRetour.obtenirObjetRamasse();
        
        if (ccDebug)
        {
        	System.out.println("Nouvelle position: "  + objPositionJoueur.x + "," + 
	            objPositionJoueur.y);
        }
        
        // Si le joueur virtuel a atteint le WinTheGame, on arr�te la partie
        if(!this.obtenirTable().obtenirButDuJeu().equals("original") && objNouvellePosition.equals(this.obtenirTable().obtenirPositionWinTheGame())) this.obtenirTable().arreterPartie(this.obtenirNom());
        
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
        	pause(intTempsJeu);
        	
        	// On incr�mente les points du joueur virtuel
        	intPointage += intPointsJeu;
        	
			// Pr�parer un �v�nement pour les autres joueurs de la table
			// pour qu'il se tienne � jour du pointage de ce joueur
			objTable.preparerEvenementMAJPointage(strNom, intPointage);
        	
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
            Vector lstObjetsMagasins = objMagasin.obtenirListeObjetsUtilisables();
            Vector lstCopieObjetsMagasins = new Vector();
            
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
    		if (lstCopieObjetsMagasins.size() >= 0 && 
    		    lstObjetsUtilisablesRamasses.size() < intNbObjetsMax &&
    		    !lstMagasinsVisites.contains(objMagasin))
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
	    				tPointageObjets[i] = -9999999;
                                        //TODO: r�gler �a
                                    /*tPointageObjets[i] = objParametreIA.tParametresIAObjetUtilisable[objObjetAVendre.obtenirUniqueId()].intValeurPoints - 
	    				    objParametreIA.tParametresIAObjetUtilisable[objObjetAVendre.obtenirUniqueId()].intPointsEnleverQuantite * 
	    				    nombreObjetsPossedes(objObjetAVendre.obtenirUniqueId());*/
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

	    	pause(intTempsReflexion);

    		if (bolDecision)
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
	    		        System.out.println("***************** Prochain id: " + objTable.obtenirProchainIdObjet().intValue);
	    		        
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
					objTable.preparerEvenementMAJArgent(strNom, intArgent);
					
                }
                else
                {
                	if (ccDebug)
                	{
                		System.out.println("Objet envol� apr�s r�flexion (" + strNom + 
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
    
    /*
     * Cette fonction pr�pare l'�v�nement indiquant que le joueur virtuel se d�place
     */
    /*private void preparerEvenementJoueurVirtuelDeplacePersonnage( String collision, Point objNouvellePosition, int nouveauPointage )
    {
        objTable.preparerEvenementJoueurDeplacePersonnage(strNom, collision, objPositionJoueur, 
            objNouvellePosition, nouveauPointage);
    }*/


    private int genererNbAleatoire(int max)
    {
        return objControleurJeu.genererNbAleatoire(max);
    }
    
    
    public void definirPointage(int valeur)
    {
        intPointage = valeur;
    }
    
    public void definirArgent(int valeur)
    {
        intArgent = valeur;
    }
    
    public void definirPositionJoueurVirtuel(Point pos)
    {
        objPositionJoueur = new Point(pos.x, pos.y);
    }

    /*
     * Cette fonction fait une pause de X secondes �mulant une r�flexion
     * par le joueur virtuel
     */
    private void pause(Integer nbSecondes)
    {
    	try
    	{
    		if (!ccFast)
    		{
    	        Thread.sleep(nbSecondes * 1000);
    	    }
    	    else
    	    {
    	    	Thread.sleep(500);
    	    }
    	    
    	}
    	catch(InterruptedException e)
    	{ 
    	}

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
    public GestionnaireEvenements obtenirGestionnaireEvenements()
    {
    	return objGestionnaireEv;
    }
    
    public Point obtenirPositionJoueur()
    {
    	return objPositionJoueur;
    }
    
    public TreeMap obtenirListeObjetsRamasses()
    {
    	return lstObjetsUtilisablesRamasses;
    }
    
    public Table obtenirTable()
    {
    	return objTable;
    }
    
    public int obtenirIdPersonnage()
    {
    	return intIdPersonnage;
    }
    
    
    /* Cette fonction permet d'obtenir le nom du joueur virtuel
     */
    public String obtenirNom()
    {
        return strNom;
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
    
    /* Cette fonction permet � la boucle dans run() de s'arr�ter
     */
    public void arreterThread()
    {
        bolStopThread = true;
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
        	tTableauSource = objParametreIA.tPourcentageChoixObjetLivre;
        }
        else
        {
        	tTableauSource = objParametreIA.tPourcentageChoix;
        }
        
        int intPourcentageCase[] = new int[ParametreIA.DEPLACEMENT_MAX];
        for (int i = 0; i < ParametreIA.DEPLACEMENT_MAX; i++)
        {
        	intPourcentageCase[i] = tTableauSource[intNiveauDifficulte][i];
        }
        
        return intPourcentageCase;
 
    }
       
    /* Cette fonction permet d'obtenir le temps de r�flexion d'un joueur
     * virtuel pour penser � son achat dans un magasin. Ce temps est bas� 
     * sur le niveau de difficult� du joueur virtuel et comprend un �l�ment 
     * al�atoire.
     */
    private int obtenirTempsReflexionAchat()
    {
        
        return objParametreIA.tTempsReflexionBase[ParametreIA.TYPE_REFLEXION_ACHAT][intNiveauDifficulte] + 
            genererNbAleatoire(objParametreIA.tTempsReflexionAleatoire[ParametreIA.TYPE_REFLEXION_ACHAT][intNiveauDifficulte]);

    }
    
    /* Cette fonction permet d'obtenir le temps de r�flexion d'un joueur
     * virtuel pour planifier son prochain coup. Ce temps est bas� sur le niveau
     * de difficult� du joueur virtuel et comprend un �l�ment al�atoire.
     */
    private int obtenirTempsReflexionCoup()
    {
        
        return objParametreIA.tTempsReflexionBase[ParametreIA.TYPE_REFLEXION_COUP][intNiveauDifficulte] + 
            genererNbAleatoire(objParametreIA.tTempsReflexionAleatoire[ParametreIA.TYPE_REFLEXION_COUP][intNiveauDifficulte]);

    }
    
    
    /* Cette fonction permet d'obtenir le temps de r�flexion d'un joueur
     * virtuel lorsqu'il r�pond � une question. Ce temps est bas� sur le niveau
     * de difficult� du joueur virtuel et comprend un �l�ment al�atoire.
     */
    private int obtenirTempsReflexionReponse()
    {
        
        return objParametreIA.tTempsReflexionBase[ParametreIA.TYPE_REFLEXION_REPONSE][intNiveauDifficulte] + 
            genererNbAleatoire(objParametreIA.tTempsReflexionAleatoire[ParametreIA.TYPE_REFLEXION_REPONSE][intNiveauDifficulte]);
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
	    	intPourcentageChoix[i] = objParametreIA.tPourcentageChoixAlternatifFinal[intNiveauDifficulte][i];
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
    	Case objttPlateauJeu[][] = objTable.obtenirPlateauJeuCourant();
        
        // V�rifier si l'�tat de la case a chang�
        switch (intRaisonPositionFinale)
        {
        	case ParametreIA.RAISON_AUCUNE: 
        	
	        	// Aucune raison = erreur en g�n�ral, donc on va recalculer
	            // une position finale
	            return true;

            
        	case ParametreIA.RAISON_PIECE:
        	
	        	// V�rifier si la pi�ce a �t� captur�e
	    	    if (((CaseCouleur)objttPlateauJeu[objPositionFinaleVisee.x][objPositionFinaleVisee.y]).obtenirObjetCase() == null)
	    	    {
	    	    	return true;
	    	    }
	    	    else
	    	    {
	    	    	return false;
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
        
        // Dans les autres cas, ce n'est pas n�cessaire de rechercher
        // tout de suite (on attend que le joueur virtuel atteigne la
        // position finale avant de recalculer une position)
        return false;

    }
 
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
        	    dblDelaiDepart = objControleurJeu.genererNbAleatoire(3);
        	    
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
		intNombreMagasins = objParametreIA.tNbJetonsMagasinBase[intNiveauDifficulte] + 
		    objControleurJeu.genererNbAleatoire(objParametreIA.tNbJetonsMagasinAleatoire[intNiveauDifficulte]);

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
        	tJetonsMagasins[i] = objControleurJeu.genererNbAleatoire(intTempsCalcul * 60);
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
		intNombreMiniJeu = objParametreIA.tNbJetonsMinijeuBase[intNiveauDifficulte] + 
		    objControleurJeu.genererNbAleatoire(objParametreIA.tNbJetonsMinijeuAleatoire[intNiveauDifficulte]);

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
        	tJetonsMiniJeu[i] = objControleurJeu.genererNbAleatoire(intTempsCalcul * 60);
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
    	if (lstObjetsUtilisablesRamasses.size() >= intNbObjetsMax || 
    		objTable.obtenirTempsRestant() <= objParametreIA.tParametresIAObjetUtilisable[uidObjet].intTempsSureteRamasser ||
    	    nombreObjetsPossedes(uidObjet) >= objParametreIA.tParametresIAObjetUtilisable[uidObjet].intQuantiteMax)
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
    	
    	// Si le joueur a atteint son quotas d'objets, alors
    	// il ne visite plus de magasins
    	if (lstObjetsUtilisablesRamasses.size() >= intNbObjetsMax)
    	{
    		return false;
    	}
    	
    	// Il faut au moins 1 dollar pour acheter ne serait-ce qu'un objet
    	if (intArgent < 1)
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
    

    private void enleverObjet(int uidObjet)
    {
        Set lstEnsembleObjets = lstObjetsUtilisablesRamasses.entrySet();
        Iterator objIterateurListeObjets = lstEnsembleObjets.iterator();
        int i = 0;
        	        
        while (objIterateurListeObjets.hasNext())
        {
        	ObjetUtilisable objObjet = (ObjetUtilisable)(((Map.Entry)(objIterateurListeObjets.next())).getValue());
        	
        	if (objObjet.obtenirUniqueId() == uidObjet)
        	{
        		lstObjetsUtilisablesRamasses.remove(objObjet.obtenirId());
        		break;
        	}
        	
        	i++;
        }
    }
    
    
    /* Cette fonction calcule le nombre d'objets que le joueur
     * virtuel poss�de du type uidObjet
     */
    private int nombreObjetsPossedes(int uidObjet)
    {
    	int intNbObjets = 0;
    	
        if (lstObjetsUtilisablesRamasses.size() > 0)
        {

	        Set lstEnsembleObjets = lstObjetsUtilisablesRamasses.entrySet();
	        Iterator objIterateurListeObjets = lstEnsembleObjets.iterator();
	        	        
	        while (objIterateurListeObjets.hasNext())
	        {
	        	ObjetUtilisable objObjet = (ObjetUtilisable)(((Map.Entry)(objIterateurListeObjets.next())).getValue());
	        	
	        	if (objObjet.obtenirUniqueId() == uidObjet)
	        	{
	        		intNbObjets++;
	        	}
	        }

        }
        
        return intNbObjets;
    }
    /*
     * Cette m�thode statique permet de valider un niveau de joueur virtuel en format String
     * 
     * @param String s : le Niveau des joueurs virtuel en cha�ne de caract�res (Facile, Intermediaire,..)
     * @return boolean : true : si le param�tre est valide
     * 					 false : si le param�tre n'est pas valide
     */
    public static boolean validerParamNiveau(String s)
    {
    	return (s.equals("Aucun") || s.equals("Facile") || s.equals("Intermediaire") ||
    			s.equals("Difficile") || s.equals("TresDifficile"));
    }
    
    public int obtenirDistanceAuWinTheGame()
    {
        return Math.abs(objPositionJoueur.x - objTable.obtenirPositionWinTheGame().x) + Math.abs(objPositionJoueur.y - objTable.obtenirPositionWinTheGame().y);
    }
}

