package ServeurJeu.ComposantesJeu.Objets.Pieces;

import ServeurJeu.ComposantesJeu.Objets.Objet;
//import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;

/**
 * @author Jean-Fran�ois Brind'Amour
 */

//NOTE: On a chang� le concept: les pi�ces ne donnent plus de points,
//      mais plut�t de "l'argent", pour acheter des objets. La valeur
//      des pi�ces est donc 0.

public class Piece extends Objet
{
	// Cette variable va contenir la valeur en points de la pi�ce
	private final int intValeur;
        
    // Cette variable va contenir la valeur en dollars de la pi�ce
    private final int intMonnaie;
	
	/**
	 * Constructeur de la classe Piece qui permet d'initialiser
	 * les valeurs de la pi�ce.
	 * 
	 * @param int valeur : La valeur en points de la pi�ce
     * @param int monnaie : La valeur en dollars de la pi�ce
	 */
	public Piece(int valeur, int monnaie)
	{
		// Initialiser la valeur de la pi�ce
		intValeur = 0;
        intMonnaie = 1;
	}

	/**
	 * Cette fonction permet de retourner la valeur de la pi�ce.
	 * 
	 * @return int : La valeur de la pi�ce
	 */
	public int obtenirValeur()
	{
	   return intValeur;
	}
        
    public int obtenirMonnaie()
    {
        return intMonnaie;
    }
}