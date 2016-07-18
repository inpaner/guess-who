package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Ivan Paner on 7/6/2016.
 */
public class Cell {
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


    Person getPerson() {
        return person;
    }


    void setPerson(Person person) {
        this.person = person;
    }


    Description getDescription() {
        return description;
    }


    void setDescription(Description description) {
        this.description = description;
    }


    private Person person;
    private Description description;
    private double score;


    Cell(Person person, Description description, double score) {
        this.person = person;
        this.description = description;
        this.score = score;
    }


    Cell(Person person, Description description) {
        this(person, description, 0.0);
    }


    public static void main(String[] args) {
//        Cell.testPersonScores();
//        Cell.testDescriptionScores();
        Cell.testUpdateScore();
    }


    private static final void testPersonScores() {
        Person person = new Person("Ashley");
        System.out.println(person);
        List<Cell> cells = Cell.getCells(person);
        for (Cell cell : cells) {
            System.out.println(cell.getDescription() + " " + cell.getScore());
        }
    }




    private static final void testDescriptionScores() {
        Description description = new Description("male");
        System.out.println(description);
        List<Cell> cells = Cell.getCells(description);
        for (Cell cell : cells) {
            System.out.println(cell.getPerson() + " " + cell.getScore());
        }
    }


    private static final void testUpdateScore() {
        Person person = new Person("Ashley");
        Description description = new Description("male");
        Cell cell = new Cell(person, description);
        cell.setScore(-8.0);
        cell.updateCell();
    }


    double getScore() {
        return score;
    }


    void setScore(double score) {
        this.score = score;
    }


    static Cell getCell(Person person, Description description) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double score = 0.0;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {person.getName(), description.getDescription()};
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
        return new Cell(person, description, score);
    }


    boolean updateCell() {
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


    static List<Cell> getCells(Person person) {
        List<Cell> cells = new ArrayList<>();
        List<Description> allDescriptions = Description.getAll();
        for (Description description : allDescriptions) {
            cells.add(new Cell(person, description, 0.0));
        }
        cells = Cell.updateWithScoresFromDb(person, cells);
        return cells;
    }


    private static List<Cell> updateWithScoresFromDb(Person person, List<Cell> cells) {
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
                Cell temp = new Cell(person, description);
                Cell toChange = cells.get(cells.indexOf(temp));
                toChange.setScore(score);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return cells;
    }


    static List<Cell> getCells(Description description) {
        List<Cell> cells = new ArrayList<>();
        List<Person> allPersons = Person.getAll();
        for (Person person : allPersons) {
            cells.add(new Cell(person, description));
        }
        cells = Cell.updateWithScoresFromDb(description, cells);
        return cells;
    }


    private static List<Cell> updateWithScoresFromDb(Description description, List<Cell> cells) {
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
                Cell temp = new Cell(person, description);
                Cell toChange = cells.get(cells.indexOf(temp));
                toChange.setScore(score);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return cells;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return Objects.equals(this.description, cell.description) &&
            Objects.equals(this.person, cell.person);
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.person, this.description);
    }
}
