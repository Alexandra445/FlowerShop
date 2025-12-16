package gui;

import client.ServerClient;
import server.Bouquet;
import server.Flower;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClientCatalogPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    
    private final int clientId;
    
    private DefaultTableModel flowersModel;
    private DefaultTableModel bouquetsModel;
    private JTable flowersTable;
    private JTable bouquetsTable;

    public ClientCatalogPanel(int clientId) {
        this.clientId = clientId;
        setLayout(new BorderLayout());
        
        JTabbedPane tabs = new JTabbedPane();
        
        // Вкладка с цветами
        JPanel flowersPanel = createFlowersPanel();
        tabs.addTab("Цветы", flowersPanel);
        
        // Вкладка с букетами
        JPanel bouquetsPanel = createBouquetsPanel();
        tabs.addTab("Букеты", bouquetsPanel);
        
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createFlowersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        flowersModel = new DefaultTableModel(
                new String[]{"Название", "Количество", "Цена за шт."}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        flowersTable = new JTable(flowersModel);
        flowersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Форматирование цены
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
        flowersTable.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);
        
        refreshFlowers();
        
        JButton orderButton = new JButton("Заказать цветы");
        orderButton.addActionListener(e -> orderFlower());
        
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refreshFlowers());
        
        JPanel buttons = new JPanel();
        buttons.add(orderButton);
        buttons.add(refreshButton);
        
        panel.add(new JScrollPane(flowersTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createBouquetsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        bouquetsModel = new DefaultTableModel(
                new String[]{"Название", "Описание", "Цена"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bouquetsTable = new JTable(bouquetsModel);
        bouquetsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Форматирование цены
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
        bouquetsTable.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);
        
        refreshBouquets();
        
        JButton orderButton = new JButton("Заказать букет");
        orderButton.addActionListener(e -> orderBouquet());
        
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refreshBouquets());
        
        JPanel buttons = new JPanel();
        buttons.add(orderButton);
        buttons.add(refreshButton);
        
        panel.add(new JScrollPane(bouquetsTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Диалог для выбора даты и времени доставки
     */
    private Timestamp selectDeliveryTime() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        // Дата
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1); // По умолчанию завтра
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy");
        dateSpinner.setEditor(dateEditor);
        
        // Время
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        
        panel.add(new JLabel("Сделать заказ к:"));
        panel.add(dateSpinner);
        panel.add(new JLabel("Время:"));
        panel.add(timeSpinner);
        
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Выберите дату и время",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            Date date = (Date) dateSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();
            
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(date);
            
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            
            Calendar finalCal = Calendar.getInstance();
            finalCal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
            finalCal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
            finalCal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));
            finalCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            finalCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            finalCal.set(Calendar.SECOND, 0);
            finalCal.set(Calendar.MILLISECOND, 0);
            
            // Проверяем, что время не в прошлом
            if (finalCal.getTimeInMillis() < System.currentTimeMillis()) {
                JOptionPane.showMessageDialog(this, "Время доставки не может быть в прошлом!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            return new Timestamp(finalCal.getTimeInMillis());
        }
        
        return null;
    }

    private void orderFlower() {
        int row = flowersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите цветок для заказа!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Flower> flowers = serverClient.getAllFlowers();
        if (row >= flowers.size()) return;
        
        Flower selectedFlower = flowers.get(row);
        int flowerId = selectedFlower.getId();
        String flowerName = selectedFlower.getName();
        Integer availableQty = selectedFlower.getQuantity();
        BigDecimal price = selectedFlower.getPrice();

        if (availableQty == null || availableQty <= 0) {
            JOptionPane.showMessageDialog(this, "Этот цветок недоступен!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String countStr = JOptionPane.showInputDialog(this, 
                "Сколько штук заказать?\nДоступно: " + availableQty + "\nЦена за шт.: " + price,
                "1");
        
        if (countStr == null || countStr.trim().isEmpty()) {
            return;
        }

        int count;
        try {
            count = Integer.parseInt(countStr.trim());
            if (count <= 0) {
                JOptionPane.showMessageDialog(this, "Количество должно быть больше нуля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (count > availableQty) {
                JOptionPane.showMessageDialog(this, "Недостаточно цветов в наличии! Доступно: " + availableQty, "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат количества!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(count));

        // Выбираем время доставки
        Timestamp deliveryTime = selectDeliveryTime();
        if (deliveryTime == null) {
            return; // Пользователь отменил
        }

        // Статус "ожидает подтверждения" - ищем по имени
        int statusId = serverClient.getStatusIdByName("ожидает подтверждения");
        if (statusId == -1) {
            // Если не найден, используем id = 1 по умолчанию
            statusId = 1;
        }

        if (serverClient.addOrder(clientId, null, flowerId, 0, count, statusId, null, totalPrice, 
                deliveryTime.toString())) {
            JOptionPane.showMessageDialog(this, 
                    "Заказ оформлен!\nЦветок: " + flowerName + "\nКоличество: " + count + "\nСумма: " + totalPrice,
                    "Успех", JOptionPane.INFORMATION_MESSAGE);
            refreshFlowers();
            // Обновляем панель заказов
            SwingUtilities.invokeLater(() -> {
                Container parent = getParent();
                while (parent != null && !(parent instanceof JTabbedPane)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JTabbedPane) {
                    JTabbedPane tabs = (JTabbedPane) parent;
                    for (int i = 0; i < tabs.getTabCount(); i++) {
                        Component comp = tabs.getComponentAt(i);
                        if (comp instanceof ClientOrdersPanel) {
                            ((ClientOrdersPanel) comp).refresh();
                            break;
                        }
                    }
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при оформлении заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void orderBouquet() {
        int row = bouquetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите букет для заказа!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Bouquet> bouquets = serverClient.getAllBouquets();
        if (row >= bouquets.size()) return;
        
        Bouquet selectedBouquet = bouquets.get(row);
        int bouquetId = selectedBouquet.getId();
        String bouquetName = selectedBouquet.getName();
        BigDecimal price = selectedBouquet.getPrice();

        String countStr = JOptionPane.showInputDialog(this, 
                "Сколько букетов заказать?\nБукет: " + bouquetName + "\nЦена: " + price,
                "1");
        
        if (countStr == null || countStr.trim().isEmpty()) {
            return;
        }

        int count;
        try {
            count = Integer.parseInt(countStr.trim());
            if (count <= 0) {
                JOptionPane.showMessageDialog(this, "Количество должно быть больше нуля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат количества!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(count));

        // Выбираем время доставки
        Timestamp deliveryTime = selectDeliveryTime();
        if (deliveryTime == null) {
            return; // Пользователь отменил
        }

        // Статус "ожидает подтверждения" - ищем по имени
        int statusId = serverClient.getStatusIdByName("ожидает подтверждения");
        if (statusId == -1) {
            // Если не найден, используем id = 1 по умолчанию
            statusId = 1;
        }

        if (serverClient.addOrder(clientId, bouquetId, null, count, 0, statusId, null, totalPrice, 
                deliveryTime.toString())) {
            JOptionPane.showMessageDialog(this, 
                    "Заказ оформлен!\nБукет: " + bouquetName + "\nКоличество: " + count + "\nСумма: " + totalPrice,
                    "Успех", JOptionPane.INFORMATION_MESSAGE);
            refreshBouquets();
            // Обновляем панель заказов
            SwingUtilities.invokeLater(() -> {
                Container parent = getParent();
                while (parent != null && !(parent instanceof JTabbedPane)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JTabbedPane) {
                    JTabbedPane tabs = (JTabbedPane) parent;
                    for (int i = 0; i < tabs.getTabCount(); i++) {
                        Component comp = tabs.getComponentAt(i);
                        if (comp instanceof ClientOrdersPanel) {
                            ((ClientOrdersPanel) comp).refresh();
                            break;
                        }
                    }
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при оформлении заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshFlowers() {
        flowersModel.setRowCount(0);
        try {
            List<Flower> flowers = serverClient.getAllFlowers();
            for (Flower f : flowers) {
                flowersModel.addRow(new Object[]{
                        f.getName() != null ? f.getName() : "",
                        f.getQuantity(),
                        f.getPrice() != null ? f.getPrice() : BigDecimal.ZERO
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке цветов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshBouquets() {
        bouquetsModel.setRowCount(0);
        try {
            List<Bouquet> bouquets = serverClient.getAllBouquets();
            for (Bouquet b : bouquets) {
                bouquetsModel.addRow(new Object[]{
                        b.getName() != null ? b.getName() : "",
                        b.getDescription() != null ? b.getDescription() : "",
                        b.getPrice() != null ? b.getPrice() : BigDecimal.ZERO
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке букетов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


