package api;

import server.Database;
import server.Flower;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlowerService {

    public List<Flower> getAll() {
        List<Flower> list = new ArrayList<>();
        String sql = "SELECT id, name, type_id, quantity, price FROM flowers ORDER BY id";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Flower(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("type_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(String name, int typeId, int quantity, BigDecimal price) {
        // Добавляем remaining_quantity и price_per_unit (обычно равно price)
        String sql = "INSERT INTO flowers (name, type_id, quantity, price, remaining_quantity, price_per_unit) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, typeId);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, price);
            ps.setInt(5, quantity); // remaining_quantity = quantity при создании
            ps.setBigDecimal(6, price); // price_per_unit = price
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при добавлении цветка: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String name, int quantity, BigDecimal price) {
        String sql = "UPDATE flowers SET name=?, quantity=?, price=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setBigDecimal(3, price);
            ps.setInt(4, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверяет, используется ли цветок в заказах
     */
    public boolean isUsedInOrders(int id) {
        String sql = "SELECT COUNT(*) FROM orders WHERE flower_id = ?";
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
     * Удаляет цветок. Возвращает строку с результатом:
     * - "success" - успешно удален
     * - "used_in_orders" - используется в заказах, нельзя удалить
     * - "error" - ошибка при удалении
     */
    public String delete(int id) {
        // Проверяем, используется ли цветок в заказах
        if (isUsedInOrders(id)) {
            return "used_in_orders";
        }

        String sql = "DELETE FROM flowers WHERE id=?";
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

    public boolean reduceQuantity(int flowerId, int amount) {
        String sql = "UPDATE flowers SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, amount);
            ps.setInt(2, flowerId);
            ps.setInt(3, amount);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

