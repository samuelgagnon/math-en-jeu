package ServeurJeu.Evenements;

import java.awt.Point;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Joueurs.Joueur;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurVirtuel;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;
import ServeurJeu.ComposantesJeu.Objets.Pieces.Piece;
import ServeurJeu.ComposantesJeu.Tables.Table;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class EvenementPartieDemarree extends Evenement
{
	// D�claration d'une variable qui va garder le temps de la partie
	private int intTempsPartie;

	// D�claration d'une liste contenant les joueurs 
	private Joueur[] lstJoueurs;

	private Table table;

	/**
	 * Constructeur de la classe EvenementPartieDemarree qui permet 
	 * d'initialiser le num�ro de la table et le plateau de jeu pour la partie 
	 * qui vient de d�buter.
	 * @param playersListe 
	 *
	 * @param int tempsPartie : Le temps total de la partie
	 * @param TreeMap listePositionJoueurs : La liste des positions des joueurs
	 * @param Case[][] plateauJeu : Un tableau � 2 dimensions repr�sentant le 
	 * 								plateau de jeu
	 */
	public EvenementPartieDemarree(Table table, Joueur[] playersListe)
	{
		// D�finir le temps de la partie, le plateau de jeu et la liste des
		// positions des joueurs
		intTempsPartie = table.obtenirTempsTotal();
		this.table = table;
		lstJoueurs = playersListe;

		generateXML();
	}


	private void generateXML()
	{
		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXML.createElement("commande");

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


		// Cr�er le noeud contenant la position initiale du pointsFinish
		Element objNoeudParametrePositionPointsFinish = objDocumentXML.createElement("parametre");
		objNoeudParametrePositionPointsFinish.setAttribute("type", "positionPointsFinish");
		objNoeudParametrePositionPointsFinish.setAttribute("tracks", Integer.toString(table.getRegles().getNbTracks()));

		for(int i = 0; i < table.getLstPointsFinish().size(); i++)
		{
			Point objPosition = table.getLstPointsFinish().get(i);

			// Cr�er un noeud de case
			Element objNoeudParametrePositionPoint = objDocumentXML.createElement("positionPoint");

			// D�finir les attributs du noeud courant
			objNoeudParametrePositionPoint.setAttribute("x", Integer.toString(objPosition.x));
			objNoeudParametrePositionPoint.setAttribute("y", Integer.toString(objPosition.y));

			// Ajouter le noeud de position courant au noeud param�tre
			objNoeudParametrePositionPointsFinish.appendChild(objNoeudParametrePositionPoint);


		}
		//objNoeudParametrePositionWinTheGame.setAttribute("pointageRequis", Integer.toString(table.pointageRequisPourAllerSurLeWinTheGame()));
		objNoeudCommande.appendChild(objNoeudParametrePositionPointsFinish);




		// Cr�er les informations concernant la taille
		objNoeudParametreTaille.setAttribute("nbLignes", Integer.toString(table.obtenirPlateauJeuCourant().length));
		objNoeudParametreTaille.setAttribute("nbColonnes", Integer.toString(table.obtenirPlateauJeuCourant()[0].length));

		// Ajouter les noeuds enfants aux noeuds param�tres
		objNoeudParametreTaillePlateauJeu.appendChild(objNoeudParametreTaille);

		for(int i = 0; i < lstJoueurs.length; i++)
		{
			// Cr�er un noeud de case en passant le bon nom
			Element objNoeudPositionJoueur = objDocumentXML.createElement("position");
			if(lstJoueurs[i] instanceof JoueurHumain)
			{
				JoueurHumain joueur = ((JoueurHumain)lstJoueurs[i]);
				// D�finir les attributs du noeud courant
				objNoeudPositionJoueur.setAttribute("nom", joueur.obtenirNom() );
				objNoeudPositionJoueur.setAttribute("x", Integer.toString(joueur.obtenirPartieCourante().obtenirPositionJoueur().x));
				objNoeudPositionJoueur.setAttribute("y", Integer.toString(joueur.obtenirPartieCourante().obtenirPositionJoueur().y));
				objNoeudPositionJoueur.setAttribute("clocolor", Integer.toString(joueur.obtenirPartieCourante().getClothesColor()));

			}else if(lstJoueurs[i] instanceof JoueurVirtuel)
			{
				JoueurVirtuel joueurV = ((JoueurVirtuel)lstJoueurs[i]);
				// D�finir les attributs du noeud courant
				objNoeudPositionJoueur.setAttribute("nom", joueurV.obtenirNom() );
				objNoeudPositionJoueur.setAttribute("x", Integer.toString(joueurV.obtenirPartieCourante().obtenirPositionJoueur().x));
				objNoeudPositionJoueur.setAttribute("y", Integer.toString(joueurV.obtenirPartieCourante().obtenirPositionJoueur().y));
				objNoeudPositionJoueur.setAttribute("clocolor", Integer.toString(joueurV.obtenirPartieCourante().getClothesColor()));
			}
			// Ajouter le noeud de position courant au noeud param�tre
			objNoeudParametrePositionJoueurs.appendChild(objNoeudPositionJoueur);
		}

		// Passer toutes les lignes du plateau de jeu et cr�er toutes 
		// les cases
		for (int i = 0; i < table.obtenirPlateauJeuCourant().length; i++)
		{
			// Passer toutes les colonnes du plateau de jeu
			for (int j = 0; j < table.obtenirPlateauJeuCourant()[0].length; j++)
			{
				// S'il y a une case au point courant, alors on peut la 
				// cr�er en XML, sinon on ne fait rien
				if (table.obtenirPlateauJeuCourant()[i][j] != null)
				{
					// D�claration d'un noeud de case
					Element objNoeudCase;

					// Si la classe de l'objet courant est CaseCouleur,
					// alors on va cr�er l'�l�ment en passant le bon nom
					if (table.obtenirPlateauJeuCourant()[i][j] instanceof CaseCouleur)
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
					objNoeudCase.setAttribute("type", Integer.toString(table.obtenirPlateauJeuCourant()[i][j].obtenirTypeCase()));

					// Si la case courante est une case couleur, alors
					// on d�finit son objet, sinon on ne fait rien de 
					// plus pour une case sp�ciale
					if (table.obtenirPlateauJeuCourant()[i][j] instanceof CaseCouleur)
					{
						// Cr�er une r�f�rence vers la case couleur 
						// courante
						CaseCouleur objCaseCouleur = (CaseCouleur) table.obtenirPlateauJeuCourant()[i][j];

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

	} // end method


}// end class
