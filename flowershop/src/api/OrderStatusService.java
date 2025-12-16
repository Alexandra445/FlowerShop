package api;

import server.Database;
import server.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderStatusService {

    public List<OrderStatus> getAll() {
        List<OrderStatus> list = new ArrayList<>();
        String sql = "SELECT id, name FROM order_statuses ORDER BY id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new OrderStatus(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

