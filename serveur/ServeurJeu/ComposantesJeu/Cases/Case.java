package ServeurJeu.ComposantesJeu.Cases;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public abstract class Case
{
	// D�claration d'une variable qui va contenir le type de case
	protected final int intTypeCase;
	
	/**
	 * Constructeur de la classe abstraite Case.
	 * 
	 * @param int typeCase : Le type de la case � cr�er
	 */
	protected Case(int typeCase)
	{
		// D�finir le type de la case
		intTypeCase = typeCase;
	}
	
	/**
	 * Cette fonction retourne le type de la case.
	 * 
	 * @return int : le type de la case
	 */
	public int obtenirTypeCase()
	{
		return intTypeCase;
	}
}
