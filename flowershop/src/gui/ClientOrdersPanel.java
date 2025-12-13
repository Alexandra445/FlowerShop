package gui;

import server.FlowerService;
import server.BouquetService;
import server.Order;
import server.OrderService;
import server.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ClientOrdersPanel extends JPanel {

    private final Client client;
    private final OrderService orderService = new OrderService();
    private final FlowerService flowerService = new FlowerService();
    private final BouquetService bouquetService = new BouquetService();
    private final DefaultTableModel tableModel = new DefaultTableModel();

    private JTable table;

    public ClientOrdersPanel(Client client) {
        this.client = client;
        setLayout(new BorderLayout());

        // Верхняя панель для оформления заказа
        JPanel orderPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JComboBox<String> bouquetBox = new JComboBox<>();
        bouquetService.getAll().forEach(b -> bouquetBox.addItem(b.getName()));
        orderPanel.add(new JLabel("Выберите букет:"));
        orderPanel.add(bouquetBox);

        JComboBox<String> flowerBox = new JComboBox<>();
        flowerService.getAll().forEach(f -> flowerBox.addItem(f.getName()));
        orderPanel.add(new JLabel("Выберите цветок:"));
        orderPanel.add(flowerBox);

        JTextField bouquetCountField = new JTextField("0");
        orderPanel.add(new JLabel("Количество букетов:"));
        orderPanel.add(bouquetCountField);

        JTextField flowerCountField = new JTextField("0");
        orderPanel.add(new JLabel("Количество цветов:"));
        orderPanel.add(flowerCountField);

        JButton orderButton = new JButton("Сделать заказ");
        orderButton.addActionListener(e -> {
            try {
                int bouquetCount = Integer.parseInt(bouquetCountField.getText());
                int flowerCount = Integer.parseInt(flowerCountField.getText());
                Integer bouquetId = bouquetCount > 0 ? bouquetService.getByName((String) bouquetBox.getSelectedItem()).getId() : null;
                Integer flowerId = flowerCount > 0 ? flowerService.getByName((String) flowerBox.getSelectedItem()).getId() : null;
                BigDecimal totalPrice = BigDecimal.ZERO;
                if (bouquetId != null) totalPrice = totalPrice.add(bouquetService.getById(bouquetId).getPrice().multiply(BigDecimal.valueOf(bouquetCount)));
                if (flowerId != null) totalPrice = totalPrice.add(flowerService.getById(flowerId).getPrice().multiply(BigDecimal.valueOf(flowerCount)));

                orderService.add(client.getId(), bouquetId, flowerId, bouquetCount, flowerCount, 1, null, totalPrice, new Timestamp(System.currentTimeMillis()));
                JOptionPane.showMessageDialog(this, "Заказ оформлен!");
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при оформлении заказа: " + ex.getMessage());
            }
        });

        add(orderPanel, BorderLayout.NORTH);
        add(orderButton, BorderLayout.CENTER);

        // Таблица заказов
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Букет", "Цветок", "Кол-во букетов", "Кол-во цветов", "Статус", "Цена"});
        List<Order> orders = orderService.getAllByClient(client.getId());
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
