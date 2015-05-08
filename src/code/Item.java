package code;
import java.io.BufferedReader;
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
public class Item extends Table{



	public Item(Connection con) {
		super(con);
	}

	void update (BufferedReader in, int iupc) throws SQLException, IOException{
		Statement stmt = con.createStatement();
		int quantity;

		System.out.println("Quantity please:");
		quantity = Integer.parseInt(in.readLine());

		System.out.println("New price (optional):");
		String line = in.readLine();
		if (line.length() == 0) {
			stmt.executeQuery("UPDATE Item SET item_stock = " + quantity + "WHERE item_upc = " + iupc);

		} else {
			stmt.executeQuery("UPDATE Item SET item_stock = " + quantity + ", item_price = " + Double.parseDouble(line) +  " WHERE item_upc = " + iupc);
		}

	}

	@Override
	void insert() {
		int                iupc = -1;     //1
		int                iyear = -1;     //6
		double             iprice = -1;    //7
		int            	   istock = -1;    //8
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("INSERT INTO Item VALUES (?,?,?,?,?,?,?,?)");


			setNull(ps, askUser(in, "Item UPC: "), true, 1, iupc);

			setNull(ps, askUser(in, "Item Title: "), false, 2);

			setNull(ps, askUser(in, "Item Type: "), false, 3);

			setNull(ps, askUser(in, "Item Category: "), false, 4);

			setNull(ps, askUser(in, "Item Company: "), false, 5);

			setNull(ps, askUser(in, "Item Year: "), false, 6, iyear);

			setNull(ps, askUser(in, "Item Price: "), false, 7, iprice);

			setNull(ps, askUser(in, "Item Stock: "), false, 8, istock);

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
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("DELETE FROM Item WHERE item_upc = ?");

			System.out.print("\n Item Upc: ");
			iupc = Integer.parseInt(in.readLine());
			ps.setInt(1, iupc);

			int rowCount = ps.executeUpdate();

			if (rowCount == 0)
			{
				System.out.println("\nItem " + iupc + " does not exist!");
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
		String             ititle;   //2
		String             itype;    //3
		String             icategory; //4
		String             icompany;  //5
		String             iyear;     //6
		String             iprice;    //7
		String             istock;    //8
		Statement  stmt;
		ResultSet  rs;

		try
		{
			stmt = con.createStatement();

			rs = stmt.executeQuery("SELECT * FROM Item");

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

				ititle = rs.getString("item_title");
				checkNull(rs, ititle);

				itype = rs.getString("item_type");
				checkNull(rs, itype);

				icategory = rs.getString("item_category");
				checkNull(rs, icategory);

				icompany = rs.getString("item_company");
				checkNull(rs, icompany);

				iyear = rs.getString("item_year");
				checkNull(rs, iyear);

				iprice = rs.getString("item_price");
				checkNull(rs, iprice);

				istock = rs.getString("item_stock");
				checkNull(rs, istock);

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
