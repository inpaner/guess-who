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
        testGet();
    }


    private static void testGetAll() {
        List<Description> descriptions = Description.getAll();
        for (Description description : descriptions) {
            String superclass = "";
            if (description.hasSuperclass()) {
                superclass = description.superclassName;
            }
            System.out.println(description.description + ": " + superclass);
        }
    }


    private static void testGet() {
        Description male = Description.get("male");
        System.out.println(male.description);
        Description isNull = Description.get("cow");
        System.out.println(isNull == null);
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
            superclass = Description.get(superclassName);
        }
        return superclass;
    }


    boolean hasSuperclass() {
        return superclassName != null && !superclassName.isEmpty();
    }


    static Description get(String description) {
        Description container = new Description(description);
        int index = cache.indexOf(container);
        if (index == -1) {
            return null;
        }
        return cache.get(index);
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


    @Override
    public String toString() {
        return description;
    }
}
