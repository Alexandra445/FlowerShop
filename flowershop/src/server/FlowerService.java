package server;

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

    public Flower getById(int id) {
        String sql = "SELECT id, name, type_id, quantity, price FROM flowers WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Flower(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("type_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Flower getByName(String name) {
        String sql = "SELECT id, name, type_id, quantity, price FROM flowers WHERE name = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Flower(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("type_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public boolean add(String name, int quantity, int typeId, BigDecimal price) {
        String sql = "INSERT INTO flowers (name, quantity, type_id, price) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, typeId);
            ps.setBigDecimal(4, price);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
