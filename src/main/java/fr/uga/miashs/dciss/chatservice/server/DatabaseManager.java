package fr.uga.miashs.dciss.chatservice.server;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {
	private static final String DB_URL = "jdbc:sqlite:chatservice.db";

	// sql queries to create each database
	// côté Serveur
	private static final String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "username TEXT NOT NULL UNIQUE, " + "password TEXT NOT NULL, "
			+ "connected BOOLEAN NOT NULL DEFAULT 0)"; // table liée aux listes d'utilisateurs connectés et déconnectés
	private static final String sqlGroupUsers = "CREATE TABLE IF NOT EXISTS groupUsers ("
			+ "idGroupUser INTEGER NOT NULL PRIMARY KEY, " + "idGroup INTEGER NOT NULL, " + "idUser INTEGER NOT NULL)";
	private static final String sqlGroups = "CREATE TABLE IF NOT EXISTS groups ("
			+ "idGroup INTEGER NOT NULL PRIMARY KEY, " + "idOwner INTEGER NOT NULL)";
	private static final String sqlMsgToBeServed = "CREATE TABLE IF NOT EXISTS msgToBeServed ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "idReceiver INTEGER NOT NULL, " + "idSender INTEGER NOT NULL, "
			+ "content TEXT, " + "delivered BOOLEAN NOT NULL DEFAULT 0)"; // 1/yes/true if message is sent already (i.e.
																			// if the receiver is connected and received
																			// the message
	

	// Phương thức tiện ích để lấy kết nối cơ sở dữ liệu
	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}

	// Khởi tạo cơ sở dữ liệu
	public static void initDatabase(String sql) {
		try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(sql);
			System.out.println("[DB] Database initialized successfully.");
		} catch (SQLException e) {
			System.err.println("[DB] Database initialization failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void initAllDatabases() {
		initDatabase(sqlUsers);
		initDatabase(sqlGroupUsers);
		initDatabase(sqlGroups);
		initDatabase(sqlMsgToBeServed);
	}

	// Xác thực người dùng
	public static boolean validateUser(String username, String password) {
		String query = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, username);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				boolean result = rs.next();
				System.out.println("[DB] User validation result for '" + username + "': " + result);
				return result;
			}
		} catch (SQLException e) {
			System.err.println("[DB] User validation failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// Đăng ký người dùng mới
	public static boolean register(String username, String password) {
		String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.setString(2, password); // Có thể hash mật khẩu ở đây
			stmt.executeUpdate();
			System.out.println("[DB] User registered: " + username);
			return true;
		} catch (SQLException e) {
			System.err.println("[DB] Registration failed for '" + username + "': " + e.getMessage());
			return false;
		}
	}

	// Đăng nhập người dùng
	public static boolean login(String username, String password) {
		return validateUser(username, password);
	}

	// Thêm tài khoản test
	public static void insertTestUser(String username, String password) {
		String sql = "INSERT OR IGNORE INTO users(username, password) VALUES (?, ?)";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.executeUpdate();
			System.out.println("[DB] Test user '" + username + "' inserted.");
		} catch (SQLException e) {
			System.err.println("[DB] Failed to insert test user: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean isOwner(int idUserAsking, int idGroup) {
		// check if user is the owner of the group
		String queryOwner = "SELECT * FROM groups WHERE idGroup = ? AND idOwner = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(queryOwner)) {

			stmt.setInt(1, idGroup);
			stmt.setInt(2, idUserAsking);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("[DB] Group-ownership check failed: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static boolean existsUser(int userId) {
		// check if user exists in the database
		String queryUserExists = "SELECT * FROM users WHERE id = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(queryUserExists)) {

			stmt.setInt(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("[DB] Failed to check if user exists: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static boolean existsGroup(int groupId) {
		// check if group exists in the database
		String queryGroupExists = "SELECT * FROM groups WHERE idGroup = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(queryGroupExists)) {

			stmt.setInt(1, groupId);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("[DB] Failed to check if group exists: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static void createNewGroup(int ownerId) {
		// 1. check if owner exists
		if (existsUser(ownerId)) {
			String selectLastGroupIdQuery = "SELECT MIN(idGroup) AS lastGroupId FROM groups";
			String insertGroupQuery = "INSERT INTO groups (idGroup, idOwner) VALUES (?, ?)";
			int newGroupId = 0;

			try (Connection connection = getConnection();
					PreparedStatement selectStmt = connection.prepareStatement(selectLastGroupIdQuery);
					PreparedStatement insertStmt = connection.prepareStatement(insertGroupQuery)) {

				// récupère l'ID du dernier groupe
				ResultSet resultSet = selectStmt.executeQuery();
				int lastGroupId = -1; // Valeur par défaut si aucun groupe n'existe
				if (resultSet.next()) {
					lastGroupId = resultSet.getInt("lastGroupId");
				}

				// calcule le nouvel ID
				newGroupId = lastGroupId - 1;

				// insère le nouveau groupe
				insertStmt.setInt(1, newGroupId);
				insertStmt.setInt(2, ownerId);
				insertStmt.executeUpdate();

				System.out.println("Nouveau groupe créé avec l'ID : " + newGroupId);

			} catch (SQLException e) {
				e.printStackTrace();
			}

			DatabaseManager.addUserToGroup(ownerId, newGroupId, ownerId);
		} else
			System.out.println("[DB] Owner doesn't exist.");
	}

	// ajouter un utilisateur à un groupe
	public static void addUserToGroup(int idUserAsking, int idGroup, int idInvitedUser) {
		// 0. check if idUserAsking exists
		if (existsUser(idUserAsking)) {
			// 1. check if group exists
			if (existsGroup(idGroup)) {
				// 2. check if userAsking is the owner of the group
				if (isOwner(idUserAsking, idGroup)) {
					// 3. check if InvitedUser exists
					if (existsUser(idInvitedUser)) {
						// 4. check if invited user is already in the group
						String queryInvitedUserAlreadyInGroup = "SELECT * FROM groupUsers WHERE idGroup = ? AND idUser = ?";
						try (Connection connec = getConnection();
								PreparedStatement stmt2 = connec.prepareStatement(queryInvitedUserAlreadyInGroup)) {

							stmt2.setInt(1, idGroup);
							stmt2.setInt(2, idInvitedUser);

							try (ResultSet rs2 = stmt2.executeQuery()) {
								if (!rs2.next()) { // if rs.next() == false <=> pas de lignes renvoyées dans la requête,
									// donc idInvitedUser n'est pas enregistré dans le groupe
									// System.out.println("[DB] Invited user isn't in the group yet.");
									// Add invited user to the group
									String sqlAddUser = "INSERT INTO groupUsers(idGroup, idUser) VALUES (?, ?)";
									try (PreparedStatement stmt3 = connec.prepareStatement(sqlAddUser)) {

										stmt3.setInt(1, idGroup);
										stmt3.setInt(2, idInvitedUser);
										stmt3.executeUpdate();
										System.out.println("[DB] Invited user '" + idInvitedUser
												+ "' inserted in group '" + idGroup + "'.");
									} catch (SQLException e) {
										System.err.println("[DB] Failed to insert test user: " + e.getMessage());
										e.printStackTrace();
									}

								} else // if rs.next() == true <=> au moins 1 ligne renvoyée par la requête, donc
										// idInvitedUser est déjà dans le groupe
									System.out.println("[DB] Invited user is already in the group.");
							}

						} catch (SQLException e) {
							System.err
									.println("[DB] Failed to check if invited user is in the group: " + e.getMessage());
							e.printStackTrace();
						}
					} else
						System.out.println("[DB] User '" + idInvitedUser + "' doesn't exist.");
				} else
					System.out.println("[DB] User '" + idUserAsking + "' isn't the owner of group " + idGroup);
			} else
				System.out.println("[DB] Group '" + idGroup + "' doesn't exist.");
		} else
			System.out.println("[DB] User '" + idUserAsking + "' doesn't exist.");

	}

	// enlever un utilisateur d'un groupe
	public static void removeUserFromGroup(int idUserAsking, int idGroup, int idRUser) {
		if (idUserAsking == idRUser) {
			DatabaseManager.deleteGroup(idUserAsking, idGroup);
		} else {
			// 0. check if idUserAsking exists
			if (existsUser(idUserAsking)) {
				// 1. check if group exists
				if (existsGroup(idGroup)) {
					// 2. check if userAsking is the owner of the group
					if (isOwner(idUserAsking, idGroup)) {
						// 3. check if InvitedUser exists
						if (existsUser(idRUser)) {
							// 4. check if invited user is already in the group
							String queryRUserAlreadyInGroup = "SELECT * FROM groupUsers WHERE idGroup = ? AND idUser = ?";
							try (Connection connec = getConnection();
									PreparedStatement stmt2 = connec.prepareStatement(queryRUserAlreadyInGroup)) {

								stmt2.setInt(1, idGroup);
								stmt2.setInt(2, idRUser);

								try (ResultSet rs2 = stmt2.executeQuery()) {
									if (rs2.next()) {
										// System.out.println("[DB] User '" + idRUser + "' is in the group.");

										// Remove user from the group
										String sqlRemoveUser = "DELETE FROM groupUsers WHERE idGroup = ? AND idUser = ?";
										try (PreparedStatement stmt3 = connec.prepareStatement(sqlRemoveUser)) {

											stmt3.setInt(1, idGroup);
											stmt3.setInt(2, idRUser);
											stmt3.executeUpdate();
											System.out.println("[DB] User '" + idRUser + "' removed from group '"
													+ idGroup + "'.");
										} catch (SQLException e) {
											System.err.println("[DB] Failed to remove user: " + e.getMessage());
											e.printStackTrace();
										}
									}
								} catch (SQLException e) {
									System.err.println(
											"[DB] Failed to check if invited user is in the group: " + e.getMessage());
									e.printStackTrace();
								}
							} catch (SQLException e) {
								System.err.println(
										"[DB] Failed to check if invited user is in the group: " + e.getMessage());
								e.printStackTrace();
							}
						} else
							System.out.println("[DB] User '" + idRUser + "' doesn't exist.");
					} else
						System.out.println("[DB] User '" + idUserAsking + "' isn't the owner of group " + idGroup);
				} else
					System.out.println("[DB] Group '" + idGroup + "' doesn't exist.");
			} else
				System.out.println("[DB] User '" + idUserAsking + "' doesn't exist.");
		}

	}

	public static void deleteGroup(int idUserAsking, int idGroup) {
		// 1. check if user exists
		if (existsUser(idUserAsking)) {
			// 2. check if group exists
			if (existsGroup(idGroup)) {
				// 3. check if user is the group owner
				if (isOwner(idUserAsking, idGroup)) {
					String RemoveGroupUsersQuery = "DELETE FROM groupUsers WHERE idGroup = ?";
					String RemoveGroupQuery = "DELETE FROM groups WHERE idGroup = ?";

					try (Connection connection = getConnection();
							PreparedStatement rmguStmt = connection.prepareStatement(RemoveGroupUsersQuery);
							PreparedStatement deleteStmt = connection.prepareStatement(RemoveGroupQuery)) {

						rmguStmt.setInt(1, idGroup);
						deleteStmt.setInt(1, idGroup);
						rmguStmt.executeUpdate();
						deleteStmt.executeUpdate();

						System.out.println("Groupe '" + idGroup + "' supprimé");

					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else
					System.out.println("[DB] User '" + idUserAsking + "' isn't the owner of group " + idGroup);
			} else
				System.out.println("[DB] Group '" + idGroup + "' doesn't exist.");
		} else
			System.out.println("[DB] User '" + idUserAsking + "' doesn't exist.");

	}

	public static int[] getRegisteredUsers() {
		// Liste temporaire pour stocker les IDs des utilisateurs
		ArrayList<Integer> userIds = new ArrayList<>();

		String queryGetUsers = "SELECT id FROM users";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(queryGetUsers)) {

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					userIds.add(rs.getInt("id"));
				}
			}
		} catch (SQLException e) {
			System.err.println("[DB] Failed to retrieve all users: " + e.getMessage());
			e.printStackTrace();
		}

		// Convertir la liste en tableau
		int[] allUsers = new int[userIds.size()];
		for (int i = 0; i < userIds.size(); i++) {
			allUsers[i] = userIds.get(i);
		}
		return allUsers;
	}
	
	
	public static int[] getAllMyGroups(int idUserAsking) {
		int[] allMyGroups = null;
		// 1. check if user exists
		if (existsUser(idUserAsking)) {
		// Liste temporaire pour stocker les IDs des groupes
		ArrayList<Integer> myGroupIds = new ArrayList<>();

		String queryGetMyGroups = "SELECT idGroup FROM groupUsers WHERE idUser = ?";
		try (Connection conn = getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(queryGetMyGroups)) {

			stmt.setInt(1, idUserAsking);
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					myGroupIds.add(rs.getInt("idGroup"));
				}
			}
		} catch (SQLException e) {
			System.err.println("[DB] Failed to retrieve all my groups: " + e.getMessage());
			e.printStackTrace();
		}

		// Convertir la liste en tableau
		allMyGroups = new int[myGroupIds.size()];
		for (int i = 0; i < myGroupIds.size(); i++) {
			allMyGroups[i] = myGroupIds.get(i);
		}
		return allMyGroups;
	} else 
		System.out.println("[DB] User '" + idUserAsking + "' doesn't exist.");
		return allMyGroups;
	}

	// -----------------------------------------------------------------------------------//

	public static void main(String[] args) {
		// DatabaseManager.createNewGroup(1);
		// DatabaseManager.addUserToGroup(9, -1, 11);
		// DatabaseManager.deleteGroup(1,-1);
		// System.out.print(isOwner(1, -2));
		// DatabaseManager.removeUserFromGroup(9, -1, 1); 
		// DatabaseManager.createNewGroup(9);
		// System.out.print(DatabaseManager.getRegisteredUsers()[0]); // affiche le premier élément du tableau de tous les users
		// System.out.print(DatabaseManager.getAllMyGroups(1)[0]); // affiche le premier élément du tableau de tous les groupes de l'user saisi
	}
}