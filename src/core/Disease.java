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
 * Created by Ivan Paner on 6/29/2016.
 */
public class Disease implements Comparable<Disease> {
    private static List<Disease> cache = new ArrayList<>();
    private String id;
    private double score = 0;


    public Disease(String id) {
        this.id = id;
    }


    static {
        Disease.initCache();
    }


    public static void main(String[] args) {
        List<Disease> diseases = Disease.getAll();
        for (Disease disease : diseases) {
            System.out.println(disease.getId());
        }
    }


    private static final String SQL_GET_ALL =
        "SELECT _id " +
        "FROM Disease " +
        "ORDER BY _id";


    /**
     * @return A copy of the cache.
     */
    public static List<Disease> getAll() {
        return new ArrayList<>(cache);
    }


    private static void initCache() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {};
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_ALL, false, values);
            rs = ps.executeQuery();

            while (rs.next()) {
                Disease disease = map(rs);
                cache.add(disease);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
    }


    String getId() {
        return id;
    }

    void addScore(double score) {
        this.score += score;
    }

    public double getScore() {
        return score;
    }


    private static Disease map(ResultSet rs) {
        Disease disease = null;
        try {
            disease = new Disease(
                rs.getString("_id")
            );
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return disease;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Disease)) return false;
        Disease disease = (Disease) o;
        return Objects.equals(this.id, disease.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return id + ": " + score;
    }


    @Override
    public int compareTo(Disease other) {
        return Double.compare(this.score, other.score);
    }
}
