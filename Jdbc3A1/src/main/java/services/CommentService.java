package services;

import models.Comment;
import utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService {

    public List<Comment> getCommentsByContentId(int contentId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT id, content_id, text, created_at, COALESCE(upvotes,0) AS upvotes, COALESCE(downvotes,0) AS downvotes " +
                "FROM comment WHERE content_id=? ORDER BY created_at DESC, id DESC";
        try (Connection c = MyDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                            rs.getInt("id"),
                            rs.getInt("content_id"),
                            rs.getString("text"),
                            rs.getTimestamp("created_at"),
                            rs.getInt("upvotes"),
                            rs.getInt("downvotes")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void addComment(int contentId, String text) {
        String sql = "INSERT INTO comment (content_id, text) VALUES (?,?)";
        try (Connection c = MyDB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            ps.setString(2, text);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteComment(int commentId) {
        String sql = "DELETE FROM comment WHERE id=?";
        try (Connection c = MyDB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void upvoteComment(int commentId) { vote(commentId, true); }
    public void downvoteComment(int commentId) { vote(commentId, false); }

    private void vote(int commentId, boolean up) {
        String sql = up
                ? "UPDATE comment SET upvotes = COALESCE(upvotes,0) + 1 WHERE id=?"
                : "UPDATE comment SET downvotes = COALESCE(downvotes,0) + 1 WHERE id=?";
        try (Connection c = MyDB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
