package gui;

import client.ServerClient;
import server.Order;
import server.OrderStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public class AdminOrdersPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Клиент", "Телефон", "Букет", "Цветы", "Кол-во", "Сумма", "Статус", "Дата заказа", "Сделать к"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };


    public AdminOrdersPanel() {
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
        table.getColumnModel().getColumn(5).setCellRenderer(priceRenderer); // "Сумма" - 6-й столбец (индекс 5)
        
        refresh();

        JButton changeStatusButton = new JButton("Изменить статус");
        JButton refreshButton = new JButton("Обновить");

        changeStatusButton.addActionListener(e -> changeStatus(table));
        refreshButton.addActionListener(e -> refresh());

        JPanel buttons = new JPanel();
        buttons.add(changeStatusButton);
        buttons.add(refreshButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void changeStatus(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите заказ для изменения статуса!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Order> orders = serverClient.getAllOrders();
        if (row >= orders.size()) return;
        int orderId = orders.get(row).getId();
        String currentStatus = (String) model.getValueAt(row, 6); // Статус - 7-й столбец (индекс 6)

        List<OrderStatus> statuses = serverClient.getAllOrderStatuses();
        if (statuses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Не удалось загрузить статусы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] statusNames = new String[statuses.size()];
        for (int i = 0; i < statuses.size(); i++) {
            statusNames[i] = statuses.get(i).getName();
        }

        int currentIndex = 0;
        for (int i = 0; i < statusNames.length; i++) {
            if (statusNames[i].equals(currentStatus)) {
                currentIndex = i;
                break;
            }
        }

        String selectedStatus = (String) JOptionPane.showInputDialog(
                this,
                "Выберите новый статус заказа:",
                "Изменение статуса",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusNames,
                statusNames[currentIndex]
        );

        if (selectedStatus != null && !selectedStatus.equals(currentStatus)) {
            int statusId = -1;
            for (OrderStatus status : statuses) {
                if (status.getName().equals(selectedStatus)) {
                    statusId = status.getId();
                    break;
                }
            }

            if (statusId != -1) {
                if (serverClient.updateOrderStatus(orderId, statusId)) {
                    JOptionPane.showMessageDialog(this, "Статус заказа изменен на: " + selectedStatus, "Успех", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при изменении статуса заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<Order> orders = serverClient.getAllOrders();
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
                String deliveryTimeStr = o.getDeliveryTime() != null ? 
                    o.getDeliveryTime().toString().substring(0, 16) : "";
                
                model.addRow(new Object[]{
                        o.getClientName() != null ? o.getClientName() : "Клиент",
                        o.getClientPhone() != null ? o.getClientPhone() : "-",
                        o.getBouquetId() != null ? itemName : "-",
                        o.getFlowerId() != null ? itemName : "-",
                        count > 0 ? count : "-",
                        o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO,
                        o.getStatusName() != null ? o.getStatusName() : "Неизвестно",
                        orderDateStr,
                        deliveryTimeStr
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке заказов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
