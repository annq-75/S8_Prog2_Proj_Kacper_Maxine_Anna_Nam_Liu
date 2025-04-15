package fr.uga.miashs.dciss.chatservice.server;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:chatservice.db";

    // Méthode utilitaire pour obtenir une connexion à la base de données
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Initialiser la base de données
    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "username TEXT NOT NULL UNIQUE, " +
                     "password TEXT NOT NULL)";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("[DB] Base de données initialisée avec succès.");
        } catch (SQLException e) {
            System.err.println("[DB] Échec de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Authentifier un utilisateur
    public static boolean validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean result = rs.next();
                System.out.println("[DB] Résultat de la validation de l'utilisateur '" + username + "' : " + result);
                return result;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Échec de la validation de l'utilisateur : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Enregistrer un nouvel utilisateur
    public static boolean register(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Vous pouvez hacher le mot de passe ici
            stmt.executeUpdate();
            System.out.println("[DB] Utilisateur enregistré : " + username);
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Échec de l'enregistrement pour '" + username + "' : " + e.getMessage());
            return false;
        }
    }

    // Connecter un utilisateur
    public static boolean login(String username, String password) {
        return validateUser(username, password);
    }

    // Ajouter un compte de test
    public static void insertTestUser() {
        String sql = "INSERT OR IGNORE INTO users(username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "namdo");
            stmt.setString(2, "1234");
            stmt.executeUpdate();
            System.out.println("[DB] Utilisateur de test 'namdo' inséré.");
        } catch (SQLException e) {
            System.err.println("[DB] Échec de l'insertion de l'utilisateur de test : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
