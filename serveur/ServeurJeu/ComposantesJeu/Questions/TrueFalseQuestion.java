package ServeurJeu.ComposantesJeu.Questions;

public class TrueFalseQuestion extends Question {

	public TrueFalseQuestion(int codeQuestion, int typeQuestion, int difficulte, String urlQuestion, 
			String reponse, String urlExplication)//, LinkedList<Integer> keywords)
	{
		super(codeQuestion, typeQuestion, difficulte, urlQuestion, reponse, urlExplication);
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
}
