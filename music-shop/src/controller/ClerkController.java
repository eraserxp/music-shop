package controller;
import gui_helper.*;
import model.ClerkModel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;
import view.ShopGUI;


import java.util.Date;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/*
 * ClerkController is a control class that handles action events on the Clerk menu.
 * It updates the GUI based on which menu item the clerk selected and also use methods
 * in ClerkModel to update the tables in database
 */

public class ClerkController implements ActionListener, ExceptionListener {

	private ShopGUI mainGui = null;
	private ClerkModel clerkModel = null;
	private DialogHelper dialogHelper = null;
	
	public ClerkController(ShopGUI mainGui) {
		this.mainGui = mainGui;
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
		int receiptId;
		JTextField cardNumberField; 
		JTextField expiryDateField;
		final JPanel itemPanel;
		final JPanel receiptPanel;
		
		// constructor
		public ProcessPurchaseDialog(ShopGUI shopGUI) {
			super(shopGUI, "Process purchase", true);
			//setResizable(false);
			
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			// create the panel to hold the purchase items
			itemPanel = dialogHelper.createInputPane("Purchase items");
			// final JPanel itemPanel = dialogHelper.createInputPane("Purchase items");
			// create the panel to output the receipt
			//final JPanel receiptPanel = new JPanel(new BorderLayout());
			receiptPanel = new JPanel(new BorderLayout());
			receiptPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
			// create the panel to hold the "add more item" button
			//JPanel addMorePanel = new JPanel(new BorderLayout());
			// create panel for the OK and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			
			final JCheckBox confirmPurchase = new JCheckBox("Confirm purchase");
			
			// an inner class to listen for the changes in the text field and 
			// update the receipt
			class UpdateReceipt implements ActionListener, FocusListener {
				private void regenerateReceipt() {
					receiptPanel.removeAll();
					int upcInt;
					String title;
					int quantity = 1;					
					int totalQuantity = 0;
					double unitPrice;
					double subtotal; 
					double totalAmount = 0.0;
					// set the decimal digits to be 2
					NumberFormat numberFormatter = NumberFormat.getNumberInstance();
					numberFormatter.setMinimumFractionDigits(2);
					numberFormatter.setMaximumFractionDigits(2);
					
					String fieldNames[] = {"UPC", "Title", "quantity", "unit Price", 
							               "subtotal"};
					// add the field names as the first row 
					dialogHelper.addOneRowToPanel(receiptPanel, fieldNames);

					// show upc, title, quantity, unit price and subtotal for each item
					itemLoop:
					for (int i=0; i<upcFieldList.size(); ++i) {
						String upcString;
						// process this row only if the corresponding removeItem is not selected
						if (!checkBoxList.get(i).isSelected()) {						
							// process the text field only if its input is not empty
							if (upcFieldList.get(i).getText().trim().length() != 0 ) {
								upcString = upcFieldList.get(i).getText().trim();
								upcInt = Integer.parseInt(upcString);
								
								// check for the validity of user input
								if (!validateUPC(upcInt)) {//upc doesn't exist or its stock = 0
									String message = "Invalid UPC:"+ upcString + " !\n"
											  + "Either the upc doesn't exist or its stock = 0";
									popUpErrorMessage(message);
									upcFieldList.get(i).setText("");
									quantityFieldList.get(i).setText("");
									break itemLoop; //jump out of the for loop
								} else if ( validateUPC(upcInt) 
										   && quantityFieldList.get(i).getText().trim().length() !=0) {
									// if the upc already exist in previous items
									if (fieldValueAlreadyExist(upcFieldList, i)) {
										String message = "The item with UPC: "+ upcInt 
												+ " already exist in purchase.\n"
												+ "Please update the quantity of the existing item.";
										upcFieldList.get(i).setText("");
										confirmPurchase.setSelected(false);
										popUpErrorMessage(message);
									}
									// if upc is valid and quantity field is not empty
									quantity=Integer.parseInt(quantityFieldList.get(i).getText().trim());
									if (!validateInputQuantity(upcInt, quantity)) { 
										// if quantity is not valid
										String message = "Invalid quantity: "+ quantity + "! " 
												+ "It should be in range from 1 to " 
												+ clerkModel.queryItemQuantity(upcInt);
										popUpErrorMessage(message);
										quantityFieldList.get(i).setText("");
										break itemLoop;
									} else { 
										// update the receipt only if both upc and quantity are valid
										unitPrice = clerkModel.queryItemPrice(upcInt);
										title = clerkModel.queryTitle(upcInt);
										subtotal = quantity*unitPrice;
										totalAmount += subtotal;
										totalQuantity += quantity;
										String fieldValues[] = {upcString, title, 
												String.valueOf(quantity),
												String.valueOf(unitPrice),
												numberFormatter.format(subtotal)};
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
							    "receipt id: " + clerkModel.getNextReceiptID(),							    
					           "total quantity: " + totalQuantity, 
					           "    ", 
					           "total amount: " + numberFormatter.format(totalAmount)
					           }; 
					
					dialogHelper.addOneRowToPanel(receiptPanel, summary);
					String dateString = getCurrentDate("yyyy-MM-dd");
					int len = cardNumberField.getText().trim().length();
					String cardNo;
					if (len==0) {
						cardNo = "null";
					} else {
						cardNo = "***" + cardNumberField.getText().trim().substring(len-5, len);
					}
					String dateAndCardNo[] = {
							"     ",
							"date: " + dateString,
							"      ",
							"      ",
							"card num: " + cardNo
					};
					dialogHelper.addOneRowToPanel(receiptPanel, dateAndCardNo);
					receiptPanel.repaint();
					pack(); 
					// center the process purchase dialog
					mainGui.centerWindow(ProcessPurchaseDialog.this);
				}
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					regenerateReceipt();					
				}

				@Override
				public void focusGained(FocusEvent arg0) {
					regenerateReceipt();					
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					//regenerateReceipt();
				}



			}; // end of class UpdatReceipt
			
			final UpdateReceipt updateReceipt = new  UpdateReceipt();
			
			//Class RemoveItemListener to listen to the remove item checkbox
			class RemoveItemListener implements ItemListener {
				@Override
				public void itemStateChanged(ItemEvent ie) {
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
						//updateReceipt.regenerateReceipt();
					}
					
									
				}
				
			} // end of class RemoveItemListener
			
			final RemoveItemListener removeItemListener = new RemoveItemListener();
			
			// add the first row of upc and quantity fields to accept user input
			dialogHelper.addComponentsToPanel(itemPanel, "Item UPC", upcField,
					                           "Quantity", quantityField, removeItem);
			upcFieldList.add(upcField);
			quantityFieldList.add(quantityField);
			checkBoxList.add(removeItem);
			

			
			// register itemUPC and quantity field to update the receipt
			//upcField.addActionListener(updateReceipt);
			//quantityField.addActionListener(updateReceipt);
			upcField.addFocusListener(updateReceipt);
			quantityField.addFocusListener(updateReceipt);
			
			removeItem.addItemListener(removeItemListener);
			removeItem.addActionListener(updateReceipt);
			
			
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
					//upcField.addActionListener(updateReceipt);
					//quantityField.addActionListener(updateReceipt);
					upcField.addFocusListener(updateReceipt);
					quantityField.addFocusListener(updateReceipt);
					// to unselect the confirm purchase checkbox
					upcField.addFocusListener(new UnSelectCheckBox(confirmPurchase));
					quantityField.addFocusListener(new UnSelectCheckBox(confirmPurchase));
					removeItem.addItemListener(removeItemListener);
					removeItem.addActionListener(updateReceipt);
					// add itemUPC and quantity text fields as a row
					dialogHelper.addComponentsToPanel(itemPanel, "Item UPC", upcField,
	                           "Quantity", quantityField, removeItem);
					pack();
				}

			});
			

									
			final JButton OKButton = new JButton("OK");
			OKButton.setEnabled(false);
			JButton cancelButton = new JButton("Cancel");
			OKButton.addActionListener(this);
			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
					dispose();
				}
			});
			
	
			// once the confirm purchase is selected, update the receipt automatically
			confirmPurchase.addActionListener(updateReceipt);
			// if the confirm button is not selected, disable the OK button
			// otherwise, enable the OK button
			confirmPurchase.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent ie) {
					JCheckBox cb = (JCheckBox) ie.getSource();
					if (cb.isSelected()) {
						OKButton.setEnabled(true);
					} else {
						OKButton.setEnabled(false);
					}
				}
			});
			
			upcField.addFocusListener(new UnSelectCheckBox(confirmPurchase));
			quantityField.addFocusListener(new UnSelectCheckBox(confirmPurchase));
			
			
			// when you click add more item button, the confirm purchase checkbox is unselected
			btnAdd.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					confirmPurchase.setSelected(false);						
				}
			});
			
			// add the OK and cancel buttons to the button panel
			dialogHelper.addComponentsToPanel(buttonPanel, OKButton, cancelButton);
			
			
			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS) );
			inputPanel.add(itemPanel);
			JPanel addAndConfirmPanel = new JPanel(new BorderLayout());
			addAndConfirmPanel.setBorder(new EmptyBorder(5, 5, 25, 5));
			
			addAndConfirmPanel.add(btnAdd, BorderLayout.WEST);
			addAndConfirmPanel.add(confirmPurchase, BorderLayout.EAST);
			inputPanel.add(addAndConfirmPanel);
			
			JPanel paymentPanel = dialogHelper.createInputPane("Payment");
			cardNumberField = new JTextField(12);
			expiryDateField = new JTextField(10);
			dialogHelper.addComponentsToPanel(paymentPanel, "card number (16-digits)", cardNumberField);
			dialogHelper.addComponentsToPanel(paymentPanel, "expiry date (yyyy-mm)", expiryDateField);
			
			inputPanel.add(paymentPanel);
			
			contentPane.add(inputPanel, BorderLayout.CENTER);
			contentPane.add(buttonPanel, BorderLayout.SOUTH);
			contentPane.add(receiptPanel, BorderLayout.EAST);
			
			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e)
				{
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
					dispose();
				}
			});
		}
		
		// when the clerk press the OK button, write the purchase into database
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<Integer> upcList = new ArrayList<Integer>();
			ArrayList<Integer> quantityList = new ArrayList<Integer>();
			// fill the upcList and quantityList with proper values from the two lists
			// of text fields
			collectNonEmptyValues(upcFieldList, quantityFieldList, upcList, quantityList);
			String dateString = getCurrentDate("yyyy-MM-dd");
			Integer cid = null;
			String cardNumber = null;
			String expiryDate = null;
			if (cardNumberField.getText().trim().length()!=0) {
				cardNumber = cardNumberField.getText().trim();
			}
			if (expiryDateField.getText().trim().length()!=0) {
				expiryDate = expiryDateField.getText().trim();
			}

			String expectedDateString = null;
			String deliveredDateString = null;
			receiptId = clerkModel.getNextReceiptID();
			if (
				clerkModel.processPurchase(receiptId, dateString, cid,
                            cardNumber, expiryDate, 
                            expectedDateString, deliveredDateString, 
                            upcList, quantityList) == true 
                ) {
				
				popUpOKMessage("The purchase is successful!");
				mainGui.updateStatusBar("Purchase operation is successful!"
				                        + " Receipt id is " + receiptId + "\n");
				// close the window
				dispose();
			} else {
				mainGui.updateStatusBar("Purchase operation is failed!\n");
				popUpErrorMessage("Failed to process this purchase!");
			}
			
		}
		

		
	}
	
	
	/**
	 * generate a dialog to process a return. It calls processReturn method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessReturnDialog extends JDialog implements ActionListener {
		private ArrayList<JTextField> returnQuantityFieldList = new ArrayList<JTextField>();
		private JTextField returnQuantityField = null;
		//private JCheckBox removeItem = new JCheckBox("remove");
		private ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
		private ArrayList<Integer> upcList = new ArrayList<Integer>();
		private ArrayList<Double> priceList = new ArrayList<Double>();
		private ArrayList<Integer> purchaseQuantityList = null;
		private ArrayList<Integer> returnQuantityList = null;
		
		
		public ProcessReturnDialog(ShopGUI shopGUI) {
			//TODO
			super(shopGUI, "Process return", true);
			//setResizable(false);
			final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
			numberFormatter.setMinimumFractionDigits(2);
			numberFormatter.setMaximumFractionDigits(2);
			
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			// a top panel on the top of contentPane
			JPanel topPane = new JPanel(new BorderLayout());

			// a pane to hold the receipt id information			
			JPanel receiptIdPane = new JPanel(new FlowLayout());
			JLabel receiptIdLabel = new JLabel("Receipt id: ");
			final JTextField receiptIdField = new JTextField(4);
			final JCheckBox see = new JCheckBox("See the contents of the receipt");
		

			receiptIdPane.add(receiptIdLabel);
			receiptIdPane.add(receiptIdField);
			receiptIdPane.add(see);
			
			topPane.add(receiptIdPane, BorderLayout.WEST);
			
			contentPane.add(topPane, BorderLayout.NORTH);
			
			final JPanel inputPane = dialogHelper.createInputPane("Return items");
			

			contentPane.add(inputPane, BorderLayout.CENTER);
			

			JPanel summaryAndDecisionPane = new JPanel(new BorderLayout());
			final JLabel summaryLabel = new JLabel("The refund amount = 0.0");
			summaryAndDecisionPane.add(summaryLabel, BorderLayout.CENTER);
			final JCheckBox confirmReturn = new JCheckBox("confirm return");
			summaryAndDecisionPane.add(confirmReturn, BorderLayout.NORTH);
			
			// buttonPane to hold the OK and Cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton OKButton = new JButton("OK");
			OKButton.setEnabled(false); // disable the Ok button initially
			JButton cancelButton = new JButton("Cancel");
			
			dialogHelper.addComponentsToPanel(buttonPanel, OKButton, cancelButton);
			summaryAndDecisionPane.add(buttonPanel, BorderLayout.SOUTH);
			contentPane.add(summaryAndDecisionPane, BorderLayout.SOUTH);
			
			// show the contents of the receipt
			class ShowReceiptContents implements ActionListener, FocusListener, ItemListener {
				
				private void showContents() {
					if (receiptIdField.getText().trim().length()!=0) {
						int receiptId = Integer.parseInt(receiptIdField.getText().trim());
						if (!isReceiptIdExisted(receiptId)) { 
							// non-existing receipt id
							popUpErrorMessage("Receipt id = " + receiptId 
									  + " doesn't exist! " );
							receiptIdField.setText("");
							see.setSelected(false);
							inputPane.removeAll();
						} else if (!isReceiptIdValid(receiptId)) { 
							// existing receipt id but not valid
							popUpErrorMessage("All items associated with Receipt id = " 
						                      + receiptId +
									          " have already been returned.");
							receiptIdField.setText("");
							see.setSelected(false);
							inputPane.removeAll();
							
						} else if (!eligibleForReturn(receiptId)) { 
							// the receipt id is outside the time range for return
							popUpErrorMessage("Receipt id = " + receiptId 
									  + " is not eligible for return!\n" +
									  "Because it is more than 15 days old.");
							receiptIdField.setText("");
							// uncheck the checkbox
							see.setSelected(false);
							inputPane.removeAll();
						} else {
							// remove all checkboxs and quantity text fields
							//checkBoxList = new ArrayList<JCheckBox> ();
							returnQuantityFieldList = new ArrayList<JTextField>();
							// clean the list
							upcList = new ArrayList<Integer>();
							priceList = new ArrayList<Double>();
							purchaseQuantityList = new ArrayList<Integer>();
							// create a table of all purchase item associated with the receipt id
							String[] columnLabels = {"return quantity", 
					                 "UPC", "title", "purchase quantity", 
					                 "unit price"};
							ArrayList< ArrayList<String> > rowList = new ArrayList< ArrayList<String> >();
							ResultSet rs = clerkModel.getPurchaseItem(receiptId);
							//ResultSetMetaData rsmd = rs.getMetaData();
						
							try {
								while (rs.next()) {
									ArrayList<String> oneRow = new ArrayList<String>();
									int upc = rs.getInt("upc");
									upcList.add(upc);
									String upcString = Integer.toString(upc );
									oneRow.add(upcString);
									String title = rs.getString("title");
									oneRow.add(title);
									int purchaseQuantity = rs.getInt("quantity");
									purchaseQuantityList.add(purchaseQuantity);
									String quantityString = Integer.toString(purchaseQuantity);
									oneRow.add(quantityString);
									double price = rs.getDouble("price");
									priceList.add(price);
									String priceString = Double.toString( price );
									oneRow.add(priceString);
									rowList.add(oneRow);
								}
								
								// create the corresponding checkbox and text field for each row
								for (int i=0; i<rowList.size(); ++i) {
									returnQuantityField = new JTextField(4);
									// when you click the field, unselect the confirm return checkbox
									returnQuantityField.addFocusListener(new UnSelectCheckBox(confirmReturn));
									//removeItem = new JCheckBox();
									// when the checkbox is selected, disable the return quantity field
									//removeItem.addItemListener(new DisableTextField(returnQuantityField));
									// and unselect the confirm return checkbox
									// otherwise enable the field
//									removeItem.addItemListener(new ItemListener() {										
//										@Override
//										public void itemStateChanged(ItemEvent e) {
//											// TODO Auto-generated method stub
//											JCheckBox cb = (JCheckBox) e.getSource();
//											if (cb.isSelected()) {
//												confirmReturn.setSelected(false);
//											} 
//										}
//									});
									
									returnQuantityFieldList.add(returnQuantityField);
									//checkBoxList.add(removeItem);
									//removeItem.
								}
								
								// add the table to the gui
								dialogHelper.addOneTableToPanel(inputPane, columnLabels,
					                       returnQuantityFieldList, 
					                       rowList); 
							} catch (SQLException ex) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								mainGui.updateStatusBar(ex.getMessage());
							} // end of try catch block
							
						}
					}
				} // end of showContents
				
				@Override
				public void focusGained(FocusEvent arg0) {
					// TODO Auto-generated method stub
					showContents();
					pack();					
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					// TODO Auto-generated method stub
					showContents();
					pack();
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					showContents();
					pack();
				}

				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					JCheckBox cb = (JCheckBox) e.getSource();
					if (cb.isSelected()) {
						showContents();
						pack();
					} else {
						// clean the input pane
						inputPane.removeAll();
						pack();
					}
				}
				
			}
			
			ShowReceiptContents showReceiptContents = new ShowReceiptContents();
			

			
			see.addItemListener(showReceiptContents);

			

			
			// when the see checkbox is unselected, disable the return quantity box
			// and the confirm return checkbox
			see.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent ie) {
					// TODO Auto-generated method stub
					JCheckBox cb = (JCheckBox) ie.getSource();
					if (!cb.isSelected()) {
						for (int i=0; i<returnQuantityFieldList.size(); ++i) {
							returnQuantityFieldList.get(i).setEnabled(false);
						}
						confirmReturn.setEnabled(false);
					} else {
						for (int i=0; i<returnQuantityFieldList.size(); ++i) {
							returnQuantityFieldList.get(i).setEnabled(true);
						}
						confirmReturn.setEnabled(true);
					}
				}
			});
			
			// when you press enter in receipt id text field, check the checkbox
			// so that the receipt contents shown in the input pane will be refreshed
			// whenever the receipt id changes, unselect the confirm return checkbox 
			// because the vital information about the return has changed
			receiptIdField.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					// if the receipt id field is empty, unselect the checkbox
					if (receiptIdField.getText().trim().length()==0) {
						see.setSelected(true);
						see.setSelected(false);
						confirmReturn.setSelected(false);
					} else {
						see.setSelected(false);
						see.setSelected(true);
						confirmReturn.setSelected(false);
					}
				}
			});
			
			// calculate the refund for the return and enable the OK button
			confirmReturn.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent ie) {
					// TODO Auto-generated method stub
					JCheckBox cb = (JCheckBox) ie.getSource();
					double refund = 0.0;
					if (cb.isSelected()) {
						returnQuantityList = obtainListFromFields(returnQuantityFieldList);						
						for (int i=0; i<returnQuantityList.size(); ++i) {
							// check if the return quantity is valid
							if (returnQuantityList.get(i)> purchaseQuantityList.get(i)) {
								popUpErrorMessage("The return quantity is not valid " +
										    "for item with upc = " + upcList.get(i));
								returnQuantityFieldList.get(i).setText("");
							} else {
								refund += returnQuantityList.get(i)*priceList.get(i);
							}
						}
						String summary = "The refund is " + 
			                      numberFormatter.format(refund) + ". "
			                      + "Return Id: " + clerkModel.getNextReturnID();
						int receiptId = Integer.parseInt(receiptIdField.getText().trim());
						String cardNo = getCardNo(receiptId);
						if (cardNo!=null) {
							summary = summary + " Credit card number: " +
									 cardNo;
						}
						summaryLabel.setText(summary);
						//enable the OK button
						OKButton.setEnabled(true);
					} else {
						summaryLabel.setText("The refund is 0.0");
						//disable the OK button
						OKButton.setEnabled(false);
					}
				}
			});
			
			// when the OK button is pressed, process the return and write to
			// the database
			OKButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					ArrayList<Integer> selectedUPCs = new ArrayList<Integer>();
					// get the upc that has been selected
					for (int i=0; i<upcList.size(); ++i) {
						returnQuantityField = returnQuantityFieldList.get(i);						
						String quantityString = null;
						int quantity = 0;
						if (returnQuantityField.getText().trim().length()!=0) {
							quantityString = returnQuantityField.getText().trim();
							quantity = Integer.parseInt(quantityString);
						}
						
						if (returnQuantityField.isEnabled()
						    && (quantityString != null)	
							&& (quantity != 0) ) {
							selectedUPCs.add(upcList.get(i));
						}
					}
					
					int receiptId = Integer.parseInt(receiptIdField.getText().trim());
					String dateString = getCurrentDate("yyyy-MM-dd");
					ArrayList<Integer> returnQuantityList = obtainListFromFields(returnQuantityFieldList);
					if (clerkModel.processReturn(receiptId, dateString, 
			                      selectedUPCs, 
			                      returnQuantityList)) {
						popUpOKMessage("The return is successful!");
						mainGui.updateStatusBar("The return is successful!");
						receiptIdField.setText("");
						see.setSelected(false);
						pack();
					} else {
						popUpErrorMessage("The return is failed!");
						mainGui.updateStatusBar("The return is failed!");
						receiptIdField.setText("");
						see.setSelected(false);
						pack();
					}
					
				}
			});

			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dispose();
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
				}
			});

			
			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e)
				{
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
					dispose();
				}
			});
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	// when an exception happens in the corresponding model class
	// it writes the exception message to the status area of the main gui
	@Override
	public void exceptionGenerated(ExceptionEvent ex) {
		String message = ex.getMessage();
		// annoying beep sound
		Toolkit.getDefaultToolkit().beep();

		if (message != null)
		{	
			mainGui.updateStatusBar(ex.getMessage());
		}
		else
		{
			mainGui.updateStatusBar("An exception occurred!");
		}
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		// you cannot use == for string comparisons
		if (actionCommand.equals(ShopGUI.PROCESS_PURCHASE)) {
			mainGui.updateStatusBar("PROCESS A PURCHASE .......");
			ProcessPurchaseDialog ppDialog = new ProcessPurchaseDialog(mainGui);
			ppDialog.pack();
			mainGui.centerWindow(ppDialog);
			ppDialog.setVisible(true);
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_RETURN)) {
			mainGui.updateStatusBar("PROCESS A RETURN .......");
			ProcessReturnDialog prDialog = new ProcessReturnDialog(mainGui);
			prDialog.pack();
			mainGui.centerWindow(prDialog);
			prDialog.setVisible(true);
			return; 
		}
	}
	
	// validate the upc exists and its stock > 0
	private boolean validateUPC(int upc) { 
		return clerkModel.isUPCValid(upc);
	}
	
	// validate the existence of the upc
	private boolean validateUPCExistence(int upc) {
		return clerkModel.isUPCExisted(upc);
	}
	
	private boolean validateInputQuantity(int upc, int quantity) {
		return clerkModel.queryItemQuantity(upc) >= quantity &&
				quantity >=1;
	}
	
	/* given a list of text fields and find out whether the corresponding value for
	 * the text field with index = excludeIndex already exist in other text fields.
	 * I use this function to check whether new entered item with a particular upc
	 * has already existed in the item list. Therefore, we can use it to guarantee 
	 * that all the UPCs are distinct
	 */
	private boolean fieldValueAlreadyExist(ArrayList<JTextField> upcFieldList, 
			                                int excludeIndex) {
		// we want to check whether this upc has already existed in the list or not
		int upcToBeChecked = Integer.parseInt(upcFieldList.get(excludeIndex).getText().trim());
		ArrayList<Integer> otherUPCs = new ArrayList<Integer>();
		for (int i=0; i<upcFieldList.size(); ++i) {
			if (i!=excludeIndex && upcFieldList.get(i).isEnabled()
				&& upcFieldList.get(i).getText().trim().length() != 0
				) {
				otherUPCs.add(
				 Integer.parseInt(upcFieldList.get(i).getText().trim() )
				);
			}
		}
		return otherUPCs.contains(upcToBeChecked);
	}
	
	// collect integers from all enable and non-empty fields in a list of JTextFields
	private ArrayList<Integer> obtainListFromFields(ArrayList<JTextField> FieldList) {
		ArrayList<Integer> fieldValueList = new ArrayList<Integer>();
		for (int i=0; i<FieldList.size(); ++i) {
			if (FieldList.get(i).isEnabled()
				&& FieldList.get(i).getText().trim().length() != 0
				) {
				fieldValueList.add(
				 Integer.parseInt(FieldList.get(i).getText().trim() )
				);
			}
		}
		return fieldValueList;
	}
	
	/* write the field values of fieldList1 to valueList1 
	*  and write the field values of fieldList2 to valueList2
	*  under the conditions:
	*  1. both fields from the the two field lists are not empty
	*  2. both text fields from the two field lists are not disabled
	*  
	*  Note fieldList1 and fieldList2 should have the same size
	*/
	private void collectNonEmptyValues(ArrayList<JTextField> fieldList1, 
			                           ArrayList<JTextField> fieldList2,
			                           ArrayList<Integer> valueList1,
			                           ArrayList<Integer> valueList2 ) {
		for (int i=0; i<fieldList1.size(); ++i) {
			if (fieldList1.get(i).isEnabled()
				&& fieldList1.get(i).getText().trim().length() != 0
				&& fieldList2.get(i).isEnabled()
				&& fieldList2.get(i).getText().trim().length() != 0
				) {
				valueList1.add(Integer.parseInt(fieldList1.get(i).getText().trim()));
				valueList2.add(Integer.parseInt(fieldList2.get(i).getText().trim()));
			}
		}
	}
	
	// get the current date in format
	// format is usually "yyyy-MM-dd"
	private String getCurrentDate(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		// get the current date
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	// check if an receiptId exists in database
	private boolean isReceiptIdExisted(int receiptId) {
		return clerkModel.isReceiptIdExisted(receiptId);
	}
	
	// check if an existing receiptId is valid by checking there is still some
	// purchase item associated with it
	private boolean isReceiptIdValid(int receiptId) {
		return clerkModel.isReceiptIdValid(receiptId);
	}
	
	// check if a valid receiptId is OK for return
	private boolean eligibleForReturn(int receiptId) {
		return !clerkModel.isReceiptIdOutdated(receiptId);
	}
	
	// obtain the credit card number for a purchase
	// if the payment is in cash, return null
	private String getCardNo(int receiptId) {
		return clerkModel.getCardNo(receiptId);
	}
	
	// create a pop up window showing the error message
	private void popUpErrorMessage(String message) {
		Toolkit.getDefaultToolkit().beep();
		// display a popup to inform the user of the validation error
		JOptionPane errorPopup = new JOptionPane();
		errorPopup.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	// create a pop up window showing the OK message
	private void popUpOKMessage(String message) {
		//Toolkit.getDefaultToolkit().beep();
		// display a popup to inform the user of the validation error
		JOptionPane errorPopup = new JOptionPane();
		//errorPopup.showMessageDialog(this, message, "OK", JOptionPane.INFORMATION_MESSAGE);
		errorPopup.showMessageDialog(null, message, "OK", JOptionPane.INFORMATION_MESSAGE);
	}
	
}




