package ServeurJeu.ComposantesJeu.Joueurs;

import ServeurJeu.Communications.ProtocoleJoueur;
import ServeurJeu.ComposantesJeu.InformationPartie;
import ServeurJeu.ComposantesJeu.Langue;
import ServeurJeu.ComposantesJeu.Salle;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class JoueurHumain extends Joueur
{
	// D�claration d'une r�f�rence vers le protocole du joueur
	private ProtocoleJoueur objProtocoleJoueur;
	
	// Cette variable va contenir le nom d'utilisateur du joueur
	private String strNomUtilisateur;
	
	// Cetta variable contient la cl� de la table joueur
	private int intCleJoueur;
	
	// Cette variable va contenir l'adresse IP du joueur
	private String strAdresseIP;
	
	// Cette variable va contenir le port du joueur
	private String strPort;
	
	// Cette variable va contenir le pr�nom du joueur
	private String strPrenom;
	
	// Cette variable va contenir le nom de famille du joueur
	private String strNomFamille;
	
	//Cette variable d�fini si un jouer peut creer une salle
	private boolean bolPeutCreerSalle;
	
	private String cleNiveau;
  
  private Langue langue;
	
	// D�claration d'une r�f�rence vers la salle dans laquelle le joueur se 
	// trouve (null si le joueur n'est dans aucune salle)
	private Salle objSalleCourante;
	
	// D�claration d'une r�f�rence vers l'objet gardant l'information sur la
	// partie courant de la table o� le joueur se trouve (null si le joueur 
	// n'est dans aucune table)
	private InformationPartie objPartieCourante;
        
	/**
	 * Constructeur de la classe JoueurHumain qui permet d'initialiser les 
	 * membres priv�s du joueur humain et de garder une r�f�rence vers l'objet
	 * permettant de faire la gestion du protocole du joueur
	 * 
	 * @param ProtocoleJoueur protocole : L'objet g�rant le protocole de 
	 * 									  communication du joueur
	 * @param String nomUtilisateur : Le nom d'utilisateur du joueur
	 * @param String adresseIP : L'adresse IP du joueur
	 * @param String port : Le port du joueur
	 * @param boolean peutCreerSalle : Permet de savoir si le joueur peut cr�er
	 * 								   de nouvelles salles
	 */
	public JoueurHumain(ProtocoleJoueur protocole, 
                      String nomUtilisateur, 
                      String adresseIP, 
                      String port) 
	{
		super();

		// Faire la r�f�rence vers le protocole du joueur
		objProtocoleJoueur = protocole;
		
		// Garder en m�moire le nom d'utilisateur, l'adresse IP et le port du
		// joueur
		strNomUtilisateur = nomUtilisateur;
		strAdresseIP = adresseIP;
		strPort = port;
		
		// Initialiser les caract�ristiques du joueur
		strPrenom = "";
		strNomFamille = "";
		bolPeutCreerSalle = false;
		
		// Au d�but, le joueur n'est dans aucune salle ni table
		objSalleCourante = null;
		objPartieCourante = null;
	}

	/**
	 * Cette fonction permet de retourner l'objet ProtocoleJoueur qui sert �
	 * ex�cuter le protocole de communication du jeu entre le joueur et le 
	 * serveur.
	 * 
	 * @return ProtocoleJoueur : L'objet ProtocoleJoueur li� au joueur humain
	 */
	public ProtocoleJoueur obtenirProtocoleJoueur()
	{
		return objProtocoleJoueur;
	}
	
	/**
	 * Cette fonction permet de retourner le nom d'utilisateur du joueur.
	 * 
	 * @return String : Le nom d'utilisateur du joueur
	 */
	public String obtenirNomUtilisateur()
	{
		return strNomUtilisateur;
	}
	
	/**
	 * Cette fonction permet de retourner l'adresse IP du joueur.
	 * 
	 * @return String : L'adresse IP du joueur
	 */
	public String obtenirAdresseIP()
	{
		return strAdresseIP;
	}
	
	/**
	 * Cette fonction permet de retourner le port de communication du joueur.
	 * 
	 * @return String : Le port du joueur qui est ouvert
	 */
	public String obtenirPort()
	{
		return strPort;
	}
	
	/**
	 * Cette fonction permet de retourner le pr�nom du joueur.
	 * 
	 * @return String : La pr�nom du joueur
	 */
	public String obtenirPrenom()
	{
		return strPrenom;
	}
	
	/**
	 * Cette fonction permet de retourner si un joueur peut creer une salle.
	 * 
	 * @return boolean : peut ou peut pas creer une salle
	 */
	public boolean obtenirPeutCreerSalle()
	{
		return bolPeutCreerSalle;
	}
	
	/**
	 * Cette m�thode permet de d�finir le pr�nom du joueur.
	 * 
	 * @param String prenom : Le pr�nom du joueur � d�finir
	 */
	public void definirPrenom(String prenom)
	{
		strPrenom = prenom;
	}
	
	/**
	 * Cette fonction permet de retourner le nom de famille du joueur.
	 * 
	 * @return String : Le nom de famille du joueur
	 */
	public String obtenirNomFamille()
	{
		return strNomFamille;
	}

	/**
	 * Cette m�thode permet de d�finir le nom de famille du joueur.
	 * 
	 * @param String prenom : Le nom de famille du joueur � d�finir
	 */
	public void definirNomFamille(String nomFamille)
	{
		strNomFamille = nomFamille;
	}
	
	/**
	 * Cette m�thode permet de d�finir si un joueur peut creer une salle.
	 * 
	 * @param boolean peutCreerSalle : peut ou peux pas creer salle
	 */
	public void definirPeutCreerSalles( boolean peutCreerSalle)
	{
		bolPeutCreerSalle = peutCreerSalle;
	}
	
	/**
	 * Cette fonction permet de retourner la r�f�rence vers la salle dans 
	 * laquelle se trouve le joueur pr�sentement.
	 * 
	 * @return Salle : La salle courante dans laquelle se trouve le joueur.
	 * 				   Si null est retourn�, alors le joueur ne se trouve dans
	 * 				   aucune salle.
	 */
	public Salle obtenirSalleCourante()
	{
		return objSalleCourante;
	}
		
	/**
	 * Cette m�thode permet de d�finir la r�f�rence vers la salle dans laquelle
	 * le joueur se trouve.
	 * 
	 * @param Salle salleCourante : La salle dans laquelle le joueur se
	 * 								trouve pr�sentement. Si la salle est null
	 * 								alors c'est que le joueur n'est dans aucune
	 * 								salle.
	 */
	public void definirSalleCourante(Salle salleCourante)
	{
		objSalleCourante = salleCourante;
	}
	
	/**
	 * Cette fonction permet de retourner la r�f�rence vers l'information sur
	 * la partie courante de la table dans laquelle se trouve le joueur pr�sentement.
	 * 
	 * @return InformationPartie : L'information sur la partie courante du joueur.
	 * 				   Si null est retourn�, alors le joueur ne se trouve dans
	 * 				   aucune table.
	 */
	public InformationPartie obtenirPartieCourante()
	{
		return objPartieCourante;
	}
		
	/**
	 * Cette m�thode permet de d�finir la r�f�rence vers l'information sur la 
	 * partie courante du joueur.
	 * 
	 * @param InformationPartie partieCourante : L'information sur la partie
	 * 					courante du joueur. Si la partie courante est null
	 * 					alors c'est que le joueur n'est dans aucune table
	 */
	public void definirPartieCourante(InformationPartie partieCourante)
	{
		objPartieCourante = partieCourante;
	}
	
	public int obtenirCleJoueur()
	{
		return intCleJoueur;
	}
	
	public void definirCleJoueur(int cle)
	{
		intCleJoueur = cle;
	}

	
	public void enleverObjet(int intIdObjet, String strTypeObjet)
	{
		objPartieCourante.enleverObjet(intIdObjet, strTypeObjet);
	}


	public String obtenirCleNiveau() 
	{
		return cleNiveau;
	}

	public void definirCleNiveau(String cleNiveau) 
	{
		this.cleNiveau = cleNiveau;
	}

  public Langue getLangue() {
    return langue;
  }

  public void setLangue(Langue langue) {
    this.langue = langue;
  }
  
  
}
