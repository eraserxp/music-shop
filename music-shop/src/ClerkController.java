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
		private ArrayList<JTextField> upcFieldList = new ArrayList<JTextField>();
		private JTextField upcField = new JTextField(4);
		private ArrayList<JTextField> quantityFieldList = new ArrayList<JTextField>();
		private JTextField quantityField = new JTextField(4);
		private JCheckBox removeItem = new JCheckBox("remove");
		private ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
		private int upc_last; // to save the last upc entered;
		
		// constructor
		public ProcessPurchaseDialog(ShopGUI shopGUI) {
			//TODO
			super(shopGUI, "Process purchase", true);
			//setResizable(false);
			
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			// create the panel to accept user input
			final JPanel inputPanel = dialogHelper.createInputPane("Purchase items");
			// create the panel to output the receipt
			final JPanel receiptPanel = new JPanel(new BorderLayout());
			receiptPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
			// create the panel to hold the "add more item" button
			JPanel addMorePanel = new JPanel(new BorderLayout());
			// create panel for the OK and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			
			// an inner class to listen for the changes in the text field and 
			// update the receipt
			class UpdateReceipt implements ActionListener { //FocusListener, ActionListener{
				// constructor
				public UpdateReceipt() {
					//empty ;
				}
				
				public void regenerateReceipt() {
					receiptPanel.removeAll();
					int upcInt;
					String title;
					int quantity = 1;					
					int totalQuantity = 0;
					double unitPrice = 1.0;
					double subtotal = 0.0; 
					double totalAmount = 0.0;
					String fieldNames[] = {"UPC", "Title", "quantity", "unit Price", 
							               "subtotal"};
					// add the field names as the first row 
					dialogHelper.addOneRowToPanel(receiptPanel, fieldNames);
					// show upc, title, quantity, unit price and subtotal for each item
					for (int i=0; i<upcFieldList.size(); ++i) {
						String upcString;
						// process this row only if the corresponding removeItem is not selected
						if (!checkBoxList.get(i).isSelected()) {
							
							// process the text field only if its input is not empty
							if (upcFieldList.get(i).getText().trim().length() != 0 ) {
								//&& quantityFieldList.get(i).getText().trim().length() !=0 ) {

								upcString = upcFieldList.get(i).getText().trim();
								upcInt = Integer.parseInt(upcString);
								title = clerkModel.queryTitle(upcInt);
								//
								// check for the validity of user input
								if (!validateItemUPC(upcInt)) {//upc not valid
									String message = "Invalid UPC:"+ upcString + " !";
									popUpErrorMessage(message);
									upcFieldList.get(i).setText("");
									quantityFieldList.get(i).setText("");
								} else if ( validateItemUPC(upcInt) 
										   && quantityFieldList.get(i).getText().trim().length() !=0) {
									// if upc is valid and quantity field is not empty
									quantity=Integer.parseInt(quantityFieldList.get(i).getText().trim());
									if (!validateInputQuantity(upcInt, quantity)) { 
										// if quantity is not valid
										String message = "Invalid quantity: "+ quantity + "! " 
												+ "It should be in range from 1 to " 
												+ clerkModel.queryItemQuantity(upcInt);
										popUpErrorMessage(message);
										quantityFieldList.get(i).setText("");
									} else { 
										// update the receipt only if both upc and quantity are valid
										unitPrice = clerkModel.queryItemPrice(upcInt);
										subtotal = quantity*unitPrice;
										totalAmount += subtotal;
										totalQuantity += quantity;
										String fieldValues[] = {upcString, title, 
												String.valueOf(quantity),
												String.valueOf(unitPrice),
												String.valueOf(subtotal)};
										dialogHelper.addOneRowToPanel(receiptPanel, fieldValues);
									}
								} 
							}
						}
					} // end of for loop
					
					// add a blank line
					String blanks[] = {"  ",  "  ",  "  ",  "   ",  " " }; 
					dialogHelper.addOneRowToPanel(receiptPanel, blanks);
					
					// add the summary for the purchase
					String summary[] = {
							   "SUMMARY", 
							    "  ", 
					           "total quantity: " + totalQuantity, 
					           "    ", 
					           "total amount: " + totalAmount
					           }; 
					dialogHelper.addOneRowToPanel(receiptPanel, summary);				
					pack(); 
				}
				
//				@Override
//				public void focusGained(FocusEvent e) {
//					//empty
//				}
				
//				// Invoked when a component loses the keyboard focus.
//				@Override
//				public void focusLost(FocusEvent e) {
//					regenerateReceipt();
//				}

				@Override
				public void actionPerformed(ActionEvent arg0) {
					regenerateReceipt();					
				}

			}; // end of class UpdatReceipt
			
			final UpdateReceipt updateReceipt = new  UpdateReceipt();
			
			//Class RemoveItemListener to listen to the remove item checkbox
			class RemoveItemListener implements ItemListener {
				@Override
				public void itemStateChanged(ItemEvent ie) {
					// TODO Auto-generated method stub
					// check all the checkbox to see which are selected
					// and update the status for the corresponding text fields
					for (int i=0; i<checkBoxList.size(); ++i) {
						JCheckBox cb = checkBoxList.get(i);
						if (cb.isSelected()) {
							// disable its corresponding upc and quantity fields
							upcFieldList.get(i).setEnabled(false);
							quantityFieldList.get(i).setEnabled(false);
							
						} else {
							// re-enable its corresponding upc and quantity fields
							upcFieldList.get(i).setEnabled(true);
							quantityFieldList.get(i).setEnabled(true);
						}
						// to update the receipt
						updateReceipt.regenerateReceipt();
					}
					
					
					
				}
				
			} // end of class RemoveItemListener
			
			final RemoveItemListener removeItemListener = new RemoveItemListener();
			
			// add the first row of upc and quantity fields to accept user input
			dialogHelper.addComponentsToPanel(inputPanel, "Item UPC", upcField,
					                           "Quantity", quantityField, removeItem);
			upcFieldList.add(upcField);
			quantityFieldList.add(quantityField);
			checkBoxList.add(removeItem);
			

			
			// register itemUPC and quantity field to update the receipt
			upcField.addActionListener(updateReceipt);
			quantityField.addActionListener(updateReceipt);
			
//			upcField.addFocusListener(updateReceipt);
//			quantityField.addFocusListener(updateReceipt);
			
			removeItem.addItemListener(removeItemListener);
			
			// set the action commands to mark the two fields
			upcField.setActionCommand("upc_field_0");
			quantityField.setActionCommand("quantity_field_0");
			
			// use this button to add an additional row to accept upc and quantity input
			JButton  btnAdd = new JButton("Add more item");
			btnAdd.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e)
				{
					upcField = new JTextField(4);
					upcFieldList.add(upcField);
					quantityField = new JTextField(4);
					quantityFieldList.add(quantityField);
					removeItem = new JCheckBox("remove");
					checkBoxList.add(removeItem);
					// register itemUPC and quantity field to update the receipt
					upcField.addActionListener(updateReceipt);
					quantityField.addActionListener(updateReceipt);					
//					upcField.addFocusListener(updateReceipt);
//					quantityField.addFocusListener(updateReceipt);
					removeItem.addItemListener(removeItemListener);
					// add itemUPC and quantity text fields as a row
					dialogHelper.addComponentsToPanel(inputPanel, "Item UPC", upcField,
	                           "Quantity", quantityField, removeItem);
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
			
			// add the OK and cancel buttons to the button panel
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
			// find out the type of source (itemUPC or quantity)
			if (e.getActionCommand().equals("item UPC")) {
				JTextField upcField = (JTextField) e.getSource(); //get the event source
				if (upcField.getText().trim().length() != 0) {
					int upc = Integer.parseInt(upcField.getText().trim());
					// save the upc
					upc_last = upc;
					// if the upc is not valid
					if (!validateItemUPC(upc)) {
						Toolkit.getDefaultToolkit().beep();

						// display a popup to inform the user of the validation error
						JOptionPane errorPopup = new JOptionPane();
						errorPopup.showMessageDialog(this, "Invalid UPC", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} 
			} else if (e.getActionCommand().equals("item quantity")) {
				JTextField quantityField = (JTextField) e.getSource(); //get the event source
				if (quantityField.getText().trim().length() != 0) {
					int quantity = Integer.parseInt(quantityField.getText().trim());
					// if the input quantity is not valid
					if (!validateInputQuantity(upc_last,quantity)) {
						Toolkit.getDefaultToolkit().beep();

						// display a popup to inform the user of the validation error
						JOptionPane errorPopup = new JOptionPane();
						errorPopup.showMessageDialog(this, "Invalid UPC", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} 
			}
			
			
		}
		
		private boolean validateItemUPC(int upc) { 
			return clerkModel.isUPCValid(upc);
		}
		
		private boolean validateInputQuantity(int upc, int quantity) {
			return clerkModel.queryItemQuantity(upc) >= quantity &&
					quantity >=1;
		}
		
		
		// set the markers for a list of text fields so that you can
		// remove or update them later
		private void setMarkersForFields(ArrayList<JTextField> fields, String prefix) {
			for (int i=0; i < fields.size(); ++i) {
				fields.get(i).setActionCommand(prefix + Integer.toString(i));
			}
		} // end of setMarkersForFields
		
		private void popUpErrorMessage(String message) {
			Toolkit.getDefaultToolkit().beep();
			// display a popup to inform the user of the validation error
			JOptionPane errorPopup = new JOptionPane();
			errorPopup.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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