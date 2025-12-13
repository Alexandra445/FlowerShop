package server;

import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            System.out.println("Пробую подключиться...");
            Connection conn = Database.getConnection();
            System.out.println("УСПЕХ! Подключение установлено: " + conn);
            conn.close();
        } catch (Exception ex) {
            System.out.println("ОШИБКА: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
