package gui;

import client.ServerClient;
import server.Client;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;

    private final ServerClient serverClient = new ServerClient();

    public LoginFrame() {
        setTitle("Авторизация - FlowerShop");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Главная панель с отступами
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("FlowerShop", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель с полями ввода
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel loginLabel = new JLabel("Логин:");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(loginLabel);
        
        loginField = new JTextField(15);
        loginField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(loginField);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(passwordLabel);
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(passwordField);

        // Пустая ячейка для выравнивания
        inputPanel.add(new JLabel());
        
        JButton loginButton = new JButton("Войти");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(e -> onLogin());
        inputPanel.add(loginButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Обработка Enter в полях ввода
        loginField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> onLogin());

        add(mainPanel);
    }

    private void onLogin() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        // Отправляем запрос к серверу (Tomcat)
        ServerClient.LoginResponse response = serverClient.login(login, password);
        
        if (response.success) {
            if ("admin".equals(response.userType)) {
                JOptionPane.showMessageDialog(this, "Вход выполнен как администратор");
                new AdminMainFrame().setVisible(true);
                dispose();
            } else if ("client".equals(response.userType)) {
                // Создаем объект Client с ID для передачи в ClientMainFrame
                Client client = new Client(response.clientId, "", "", login, "");
                JOptionPane.showMessageDialog(this, "Вход выполнен как клиент");
                new ClientMainFrame(client).setVisible(true);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
