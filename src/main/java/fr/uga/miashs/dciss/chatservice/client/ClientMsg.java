/*
 * Copyright (c) 2024.  Jerome David. Univ. Grenoble Alpes.
 * This file is part of DcissChatService.
 *
 * DcissChatService is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * DcissChatService is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.uga.miashs.dciss.chatservice.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.uga.miashs.dciss.chatservice.common.Packet;

/**
 * Manages the connection to a ServerMsg. Method startSession() is used to
 * establish the connection. Then messages can be send by a call to sendPacket.
 * The reception is done asynchronously (internally by the method receiveLoop())
 * and the reception of a message is notified to MessagesListeners. To register
 * a MessageListener, the method addMessageListener has to be called. Session
 * are closed thanks to the method closeSession().
 */
public class ClientMsg {

	private String serverAddress;
	private int serverPort;

	private Socket s;
	private DataOutputStream dos;
	private DataInputStream dis;

	private int identifier;

	private List<MessageListener> mListeners;
	private List<ConnectionListener> cListeners;

	/**
	 * Create a client with an existing id, that will connect to the server at the
	 * given address and port
	 * 
	 * @param id      The client id
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(int id, String address, int port) {
		if (id < 0)
			throw new IllegalArgumentException("id must not be less than 0");
		if (port <= 0)
			throw new IllegalArgumentException("Server port must be greater than 0");
		serverAddress = address;
		serverPort = port;
		identifier = id;
		mListeners = new ArrayList<>();
		cListeners = new ArrayList<>();
	}

	/**
	 * Create a client without id, the server will provide an id during the the
	 * session start
	 * 
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(String address, int port) {
		this(0, address, port);
	}

	/**
	 * Register a MessageListener to the client. It will be notified each time a
	 * message is received.
	 * 
	 * @param l
	 */
	public void addMessageListener(MessageListener l) {
		if (l != null)
			mListeners.add(l);
	}
	protected void notifyMessageListeners(Packet p) {
		mListeners.forEach(x -> x.messageReceived(p));
	}
	
	/**
	 * Register a ConnectionListener to the client. It will be notified if the connection  start or ends.
	 * 
	 * @param l
	 */
	public void addConnectionListener(ConnectionListener l) {
		if (l != null)
			cListeners.add(l);
	}
	protected void notifyConnectionListeners(boolean active) {
		cListeners.forEach(x -> x.connectionEvent(active));
	}


	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Method to be called to establish the connection.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */

	 // không nên tạo lại socket nếu socket hiện tại đã được kết nối
	public void startSession() throws UnknownHostException {
		if (s == null || s.isClosed()) {
			try {
				System.out.println("Attempting to connect to server at " + serverAddress + ":" + serverPort + "...");
				s = new Socket(serverAddress, serverPort);
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
				dos.writeInt(identifier);
				dos.flush();
				if (identifier == 0) {
					identifier = dis.readInt();
				}
				// Start the receive loop in a new thread
				new Thread(() -> receiveLoop()).start();
				notifyConnectionListeners(true);
				System.out.println("Connection established successfully.");
			} catch (IOException e) {
				System.err.println("Failed to connect to server at " + serverAddress + ":" + serverPort);
				e.printStackTrace();
				closeSession();
				throw new IllegalStateException("Failed to establish connection to the server.");
			}
		} else {
			System.out.println("Socket is already connected.");
		}
	}

	/**
	 * Send a packet to the specified destination (etiher a userId or groupId)
	 * 
	 * @param destId the destinatiion id
	 * @param data   the data to be sent
	 */

	 // không nên gọi lại startSession nếu socket đã được kết nối
	public void sendPacket(int destId, byte[] data) throws IOException {
		if (s == null || s.isClosed()) {
			throw new IllegalStateException("Socket is not connected. Ensure the connection is established.");
		}
		if (dos == null) {
			throw new IllegalStateException("DataOutputStream is not initialized. Ensure the connection is established.");
		}
		synchronized (dos) {
			dos.writeInt(destId);
			dos.writeInt(data.length);
			dos.write(data);
			dos.flush();
		}
	}


	/**
	 * Start the receive loop. Has to be called only once.
	 */


	private void receiveLoop() {
		try {
			while (s != null && !s.isClosed()) {
				int sender = dis.readInt();
				int dest = dis.readInt();
				int length = dis.readInt();
				byte[] data = new byte[length];
				dis.readFully(data);
				notifyMessageListeners(new Packet(sender, dest, data));
			}
		} catch (IOException e) {
			System.err.println("Error in receive loop: " + e.getMessage());
			closeSession();
		}
	}

	public void closeSession() {
		try {
			if (s != null)
				s.close();
		} catch (IOException e) {
		}
		s = null;
		notifyConnectionListeners(false);
	}


	/////////////////////// Envoyer les informations de connexion au serveur

	/**
	 * Se connecte au serveur avec un nom d'utilisateur et un mot de passe.
	 * 
	 * @param serverAddress L'adresse du serveur
	 * @param serverPort    Le port du serveur
	 * @param username      Le nom d'utilisateur
	 * @param password      Le mot de passe
	 * @return true si la connexion est réussie, sinon false
	 */
	public boolean connectToServer(String serverAddress, int serverPort, String username, String password) throws IOException {
		ensureConnection(); // Assurer que la connexion est établie
		try {
			// Établir une connexion avec le serveur
			s = new Socket(serverAddress, serverPort);
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());

			// Envoyer les informations de connexion
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();

			// Recevoir la réponse du serveur
			int userId = dis.readInt();
			if (userId == -1) {
				System.out.println("Échec de la connexion ! Vérifiez le nom d'utilisateur ou le mot de passe.");
				s.close();
				return false; // Connexion échouée
			} else {
				System.out.println("Connexion réussie ! ID utilisateur : " + userId);
				// Continuer le traitement après une connexion réussie
				startMessaging();
				return true; // Connexion réussie
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false; // Erreur de connexion
		}
	}

	/**
	 * Vérifie si les informations d'authentification sont valides.
	 * 
	 * @param username Le nom d'utilisateur
	 * @param password Le mot de passe
	 * @return true si les informations sont valides, sinon false
	 */
	public boolean isAuthenticated(String username, String password) {
		// Implémenter la logique d'authentification ici
		// Retourne true si le nom d'utilisateur et le mot de passe sont valides
		return "validUser".equals(username) && "validPass".equals(password);
	}

	public boolean login(String username, String password) {
		try {
			// Đảm bảo kết nối được thiết lập
			startSession();
	
			// Gửi yêu cầu đăng nhập
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeByte(7); // Type de paquet 7 pour la connexion
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();
	
			sendPacket(0, bos.toByteArray()); // Gửi gói tin đăng nhập đến server
	
			// Nhận phản hồi từ server
			byte[] response = receivePacketFromServer();
			String responseStr = new String(response);
			return responseStr.equals("SUCCESS");
		} catch (Exception e) {
			System.err.println("Login failed due to an error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Enregistre un nouvel utilisateur sur le serveur.
	 * 
	 * @param username Le nom d'utilisateur
	 * @param password Le mot de passe
	 * @return true si l'enregistrement est réussi, sinon false
	 */

	 public boolean register(String username, String password) {
		try {
			if (s == null || s.isClosed()) {
				startSession();
			}
	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeByte(8); // Gói tin đăng ký
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();
	
			sendPacket(0, bos.toByteArray());
	
			// Đợi phản hồi từ server
			Packet responsePacket = waitForResponse();
			if (responsePacket != null) {
				String response = new String(responsePacket.data);
				return response.equals("SUCCESS");
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Phương thức hỗ trợ đợi phản hồi
	private Packet waitForResponse() {
		final AtomicBoolean received = new AtomicBoolean(false);
		final Packet[] response = new Packet[1];
		
		MessageListener tempListener = p -> {
			if (p.destId == this.identifier) { // Kiểm tra gói tin gửi đến đúng client
				response[0] = p;
				received.set(true);
			}
		};
		
		this.addMessageListener(tempListener);
		
		try {
			// Đợi tối đa 5 giây
			int count = 0;
			while (!received.get() && count < 50) {
				Thread.sleep(100);
				count++;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			this.mListeners.remove(tempListener);
		}
		
		return response[0];
	}

	public boolean logout() {
		try {
			// Assurer que la connexion est établie
			if (s == null || s.isClosed() || !s.isConnected()) {
				startSession(); // Établir la connexion si elle n'existe pas
			}
	
			// Envoyer une demande de déconnexion
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeByte(9); // Type de paquet 9 pour la déconnexion
			dos.flush();
	
			sendPacket(0, bos.toByteArray()); // Envoyer le paquet de déconnexion au serveur
	
			// Recevoir la réponse du serveur
			byte[] response = receivePacketFromServer();
			String responseStr = new String(response);
			return responseStr.equals("SUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Reçoit un paquet du serveur.
	 * 
	 * @return le tableau d'octets reçu
	 * @throws IOException si une erreur d'E/S se produit
	 */
	private byte[] receivePacketFromServer() throws IOException {
		int length = dis.readInt();
		byte[] data = new byte[length];
		dis.readFully(data);
		return data;
	}

	public void ensureConnection() throws IOException {
		if (s == null || s.isClosed() || !s.isConnected()) {
			startSession(); // Établir la connexion
		}
	}

	/////////////////////////////

	

	private void startMessaging() {
		// Logique pour envoyer et recevoir des messages après une connexion réussie
		System.out.println("Commencer à envoyer et recevoir des messages...");
	}

	// public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
	// 	ClientMsg c = new ClientMsg("localhost", 1666);

	// 	// add a dummy listener that print the content of message as a string
	// 	c.addMessageListener(p -> System.out.println(p.srcId + " says to " + p.destId + ": " + new String(p.data)));
		
	// 	// add a connection listener that exit application when connection closed
	// 	c.addConnectionListener(active ->  {if (!active) System.exit(0);});

	// 	c.startSession();
	// 	System.out.println("Vous êtes : " + c.getIdentifier());

	// 	// Thread.sleep(5000);

	// 	// l'utilisateur avec id 4 crée un grp avec 1 et 3 dedans (et lui meme)
	// 	if (c.getIdentifier() == 4) {
	// 		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// 		DataOutputStream dos = new DataOutputStream(bos);

	// 		// byte 1 : create group on server
	// 		dos.writeByte(1);

	// 		// nb members
	// 		dos.writeInt(2);
	// 		// list members
	// 		dos.writeInt(1);
	// 		dos.writeInt(3);
	// 		dos.flush();

	// 		c.sendPacket(0, bos.toByteArray());

	// 	}
		
		

	// 	Scanner sc = new Scanner(System.in);
	// 	String lu = null;
	// 	while (!"\\quit".equals(lu)) {
	// 		try {
	// 			System.out.println("A qui voulez vous écrire ? ");
	// 			int dest = Integer.parseInt(sc.nextLine());

	// 			System.out.println("Votre message ? ");
	// 			lu = sc.nextLine();
	// 			c.sendPacket(dest, lu.getBytes());
	// 		} catch (InputMismatchException | NumberFormatException e) {
	// 			System.out.println("Mauvais format");
	// 		}

	// 	}

	// 	/*
	// 	 * int id =1+(c.getIdentifier()-1) % 2; System.out.println("send to "+id);
	// 	 * c.sendPacket(id, "bonjour".getBytes());
	// 	 * 
	// 	 * 
	// 	 * Thread.sleep(10000);
	// 	 */

	// 	c.closeSession();

	// }

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		ClientMsg client = new ClientMsg("localhost", 1666);
	
		Scanner scanner = new Scanner(System.in);
	
		System.out.println("1. Register");
		System.out.println("2. Login");
		System.out.println("3. Logout");
		System.out.print("Choose an option: ");
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume newline
	
		switch (choice) {
			case 1:
				System.out.print("Enter username: ");
				String regUsername = scanner.nextLine();
				System.out.print("Enter password: ");
				String regPassword = scanner.nextLine();
				if (client.register(regUsername, regPassword)) {
					System.out.println("Registration successful!");
				} else {
					System.out.println("Registration failed.");
				}
				break;
	
			case 2:
				System.out.print("Enter username: ");
				String loginUsername = scanner.nextLine();
				System.out.print("Enter password: ");
				String loginPassword = scanner.nextLine();
				if (client.login(loginUsername, loginPassword)) {
					System.out.println("Login successful!");
				} else {
					System.out.println("Login failed.");
				}
				break;
	
			case 3:
				if (client.logout()) {
					System.out.println("Logout successful!");
				} else {
					System.out.println("Logout failed.");
				}
				break;
	
			default:
				System.out.println("Invalid option.");
		}
	
		scanner.close();
		client.closeSession();
	}

}
