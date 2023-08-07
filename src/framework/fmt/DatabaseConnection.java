package framework.fmt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class provides a utility method for connecting to a database It contains
 * the connection parameters, username, password, and url necessary for
 * connecting to the database
 * 
 * @author lucyb
 *
 */
public class DatabaseConnection {

	public static final String parameters = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public static final String username = "lbernard";
	public static final String password = "WlRtZAkIRA8";
	public static final String url = "jdbc:mysql://cse.unl.edu/" + username + parameters;

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}