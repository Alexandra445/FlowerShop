package gui;

import server.Order;
import server.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminOrdersPanel extends JPanel {

    private final OrderService orderService = new OrderService();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private JTable table;

    public AdminOrdersPanel() {
        setLayout(new BorderLayout());

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refreshTable());
        add(refreshButton, BorderLayout.NORTH);

        JButton changeStatusButton = new JButton("Изменить статус");
        changeStatusButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int orderId = (int) tableModel.getValueAt(row, 0);
                String statusStr = JOptionPane.showInputDialog("Введите новый статус (ID):");
                try {
                    int statusId = Integer.parseInt(statusStr);
                    orderService.updateStatus(orderId, statusId);
                    refreshTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный ID статуса");
                }
            }
        });
        add(changeStatusButton, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Букет", "Цветок", "Кол-во букетов", "Кол-во цветов", "Статус", "Цена"});
        List<Order> orders = orderService.getAll();
        for (Order o : orders) {
            tableModel.addRow(new Object[]{
                    o.getId(),
                    o.getBouquetName(),
                    o.getFlowerName(),
                    o.getBouquetCount(),
                    o.getFlowerCount(),
                    o.getStatusName(),
                    o.getTotalPrice()
            });
        }
    }
}
