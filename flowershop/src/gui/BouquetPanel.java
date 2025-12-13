package gui;

import server.Bouquet;
import server.BouquetService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class BouquetPanel extends JPanel {

    private final BouquetService bouquetService = new BouquetService();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private JTable table;

    public BouquetPanel() {
        setLayout(new BorderLayout());

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        addPanel.add(nameField);
        addPanel.add(descriptionField);
        addPanel.add(priceField);

        JButton addButton = new JButton("Добавить букет");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String desc = descriptionField.getText();
                BigDecimal price = new BigDecimal(priceField.getText());
                if (bouquetService.add(name, desc, price)) {
                    JOptionPane.showMessageDialog(this, "Букет добавлен");
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
            }
        });

        add(addPanel, BorderLayout.NORTH);
        add(addButton, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Название", "Описание", "Цена"});
        List<Bouquet> bouquets = bouquetService.getAll();
        for (Bouquet b : bouquets) {
            tableModel.addRow(new Object[]{b.getId(), b.getName(), b.getDescription(), b.getPrice()});
        }
    }
}
