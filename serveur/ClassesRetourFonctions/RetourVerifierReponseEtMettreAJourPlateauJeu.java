package ClassesRetourFonctions;

import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import java.awt.Point;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class RetourVerifierReponseEtMettreAJourPlateauJeu
{
	// D�claration d'une variable qui va contenir si oui ou non le d�placement
	// a �t� accept� � cause que la r�ponse �tait bonne
	private boolean bolDeplacementAccepte;
	
	// D�claration d'une variable qui va contenir l'explication du r�sultat
	private String strExplications;
	
	// D�claration d'une variable qui va contenir la r�f�rence vers l'objet
	// ramass�
	private ObjetUtilisable objObjetRamasse;
	
	// D�claration d'une variable qui va contenir la r�f�rence vers l'objet
	// que le joueur a subi
	private ObjetUtilisable objObjetSubi;
	
	// D�claration d'une r�f�rence vers un magasin que le joueur rencontre
	private Magasin objMagasin;
	
	//TODO: Il ne serait pas n�cessaire de retourner la nouvelle position
	// 		mais on doit la retourner � cause du client 
	private Point objNouvellePosition;
	
	// D�claration d'une variable qui va contenir le nouveau pointage
	private final int intNouveauPointage;
        
    private final int intNouvelArgent;
	
	private String strCollision;
	
	private final int playerBonus;
	
	/**
	 * Constructeur de la classe RetourVerifierReponseEtMettreAJourPlateauJeu.
	 * @param bonus 
	 * 
	 * @param boolean deplacementAccepte : Permet de savoir si le d�placement a �t� accept� ou non
	 * @param int nouveauPointage : Le nouveau pointage du joueur
	 */
	public RetourVerifierReponseEtMettreAJourPlateauJeu(boolean deplacementAccepte, int nouveauPointage, int nouvelArgent, int bonus)
	{
		// Initialiser les membres de la classe de retour
		bolDeplacementAccepte = deplacementAccepte;
		//strExplications = "";
		//objObjetRamasse = null;
		//objObjetSubi = null;
		//objNouvellePosition = null;
		intNouveauPointage = nouveauPointage;
        intNouvelArgent = nouvelArgent;
		//strCollision = "vide";
		playerBonus = bonus;
	}
	
	/**
	 * @return the playerBonus
	 */
	public int getPlayerBonus() {
		return playerBonus;
	}

	

	/**
	 * Cette fonction retourne si oui ou non le d�placement est accept�.
	 * 
	 * @return boolean : true si la r�ponse �tait bonne et que le d�placement
	 * 						est accept�
	 * 					 false sinon
	 */
	public boolean deplacementEstAccepte()
	{
		return bolDeplacementAccepte;
	}
	
	/**
	 * Cette fonction retourne l'url de l'explication de la r�ponse.
	 * 
	 * @return String : L'Url de l'explication de la r�ponse
	 */
	public String obtenirExplications()
	{
		return strExplications;
	}
	
	/**
	 * Cette m�thode d�finit l'url de l'explication de la r�ponse.
	 * 
	 * @param String explications : L'url de l'explication de la r�ponse
	 */
	public void definirExplications(String explications)
	{
		strExplications = explications;
	}
	
	/**
	 * Cette fonction retourne l'objet ramass� sur la case
	 * 
	 * @return ObjetUtilisable : L'objet ramass� sur la case
	 */
	public ObjetUtilisable obtenirObjetRamasse()
	{
		return objObjetRamasse;
	}
	
	/**
	 * Cette m�thode d�finit l'objet ramass� sur la case.
	 * 
	 * @param ObjetUtilisable objetRamasse : L'objet ramass�
	 */
	public void definirObjetRamasse(ObjetUtilisable objetRamasse)
	{
		objObjetRamasse = objetRamasse;
	}
	
	/**
	 * Cette fonction retourne l'objet subi sur la case.
	 * 
	 * @return ObjetUtilisable : L'objet subi sur la case
	 */
	public ObjetUtilisable obtenirObjetSubi()
	{
		return objObjetSubi;
	}
	
	/**
	 * Cette m�thode d�finit l'objet subi sur la case.
	 * 
	 * @param ObjetUtilisable objetSubi : L'objet subi
	 */
	public void definirObjetSubi(ObjetUtilisable objetSubi)
	{
		objObjetSubi = objetSubi;
	}
	
	/**
	 * Cette fonction retourne la nouvelle position du joueur.
	 * 
	 * @return Point : La nouvelle position du joueur
	 */
	public Point obtenirNouvellePosition()
	{
		return objNouvellePosition;
	}
	
	/**
	 * Cette m�thode d�finit la nouvelle position du joueur.
	 * 
	 * @param Point nouvellePosition : La nouvelle position du joueur
	 */
	public void definirNouvellePosition(Point nouvellePosition)
	{
		objNouvellePosition = nouvellePosition;
	}
	
	/**
	 * Cette fonction retourne le nouveau pointage du joueur.
	 * 
	 * @return int : Le nouveau pointage du joueur
	 */
	public int obtenirNouveauPointage()
	{
		return intNouveauPointage;
	}
	
	

    public int obtenirNouvelArgent()
    {
        return intNouvelArgent;
    }
	
	public String obtenirCollision()
	{
		return strCollision;
	}
	
	public void definirCollision( String collision )
	{
		strCollision = collision;
	}
	
	public Magasin obtenirMagasin()
	{
		return objMagasin;
	}
	
	public void definirMagasin(Magasin mag)
    {
    	objMagasin = mag;
    }	
}
