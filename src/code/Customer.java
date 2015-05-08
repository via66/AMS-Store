package code;                                                                  
                                                                                                                                                                                   
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class Customer extends Table{

	public Customer(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                ccid = -1;     //1
		int			       cphone = -1; 	//5
		
		PreparedStatement  ps; 
		
		BufferedReader in = new BufferedReader((new InputStreamReader(System.in)));
		try {
			
			Statement stmt = con.createStatement();
			ResultSet rs;
			
			System.out.println("Enter the Customer Id: ");			
			int cid = Integer.parseInt(in.readLine());
			rs = stmt.executeQuery("SELECT customer_cid FROM Customer WHERE customer_cid = " + cid);
			while (rs.next()) {
			if (!rs.getString("customer_cid").isEmpty()){
				System.out.println("Customer Id is taken. Please enter another one.");
				this.insert();
			}
			}
		
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try	{
			ps = con.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?,?)");


			setNull(ps, askUser(in, "Customer ID: "), true, 1, ccid);

			setNull(ps, askUser(in, "Customer Password: "), false, 2);
			
			setNull(ps, askUser(in, "Customer Name: "), false, 3);

			setNull(ps, askUser(in, "Customer Address: "), false, 4);
			
			setNull(ps, askUser(in, "Customer Phone: "), false, 5, cphone);

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
        int                ccid = -1;     
        PreparedStatement  ps;  
          
        try
        {
          ps = con.prepareStatement("DELETE FROM Customer WHERE customer_cid = ?");
        
          System.out.print("\n Customer ID: ");
          ccid = Integer.parseInt(in.readLine());
          ps.setInt(1, ccid);

          int rowCount = ps.executeUpdate();

          if (rowCount == 0)
          {
              System.out.println("\nCustomer " + ccid + " does not exist!");
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
	    String             ccid;     //1
	    String             cpassword;   //2
	    String             cname;    //3
	    String             caddress; //4
	    String             cphone;  //5
	    Statement  stmt;
	    ResultSet  rs;

	    try
	    {
	        stmt = con.createStatement();

	        rs = stmt.executeQuery("SELECT * FROM Customer");

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

	            ccid = rs.getString("customer_cid");
	            System.out.printf("%-10.10s", ccid);

	            cpassword = rs.getString("customer_password");
	            checkNull(rs, cpassword);

	            cname = rs.getString("customer_name");
	            checkNull(rs, cname);

	            caddress = rs.getString("customer_address");
	            checkNull(rs, caddress);

	            cphone = rs.getString("customer_phone");
	            checkNull(rs, cphone);
	            
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