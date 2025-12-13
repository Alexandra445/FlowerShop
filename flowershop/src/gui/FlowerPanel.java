package gui;

import server.Flower;
import server.FlowerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FlowerPanel extends JPanel {

    public FlowerPanel() {
        setLayout(new BorderLayout());

        FlowerService service = new FlowerService();
        JTable table = new JTable();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Название", "Тип", "Цена", "Количество"}, 0
        );

        for (Flower f : service.getAll()) {
            model.addRow(new Object[]{
                    f.getName(),
                    f.getTypeName(),
                    f.getPrice(),
                    f.getQuantity()
            });
        }

        table.setModel(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
