import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Android
 *
 */
public class ServerInterfaceConnection implements Runnable, ServerConnector {

	private AppletCommander serverInterface;
	private Socket interfaceSocket;
	private OutputStream objOutput;
	private InputStream objInput;
	private String host;
	private int port;
	// to indicate that we don't need a connection with server
	// we prefer to be disconnected
	private boolean disconnectedFlag;
	private boolean runApplet;
	//public static enum Commands {STOP,STATE};

	public ServerInterfaceConnection(AppletCommander applet, String host, String port)
	{
		this.serverInterface = applet; 
		this.host = host;
		this.port = Integer.parseInt(port);
		this.disconnectedFlag = false;
		this.runApplet = true;          
	}

	public void run(){		

		while(runApplet){
			if(interfaceSocket == null && !disconnectedFlag)
			{
				connectToServer();
			}else if(interfaceSocket != null && interfaceSocket.isConnected()){

				try {
					serverInterface.newMessage("We are connected... listen for messages ");

					// Cr�er le canal qui permet de recevoir des donn�es sur le canal
					// de communication entre le client et le serveur
					DataInputStream in = new DataInputStream(new BufferedInputStream(interfaceSocket.getInputStream()));

					// Cette objet va contenir le message envoy� par le client au serveur
					StringBuffer strMessageRecu = new StringBuffer();

					// Cr�ation d'un tableau de 1024 bytes qui va servir � lire sur le canal
					byte[] byttBuffer = new byte[4096];

					// Boucler et obtenir les messages du client (joueur), puis les
					// traiter tant que le client n'a pas d�cid� de quitter (ou que la
					// connexion ne s'est pas d�connect�e)
					while (!disconnectedFlag) {

						// D�claration d'une variable qui va servir de marqueur
						// pour savoir o� on en est rendu dans la lecture
						int intMarqueur = 0;

						// D�claration d'une variable qui va contenir le nombre de
						// bytes r�ellement lus dans le canal
						int intBytesLus = in.read(byttBuffer);

						// Si le nombre de bytes lus est -1, alors c'est que le
						// stream a �t� ferm�, il faut donc terminer le thread
						if (intBytesLus == -1) {
							serverInterface.newMessage("Une erreur est survenue: nombre d'octets lus = -1");
							cleanSocket();
						}

						if (interfaceSocket.isClosed()) {
							serverInterface.newMessage("Une erreur est survenue: sur socket.....");
							cleanSocket();						
						} 

						// Passer tous les bytes lus dans le canal de r�ception et
						// d�couper le message en cha�ne de commandes selon le byte
						// 0 marquant la fin d'une commande
						for (int i = 0; i < intBytesLus; i++) {
							// Si le byte courant est le byte de fin de message (EOM)
							// alors c'est qu'une commande vient de finir, on va donc
							// traiter la commande re�ue
							if (byttBuffer[i] == (byte)0) {
								// Cr�er une cha�ne temporaire qui va garder la cha�ne
								// de caract�res lue jusqu'� maintenant
								String strChaineAccumulee = new String(byttBuffer,
										intMarqueur, i - intMarqueur);

								// Ajouter la cha�ne courante � la cha�ne de commande
								strMessageRecu.append(strChaineAccumulee);

								treatServerMessage(strMessageRecu.toString());

								// if we have problems in transmition
								//if(i == 0) this.setBolStopThread(true);

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
						if (intMarqueur < intBytesLus) {
							// On garde la partie du message non termin� dans la
							// cha�ne qui va contenir le message � traiter lorsqu'on
							// recevra le EOM
							strMessageRecu.append(new String(byttBuffer, intMarqueur, intBytesLus - intMarqueur));
						}

					}
				} catch (IOException ioe) {
					serverInterface.newMessage(ioe.getMessage() + " IOException in input");
					cleanSocket();
				} catch (Exception e) {
					serverInterface.newMessage(e.getMessage());
					cleanSocket();
				} finally {
					serverInterface.newMessage("We are disconnected");
					cleanSocket();
				}      

			}
		}

	}// end method

	private void treatServerMessage(String command){

		serverInterface.newMessage("Server answered :  " + command);
		if( command.equalsIgnoreCase("Will close..."))
		{
			serverInterface.newMessage("Server will close...");
			cleanSocket();
		}

	}

	/* (non-Javadoc)
	 * @see ServerConnector#sendCommand(int)
	 */
	@Override
	public void sendCommand(String command) {

		if(interfaceSocket != null && interfaceSocket.isConnected() && !interfaceSocket.isClosed()){

			try {
				DataOutputStream outS = new DataOutputStream(new BufferedOutputStream(interfaceSocket.getOutputStream()));

				byte[] bytes = command.getBytes("UTF8");
				// �crire le message sur le canal d'envoi au client
				outS.write(bytes);

				// �crire le byte 0 sur le canal d'envoi pour signifier la fin du message
				outS.write((byte)0);

				// Envoyer le message sur le canal d'envoi
				outS.flush();

				serverInterface.newMessage("We are sending command :  " + command);				

			} catch (IOException e) {

				serverInterface.newMessage(" IO exception  :  " + e.getMessage());
			}
		}else{
			serverInterface.newMessage("Command -  " + command + " not sent. We try to get connection.");
			connectToServer();
		}

	}// end method



	/* (non-Javadoc)
	 *
	 */
	public void connectToServer(){
		disconnectedFlag = false;		
		try {
			interfaceSocket = new Socket(InetAddress.getByName(this.host), this.port);

		} catch (UnknownHostException e) {

			serverInterface.newMessage("Error : UnknownHostException " + e.getMessage());
			interfaceSocket = null;

		}catch (ConnectException e)
		{			
			serverInterface.newMessage("Error : ConnectException " + e.getMessage());
			interfaceSocket = null;
		} 
		catch (IOException e) {

			serverInterface.newMessage("Error : IOException " + e.getMessage());
			interfaceSocket = null;			
		}		
	}


	/* (non-Javadoc)
	 *
	 */
	public void disconnectFromServer() {

		disconnectedFlag = true;
		try {
			if(interfaceSocket != null && interfaceSocket.isConnected()){
				interfaceSocket.close();
				serverInterface.newMessage("Connection with server is off. Socket is disconnected.");			
			}
			else
				serverInterface.newMessage("Connection with server is off. Socket is not connected.");
		} catch (IOException e) {

			serverInterface.newMessage(e.getMessage());
		}

	}

	protected void cleanSocket() {
		if (interfaceSocket != null) {
			try {
				if (objOutput != null) {
					objOutput.close();
					objOutput = null;
				}
			} catch (Exception e) { 
				serverInterface.newMessage("Error in cleaning socket.");
			} 
			try {
				if (objInput != null) {
					objInput.close();
					objInput = null;
				}
			} catch (Exception e) {
				serverInterface.newMessage("Error in cleaning socket.");
			} 
			try {
				if (interfaceSocket != null) {
					interfaceSocket.close();
					interfaceSocket = null;
				}
			} catch (Exception e) {
				serverInterface.newMessage("Error in cleaning socket.");
			} 
		}
	}

	/**
	 * Check if socket connection with server is valid
	 * @return
	 */
	public boolean connectionCheck() {
		
		if(interfaceSocket == null)
			return false;
		else
		    return interfaceSocket.isConnected();
	}

}
