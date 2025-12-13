package gui;

import server.Client;

import javax.swing.*;

public class ClientMainFrame extends JFrame {

    private final Client currentClient;

    public ClientMainFrame(Client client) {
        this.currentClient = client;

        setTitle("Личный кабинет клиента");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Букеты", new BouquetPanel());          // просмотр букетов
        tabs.addTab("Мои заказы", new ClientOrdersPanel(client)); // заказы клиента

        add(tabs);
    }
}
