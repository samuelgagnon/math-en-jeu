package Enumerations;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class Commande
{
	// D�claration des diff�rentes commandes possibles trait�es par le serveur
	public static final String Connexion = "Connexion";
	public static final String Deconnexion = "Deconnexion";
	public static final String ObtenirListeJoueurs = "ObtenirListeJoueurs";
	public static final String ObtenirListeSalles = "ObtenirListeSalles";
	public static final String EntrerSalle = "EntrerSalle";
	public static final String QuitterSalle = "QuitterSalle";
	public static final String ObtenirListeJoueursSalle = "ObtenirListeJoueursSalle";
	public static final String ObtenirListeTables = "ObtenirListeTables";
	public static final String CreerTable = "CreerTable";
	public static final String EntrerTable = "EntrerTable";
	public static final String QuitterTable = "QuitterTable";
	public static final String DemarrerMaintenant = "DemarrerMaintenant";
	public static final String DemarrerPartie = "DemarrerPartie";
	public static final String DeplacerPersonnage = "DeplacerPersonnage";
	public static final String RepondreQuestion = "RepondreQuestion";
	public static final String Pointage = "Pointage";
        public static final String RejoindrePartie = "RejoindrePartie";
        public static final String NePasRejoindrePartie = "NePasRejoindrePartie";
        public static final String UtiliserObjet = "UtiliserObjet";
        public static final String AcheterObjet = "AcheterObjet";
        public static final String Argent = "Argent";
        public static final String ChatMessage = "ChatMessage";
    
	/**
	 * Constructeur par d�faut est priv� pour emp�cher de pourvoir cr�er des 
	 * instances de cette classe.
	 */
	private Commande(){}
	
	/**
	 * Cette fonction statique permet de d�terminer si la valeur pass�e en 
	 * param�tres est un membre de cette �num�ration.
	 * 
	 * @param String valeur : La valeur � v�rifier
	 * @return boolean : true si la valeur est un membre de cette �num�ration
	 *                   false sinon
	 */
	public static boolean estUnMembre(String valeur)
	{
		// Si la valeur pass�e en param�tre n'est pas �gale � aucune des
		// valeurs d�finies dans cette classe, alors la valeur n'est pas
		// un membre de cette �num�ration, sinon elle en est un
		return (valeur.equals(Connexion) || valeur.equals(Deconnexion) || 
		        valeur.equals(ObtenirListeJoueurs) || valeur.equals(ObtenirListeSalles) || 
		        valeur.equals(EntrerSalle) || valeur.equals(QuitterSalle) || 
		        valeur.equals(ObtenirListeJoueursSalle) || valeur.equals(ObtenirListeTables) ||
		        valeur.equals(CreerTable) || valeur.equals(EntrerTable) ||
		        valeur.equals(QuitterTable) || valeur.equals(DemarrerPartie) ||
		        valeur.equals(DeplacerPersonnage) || valeur.equals(RepondreQuestion) ||
		        valeur.equals(DemarrerMaintenant) || valeur.equals(Pointage) ||
		        valeur.equals(RejoindrePartie) || valeur.equals(UtiliserObjet) ||
		        valeur.equals(AcheterObjet) || valeur.equals(Argent) || valeur.equals(ChatMessage));
	}
}
