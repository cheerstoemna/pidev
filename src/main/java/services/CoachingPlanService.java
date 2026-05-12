package services;

import models.CoachingPlan;
import utils.MyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CoachingPlanService {

    private Connection getConnection() {
        return MyDB.getInstance().getConnection();
    }

    // CREATE
    public boolean addCoachingPlan(CoachingPlan plan) {
        String sql = "INSERT INTO coaching_plan (user_id, title, description, goals, image_path) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, plan.getUserId());
            ps.setString(2, plan.getTitle());
            ps.setString(3, plan.getDescription());
            ps.setString(4, plan.getGoals());
            ps.setString(5, plan.getImagePath());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // READ
    public List<CoachingPlan> getAllPlans() {
        List<CoachingPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM coaching_plan";

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                CoachingPlan p = new CoachingPlan();

                p.setPlanId(rs.getInt("plan_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setTitle(rs.getString("title"));
                p.setDescription(rs.getString("description"));
                p.setGoals(rs.getString("goals"));
                p.setImagePath(rs.getString("image_path"));

                plans.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plans;
    }

    // UPDATE
    public void updatePlan(CoachingPlan plan) {
        String sql = "UPDATE coaching_plan SET title=?, description=?, goals=?, image_path=? WHERE plan_id=?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, plan.getTitle());
            ps.setString(2, plan.getDescription());
            ps.setString(3, plan.getGoals());
            ps.setString(4, plan.getImagePath());
            ps.setInt(5, plan.getPlanId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void deletePlan(int planId) {
        String sql = "DELETE FROM coaching_plan WHERE plan_id=?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
