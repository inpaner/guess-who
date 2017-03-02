package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class represents a DAO factory for a SQL database. You can use {@link #getInstance()}
 * to obtain a new instance for the given database name. The specific instance returned depends on
 * the properties file configuration. You can obtain DAO's for the DAO factory instance using the
 * DAO getters.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class DaoFactory {

    // Constants ----------------------------------------------------------------------------------

//    private final String url = "jdbc:sqlite:db/guess_who.db";
    private final String url = "jdbc:sqlite:db/disease_flowchart.db";

    // Actions ------------------------------------------------------------------------------------

    public static DaoFactory getInstance() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        DaoFactory instance = new DaoFactory();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
