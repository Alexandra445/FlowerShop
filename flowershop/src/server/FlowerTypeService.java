package server;

import java.sql.*;

public class FlowerTypeService {
    public String getTypeName(int typeId) {
        String sql = "SELECT name FROM flower_types WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Неизвестно";
    }
}
