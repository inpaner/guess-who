package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Answer.testScore();
    }

    static void testGet() {
        for (Answer answer : cache) {
            System.out.println(answer.answer);
        }
    }

    static void testScore() {
        double score = Answer.getScore("yes");
        System.out.println(score);
        score = Answer.getScore("eh");
        System.out.println(score);
    }

    Answer(String answer, double score) {
        this.answer = answer;
        this.score = score;
    }


    public static double getScore(String answerStr) {
        double score = 0;
        Answer answerTemp = new Answer(answerStr.toLowerCase(), 0);
        int index = cache.indexOf(answerTemp);
        if (index >= 0) {
            Answer answer = cache.get(index);
            score = answer.score;
        }
        return score;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Answer)) return false;
        Answer other = (Answer) o;
        return Objects.equals(this.answer, other.answer);
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.answer);
    }


}
