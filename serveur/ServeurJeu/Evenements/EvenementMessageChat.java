package ServeurJeu.Evenements;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Fran�ois Gingras
 * Y'a pas beaucoup de commentaires mais c'est pratiquement la m�me chose que
 * les autres �v�nements comme EvenementMAJPointage
 */

public class EvenementMessageChat extends Evenement
{
	// Cha�ne de caract�re qui va garder en m�moire le nom du joueur qui a
	// envoy� le message
	private String joueurQuiEnvoieLeMessage;

	// Le message en tant que tel
	private String messageAEnvoyer;

	public EvenementMessageChat(String joueurQuiEnvoieLeMessage, String messageAEnvoyer)
	{
		this.joueurQuiEnvoieLeMessage = joueurQuiEnvoieLeMessage;
		this.messageAEnvoyer = messageAEnvoyer;

		generateXML();
	}

	private void generateXML()
	{

		Element objNoeudCommande = objDocumentXML.createElement("commande");
		
		objNoeudCommande.setAttribute("type", "Evenement");
		objNoeudCommande.setAttribute("nom", "MessageChat");

		Element objNoeudParametreJoueurQuiEnvoieLeMessage = objDocumentXML.createElement("parametre");
		Element objNoeudParametreMessageAEnvoyer = objDocumentXML.createElement("parametre");

		Text objNoeudTexteJoueurQuiEnvoieLeMessage = objDocumentXML.createTextNode(joueurQuiEnvoieLeMessage);
		Text objNoeudTexteMessageAEnvoyer = objDocumentXML.createTextNode(messageAEnvoyer);

		objNoeudParametreJoueurQuiEnvoieLeMessage.setAttribute("type", "joueurQuiEnvoieLeMessage");
		objNoeudParametreMessageAEnvoyer.setAttribute("type", "messageAEnvoyer");

		objNoeudParametreJoueurQuiEnvoieLeMessage.appendChild(objNoeudTexteJoueurQuiEnvoieLeMessage);
		objNoeudParametreMessageAEnvoyer.appendChild(objNoeudTexteMessageAEnvoyer);

		objNoeudCommande.appendChild(objNoeudParametreJoueurQuiEnvoieLeMessage);
		objNoeudCommande.appendChild(objNoeudParametreMessageAEnvoyer);

		objDocumentXML.appendChild(objNoeudCommande);

	}
}
