package fr.uga.miashs.dciss.chatservice.client;

import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientChatInteractif {//This is just a test class. Anna.

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Entrez votre ID (0 pour un nouvel utilisateur) : ");
        int id = Integer.parseInt(sc.nextLine());

        ClientMsg client = new ClientMsg(id, "localhost", 1666);

        // Écoute des messages entrants
        client.addMessageListener(p -> {
            String msg = new String(p.data);
            System.out.println("Message reçu de " + p.srcId + " : " + msg);
        });

        // Écoute des connexions/déconnexions
        client.addConnectionListener(active -> {
            if (!active) {
                System.out.println("Connexion perdue -- Fin du programme");
                System.exit(0);
            }
        });

        // Connexion
        client.startSession();
        System.out.println("Connecté en tant qu'utilisateur avec l'ID : " + client.getIdentifier());

        String input;

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Envoyer un message à un utilisateur");
            System.out.println("2. Envoyer un message à un groupe");
            System.out.println("3. Créer un groupe");
            System.out.println("4. Quitter");
            System.out.print("Votre choix : ");
            input = sc.nextLine();

            switch (input) {
                case "1":
                    System.out.print("ID du destinataire : ");
                    int userId = Integer.parseInt(sc.nextLine());
                    System.out.print("Message : ");
                    String msg1 = sc.nextLine();
                    client.sendPacket(userId, msg1.getBytes());
                    break;

                case "2":
                    System.out.print("ID du groupe (nombre négatif) : ");
                    int groupId = Integer.parseInt(sc.nextLine());
                    System.out.print("Message : ");
                    String msg2 = sc.nextLine();
                    client.sendPacket(groupId, msg2.getBytes());
                    break;

                case "3":
                    System.out.print("Nombre de membres à ajouter (hors vous-même) : ");
                    int nb = Integer.parseInt(sc.nextLine());

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);
                    dos.writeByte(1); // Type : création de groupe
                    dos.writeInt(nb);

                    for (int i = 0; i < nb; i++) {
                        System.out.print("ID du membre #" + (i + 1) + " : ");
                        int mid = Integer.parseInt(sc.nextLine());
                        dos.writeInt(mid);
                    }
                    dos.flush();

                    client.sendPacket(0, bos.toByteArray());
                    System.out.println("Demande de création de groupe envoyée");
                    break;

                case "4":
                    System.out.println("Déconnexion");
                    client.closeSession();
                    return;

                default:
                    System.out.println("Choix invalide. Veuillez réessayer");
            }
        }
    }
}
