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
public class Return extends Table{

	public Return(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                rid = -1;     //1
		Date			   rdate = null;
		int			       preceiptid = -1; //3
		
		PreparedStatement  ps;  

		try
		{
			ps = con.prepareStatement("INSERT INTO Return VALUES (?,?,?)");

			setNull(ps, askUser(in, "Return ID: "), true, 1, rid);

			setNull(ps, askUser(in, "Return Date: "), false, 2, rdate);
			
			setNull(ps, askUser(in, "Purchase Receipt ID: "), false, 3, preceiptid);

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
        PreparedStatement  ps;  
          
        try
        {
          ps = con.prepareStatement("DELETE FROM Return WHERE return_retid = ?");
        
          System.out.print("\n Return ID: ");
          rrid = Integer.parseInt(in.readLine());
          ps.setInt(1, rrid);

          int rowCount = ps.executeUpdate();

          if (rowCount == 0)
          {
              System.out.println("\nItem " + rrid + " does not exist!");
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
	    String             rretid;     //1
	    String             rdate;   //2
	    String             preceiptid;    //3
	    Statement  stmt;
	    ResultSet  rs;

	    try
	    {
	        stmt = con.createStatement();

	        rs = stmt.executeQuery("SELECT * FROM Return");

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

	            rretid = rs.getString("return_retid");
	            System.out.printf("%-10.10s", rretid);

	            rdate = rs.getString("return_date");
	            checkNull(rs, rdate);

	            preceiptid = rs.getString("purchase_receiptId");
	            checkNull(rs, preceiptid);

	            
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
