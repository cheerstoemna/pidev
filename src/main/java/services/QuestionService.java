package services;

import models.Question;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService {

    public void add(Question q) {
        String sql = "INSERT INTO question (quiz_id, question_text, option_a, option_b, option_c, option_d) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cnx = MyConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, q.getQuizId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Question> getByQuiz(int quizId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE quiz_id = ?";

        try (Connection cnx = MyConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getInt("quiz_id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                );
                list.add(q);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
