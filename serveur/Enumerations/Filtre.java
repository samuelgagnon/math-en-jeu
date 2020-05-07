package Enumerations;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class Filtre
{
	// D�claration des membres de cette �num�ration
	public static final String Toutes = "Toutes";
	public static final String CompletesNonCommencees = "CompletesNonCommencees";
	public static final String CompletesCommencees = "CompletesCommencees";
	public static final String IncompletesNonCommencees = "IncompletesNonCommencees";
	public static final String IncompletesCommencees = "IncompletesCommencees";
	
	/**
	 * Constructeur par d�faut est priv� pour emp�cher de pourvoir cr�er des 
	 * instances de cette classe.
	 */
	private Filtre(){}
	
	/**
	 * Cette fonction statique permet de d�terminer si la valeur pass�e en 
	 * param�tres est un membre de cette �num�ration.
	 * 
	 * @param String valeur : la valeur de filtre � v�rifier
	 * @return boolean : true si la valeur est un membre de cette �num�ration
	 *                   false sinon
	 */
	public static boolean estUnMembre(String valeur)
	{
		// Si la valeur pass�e en param�tre n'est pas �gale � aucune des
		// valeurs d�finies dans cette classe, alors la valeur n'est pas
		// un membre de cette �num�ration, sinon elle en est un
		return (valeur.equals(Toutes) || valeur.equals(CompletesNonCommencees) || 
				valeur.equals(CompletesCommencees) || valeur.equals(IncompletesNonCommencees) ||
				valeur.equals(IncompletesCommencees));
	}
}
