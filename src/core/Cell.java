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
        "SELECT disease_id, symptom_id, score " +
        "FROM ScoreMatrix " +
        "WHERE disease_id = ? AND symptom_id = ? ";

    private static final String SQL_GET_PERSON_SCORES =
        "SELECT disease_id, symptom_id, score " +
        "FROM ScoreMatrix " +
        "WHERE disease_id = ? ";

    private static final String SQL_GET_DESCRIPTION_SCORES =
        "SELECT disease_id, symptom_id, score " +
        "FROM ScoreMatrix " +
        "WHERE symptom_id = ? ";

    private static final String SQL_INSERT =
        "INSERT INTO ScoreMatrix(disease_id, symptom_id, score) " +
        "VALUES (?, ?, ?) ";

    private static final String SQL_UPDATE =
        "UPDATE ScoreMatrix " +
        "SET score = ? " +
        "WHERE disease_id = ? AND symptom_id = ? ";


    Disease getDisease() {
        return disease;
    }


    void setDisease(Disease disease) {
        this.disease = disease;
    }


    Symptom getSymptom() {
        return symptom;
    }


    void setSymptom(Symptom symptom) {
        this.symptom = symptom;
    }


    private Disease disease;
    private Symptom symptom;
    private double score;


    Cell(Disease disease, Symptom symptom, double score) {
        this.disease = disease;
        this.symptom = symptom;
        this.score = score;
    }


    Cell(Disease disease, Symptom symptom) {
        this(disease, symptom, 0.0);
    }


    public static void main(String[] args) {
//        Cell.testPersonScores();
//        Cell.testDescriptionScores();
        Cell.testUpdateScore();
    }


    private static final void testPersonScores() {
        Disease disease = new Disease("Ashley");
        System.out.println(disease);
        List<Cell> cells = Cell.getCells(disease);
        for (Cell cell : cells) {
            System.out.println(cell.getSymptom() + " " + cell.getScore());
        }
    }


    private static final void testDescriptionScores() {
        Symptom symptom = new Symptom("male");
        System.out.println(symptom);
        List<Cell> cells = Cell.getCells(symptom);
        for (Cell cell : cells) {
            System.out.println(cell.getDisease() + " " + cell.getScore());
        }
    }


    private static final void testUpdateScore() {
        Disease disease = new Disease("Ashley");
        Symptom symptom = new Symptom("male");
        Cell cell = new Cell(disease, symptom);
        cell.setScore(-8.0);
        cell.updateCell();
    }


    double getScore() {
        return score;
    }


    void setScore(double score) {
        this.score = score;
    }


    void addScore(double scoreToAdd) {
        this.score += scoreToAdd;
    }


    static Cell getCell(Disease disease, Symptom symptom) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double score = 0.0;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {disease.getId(), symptom.getId()};
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
        return new Cell(disease, symptom, score);
    }


    boolean updateCell() {
        boolean success = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {disease.getId(), symptom.getId(), score};
            ps = DaoUtil.prepareStatement(conn, SQL_INSERT, false, values);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            try {
                Object[] values = {score, disease.getId(), symptom.getId()};
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


    static List<Cell> getCells(Disease disease) {
        List<Cell> cells = new ArrayList<>();
        List<Symptom> allSymptoms = Symptom.getAll();
        for (Symptom symptom : allSymptoms) {
            cells.add(new Cell(disease, symptom, 0.0));
        }
        cells = Cell.updateWithScoresFromDb(disease, cells);
        return cells;
    }


    private static List<Cell> updateWithScoresFromDb(Disease disease, List<Cell> cells) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            Object[] values = {disease.getId()};
            ps = DaoUtil.prepareStatement(conn, SQL_GET_PERSON_SCORES, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                double score = rs.getDouble("score");
                Symptom symptom = new Symptom(rs.getString("symptom_id"));
                Cell temp = new Cell(disease, symptom);
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


    static List<Cell> getCells(Symptom symptom) {
        List<Cell> cells = new ArrayList<>();
        List<Disease> allDiseases = Disease.getAll();
        for (Disease disease : allDiseases) {
            cells.add(new Cell(disease, symptom));
        }
        cells = Cell.updateWithScoresFromDb(symptom, cells);
        return cells;
    }


    private static List<Cell> updateWithScoresFromDb(Symptom symptom, List<Cell> cells) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {symptom.getId()};
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_DESCRIPTION_SCORES, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                double score = rs.getDouble("score");
                Disease disease = new Disease(rs.getString("disease_id"));
                Cell temp = new Cell(disease, symptom);
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
    public String toString() {
        return disease + ", " + symptom + ", " + score;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return Objects.equals(this.symptom, cell.symptom) &&
            Objects.equals(this.disease, cell.disease);
    }


    @Override
    public int hashCode() {
        return Objects.hash(disease, symptom);
    }
}
