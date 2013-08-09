package model;
import subject_observer.*;

import java.sql.*; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.event.EventListenerList;

import database.MyOracleConnection;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;



/* provides methods to carry out the actual transactions performed by a clerk:
 * 
 * 1. process a purchase of items in the store
 * 2. process a return of an item for refund
 * 
 * This class provides interface for the database operations (like insert, update 
 * tables..) that are related to the above transactions. It doesn't know anything
 * about the user interface. A ClerkController class will act as a glue between the
 * GUI and ClerkModel
 */

public class ClerkModel {
	
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null; 


	/*
	 * Default constructor
	 * Precondition: The Connection object in MvbOracleConnection must be
	 * a valid database connection.
	 */ 
	public ClerkModel() {
		con = MyOracleConnection.getInstance().getConnection();
	}

	// allow the clerk to process one purchase
	// and add the corresponding purchaseItem
	// dateString must be in the form "yyyy-MM-dd"
	// expectedDate and deliveredDate also in the form "yyyy-MM-dd"
	public boolean processPurchase(int receiptId, String dateString, String cid,
                                   String cardNumber, String expiryDate, 
                               String expectedDateString, String deliveredDateString, 
                                 ArrayList<Integer> upcList,
			                     ArrayList<Integer> quantityList) {
		try {
			// insert a tuple into the purchase table
			ps = con.prepareStatement("insert into purchase values" 
				                   + "(receiptID_counter.nextval, ?, ?, ?, ?, ?, ?)");
			//ps.setInt(1, receiptId);
			java.sql.Date date = convertStringToDate(dateString, "yyyy-MM-dd");
			ps.setDate(1, date);
			if (cid == null) {
				ps.setNull(2, java.sql.Types.VARCHAR);
			} else {
				ps.setString(2, cid);
			}
			
			if (cardNumber == null) {
				ps.setNull(3, java.sql.Types.VARCHAR);
			} else {
				ps.setString(3, cardNumber);
			}
			
			if (expiryDate == null) {
				ps.setNull(4, java.sql.Types.VARCHAR);
			} else {
				ps.setString(4, expiryDate);
			}
			
			if (expectedDateString==null) {
				ps.setNull(5, java.sql.Types.DATE);
			} else {
				java.sql.Date expectedDate = convertStringToDate(expectedDateString, "yyyy-MM-dd");
				ps.setDate(5, expectedDate);
			}
			
			if (deliveredDateString==null) {
				ps.setNull(6, java.sql.Types.DATE);
			} else {
				java.sql.Date deliveredDate = convertStringToDate(deliveredDateString, "yyyy-MM-dd");
				ps.setDate(6, deliveredDate);
			}
			ps.executeUpdate();
			// insert the corresponding purchaseItem tuples
			ps = con.prepareStatement("insert into PurchaseItem values (?, ?, ?)");
			for (int i=0; i<upcList.size(); ++i) {
				int upc = upcList.get(i);
				int quantity = quantityList.get(i);
				ps.setInt(1, receiptId);
				ps.setInt(2, upc);
				ps.setInt(3, quantity);
				ps.executeUpdate();
			}
			// update the stock for items
			// you should never delete an item even if its stock = 0 because some
			// previous purchase may refer to it
			ps = con.prepareStatement("update item set stock = stock - ? where upc = ?");
			for (int i=0; i<upcList.size(); ++i) {				
				int upc = upcList.get(i);
				int quantity = quantityList.get(i);
				ps.setInt(1, quantity);
				ps.setInt(2, upc);
				ps.executeUpdate();	
			}

			// commit as one transaction
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
	
	public boolean processReturn(int receiptId, String dateString, 
			                      ArrayList<Integer> upcList, 
			                      ArrayList<Integer> returnQuantityList) {
		try {
			// insert the return
			ps = con.prepareStatement("insert into return values " +
					                 "(retid_counter.nextval, ?, ?)");
			java.sql.Date date = convertStringToDate(dateString, "yyyy-MM-dd");
			ps.setDate(1, date);
			ps.setInt(2, receiptId);

			
			ps.executeUpdate();
			
			// insert the returnItems
			ps = con.prepareStatement("insert into returnItem values " +
					         "(retid_counter.currval, ?, ?)");
			for (int i=0; i<upcList.size(); ++i) {
				ps.setInt(1, upcList.get(i));
				ps.setInt(2, returnQuantityList.get(i));
				ps.executeUpdate();
			}
			
			// update the quantity field of the corresponding purchaseItems
			ps = con.prepareStatement("update purchaseItem set quantity=quantity-?" +
					" where receiptId=? and upc = ?");
			for (int i=0; i<upcList.size(); ++i) {
				ps.setInt(1, returnQuantityList.get(i));
				ps.setInt(2, receiptId);
				ps.setInt(3, upcList.get(i));
				ps.executeUpdate();
			}
			// delete those purchaseItems whose quantity = 0
			Statement stmt = con.createStatement();
			stmt.executeUpdate("delete from purchaseItem where quantity = 0 ");
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
	
	
	// obtain the title given the item UPC
	public String queryTitle(int itemUPC) {
		String title = null; 
		ResultSet rs = null;
		String sqlStatement = "select title from Item where upc = ?";
		                      
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setInt(1,itemUPC);
			rs = ps.executeQuery();
			while (rs.next()) {
				title = rs.getString("title");	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		return title;
	}
	
	// obtain the unit price given the UPC of the item
	public double queryItemPrice(int itemUPC) {
		double unitPrice = 0.0; 
		ResultSet rs = null;
		String sqlStatement = "select price from Item where upc = ?";
		                      
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setInt(1,itemUPC);
			rs = ps.executeQuery();
			while (rs.next()) {
				unitPrice = rs.getDouble("price");	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		return unitPrice;
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
	
	// check whether the item UPC is valid or not
	// an upc is valid if it exists in database and its corresponding stock > 0
	public boolean isUPCValid(int itemUPC) {
		//boolean isValid = false;
		int stock = 0;
		ResultSet rs = null;
		// every item must have a non-null title 
		String sqlStatement = "select stock from Item where upc = ?";
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setInt(1,itemUPC);
			rs = ps.executeQuery();
			while (rs.next()) {
				stock = rs.getInt(1);	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }

		if (stock == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	// obtain the stok quantity of an item
	public int queryItemQuantity(int itemUPC) {
		int stockQuantity = 0; 
		ResultSet rs = null;
		String sqlStatement = "select stock from Item where upc = ?";
		                      
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setInt(1,itemUPC);
			rs = ps.executeQuery();
			while (rs.next()) {
				stockQuantity = rs.getInt("stock");	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		
		return stockQuantity;
	}

	// obtain the next receiptID and decrement it by 1 in the database
	// so that this function will not change the nextval of the sequence
	public int getNextReceiptID() {
		int nextReceiptID = 0; 
		ResultSet rs = null;
		String getNext = "select receiptID_counter.nextval from dual";
		String decrementByOne = "alter sequence receiptID_counter increment by -1";
		String resetToOriginal = "select receiptID_counter.nextval from dual";
		String incrementByOne = "alter sequence receiptID_counter increment by 1";
		
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(getNext);
			while (rs.next()) {
				nextReceiptID = rs.getInt(1);	
			}
			con.commit();
			stmt.executeQuery(decrementByOne);
			con.commit();
			stmt.executeQuery(resetToOriginal);
			con.commit();
			stmt.executeQuery(incrementByOne);
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		return nextReceiptID;
	}
	

	// obtain the next returnID and decrement it by 1 in the database
	// so that this function will not change the nextval of the sequence
	public int getNextReturnID() {
		int nextReturnID = 0; 
		ResultSet rs = null;
		String getNext = "select retid_counter.nextval from dual";
		String decrementByOne = "alter sequence retid_counter increment by -1";
		String resetToOriginal = "select retid_counter.nextval from dual";
		String incrementByOne = "alter sequence retid_counter increment by 1";
		
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(getNext);
			while (rs.next()) {
				nextReturnID = rs.getInt(1);	
			}
			con.commit();
			stmt.executeQuery(decrementByOne);
			con.commit();
			stmt.executeQuery(resetToOriginal);
			con.commit();
			stmt.executeQuery(incrementByOne);
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		return nextReturnID;
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


	/* valid purchase ---- there is some purchaseItems associated with it.
	*                      If there is no purchaseItems associated with it,
	*                      this means that all the items in this purchase
	*                      have been returned
	* find the valid purchase 
	* The query should return 1 if the receiptId is OK
	*/ 
	public boolean isReceiptIdValid(int receiptId) {
		int count = 0;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(
					"select count(*) from purchase P" +
					" where 0 < ( select count(*) from purchaseItem PI " +
					" where PI.receiptId = P.receiptId) " +
					" and P.receiptId = ?"
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
	
	// check if a receipt id is within 15 days of its purchase date
	// receiptId should be an existing one
	public boolean isReceiptIdOutdated(int receiptId) {
		int count = 0;
		ResultSet rs = null;
		try {
			/* valid purchase ---- there is some purchaseItems associated with it.
			*                      If there is no purchaseItems associated with it,
			*                      this means that all the items in this purchase
			*                      have been returned
			* find the valid purchase whose date is within 15 days of today
			* The query should return 1 if the receiptId is OK
			*/ 
			ps = con.prepareStatement(
					"select count(*) from purchase P" +
					" where 15>=(select sysdate from dual) - P.pdate " +
					" and 0 < ( select count(*) from purchaseItem PI " +
					" where PI.receiptId = P.receiptId) " +
					" and P.receiptId = ?"
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
			return false;
		} else {
			return true;
		}

	}

	
	
	// get the purchase items for receiptId
	public ResultSet getPurchaseItem(int receiptId) {
		try
		{	 
			ps = con.prepareStatement("SELECT I.upc, title, quantity, price" 
		                              + " FROM Item I, purchaseItem P" 
					 +" where I.upc = P.upc" +
					 " and receiptId = ?" );
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
	
	// receiptId must be existed in database
	public String getCardNo(int receiptId) {
		String cardNo = null;
		ResultSet rs = null;
		try
		{	 
			ps = con.prepareStatement("SELECT cardN from purchase" 
		                               + " where receiptId = ?" );
			ps.setInt(1, receiptId);
			rs = ps.executeQuery();
			while (rs.next()) {
				cardNo = rs.getString(1);
			}
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query
			return null; 
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		
		return cardNo;
		
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