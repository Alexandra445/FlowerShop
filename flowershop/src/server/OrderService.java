package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class OrderService {

    private final FlowerService flowerService = new FlowerService();
    private final BouquetService bouquetService = new BouquetService();

    // Добавление заказа (букет или цветы)
    public boolean add(int clientId, Integer bouquetId, Integer flowerId, int bouquetCount,
                       int flowerCount, int statusId, Integer adminId, BigDecimal totalPrice,
                       Timestamp deliveryTime) {
        String sql = "INSERT INTO orders (client_id, bouquet_id, flower_id, status_id, order_date, delivery_time, " +
                "bouquet_count, flower_count, administrator_id, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            if (bouquetId != null) ps.setInt(2, bouquetId); else ps.setNull(2, Types.INTEGER);
            if (flowerId != null) ps.setInt(3, flowerId); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, statusId);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(6, deliveryTime);
            ps.setInt(7, bouquetCount);
            ps.setInt(8, flowerCount);
            if (adminId != null) ps.setInt(9, adminId); else ps.setNull(9, Types.INTEGER);
            ps.setBigDecimal(10, totalPrice);

            boolean added = ps.executeUpdate() == 1;

            // уменьшение количества цветов на складе, если заказ отдельного цветка
            if (flowerId != null && flowerCount > 0) {
                flowerService.reduceQuantity(flowerId, flowerCount);
            }

            return added;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получить все заказы (для админа)
    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.client_id, o.bouquet_id, o.flower_id, o.status_id, o.order_date, " +
                "o.delivery_time, o.bouquet_count, o.flower_count, o.administrator_id, o.total_price, " +
                "b.name AS bouquet_name, f.name AS flower_name, s.name AS status_name, c.full_name AS client_name, " +
                "c.phone AS client_phone " +
                "FROM orders o " +
                "LEFT JOIN bouquets b ON o.bouquet_id = b.id " +
                "LEFT JOIN flowers f ON o.flower_id = f.id " +
                "LEFT JOIN order_statuses s ON o.status_id = s.id " +
                "LEFT JOIN clients c ON o.client_id = c.id " +
                "ORDER BY o.id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order o = new Order(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        (Integer) rs.getObject("bouquet_id"),
                        (Integer) rs.getObject("flower_id"),
                        rs.getInt("bouquet_count"),
                        rs.getInt("flower_count"),
                        rs.getInt("status_id"),
                        (Integer) rs.getObject("administrator_id"),
                        rs.getBigDecimal("total_price"),
                        rs.getTimestamp("order_date"),
                        rs.getTimestamp("delivery_time"),
                        rs.getString("bouquet_name"),
                        rs.getString("flower_name"),
                        rs.getString("status_name"),
                        rs.getString("client_name"),
                        rs.getString("client_phone")
                );
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Получить заказы конкретного клиента
    public List<Order> getAllByClient(int clientId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.client_id, o.bouquet_id, o.flower_id, o.status_id, o.order_date, " +
                "o.delivery_time, o.bouquet_count, o.flower_count, o.administrator_id, o.total_price, " +
                "b.name AS bouquet_name, f.name AS flower_name, s.name AS status_name, c.full_name AS client_name, " +
                "c.phone AS client_phone " +
                "FROM orders o " +
                "LEFT JOIN bouquets b ON o.bouquet_id = b.id " +
                "LEFT JOIN flowers f ON o.flower_id = f.id " +
                "LEFT JOIN order_statuses s ON o.status_id = s.id " +
                "LEFT JOIN clients c ON o.client_id = c.id " +
                "WHERE o.client_id = ? ORDER BY o.id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        (Integer) rs.getObject("bouquet_id"),
                        (Integer) rs.getObject("flower_id"),
                        rs.getInt("bouquet_count"),
                        rs.getInt("flower_count"),
                        rs.getInt("status_id"),
                        (Integer) rs.getObject("administrator_id"),
                        rs.getBigDecimal("total_price"),
                        rs.getTimestamp("order_date"),
                        rs.getTimestamp("delivery_time"),
                        rs.getString("bouquet_name"),
                        rs.getString("flower_name"),
                        rs.getString("status_name"),
                        rs.getString("client_name"),
                        rs.getString("client_phone")
                );
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Изменение статуса заказа
    public void updateStatus(int orderId, int statusId) {
        String sql = "UPDATE orders SET status_id = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
