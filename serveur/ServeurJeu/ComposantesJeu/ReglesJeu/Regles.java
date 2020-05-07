package ServeurJeu.ComposantesJeu.ReglesJeu;

import java.util.TreeSet;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Regles
{
	// D�claration d'une liste qui contient les cases de couleur possibles (le 
	// contenu est un objet ReglesCaseCouleur)
	//private TreeSet<CaseCouleur> lstCasesCouleurPossibles;
	
	// D�claration d'une liste qui contient les magasins possibles (le 
	// contenu est un objet ReglesMagasin)
	private final TreeSet<ReglesMagasin> lstMagasinsPossibles;
	
	// D�claration d'une liste qui contient les cases sp�ciales possibles (le 
	// contenu est un objet ReglesCaseSpeciale)
	//private TreeSet<CaseSpeciale> lstCasesSpecialesPossibles;
	
	// D�claration d'une liste qui contient les objets utilisables possibles 
	// (le contenu est un objet ReglesObjetUtilisable)
	private final TreeSet<ReglesObjetUtilisable> lstObjetsUtilisablesPossibles;
	
	// Cette variable permet de savoir si on permet le chat ou non
	private boolean bolPermetChat;
	
	// Cette variable va contenir le ratio de trous par rapport au nombre 
	// total de cases
	private float fltRatioTrous;
	
	// Cette variable va contenir le ratio de magasins par rapport au 
	// nombre total de cases
	private float fltRatioMagasins;
	
	// Cette variable va contenir le ratio de cases sp�ciales par rapport au 
	// nombre total de cases
	private float fltRatioCasesSpeciales;
	
	// Cette variable va contenir le ratio de pi�ces par rapport au 
	// nombre total de cases
	private float fltRatioPieces;
	
	// Cette variable va contenir le ratio d'objets utilisables par rapport au 
	// nombre total de cases
	private float fltRatioObjetsUtilisables;
	
	// Cette variable va contenir la valeur de la pi�ce la plus �lev�e
	private int intValeurPieceMaximale;
	
	// Cette variable va contenir le temps minimal (minutes) d'une partie
	private int intTempsMinimal;
	
	// Cette variable va contenir le temps maximal (minutes) d'une partie
	private int intTempsMaximal;
	
	// Cette variable va contenir le nombre de cases maximales dont le joueur
	// peut se d�placer (minimum de 1)
	private int intDeplacementMaximal;
	
	// Max of objects that shop can sale
	private int intMaxSaledObjects;
	
	//Room is active in tournament and you enter directly in a board of this room
	private boolean bolMoneyPermit;
	
	// show or not the question number in client
	private boolean showNumber;
	
	//max number of players that game can hold
	private int maxNbPlayers;
		
	//This is the number of tracks of the board of the game of type "Tournament" or "Course"
	private int nbTracks;
	
	//This is the number of virtual players for the game. Defined in DB in room options
	private int nbVirtualPlayers;
	
	/**
	 * Constructeur de la classe Regles qui permet d'initialiser
	 * les r�gles.
	 */
	public Regles()
	{
		// Cr�er un nouveau comparateur de ReglesObjet
		ReglesComparator objReglesComparator = new ReglesComparator();
		
	    // Cr�er les listes de couleurs possibles, cases sp�ciales possibles,
		// d'objets utilisables possibles et de magasins possibles
	    
	    lstMagasinsPossibles = new TreeSet<ReglesMagasin>(objReglesComparator);
	    //lstCasesSpecialesPossibles = new TreeSet<CaseSpeciale>(objReglesComparator);
	    //lstCasesCouleurPossibles = new TreeSet<CaseCouleur>(objReglesComparator);
	    lstObjetsUtilisablesPossibles = new TreeSet<ReglesObjetUtilisable>(objReglesComparator);
	    
	    // Initialiser les variables par d�faut
	    bolPermetChat = true;
	    bolMoneyPermit = false;
	    //fltRatioTrous = 0.0f;
	    //fltRatioMagasins = 0.0f;
	    //fltRatioCasesSpeciales = 0.0f;
	    //fltRatioPieces = 0.0f;
	    //fltRatioObjetsUtilisables = 0.0f;
	    //intValeurPieceMaximale = 0;
	    //intTempsMinimal = 0;
	    //intTempsMaximal = 0;
	    intDeplacementMaximal = 1;
	    //intMaxSaledObjects = 0;
	    //tournamentState = false;
	    showNumber = true;
	    setNbTracks(4);
	    setNbVirtualPlayers(0);
	}

	/*
	 * Cette fonction permet de retourner la liste des cases de couleur 
	 * possibles.
	 * 
	 * @return TreeSet : La liste des cases de couleur possibles
	
	public TreeSet<CaseCouleur> obtenirListeCasesCouleurPossibles()
	{
	   return lstCasesCouleurPossibles;
	} */
	
	/**
	 * Cette fonction permet de retourner la liste des magasins possibles.
	 * 
	 * @return TreeSet : La liste des magasins possibles
	 */
	public TreeSet<ReglesMagasin> obtenirListeMagasinsPossibles()
	{
	   return lstMagasinsPossibles;
	}
	
	/*
	 * Cette fonction permet de retourner la liste des cases sp�ciales 
	 * possibles.
	 * 
	 * @return TreeSet : La liste des cases sp�ciales possibles
	
	public TreeSet<CaseSpeciale> obtenirListeCasesSpecialesPossibles()
	{
	   return lstCasesSpecialesPossibles;
	} */
	
	/**
	 * Cette fonction permet de retourner la liste des objets utilisables 
	 * possibles.
	 * 
	 * @return TreeSet : La liste des objets utilisables possibles
	 */
	public TreeSet<ReglesObjetUtilisable> obtenirListeObjetsUtilisablesPossibles()
	{
	   return lstObjetsUtilisablesPossibles;
	}
	
	/**
	 * Cette fonction permet de retourner si oui ou non on permet le chat.
	 * 
	 * @return boolean : true si on permet le chat
	 * 					 false sinon
	 */
	public boolean obtenirPermetChat()
	{
	   return bolPermetChat;
	}
	
	/**
	 * Cette fonction permet de d�finir si oui ou non on veut le chat.
	 * 
	 * @param boolean chat : Permet de savoir si on permet le chat ou non
	*/
	public void definirPermetChat(boolean chat)
	{
	   bolPermetChat = chat;
	} 
	
	/**
	 * Cette fonction permet de retourner le ratio de trous.
	 * 
	 * @return float : Le ratio de trous (de 0 � 1)
	 */
	public float obtenirRatioTrous()
	{
	   return fltRatioTrous;
	}
	
	/**
	 * Cette fonction permet de d�finir le ratio de trous.
	 * 
	 * @param float ratio : Le ratio de trous (entre 0 et 1)
	 */
	public void definirRatioTrous(float ratio)
	{
		fltRatioTrous = ratio;
	}
	
	/**
	 * Cette fonction permet de retourner le ratio de magasins.
	 * 
	 * @return float : Le ratio de magasins (de 0 � 1)
	 */
	public float obtenirRatioMagasins()
	{
	   return fltRatioMagasins;
	}
	
	/**
	 * Cette fonction permet de d�finir le ratio de magasins.
	 * 
	 * @param float ratio : Le ratio de magasins (entre 0 et 1)
	*/
	public void definirRatioMagasins(float ratio)
	{
		fltRatioMagasins = ratio;
	} 
	
	/**
	 * Cette fonction permet de retourner le ratio de cases sp�ciales.
	 * 
	 * @return float : Le ratio de cases sp�ciales (de 0 � 1)
	 */
	public float obtenirRatioCasesSpeciales()
	{
	   return fltRatioCasesSpeciales;
	}
	
	/**
	 * Cette fonction permet de d�finir le ratio de cases sp�ciales.
	 * 
	 * @param float ratio : Le ratio de cases sp�ciales (entre 0 et 1)
	 */
	public void definirRatioCasesSpeciales(float ratio)
	{
		fltRatioCasesSpeciales = ratio;
	}
	
	/**
	 * Cette fonction permet de retourner le ratio de pi�ces.
	 * 
	 * @return float : Le ratio de pi�ces (de 0 � 1)
	 */
	public float obtenirRatioPieces()
	{
	   return fltRatioPieces;
	}
	
	/**
	 * Cette fonction permet de d�finir le ratio de pi�ces.
	 * 
	 * @param float ratio : Le ratio de pi�ces (entre 0 et 1)
	 */
	public void definirRatioPieces(float ratio)
	{
		fltRatioPieces = ratio;
	}
	
	/**
	 * Cette fonction permet de retourner le ratio d'objets utilisables.
	 * 
	 * @return float : Le ratio d'objets utilisables (de 0 � 1)
	 */
	public float obtenirRatioObjetsUtilisables()
	{
	   return fltRatioObjetsUtilisables;
	}
	
	/**
	 * Cette fonction permet de d�finir le ratio d'objets utilisables.
	 * 
	 * @param float ratio : Le ratio d'objets utilisables (entre 0 et 1)
	 */
	public void definirRatioObjetsUtilisables(float ratio)
	{
		fltRatioObjetsUtilisables = ratio;
	}
	
	/**
	 * Cette fonction permet de retourner la valeur qu'une pi�ce maximale 
	 * peut avoir durant le jeu.
	 * 
	 * @return int : La valeur maximale possible qu'une pi�ce peut avoir
	 */
	public int obtenirValeurPieceMaximale()
	{
	   return intValeurPieceMaximale;
	}
	
	/**
	 * Cette fonction permet de d�finir la valeur maximale d'une pi�ce de jeu.
	 * 
	 * @param int valeur : La valeur maximale d'une pi�ce
	 */
	public void definirValeurPieceMaximale(int valeur)
	{
		intValeurPieceMaximale = valeur;
	}
	
	/**
	 * Cette fonction permet de retourner le temps minimal permis 
	 * pour une partie en minutes.
	 * 
	 * @return int : Le temps minimal d'une partie en minutes
	 */
	public int obtenirTempsMinimal()
	{
	   return intTempsMinimal;
	}
	
	/**
	 * Cette fonction permet de d�finir le temps minimal d'une partie en minutes.
	 * 
	 * @param int temps : Le temps minimal d'une partie en minutes
	 */
	public void definirTempsMinimal(int temps)
	{
		intTempsMinimal = temps;
	}
	
	/**
	 * Cette fonction permet de retourner le temps maximal permis 
	 * pour une partie en minutes.
	 * 
	 * @return int : Le temps maximal d'une partie en minutes
	 */
	public int obtenirTempsMaximal()
	{
	   return intTempsMaximal;
	}
	
	/**
	 * Cette fonction permet de d�finir le temps maximal d'une partie en minutes.
	 * 
	 * @param int temps : Le temps maximal d'une partie en minutes
	 */
	public void definirTempsMaximal(int temps)
	{
		intTempsMaximal = temps;
	}
	
	/**
	 * Cette fonction permet de retourner le d�placement maximal permis 
	 * pour chaque joueur.
	 * 
	 * @return int : Le d�placement maximal d'un joueur
	 */
	public int obtenirDeplacementMaximal()
	{
	   return intDeplacementMaximal;
	}
	
	/**
	 * Cette fonction permet de d�finir le d�placement maximal des joueurs.
	 * 
	 * @param int deplacement : Le d�placement maximal d'un joueur
	 */
	public void definirDeplacementMaximal(int deplacement)
	{
		intDeplacementMaximal = deplacement;
	}

	public void setIntMaxSaledObjects(int intMaxSaledObjects) {
		this.intMaxSaledObjects = intMaxSaledObjects;
	}

	public int getIntMaxSaledObjects() {
		return intMaxSaledObjects;
	}
/*
	public void setTournamentState(boolean tournamentActive) {
		this.tournamentState = tournamentActive;
	}

	public boolean getTournamentState() {
		return tournamentState;
	}
*/
	public void setShowNumber(boolean showNumber) {
		this.showNumber = showNumber;
	}

	public boolean getShowNumber() {
		return showNumber;
	}

	public void setMaxNbPlayers(int maxNbPlayers) {
		this.maxNbPlayers = maxNbPlayers;
	}

	public int getMaxNbPlayers() {
		return maxNbPlayers;
	}

	/*public void setMaxNbObjectsAndMoney(int maxNbObjectsAndMoney) {
		this.maxNbObjectsAndMoney = maxNbObjectsAndMoney;
	}

	public int getMaxNbObjectsAndMoney() {
		return maxNbObjectsAndMoney;
	}*/

	public void setNbTracks(int nbTracks) {
		this.nbTracks = nbTracks;
	}

	public int getNbTracks() {
		return nbTracks;
	}

	/**
	 * @return the nbVirtualPlayers
	 */
	public int getNbVirtualPlayers() {
		return nbVirtualPlayers;
	}

	/**
	 * @param nbVirtualPlayers the nbVirtualPlayers to set
	 */
	public void setNbVirtualPlayers(int nbVirtualPlayers) {
		this.nbVirtualPlayers = nbVirtualPlayers;
	}
	
	/**
	 * @return the bolMoneyPermit
	 */
	public boolean isBolMoneyPermit() {
		return bolMoneyPermit;
	}

	/**
	 * @param bolMoneyPermit the bolMoneyPermit to set
	 */
	public void setBolMoneyPermit(boolean bolMoneyPermit) {
		this.bolMoneyPermit = bolMoneyPermit;
	}
}