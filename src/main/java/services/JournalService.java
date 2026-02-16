package services;

import models.Journal;
import utils.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JournalService {

    // Add a new journal
    public void add(Journal journal) {
        String sql = "INSERT INTO journal(title, content, mood, created_at) VALUES (?, ?, ?, ?)";
        try (Connection cnx = MyConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, journal.getTitle());
            ps.setString(2, journal.getContent());
            ps.setString(3, journal.getMood());
            ps.setTimestamp(4, Timestamp.valueOf(journal.getDate()));

            ps.executeUpdate();

            System.out.println("Journal added successfully!");

        } catch (SQLException e) {
            System.err.println("Error adding journal: " + e.getMessage());
        }
    }

    // Get all journals
    public List<Journal> getAll() {
        List<Journal> list = new ArrayList<>();
        String sql = "SELECT * FROM journal ORDER BY created_at DESC"; // newest first

        try (Connection cnx = MyConnection.getConnection();
             Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Journal j = new Journal(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content")

                );
                list.add(j);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching journals: " + e.getMessage());
        }

        return list;
    }

    // Delete a journal by ID
    public void delete(int id) {
        String sql = "DELETE FROM journal WHERE id = ?";

        try (Connection cnx = MyConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Journal deleted successfully!");
            } else {
                System.out.println("No journal found with ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting journal: " + e.getMessage());
        }
    }

    // Update an existing journal
    public void update(Journal journal) {
        String sql = "UPDATE journal SET title = ?, content = ?, mood = ? WHERE id = ?";

        try (Connection cnx = MyConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, journal.getTitle());
            ps.setString(2, journal.getContent());
            ps.setString(3, journal.getMood());
            ps.setInt(4, journal.getId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Journal updated successfully!");
            } else {
                System.out.println("No journal found with ID: " + journal.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating journal: " + e.getMessage());
        }
    }
}
