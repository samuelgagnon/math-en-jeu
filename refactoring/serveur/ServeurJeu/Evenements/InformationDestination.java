package ServeurJeu.Evenements;

import ServeurJeu.Communications.ProtocoleJoueur;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class InformationDestination
{
	// D�claration d'une variable qui va contenir le num�ro de commande g�n�r�
	private int intNoCommande;
	
	// D�claration d'une r�f�rence vers le ProtocoleJoueur � qui envoyer le message
	private ProtocoleJoueur objProtocoleJoueur;
	
	/**
	 * Constructeur de la classe InformationDestination qui permet d'initialiser
	 * le num�ro de la commande ainsi que le protocole.
	 */
	public InformationDestination(int noCommande, ProtocoleJoueur protocole)
	{
	    // D�finir les propri�t�s de l'objet InformationDestination
	    intNoCommande = noCommande;
	    objProtocoleJoueur = protocole;
	}
	
	/**
	 * Cette fonction permet de retourner le num�ro de la commande pour le joueur
	 * de destination suivant.
	 * 
	 * @return int : Le num�ro de commande du InformationDestination courant
	 */
	public int obtenirNoCommande()
	{
	   return intNoCommande; 
	}
	
	/**
	 * Cette fonction permet de retourner le protocole du joueur � qui envoyer
	 * l'�v�nement.
	 * 
	 * @return ProtocoleJoueur : Le protocole du joueur � qui envoyer l'�v�nement
	 */
	public ProtocoleJoueur obtenirProtocoleJoueur()
	{
	   return objProtocoleJoueur;
	}
}
