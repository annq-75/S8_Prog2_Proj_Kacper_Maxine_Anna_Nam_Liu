package fr.uga.miashs.dciss.chatservice.server;

public class DistributeMsgFromDatabase {
	//this class is going to be for sending messages from the server database once the receiver is connected
	// lorsqu'un utilisateur se connecte, on check la bdd-serveur : 
	// on regarde s'il y a correspondance entre l'idReceiver d'un ou plusieurs messages en attente et 
	// l'id de l'utilisateur connecté et des groupes auxquels il appartient
	// s'il y a correspondance alors envoi de chaque message au.x groupe.s dont l'utilisateur fait partie ou à lui-même directement
	
	public DistributeMsgFromDatabase() {
		// TODO Auto-generated constructor stub
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
