package ServeurJeu.ComposantesJeu.ReglesJeu;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class ReglesObjetUtilisable extends ReglesObjet
{
	// D�claration d'une variable qui va permettre de savoir si l'objet doit 
	// �tre toujours visible, jamais visible ou al�atoire
	private String strVisibilite;
	
	// D�claration d'une variable qui va contenir le nom de l'objet utilisable
	private String strNomObjetUtilisable;
	
	/**
	 * Constructeur de la classe ReglesObjetUtilisable qui permet d'initialiser
	 * la visibilit� et le nom de l'objet utilisable.
	 *
	 * @param String nom : Le nom de l'objet
	 * @param String visibilite : La visibilit� de l'objet
	 */
	public ReglesObjetUtilisable(String nom, String visibilite)
	{
		super();
		
		// Initialiser la visibilit� et le nom de l'objet
		strVisibilite = visibilite;
		strNomObjetUtilisable = nom;
	}
	
	/**
	 * Constructeur de la classe ReglesObjetUtilisable qui permet d'initialiser
	 * la priorit�, la visibilit� et le nom de l'objet.
	 * 
	 * @param int priorite : La priorit� pour cette r�gle
	 * @param String nom : Le nom de l'objet
	 * @param String visibilite : La visibilit� de l'objet
	 */
	public ReglesObjetUtilisable(int priorite, String nom, String visibilite)
	{
		super(priorite);
		
		// Initialiser la visibilit� et le nom de l'objet
		strVisibilite = visibilite;
		strNomObjetUtilisable = nom;
	}
	
	/**
	 * Cette fonction permet de retourner la visibilit� de l'objet.
	 * 
	 * @return String : La visibilit� de l'objet
	 */
	public String obtenirVisibilite()
	{
	   return strVisibilite;
	}
	
	/**
	 * Cette fonction permet de retourner le nom de l'objet.
	 * 
	 * @return String : Le nom de l'objet utilisable
	 */
	public String obtenirNomObjetUtilisable()
	{
	   return strNomObjetUtilisable;
	}
}