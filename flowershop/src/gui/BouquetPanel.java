package gui;

import client.ServerClient;
import server.Bouquet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class BouquetPanel extends JPanel {

    private final ServerClient serverClient = new ServerClient();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Название", "Описание", "Цена"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public BouquetPanel() {
        setLayout(new BorderLayout());
        JTable table = new JTable(model);
        refresh();

        JButton add = new JButton("Добавить");
        JButton edit = new JButton("Изменить");
        JButton del = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить");

        add.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Название:");
            if (name == null || name.trim().isEmpty()) return;
            String desc = JOptionPane.showInputDialog("Описание:");
            if (desc == null) return;
            String priceStr = JOptionPane.showInputDialog("Цена:");
            if (priceStr == null || priceStr.trim().isEmpty()) return;
            try {
                BigDecimal price = new BigDecimal(priceStr.trim().replace(",", "."));
                if (serverClient.addBouquet(name, desc, price)) {
                    JOptionPane.showMessageDialog(this, "Букет успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при добавлении букета!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Неверный формат цены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        edit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Выберите букет для редактирования!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            List<Bouquet> bouquets = serverClient.getAllBouquets();
            if (r >= bouquets.size()) return;
            Bouquet selectedBouquet = bouquets.get(r);
            int id = selectedBouquet.getId();
            
            String name = JOptionPane.showInputDialog("Название:", model.getValueAt(r, 0));
            if (name == null || name.trim().isEmpty()) return;
            String desc = JOptionPane.showInputDialog("Описание:", model.getValueAt(r, 1));
            if (desc == null) return;
            String priceStr = JOptionPane.showInputDialog("Цена:", model.getValueAt(r, 2));
            if (priceStr == null || priceStr.trim().isEmpty()) return;
            try {
                BigDecimal price = new BigDecimal(priceStr.trim().replace(",", "."));
                if (serverClient.updateBouquet(id, name, desc, price)) {
                    JOptionPane.showMessageDialog(this, "Букет успешно обновлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении букета!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Неверный формат цены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Выберите букет для удаления!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            List<Bouquet> bouquets = serverClient.getAllBouquets();
            if (r >= bouquets.size()) return;
            Bouquet selectedBouquet = bouquets.get(r);
            int id = selectedBouquet.getId();
            String name = (String) model.getValueAt(r, 0);
            
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Вы уверены, что хотите удалить букет \"" + name + "\"?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (serverClient.deleteBouquet(id)) {
                    JOptionPane.showMessageDialog(this, "Букет успешно удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Ошибка при удалении букета!\n" +
                        "Возможно, букет используется в заказах.",
                        "Ошибка удаления", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshButton.addActionListener(e -> refresh());

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(edit);
        buttons.add(del);
        buttons.add(refreshButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<Bouquet> bouquets = serverClient.getAllBouquets();
            for (Bouquet b : bouquets) {
                model.addRow(new Object[]{
                        b.getName() != null ? b.getName() : "",
                        b.getDescription() != null ? b.getDescription() : "",
                        b.getPrice() != null ? b.getPrice() : BigDecimal.ZERO
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
