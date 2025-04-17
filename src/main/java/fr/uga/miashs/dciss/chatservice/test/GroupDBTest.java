package fr.uga.miashs.dciss.chatservice.test;

import fr.uga.miashs.dciss.chatservice.server.DatabaseManager;

public class GroupDBTest {

	public GroupDBTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//DatabaseManager.createNewGroup(4);
		//DatabaseManager.addUserToGroup(1, -1, 2);
		
		 int idUserAsking = 9; // Remplacez par un ID qui n'existe pas pour tester
		 int idGroup = -1; // Remplacez par l'ID du groupe
		 int idInvitedUser = 2; // Remplacez par l'ID de l'utilisateur invité

		DatabaseManager.createNewGroup(idUserAsking);
	}

}
