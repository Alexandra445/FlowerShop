package gui;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Flower Shop");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Цветы", new FlowerPanel());
        tabbedPane.addTab("Букеты", new BouquetPanel());
        tabbedPane.addTab("Клиенты", new ClientPanel());
        tabbedPane.addTab("Заказы", new OrderPanel());
        tabbedPane.addTab("Администраторы", new AdminPanel());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
