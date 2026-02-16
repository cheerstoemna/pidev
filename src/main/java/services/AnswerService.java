package services;

import utils.MyConnection;
import java.sql.*;

public class AnswerService {

    public void saveAnswer(int quizId, int questionId, String answer) {

        String sql = """
                INSERT INTO user_answer (quiz_id, question_id, selected_answer)
                VALUES (?, ?, ?)
                """;

        try (Connection cnx = MyConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            ps.setInt(2, questionId);
            ps.setString(3, answer);

            ps.executeUpdate();

            System.out.println("Answer saved!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
