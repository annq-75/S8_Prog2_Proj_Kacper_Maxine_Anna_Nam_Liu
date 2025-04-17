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
	public void startSession() throws UnknownHostException {
		if (s == null || s.isClosed()) {
			try {
				s = new Socket(serverAddress, serverPort);
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
				dos.writeInt(identifier);
				dos.flush();
				if (identifier == 0) {
					identifier = dis.readInt();
				}
				// start the receive loop
				new Thread(() -> receiveLoop()).start();
				notifyConnectionListeners(true);
			} catch (IOException e) {
				e.printStackTrace();
				// error, close session
				closeSession();
			}
		}
	}

	/**
	 * Send a packet to the specified destination (etiher a userId or groupId)
	 * 
	 * @param destId the destinatiion id
	 * @param data   the data to be sent
	 */
	/*public void sendPacket(int destId, byte[] data) {
		try {
			synchronized (dos) {
				dos.writeInt(destId);
				dos.writeInt(data.length);
				dos.write(data);
				dos.flush();
			}
		} catch (IOException e) {
			// error, connection closed
			closeSession();
		}
		
	}*/
	
	public void sendPacket(int destId, byte[] data) {
	    try {
	        synchronized (dos) {
	            System.out.println("[CLIENT → SERVER] Sending packet to: " + destId + ", bytes: " + Arrays.toString(data));
	            dos.writeInt(destId);
	            dos.writeInt(data.length);
	            dos.write(data);
	            dos.flush();
	        }
	    } catch (IOException e) {
	        System.err.println("[CLIENT] Failed to send packet to " + destId);
	        e.printStackTrace();
	        closeSession();
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
			// error, connection closed
		}
		closeSession();
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

	//=============================================Ajout menu - Version Liu============================================================
        //Ajout un menu pour demander au user s'il veux créer un groupe
        public void handleUserInteraction() {
            Scanner sc = new Scanner(System.in);
            String input = "";

            while (true) {
                System.out.println("\nQue voulez-vous faire ?");
                System.out.println("1. Envoyer un message");
                System.out.println("2. Créer un groupe");
                System.out.println("3. Voir mes groupes");
                System.out.println("4. Quitter");
                System.out.print("Votre choix: ");
                input = sc.nextLine();

                switch (input) {
                    case "1":
                        handleSendMessage(sc);
                        break;
                    case "2":
                        handleCreateGroup(sc);
                        break;
                    case "3":
                    	handleQueryGroups();
                    	break;
                    case "4":
                        closeSession();
                        return;
                    default:
                        System.out.println("Choix invalide !");
                }
            }
        }
        
        private void handleSendMessage(Scanner sc) {
            try {
                System.out.print("ID du destinataire (Utilisateur positif / Groupe négatif) : ");
                int destId = Integer.parseInt(sc.nextLine());

                System.out.print("Votre message : ");
                String message = sc.nextLine();

                sendPacket(destId, message.getBytes());  // 复用sendPacket
                System.out.println("Message envoyé à " + destId);

            } catch (NumberFormatException e) {
                System.out.println("Identifiant invalide !");
            }
        }
        
        private void handleCreateGroup(Scanner sc) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                dos.writeByte(1);  // 操作码1：创建群组

                System.out.print("Combien de membres voulez-vous ajouter ? ");
                int nbMembers = Integer.parseInt(sc.nextLine());
                dos.writeInt(nbMembers);

                for (int i = 0; i < nbMembers; i++) {
                    System.out.print("ID du membre " + (i + 1) + " : ");
                    int memberId = Integer.parseInt(sc.nextLine());
                    dos.writeInt(memberId);
                }

                dos.flush();
                sendPacket(0, bos.toByteArray());  // 0 = 发给服务器，要求创建群组
                System.out.println("Demande de création de groupe envoyée au serveur.");

            } catch (Exception e) {
                System.out.println("Erreur lors de la création du groupe : " + e.getMessage());
            }
        }
        
        private void handleQueryGroups() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                dos.writeByte(-2);  // 操作码2：查询群组
                dos.flush();

                sendPacket(0, bos.toByteArray());  // 发给服务器
                System.out.println("Demande de liste des groupes envoyée au serveur.");

            } catch (Exception e) {
                System.out.println("Erreur lors de la demande : " + e.getMessage());
            }
        }


		public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
            ClientMsg c = new ClientMsg("localhost", 1666);

            // add a dummy listener that print the content of message as a string
            //c.addMessageListener(p -> System.out.println(p.srcId + " says to " + p.destId + ": " + new String(p.data)));
            
            c.addMessageListener(p -> {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(p.data);
                    DataInputStream dis = new DataInputStream(bis);
                    byte responseCode = dis.readByte();

                    if (responseCode == -2) {  // 服务器返回群组列表
                        int groupCount = dis.readInt();
                        System.out.println("Vous êtes dans " + groupCount + " groupes :");
                        for (int i = 0; i < groupCount; i++) {
                            int groupId = dis.readInt();
                            System.out.println("- Groupe ID: " + groupId);
                        }
                    } else {
                        // 普通消息
                        System.out.println(p.srcId + " says to " + p.destId + ": " + new String(p.data));
                    }
                } catch (IOException e) {
                    System.out.println("Erreur lors de la lecture du paquet.");
                }
            });
            
            // add a connection listener that exit application when connection closed
            c.addConnectionListener(active ->  {if (!active) System.exit(0);});

            c.startSession();
            System.out.println("Vous êtes : " + c.getIdentifier());
            
            c.handleUserInteraction();
            // Thread.sleep(5000);

            // l'utilisateur avec id 4 crée un grp avec 1 et 3 dedans (et lui meme)
//            if (c.getIdentifier() == 4) {
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                DataOutputStream dos = new DataOutputStream(bos);
    //
//                // byte 1 : create group on server
//                dos.writeByte(1);
    //
//                // nb members
//                dos.writeInt(2);
//                // list members
//                dos.writeInt(1);
//                dos.writeInt(3);
//                dos.flush();
    //
//                c.sendPacket(0, bos.toByteArray());
    //
//            }
            
            

            Scanner sc = new Scanner(System.in);
            String lu = null;
            while (!"\\quit".equals(lu)) {
                try {
                    System.out.println("A qui voulez vous écrire ? ");
                    int dest = Integer.parseInt(sc.nextLine());

                    System.out.println("Votre message ? ");
                    lu = sc.nextLine();
                    c.sendPacket(dest, lu.getBytes());
                } catch (InputMismatchException | NumberFormatException e) {
                    System.out.println("Mauvais format");
                }

            }

            /*
             * int id =1+(c.getIdentifier()-1) % 2; System.out.println("send to "+id);
             * c.sendPacket(id, "bonjour".getBytes());
             *
             *
             * Thread.sleep(10000);
             */

            c.closeSession();

        }

}
