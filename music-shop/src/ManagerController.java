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

	private ShopGUI shopGUI = null;
	private ManagerModel managerModel = null;
	
	public ManagerController(ShopGUI sg) {
		shopGUI = sg;
		managerModel = new ManagerModel();
		// add this to the listener list of the manager model
		managerModel.addExceptionListener(this);
	}
	
	
	/**
	 * generate a dialog to allow the manager to add items to store
	 *
	 */
	class AddItemToStoreDialog extends JDialog implements ActionListener {

		public AddItemToStoreDialog(ShopGUI shopGUI) {
			// TODO
		}
		
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

		public ProcessDeliveryDialog(ShopGUI shopGUI) {
			// TODO
		}
		
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

		public GenerateDailyReportDialog(ShopGUI shopGUI) {
			// TODO
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the manager to find out the top selling items
	 *
	 */
	class ShowTopSellingItemsDialog extends JDialog implements ActionListener {

		public ShowTopSellingItemsDialog(ShopGUI shopGUI) {
			// TODO
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
		if (actionCommand.equals(ShopGUI.ADD_ITEM_TO_STORE)) {
			//TODO
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_DELIVERY)) {
			
			return; 
		} else if (actionCommand.equals(ShopGUI.GENERATE_DAILY_REPORT)) {
			// TODO
			return;
		} else if (actionCommand.equals(ShopGUI.SHOW_TOP_SELLING_ITEMS)) {
			// TODO
			return;
		}
	}

	
}