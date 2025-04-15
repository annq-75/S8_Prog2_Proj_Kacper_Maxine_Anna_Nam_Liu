package fr.uga.miashs.dciss.chatservice.server;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:chatservice.db";
    
    //sql queries to create each database
    // côté Serveur
    private static final String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL, " +
            "connected BOOLEAN NOT NULL DEFAULT 0)";   //table liée aux listes d'utilisateurs connectés et déconnectés
    private static final String sqlGroupUsers = "CREATE TABLE IF NOT EXISTS groupUsers (" +
    		"idGroupUser INTEGER NOT NULL PRIMARY KEY, " +
    		"idGroup INTEGER NOT NULL, " +
    		"idUser INTEGER NOT NULL)";
    private static final String sqlGroups = "CREATE TABLE IF NOT EXISTS groups (" +
    		"idGroup INTEGER NOT NULL PRIMARY KEY, " +
    		"idOwner INTEGER NOT NULL)";
    private static final String sqlMsgToBeServed = "CREATE TABLE IF NOT EXISTS msgToBeServed (" +
    		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		"idReceiver INTEGER NOT NULL, " +
    		"idSender INTEGER NOT NULL, " +
    		"content TEXT, " +
    		"delivered BOOLEAN NOT NULL DEFAULT 0)";  // 1/yes/true if message is sent already (i.e. if the receiver is connected and received the message 
    				// and so, if yes, the line will be deleted so no data is kept unnecessarily by the server database 
    
    // côté Client
    // pas sûr qu'on se serve d'une table ici, voir plus tard
    /*private static final String sqlMsgSent = "CREATE TABLE IF NOT EXISTS msgSent (" +
    		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		"idReceiver INTEGER NOT NULL, " +  // group id if negative or user id if positive
    		"idSender INTEGER NOT NULL, " +    // group id if negative or user id if positive
    		"content TEXT)";
    */
    

    // Phương thức tiện ích để lấy kết nối cơ sở dữ liệu
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    // Khởi tạo cơ sở dữ liệu
    public static void initDatabase(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("[DB] Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[DB] Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void initAllDatabases() {
    	//initDatabase("DROP TABLE users;");		//uncomment if you've already created 'users' table, leave commented otherwise
    	initDatabase(sqlUsers);
    	initDatabase(sqlGroupUsers);
    	initDatabase(sqlGroups);
    	initDatabase(sqlMsgToBeServed);
    	//initDatabase(sqlMsgSent);
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
