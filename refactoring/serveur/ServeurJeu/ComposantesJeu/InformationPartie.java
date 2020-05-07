package ServeurJeu.ComposantesJeu;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ClassesRetourFonctions.RetourVerifierReponseEtMettreAJourPlateauJeu;
import ServeurJeu.ControleurJeu;
import ServeurJeu.BD.GestionnaireBD;
import ServeurJeu.ComposantesJeu.Cases.Case;
import ServeurJeu.ComposantesJeu.Cases.CaseCouleur;
import ServeurJeu.ComposantesJeu.Joueurs.Joueur;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurVirtuel;
import ServeurJeu.ComposantesJeu.Objets.Objet;
import ServeurJeu.ComposantesJeu.Objets.Magasins.Magasin;
import ServeurJeu.ComposantesJeu.Objets.ObjetsUtilisables.ObjetUtilisable;
import ServeurJeu.ComposantesJeu.Objets.Pieces.Piece;
import ServeurJeu.Evenements.GestionnaireEvenements;
import exception.NoQuestionException;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class InformationPartie
{
  
  private  static Logger objLogger = Logger.getLogger( ControleurJeu.class );
  // D�claration d'une r�f�rence vers le gestionnaire de bases de donn�es
  //private GestionnaireBD objGestionnaireBD;
  
        // D�claration d'une r�f�rence vers le gestionnaire d'evenements
  private GestionnaireEvenements objGestionnaireEv;
  
  // D�claration d'une r�f�rence vers un joueur humain correspondant � cet
  // objet d'information de partie
  private JoueurHumain objJoueurHumain;
  
  // D�claration d'une r�f�rence vers la table courante
  private Table objTable;
  
        // D�claration d'une variable qui va contenir le num�ro Id du personnage 
  // choisit par le joueur
  private int intIdPersonnage;
  
        // D�claration d'une variable qui va contenir le pointage de la 
        // partie du joueur poss�dant cet objet
  private int intPointage;
        
        // Combien d'argent ce joueur a-t-il?
        private int intArgent;
  
  // D�claration d'une position du joueur dans le plateau de jeu
  private Point objPositionJoueur;
  
  // D�claration d'un point qui va garder la position o� le joueur
  // veut aller
  private Point objPositionJoueurDesiree;
  
  // D�claration d'une liste de questions qui ont �t� r�pondues 
  // par le joueur
  private TreeMap lstQuestionsRepondues;
  
  // D�claration d'une variable qui va garder la question qui est 
  // pr�sentement pos�e au joueur. S'il n'y en n'a pas, alors il y a 
  // null dans cette variable
  private Question objQuestionCourante;
  
  // D�claration d'une liste d'objets utilisables ramass�s par le joueur
  private TreeMap lstObjetsUtilisablesRamasses;
        
        // D�claration de la bo�te de question personnelle au joueur poss�dant
        // cet objet
        BoiteQuestions objBoiteQuestions;
        
        // D�claration d'un boolean qui dit si le joueur est 'target�' pour subir une banane
        // (si le string n'est pas "", et alors le string dit qui l'a utilis�e)
        private String vaSubirUneBanane;
   
  
  public InformationPartie(GestionnaireEvenements gestionnaireEv, JoueurHumain joueur, Table tableCourante) {
    //  Au d�but, on ne subit pas de banane!
    vaSubirUneBanane = "";
    
    // Faire la r�f�rence vers le gestionnaire de base de donn�es

    // Faire la r�f�rence vers le gestionnaire d'evenements
    objGestionnaireEv = gestionnaireEv;

    // Faire la r�f�rence vers le joueur humain courant
    objJoueurHumain = joueur;

    // D�finir les propri�t�s de l'objet InformationPartie
    intPointage = 0;
        intArgent = 0;
    intIdPersonnage = 0;
    
    // Faire la r�f�rence vers la table courante
    objTable = tableCourante;
    
    // Au d�part, le joueur est nul part
    objPositionJoueur = null;
    
    // Au d�part, le joueur ne veut aller nul part
    objPositionJoueurDesiree = null;
    
    // Au d�part, aucune question n'est pos�e au joueur
    objQuestionCourante = null;
    
    // Cr�er la liste des questions qui ont �t� r�pondues
    lstQuestionsRepondues = new TreeMap();
    
    // Cr�er la liste des objets utilisables qui ont �t� ramass�s
    lstObjetsUtilisablesRamasses = new TreeMap();

    objBoiteQuestions = new BoiteQuestions(joueur.obtenirProtocoleJoueur().langue, joueur.obtenirSalleCourante().getName(joueur.getLangue().getNomCourt()));
    //objGestionnaireBD.remplirBoiteQuestions(objBoiteQuestions, objJoueurHumain.obtenirCleNiveau());
    GestionnaireBD lBd = new GestionnaireBD(ControleurJeu.getInstance().getConnection());
    lBd.remplirBoiteQuestions(objJoueurHumain, objBoiteQuestions);
    

  }
  /**
   * Constructeur de la classe InformationPartie qui permet d'initialiser
   * les propri�t�s de la partie et de faire la r�f�rence vers la table.
   * @deprecated
   */
  /*
  public InformationPartie( GestionnaireEvenements gestionnaireEv, GestionnaireBD gestionnaireBD, JoueurHumain joueur, Table tableCourante)
  {
            // Au d�but, on ne subit pas de banane!
            vaSubirUneBanane = "";
            
            // Faire la r�f�rence vers le gestionnaire de base de donn�es
            //objGestionnaireBD = gestionnaireBD;

            // Faire la r�f�rence vers le gestionnaire d'evenements
            objGestionnaireEv = gestionnaireEv;

            // Faire la r�f�rence vers le joueur humain courant
            objJoueurHumain = joueur;
    
      // D�finir les propri�t�s de l'objet InformationPartie
      intPointage = 0;
            intArgent = 0;
      intIdPersonnage = 0;
      
      // Faire la r�f�rence vers la table courante
      objTable = tableCourante;
      
      // Au d�part, le joueur est nul part
      objPositionJoueur = null;
      
      // Au d�part, le joueur ne veut aller nul part
      objPositionJoueurDesiree = null;
      
      // Au d�part, aucune question n'est pos�e au joueur
      objQuestionCourante = null;
      
      // Cr�er la liste des questions qui ont �t� r�pondues
      lstQuestionsRepondues = new TreeMap();
      
      // Cr�er la liste des objets utilisables qui ont �t� ramass�s
      lstObjetsUtilisablesRamasses = new TreeMap();

            objBoiteQuestions = new BoiteQuestions(joueur.obtenirProtocoleJoueur().langue, joueur.obtenirSalleCourante().obtenirNoeudLangue(), joueur.obtenirSalleCourante().obtenirNomSalle());
            //objGestionnaireBD.remplirBoiteQuestions(objBoiteQuestions, objJoueurHumain.obtenirCleNiveau());
            objGestionnaireBD.remplirBoiteQuestions(objBoiteQuestions, objJoueurHumain.obtenirCleNiveau());
            
            
  }
  */

  /**
   * Cette fonction permet de retourner la r�f�rence vers la table courante 
   * du joueur.
   * 
   * @return Table : La r�f�rence vers la table de cette partie
   */
  public Table obtenirTable()
  {
     return objTable;
  }
  
  /**
   * Cette fonction permet de retourner le pointage du joueur.
   * 
   * @return int : Le pointage du joueur courant
   */
  public int obtenirPointage()
  {
     return intPointage;
  }
  
  /**
   * Cette fonction permet de red�finir le pointage du joueur.
   * 
   * @param int pointage : Le pointage du joueur courant
   */
  public void definirPointage(int pointage)
  {
     intPointage = pointage;
  }
        
  /**
   * Cette fonction permet de retourner l'argent du joueur.
   * 
   * @return int : L'argent du joueur courant
   */
  public int obtenirArgent()
  {
     return intArgent;
  }
  /**
   * Cette fonction permet de red�finir l'argent du joueur.
   * 
   * @param int argent : L'argent du joueur courant
   */
  public void definirArgent(int argent)
  {
     intArgent = argent;
  }
  
  /**
   * Cette fonction permet de retourner le Id du personnage du joueur.
   * 
   * @return int : Le Id du personnage choisi par le joueur
   */
  public int obtenirIdPersonnage()
  {
     return intIdPersonnage;
  }
  
  /**
   * Cette fonction permet de red�finir le personnage choisi par le joueur.
   * 
   * @param int idPersonnage : Le num�ro Id du personnage choisi 
   *               pour cette partie
   */
  public void definirIdPersonnage(int idPersonnage)
  {
     intIdPersonnage = idPersonnage;
  }
  
  /**
   * Cette fonction permet de retourner la position du joueur dans le 
   * plateau de jeu.
   * 
   * @return Point : La position du joueur dans le plateau de jeu
   */
  public Point obtenirPositionJoueur()
  {
     return objPositionJoueur;
  }
  
  /**
   * Cette fonction permet de red�finir la nouvelle position du joueur.
   * 
   * @param Point positionJoueur : La position du joueur
   */
  public void definirPositionJoueur(Point positionJoueur)
  {
    objPositionJoueur = positionJoueur;
  }
  
  /**
   * Cette fonction permet de retourner la liste des questions r�pondues.
   * 
   * @return TreeMap : La liste des questions qui ont �t� r�pondues
   */
  public TreeMap obtenirListeQuestionsRepondues()
  {
     return lstQuestionsRepondues;
  }
  
  /**
   * Cette fonction permet de retourner la question qui est pr�sentement 
   * pos�e au joueur.
   * 
   * @return Question : La question qui est pr�sentement pos�e au joueur
   */
  public Question obtenirQuestionCourante()
  {
     return objQuestionCourante;
  }
  
  /**
   * Cette fonction permet de red�finir la question pr�sentement pos�e 
   * au joueur.
   * 
   * @param Question questionCourante : La question qui est pr�sentement 
   *                    pos�e au joueur
   */
  public void definirQuestionCourante(Question questionCourante)
  {
    objQuestionCourante = questionCourante;
  }
  
  /**
   * Cette fonction d�termine si le d�placement vers une certaine
   * case est permis ou non. Pour �tre permis, il faut que le d�placement
   * d�sir� soit en ligne droite, qu'il n'y ait pas de trous le s�parant
   * de sa position d�sir�e et que la distance soit accept�e comme niveau
   * de difficult� pour la salle. La distance minimale � parcourir est 1.
   * 
   * @param Point nouvellePosition : La position vers laquelle le joueur
   *                   veut aller
   * @return boolean : true si le d�placement est permis
   *           false sinon
   */
  public boolean deplacementEstPermis(Point nouvellePosition)
  {
    boolean bolEstPermis = true;
    
    // Si la position de d�part est la m�me que celle d'arriv�e, alors
    // il y a une erreur, car le personnage doit faire un d�placement d'au
    // moins 1 case
    if (nouvellePosition.x == objPositionJoueur.x && nouvellePosition.y == objPositionJoueur.y)
    {
      bolEstPermis = false;
    }
    
    // D�terminer si la position d�sir�e est en ligne droite par rapport 
    // � la position actuelle
    if (bolEstPermis == true && nouvellePosition.x != objPositionJoueur.x && nouvellePosition.y != objPositionJoueur.y)
    {
      bolEstPermis = false;
    }

    // Si la distance parcourue d�passe le nombre de cases maximal possible, alors il y a une erreur
    if (bolEstPermis == true && ((nouvellePosition.x != objPositionJoueur.x && Math.abs(nouvellePosition.x - objPositionJoueur.x) > objTable.obtenirRegles().obtenirDeplacementMaximal()) || 
                   (nouvellePosition.y != objPositionJoueur.y && Math.abs(nouvellePosition.y - objPositionJoueur.y) > objTable.obtenirRegles().obtenirDeplacementMaximal())))
    {
      bolEstPermis = false;
    }
    
    // Si le d�placement est toujours permis jusqu'� maintenant, alors on 
    // va v�rifier qu'il n'y a pas de trous s�parant le joueur de la 
    // position qu'il veut aller
    if (bolEstPermis == true)
    {
      // Si on se d�place vers la gauche
      if (nouvellePosition.x != objPositionJoueur.x && nouvellePosition.x > objPositionJoueur.x)
      {
        // On commence le d�placement � la case juste � gauche de la 
        // position courante
        int i = objPositionJoueur.x + 1;
        
        // On boucle tant qu'on n'a pas atteint la case de destination
        // et qu'on a pas eu de trous
        while (i <= nouvellePosition.x && bolEstPermis == true)
        {
          // S'il n'y a aucune case � la position courante, alors on 
          // a trouv� un trou et le d�placement n'est pas possible
          if (objTable.obtenirPlateauJeuCourant()[i][objPositionJoueur.y] == null)
          {
            bolEstPermis = false;
          }
          
          i++;
        }
      }
      // Si on se d�place vers la droite
      else if (nouvellePosition.x != objPositionJoueur.x && nouvellePosition.x < objPositionJoueur.x)
      {
        // On commence le d�placement � la case juste � droite de la 
        // position courante
        int i = objPositionJoueur.x - 1;
        
        // On boucle tant qu'on n'a pas atteint la case de destination
        // et qu'on a pas eu de trous
        while (i >= nouvellePosition.x && bolEstPermis == true)
        {
          // S'il n'y a aucune case � la position courante, alors on 
          // a trouv� un trou et le d�placement n'est pas possible
          if (objTable.obtenirPlateauJeuCourant()[i][objPositionJoueur.y] == null)
          {
            bolEstPermis = false;
          }
          
          i--;
        }
      }
      // Si on se d�place vers le bas
      else if (nouvellePosition.y != objPositionJoueur.y && nouvellePosition.y > objPositionJoueur.y)
      {
        // On commence le d�placement � la case juste en bas de la 
        // position courante
        int i = objPositionJoueur.y + 1;
        
        // On boucle tant qu'on n'a pas atteint la case de destination
        // et qu'on a pas eu de trous
        while (i <= nouvellePosition.y && bolEstPermis == true)
        {
          // S'il n'y a aucune case � la position courante, alors on 
          // a trouv� un trou et le d�placement n'est pas possible
          if (objTable.obtenirPlateauJeuCourant()[objPositionJoueur.x][i] == null)
          {
            bolEstPermis = false;
          }
          
          i++;
        }
      }
      // Si on se d�place vers le haut
      else if (nouvellePosition.y != objPositionJoueur.y && nouvellePosition.y < objPositionJoueur.y)
      {
        // On commence le d�placement � la case juste en haut de la 
        // position courante
        int i = objPositionJoueur.y - 1;
        
        // On boucle tant qu'on n'a pas atteint la case de destination
        // et qu'on a pas eu de trous
        while (i >= nouvellePosition.y && bolEstPermis == true)
        {
          // S'il n'y a aucune case � la position courante, alors on 
          // a trouv� un trou et le d�placement n'est pas possible
          if (objTable.obtenirPlateauJeuCourant()[objPositionJoueur.x][i] == null)
          {
            bolEstPermis = false;
          }
          
          i--;
        }
      }
    }
    
    return bolEstPermis;
  }
  
  /**
   * Cette fonction permet de trouver une question selon la difficult�
   * et le type de question � poser.
   * 
   * @param Point nouvellePosition : La position o� le joueur d�sire se d�placer
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *            g�n�rer un num�ro de commande � retourner
   * @return Question : La question trouv�e, s'il n'y a pas eu de d�placement,
   *            alors la question retourn�e est null
   */
  public Question trouverQuestionAPoser(Point nouvellePosition, boolean doitGenererNoCommandeRetour) throws NoQuestionException
  {
    
    //FIXME : change the way questions are loaded when no more questions are available
    
    
    // D�clarations de variables qui vont contenir la cat�gorie de question 
    // � poser, la difficult� et la question � retourner
    int intCategorieQuestion = objTable.obtenirPlateauJeuCourant()[nouvellePosition.x][nouvellePosition.y].obtenirTypeCase();
    int intDifficulte = 0;
                int grandeurDeplacement = 0;
    Question objQuestionTrouvee = null;
    
    //check to see if the questions box is empty or not
    
                // Si la position en x est diff�rente de celle d�sir�e, alors
                // c'est qu'il y a eu un d�placement sur l'axe des x
                if (objPositionJoueur.x != nouvellePosition.x)
                {
                        grandeurDeplacement = Math.abs(nouvellePosition.x - objPositionJoueur.x);
                }
                // Si la position en y est diff�rente de celle d�sir�e, alors
                // c'est qu'il y a eu un d�placement sur l'axe des y
                else if (objPositionJoueur.y != nouvellePosition.y)
                {
                        grandeurDeplacement = Math.abs(nouvellePosition.y - objPositionJoueur.y);
                }
                
                int distanceFuture = Math.abs(nouvellePosition.x - objTable.obtenirPositionWinTheGame().x) + Math.abs(nouvellePosition.y - objTable.obtenirPositionWinTheGame().y);
                distanceFuture -= 1;
                if(distanceFuture < 0) distanceFuture = 0;
                int stepDifficulte = Math.max(Math.abs(this.objTable.obtenirPlateauJeuCourant()[0].length-objTable.obtenirPositionWinTheGame().y), Math.abs(objTable.obtenirPositionWinTheGame().y-this.objTable.obtenirPlateauJeuCourant()[0].length)) / 5;
                intDifficulte = 0;
                
                if(stepDifficulte * 0 <= distanceFuture && distanceFuture <= stepDifficulte * 1) intDifficulte = 6;
                if(stepDifficulte * 1 < distanceFuture && distanceFuture <= stepDifficulte * 2) intDifficulte = 5;
                if(stepDifficulte * 2 < distanceFuture && distanceFuture <= stepDifficulte * 3) intDifficulte = 4;
                if(stepDifficulte * 3 < distanceFuture && distanceFuture <= stepDifficulte * 4) intDifficulte = 3;
                if(stepDifficulte * 4 < distanceFuture && distanceFuture <= stepDifficulte * 5) intDifficulte = 2;
                if(intDifficulte == 0) intDifficulte = 1;
                intDifficulte = Math.max(intDifficulte, grandeurDeplacement);
    
    // Il faut que la difficult� soit plus grande que 0 pour pouvoir trouver 
    // une question
    if (intDifficulte > 0)
    {
      
      objQuestionTrouvee = trouverQuestion(intCategorieQuestion, intDifficulte);
    }
    
    // S'il y a eu une question trouv�e, alors on l'ajoute dans la liste 
    // des questions pos�es et on la garde en m�moire pour pouvoir ensuite
    // traiter la r�ponse du joueur, on va aussi garder la position que le
    // joueur veut se d�placer
    if (objQuestionTrouvee != null)
    {
      lstQuestionsRepondues.put(new Integer(objQuestionTrouvee.obtenirCodeQuestion()), objQuestionTrouvee);
      objQuestionCourante = objQuestionTrouvee;
      objPositionJoueurDesiree = nouvellePosition;
    }
    else if (intDifficulte > 0)
    {
      GestionnaireBD lBd = new GestionnaireBD(ControleurJeu.getInstance().getConnection());
      //lBd.remplirBoiteQuestions( objBoiteQuestions, objJoueurHumain.obtenirCleNiveau(), intDifficulte);
      lBd.remplirBoiteQuestions(objJoueurHumain, objBoiteQuestions);
      objQuestionTrouvee = trouverQuestion(intCategorieQuestion, intDifficulte);
      
      lstQuestionsRepondues.clear();
      
      // S'il y a eu une question trouv�e, alors on l'ajoute dans la liste 
      // des questions pos�es et on la garde en m�moire pour pouvoir ensuite
      // traiter la r�ponse du joueur
      if (objQuestionTrouvee != null)
      {
        lstQuestionsRepondues.put(new Integer(objQuestionTrouvee.obtenirCodeQuestion()), objQuestionTrouvee);
        objQuestionCourante = objQuestionTrouvee;
        objPositionJoueurDesiree = nouvellePosition;
      }
      else
      {
        
        //check for a question of a lower difficulty level
        while (intDifficulte > 0 && objQuestionTrouvee == null) {
          intDifficulte--;
          objQuestionTrouvee = trouverQuestion(intCategorieQuestion, intDifficulte);
        }
        
        if (objQuestionTrouvee != null)
        {
          lstQuestionsRepondues.put(new Integer(objQuestionTrouvee.obtenirCodeQuestion()), objQuestionTrouvee);
          objQuestionCourante = objQuestionTrouvee;
          objPositionJoueurDesiree = nouvellePosition;
        } else {
        
          //should not end up here anymore
          throw new NoQuestionException("Unable to get a question.");
          
        }
        
      }
    }
    
    // Si on doit g�n�rer le num�ro de commande de retour, alors
    // on le g�n�re, sinon on ne fait rien (�a devrait toujours
    // �tre vrai, donc on le g�n�re tout le temps)
    if (doitGenererNoCommandeRetour == true)
    {
      // G�n�rer un nouveau num�ro de commande qui sera 
        // retourn� au client
        objJoueurHumain.obtenirProtocoleJoueur().genererNumeroReponse();              
    }
    
    return objQuestionTrouvee;
  }
  
  /**
   * Cette fonction essaie de de piger une question du niveau de dificult� proche 
   * de intDifficulte, si on y arrive pas, �a veut dire qu'il ne 
   * reste plus de questions de niveau de difficult� proche 
   * de intDifficulte
   * 
   * @param intCategorieQuestion
   * @param intDifficulte
   * @return la question trouver ou null si aucune question n'a pu �tre pig�e
   */
  
  private Question trouverQuestion(int intCategorieQuestion, int intDifficulte)
  {
    
    int intDifficulteTmp=intDifficulte;
    Question objQuestionTrouvee = null;
    int i=0;
    do
    {
      if(i%2==0)
      {
        intDifficulteTmp+=i;
      }
      else
      {
        intDifficulteTmp-=i;
      }
      
      i++;
      
      if(intDifficulteTmp>0)
      {
        objQuestionTrouvee = objBoiteQuestions.pigerQuestion( intCategorieQuestion, intDifficulteTmp );
      }
      if(i>=5)
      {
        break;
      }
    }while(objQuestionTrouvee==null);
    
    return objQuestionTrouvee;
    
  }
  
  /**
   * Cette fonction met � jour le plateau de jeu si le joueur a bien r�pondu
   * � la question. Les objets sur la nouvelle case sont enlev�s et le pointage et l'argent
   * du joueur sont mis � jour. Utilis� par les joueurs humains et les joueurs virtuels
   *
   */
  public static RetourVerifierReponseEtMettreAJourPlateauJeu verifierReponseEtMettreAJourPlateauJeu(String reponse, 
      Point objPositionDesiree, Joueur objJoueur)
    {
        
    // D�claration de l'objet de retour 
    RetourVerifierReponseEtMettreAJourPlateauJeu objRetour = null;
    
    int intPointageCourant; 
                int intArgentCourant;
    Table table;
    int intDifficulteQuestion;
    TreeMap objListeObjetsUtilisablesRamasses; 
    Point positionJoueur; 
    GestionnaireEvenements gestionnaireEv;
    Question objQuestion; 
    String nomJoueur; 
    boolean bolReponseEstBonne; 
    
    int maxObjet;
    int maxObjetVente;
    

    // Obtenir les divers informations � utiliser d�pendamment de si
    // la fonction s'applique � un joueur humain ou un joueur virtuel
    //TODO: change this to be more generic : be able to do objJoueur.obtenirPartieCourante()
    if (objJoueur instanceof JoueurHumain)
    {
      InformationPartie objPartieCourante = ((JoueurHumain)objJoueur).obtenirPartieCourante();
      
      // Obtenir les informations du joueur humain
      maxObjet = objPartieCourante.objTable.obtenirRegles().obtenirMaxObjet();
      maxObjetVente = objPartieCourante.objTable.obtenirRegles().obtenirMaxObjetsVente();
      
      intPointageCourant = objPartieCourante.obtenirPointage();
                        intArgentCourant = objPartieCourante.obtenirArgent();
        table = objPartieCourante.obtenirTable();
        intDifficulteQuestion = objPartieCourante.obtenirQuestionCourante().obtenirDifficulte();
        objListeObjetsUtilisablesRamasses = objPartieCourante.obtenirListeObjets();
        positionJoueur = objPartieCourante.obtenirPositionJoueur();
        gestionnaireEv = objPartieCourante.obtenirGestionnaireEvenements();
        objQuestion = objPartieCourante.obtenirQuestionCourante();
        nomJoueur = ((JoueurHumain)objJoueur).obtenirNomUtilisateur();
                    
                    // If we're in debug mode, accept any answer
                    if(ControleurJeu.modeDebug)
                    {
                        bolReponseEstBonne = true;
                    }
                    else
                    {
                        bolReponseEstBonne = objQuestion.reponseEstValide(reponse);
                    }       
    }
    else
    {
      JoueurVirtuel objJoueurVirtuel = (JoueurVirtuel)objJoueur;
      
      //FIXME: change those value to be dymanic
      maxObjet = 4;//objPartieCourante.objTable.obtenirRegles().obtenirMaxObjet();
      maxObjetVente = 10;//objPartieCourante.objTable.obtenirRegles().obtenirMaxObjetsVente();
      
      
      // Obtenir les informations du joueur virtuel
      intPointageCourant = objJoueurVirtuel.obtenirPointage();
                        intArgentCourant   = objJoueurVirtuel.obtenirArgent();
        table = objJoueurVirtuel.obtenirTable();
        intDifficulteQuestion = objJoueurVirtuel.obtenirPointage(objJoueurVirtuel.obtenirPositionJoueur(), objPositionDesiree);
        objListeObjetsUtilisablesRamasses = objJoueurVirtuel.obtenirListeObjetsRamasses();
        positionJoueur = objJoueurVirtuel.obtenirPositionJoueur();
        gestionnaireEv = objJoueurVirtuel.obtenirGestionnaireEvenements();
        
        // Pas de question pour les joueurs virtuels
        objQuestion = null;
        nomJoueur = objJoueurVirtuel.obtenirNom();
        
        // On appelle jamais cette fonction si le joueur virtuel rate 
        // la question
        bolReponseEstBonne = true;

    }
    
    // Le nouveau pointage est initialement le pointage courant
    int intNouveauPointage = intPointageCourant;
                
                int intNouvelArgent = intArgentCourant;
    
    // D�claration d'une r�f�rence vers l'objet ramass�
    ObjetUtilisable objObjetRamasse = null;
    
    // D�claration d'une r�f�rence vers l'objet subi
    ObjetUtilisable objObjetSubi = null;
    
    String collision = "";
    
    // D�claration d'une r�f�rence vers le magasin recontr�
    Magasin objMagasinRencontre = null;
    
    // Si la r�ponse est bonne, alors on modifie le plateau de jeu
    if (bolReponseEstBonne == true)
    {
      // Faire la r�f�rence vers la case de destination
      Case objCaseDestination = table.obtenirPlateauJeuCourant()[objPositionDesiree.x][objPositionDesiree.y];
      
      // Calculer le nouveau pointage du joueur
                        switch(intDifficulteQuestion)
                        {
                            case 1:
                                intNouveauPointage += 1;
                                break;
                            case 2:
                                intNouveauPointage += 2;
                                break;
                            case 3:
                                intNouveauPointage += 3;
                                break;
                            case 4:
                                intNouveauPointage += 5;
                                break;
                            case 5:
                                intNouveauPointage += 8;
                                break;
                            case 6:
                                intNouveauPointage += 13;
                                break;
                        }
      
      // Si la case de destination est une case de couleur, alors on 
      // v�rifie l'objet qu'il y a dessus et si c'est un objet utilisable, 
      // alors on l'enl�ve et on le donne au joueur, sinon si c'est une 
      // pi�ce on l'enl�ve et on met � jour le pointage du joueur, sinon 
      // on ne fait rien
      if (objCaseDestination instanceof CaseCouleur)
      {
        // Faire la r�f�rence vers la case de couleur
        CaseCouleur objCaseCouleurDestination = (CaseCouleur) objCaseDestination;
        
        // S'il y a un objet sur la case, alors on va faire l'action 
        // tout d�pendant de l'objet (pi�ce, objet utilisable ou autre)
        if (objCaseCouleurDestination.obtenirObjetCase() != null)
        {
          // Si l'objet est un objet utilisable, alors on l'ajoute � 
          // la liste des objets utilisables du joueur
          if (objCaseCouleurDestination.obtenirObjetCase() instanceof ObjetUtilisable)
          {
            
                                                if(maxObjet > intNouvelArgent + objListeObjetsUtilisablesRamasses.size())
                                                {
                                                    // Faire la r�f�rence vers l'objet utilisable
                                                    ObjetUtilisable objObjetUtilisable = (ObjetUtilisable) objCaseCouleurDestination.obtenirObjetCase();

                                                    // Garder la r�f�rence vers l'objet utilisable pour l'ajouter � l'objet de retour
                                                    objObjetRamasse = objObjetUtilisable;

                                                    // Ajouter l'objet ramass� dans la liste des objets du joueur courant
                                                    objListeObjetsUtilisablesRamasses.put(new Integer(objObjetUtilisable.obtenirId()), objObjetUtilisable);

                                                    // Enlever l'objet de la case du plateau de jeu
                                                    objCaseCouleurDestination.definirObjetCase(null);

                                                    // On va dire aux clients qu'il y a eu collision avec cet objet
                                                    collision = objObjetUtilisable.obtenirTypeObjet();
                                                }
          }
          else if (objCaseCouleurDestination.obtenirObjetCase() instanceof Piece)
          {
                                                if(maxObjet > intNouvelArgent + objListeObjetsUtilisablesRamasses.size())
                                                {
                                                    // Faire la r�f�rence vers la pi�ce
                                                    Piece objPiece = (Piece) objCaseCouleurDestination.obtenirObjetCase();

                                                    // Mettre � jour l'argent du joueur
                                                    intNouvelArgent += objPiece.obtenirMonnaie();

                                                    // Enlever la pi�ce de la case du plateau de jeu
                                                    objCaseCouleurDestination.definirObjetCase(null);

                                                    collision = "piece";

                                                    // TODO: Il faut peut-�tre lancer un algo qui va placer 
                                                    //     les pi�ces sur le plateau de jeu s'il n'y en n'a
                                                    //     plus
                                                }
          }
          else if (objCaseCouleurDestination.obtenirObjetCase() instanceof Magasin)
          {
            // D�finir la collision
            collision = "magasin";
            
            // D�finir la r�f�rence vers le magasin rencontr�
            objMagasinRencontre = (Magasin) objCaseCouleurDestination.obtenirObjetCase();
          }
        }
        
        // S'il y a un objet � subir sur la case, alors on va faire une
        // certaine action (TODO: � compl�ter)
        if (objCaseCouleurDestination.obtenirObjetArme() != null)
        {
          // Faire la r�f�rence vers l'objet utilisable
          ObjetUtilisable objObjetUtilisable = (ObjetUtilisable) objCaseCouleurDestination.obtenirObjetArme();
          
          // Garder la r�f�rence vers l'objet utilisable � subir
          objObjetSubi = objObjetUtilisable;
          
          //TODO: Faire une certaine action au joueur
          
          // Enlever l'objet subi de la case
          objCaseCouleurDestination.definirObjetArme(null);
        }
      }
      
      // Cr�er l'objet de retour
      objRetour = new RetourVerifierReponseEtMettreAJourPlateauJeu(bolReponseEstBonne, intNouveauPointage, intNouvelArgent);
      objRetour.definirObjetRamasse(objObjetRamasse);
      objRetour.definirObjetSubi(objObjetSubi);
      objRetour.definirNouvellePosition(objPositionDesiree);
      objRetour.definirCollision( collision );
      objRetour.definirMagasin(objMagasinRencontre);
      
      synchronized (table.obtenirListeJoueurs())
        {
        // Pr�parer l'�v�nement de deplacement de personnage. 
        // Cette fonction va passer les joueurs et cr�er un 
        // InformationDestination pour chacun et ajouter l'�v�nement 
        // dans la file de gestion d'�v�nements
        table.preparerEvenementJoueurDeplacePersonnage(nomJoueur, collision, positionJoueur, objPositionDesiree, intNouveauPointage, intNouvelArgent, "");
                  
        }
        
      // Modifier la position, le pointage et l'argent
      if (objJoueur instanceof JoueurHumain)
      {
        ((JoueurHumain)objJoueur).obtenirPartieCourante().definirPositionJoueur(objPositionDesiree);
          ((JoueurHumain)objJoueur).obtenirPartieCourante().definirPointage(intNouveauPointage);
                            ((JoueurHumain)objJoueur).obtenirPartieCourante().definirArgent(intNouvelArgent);
      }
      else if (objJoueur instanceof JoueurVirtuel)
      {
        ((JoueurVirtuel)objJoueur).definirPositionJoueurVirtuel(objPositionDesiree);
        ((JoueurVirtuel)objJoueur).definirPointage(intNouveauPointage);
                                ((JoueurVirtuel)objJoueur).definirArgent(intNouvelArgent);
      }
    }
    else
    {
      // Cr�er l'objet de retour
      objRetour = new RetourVerifierReponseEtMettreAJourPlateauJeu(bolReponseEstBonne, intNouveauPointage, intNouvelArgent);
      
      // La question sera nulle pour les joueurs virtuels
      if (objQuestion != null)
      {
        objRetour.definirExplications(objQuestion.obtenirURLExplication());
      }
    }
    
    return objRetour;
    
  }
  
  /**
   * Cette fonction met � jour le plateau de jeu si le joueur a bien r�pondu
   * � la question. Les objets sur la nouvelle case sont enlev�s et le pointage
   * et l'argent du joueur sont mis � jour.
   * 
   * @param String reponse : La r�ponse du joueur
   * @param boolean doitGenererNoCommandeRetour : Permet de savoir si on doit 
   *            g�n�rer un num�ro de commande � retourner
   * @return RetourVerifierReponseEtMettreAJourPlateauJeu : Un objet contenant 
   *        toutes les valeurs � retourner au client
   */
  public RetourVerifierReponseEtMettreAJourPlateauJeu verifierReponseEtMettreAJourPlateauJeu(String reponse, boolean doitGenererNoCommandeRetour)
  {
    
    RetourVerifierReponseEtMettreAJourPlateauJeu objRetour =
        verifierReponseEtMettreAJourPlateauJeu(reponse, objPositionJoueurDesiree, objJoueurHumain);
    
    // Si on doit g�n�rer le num�ro de commande de retour, alors
    // on le g�n�re, sinon on ne fait rien (�a devrait toujours
    // �tre vrai, donc on le g�n�re tout le temps)
    if (doitGenererNoCommandeRetour == true)
    {
      // G�n�rer un nouveau num�ro de commande qui sera 
        // retourn� au client
        objJoueurHumain.obtenirProtocoleJoueur().genererNumeroReponse();              
    }
    
    objQuestionCourante = null;

    return objRetour;
  }
  
  /*
   * Retourne une r�f�rence vers la liste des objets ramass�s
   */
  public TreeMap obtenirListeObjets()
  {
    return lstObjetsUtilisablesRamasses;
  }
  
  public void ajouterObjetUtilisableListe(ObjetUtilisable objObjetUtilisable)
  {
    lstObjetsUtilisablesRamasses.put(new Integer(objObjetUtilisable.obtenirId()), objObjetUtilisable);
  }
  
  /* 
   * Aller chercher une r�f�rence vers un objet de la liste des objets selon
   * son id
   */
  public ObjetUtilisable obtenirObjetUtilisable(int intObjetId)
  {
       Set lstEnsembleObjets = lstObjetsUtilisablesRamasses.entrySet();
       Iterator objIterateurListeObjets = lstEnsembleObjets.iterator();
       while (objIterateurListeObjets.hasNext() == true)
       {
        Objet objObjet = (Objet)(((Map.Entry)(objIterateurListeObjets.next())).getValue());
        if (objObjet instanceof ObjetUtilisable)
        {
          if (((ObjetUtilisable)objObjet).obtenirId() == intObjetId)
          {
            return (ObjetUtilisable)objObjet;
          }
        }
       }
       return null;
  }
  
  /*
   * D�termine si le joueur poss�de un certain objet, permet
   * de valider l'information envoy� par le client lorsqu'il utiliser l'objet
   */
   public boolean joueurPossedeObjet(int id)
   {
       // Pr�paration pour parcourir la liste d'objets
       Set lstEnsembleObjets = lstObjetsUtilisablesRamasses.entrySet();
       Iterator objIterateurListeObjets = lstEnsembleObjets.iterator();
       
       // Parcours du TreeMap
       while (objIterateurListeObjets.hasNext() == true)
       {
        Objet objObjet = (Objet)(((Map.Entry)(objIterateurListeObjets.next())).getValue());
        if (objObjet instanceof ObjetUtilisable)
        {
          if (((ObjetUtilisable)objObjet).obtenirId() == id)
          {
            return true;
          }
        }
       }
       
       return false;
   }
   
   public GestionnaireEvenements obtenirGestionnaireEvenements()
   {
    return objGestionnaireEv;
   }
   
  public void enleverObjet(int intIdObjet, String strTypeObjet)
  {
    lstObjetsUtilisablesRamasses.remove(intIdObjet);
  }
  
  public Objet obtenirObjetCaseCourante()
  {
    // L'objet � retourn�
    Objet objObjet = null;
    
    // Aller chercher le plateau de jeu
    Case[][] objPlateauJeu = objTable.obtenirPlateauJeuCourant();
    
    // Aller chercher la case o� le joueur se trouve
    Case objCaseJoueur = objPlateauJeu[objPositionJoueur.x][objPositionJoueur.y];
    
    // Si c'est une case couleur, retourner l'objet, sinon on va retourner null
    if (objCaseJoueur instanceof CaseCouleur)
    {
      objObjet = ((CaseCouleur) objCaseJoueur).obtenirObjetCase();
    }
    
    return objObjet;
    
  }
  
        public Point obtenirPositionJoueurDesiree()
        {
            return objPositionJoueurDesiree;
        }
        
        /*
        public GestionnaireBD obtenirGestionnaireBD()
        {
            return objGestionnaireBD;
        }
        */
        
        public String obtenirVaSubirUneBanane()
        {
            return vaSubirUneBanane;
        }
        
        public void definirVaSubirUneBanane(String b)
        {
            vaSubirUneBanane = b;
        }
        
        public int obtenirDistanceAuWinTheGame()
        {
            return Math.abs(objPositionJoueur.x - objTable.obtenirPositionWinTheGame().x) + Math.abs(objPositionJoueur.y - objTable.obtenirPositionWinTheGame().y);
        }
}