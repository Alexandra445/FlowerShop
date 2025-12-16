package api;

import server.Bouquet;
import server.Database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BouquetService {

    public List<Bouquet> getAll() {
        List<Bouquet> list = new ArrayList<>();
        String sql = "SELECT id, name, description, price FROM bouquets ORDER BY id";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Bouquet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(String name, String description, BigDecimal price) {
        String sql = "INSERT INTO bouquets (name, description, price) VALUES (?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setBigDecimal(3, price);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String name, String description, BigDecimal price) {
        String sql = "UPDATE bouquets SET name=?, description=?, price=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setBigDecimal(3, price);
            ps.setInt(4, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверяет, используется ли букет в заказах
     */
    public boolean isUsedInOrders(int id) {
        String sql = "SELECT COUNT(*) FROM orders WHERE bouquet_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Удаляет букет. Возвращает строку с результатом:
     * - "success" - успешно удален
     * - "used_in_orders" - используется в заказах, нельзя удалить
     * - "error" - ошибка при удалении
     */
    public String delete(int id) {
        // Проверяем, используется ли букет в заказах
        if (isUsedInOrders(id)) {
            return "used_in_orders";
        }

        String sql = "DELETE FROM bouquets WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            if (ps.executeUpdate() == 1) {
                return "success";
            } else {
                return "error";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Проверяем, это ошибка внешнего ключа или другая
            if (e.getMessage() != null && e.getMessage().contains("нарушает ограничение внешнего ключа")) {
                return "used_in_orders";
            }
            return "error";
        }
    }
}

