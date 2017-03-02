package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan Paner on 7/5/2016.
 */
public class Record {
    private static final String SQL_GET =
        "SELECT name, symptom, answer " +
            "FROM PersonDescription " +
            "WHERE _id = ? ";
    private Disease disease;
    private Map<Symptom, Boolean> descriptionAnswers;


    Record(Disease disease, Map<Symptom, Boolean> descriptionValues) {
        this.disease = disease;
        this.descriptionAnswers = descriptionValues;
    }

    public static void main(String[] args) {
        test();
    }


    Map<Symptom, Boolean> getDescriptionAnswers() {
        return descriptionAnswers;
    }


    public static final void test() {
        Record record = Record.get(new Disease("Ashley"));
        System.out.println(record.getAnswer("white"));
        System.out.println(record.getAnswer("black"));
    }


    static Record get(Disease disease) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {disease.getId()};
        Record toGet = null;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET, false, values);
            rs = ps.executeQuery();
            Map<Symptom, Boolean> descriptions = new HashMap<>();
            while (rs.next()) {
                String descriptionStr = rs.getString("symptom");
                Symptom symptom = Symptom.get(descriptionStr);
                String answerStr = rs.getString("answer");
                boolean answer = false;
                if (answerStr.equals("yes")) {
                    answer = true;
                }
                descriptions.put(symptom, answer);
            }
            toGet = new Record(disease, descriptions);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return toGet;
    }

    boolean getAnswer(Symptom symptom) {
        return descriptionAnswers.get(symptom);
    }

    boolean getAnswer(String descriptionStr) {
        Symptom symptom = new Symptom(descriptionStr);
        if (!descriptionAnswers.containsKey(symptom)) {
            return false;
        }
        return getAnswer(symptom);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(disease.getId() + "\n");
        for (Symptom symptom : descriptionAnswers.keySet()) {
            String line = symptom.getId() + ": " + descriptionAnswers.get(symptom) + "\n";
            output.append(line);
        }
        return output.toString();
    }
}
