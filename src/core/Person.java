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
 * Created by Ivan Paner on 6/29/2016.
 */
public class Person {
    private String name;

    Person(String name) {
        this.name = name;
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


    public static List<Person> getAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] values = {};
        List<Person> persons = new ArrayList<>();
        try {
            DaoFactory factory = DaoFactory.getInstance();
            conn = factory.getConnection();
            ps = DaoUtil.prepareStatement(conn, SQL_GET_ALL, false, values);
            rs = ps.executeQuery();

            while (rs.next()) {
                Person person = map(rs);
                persons.add(person);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            DaoUtil.close(conn, ps, rs);
        }
        return persons;
    }


    String getName() {
        return name;

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
}
