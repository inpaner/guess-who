package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Ivan Paner on 6/29/2016.
 */
public class Disease implements Comparable<Disease> {
    private static Map<String, Disease> cache = new HashMap<>();
    private String id;
    private double score = 0;
    private List<Rule> parents = new ArrayList<>();
    private String parentCondition;


    public Disease(String id) {
        this.id = id;
    }

    public static void main(String[] args) {
        new Disease("test").testAncestry();
    }


    private void testAncestry() {
        new Session();
        Disease d1 = Disease.get("critical_danger:eye_problem");
        Rule r = RuleManager.get("32");
        System.out.println("Yes: " + d1.ancestry(r));

        Disease d2 = Disease.get("cataract");
        System.out.println("No: " + d2.ancestry(r));

        Rule r2 = RuleManager.get("33a");
        System.out.println("Not ancestor: " + d1.ancestry(r2));
    }


    private static final String SQL_GET_ALL =
        "SELECT _id " +
        "FROM Disease " +
        "ORDER BY _id";

    private static final String SQL_GET_PARENTS =
        "SELECT disease_id, parent, condition " +
        "FROM RuleTree " +
        "WHERE disease_id NOT NULL ";

    /**
     * @return A copy of the cache.
     */
    public static List<Disease> getAll() {
        return new ArrayList<>(cache.values());
    }


    public static Disease get(String id) {
        return cache.get(id);
    }


    static void initCache() {
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
                cache.put(disease.id, disease);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
    }


    static void initParents() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {};
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_PARENTS, false, values);
            rs = ps.executeQuery();

            while (rs.next()) {
                Rule parent = RuleManager.get(rs.getString("parent"));
                Disease disease = get(rs.getString("disease_id"));
                String condition = rs.getString("condition");
                disease.parents.add(parent);
                disease.parentCondition = condition;
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


    public Rule.Ancestry ancestry(Rule rule) {
        for (Rule parent : parents) {
            if (parent.equals(rule)) {
                return Rule.Ancestry.YES;
            } else {
                Rule.Ancestry cumulativeAncestry = parent.isAncestor(rule);
                if (cumulativeAncestry != Rule.Ancestry.NOT_ANCESTOR) {
                    return cumulativeAncestry;
                }
            }
        }
        return Rule.Ancestry.NOT_ANCESTOR;
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
