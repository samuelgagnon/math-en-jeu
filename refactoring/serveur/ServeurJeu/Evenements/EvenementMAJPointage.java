package ServeurJeu.Evenements;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import ServeurJeu.Monitoring.Moniteur;
import ClassesUtilitaires.UtilitaireXML;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * @author Jean-Fran�ois Fournier
 */

public class EvenementMAJPointage extends Evenement
{
	// D�claration d'une variable qui va garder le nom d'utilisateur du
    // joueur dont le pointage est mis � jour
    private String strNomUtilisateur;
	
	// D�claration d'une variable qui va garder le nouveau pointage
	private int intPointage;
	
    /**
     * Constructeur de la classe EvenementMAJPointage qui permet d'initialiser
     * le nom d'utilisateur du joueur et son nouveau pointage 
     */
    public EvenementMAJPointage(String nomUtilisateur, int nouveauPointage)
    {
        // D�finir le nom d'utilisateur du joueur
        strNomUtilisateur = nomUtilisateur;
        
        // D�finir le nouveau pointage du joueur
        intPointage = nouveauPointage;
    }
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement pour la
	 * mise � jour d'un pointage et de le retourner.
	 * 
	 * @param InformationDestination information : Les informations � qui 
	 * 					envoyer l'�v�nement
	 * @return String : Le code XML de l'�v�nement � envoyer
	 */
	protected String genererCodeXML(InformationDestination information)
	{
		Moniteur.obtenirInstance().debut( "EvenementMAJPointage.genererCodeXML" );
		
		/*
		 * <commande no="57" nom="MiseAJourPointage" type="Evenement">
		 *     <parametre type="NomUtilisateur">AdversaireXYZ</parametre>
		 *     <parametre type="Pointage">154</parametre>
		 * </commande>
		 *
		 */
		 
	    // D�claration d'une variable qui va contenir le code XML � retourner
	    String strCodeXML = "";
	    
		try
		{
	        // Appeler une fonction qui va cr�er un document XML dans lequel 
		    // on peut ajouter des noeuds
	        Document objDocumentXML = UtilitaireXML.obtenirDocumentXML();

			// Cr�er le noeud de commande � retourner
			Element objNoeudCommande = objDocumentXML.createElement("commande");
			
			// Cr�er les noeuds des param�tres
			Element objNoeudParametreNom = objDocumentXML.createElement("parametre");
			Element objNoeudParametrePointage = objDocumentXML.createElement("parametre");
			
			// Cr�er un noeud contenant le nom d'utilisateur du noeud param�tre
			Text objNoeudTexteNom = objDocumentXML.createTextNode(strNomUtilisateur);
			Text objNoeudTextePointage = objDocumentXML.createTextNode(Integer.toString(intPointage));
			
			// D�finir les attributs du noeud de commande
			objNoeudCommande.setAttribute("no", Integer.toString(information.obtenirNoCommande()));
			objNoeudCommande.setAttribute("type", "Evenement");
			objNoeudCommande.setAttribute("nom", "MAJPointage");
			
			// On ajoute un attribut type qui va contenir le type
			// du param�tre pour le nom de l'utilisateur
			objNoeudParametreNom.setAttribute("type", "NomUtilisateur");
			
			// Ajouter le noeud texte avec le nom de l'utilisateur au noeud du param�tre
			objNoeudParametreNom.appendChild(objNoeudTexteNom);
			
			// On ajoute un attribut type qui va contenir le type
			// du param�tre pour le pointage
			objNoeudParametrePointage.setAttribute("type", "Pointage");
			
			// Ajouter le noeud texte avec le pointage au noeud du param�tre
			objNoeudParametrePointage.appendChild(objNoeudTextePointage);
			
			// Ajouter les noeuds param�tres au noeud de commande
			objNoeudCommande.appendChild(objNoeudParametreNom);
			objNoeudCommande.appendChild(objNoeudParametrePointage);
			
			// Ajouter le noeud de commande au noeud racine dans le document
			objDocumentXML.appendChild(objNoeudCommande);

			// Transformer le document XML en code XML
			strCodeXML = UtilitaireXML.transformerDocumentXMLEnString(objDocumentXML);
		}
		catch (TransformerConfigurationException tce)
		{
			System.out.println(GestionnaireMessages.message("evenement.XML_transformation"));
		}
		catch (TransformerException te)
		{
			System.out.println(GestionnaireMessages.message("evenement.XML_conversion"));
		}
		
		Moniteur.obtenirInstance().fin();
		
		return strCodeXML;
	}
	
}
