package core;

import db.DaoFactory;
import db.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Ivan Paner on 7/4/2016.
 */
public class Symptom implements Comparable<Symptom> {
    private static Map<String, Symptom> cache = new HashMap<>();
    private String id;
    private String question;
    private Symptom superclass;
    private String superclassName;
    private String grouping;
    private boolean chiefComplaint;
    private String ageGroup;
    private String combinedSymptom;


    public static void main(String[] args) {
        testGet();
    }


    private static void testGetAll() {
        List<Symptom> symptoms = Symptom.getAll();
        for (Symptom symptom : symptoms) {
            String superclass = "";
            if (symptom.hasSuperclass()) {
                superclass = symptom.superclassName;
            }
            System.out.println(symptom.id + ": " + superclass);
        }
    }


    public String getQuestion() {
        return id;
        // return question;
    }


    private static void testGet() {
        Symptom male = Symptom.get("eye_swollen");
        System.out.println(male.id);
        Symptom isNull = Symptom.get("cow");
        System.out.println(isNull == null);
    }


    private static final String SQL_GET_ALL =
            "SELECT _id, question, superclass, grouping, chief_complaint, age_group, combined_symptom " +
            "FROM Symptom " +
            "ORDER BY _id";



    private Symptom(String id, String question, String superclass, String grouping,
                    boolean chiefComplaint, String ageGroup, String combinedSymptom) {
        this.id = id;
        this.question = question;
        this.superclassName = superclass;
        this.grouping = grouping;
        this.chiefComplaint = chiefComplaint;
        this.ageGroup = ageGroup;
        this.combinedSymptom = combinedSymptom;
    }

    public Symptom(String id) {
        this(id, "", "", "", false, "", "");
    }


    String getId() {
        return id;
    }


    Symptom getSuperclass() {
        if (superclassName == null) {
            return null;
        } else if (superclass == null) {
            superclass = Symptom.get(superclassName);
        }
        return superclass;
    }


    boolean hasSuperclass() {
        return superclassName != null && !superclassName.isEmpty();
    }


    static Symptom get(String description) {
        return cache.get(description);
    }


    static void initCache() {
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
                Symptom symptom = map(rs);
                cache.put(symptom.id, symptom);
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
    public static List<Symptom> getAll() {
        List<Symptom> results = new ArrayList<Symptom>(cache.values());
        Collections.sort(results);
        return results;
    }


    private static Symptom map(ResultSet rs) {
        Symptom person = null;

        try {
            int chiefComplaintInt = rs.getInt("chief_complaint");
            boolean chiefComplaint = chiefComplaintInt == 1;
            person = new Symptom(
                rs.getString("_id"),
                rs.getString("question"),
                rs.getString("superclass"),
                rs.getString("grouping"),
                chiefComplaint,
                rs.getString("age_group"),
                rs.getString("combined_symptom")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Symptom)) return false;
        Symptom symptom = (Symptom) o;
        return Objects.equals(this.id, symptom.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return id;
    }


    @Override
    public int compareTo(Symptom other) {
        return id.compareTo(other.id);
    }
}
