package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sun.corba.se.impl.util.RepositoryId.cache;

/**
 * Created by Ivan on 3/2/2017.
 */
public class RuleManager {
    private static Map<String, Rule> cache = new HashMap<>();
    private static Map<Symptom, List<Rule>> symptomCache = new HashMap<>();

    private static final String SQL_GET_ALL =
            "SELECT _id, type, value " +
                    "FROM Rule " +
                    "ORDER BY _id ";

    private static final String SQL_GET_PARENTS =
            "SELECT rule_id, parent, condition " +
                    "FROM RuleTree " +
                    "WHERE rule_id NOT NULL ";

    private static final String SQL_GET_SYMPTOMS =
            "SELECT rule_id, symptom_id " +
                    "FROM RuleContent ";


    static void initCache() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {};

        try {
            // init rules
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_ALL, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                Rule rule = map(rs);
                cache.put(rule.getId(), rule);
            }

            // attach parents
            ps = DaoUtil.prepareStatement(conn, SQL_GET_PARENTS, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                Rule child = get(rs.getString("rule_id"));
                Rule parent = get(rs.getString("parent"));
                String condition = rs.getString("condition");
                child.addParent(parent, condition);
            }

            // attach symptoms
            ps = DaoUtil.prepareStatement(conn, SQL_GET_SYMPTOMS, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                Rule rule = get(rs.getString("rule_id"));
                Symptom symptom = Symptom.get(rs.getString("symptom_id"));
                rule.addSymptom(symptom);
                addToSymptomCache(symptom, rule);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
    }


    private static void addToSymptomCache(Symptom symptom, Rule rule) {
        List<Rule> rules = symptomCache.get(symptom);
        if (rules == null) {
            rules = new ArrayList<>();
            symptomCache.put(symptom, rules);
        }
        rules.add(rule);
    }

    static Rule get(String id) {
        return cache.get(id);
    }

    static List<Rule> getAll() {
        return new ArrayList<>(cache.values());
    }

    private static Rule map(ResultSet rs) {
        Rule rule = null;
        try {
            rule = new Rule(
                    rs.getString("_id"),
                    rs.getString("type"),
                    rs.getInt("value")
            );
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rule;
    }


    public static void main(String[] args) {
        new RuleManager().testSymptomCache();
    }


    private void testSymptomCache() {
        new Session();
        for (Symptom symptom : symptomCache.keySet()) {
            System.out.println(symptom);
            System.out.println(symptomCache.get(symptom));
            System.out.println();
        }
    }
}
