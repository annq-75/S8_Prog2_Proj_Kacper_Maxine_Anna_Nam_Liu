package fr.uga.miashs.dciss.chatservice.client;

import javax.swing.*;
import java.awt.*;

public class LoginFormSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowLoginForm());
    }

    private static void createAndShowLoginForm() {
        JFrame frame = new JFrame("ChatService");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new CardLayout());

        JPanel loginPanel = createLoginPanel(frame);
        JPanel registerPanel = createRegisterPanel(frame);

        frame.add(loginPanel, "Login");
        frame.add(registerPanel, "Register");

        // Hiển thị giao diện đăng nhập
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "Login");

        frame.setVisible(true);
    }

    private static JPanel createLoginPanel(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        // Xử lý sự kiện khi nhấn nút "Login"
        loginButton.addActionListener(e -> handleLogin(usernameField, passwordField, messageArea));

        // Xử lý sự kiện khi nhấn nút "Register"
        registerButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Register");
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(new JLabel()); // Placeholder
        panel.add(new JScrollPane(messageArea));

        return panel;
    }

    private static JPanel createRegisterPanel(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        // Xử lý sự kiện khi nhấn nút "Register"
        registerButton.addActionListener(e -> handleRegister(usernameField, passwordField, confirmPasswordField, messageArea));

        // Xử lý sự kiện khi nhấn nút "Back"
        backButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Login");
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(registerButton);
        panel.add(backButton);
        panel.add(new JLabel()); // Placeholder
        panel.add(new JScrollPane(messageArea));

        return panel;
    }

    private static void handleLogin(JTextField usernameField, JPasswordField passwordField, JTextArea messageArea) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageArea.setText("Username and password cannot be empty.");
            return;
        }

        // Gọi ClientMsg để kết nối đến server
        ClientMsg client = new ClientMsg("localhost", 1666);
        try {
            boolean loginSuccess = client.connectToServer("localhost", 1666, username, password);
            if (loginSuccess) {
                messageArea.setText("Login successful!");
                // Mở giao diện chính sau khi đăng nhập thành công
                openMainChatWindow(client);
            } else {
                messageArea.setText("Login failed! Invalid username or password.");
            }
        } catch (Exception ex) {
            messageArea.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void handleRegister(JTextField usernameField, JPasswordField passwordField, JPasswordField confirmPasswordField, JTextArea messageArea) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageArea.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageArea.setText("Passwords do not match!");
            return;
        }

        // Gọi ClientMsg để gửi yêu cầu đăng ký đến server
        ClientMsg client = new ClientMsg("localhost", 1666);
        try {
            boolean registerSuccess = client.register(username, password);
            if (registerSuccess) {
                messageArea.setText("Registration successful!");
            } else {
                messageArea.setText("Registration failed! Username may already exist.");
            }
        } catch (Exception ex) {
            messageArea.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void openMainChatWindow(ClientMsg client) {
        JFrame chatFrame = new JFrame("ChatService - Main");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(500, 400);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                client.sendPacket(0, message.getBytes());
                chatArea.append("You: " + message + "\n");
                messageField.setText("");
            }
        });

        chatFrame.setLayout(new BorderLayout());
        chatFrame.add(scrollPane, BorderLayout.CENTER);
        chatFrame.add(messageField, BorderLayout.SOUTH);
        chatFrame.add(sendButton, BorderLayout.EAST);

        chatFrame.setVisible(true);
    }
}
