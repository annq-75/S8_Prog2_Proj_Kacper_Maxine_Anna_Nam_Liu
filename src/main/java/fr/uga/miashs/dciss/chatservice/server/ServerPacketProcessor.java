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
		// ByteBufferVersion. On aurait pu utiliser un ByteArrayInputStream + DataInputStream à la place
		ByteBuffer buf = ByteBuffer.wrap(p.data);
		byte type = buf.get();
		
		if (type == 1) { // cas creation de groupe
			createGroup(p.srcId,buf);
		} else {
			LOG.warning("Server message of type=" + type + " not handled by procesor");
		}
		
		//l'architecture du paquetage: 
		// sourceID : 4 octets -- c'est celui qu envoie le paquet (utilisé que dans le cas d'utilisateur)
		// destID : 4 octets -- l'adresse (peut etre un utilisateur, groupe, base de donnée??)
		// Data : [] qui contient en position 0 : le type de l'action
		// en positions qui suivent contient les données nécessaires au fonctionnement des actions
		// data contient un byte en position 0 -> faut définir la longueur de la taille des données selon le type
		
		switch(type) {//on commence par client -> serveur
		
		case 1://création group; data : nombre de membres; nom du groupe
			//nombre de membres (N): 4 octets (4bytes)
			// ids des membres : N * 4 octets (4bytes)
			// nom du groupe: ----------------------- pour après, pas d'idée comment définir la taille du paquetage à parcourir
			
			int nbMb = buf.getInt();
			int i =0; //compteur de id memebres
			int[] idMb = new int[5];
			while (buf.remaining() >= Integer.BYTES) {
				idMb[i] = buf.getInt();
			}
			
			
		case 2:// suppression groupe; data : nom groupe
			
		case 3:// ajout utilisateurs; data : id/noms utilisateurs, nom du groupe
			
		case 4:// suppression d'utilisateurs; data : id/noms utilisateurs, nom du groupe
			
		case 5:// envoie de message; data: id/nom utilisateurs/groupe; contenu
			
		case 6:// suppression de groupe; data: nom du groupe
			
		//serveur -> client
			
		case 7:// envoie de message stocké; data : message
			
			
		// client -> base de données
		case 8:// enregistrement de l'historique de messages; data: les messages enregistrés sur la session
			
		//serveur -> base de données
		case 9:// stockage de messages envoyes (non recus); data: les messages non recus
			
		}
	}
	
	public void createGroup(int ownerId, ByteBuffer data) {
		int nb = data.getInt();
		GroupMsg g = server.createGroup(ownerId);
		for (int i = 0; i < nb; i++) {
			g.addMember(server.getUser(data.getInt()));
		}
	}

}
