package ClassesUtilitaires;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class UtilitaireEncodeurDecodeur 
{
	/**
	 * Constructeur par d�faut est priv� pour emp�cher de pourvoir cr�er des 
	 * instances de cette classe.
	 */
    private UtilitaireEncodeurDecodeur() {}

    /**
     * Cette fonction permet d'encoder la cha�ne pass�e en param�tres en
     * caract�res UTF-8. Cet encodage n'accepte que les caract�res de 0 � 127.
     * Tous les autres caract�res seront remplac�s par leur code &#ascii;
     *
     * @param String stringToEncode : La cha�ne � encoder en UTF-8
     * @return String : La cha�ne encod�e
     */
    public static String encodeToUTF8(String stringToEncode)
    {
        // D�claration d'une cha�ne qui va contenir la version encod�e en UTF-8
        // de la cha�ne pass�e en param�tres
        StringBuilder strChaineEncodee = new StringBuilder();

        // Si la cha�ne contient au moins un caract�re, alors on peut l'encoder
        if (stringToEncode != null && stringToEncode.equals("") == false)
        {
            // Passer tous les caract�res de la cha�ne et transformer en
            // codes ascii ceux dont le code est sup�rieur � 127
            for (int i = 0; i < stringToEncode.length(); i++)
            {
                // Si le code ascii du caract�re courant est plus grand que 127
                // alors on doit le transformer en code ascii, sinon on le
                // laisse tel quel
                if (((int) stringToEncode.charAt(i)) > 127)
                {
                    strChaineEncodee.append("&#");
                    strChaineEncodee.append(UtilitaireNombres.formatNumber(((int) stringToEncode.charAt(i)), 3, "0"));
                    strChaineEncodee.append(";");
                }
                else
                {
                    strChaineEncodee.append(stringToEncode.charAt(i));
                }
            }
        }

        return strChaineEncodee.toString();
    }

    /**
     * Cette fonction permet de d�coder la cha�ne pass�e en param�tres. On
     * va remplacer tous les cara�tres �cris en ascii &#ascii; en un v�ritable
     * caract�re.
     *
     * @param String stringToDecode : La cha�ne UTF-8 � d�coder
     * @return String : La cha�ne d�cod�e
     */
    public static String decodeFromUTF8(String stringToDecode)
    {
        // D�claration d'une cha�ne qui va contenir la version d�cod�e
        // de la cha�ne UTF-8 pass�e en param�tres
        StringBuilder strChaineDecodee = new StringBuilder();

        // Si la cha�ne contient au moins un caract�re, alors on peut la
        // d�coder
        if (stringToDecode != null && stringToDecode.equals("") == false)
        {
            // Passer tous les caract�res et transformer ceux qui sont
            // sous la forme &#xxx;
            for (int i = 0; i < stringToDecode.length(); i++)
            {
                // Si on a bien trouv� un code de caract�re, alors on va
                // ajouter le caract�re correspondant dans la cha�ne
                if (i <= stringToDecode.length() - 6 &&
                    stringToDecode.charAt(i) == '&' &&
                    stringToDecode.charAt(i + 1) == '#' &&
                    stringToDecode.charAt(i + 5) == ';' &&
                    UtilitaireNombres.isPositiveNumber(stringToDecode.substring(i + 2, i + 5)) == true)
                {
                    // Obtenir le caract�re correspondant au code ASCII courant
                    strChaineDecodee.append(((char) Integer.parseInt(stringToDecode.substring(i + 2, i + 5))));

                    // Augmenter le i pour ne pas retraiter le m�me caract�re
                    i += 5;
                }
                else
                {
                    strChaineDecodee.append(stringToDecode.charAt(i));
                }
            }
        }

        return strChaineDecodee.toString();
    }
}
