package code;                                                                     
                                                                                                            
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/**
*
* @author Emily Bird
*/
// Set-up an example database for the purposes of testing or demonstrating
public class Database {
	Connection con;
	
	public Database (Connection con) {
		this.con = con;
	}
	
	public void insertItems() {
		try {
		Statement stmt = con.createStatement();

		stmt.executeQuery("DELETE FROM ReturnItem");
		stmt.executeQuery("DELETE FROM Return");
		stmt.executeQuery("DELETE FROM PurchaseItem");
		stmt.executeQuery("DELETE FROM Purchase");
		stmt.executeQuery("DELETE FROM Customer");
		stmt.executeQuery("DELETE FROM HasSong");
		stmt.executeQuery("DELETE FROM LeadSinger");
		stmt.executeQuery("DELETE FROM Item");
		
		String items = "INSERT ALL " +
							"INTO Item VALUES (1, 'Hello World', 'CD', 'pop', 'EMI', 1990, 24.99, 100) " +
							"INTO Item VALUES(2, 'Boring House', 'CD', 'rock', 'WMG', 1990, 24.99, 100) " +
							"INTO Item VALUES(3, 'Sunshine', 'CD', 'new age', 'Sony', 1992, 24.99, 100) " +
							"INTO Item VALUES(4, 'Yablaka', 'CD', 'instrumental', 'Sony', 1991, 24.99, 150) " +
							"INTO Item VALUES(5, 'Paniting Pictures', 'DVD', 'pop', 'Sony', 1990, 29.99, 100) " +
							"INTO Item VALUES(6, 'Brain Salad', 'CD', 'pop', 'OMG Records', 1992, 24.99, 100) " +
							"INTO Item VALUES(7, 'Lochka', 'CD', 'instrumental', 'Spice', 1993, 9.99, 32) " +
							"INTO Item VALUES(8, 'Il pleut maintenant', 'CD', 'pop', 'WMG', 1993, 24.99, 100) " +
							"INTO Item VALUES(9, 'Summer Garden', 'DVD', 'new age', 'WMG', 1993, 24.99, 150) " +
							"INTO Item VALUES(10, 'Penguins', 'CD', 'rap', 'WMG', 1986, 24.99, 200) " +
							"INTO Item VALUES(11, 'Charlie Needs a Heart', 'DVD', 'country', 'WMG', 1979, 24.99, 100) " +
							"INTO Item VALUES(12, 'Home in the Nest', 'CD', 'pop', 'Sony', 1991, 24.99, 100) " +
							"INTO Item VALUES(13, 'Places I Have Never Been', 'DVD', 'pop', 'Sony', 1989, 24.99, 100) " +
							"INTO Item VALUES(14, 'Swan Lake', 'CD', 'classical', 'WMG', 1999, 15.99, 82) " +
							"INTO Item VALUES(15, 'The Nutcracker', 'CD', 'classical', 'WMG', 1999, 15.99, 74) " +
						"SELECT 1 FROM DUAL";
		
		String leadSingers = "INSERT ALL " +
				"INTO LeadSinger VALUES (1, 'Leo Banks') " +
				"INTO LeadSinger VALUES (2, 'Remy Loave') " +
				"INTO LeadSinger VALUES (3, 'Sunny Skye') " +
				"INTO LeadSinger VALUES (4, 'Anya Plushenko') " +
				"INTO LeadSinger VALUES (5, 'Selena Ganner') " +
				"INTO LeadSinger VALUES (6, 'Harry Clove') " +
				"INTO LeadSinger VALUES (7, 'Nataliya Podolskaya') " +
				"INTO LeadSinger VALUES (8, 'Jean-Pierre François') " +
				"INTO LeadSinger VALUES (9, 'Clementine Poppy') " +
				"INTO LeadSinger VALUES (10, 'Sally Wong') " +
				"INTO LeadSinger VALUES (11, 'Peggy Range') " +
				"INTO LeadSinger VALUES (12, 'Leila Tee') " +
				"INTO LeadSinger VALUES (13, 'Hikaru Shidou') " +
				"INTO LeadSinger VALUES (14, 'RNO') " +
				"INTO LeadSinger VALUES (15, 'RNO') " +
				"INTO LeadSinger VALUES (5, 'Chuck Ng') " +
				"INTO LeadSinger VALUES (5, 'Barry Tongos') " +
			"SELECT 1 FROM DUAL";

		String hasSongs = "INSERT ALL " +
				"INTO hasSong VALUES (1, 'Hello World') " +
				"INTO hasSong VALUES (1, 'Recursion') " +
				"INTO hasSong VALUES (1, 'Too Many Variables') " +
				"INTO hasSong VALUES (2, 'Coffee Hut') " +
				"INTO hasSong VALUES (2, 'The Last Day') " +
				"INTO hasSong VALUES (3, 'A Heart in the Earth') " +
				"INTO hasSong VALUES (3, 'The Last Day') " +
				"INTO hasSong VALUES (4, 'Krasochnaya Zhizn') " +
				"INTO hasSong VALUES (5, 'Life in Colour') " +
				"INTO hasSong VALUES (6, 'Cheesy Tokens') " +
				"INTO hasSong VALUES (7, 'Semya') " +
				"INTO hasSong VALUES (8, 'Aidez-moi, sil vous plaît!') " +
				"INTO hasSong VALUES (9, 'Peaceful Breeze') " +
				"INTO hasSong VALUES (10, 'Floppy Fins') " +
				"INTO hasSong VALUES (11, 'Broken Trail') " +
				"INTO hasSong VALUES (12, 'My Place') " +
				"INTO hasSong VALUES (13, 'Lost Fire') " +
				"INTO hasSong VALUES (13, 'A Letter for You') " +
				"INTO hasSong VALUES (14, 'No. 1 Scène: Allegro giusto') " +
				"INTO hasSong VALUES (14, 'No. 2 Waltz: Tempo di valse') " +
				"INTO hasSong VALUES (14, 'No. 3 Scène: Allegro moderato') " +
				"INTO hasSong VALUES (14, 'No. 4 Pas de trois') " +
				"INTO hasSong VALUES (15, 'March') " +
				"INTO hasSong VALUES (15, 'Waltz of the Snowflakes') " +
				"INTO hasSong VALUES (15, 'Chocolate') " +
				"INTO hasSong VALUES (15, 'Coffee') " +
				"INTO hasSong VALUES (15, 'Tea') " +
				"INTO hasSong VALUES (15, 'Trepak') " +		
			"SELECT 1 FROM DUAL";
		
		String customers = "INSERT ALL " +
							   "INTO Customer VALUES(-1, NULL, 'Default', NULL, 0) " +
							   "INTO Customer VALUES(224, 'abc', 'Bob Lee', '8373 Dolly Avenue', 2222) " +
							   "INTO Customer VALUES(134, 'oranges', 'Harry Golding', '3563 Halo Street', 8692) " +
							   "INTO Customer VALUES(2345, 'trees', 'Stacy Song', '8344 Mango Avenue', 4744) " +
							   "INTO Customer VALUES(2346, 'food', 'Luke Hall', '1344 Lemon Avenue', 6565) " +
							   "INTO Customer VALUES(23, 'Rabbits', 'Daisy Cho', '5533 Pocket Road', 8755) " +
							   "INTO Customer VALUES(5688, 'lucky77', 'Eri Yukimoto', '2453 Haste Street', 6645) " +
							   "INTO Customer VALUES(234, '5birdS', 'Madison Freely', '7575 Norm Avenue', 8855) " +
						   "SELECT 1 FROM DUAL";
		
		String purchases = "INSERT ALL " +
								"INTO Purchase VALUES(1, to_date('2012-12-01', 'YYYY-MM-DD'), 224, 5456, to_date('03/14', 'MM/YY'), to_date('2012-12-03', 'YYYY-MM-DD'), to_date('2012-12-02', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(2, to_date('2012-12-01', 'YYYY-MM-DD'), 234, 5456, to_date('03/14', 'MM/YY'), to_date('2012-12-03', 'YYYY-MM-DD'), to_date('2012-12-02', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(3, to_date('2012-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(4, to_date('2012-12-01', 'YYYY-MM-DD'), 2346, 7383, to_date('06/13', 'MM/YY'), to_date('2012-12-04', 'YYYY-MM-DD'), to_date('2012-12-03', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(5, to_date('2012-12-04', 'YYYY-MM-DD'), 2346, 7383, to_date('06/13', 'MM/YY'), to_date('2012-12-08', 'YYYY-MM-DD'), to_date('2012-12-04', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(6, to_date('2012-12-09', 'YYYY-MM-DD'), 2346, 7383, to_date('06/13', 'MM/YY'), to_date('2012-12-20', 'YYYY-MM-DD'), to_date('2012-12-19', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(7, to_date('2012-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(8, to_date('1996-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(9, to_date('2012-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(10, to_date('2012-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(11, to_date('1997-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(12, to_date('1997-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(13, to_date('2012-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(14, to_date('1997-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(15, to_date('1999-12-01', 'YYYY-MM-DD'), 5688, 9403, to_date('11/03', 'MM/YY'), to_date('1999-12-04', 'YYYY-MM-DD'), to_date('1999-12-04', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(16, to_date('2013-08-03', 'YYYY-MM-DD'), 134, 1304, to_date('06/14', 'MM/YY'), to_date('2013-08-10', 'YYYY-MM-DD'), NULL)" +
								"INTO Purchase VALUES(17, to_date('2012-12-01', 'YYYY-MM-DD'), -1, NULL, NULL, NULL, NULL)" +
								"INTO Purchase VALUES(18, to_date('1999-12-01', 'YYYY-MM-DD'), 5688, 9403, to_date('11/03', 'MM/YY'), to_date('1999-12-04', 'YYYY-MM-DD'), to_date('1999-12-04', 'YYYY-MM-DD'))" +
								"INTO Purchase VALUES(19, to_date('2013-08-05', 'YYYY-MM-DD'), 23, 3042, to_date('07/15', 'MM/YY'), to_date('2013-08-07', 'YYYY-MM-DD'), NULL)" +
								"INTO Purchase VALUES(20, to_date('2013-08-06', 'YYYY-MM-DD'), 23, 3042, to_date('07/15', 'MM/YY'), to_date('2013-08-08', 'YYYY-MM-DD'), NULL)" +
							"SELECT 1 FROM DUAL";
		
		String purchaseItems = "INSERT ALL " +
									"INTO PurchaseItem VALUES(1, 1, 10) " +
									"INTO PurchaseItem VALUES(1, 3, 35) " +
									"INTO PurchaseItem VALUES(1, 5, 1) " +
									"INTO PurchaseItem VALUES(2, 2, 11) " +
									"INTO PurchaseItem VALUES(3, 3, 19) " +
									"INTO PurchaseItem VALUES(4, 4, 15) " +
									"INTO PurchaseItem VALUES(5, 5, 1) " +
									"INTO PurchaseItem VALUES(6, 6, 4) " +
									"INTO PurchaseItem VALUES(7, 7, 17) " +
									"INTO PurchaseItem VALUES(8, 1, 10) " +
									"INTO PurchaseItem VALUES(8, 5, 2) " +
									"INTO PurchaseItem VALUES(9, 8, 64) " +
									"INTO PurchaseItem VALUES(10, 9, 34) " +
									"INTO PurchaseItem VALUES(11, 1, 10) " +
									"INTO PurchaseItem VALUES(12, 1, 10) " +
									"INTO PurchaseItem VALUES(13, 10, 83) " +
									"INTO PurchaseItem VALUES(13, 1, 12) " +
									"INTO PurchaseItem VALUES(14, 1, 10) " +
									"INTO PurchaseItem VALUES(15, 1, 10) " +
									"INTO PurchaseItem VALUES(16, 1, 10) " +
									"INTO PurchaseItem VALUES(17, 11, 22) " +
									"INTO PurchaseItem VALUES(18, 11, 45) " +
									"INTO PurchaseItem VALUES(19, 11, 45) " +
									"INTO PurchaseItem VALUES(20, 11, 20) " +	
							  "SELECT 1 FROM DUAL";
		
		String returns = "INSERT ALL " +
				"INTO Return VALUES(1, to_date('2012-12-07', 'YYYY-MM-DD'), 1) " + 
				"INTO Return VALUES(2, to_date('2012-12-06', 'YYYY-MM-DD'), 1) " +
				"INTO Return VALUES(3, to_date('2012-12-09', 'YYYY-MM-DD'), 1) " +
				"INTO Return VALUES(4, to_date('2012-12-06', 'YYYY-MM-DD'), 4) " +
				"INTO Return VALUES(5, to_date('2012-12-06', 'YYYY-MM-DD'), 7) " +
				"INTO Return VALUES(6, to_date('1999-12-04', 'YYYY-MM-DD'), 18) " +
				"INTO Return VALUES(7, to_date('1997-12-06', 'YYYY-MM-DD'), 14) " +
				"INTO Return VALUES(8, to_date('1997-12-05', 'YYYY-MM-DD'), 12) " +
		  "SELECT 1 FROM DUAL";
		
		String returnItems = "INSERT ALL " +
				"INTO ReturnItem VALUES(1, 1, 4) " + 
				"INTO ReturnItem VALUES(1, 3, 11) " +
				"INTO ReturnItem VALUES(1, 5, 1) " +
				"INTO ReturnItem VALUES(2, 1, 1) " +
				"INTO ReturnItem VALUES(3, 3, 5) " +
				"INTO ReturnItem VALUES(4, 4, 4) " +
				"INTO ReturnItem VALUES(5, 7, 1) " +
				"INTO ReturnItem VALUES(6, 11, 20) " +
				"INTO ReturnItem VALUES(7, 1, 4) " +
				"INTO ReturnItem VALUES(8, 1, 3) " +
		  "SELECT 1 FROM DUAL";
		
		stmt.executeQuery(items);
		stmt.executeQuery(leadSingers);
		stmt.executeQuery(hasSongs);
		stmt.executeQuery(customers);
		stmt.executeQuery(purchases);
		stmt.executeQuery(purchaseItems);
		stmt.executeQuery(returns);
		stmt.executeQuery(returnItems);
		
		stmt.executeQuery("DROP SEQUENCE purchase_receiptid_counter");
		stmt.executeQuery("DROP SEQUENCE return_retid_counter");
		stmt.executeQuery("CREATE SEQUENCE purchase_receiptid_counter START WITH 21");
		stmt.executeQuery("CREATE SEQUENCE return_retid_counter START WITH 9");
		
		con.commit();
		
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		}
	}
	
}
