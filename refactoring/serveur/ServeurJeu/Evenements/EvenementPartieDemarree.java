package ServeurJeu.Evenements;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.awt.Point;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;
import ServeurJeu.ComposantesJeu.Objets.Pieces.Piece;
import ServeurJeu.ControleurJeu;
import ServeurJeu.Monitoring.Moniteur;
import ClassesUtilitaires.UtilitaireXML;
import ServeurJeu.ComposantesJeu.Table;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementPartieDemarree extends Evenement
{
	// D�claration d'une variable qui va garder le temps de la partie
    private int intTempsPartie;
    
	// D�claration d'un tableau � 2 dimensions qui va contenir les informations 
	// sur les cases du jeu
	private Case[][] objttPlateauJeu;
	
	// D�claration d'une liste contenant les positions des joueurs et dont la 
	// cl� est le nom d'utilisateur du joueur
	private TreeMap lstPositionJoueurs;
	
	private Document objDocumentXML;
	private Element objNoeudCommande;
        private Table table;
    
    /**
     * Constructeur de la classe EvenementPartieDemarree qui permet 
     * d'initialiser le num�ro de la table et le plateau de jeu pour la partie 
     * qui vient de d�buter.
     *
     * @param int tempsPartie : Le temps total de la partie
     * @param TreeMap listePositionJoueurs : La liste des positions des joueurs
     * @param Case[][] plateauJeu : Un tableau � 2 dimensions repr�sentant le 
     * 								plateau de jeu
     */
    public EvenementPartieDemarree(int tempsPartie, TreeMap listePositionJoueurs, Case[][] plateauJeu, Table table)
    {
        // D�finir le temps de la partie, le plateau de jeu et la liste des
    	// positions des joueurs
    	intTempsPartie = tempsPartie;
        objttPlateauJeu = plateauJeu;
        this.table = table;
        lstPositionJoueurs = listePositionJoueurs;
        objDocumentXML = null;
        objNoeudCommande = null;
    }
	
	/**
	 * Cette fonction permet de g�n�rer le code XML de l'�v�nement du d�but 
	 * d'une partie et de le retourner.
	 * 
	 * @param InformationDestination information : Les informations � qui 
	 * 					envoyer l'�v�nement
	 * @return String : Le code XML de l'�v�nement � envoyer
	 */
	protected String genererCodeXML(InformationDestination information)
	{
		Moniteur.obtenirInstance().debut( "EvenementPartieDemarree.genererCodeXML" );
	    // D�claration d'une variable qui va contenir le code XML � retourner
	    String strCodeXML = "";
	 
		try
		{
			if( objDocumentXML == null )
			{
		        // Appeler une fonction qui va cr�er un document XML dans lequel 
			    // on peut ajouter des noeuds
		        objDocumentXML = UtilitaireXML.obtenirDocumentXML();
	
				// Cr�er le noeud de commande � retourner
				objNoeudCommande = objDocumentXML.createElement("commande");
				
				// Cr�er les noeuds de param�tre et de taille
				Element objNoeudParametreTempsPartie = objDocumentXML.createElement("parametre");
				Element objNoeudParametreTaillePlateauJeu = objDocumentXML.createElement("parametre");
				Element objNoeudParametrePositionJoueurs = objDocumentXML.createElement("parametre");
				Element objNoeudParametrePlateauJeu = objDocumentXML.createElement("parametre");
				Element objNoeudParametreTaille = objDocumentXML.createElement("taille");
				
				// Cr�er un noeud contenant le temps de la partie
				Text objNoeudTexte = objDocumentXML.createTextNode(Integer.toString(intTempsPartie));
				
				// Ajouter le noeud texte au noeud du param�tre
				objNoeudParametreTempsPartie.appendChild(objNoeudTexte);
				
				// D�finir les attributs du noeud de commande
				objNoeudCommande.setAttribute("type", "Evenement");
				objNoeudCommande.setAttribute("nom", "PartieDemarree");
				
				// On ajoute un attribut type qui va contenir le type
				// du param�tre
				objNoeudParametreTempsPartie.setAttribute("type", "TempsPartie");
				objNoeudParametreTaillePlateauJeu.setAttribute("type", "Taille");
				objNoeudParametrePositionJoueurs.setAttribute("type", "PositionJoueurs");
				objNoeudParametrePlateauJeu.setAttribute("type", "PlateauJeu");
                                
                                
                                if(!table.obtenirButDuJeu().equals("original"))
                                {
                                    // Cr�er le noeud contenant la position initiale du WinTheGame
                                    {
                                        int z=0;
                                        while((table.obtenirPositionWinTheGame().x == -1 || table.obtenirPositionWinTheGame().y == -1) && z<100)
                                        {
                                            table.definirNouvellePositionWinTheGame();
                                            z++;
                                        }
                                    }

                                    Element objNoeudParametrePositionWinTheGame = objDocumentXML.createElement("parametre");
                                    objNoeudParametrePositionWinTheGame.setAttribute("type", "positionWinTheGame");
                                    objNoeudParametrePositionWinTheGame.setAttribute("x", Integer.toString(table.obtenirPositionWinTheGame().x));
                                    objNoeudParametrePositionWinTheGame.setAttribute("y", Integer.toString(table.obtenirPositionWinTheGame().y));
                                     objNoeudParametrePositionWinTheGame.setAttribute("pointageRequis", Integer.toString(table.pointageRequisPourAllerSurLeWinTheGame()));
                                    objNoeudCommande.appendChild(objNoeudParametrePositionWinTheGame);
                                }
				
				// Cr�er les informations concernant la taille
				objNoeudParametreTaille.setAttribute("nbLignes", Integer.toString(objttPlateauJeu.length));
				objNoeudParametreTaille.setAttribute("nbColonnes", Integer.toString(objttPlateauJeu[0].length));
				
				// Ajouter les noeuds enfants aux noeuds param�tres
				objNoeudParametreTaillePlateauJeu.appendChild(objNoeudParametreTaille);
				
				// Cr�er un ensemble contenant tous les tuples de la liste 
				// des positions de joueurs (chaque �l�ment est un Map.Entry)
				Set lstEnsemblePositionJoueurs = lstPositionJoueurs.entrySet();
				
				// Obtenir un it�rateur pour l'ensemble contenant les positions 
				// des joueurs
				Iterator objIterateurListe = lstEnsemblePositionJoueurs.iterator();
				
				// Passer tous les positions des joueurs et cr�er leur code XML
				while (objIterateurListe.hasNext() == true)
				{
					// D�claration d'une r�f�rence vers l'objet cl� valeur courant
					Map.Entry objPositionJoueur = (Map.Entry) objIterateurListe.next();
					
					// Cr�er une r�f�rence vers la position du joueur courant
					Point objPosition = (Point) objPositionJoueur.getValue();
					
					// Cr�er un noeud de case en passant le bon nom
					Element objNoeudPositionJoueur = objDocumentXML.createElement("position");
					
					// D�finir les attributs du noeud courant
					objNoeudPositionJoueur.setAttribute("nom", (String) objPositionJoueur.getKey());
					objNoeudPositionJoueur.setAttribute("x", Integer.toString(objPosition.x));
					objNoeudPositionJoueur.setAttribute("y", Integer.toString(objPosition.y));
					
					// Ajouter le noeud de position courant au noeud param�tre
					objNoeudParametrePositionJoueurs.appendChild(objNoeudPositionJoueur);
				}
				
				// Passer toutes les lignes du plateau de jeu et cr�er toutes 
				// les cases
				for (int i = 0; i < objttPlateauJeu.length; i++)
				{
					// Passer toutes les colonnes du plateau de jeu
					for (int j = 0; j < objttPlateauJeu[0].length; j++)
					{
						// S'il y a une case au point courant, alors on peut la 
						// cr�er en XML, sinon on ne fait rien
						if (objttPlateauJeu[i][j] != null)
						{
							// D�claration d'un noeud de case
							Element objNoeudCase;
							
							// Si la classe de l'objet courant est CaseCouleur,
							// alors on va cr�er l'�l�ment en passant le bon nom
							if (objttPlateauJeu[i][j] instanceof CaseCouleur)
							{
								// Cr�er le noeud de case en passant le bon nom
								objNoeudCase = objDocumentXML.createElement("caseCouleur");
							}
							else
							{
								// Cr�er le noeud de case en passant le bon nom
								objNoeudCase = objDocumentXML.createElement("caseSpeciale");		
							}
							
							// Cr�er les informations de la case
							objNoeudCase.setAttribute("x", Integer.toString(i));
							objNoeudCase.setAttribute("y", Integer.toString(j));
							objNoeudCase.setAttribute("type", Integer.toString(objttPlateauJeu[i][j].obtenirTypeCase()));
							
							// Si la case courante est une case couleur, alors
							// on d�finit son objet, sinon on ne fait rien de 
							// plus pour une case sp�ciale
							if (objttPlateauJeu[i][j] instanceof CaseCouleur)
							{
								// Cr�er une r�f�rence vers la case couleur 
								// courante
								CaseCouleur objCaseCouleur = (CaseCouleur) objttPlateauJeu[i][j];
								
								// S'il y a un objet sur la case, alors on va 
								// cr�er le code XML pour cet objet (il ne peut 
								// y en avoir qu'un seul)
								if (objCaseCouleur.obtenirObjetCase() != null)
								{
									// D�claration d'un noeud d'objet
									Element objNoeudObjet;
									
									// Si l'objet sur la case est un magasin
									if (objCaseCouleur.obtenirObjetCase() instanceof Magasin)
									{
										// Cr�er le noeud d'objet
										objNoeudObjet = objDocumentXML.createElement("magasin");
										
										// Mettre le nom de la classe de l'objet comme attribut
										objNoeudObjet.setAttribute("nom", objCaseCouleur.obtenirObjetCase().getClass().getSimpleName());
									}
									else if (objCaseCouleur.obtenirObjetCase() instanceof ObjetUtilisable)
									{
										// Cr�er le noeud d'objet
										objNoeudObjet = objDocumentXML.createElement("objetUtilisable");
										
										// D�finir les attributs de l'objet
										objNoeudObjet.setAttribute("id", Integer.toString(((ObjetUtilisable) objCaseCouleur.obtenirObjetCase()).obtenirId()));
										objNoeudObjet.setAttribute("nom", objCaseCouleur.obtenirObjetCase().getClass().getSimpleName());
										objNoeudObjet.setAttribute("visible", Boolean.toString(((ObjetUtilisable) objCaseCouleur.obtenirObjetCase()).estVisible()));
									}
									else
									{
										// Cr�er le noeud d'objet
										objNoeudObjet = objDocumentXML.createElement("piece");
										
										// D�finir la valeur de l'objet
										objNoeudObjet.setAttribute("valeur", Integer.toString(((Piece) objCaseCouleur.obtenirObjetCase()).obtenirValeur()));										
									}
									
									// Ajouter le noeud objet au noeud de la case
									objNoeudCase.appendChild(objNoeudObjet);
								}
							}
							
							// Ajouter la case courante au noeud du plateau de 
							// jeu
							objNoeudParametrePlateauJeu.appendChild(objNoeudCase);
						}
					}
				}
                                
				// Ajouter le noeud param�tre au noeud de commande
				objNoeudCommande.appendChild(objNoeudParametreTempsPartie);
				objNoeudCommande.appendChild(objNoeudParametreTaillePlateauJeu);
				objNoeudCommande.appendChild(objNoeudParametrePositionJoueurs);
				objNoeudCommande.appendChild(objNoeudParametrePlateauJeu);
	
				// Ajouter le noeud de commande au noeud racine dans le document
				objDocumentXML.appendChild(objNoeudCommande);
			}
			
			objNoeudCommande.setAttribute("no", Integer.toString(information.obtenirNoCommande()));

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
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		Moniteur.obtenirInstance().fin();
                if(ControleurJeu.modeDebug) System.out.println("EvenementPartieDemarrer: " + strCodeXML);
		return strCodeXML;
	}
}
