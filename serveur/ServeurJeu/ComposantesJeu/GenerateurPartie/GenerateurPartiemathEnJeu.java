package ServeurJeu.ComposantesJeu.GenerateurPartie;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import Enumerations.Visibilite;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Cases.CaseSpeciale;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin1;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin2;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin3;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Banane;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Boule;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Brainiac;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.Livre;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;
import ServeurJeu.ComposantesJeu.Objets.Pieces.Piece;
import ServeurJeu.ComposantesJeu.ReglesJeu.ReglesMagasin;
import ServeurJeu.ComposantesJeu.ReglesJeu.ReglesObjetUtilisable;
import ServeurJeu.ComposantesJeu.Tables.Table;

/**
 * @author Oloieri Lilian 31.12.2009 
 */
public class GenerateurPartiemathEnJeu extends GenerateurPartie {

	
	
	public GenerateurPartiemathEnJeu() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
     * Cette fonction permet de retourner une matrice � deux dimensions
     * repr�sentant le plateau de jeu qui contient les informations sur 
     * chaque case selon des param�tres.
     * @param lstPointsFinish 
     * @param Regles reglesPartie : L'ensemble des r�gles pour la partie
     * @param Vector listePointsCaseLibre : La liste des points des cases 
     * 										libres (param�tre de sortie)
     * @return Case[][] : Un tableau � deux dimensions contenant l'information
     * 					  sur chaque case.
     * @throws NullPointerException : Si la liste pass�e en param�tre qui doit 
     * 								  �tre remplie est nulle
     */
    public Case[][] genererPlateauJeu(ArrayList<Point> lstPointsCaseLibre, ArrayList<Point> lstPointsFinish, 
    		Table table) throws NullPointerException
    {
		//*********** 
    	this.lstPointsCasesPresentes = new LinkedList<Point>();
		this.lstPointsCasesSpeciales = new LinkedList<Point>();
		this.lstPointsCasesCouleur = new LinkedList<Point>();
		this.lstPointsMagasins = new LinkedList<Point>();
		this.lstPointsPieces = new LinkedList<Point>();
		this.lstPointsObjetsUtilisables = new LinkedList<Point>();
		//this.intCompteurCases = 0;
		this.intCompteurIdObjet = 1;
		this.objCaseParcourue = new CaseCouleur(1);
    	//***************
    	
    	
    	this.reglesPartie = table.getRegles();
		this.objGestionnaireBD = table.getObjSalle().getObjControleurJeu().obtenirGestionnaireBD();
		this.objSalle = table.getObjSalle();
    	
		int temps = table.obtenirTempsTotal();
		
		// calculate number of lines and of columns  
        //if gametype = mathenjeu
		
		// Modifier le temps pour qu'il soit au moins le minimum de minutes
		temps = Math.max(temps, reglesPartie.obtenirTempsMinimal());
			
		// Modifier le temps pour qu'il soit au plus le maximum de minutes
		temps = Math.min(temps, reglesPartie.obtenirTempsMaximal());
			
		//to have a more equilibrate dimension of game table
		temps = (int) Math.ceil(temps * 5 / 10) + 10;
			
		// Le nombre de lignes sera de ceiling(temps / 2) � temps
		if(intNbLines == 0)
		   intNbLines = objRandom.nextInt(temps - ((int) Math.ceil(temps /2)) + 1) + ((int) Math.ceil(temps /2 ));

		// Le nombre de colonnes sera de temps � 2 * temps 
		if(intNbColumns == 0)
		   intNbColumns = (int) Math.floor((temps * temps ) / intNbLines); 
		
		// D�claration de variables qui vont garder le nombre de trous, 
		// le nombre de cases sp�ciales, le nombres de magasins,
		// le nombre de pi�ces, le nombre de
		int intNbTrous = ((int) Math.floor(intNbLines * intNbColumns * reglesPartie.obtenirRatioTrous()));
		int intNbCasesSpeciales = (int) Math.floor(intNbLines * intNbColumns * reglesPartie.obtenirRatioCasesSpeciales());
		int intNbMagasins = (int) Math.floor(intNbLines * intNbColumns * reglesPartie.obtenirRatioMagasins());
		int intNbPieces = (int) Math.floor(intNbLines * intNbColumns * reglesPartie.obtenirRatioPieces());
		int intNbObjetsUtilisables = (int) Math.floor(intNbLines * intNbColumns * reglesPartie.obtenirRatioObjetsUtilisables());

		//System.out.println("temps : " + temps);
		//System.out.println("lignes : " + intNbLines);
		//System.out.println("colognes : " + intNbColumns);
					
		// Maintenant qu'on a le nombre de lignes et de colonnes, on va cr�er
		// le tableau � 2 dimensions repr�sentant le plateau de jeu (null est 
		// mis par d�faut dans chaque �l�ment)
		Case[][] objttPlateauJeu = new Case[intNbLines][intNbColumns];	
		
					
		boardCreation(intNbTrous, objttPlateauJeu); 
		
		for(int i = 0; i < reglesPartie.getNbVirtualPlayers(); i++)
		{
			objPoint = (Point) lstPointsCasesPresentes.get(objRandom.nextInt(lstPointsCasesPresentes.size()));
			lstPointsFinish.add(objPoint);
		} 
		caseDefinition(intNbCasesSpeciales, objttPlateauJeu, intNbTrous);
			
		// Si on doit afficher des magasins dans le plateau de jeu, 
		// alors on fait le code suivant
		if (reglesPartie.obtenirListeMagasinsPossibles().size() > 0)
		{
			// R�initialiser le compteur de cases
			intCompteurCases = 1;
			
			// Obtenir un it�rateur pour la liste des r�gles de magasins
			// tri�es par priorit� (c'est certain que la premi�re fois il y a au 
			// moins une r�gle de case)
			Iterator<ReglesMagasin> objIterateurListePriorite = reglesPartie.obtenirListeMagasinsPossibles().iterator();
			
			// On va choisir des magasins en commen�ant par la case la plus 
			// prioritaire et on va faire �a tant qu'on n'a pas atteint le 
			// pourcentage de magasins devant se trouver sur le plateau 
			// de jeu. Si on atteint la fin de la liste de magasins, on 
			// recommence depuis le d�but
			while (intCompteurCases <= intNbMagasins)
			{
				// Faire la r�f�rence vers la r�gle du magasin courant 
				ReglesMagasin objReglesMagasin = objIterateurListePriorite.next();
				
				// Obtenir un point al�atoirement parmi les points restants
				// qui n'ont pas de magasins et enlever en m�me temps ce point 
				// de la liste
				objPoint = (Point) lstPointsCasesPresentes.remove(objRandom.nextInt(lstPointsCasesPresentes.size()));
				
				// Ajouter le point trouv� dans la liste des points de magasins 
				// trouv�s
				lstPointsMagasins.add(objPoint);

				// Si le nom du magasin est Magasin1, alors on met un objet 
				// Magasin(1) sur la case, sinon on fait le m�me genre de 
				// v�rifications pour les autres types de magasins                                
				if (objReglesMagasin.obtenirNomMagasin().equals("Magasin1") || 
						objReglesMagasin.obtenirNomMagasin().equals("Shop1"))
				{
					// D�finir la valeur de la case au point sp�cifi� � la case 
					// d'identification
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Magasin1());
				}
				else if (objReglesMagasin.obtenirNomMagasin().equals("Magasin2") || 
						objReglesMagasin.obtenirNomMagasin().equals("Shop2"))
				{
					// D�finir la valeur de la case au point sp�cifi� � la case 
					// d'identification
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Magasin2());
				}else if (objReglesMagasin.obtenirNomMagasin().equals("Magasin3") || 
						objReglesMagasin.obtenirNomMagasin().equals("Shop3"))
				{
					// D�finir la valeur de la case au point sp�cifi� � la case 
					// d'identification
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Magasin3());
				}
				// Aller chercher une r�f�rence vers le magasin que l'on vient de cr�er
				Magasin objMagasin = (Magasin)((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).obtenirObjetCase();

				
				// Get the list of items to be sold by shops
				ArrayList<String> listObjects = new ArrayList<String>();
				objGestionnaireBD.fillShopObjects(objReglesMagasin.obtenirNomMagasin(), listObjects);
				
				for(String nomDeLObjet : listObjects)
				{
                   	// Incr�menter le compteur de ID pour les objets
                    intCompteurIdObjet++;

                    
                    // On cr�e un nouvel objet du type correspondant
                    // puis on l'ajoute dans la liste des objets utilisables du magasin
                    if(nomDeLObjet.equals("Livre"))
                    {
                        Livre objAAjouter = new Livre(intCompteurIdObjet, true);
                        objMagasin.ajouterObjetUtilisable((ObjetUtilisable)objAAjouter);
                    }
                    else if(nomDeLObjet.equals("Boule"))
                    {
                        Boule objAAjouter = new Boule(intCompteurIdObjet, true);
                        objMagasin.ajouterObjetUtilisable((ObjetUtilisable)objAAjouter);
                    }                    
                    else if(nomDeLObjet.equals("Banane"))
                    {
                        Banane objAAjouter = new Banane(intCompteurIdObjet, true);
                        objMagasin.ajouterObjetUtilisable((ObjetUtilisable)objAAjouter);
                    }
				}// end for              
				
				// Incr�menter le nombre de cases pass�es
				intCompteurCases++;
				
				// Si on est arriv� � la fin de la liste, alors il faut 
				// retourner au d�but
				if (objIterateurListePriorite.hasNext() == false)
				{
					// Obtenir un autre it�rateur pour la liste
					objIterateurListePriorite = reglesPartie.obtenirListeMagasinsPossibles().iterator();
				}
			}			
		}
		
		// Bloc de code qui va s'assurer de cr�er les pi�ces dans le plateau de jeu
		// R�initialiser le compteur de cases
			intCompteurCases = 1;
			
			// On va choisir des pi�ces dont la valeur est al�atoire selon 
			// une loi normale centr�e � 0 tant qu'on n'a pas atteint le 
			// nombre de pi�ces d�sir�
			while (intCompteurCases <= intNbPieces)
			{
				// Calculer la valeur de la pi�ce � cr�er de fa�on al�atoire 
				// selon une loi normale
				int intValeur =1; // Math.max(Math.abs(ClassesUtilitaires.UtilitaireNombres.genererNbAleatoireLoiNormale(0.0d, Math.pow(((double) reglesPartie.obtenirValeurPieceMaximale()) / 3.0d, 2.0d))), 1);
					
				// Obtenir un point al�atoirement parmi les points restants
				// qui n'ont pas de pi�ces et enlever en m�me temps ce point 
				// de la liste
				objPoint = (Point) lstPointsCasesPresentes.remove(objRandom.nextInt(lstPointsCasesPresentes.size()));
				
				// Ajouter le point trouv� dans la liste des points de pi�ces 
				// trouv�es
				lstPointsPieces.add(objPoint);
				
				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Piece(intValeur, 1));				
				
				// Incr�menter le nombre de cases pass�es
				intCompteurCases++;
			}			
		
			
		
		// Si on doit afficher des objets utilisables dans le plateau de jeu, 
		// alors on fait le code suivant
		if (reglesPartie.obtenirListeObjetsUtilisablesPossibles().size() > 0)
		{
			// R�initialiser le compteur de cases
			intCompteurCases = 1;
			
			// Obtenir un it�rateur pour la liste des r�gles d'objets utilisables
			// tri�s par priorit� (c'est certain que la premi�re fois il y a au 
			// moins une r�gle de case)
			Iterator<ReglesObjetUtilisable> objIterateurListePriorite = reglesPartie.obtenirListeObjetsUtilisablesPossibles().iterator();
			
			// On va choisir des objets utilisables en commen�ant par 
			// l'objet le plus prioritaire et on va faire �a tant qu'on n'a 
			// pas atteint le pourcentage d'objets utilisables devant se 
			// trouver sur le plateau de jeu. Si on atteint la fin de la 
			// liste d'objets utilisables, on recommence depuis le d�but
			while (intCompteurCases <= intNbObjetsUtilisables)
			{
				// Faire la r�f�rence vers la r�gle de l'objet utilisable 
				// courant
				ReglesObjetUtilisable objReglesObjetUtilisable = objIterateurListePriorite.next();
				
				// D�claration d'une variable qui va permettre de savoir si 
				// l'objet doit �tre visible ou non
				boolean bolEstVisible;
				
				// Si l'objet est toujours visible, alors on va dire qu'il 
				// est visible
				if (objReglesObjetUtilisable.obtenirVisibilite().equals(Visibilite.ToujoursVisible))
				{
					bolEstVisible = true;
				}
				// Si l'objet est jamais visible, alors on va dire qu'il 
				// n'est pas visible
				else if (objReglesObjetUtilisable.obtenirVisibilite().equals(Visibilite.JamaisVisible))
				{
					bolEstVisible = false;
				}
				// Si l'objet doit avoir une visibilit� al�atoire, alors 
				// on va g�n�rer un nombre al�atoire qui va donner soit true 
				// soit false
				else
				{
					bolEstVisible = objRandom.nextBoolean();
				}
				
				// Obtenir un point al�atoirement parmi les points restants
				// qui n'ont pas d'objets utilisables et enlever en m�me temps 
				// ce point de la liste
				objPoint = (Point) lstPointsCasesPresentes.remove(objRandom.nextInt(lstPointsCasesPresentes.size()));
				
				// Ajouter le point trouv� dans la liste des points d'objets 
				// utilisables trouv�s
				lstPointsObjetsUtilisables.add(objPoint);

				// Si le nom de l'objet est Livre, alors on met un objet 
				// Livre sur la case, sinon on fait le m�me genre de 
				// v�rifications pour les autres types de magasins
                                // On d�finit la valeur de la case au point sp�cifi� � la case d'identification
				if (objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Livre") || 
						objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Book"))
				{
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Livre(intCompteurIdObjet, bolEstVisible));					
				}                          
                                else if (objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Boule") ||
                                		objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Sphere"))
				{
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Boule(intCompteurIdObjet, bolEstVisible));					
				}                                
                                else if (objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Banane") ||
                                		objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Banana"))
				{
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Banane(intCompteurIdObjet, bolEstVisible));					
				}
                                else if (objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Brainiac") ||
                                		objReglesObjetUtilisable.obtenirNomObjetUtilisable().equals("Brainiac"))
				{
					((CaseCouleur) objttPlateauJeu[objPoint.x][objPoint.y]).definirObjetCase(new Brainiac(intCompteurIdObjet, bolEstVisible));					
				}
				
				// Incr�menter le nombre de cases pass�es
				intCompteurCases++;
				
				// Incr�menter le compteur des id des objets
				intCompteurIdObjet++;
				
				// Si on est arriv� � la fin de la liste, alors il faut 
				// retourner au d�but
				if (objIterateurListePriorite.hasNext() == false)
				{
					// Obtenir un autre it�rateur pour la liste
					objIterateurListePriorite = reglesPartie.obtenirListeObjetsUtilisablesPossibles().iterator();
				}
			}			
		}
		
		// Ajouter les points restants dans la liste des points repr�sentant 
		// les cases sans objets et n'�tant pas des cases sp�ciales
		 lstPointsCaseLibre.addAll(lstPointsCasesPresentes);
		
		// Indiquer quel a �t� le dernier id des objets
		table.setObjProchainIdObjet(intCompteurIdObjet);
				
		return objttPlateauJeu;
    }// end method

	
	 /**
     * Method for "mathenjeu" game board
     * @param intCompteurCases
     * @param objttPlateauJeu
     */
	private void caseDefinition(int intNbCasesSpeciales, Case[][] objttPlateauJeu, int intNbTrous) {
		Point objPoint;
		// Si on doit afficher des cases sp�ciales dans le plateau de jeu, 
		// alors on fait le code suivant
		//if (reglesPartie.obtenirListeCasesSpecialesPossibles().size() > 0)
		//{
			// R�initialiser le compteur de cases
			intCompteurCases = 1;
			
			// Obtenir un it�rateur pour la liste des r�gles de cases sp�ciales
			// tri�es par priorit� (c'est certain que la premi�re fois il y a au 
			// moins une r�gle de case)
			//Iterator objIterateurListePriorite = reglesPartie.obtenirListeCasesSpecialesPossibles().iterator();
			
			// On va choisir des cases sp�ciales en commen�ant par la case
			// la plus prioritaire et on va faire �a tant qu'on n'a pas atteint 
			// le pourcentage de cases sp�ciales devant se trouver sur le plateau 
			// de jeu. Si on atteint la fin de la liste de cases sp�ciales, on 
			// recommence depuis le d�but
			while (intCompteurCases <= intNbCasesSpeciales)
			{
				// Faire la r�f�rence vers la r�gle de la case sp�ciale 
				// courante
				//ReglesCaseSpeciale objReglesCaseSpeciale = (ReglesCaseSpeciale) objIterateurListePriorite.next();
				
				// Obtenir un point al�atoirement parmi les points restants
				// qui n'ont pas de cases sp�ciales et enlever en m�me temps 
				// ce point de la liste
				objPoint = (Point) lstPointsCasesPresentes.remove(objRandom.nextInt(lstPointsCasesPresentes.size()));
				
				// Ajouter le point trouv� dans la liste des points de cases 
				// sp�ciales trouv�es
				lstPointsCasesSpeciales.add(objPoint);

				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				objttPlateauJeu[objPoint.x][objPoint.y] = new CaseSpeciale(1);				
				
				// Incr�menter le nombre de cases pass�es
				intCompteurCases++;
				
				// Si on est arriv� � la fin de la liste, alors il faut 
				// retourner au d�but
				//if (objIterateurListePriorite.hasNext() == false)
				//{
					// Obtenir un autre it�rateur pour la liste
					//objIterateurListePriorite = reglesPartie.obtenirListeCasesSpecialesPossibles().iterator();
				//}
			//}			
		}
		
		// Bloc de code qui va s'assurer de cr�er les cases de couleur dans le 
		// plateau de jeu (il y en a au moins un type)
		{
			// R�initialiser le compteur de cases
			intCompteurCases = 1;
			
			// Obtenir un it�rateur pour la liste des r�gles de cases de couleur
			// tri�es par priorit� (c'est certain que la premi�re fois il y a au 
			// moins une r�gle de case)
			//Iterator objIterateurListePriorite = reglesPartie.obtenirListeCasesCouleurPossibles().iterator();
			
			// On va choisir des cases de couleur en commen�ant par la case
			// la plus prioritaire et on va faire �a tant qu'on n'a pas atteint 
			// le pourcentage de cases sp�ciales devant se trouver sur le plateau 
			// de jeu. Si on atteint la fin de la liste de cases de couleur, on 
			// recommence depuis le d�but
			while (intCompteurCases <= (intNbLines * intNbColumns) - intNbTrous - intNbCasesSpeciales)
			{
				// Faire la r�f�rence vers la r�gle de la case de couleur 
				// courante
				//ReglesCaseCouleur objReglesCaseCouleur = (ReglesCaseCouleur) objIterateurListePriorite.next();
				
				// Obtenir un point al�atoirement parmi les points restants
				// qui n'ont pas de cases sp�ciales et de cases de couleur 
				// et enlever en m�me temps ce point de la liste
				objPoint = (Point) lstPointsCasesPresentes.remove(objRandom.nextInt(lstPointsCasesPresentes.size()));
				
				// Ajouter le point trouv� dans la liste des points de cases 
				// de couleur trouv�es
				lstPointsCasesCouleur.add(objPoint);

				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				objttPlateauJeu[objPoint.x][objPoint.y] = new CaseCouleur(1);				
				
				// Incr�menter le nombre de cases pass�es
				intCompteurCases++;
				
				// Si on est arriv� � la fin de la liste, alors il faut 
				// retourner au d�but
				//if (objIterateurListePriorite.hasNext() == false)
				//{
					// Obtenir un autre it�rateur pour la liste
					//objIterateurListePriorite = reglesPartie.obtenirListeCasesCouleurPossibles().iterator();
				//}
			}
			
			// La liste des cases pr�sentes est maintenant la liste des cases 
			// de couleur, car tous les points de la liste ont �t� copi�s dans 
			// l'autre liste
			lstPointsCasesPresentes = lstPointsCasesCouleur;
			lstPointsCasesCouleur = null;
		}
		
	}// end methode

	
	/**
	 * Method used to create the game board for the game type "mathEnJeu"
	 * @param intNbTrous
	 * @param objttPlateauJeu
	 */
	private void boardCreation(int intNbTrous, Case[][] objttPlateauJeu) {
		
		Point objPoint;
        Point objPointFile;
		
		// D�claration d'une file qui va contenir des points
		ArrayList<Point> lstFile = new ArrayList<Point>();
		
		// Trouver un point al�atoire dans le plateau de jeu et le garder 
		// en m�moire (�a va �tre le point de d�part) 
		objPoint = new Point(objRandom.nextInt(intNbLines), objRandom.nextInt(intNbColumns));
		
		// Au point calcul�, on va d�finir la case sp�ciale qui sert 
		// d'identification et dont le type est -1
		objttPlateauJeu[objPoint.x][objPoint.y] = objCaseParcourue;
		
		// Ajouter le point dans la file de priorit� et dans la liste des 
		// points pass�s
		lstFile.add(objPoint);
		lstPointsCasesPresentes.add(objPoint);
		
		// On choisi des cases dans le plateau en leur mettant la case sp�ciale 
		// d'identification comme valeur tant qu'on ne passe pas un certain 
		// pourcentage du nombre de cases totales possibles
		while (intCompteurCases <= (intNbLines * intNbColumns) - intNbTrous)
		{
			// S'il y a au moins un point dans la file, alors on va retirer 
			// ce point et le traiter, sinon il faut prendre un point 
			// al�atoirement parmis les points qui ont �t� pass�s
			if (lstFile.size() > 0)
			{
				// Prendre le point au d�but de la file
				objPointFile = (Point) lstFile.remove(0);
			}
			else
			{
				// Obtenir un point al�atoirement parmi les points qui ont 
				// d�j� �t� pass�s
				objPointFile = (Point) lstPointsCasesPresentes.get(objRandom.nextInt(lstPointsCasesPresentes.size()));
			}
			
					
			// S'il y a une case � gauche, et que cette case n'a pas encore �t�
			// pass�e et que une valeur al�atoire retourne true, alors on va
			// choisir cette case
			
			if ((objPointFile.x - 1 >= 0) && 
				(objttPlateauJeu[objPointFile.x - 1][objPointFile.y] == null) &&
				(objRandom.nextBoolean() == true))
			{
				// Cr�er le point � gauche de la case courante
				objPoint = new Point(objPointFile.x - 1, objPointFile.y);
				
				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				objttPlateauJeu[objPoint.x][objPoint.y] = objCaseParcourue;
				
				// Ajouter le point dans la file de priorit� et dans la liste des 
				// points pass�s
				lstFile.add(objPoint);
				lstPointsCasesPresentes.add(objPoint);
				
				// Incr�menter le nombre de points pass�s
				intCompteurCases++;
			}
			
			// S'il y a une case � droite, et que cette case n'a pas encore �t�
			// pass�e et que une valeur al�atoire retourne true, alors on va
			// choisir cette case
			if ((objPointFile.x + 1 < intNbLines) && 
				(objttPlateauJeu[objPointFile.x + 1][objPointFile.y] == null) && 
				(objRandom.nextBoolean() == true))
			{
				// Cr�er le point � droite de la case courante
				objPoint = new Point(objPointFile.x + 1, objPointFile.y);
				
				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				objttPlateauJeu[objPoint.x][objPoint.y] = objCaseParcourue;
				
				// Ajouter le point dans la file de priorit� et dans la liste des 
				// points pass�s
				lstFile.add(objPoint);
				lstPointsCasesPresentes.add(objPoint);
				
				// Incr�menter le nombre de points pass�s
				intCompteurCases++;
			}
			
			// S'il y a une case en haut, et que cette case n'a pas encore �t�
			// pass�e et que une valeur al�atoire retourne true, alors on va
			// choisir cette case
			if ((objPointFile.y - 1 >= 0) && 
				(objttPlateauJeu[objPointFile.x][objPointFile.y - 1] == null) && 
				(objRandom.nextBoolean() == true))
			{
				// Cr�er le point en haut de la case courante
				objPoint = new Point(objPointFile.x, objPointFile.y - 1);
				
				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				objttPlateauJeu[objPoint.x][objPoint.y] = objCaseParcourue;
				
				// Ajouter le point dans la file de priorit� et dans la liste des 
				// points pass�s
				lstFile.add(objPoint);
				lstPointsCasesPresentes.add(objPoint);
				
				// Incr�menter le nombre de points pass�s
				intCompteurCases++;
			}
			
			// S'il y a une case en bas, et que cette case n'a pas encore �t�
			// pass�e et que une valeur al�atoire retourne true, alors on va
			// choisir cette case
			if ((objPointFile.y + 1 < intNbColumns) && 
				(objttPlateauJeu[objPointFile.x][objPointFile.y + 1] == null) && 
				(objRandom.nextBoolean() == true))
			{
				// Cr�er le point en bas de la case courante
				objPoint = new Point(objPointFile.x, objPointFile.y + 1);
				
				// D�finir la valeur de la case au point sp�cifi� � la case 
				// d'identification
				objttPlateauJeu[objPoint.x][objPoint.y] = objCaseParcourue;
				
				// Ajouter le point dans la file de priorit� et dans la liste des 
				// points pass�s
				lstFile.add(objPoint);
				lstPointsCasesPresentes.add(objPoint);
				
				// Incr�menter le nombre de points pass�s
				intCompteurCases++;
			}
		}
	
	}// end method
	
	
	/**
     * Cette fonction permet de g�n�rer la position des joueurs. Chaque joueur 
     * est g�n�r� sur une case vide.
     * 
     * @param int nbJoueurs : Le nombre de joueurs dont g�n�rer la position
     * @param ArrayList listePointsCaseLibre : La liste des points des cases libres
     * @return Point[] : Un tableau de points pour chaque joueur 
     */
    public Point[] genererPositionJoueurs( Table table, int nbJoueurs, ArrayList<Point> lstPointsCaseLibre)
    {
    	// Cr�er un tableau contenant les nbJoueurs points
		Point[] objtPositionJoueurs = new Point[nbJoueurs];
		
		// Cr�ation d'un objet permettant de g�n�rer des nombres al�atoires
		Random objRandom = new Random();
		
		// Pour tous les joueurs de la partie, on va g�n�rer des positions de joueurs
		for (int i = 0; i < nbJoueurs; i++)
		{
			// Obtenir un point al�atoirement
			objtPositionJoueurs[i] = (Point) lstPointsCaseLibre.remove(objRandom.nextInt(lstPointsCaseLibre.size()));
		}
		
		return objtPositionJoueurs;
    }


}// end class
