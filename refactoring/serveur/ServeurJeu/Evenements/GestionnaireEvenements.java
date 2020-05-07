package ServeurJeu.Evenements;

import java.util.Vector;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class GestionnaireEvenements implements Runnable
{
	// D�claration d'une liste d'�v�nements
	private Vector lstEvenements;
	
	// Cette variable permet de savoir s'il faut arr�ter le thread ou non
	private boolean bolStopThread = false;
	
	/**
	 * Constructeur de la classe GestionnaireEvenements qui permet d'initialiser
	 * la liste des �v�nements
	 */
	public GestionnaireEvenements() 
	{
		super();
		
		// Cr�er une liste des �v�nements
		lstEvenements = new Vector();
	}
	
	/**
	 * Cette m�thode est appel�e automatiquement par le thread du joueur et elle
	 * permet de traiter les �v�nements qu'il y a en attente d'�tre envoy�s.
	 */
	public void run()
	{
		// Boucler tant qu'il ne faut pas arr�ter le thread
		while (bolStopThread == false)
		{
		    // Si la liste des �v�nements contient au moins un �v�nement, alors
		    // on va traiter celui qui est au d�but du vecteur
		    if (lstEvenements.size() > 0)
		    {
		        // Faire la r�f�rence vers l'�v�nement se trouvant au d�but de 
		        // la liste
		        Evenement evenementPrioritaire = (Evenement) lstEvenements.get(0);
		        
		        // Envoyer l'�v�nement � tous les joueurs qui doivent le recevoir
		        evenementPrioritaire.envoyerEvenement();
		        
		        // Enlever l'�v�nement de la liste d'�v�nements � traiter
		        lstEvenements.remove(evenementPrioritaire);
		    }
		    
			try
			{
				// Stopper le thread du gestionnaire d'�v�nements pour
				// laisser un moment de r�pit au CPU
				Thread.sleep(10);
			}
			catch (InterruptedException ie) {}
		}
	}
	
	/**
	 * Cette m�thode permet d'ajouter l'�v�nement pass� en param�tres � la liste
	 * des �v�nements � traiter.
	 * 
	 * @param Evenement evenementATraiter : L'�v�nement � ajouter � la fin de
	 * 										la liste d'�v�nements
	 */
	public void ajouterEvenement(Evenement evenementATraiter)
	{
	    // Ajouter un nouvel �v�nement dans la liste d'�v�nements � traiter
	    lstEvenements.add(evenementATraiter);
	}
	
	/**
	 * Cette m�thode permet d'arr�ter le thread du gestionnaire d'�v�nements.
	 * Il n'est plus possible de relancer le gestionnaire par la suite.
	 */
	public void arreterGestionnaireEvenements()
	{
		bolStopThread = true;
	}
}
