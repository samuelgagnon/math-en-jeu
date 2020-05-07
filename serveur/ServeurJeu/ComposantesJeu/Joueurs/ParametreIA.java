package ServeurJeu.ComposantesJeu.Joueurs;

import ServeurJeu.ComposantesJeu.Objets.Objet;
import java.awt.Point;

// Cette classe contient tous les param�tres utilis�s
// par les joueurs virtuels
public class ParametreIA {

	
	// Ce tableau contient les informations pour tous les objets
	// utilisables
	public static ParametreIAObjet tParametresIAObjetUtilisable[];
	
	// Param�tres pour les pi�ces
	// (S�par� car diff�rent, pourrait �tre regroup� dans un 
	// tableau d'objet � usage imm�diat, on ne se sert
	// pas des champs qui tiennent compte de la quantit�)
	public static ParametreIAObjet objParametreIAPiece;
	
	// Param�tres pour les magasins, on ne se sert pas de tout
	public static ParametreIAObjet objParametreIAMagasin;
	
	// Param�tres pour les minijeus, on ne se sert pas de tout
	public static ParametreIAObjet objParametreIAMinijeu;
	
	// Tableau pour le pourcentage des choix
	public static int tPourcentageChoix[][];
	
	// Tableau pour le pourcentage des r�ponses
	public static int tPourcentageReponse[][];
	
	// Tableau pour le pourcentage des choix lorsqu'on poss�de l'objet Livre
	public static int tPourcentageChoixObjetLivre[][];
	
	// Tableau pour le pourcentage des r�ponses lorsqu'on poss�de l'objet Livre
	public static int tPourcentageReponseObjetLivre[][];
		
	// Tableaux pour les temps de r�flexion
	public static int tTempsReflexionBase[][];
	public static int tTempsReflexionAleatoire[][];
	
	// Cr�er les tableaux pour les jetons minijeu
	// Ces tableaux permettent de g�n�rer les jetons pour les minijeus
	public final static int tNbJetonsMinijeuBase[];
		
	public final static int tNbJetonsMinijeuAleatoire[];
	
	// Tableau pour le d�placement moyen
	public static double tDeplacementMoyen[];
	
	// Tableau pour le nombre de points maximum qu'un joueur
    // virtuel peut n�gliger lors du choix de la position finale
    public static int tNombrePointsMaximalChoixFinal[];
	
	// Tableau pour les choix alternatif final
	public static int tPourcentageChoixAlternatifFinal[][];
	
    // Ce tableau nous permettra de traiter les 4 cases autour d'une case
    // � l'int�rieur d'une boucle.
    public static Point ptDxDy[];
	
    // Ce tableau contient des param�tres pour prioriser les
    // regroupement de pi�ces
    public static int ttPointsRegionPiece[][];
		
	// Ces tableaux permettent de g�n�rer les jetons pour les magasins
	public static int tNbJetonsMagasinBase[];
	public static int tNbJetonsMagasinAleatoire[];
	
	// Ce tableau contient les noms des joueurs virtuels
	//public static String tBanqueNomsJoueurVirtuels[];	
	
	//-----------------------------
	//           CONSTANTES
	//-----------------------------
	
	
	
	// Constantes pour les points de d�part des cases couleurs
	public final static int POINTS_BASE_CASE_COULEUR = -50;
	public final static int POINTS_ALEATOIRE_CASE_COULEUR = 101;
	
	// Constante donnant un pointage tr�s bas au case que l'on ne veut pas
	// choisir
	public final static int POINTS_IGNORER_CASE = -999999999;
	
	// Constante pour le nombre de niveau de difficult� disponible
	public final static int NOMBRE_NIVEAU_DIFFICULTE = 4;
	
	// Constantes pour d�finir les niveaux de difficult� disponibles
    public static final int DIFFICULTE_FACILE = 0;
    public static final int DIFFICULTE_MOYEN = 1;
    public static final int DIFFICULTE_DIFFICILE = 2;
    public static final int DIFFICULTE_TRES_DIFFICILE = 3;
    	
	// Constante pour le nombre de type de r�flexion
	public final static int NOMBRE_TYPE_REFLEXION = 3;
	
    // D�placement maximum autoris�
    public final static int DEPLACEMENT_MAX = 6;
    
	    
    // Constantes pour d�finir le type de r�flexion
    public static final int TYPE_REFLEXION_COUP = 0;
    public static final int TYPE_REFLEXION_REPONSE = 1;
    public static final int TYPE_REFLEXION_ACHAT = 2;
    
    // Constant pour d�finir le nombre de choix alternatif final � consid�rer
    public static final int NOMBRE_CHOIX_ALTERNATIF = 5;
    
	// Cette constante d�finit le temps de pause lors d'une r�troaction
	public final static int TEMPS_RETROACTION = 14;
	
	// Quelques raisons pour d�placer le joueur virtuel
	public final static int RAISON_AUCUNE = 0;
	public final static int RAISON_PIECE = 1;
	public final static int RAISON_MINIJEU = 2;
	public final static int RAISON_MAGASIN = 3;
	public final static int RAISON_OBJET = 4;
	public final static int RAISON_FINISH = 5;

    // Constante repr�sentant les mini-jeus
    public final static int MINIJEU_BALLE_AU_MUR = 0;
    
	// Autres constantes utilis�s dans les algorithmes de recherche de choix
    public final static int DROITE = 0;
    public final static int BAS = 1;
    public final static int GAUCHE = 2;
    public final static int HAUT = 3;
    
      
    // Constante contenant le nombre de id personnage
    public final static int NOMBRE_PERSONNAGE_ID = 10;
     
    // Facteur d'�loignement minimal
    public final static double FACTEUR_AJUSTEMENT_MIN = 0.01;
    
    // Valeur en points qu'on enl�ve pour chaque objets qu'on poss�de
    public final static int PTS_ENLEVER_MAGASIN_NB_OBJETS = 400;
    
    // Temps de s�ret� pour magasin
    public final static int TEMPS_SURETE_MAGASIN = 60;
    
    // Temps de s�ret� fin de partie fin de mini-jeu
    public final static int TEMPS_SURETE_MINIJEU_FIN_PARTIE = 3;
    
    // Ratio de questions � choix de r�ponse (pour objet Livre)
    public final static int RATIO_CHOIX_DE_REPONSE = 85;
    
	public ParametreIA(){}
	
	static {
		// Aller chercher la r�f�rence vers le gestionnaire de configuration
		//GestionnaireConfiguration config = GestionnaireConfiguration.obtenirInstance();
		
		// Cr�er le tableau contenant l'info pour chaque objet utilisable
		tParametresIAObjetUtilisable = new ParametreIAObjet[8];
		
		// Objet Utilisable "Reponse" UID = 0
		tParametresIAObjetUtilisable[Objet.UID_OU_LIVRE] = 
		    new ParametreIAObjet(4950, 101, 6, 800, 200, 100, 20, 3, 60);
		
		// Objet Utilisable "Reponse" UID = 0
		tParametresIAObjetUtilisable[Objet.UID_OU_BOULE] = 
		    new ParametreIAObjet(4950, 101, 6, 800, 200, 100, 20, 3, 60);
		
		// Objet Utilisable "Banane" 
		tParametresIAObjetUtilisable[Objet.UID_OU_BANANE] = 
		    new ParametreIAObjet(9950, 201, 6, 300, 100, 2000, 10, 4, 50);
		
		// Objet Utilisable "Braniac" 
		tParametresIAObjetUtilisable[Objet.UID_OU_BRAINIAC] = 
		    new ParametreIAObjet(9950, 151, 6, 300, 100, 900, 10, 4, 50);
		
		// Param�tres pour les pi�ces
		objParametreIAPiece = 
		    new ParametreIAObjet(4950, 101, 6, 1000, 0, 100, 0, 0, 0);
		    
		// Param�tres pour les magasins
		objParametreIAMagasin = 
		    new ParametreIAObjet(4950, 101, 6, 1000, 0, 100, 0, 0, 0);
		    
		// Param�tres pour les minijeus
		objParametreIAMinijeu = 
		    new ParametreIAObjet(4950, 101, 6, 800, 0, 100, 0, 0, 0);
		
		// Cr�er le tableau contenant les pourcentages pour les choix
		// des d�placements
		tPourcentageChoix = new int[NOMBRE_NIVEAU_DIFFICULTE][DEPLACEMENT_MAX];
		tPourcentageChoix[DIFFICULTE_FACILE][0] = 60;
		tPourcentageChoix[DIFFICULTE_FACILE][1] = 25;
		tPourcentageChoix[DIFFICULTE_FACILE][2] = 12;
		tPourcentageChoix[DIFFICULTE_FACILE][3] = 2;
		tPourcentageChoix[DIFFICULTE_FACILE][4] = 1;
		tPourcentageChoix[DIFFICULTE_FACILE][5] = 0;
		tPourcentageChoix[DIFFICULTE_MOYEN][0] = 40;
		tPourcentageChoix[DIFFICULTE_MOYEN][1] = 35;
		tPourcentageChoix[DIFFICULTE_MOYEN][2] = 19;
		tPourcentageChoix[DIFFICULTE_MOYEN][3] = 3;
		tPourcentageChoix[DIFFICULTE_MOYEN][4] = 2;
		tPourcentageChoix[DIFFICULTE_MOYEN][5] = 1;
		tPourcentageChoix[DIFFICULTE_DIFFICILE][0] = 10;
		tPourcentageChoix[DIFFICULTE_DIFFICILE][1] = 20;
		tPourcentageChoix[DIFFICULTE_DIFFICILE][2] = 35;
		tPourcentageChoix[DIFFICULTE_DIFFICILE][3] = 20;
		tPourcentageChoix[DIFFICULTE_DIFFICILE][4] = 10;
		tPourcentageChoix[DIFFICULTE_DIFFICILE][5] = 5;    
		tPourcentageChoix[DIFFICULTE_TRES_DIFFICILE][0] = 5;
		tPourcentageChoix[DIFFICULTE_TRES_DIFFICILE][1] = 10;
		tPourcentageChoix[DIFFICULTE_TRES_DIFFICILE][2] = 20;
		tPourcentageChoix[DIFFICULTE_TRES_DIFFICILE][3] = 35;
		tPourcentageChoix[DIFFICULTE_TRES_DIFFICILE][4] = 20;
		tPourcentageChoix[DIFFICULTE_TRES_DIFFICILE][5] = 10;
		
		
		// Cr�er le tableau contenant les pourcentages pour les
		// r�ussites des r�ponses
		tPourcentageReponse = new int[NOMBRE_NIVEAU_DIFFICULTE][DEPLACEMENT_MAX];
		tPourcentageReponse[DIFFICULTE_FACILE][0] = 70;
		tPourcentageReponse[DIFFICULTE_FACILE][1] = 55;
		tPourcentageReponse[DIFFICULTE_FACILE][2] = 45;
		tPourcentageReponse[DIFFICULTE_FACILE][3] = 15;
		tPourcentageReponse[DIFFICULTE_FACILE][4] = 2;
		tPourcentageReponse[DIFFICULTE_FACILE][5] = 1;
		tPourcentageReponse[DIFFICULTE_MOYEN][0] = 80;
		tPourcentageReponse[DIFFICULTE_MOYEN][1] = 70;
		tPourcentageReponse[DIFFICULTE_MOYEN][2] = 50;
		tPourcentageReponse[DIFFICULTE_MOYEN][3] = 20;
		tPourcentageReponse[DIFFICULTE_MOYEN][4] = 3;
		tPourcentageReponse[DIFFICULTE_MOYEN][5] = 2;
		tPourcentageReponse[DIFFICULTE_DIFFICILE][0] = 90;
		tPourcentageReponse[DIFFICULTE_DIFFICILE][1] = 80;
		tPourcentageReponse[DIFFICULTE_DIFFICILE][2] = 75;
		tPourcentageReponse[DIFFICULTE_DIFFICILE][3] = 50;
		tPourcentageReponse[DIFFICULTE_DIFFICILE][4] = 20;
		tPourcentageReponse[DIFFICULTE_DIFFICILE][5] = 5; 
		tPourcentageReponse[DIFFICULTE_TRES_DIFFICILE][0] = 100;
		tPourcentageReponse[DIFFICULTE_TRES_DIFFICILE][1] = 90;
		tPourcentageReponse[DIFFICULTE_TRES_DIFFICILE][2] = 70;
		tPourcentageReponse[DIFFICULTE_TRES_DIFFICILE][3] = 60;
		tPourcentageReponse[DIFFICULTE_TRES_DIFFICILE][4] = 40;
		tPourcentageReponse[DIFFICULTE_TRES_DIFFICILE][5] = 20; 
        
        // Cr�er le tableau pour le pourcentage des choix lorsque le 
        // joueur poss�de l'objet Livre
        tPourcentageChoixObjetLivre = new int[NOMBRE_NIVEAU_DIFFICULTE][DEPLACEMENT_MAX];
		tPourcentageChoixObjetLivre[DIFFICULTE_FACILE][0] = 10;
		tPourcentageChoixObjetLivre[DIFFICULTE_FACILE][1] = 30;
		tPourcentageChoixObjetLivre[DIFFICULTE_FACILE][2] = 40;
		tPourcentageChoixObjetLivre[DIFFICULTE_FACILE][3] = 20;
		tPourcentageChoixObjetLivre[DIFFICULTE_FACILE][4] = 0;
		tPourcentageChoixObjetLivre[DIFFICULTE_FACILE][5] = 0;
		tPourcentageChoixObjetLivre[DIFFICULTE_MOYEN][0] = 5;
		tPourcentageChoixObjetLivre[DIFFICULTE_MOYEN][1] = 20;
		tPourcentageChoixObjetLivre[DIFFICULTE_MOYEN][2] = 50;
		tPourcentageChoixObjetLivre[DIFFICULTE_MOYEN][3] = 20;
		tPourcentageChoixObjetLivre[DIFFICULTE_MOYEN][4] = 5;
		tPourcentageChoixObjetLivre[DIFFICULTE_MOYEN][5] = 0;
		tPourcentageChoixObjetLivre[DIFFICULTE_DIFFICILE][0] = 0;
		tPourcentageChoixObjetLivre[DIFFICULTE_DIFFICILE][1] = 5;
		tPourcentageChoixObjetLivre[DIFFICULTE_DIFFICILE][2] = 20;
		tPourcentageChoixObjetLivre[DIFFICULTE_DIFFICILE][3] = 50;
		tPourcentageChoixObjetLivre[DIFFICULTE_DIFFICILE][4] = 15;
		tPourcentageChoixObjetLivre[DIFFICULTE_DIFFICILE][5] = 10; 
		tPourcentageChoixObjetLivre[DIFFICULTE_TRES_DIFFICILE][0] = 0;
		tPourcentageChoixObjetLivre[DIFFICULTE_TRES_DIFFICILE][1] = 0;
		tPourcentageChoixObjetLivre[DIFFICULTE_TRES_DIFFICILE][2] = 5;
		tPourcentageChoixObjetLivre[DIFFICULTE_TRES_DIFFICILE][3] = 5;
		tPourcentageChoixObjetLivre[DIFFICULTE_TRES_DIFFICILE][4] = 50;
		tPourcentageChoixObjetLivre[DIFFICULTE_TRES_DIFFICILE][5] = 40; 
        
        
        // Cr�er le tableau pour le pourcentage de r�ponse lorsque le
        // joueur utilise l'objet Livre
        tPourcentageReponseObjetLivre = new int[NOMBRE_NIVEAU_DIFFICULTE][DEPLACEMENT_MAX];
		tPourcentageReponseObjetLivre[DIFFICULTE_FACILE][0] = 90;
		tPourcentageReponseObjetLivre[DIFFICULTE_FACILE][1] = 80;
		tPourcentageReponseObjetLivre[DIFFICULTE_FACILE][2] = 60;
		tPourcentageReponseObjetLivre[DIFFICULTE_FACILE][3] = 25;
		tPourcentageReponseObjetLivre[DIFFICULTE_FACILE][4] = 0;
		tPourcentageReponseObjetLivre[DIFFICULTE_FACILE][5] = 0;
		tPourcentageReponseObjetLivre[DIFFICULTE_MOYEN][0] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_MOYEN][1] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_MOYEN][2] = 85;
		tPourcentageReponseObjetLivre[DIFFICULTE_MOYEN][3] = 35;
		tPourcentageReponseObjetLivre[DIFFICULTE_MOYEN][4] = 30;
		tPourcentageReponseObjetLivre[DIFFICULTE_MOYEN][5] = 0;
		tPourcentageReponseObjetLivre[DIFFICULTE_DIFFICILE][0] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_DIFFICILE][1] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_DIFFICILE][2] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_DIFFICILE][3] = 80;
		tPourcentageReponseObjetLivre[DIFFICULTE_DIFFICILE][4] = 55;
		tPourcentageReponseObjetLivre[DIFFICULTE_DIFFICILE][5] = 40;
		tPourcentageReponseObjetLivre[DIFFICULTE_TRES_DIFFICILE][0] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_TRES_DIFFICILE][1] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_TRES_DIFFICILE][2] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_TRES_DIFFICILE][3] = 100;
		tPourcentageReponseObjetLivre[DIFFICULTE_TRES_DIFFICILE][4] = 95;
		tPourcentageReponseObjetLivre[DIFFICULTE_TRES_DIFFICILE][5] = 85; 


        // Cr�er les tableaux pour les temps de r�flexion
        tTempsReflexionBase = new int [NOMBRE_TYPE_REFLEXION][NOMBRE_NIVEAU_DIFFICULTE];
        tTempsReflexionAleatoire = new int [NOMBRE_TYPE_REFLEXION][NOMBRE_NIVEAU_DIFFICULTE];
	    
	    // Temps de r�flexion lors d'achat
	    tTempsReflexionBase[TYPE_REFLEXION_ACHAT][DIFFICULTE_FACILE] = 5;
	    tTempsReflexionBase[TYPE_REFLEXION_ACHAT][DIFFICULTE_MOYEN] = 4;
	    tTempsReflexionBase[TYPE_REFLEXION_ACHAT][DIFFICULTE_DIFFICILE] = 3;
	    tTempsReflexionBase[TYPE_REFLEXION_ACHAT][DIFFICULTE_TRES_DIFFICILE] = 2;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_ACHAT][DIFFICULTE_FACILE] = 6;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_ACHAT][DIFFICULTE_MOYEN] = 5;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_ACHAT][DIFFICULTE_DIFFICILE] = 4;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_ACHAT][DIFFICULTE_TRES_DIFFICILE] = 3;
	      
        // Temps de r�flexion avant un coup
	    tTempsReflexionBase[TYPE_REFLEXION_COUP][DIFFICULTE_FACILE] = 6;
	    tTempsReflexionBase[TYPE_REFLEXION_COUP][DIFFICULTE_MOYEN] = 5;
	    tTempsReflexionBase[TYPE_REFLEXION_COUP][DIFFICULTE_DIFFICILE] = 4;
	    tTempsReflexionBase[TYPE_REFLEXION_COUP][DIFFICULTE_TRES_DIFFICILE] = 3;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_COUP][DIFFICULTE_FACILE] = 6;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_COUP][DIFFICULTE_MOYEN] = 5;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_COUP][DIFFICULTE_DIFFICILE] = 4;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_COUP][DIFFICULTE_TRES_DIFFICILE] = 3;
	    
        // Temps de r�flexion pour r�pondre � une question  
	    tTempsReflexionBase[TYPE_REFLEXION_REPONSE][DIFFICULTE_FACILE] = 32;  //???????????????
	    tTempsReflexionBase[TYPE_REFLEXION_REPONSE][DIFFICULTE_MOYEN] = 28;
	    tTempsReflexionBase[TYPE_REFLEXION_REPONSE][DIFFICULTE_DIFFICILE] = 24;
	    tTempsReflexionBase[TYPE_REFLEXION_REPONSE][DIFFICULTE_TRES_DIFFICILE] = 20;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_REPONSE][DIFFICULTE_FACILE] = 14;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_REPONSE][DIFFICULTE_MOYEN] = 12;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_REPONSE][DIFFICULTE_DIFFICILE] = 10;
	    tTempsReflexionAleatoire[TYPE_REFLEXION_REPONSE][DIFFICULTE_TRES_DIFFICILE] = 8;
	 
	    // Cr�er le tableau pour le d�placement moyen
	    tDeplacementMoyen = new double[NOMBRE_NIVEAU_DIFFICULTE];   
	    tDeplacementMoyen[DIFFICULTE_FACILE] = 1.276;
	    tDeplacementMoyen[DIFFICULTE_MOYEN] = 2.0685 ;
	    tDeplacementMoyen[DIFFICULTE_DIFFICILE] = 3.19 ;
	    tDeplacementMoyen[DIFFICULTE_TRES_DIFFICILE] = 3.19 ;
	    
	    // Cr�er le tableau pour le nombre de points maximum pour la position
	    // final
	    tNombrePointsMaximalChoixFinal = new int[NOMBRE_NIVEAU_DIFFICULTE];
	    tNombrePointsMaximalChoixFinal[DIFFICULTE_FACILE] = 2000;
	    tNombrePointsMaximalChoixFinal[DIFFICULTE_MOYEN] = 1000;
	    tNombrePointsMaximalChoixFinal[DIFFICULTE_DIFFICILE] = 400;
	    tNombrePointsMaximalChoixFinal[DIFFICULTE_TRES_DIFFICILE] = 400;
	    
	    // Cr�er le tableau pour les pourcentages des choix alternatif finaux
	    tPourcentageChoixAlternatifFinal = new int[NOMBRE_NIVEAU_DIFFICULTE][NOMBRE_CHOIX_ALTERNATIF];
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_FACILE][0] = 70;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_FACILE][1] = 20;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_FACILE][2] = 5;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_FACILE][3] = 4;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_FACILE][4] = 1;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_MOYEN][0] = 80;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_MOYEN][1] = 17;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_MOYEN][2] = 2;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_MOYEN][3] = 1;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_MOYEN][4] = 0;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_DIFFICILE][0] = 90;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_DIFFICILE][1] = 8;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_DIFFICILE][2] = 2;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_DIFFICILE][3] = 0;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_DIFFICILE][4] = 0;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_TRES_DIFFICILE][0] = 90;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_TRES_DIFFICILE][1] = 8;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_TRES_DIFFICILE][2] = 2;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_TRES_DIFFICILE][3] = 0;
	    tPourcentageChoixAlternatifFinal[DIFFICULTE_TRES_DIFFICILE][4] = 0;
	    
		// Cr�er le tableau qui permet de parcourir les 4 cases adjacentes
	    ptDxDy = new Point[4];
	    ptDxDy[DROITE] = new Point(0,1);
	    ptDxDy[BAS] = new Point(1,0);
	    ptDxDy[GAUCHE] = new Point(0,-1);
	    ptDxDy[HAUT] = new Point(-1,0);
	    
        // Cr�er le tableau pour les regroupements de pi�ces
        int[][] ttTemp =
                    {{ 10, 10,  0,  0,  0,  0,  0}, 
                     { 20, 15, 10  ,0,  0,  0,  0},
                     { 50, 30, 15, 10,  0,  0,  0},
                     {100, 75, 30, 15, 10,  0,  0},
                     {200,150, 75, 30, 15, 10,  0},
                     {400,300,150, 75, 30, 15, 10},
                     {500,400,200,100, 50, 20, 10}};
       
	    ttPointsRegionPiece = ttTemp;
	    
	    // Cr�er les tableaux pour les jetons minijeu
	    tNbJetonsMinijeuBase = new int[NOMBRE_NIVEAU_DIFFICULTE];
	    tNbJetonsMinijeuBase[DIFFICULTE_FACILE] = 1;
	    tNbJetonsMinijeuBase[DIFFICULTE_MOYEN] = 2;
	    tNbJetonsMinijeuBase[DIFFICULTE_DIFFICILE] = 3;
	    tNbJetonsMinijeuBase[DIFFICULTE_TRES_DIFFICILE] = 3;
	    
	    tNbJetonsMinijeuAleatoire = new int[NOMBRE_NIVEAU_DIFFICULTE];
	    tNbJetonsMinijeuAleatoire[DIFFICULTE_FACILE] = 3;
	    tNbJetonsMinijeuAleatoire[DIFFICULTE_MOYEN] = 3;
	    tNbJetonsMinijeuAleatoire[DIFFICULTE_DIFFICILE] = 3;
	    tNbJetonsMinijeuAleatoire[DIFFICULTE_TRES_DIFFICILE] = 3;
	    
	    // Cr�er les tableaux pour les jetons magasins
	    tNbJetonsMagasinBase = new int[NOMBRE_NIVEAU_DIFFICULTE];
	    tNbJetonsMagasinBase[DIFFICULTE_FACILE] = 12;
	    tNbJetonsMagasinBase[DIFFICULTE_MOYEN] = 12;
	    tNbJetonsMagasinBase[DIFFICULTE_DIFFICILE] = 12;
	    tNbJetonsMagasinBase[DIFFICULTE_TRES_DIFFICILE] = 12;
	    tNbJetonsMagasinAleatoire = new int[NOMBRE_NIVEAU_DIFFICULTE];
	    tNbJetonsMagasinAleatoire[DIFFICULTE_FACILE] = 6;
	    tNbJetonsMagasinAleatoire[DIFFICULTE_MOYEN] = 6;
	    tNbJetonsMagasinAleatoire[DIFFICULTE_DIFFICILE] = 6;
	    tNbJetonsMagasinAleatoire[DIFFICULTE_TRES_DIFFICILE] = 6;
	    
	    // Initialiser les noms des joueurs virtuels        
       // String[] tNomsTemp = config.obtenirString("joueurs-virtuels.noms").split("/");
	   // tBanqueNomsJoueurVirtuels = tNomsTemp;
	    
	}

}

