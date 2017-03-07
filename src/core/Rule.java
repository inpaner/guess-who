package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Ivan on 3/2/2017.
 */
public class Rule {

    private String id;
    private String type;
    private int value;
    private int satisfiedConditions = 0;
    private int totalSymptoms;
    private Status status = Status.INACTIVE;
    private List<Symptom> attachedSymptoms = new ArrayList<>();
    private Map<Symptom, Answer> answeredSymptoms = new HashMap<>();
    private Map<Rule, String> parents = new HashMap<>();

    public enum Status {
        PASS, FAIL, ACTIVE, INACTIVE
    }


    public enum Ancestry {
        YES, NO, NOT_ANCESTOR
    }

    private Rule(){}


    Rule(String id, String type, int value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }


    boolean isPass() {
        return status.equals(Status.PASS);
    }


    boolean isFail() {
        return status.equals(Status.FAIL);
    }


    boolean isActive() {
        return status.equals(Status.ACTIVE);
    }


    boolean isInactive() {
        return status.equals(Status.INACTIVE);
    }


    int getValue() {
        return value;
    }


    Rule.Status getStatus() {
        return status;
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


    void attachSymptom(Symptom symptom) {
        attachedSymptoms.add(symptom);
    }


    public Rule.Ancestry isAncestor(Rule rule) {
        for (Rule parent : parents.keySet()) {
            if (parent.equals(rule)) {
                String ancestry = parents.get(rule);
                if (ancestry.startsWith("y")) {
                    return Ancestry.YES;
                } else {
                    return Ancestry.NO;
                }
            } else {
                Rule.Ancestry cumulativeAncestry = parent.isAncestor(rule);
                if (cumulativeAncestry != Ancestry.NOT_ANCESTOR) {
                    return cumulativeAncestry;
                }
            }
        }
        return Ancestry.NOT_ANCESTOR;
    }


    Rule.Status answerSymptom(Symptom symptom, Answer answer) {
        if (!attachedSymptoms.contains(symptom)) {
            return status;
        } else if (answeredSymptoms.containsKey(symptom)) {
            removeSymptom(symptom);
        }
        answeredSymptoms.put(symptom, answer);
        if (answer.getScore() > 0) {
            satisfiedConditions++;
        }
        if (satisfiedConditions >= value) {
            status = Status.PASS;
        } else if (answeredSymptoms.size() == attachedSymptoms.size()) {
            status = Status.FAIL;
        } else {
            status = Status.ACTIVE;
        }

        return status;
    }


    Rule.Status removeSymptom(Symptom symptom) {
        if (!attachedSymptoms.contains(symptom) || !answeredSymptoms.containsKey(symptom)) {
            return status;
        }
        Answer answer = answeredSymptoms.get(symptom);
        answeredSymptoms.remove(symptom);

        if (answer.getScore() > 0) {
            satisfiedConditions--;
        }
        if (satisfiedConditions >= value) {
            status = Status.PASS;
        } else if (answeredSymptoms.isEmpty()) {
            status = Status.INACTIVE;
        } else {
            status = Status.ACTIVE;
        }
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Rule)) return false;
        Rule rule = (Rule) o;
        return Objects.equals(this.id, rule.id);
    }


    @Override
    public String toString() {
        return id + ":" + type + " " + value;
    }
}
