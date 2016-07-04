package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Paner on 7/4/2016.
 */
public class Description {
    private String description;
    private String question = "";
    private Description superclass;
    private String superclassName;
    private String grouping = "";


    public static void main(String[] args) {
        List<Description> descriptions = Description.getAll();
        for (Description description : descriptions) {
            System.out.println(description.description);
        }
    }


    private static final String SQL_GET_ALL =
        "SELECT description, question, superclass, grouping " +
        "FROM Description " +
        "ORDER BY description";


    Description(String description, String question, String superclass, String grouping) {
        this.description = description;
        this.question = question;
        this.superclassName = superclass;
        this.grouping = grouping;
    }


    String getDescription() {
        return description;
    }


    public static List<Description> getAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {};
        List<Description> descriptions = new ArrayList<>();
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_ALL, false, values);
            rs = ps.executeQuery();

            while (rs.next()) {
                Description description = map(rs);
                descriptions.add(description);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return descriptions;
    }


    private static Description map(ResultSet rs) {
        Description person = null;
        try {
            person = new Description(
                    rs.getString("description"),
                    rs.getString("question"),
                    rs.getString("superclass"),
                    rs.getString("grouping")
            );
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }
}
