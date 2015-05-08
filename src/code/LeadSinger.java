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
public class LeadSinger extends Table{

	public LeadSinger(Connection con) {
		super(con);
	}

	@Override
	void insert() {
		int                iupc = -1;     //1
	    PreparedStatement  ps;  
	      
	    try
	    {
	      ps = con.prepareStatement("INSERT INTO LeadSinger VALUES (?,?)");


	      setNull(ps, askUser(in, "Item UPC: "), true, 1, iupc);
	      
	      setNull(ps, askUser(in, "LeadSinger Name: "), true, 2);

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
        String 			   sname = null;
        PreparedStatement  ps;  
          
        try
        {
          ps = con.prepareStatement("DELETE FROM LeadSinger WHERE item_upc = ? AND singer_name = ?");
        
          System.out.print("\n Item Upc: ");
          iupc = Integer.parseInt(in.readLine());
          ps.setInt(1, iupc);

          System.out.print("\n Singer Name: ");
          sname = in.readLine();
          ps.setString(2, sname);
          
          int rowCount = ps.executeUpdate();

          if (rowCount == 0)
          {
              System.out.println("\nHasSong " + iupc + " " + sname + " does not exist!");
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
	    String             sname;   //2
	    Statement  stmt;
	    ResultSet  rs;

	    try
	    {
	        stmt = con.createStatement();

	        rs = stmt.executeQuery("SELECT * FROM LeadSinger");

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

	            sname = rs.getString("singer_name");
	            checkNull(rs, sname);
	            
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
