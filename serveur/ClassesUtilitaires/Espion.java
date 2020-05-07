package ClassesUtilitaires;

import org.apache.log4j.Logger;
import java.io.FileWriter;
import java.util.Date;
import java.text.DateFormat;
import ServeurJeu.ControleurJeu;
import ServeurJeu.Communications.ProtocoleJoueur;
import ServeurJeu.ComposantesJeu.Salle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurVirtuel;
import ServeurJeu.ComposantesJeu.Tables.Table;
import ServeurJeu.Configuration.GestionnaireMessages;

/**
 * L'espion qui �crit dans un fichier p�riodiquement les informations du serveur
 * @author Jean-Fran�ois Fournier
 */
public class Espion implements Runnable{
	
	// D�claration d'une constante contenant la version du serveur
	private String strVersion;
	
	// D�claration d'une variable contenant le nombre de millisecondes entre
	// chaque mise � jour
	private int intDelaiMaj;
	
	// D�claration de la variable contenant le nom du fichier espion
	private String strNomFichier;
	
	// D�claration de l'objet logger qui permettra d'afficher des messages
	// d'erreurs dans le fichier log si n�cessaire
	static private Logger objLogger = Logger.getLogger( Espion.class );
	
	// D�claration d'une constante pour le mode "Rien" qui met l'espion au repos
	public static final int MODE_RIEN = 0;
	
	// D�claration d'une constante pour le mode "Fichier texte" qui va mettre
	// le r�sultat de la mise � jour dans un fichier texte
	public static final int MODE_FICHIER_TEXTE = 1;

    // Cette variable contiendra le mode dans lequel l'espion est
    private int intModeEspion;
	
	// D�claration d'un objet pour faire r�f�rence au Contr�leur de jeu
	private final ControleurJeu objControleurJeu;
	
	// D�claration d'une variable qui contiendra le s�parateur de ligne
	private String strFinLigne;
	
	// D�claration d'une variable qui nous permettra d'arr�ter l'espion
	private boolean bolArreterEspion;
	
	/* Contructeur de la classe
	 * @param String nomFichier: Le fichier dans lequel l'espion �crira p�riodiquement
	 * 
	 * @param int delaiMaj: Le d�lai en millisecondes entre chaque maj
	 */
	public Espion(ControleurJeu controleur, String nomFichier, int delaiMaj, int mode)
	{
		String strArr[] = System.getProperty("java.class.path").split(":");
		strVersion = "Serveur: " + strArr[0];
		    
		intModeEspion = mode;
		objControleurJeu = controleur;
		strFinLigne = System.getProperty("line.separator");
		strNomFichier = nomFichier;
		intDelaiMaj = delaiMaj;
		//bolArreterEspion = false;
	}


    /* Changer le mode de l'espion, on peut le mettre en mode RIEN, mais si
     * on veut l'arr�ter pour de bon, on doit utiliser arreterEspion()
     */
    public void changerMode(int nouveauMode)
    {
    	intModeEspion = nouveauMode;
    }
    
    /*
     * changerDelaiMaj
     */
    public void changerDelaiMaj(int nouveauDelai)
    {
        intDelaiMaj = nouveauDelai;	
    }
    
    
    /* Arr�ter l'espion pour de bon
     */
    public void arreterEspion()
    {
    	bolArreterEspion = true;
    }
    
    /*
     * Effectuer une mise � jour imm�diate, on peut se servir de cette fonction
     * pour faire des mise � jour � certains moments pr�cis.
     */
    public void faireMajImmediate()
    {
    	faireMaj();
    }
    
    /* Thread run, met � jour les informations du serveur p�riodiquement
     */
    public void run()
    {   
    
    	while (bolArreterEspion == false)
    	{
    	
	    	try
	    	{
	
                // Effectuer une mise � jour des informations
                faireMaj();

                // Bloquer la thread jusqu'� la prochaine mise � jour
               	Thread.sleep(intDelaiMaj);
	               	
	    	}
			catch( Exception e )
			{
				//System.out.println("Erreur dans la thread de l'espion.");
				//System.out.println(e.getMessage());
				objLogger.info(GestionnaireMessages.message("espion.erreur_thread"), e);				
			}
		}
		
	    objLogger.info(GestionnaireMessages.message("espion.arrete"));
	}
    
    
    private void faireMaj()
    {
        switch(intModeEspion)
        {
        
        case MODE_RIEN: 
        
            // Ne rien faire
            break;
                
                        
        case MODE_FICHIER_TEXTE:   
        
            // �crire dans le fichier texte
            traiterFichierTexte();
            break;

            
        }	
    }
    
    /* traiterFichierTexte
     * 
     * Cette fonction �crit dans le fichier strNomFichier les informations
     * du serveur.
     */
    private void traiterFichierTexte()
    {
    	
	    // D�claration de la variable qui contiendra le r�sultat
	    // de l'espion pour cette mise � jour
	    StringBuilder strResultat = new StringBuilder();
	    
	    // D�claration des diff�rentes parties du r�sultat
	    StringBuilder strEntete = new StringBuilder();
	    StringBuilder strDerniereMAJ = new StringBuilder();
	    StringBuilder strJoueursConnectes = new StringBuilder();
	    StringBuilder strJoueursDeconnectes = new StringBuilder();
	    StringBuilder strSalles = new StringBuilder();
	    StringBuilder strTables = new StringBuilder();
	    
	    // D�claration d'un objet qui contiendra une r�f�rence vers la liste des joueurs
	    LinkedList<ProtocoleJoueur> lstProtocoleJoueur = objControleurJeu.obtenirGestionnaireCommunication().obtenirListeProtocoleJoueur();
	    
		// D�claration d'une liste de ProtocoleJoueur qui va contenir une copie
		Vector<ProtocoleJoueur> lstCopieProtocoleJoueur = null;
		
		// D�claration d'un objet qui contiendra une r�f�rence vers la liste des joueurs d�connect�s
		HashMap<String, JoueurHumain> lstJoueursDeconnectes = objControleurJeu.obtenirListeJoueursDeconnectes();
		
		
		// D�claration d'un objet qui contiendra une r�f�rence vers la liste des salles
		HashMap<Integer, Salle> lstSalles = objControleurJeu.obtenirListeSalles("");
		
	    // D�claration d'un objet qui contiendra une r�f�rence vers la liste des tables
	    // pour une certaine salle
	    HashMap<Integer, Table> lstTables;
		
		// D�claration d'un objet qui contiendra une r�f�rence vers la liste des joueurs
		// pour une table
		ConcurrentHashMap<String, JoueurHumain> lstJoueurs;

        // D�claration d'un objet qui contiendra une r�f�rence vers la liste des 
        // jouers connect�s au serveur
        HashMap<String, JoueurHumain> lstJoueursConnectes = objControleurJeu.obtenirListeJoueurs();		
		
		// Permet de calculer le nombre de connexion refus�
		int intConnexionRefusee = 0;
		
		
		// Emp�cher d'autres threads de toucher � la liste des protocoles 
		// de joueur
		synchronized (lstProtocoleJoueur)
		{
			// Faire une copie de la liste des ProtocoleJoueur
			lstCopieProtocoleJoueur = (Vector<ProtocoleJoueur>) lstProtocoleJoueur.clone();
		}
		

	    // Ent�te 
	    // Ajouter la version du serveur
	    strEntete.append(strVersion);
		strEntete.append(strFinLigne);
	    
	    
	    // Derni�re mise � jour
	    // Ajouter la date et l'heure de la derni�re mise � jour
        Date objToday = new Date();
        String strDate;
        strDate = DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.MEDIUM).format(objToday);
        strDerniereMAJ.append("Derni�re mise � jour: ");
        strDerniereMAJ.append(strDate);


	    // Joueurs connect�s au gestionnaire de communication
	    // Ajouter le nombre de joueurs connect�s
	    strJoueursConnectes.append("Joueurs connect�s (protocole): ");
	    strJoueursConnectes.append(lstCopieProtocoleJoueur.size());
		strJoueursConnectes.append(strFinLigne);
		
        // S'il y a des joueurs connect�s, on va ajouter leurs noms      
        if (lstCopieProtocoleJoueur.size() > 0)
        {
			// Passer tous les objets ProtocoleJoueur et ajouter les noms d'utilisateur
			// des joueurs
			for (int i = 0; i < lstCopieProtocoleJoueur.size(); i++)
			{

                ProtocoleJoueur objProtocoleJoueur = (ProtocoleJoueur) lstCopieProtocoleJoueur.get(i);
                
                if (objProtocoleJoueur != null)
                {
                	JoueurHumain objJoueurHumain = objProtocoleJoueur.obtenirJoueurHumain();
                	
                	// Le joueur humain peut �tre nul si la connexion est refus�e 
                	if (objJoueurHumain != null)
                	{
						if (i == 0)
						{
        	                strJoueursConnectes.append("    ");	
						}
						
						if (i > 0 )
						{
							strJoueursConnectes.append(", ");
						}

						
						// Ajouter le nom du joueur humain
						strJoueursConnectes.append(objJoueurHumain.obtenirNom());
					}
					else
					{
						intConnexionRefusee++;
					}
				}

			}
			
	        strJoueursConnectes.append(strFinLigne);
        	
        }

        // Joueurs connect�s au serveur
	    // Pr�paration pour parcourir le TreeMap des joueurs connect�s
	    Set<Map.Entry<String,JoueurHumain>> lstEnsembleJoueursConnectes = lstJoueursConnectes.entrySet();
	    Iterator<Entry<String, JoueurHumain>> objIterateurListeJoueursConnectes = lstEnsembleJoueursConnectes.iterator();
	    
	    // Ajouter le nombre de joueurs connect�s
	    strJoueursConnectes.append(strFinLigne);
	    strJoueursConnectes.append("Joueurs connect�s (serveur): ");
	    strJoueursConnectes.append(lstEnsembleJoueursConnectes.size());
        
        // Afficher la liste des joueurs connect�s
        if (lstEnsembleJoueursConnectes.size() > 0)
        {
        	strJoueursConnectes.append(strFinLigne);
        	strJoueursConnectes.append("    ");
        	
			// Passer tous les joueurs connect�s et ajouter leur nom
			int intCompteur = 0;
			while (objIterateurListeJoueursConnectes.hasNext() == true)
			{
				if (intCompteur > 0)
				{
					strJoueursConnectes.append(", ");
				}
				
				strJoueursConnectes.append(((JoueurHumain)((Map.Entry<String,JoueurHumain>)objIterateurListeJoueursConnectes.next()).getValue()).obtenirNom());
			    intCompteur++;
			}
        }
        
        // S'il y a des connexions refus�es, on les ajouter ici
        if (intConnexionRefusee > 0)
        {
        	strJoueursConnectes.append(strFinLigne);
        	strJoueursConnectes.append(strFinLigne);
        	strJoueursConnectes.append("Connexion refus�e: ");
        	strJoueursConnectes.append(intConnexionRefusee);
        }
        
        
        // Joueurs d�connect�s
        // Pr�paration pour parcourir le TreeMap des joueurs d�connect�s
        Set<Map.Entry<String,JoueurHumain>> lstEnsembleJoueursDeconnectes = lstJoueursDeconnectes.entrySet();
        Iterator<Entry<String, JoueurHumain>> objIterateurListeJoueursDeconnectes = lstEnsembleJoueursDeconnectes.iterator();
        
        // Afficher le nombre de joueurs d�connect�s
        strJoueursDeconnectes.append("Joueurs d�connect�s: ");
        strJoueursDeconnectes.append(lstEnsembleJoueursDeconnectes.size());

        // Afficher la liste des joueurs d�connect�s
        if (lstEnsembleJoueursDeconnectes.size() > 0)
        {
        	strJoueursDeconnectes.append(strFinLigne);
        	strJoueursDeconnectes.append("    ");
        	
			// Passer tous les joueurs d�connect�s et ajouter leur nom
			int intCompteur = 0;
			while (objIterateurListeJoueursDeconnectes.hasNext() == true)
			{
				if (intCompteur > 0)
				{
					strJoueursDeconnectes.append(", ");
				}
				
				strJoueursDeconnectes.append(((JoueurHumain)((Map.Entry<String,JoueurHumain>)objIterateurListeJoueursDeconnectes.next()).getValue()).obtenirNom());
			    intCompteur++;
			}
        }

        
	    // Salles
        // Pr�paration pour parcourir le TreeMap des salles
        Set<Map.Entry<Integer,Salle>> lstEnsembleSalles = lstSalles.entrySet();
        Iterator<Entry<Integer, Salle>> objIterateurListeSalles = lstEnsembleSalles.iterator();	  
          
	    // Afficher le nombre de salles 
	    strSalles.append("Salles : ");
	    strSalles.append(lstEnsembleSalles.size());


        // Afficher la liste des salles     
	    if (lstEnsembleSalles.size() > 0)
	    {
        	strSalles.append(strFinLigne);
        	strSalles.append("    ");
        	
			// Passer toutes les salles et ajouter leur nom
			int intCompteur = 0;
			while (objIterateurListeSalles.hasNext() == true)
			{
				if (intCompteur > 0 )
				{
					strSalles.append(", ");
				}
				
				intCompteur++;
								
				// Ajouter le nom de la salle
				Salle objSalle = (Salle)(((Map.Entry<Integer,Salle>)(objIterateurListeSalles.next())).getValue());
				strSalles.append(objSalle.getRoomName(""));
			}	
	    }
	    
        // Pr�paration pour parcourir le TreeMap des salles
        lstEnsembleSalles = lstSalles.entrySet();
        objIterateurListeSalles = lstEnsembleSalles.iterator();	  
        
        
	    // Tables
	    // On va parcourir chaque salle, et pour chaque salle, on va afficher la
	    // liste des tables avec leur d�tails
	    if (lstEnsembleSalles.size() > 0)
	    {
        	
        	// Boucle du parcours de la liste des salles 
			while (objIterateurListeSalles.hasNext() == true)
			{
				// Aller chercher l'objet Salle
                Salle objSalle = (Salle)(((Map.Entry<Integer,Salle>)(objIterateurListeSalles.next())).getValue());				
				
				// Aller chercher la liste des tables
				lstTables = (HashMap<Integer,Table>)(objSalle.obtenirListeTables());
				
				// Pr�paration pour parcourir le TreeMap des tables
				Set<Map.Entry<Integer,Table>> lstEnsembleTables = lstTables.entrySet();
				Iterator<Entry<Integer, Table>> objIterateurListeTables = lstEnsembleTables.iterator();

                // Ajouter le nom de la salle et le nombre de tables
         	    strTables.append("Tables pour la salle ");
         	    strTables.append(objSalle.getRoomName(""));
         	    strTables.append(" : ");
         	    strTables.append(lstTables.size());
         	    
         	    // Pour chaque table, ajouter les informations pour celle-ci
         	    if (lstEnsembleTables.size() > 0 )
         	    {
         	    	strTables.append(strFinLigne);
	         	    
	         	    // Boucle de parcours de la liste de tables pour la salle courante
	         	    while(objIterateurListeTables.hasNext() == true)
	         	    {
	         	    	 // Aller chercher l'objet Table
	         	    	 Table objTable = (Table)(((Map.Entry<Integer, Table>)(objIterateurListeTables.next())).getValue());	
	         	   	     
	         	   	     // Ajouter les informations sur cette table
	         	   	     strTables.append("    Num�ro de table : ");
	         	   	     strTables.append(objTable.obtenirNoTable());
	         	   	     strTables.append(strFinLigne);
	         	   	     strTables.append("    Temps : ");
	         	   	     strTables.append(objTable.obtenirTempsTotal());
	         	   	     if (objTable.obtenirTempsTotal() <= 1)
	         	   	     {
	         	   	     	strTables.append(" minute");
	         	   	     }
	         	   	     else
	         	   	     {
	         	   	     	strTables.append(" minutes");
	         	   	     }
	         	   	     
	         	   	     strTables.append(strFinLigne);
	         	   	     strTables.append("    �tat : ");
	         	   	         
	         	   	     // Traiter l'�tat de la table
	         	   	     if (objTable.estCommencee() == false)
	         	   	     {
	         	   	     	strTables.append("En attente de joueurs");
	         	   	     }
	         	   	     else
	         	   	     {
	         	   	     	if (objTable.estArretee() == false)
	         	   	     	{
	         	   	     		strTables.append("Partie en cours");
	         	   	     	}
	         	   	     	else
	         	   	     	{
	         	   	     		strTables.append("Partie termin�e");
	         	   	     	}
	         	   	     }
	         	   	     
	         	   	     strTables.append(strFinLigne);
	         	   	     
	         	   	     // Si une partie est en cours, ajouter le temps restant
	         	   	     if (objTable.estCommencee() == true && objTable.estArretee() == false)
	         	   	     {
	         	   	     	strTables.append("    Temps Restant : ");
	         	   	     	strTables.append(objTable.obtenirTempsRestant());
	         	   	     	if (objTable.obtenirTempsRestant() <= 1)
	         	   	     	{
	         	   	     		strTables.append(" seconde");
	         	   	     	}
	         	   	     	else
	         	   	     	{
	         	   	     		strTables.append(" secondes");
	         	   	     	}
	         	   	     		
	         	   	     	strTables.append(strFinLigne);
	         	   	     }
	         	   	     
	         	   	     // Obtenir la liste joueurs
	         	   	     lstJoueurs = objTable.obtenirListeJoueurs();
	         	   	     
	         	   	     // Pr�paration pour parcourir la liste des joueurs
				         Set<Map.Entry<String,JoueurHumain>> lstEnsembleJoueurs = lstJoueurs.entrySet();
				         Iterator<Entry<String, JoueurHumain>> objIterateurListeJoueurs = lstEnsembleJoueurs.iterator();
	         	   	     
	         	   	     // Ajouter le nom de chaque joueur sur la table
	         	   	     strTables.append("    Joueurs : ");
	         	   	     int intCompteur = 0;
	         	   	     while(objIterateurListeJoueurs.hasNext() == true)
	         	   	     {
	         	   	     	if (intCompteur > 0)
	         	   	     	{
	         	   	     		strTables.append(", ");
	         	   	     	}
	         	   	     	intCompteur++;
	         	   	     	
	         	   	     	// Aller chercher l'objet JoueurHumain
	         	   	     	JoueurHumain objJoueurHumain = (JoueurHumain)(((Map.Entry<String,JoueurHumain>)(objIterateurListeJoueurs.next())).getValue());
	         	   	     	
	         	   	     	// Ajouter le nom d'utilisateur du joueur
	         	   	     	strTables.append(objJoueurHumain.obtenirNom());

                         }
                         
                         // Obtenir la liste des joueurs virtuels
                         if (objTable.obtenirListeJoueursVirtuels() != null)
                         {
                             ArrayList<JoueurVirtuel> lstJoueursVirtuels = objTable.obtenirListeJoueursVirtuels();
                             if (intCompteur > 0)
                             {
                             	strTables.append(", ");
                             }
                             
                             for (int i = 0; i < lstJoueursVirtuels.size(); i++)
                             {
                                 
                             	 if (i > 0)
                             	 {
                             	     strTables.append(", ");
                             	 }
                             	 
                                 JoueurVirtuel objJoueurVirtuel = (JoueurVirtuel) lstJoueursVirtuels.get(i);
                                 strTables.append(objJoueurVirtuel.obtenirNom());
                             }   
                         }
                         
                         // Fin de la liste des joueurs                         
                         strTables.append(strFinLigne);

                         // Obtenir la liste des joueurs d�connect�s
                         if (objTable.obtenirListeJoueursDeconnectes() != null && 
                             objTable.obtenirListeJoueursDeconnectes().size() > 0)
                         {
                             strTables.append("    Joueurs d�connect�s : ");
                             
	                         for (String name : objTable.obtenirListeJoueursDeconnectes().keySet())
	                         {            	
	                           	strTables.append(name + ", ");
	                         }
	                         
	                         strTables.deleteCharAt(strTables.lastIndexOf(","));	                                  
                             strTables.append(strFinLigne);
                         }

                         
                         // Ligne vide entre chaque block de table
                         strTables.append(strFinLigne);
	         	   	     
	         	    }
         	    }
         	       

			}	
	    }
	    
    	// Concatener chaque partie pour former le r�sultat final
    	strResultat.append(strEntete);
    	strResultat.append(strFinLigne);
    	strResultat.append(strDerniereMAJ);
    	strResultat.append(strFinLigne);
    	strResultat.append(strFinLigne);
    	strResultat.append(strJoueursConnectes);
    	strResultat.append(strFinLigne);
    	strResultat.append(strFinLigne);
        strResultat.append(strJoueursDeconnectes);
    	strResultat.append(strFinLigne);
    	strResultat.append(strFinLigne);
    	strResultat.append(strSalles);
    	strResultat.append(strFinLigne);
    	strResultat.append(strFinLigne);
    	strResultat.append(strTables);
    	strResultat.append(strFinLigne);
    	    
    	try
    	{
    		// �crire dans le fichier
			FileWriter writer = new FileWriter(strNomFichier);
			writer.write(strResultat.toString());
			//writer.write(strFinLigne + "(" + writer.getEncoding() + ")");
			writer.close();

	    }
	    catch( Exception e)
	    {
			//System.out.println("Erreur d'�criture dans le fichier espion.");
		    objLogger.info(GestionnaireMessages.message("espion.erreur_fichier"), e);
	    }
	
    }
}
