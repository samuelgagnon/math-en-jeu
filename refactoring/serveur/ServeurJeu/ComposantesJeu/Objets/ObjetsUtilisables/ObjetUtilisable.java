package ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables;

import ServeurJeu.ComposantesJeu.Objets.Objet;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class ObjetUtilisable extends Objet
{
	// D�claration d'une variable qui va garder si oui ou non l'objet 
	// courant est visible sur la case ou non
	protected boolean bolEstVisible;
	
	// D�claration d'une variable qui va garder le num�ro d'identification 
	// de l'objet courant
	protected int intId;
	
	// D�claration d'une variable qui contient un num�ro id identifiant le
	// type d'objet
	protected int intUID;
	
	// Le prix de l'objet utilsable
	protected int intPrix;
	
	// Indique si l'objet peut �tre arm�
	protected boolean bolPeutEtreArme;
	
	// Indique si l'objet se vend de fa�on illimit� dans les magasins
	protected boolean bolEstLimite;
	
	// Le type de l'objet en cha�ne de caract�res, utilis� de fa�on
	// unique pour communiquer avec le client
	protected String strTypeObjet;
	
	/**
	 * Constructeur de la classe ObjetUtilisable qui permet d'initialiser 
	 * les propri�t�s de l'objet courant.
	 * 
	 * @param in id : Le num�ro d'identification de l'objet
	 * @param boolean estVisible : Permet de savoir si l'objet doit �tre 
	 * 							   visible ou non
	 */
	public ObjetUtilisable(int id, boolean estVisible, int intUniqueId,
	    int prix, boolean peutEtreArme, boolean estLimite, String typeObjet)
	{
		// D�finir les propri�t�s de l'objet courant
		intId = id;
		bolEstVisible = estVisible;
		intUID = intUniqueId;
		intPrix = prix;
		bolPeutEtreArme = peutEtreArme;
		bolEstLimite = estLimite;
		strTypeObjet = typeObjet;
	}
	
	/**
	 * Cette fonction permet de retourner le Id g�n�r� pour l'objet utilisable 
	 * courant.
	 * 
	 * @return int : Le Id de l'objet
	 */
	public int obtenirId()
	{
	   return intId;
	}
	
	/**
	 * Cette fonction permet de retourner si oui ou non l'objet est visible.
	 * 
	 * @return boolean : true si l'objet est visible
	 * 					 false sinon
	 */
	public boolean estVisible()
	{
	   return bolEstVisible;
	}
	
	public int obtenirUniqueId()
	{
		return intUID;
	}
	
	public int obtenirPrix()
	{
		return intPrix;
	}
	
	public boolean obtenirEstLimite()
	{
		return bolEstLimite;
	}
	
	public boolean obtenirPeutEtreArme()
	{
		return bolPeutEtreArme;
	}
	
	public String obtenirTypeObjet()
	{
		return strTypeObjet;
	}
}