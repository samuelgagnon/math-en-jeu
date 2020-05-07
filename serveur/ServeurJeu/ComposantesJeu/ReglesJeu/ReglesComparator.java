package ServeurJeu.ComposantesJeu.ReglesJeu;

import java.util.Comparator;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class ReglesComparator implements Comparator<ReglesObjet>
{
	/**
	 * Constructeur de la classe ReglesComparator.
	 */
	public ReglesComparator() {}
	
	/**
	 * Cette fonction retourne une valeur permettant de d�terminer quel objet 
	 * est plus grand que l'autre.
	 * 
         * @param reglesObjet1 l'objet � gauche de l'(in)�galit�e.
         * @param reglesObjet2 l'objet � droite de l'(in)�galit�e.
         * @return int : Une valeur n�gative si le premier objet est plus petit
	 * 				 que le deuxi�me
	 * 				 Zero si les deux sont �gaux
	 * 				 Une valeur positive si le premier objet est plus grand 
	 * 				 que le deuxi�me
	 */
	public int compare(ReglesObjet reglesObjet1, ReglesObjet reglesObjet2)
	{
		// Si les deux priorit�s sont pareilles, alors on retourne 0
		if (reglesObjet1.obtenirPriorite() == reglesObjet2.obtenirPriorite())
		{
			return 0;
		}
		// Si le premier objet a une priorit� de -1 ou qu'il est plus grand 
		// que le deuxi�me, alors on retourne 1
		else if (reglesObjet1.obtenirPriorite() == -1 || (reglesObjet1.obtenirPriorite() > reglesObjet2.obtenirPriorite()))
		{
			return 1;
		}
		// Si le deuxi�me objet a une priorit� de -1 ou qu'il est plus grand 
		// que le premier, alors on retourne -1
		else
		{
			return -1;
		}
	}
}
