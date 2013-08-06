package model;
import subject_observer.*;

import java.sql.*; 

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

	public boolean addItemToStore() {
		return false;
		
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