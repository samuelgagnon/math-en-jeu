package ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables;

import ClassesUtilitaires.UtilitaireXML;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Table;
import ServeurJeu.Configuration.GestionnaireMessages;
import java.awt.Point;
import java.util.Random;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Fran�ois Gingras
 */
public class Banane extends ObjetUtilisable 
{
	// Cette constante sp�cifie le prix de l'objet courant
	public static final int PRIX = 1;
	
	// Cette constante affirme que l'objet courant n'est pas limit� 
	// lorsqu'on l'ach�te (c'est-�-dire qu'un magasin n'�puise jamais 
	// son stock de cet objet)
	public static final boolean EST_LIMITE = false;
	
	// Cette constante affirme que l'objet courant ne peut �tre arm� 
	// et d�pos� sur une case pour qu'un autre joueur tombe dessus. Elle 
	// ne peut seulement �tre utilis�e imm�diatement par le joueur
	public static final boolean PEUT_ETRE_ARME = false;
	
	// Cette constante d�finit le nom de cet objet
	public static final String TYPE_OBJET = "Banane";
	
	/**
	 * Constructeur de la classe Banane qui permet de d�finir les propri�t�s 
	 * propres � l'objet courant.
	 *
	 * @param in id : Le num�ro d'identification de l'objet
	 * @param boolean estVisible : Permet de savoir si l'objet doit �tre visible ou non
	 */
	public Banane(int id, boolean estVisible)
	{
		// Appeler le constructeur du parent
		super(id, estVisible, UID_OU_BANANE, PRIX, EST_LIMITE, PEUT_ETRE_ARME, TYPE_OBJET);
	}
	
        public static void utiliserBanane(String joueurQuiUtilise, Point positionJoueurChoisi, String nomJoueurChoisi, Table table, boolean estHumain)
        {
            // On a trouv� le joueur et on l'a sett� � subir une banane;
            // le reste, on le fera au d�placement de la personne

            // On obtient la position du WinTheGame
            Point positionDuWinTheGame = new Point(table.obtenirPositionWinTheGame());

            // Obtention du plateau de jeu
            Case[][] plateauDeJeu = table.obtenirPlateauJeuCourant();
            
            // Distance (en cases) de l'�loignement souhait� du joueur
            // On prend la distance actuelle d'avec le WinTheGame
            // et on lui ajoute une quantit� qui d�pend de la taille du plateau
            int deCombienOnVeutEloigner = Math.abs(positionDuWinTheGame.x - positionJoueurChoisi.x) + Math.abs(positionDuWinTheGame.y - positionJoueurChoisi.y);
            deCombienOnVeutEloigner += plateauDeJeu.length*4/3;

            // Point optimal
            Point pointOptimal = new Point(positionJoueurChoisi);

            // Distance optimale atteinte
            int distanceOptimale = 0;

            // On parcourt le plateau pour trouver la meilleure position o� envoyer le joueur
            // La case doit exister et �tre vide
            int nbEssaisI = 0;
            int nbEssaisJ = 0;
            Random objRandom = new Random();
            Case[][] objttPlateauJeu = table.obtenirPlateauJeuCourant();
            int maxEssais = 5*objttPlateauJeu.length;
            for(int i=objRandom.nextInt(objttPlateauJeu.length); nbEssaisI < maxEssais && distanceOptimale != deCombienOnVeutEloigner; i=objRandom.nextInt(objttPlateauJeu.length))
            {
                nbEssaisI++;
                nbEssaisJ=0;
                for(int j=objRandom.nextInt(objttPlateauJeu[i].length); nbEssaisJ < maxEssais && distanceOptimale != deCombienOnVeutEloigner; j=objRandom.nextInt(objttPlateauJeu[i].length))
                {
                    nbEssaisJ++;
                    // Est-ce que la case existe? Est-ce que c'est une case couleur?
                    if(plateauDeJeu[i][j] != null && plateauDeJeu[i][j] instanceof CaseCouleur)
                    {
                        CaseCouleur caseTemp = (CaseCouleur)plateauDeJeu[i][j];
                        // Est-ce qu'il n'y a rien dessus?
                        if(caseTemp.obtenirObjetArme() == null && caseTemp.obtenirObjetCase() == null)
                        {
                            // On regarde si c'est un meilleur point que l'optimal trouv� jusqu'� pr�sent
                            int distanceActuelle = Math.abs(i-positionDuWinTheGame.x) + Math.abs(j-positionDuWinTheGame.y);
                            if(Math.abs(deCombienOnVeutEloigner - distanceActuelle) < Math.abs(deCombienOnVeutEloigner - distanceOptimale))
                            {
                                distanceOptimale = distanceActuelle;
                                pointOptimal.setLocation(i, j);
                            }
                        }
                    }
                }
            }

            // On d�place le joueur � l'interne
            int nouveauPointage;
            int nouvelArgent;
            if(estHumain)
            {
                nouveauPointage = table.obtenirJoueurHumainParSonNom(nomJoueurChoisi).obtenirPartieCourante().obtenirPointage();
                nouvelArgent = table.obtenirJoueurHumainParSonNom(nomJoueurChoisi).obtenirPartieCourante().obtenirArgent();
                table.preparerEvenementJoueurDeplacePersonnage(nomJoueurChoisi, "", positionJoueurChoisi, pointOptimal, nouveauPointage, nouvelArgent, "Banane");
                table.obtenirJoueurHumainParSonNom(nomJoueurChoisi).obtenirPartieCourante().definirPositionJoueur(pointOptimal);
            }
            else
            {
                nouveauPointage = table.obtenirJoueurVirtuelParSonNom(nomJoueurChoisi).obtenirPointage();
                nouvelArgent = table.obtenirJoueurVirtuelParSonNom(nomJoueurChoisi).obtenirArgent();
                table.preparerEvenementJoueurDeplacePersonnage(nomJoueurChoisi, "", positionJoueurChoisi, pointOptimal, nouveauPointage, nouvelArgent, "Banane");
                table.obtenirJoueurVirtuelParSonNom(nomJoueurChoisi).definirPositionJoueurVirtuel(pointOptimal);
            }

            Document objDocumentXMLTemp = UtilitaireXML.obtenirDocumentXML();
            Element objNoeudCommandeTemp = objDocumentXMLTemp.createElement("Banane");

            Element objNoeudParametreNouvellePositionX = objDocumentXMLTemp.createElement("parametre");
            Element objNoeudParametreNouvellePositionY = objDocumentXMLTemp.createElement("parametre");
            objNoeudParametreNouvellePositionX.setAttribute("type", "NouvellePositionX");
            objNoeudParametreNouvellePositionY.setAttribute("type", "NouvellePositionY");
            Text objNoeudTexteNouvellePositionX = objDocumentXMLTemp.createTextNode(Integer.toString(pointOptimal.x));
            Text objNoeudTexteNouvellePositionY = objDocumentXMLTemp.createTextNode(Integer.toString(pointOptimal.y));
            objNoeudParametreNouvellePositionX.appendChild(objNoeudTexteNouvellePositionX);
            objNoeudParametreNouvellePositionY.appendChild(objNoeudTexteNouvellePositionY);
            objNoeudCommandeTemp.appendChild(objNoeudParametreNouvellePositionX);
            objNoeudCommandeTemp.appendChild(objNoeudParametreNouvellePositionY);

            objDocumentXMLTemp.appendChild(objNoeudCommandeTemp);
            String strCodeXML = "";
            try
            {
                strCodeXML = UtilitaireXML.transformerDocumentXMLEnString(objDocumentXMLTemp);
            }
            catch (TransformerConfigurationException tce)
            {
                    System.out.println(GestionnaireMessages.message("evenement.XML_transformation"));
            }
            catch (TransformerException te)
            {
                    System.out.println(GestionnaireMessages.message("evenement.XML_conversion"));
            }

            // On pr�pare l'�v�nement � envoyer � tous
            table.preparerEvenementUtiliserObjet(joueurQuiUtilise, nomJoueurChoisi, "Banane", strCodeXML);
        }
}