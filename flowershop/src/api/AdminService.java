package api;

import server.Administrator;
import server.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public List<Administrator> getAll() {
        String sql = "SELECT id, full_name, phone, login, password FROM administrators ORDER BY id";
        List<Administrator> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Administrator admin = new Administrator(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("login"),
                        rs.getString("password")
                );
                list.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(String fullName, String phone, String login, String password) {
        String sql = "INSERT INTO administrators (full_name, phone, login, password) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, login);
            ps.setString(4, password);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Авторизация администратора
    public boolean login(String login, String password) {
        String sql = "SELECT * FROM administrators WHERE login = ? AND password = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // если есть хотя бы одна строка — логин верный
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

