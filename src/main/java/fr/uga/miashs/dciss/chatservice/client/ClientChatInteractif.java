package fr.uga.miashs.dciss.chatservice.client;

import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

public class ClientChatInteractif {

    private ClientMsg client;
    private MessageHistory history;
    private String historyFilePath;

    public ClientChatInteractif(String address, int port) {
        this.client = new ClientMsg(address, port);

        // 使用登录ID保存不同用户的聊天记录
        this.historyFilePath = "chat_history_user_" + client.getIdentifier() + ".dat";
        this.history = MessageHistory.loadFromFile(historyFilePath);

        client.addMessageListener(packet -> {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
                DataInputStream dis = new DataInputStream(bis);
                byte responseCode = dis.readByte();

                if (responseCode == -2) {
                    int groupCount = dis.readInt();
                    System.out.println("Vous êtes membre de " + groupCount + " groupe(s) :");
                    for (int i = 0; i < groupCount; i++) {
                        int groupId = dis.readInt();
                        System.out.println("- Groupe ID : " + groupId);
                    }
                } else {
                    String msg = new String(packet.data);
                    System.out.println(packet.srcId + " -> " + packet.destId + " : " + msg);

                    // 保存聊天记录
                    history.addMessage(packet.srcId, packet.destId, msg);
                    history.saveToFile(historyFilePath);
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la réception des données !");
            }
        });

        client.addConnectionListener(active -> {
            if (!active) System.exit(0);
        });
    }

    private void displayChatHistory(int otherId) {
        List<MessageHistory.MessageRecord> conversation = history.getConversation(otherId);
        System.out.println("--- Historique avec " + otherId + " ---");
        if (conversation.isEmpty()) {
            System.out.println("Aucun message précédent.");
        } else {
            for (MessageHistory.MessageRecord record : conversation) {
                System.out.println(record);
            }
        }
        System.out.println("-------------------------------");
    }

    public void handleUserInteraction() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoisissez une option :");
            System.out.println("1. Envoyer un message");
            System.out.println("2. Créer un groupe");
            System.out.println("3. Voir mes groupes");
            System.out.println("4. Quitter");
            System.out.print("Votre choix : ");
            String input = sc.nextLine();

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
                    client.closeSession();
                    return;
                default:
                    System.out.println("Choix invalide, veuillez réessayer !");
            }
        }
    }

    private void handleSendMessage(Scanner sc) {
        try {
            System.out.print("Entrez l'ID de la cible (utilisateur : positif, groupe : négatif) : ");
            int destId = Integer.parseInt(sc.nextLine());

            displayChatHistory(destId);  // 自动显示历史

            System.out.print("Entrez le contenu du message : ");
            String message = sc.nextLine();

            client.sendPacket(destId, message.getBytes());
            history.addMessage(client.getIdentifier(), destId, message);
            history.saveToFile(historyFilePath);

            System.out.println("Message envoyé à " + destId);
        } catch (NumberFormatException e) {
            System.out.println("ID invalide !");
        }
    }

    private void handleCreateGroup(Scanner sc) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeByte(1);

            System.out.print("Combien de membres voulez-vous ajouter ? : ");
            int nbMembers = Integer.parseInt(sc.nextLine());
            dos.writeInt(nbMembers);

            for (int i = 0; i < nbMembers; i++) {
                System.out.print("ID du membre " + (i + 1) + " : ");
                int memberId = Integer.parseInt(sc.nextLine());
                dos.writeInt(memberId);
            }

            dos.flush();
            client.sendPacket(0, bos.toByteArray());
            System.out.println("Demande de création de groupe envoyée au serveur.");

        } catch (Exception e) {
            System.out.println("Erreur lors de la création du groupe : " + e.getMessage());
        }
    }

    private void handleQueryGroups() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeByte(-2);
            dos.flush();

            client.sendPacket(0, bos.toByteArray());
            System.out.println("Demande de consultation des groupes envoyée.");

        } catch (Exception e) {
            System.out.println("Erreur lors de la consultation des groupes : " + e.getMessage());
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        ClientChatInteractif chat = new ClientChatInteractif("localhost", 1666);
        chat.client.startSession();
        System.out.println("Votre ID client est : " + chat.client.getIdentifier());
        chat.handleUserInteraction();
    }
}



//====================================verison1 Liu==============================================
//package fr.uga.miashs.dciss.chatservice.client;
//
//import fr.uga.miashs.dciss.chatservice.common.Packet;
//
//import java.io.*;
//import java.net.UnknownHostException;
//import java.util.*;
//
//public class ClientChatInteractif {//verison1 Liu
//
//    private ClientMsg client;
//    private Map<Integer, List<String>> chatHistory;
//
//    public ClientChatInteractif(String address, int port) {
//        this.client = new ClientMsg(address, port);
//        this.chatHistory = new HashMap<>();
//
//        // Listener pour la réception de messages
//        client.addMessageListener(packet -> {
//            try {
//                ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
//                DataInputStream dis = new DataInputStream(bis);
//                byte responseCode = dis.readByte();
//
//                if (responseCode == -2) {
//                    int groupCount = dis.readInt();
//                    System.out.println("Vous êtes membre de " + groupCount + " groupe(s) :");
//                    for (int i = 0; i < groupCount; i++) {
//                        int groupId = dis.readInt();
//                        System.out.println("- Groupe ID : " + groupId);
//                    }
//                } else {
//                    String msg = new String(packet.data);
//                    saveMessage(packet.srcId, packet.srcId + " : " + msg);
//                    System.out.println("[ " + packet.srcId + " -> " + packet.destId + "] : " + msg);
//                }
//            } catch (IOException e) {
//                System.out.println("Erreur lors de la réception des données !");
//            }
//        });
//
//        // Listener de déconnexion
//        client.addConnectionListener(active -> {
//            if (!active) System.exit(0);
//        });
//    }
//
//    private void saveMessage(int senderId, String message) {
//        chatHistory.computeIfAbsent(senderId, k -> new ArrayList<>()).add(message);
//    }
//
//    private void displayChatHistory(int otherId) {
//        List<String> history = chatHistory.getOrDefault(otherId, new ArrayList<>());
//        System.out.println("--- Historique de conversation avec " + otherId + " ---");
//        if (history.isEmpty()) {
//            System.out.println("Aucun message précédent.");
//        } else {
//            history.forEach(System.out::println);
//        }
//        System.out.println("------------------------------------------");
//    }
//
//    public void handleUserInteraction() {
//        Scanner sc = new Scanner(System.in);
//        while (true) {
//            System.out.println("\nChoisissez une option :");
//            System.out.println("1. Envoyer un message");
//            System.out.println("2. Créer un groupe");
//            System.out.println("3. Voir mes groupes");
//            System.out.println("4. Quitter");
//            System.out.print("Votre choix : ");
//            String input = sc.nextLine();
//
//            switch (input) {
//                case "1":
//                    handleSendMessage(sc);
//                    break;
//                case "2":
//                    handleCreateGroup(sc);
//                    break;
//                case "3":
//                    handleQueryGroups();
//                    break;
//                case "4":
//                    client.closeSession();
//                    return;
//                default:
//                    System.out.println("Choix invalide, veuillez réessayer !");
//            }
//        }
//    }
//
//    private void handleSendMessage(Scanner sc) {
//        try {
//            System.out.print("Entrez l'ID de la cible (utilisateur : positif, groupe : négatif) : ");
//            int destId = Integer.parseInt(sc.nextLine());
//
//            displayChatHistory(destId);  // Afficher l'historique automatiquement
//
//            System.out.print("Entrez le contenu du message : ");
//            String message = sc.nextLine();
//
//            client.sendPacket(destId, message.getBytes());
//            saveMessage(destId, "Moi : " + message);
//            System.out.println("Message envoyé à " + destId);
//        } catch (NumberFormatException e) {
//            System.out.println("ID invalide !");
//        }
//    }
//
//    private void handleCreateGroup(Scanner sc) {
//        try {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            DataOutputStream dos = new DataOutputStream(bos);
//
//            dos.writeByte(1);
//
//            System.out.print("Combien de membres voulez-vous ajouter ? : ");
//            int nbMembers = Integer.parseInt(sc.nextLine());
//            dos.writeInt(nbMembers);
//
//            for (int i = 0; i < nbMembers; i++) {
//                System.out.print("ID du membre " + (i + 1) + " : ");
//                int memberId = Integer.parseInt(sc.nextLine());
//                dos.writeInt(memberId);
//            }
//
//            dos.flush();
//            client.sendPacket(0, bos.toByteArray());
//            System.out.println("Demande de création de groupe envoyée au serveur.");
//
//        } catch (Exception e) {
//            System.out.println("Erreur lors de la création du groupe : " + e.getMessage());
//        }
//    }
//
//    private void handleQueryGroups() {
//        try {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            DataOutputStream dos = new DataOutputStream(bos);
//
//            dos.writeByte(-2);
//            dos.flush();
//
//            client.sendPacket(0, bos.toByteArray());
//            System.out.println("Demande de consultation des groupes envoyée.");
//
//        } catch (Exception e) {
//            System.out.println("Erreur lors de la consultation des groupes : " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) throws UnknownHostException, IOException {
//        ClientChatInteractif chat = new ClientChatInteractif("localhost", 1666);
//        chat.client.startSession();
//        System.out.println("Votre ID client est : " + chat.client.getIdentifier());
//        chat.handleUserInteraction();
//    }
//}
//========================================================================================================







//package fr.uga.miashs.dciss.chatservice.client;
//
//import fr.uga.miashs.dciss.chatservice.common.Packet;
//
//import java.io.*;
//import java.net.UnknownHostException;
//import java.util.Scanner;
//
//public class ClientChatInteractif {//This is just a test class. Anna.
//
//    public static void main(String[] args) throws IOException {
//        Scanner sc = new Scanner(System.in);
//
//        System.out.println("Entrez votre ID (0 pour un nouvel utilisateur) : ");
//        int id = Integer.parseInt(sc.nextLine());
//
//        ClientMsg client = new ClientMsg(id, "localhost", 1666);
//
//        // Écoute des messages entrants
//        client.addMessageListener(p -> {
//            String msg = new String(p.data);
//            System.out.println("Message reçu de " + p.srcId + " : " + msg);
//        });
//
//        // Écoute des connexions/déconnexions
//        client.addConnectionListener(active -> {
//            if (!active) {
//                System.out.println("Connexion perdue -- Fin du programme");
//                System.exit(0);
//            }
//        });
//
//        // Connexion
//        client.startSession();
//        System.out.println("Connecté en tant qu'utilisateur avec l'ID : " + client.getIdentifier());
//
//        String input;
//
//        while (true) {
//            System.out.println("\n--- Menu ---");
//            System.out.println("1. Envoyer un message à un utilisateur");
//            System.out.println("2. Envoyer un message à un groupe");
//            System.out.println("3. Créer un groupe");
//            System.out.println("4. Quitter");
//            System.out.print("Votre choix : ");
//            input = sc.nextLine();
//
//            switch (input) {
//                case "1":
//                    System.out.print("ID du destinataire : ");
//                    int userId = Integer.parseInt(sc.nextLine());
//                    System.out.print("Message : ");
//                    String msg1 = sc.nextLine();
//                    client.sendPacket(userId, msg1.getBytes());
//                    break;
//
//                case "2":
//                    System.out.print("ID du groupe (nombre négatif) : ");
//                    int groupId = Integer.parseInt(sc.nextLine());
//                    System.out.print("Message : ");
//                    String msg2 = sc.nextLine();
//                    client.sendPacket(groupId, msg2.getBytes());
//                    break;
//
//                case "3":
//                    System.out.print("Nombre de membres à ajouter (hors vous-même) : ");
//                    int nb = Integer.parseInt(sc.nextLine());
//
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    DataOutputStream dos = new DataOutputStream(bos);
//                    dos.writeByte(1); // Type : création de groupe
//                    dos.writeInt(nb);
//
//                    for (int i = 0; i < nb; i++) {
//                        System.out.print("ID du membre #" + (i + 1) + " : ");
//                        int mid = Integer.parseInt(sc.nextLine());
//                        dos.writeInt(mid);
//                    }
//                    dos.flush();
//
//                    client.sendPacket(0, bos.toByteArray());
//                    System.out.println("Demande de création de groupe envoyée");
//                    break;
//
//                case "4":
//                    System.out.println("Déconnexion");
//                    client.closeSession();
//                    return;
//
//                default:
//                    System.out.println("Choix invalide. Veuillez réessayer");
//            }
//        }
//    }
//}
