package ServeurJeu.ComposantesJeu.ReglesJeu;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class ReglesCaseCouleur extends ReglesObjet
{
	// D�claration d'une variable qui va contenir le type de case
	private int intTypeCase;
	
	/**
	 * Constructeur de la classe ReglesCaseCouleur qui permet d'initialiser
	 * la priorit� et le type de case.
	 * 
	 * @param int typeCase : Le type de case 
	 */
	public ReglesCaseCouleur(int typeCase)
	{
		super();
		
		// Initialiser les propri�t�s de la r�gle de la case couleur
		intTypeCase = typeCase;
	}
	
	/**
	 * Constructeur de la classe ReglesCaseCouleur qui permet d'initialiser
	 * la priorit� et le type de case.
	 * 
	 * @param int priorite : La priorit� de cette r�gle
	 * @param int typeCase : Le type de case 
	 */
	public ReglesCaseCouleur(int priorite, int typeCase)
	{
		super(priorite);
		
		// Initialiser les propri�t�s de la r�gle de la case couleur
		intTypeCase = typeCase;		
	}

	/**
	 * Cette fonction permet de retourner le type de case courant.
	 * 
	 * @return int : Le type de case de la case couleur
	 */
	public int obtenirTypeCase()
	{
	   return intTypeCase;
	}
}