package Enumerations;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class TypeQuestion
{
	// D�claration des membres de cette �num�ration
	public static final String ChoixReponse = "ChoixReponse";
	public static final String VraiFaux = "VraiFaux";
	public static final String ReponseCourte = "ReponseCourte";
	
	/**
	 * Constructeur par d�faut est priv� pour emp�cher de pourvoir cr�er des 
	 * instances de cette classe.
	 */
	private TypeQuestion(){}
	
	/**
	 * Cette fonction statique permet de d�terminer si la valeur pass�e en 
	 * param�tres est un membre de cette �num�ration.
	 * 
	 * @param String valeur : la valeur du type de question � v�rifier
	 * @return boolean : true si la valeur est un membre de cette �num�ration
	 *                   false sinon
	 */
	public static boolean estUnMembre(String valeur)
	{
		// Si la valeur pass�e en param�tre n'est pas �gale � aucune des
		// valeurs d�finies dans cette classe, alors la valeur n'est pas
		// un membre de cette �num�ration, sinon elle en est un
		return (valeur.equals(ChoixReponse) || valeur.equals(VraiFaux) || 
				valeur.equals(ReponseCourte));
	}
}
