package ServeurJeu.ComposantesJeu.ReglesJeu;

import java.util.TreeSet;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Regles
{
  // D�claration d'une liste qui contient les cases de couleur possibles (le 
  // contenu est un objet ReglesCaseCouleur)
  private TreeSet lstCasesCouleurPossibles;
  
  // D�claration d'une liste qui contient les magasins possibles (le 
  // contenu est un objet ReglesMagasin)
  private TreeSet lstMagasinsPossibles;
  
  // D�claration d'une liste qui contient les cases sp�ciales possibles (le 
  // contenu est un objet ReglesCaseSpeciale)
  private TreeSet lstCasesSpecialesPossibles;
  
  // D�claration d'une liste qui contient les objets utilisables possibles 
  // (le contenu est un objet ReglesObjetUtilisable)
  private TreeSet lstObjetsUtilisablesPossibles;
  
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
  
  private int maxPossessionPieceEtObjet;
  
  private int maxObjetsVente;

  
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
      lstCasesCouleurPossibles = new TreeSet(objReglesComparator);
      lstMagasinsPossibles = new TreeSet(objReglesComparator);
      lstCasesSpecialesPossibles = new TreeSet(objReglesComparator);
      lstObjetsUtilisablesPossibles = new TreeSet(objReglesComparator);
      
      // Initialiser les variables par d�faut
      bolPermetChat = true;
      fltRatioTrous = 0.0f;
      fltRatioMagasins = 0.0f;
      fltRatioCasesSpeciales = 0.0f;
      fltRatioPieces = 0.0f;
      fltRatioObjetsUtilisables = 0.0f;
      intValeurPieceMaximale = 0;
      intTempsMinimal = 0;
      intTempsMaximal = 0;
      intDeplacementMaximal = 1;
      maxPossessionPieceEtObjet = 10;
      maxObjetsVente = 4;
  }

  /**
   * Cette fonction permet de retourner la liste des cases de couleur 
   * possibles.
   * 
   * @return TreeSet : La liste des cases de couleur possibles
   */
  public TreeSet obtenirListeCasesCouleurPossibles()
  {
     return lstCasesCouleurPossibles;
  }
  
  /**
   * Cette fonction permet de retourner la liste des magasins possibles.
   * 
   * @return TreeSet : La liste des magasins possibles
   */
  public TreeSet obtenirListeMagasinsPossibles()
  {
     return lstMagasinsPossibles;
  }
  
  
  /**
   * Cette fonction permet de retourner la liste des cases sp�ciales 
   * possibles.
   * 
   * @return TreeSet : La liste des cases sp�ciales possibles
   */
  public TreeSet obtenirListeCasesSpecialesPossibles()
  {
     return lstCasesSpecialesPossibles;
  }
  
  /**
   * Cette fonction permet de retourner la liste des objets utilisables 
   * possibles.
   * 
   * @return TreeSet : La liste des objets utilisables possibles
   */
  public TreeSet obtenirListeObjetsUtilisablesPossibles()
  {
     return lstObjetsUtilisablesPossibles;
  }
  
  /**
   * Cette fonction permet de retourner si oui ou non on permet le chat.
   * 
   * @return boolean : true si on permet le chat
   *           false sinon
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
  
  public int obtenirMaxObjet() {
    return maxPossessionPieceEtObjet;
  }
  
  public void definirMaxObjet(int pMax) {
    maxPossessionPieceEtObjet = pMax;
  }
  
  public void definirMaxObjetsVente(int pMax) {
    maxObjetsVente = pMax;
  }
  
  public int obtenirMaxObjetsVente() {
    return maxObjetsVente;
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
}