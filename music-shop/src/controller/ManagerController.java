package controller;

import gui_helper.DialogHelper;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 

import controller.ClerkController.ProcessPurchaseDialog;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;
import view.ShopGUI;

import model.ManagerModel;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/*
 * ManagerController is a control class that handles action events on the Manager menu.
 * It updates the GUI based on which menu item the Manager selected and also use methods
 * in ManagerModel to update the tables in database
 */

public class ManagerController implements ActionListener, ExceptionListener {

	private ShopGUI mainGui = null;
	private ManagerModel managerModel = null;
	private DialogHelper dialogHelper = null;
	private DialogHelper dialogHelper2 = null;
	
	public ManagerController(ShopGUI sg) {
		mainGui = sg;
		managerModel = new ManagerModel();
		// add this to the listener list of the manager model
		managerModel.addExceptionListener(this);
		dialogHelper = new DialogHelper();
		dialogHelper2 = new DialogHelper();
	}
	
	
	/**
	 * generate a dialog to allow the manager to add items to store
	 *
	 */
	class AddItemToStoreDialog extends JDialog implements ActionListener {
		JTextField upcField = new JTextField(4);
		JCheckBox confirmUPC = new JCheckBox("confirm");
		JTextField titleField = new JTextField(20);
		String[] allowedTypes = {"CD", "DVD"};
		JComboBox<String> typeComboBox = new JComboBox<String>(allowedTypes);
		String[] allowedCategories = {"rock", "pop", "rap", "country", "classical", "new age", "instrumental"};
		JComboBox<String> CategoryComboBox = new JComboBox<String>(allowedCategories);
		JTextField companyField = new JTextField(10);
		JTextField yearField = new JTextField(4);
		JTextField priceField = new JTextField(4);
		JTextField quantityField = new JTextField(4);
		JTextField singerField =  new JTextField(10);
		ArrayList<JTextField> singerFieldList = new ArrayList<JTextField>();
		
		JTextField songField =  new JTextField(10);
		ArrayList<JTextField> songFieldList = new ArrayList<JTextField>();
		
		boolean upcExisted = false;
		Double price;
		Integer quantity;
		
		public AddItemToStoreDialog(ShopGUI shopGUI) {
			super(shopGUI, "Add item to store", true);
			//setResizable(false);			
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);			
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			// input pane to hold item pane, singer pane and song pane
			JPanel inputPane = new JPanel(new BorderLayout());
			
			JPanel itemPane = new JPanel(new BorderLayout());
			
			JPanel upcPane = dialogHelper.createInputPane("");
			dialogHelper.addComponentsToPanel(upcPane, "Enter upc", upcField, confirmUPC);
			
			
			final JPanel otherInfoPane = dialogHelper.createInputPane("Other information");
			//dialogHelper.addComponentsToPanel2(itemPane, "Enter upc", upcField);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Enter title", titleField);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Choose type", typeComboBox);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Choose category", CategoryComboBox);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Enter company", companyField);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Enter year(yyyy)", yearField);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Enter price", priceField);
			dialogHelper.addComponentsToPanel2(otherInfoPane, "Enter quantity", quantityField);

			
			itemPane.add(upcPane, BorderLayout.NORTH);
			itemPane.add(otherInfoPane,BorderLayout.SOUTH);
			
			inputPane.add(itemPane, BorderLayout.NORTH);
			
			//
			JPanel singerPane = new JPanel(new BorderLayout());
			final JPanel singerPaneTop = new JPanel(new BorderLayout());
			JButton addSingerButton = new JButton("Add more singer");
			singerPaneTop.add(addSingerButton, BorderLayout.WEST);
			final JPanel singerPaneBottom = dialogHelper.createInputPane("");
			dialogHelper.addComponentsToPanel(singerPaneBottom, "Singer name", singerField);
			singerFieldList.add(singerField);
			
			singerPane.add(singerPaneTop,BorderLayout.NORTH);
			singerPane.add(singerPaneBottom,BorderLayout.SOUTH);

			

			final JPanel songPane = new JPanel(new BorderLayout());
			final JPanel songPaneTop = new JPanel(new BorderLayout());
			JButton addSongButton = new JButton("Add more song");
			songPaneTop.add(addSongButton, BorderLayout.WEST);
			final JPanel songPaneBottom = dialogHelper2.createInputPane("");
			dialogHelper2.addComponentsToPanel(songPaneBottom, "Song title", songField);
			songFieldList.add(songField);
			
			songPane.add(songPaneTop,BorderLayout.NORTH);
			songPane.add(songPaneBottom,BorderLayout.SOUTH);
			

			
			inputPane.add(singerPane, BorderLayout.CENTER);
			inputPane.add(songPane, BorderLayout.SOUTH);
			
			contentPane.add(inputPane, BorderLayout.CENTER);
			

			// pane to hold the register and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton addButton = new JButton("Add to store");	
			
			JButton cancelButton = new JButton("Cancel");			
			dialogHelper.addComponentsToPanel(buttonPanel, addButton, cancelButton); 
			
			contentPane.add(buttonPanel, BorderLayout.SOUTH);
			
			for (Component c:otherInfoPane.getComponents())
				c.setEnabled(false);
			for (Component c:singerPaneTop.getComponents())
				c.setEnabled(false);
			for (Component c:singerPaneBottom.getComponents())
				c.setEnabled(false);
			for (Component c:songPaneTop.getComponents())
				c.setEnabled(false);
			for (Component c:songPaneBottom.getComponents())
				c.setEnabled(false);
			addButton.setEnabled(false);
			
			// add listeners
			
			// when confirm checkbox is selected, check the upc, enable the relevant fields
			confirmUPC.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					if (confirmUPC.isSelected()) {
						if (upcField.getText().trim().length()==0) {
							popUpErrorMessage("UPC cannot be empty!");
							confirmUPC.setSelected(false);
						} else {
							int upc = Integer.parseInt(upcField.getText().trim());
							// if upc is already existed
							if (managerModel.isUPCExisted(upc)) {
								for (Component c: otherInfoPane.getComponents()) {
									if (c instanceof JLabel) {
										JLabel label = (JLabel) c;
										if (label.getText().equals("Enter price"+": ") 
											|| label.getText().equals("Enter quantity"+": "))
											c.setEnabled(true);
									}
								}
								priceField.setEnabled(true);
								quantityField.setEnabled(true);
								upcField.setEnabled(false);
								upcExisted = true;
							} else { // if upc is not existed
								upcExisted = false;
								for (Component c:otherInfoPane.getComponents())
									c.setEnabled(true);
								for (Component c:singerPaneTop.getComponents())
									c.setEnabled(true);
								for (Component c:singerPaneBottom.getComponents())
									c.setEnabled(true);
								for (Component c:songPaneTop.getComponents())
									c.setEnabled(true);
								for (Component c:songPaneBottom.getComponents())
									c.setEnabled(true);
								upcField.setEnabled(false);
							}
						}
						
						addButton.setEnabled(true);
					} else { // if the confirm checkbox is unselected
						for (Component c:otherInfoPane.getComponents())
							c.setEnabled(false);
						for (Component c:singerPaneTop.getComponents())
							c.setEnabled(false);
						for (Component c:singerPaneBottom.getComponents())
							c.setEnabled(false);
						for (Component c:songPaneTop.getComponents())
							c.setEnabled(false);
						for (Component c:songPaneBottom.getComponents())
							c.setEnabled(false);
						upcField.setEnabled(true);
						addButton.setEnabled(false);
					} // end of if else
				}// end of itemStateChanged
			});
			
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
					dispose();
				}
			});
			
			addSingerButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTextField singerField = new JTextField(10);
					dialogHelper.addComponentsToPanel(singerPaneBottom, "Singer name", singerField);
					singerFieldList.add(singerField);
					pack();
				}
			});
			
			addSongButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTextField songField = new JTextField(10);
					dialogHelper2.addComponentsToPanel(songPaneBottom, "Song title", songField);
					songFieldList.add(songField);
					pack();
				}
			});
			
			// when the add button is clicked, check the validity of inputs and write to database
			addButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (upcExisted) { // only check the stock field
						if (quantityField.getText().trim().length()==0 
							&& priceField.getText().trim().length()==0) {
							popUpErrorMessage("Price and quantity can't be both empty!");
						} else {
							
							if (priceField.getText().trim().length()==0) {
								price = null;
							} else {
								price = Double.parseDouble(priceField.getText().trim());
							}
							
							if (quantityField.getText().trim().length()==0) {
								quantity = null;
							} else {
								quantity = Integer.parseInt(quantityField.getText().trim());
							}
							// write to the database
							int upc = Integer.parseInt(upcField.getText().trim() );
							if (managerModel.updateExistingItem(upc, price, quantity)) {
								popUpOKMessage("Update the item information successfully!");
								mainGui.updateStatusBar("Update the item" +"(upc=" + upc + ") "
										+ "successfully!");
//								quantityField.setText("");
//								priceField.setText("");
//								upcField.setText("");
//								for (Component c:otherInfoPane.getComponents())
//									c.setEnabled(false);
//								for (Component c:singerPaneTop.getComponents())
//									c.setEnabled(false);
//								for (Component c:singerPaneBottom.getComponents())
//									c.setEnabled(false);
//								for (Component c:songPaneTop.getComponents())
//									c.setEnabled(false);
//								for (Component c:songPaneBottom.getComponents())
//									c.setEnabled(false);
//								upcField.setEnabled(true);
//								addButton.setEnabled(false);
//								confirmUPC.setSelected(false);
								dispose();
							} else {
								popUpErrorMessage("Failed to update the item information!");
								mainGui.updateStatusBar("Failed to update the item" +"(upc=" + upc + "). ");
							}
							
						}
						
					} else { // upc is not existed, check all fields
						if (titleField.getText().trim().length()==0) {
							popUpErrorMessage("Title cannot be empty!");
						} else if (companyField.getText().trim().length()==0) {
							popUpErrorMessage("Company cannot be empty!");
						} else if (yearField.getText().trim().length()==0) {
							popUpErrorMessage("Year cannot be empty!");
						} else if (priceField.getText().trim().length()==0) {
							popUpErrorMessage("Price cannot be empty!");
						} else if (quantityField.getText().trim().length()==0) {
							popUpErrorMessage("Quantity cannot be empty!");
						} else if (singerFieldList.get(0).getText().trim().length()==0) {
							popUpErrorMessage("The first singer name cannot be empty!");
						} else if (songFieldList.get(0).getText().trim().length()==0) {
							popUpErrorMessage("The first song title cannot be empty!");
						} else { // every input is valid and process those inputs
							
							int upc = Integer.parseInt(upcField.getText().trim() );
							String title = titleField.getText().trim();
							String type = allowedTypes[typeComboBox.getSelectedIndex()];
							String category = allowedCategories[CategoryComboBox.getSelectedIndex()];
							String company = companyField.getText().trim();
							String year = yearField.getText().trim();
							double price = Double.parseDouble(priceField.getText().trim());
							int quantity = Integer.parseInt(quantityField.getText().trim());
							ArrayList<String> singerList = new ArrayList<String>();
							for (JTextField field:singerFieldList) {
								if (field.getText().trim().length()!=0) {
									singerList.add(field.getText().trim());
								}								
							}
							ArrayList<String> songList = new ArrayList<String>();
							for (JTextField field:songFieldList) {
								if (field.getText().trim().length()!=0) {
									songList.add(field.getText().trim());
								}								
							}

							// write to the database
							if (managerModel.addNewItem(upc, title, type, category, company,
					                  year, price, quantity, singerList,songList)==true) {
								popUpOKMessage("Add the new item successfully!");
								mainGui.updateStatusBar("Add the new item" +"(upc=" + upc + ") "
										+ "successfully!");
//								quantityField.setText("");
//								priceField.setText("");
//								upcField.setText("");
//								companyField.setText("");
//								yearField.setText("");
//								titleField.setText("");
//								for (JTextField singerField: singerFieldList){
//									singerField.setText("");
//								}
//								for (JTextField songField: songFieldList){
//									songField.setText("");
//								}
//								for (Component c:otherInfoPane.getComponents())
//									c.setEnabled(false);
//								for (Component c:singerPaneTop.getComponents())
//									c.setEnabled(false);
//								for (Component c:singerPaneBottom.getComponents())
//									c.setEnabled(false);
//								for (Component c:songPaneTop.getComponents())
//									c.setEnabled(false);
//								for (Component c:songPaneBottom.getComponents())
//									c.setEnabled(false);
//								upcField.setEnabled(true);
//								addButton.setEnabled(false);
//								confirmUPC.setSelected(false);
								dispose();
							} else {
								popUpErrorMessage("Failed to add the new item!");
								mainGui.updateStatusBar("Failed to add the new item" +"(upc=" + upc + "). ");
							}
						}
						
					}
					
				}
			});
			
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
		JTextField receiptIdField = new JTextField(4);
		JCheckBox showPurchase = new JCheckBox("show the purchase");
		JTextField deliveryDateField = new JTextField(10);
		JPanel purchasePane;
		JPanel purchaseItemPane;
		
		public ProcessDeliveryDialog(ShopGUI shopGUI) {
			// TODO
			super(shopGUI, "Set delivery date", true);
			//setResizable(false);	
			setSize(500, 500);
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);			
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			DialogHelper dialogHelper = new DialogHelper();
			JPanel receiptIdPane = dialogHelper.createInputPane("");
			dialogHelper.addComponentsToPanel(receiptIdPane, "Enter receipt id", receiptIdField, showPurchase);
			purchasePane = dialogHelper.createInputPane("Purchase");
			purchaseItemPane = dialogHelper.createInputPane("Purchase items");
			JPanel topPane = new JPanel(new BorderLayout());
			topPane.add(receiptIdPane, BorderLayout.NORTH);
			topPane.add(purchasePane, BorderLayout.CENTER);
			topPane.add(purchaseItemPane, BorderLayout.SOUTH);
			
			contentPane.add(topPane, BorderLayout.NORTH);
			
			final JPanel setDatePane = dialogHelper.createInputPane("Set delivery date (default to the current date)");
			DialogHelper dialogHelper2 = new DialogHelper();
			dialogHelper2.addComponentsToPanel(setDatePane, "Enter date (yyyy-mm-dd)", deliveryDateField);
			
			contentPane.add(setDatePane, BorderLayout.CENTER);
			
			// pane to hold the register and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton setButton = new JButton("Set delivery date");				
			JButton cancelButton = new JButton("Cancel");		
			dialogHelper.addComponentsToPanel(buttonPanel, setButton, cancelButton);
			
			contentPane.add(buttonPanel,BorderLayout.SOUTH);
			
			// disable user input
			for (Component c:setDatePane.getComponents())
				c.setEnabled(false);
			setButton.setEnabled(false);
			
			// add listeners
			showPurchase.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					// TODO Auto-generated method stub
					if (showPurchase.isSelected()) {
						
						// check the validity of receiptId
						if (receiptIdField.getText().trim().length()==0) {
							popUpErrorMessage("Receipt id can't be empty!");
							receiptIdField.requestFocus();
							receiptIdField.selectAll();
							// disable user input
							for (Component c:setDatePane.getComponents())
								c.setEnabled(false);
							setButton.setEnabled(false);
							showPurchase.setSelected(false);
						} else { // check the existence of receiptId
							int inputReceiptId = Integer.parseInt(receiptIdField.getText().trim());
							if (!managerModel.isReceiptIdExisted(inputReceiptId)) {
								popUpErrorMessage("Receipt id doesn't exist!");
								receiptIdField.requestFocus();
								receiptIdField.selectAll();
								// disable user input
								for (Component c:setDatePane.getComponents())
									c.setEnabled(false);
								setButton.setEnabled(false);
								receiptIdField.setEnabled(false);
								// update the content of purchase pane and purchaseItem pane
								showReceiptContents();
							} else { // receipt id existed, now check if it is online order
								if (managerModel.isOnlineOrder(inputReceiptId)) {
									for (Component c:setDatePane.getComponents())
										c.setEnabled(true);
									setButton.setEnabled(true);
									
								} 
								receiptIdField.setEnabled(false);
								// update the content of purchase pane and purchaseItem pane	
								showReceiptContents();
							}
							
						} 
							
					} else {
						receiptIdField.setEnabled(true);
						purchasePane.removeAll();
						purchaseItemPane.removeAll();
						// disable user input
						for (Component c:setDatePane.getComponents())
							c.setEnabled(false);
						setButton.setEnabled(false);
					}
						
					
				}
			});
			
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					dispose();
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
				}
			});
			
			// set button to set the delivery date for an online order
			setButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					String date;
					int receiptId =Integer.parseInt( receiptIdField.getText().trim() );
					
					if (deliveryDateField.getText().trim().length()==0) {
						date = getCurrentDate("yyyy-MM-dd");
					} else {
						date = deliveryDateField.getText().trim();
					}
					
					if (managerModel.setDeliveryDate(receiptId, date)==true) {
						popUpOKMessage("Set the delivered date successfully!");
						mainGui.updateStatusBar("Delivered date is set for the oneline purchase" +
								" with receipt id = " + receiptId + ".");
						dispose();
					} else {
						popUpOKMessage("Failed to set the delivered date successfully!");
						mainGui.updateStatusBar("Failed to set the delivered date for the oneline purchase" +
								" with receipt id = " + receiptId + ".");
						showPurchase.setSelected(false);
					}
				}
			});
			
		}
		
		private void showReceiptContents() {
			int inputReceiptId = Integer.parseInt(receiptIdField.getText().trim());
			// create a table of the purchase associated with the receipt id
			purchasePane.removeAll();
			String[] columnLabels = {"receipt id", "purchase date", "cid", "card number", "expiry date",
	                 "expected delivery date", "delivery date"};
			ArrayList< ArrayList<String> > rowList = new ArrayList< ArrayList<String> >();
			
			ResultSet rs = managerModel.getPurchase(inputReceiptId);
			// check if rs is empty
			try {
				if (rs.isBeforeFirst()) {

					try {
						while (rs.next()) {
							ArrayList<String> oneRow = new ArrayList<String>();
							int receiptId = rs.getInt("receiptID");										
							String receiptIdString = Integer.toString(receiptId);
							oneRow.add(receiptIdString);
							
							java.sql.Date purchaseDate = rs.getDate("Pdate");	
							String purchaseDateString = convertSqlDateToString(purchaseDate, "yyyy-MM-dd");
							oneRow.add(purchaseDateString);
							
							String cid = rs.getString("cid");
							if (rs.wasNull())
								cid = "null";
							oneRow.add(cid);
							
							String cardNo = rs.getString("cardN");
							if (rs.wasNull())
								cardNo = "null";
							oneRow.add(cardNo);
							
							String expiryDate = rs.getString("expiryDate");
							if (rs.wasNull())
								expiryDate = "null";
							oneRow.add(expiryDate);
							
							java.sql.Date expectedDate = rs.getDate("expectedDate");
							String expectedDateString;
							if (rs.wasNull()) {
								expectedDateString = "null";
							} else {
								expectedDateString = convertSqlDateToString(expectedDate, "yyyy-MM-dd");
							}
							oneRow.add(expectedDateString);
							
							java.sql.Date deliveredDate = rs.getDate("deliveredDate");	
							String deliveredDateString;
							if (rs.wasNull()) {
								deliveredDateString = "null";
							} else {
								deliveredDateString = convertSqlDateToString(deliveredDate, "yyyy-MM-dd");
							}
							oneRow.add(deliveredDateString);
							
							rowList.add(oneRow);
						}
					
						DialogHelper dialogHelper = new DialogHelper();
						// add the table to the gui
						dialogHelper.addOneTableToPanel(purchasePane, columnLabels,rowList); 
					} catch (SQLException ex) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						mainGui.updateStatusBar(ex.getMessage());
					} // end of try catch block

				}
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				mainGui.updateStatusBar(ex.getMessage());
				//ex.printStackTrace();
			}
			
			purchaseItemPane.removeAll();
			// create a table of all purchase item associated with the receipt id
			String[] columnLabels2 = {"receipt id", 
	                 "UPC", "purchase quantity"};
	                 
			ArrayList< ArrayList<String> > rowList2 = new ArrayList< ArrayList<String> >();
			
			ResultSet rs2 = managerModel.getPurchaseItem(inputReceiptId);
			// check if rs is empty
			try {
				if (rs2.isBeforeFirst()) {

					try {
						while (rs2.next()) {
							ArrayList<String> oneRow = new ArrayList<String>();
							int receiptId = rs2.getInt("receiptId");										
							String receiptIdString = Integer.toString(receiptId);
							oneRow.add(receiptIdString);
							int upc = rs2.getInt("upc");										
							String upcString = Integer.toString(upc);
							oneRow.add(upcString);
							int purchaseQuantity = rs2.getInt("quantity");
							String quantityString = Integer.toString(purchaseQuantity);
							oneRow.add(quantityString);
							rowList2.add(oneRow);
						}
					
						DialogHelper dialogHelper = new DialogHelper();
						// add the table to the gui
						dialogHelper.addOneTableToPanel(purchaseItemPane, columnLabels2,rowList2); 
					} catch (SQLException ex) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						mainGui.updateStatusBar(ex.getMessage());
					} // end of try catch block

				}
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				mainGui.updateStatusBar(ex.getMessage());
				//ex.printStackTrace();
			}
			pack();
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
		JTextField dateField = new JTextField(10); 
		JCheckBox showReport = new JCheckBox("show daily report");
		
		public GenerateDailyReportDialog(ShopGUI shopGUI) {
			super(shopGUI, "Generate daily sales report", true);
			//setResizable(false);	
			//setSize(500, 500);
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);			
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			DialogHelper dialogHelper = new DialogHelper();
			JPanel datePane = dialogHelper.createInputPane("");
			dialogHelper.addComponentsToPanel(datePane, "Enter date (yyyy-mm-dd)", dateField, showReport);
			
			final JPanel reportPane = dialogHelper.createInputPane("");
			

			
			contentPane.add(datePane,BorderLayout.NORTH);
			contentPane.add(reportPane,BorderLayout.CENTER);
			
			// when the show daily report checkbox is selected, check the date input and generate the report
			showReport.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					if (showReport.isSelected()) {
						if (dateField.getText().trim().length()==0) {
							popUpErrorMessage("Date can't be empty!");
							showReport.setSelected(false);
						} else { // we assume the date is in good format
							String date = dateField.getText().trim();
							ArrayList< ArrayList<String> > table = managerModel.getDailyReport(date);
							String[] columnLabels = {"UPC", "Category", "Unit Price", "Units",
					                 "Total Value"};
							DialogHelper dialogHelper = new DialogHelper();
							dialogHelper.addOneTableToPanel(reportPane, columnLabels, table);
							dateField.setEnabled(false);
							pack();
						}
					} else {
						dateField.setEnabled(true);
						reportPane.removeAll();
						pack();
					}
					
				}
			});

			
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
		if (actionCommand.equals(ShopGUI.ADD_ITEM_TO_STORE)) {
			mainGui.updateStatusBar("PROCESS A PURCHASE .......");
			AddItemToStoreDialog dialog = new AddItemToStoreDialog(mainGui);
			dialog.pack();
			mainGui.centerWindow(dialog);
			dialog.setVisible(true);
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_DELIVERY)) {
			mainGui.updateStatusBar("SET DELIVERY DATE .......");
			ProcessDeliveryDialog dialog = new ProcessDeliveryDialog(mainGui);
			dialog.pack();
			mainGui.centerWindow(dialog);
			dialog.setVisible(true);
			return; 
		} else if (actionCommand.equals(ShopGUI.GENERATE_DAILY_REPORT)) {
			mainGui.updateStatusBar("GENERATE DAILY SALE REPORT .......");
			GenerateDailyReportDialog dialog = new GenerateDailyReportDialog(mainGui);
			dialog.pack();
			mainGui.centerWindow(dialog);
			dialog.setVisible(true);
			return; 
		} else if (actionCommand.equals(ShopGUI.SHOW_TOP_SELLING_ITEMS)) {
			mainGui.updateStatusBar("SHOW TOP SELLING ITEMS .......");
			ShowTopSellingItemsDialog dialog = new ShowTopSellingItemsDialog(mainGui);
			dialog.pack();
			mainGui.centerWindow(dialog);
			dialog.setVisible(true);
			return; 
		}
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
	
	// get the current date in format
	// format is usually "yyyy-MM-dd"
	private String getCurrentDate(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		// get the current date
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	// convert sql date to string in the format of "yyyy-MM-dd"
	private String convertSqlDateToString(java.sql.Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);		
		return dateFormat.format((java.util.Date) date);
	}
	
}