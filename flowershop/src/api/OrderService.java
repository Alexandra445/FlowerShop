package api;

import server.Database;
import server.Order;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final FlowerService flowerService = new FlowerService();
    private final BouquetService bouquetService = new BouquetService();

    // Добавление заказа
    public boolean add(int clientId, Integer bouquetId, Integer flowerId, int bouquetCount,
                       int flowerCount, int statusId, Integer adminId, BigDecimal totalPrice,
                       Timestamp deliveryTime) {
        // КРИТИЧЕСКИ ВАЖНО: Используем порядок колонок, который соответствует SELECT запросу
        // В SELECT: o.id, o.client_id, o.bouquet_id, o.flower_id, o.status_id, o.order_date,
        // o.delivery_time, o.bouquet_count, o.flower_count, o.administrator_id, o.total_price
        // В INSERT (без id): client_id, bouquet_id, flower_id, status_id, order_date, delivery_time,
        // bouquet_count, flower_count, administrator_id, total_price
        String sql = "INSERT INTO orders (client_id, bouquet_id, flower_id, status_id, order_date, delivery_time, " +
                "bouquet_count, flower_count, administrator_id, total_price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id, bouquet_count, flower_count, flower_id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            // Проверка перед вставкой: хотя бы одно должно быть > 0
            if (bouquetCount <= 0 && flowerCount <= 0) {
                System.err.println("Ошибка: bouquet_count и flower_count оба <= 0");
                return false;
            }

            // Проверка логической согласованности: если указан ID, то количество должно быть > 0
            if (bouquetId != null && bouquetCount <= 0) {
                System.err.println("Ошибка: bouquet_id указан, но bouquet_count <= 0");
                return false;
            }
            if (flowerId != null && flowerCount <= 0) {
                System.err.println("Ошибка: flower_id указан, но flower_count <= 0");
                return false;
            }

            // Убеждаемся, что значения правильные (не отрицательные и не null)
            int finalBouquetCount = bouquetCount < 0 ? 0 : bouquetCount;
            int finalFlowerCount = flowerCount < 0 ? 0 : flowerCount;

            // Дополнительная проверка для CHECK constraint
            if (finalBouquetCount <= 0 && finalFlowerCount <= 0) {
                System.err.println("Ошибка: после нормализации bouquet_count и flower_count оба <= 0");
                return false;
            }

            // Логирование для отладки
            System.out.println("DEBUG: Установка параметров заказа:");
            System.out.println("  Входные параметры: bouquetCount=" + bouquetCount + ", flowerCount=" + flowerCount);
            System.out.println("  Нормализованные: finalBouquetCount=" + finalBouquetCount + ", finalFlowerCount=" + finalFlowerCount);
            System.out.println("  bouquetId=" + bouquetId + ", flowerId=" + flowerId);

            // Устанавливаем параметры в правильном порядке согласно SQL:
            // INSERT INTO orders (client_id, bouquet_id, flower_id, status_id, order_date, delivery_time,
            //                     bouquet_count, flower_count, administrator_id, total_price)
            ps.setInt(1, clientId);
            System.out.println("  Параметр 1 (client_id) = " + clientId);

            if (bouquetId != null) {
                ps.setInt(2, bouquetId);
                System.out.println("  Параметр 2 (bouquet_id) = " + bouquetId);
            } else {
                ps.setNull(2, Types.INTEGER);
                System.out.println("  Параметр 2 (bouquet_id) = NULL");
            }

            if (flowerId != null) {
                ps.setInt(3, flowerId);
                System.out.println("  Параметр 3 (flower_id) = " + flowerId);
            } else {
                ps.setNull(3, Types.INTEGER);
                System.out.println("  Параметр 3 (flower_id) = NULL");
            }

            ps.setInt(4, statusId);
            System.out.println("  Параметр 4 (status_id) = " + statusId);

            Timestamp orderDate = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(5, orderDate);
            System.out.println("  Параметр 5 (order_date) = " + orderDate);

            ps.setTimestamp(6, deliveryTime);
            System.out.println("  Параметр 6 (delivery_time) = " + deliveryTime);

            // ВАЖНО: CHECK constraint требует, чтобы хотя бы одно было > 0
            // Убеждаемся, что передаем 0, а не null (оба поля NOT NULL)
            // Если заказываем букет, bouquet_count > 0, flower_count = 0
            // Если заказываем цветы, bouquet_count = 0, flower_count > 0
            // Пробуем использовать setInt с явной проверкой, что значение не null
            if (finalBouquetCount < 0) {
                System.err.println("ОШИБКА: finalBouquetCount < 0: " + finalBouquetCount);
                return false;
            }
            ps.setInt(7, finalBouquetCount);
            System.out.println("  Параметр 7 (bouquet_count) = " + finalBouquetCount + " (тип: INTEGER, setInt)");

            if (finalFlowerCount < 0) {
                System.err.println("ОШИБКА: finalFlowerCount < 0: " + finalFlowerCount);
                return false;
            }
            // КРИТИЧЕСКИ ВАЖНО: используем setObject с явным указанием типа INTEGER
            // Это должно гарантировать, что значение не станет null
            ps.setObject(8, Integer.valueOf(finalFlowerCount), Types.INTEGER);
            System.out.println("  Параметр 8 (flower_count) = " + finalFlowerCount + " (тип: INTEGER, setObject с Types.INTEGER)");

            // ВАЖНО: Порядок должен соответствовать SQL: administrator_id, затем total_price
            if (adminId != null) {
                ps.setInt(9, adminId);
                System.out.println("  Параметр 9 (administrator_id) = " + adminId);
            } else {
                ps.setNull(9, Types.INTEGER);
                System.out.println("  Параметр 9 (administrator_id) = NULL");
            }

            ps.setBigDecimal(10, totalPrice);
            System.out.println("  Параметр 10 (total_price) = " + totalPrice);

            // Используем executeQuery вместо executeUpdate, так как у нас есть RETURNING
            ResultSet rs = ps.executeQuery();
            boolean added = false;
            if (rs.next()) {
                int insertedId = rs.getInt("id");
                int insertedBouquetCount = rs.getInt("bouquet_count");
                int insertedFlowerCount = rs.getInt("flower_count");
                int insertedFlowerId = rs.getInt("flower_id");
                System.out.println("  ВСТАВЛЕНО В БД: id=" + insertedId +
                        ", bouquet_count=" + insertedBouquetCount +
                        ", flower_count=" + insertedFlowerCount +
                        ", flower_id=" + insertedFlowerId);
                added = true;
            }

            if (flowerId != null && flowerCount > 0) {
                flowerService.reduceQuantity(flowerId, flowerCount);
            }

            return added;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при добавлении заказа: " + e.getMessage());
            System.err.println("Параметры: clientId=" + clientId + ", bouquetId=" + bouquetId +
                    ", flowerId=" + flowerId + ", bouquetCount=" + bouquetCount +
                    ", flowerCount=" + flowerCount);
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
                list.add(new Order(
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
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при загрузке заказов: " + e.getMessage());
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
                list.add(new Order(
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
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при загрузке заказов клиента: " + e.getMessage());
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

    // Получить statusId по имени
    public int getStatusIdByName(String name) {
        String sql = "SELECT id FROM order_statuses WHERE name = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}