package services;

import utils.MyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FavoritePlanService {

    private Connection getConnection() {
        return MyDB.getInstance().getConnection();
    }

    public Set<Integer> getFavoritePlanIds(int userId) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT plan_id FROM favorite_plans WHERE user_id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("plan_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public boolean isFavorite(int userId, int planId) {
        String sql = "SELECT 1 FROM favorite_plans WHERE user_id = ? AND plan_id = ? LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, planId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void add(int userId, int planId) {
        String sql = "INSERT INTO favorite_plans (user_id, plan_id) VALUES (?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, planId);
            ps.executeUpdate();
            createCoachLikeNotification(userId, planId);
        } catch (SQLException e) {
            if (!"23000".equals(e.getSQLState())) {
                e.printStackTrace();
            }
        }
    }

    public void remove(int userId, int planId) {
        String sql = "DELETE FROM favorite_plans WHERE user_id = ? AND plan_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, planId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Integer> getFavoriteCountsByPlan() {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        String sql = """
                SELECT plan_id, COUNT(*) AS fav_count
                FROM favorite_plans
                GROUP BY plan_id
                ORDER BY fav_count DESC, plan_id DESC
                """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getInt("plan_id"), rs.getInt("fav_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counts;
    }

    private void createCoachLikeNotification(int patientId, int planId) {
        String ownerSql = """
                SELECT p.user_id AS coach_id, p.title, u.role
                FROM coaching_plan p
                JOIN users u ON u.id = p.user_id
                WHERE p.plan_id = ?
                """;
        String patientSql = "SELECT name FROM users WHERE id = ?";
        String insertSql = """
                INSERT INTO coach_notifications (coach_id, patient_id, plan_id, message, is_read, created_at)
                VALUES (?, ?, ?, ?, 0, NOW())
                """;

        try (PreparedStatement ownerPs = getConnection().prepareStatement(ownerSql)) {
            ownerPs.setInt(1, planId);

            try (ResultSet ownerRs = ownerPs.executeQuery()) {
                if (!ownerRs.next()) {
                    return;
                }

                int coachId = ownerRs.getInt("coach_id");
                String ownerRole = ownerRs.getString("role");
                String planTitle = ownerRs.getString("title");

                if (coachId == patientId || !"COACH".equalsIgnoreCase(ownerRole)) {
                    return;
                }

                String patientName = "A patient";
                try (PreparedStatement patientPs = getConnection().prepareStatement(patientSql)) {
                    patientPs.setInt(1, patientId);
                    try (ResultSet patientRs = patientPs.executeQuery()) {
                        if (patientRs.next() && patientRs.getString("name") != null && !patientRs.getString("name").isBlank()) {
                            patientName = patientRs.getString("name");
                        }
                    }
                }

                String message = patientName + " liked your plan \"" + (planTitle == null ? "Untitled plan" : planTitle) + "\".";

                try (PreparedStatement insertPs = getConnection().prepareStatement(insertSql)) {
                    insertPs.setInt(1, coachId);
                    insertPs.setInt(2, patientId);
                    insertPs.setInt(3, planId);
                    insertPs.setString(4, message);
                    insertPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
