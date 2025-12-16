package gui;

import server.Client;

import javax.swing.*;
import java.awt.*;

public class ClientMainFrame extends JFrame {

    public ClientMainFrame(Client client) {
        setTitle("Клиент - FlowerShop");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Обработка закрытия окна - выход в окно авторизации
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitToLogin();
            }
        });

        int clientId = client.getId(); // получаем ID из объекта Client

        // Главная панель
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Панель с вкладками
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Каталог", new ClientCatalogPanel(clientId));
        tabs.addTab("Мои заказы", new ClientOrdersPanel(clientId));

        mainPanel.add(tabs, BorderLayout.CENTER);

        // Панель с кнопкой выхода
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exitButton = new JButton("Выход");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 12));
        exitButton.addActionListener(e -> exitToLogin());
        bottomPanel.add(exitButton);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void exitToLogin() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите выйти?",
                "Выход",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
