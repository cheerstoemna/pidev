package services;

import utils.MyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AnalyticsService {

    public void logEvent(int userId, int contentId, String type, int weight) {

        String sql = """
            INSERT INTO user_content_events
            (user_id, content_id, event_type, weight)
            VALUES (?, ?, ?, ?)
        """;

        try {

            Connection c = MyDB.getInstance().getConnection();

            try (PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ps.setInt(2, contentId);
                ps.setString(3, type);
                ps.setInt(4, weight);

                ps.executeUpdate();
            }

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }

}