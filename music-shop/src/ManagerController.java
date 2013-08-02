import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 
import java.sql.*;


/*
 * ManagerController is a control class that handles action events on the Manager menu.
 * It updates the GUI based on which menu item the Manager selected and also use methods
 * in ManagerModel to update the tables in database
 */

public class ManagerController implements ActionListener, ExceptionListener {

	/**
	 * generate a dialog to allow the manager to add items to store
	 *
	 */
	class addItemToStoreDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * generate a dialog to allow the manager to process the delivery of an order
	 * (it just sets the delivery date)
	 *
	 */
	class ProcessDeliveryDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the customer to generate a daily sales report
	 *
	 */
	class GenerateDailyReportDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the manager to find out the top selling items
	 *
	 */
	class showTopSellingItemsDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the customer to checkout and pay by credit card
	 *
	 */
	class CheckOutDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	@Override
	public void exceptionGenerated(ExceptionEvent ex) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}