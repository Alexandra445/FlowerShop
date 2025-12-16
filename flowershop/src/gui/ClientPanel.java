package gui;

import client.ServerClient;
import server.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    private DefaultTableModel tableModel;
    private JTable table;

    public ClientPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "ФИО", "Телефон", "Логин", "Пароль"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Добавить клиента");
        JButton refreshButton = new JButton("Обновить");
        buttonsPanel.add(addButton);
        buttonsPanel.add(refreshButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addClient());
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Client> clients = serverClient.getAllClients();
        for (Client c : clients) {
            tableModel.addRow(new Object[]{c.getId(), c.getFullName(), c.getPhone(), c.getLogin(), c.getPassword()});
        }
    }

    private void addClient() {
        String fullName = JOptionPane.showInputDialog(this, "ФИО клиента:");
        if (fullName == null || fullName.isEmpty()) return;

        String phone = JOptionPane.showInputDialog(this, "Телефон:");
        String login = JOptionPane.showInputDialog(this, "Логин:");
        String password = JOptionPane.showInputDialog(this, "Пароль:");

        if (serverClient.addClient(fullName, phone, login, password)) {
            JOptionPane.showMessageDialog(this, "Клиент добавлен!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении.");
        }
    }
}
