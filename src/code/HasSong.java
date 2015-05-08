package code;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
*
* @author Evan Ng, Emily Bird, Jennifer Lui, Velina Ivanova
*/
public class HasSong extends Table{

	public HasSong(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                iupc = -1;     //1
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("INSERT INTO HasSong VALUES (?,?,?,?,?,?,?,?)");


			setNull(ps, askUser(in, "Item UPC: "), true, 1, iupc);

			setNull(ps, askUser(in, "Song Name: "), true, 2);

			ps.executeUpdate();

			// commit work 
			con.commit();

			ps.close();
		} catch (IOException e) {
			System.out.println("IOException!");
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
			try {
				// undo the insert
				con.rollback(); 
			} catch (SQLException ex2) {
				System.out.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}

	}

	@Override
	void delete() {
		int                iupc = -1;  
		String 			   stitle = null;
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("DELETE FROM HasSong WHERE item_upc = ? AND song_title = ?");

			System.out.print("\n Item Upc: ");
			iupc = Integer.parseInt(in.readLine());
			ps.setInt(1, iupc);

			System.out.print("\n Song Title: ");
			stitle = in.readLine();
			ps.setString(2, stitle);

			int rowCount = ps.executeUpdate();

			if (rowCount == 0)
			{
				System.out.println("\nHasSong " + iupc + " " + stitle + " does not exist!");
			}

			con.commit();

			ps.close();
		}
		catch (IOException e)
		{
			System.out.println("IOException!");
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());

			try 
			{
				con.rollback(); 
			}
			catch (SQLException ex2)
			{
				System.out.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}

	}

	@Override
	void display() {
		String             iupc;     //1
		String             stitle;   //2
		Statement  stmt;
		ResultSet  rs;

		try
		{
			stmt = con.createStatement();

			rs = stmt.executeQuery("SELECT * FROM HasSong");

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols; i++)
			{
				// get column name and print it

				System.out.printf("%-15s", rsmd.getColumnName(i+1));
			}

			System.out.println(" ");

			while(rs.next())
			{
				// for display purposes get everything from Oracle
				// as a string

				// simplified output formatting; truncation may occur

				iupc = rs.getString("item_upc");
				System.out.printf("%-10.10s", iupc);

				stitle = rs.getString("song_title");
				checkNull(rs, stitle);

				System.out.println(" ");

			}

			// close the statement;
			// the ResultSet will also be closed
			stmt.close();
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
		}

	}



}
