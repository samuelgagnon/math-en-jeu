package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Oloieri Lilian
 *
 */
public class StopServerEvent extends Evenement {

	// D�claration d'une variable qui va garder le nombre des seconds
	// apres lesquelles le serveur va etre fermer ou restarter 
	private int intSeconds;

	/**
	 * Constructeur de la classe StopServerEvent qui permet 
	 * d'initialiser le nombre des secondes. 
	 */
	public StopServerEvent( int intSeconds) {

		this.intSeconds = intSeconds;
		generateXML();		
	}


	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

		// Cr�er les noeuds de param�tre
		Element objNoeudParametreSeconds = objDocumentXML.createElement("parametre");

		// Cr�er le noeud texte contenant le nombre des seconds 
		Text objNoeudTexteSeconds = objDocumentXML.createTextNode(Integer.toString(intSeconds));

		// D�finir les attributs du noeud de commande
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "ServerWillStop");

		// On ajoute un attribut type qui va contenir le type
		// du param�tre
		objNoeudParametreSeconds.setAttribute("type", "nrSeconds");

		// Ajouter les noeuds texte aux noeuds des param�tres
		objNoeudParametreSeconds.appendChild(objNoeudTexteSeconds);

		// Ajouter les noeuds param�tres au noeud de commande
		objNoeudCommande.appendChild(objNoeudParametreSeconds);

		// Ajouter le noeud de commande au noeud racine dans le document
		objDocumentXML.appendChild(objNoeudCommande);
	}

}
