package gui;

import client.ServerClient;
import server.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public class ClientOrdersPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Букет", "Цветы", "Кол-во", "Сумма", "Статус", "Дата заказа"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final int clientId;

    public ClientOrdersPanel(int clientId) {
        this.clientId = clientId;
        setLayout(new BorderLayout());
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Форматирование суммы
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            private NumberFormat format = NumberFormat.getNumberInstance();
            {
                format.setMinimumFractionDigits(2);
                format.setMaximumFractionDigits(2);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof BigDecimal) {
                    value = format.format(((BigDecimal) value).doubleValue());
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        table.getColumnModel().getColumn(3).setCellRenderer(priceRenderer);
        
        refresh();

        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refresh());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
    }

    public void refresh() {
        model.setRowCount(0);
        try {
            List<Order> orders = serverClient.getOrdersByClient(clientId);
            for (Order o : orders) {
                String itemName = "";
                int count = 0;
                
                if (o.getBouquetId() != null && o.getBouquetCount() > 0) {
                    itemName = o.getBouquetName() != null ? o.getBouquetName() : "Букет";
                    count = o.getBouquetCount();
                } else if (o.getFlowerId() != null && o.getFlowerCount() > 0) {
                    itemName = o.getFlowerName() != null ? o.getFlowerName() : "Цветок";
                    count = o.getFlowerCount();
                }
                
                String orderDateStr = o.getOrderDate() != null ? 
                    o.getOrderDate().toString().substring(0, 16) : "";
                
                model.addRow(new Object[]{
                        o.getBouquetId() != null ? itemName : "-",
                        o.getFlowerId() != null ? itemName : "-",
                        count > 0 ? count : "-",
                        o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO,
                        o.getStatusName() != null ? o.getStatusName() : "Неизвестно",
                        orderDateStr
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке заказов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
