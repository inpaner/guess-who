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

    private String id;
    private String type;
    private int value;
    private Status status;
    private List<Symptom> symptoms = new ArrayList<>();
    private Map<Rule, String> parents = new HashMap<>();


    enum Status {
        PASS, FAIL, ACTIVE, INACTIVE
    }

    private Rule(){}

    Rule(String id, String type, int value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }


    public static void main(String[] args) {
        new Session();
        new Rule().testInit();
    }


    void testInit() {
        List<Rule> rules = RuleManager.getAll();
        for (Rule rule : rules) {
            System.out.println(rule);
        }
    }


    public String getId() {
        return id;
    }


    void addParent(Rule parent, String condition) {
        parents.put(parent, condition);
    }


    void addSymptom(Symptom symptom) {
        symptoms.add(symptom);
    }


    @Override
    public String toString() {
        return id + ":" + type + " " + value;
    }
}
