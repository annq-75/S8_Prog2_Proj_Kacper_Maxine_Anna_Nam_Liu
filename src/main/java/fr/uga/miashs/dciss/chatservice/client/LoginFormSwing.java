package fr.uga.miashs.dciss.chatservice.client;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;


public class LoginFormSwing extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private ClientMsg client;

    public LoginFormSwing() {
        setTitle("Login / Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);

        usernameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(loginButton, gbc);

        registerButton = new JButton("Register");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(registerButton, gbc);

        try {
            client = new ClientMsg("localhost", 1666);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
    }

    private void handleLogin() {
        new Thread(() -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
    
            try (Socket socket = new Socket("localhost", 1666);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
    
                dis.readUTF(); // Welcome message
                dos.writeUTF("login:" + username + ":" + password);
    
                String response = dis.readUTF();
                if ("login_success".equals(response)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Login successful!");
                        this.dispose(); // Close login window
                        // TODO: Show main GUI
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        int retry = JOptionPane.showConfirmDialog(this, "Login failed. Retry?", "Login Failed", JOptionPane.YES_NO_OPTION);
                        if (retry == JOptionPane.YES_OPTION) handleLogin();
                    });
                }
    
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    

    private void handleRegister() {
        new Thread(() -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
    
            try (Socket socket = new Socket("localhost", 1666);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
    
                dis.readUTF(); // Welcome message
                dos.writeUTF("register:" + username + ":" + password);
    
                String response = dis.readUTF();
                if ("register_success".equals(response)) {
                    dis.readUTF(); // Message: "You can now login."
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Registration successful! Logging in...");
                        handleLogin();
                    });
                } else {
                    String retryMsg = dis.readUTF();
                    SwingUtilities.invokeLater(() -> {
                        int retry = JOptionPane.showConfirmDialog(this, retryMsg, "Register Failed", JOptionPane.YES_NO_OPTION);
                        if (retry == JOptionPane.YES_OPTION) handleRegister();
                    });
                }
    
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFormSwing().setVisible(true));
    }
}
