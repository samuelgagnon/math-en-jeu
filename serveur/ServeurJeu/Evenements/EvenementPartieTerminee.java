/*
 * Created on 2006-03-17
 *
 * 
 */
package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import java.util.TreeSet;
import ServeurJeu.ComposantesJeu.Joueurs.StatisticsPlayer;
import ServeurJeu.ComposantesJeu.Tables.Table;

/**
 * @author Marc
 *
 * 
 */
public class EvenementPartieTerminee  extends Evenement
{
	//private String joueurGagnant;
	private TreeSet<StatisticsPlayer> ourResults;

	public EvenementPartieTerminee( Table table, TreeSet<StatisticsPlayer> ourResults, String joueurGagnant)
	{
		//this.joueurGagnant = joueurGagnant;
		this.ourResults = ourResults;
		generateXML();
	}

	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er un noeud contenant le nom d'utilisateur du noeud param�tre

		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "PartieTerminee");

		// Cr�er le noeud du param�tre
		Element objNoeudParametre = objDocumentXML.createElement("parametre");
		objNoeudParametre.setAttribute("type", "StatistiqueJoueur");

		//Element objNoeudARejointLeWinTheGame = objDocumentXML.createElement("joueurWinTheGame");
		//objNoeudARejointLeWinTheGame.setAttribute("nom", joueurGagnant);
		//objNoeudCommande.appendChild(objNoeudARejointLeWinTheGame);

		int i = ourResults.size();
		for(StatisticsPlayer s: ourResults)
		{

			String nomUtilisateur = s.getUsername();
			int pointage = s.getPoints();

			Element objNoeudJoueur = objDocumentXML.createElement("joueur");
			objNoeudJoueur.setAttribute("utilisateur", nomUtilisateur);
			objNoeudJoueur.setAttribute("pointage", new Integer( pointage).toString());
			objNoeudJoueur.setAttribute("position", new Integer(i).toString());

			objNoeudParametre.appendChild( objNoeudJoueur );

			// Ajouter le noeud param�tre au noeud de commande
			objNoeudCommande.appendChild(objNoeudParametre);

			System.out.println("Stats : " + nomUtilisateur + " " + pointage + " " + i);

			i--;
		}

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);
	}
}
