package ServeurJeu.ComposantesJeu.GenerateurPartie;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import ServeurJeu.BD.GestionnaireBDControleur;
import ServeurJeu.ComposantesJeu.Salle;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.ReglesJeu.Regles;
import ServeurJeu.ComposantesJeu.Tables.Table;

/**
 * @author Jean-Fran�ois Brind'Amour
 * last changed 31.12.2009 Oloieri Lilian
 */
public abstract class GenerateurPartie 
{
	// Cr�ation d'un objet permettant de g�n�rer des nombres al�atoires
	protected final static Random objRandom = new Random();
    		
	// D�claration de points
	protected Point objPoint;
	
	// D�claration d'une liste de points contenant les points qui ont 
	// �t� pass�s
	protected LinkedList<Point> lstPointsCasesPresentes;

	// D�claration d'une liste de points contenant les points qui 
	// contiennent des cases sp�ciales
	protected LinkedList<Point> lstPointsCasesSpeciales;
	
	// D�claration d'une liste de points contenant les points qui 
	// contiennent des cases de couleur
	protected LinkedList<Point> lstPointsCasesCouleur;
	
	// D�claration d'une liste de points contenant les points qui 
	// contiennent des magasins
	protected LinkedList<Point> lstPointsMagasins;
	
	// D�claration d'une liste de points contenant les points qui 
	// contiennent des pi�ces
	protected LinkedList<Point> lstPointsPieces;
	
	// D�claration d'une liste de points contenant les points qui 
	// contiennent des objets utilisables
	protected LinkedList<Point> lstPointsObjetsUtilisables;
		
	// D�claration d'un compteur de cases
	protected int intCompteurCases;

    // D�claration d'un compteur des id des objets
	protected int intCompteurIdObjet;
    
	// D�claration d'une case dont le type est -1 (�a n'existe pas) qui
	// va nous servir pour identifier les cases qui ont �t� pass�es
	protected CaseCouleur objCaseParcourue;
			
	// Nbs lines and columns in the table to be build   
	protected int intNbColumns;
	protected int intNbLines;
	
	protected Regles reglesPartie;
	
	// D�claration d'une r�f�rence vers le gestionnaire de bases de donn�es
	protected GestionnaireBDControleur objGestionnaireBD;
	
	// D�claration d'une r�f�rence vers la salle parente ou se trouve cet objet 
	protected Salle objSalle;
	
		
	
	// Constractor	
	protected GenerateurPartie() {
		super();
		//this.objRandom = 
		lstPointsCasesPresentes = new LinkedList<Point>();
		lstPointsCasesSpeciales = new LinkedList<Point>();
		lstPointsCasesCouleur = new LinkedList<Point>();
		lstPointsMagasins = new LinkedList<Point>();
		lstPointsPieces = new LinkedList<Point>();
		lstPointsObjetsUtilisables = new LinkedList<Point>();
		intCompteurCases = 0;
		intCompteurIdObjet = 1;
		objCaseParcourue = new CaseCouleur(1);
		reglesPartie = null;
		objGestionnaireBD = null;//salle.getObjControleurJeu().obtenirGestionnaireBD();
		objSalle = null;
		intNbColumns = 0;
		intNbLines = 0;
		
	}

	/**
     * Cette fonction permet de retourner une matrice � deux dimensions
     * repr�sentant le plateau de jeu qui contient les informations sur 
     * chaque case selon des param�tres.
     * @param lstPointsFinish 
     * @param Regles reglesPartie : L'ensemble des r�gles pour la partie
     * @param Vector listePointsCaseLibre : La liste des points des cases 
     * 										libres (param�tre de sortie)
     * @return Case[][] : Un tableau � deux dimensions contenant l'information
     * 					  sur chaque case.
     * @throws NullPointerException : Si la liste pass�e en param�tre qui doit 
     * 								  �tre remplie est nulle
     */
    public abstract Case[][] genererPlateauJeu(ArrayList<Point> lstPointsCaseLibre, ArrayList<Point> lstPointsFinish, 
    		Table table) throws NullPointerException;


	/**
	 * Method for game board
	 * @param intNbCasesSpeciales
	 * @param objttPlateauJeu
	 */
	//protected abstract void caseDefinition(int intNbCasesSpeciales, Case[][] objttPlateauJeu);

    /**
     * Method used to create the game board 
     * @param intNbTrous 
     * @param objttPlateauJeu
     */
	//protected abstract void boardCreation(int intNbTrous, Case[][] objttPlateauJeu);

  
    
    /**
     * Cette fonction permet de g�n�rer la position des joueurs. Chaque joueur 
     * est g�n�r� sur une case vide.
     * 
     * @param int nbJoueurs : Le nombre de joueurs dont g�n�rer la position
     * @param Vector listePointsCaseLibre : La liste des points des cases libres
     * @return Point[] : Un tableau de points pour chaque joueur 
     */
    public abstract Point[] genererPositionJoueurs(Table table, int nbJoueurs, ArrayList<Point> lstPointsCaseLibre);
    
    /**
     * @return the nbLines
     */
    public int getNbLines() {
        return intNbLines;
    }

    /**
     * @param nbLines the nbLines to set
     */
    public void setNbLines(int nbLines) {
        this.intNbLines = nbLines;
    }
 

    /**
     * @return the nbColumns
     */
    public int getNbColumns() {
        return intNbColumns;
    }

    /**
     * @param nbColumns the nbColumns to set
     */
    public void setNbColumns(int nbColumns) {
        this.intNbColumns = nbColumns;
    }
}