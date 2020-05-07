package ClassesUtilitaires;

import java.util.Random;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class UtilitaireNombres 
{
	/**
	 * Constructeur par d�faut est priv� pour emp�cher de pourvoir cr�er des 
	 * instances de cette classe.
	 */
    private UtilitaireNombres() {}

    /**
     * Cette fonction permet de retourner si oui ou non la cha�ne pass�e en
     * param�tres est un chiffre positif (>= 0) ou non.
     *
     * @param String stringNumber : La cha�ne � v�rifier
     * @return boolean : true si stringNumber est un chiffre,
     *                   false sinon
     */
    public static boolean isPositiveNumber(String stringNumber)
    {
        // D�claration d'une variable bool�enne qui va permettre de savoir si
        // la cha�ne passe en param�tres est un chiffre ou non
        boolean bolEstChiffre = true;

        // D�claration d'un compteur
        int i = 0;

        // Boucler tant qu'on n'a pas trouv� un caract�re qui n'est pas un
        // chiffre
        while (i < stringNumber.length() && bolEstChiffre == true)
        {
            // Si le code ascii du caract�re courant est entre 48 et 57
            // (chiffres de 0 � 9), alors c'est un chiffre sinon ce n'en
            // n'est pas un
            bolEstChiffre = (((int) stringNumber.charAt(i)) >= 48 &&
                             ((int) stringNumber.charAt(i)) <= 57);

            i++;
        }

        return bolEstChiffre;
    }
    
    /**
     * Cette fonction permet de formater un chiffre pass� en param�tres
     * en lui ajoutant des caract�res de remplissages � gauche tant que
     * la longueur du chiffre minimale en termes de caract�res n'est pas
     * atteinte. Le champs fillCharacter peut contenir plus d'un caract�re.
     *
     * @param int numberToFormat : Le chiffre � formater
     * @param int minLength : La longueur minimale que le chiffre doit avoir
     * @param String fillCharacter : Le chiffre � formater
     * @return String : La cha�ne contenant le chiffre et les caract�res de
     *                  remplissage
     */
    public static String formatNumber(int numberToFormat, int minLength, String fillCharacter)
    {
        // D�claration d'une cha�ne qui va contenir le chiffre avec le nombre
        // appropri� de caract�res de remplissage
        String strChiffre = Integer.toString(numberToFormat);

        // Boucler tant qu'on n'a pas atteint la longueur minimum
        while (strChiffre.length() < minLength)
        {
            // On ajoute un caract�re de remplissage au d�but
            strChiffre = fillCharacter + strChiffre;
        }

        return strChiffre;
    }
    
    /**
     * Cette fonction permet de g�n�rer un entier � partir d'une loi normale.
     * La moyenne doit �tre plus grande que le double de la variance.
     * 
     * @param double moyenne : La moyenne
     * @param double variance : La variance
     * @return int : L'entier pris al�atoirement selon la loi normale
     */
    public static int genererNbAleatoireLoiNormale(double moyenne, double variance)
    {
    	// D�claration d'une variable qui va permettre de savoir si on a 
    	// trouv� les valeurs recherch�es
    	boolean bolTrouve = false;
    	
    	// Cr�er un objet permettant de g�n�rer des nombres al�atoires
    	Random objRandom = new Random();
    	
    	// D�clarations des variables utilis�es pour l'algorithme
    	double u1 = 0.0d;
    	double u2 = 0.0d;
    	double v1 = 0.0d;
    	double v2 = 0.0d;
    	double s = 0.0d;
    	double stDev = 0.0d;
    	double z = 0.0d;
    	double rndNormal = 0.0d;
        
        // On boucle tant qu'on n'a pas trouv� les bonnes valeurs 
    	// de u1, u2, v1, v2 et s
        while (bolTrouve == false)
        {
        	// On g�n�re les nombres al�atoires pour u1 et u2
            u1 = objRandom.nextFloat();
            u2 = objRandom.nextFloat();
        	
            // On calcule v1, v2 et s
            v1 = 2 * u1 - 1;
            v2 = 2 * u2 - 1;
            s = (v1 * v1) + (v2 * v2);
            
            // Si s est >= � 1, alors il faut obtenir d'autres valeurs 
            // al�atoires u1 et u2
            bolTrouve = (s < 1);
        }

        // Calculer la valeur normale
        z = Math.sqrt((-2 * Math.log(s)) / s) * v1;
        stDev = Math.sqrt(variance);
        rndNormal = (z * stDev) + moyenne;

     return (int) Math.round(rndNormal);   
    }
    
    public static int genererNbAleatoire(int max)
    {
    	Random objRandom = new Random();
    	return objRandom.nextInt(max);
    }
}