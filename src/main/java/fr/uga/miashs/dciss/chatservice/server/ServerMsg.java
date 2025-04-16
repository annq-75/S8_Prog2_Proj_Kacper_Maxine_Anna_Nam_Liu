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

package fr.uga.miashs.dciss.chatservice.server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.util.*;

public class ServerMsg {
	
	private final static Logger LOG = Logger.getLogger(ServerMsg.class.getName());
	public final static int SERVER_CLIENTID = 0;

	private transient ServerSocket serverSock;
	private transient boolean started;
	private transient ExecutorService executor;
	private transient ServerPacketProcessor sp;
	
	// maps pour associer les id aux users et groupes
	private Map<Integer, UserMsg> users;
	private Map<Integer, GroupMsg> groups;
	
	
	
	// séquences pour générer les identifiant d'utilisateurs et de groupe
	private AtomicInteger nextUserId;
	private AtomicInteger nextGroupId;

	public ServerMsg(int port) throws IOException {
		serverSock = new ServerSocket(port);
		started = false;
		users = new ConcurrentHashMap<>();
		groups = new ConcurrentHashMap<>(); 
		nextUserId = new AtomicInteger(1);
		nextGroupId = new AtomicInteger(-1);
		sp = new ServerPacketProcessor(this);
		executor = Executors.newCachedThreadPool();
	}
	
	public GroupMsg createGroup(int ownerId) {
		UserMsg owner = users.get(ownerId);
		if (owner==null) throw new ServerException("User with id="+ownerId+" unknown. Group creation failed.");
		int id = nextGroupId.getAndDecrement();
		GroupMsg res = new GroupMsg(id,owner);
		groups.put(id, res);
		LOG.info("Group "+res.getId()+" created");
		return res;
	}
	
	public boolean removeGroup(int groupId) {
		GroupMsg g =groups.remove(groupId);
		if (g==null) return false;
		g.beforeDelete();
		return true;
	}
	
	public boolean removeUser(int userId) {
		UserMsg u =users.remove(userId);
		if (u==null) return false;
		u.beforeDelete();
		return true;
	}
	///// Méthodes de gestion des utilisateurs /////

		// Méthode pour connecter un utilisateur
	public boolean loginUser(int userId, String username, String password) {
		if (users.containsKey(userId)) {
			LOG.warning("User with ID " + userId + " is already logged in.");
			return false;
		}

		if (DatabaseManager.validateUser(username, password)) {
			UserMsg newUser = new UserMsg(userId, this);
			users.put(userId, newUser);
			LOG.info("User " + username + " logged in successfully with ID " + userId);
			return true;
		} else {
			LOG.warning("Invalid login credentials for username: " + username);
			return false;
		}
	}

	// Méthode pour enregistrer un utilisateur
	public boolean registerUser(int userId, String username, String password) {
		if (DatabaseManager.userExists(username)) {
			LOG.warning("Registration failed: Username " + username + " already exists.");
			return false;
		}

		boolean success = DatabaseManager.addUser(username, password);
		if (success) {
			LOG.info("User " + username + " registered successfully.");
			return true;
		} else {
			LOG.warning("Registration failed for username: " + username);
			return false;
		}
	}

	// Méthode pour déconnecter un utilisateur
	public boolean logoutUser(int userId) {
		UserMsg user = users.remove(userId);
		if (user != null) {
			user.close();
			LOG.info("User with ID " + userId + " logged out successfully.");
			return true;
		} else {
			LOG.warning("Logout failed: User with ID " + userId + " not found.");
			return false;
		}
	}

	public void handleClientConnections(Socket clientSocket) {
    try {
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

        boolean connected = true;

        while (connected) {
            dos.writeUTF("Welcome to ChatService");
            String credentials = dis.readUTF(); // format: login:username:password
            String[] parts = credentials.split(":");

            if (parts.length != 3) {
                dos.writeUTF("invalid_format");
				continue; // Allow the user to retry
            }

            String action = parts[0];
            String username = parts[1];
            String password = parts[2];

            if (action.equals("login")) {
                if (DatabaseManager.validateUser(username, password)) {
                    dos.writeUTF("login_success");
                    System.out.println("[SERVER] User logged in: " + username);
					// Call further processing after successful login (app communication)
                    connected = false; // loop exit after successful login
                } else {
                    dos.writeUTF("login_failed");
                    dos.writeUTF("Press 'y' to retry or any other key to exit.");
                    String retry = dis.readUTF();
                    if (!retry.equalsIgnoreCase("y")) {
                        dos.writeUTF("Connection closed.");
                        connected = false;
                        clientSocket.close();
                    }
                }

            } else if (action.equals("register")) {
                if (!DatabaseManager.userExists(username)) {
                    boolean registered = DatabaseManager.addUser(username, password);
                    dos.writeUTF(registered ? "register_success" : "register_failed");
                    dos.writeUTF("You can now login.");
					// Allow the user to log in after registration without closing the socket
                } else {
                    dos.writeUTF("register_user_exists");
                    dos.writeUTF("Press 'y' to retry or any other key to exit.");
                    String retry = dis.readUTF();
                    if (!retry.equalsIgnoreCase("y")) {
                        dos.writeUTF("Connexion fermée.");
                        connected = false;
                        clientSocket.close();
                    }
                }

            } else {
                dos.writeUTF("unknown_action");
                dos.writeUTF("Press 'y' to retry or any other key to exit.");
                String retry = dis.readUTF();
                if (!retry.equalsIgnoreCase("y")) {
                    dos.writeUTF("Connection closed.");
                    connected = false;
                    clientSocket.close();
                }
            }
        }

    } catch (IOException e) {
        System.err.println("[SERVER] Error processing client request: " + e.getMessage());
    }
}

	

	////////////////////////////
		
	public UserMsg getUser(int userId) {
		return users.get(userId);
	}
	// Methode utilisée pour savoir quoi faire d'un paquet
	// reçu par le serveur
	public void processPacket(Packet p) {
		PacketProcessor pp = null;
		if (p.destId < 0) { //message de groupe
			// can be send only if sender is member
			UserMsg sender = users.get(p.srcId);
			GroupMsg g = groups.get(p.destId);
			if (g.getMembers().contains(sender)) pp=g;
		}
		else if (p.destId > 0) { // message entre utilisateurs
			 pp = users.get(p.destId);
		}
		else { // message de gestion pour le serveur
			pp=sp;
		}
		
		if (pp != null) {
			pp.process(p);
		}
	}


	// J'ai modifié cette méthode car j'ai déplacé toute la logique de l'ancienne méthode start() dans handleClientConnections()
	// et j'ai laissé uniquement une boucle pour attendre de nouvelles connexions des clients

	public void start() {
		started = true;
		LOG.info("Server started and waiting for client connections...");
	
		while (started) {
			try {
				// Accepter la connexion du client
				Socket s = serverSock.accept();
				LOG.info("New client connection accepted: " + s.getInetAddress());
	
				// Appeler handleClientConnection pour gérer la connexion
				handleClientConnections(s);
	
			} catch (IOException e) {
				LOG.warning("Erreur lors de la connexion client : " + e.getMessage());
			}
		}
	}
	

	public void stop() {
		started = false;
		try {
			serverSock.close();
			users.values().forEach(s -> s.close());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		// Initialize the database
		DatabaseManager.initDatabase();
		// DatabaseManager.initAllDatabas();
		DatabaseManager.insertTestUser();
		ServerMsg s = new ServerMsg(1666);
		s.start();
	}
	

}
