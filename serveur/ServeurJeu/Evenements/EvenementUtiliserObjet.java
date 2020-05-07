package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Fran�ois Gingras
 * Y'a pas beaucoup de commentaires mais c'est pratiquement la m�me chose que
 * les autres �v�nements comme EvenementMAJPointage
 */

public class EvenementUtiliserObjet extends Evenement
{
	// Cha�nes de caract�res qui vont garder en m�moire le nom des joueurs
	// qui ont utilis� et qui sont affect�s par l'objet utilis�
	private String joueurQuiUtilise;
	private String joueurAffecte;

	// Nom de l'objet utilis�
	private String objetUtilise;

	// Autres informations � envoyer
	private String autresInformations;

	public EvenementUtiliserObjet(String joueurQuiUtilise, String joueurAffecte, String objetUtilise, String autresInformations)
	{
		this.joueurQuiUtilise = joueurQuiUtilise;
		this.joueurAffecte = joueurAffecte;
		this.objetUtilise = objetUtilise;
		this.autresInformations = autresInformations;
		generateXML();
	}

	private void generateXML()
	{
		Element objNoeudCommande = objDocumentXML.createElement("commande");
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "UtiliserObjet");

		Element objNoeudParametreNomUtilise = objDocumentXML.createElement("parametre");
		Element objNoeudParametreNomAffecte = objDocumentXML.createElement("parametre");
		Element objNoeudParametreObjetUtilise = objDocumentXML.createElement("parametre");
		Element objNoeudParametreAutresInformations = objDocumentXML.createElement("parametre");

		Text objNoeudTexteNomUtilise = objDocumentXML.createTextNode(joueurQuiUtilise);
		Text objNoeudTexteNomAffecte = objDocumentXML.createTextNode(joueurAffecte);
		Text objNoeudTexteObjetUtilise = objDocumentXML.createTextNode(objetUtilise);
		Text objNoeudTexteAutresInformations = objDocumentXML.createTextNode(autresInformations);

		objNoeudParametreNomUtilise.setAttribute("type", "joueurQuiUtilise");
		objNoeudParametreNomAffecte.setAttribute("type", "joueurAffecte");
		objNoeudParametreObjetUtilise.setAttribute("type", "objetUtilise");
		objNoeudParametreAutresInformations.setAttribute("type", "autresInformations");

		objNoeudParametreNomUtilise.appendChild(objNoeudTexteNomUtilise);
		objNoeudParametreNomAffecte.appendChild(objNoeudTexteNomAffecte);
		objNoeudParametreObjetUtilise.appendChild(objNoeudTexteObjetUtilise);
		objNoeudParametreAutresInformations.appendChild(objNoeudTexteAutresInformations);

		objNoeudCommande.appendChild(objNoeudParametreNomUtilise);
		objNoeudCommande.appendChild(objNoeudParametreNomAffecte);
		objNoeudCommande.appendChild(objNoeudParametreObjetUtilise);
		objNoeudCommande.appendChild(objNoeudParametreAutresInformations);

		objDocumentXML.appendChild(objNoeudCommande);			
	}
}
