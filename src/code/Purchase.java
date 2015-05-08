package code;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

/**
*
* @author Evan Ng, Emily Bird, Jennifer Lui, Velina Ivanova
*/
public class Purchase extends Table{

	public Purchase(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                preceiptid = -1;     //1
		Date			   pdate = null;	//2
		int 			   ccid = -1;		//3
		int                ccard = -1;     //4
		Date			   ccedate = null;	//5
		Date			   eddate = null;	//6
		Date			   addate = null;	//7
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("INSERT INTO Purchase VALUES (?,?,?,?,?,?,?)");


			setNull(ps, askUser(in, "Purchase Receipt ID: "), true, 1, preceiptid);

			setNull(ps, askUser(in, "Purchase Date: "), false, 2, pdate);
			
			setNull(ps, askUser(in, "Customer ID: "), false, 3, ccid);
			
			setNull(ps, askUser(in, "Credit Card #: "), false, 4, ccard);
			
			setNull(ps, askUser(in, "Credit Card Expiry Date: "), false, 5, ccedate);
			
			setNull(ps, askUser(in, "Expected Delivery Date: "), false, 6, eddate);
			
			setNull(ps, askUser(in, "Actual Delivery Date: "), false, 7, addate);

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
        PreparedStatement  ps;  
          
        try
        {
          ps = con.prepareStatement("DELETE FROM Purchase WHERE purchase_receiptid = ?");
        
          System.out.print("\n Item Upc: ");
          preceiptid = Integer.parseInt(in.readLine());
          ps.setInt(1, preceiptid);

          int rowCount = ps.executeUpdate();

          if (rowCount == 0)
          {
              System.out.println("\nHasSong " + preceiptid + " does not exist!");
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
	    String             pdate;   //2
	    String             ccid;    //3
	    String             ccard; //4
	    String             ccardexp;  //5
	    String             pexpdate;     //6
	    String             pdeldate;    //7
	    Statement  stmt;
	    ResultSet  rs;

	    try
	    {
	        stmt = con.createStatement();

	        rs = stmt.executeQuery("SELECT * FROM Purchase");

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

	            preceiptid = rs.getString("purchase_receiptid");
	            System.out.printf("%-10.10s", preceiptid);

	            pdate = rs.getString("purchase_date");
	            checkNull(rs, pdate);

	            ccid = rs.getString("customer_cid");
	            checkNull(rs, ccid);

	            ccard = rs.getString("custorder_card#");
	            checkNull(rs, ccard);

	            ccardexp = rs.getString("custorder_expiryDate");
	            checkNull(rs, ccardexp);

	            pexpdate = rs.getString("purchase_expectedDate");
	            checkNull(rs, pexpdate);

	            pdeldate = rs.getString("purchase_deliveredDate");
	            checkNull(rs, pdeldate);
	            
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
