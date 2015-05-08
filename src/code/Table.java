package code;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
*
* @author Evan Ng, Emily Bird, Jennifer Lui, Velina Ivanova
*/
public abstract class Table {

	protected BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	protected Connection con;  
	
	public Table(Connection con){
		this.con = con;
	}
	
	/**
	 * If the value jsut read from rs was null print nothing, otherwise print the value
	 * @param rs the result set holding the information to check
	 * @param attribute the value that was just read in 
	 * @throws SQLException
	 */
	public void checkNull(ResultSet rs, String attribute) throws SQLException {
	    if (rs.wasNull())
	        System.out.printf("%-10.10s", " ");

	    else
	        System.out.printf("%-10.10s", attribute);

	}
	
	/**
	 * 
	 * @param in the BufferedReader to use to get input
	 * @param message the message to display for user
	 * @return the line read in after displaying message
	 * @throws IOException
	 */
	public String askUser ( BufferedReader in, String message) throws IOException
	{
		System.out.println("\n" + message + ": ");
		return in.readLine();
	}

	/**
	 * If attribute is not a primary key, set null at position position of ps, else set attribute
	 * @param ps the prepared statement
	 * @param line the line that was just read in from user
	 * @param key true if attribute is the primary key
	 * @param position the position of attribute in the ps
	 * @param attribute the value
	 * @throws SQLException
	 */
	public void setNull (PreparedStatement ps, String line, boolean key, int position, double attribute) throws SQLException {
		if (line.length() == 0 && !key) {
			ps.setNull(position, java.sql.Types.DOUBLE);
		} else {
			attribute = Double.parseDouble(line);
			ps.setDouble(position, attribute);
		}
	}

	/**
	 * If attribute is not a primary key, set null at position position of ps, else set attribute
	 * @param ps the prepared statement
	 * @param line the line that was just read in from user
	 * @param key true if attribute is the primary key
	 * @param position the position of attribute in the ps
	 * @param attribute the value
	 * @throws SQLException
	 */
	public void setNull (PreparedStatement ps, String line, boolean key, int position, int attribute) throws SQLException {
		if (line.length() == 0 && !key) {
			ps.setNull(position, java.sql.Types.INTEGER);
		} else {
			attribute = Integer.parseInt(line);
			ps.setInt(position, attribute);
		}
	}
	
	/**
	 * If attribute is not a primary key, set null at position position of ps, else set attribute
	 * @param ps the prepared statement
	 * @param line the line that was just read in from user
	 * @param key true if attribute is the primary key
	 * @param position the position of attribute in the ps
	 * @param attribute the value
	 * @throws SQLException
	 */
	public void setNull (PreparedStatement ps, String line, boolean key, int position, Date attribute) throws SQLException {
		if (line.length() == 0 && !key) {
			ps.setDate(position, null);
		} else {
			ps.setDate(position, attribute);
		}
	}
	
	/**
	 * If attribute is not a primary key, set null at position position of ps, else set attribute
	 * @param ps the prepared statement
	 * @param line the line that was just read in from user
	 * @param key true if attribute is the primary key
	 * @param position the position of attribute in the ps
	 * @throws SQLException
	 */
	public void setNull (PreparedStatement ps, String line, boolean key, int position) throws SQLException {
		if (line.length() == 0 && !key) {
			ps.setString(position, null);
		} else {
			ps.setString(position, line);
		}
	}


	abstract void insert();

	abstract void delete();

	abstract void display();
}
