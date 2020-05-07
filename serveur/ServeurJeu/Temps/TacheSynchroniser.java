/*
 * Created on 2006-03-17
 *
 * 
 */
package ServeurJeu.Temps;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.Iterator;

/**
 * @author Marc Dumoulin
 * 
 * Classe qui impl�mente une t�che pour la synchronisation des joueurs 
 * 
 */
public class TacheSynchroniser extends TimerTask
{
	//Liste des observateurs qui seront notifi�s pour la synchronisation
	private HashMap<Integer, ObservateurSynchroniser> lstObservateurs;
	
	/**
	 * Constructeur de la classe TacheSynchroniser
	 * 
	 */
	public TacheSynchroniser()
	{
		lstObservateurs = new HashMap<Integer, ObservateurSynchroniser>();
	}
	
	/**
	 * Cette m�thode est appel� pour ajouter un observateur qui va recevoir 
	 * une notification de se synchoniser
	 * 
	 * @param ObservateurSynchroniser obs : Un objet qui impl�mente l'interface observateur
	 * @return
	 * @throws
	 */
	public void ajouterObservateur( ObservateurSynchroniser obs )
	{
		synchronized( lstObservateurs )
		{
			lstObservateurs.put( new Integer( obs.getObservateurSynchroniserId() ), obs );
		}
	}
	
	/**
	 * Cette m�thode est appel� pour enlever un observateur 
	 * 
	 * @param ObservateurSynchroniser obs : Un objet qui impl�mente l'interface observateur
	 * @return
	 * @throws
	 */
	public void enleverObservateur( ObservateurSynchroniser obs )
	{
		synchronized( lstObservateurs )
		{
			lstObservateurs.remove( new Integer( obs.getObservateurSynchroniserId() ) );
		}
	}
	
	/**
	 * Cette m�thode est appel� quand le thread de la t�che d�marre
	 * @param
	 * @return
	 * @throws
	 */
	public void run()
	{
		//notifier les observateurs
		synchronized( lstObservateurs )
		{
			Iterator<ObservateurSynchroniser> it = lstObservateurs.values().iterator();
			while( it.hasNext() )
			{
				ObservateurSynchroniser obs = (ObservateurSynchroniser)it.next();
				obs.synchronise();
			}
		}
	}
}
