package core;

import db.DaoFactory;
import db.DaoUtil;

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
        "SELECT name, description, score " +
        "FROM ScoreMatrix " +
        "WHERE name = ? AND description = ? ";

    private static final String SQL_GET_PERSON_SCORES =
        "SELECT name, description, score " +
        "FROM ScoreMatrix " +
        "WHERE name = ? ";

    private static final String SQL_GET_DESCRIPTION_SCORES =
        "SELECT name, description, score " +
        "FROM ScoreMatrix " +
        "WHERE description = ? ";

    private static final String SQL_INSERT =
        "INSERT INTO ScoreMatrix(name, description, score) " +
        "VALUES (?, ?, ?) ";

    private static final String SQL_UPDATE =
        "UPDATE ScoreMatrix " +
        "SET score = ? " +
        "WHERE name = ? AND description = ? ";


    public static void main(String[] args) {
        new ScoreManager().testPersonScores();
//        new ScoreManager().testDescriptionScores();
//        new ScoreManager().testUpdateScore();
    }


    private final void testPersonScores() {
        Person person = new Person("Ashley");
        System.out.println(person);
        Map<Description, Double> scores = this.getScores(person);
        for (Description description : scores.keySet()) {
            System.out.println(description + " " + scores.get(description));
        }
    }


    private final void testDescriptionScores() {
        Description description = new Description("male");
        System.out.println(description);
        Map<Person, Double> scores = this.getScores(description);
        for (Person person : scores.keySet()) {
            System.out.println(person + " " + scores.get(person));
        }
    }


    private final void testUpdateScore() {
        Person person = new Person("Ashley");
        Description description = new Description("male");
        this.updateScore(person, description, -8.0);
    }


    double getScore(Person person, Description description) {
        return this.getScore(person.getName(), description.getDescription());
    }


    double getScore(String name, String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double score = 0.0;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {name, description};
            ps = DaoUtil.prepareStatement(conn, SQL_GET_SCORE, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                score = rs.getDouble("score");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return score;
    }


    boolean updateScore(Person person, Description description, double score) {
        boolean success = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {person.getName(), description.getDescription(), score};
            ps = DaoUtil.prepareStatement(conn, SQL_INSERT, false, values);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            try {
                Object[] values = {score, person.getName(), description.getDescription()};
                ps = DaoUtil.prepareStatement(conn, SQL_UPDATE, false, values);
                ps.executeUpdate();
                success = true;
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } finally {
            DaoUtil.close(conn, ps);
        }
        return success;
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
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {person.getName()};
            ps = DaoUtil.prepareStatement(conn, SQL_GET_PERSON_SCORES, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                double score = rs.getDouble("score");
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


    Map<Person, Double> getScores(Description description) {
        Map<Person, Double> scores = new HashMap<>();
        List<Person> allPersons = Person.getAll();
        for (Person person : allPersons) {
            scores.put(person, 0.0);
        }
        scores = this.updateWithScoresFromDb(description, scores);
        return scores;
    }


    private Map<Person, Double> updateWithScoresFromDb(Description description, Map<Person, Double> scores) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {description.getDescription()};
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_DESCRIPTION_SCORES, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                double score = rs.getDouble("score");
                Person person = new Person(rs.getString("name"));
                scores.put(person, score);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return scores;
    }
}
