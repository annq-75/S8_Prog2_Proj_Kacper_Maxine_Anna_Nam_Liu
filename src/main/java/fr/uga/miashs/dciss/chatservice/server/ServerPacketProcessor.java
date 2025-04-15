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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
		ByteBuffer buf = ByteBuffer.wrap(p.data);
		byte type = buf.get();
		
		switch (type) {
			case 1:
				createGroup(p.srcId, buf); // Create group
				break;
			case 7:
				handleLogin(p.srcId, buf); // Login
				break;
			case 8:
				handleRegister(p.srcId, buf); // Register
				break;
			case 9:
				handleLogout(p.srcId, buf); // Logout
				break;
			default:
				LOG.warning("Unhandled message type: " + type);
				break;
		}
	}
	
	private void createGroup(int ownerId, ByteBuffer data) {
		int nb = data.getInt();
		GroupMsg g = server.createGroup(ownerId);
		for (int i = 0; i < nb; i++) {
			g.addMember(server.getUser(data.getInt()));
		}
	}


	////////////////////////// LOGIN, REGISTER, LOGOUT //////////////////////////

	private void handleLogin(int userId, ByteBuffer data) {
		String username = readString(data);
		String password = readString(data);
		boolean isSuccess = server.loginUser(userId, username, password);
	
		// Gửi phản hồi về client
		try {
			UserMsg user = server.getUser(userId);
			if (user != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeUTF(isSuccess ? "SUCCESS" : "FAIL");
				user.sendPacket(0, bos.toByteArray()); // Gửi đến client với destId = 0
			}
		} catch (IOException e) {
			LOG.severe("Failed to send login response: " + e.getMessage());
		}
	
		if (isSuccess) {
			LOG.info("User " + username + " logged in.");
		} else {
			LOG.warning("Login failed for user " + username);
		}
	}
	
	private void handleRegister(int userId, ByteBuffer data) {
		String username = readString(data);
		String password = readString(data);
		boolean isSuccess = server.registerUser(userId, username, password);
	
		// Gửi phản hồi về client
		try {
			UserMsg user = server.getUser(userId);
			if (user != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeUTF(isSuccess ? "SUCCESS" : "FAIL");
				user.sendPacket(0, bos.toByteArray()); // Gửi đến client với destId = 0
			}
		} catch (IOException e) {
			LOG.severe("Failed to send registration response: " + e.getMessage());
		}
	
		if (isSuccess) {
			LOG.info("User " + username + " registered.");
		} else {
			LOG.warning("Registration failed for user " + username);
		}
	}
	
	private void handleLogout(int userId, ByteBuffer data) {
		boolean isSuccess = server.logoutUser(userId);
	
		// Gửi phản hồi về client
		try {
			UserMsg user = server.getUser(userId);
			if (user != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeUTF(isSuccess ? "SUCCESS" : "FAIL");
				user.sendPacket(0, bos.toByteArray()); // Gửi đến client với destId = 0
			}
		} catch (IOException e) {
			LOG.severe("Failed to send logout response: " + e.getMessage());
		}
	
		if (isSuccess) {
			LOG.info("User ID " + userId + " logged out.");
		} else {
			LOG.warning("Logout failed for user ID " + userId);
		}
	}
	
	private String readString(ByteBuffer data) {
		int length = data.getInt();
		byte[] bytes = new byte[length];
		data.get(bytes);
		return new String(bytes);
	}
}
