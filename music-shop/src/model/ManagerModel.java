package model;
import subject_observer.*;


import java.sql.*; 
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
	
	
	public boolean generateDailyReport() {
		return false;
		
	}
	
	public boolean showTopSellingItems() {
		return false;
		
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