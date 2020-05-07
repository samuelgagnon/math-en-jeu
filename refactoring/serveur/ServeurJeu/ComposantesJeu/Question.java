package ServeurJeu.ComposantesJeu;

import ClassesUtilitaires.UtilitaireNombres;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class Question
{
	// D�claration d'une variable qui va contenir le code de la question
	private int intCodeQuestion;
	
	// D�claration d'une variable qui va contenir l'URL de la question
	private String strURLQuestion;
	
	// D�claration d'une variable qui va garder le type de la question
	private String objTypeQuestion;
	
	// D�claration d'une variable qui va garder la difficult� de la question
	private int intDifficulte;
	
	// D�claration d'une variable qui va contenir la r�ponse � la question
	private String strReponse;
	
	// D�claration d'une variable qui va contenir l'url de l'explication de 
	// la r�ponse
	private String strURLExplication;
	
    // D�claration d'une variable qui va contenir la cat�gorie de la question
	private int intCategorie;
	
	/**
	 * Constructeur de la classe Question qui initialise les propri�t�s de 
	 * la question.
	 * 
	 * @param int codeQuestion : Le code de la question
	 * @param String typeQuestion : Le type de la question
	 * @param int difficulte : La difficulte de la question
	 * @param String urlQuestion : Le URL de la question
	 * @param String reponse : La r�ponse � la question
	 * @param String urlExplication : Le URL de l'explication de la r�ponse
	 */
	public Question(int codeQuestion, String typeQuestion, int difficulte, String urlQuestion, String reponse, String urlExplication)
	{
		// D�finir les propri�t�s des questions
		intCodeQuestion = codeQuestion;
		objTypeQuestion = typeQuestion;
		intDifficulte = difficulte;
		strURLQuestion = urlQuestion;
		strReponse = reponse;
		strURLExplication = urlExplication;
		intCategorie = 1;
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
	public String obtenirTypeQuestion()
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
	 * 
	 * @return boolean : true si la r�ponse est valide
	 * 					 false sinon
	 */
	public boolean reponseEstValide(String reponse)
	{
		return strReponse.toUpperCase().replace(".",",").equals(reponse.toUpperCase());
	}
	
	
	/**
	 * Cette fonction retourne une mauvaise r�ponse. Utilis� lorsqu'un
	 * joueur utilise l'objet "Livre" qui permet d'�liminer un choix
	 * de r�ponse. Dans le cas d'une question sans choix de r�ponse, la 
	 * fonction retourne "PasUnChoixDeReponse"
	 */
	 public String obtenirMauvaiseReponse()
	 {
	 	// V�rifier si la r�ponse est un choix de r�ponse
	 	if (strReponse.toUpperCase().equals("A") ||
	 	    strReponse.toUpperCase().equals("B") ||
	 	    strReponse.toUpperCase().equals("C") ||
	 	    strReponse.toUpperCase().equals("D") )
	 	{
	 		// Choisir al�atoirement une mauvaise r�ponse
	 	    int arrShuffle[] = {0,1,2,3};
	 	    for (int x = 1; x < 10; x++)
	 	    {
	 	    	int a = UtilitaireNombres.genererNbAleatoire(4);
	 	    	int b = UtilitaireNombres.genererNbAleatoire(4);
	 	    	
	 	    	int temp = arrShuffle[a];
	 	    	arrShuffle[a] = arrShuffle[b];
	 	    	arrShuffle[b] = temp;
	 	    }
	 	    for (int x = 1; x < 4; x++)
	 	    {
	 	    	Character c = new Character((char)(arrShuffle[x] + 65));
	 	    	String strMauvaiseReponse = c.toString();
	 	    	if (!strMauvaiseReponse.equals(strReponse.toUpperCase()))
	 	    	{
	 	    		return strMauvaiseReponse;
	 	    	}
	 	    }	 
	 	    
	 	    return "Erreur";		
	 	}
	 	else
	 	{
	 		return "PasUnChoixDeReponse";
	 	}
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
	
	public void definirDifficulte(int difficulte)
	{
		intDifficulte = difficulte;
	}

	public int obtenirCategorie() 
	{
		return intCategorie;
	}

	public void definirCategorie( int categorie ) 
	{
		intCategorie = categorie;
	}

}
