package gui;

import server.AdminService;
import server.Client;
import server.ClientService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;

    private final AdminService adminService = new AdminService();
    private final ClientService clientService = new ClientService();

    public LoginFrame() {
        setTitle("Авторизация");
        setSize(350, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Логин:"));
        loginField = new JTextField();
        add(loginField);

        add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Войти");
        add(loginButton);

        loginButton.addActionListener(e -> onLogin());
    }

    private void onLogin() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        // 1) Проверяем администратора
        if (adminService.login(login, password)) {
            JOptionPane.showMessageDialog(this, "Вход выполнен как администратор");
            new AdminMainFrame().setVisible(true);
            dispose();
            return;
        }

        // 2) Проверяем клиента
        Client client = clientService.login(login, password);
        if (client != null) {
            JOptionPane.showMessageDialog(this, "Вход выполнен как клиент");
            new ClientMainFrame(client).setVisible(true);
            dispose();
            return;
        }

        // 3) Если ни один не подошёл
        JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
