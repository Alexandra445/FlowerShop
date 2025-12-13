package gui;

import server.Administrator;
import server.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private AdminService adminService = new AdminService();
    private DefaultTableModel tableModel;
    private JTable table;

    public AdminPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "ФИО", "Телефон", "Логин", "Пароль"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton refreshButton = new JButton("Обновить");
        buttonsPanel.add(addButton);
        buttonsPanel.add(refreshButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addAdmin());
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Administrator> admins = adminService.getAll();
        for (Administrator a : admins) {
            tableModel.addRow(new Object[]{a.getId(), a.getFullName(), a.getPhone(), a.getLogin(), a.getPassword()});
        }
    }

    private void addAdmin() {
        String fullName = JOptionPane.showInputDialog(this, "ФИО администратора:");
        if (fullName == null || fullName.isEmpty()) return;

        String phone = JOptionPane.showInputDialog(this, "Телефон:");
        String login = JOptionPane.showInputDialog(this, "Логин:");
        String password = JOptionPane.showInputDialog(this, "Пароль:");

        if (adminService.add(fullName, phone, login, password)) {
            JOptionPane.showMessageDialog(this, "Администратор добавлен!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении.");
        }
    }
}
