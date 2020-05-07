package ServeurJeu.ComposantesJeu.Joueurs;

import ServeurJeu.Communications.ProtocoleJoueur;
import ServeurJeu.ComposantesJeu.Salle;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class JoueurHumain extends Joueur
{
	// D�claration d'une r�f�rence vers le protocole du joueur
	private ProtocoleJoueur objProtocoleJoueur;
		
	// It's player id in the DB
	private int intCleJoueur;

	// La langue du joueur: "fr" ou "en"
    private String strLangue;    
	
	// Cette variable va contenir l'adresse IP du joueur
	private final String strAdresseIP;
	
	// Cette variable va contenir le port du joueur
	private final String strPort;
	
	// Cette variable va contenir le pr�nom du joueur
	private String strPrenom;
	
	// Cette variable va contenir le nom de famille du joueur
	private String strNomFamille;	
	
	/**
	 * D�claration d'un variable qui contient la valeur du niveau du joueur 
	 * valeurs possibles -> entre 1 et 19
	 */
	private int cleNiveau;
	
	// D�claration d'une r�f�rence vers la salle dans laquelle le joueur se 
	// trouve (null si le joueur n'est dans aucune salle)
	private  Salle objSalleCourante;
	
	// D�claration d'une r�f�rence vers l'objet gardant l'information sur la
	// partie courant de la table o� le joueur se trouve (null si le joueur 
	// n'est dans aucune table)
	private InformationPartieHumain objPartieCourante;
	        
	/**
	 * Constructeur de la classe JoueurHumain qui permet d'initialiser les 
	 * membres priv�s du joueur humain et de garder une r�f�rence vers l'objet
	 * permettant de faire la gestion du protocole du joueur
	 * 
	 * @param protocole L'objet g�rant le protocole de communication du joueur
	 * @param nomUtilisateur : Le nom d'utilisateur du joueur
	 * @param adresseIP : L'adresse IP du joueur
	 * @param port : Le port du joueur
	 */
	public JoueurHumain(ProtocoleJoueur protocole, String nomUtilisateur, String adresseIP, String port) 
	{
		super(nomUtilisateur);

		// Faire la r�f�rence vers le protocole du joueur
		objProtocoleJoueur = protocole;
		
		// Garder en m�moire le nom d'utilisateur, l'adresse IP et le port du
		// joueur		
		strAdresseIP = adresseIP;
		strPort = port;
		
		// Initialiser les caract�ristiques du joueur
		//strPrenom = "";
		//strNomFamille = "";
       				
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
	 * Cette m�thode permet de d�finir le pr�nom du joueur.
	 * 
	 * @param prenom Le pr�nom du joueur � d�finir
	 */
	public void definirPrenom(String prenom)
	{
		strPrenom = prenom;
	}
	
	/**
	 * Cette fonction permet de retourner le nom de famille du joueur.
	 * 
	 * @return Le nom de famille du joueur
	 */
	public String obtenirNomFamille()
	{
		return strNomFamille;
	}

	/**
	 * Cette m�thode permet de d�finir le nom de famille du joueur.
	 * 
	 * @param nomFamille Le nom de famille du joueur � d�finir
	 */
	public void definirNomFamille(String nomFamille)
	{
		strNomFamille = nomFamille;
	}
	
		
	/**
	 * Cette fonction permet de retourner la r�f�rence vers la salle dans 
	 * laquelle se trouve le joueur pr�sentement.
	 * 
	 * @return La salle courante dans laquelle se trouve le joueur.
         *         Si null est retourn�, alors le joueur ne se trouve dans
         *         aucune salle.
	 */
	public Salle obtenirSalleCourante()
	{
		return objSalleCourante;
	}
		
	/**
	 * Cette m�thode permet de d�finir la r�f�rence vers la salle dans laquelle
	 * le joueur se trouve.
	 * 
	 * @param salleCourante La salle dans laquelle le joueur se trouve
         *        pr�sentement. Si la salle est null alors c'est que le joueur
         *        n'est dans aucune salle.
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
	public InformationPartieHumain obtenirPartieCourante()
	{
		return objPartieCourante;
	}
	
	public int obtenirCleJoueur()
	{
		return intCleJoueur;
	}
	
	public void definirCleJoueur(int cle)
	{
		this.intCleJoueur = cle;
	}

	public void enleverObjet(int intIdObjet, String strTypeObjet)
	{
		objPartieCourante.enleverObjet(intIdObjet, strTypeObjet);
	}

    
	public int obtenirCleNiveau() 
	{
		return cleNiveau;
	}

	public void definirCleNiveau(int cleNiveau) 
	{
		this.cleNiveau = cleNiveau;
	} 
	
	
	/**
	 * Cette m�thode permet de d�finir la r�f�rence vers l'information sur la 
	 * partie courante du joueur.
	 * 
	 * @param partieCourante L'information sur la partie courante du joueur.
         *        Si la partie courante est null alors c'est que le joueur n'est
         *        dans aucune table.
	 */
	public void definirPartieCourante(InformationPartieHumain partieCourante)
	{
		objPartieCourante = partieCourante;
	}

	
	public void setObjProtocoleJoueur(ProtocoleJoueur protocoleJoueur) {
		objProtocoleJoueur = protocoleJoueur;
		
	}
	
	public InformationPartie getPlayerGameInfo()
	{
		return objPartieCourante;
	}
	
	public String obtenirLangue()
	{
		return strLangue;
	}

	public void definirLangue(String lang)
	{
		strLangue = lang;
		if (!lang.equals("fr") && !lang.equals("en"))
			strLangue = "fr";
	}

		
}
