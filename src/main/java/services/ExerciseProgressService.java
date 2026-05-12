package services;

import utils.MyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExerciseProgressService {

    private Connection getConnection() {
        return MyDB.getInstance().getConnection();
    }

    // Returns the user's status for one exercise. If none exists, returns "Not Started".
    public String getStatus(int exerciseId, int userId) {
        String sql = "SELECT status FROM exercise_progress WHERE exercise_id = ? AND user_id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, exerciseId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Not Started";
    }

    /**
     * Insert if not exists, otherwise update (per-user progress).
     */
    public void upsertStatus(int exerciseId, int userId, String status) {
        String sql =
                "INSERT INTO exercise_progress (exercise_id, user_id, status) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "  status = VALUES(status)";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, exerciseId);
            ps.setInt(2, userId);
            ps.setString(3, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countCompletedForPlan(int planId, int userId) {
        String sql =
                "SELECT COUNT(*) AS c " +
                        "FROM exercise e " +
                        "JOIN exercise_progress p ON p.exercise_id = e.exercise_id " +
                        "WHERE e.plan_id = ? AND p.user_id = ? AND p.status = 'Completed'";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int[] getBreakdownForPlan(int planId, int userId) {
        // [notStarted, inProgress, completed, skipped]
        String sql =
                "SELECT " +
                        " SUM(CASE WHEN COALESCE(p.status,'Not Started')='Not Started' THEN 1 ELSE 0 END) AS ns, " +
                        " SUM(CASE WHEN COALESCE(p.status,'Not Started')='In Progress' THEN 1 ELSE 0 END) AS ip, " +
                        " SUM(CASE WHEN COALESCE(p.status,'Not Started')='Completed' THEN 1 ELSE 0 END) AS co, " +
                        " SUM(CASE WHEN COALESCE(p.status,'Not Started')='Skipped' THEN 1 ELSE 0 END) AS sk " +
                        "FROM exercise e " +
                        "LEFT JOIN exercise_progress p " +
                        "  ON p.exercise_id = e.exercise_id AND p.user_id = ? " +
                        "WHERE e.plan_id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, planId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt("ns"),
                            rs.getInt("ip"),
                            rs.getInt("co"),
                            rs.getInt("sk")
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{0,0,0,0};
    }
    public void deleteProgressForExercise(int exerciseId) {
        String sql = "DELETE FROM exercise_progress WHERE exercise_id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, exerciseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
