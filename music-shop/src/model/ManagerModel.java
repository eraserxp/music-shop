package model;
import subject_observer.*;


import java.sql.*; 
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import database.MyOracleConnection;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;





/* provides methods to carry out the actual transactions performed by a manager:
 * 
 * 1. add items to store
 * 2. set the delivery date for an order
 * 3. generate daily sale report
 * 4. show the top selling items
 * 
 * This class provides interface for the database operations (like insert, update 
 * tables..) that are related to the above transactions. It doesn't know anything
 * about the user interface. A ManagerController class will act as a glue between the
 * GUI and ManagerModel
 */

public class ManagerModel {
	
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null; 


	/*
	 * Default constructor
	 * Precondition: The Connection object in MvbOracleConnection must be
	 * a valid database connection.
	 */ 
	public ManagerModel()
	{
		con = MyOracleConnection.getInstance().getConnection();
	}

	public boolean addNewItem(int upc, String title, String type, String category, String company,
			                  String year, double price, int quantity, ArrayList<String> singerList,
			                  ArrayList<String> songList) {
		try {
			String sqlStatement = "insert into item values" +
					"(?, ?, ?, ?, ?, ?, ?, ?)";
			ps = con.prepareStatement(sqlStatement);
			ps.setInt(1,upc);
			ps.setString(2,title);
			ps.setString(3,type);
			ps.setString(4,category);
			ps.setString(5,company);
			ps.setString(6,year);
			ps.setDouble(7, price);
			ps.setInt(8, quantity);
			ps.executeUpdate();
			// insert into LeadSinger table
			sqlStatement = "insert into LeadSinger values (?, ?)";
			ps = con.prepareStatement(sqlStatement);
			for (String singer: singerList) {
				ps.setInt(1, upc);
				ps.setString(2, singer);
				ps.executeUpdate();
			}
			// insert into HasSong table
			sqlStatement = "insert into HasSong values (?, ?)";
			ps = con.prepareStatement(sqlStatement);
			for (String song: songList) {
				ps.setInt(1, upc);
				ps.setString(2, song);
				ps.executeUpdate();
			}
			con.commit();
			return true;
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			try{
				con.rollback();
				return false; 
			} catch (SQLException ex2) {
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return false; 
			}
		} 
		
	}
	
	// update the stock and (or) price of the existing item
	public boolean updateExistingItem(int upc, Double price, Integer quantity) {
		if (price==null) {		
			String sqlStatement = "update  Item set stock=stock+? where upc = ?";
			try {
				ps = con.prepareStatement(sqlStatement);
				ps.setInt(1,quantity);
				ps.setInt(2,upc);
				ps.executeUpdate();
				con.commit();
				return true;
			} catch (SQLException ex) {
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				try{
					con.rollback();
					return false; 
				} catch (SQLException ex2) {
					event = new ExceptionEvent(this, ex2.getMessage());
					fireExceptionGenerated(event);
					return false; 
				}
			} 

		} else if (quantity==null) {
			String sqlStatement = "update  Item set price=? where upc = ?";
			try {
				ps = con.prepareStatement(sqlStatement);
				ps.setDouble(1,price);
				ps.setInt(2,upc);
				ps.executeUpdate();
				con.commit();
				return true;
			} catch (SQLException ex) {
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				try{
					con.rollback();
					return false; 
				} catch (SQLException ex2) {
					event = new ExceptionEvent(this, ex2.getMessage());
					fireExceptionGenerated(event);
					return false; 
				}
			} 

		} else {
			String sqlStatement = "update  Item set price=?, stock=stock+? where upc = ?";
			try {
				ps = con.prepareStatement(sqlStatement);
				ps.setDouble(1, price);
				ps.setInt(2,quantity);
				ps.setInt(3, upc);
				ps.executeUpdate();
				con.commit();
				return true;
			} catch (SQLException ex) {
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				try{
					con.rollback();
					return false; 
				} catch (SQLException ex2) {
					event = new ExceptionEvent(this, ex2.getMessage());
					fireExceptionGenerated(event);
					return false; 
				}
				
			} 
		}
		
	}
	
	// check if the receiptId existed in database
	public boolean isReceiptIdExisted(int receiptId) {
		int count = 0;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(
					"select count(*) from purchase P " +
					" where P.receiptId = ?"
					); 

			ps.setInt(1, receiptId);
			rs = ps.executeQuery();
			if (rs.next()) {
				count =  rs.getInt(1);
			}
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		
		if (count==1) {
			return true;
		} else {
			return false;
		}
	}
	

	// check if the receiptId corresponding to an online order
	public boolean isOnlineOrder(int receiptId) {
		int count = 0;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(
					"select count(*) from purchase P " +
					" where P.receiptId = ? and cid is not null"
					); 

			ps.setInt(1, receiptId);
			rs = ps.executeQuery();
			if (rs.next()) {
				count =  rs.getInt(1);
			}
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		
		if (count==1) {
			return true;
		} else {
			return false;
		}
	}
	
	
	// get the purchase items for receiptId
	public ResultSet getPurchaseItem(int receiptId) {
		try
		{	 
			ps = con.prepareStatement("SELECT * from purchaseItem where receiptId = ?");
			ps.setInt(1, receiptId);
			ResultSet rs = ps.executeQuery();
			return rs; 
		} catch (SQLException ex){
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
			return null; 
		} 
	}
	
	// get the purchase for receiptId
	public ResultSet getPurchase(int receiptId) {
		try
		{	 
			ps = con.prepareStatement("SELECT * from purchase where receiptId = ?" );
			ps.setInt(1, receiptId);
			ResultSet rs = ps.executeQuery();
			return rs; 
		} catch (SQLException ex){
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
			return null; 
		} 
	}
	
	// set the delivery date for a online order
	// note the date must be in the format "yyyy-MM-dd"
	public boolean setDeliveryDate(int receiptId, String date) {
		String sqlStatement = "update purchase set deliveredDate=? where receiptId = ?";
		try {
			ps = con.prepareStatement(sqlStatement);
			java.sql.Date deliveredDate = convertStringToDate(date, "yyyy-MM-dd");
			ps.setDate(1,deliveredDate);
			ps.setInt(2,receiptId);
			ps.executeUpdate();
			con.commit();
			return true;
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			try{
				con.rollback();
				return false; 
			} catch (SQLException ex2) {
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return false; 
			}
		} 
		
	}
	
	// convert a date string in format to a sql date
	public java.sql.Date convertStringToDate(String dateString, String format) {
		SimpleDateFormat fm = new SimpleDateFormat(format);
		java.util.Date utilDate;
		try {
			utilDate = fm.parse(dateString);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			return sqlDate;
		} catch (ParseException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		}
		return null;
	}
	
	// get the categories of the purchase items for a date
	public ArrayList<String> getPurchaseCategories(String date) {
		int count = 0;
		ArrayList<String> categoryList = new ArrayList<String>();
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(
					"select distinct category from purchase P, Item I, PurchaseItem PI " +
					" where P.pdate = ? and P.receiptId=PI.receiptId and I.upc = PI.upc "
					); 
			java.sql.Date purchaseDate = convertStringToDate(date, "yyyy-MM-dd");
			ps.setDate(1, purchaseDate);
			rs = ps.executeQuery();
			while (rs.next()) {
				String category =  rs.getString(1);
				categoryList.add(category);
			}
			
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		return categoryList;
	}
	
	// get the daily report
	public ArrayList< ArrayList<String> > getDailyReport(String date) {
		ArrayList< ArrayList<String> > rowList = new ArrayList< ArrayList<String> >();
		int totalQuantity = 0;
		double totalAmount = 0.0;
		ArrayList<String> categoryList = getPurchaseCategories(date);
		ResultSet rs = null;
		for (String category: categoryList) {
			try {
				ps = con.prepareStatement(
						"select I.upc, price, sum(quantity) as units, " +
						" price*sum(quantity) as total_value " + 
                        " from Item I, PurchaseItem PI " + 
                        " where I.upc = PI.upc and I.category = ? " +
                        " and PI.receiptId in " +
                        "( select receiptId from Purchase P where P.pdate=? ) " +
                        " group by I.upc, I.category, I.price" 
						); 

				ps.setString(1, category);				
				java.sql.Date purchaseDate = convertStringToDate(date, "yyyy-MM-dd");
				ps.setDate(2, purchaseDate);
				rs = ps.executeQuery();
				ArrayList<Integer> unitsList = new ArrayList<Integer>();
				ArrayList<Double> subtotalList = new ArrayList<Double>();
				while (rs.next()) {
					ArrayList<String> oneRow = new ArrayList<String>();
					int upc =  rs.getInt(1);
					String upcString = Integer.toString(upc);
					oneRow.add(upcString);
					oneRow.add(category);
					double price = rs.getDouble(2);
					oneRow.add(formatDouble(price, 2));
					int units = rs.getInt(3);
					unitsList.add(units);
					//total += total + units;
					oneRow.add(Integer.toString(units));
					double subtotal = rs.getDouble(4);
					subtotalList.add(subtotal);
					oneRow.add(formatDouble(subtotal, 2));
					rowList.add(oneRow);

				}
				// add summary for one category
				ArrayList<String> oneRow = new ArrayList<String>();
				// add a blank line 
				oneRow.add("   ");
				oneRow.add("   ");
				oneRow.add("   ");
				oneRow.add("   ");
				oneRow.add("   ");
				rowList.add(oneRow);
				
				oneRow = new ArrayList<String>();
				oneRow.add("    ");
				oneRow.add("Total");
				oneRow.add("     ");
				int unitsForOneCategory = sumIntegers(unitsList);
				totalQuantity += totalQuantity + unitsForOneCategory;
				oneRow.add(Integer.toString(unitsForOneCategory));
				double subtotalForOneCategory = sumDoubles(subtotalList);
				totalAmount += totalAmount + subtotalForOneCategory; 
				oneRow.add(formatDouble(subtotalForOneCategory, 2));
				rowList.add(oneRow);
				
				// add another blank line 
				oneRow = new ArrayList<String>();					
				oneRow.add("   ");
				oneRow.add("   ");
				oneRow.add("   ");
				oneRow.add("   ");
				oneRow.add("   ");
				rowList.add(oneRow);
				
				
			} catch (SQLException ex) {
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
			} finally {
		        try { rs.close(); } catch (Exception ignore) { }
		    }
		}// end of for loop
		// add the final summary
		ArrayList<String> oneRow = new ArrayList<String>();
		oneRow.add("   ");
		oneRow.add("   ");
		oneRow.add("   ");
		oneRow.add("   ");
		oneRow.add("-----------");
		rowList.add(oneRow);
		oneRow = new ArrayList<String>();
		oneRow.add("   ");
		oneRow.add("Total Daily sales");
		oneRow.add("   ");
		oneRow.add(Integer.toString(totalQuantity));
		oneRow.add(formatDouble(totalAmount, 2));
		rowList.add(oneRow);
		
		return rowList;
		
	}

	
	
	// calculate the sum of a list of integers 
	private int sumIntegers(ArrayList<Integer> integerList) {
		int sum = 0;
		for (Integer i: integerList) {
			sum += i;
		}
		return sum;
	}
	
	// calculate the sum of a list of doubles and return the result as a string (with 2 decimal digits)
	private double sumDoubles(ArrayList<Double> doubleList) {
		double sum = 0.0;		
		for (Double i: doubleList) {
			sum += i;
		}
		return sum;
	}
	
	// return the string corresponding to a double in a format with ndigits after decimal points
	private String formatDouble(double number, int ndigit) {
		NumberFormat numberFormatter = NumberFormat.getNumberInstance();
		numberFormatter.setMinimumFractionDigits(ndigit);
		numberFormatter.setMaximumFractionDigits(ndigit);
		return numberFormatter.format(number);
	}
	
	
	
	public ArrayList< ArrayList<String> > getTopSellingItems(String date, int topN) {
		ArrayList< ArrayList<String> > rowList = new ArrayList< ArrayList<String> >();
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(
					"select I.title, I.company, I.stock, sum(PI.quantity) as sale_quantity"
							+ " from Item I, PurchaseItem PI "
							+ " where I.upc = PI.upc and PI.receiptId in "
							+ " (select receiptId from Purchase P where P.pdate = ?)"
							+ " group by I.upc, I.title, I.company, I.stock"
							+ " order by sum(PI.quantity) desc " 
					); 				
			java.sql.Date purchaseDate = convertStringToDate(date, "yyyy-MM-dd");
			ps.setDate(1, purchaseDate);
			rs = ps.executeQuery();
			int counter = 1;
			while (rs.next() && counter <= topN) {
				ArrayList<String> oneRow = new ArrayList<String>();
				oneRow.add(Integer.toString(counter));
				counter += 1;

				String title =  rs.getString(1);
				oneRow.add(title);

				String company =  rs.getString(2);
				oneRow.add(company);

				int stock = rs.getInt(3);
				oneRow.add(Integer.toString(stock));

				int soldCopies = rs.getInt("sale_quantity");
				oneRow.add(Integer.toString(soldCopies) );

				rowList.add(oneRow);

			}

		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
		} finally {
			try { rs.close(); } catch (Exception ignore) { }
		}
		
		return rowList;
		
		
	}
	
	// find out whether a upc exist in the item list (even if its stock = 0)
	public boolean isUPCExisted(int itemUPC) {
		//boolean isValid = false;
		String title = null;
		ResultSet rs = null;
		// every item must have a non-null title 
		String sqlStatement = "select title from Item where upc = ?";
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setInt(1,itemUPC);
			rs = ps.executeQuery();
			while (rs.next()) {
				title = rs.getString(1);	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }

		if (title == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/******************************************************************************
	 * Below are the methods to add and remove ExceptionListeners.
	 * 
	 * Whenever an exception occurs in BranchModel, an exception event
	 * is sent to all registered ExceptionListeners.
	 ******************************************************************************/ 

	public void addExceptionListener(ExceptionListener l) 
	{
		listenerList.add(ExceptionListener.class, l);
	}


	public void removeExceptionListener(ExceptionListener l) 
	{
		listenerList.remove(ExceptionListener.class, l);
	}


	/*
	 * This method notifies all registered ExceptionListeners.
	 * The code below is similar to the example in the Java 2 API
	 * documentation for the EventListenerList class.
	 */ 
	public void fireExceptionGenerated(ExceptionEvent ex) 
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event.
		// I have no idea why the for loop counts backwards by 2
		// and the array indices are the way they are.
		for (int i = listeners.length-2; i>=0; i-=2) 
		{
			if (listeners[i]==ExceptionListener.class) 
			{
				((ExceptionListener)listeners[i+1]).exceptionGenerated(ex);
			}
		}
	}
	
}