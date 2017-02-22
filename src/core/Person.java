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
 * Created by Ivan Paner on 6/29/2016.
 */
public class Person implements Comparable<Person> {
    private static List<Person> cache = new ArrayList<>();
    private String name;
    private double score = 0;


    public Person(String name) {
        this.name = name;
    }


    static {
        Person.initCache();
    }


    public static void main(String[] args) {
        List<Person> persons = Person.getAll();
        for (Person person : persons) {
            System.out.println(person.getName());
        }
    }


    private static final String SQL_GET_ALL =
        "SELECT name " +
        "FROM Person " +
        "ORDER BY name";


    /**
     * @return A copy of the cache.
     */
    public static List<Person> getAll() {
        return new ArrayList<>(cache);
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
                Person person = map(rs);
                cache.add(person);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
    }


    String getName() {
        return name;
    }

    void addScore(double score) {
        this.score += score;
    }

    public double getScore() {
        return score;
    }


    private static Person map(ResultSet rs) {
        Person person = null;
        try {
            person = new Person(
                rs.getString("name")
            );
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(this.name, person.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    @Override
    public String toString() {
        return name + ": " + score;
    }


    @Override
    public int compareTo(Person other) {
        return Double.compare(this.score, other.score);
    }
}
