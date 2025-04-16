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
 
		 } else {
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
 }
 