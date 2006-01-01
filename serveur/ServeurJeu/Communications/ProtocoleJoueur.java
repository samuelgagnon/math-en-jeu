package ServeurJeu.Communications;

import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.awt.Point;
import Enumerations.Filtre;
import ClassesUtilitaires.UtilitaireXML;
import ClassesUtilitaires.UtilitaireEncodeurDecodeur;
import ClassesUtilitaires.UtilitaireNombres;
import Enumerations.Commande;
import Enumerations.RetourFonctions.ResultatAuthentification;
import Enumerations.RetourFonctions.ResultatEntreeTable;
import Enumerations.RetourFonctions.ResultatDemarrerPartie;
import ServeurJeu.ControleurJeu;
import ServeurJeu.ComposantesJeu.Salle;
import ServeurJeu.ComposantesJeu.Table;
import ServeurJeu.ComposantesJeu.Question;
import ServeurJeu.ComposantesJeu.Joueurs.JoueurHumain;
import ClassesRetourFonctions.RetourVerifierReponseEtMettreAJourPlateauJeu;

/**
 * @author Jean-Fran�ois Brind'Amour
 */
public class ProtocoleJoueur implements Runnable
{
	// D�claration d'une r�f�rence vers le contr�leur de jeu
	private ControleurJeu objControleurJeu;
	
	// D�claration d'une r�f�rence vers le gestionnaire des communications
	private GestionnaireCommunication objGestionnaireCommunication;
	
	// D�claration d'une r�f�rence vers le v�rificateur des connexions
	private VerificateurConnexions objVerificateurConnexions;
	
	// Cet objet permet de garder une r�f�rence vers le canal de communication 
	// entre le serveur et le client (joueur) courant
	private Socket objSocketJoueur;
	
	// D�claration d'un canal de r�ception	
	private InputStream objCanalReception;
	
	// Cette variable permet de savoir s'il faut arr�ter le thread ou non
	private boolean bolStopThread;

	// D�claration d'une r�f�rence vers un joueur humain correspondant � ce
	// protocole
	private JoueurHumain objJoueurHumain;
	
	// D�claration d'une variable qui va servir de compteur pour envoyer des
	// commandes ou �v�nements au joueur de ce ProtocoleJoueur (sa valeur 
	// maximale est 100, apr�s 100 on recommence � 0)
	private int intCompteurCommande;
	
	// D�claration d'une contante gardant le maximum possible pour le 
	// compteur de commandes du serveur de jeu
	private final int MAX_COMPTEUR = 100;
	
	// D�claration d'une variable qui va contenir le num�ro de commande � 
	// retourner au client ayant fait une requ�te au serveur
	private int intNumeroCommandeReponse;
	
	/**
	 * Constructeur de la classe ProtocoleJoueur qui permet de garder une 
	 * r�f�rence vers le contr�leur de jeu, vers le gestionnaire des 
	 * communications et vers le socket du joueur demandant la connexion 
	 * et de s'assurer qu'il n'y a pas de d�lai.
	 * 
	 * @param ControleurJeu controleur : Le contr�leur du jeu
	 * @param GestionnaireCommunication communication : Le gestionnaire des 
	 * 							communications entre les clients et le serveur
	 * @param VerificateurConnexions verificateur : Le v�rificateur des connexions
	 * @param Socket socketJoueur : Le canal de communication associ� au joueur
	 */
	public ProtocoleJoueur(ControleurJeu controleur, GestionnaireCommunication communication, 
						   VerificateurConnexions verificateur, Socket socketJoueur) 
	{
		super();
		
		// Initialiser les valeurs du ProtocoleJoueur courant
		objControleurJeu = controleur;
		objGestionnaireCommunication = communication;
		objVerificateurConnexions = verificateur;
		objSocketJoueur = socketJoueur;
		objJoueurHumain = null;
		bolStopThread = false;
		intCompteurCommande = 0;
		intNumeroCommandeReponse = -1;
		
		System.out.println("Le client " + socketJoueur.getInetAddress().toString() + " est connecte");
		
		try
		{
			// �tant donn� que ce sont seulement de petits messages qui sont 
			// envoy�s entre le client et le serveur, alors il n'est pas 
			// n�cessaire d'attendre un d�lai suppl�mentaire
			objSocketJoueur.setTcpNoDelay(true);
		}
		catch (SocketException se)
		{
			System.out.println("Le canal de communication entre le serveur et le client est ferme");
			
			// Arr�ter le thread
			bolStopThread = true;
		}
	}
	
	/**
	 * Cette m�thode est appel�e automatiquement par le thread du joueur et elle
	 * permet d'ex�cuter le protocole du joueur courant.
	 * 
	 * @synchronism Cette m�thode n'a pas besoin d'�tre synchronis�e
	 */
	public void run()
	{
		try
		{
			// Cr�er le canal qui permet de recevoir des donn�es sur le canal
			// de communication entre le client et le serveur
			objCanalReception = objSocketJoueur.getInputStream();
			
			// Cette objet va contenir le message envoy� par le client au serveur
			StringBuffer strMessageRecu = new StringBuffer();
			
			// Cr�ation d'un tableau de 1024 bytes qui va servir � lire sur le canal
			byte[] byttBuffer = new byte[1024];
			
			// Boucler et obtenir les messages du client (joueur), puis les 
			// traiter tant que le client n'a pas d�cid� de quitter (ou que la
			// connexion ne s'est pas d�connect�e)
			while (bolStopThread == false)
			{
				// D�claration d'une variable qui va servir de marqueur 
				// pour savoir o� on en est rendu dans la lecture
				int intMarqueur = 0;
				
				// D�claration d'une variable qui va contenir le nombre de 
				// bytes r�ellement lus dans le canal
				int intBytesLus = objCanalReception.read(byttBuffer);
				
				// Si le nombre de bytes lus est -1, alors c'est que le 
				// stream a �t� ferm�, il faut donc terminer le thread
				if (intBytesLus == -1)
				{
					bolStopThread = true;
				}
				
				// Passer tous les bytes lus dans le canal de r�ception et 
				// d�couper le message en cha�ne de commandes selon le byte 
				// 0 marquant la fin d'une commande
				for (int i = 0; i < intBytesLus; i++)
				{
					// Si le byte courant est le byte de fin de message (EOM)
					// alors c'est qu'une commande vient de finir, on va donc
					// traiter la commande re�ue
					if (byttBuffer[i] == (byte) 0)
					{
						// Cr�er une cha�ne temporaire qui va garder la cha�ne 
						// de caract�res lue jusqu'� maintenant
						String strChaineAccumulee = new String(byttBuffer, 
												intMarqueur, i - intMarqueur);
						
						// Ajouter la cha�ne courante � la cha�ne de commande
						strMessageRecu.append(strChaineAccumulee);
						
						// On appelle une fonction qui va traiter le message re�u du 
						// client et mettre le r�sultat � retourner dans une variable
						String strMessageAEnvoyer = traiterCommandeJoueur(strMessageRecu.toString());
						
						// On remet la variable contenant le num�ro de commande
						// � retourner � -1, pour dire qu'il n'est pas initialis�
						intNumeroCommandeReponse = -1;
						
						// On renvoit une r�ponse au client seulement si le
						// message n'est pas � null
						if (strMessageAEnvoyer != null)
						{
							// On appelle la m�thode qui permet de renvoyer un 
							// message au client
							envoyerMessage(strMessageAEnvoyer);
						}
													
						// Vider la cha�ne contenant les commandes � traiter
						strMessageRecu.setLength(0);
						
						// Mettre le marqueur � l'endroit courant pour 
						// pouvoir ensuite recommancer une nouvelle cha�ne 
						// de commande � partir d'ici
						intMarqueur = i + 1;
					}
				}
				
				// Si le marqueur est toujours plus petit que le nombre de
				// caract�res lus, alors c'est qu'on n'a pas encore re�u
				// le marqueur de fin de message EOM (byte 0)
				if (intMarqueur < intBytesLus)
				{
					// On garde la partie du message non termin� dans la 
					// cha�ne qui va contenir le message � traiter lorsqu'on
					// recevra le EOM
					strMessageRecu.append(new String(byttBuffer, intMarqueur, intBytesLus - intMarqueur));
				}
			}
		}
		catch (IOException ioe)
		{
			System.out.println("Une erreur est survenue lors de la reception du message du client");
		}
		catch (TransformerConfigurationException tce)
		{
			System.out.println("Une erreur est survenue lors de la transformation du document XML en chaine de caracteres");
		}
		catch (TransformerException te)
		{
			System.out.println("Une erreur est survenue lors de la conversion du document XML en chaine de caracteres");
		}
		finally
		{
			try
			{
				// On tente de fermer le canal de r�ception
				objCanalReception.close();
			}
			catch (IOException ioe) {}
						
			try
			{
				// On tente de fermer le socket liant le client au serveur
				objSocketJoueur.close();						
			}
			catch (IOException ioe) {}
			
			// Si le joueur humain a �t� d�fini dans le protocole, alors
			// c'est qu'il a r�ussi � se connecter au serveur de jeu, il
			// faut donc aviser le contr�leur de jeu pour qu'il enl�ve
			// le joueur du serveur de jeu
			if (objJoueurHumain != null)
			{
				// Informer le contr�leur de jeu que la connexion avec le 
				// client (joueur) a �t� ferm�e (on ne doit pas obtenir de
			    // num�ro de commande de cette fonction, car on ne retournera
			    // rien du tout)
				objControleurJeu.deconnecterJoueur(objJoueurHumain, false);					
			}
			
			// Enlever le protocole du joueur courant de la liste des 
			// protocoles de joueurs
			objGestionnaireCommunication.supprimerProtocoleJoueur(this);
		}
		
		System.out.println("Le thread du client " + objSocketJoueur.getInetAddress().toString() + " est termine");
	}
	
	/**
	 * Cette m�thode permet de traiter le message de commande pass� en 
	 * param�tres et de retourner le message � renvoyer au client.
	 * 
	 * @param String message : le message de commande � traiter (en format XML)
	 * @return String : le message � renvoyer au client (en format XML)
	 * 		   			null si on ne doit rien retourner au client
	 * @throws TransformerConfigurationException : S'il y a une erreur dans la 
	 * 					configuration du transformeur
	 * @throws TransformerException : S'il y a une erreur lors de la conversion
	 * 					d'un document XML en une cha�ne de code XML 
	 * @synchronism Cette fonction est synchronis�e lorsque n�cessaire.
	 * 	     		La plupart du temps, on doit synchroniser le 
	 * 				traitement de la commande seulement dans le cas o�
	 * 				on doit passer les �l�ments d'une liste et qu'il
	 * 				peut y avoir des modifications de cette liste par
	 * 				un autre joueur. Dans les autres cas, ce sont les
	 * 				fonctions appel�es qui vont �tre synchronis�es.
	 */
	private String traiterCommandeJoueur(String message) throws TransformerConfigurationException,
																TransformerException
	{
		// D�claration d'une variable qui permet de savoir si on doit retourner 
		// une commande au client ou si ce n'�tait qu'une r�ponse du client 
		boolean bolDoitRetournerCommande = true;
System.out.println("Message recu : " + message);
		// Cr�er un nouveau Document qui va contenir le code XML du message 
		// pass� en param�tres
		Document objDocumentXMLEntree = UtilitaireXML.obtenirDocumentXML(message);
		
		// Cr�er un nouveau Document qui va contenir le code XML � retourner 
		// au client
		Document objDocumentXMLSortie = UtilitaireXML.obtenirDocumentXML();
		
		// D�clarer une r�f�rence vers le premier noeud de la commande
		// du client. Ce noeud est le noeud commande
		Element objNoeudCommandeEntree = objDocumentXMLEntree.getDocumentElement();

		// Cr�er le noeud de commande � retourner
		Element objNoeudCommande = objDocumentXMLSortie.createElement("commande");
		
		// Initialement, on d�finit les attributs type et nom comme �tant Erreur
		// et Commande respectivement pour dire qu'il y a une erreur avec la
		// commande (la commande n'est pas connue) -> Ces attributs seront 
		// modifi�s par la suite s'il y a d'autres erreurs. Par contre, on ne
		// d�finit pas tout de suite le num�ro de commande � envoyer au client
		objNoeudCommande.setAttribute("type", "Erreur");
		objNoeudCommande.setAttribute("nom", "CommandeNonReconnue");
		
		// Si la commande est un ping et qu'il a bel et bien un num�ro, alors
		// on peut appeler la m�thode du v�rificateur de connexions pour lui
		// dire qu'on a re�u un ping, il ne faut rien retourner au client
		if (objDocumentXMLEntree.getChildNodes().getLength() == 1 &&
		    objDocumentXMLEntree.getChildNodes().item(0).getNodeName().equals("ping") &&
		    objDocumentXMLEntree.getChildNodes().item(0).hasAttributes() == true &&
		    objDocumentXMLEntree.getChildNodes().item(0).getAttributes().getNamedItem("numero") != null)
		{
		    // TODO Modifier cette partie pour que la confirmation du ping soit le m�me 
		    // principe pour tous les autres �v�nements sauf que le ping ne renvoit pas 
		    // de commande au client
			// On ne retourne aucune commande au client
			bolDoitRetournerCommande = false;

			// Appeler la m�thode du v�rificateur de connexions permettant de
			// dire qu'on vient de recevoir une r�ponse � un ping de la part
			// d'un client
			objVerificateurConnexions.confirmationPing(this, 
					Integer.parseInt(objDocumentXMLEntree.getChildNodes().item(0).getAttributes().getNamedItem("numero").getNodeValue()));
		}
		// S'il n'y a pas de noeud commande dans le document XML, alors il y a 
		// une erreur, sinon on peut traiter le contenu du message
		else if (objDocumentXMLEntree.getChildNodes().getLength() == 1 &&
			objDocumentXMLEntree.getChildNodes().item(0).getNodeName().equals("commande") &&
			objDocumentXMLEntree.getChildNodes().item(0).hasAttributes() == true &&
			objDocumentXMLEntree.getChildNodes().item(0).getAttributes().getNamedItem("nom") != null &&
			objDocumentXMLEntree.getChildNodes().item(0).getAttributes().getNamedItem("no") != null &&
			Commande.estUnMembre(objNoeudCommandeEntree.getAttribute("nom")) == true)
		{
			// Avant de continuer les v�rifications, on va pouvoir retourner
			// une commande avec le num�ro de commande envoy� par le client
			objNoeudCommande.setAttribute("noClient", objNoeudCommandeEntree.getAttribute("no"));

			// Si le noeud de commande n'a pas une structure valide ou ne 
			// respecte pas tous les param�tres n�cessaires pour le type 
			// commande, alors il y a une erreur, sinon on peut traiter cette 
			// commande (donc on ne fait rien puisque l'erreur est d�j� 
			// d�finie comme �tant une erreur de param�tres)
			if (commandeEstValide(objNoeudCommandeEntree) == false)
			{
				// L'erreur est qu'un ou plusieurs des param�tres n'est pas bon 
				// (soit par le nombre, soit le type, ...)
				objNoeudCommande.setAttribute("nom", "ParametrePasBon");
			}
			else
			{
				// Pour chaque commande, on va faire certaines validations.
				// On va ensuite traiter la demande du client
				if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.Connexion))
				{
					// Si le joueur est d�j� connect� au serveur de jeu, alors
					// il y a une erreur, sinon on peut valider les informations
					// sur ce joueur pour ensuite le connecter (m�me si cette 
					// v�rification est faite lors de l'authentification, il vaut 
					// mieux la faire imm�diatement, car �a r�duit de beaucoup 
					// les chances que ce joueur se connecte juste apr�s cette 
					// validation)
					if (objControleurJeu.joueurEstConnecte(obtenirValeurParametre(objNoeudCommandeEntree, 
												"NomUtilisateur").getNodeValue()) == true)
					{
						// Le joueur est d�j� connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurDejaConnecte");
					}
					else
					{
						// On v�rifie si le joueur est bel et bien dans la BD et si 
						// son mot de passe est correct
						String strResultatAuthentification = 
							objControleurJeu.authentifierJoueur(this, 
									obtenirValeurParametre(objNoeudCommandeEntree, "NomUtilisateur").getNodeValue(), 
									obtenirValeurParametre(objNoeudCommandeEntree, "MotDePasse").getNodeValue(), true);
						
						// Si le r�sultat de l'authentification est true alors le
						// joueur est maintenant connect�
						if (strResultatAuthentification.equals(ResultatAuthentification.Succes))
						{
							// Il n'y a pas eu d'erreurs
							objNoeudCommande.setAttribute("type", "Reponse");
							objNoeudCommande.setAttribute("nom", "Ok");
						}
						else if (strResultatAuthentification.equals(ResultatAuthentification.JoueurDejaConnecte))
						{
							// Le joueur est d�j� connect� au serveur de jeu
							objNoeudCommande.setAttribute("nom", "JoueurDejaConnecte");
						}
						else
						{
							// Sinon la connexion est refus�e
							objNoeudCommande.setAttribute("nom", "JoueurNonConnu");    
						}
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.Deconnexion))
				{
					// Si le joueur humain a �t� d�fini dans le protocole, alors
					// c'est qu'il a r�ussi � se connecter au serveur de jeu, il
					// faut donc aviser le contr�leur de jeu pour qu'il enl�ve
					// le joueur du serveur de jeu
					if (objJoueurHumain != null)
					{
						// Informer le contr�leur de jeu que la connexion avec le 
						// client (joueur) a �t� ferm�e (il faut obtenir un num�ro
					    // de commandes de cette fonction)
						objControleurJeu.deconnecterJoueur(objJoueurHumain, true);
						
						// Il n'y a pas eu d'erreurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "Ok");
					}
					else
					{
						// Le joueur n'est pas connect�
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.ObtenirListeJoueurs))
				{
					// Si le joueur est connect� au serveur de jeu, alors on va
					// retourner au client la liste des joueurs connect�s
					if (objJoueurHumain != null)
					{
						// Il n'y a pas eu d'erreurs et il va falloir retourner 
						// une liste de joueurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "ListeJoueurs");
						
						// Cr�er le noeud de pour le param�tre contenant la liste
						// des joueurs � retourner
						Element objNoeudParametreListeJoueurs = objDocumentXMLSortie.createElement("parametre");
												
						// On ajoute un attribut type qui va contenir le type
						// du param�tre
						objNoeudParametreListeJoueurs.setAttribute("type", "ListeNomUtilisateurs");
						
						// Obtenir la liste des joueurs connect�s au serveur de jeu
						TreeMap lstListeJoueurs = objControleurJeu.obtenirListeJoueurs();
						
						// Emp�cher d'autres thread de toucher � la liste des
						// joueurs connect�s au serveur de jeu
						synchronized (lstListeJoueurs)
						{
							// Cr�er un ensemble contenant tous les tuples de la liste 
							// lstListeJoueurs (chaque �l�ment est un Map.Entry)
							Set lstEnsembleJoueurs = lstListeJoueurs.entrySet();
							
							// Obtenir un it�rateur pour l'ensemble contenant les joueurs
							Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
							
							// G�n�rer un nouveau num�ro de commande qui sera 
						    // retourn� au client
						    genererNumeroReponse();
							
							// Passer tous les joueurs connect�s et cr�er un noeud
							// pour chaque joueur et l'ajouter au noeud de param�tre
							while (objIterateurListe.hasNext() == true)
							{
								// Cr�er une r�f�rence vers le joueur humain courant dans la liste
								JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
								
								// Cr�er le noeud du joueur courant
								Element objNoeudJoueur = objDocumentXMLSortie.createElement("joueur");
								
								// On ajoute un attribut nom qui va contenir le nom
								// du joueur
								objNoeudJoueur.setAttribute("nom", objJoueur.obtenirNomUtilisateur());
								
								// Ajouter le noeud du joueur au noeud du param�tre
								objNoeudParametreListeJoueurs.appendChild(objNoeudJoueur);
							}
						}
						
						// Ajouter le noeud param�tre au noeud de commande dans
						// le document de sortie
						objNoeudCommande.appendChild(objNoeudParametreListeJoueurs);
					}
					else
					{
						// Sinon, il y a une erreur car le joueur doit �tre connect�
						// pour pouvoir avoir acc�s � la liste des joueurs
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.ObtenirListeSalles))
				{
					// Si le joueur est connect� au serveur de jeu, alors on va
					// retourner au client la liste des salles actives
					if (objJoueurHumain != null)
					{
					    // Il n'est pas n�cessaire de synchroniser cette partie
					    // du code car on n'ajoute ou retire jamais de salles
					    
						// Il n'y a pas eu d'erreurs et il va falloir retourner 
						// une liste de salles
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "ListeSalles");
						
						// Cr�er le noeud pour le param�tre contenant la liste
						// des salles � retourner
						Element objNoeudParametreListeSalles = objDocumentXMLSortie.createElement("parametre");
												
						// On ajoute un attribut type qui va contenir le type
						// du param�tre
						objNoeudParametreListeSalles.setAttribute("type", "ListeNomSalles");
					    
					    // Obtenir la liste des salles du serveur de jeu
						TreeMap lstListeSalles = objControleurJeu.obtenirListeSalles();
						
						// G�n�rer un nouveau num�ro de commande qui sera 
					    // retourn� au client
					    genererNumeroReponse();

						// Cr�er un ensemble contenant tous les tuples de la liste 
						// lstListeSalles (chaque �l�ment est un Map.Entry)
						Set lstEnsembleSalles = lstListeSalles.entrySet();
						
						// Obtenir un it�rateur pour l'ensemble contenant les salles
						Iterator objIterateurListe = lstEnsembleSalles.iterator();
						
						// Passer toutes les salles et cr�er un noeud pour 
						// chaque salle et l'ajouter au noeud de param�tre
						while (objIterateurListe.hasNext() == true)
						{
							// Cr�er une r�f�rence vers la salle courante dans la liste
							Salle objSalle = (Salle)(((Map.Entry)(objIterateurListe.next())).getValue());
							
							// Cr�er le noeud de la salle courante
							Element objNoeudSalle = objDocumentXMLSortie.createElement("salle");
							
							// On ajoute un attribut nom qui va contenir le nom
							// de la salle
							objNoeudSalle.setAttribute("nom", objSalle.obtenirNomSalle());
							
							// On ajoute un attribut protegee qui va contenir
							// une valeur bool�enne permettant de savoir si la
							// salle est prot�g�e par un mot de passe ou non
							objNoeudSalle.setAttribute("protegee", Boolean.toString(objSalle.protegeeParMotDePasse()));

							// Ajouter le noeud de la salle au noeud du param�tre
							objNoeudParametreListeSalles.appendChild(objNoeudSalle);
						}
						
						// Ajouter le noeud param�tre au noeud de commande dans
						// le document de sortie
						objNoeudCommande.appendChild(objNoeudParametreListeSalles);
					}
					else
					{
						// Sinon, il y a une erreur car le joueur doit �tre connect�
						// pour pouvoir avoir acc�s � la liste des salles
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.EntrerSalle))
				{
					// Si le joueur est connect�, alors on peut faire d'autre 
					// v�rifications, sinon il y a une erreur
					if (objJoueurHumain != null)
					{
						// D�claration d'une variable qui va contenir le noeud
						// du nom de la salle dans laquelle le client veut entrer
						Node objNomSalle = obtenirValeurParametre(objNoeudCommandeEntree, "NomSalle");
						
						// D�claration d'une variable qui va contenir le noeud
						// du mot de passe permettant d'acc�der � la salle (s'il 
						// n'y en a pas, alors le mot de passe sera vide)
						Node objMotDePasse = obtenirValeurParametre(objNoeudCommandeEntree, "MotDePasse");
						
						// D�claration d'une variable qui va contenir le mot de
						// passe pour acc�der � la salle (peut �tre vide)
						String strMotDePasse = "";
						
						// Si le noeud du mot de passe n'est pas null alors il y
						// a un mot de passe pour la salle
						if (objMotDePasse != null)
						{
							// Garder le mot de passe en m�moire
							strMotDePasse = objMotDePasse.getNodeValue();
						}
						
						// Il n'est pas n�cessaire de synchroniser ces v�rifications
						// car un protocole ne peut pas ex�cuter plus qu'une fonction
						// � la fois, donc les valeurs ne peuvent �tre modifi�es par
						// deux threads � la fois
						
						// Si la salle n'existe pas dans le serveur de jeu, alors il
						// y a une erreur
						if (objControleurJeu.salleExiste(objNomSalle.getNodeValue()) == false)
						{
							// La salle n'existe pas
							objNoeudCommande.setAttribute("nom", "SalleNonExistante");
						}
						// Si le joueur courant se trouve d�j� dans une salle, 
						// alors il y a une erreur (pas besoin de synchroniser 
						// cette validation, car un seul thread peut modifier cet 
						// objet)
						else if (objJoueurHumain.obtenirSalleCourante() != null)
						{
							// Le joueur est d�j� dans une salle
							objNoeudCommande.setAttribute("nom", "JoueurDansSalle");							
						}
						else
						{
							// D�claration d'une variable qui va permettre de
							// savoir si le le joueur a r�ussi � entrer dans
							// la salle (donc que le mot de passe �tait le bon)
							boolean bolResultatEntreeSalle = objControleurJeu.entrerSalle(objJoueurHumain, 
										objNomSalle.getNodeValue(), strMotDePasse, true);

							// Si le joueur a r�ussi � entrer
							if (bolResultatEntreeSalle == true)
							{
								// Il n'y a pas eu d'erreurs
								objNoeudCommande.setAttribute("type", "Reponse");
								objNoeudCommande.setAttribute("nom", "Ok");								
							}
							else
							{
								// Le mot de passe pour entrer dans la salle 
								// n'est pas le bon
								objNoeudCommande.setAttribute("nom", "MauvaisMotDePasseSalle");								
							}
						}
					}
					else
					{
						// Le joueur doit �tre connect� au serveur de jeu pour 
						// pouvoir entrer dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");						
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.QuitterSalle))
				{
					// Si le joueur n'est pas connect�, alors il y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur n'est pas connect�
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est pas dans aucune salle, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur n'est pas dans aucune salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");						
					}
					else
					{
						// Appeler la m�thode pour quitter la salle
						objJoueurHumain.obtenirSalleCourante().quitterSalle(objJoueurHumain, true);
						
						// Il n'y a pas eu d'erreurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "Ok");
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.ObtenirListeJoueursSalle))
				{
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas acc�der � la liste des joueurs 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas obtenir la liste des joueurs dans cette salle
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas acc�der � la liste des joueurs 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");						
					}
					else
					{
						// Il n'y a pas eu d'erreurs et il va falloir retourner 
						// une liste de joueurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "ListeJoueursSalle");
						
						// Cr�er le noeud de pour le param�tre contenant la liste
						// des joueurs � retourner
						Element objNoeudParametreListeJoueurs = objDocumentXMLSortie.createElement("parametre");
												
						// On ajoute un attribut type qui va contenir le type
						// du param�tre
						objNoeudParametreListeJoueurs.setAttribute("type", "ListeNomUtilisateurs");
						
					    // Obtenir la liste des joueurs se trouvant dans la 
						// salle courante
						TreeMap lstListeJoueurs = objJoueurHumain.obtenirSalleCourante().obtenirListeJoueurs();
						
						// Emp�cher d'autres thread de toucher � la liste des
						// joueurs se trouvant dans la salle courante
						synchronized (lstListeJoueurs)
						{
							// Cr�er un ensemble contenant tous les tuples de la liste 
							// lstListeJoueurs (chaque �l�ment est un Map.Entry)
							Set lstEnsembleJoueurs = lstListeJoueurs.entrySet();
							
							// Obtenir un it�rateur pour l'ensemble contenant les joueurs
							Iterator objIterateurListe = lstEnsembleJoueurs.iterator();
							
							// G�n�rer un nouveau num�ro de commande qui sera 
						    // retourn� au client
						    genererNumeroReponse();
							
							// Passer tous les joueurs connect�s et cr�er un noeud
							// pour chaque joueur et l'ajouter au noeud de param�tre
							while (objIterateurListe.hasNext() == true)
							{
								// Cr�er une r�f�rence vers le joueur humain courant dans la liste
								JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListe.next())).getValue());
								
								// Cr�er le noeud du joueur courant
								Element objNoeudJoueur = objDocumentXMLSortie.createElement("joueur");
								
								// On ajoute un attribut nom qui va contenir le nom
								// du joueur
								objNoeudJoueur.setAttribute("nom", objJoueur.obtenirNomUtilisateur());
								
								// Ajouter le noeud du joueur au noeud du param�tre
								objNoeudParametreListeJoueurs.appendChild(objNoeudJoueur);
							}						    
						}
						
						// Ajouter le noeud param�tre au noeud de commande dans
						// le document de sortie
						objNoeudCommande.appendChild(objNoeudParametreListeJoueurs);
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.ObtenirListeTables))
				{
				    // Cette partie de code est synchronis�e de telle mani�re
				    // que le client peut recevoir des �v�nements d'entr�e/sortie
				    // de table avant le no de retour, un peu apr�s, ou 
				    // compl�tement apr�s, dans tous les cas, le client doit 
				    // s'occuper d'arranger tout �a et de ne rien faire si des 
				    // �v�nements arrivent apr�s le no de retour et que �a ne 
				    // change rien � la liste, car c'est normal
				    
					// Obtenir la valeur du param�tre Filtre et le garder en 
					// m�moire dans une variable
					String strFiltre = obtenirValeurParametre(objNoeudCommandeEntree, "Filtre").getNodeValue();
					
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas acc�der � la liste des tables 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas obtenir la liste des tables dans cette salle
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas acc�der � la liste des tables 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");						
					}
					// Si le param�tre Filtre n'est pas l'un des �l�ments de 
					// l'�num�ration des filtres, alors il y a une erreur
					else if (Filtre.estUnMembre(strFiltre) == false)
					{
						// Le filtre n'a pas une valeur valide
						objNoeudCommande.setAttribute("nom", "FiltreNonConnu");						
					}
					else
					{
						// Il n'y a pas eu d'erreurs et il va falloir retourner
						// une liste de tables
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "ListeTables");
						
						// Cr�er le noeud pour le param�tre contenant la liste
						// des tables � retourner
						Element objNoeudParametreListeTables = objDocumentXMLSortie.createElement("parametre");
						
						// On ajoute un attribut type qui va contenir le type
						// du param�tre
						objNoeudParametreListeTables.setAttribute("type", "ListeTables");
						
					    // Obtenir la liste des tables se trouvant dans la 
						// salle courante
						TreeMap lstListeTables = objJoueurHumain.obtenirSalleCourante().obtenirListeTables();
						
						// Emp�cher d'autres thread de toucher � la liste des
						// tables se trouvant dans la salle courante
						synchronized (lstListeTables)
						{
							// Cr�er un ensemble contenant tous les tuples de la liste 
							// lstListeTables (chaque �l�ment est un Map.Entry)
							Set lstEnsembleTables = lstListeTables.entrySet();
							
							// Obtenir un it�rateur pour l'ensemble contenant les tables
							Iterator objIterateurListeTables = lstEnsembleTables.iterator();
							
							// G�n�rer un nouveau num�ro de commande qui sera 
						    // retourn� au client
						    genererNumeroReponse();
							
							// Passer toutes les tables et cr�er un noeud pour 
							// chaque table et l'ajouter au noeud de param�tre
							while (objIterateurListeTables.hasNext() == true)
							{
								// Cr�er une r�f�rence vers la table courante dans la liste
								Table objTable = (Table)(((Map.Entry)(objIterateurListeTables.next())).getValue());
								
								// Obtenir la liste des joueurs se trouvant dans la 
								// table courante
								TreeMap lstListeJoueurs = objTable.obtenirListeJoueurs();
								
								// Emp�cher d'autres thread de toucher � la liste des
								// joueurs de la table courante
								synchronized (lstListeJoueurs)
								{
									//TODO: Peut-�tre va-t-il falloir ajouter 
									// des validations suppl�mentaires ici lorsqu'une 
									// partie d�butera ou se terminera
									// Si la table est une de celles qui doivent �tre 
									// retourn�es selon le filtre, alors on continue 
									if (strFiltre.equals(Filtre.Toutes) ||
									   (strFiltre.equals(Filtre.IncompletesNonCommencees) && objTable.estComplete() == false && objTable.estCommencee() == false) || 
									   (strFiltre.equals(Filtre.IncompletesCommencees) && objTable.estComplete() == false && objTable.estCommencee() == true) ||
									   (strFiltre.equals(Filtre.CompletesNonCommencees) && objTable.estComplete() == true && objTable.estCommencee() == false) ||
									   (strFiltre.equals(Filtre.CompletesCommencees) && objTable.estComplete() == true && objTable.estCommencee() == true))
									{
										// Cr�er le noeud de la table courante
										Element objNoeudTable = objDocumentXMLSortie.createElement("table");
										
										// On ajoute un attribut no qui va contenir le 
										// num�ro de la table
										objNoeudTable.setAttribute("no", Integer.toString(objTable.obtenirNoTable()));
										
										// On ajoute un attribut temps qui va contenir le 
										// temps des parties qui se d�roulent sur cette table
										objNoeudTable.setAttribute("temps", Integer.toString(objTable.obtenirTempsTotal()));
	
										// Cr�er un ensemble contenant tous les tuples de la liste 
										// lstListeJoueurs (chaque �l�ment est un Map.Entry)
										Set lstEnsembleJoueurs = lstListeJoueurs.entrySet();
										
										// Obtenir un it�rateur pour l'ensemble contenant les joueurs
										Iterator objIterateurListeJoueurs = lstEnsembleJoueurs.iterator();
										
										// Passer tous les joueurs et cr�er un noeud pour 
										// chaque joueur et l'ajouter au noeud de la table 
										// courante
										while (objIterateurListeJoueurs.hasNext() == true)
										{
											// Cr�er une r�f�rence vers le joueur courant 
										    // dans la liste
											JoueurHumain objJoueur = (JoueurHumain)(((Map.Entry)(objIterateurListeJoueurs.next())).getValue());
											
											// Cr�er le noeud du joueur courant
											Element objNoeudJoueur = objDocumentXMLSortie.createElement("joueur");
											
											// On ajoute un attribut nom qui va contenir le 
											// nom d'utilisateur du joueur
											objNoeudJoueur.setAttribute("nom", objJoueur.obtenirNomUtilisateur());
											
											// Ajouter le noeud du joueur au noeud de la table
											objNoeudTable.appendChild(objNoeudJoueur);
										}									    
										
										// Ajouter le noeud de la table au noeud du param�tre
										objNoeudParametreListeTables.appendChild(objNoeudTable);
									}
								}
							}						    
						}
						
						// Ajouter le noeud param�tre au noeud de commande dans
						// le document de sortie
						objNoeudCommande.appendChild(objNoeudParametreListeTables);
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.CreerTable))
				{
					// Il n'est pas n�cessaire de synchroniser ces v�rifications
					// car un protocole ne peut pas ex�cuter plus qu'une fonction
					// � la fois, donc les valeurs ne peuvent �tre modifi�es par
					// deux threads � la fois
					
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas acc�der � la liste des joueurs 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas cr�er de tables
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas cr�er de nouvelles tables 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");
					}
					//TODO: Il va falloir synchroniser cette validation lorsqu'on va 
					// avoir cod� la commande SortirJoueurTable -> �a va ressembler au
					// processus d'authentification
					// Si le joueur est dans une table, alors il ne 
					// peut pas cr�er de tables, il faut qu'il sorte avant
					else if (objJoueurHumain.obtenirPartieCourante() != null)
					{
						// Le joueur ne peut pas cr�er de nouvelles tables 
						// s'il est d�j� dans une table
						objNoeudCommande.setAttribute("nom", "JoueurDansTable");
					}
					else
					{
						// Il n'y a pas eu d'erreurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "NoTable");
						
						// D�claration d'une variable qui va contenir le temps
						// de la partie que le client veut cr�er
						int intTempsPartie = Integer.parseInt(obtenirValeurParametre(objNoeudCommandeEntree, "TempsPartie").getNodeValue());
						
						// Appeler la m�thode permettant de cr�er la nouvelle
						// table et d'entrer le joueur dans cette table
						int intNoTable = objJoueurHumain.obtenirSalleCourante().creerTable(objJoueurHumain, 
									intTempsPartie, true);
						
						// Cr�er le noeud param�tre du num�ro de la table
						Element objNoeudParametreNoTable = objDocumentXMLSortie.createElement("parametre"); 

						// Cr�er un noeud texte contenant le num�ro de la table
						Text objNoeudTexteNoTable = objDocumentXMLSortie.createTextNode(Integer.toString(intNoTable));
						
						// D�finir l'attribut type pour le noeud param�tre
						objNoeudParametreNoTable.setAttribute("type", "NoTable");
						
						// Ajouter le noeud texte au noeud param�tre
						objNoeudParametreNoTable.appendChild(objNoeudTexteNoTable);
						
						// Ajouter le noeud param�tre au noeud de commande
						objNoeudCommande.appendChild(objNoeudParametreNoTable);
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.EntrerTable))
				{
					// Il n'est pas n�cessaire de synchroniser ces v�rifications
					// car un protocole ne peut pas ex�cuter plus qu'une fonction
					// � la fois, donc les valeurs ne peuvent �tre modifi�es par
					// deux threads � la fois
					
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas entrer dans une table 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas entrer dans une table
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas entrer dans une table 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");
					}
					//TODO: Il va falloir synchroniser cette validation lorsqu'on va 
					// avoir cod� la commande SortirJoueurTable -> �a va ressembler au
					// processus d'authentification
					// Si le joueur est dans une table, alors il ne 
					// peut pas entrer dans une autre table sans sortir de celle 
					// dans laquelle il se trouve pr�sentement
					else if (objJoueurHumain.obtenirPartieCourante() != null)
					{
						// Le joueur ne peut pas entrer dans une table 
						// s'il est d�j� dans une table
						objNoeudCommande.setAttribute("nom", "JoueurDansTable");
					}
					else
					{
						// Obtenir le num�ro de la table dans laquelle le joueur 
						// veut entrer et le garder en m�moire dans une variable
						int intNoTable = Integer.parseInt(obtenirValeurParametre(objNoeudCommandeEntree, "NoTable").getNodeValue());
						
						// D�claration d'une nouvelle liste de personnages
						TreeMap lstPersonnageJoueurs = new TreeMap();
						
						// Appeler la m�thode permettant d'entrer dans la
						// table et garder son r�sultat dans une variable
						String strResultatEntreeTable = objJoueurHumain.obtenirSalleCourante().entrerTable(objJoueurHumain, 
																										   intNoTable, true, 
																										   lstPersonnageJoueurs);
						
						// Si le r�sultat de l'entr�e dans la table est true alors le
						// joueur est maintenant dans la table
						if (strResultatEntreeTable.equals(ResultatEntreeTable.Succes))
						{
							// Il n'y a pas eu d'erreurs, mais on doit retourner
							// la liste des joueurs avec leur idPersonnage
							objNoeudCommande.setAttribute("type", "Reponse");
							objNoeudCommande.setAttribute("nom", "ListePersonnageJoueurs");
							
							// Cr�er le noeud pour le param�tre contenant la liste
							// des personnages � retourner
							Element objNoeudParametreListePersonnageJoueurs = objDocumentXMLSortie.createElement("parametre");
							
							// On ajoute un attribut type qui va contenir le type
							// du param�tre
							objNoeudParametreListePersonnageJoueurs.setAttribute("type", "ListePersonnageJoueurs");
							
							// Cr�er un ensemble contenant tous les tuples de la liste 
							// lstPersonnageJoueurs (chaque �l�ment est un Map.Entry)
							Set lstEnsemblePersonnageJoueurs = lstPersonnageJoueurs.entrySet();
							
							// Obtenir un it�rateur pour l'ensemble contenant les personnages
							Iterator objIterateurListePersonnageJoueurs = lstEnsemblePersonnageJoueurs.iterator();
							
							// Passer tous les personnages et cr�er un noeud pour 
							// chaque id de personnage et l'ajouter au noeud de param�tre
							while (objIterateurListePersonnageJoueurs.hasNext() == true)
							{
								// Garder une r�f�rence vers l'entr�e courante
								Map.Entry objEntreeListePersonnageJoueurs = (Map.Entry)objIterateurListePersonnageJoueurs.next();
								
								// Cr�er le noeud pour le joueur courant
								Element objNoeudPersonnage = objDocumentXMLSortie.createElement("personnage");
								
								// D�finir le nom d'utilisateur du joueur ainsi que le id du personnage
								objNoeudPersonnage.setAttribute("nom", (String) objEntreeListePersonnageJoueurs.getKey());
								objNoeudPersonnage.setAttribute("idPersonnage", ((Integer) objEntreeListePersonnageJoueurs.getValue()).toString());
								
								// Ajouter le noeud du personnage au noeud de param�tre
								objNoeudParametreListePersonnageJoueurs.appendChild(objNoeudPersonnage);
							}
							
							// Ajouter le noeud de param�tres au noeud de commande
							objNoeudCommande.appendChild(objNoeudParametreListePersonnageJoueurs);
						}
						else if (strResultatEntreeTable.equals(ResultatEntreeTable.TableNonExistante))
						{
							// La table n'existe plus
							objNoeudCommande.setAttribute("nom", "TableNonExistante");
						}
						else if (strResultatEntreeTable.equals(ResultatEntreeTable.TableComplete))
						{
							// La table est compl�te
							objNoeudCommande.setAttribute("nom", "TableComplete");
						}
						else
						{
							// Une partie est d�j� commenc�e
							objNoeudCommande.setAttribute("nom", "PartieEnCours");    
						}
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.QuitterTable))
				{
					// Si le joueur n'est pas connect�, alors il y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur n'est pas connect�
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est pas dans aucune salle, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur n'est pas dans aucune salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");						
					}
					//TODO: Il va falloir synchroniser cette validation lorsqu'on va 
					// avoir cod� la commande SortirJoueurTable -> �a va ressembler au
					// processus d'authentification
					// Si le joueur n'est pas dans aucune table, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirPartieCourante() == null)
					{
						// Le joueur n'est pas dans aucune table
						objNoeudCommande.setAttribute("nom", "JoueurPasDansTable");						
					}
					else
					{
						// Appeler la m�thode pour quitter la table
						objJoueurHumain.obtenirPartieCourante().obtenirTable().quitterTable(objJoueurHumain, true);
						
						// Il n'y a pas eu d'erreurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "Ok");
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.DemarrerPartie))
				{
					// Il n'est pas n�cessaire de synchroniser ces v�rifications
					// car un protocole ne peut pas ex�cuter plus qu'une fonction
					// � la fois, donc les valeurs ne peuvent �tre modifi�es par
					// deux threads � la fois
					
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas d�marrer une partie 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas d�marrer une partie
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas d�marrer une partie 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");
					}
					//TODO: Il va falloir synchroniser cette validation lorsqu'on va 
					// avoir cod� la commande SortirJoueurTable -> �a va ressembler au
					// processus d'authentification
					// Si le joueur n'est pas dans aucune table, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirPartieCourante() == null)
					{
						// Le joueur ne peut pas d�marrer une partie 
						// s'il n'est dans aucune table
						objNoeudCommande.setAttribute("nom", "JoueurPasDansTable");
					}
					// On n'a pas besoin de valider qu'il n'y aucune partie de 
					// commenc�e, car le joueur doit obligatoirement �tre dans 
					// la table pour d�marrer la partie et comme il ne peut entrer 
					// si une partie est en cours, alors c'est certain qu'il n'y 
					// aura pas de parties en cours
					else
					{
						// Obtenir le num�ro Id du personnage choisi et le garder 
						// en m�moire dans une variable
						int intIdPersonnage = Integer.parseInt(obtenirValeurParametre(objNoeudCommandeEntree, "IdPersonnage").getNodeValue());
						
						// Appeler la m�thode permettant de d�marrer une partie
						// et garder son r�sultat dans une variable
						String strResultatDemarrerPartie = objJoueurHumain.obtenirPartieCourante().obtenirTable().demarrerPartie(objJoueurHumain, 
																intIdPersonnage, true);
						
						// Si le r�sultat du d�marrage de partie est Succes alors le
						// joueur est maintenant en attente
						if (strResultatDemarrerPartie.equals(ResultatDemarrerPartie.Succes))
						{
							// Il n'y a pas eu d'erreurs
							objNoeudCommande.setAttribute("type", "Reponse");
							objNoeudCommande.setAttribute("nom", "Ok");
						}
						else if (strResultatDemarrerPartie.equals(ResultatDemarrerPartie.PartieEnCours))
						{
							// Il y avait d�j� une partie en cours
							objNoeudCommande.setAttribute("nom", "PartieEnCours");
						}
						else
						{
							// Le joueur �tait d�j� en attente
							objNoeudCommande.setAttribute("nom", "DejaEnAttente");
						}
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.DeplacerPersonnage))
				{
					// Faire la r�f�rence vers le noeud gardant l'information
					// sur la nouvelle position du joueur
					Node objNoeudNouvellePosition = obtenirValeurParametre(objNoeudCommandeEntree, "NouvellePosition");
					
					// Obtenir la position x, y o� le joueur souhaite se d�placer 
					Point objNouvellePosition = new Point(Integer.parseInt(objNoeudNouvellePosition.getAttributes().getNamedItem("x").getNodeValue()), Integer.parseInt(objNoeudNouvellePosition.getAttributes().getNamedItem("y").getNodeValue()));
					
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas d�placer son personnage 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas d�placer son personnage
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas d�placer son personnage 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");
					}
					//TODO: Il va falloir synchroniser cette validation lorsqu'on va 
					// avoir cod� la commande SortirJoueurTable -> �a va ressembler au
					// processus d'authentification
					// Si le joueur n'est pas dans aucune table, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirPartieCourante() == null)
					{
						// Le joueur ne peut pas d�placer son personnage 
						// s'il n'est dans aucune table
						objNoeudCommande.setAttribute("nom", "JoueurPasDansTable");
					}
					// Si la partie n'est pas commenc�e, alors il y a une erreur
					else if (objJoueurHumain.obtenirPartieCourante().obtenirTable().estCommencee() == false)
					{
						// Le joueur ne peut pas d�placer son personnage 
						// si la partie n'est pas commenc�e
						objNoeudCommande.setAttribute("nom", "PartiePasDemarree");
					}
					// Si une question a d�j� �t� pos�e au client, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirPartieCourante().obtenirQuestionCourante() != null)
					{
						// Le joueur ne peut pas d�placer son personnage 
						// si une question lui a d�j� �t� pos�e
						objNoeudCommande.setAttribute("nom", "QuestionPasRepondue");
					}
					// Si le d�placement n'est pas permis, alors il y a une erreur
					else if (objJoueurHumain.obtenirPartieCourante().deplacementEstPermis(objNouvellePosition) == false)
					{
						// Le joueur ne peut pas d�placer son personnage 
						// si une question lui a d�j� �t� pos�e
						objNoeudCommande.setAttribute("nom", "DeplacementNonAutorise");
					}
					else
					{
						// Trouver la question � poser selon la difficult� et 
						// le type de case sur laquelle on veut se diriger
						Question objQuestionAPoser = objJoueurHumain.obtenirPartieCourante().trouverQuestionAPoser(objNouvellePosition, true);
						
						// Il n'y a pas eu d'erreurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "Question");
						
						// Cr�er le noeud param�tre de la question
						Element objNoeudParametreQuestion = objDocumentXMLSortie.createElement("parametre"); 
						
						// D�finir les attributs pour le noeud param�tre et question
						objNoeudParametreQuestion.setAttribute("type", "Question");
						
						// Si aucune question n'a �t� trouv�e, alors c'est que
						// le joueur ne s'est pas d�plac�, on ne renvoit donc
						// que le param�tre sans la question, sinon on renvoit
						// �galement l'information sur la question
						if (objQuestionAPoser != null)
						{
							// Cr�er un noeud texte contenant l'information sur la question
							Element objNoeudQuestion = objDocumentXMLSortie.createElement("question");
													
							objNoeudQuestion.setAttribute("id", Integer.toString(objQuestionAPoser.obtenirCodeQuestion()));
							objNoeudQuestion.setAttribute("type", objQuestionAPoser.obtenirTypeQuestion().toString());
							objNoeudQuestion.setAttribute("url", objQuestionAPoser.obtenirURLQuestion());
							
							// Ajouter le noeud question au noeud param�tre
							objNoeudParametreQuestion.appendChild(objNoeudQuestion);
						}
						
						// Ajouter le noeud param�tre au noeud de commande
						objNoeudCommande.appendChild(objNoeudParametreQuestion);
					}
				}
				else if (objNoeudCommandeEntree.getAttribute("nom").equals(Commande.RepondreQuestion))
				{
					// Obtenir la r�ponse du joueur
					String strReponse = obtenirValeurParametre(objNoeudCommandeEntree, "Reponse").getNodeValue();
					
					// Si le joueur n'est pas connect� au serveur de jeu, alors il
					// y a une erreur
					if (objJoueurHumain == null)
					{
						// Le joueur ne peut pas r�pondre � une question 
						// s'il n'est pas connect� au serveur de jeu
						objNoeudCommande.setAttribute("nom", "JoueurNonConnecte");
					}
					// Si le joueur n'est connect� � aucune salle, alors il ne 
					// peut pas r�pondre � aucune question
					else if (objJoueurHumain.obtenirSalleCourante() == null)
					{
						// Le joueur ne peut pas r�pondre � aucune question 
						// s'il n'est pas dans une salle
						objNoeudCommande.setAttribute("nom", "JoueurPasDansSalle");
					}
					//TODO: Il va falloir synchroniser cette validation lorsqu'on va 
					// avoir cod� la commande SortirJoueurTable -> �a va ressembler au
					// processus d'authentification
					// Si le joueur n'est pas dans aucune table, alors il y a 
					// une erreur
					else if (objJoueurHumain.obtenirPartieCourante() == null)
					{
						// Le joueur ne peut pas r�pondre � aucune question 
						// s'il n'est dans aucune table
						objNoeudCommande.setAttribute("nom", "JoueurPasDansTable");
					}
					// Si la partie n'est pas commenc�e, alors il y a une erreur
					else if (objJoueurHumain.obtenirPartieCourante().obtenirTable().estCommencee() == false)
					{
						// Le joueur ne peut pas r�pondre � aucune question 
						// si la partie n'est pas commenc�e
						objNoeudCommande.setAttribute("nom", "PartiePasDemarree");
					}
					// Si une question n'a pas d�j� �t� pos�e au client, alors 
					// il y a une erreur
					else if (objJoueurHumain.obtenirPartieCourante().obtenirQuestionCourante() == null)
					{
						// Le joueur ne peut pas r�pondre � une question 
						// si une question ne lui a pas d�j� �t� pos�e
						objNoeudCommande.setAttribute("nom", "DeplacementNonDemande");
					}
					else
					{
						// V�rifier si la r�ponse est bonne et obtenir un objet
						// contenant toutes les informations � retourner
						RetourVerifierReponseEtMettreAJourPlateauJeu objRetour = objJoueurHumain.obtenirPartieCourante().verifierReponseEtMettreAJourPlateauJeu(strReponse, true);
						
						// Il n'y a pas eu d'erreurs
						objNoeudCommande.setAttribute("type", "Reponse");
						objNoeudCommande.setAttribute("nom", "Deplacement");
						
						// Cr�er les noeuds param�tres et enfants et construire
						// le document XML de retour
						Element objNoeudParametreDeplacementAccepte = objDocumentXMLSortie.createElement("parametre");
						Element objNoeudParametrePointage = objDocumentXMLSortie.createElement("parametre");
						
						Text objNoeudTexteDeplacementAccepte = objDocumentXMLSortie.createTextNode(Boolean.toString(objRetour.deplacementEstAccepte()));
						Text objNoeudTextePointage = objDocumentXMLSortie.createTextNode(Integer.toString(objRetour.obtenirNouveauPointage()));
						
						objNoeudParametreDeplacementAccepte.setAttribute("type", "DeplacementAccepte");
						objNoeudParametrePointage.setAttribute("type", "Pointage");
						
						objNoeudParametreDeplacementAccepte.appendChild(objNoeudTexteDeplacementAccepte);
						objNoeudParametrePointage.appendChild(objNoeudTextePointage);
						
						objNoeudCommande.appendChild(objNoeudParametreDeplacementAccepte);

						// Si le d�placement est accept�, alors on cr�e les 
						// noeuds sp�cifiques au succ�s de la r�ponse
						if (objRetour.deplacementEstAccepte() == true)
						{
							Element objNoeudParametreObjetRamasse = objDocumentXMLSortie.createElement("parametre");
							Element objNoeudParametreObjetSubi = objDocumentXMLSortie.createElement("parametre");
							Element objNoeudParametreNouvellePosition = objDocumentXMLSortie.createElement("parametre");
							
							objNoeudParametreObjetRamasse.setAttribute("type", "ObjetRamasse");
							objNoeudParametreObjetSubi.setAttribute("type", "ObjetSubi");
							objNoeudParametreNouvellePosition.setAttribute("type", "NouvellePosition");
							
							// S'il y a un objet qui a �t� ramass�, alors on peut
							// cr�er son noeud enfant, sinon on n'en cr�e pas
							if (objRetour.obtenirObjetRamasse() != null)
							{
								Element objNoeudObjetRamasse = objDocumentXMLSortie.createElement("objetRamasse");
								objNoeudObjetRamasse.setAttribute("id", Integer.toString(objRetour.obtenirObjetRamasse().obtenirId()));
								objNoeudObjetRamasse.setAttribute("type", objRetour.obtenirObjetRamasse().getClass().getSimpleName());
								objNoeudParametreObjetRamasse.appendChild(objNoeudObjetRamasse);
								objNoeudCommande.appendChild(objNoeudParametreObjetRamasse);
							}
							
							// Si le joueur a subi un objet, alors on peut cr�er 
							// son noeud enfant, sinon on n'en cr�e pas
							if (objRetour.obtenirObjetSubi() != null)
							{
								Element objNoeudObjetSubi = objDocumentXMLSortie.createElement("objetSubi");
								objNoeudObjetSubi.setAttribute("id", Integer.toString(objRetour.obtenirObjetSubi().obtenirId()));
								objNoeudObjetSubi.setAttribute("type", objRetour.obtenirObjetSubi().getClass().getSimpleName());
								objNoeudParametreObjetSubi.appendChild(objNoeudObjetSubi);
								objNoeudCommande.appendChild(objNoeudParametreObjetSubi);
							}
							
							Element objNoeudNouvellePosition = objDocumentXMLSortie.createElement("position");
							objNoeudNouvellePosition.setAttribute("x", Integer.toString(objRetour.obtenirNouvellePosition().x));
							objNoeudNouvellePosition.setAttribute("y", Integer.toString(objRetour.obtenirNouvellePosition().y));
							objNoeudParametreNouvellePosition.appendChild(objNoeudNouvellePosition);
							objNoeudCommande.appendChild(objNoeudParametreNouvellePosition);
						}
						else
						{
							// Cr�er le noeud explications
							Element objNoeudParametreExplication = objDocumentXMLSortie.createElement("parametre");
							Text objNoeudTexteExplication = objDocumentXMLSortie.createTextNode(objRetour.obtenirExplications());
							objNoeudParametreExplication.setAttribute("type", "Explication");
							objNoeudParametreExplication.appendChild(objNoeudTexteExplication);
							objNoeudCommande.appendChild(objNoeudParametreExplication);
						}
						
						// Ajouter les noeuds param�tres au noeud de commande
						objNoeudCommande.appendChild(objNoeudParametrePointage);
					}
				}
			}
		}
		
		// Si on doit retourner une commande alors on ajoute le noeud de commande 
		// et on retourne le code XML de la commande. Si le num�ro de commande 
		// n'avait pas �t� g�n�r�, alors on le g�n�re
		if (bolDoitRetournerCommande == true)
		{
		    // Si le num�ro de commande � envoyer au client n'a pas encore
		    // �t� d�fini, alors on le d�finit, puis on ajoute l'attribut
		    // no du noeud de commande
			if (intNumeroCommandeReponse == -1)
			{
			    // G�n�rer un nouveau num�ro de commande � renvoyer
			    genererNumeroReponse();
			}
			
			// D�finir le num�ro de la commande � retourner
			objNoeudCommande.setAttribute("no", Integer.toString(intNumeroCommandeReponse));
		    
			// Ajouter le noeud de commande au noeud racine dans le document de sortie
			objDocumentXMLSortie.appendChild(objNoeudCommande);

	        // Retourner le document XML ne contenant pas l'ent�te XML ajout�e 
	        // par d�faut par le transformateur
			return UtilitaireXML.transformerDocumentXMLEnString(objDocumentXMLSortie);
		}
		else
		{
			// Si on ne doit rien retourner, alors on retourne null
			return null;
		}
	}

	/**
	 * Cette m�thode permet d'envoyer le message pass� en param�tre au 
	 * client (joueur). Deux threads ne peuvent �crire sur le socket en m�me
	 * temps.
	 * 
	 * @param String message : le message � envoyer au client
	 * @throws IOException : Si on ne peut pas obtenir l'acc�s en 
	 * 						 �criture sur le canal de communication
	 */
	public void envoyerMessage(String message) throws IOException
	{
		// Synchroniser cette partie de code pour emp�cher 2 threads d'envoyer
		// un message en m�me temps sur le canal d'envoi du socket
		synchronized (objSocketJoueur)
		{
			// Cr�er le canal qui permet d'envoyer des donn�es sur le canal
			// de communication entre le client et le serveur
			OutputStream objCanalEnvoi = objSocketJoueur.getOutputStream();
String chainetemp = UtilitaireEncodeurDecodeur.encodeToUTF8(message);
if (chainetemp.contains("ping") == false)
{
	System.out.println("Message envoye : " + chainetemp);	
}
			// �crire le message sur le canal d'envoi au client
			objCanalEnvoi.write(UtilitaireEncodeurDecodeur.encodeToUTF8(message).getBytes());
			
			// �crire le byte 0 sur le canal d'envoi pour signifier la fin du message
			objCanalEnvoi.write((byte) 0);
			
			// Envoyer le message sur le canal d'envoi
			objCanalEnvoi.flush();
			
			System.out.println("Une confirmation a ete envoyee a " + objSocketJoueur.getInetAddress().toString());
		}
	}
	
	/**
	 * Cette m�thode permet de d�terminer si le noeud de commande pass� en 
	 * param�tres ne contient que des param�tres valides et que chacun de
	 * ces param�tres contient bien ce qu'il doit contenir. On suppose que le
	 * noeud pass� en param�tres est bel et bien un noeud de commande et qu'il
	 * poss�de un attribut nom.
	 * 
	 * @param Element noeudCommande : le noeud de comande � valider
	 * @return boolean : true si le noeud de commande et tous ses enfants sont
	 * 				     	  corrects
	 * 					 false sinon
	 */
	private boolean commandeEstValide(Element noeudCommande)
	{
		// D�claration d'une variable qui va permettre de savoir si la 
		// commande est valide ou non
		boolean bolCommandeValide = false;
		
		// Si le nom de la commande est Connexion, alors il doit y avoir 
		// 2 param�tres correspondants au nom d'utilisateur du joueur et 
		// � son mot de passe
		if (noeudCommande.getAttribute("nom").equals(Commande.Connexion))
		{
			// Si le nombre d'enfants du noeud de commande est de 2, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 2)
			{
				// D�clarer une variable qui va permettre de savoir si les 
				// noeuds enfants sont valides
				boolean bolNoeudValide = true;
				
				// D�claration d'un compteur
				int i = 0;
				
				// Passer tous les noeuds enfants et v�rifier qu'ils sont bien 
				// des param�tres avec le type appropri�
				while (i < noeudCommande.getChildNodes().getLength() &&
					   bolNoeudValide == true)
				{
					// Faire la r�f�rence vers le noeud enfant courant
					Node objNoeudCourant = noeudCommande.getChildNodes().item(i);
					
					// Si le noeud courant n'est pas un param�tre, ou qu'il n'a
					// pas exactement 1 attribut, ou que le nom de cet attribut 
					// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
					// il y a une erreur dans la structure
					if (objNoeudCourant.getNodeName().equals("parametre") == false || 
						objNoeudCourant.getAttributes().getLength() != 1 ||
						objNoeudCourant.getAttributes().getNamedItem("type") == null ||
						(objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("NomUtilisateur") == false &&
						objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("MotDePasse") == false) ||
						objNoeudCourant.getChildNodes().getLength() != 1 ||
						objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false)
					{
						bolNoeudValide = false;
					}
					
					i++;
				}
				
				// Si les enfants du noeud courant sont tous valides alors la
				// commande est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est Deconnexion, alors il ne doit pas y avoir 
		// de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.Deconnexion))
		{
			// Si le nombre d'enfants du noeud de commande est de 0, alors
			// il n'y a vraiment aucun param�tres
			if (noeudCommande.getChildNodes().getLength() == 0)
			{
				bolCommandeValide = true;
			}
		}
		// Si le nom de la commande est ObtenirListeJoueurs, alors il ne doit 
		// pas y avoir de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.ObtenirListeJoueurs))
		{
			// Si le nombre d'enfants du noeud de commande est de 0, alors
			// il n'y a vraiment aucun param�tres
			if (noeudCommande.getChildNodes().getLength() == 0)
			{
				bolCommandeValide = true;
			}
		}
		// Si le nom de la commande est ObtenirListeSalles, alors il ne doit 
		// pas y avoir de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.ObtenirListeSalles))
		{
			// Si le nombre d'enfants du noeud de commande est de 0, alors
			// il n'y a vraiment aucun param�tres
			if (noeudCommande.getChildNodes().getLength() == 0)
			{
				bolCommandeValide = true;
			}
		}
		// Si le nom de la commande est EntrerSalle, alors il doit y avoir 2 param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.EntrerSalle))
		{
			// Si le nombre d'enfants du noeud de commande est de 2, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 2)
			{
				// D�clarer une variable qui va permettre de savoir si les 
				// noeuds enfants sont valides
				boolean bolNoeudValide = true;
				
				// D�claration d'un compteur
				int i = 0;
				
				// Passer tous les noeuds enfants et v�rifier qu'ils sont bien 
				// des param�tres avec le type appropri�
				while (i < noeudCommande.getChildNodes().getLength() &&
					   bolNoeudValide == true)
				{
					// Faire la r�f�rence vers le noeud enfant courant
					Node objNoeudCourant = noeudCommande.getChildNodes().item(i);
					
					// Si le noeud courant n'est pas un param�tre, ou qu'il n'a
					// pas exactement 1 attribut, ou que le nom de cet attribut 
					// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
					// il y a une erreur dans la structure (le deuxi�me param�tre 
					// peut avoir aucune valeur)
					if (objNoeudCourant.getNodeName().equals("parametre") == false || 
						objNoeudCourant.getAttributes().getLength() != 1 ||
						objNoeudCourant.getAttributes().getNamedItem("type") == null ||
						(objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("NomSalle") == false &&
						objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("MotDePasse") == false) ||
						(objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("NomSalle") &&
						objNoeudCourant.getChildNodes().getLength() != 1) ||
						(objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("MotDePasse") &&
						objNoeudCourant.getChildNodes().getLength() > 1) ||
						(objNoeudCourant.getChildNodes().getLength() == 1 &&
						objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false))
					{
						bolNoeudValide = false;
					}
					
					i++;
				}
				
				// Si les enfants du noeud courant sont tous valides alors la
				// commande est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est QuitterSalle, alors il ne doit pas y avoir 
		// de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.QuitterSalle))
		{
			// Si le nombre d'enfants du noeud de commande est de 0, alors
			// il n'y a vraiment aucun param�tres
			if (noeudCommande.getChildNodes().getLength() == 0)
			{
				bolCommandeValide = true;
			}
		}
		// Si le nom de la commande est ObtenirListeJoueursSalle, alors il ne 
		// doit pas y avoir de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.ObtenirListeJoueursSalle))
		{
			// Si le nombre d'enfants du noeud de commande est de 0, alors
			// il n'y a vraiment aucun param�tres
			if (noeudCommande.getChildNodes().getLength() == 0)
			{
				bolCommandeValide = true;
			}
		}
		// Si le nom de la commande est ObtenirListeTables, alors il ne doit 
		// pas y avoir de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.ObtenirListeTables))
		{
			// Si le nombre d'enfants du noeud de commande est de 1, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 1)
			{
				// D�clarer une variable qui va permettre de savoir si le 
				// noeud enfant est valide
				boolean bolNoeudValide = true;
		
				// Faire la r�f�rence vers le noeud enfant courant
				Node objNoeudCourant = noeudCommande.getChildNodes().item(0);
				
				// Si le noeud enfant n'est pas un param�tre, ou qu'il n'a
				// pas exactement 1 attribut, ou que le nom de cet attribut 
				// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
				// il y a une erreur dans la structure
				if (objNoeudCourant.getNodeName().equals("parametre") == false || 
					objNoeudCourant.getAttributes().getLength() != 1 ||
					objNoeudCourant.getAttributes().getNamedItem("type") == null ||
					objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("Filtre") == false ||
					objNoeudCourant.getChildNodes().getLength() != 1 ||
					objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false)
				{
					bolNoeudValide = false;
				}
				
				// Si l'enfant du noeud courant est valide alors la commande 
				// est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est CreerTable, alors il doit y avoir 1 param�tre
		else if (noeudCommande.getAttribute("nom").equals(Commande.CreerTable))
		{
			// Si le nombre d'enfants du noeud de commande est de 1, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 1)
			{
				// D�clarer une variable qui va permettre de savoir si le 
				// noeud enfant est valide
				boolean bolNoeudValide = true;
		
				// Faire la r�f�rence vers le noeud enfant courant
				Node objNoeudCourant = noeudCommande.getChildNodes().item(0);
				
				// Si le noeud enfant n'est pas un param�tre, ou qu'il n'a
				// pas exactement 1 attribut, ou que le nom de cet attribut 
				// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
				// il y a une erreur dans la structure
				if (objNoeudCourant.getNodeName().equals("parametre") == false || 
					objNoeudCourant.getAttributes().getLength() != 1 ||
					objNoeudCourant.getAttributes().getNamedItem("type") == null ||
					objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("TempsPartie") == false ||
					objNoeudCourant.getChildNodes().getLength() != 1 ||
					objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false ||
					UtilitaireNombres.isPositiveNumber(objNoeudCourant.getChildNodes().item(0).getNodeValue()) == false)
				{
					bolNoeudValide = false;
				}
				
				// Si l'enfant du noeud courant est valide alors la commande 
				// est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est EntrerTable, alors il doit y avoir 1 param�tre
		else if (noeudCommande.getAttribute("nom").equals(Commande.EntrerTable))
		{
			// Si le nombre d'enfants du noeud de commande est de 1, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 1)
			{
				// D�clarer une variable qui va permettre de savoir si le 
				// noeud enfant est valide
				boolean bolNoeudValide = true;
		
				// Faire la r�f�rence vers le noeud enfant courant
				Node objNoeudCourant = noeudCommande.getChildNodes().item(0);
				
				// Si le noeud enfant n'est pas un param�tre, ou qu'il n'a
				// pas exactement 1 attribut, ou que le nom de cet attribut 
				// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
				// il y a une erreur dans la structure
				if (objNoeudCourant.getNodeName().equals("parametre") == false || 
					objNoeudCourant.getAttributes().getLength() != 1 ||
					objNoeudCourant.getAttributes().getNamedItem("type") == null ||
					objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("NoTable") == false ||
					objNoeudCourant.getChildNodes().getLength() != 1 ||
					objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false ||
					UtilitaireNombres.isPositiveNumber(objNoeudCourant.getChildNodes().item(0).getNodeValue()) == false)
				{
					bolNoeudValide = false;
				}
				
				// Si l'enfant du noeud courant est valide alors la commande 
				// est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est QuitterTable, alors il ne doit pas y avoir 
		// de param�tres
		else if (noeudCommande.getAttribute("nom").equals(Commande.QuitterTable))
		{
			// Si le nombre d'enfants du noeud de commande est de 0, alors
			// il n'y a vraiment aucun param�tres
			if (noeudCommande.getChildNodes().getLength() == 0)
			{
				bolCommandeValide = true;
			}
		}
		// Si le nom de la commande est DemarrerPartie, alors il doit y avoir 1 param�tre
		else if (noeudCommande.getAttribute("nom").equals(Commande.DemarrerPartie))
		{
			// Si le nombre d'enfants du noeud de commande est de 1, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 1)
			{
				// D�clarer une variable qui va permettre de savoir si le 
				// noeud enfant est valide
				boolean bolNoeudValide = true;
		
				// Faire la r�f�rence vers le noeud enfant courant
				Node objNoeudCourant = noeudCommande.getChildNodes().item(0);
				
				// Si le noeud enfant n'est pas un param�tre, ou qu'il n'a
				// pas exactement 1 attribut, ou que le nom de cet attribut 
				// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
				// il y a une erreur dans la structure
				if (objNoeudCourant.getNodeName().equals("parametre") == false || 
					objNoeudCourant.getAttributes().getLength() != 1 ||
					objNoeudCourant.getAttributes().getNamedItem("type") == null ||
					objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("IdPersonnage") == false ||
					objNoeudCourant.getChildNodes().getLength() != 1 ||
					objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false ||
					UtilitaireNombres.isPositiveNumber(objNoeudCourant.getChildNodes().item(0).getNodeValue()) == false)
				{
					bolNoeudValide = false;
				}
				
				// Si l'enfant du noeud courant est valide alors la commande 
				// est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est DeplacerPersonnage, alors il doit y avoir 
		// 1 param�tre position contenant les coordonn�es x, y
		else if (noeudCommande.getAttribute("nom").equals(Commande.DeplacerPersonnage))
		{
			// Si le nombre d'enfants du noeud de commande est de 1, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 1)
			{
				// D�clarer une variable qui va permettre de savoir si les 
				// noeuds enfants sont valides
				boolean bolNoeudValide = true;
		
				// Faire la r�f�rence vers le noeud enfant courant
				Node objNoeudCourant = noeudCommande.getChildNodes().item(0);
				
				// Si le noeud enfant n'est pas un param�tre, ou qu'il n'a
				// pas exactement 1 attribut, ou que le nom de cet attribut 
				// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
				// il y a une erreur dans la structure
				if (objNoeudCourant.getNodeName().equals("parametre") == false || 
					objNoeudCourant.getAttributes().getLength() != 1 ||
					objNoeudCourant.getAttributes().getNamedItem("type") == null ||
					objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("NouvellePosition") == false ||
					objNoeudCourant.getChildNodes().getLength() != 1 ||
					objNoeudCourant.getChildNodes().item(0).getNodeName().equals("position") == false ||
					objNoeudCourant.getChildNodes().item(0).getAttributes().getLength() != 2 ||
					objNoeudCourant.getChildNodes().item(0).getAttributes().getNamedItem("x") == null ||
					objNoeudCourant.getChildNodes().item(0).getAttributes().getNamedItem("y") == null ||
					UtilitaireNombres.isPositiveNumber(objNoeudCourant.getChildNodes().item(0).getAttributes().getNamedItem("x").getNodeValue()) == false ||
					UtilitaireNombres.isPositiveNumber(objNoeudCourant.getChildNodes().item(0).getAttributes().getNamedItem("y").getNodeValue()) == false)
				{
					bolNoeudValide = false;
				}
				
				// Si l'enfant du noeud courant est valide alors la commande 
				// est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		// Si le nom de la commande est RepondreQuestion, alors il doit y avoir 1 param�tre
		else if (noeudCommande.getAttribute("nom").equals(Commande.RepondreQuestion))
		{
			// Si le nombre d'enfants du noeud de commande est de 1, alors
			// le nombre de param�tres est correct et on peut continuer
			if (noeudCommande.getChildNodes().getLength() == 1)
			{
				// D�clarer une variable qui va permettre de savoir si le 
				// noeud enfant est valide
				boolean bolNoeudValide = true;
		
				// Faire la r�f�rence vers le noeud enfant courant
				Node objNoeudCourant = noeudCommande.getChildNodes().item(0);
				
				// Si le noeud enfant n'est pas un param�tre, ou qu'il n'a
				// pas exactement 1 attribut, ou que le nom de cet attribut 
				// n'est pas type, ou que le noeud n'a pas de valeurs, alors 
				// il y a une erreur dans la structure
				if (objNoeudCourant.getNodeName().equals("parametre") == false || 
					objNoeudCourant.getAttributes().getLength() != 1 ||
					objNoeudCourant.getAttributes().getNamedItem("type") == null ||
					objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals("Reponse") == false ||
					objNoeudCourant.getChildNodes().getLength() != 1 ||
					objNoeudCourant.getChildNodes().item(0).getNodeName().equals("#text") == false)
				{
					bolNoeudValide = false;
				}
				
				// Si l'enfant du noeud courant est valide alors la commande 
				// est valide
				bolCommandeValide = bolNoeudValide;
			}
		}
		
		return bolCommandeValide;
	}
		
	/**
	 * Cette fonction permet de retourner le noeud correspondant � la valeur
	 * du param�tre dont le nom est pass� en param�tres. On recherche d'abord
	 * le noeud parametre parmi les noeuds enfants du noeud de commande pass�
	 * en param�tres puis une fois qu'on a trouv� le bon, on retourne son noeud
	 * enfant. On suppose que la structure est conforme et que la valeur du 
	 * param�tre est un seul noeud (soit un noeud texte ou une liste).
	 * 
	 * @param Element noeudCommande : le noeud de comande dans lequel chercher 
	 * 								  le bon param�tre
	 * @param String nomParametre : le nom du param�tre � chercher
	 * @return Node : le noeud contenant la valeur du param�tre (soit un noeud 
	 * 				  texte ou un noeud contenant une liste)
	 */
	private Node obtenirValeurParametre(Element noeudCommande, String nomParametre)
	{
		// D�claration d'une variable qui va contenir le noeud repr�sentant
		// la valeur du param�tre
		Node objValeurParametre = null;
		
		// D�claration d'un compteur
		int i = 0;
		
		// D�claration d'une variable qui va nous permettre de savoir si on a 
		// trouv� la valeur du param�tre recherch�
		boolean bolTrouve = false;
		
		// Passer tous les noeuds enfants (param�tres) du noeud de commande et 
		// boucler tant qu'on n'a pas trouver le bon param�tre
		while (i < noeudCommande.getChildNodes().getLength() && bolTrouve == false)
		{
			// Garder une r�f�rence vers le noeud courant
			Node objNoeudCourant = noeudCommande.getChildNodes().item(i);
			
			// Si le noeud courant a l'attribut type dont la valeur est pass�e
			// en param�tres, alors on l'a trouv�, on va garder une r�f�rence 
			// vers la valeur du noeud courant
			if (objNoeudCourant.getAttributes().getNamedItem("type").getNodeValue().equals(nomParametre))
			{
				bolTrouve = true;
				
				// Garder la r�f�rence vers le noeud enfant (il est le seul et 
				// il est soit un noeud texte ou un noeud repr�sentant une liste)
				objValeurParametre = objNoeudCourant.getChildNodes().item(0);
			}
			
			i++;
		}
		
		return objValeurParametre;
	}
	
	/**
	 * Cette m�thode permet de g�n�rer un nouveau num�ro de commande � retourner
	 * en r�ponse au client.
	 */
	public void genererNumeroReponse()
	{
	    // Modifier le num�ro de commande � retourner au client
	    intNumeroCommandeReponse = obtenirNumeroCommande();
	}
		
	/**
	 * Cette fonction permet de retourner le num�ro de la commande courante et 
	 * d'augmenter le compteur de commandes. Le num�ro de commande permet au 
	 * client de savoir quel �v�nement est arriv� avant quel autre.
	 * 
	 * @return int : le num�ro de la commande
	 */
	public int obtenirNumeroCommande()
	{
		// D�claration d'une variable qui va contenir le num�ro de la commande
		// � retourner
		int intNumeroCommande = intCompteurCommande;
		
		// Incr�menter le compteur de commandes
		intCompteurCommande++;
		
		// Si le compteur de commandes est maintenant plus grand que la plus 
		// grande valeur possible, alors on r�initialise le compteur � 0
		if (intCompteurCommande > MAX_COMPTEUR)
		{
			intCompteurCommande = 0;
		}
		
		return intNumeroCommande;
	}
		
	/**
	 * Cette m�thode permet d'envoyer un �v�nement ping au joueur courant 
	 * pour savoir s'il est toujours connect� au serveur de jeu.
	 * 
	 * @param : int numeroPing : le num�ro du ping, c'est le num�ro qui 
	 * 							 va servir � identifier le ping
	 */
	public void envoyerEvenementPing(int numeroPing)
	{
		try
		{
			// Envoyer le message ping au joueur
			envoyerMessage("<ping numero=\"" + numeroPing + "\"/>");
		}
		catch (IOException e)
		{
			System.out.println("L'evenement ping n'a pas pu etre envoyee");
		}
	}
	
	/**
	 * Cette m�thode permet d'arr�ter le thread et de fermer le socket du 
	 * client. Si le joueur �tait connect� � une table, une salle ou au serveur
	 * de jeu, alors il sera compl�tement d�connect�.
	 */
	public void arreterProtocoleJoueur()
	{
		try
		{
			// On tente de fermer le canal de r�ception. Cela va provoquer 
			// une erreur dans le thread et le joueur va �tre d�connect� et 
			// le thread va arr�ter
			objCanalReception.close();
		}
		catch (IOException ioe) {}
		
		try
		{
			// On tente de fermer le socket liant le client au serveur. Cela
			// va provoquer une erreur dans le thread et le joueur va �tre
			// d�connect� et le thread va arr�ter
			objSocketJoueur.close();						
		}
		catch (IOException ioe) {}
	}
	
	/**
	 * Cette fonction permet de retourner l'adresse IP du joueur courant. 
	 * 
	 * @return String : L'adresse IP du joueur courant
	 */
	public String obtenirAdresseIP()
	{
		// Retourner l'adresse IP du joueur
		return objSocketJoueur.getInetAddress().getHostAddress();
	}
	
	/**
	 * Cette fonction permet de retourner le port du joueur courant. 
	 * 
	 * @return String : Le port du joueur courant
	 */
	public String obtenirPort()
	{
		// Retourner le port du joueur
		return Integer.toString(objSocketJoueur.getPort());
	}
	
	/**
	 * Cette m�thode permet de d�finir la nouvelle r�f�rence vers un joueur 
	 * humain. 
	 * 
	 * @param JoueurHumain joueur : Le joueur humain auquel faire la r�f�rence
	 */
	public void definirJoueur(JoueurHumain joueur)
	{
		// Faire la r�f�rence vers le joueur humain
		objJoueurHumain = joueur;
	}
}