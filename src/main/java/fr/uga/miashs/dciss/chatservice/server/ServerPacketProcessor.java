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

 import java.nio.ByteBuffer;
 import java.nio.charset.StandardCharsets;
 import java.util.HashSet;
 import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
 import java.util.logging.Logger;
 
 import fr.uga.miashs.dciss.chatservice.common.Packet;
 
 public class ServerPacketProcessor implements PacketProcessor {
	 private final static Logger LOG = Logger.getLogger(ServerPacketProcessor.class.getName());
	 private ServerMsg server;
 
	 public ServerPacketProcessor(ServerMsg s) {
		 this.server = s;
	 }
 
	 @Override
	 public void process(Packet p) {
		 ByteBuffer buf = ByteBuffer.wrap(p.data);
		 byte type = buf.get();
 
		 if (type == 1) {
			 // FR : Création d’un nouveau groupe -- RU: Создание новой группы
			 createGroup(p.srcId, buf);
 
		 } else if (type == 2) {
			 // FR : Envoi des utilisateurs en ligne -- RU: Список пользователей онлайн
			 sendOnlineUsers(p.srcId);
 
		 } 
		 /*
		 else if (type == 3) {
			 // FR : Envoi des utilisateurs hors ligne -- RU: Список пользователей офлайн
			 sendOfflineUsers(p.srcId);
		 }
		 */
		 else if (type == 4) {
			 // FR : Envoi des groupes de l'utilisateur -- RU: Список групп, где пользователь участник
			 sendUserGroups(p.srcId);
 
		 } else if (type == 5) {
			 //suppression de groupe
			 removeGroup(p.srcId, buf);
			 //on passe en paramètres l'id de l'utilisateur qui essai de faire l'action
			 // on envoie en paramètre aussi la partie du paquet qui contient l'information sur le groupe	 
		 } else if(type == 6){
			 // ajout utilisateurs
			 addMember(p.srcId, null, null);
		 } else if(type == 7) {
			 //suppression d'utilisateurs
			 removeMember(null, null, null);
		 } else if(type == 8) {
			 //envoie de message -> à créer dans la classe clientMsg?? ou userMsg??
			 //sendMessage(string message, int target/UserMsg target??)
		 } else if(type == 9) {
			 //envoie de message stocké -> packet envoyé par le serveur
			 // à créer dans le serveur
		 } else if(type == 10) {
			 //enregistrement de l'historique des messages
			 // à créer dans le clientMsg, c'est le client qui va l'appeler
		 }else if(type == 11) {
			 //stockage des messages envoyés (mais non recus)
		 }
		 else if(type == 12){
			// Version-liu 
			 // FR : Envoi des groupes créés par l'utilisateur -- RU: Список групп, созданных пользователем
			sendCreatedGroups(p.srcId);
		 } else{

			 // FR : Commande inconnue -- RU: Неизвестный тип команды
			 LOG.warning("Server message of type=" + type + " not handled by processor");
		 }
	 }
 
	 // ----------- Création de groupe -----------
	 public void createGroup(int ownerId, ByteBuffer data) {
		 int nb = data.getInt();
		 GroupMsg g = server.createGroup(ownerId);
		 for (int i = 0; i < nb; i++) {
			 int memberId = data.getInt();
			 UserMsg member = server.getUser(memberId);
			 if (member != null) {
				 g.addMember(member);
			 }
		 }
		 // Envoi d'un accusé de réception
		 String ack = "Groupe créé avec ID : " + g.getId();
		 sendTextResponse(ownerId, ack);
	 }
 
	 // ----------- Envoi des utilisateurs en ligne -----------
	 private void sendOnlineUsers(int requesterId) {
		    StringBuilder sb = new StringBuilder();
		    Map<Integer, Boolean> connectedUsers = server.getUsers(); // or getOnlineUserIds()

		    for (Map.Entry<Integer, Boolean> entry : connectedUsers.entrySet()) {
		        if (entry.getValue()) {
		            sb.append(entry.getKey()).append(",");
		        }
		    }

		    System.out.println("DEBUG: Online users sent = " + sb.toString()); // temp for debug--- ВРЕМЕННО для проверки
		    sendTextResponse(requesterId, sb.toString());
		}

	 // ------------------ old vers ---------------
	 /*
	 private void sendOnlineUsers(int requesterId) {
		 StringBuilder sb = new StringBuilder();
		 for (UserMsg u : server.getUsers().values()) {
			 if (u.isConnected()) {
				 sb.append(u.getId()).append(",");
			 }
		 }
		 sendTextResponse(requesterId, sb.toString());
	 }
	 */
 
	 // ----------- Envoi des utilisateurs hors ligne -----------
	 /*
	 private void sendOfflineUsers(int requesterId) {
		 StringBuilder sb = new StringBuilder();
		 for (UserMsg u : server.getUsers().values()) {
			 if (!u.isConnected()) {
				 sb.append(u.getId()).append(",");
			 }
		 }
		 sendTextResponse(requesterId, sb.toString());
	 }
	 */
 
	 // ----------- Envoi des groupes auxquels appartient l'utilisateur -----------
	 private void sendUserGroups(int userId) {
		 UserMsg user = server.getUser(userId);
		 if (user == null) return;
 
		 Set<GroupMsg> groups = user.getGroups();
		 StringBuilder sb = new StringBuilder();
		 for (GroupMsg g : groups) {
			 sb.append(g.getId()).append(",");
		 }
		 sendTextResponse(userId, sb.toString());
	 }
 
	 // ----------- Méthode d'envoi d'un message texte générique -----------
	 private void sendTextResponse(int destId, String message) {
		 byte[] content = message.getBytes(StandardCharsets.UTF_8);
		 Packet response = new Packet(ServerMsg.SERVER_CLIENTID, destId, content);
		 UserMsg destUser = server.getUser(destId);
		 if (destUser != null) {
			 destUser.process(response);
		 }
	 }
	 
	 //--------------------------------------------------------------------
	 // faut prendre en compte la lecture de paquets -> décider si on lit les informations
	 // du paquet dans la méthode ou dans les if (et on les passe en parametre)?
	 
	 //----------------méthode suppression de groupe
	 public void removeGroup(int userId, ByteBuffer data) {//on redéfinit la méthode remove à partir de "ServerMsg.java"
			int id = data.getInt();
//			if(userId == ownerId) vérification si cest un propriétaire du groupe
			server.removeGroup(id);
		}
	 
	 //-----------------méthode ajout membre dans groupe--------------
	 public void addMember(int userId, GroupMsg groupe, UserMsg user) {
		 //if(ownerId == p.srcId && groupe.get(user)==null) -> vérifier si 
		 // l'utilisateur est un propriétaire et que l'utilisateur n'est pas dans le groupe
		 groupe.addMember(user);
	 }
	 
	 
	 ////------------------méthode suppression de membre du groupe-------------
	 public void removeMember(UserMsg user, UserMsg target, GroupMsg groupe) {
		 if (user == target){//l'utilisateur veut enlever lui meme
			 groupe.removeMember(target);
			 //if user == owner -> removeGroup();
		 }else {
			 //if (user == owner) -> verification si l'utilisateur est aussi un usager
			 groupe.removeMember(target);
		 }
		 
		
	 }
	 
	 // ----------------------méthode envoie de message
	 public void sendMessage(UserMsg target, GroupMsg groupTarget, Packet message) {
		 //possbilité que target ou group target soit nul selon si on envoie a un groupe ou une personne en particulier??
		 if(target == null) {//cas ou on envoie à un groupe
			 groupTarget.process(message);
		 }else {
			 
		 }
	 }
	 
	 //----------------------méthode envoie de message stocké------------------
	 public void sendStock() {
		 // recherche périodique dans la base de données à chaque reconnexion des utilisateurs
		 //-> utilisateur(1) se connecte -> le serveur recherche dans la base de données
		 // les messages qui ne n'ont pas été envoyés -> si il y en a ou le destId == userId alors on envoie
		 // -> utilisation de 
	 }

	 //----------------------les méthodes de liu-----------------------------
	 //Version- Liu
	     // ----------- Envoi des groupes créés par l'utilisateur -----------
	     private void sendCreatedGroups(int userId) {
	         Set<GroupMsg> createdGroups = new HashSet<>();
	 
	         // 遍历服务器上的所有群组
	         for (GroupMsg group : server.getGroups().values()) {
	             if (group.getOwner().getId() == userId) {
	                 createdGroups.add(group);
	             }
	         }
	 
	  // 用ByteBuffer打包
	         ByteBuffer buffer = ByteBuffer.allocate(4 + createdGroups.size() * 4);
	         buffer.putInt(createdGroups.size());
	         for (GroupMsg g : createdGroups) {
	             buffer.putInt(g.getId());
	         }
	 
	 Packet response = new Packet(ServerMsg.SERVER_CLIENTID, -2, buffer.array());
	         UserMsg destUser = server.getUser(userId);
	         if (destUser != null) {
	             destUser.process(response);
	         }
	 
 }
 