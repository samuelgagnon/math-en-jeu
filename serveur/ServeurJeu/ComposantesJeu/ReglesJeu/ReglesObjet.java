package ServeurJeu.ComposantesJeu.ReglesJeu;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public abstract class ReglesObjet
{
	// Cette variable va contenir le num�ro de priorit�
	protected int intPriorite;
	
	/**
	 * Constructeur de la classe ReglesObjet qui permet d'initialiser
	 * la priorit�.
	 */
	protected ReglesObjet()
	{
		// Initialiser la priorit� � -1 (cel� signifie que la priorit� 
		// importe peu
		intPriorite = -1;
	}
	
	/**
	 * Constructeur de la classe ReglesObjet qui permet d'initialiser
	 * la priorit�.
	 */
	protected ReglesObjet(int priorite)
	{
		// Initialiser la priorit�
		intPriorite = priorite;
	}

	/**
	 * Cette fonction permet de retourner la priorit� de la r�gle.
	 * 
	 * @return int : La priorit� de la r�gle
	 */
	public int obtenirPriorite()
	{
	   return intPriorite;
	}
}