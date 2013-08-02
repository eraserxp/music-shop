import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 
import java.sql.*;


/*
 * CustomerController is a control class that handles action events on the Clerk menu.
 * It updates the GUI based on which menu item the clerk selected and also use methods
 * in CustomerModel to update the tables in database
 */

public class CustomerController implements ActionListener, ExceptionListener {

	/**
	 * generate a dialog to allow the customer to search items
	 *
	 */
	class SearchItemDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * generate a dialog to allow the customer to add item to the shopping cart
	 *
	 */
	class AddItemDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the customer to remove item to the shopping cart
	 *
	 */
	class RemoveItemDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the customer to register
	 *
	 */
	class RegistrationDialog extends JDialog implements ActionListener {

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