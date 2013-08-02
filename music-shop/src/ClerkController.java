import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 
import java.sql.*;


/*
 * ClerkController is a control class that handles action events on the Clerk menu.
 * It updates the GUI based on which menu item the clerk selected and also use methods
 * in ClerkModel to update the tables in database
 */

public class ClerkController implements ActionListener, ExceptionListener {

	/**
	 * generate a dialog to process a purchase. It calls processPurchase method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessPurchaseDialog extends JDialog implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * generate a dialog to process a return. It calls processReturn method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessReturnDialog extends JDialog implements ActionListener {

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