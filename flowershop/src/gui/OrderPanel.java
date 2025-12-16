package gui;

import client.ServerClient;
import server.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    private DefaultTableModel tableModel;
    private JTable table;

    public OrderPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Клиент ID", "Букет ID", "Статус ID", "Дата заказа", "Время доставки", "Кол-во", "Админ ID", "Сумма"}, 0
        );
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Обновить");
        add(refreshButton, BorderLayout.SOUTH);
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Order> orders = serverClient.getAllOrders();
        for (Order o : orders) {
            tableModel.addRow(new Object[]{
                    o.getId(),
                    o.getClientId(),
                    o.getBouquetId(),
                    o.getStatusId(),
                    o.getOrderDate(),
                    o.getDeliveryTime(),
                    o.getBouquetCount(),
                    o.getAdministratorId(),
                    o.getTotalPrice()
            });
        }
    }
}
