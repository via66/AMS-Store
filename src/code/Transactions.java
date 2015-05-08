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
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.sql.Date;

import GUI.NewJFrame;

/**
*
* @author Evan Ng, Emily Bird, Jennifer Lui, Velina Ivanova
*/
public class Transactions {
	Connection con;
	NewJFrame gui;
	BufferedReader in = new BufferedReader((new InputStreamReader(System.in)));
	int receiptno;		
	int returnId;
	SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
	final int ORDERS_A_DAY = 3;
	
	int receiptId;
	String rdate;

	public Transactions (Connection con, NewJFrame gui) {
		this.con = con;
		this.gui = gui;
	}

	/**
	 * 
	 * @param date starting date
	 * @param numDays number of days we want to add to date
	 * @return the resulting date when we add numDays to date
	 */
	public static Date addDays(Date date, int numDays)		
	{
		date.setTime(date.getTime() + numDays * 1000 * 60 * 60 * 24);
		return date;
	}

  //---------------------------IN STORE PURCHASE METHODS----------------------------------------------- 
    /**
     * Subtract from item stock the items that were just bought in store. If customer was trying to buy more than are available, 
     * sell max amount instead and inform customer.
     */
    public void updateAddItemStock() throws SQLException {
        
        Statement stmt = con.createStatement();
        Statement stmtt = con.createStatement();
        Statement stmttt = con.createStatement();
        ResultSet rs;
        ResultSet rss;
        
        rs = stmt.executeQuery("SELECT item_upc, purchaseitem_quantity FROM PurchaseItem WHERE purchase_receiptid = " + receiptno);
        
        // for all items bought subtract from stock the quantity customer bought
        // if they tried to buy more than are available, let them know, and give them the max number available
        while (rs.next()) {
            int upc = rs.getInt("item_upc");
            int qty = rs.getInt("purchaseitem_quantity");   
            rss = stmttt.executeQuery("SELECT item_stock FROM Item WHERE item_upc = " + upc);
            rss.next();
            int stock = rss.getInt(1);
            int diff = stock - qty;
            if (diff < 0) {
                stmtt.executeQuery("UPDATE Item SET item_stock = 0 WHERE item_upc = " + upc);
                gui.displayMessage("We do not have enough in stock. You bought " + stock + " instead.");
                stmt.executeUpdate("UPDATE PurchaseItem SET purchaseitem_quantity = " + stock + " WHERE purchase_receiptid = " + receiptno);
                
            } else {
                stmtt.executeQuery("UPDATE Item SET item_stock = item_stock - " + qty + " WHERE item_upc = " + upc);
            }
            
        }
        
        // inform customer of their receipt id so they can refund later if they wish
        gui.displayMessage("For future reference, your receiptId is " + receiptno);
    }
    
    /**
     * Insert new Purchase tuple for in store purchase
     * @throws SQLException
     * @throws IOException
     */
    public void insertNewPurchase() throws SQLException, IOException {
        
        Statement stmt = con.createStatement();
        ResultSet rs;
        
        // cid -1 is used as a default to refer to in-store customers 
        int cid = -1;
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        String purchaseDate = format.format(sqlDate);
        System.out.println("Current Date: " + purchaseDate); 

        // insert a new tuple into Purchase table with customer id and current date
        stmt.executeQuery("INSERT INTO Purchase (purchase_receiptid, purchase_date, customer_cid) VALUES ( purchase_receiptid_counter.nextval, to_date('" + purchaseDate + "','yyyy-MM-dd')" + ", " + cid + ")");
        rs = stmt.executeQuery("SELECT purchase_receiptid_counter.currval FROM Purchase");
        while (rs.next()) {
            receiptno = rs.getInt(1);       // get current counter for receiptno
        }
    }
    
    /**
     * 
     * @param iupc the item upc selected
     * @param quantity the quantity selected
     * @return true id successfully added to cart and false if this iupc does not exist in system
     * @throws SQLException
     * @throws IOException
     */
    public boolean addToCart(int iupc, int quantity) throws SQLException, IOException {

        Statement stmt = con.createStatement();
        ResultSet rs;

        // check if the item specified exists
        rs = stmt.executeQuery("SELECT item_title FROM Item WHERE item_upc = " + iupc);     
        if (!rs.next()) {
        	gui.displayMessage("No such item.");
        	return false;
        }
        
        // add tuple into PurchaseItem table
        stmt.executeQuery("INSERT INTO PurchaseItem VALUES (" + receiptno + ", " + iupc + ", " + quantity + ")");
        gui.displayMessage("INSERTED INTO PURCHASE ITEM");
        return true;
    }
    /**
     * Check if credit card is authorize and if yes update Purchase tuple with credit information
     * @param creditno the credit card number entered
     * @param expiryDate the credit card expiry date entered
     * @throws SQLException
     * @throws IOException
     */
    public void creditCardOrCash(int creditno, String expiryDate) throws SQLException, IOException {
        
        Statement stmt = con.createStatement();
        
            boolean q = gui.displayQuestion("Clerk, was the customer's card approved?");
            if (q == false) {
                boolean qq = gui.displayQuestion("Do you have cash?");
                if (qq == false) {
                    stmt.executeQuery("DELETE PurchaseItem WHERE purchase_receiptId = " + receiptno);
                    stmt.executeQuery("DELETE Purchase WHERE purchase_receiptId = " + receiptno);
                    return;
                } else {
                    creditno = -1;
                    expiryDate = null;  
                    updateAddItemStock();
                    return;
                }
            } 
                        
            // update customer card # and card expiry date
            stmt.executeQuery("UPDATE Purchase SET custorder_card# = " + creditno + " WHERE purchase_receiptid = " + receiptno);        
            stmt.executeQuery("UPDATE Purchase SET custorder_expiryDate = to_date('" + expiryDate + "','MM/yy')" + " WHERE purchase_receiptid = " + receiptno);
            
            updateAddItemStock();               
    }
    
    public void cancelCart() throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeQuery("DELETE PurchaseItem WHERE purchase_receiptId = " + receiptno);
        stmt.executeQuery("DELETE Purchase WHERE purchase_receiptId = " + receiptno);
    }
     
	
	//---------------------------IN STORE PURCHASE-----------------------------------------------	
    /** the following is the method for conducting an in store purchase through the console (rather than the GUI)*/
	public void inStorePurchase() throws SQLException, IOException, ParseException {
		Statement stmt = con.createStatement();
		Statement stmtt = con.createStatement();
		ResultSet rs;
		int iupc = -1;
		String purchaseDate = null;
		int quantity = -1;
		int creditno = -1;
		int cid = -1;
		String expiryDate = null;			

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		purchaseDate = format.format(sqlDate);
		System.out.println("Current Date: " + purchaseDate); 


		stmt.executeQuery("INSERT INTO Purchase (purchase_receiptid, purchase_date, customer_cid) VALUES ( purchase_receiptid_counter.nextval, to_date('" + purchaseDate + "','yyyy-MM-dd')" + ", " + cid + ")");
		rs = stmt.executeQuery("SELECT purchase_receiptid_counter.currval FROM Purchase");
		while (rs.next()) {
			receiptno = rs.getInt(1);		// get current counter for receiptno
		}

		do {	
			System.out.println("Please input upc of item.");
			iupc = Integer.parseInt(in.readLine());
			rs = stmt.executeQuery("SELECT item_title FROM Item WHERE item_upc = " + iupc);		
			rs.next();

			System.out.println("Insert Quantity.");
			quantity = Integer.parseInt(in.readLine()); 

			stmt.executeQuery("INSERT INTO PurchaseItem VALUES (" + receiptno + ", " + iupc + ", " + quantity + ")");
			System.out.println("Do you have more stuff? (Y/N)");
		} while ("Y".equals(in.readLine()));

		System.out.println("Are you paying by Credit Card? (Y/N) ");
		if ("Y".equals(in.readLine())) {
			System.out.println("Input Credit Card Number.");
			creditno = Integer.parseInt(in.readLine());
			System.out.println("Input Credit Card Expiry Date.");
			expiryDate = in.readLine();
			System.out.println("expiryDate:" + expiryDate);

			System.out.println("Clerk, was the customer's card approved?");
			if ("N".equals(in.readLine())) {
				System.out.println("Do you have cash?");
				if ("N".equals(in.readLine())) {
					stmt.executeQuery("DELETE PurchaseItem WHERE purchase_receiptId = " + receiptno);
					stmt.executeQuery("DELETE Purchase WHERE purchase_receiptId = " + receiptno);
					System.out.println("Purchase deleted");
					return;
				} else {
					creditno = -1;
					expiryDate = null;	
					System.out.println("check check.");

				}
			} 
			System.out.println("creditno = " + creditno + " expirydate: " + expiryDate);
			// update customer card # and card expiry date
			stmt.executeQuery("UPDATE Purchase SET custorder_card# = " + creditno + " WHERE purchase_receiptid = " + receiptno);		
			System.out.println("check 1");
			stmt.executeQuery("UPDATE Purchase SET custorder_expiryDate = to_date('" + expiryDate + "','MM/yy')" + " WHERE purchase_receiptid = " + receiptno);
			System.out.println("check 2");
		} 

		
		// update store stock
		rs = stmt.executeQuery("SELECT item_upc, purchaseitem_quantity FROM PurchaseItem WHERE purchase_receiptid = " + receiptno);

		while (rs.next()) {
			int upc = rs.getInt("item_upc");
			int qty = rs.getInt("purchaseitem_quantity");		
			stmtt.executeQuery("UPDATE Item SET item_stock = item_stock - " + qty + " WHERE item_upc = " + upc);

		}
	}

// ------------------------------------In STORE REFUND: Check Receipt------------------------------------------------   
    /**
     * @param rid the receipt id entered
     * @return true if the receipt id exists in the system and is no more than 15 days old
     */
    public boolean checkReceipt(int rid) throws SQLException, IOException, ParseException {
        
        receiptId = rid;
        
        Statement stmt = con.createStatement();
        ResultSet rs;

        rdate = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        rdate = format.format(sqlDate);     // gets return date or current date

        java.util.Date returnDate = format.parse(rdate);        // need Date returnDate for comparison later
        java.sql.Date sqlReturnDate = new java.sql.Date(returnDate.getTime());

        //System.out.println("Return Date: " + rdate); // prints out 2013-08-15

        // check if receipt id exists
        rs = stmt.executeQuery("SELECT purchase_receiptId FROM PurchaseItem WHERE purchase_receiptId = " + receiptId);
        if (rs.next() == false) {

            gui.displayMessage("Clerk, the receipt id does not exist.");
            return false;
        }

        // check that purchase date is within 15 days 

        // get purchase date
        rs = stmt.executeQuery("SELECT purchase_date FROM Purchase WHERE purchase_receiptid = " + receiptId);

        while (rs.next()) {
            String pdate = rs.getString("purchase_date");
            if (rs.wasNull()) {
                gui.displayMessage("Purchase date not found.");
                return false;
            }

            //calculate when the last possible day for refund is 
            java.util.Date purchaseDate = format.parse(pdate);      // turned purchase date into a Date
            java.sql.Date sqlPurchaseDate = new java.sql.Date(purchaseDate.getTime());
            Date addedDate = addDays(sqlPurchaseDate, 15);
                    
            if (sqlReturnDate.after(addedDate)) {           // if today is after the refund deadline date, then...
                gui.displayMessage("Clerk, the refund deadline has passed. Customer is unable to do a refund anymore.");
                return false;
            }
        }
        
        return true;
    }
    
    // ------------------------------------In STORE REFUND------------------------------------------------  
    /**
     * @param iupc the item upc to be returned
     * @param iquantity the item quantity to be returned
     * @return true if successfully returned, false if price cannot be found or iquantity is greater than quantity bought
     */
    public boolean inStoreRefund(int iupc, int iquantity) throws SQLException, IOException, ParseException {

        Statement stmt = con.createStatement();
        ResultSet rs;
        
        // get price of item that they would like to return
        String iprice;
        
        rs = stmt.executeQuery("SELECT item_price FROM Item WHERE item_upc = " + iupc);
        rs.next();
        if (rs.getString(1) != null){
            iprice = rs.getString(1);       // multiply by quantity
            Double ipriceDouble = Double.parseDouble(iprice);
            ipriceDouble *= iquantity;
            iprice = "" + ipriceDouble;
        }
        else {
            gui.displayMessage("Price cannot be found."); 
            return false;
        }

        rs = stmt.executeQuery("SELECT purchaseItem_quantity FROM PurchaseItem WHERE purchase_receiptid = " + receiptId);
        rs.next();
        if (rs.getInt(1)>iquantity) {
        	gui.displayMessage("You did not buy this many here. We can not refund."); 
        	return false;
        }
        
        // find out whether customer paid credit card or cash
        rs = stmt.executeQuery("SELECT custorder_card# FROM Purchase WHERE purchase_receiptid = " + receiptId);
        rs.next();        
        
        String cardno = rs.getString(1);
        if (cardno != null){
            gui.displayMessage("Clerk, refund $" + iprice + " into credit card number " + cardno + " for item.");
        }
        else {
            gui.displayMessage("Clerk, refund $" + iprice + " for item in cash.");
        }

        stmt.executeQuery("INSERT INTO Return VALUES (return_retid_counter.nextval , to_date('" + rdate + "','yyyy-MM-dd')" + ", " + receiptId + ")");
        stmt.executeQuery("INSERT INTO ReturnItem VALUES (return_retid_counter.currval, " + iupc + ", " + iquantity + ")");   

        // each refund will consist of one type of item

        // update item storage
        stmt.executeQuery("UPDATE Item SET item_stock = item_stock + " + iquantity + " WHERE item_upc = " + iupc);   
        
        con.commit();
        return true;
    }


	//-------------------------------ONLINE PURCHASE------------------------------------------------------- 

    /**
     * @param id - the customer id to check
     * @return true if id is in the system, false otherwise
     */
	public boolean checkCustomerID (String id) throws SQLException {
		int cid = Integer.parseInt(id);

		ResultSet rs;
		Statement stmt = con.createStatement();

		rs = stmt.executeQuery("SELECT * FROM Customer WHERE customer_cid = " + cid);
		if (!rs.next()) return false;
		return true;
	}
	
	/**
	 * Check if inputed password matches the one we have for this cid in the system.
	 * @param id the id entered
	 * @param pass the password entered
	 * @return true if correct password, false otherwise
	 * @throws SQLException
	 */
	public boolean checkCustomerPassword (String id, String pass) throws SQLException {
		int cid = Integer.parseInt(id);
		String expectedPassword;
		ResultSet rs;
		Statement stmt = con.createStatement();
		
		rs = stmt.executeQuery("SELECT * FROM Customer WHERE customer_cid = " + cid);
		rs.next();
		expectedPassword = rs.getString("customer_password");
		if (expectedPassword.equals(pass)) return true;
		return false;
		
	}
	
	/**
	 * Insert Purchase tuple for an in-store purchase.
	 * @param cid the customer id
	 * @return the value of the receipt id for current purchase
	 * @throws SQLException
	 */
	public int insertPurchase (int cid) throws SQLException {
		ResultSet rs;
		Statement stmt = con.createStatement();
		// insert a purchase tuple with the receiptid and cid
		stmt.executeQuery("INSERT INTO Purchase (purchase_receiptid, customer_cid) VALUES (purchase_receiptid_counter.nextval, " + cid + ")");

		// for future use, get and return the current value of the receiptno
		rs = stmt.executeQuery("SELECT purchase_receiptid_counter.currval FROM Purchase");
		rs.next();
		return rs.getInt(1);
	}
	
	/**
	 * Finds all items in database that match the customer's search.
	 * @param category the category entered
	 * @param title the title entered
	 * @param singer the singer entered
	 * @return a formatted list of all items that match description
	 * @throws SQLException
	 */
	public String getItemList(String category, String title, String singer) throws SQLException {
		ResultSet rs = null;
		Statement stmt = con.createStatement();
		StringBuffer list = new StringBuffer();
		
		
		// get list of items based on customer description
		if (category.isEmpty() && title.isEmpty() && singer.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item MINUS SELECT * FROM Item WHERE item_stock = 0");
		} else if (title.isEmpty() && singer.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item WHERE item_category = '" + category + "' MINUS SELECT * FROM Item WHERE item_stock = 0");  
		} else if (category.isEmpty() && singer.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item WHERE item_title = '" + title + "' MINUS SELECT * FROM Item WHERE item_stock = 0");                
		} else if (category.isEmpty() && title.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item I, LeadSinger L WHERE I.item_upc = L.item_upc AND singer_name = '" + singer + "' MINUS SELECT * FROM Item WHERE item_stock = 0");             
		} else if (category.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item I, LeadSinger L WHERE I.item_upc = L.item_upc AND singer_name = '" + singer + "' AND item_title = '" + title + "' MINUS SELECT * FROM Item WHERE item_stock = 0");                  
		} else if (title.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item I, LeadSinger L WHERE I.item_upc = L.item_upc AND singer_name = '" + singer + "' AND item_category = '" + category + "' MINUS SELECT * FROM Item WHERE item_stock = 0");                
		} else if (singer.isEmpty()) {
			rs = stmt.executeQuery("SELECT * FROM Item WHERE item_category = '" + category + "' AND item_title = '" + title + "' MINUS SELECT * FROM Item WHERE item_stock = 0");              
		}

		String             ititle;   //2
		String             itype;    //3
		String             icategory; //4
		String             icompany;  //5
		String             iyear;     //6
		String             iprice;    //7
		String             istock;    //8

		ResultSetMetaData rsmd = rs.getMetaData();
		int numCols = rsmd.getColumnCount();

		String[] columnNames = {"UPC", "Title", "Type", "Category", "Company", "Year", "Price", "Stock"};
		list.append("\n");
		// display column names;
		for (int i = 0; i < numCols; i++) {
			//list.append("    " + rsmd.getColumnName(i+1) + "   ");
			list.append("    " + columnNames[i] + "        ");
		}
		list.append("\n\n");

		while(rs.next()) {
			list.append("      " + rs.getString("item_upc")+ "     ");

			ititle = rs.getString("item_title");
			if (rs.wasNull()) list.append("      ");
			else list.append("  " + ititle + "     ");

			itype = rs.getString("item_type");
			if (rs.wasNull()) list.append("      ");
			else list.append("  " + itype + "    ");

			icategory = rs.getString("item_category");
			if (rs.wasNull()) list.append("      ");
			else list.append("       " + icategory + "    ");

			icompany = rs.getString("item_company");
			if (rs.wasNull()) list.append("      ");
			else list.append("         " + icompany + "    ");
 
			iyear = rs.getString("item_year");
			if (rs.wasNull()) list.append("      ");
			else list.append("         " + iyear + "    ");

			iprice = rs.getString("item_price");
			if (rs.wasNull()) list.append("      ");
			else list.append("       " + iprice + "    ");

			istock = rs.getString("item_stock");
			if (rs.wasNull()) list.append("      ");
			else list.append("       " + istock + "    ");

			list.append("\n");


		}
		
		return list.toString();
	}
	
	/**
	 * Adds qty (or max available) of upc to customer's cart.
	 * @param upc the item upc
	 * @param qty the quantity selected
	 * @param receiptno the receipt id for current purchase
	 * @throws SQLException
	 */
	public void addToCart (int upc, int qty, int receiptno) throws SQLException {
		// check how many are in stock and if not enough, ask customer if they want to settle for the available
		int availableQty;
		ResultSet rs;
		Statement stmt = con.createStatement();
		rs = stmt.executeQuery("SELECT item_stock FROM Item WHERE item_upc = " + upc);

		if (!rs.next()) {
			gui.displayMessage("No such item.");
			return;
		}

		availableQty = rs.getInt("item_stock");

		if (availableQty < qty) {
			if (gui.displayQuestion("We don't have that many. Is " + availableQty + " ok?")) {
				qty = availableQty;	
			} else {
				gui.displayMessage("Nothing was added to your cart.");
				return;
			}				
		}

		try {
			stmt.executeUpdate("INSERT INTO PurchaseItem VALUES (" + receiptno + ", " + upc + ", " + qty + ")");
			gui.displayMessage("You added " + qty + " of item# " + upc + " to your cart.");
		} catch (SQLException e) {
			gui.displayMessage("You already put this item in your cart previously. We will update your cart with the new quantity.");
			stmt.executeUpdate("UPDATE PurchaseItem SET purchaseItem_quantity = " + qty + " WHERE item_upc = " + upc + " AND purchase_receiptid = " + receiptno);
		}

	}
	
	/**
	 * Displays current bill for customer.
	 * @param receiptno the current receipt id
	 * @return formatted string with cart items and total
	 * @throws SQLException
	 */
	public String viewCart(int receiptno) throws SQLException {
		ResultSet rs;
		Statement stmt = con.createStatement();
		StringBuffer bill = new StringBuffer();
		double total = 0;
		
		rs = stmt.executeQuery("SELECT I.item_upc, purchaseItem_quantity, item_price FROM Item I, PurchaseItem R WHERE I.item_upc = R.item_upc AND R.purchase_receiptId = " + receiptno);
		bill.append("  UPC         Units         Price\n");
		
		while (rs.next()) {
			bill.append("    " + rs.getString("item_upc") + "     ");
			bill.append("           " + rs.getString("purchaseItem_quantity") + "    ");
			rs.getString("item_price");
			if (rs.wasNull()) {
				bill.append("       FREE   ");
			} else {
				bill.append("       $" + rs.getString("item_price") + "    ");
			}
			if (!rs.wasNull()){
				total += (rs.getDouble("item_price")*rs.getInt("purchaseItem_quantity"));
			}
			bill.append("\n");
		}
		bill.append("\nTOTAL:       $" + Math.floor(total * 100) / 100);
		return bill.toString();
	}
	
	/**
	 * Updates Purchase tuple with given information and informs customer of expected delivery date 
	 * @param creditno the credit card number entered
	 * @param date the credit card expiry date entered
	 * @param receiptno the current purchase's receipt id
	 * @throws SQLException
	 */
	public void finalizePurchase (int creditno, String date, int receiptno) throws SQLException {
		ResultSet rs;
		Statement stmt = con.createStatement();
		int outstandingOrders = 0;
		int daysUntilDelivery;
		java.sql.Date currDate;
		currDate = new java.sql.Date( (new java.util.Date()).getTime());
		String purchaseDate = format.format(currDate);


		// finalize purchase by inserting the rest of the info into the tuple


		// determine expected date of delivery
		rs = stmt.executeQuery("SELECT purchase_expectedDate, purchase_deliveredDate FROM Purchase");
		while (rs.next()) {
			rs.getDate("purchase_expectedDate");
			if (!rs.wasNull()) {
				rs.getDate("purchase_deliveredDate");
				if (rs.wasNull()) {
					outstandingOrders++;
				}
			}
		}
		daysUntilDelivery = outstandingOrders/ORDERS_A_DAY;
		java.sql.Date expectedDate = addDays(currDate, daysUntilDelivery);
		String expDate = format.format(expectedDate);

		stmt.executeUpdate("UPDATE Purchase SET purchase_date = to_date('" + purchaseDate + "', 'yyyy-MM-dd'), custorder_card# = " + creditno + ", custorder_expiryDate = to_date( '" + date + "','MM/yy'), purchase_expectedDate = to_date('" + expDate + "','yyyy-MM-dd') WHERE purchase_receiptid = " + receiptno);
		gui.displayMessage("Thank you for shopping at AMS. Your order should arrive in about " + daysUntilDelivery + " days. \nFor future reference, your receipt ID is " + receiptno + ".");

	}
	
	/**
	 * Subtracts from store stock the items which were bought from customer (listed on given receipt id)
	 * @param receiptno the current purchase's receipt id
	 * @throws SQLException
	 */
	public void updateStoreStock(int receiptno) throws SQLException {
		ResultSet rs;
		Statement stmt = con.createStatement();
		Statement stmtB = con.createStatement();
		// update store stock
		rs = stmt.executeQuery("SELECT item_upc, purchaseItem_quantity FROM PurchaseItem WHERE purchase_receiptid = " + receiptno);

		while (rs.next()) {         
			int q = rs.getInt("purchaseItem_quantity");
			int u = rs.getInt("item_upc");
			stmtB.executeQuery("UPDATE Item SET item_stock = item_stock - " + q + " WHERE item_upc = " + u);            
		}
	}
	
	/** the following is the method for conducting an online purchase through the console (not the GUI)*/
	public void onlinePurchase () throws  IOException, SQLException, ParseException {
		int id; // customer id
		String pass; // customer password
		Statement stmt = con.createStatement(); // the universal statement
		Statement stmtB = con.createStatement(); // actually we needed another one
		PreparedStatement ps; // prepared statement for updating Purchase entry at the end
		ResultSet rs;  // ResultSet for the universal statement

		String category; // input category
		String title;   // input title
		String singer;  // input lead singer

		int upc = 0;          // upc of selected item
		int qty = 0;            // quantity desired of selected item
		int availableQty = 0;   // number of pieces available in store

		double total = 0.0;     // customer total

		int creditno;        // credit card number
		SimpleDateFormat format = new SimpleDateFormat("MM/yy");
		java.util.Date javaDate; 
		java.sql.Date date;   // credit card expiry date

		int outstandingOrders = 0;   // number of orders not delivered
		int daysUntilDelivery;      // estimated time until delivery
		java.sql.Date currDate;     // the current date

		boolean outOfItems = false;  // set if user goes through everything but does not select

		int receiptno;              // use to store receiptno gotten from sequence currval

		// get the user id and password
		do {
			System.out.println("Identify yourself.");
			id = Integer.parseInt(in.readLine());
			rs = stmt.executeQuery("SELECT * FROM Customer WHERE customer_cid = " + id);
		} while(!rs.next());

		do {
			System.out.println("Pass word?");
			pass = in.readLine();       
		} while (!rs.getString("customer_password").equals(pass));

		// insert a purchase tuple with the receiptid and cid
		stmt.executeQuery("INSERT INTO Purchase (purchase_receiptid, customer_cid) VALUES (purchase_receiptid_counter.nextval, " + id + ")");

		// for future use, get and store the current value of the receiptno
		rs = stmt.executeQuery("SELECT purchase_receiptid_counter.currval FROM Purchase");
		rs.next();
		receiptno = rs.getInt(1);

		// huge loop for selecting items
		do {
			System.out.println("Let's describe the item you want...");
			System.out.println("Category:");
			category = in.readLine();
			System.out.println("Title:");
			title = in.readLine();
			System.out.println("Leading Singer:");
			singer = in.readLine();

			// get list of items based on customer description
			if (category.isEmpty() && title.isEmpty() && singer.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item MINUS SELECT * FROM Item WHERE item_stock = 0");
			} else if (title.isEmpty() && singer.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item WHERE item_category = '" + category + "' MINUS SELECT * FROM Item WHERE item_stock = 0");  
			} else if (category.isEmpty() && singer.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item WHERE title = '" + title + "' MINUS SELECT * FROM Item WHERE item_stock = 0");                
			} else if (category.isEmpty() && title.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item I, LeadSinger L WHERE I.item_upc = L.item_upc AND singer_name = '" + singer + "' MINUS SELECT * FROM Item WHERE item_stock = 0");             
			} else if (category.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item I, LeadSinger L WHERE I.item_upc = L.item_upc AND singer_name = '" + singer + "' AND item_title = '" + title + "' MINUS SELECT * FROM Item WHERE item_stock = 0");                  
			} else if (title.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item I, LeadSinger L WHERE I.item_upc = L.item_upc AND singer_name = '" + singer + "' AND item_category = '" + category + "' MINUS SELECT * FROM Item WHERE item_stock = 0");                
			} else if (singer.isEmpty()) {
				rs = stmt.executeQuery("SELECT * FROM Item WHERE item_category = '" + category + "' AND item_title = '" + title + "' MINUS SELECT * FROM Item WHERE item_stock = 0");              
			}

			// go through list asking customer if this is the one they want
			do {
				if (!rs.next()) {
					outOfItems = true;
					break;
				}
				upc = rs.getInt("item_upc");
				System.out.println("Did you mean " + upc + ", " + rs.getString("item_title") + "?");

			} while ("N".equals(in.readLine()));

			if (outOfItems) {
				System.out.println("No more items matching the description. Want to start over?");
				outOfItems = false;
				continue;
			}


			// for item selected, insert tuple into PurchaseItem
			System.out.println("How many do you want?");
			qty = Integer.parseInt(in.readLine());

			// check how many are in stock and if not enough, ask customer if they want to settle for the available

			rs = stmt.executeQuery("SELECT item_stock FROM Item WHERE item_upc = " + upc);
			rs.next();

			availableQty = rs.getInt("item_stock");

			if (availableQty < qty) {
				System.out.println("We don't have that many. Is " + availableQty + " okay?"); 
				if ("Y".equals(in.readLine())) {
					qty = availableQty;
				}
			}

			try {
				stmt.executeUpdate("INSERT INTO PurchaseItem VALUES (" + receiptno + ", " + upc + ", " + qty + ")");
			} catch (SQLException e) {
				System.out.println("You already put this item in your cart previously. We will update your cart with the new quantity.");
				stmt.executeUpdate("UPDATE PurchaseItem SET purchaseItem_quantity = " + qty + " WHERE item_upc = " + upc + " AND purchase_receiptid = " + receiptno);
			}

			System.out.println("Do you want to buy more stuff?");
		} while ("Y".equals(in.readLine()));


		// print bill for customer
		rs = stmt.executeQuery("SELECT I.item_upc, purchaseItem_quantity, item_price FROM Item I, PurchaseItem R WHERE I.item_upc = R.item_upc AND R.purchase_receiptId = " + receiptno);

		while (rs.next()) {
			System.out.printf("%-10.10s", rs.getString("item_upc"));
			System.out.printf("%-10.10s", rs.getString("purchaseItem_quantity"));
			System.out.printf("%-10.10s", rs.getString("item_price"));
			total += (rs.getDouble("item_price")*rs.getInt("purchaseItem_quantity"));
			System.out.println("");
		}
		System.out.println("TOTAL:       " + Math.floor(total * 100) / 100);

		// get payment information
		System.out.println("Credit Card Number Please:");
		creditno = Integer.parseInt(in.readLine());
		System.out.println("Credit Card Expiry Date:");
		javaDate = format.parse(in.readLine());
		date = new java.sql.Date(javaDate.getTime());

		// finalize purchase by inserting the rest of the info into the tuple
		con.setAutoCommit(false);
		ps = con.prepareStatement("UPDATE Purchase SET purchase_date = ?, custorder_card# = ?, custorder_expiryDate = ?, purchase_expectedDate = ?, purchase_deliveredDate = ? WHERE purchase_receiptid = " + receiptno);
		currDate = new java.sql.Date( (new java.util.Date()).getTime());
		ps.setDate(1, currDate);  // sets current date
		ps.setInt(2, creditno);
		ps.setDate(3, date);

		// determine expected date of delivery
		rs = stmt.executeQuery("SELECT purchase_expectedDate, purchase_deliveredDate FROM Purchase");
		while (rs.next()) {
			rs.getDate("purchase_expectedDate");
			if (!rs.wasNull()) {
				rs.getDate("purchase_deliveredDate");
				if (rs.wasNull()) {
					outstandingOrders++;
				}
			}
		}
		daysUntilDelivery = outstandingOrders/ORDERS_A_DAY;
		ps.setDate(4, addDays(currDate, daysUntilDelivery));
		ps.setDate(5, null);

		ps.executeUpdate();
		con.commit();
		ps.close();
		con.setAutoCommit(true);

		System.out.println("Thank you for shopping at AMS. Your order should arrive in about " + daysUntilDelivery + " days.");

		// update store stock
		rs = stmt.executeQuery("SELECT item_upc, purchaseItem_quantity FROM PurchaseItem WHERE purchase_receiptid = " + receiptno);

		while (rs.next()) {         
			int q = rs.getInt("purchaseItem_quantity");
			int u = rs.getInt("item_upc");
			stmtB.executeQuery("UPDATE Item SET item_stock = item_stock - " + q + " WHERE item_upc = " + u);            
		}

	}

	//--------------------------DAILY REPORT------------------------------------------------------------------------    
	/**
	 * @param date the date for which a report is requested
	 * @return a formatted string containing report
	 */
	public String dailyReport(String date) throws ParseException, IOException, SQLException {
		int units = 0;
		double value = 0;
		double totalVal = 0;
		int totalUn = 0;
		String prevCat;      
		ResultSet rs;
		Statement stmt = con.createStatement();
		StringBuffer report = new StringBuffer();

		stmt.executeQuery("CREATE VIEW QtyBought (upc, purchaseitem_quantity) AS " 
				+ "(SELECT item_upc, SUM(purchaseitem_quantity) "
				+ "FROM PurchaseItem R, Purchase P "
				+ "WHERE P.purchase_receiptid = R.purchase_receiptId AND P.purchase_date = to_date('" + date + "', 'yyyy-MM-dd')" 
				+ " GROUP BY item_upc)");

		rs = stmt.executeQuery("SELECT I.item_upc, item_category, item_price, Q.purchaseitem_quantity, item_price*purchaseitem_quantity as value "
				+ "FROM Item I, QtyBought Q "
				+ "WHERE I.item_upc = Q.upc "
				+ "ORDER BY item_category");


		// get info on ResultSet
		ResultSetMetaData rsmd = rs.getMetaData();

		// get number of columns
		int numCols = rsmd.getColumnCount();
		String[] columnNames = {"UPC", "Category", "Price", "# Units", "Value"};

		report.append("\n");

		// display column names;
		for (int i = 0; i < numCols; i++)
		{
			// get column name and print it
			//report.append("  " + rsmd.getColumnName(i+1) + "  ");
			report.append("  " + columnNames[i] + "            ");
		}

		//System.out.println(" ");
		report.append("\n\n");

		if (!rs.next()) {
			stmt.executeQuery("DROP VIEW QtyBought");
			return "Nothing sold on this day";
		}
		
		prevCat = rs.getString("item_category");
		do {
			if (!rs.getString("item_category").equals(prevCat)) {
				//report.append("   total                                                          ");
				report.append(String.format("%-80s", "   total"));
				report.append(units + "                "); 
				report.append(Math.floor(value * 100) / 100);
				report.append("\n");
				report.append("-----------------------------------------------------------------------------------");
				report.append("\n");
				totalUn += units;
				totalVal += value;
				units = 0;
				value = 0;
			} 
			report.append("    ");
			report.append(String.format("%1$-15s %2$-30s %3$-20s %4$-20s %5$-30s", rs.getString("item_upc"), rs.getString("item_category"), rs.getString("item_price"), rs.getString("purchaseItem_quantity"), rs.getString("value")));
			prevCat = rs.getString("item_category");
			units += rs.getInt("purchaseitem_quantity");
			value += rs.getDouble("value");
			report.append("\n");

		} while (rs.next());
		// have to repeat one more time for the last category (perhaps there is an easier way but isLast doesn't work in oracle, so I couldn't do that)
		
		report.append(String.format("%-80s", "   total"));
		report.append(units + "                  "); 
		report.append(Math.floor(value * 100) / 100);
		report.append("\n");
		totalUn += units;
		totalVal += value;
		units = 0;
		value = 0;
		report.append("\n");
		report.append("        day total                                           ");
		report.append(totalUn + " units    $");
		report.append(Math.floor(totalVal * 100) / 100);
		report.append("\n");

		stmt.executeQuery("DROP VIEW QtyBought");
		con.commit();
		return report.toString();

	}

	//----------------------------------------RE-STOCK STORE-------------------------------------------------
	
	/**
	 * Adds qty of iupc to stock
	 * @param iupc the item upc specified
	 * @param qty the quantity specified
	 */
	public void addItems (int iupc, int qty) throws IOException, SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs;

		rs = stmt.executeQuery("SELECT item_upc, item_title FROM Item WHERE item_upc = " + iupc);
		if (rs.next()) {
			if (!gui.displayQuestion("Is this the one you meant: " + rs.getString(1) + ", " + rs.getString("item_title") + "?")) {
				gui.displayMessage("Item not added.");
			} else {
				stmt.executeUpdate("UPDATE Item SET item_stock = item_stock + " + qty + " WHERE item_upc = " + iupc);
			}

		} else {		
			boolean confirm = gui.displayQuestion("This item does not exist in the database. Do you want to add it?");
			if (!confirm) {
				gui.displayMessage("Item not added.");
			} else {
				stmt.executeUpdate("INSERT INTO Item (item_upc, item_stock) VALUES (" + iupc + ", " + qty + ")");
			}
		}		
		con.commit();
	}
	
	/**
	 * Adds qty of iupc to stock and updates its price to price
	 * @param iupc the item upc specified
	 * @param qty the quanitty specified
	 * @param price the price specified
	 * @throws IOException
	 * @throws SQLException
	 */
	public void addItems (int iupc, int qty, double price) throws IOException, SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs;

		rs = stmt.executeQuery("SELECT item_upc, item_title FROM Item WHERE item_upc = " + iupc);
		if (rs.next()) {
			if (!gui.displayQuestion("Is this the one you meant: " + rs.getString(1) + ", " + rs.getString("item_title") + "?")) {
				gui.displayMessage("Item not added.");
			} else {
				stmt.executeUpdate("UPDATE Item SET item_stock = item_stock + " + qty + ", item_price = " + price + " WHERE item_upc = " + iupc);
			}

		} else {		
			boolean confirm = gui.displayQuestion("This item does not exist in the database. Do you want to add it?");
			if (!confirm) {
				gui.displayMessage("Item not added.");
			} else {
				stmt.executeUpdate("INSERT INTO Item (item_upc, item_stock, item_price) VALUES (" + iupc + ", " + qty + ", " + price + ")");
			}
		}		
		con.commit();
	}
	
	//---------------------------------PROCESS DELIVERY--------------------------------------------------------
	/**
	 * Updates the delivered date of specified order to the current date
	 * @param receiptid the receiptid for the delivery we want want to process
	 */
	public void processDelivery(int receiptid) throws NumberFormatException, IOException, SQLException {
		Statement stmt = con.createStatement();
		java.sql.Date currDate =  new java.sql.Date( (new java.util.Date()).getTime());
		String date = format.format(currDate);
		
		stmt.executeUpdate("UPDATE Purchase SET purchase_deliveredDate = to_date('" + date + "', 'yyyy-MM-dd') WHERE purchase_receiptid = " + receiptid);
		gui.displayMessage("Order has been processed.");
		con.commit();
	}

	//--------------------------------TOP SELLING REPORT -----------------------------------------------------------
	/**
	 * @param num the number of top items we want the report for
	 * @param date the date we want the report for
	 * @return formatted string containing report
	 */
	public String topSellingItems(int num, String date) throws IOException, SQLException{
		//SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd");
		Statement stmt = con.createStatement();
		ResultSet rs;
		StringBuffer report = new StringBuffer();
		
		// Select top "num" selling items		
		rs = stmt.executeQuery("SELECT TOPTEN.item_title, TOPTEN.item_company, TOPTEN.item_stock, TOPTEN.QSUM "+ // selects the top "num" selling items
				  // sum of purchase quantities of each item ordered by best selling to worst
				  			   "FROM (SELECT I.item_title, I.item_company, I.item_stock, QTYSUM.QSUM " +
         		                 // items purchased on that date
         		                      "FROM Item I, (SELECT I.item_upc, SUM(PI.purchaseItem_quantity) AS QSUM " +
         		                      				 "FROM Purchase P, PurchaseItem PI, Item I " +
         		                      				 "WHERE P.purchase_receiptId = PI.purchase_receiptId AND PI.item_upc = I.item_upc AND P.purchase_date = to_date('" + date + "', 'YYYY-MM-DD') " +
         		                      				 "GROUP BY I.item_upc " +
         		                      				 "ORDER BY SUM(PI.purchaseItem_quantity) DESC) QTYSUM " +
         		                      "WHERE I.item_upc = QTYSUM.item_upc " +
         		                      "ORDER BY QSUM DESC) TOPTEN " +
         		                "WHERE ROWNUM <= " + num + " " +
								"ORDER BY TOPTEN.QSUM DESC");
		
		String[] columnNames = {"Title", "Company", "Stock", "Sold"};
		for (int i = 0; i<4; i++) {
			report.append("          " + columnNames[i] + "                              " );
		}
		report.append("\n");
		
		
		// Print out the top "num" selling items
		int j = 1;
		while(rs.next()){
			report.append(j + ". ");
			
			for(int i = 1; i <= 4; i++){
				int length = 50 - (rs.getString(i).length()) / 2;
				report.append(String.format("%-"+ length + ".60s", rs.getString(i)));
				//System.out.print(rs.getString(i) + ", ");
			}
			//System.out.println("");
			report.append("\n");
			j++;
		}
		return report.toString();
	}

	//-------------------------------------------------CUSTOMER REGISTRATION-----------------------------------------------
	/**
	 * With the provided information, adds customer to the system.
	 * @param id customer inputted id
	 * @param name the customer name
	 * @param pass the customer password
	 * @param addr the customer address
	 * @param phone the customer phone number
	 */
	public boolean registerCustomer(int id, String name, String pass, String addr, int phone) throws SQLException {

		Statement stmt = con.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery("SELECT customer_cid FROM Customer WHERE customer_cid = " + id);
		while (rs.next()) {
			if (!rs.getString("customer_cid").isEmpty()){
				gui.displayMessage("Customer Id is taken. Please enter another one.");
				return false;
			}
		}
		stmt.executeUpdate("INSERT INTO Customer VALUES (" + id + ", '" + pass + "', '" + name + "', '" + addr + "', " + phone + ")");
		con.commit();
		gui.displayMessage("Your account has been created. You can now shop online.");
		return true;
	}


}
