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
        "SELECT name, description, answer " +
        "FROM PersonDescription " +
        "WHERE name = ? ";
    private Person person;
    private Map<Description, Boolean> descriptionAnswers;


    Record(Person person, Map<Description, Boolean> descriptionValues) {
        this.person = person;
        this.descriptionAnswers = descriptionValues;
    }

    public static void main(String[] args) {
        test();
    }

    public static final void test() {
        Record record = Record.get("Ashley");
        System.out.println(record.getAnswer("white"));
        System.out.println(record.getAnswer("black"));
    }

    static Record get(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {name};
        Record toGet = null;
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET, false, values);
            rs = ps.executeQuery();
            Map<Description, Boolean> descriptions = new HashMap<>();
            while (rs.next()) {
                String descriptionStr = rs.getString("description");
                Description description = Description.get(descriptionStr);
                String answerStr = rs.getString("answer");
                boolean answer = false;
                if (answerStr.equals("yes")) {
                    answer = true;
                }
                descriptions.put(description, answer);
            }
            Person person = new Person(name);
            toGet = new Record(person, descriptions);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return toGet;
    }

    boolean getAnswer(Description description) {
        return descriptionAnswers.get(description);
    }

    boolean getAnswer(String descriptionStr) {
        Description description = new Description(descriptionStr);
        if (!descriptionAnswers.containsKey(description)) {
            return false;
        }
        return this.getAnswer(description);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(person.getName() + "\n");
        for (Description description : descriptionAnswers.keySet()) {
            String line = description.getDescription() + ": " + descriptionAnswers.get(description) + "\n";
            output.append(line);
        }
        return output.toString();
    }
}
