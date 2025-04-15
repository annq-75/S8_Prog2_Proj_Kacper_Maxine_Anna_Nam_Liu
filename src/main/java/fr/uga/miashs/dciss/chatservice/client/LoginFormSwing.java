package fr.uga.miashs.dciss.chatservice.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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

        // Afficher l'interface de connexion
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

        // Gérer l'événement lors du clic sur le bouton "Login"
        loginButton.addActionListener(e -> handleLogin(usernameField, passwordField, messageArea));

        // Gérer l'événement lors du clic sur le bouton "Register"
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
        panel.add(new JLabel()); // Espace réservé
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

        // Gérer l'événement lors du clic sur le bouton "Register"
        registerButton.addActionListener(e -> handleRegister(usernameField, passwordField, confirmPasswordField, messageArea));

        // Gérer l'événement lors du clic sur le bouton "Back"
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
        panel.add(new JLabel()); // Espace réservé
        panel.add(new JScrollPane(messageArea));

        return panel;
    }

    private static void handleLogin(JTextField usernameField, JPasswordField passwordField, JTextArea messageArea) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageArea.setText("Le nom d'utilisateur et le mot de passe ne peuvent pas être vides.");
            return;
        }

        // Appeler ClientMsg pour se connecter au serveur
        ClientMsg client = new ClientMsg("localhost", 1666);
        try {
            boolean loginSuccess = client.connectToServer("localhost", 1666, username, password);
            if (loginSuccess) {
                messageArea.setText("Connexion réussie !");
                // Ouvrir l'interface principale après une connexion réussie
                openMainChatWindow(client);
            } else {
                messageArea.setText("Échec de la connexion ! Nom d'utilisateur ou mot de passe invalide.");
            }
        } catch (Exception ex) {
            messageArea.setText("Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void handleRegister(JTextField usernameField, JPasswordField passwordField, JPasswordField confirmPasswordField, JTextArea messageArea) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageArea.setText("Tous les champs sont obligatoires.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageArea.setText("Les mots de passe ne correspondent pas !");
            return;
        }

        // Appeler ClientMsg pour envoyer une demande d'inscription au serveur
        ClientMsg client = new ClientMsg("localhost", 1666);
        try {
            boolean registerSuccess = client.register(username, password);
            if (registerSuccess) {
                messageArea.setText("Inscription réussie !");
            } else {
                messageArea.setText("Échec de l'inscription ! Le nom d'utilisateur existe peut-être déjà.");
            }
        } catch (Exception ex) {
            messageArea.setText("Erreur : " + ex.getMessage());
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
        JButton sendButton = new JButton("Envoyer");

        // Gérer l'événement lors de l'envoi d'un message
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                try {
                    client.sendPacket(0, message.getBytes());
                    chatArea.append("Vous : " + message + "\n");
                    messageField.setText("");
                } catch (IOException ex) {
                    chatArea.append("Erreur lors de l'envoi du message : " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        chatFrame.setLayout(new BorderLayout());
        chatFrame.add(scrollPane, BorderLayout.CENTER);
        chatFrame.add(messageField, BorderLayout.SOUTH);
        chatFrame.add(sendButton, BorderLayout.EAST);

        chatFrame.setVisible(true);
    }
}
