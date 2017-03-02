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

/**
 * Created by Ivan on 3/2/2017.
 */
public class Rule {
    private static Map<String, Rule> cache = new HashMap<>();

    private String id;
    private String type;
    private int value;
    private Status status;
    private List<Symptom> symptoms = new ArrayList<>();
    private Rule parent;
    private String parentCondition;


    enum Status {
        PASS, FAIL, ACTIVE, INACTIVE
    }

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
                cache.put(rule.id, rule);
            }

            // attach parents
            ps = DaoUtil.prepareStatement(conn, SQL_GET_PARENTS, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                Rule child = get(rs.getString("rule_id"));
                Rule parent = get(rs.getString("parent"));
                String condition = rs.getString("condition");
                child.parent = parent;
                child.parentCondition = condition;
            }

            // attach symptoms
            ps = DaoUtil.prepareStatement(conn, SQL_GET_SYMPTOMS, false, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                Rule rule = get(rs.getString("rule_id"));
                Symptom symptom = Symptom.get(rs.getString("symptom_id"));
                rule.symptoms.add(symptom);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }

        // attach symptoms
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


    private static Rule get(String id) {
        return cache.get(id);
    }


    private static List<Rule> getAll() {
        return new ArrayList<>(cache.values());
    }

    private Rule(){}

    private Rule(String id, String type, int value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }


    public static void main(String[] args) {
        new Session();
        new Rule().testInit();
    }


    void testInit() {
        List<Rule> rules = getAll();
        for (Rule rule : rules) {
            System.out.println(rule);
        }
    }


    @Override
    public String toString() {
        return id + ":" + type + " " + value;
    }
}
