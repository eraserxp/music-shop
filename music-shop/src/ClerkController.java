import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 


import java.sql.*;
import java.util.ArrayList;


/*
 * ClerkController is a control class that handles action events on the Clerk menu.
 * It updates the GUI based on which menu item the clerk selected and also use methods
 * in ClerkModel to update the tables in database
 */

public class ClerkController implements ActionListener, ExceptionListener {

	private ShopGUI shopGUI = null;
	private ClerkModel clerkModel = null;
	private DialogHelper dialogHelper = null;
	
	public ClerkController(ShopGUI sg) {
		shopGUI = sg;
		clerkModel = new ClerkModel();
		// add this to the listener list of the clerk model
		clerkModel.addExceptionListener(this);
		
		dialogHelper = new DialogHelper();
	}
	
	
	
	/**
	 * generate a dialog to process a purchase. It calls processPurchase method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessPurchaseDialog extends JDialog implements ActionListener {
		// to save the item upc list 
		private ArrayList<JTextField> itemUPCList = new ArrayList<JTextField>();
		private JTextField itemUPC = new JTextField(4);
		private ArrayList<JTextField> quantityList = new ArrayList<JTextField>();
		private JTextField quantity = new JTextField(4);
		
		
		public ProcessPurchaseDialog(ShopGUI shopGUI) {
			//TODO
			super(shopGUI, "Process purchase", true);
			//setResizable(false);
			
			// create the panel to output the receipt
			final JPanel receiptPanel = new JPanel(new BorderLayout());
			receiptPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
			
			class UpdateReceipt implements FocusListener, ActionListener{

				public UpdateReceipt() {
					//empty ;
				}
				
				private void regenerateReceipt() {
					receiptPanel.removeAll();
					int itemUPC;
					String title;
					int quantity = 1;
					
					int totalQuantity = 0;
					double unitPrice = 1.0;
					double subtotal = 0.0; 
					double totalAmount = 0.0;

					dialogHelper.addComponentsToPanel(receiptPanel, "UPC", "Title", 
							           "quantity", "unit Price", "subtotal");
					
					// show upc, title, quantity, unit price and subtotal for each item
					for (int i=0; i<itemUPCList.size(); ++i) {
						String upc;
						// process the text field only if its input is not empty
						if (itemUPCList.get(i).getText().trim().length() != 0
							&& quantityList.get(i).getText().trim().length() !=0 ) {

							upc = itemUPCList.get(i).getText().trim();
							itemUPC = Integer.parseInt(upc);
							title = clerkModel.queryTitle(itemUPC);
							quantity=Integer.parseInt(quantityList.get(i).getText().trim());
							if (quantity != 0) {
								unitPrice = clerkModel.queryItemPrice(itemUPC);
								subtotal = quantity*unitPrice;
								totalAmount += subtotal;
								totalQuantity += quantity;
								dialogHelper.addComponentsToPanel(receiptPanel, upc, title, 
										String.valueOf(quantity), String.valueOf(unitPrice), 
										String.valueOf(subtotal) );
							}
						}
					
					}
					// add a blank line
					dialogHelper.addComponentsToPanel(receiptPanel, "  ", "  ", 
					           "  " , "    ", 
					           "  ");	
					
					// add the summary for the purchase
					dialogHelper.addComponentsToPanel(receiptPanel, "SUMMARY", "  ", 
					           "total quantity: " + totalQuantity, "    ", 
					           "total amount: " + totalAmount);					
					pack();
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					
				}
				
				// Invoked when a component loses the keyboard focus.
				@Override
				public void focusLost(FocusEvent e) {
					regenerateReceipt();
				}

				@Override
				public void actionPerformed(ActionEvent arg0) {
					regenerateReceipt();					
				}


			};
			


			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			final JPanel inputPanel = dialogHelper.createInputPane("Purchase fields");
			
			dialogHelper.addComponentsToPanel(inputPanel, "Item UPC", itemUPC,
					                           "Quantity", quantity);
			itemUPCList.add(itemUPC);
			
			final UpdateReceipt updateReceipt = new  UpdateReceipt();
			quantityList.add(quantity);
			// register itemUPC and quantity field to update the receipt
			itemUPC.addActionListener(updateReceipt);
			quantity.addActionListener(updateReceipt);
			
			itemUPC.addFocusListener(updateReceipt);
			quantity.addFocusListener(updateReceipt);
			
			//dialogHelper.addComponentsToDialog(inputPane, "Branch name", branchName);
			
			
			JPanel addMorePanel = new JPanel(new BorderLayout());
			//JPanel generateReceiptPanel = new JPanel(new BorderLayout());
			JButton  btnAdd = new JButton("Add more item");
			btnAdd.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e)
				{
					itemUPC = new JTextField(4);
					itemUPCList.add(itemUPC);
					quantity = new JTextField(4);
					quantityList.add(quantity);
					// register itemUPC and quantity field to update the receipt
					itemUPC.addActionListener(updateReceipt);
					quantity.addActionListener(updateReceipt);
					
					itemUPC.addFocusListener(updateReceipt);
					quantity.addFocusListener(updateReceipt);
					
					dialogHelper.addComponentsToPanel(inputPanel, "Item UPC", itemUPC,
	                           "Quantity", quantity);
					pack();
				}

			});
			
			addMorePanel.add(btnAdd);
			

			

			
			JButton OKButton = new JButton("OK");
			OKButton.addActionListener(updateReceipt);
			JButton cancelButton = new JButton("Cancel");
			OKButton.addActionListener(this);

			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			});
			
			// panel for the OK and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			dialogHelper.addComponentsToPanel(buttonPanel, OKButton, cancelButton);
			
			contentPane.add(inputPanel, BorderLayout.CENTER);
			contentPane.add(addMorePanel, BorderLayout.NORTH);
			contentPane.add(buttonPanel, BorderLayout.SOUTH);
			contentPane.add(receiptPanel, BorderLayout.EAST);
			
			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e)
				{
					dispose();
				}
			});
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
			ProcessPurchaseDialog ppDialog = new ProcessPurchaseDialog(shopGUI);
			ppDialog.pack();
			//mvb.centerWindow(iDialog);
			ppDialog.setVisible(true);
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_RETURN)) {
			//TODO
			return; 
		}
	}
	
}