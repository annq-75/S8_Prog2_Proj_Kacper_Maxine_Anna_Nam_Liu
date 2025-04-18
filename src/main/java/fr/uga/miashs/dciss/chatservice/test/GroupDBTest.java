package fr.uga.miashs.dciss.chatservice.test;

import fr.uga.miashs.dciss.chatservice.server.DatabaseManager;

public class GroupDBTest {

	public GroupDBTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		
		
		
		//Créons le groupe 5 :
		//DatabaseManager.createNewGroup(12); //créér le groupe et ajoute Maria au groupe
		//DatabaseManager.addUserToGroup(1, -3, 13); // Je ne peux pas ajouter Ilef au groupe
		//DatabaseManager.addUserToGroup(12, -3, 13); // Maria ajoute Ilef au groupe
		//DatabaseManager.addUserToGroup(12, -3, 11); // Maria ajoute Kacper au groupe
		
		// Erreur, elle veut enlever Kacper du groupe:
		//DatabaseManager.removeUserFromGroup(12, -3, 11); 
		
		//Maintenant, elle veut ajouter Amina
		//DatabaseManager.addUserToGroup(12, -3, 14);
		
		//A la fin du projet, suppression du groupe:
		//DatabaseManager.deleteGroup(12, -3);
		
		//Je créé un nouveau groupe
		//DatabaseManager.createNewGroup(1);
		//DatabaseManager.addUserToGroup(1, -4, 11);
		
		//Je veux m'enlever de mon propre groupe :
		//DatabaseManager.removeUserFromGroup(1, -4, 1); //ça supprime le groupe entièrement et enlève les membres de mon groupe de la table
		
		//System.out.print(DatabaseManager.getRegisteredUsers()[0]); // affiche le premier élément du tableau de tous les users
		//System.out.print(DatabaseManager.getAllMyGroups(1)[0]); // affiche le premier élément du tableau de tous les groupes de l'user saisi
		
		//--------------------------------------------------------------------------------------------------------------------------
		
		//int idUserAsking = 1; // Remplacez par un ID qui n'existe pas pour tester
		//int idGroup = -1; // Remplacez par l'ID du groupe ou un id de groupe qui n'existe pas
		//int idInvitedUser = 12; // Remplacez par l'ID de l'utilisateur invité

		//DatabaseManager.createNewGroup(idUserAsking);
		
		//System.out.print(isOwner(1, -2));
		//DatabaseManager.createNewGroup(9);
		//DatabaseManager.addUserToGroup(9, -1, 11);
		//DatabaseManager.deleteGroup(1,-5);
		//DatabaseManager.removeUserFromGroup(9, -1, 1); 
	}

}
