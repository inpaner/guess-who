package core;

import db.DaoFactory;
import db.DaoUtil;
import sun.security.krb5.internal.crypto.Des;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan Paner on 7/6/2016.
 */
public class ScoreManager {
    private static final String SQL_GET_SCORE =
        "SELECT person, description, value " +
        "FROM ScoreMatrix " +
        "WHERE person = ? AND description = ? ";

    private static final String SQL_GET_PERSON_SCORES =
        "SELECT person, description, value " +
        "FROM ScoreMatrix " +
        "WHERE person = ? ";

    private static final String SQL_GET_DESCRIPTION_SCORES =
        "SELECT person, description, value " +
        "FROM ScoreMatrix " +
        "WHERE description = ? ";


    public static void main(String[] args) {
            new ScoreManager().test1();
    }


    private final void test1() {
        Person person = new Person("Ashley");
        System.out.println(person);
        Map<Description, Double> scores = this.getScores(person);
        for (Description description : scores.keySet()) {
            System.out.println(description + " " + scores.get(description));
        }
    }


    double getScore(Person person, Description description) {
        return this.getScore(person.getName(), description.getDescription());
    }


    double getScore(String name, String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {name, description};
        double score = 0.0;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_SCORE, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                score = rs.getDouble("value");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return score;
    }

    Map<Description, Double> getScores(Person person) {
        Map<Description, Double> scores = new HashMap<>();
        List<Description> allDescriptions = Description.getAll();
        for (Description description : allDescriptions) {
            scores.put(description, 0.0);
        }
        scores = this.updateWithScoresFromDb(person, scores);
        return scores;
    }


    private Map<Description, Double> updateWithScoresFromDb(Person person, Map<Description, Double> scores) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {person.getName()};
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_PERSON_SCORES, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                double score = rs.getDouble("value");
                Description description = new Description(rs.getString("description"));
                scores.put(description, score);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return scores;
    }
}
