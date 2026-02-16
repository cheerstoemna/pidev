package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDB {
    private static final String URL = "jdbc:mysql://localhost:3306/mindnest?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
            ensureSchema(connection);
        }
        return connection;
    }

    /**
     * Creates/updates tables used by the app without introducing new tables.
     * This method is defensive: if something already exists, it won't fail the app.
     */
    private static void ensureSchema(Connection c) {
        try (Statement st = c.createStatement()) {
            // content table
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS content (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "title VARCHAR(255) NOT NULL," +
                            "description TEXT," +
                            "type VARCHAR(50)," +
                            "source_url VARCHAR(1024)," +
                            "image_url VARCHAR(2048)," +
                            "category VARCHAR(100)," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            // comment table
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS comment (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "content_id INT NOT NULL," +
                            "text TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "upvotes INT DEFAULT 0," +
                            "downvotes INT DEFAULT 0," +
                            "CONSTRAINT fk_comment_content FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE" +
                            ")"
            );

            // Add voting columns to content (if missing)
            safeAlter(st, "ALTER TABLE content ADD COLUMN upvotes INT DEFAULT 0");
            safeAlter(st, "ALTER TABLE content ADD COLUMN downvotes INT DEFAULT 0");

            // If older schemas used different image column size
            safeAlter(st, "ALTER TABLE content MODIFY COLUMN image_url VARCHAR(2048)");

        } catch (Exception ignored) {
            // keep app running even if schema changes aren't possible
        }
    }

    private static void safeAlter(Statement st, String sql) {
        try { st.executeUpdate(sql); } catch (Exception ignored) {}
    }
}
