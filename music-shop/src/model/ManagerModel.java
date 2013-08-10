package model;
import subject_observer.*;


import java.sql.*; 
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
					"(?, ?, ?, ?, ?, ?, ?)";
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
			con.commit();
			return true;
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			return false;
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
				return false;
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
				return false;
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
				return false;
			} 
		}
		
	}
	
	
	
	public boolean setDeliveryDate() {
		return false;
		
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