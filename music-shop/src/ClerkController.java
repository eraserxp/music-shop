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

	private ShopGUI shopGUI = null;
	private ClerkModel clerkModel = null;
	
	public ClerkController(ShopGUI sg) {
		shopGUI = sg;
		clerkModel = new ClerkModel();
		// add this to the listener list of the clerk model
		clerkModel.addExceptionListener(this);
	}
	
	/**
	 * generate a dialog to process a purchase. It calls processPurchase method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessPurchaseDialog extends JDialog implements ActionListener {

		public ProcessPurchaseDialog(ShopGUI shopGUI) {
			//TODO
		}
		
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

		public ProcessReturnDialog(ShopGUI shopGUI) {
			//TODO
		}
		
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
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		// you cannot use == for string comparisons
		if (actionCommand.equals(ShopGUI.PROCESS_PURCHASE)) {
			//TODO
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_RETURN)) {
			//TODO
			return; 
		}
	}
	
}