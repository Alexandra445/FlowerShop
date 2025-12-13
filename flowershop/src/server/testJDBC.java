package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class testJDBC {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/flowerstore";
        String user = "postgres"; // твой логин
        String password = "postgres123"; // твой пароль

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Соединение с PostgreSQL установлено!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
