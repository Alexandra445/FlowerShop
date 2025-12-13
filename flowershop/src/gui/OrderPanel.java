package gui;

import server.Order;
import server.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {

    private OrderService orderService = new OrderService();
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
        List<Order> orders = orderService.getAll();
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
