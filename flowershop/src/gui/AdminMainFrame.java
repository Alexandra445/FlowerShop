package gui;

import javax.swing.*;

public class AdminMainFrame extends JFrame {

    public AdminMainFrame() {
        setTitle("Панель администратора");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Цветы", new FlowerPanel());
        tabs.addTab("Букеты", new BouquetPanel());
        tabs.addTab("Клиенты", new ClientPanel());
        tabs.addTab("Заказы", new AdminOrdersPanel());
        tabs.addTab("Администраторы", new AdminPanel());

        add(tabs);
    }
}
