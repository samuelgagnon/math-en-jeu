package ServeurJeu.ComposantesJeu.ReglesJeu;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class ReglesCaseSpeciale extends ReglesObjet
{
	// D�claration d'une variable qui va contenir le type de case
	private int intTypeCase;
	
	/**
	 * Constructeur de la classe ReglesCaseSpeciale qui permet d'initialiser
	 * la priorit� et le type de case.
	 * 
	 * @param int typeCase : Le type de case 
	 */
	public ReglesCaseSpeciale(int typeCase)
	{
		super();
		
		// Initialiser les propri�t�s de la r�gle de la case sp�ciale
		intTypeCase = typeCase;
	}
	
	/**
	 * Constructeur de la classe ReglesCaseSpeciale qui permet d'initialiser
	 * la priorit� et le type de case.
	 * 
	 * @param int priorite : La priorit� de cette r�gle
	 * @param int typeCase : Le type de case 
	 */
	public ReglesCaseSpeciale(int priorite, int typeCase)
	{
		super(priorite);
		
		// Initialiser les propri�t�s de la r�gle de la case sp�ciale
		intTypeCase = typeCase;
	}

	/**
	 * Cette fonction permet de retourner le type de case courant.
	 * 
	 * @return int : Le type de case de la case sp�ciale
	 */
	public int obtenirTypeCase()
	{
	   return intTypeCase;
	}
}