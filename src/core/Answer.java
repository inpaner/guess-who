package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Paner on 7/18/2016.
 */
public class Answer {
    private static List<Answer> cache = new ArrayList<>();

    private String answer;
    private double score;

    static {
        Answer.populateCache();
    }


    private static final String SQL_GET =
        "SELECT answer, score " +
        "FROM Answer ";


    public static void main(String[] args) {
        for (Answer answer : cache) {
            System.out.println(answer.answer);
        }
    }


    Answer(String answer, double score) {
        this.answer = answer;
        this.score = score;
    }


    private static void populateCache() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {};
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                String answerStr = rs.getString("answer");
                double score = rs.getDouble("score");
                Answer answer = new Answer(answerStr, score);
                cache.add(answer);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
    }
}
