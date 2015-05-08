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
public class PurchaseItem extends Table{

	public PurchaseItem(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                preceiptid = -1;     //1
		int                iupc = -1;           //2
		int                piquantity = -1;           //3

		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("INSERT INTO PurchaseItem VALUES (?,?,?)");


			setNull(ps, askUser(in, "Purchase Receipt ID: "), true, 1, preceiptid);

			setNull(ps, askUser(in, "Item UPC: "), false, 2, iupc);
			
			setNull(ps, askUser(in, "Item Quantity: "), false, 3, piquantity);

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
        int                preceiptid = -1;  
        int 			   iupc = -1;
        PreparedStatement  ps;  
          
        try
        {
          ps = con.prepareStatement("DELETE FROM PurchaseItem WHERE purchase_receiptId = ? AND item_upc = ?");
        
          System.out.print("\n Purchase Receipt ID: ");
          preceiptid = Integer.parseInt(in.readLine());
          ps.setInt(1, preceiptid);

          System.out.print("\n Item UPC: ");
          iupc = Integer.parseInt(in.readLine());
          ps.setInt(2, iupc);
          
          int rowCount = ps.executeUpdate();

          if (rowCount == 0)
          {
              System.out.println("\nHasSong " + preceiptid + " " + iupc + " does not exist!");
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
	    String             preceiptid;     //1
	    String             iupc;   //2
	    String                piquantity;    //3
	    Statement  stmt;
	    ResultSet  rs;

	    try
	    {
	        stmt = con.createStatement();

	        rs = stmt.executeQuery("SELECT * FROM PurchaseItem");

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

	            preceiptid = rs.getString("purchase_receiptId");
	            System.out.printf("%-10.10s", preceiptid);

	            iupc = rs.getString("item_upc");
	            checkNull(rs, iupc);

	            piquantity = rs.getString("purchaseitem_quantity");
	            checkNull(rs, piquantity);
	            
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
