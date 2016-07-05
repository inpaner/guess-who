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
 * Created by Ivan Paner on 7/4/2016.
 */
public class Description {
    private static List<Description> cache = new ArrayList<>();
    private String description;
    private String question;
    private Description superclass;
    private String superclassName;
    private String grouping;


    static {
        initCache();
    }

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


    Description(String description) {
        this(description, "", "", "");
    }


    String getDescription() {
        return description;
    }


    Description getSuperclass() {
        if (superclassName == null) {
            return null;
        } else if (superclass == null) {
            Description container = new Description(superclassName);
            superclass = cache.get(cache.indexOf(container));
        }
        return superclass;
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
                Description description = map(rs);
                cache.add(description);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
    }


    /**
     * @return A copy of the cache.
     */
    public static List<Description> getAll() {
        return new ArrayList<>(cache);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Description)) return false;
        Description description = (Description) o;
        return Objects.equals(this.description, description.description);
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.description);
    }
}
