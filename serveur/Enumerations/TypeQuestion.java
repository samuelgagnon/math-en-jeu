package Enumerations;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class TypeQuestion
{
	// D�claration des membres de cette �num�ration
	public static final String ChoixReponse = "MULTIPLE_CHOICE"; 
	public static final String MiniDoku = "MINI_DOKU"; 
	public static final String ChoixReponse5 = "MULTIPLE_CHOICE_5"; 
	public static final String VraiFaux =  "TRUE_OR_FALSE";      
	public static final String ReponseCourte = "SHORT_ANSWER"; 
	
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
		return (valeur.equals(ChoixReponse) || valeur.equals(MiniDoku) || valeur.equals(ChoixReponse5) || valeur.equals(VraiFaux) || 
				valeur.equals(ReponseCourte));
	}
	
	public static String getValue(int type)
	{
		String resp = "";
		switch(type)
		{
		   case 1:
			   resp = ChoixReponse;
			   break;
		   case 2:
			   resp = VraiFaux;
			   break;
		   case 3:
			   resp = ReponseCourte;
		       break;
		   case 4:
			   resp = ChoixReponse5;
			   break;
		   case 5:
			   resp = MiniDoku;
			   break;
		   default: 
		       resp = "Error";
		   
		}
	   return resp;	
	}
}
