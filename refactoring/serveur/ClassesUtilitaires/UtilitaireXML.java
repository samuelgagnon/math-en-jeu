package ClassesUtilitaires;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public final class UtilitaireXML 
{
	/**
	 * Constructeur par d�faut est priv� pour emp�cher de pourvoir cr�er des 
	 * instances de cette classe.
	 */
    private UtilitaireXML() {}
    
    /**
     * Cette fonction permet de cr�er et de retourner un document XML vide.
     * 
     * @return Document : Un document XML vide permettant j'ajouter des noeuds
     */
    public static Document obtenirDocumentXML()
    {
        // D�claration d'un document XML
        Document objDocumentXML = null;
        
        try
        {
    		// Cr�er une nouvelle instance de DocumentBuilderFactory qui va permettre
    		// de cr�er un document XML dans lequel on va pouvoir �crire et lire des
    		// cha�nes de caract�res format�es en XML
    		DocumentBuilderFactory objXMLFactory = DocumentBuilderFactory.newInstance();
    		
    		// D�finir les propri�t�s de la factory
    		objXMLFactory.setValidating(false);
    		objXMLFactory.setIgnoringComments(false);
    		objXMLFactory.setIgnoringElementContentWhitespace(false);
    		objXMLFactory.setExpandEntityReferences(true);
    		
    		// Cr�er un nouveau DocumentBuilder qui va permettre de cr�er un 
    		// document XML
    		DocumentBuilder objCreateurDocumentXML = objXMLFactory.newDocumentBuilder();
    					
    		// Cr�er un nouveau Document avec le cr�ateur de document XML 
    		// qui va contenir le code XML � retourner au client
    		objDocumentXML = objCreateurDocumentXML.newDocument();
        }
		catch (ParserConfigurationException pce)
		{
			System.out.println(GestionnaireMessages.message("erreur_transformation"));
		}
		
		return objDocumentXML;
    }
    
    /**
     * Cette fonction permet de cr�er et de retourner un document XML contenant
     * le code XML pass� en param�tres.
     * 
     * @return Document : Un document XML contenant le code XML pass� en 
     * 					  param�tres
     */
    public static Document obtenirDocumentXML(String codeXML)
    {
        // D�claration d'un document XML
        Document objDocumentXML = null;
        
        try
        {
    		// Cr�er une nouvelle instance de DocumentBuilderFactory qui va permettre
    		// de cr�er un document XML dans lequel on va pouvoir �crire et lire des
    		// cha�nes de caract�res format�es en XML
    		DocumentBuilderFactory objXMLFactory = DocumentBuilderFactory.newInstance();
    		
    		// D�finir les propri�t�s de la factory
    		objXMLFactory.setValidating(false);
    		objXMLFactory.setIgnoringComments(false);
    		objXMLFactory.setIgnoringElementContentWhitespace(false);
    		objXMLFactory.setExpandEntityReferences(true);
    		
    		// Cr�er un nouveau DocumentBuilder qui va permettre de cr�er un 
    		// document XML
    		DocumentBuilder objCreateurDocumentXML = objXMLFactory.newDocumentBuilder();
    					
    		// Cr�er un nouveau Document avec le cr�ateur de document XML 
    		// qui va contenir le code XML � retourner au client
    		objDocumentXML = objCreateurDocumentXML.parse(new InputSource(new StringReader(codeXML.trim())));
        }
		catch (ParserConfigurationException pce)
		{
			System.out.println(GestionnaireMessages.message("erreur_transformation"));
		}
		catch (IOException ioe)
		{
			System.out.println(GestionnaireMessages.message("erreur_io"));
		}
		catch (SAXException saxe)
		{
			System.out.println(GestionnaireMessages.message("erreur_sax"));
		}
		
		return objDocumentXML;
    }

	/**
	 * Cette fonction permet de transformer un document XML pass� en param�tres
	 * en une cha�ne de caract�res XML.
	 * 
	 * @param Document documentXML : le document � convertir en cha�ne de 
	 * 								 caract�res
	 * @return String : le document XML converti en une cha�ne de caract�res XML
	 * @throws TransformerConfigurationException : S'il y a une erreur lors de la
	 * 											   conversion d'un document XML en
	 * 											   une cha�ne de caract�res
	 * @throws TransformerException : S'il y a une erreur conversion entre XML et
	 *								  une cha�ne de caract�res
	 */
	public static String transformerDocumentXMLEnString(Document documentXML) throws TransformerConfigurationException,
																			   		 TransformerException
	{
		// Cr�er une factory qui va permettre de cr�er un transformeur de 
		// document XML en cha�ne de caract�res, puis cr�er le transformateur
		TransformerFactory objTransformeurXMLFactory = TransformerFactory.newInstance();
		Transformer objTransformeurXML = objTransformeurXMLFactory.newTransformer();
        
        // Cr�er un objet qui va permettre d'�crire le document XML sous forme 
		// de cha�ne de caract�res
        StringWriter objEcrivainChaine = new StringWriter();
		
        // Transformer le document XML en une cha�ne de caract�res
        objTransformeurXML.transform(new DOMSource(documentXML), 
        							 new StreamResult(objEcrivainChaine));
        
        // Retourner le document XML ne contenant pas l'ent�te XML ajout�e 
        // par d�faut par le transformateur
		return retirerEnteteXML(objEcrivainChaine.toString());
	}
	
	/**
	 * Cette fonction permet de retourner la cha�ne de caract�re repr�sentant le
	 * code XML � retourner au client ne contenant pas l'ent�te XML commen�ant par
	 * <? et terminant par ?>. Si aucune ent�te n'est trouv�e, alors on retourne
	 * le message originel. On suppose que l'ent�te se trouve toujours au d�but
	 * du message.
	 * 
	 * @param String message : le code XML contenant l'ent�te � enlever
	 * @return String : le code XML du message � retourner ne contenant pas 
	 *                  l'ent�te XML
	 */
	private static String retirerEnteteXML(String message)
	{
		// Si le message ne contient pas les balises d'une ent�te XML, alors
		// on retourne le message directement, sinon on l'enl�ve
		if (message.contains("<?") == false || message.contains("?>") == false)
		{
			return message;		
		}
		else
		{
			// On retourne le message sans l'ent�te XML
			return (message.substring(message.indexOf("?>") + 2));
		}
	}
}
