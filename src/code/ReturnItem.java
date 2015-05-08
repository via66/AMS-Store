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
public class ReturnItem extends Table{

	public ReturnItem(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                rretid = -1;         //1
		int			       iupc = -1;          //2
		int			       riquantity = -1;    //3		
		
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("INSERT INTO ReturnItem VALUES (?,?,?)");


			setNull(ps, askUser(in, "Return ID: "), true, 1, rretid);

			setNull(ps, askUser(in, "Item UPC: "), false, 2, iupc);
			
			setNull(ps, askUser(in, "Return Item Quantity: "), false, 3, riquantity);

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
        int                rrid = -1;  
        int 			   iupc = -1;
        PreparedStatement  ps;  
          
        try
        {
          ps = con.prepareStatement("DELETE FROM ReturnItem WHERE return_retid = ? AND item_upc = ?");
        
          System.out.print("\n Purchase Receipt ID: ");
          rrid = Integer.parseInt(in.readLine());
          ps.setInt(1, rrid);

          System.out.print("\n Item UPC: ");
          iupc = Integer.parseInt(in.readLine());
          ps.setInt(2, iupc);
          
          int rowCount = ps.executeUpdate();

          if (rowCount == 0)
          {
              System.out.println("\nHasSong " + rrid + " " + iupc + " does not exist!");
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
	    String             rrid;     //1
	    String             iupc;   //2
	    String             qunatity;    //3
	    Statement  stmt;
	    ResultSet  rs;

	    try
	    {
	        stmt = con.createStatement();

	        rs = stmt.executeQuery("SELECT * FROM ReturnItem");

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

	            rrid = rs.getString("return_retid");
	            System.out.printf("%-10.10s", rrid);

	            iupc = rs.getString("item_upc");
	            checkNull(rs, iupc);

	            qunatity = rs.getString("returnItem_quantity");
	            checkNull(rs, qunatity);

	            
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
