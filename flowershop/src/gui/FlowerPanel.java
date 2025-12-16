package gui;

import client.ServerClient;
import server.Flower;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public class FlowerPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Название", "Количество", "Цена"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public FlowerPanel() {
        setLayout(new BorderLayout());
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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
        table.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);
        
        refresh();

        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Изменить");
        JButton deleteButton = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить");

        addButton.addActionListener(e -> addFlower());
        editButton.addActionListener(e -> editFlower(table));
        deleteButton.addActionListener(e -> deleteFlower(table));
        refreshButton.addActionListener(e -> refresh());

        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addFlower() {
        String name = JOptionPane.showInputDialog(this, "Название цветка:");
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Количество:");
        if (qtyStr == null || qtyStr.trim().isEmpty()) {
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr.trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Количество не может быть отрицательным!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат количества!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String priceStr = JOptionPane.showInputDialog(this, "Цена:");
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr.trim().replace(",", "."));
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Цена не может быть отрицательной!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат цены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (serverClient.addFlower(name.trim(), 1, quantity, price)) {
            JOptionPane.showMessageDialog(this, "Цветок успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении цветка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editFlower(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите цветок для редактирования!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Получаем ID из объекта Flower, так как в таблице его нет
        List<Flower> flowers = serverClient.getAllFlowers();
        if (row >= flowers.size()) return;
        Flower selectedFlower = flowers.get(row);
        int id = selectedFlower.getId();
        
        String currentName = (String) model.getValueAt(row, 0);
        Integer currentQty = (Integer) model.getValueAt(row, 1);
        BigDecimal currentPrice = (BigDecimal) model.getValueAt(row, 2);

        String name = JOptionPane.showInputDialog(this, "Название цветка:", currentName);
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Количество:", currentQty);
        if (qtyStr == null || qtyStr.trim().isEmpty()) {
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr.trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Количество не может быть отрицательным!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат количества!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String priceStr = JOptionPane.showInputDialog(this, "Цена:", currentPrice);
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr.trim().replace(",", "."));
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Цена не может быть отрицательной!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат цены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (serverClient.updateFlower(id, name.trim(), quantity, price)) {
            JOptionPane.showMessageDialog(this, "Цветок успешно обновлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при обновлении цветка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFlower(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите цветок для удаления!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Получаем ID из объекта Flower
        List<Flower> flowers = serverClient.getAllFlowers();
        if (row >= flowers.size()) return;
        Flower selectedFlower = flowers.get(row);
        int id = selectedFlower.getId();
        String name = (String) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить цветок \"" + name + "\"?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (serverClient.deleteFlower(id)) {
                JOptionPane.showMessageDialog(this, "Цветок успешно удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Ошибка при удалении цветка!\n" +
                    "Возможно, цветок используется в заказах.",
                    "Ошибка удаления", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<Flower> flowers = serverClient.getAllFlowers();
            for (Flower f : flowers) {
                model.addRow(new Object[]{
                        f.getName() != null ? f.getName() : "",
                        f.getQuantity(),
                        f.getPrice() != null ? f.getPrice() : BigDecimal.ZERO
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
