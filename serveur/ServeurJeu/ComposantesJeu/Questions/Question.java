package ServeurJeu.ComposantesJeu.Questions;

import java.text.Collator;
import java.util.Locale;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Question
{
    private static final Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);
	// D�claration d'une variable qui va contenir le code de la question
	private final int intCodeQuestion;
	
	// D�claration d'une variable qui va contenir l'URL de la question
	private final String strURLQuestion;
	
	// D�claration d'une variable qui va garder le type de la question
	// values - from 1 to 5 for now
	private final int objTypeQuestion;
	
	// D�claration d'une variable qui va contenir la r�ponse � la question
	private final String strReponse;
	
	// D�claration d'une variable qui va contenir l'url de l'explication de
	// la r�ponse
	private final String strURLExplication;
	
    /**
     *  D�claration d'une variable qui va contenir les keywords de la question
     */
	// we don't need it for the moment
	//private final LinkedList<Integer> intKeywords;
		
	/**
	 *  D�claration d'une variable qui va garder la difficult� de la question.
	 *	Peut avoir une valeur entre 1 et 6, que d�pend de niveau scolaire du
	 *  joueur pour cette categorie, si 0 est pas applicable pour joueur
     */
	private final int intDifficulte;
	 
	
	
  /**
	 * Constructeur de la classe Question qui initialise les propri�t�s de
	 * la question.
	 * 
	 * @param codeQuestion Le code de la question
	 * @param typeQuestion Le type de la question
	 * @param difficulte La difficulte de la question - entre 0 et 6
	 * @param urlQuestion Le URL de la question
	 * @param reponse La r�ponse � la question
	 * @param urlExplication Le URL de l'explication de la r�ponse
	 */
	public Question(int codeQuestion, int typeQuestion, int difficulte, String urlQuestion, 
			String reponse, String urlExplication)//, LinkedList<Integer> keywords)
	{
		// D�finir les propri�t�s des questions
		intCodeQuestion = codeQuestion;
		objTypeQuestion = typeQuestion;
		intDifficulte = difficulte;
		strURLQuestion = urlQuestion;
		strReponse = reponse.toLowerCase().replace(",",".");
		strURLExplication = urlExplication;
		//intKeywords = keywords;
	}
	
	/**
	 * Cette fonction retourne la reponse  de la question.
	 * 
	 * @return string : La reponse de la question
	 */
	public String getStringAnswer()
	{
		return strReponse;
	}
	
	/**
	 * Cette fonction retourne le code de la question.
	 * 
	 * @return int : Le code de la question
	 */
	public int obtenirCodeQuestion()
	{
		return intCodeQuestion;
	}

	
	/**
	 * Cette fonction retourne le type de la question.
	 * 
	 * @return String : Le type de la question
	 */
	public int obtenirTypeQuestion()
	{
		return objTypeQuestion;
	}
	
	/**
	 * Cette fonction retourne la difficult� de la question.
	 * 
	 * @return String : La difficult� de la question
	 */
	public int obtenirDifficulte()
	{
		return intDifficulte;
	}
	
	/**
	 * Cette fonction retourne le URL de la question courante.
	 * 
	 * @return String : Le URL de la question courante
	 */
	public String obtenirURLQuestion()
	{
		return strURLQuestion;
	}
	
	/**
	 * Cette fonction retourne si oui ou non la r�ponse est valide.
         * @param reponse R�ponse donn�e par le joueur
         * @param reponseCorrect R�ponse dans la BD
	 * 
	 * @return true si la r�ponse est valide
	 *         false sinon
	 */
	public boolean reponseEstValide(String reponse)
	{
            //En utilisant Collator.PRIMARY, les r�ponses sont consid�r�es
            //�quivalentes en ignorant la diff�rence d� aux accents, au nombre
            //d'espaces blancs (espace, tab) et � la capitalisation. Par example
            // "�T�, \t   hiver" est equivalent � "�te,h�v�r"
            // Cependant les erreures de ponctuation sont importantes, par example
            // "1,2,3" n'est pas �quivalent � "1;2;3" ni � "1-2-3"
            collator.setStrength(Collator.PRIMARY);
            int cmp = collator.compare(reponse, strReponse);
            System.out.println("Reponse du joueur: '" + reponse + "', reponse correcte: '" + strReponse + "', cmp == " + cmp);
            return cmp == 0;
	}
		 
	/**
	 * Cette fonction retourne le URL de l'explication de la r�ponse � la
	 * question courante.
	 * 
	 * @return String : Le URL de l'explication de la r�ponse
	 */
	public String obtenirURLExplication()
	{
		return strURLExplication;
	}
	
	/**
	 * Cette fonction retourne une mauvaise r�ponse. Utilis� lorsqu'un
	 * joueur utilise l'objet "Livre" qui permet d'�liminer un choix
	 * de r�ponse. Dans le cas d'une question sans choix de r�ponse, la 
	 * fonction retourne "PasUnChoixDeReponse"
	 */
	public  String obtenirMauvaiseReponse()
	{
		return "PasUnChoixDeReponse";
	}
	

	/**
	 * 
	 * @return
	 
	public LinkedList<Integer> getKeyword() 
	{
		return intKeywords;
	}*/
			
} // fin classe
