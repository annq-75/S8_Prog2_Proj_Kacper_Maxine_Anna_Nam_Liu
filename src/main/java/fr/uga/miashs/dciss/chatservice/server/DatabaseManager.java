package fr.uga.miashs.dciss.chatservice.server;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:chatservice.db";

    // Phương thức tiện ích để lấy kết nối cơ sở dữ liệu
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Khởi tạo cơ sở dữ liệu
    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "username TEXT NOT NULL UNIQUE, " +
                     "password TEXT NOT NULL)";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("[DB] Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[DB] Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Xác thực người dùng
    public static boolean validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

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
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
    public static void insertTestUser() {
        String sql = "INSERT OR IGNORE INTO users(username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "namdo");
            stmt.setString(2, "1234");
            stmt.executeUpdate();
            System.out.println("[DB] Test user 'namdo' inserted.");
        } catch (SQLException e) {
            System.err.println("[DB] Failed to insert test user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
