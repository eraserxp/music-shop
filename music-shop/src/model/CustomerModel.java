package model;
import subject_observer.*;

import java.sql.*; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

	
    // get an item given its upc
	public ResultSet getItem(int upc) {
		ResultSet rs = null;
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, category," +
						"LISTAGG(name, ', ') within group (order by name) as singers, " +
						" price, stock" +
						" from Item I, LeadSinger L" +
						" where I.upc = L.upc and I.upc=?" +
						" group by I.upc, title, category, price, stock" );
				ps.setInt(1, upc);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
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
	
	// search items with given category, title, and singerName
	// any of three input can be empty string, but there must be at least one is not empty
	// Note we don't allow any of the input to be null
	public ResultSet searchItem(String category, String title, String singerName) {
		ResultSet rs = null;
			
		if (category.length()!=0 && title.length()==0 && singerName.length()==0) {
			// only category is not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, " +
						" LISTAGG(name, ', ') within group (order by name) as singers," +
						" category, price" 
						+ " FROM Item I, LeadSinger L " +
						" where L.upc = I.upc and category = ? and stock > 0" +
						" group by I.upc, title, category, price" );
				ps.setString(1, category);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} else if (category.length()==0 && title.length()!=0 && singerName.length()==0) {
			// only title is not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, "
						+ " LISTAGG(name, ', ') within group (order by name) as singers," 
						+ " category, price" 
						+ " FROM Item I, LeadSinger L " 
						+ " where L.upc = I.upc and title = ? and stock > 0"  
						+ " group by I.upc, title, category, price");
				ps.setString(1, title);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} else if (category.length()==0 && title.length()==0 && singerName.length()!=0) {
			// only singerName is not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, "
						+ " LISTAGG(name, ', ') within group (order by name) as singers," 
						+ " category, price" 
						+ " FROM Item I, LeadSinger L " 
						+ " where L.upc = I.upc and name = ? and stock > 0" 
						+ " group by I.upc, title, category, price");
				ps.setString(1, singerName);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} else if (category.length()!=0 && title.length()!=0 && singerName.length()==0) {
			// category and title are not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, "
						+ " LISTAGG(name, ', ') within group (order by name) as singers," 
						+ " category, price" 
						+ " FROM Item I, LeadSinger L " 
						+ "where L.upc = I.upc and category = ? and title = ? and stock > 0"
						+ " group by I.upc, title, category, price");
				ps.setString(1, category);
				ps.setString(2, title);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} else if (category.length()!=0 && title.length()==0 && singerName.length()!=0) {
			// category and singerName are not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, "
						+ " LISTAGG(name, ', ') within group (order by name) as singers," 
						+ " category, price" 
						+ " FROM Item I, LeadSinger L " 
						+ " where L.upc = I.upc and category = ? and name = ? and stock > 0"
						+ " group by I.upc, title, category, price");
				ps.setString(1, category);
				ps.setString(2, singerName);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} else if (category.length()==0 && title.length()!=0 && singerName.length()!=0) {
			// title and singerName are not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, "
						+ " LISTAGG(name, ', ') within group (order by name) as singers," 
						+ " category, price" 
						+ " FROM Item I, LeadSinger L " 
						+ " where L.upc = I.upc and title = ? and name = ? and stock > 0"
						+ " group by I.upc, title, category, price");
				ps.setString(1, title);
				ps.setString(2, singerName);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} else {
			// all are not empty
			try
			{	 
				ps = con.prepareStatement("SELECT I.upc, title, "
						+ " LISTAGG(name, ', ') within group (order by name) as singers," 
						+ " category, price" 
						+ " FROM Item I, LeadSinger L " 
						+ " where L.upc = I.upc and category=? and title = ? and name = ? and stock > 0" 
						+ " group by I.upc, title, category, price");
				ps.setString(1, category);
				ps.setString(2, title);
				ps.setString(3, singerName);
				rs = ps.executeQuery();
				return rs; 
			} catch (SQLException ex){
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);
				// no need to commit or rollback since it is only a query
				return null; 
			} // end of try catch block
			
		} // end of if-else-if-else block
		
		
	}
	
	public boolean queryUsernamePassword(String username, String password) {
		int count = 0; 
		ResultSet rs = null;
		String sqlStatement = "select count(*) from customer where cid = ? and cpassword = ?";
		                      
		try {
			ps = con.prepareStatement(sqlStatement);
			ps.setString(1,username);
			ps.setString(2, password);
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

	// obtain the expected delivery date for a online purchase processed at the current time
	// we assume the maximum number of orders that can be delivered in a day is 20
	public String getExpectedDeliveryDate() {
		String sqlStatement = "select count(P.receiptId) from purchase P" +
				" where P.cid is not null and deliveredDate is null";
		ResultSet rs = null;
		int undeliveredOrder = 0;
		int maxOrdersPerDay = 10;
		
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(sqlStatement);
			while (rs.next()) {
				undeliveredOrder = rs.getInt(1);	
			}
			con.commit();
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
		} finally {
	        try { rs.close(); } catch (Exception ignore) { }
	    }
		GregorianCalendar gregCalendar = new GregorianCalendar();
		
		int daysToAdd = undeliveredOrder/maxOrdersPerDay;
		// add days to current date
		gregCalendar.add(Calendar.DATE, daysToAdd);
		java.sql.Date sqlDate = new java.sql.Date(gregCalendar.getTime().getTime());
		return sqlDate.toString();
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
	
	
	// allow the customer to process one purchase
	// and add the corresponding purchaseItem
	// dateString must be in the form "yyyy-MM-dd"
	// expectedDate and deliveredDate also in the form "yyyy-MM-dd"
	// precondition: cid, cardNumber, expiryDate, expectedDateString can't be null
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
			ps.setString(2, cid);
			ps.setString(3, cardNumber);
			ps.setString(4, expiryDate);

			java.sql.Date expectedDate = convertStringToDate(expectedDateString, "yyyy-MM-dd");
			ps.setDate(5, expectedDate);

			
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