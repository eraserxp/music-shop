package model;
import subject_observer.*;

import java.sql.*; 

import javax.swing.event.EventListenerList;

import database.MyOracleConnection;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;



/* provides methods to carry out the actual transactions performed by a customer:
 * 
 * 1. registration
 * 2. purchase of items online
 * 3. search items
 * 
 * This class provides interface for the database operations (like insert, update 
 * tables..) that are related to the above transactions. It doesn't know anything
 * about the user interface. A CustomerController class will act as a glue between the
 * GUI and CustomerModel
 */

public class CustomerModel {
	
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null; 


	/*
	 * Default constructor
	 * Precondition: The Connection object in MvbOracleConnection must be
	 * a valid database connection.
	 */ 
	public CustomerModel()
	{
		con = MyOracleConnection.getInstance().getConnection();
	}

	public boolean addCustomer() {
		return false;
		
	}
	
	public boolean searchItem() {
		return false;
		
	}
	
	public boolean addPurchase() {
		return false;
		
	}
	
	public String queryTitle(int itemUPC) {
		return "title";
	}
	
	// obtain the unit price given the UPC of the item
	public float queryItemPrice(int itemUPC) {
		return 0;
	}
	
	// query a nonempty username, if it is already existed
	// return true, otherwise return false
	public boolean queryUsername(String username) {
		int count = 0; 
		ResultSet rs = null;
		String sqlStatement = "select count(*) from customer where cid = ?";
		                      
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setString(1,username);
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		
		if (count==0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean insertCustomer(String username, String password, String name, 
			                      String address, String phone) {
		
		String sqlStatement = "insert into customer values (?,?,?,?,?)";
        
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setString(1,username);
			ps.setString(2,password);
			ps.setString(3,name);
			ps.setString(4,address);
			ps.setString(5,phone);
			ps.executeUpdate();
			con.commit();
			return true;
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			return false;
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