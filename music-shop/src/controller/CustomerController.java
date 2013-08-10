package controller;

import gui_helper.DialogHelper;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.ClerkModel;
import model.CustomerModel;

import org.omg.CORBA.portable.CustomValue;

import controller.ClerkController.ProcessPurchaseDialog;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;
import view.ShopGUI;

import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;


/*
 * CustomerController is a control class that handles action events on the Clerk menu.
 * It updates the GUI based on which menu item the clerk selected and also use methods
 * in CustomerModel to update the tables in database
 */

public class CustomerController implements ActionListener, ExceptionListener {

	private ShopGUI mainGui = null;
	private CustomerModel customerModel = null;
	private DialogHelper dialogHelper = null;
	
	public CustomerController(ShopGUI sg) {
		mainGui = sg;
		customerModel = new CustomerModel();
		// add this to the listener list of the customer model
		customerModel.addExceptionListener(this);
		dialogHelper = new DialogHelper();
	}
	
	
	


	/**
	 * generate a dialog to allow the customer to register
	 *
	 */
	class RegistrationDialog extends JDialog implements ActionListener {
		final JTextField nameField = new JTextField(10);
		final JTextField addressField = new JTextField(10);
		final JTextField phoneField = new JTextField(10);
		final JTextField usernameField = new JTextField(10);
		final JPasswordField passwordField1 = new JPasswordField(10);
		final JPasswordField passwordField2 = new JPasswordField(10);
		
		public RegistrationDialog(ShopGUI mainGUI) {
			// TODO
			super(mainGUI, "Customer registration", true);
			//setResizable(false);
			final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
			numberFormatter.setMinimumFractionDigits(2);
			numberFormatter.setMaximumFractionDigits(2);
			
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			// inputpane to allow the user to input username and password
			JPanel inputPane = dialogHelper.createInputPane("");
			// add username field and password field into the input pane
			dialogHelper.addComponentsToPanel(inputPane, "Enter name: ", nameField);
			dialogHelper.addComponentsToPanel(inputPane, "Enter address: ", addressField);
			dialogHelper.addComponentsToPanel(inputPane, "Enter phone: ", phoneField);
			dialogHelper.addComponentsToPanel(inputPane, "Enter username: ", usernameField);
			dialogHelper.addComponentsToPanel(inputPane, "Enter password: ", passwordField1);
			dialogHelper.addComponentsToPanel(inputPane, "Enter password again: ", passwordField2);
			passwordField1.setEchoChar('*');
			passwordField1.setEchoChar('*');
			
			contentPane.add(inputPane, BorderLayout.CENTER);
			
			// pane to hold the register and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton registerButton = new JButton("Register");			
			JButton cancelButton = new JButton("Cancel");			
			dialogHelper.addComponentsToPanel(buttonPanel, registerButton, cancelButton); 
			
			contentPane.add(buttonPanel, BorderLayout.SOUTH);
			
			// add listeners
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
			
			//when the focus is in username field, change the name, address and phone
			// to make sure that they are not empty
			usernameField.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
					if (nameField.getText().trim().length()==0) {
						nameField.requestFocus();
						popUpErrorMessage("Name cannot be empty!");
						return;
					} 
					
					if (addressField.getText().trim().length()==0) {
						addressField.requestFocus();
						popUpErrorMessage("Address cannot be empty!");
						return;
					} 
					
					if (phoneField.getText().trim().length()==0) {
						phoneField.requestFocus();
						popUpErrorMessage("Phone cannot be empty!");
						return;
					} 
				}
			});
			
			// once the focus is in password field, check all the field above it
			passwordField1.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
					String username = usernameField.getText().trim();
				
					if (nameField.getText().trim().length()==0) {
						nameField.requestFocus();
						popUpErrorMessage("Name cannot be empty!");
						return;
					} 
					
					if (addressField.getText().trim().length()==0) {
						addressField.requestFocus();
						popUpErrorMessage("Address cannot be empty!");
						return;
					} 
					
					if (phoneField.getText().trim().length()==0) {
						phoneField.requestFocus();
						popUpErrorMessage("Phone cannot be empty!");
						return;
					} 
					
					if (username.length()==0) {
						usernameField.requestFocus();
						popUpErrorMessage("Username cannot be empty!");
						
					} else { 
						if (isUsernameAlreadyExisted(username)) {
							usernameField.requestFocus();
							popUpErrorMessage("Username already exists! Try a different one.");
							
						}
					} 
				}
			}); // end of passwordField1.addFocusListener
			
		// once the focus is in password field, check all the fields above it	
		passwordField2.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void focusGained(FocusEvent arg0) {
					// TODO Auto-generated method stub
					if (nameField.getText().trim().length()==0) {
						nameField.requestFocus();
						popUpErrorMessage("Name cannot be empty!");
						return;
					} 
					
					if (addressField.getText().trim().length()==0) {
						addressField.requestFocus();
						popUpErrorMessage("Address cannot be empty!");
						return;
					} 
					
					if (phoneField.getText().trim().length()==0) {
						phoneField.requestFocus();
						popUpErrorMessage("Phone cannot be empty!");
						return;
					} 
					
					String username = usernameField.getText().trim();
				
					if (username.length()==0) {
						usernameField.requestFocus();
						popUpErrorMessage("Username cannot be empty!");
						
					} else { 
						if (isUsernameAlreadyExisted(username)) {
							usernameField.requestFocus();
							popUpErrorMessage("Username already exists! Try a different one.");
							
						}
					} 
					
					if (passwordField1.getText().trim().length()==0) {
						passwordField1.requestFocus();
						popUpErrorMessage("The first password field cannot be empty!");
						return;
					} 
					
				}
			}); // end of passwordField2.addFocusListener
			
		// allow user to register and check all fields for validity
		registerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (nameField.getText().trim().length()==0) {
					nameField.requestFocus();
					popUpErrorMessage("Name cannot be empty!");
					return;
				} 
				
				if (addressField.getText().trim().length()==0) {
					addressField.requestFocus();
					popUpErrorMessage("Address cannot be empty!");
					return;
				} 
				
				if (phoneField.getText().trim().length()==0) {
					phoneField.requestFocus();
					popUpErrorMessage("Phone cannot be empty!");
					return;
				} 
				
				String username = usernameField.getText().trim();
			
				if (username.length()==0) {
					usernameField.requestFocus();
					popUpErrorMessage("Username cannot be empty!");
					
				} else { 
					if (isUsernameAlreadyExisted(username)) {
						usernameField.requestFocus();
						popUpErrorMessage("Username already exists! Try a different one.");
						
					}
				} 
				
				if (passwordField1.getText().trim().length()==0) {
					passwordField1.requestFocus();
					popUpErrorMessage("The first password field cannot be empty!");
					return;
				} 
				
				if (passwordField2.getText().trim().length()==0) {
					passwordField2.requestFocus();
					popUpErrorMessage("The second password field cannot be empty!");
					return;
				} 
				
				@SuppressWarnings("deprecation")
				String password1 = passwordField1.getText().trim();
				@SuppressWarnings("deprecation")
				String password2 = passwordField2.getText().trim();
				if (!password1.equals(password2)) {
					popUpErrorMessage("The two passwords don't match!");
					passwordField1.setText("");
					passwordField2.setText("");
				} else { // create a customer tuple and write to the database
					String name = nameField.getText().trim();
					String address = addressField.getText().trim();
					String phone = phoneField.getText().trim();
					username = usernameField.getText().trim();
					
					if (customerModel.insertCustomer(username, password1, name, address, phone)) {
						popUpOKMessage("Registration is successful!");
						mainGui.updateStatusBar("Registration is successful!\n");
						dispose();
					} else {
						popUpErrorMessage("Registration is failed!");
						mainGui.updateStatusBar("Registration is failed!\n");
						nameField.setText("");
						addressField.setText("");
						phoneField.setText("");
						usernameField.setText("");
						passwordField1.setText("");
						passwordField2.setText("");
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


	
	// return true if a nonempty username is not existed in database
	// otherwise return false 
	private boolean isUsernameAlreadyExisted(String username) {

		return customerModel.queryUsername(username);
	}
	
	@Override
	public void exceptionGenerated(ExceptionEvent ex) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * generate a dialog to allow the customer to go shopping (including login,
	 * search items, add items to cart, remove items from cart, check out)
	 *
	 */
	class GoShoppingDialog extends JDialog implements ActionListener {
		final JTextField usernameField = new JTextField(10);
		final JPasswordField passwordField = new JPasswordField(10);
		final JButton loginButton = new JButton("Login");
		final JTextField categoryField = new JTextField(10);
		final JTextField titleField = new JTextField(20);
		final JTextField singerField = new JTextField(10);
		final JButton searchButton = new JButton("search");
		ResultSet searchResult = null;
		String cid = null; // to record the username
		// to record the upc of items that are already in cart
		ArrayList<Integer> itemsInCart =  new ArrayList<Integer>();
		// record a selection of items (upc) through the search result dialog
		ArrayList<Integer> itemsSelection =  new ArrayList<Integer>();
		JPanel cartPane = null;
		// quantity fields in the cart panel
		ArrayList<JTextField> quantityFieldList = new ArrayList<JTextField>();
		// mapping <upc, quantity>, use LinkedHashMap to keep insertion order
		LinkedHashMap<Integer, Integer> shoppingCart = new LinkedHashMap<Integer, Integer>();
		// to save the upcs for the items in the shopping cart
		//ArrayList<Integer> upcList = new ArrayList<Integer>();
		// associate the quantity text field with the corresponding upc
		LinkedHashMap<JTextField, Integer> quantityFieldUPC = new LinkedHashMap<JTextField, Integer>();
		JTextField cardNumberField; 
		JTextField expiryDateField;
		
		
		
		
		public GoShoppingDialog(ShopGUI mainGUI) {
			// TODO Auto-generated constructor stub
			super(mainGUI, "Customer registration", true);
			//setResizable(false);
			// for formating the floats
			final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
			numberFormatter.setMinimumFractionDigits(2);
			numberFormatter.setMaximumFractionDigits(2);
			
			final JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			// the pane to show login information, contain username 
			final JPanel loginPane = dialogHelper.createInputPane("User login");
			dialogHelper.addComponentsToPanel(loginPane, "username", usernameField, 
					                          "password", passwordField, loginButton);
			
			// the pane allow the users to search and add items to shopping cart
			final JPanel searchPane = dialogHelper.createInputPane("Search items");			
			dialogHelper.addComponentsToPanel(searchPane, "category", categoryField, 
                    "title", titleField, "singer", singerField, searchButton);
			// disable all components in the search pane because the user has not logged in
			for (Component c : searchPane.getComponents()) 
				c.setEnabled(false);
			
			// show the contents of the shopping cart and also allow the user to change
			// the contents of the shopping cart
			cartPane = dialogHelper.createInputPane("Shopping cart");
			
			// the pane to allow the user to check out
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton checkOutButton = new JButton("Check out");
			checkOutButton.setEnabled(false);
			JButton cancelButton = new JButton("Cancel");			
			dialogHelper.addComponentsToPanel(buttonPane, checkOutButton, cancelButton);
			
			contentPane.add(loginPane, BorderLayout.NORTH);
			JPanel centerPane = new JPanel(new BorderLayout());
			centerPane.add(searchPane, BorderLayout.NORTH);
			centerPane.add(cartPane, BorderLayout.CENTER);
			
			final JPanel paymentPanel = dialogHelper.createInputPane("Payment");
			cardNumberField = new JTextField(12);
			expiryDateField = new JTextField(10);
			dialogHelper.addComponentsToPanel(paymentPanel, "card number (16-digits)", cardNumberField);
			dialogHelper.addComponentsToPanel(paymentPanel, "expiry date (yyyy-mm)", expiryDateField);
			
			final JCheckBox confirmCheckbox = new JCheckBox("Confirm purchase");
			JPanel bottomPane = new JPanel(new BorderLayout());
			bottomPane.add(confirmCheckbox, BorderLayout.NORTH);
			bottomPane.add(buttonPane, BorderLayout.SOUTH);
			
			centerPane.add(paymentPanel, BorderLayout.SOUTH);
			
			contentPane.add(centerPane, BorderLayout.CENTER);
			contentPane.add(bottomPane, BorderLayout.SOUTH);

			
			// inner class to create a dialogue to hold the search result
			class SearchResultDialog extends JDialog {
				ResultSet rs;
				ArrayList<Integer> selectedUPCs = new ArrayList<Integer>();
				ArrayList<Integer> upcList = new ArrayList<Integer>();
				ArrayList<JCheckBox> checkboxList = new ArrayList<JCheckBox>();
				
				public SearchResultDialog(JDialog parentDialog, ResultSet resultSet) {
					super(parentDialog, "Search results", true);
					rs = resultSet;
					//setResizable(false);
					//setSize(200,200);
					final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
					numberFormatter.setMinimumFractionDigits(2);
					numberFormatter.setMaximumFractionDigits(2);
					
					JPanel contentPane = new JPanel(new BorderLayout());
					setContentPane(contentPane);
					contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
					JPanel resultPane = dialogHelper.createInputPane("Matching items");
							
					final JButton addButton = new JButton("Add to shopping cart");
					
					contentPane.add(resultPane, BorderLayout.CENTER);
					contentPane.add(addButton, BorderLayout.SOUTH);
					
					// create a table of all purchase item associated with the receipt id
					String[] columnLabels = {"select", 
			                 "UPC", "title", "leading singers", 
			                 "category", "price"};
					ArrayList< ArrayList<String> > rowList = new ArrayList< ArrayList<String> >();
					try {
						while (rs.next()) {
							ArrayList<String> oneRow = new ArrayList<String>();
							int upc = rs.getInt("upc");
							upcList.add(upc);
							String upcString = Integer.toString(upc);
							oneRow.add(upcString);
							String title = rs.getString("title");
							oneRow.add(title);
							String singerName = rs.getString("singers");
							oneRow.add(singerName);
							String category = rs.getString("category");
							oneRow.add(category);
							double price = rs.getDouble("price");
							String priceString = Double.toString( price );
							oneRow.add(priceString);
							rowList.add(oneRow);
							JCheckBox cb = new JCheckBox();
							checkboxList.add(cb);
						}
						rs.close();
						// add the table to the gui
						dialogHelper.addOneTableToPanel2(resultPane, columnLabels,
								        checkboxList, rowList); 
						//repaint();
					} catch (SQLException ex) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						mainGui.updateStatusBar(ex.getMessage());
					} // 
					
					// when user click the add item button
					// pass the list of selected item to the go shopping dialog
					// and close the current search result dialog
					addButton.addActionListener(new ActionListener() {						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							itemsSelection = getSelectedItems();
							// update the items in the shopping cart
							updateCartPanel();
							GoShoppingDialog.this.pack();
							dispose();
						}
					});
				}
				
				public ArrayList<Integer> getSelectedItems() {
					for (int i=0; i<upcList.size(); ++i) {
						if (checkboxList.get(i).isSelected()) {
							selectedUPCs.add(upcList.get(i));
						}
					}
					return selectedUPCs;
				}
				
			}
			
			checkOutButton.addActionListener(this);
			
			// add listeners
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
			
			// when the login button is pressed, check the username, password and 
			// allow the user in and enable the components in search panel
			loginButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (usernameField.getText().trim().length()==0) {
						popUpErrorMessage("Username is empty!");
						usernameField.requestFocus();
					} else if (passwordField.getText().trim().length()==0) {
						popUpErrorMessage("Password is empty!");
						passwordField.requestFocus();
					} else {
						String username = usernameField.getText().trim();
						String password = passwordField.getText().trim();
						// check the username and password
						if (customerModel.queryUsernamePassword(username, password)) {
							cid = username;			
							String info = "Welcome " + cid + "! You have successfully logged in!";
							popUpOKMessage(info);
							// disable all components in the login pane
							for (Component c : loginPane.getComponents()) 
								c.setEnabled(false);
							// enable all components in the search pane
							for (Component c : searchPane.getComponents()) 
								c.setEnabled(true);
							
							repaint();
						} else {
							popUpErrorMessage("Failed to log in!");
							usernameField.setText("");
							passwordField.setText("");
						}
					}
				}
			});
			
			// when the search button is pressed, check all three fields are not empty
			// and create a new windows shows all matched items
			searchButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (categoryField.getText().trim().length()==0 
					    && titleField.getText().trim().length()==0
					    && singerField.getText().trim().length()==0) {
						
						popUpErrorMessage("All three fields are empty!");
						categoryField.requestFocus();
					} else {
						// search itms in the database
						String category = categoryField.getText().trim();
						String title = titleField.getText().trim();
						String singerName = singerField.getText().trim();
						searchResult = customerModel.searchItem(category, title, singerName);
						SearchResultDialog searchResultDialog = 
								new SearchResultDialog(GoShoppingDialog.this, searchResult);
						searchResultDialog.pack();
						mainGui.centerWindow(searchResultDialog);
						searchResultDialog.setVisible(true);
						return;
					}
				}
			});
			
			// check all inputs are reasonable and print a receipt in a new window
			confirmCheckbox.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent ie) {
					// TODO Auto-generated method stub
					JCheckBox cb = (JCheckBox) ie.getItem();
					if (cb.isSelected()) {
						//check card number and expiery date are not empty
						String cardNumberString = cardNumberField.getText().trim();
						String expiryDateString = expiryDateField.getText().trim();
						if (cardNumberString.length()==0) {
							popUpErrorMessage("Card number is empty!");
						} else if (expiryDateString.length()==0) {
							popUpErrorMessage("Expiry date is empty!");
						} else { // check all purchase quantity is reasonable
							if (checkQuantityValidity()==true) {
								//lock all input fields
								for (Component c : searchPane.getComponents()) 
									c.setEnabled(false);
								for (Component c : cartPane.getComponents()) 
									c.setEnabled(false);
								for (Component c : paymentPanel.getComponents()) 
									c.setEnabled(false);
								
								checkOutButton.setEnabled(true);
							} else {
								confirmCheckbox.setSelected(false);
							}
						}
					} else {
						//enable the relevant text fields and buttons
						//lock all input fields
						for (Component c : searchPane.getComponents()) 
							c.setEnabled(true);
						for (Component c : cartPane.getComponents()) 
							c.setEnabled(true);
						for (Component c : paymentPanel.getComponents()) 
							c.setEnabled(true);
						
						checkOutButton.setEnabled(false);
					}
				}
				
			});
			
			
		}

		// process the purchase
		
		
		// check the values inside the quantity fields are valid
		private boolean checkQuantityValidity() {
			for (int i=0; i<quantityFieldList.size(); ++i) {
				JTextField field = quantityFieldList.get(i);
				int upc = quantityFieldUPC.get(field);
				int newQuantity = 0;
				if (field.getText().trim().length()!=0) {
					newQuantity = Integer.parseInt(field.getText().trim());
				}
				
				if (newQuantity>customerModel.queryItemQuantity(upc)) {
					popUpErrorMessage("Purchase quantity for item with upc=" + upc
							+ " is larger than stock quantity!");
					field.requestFocus();
					field.selectAll();
					return false;
				} else {
					//update the shopping cart quantity
					shoppingCart.put(upc, newQuantity);
				}
			}
			return true;
		}
		
		// updates the content of shopping cart and show the shopping cart
		private void updateCartPanel() {
			/* update the shopping cart according to itemsSelection 
			 */
			for (int i=0; i<itemsSelection.size(); ++i) {
				int upc = itemsSelection.get(i);
				// if the item is already in the cart, add the quantity of the item by 1
				if (shoppingCart.keySet().contains(upc)) {
					int newQuantity = shoppingCart.get(upc) + 1;
					// if the stock is less than the new quantity
					if (customerModel.queryItemQuantity(upc)<newQuantity) {
						popUpErrorMessage("You can't add the item with upc=" + upc 
								+ " because the new purchase quantity=" + newQuantity
								+ " exceeds the stock quantity!");
					} else {
						shoppingCart.put(upc, newQuantity);
					}
					
				} else { // if the item is not in the shopping cart, add it to shopping cart
					shoppingCart.put(upc, 1);
				}
			}
			// redrawing the content of the shopping cart in cart pane
			cartPane.removeAll();
			quantityFieldUPC.clear();
			// clean up quantity field list
			quantityFieldList = new ArrayList<JTextField>();
			// create a table of all purchase item associated with the receipt id
			String[] columnLabels = {"purchase quantity", 
					"upc", "title", "category", "Leading singers", "price", "stock"};
			ArrayList< ArrayList<String> > rowList = new ArrayList< ArrayList<String> >();
			//upcList = new ArrayList<Integer>();
			// iterate over the shopping cart
			for (int upc:shoppingCart.keySet()) {
				// obtain the information about the item 
				ResultSet rs = customerModel.getItem(upc);
				try {
					while (rs.next()) {
						ArrayList<String> oneRow = new ArrayList<String>();
						int upcInt = rs.getInt("upc");
						//upcList.add(upcInt);
						String upcString = Integer.toString(upcInt);
						oneRow.add(upcString);
						String title = rs.getString("title");
						oneRow.add(title);
						String category = rs.getString("category");
						oneRow.add(category);
						String singerName = rs.getString("singers");
						oneRow.add(singerName);
						double price = rs.getDouble("price");
						String priceString = Double.toString( price );
						oneRow.add(priceString);
						int stock = rs.getInt("stock");
						String stockString = Integer.toString(stock);
						oneRow.add(stockString);
						rowList.add(oneRow);
						JTextField quantityField = new JTextField();
						quantityField.setActionCommand("for_upc_"+upc);
						quantityField.setText(Integer.toString(shoppingCart.get(upc)));
										
						quantityFieldList.add(quantityField);
						// associated the quantity field with its corresponding upc
						quantityFieldUPC.put(quantityField, upcInt);
					}
					
					rs.close();
					
					// listen for the changes in the fields and update the value in shopping cart
					for (int i=0; i<quantityFieldList.size(); ++i) {
						quantityFieldList.get(i).addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								JTextField field = (JTextField) e.getSource();
								int upc = quantityFieldUPC.get(field);
								int newQuantity = 0;
								if (field.getText().trim().length()!=0) {
									newQuantity = Integer.parseInt(field.getText().trim());
								}
								//update the shopping cart quantity
								shoppingCart.put(upc, newQuantity);
							}
						});
						
						quantityFieldList.get(i).addFocusListener(new FocusListener() {	
							@Override
							public void focusGained(FocusEvent e) {
								// TODO Auto-generated method stub
								// TODO Auto-generated method stub
								JTextField field = (JTextField) e.getSource();
								int upc = quantityFieldUPC.get(field);
								int newQuantity = 0;
								if (field.getText().trim().length()!=0) {
									newQuantity = Integer.parseInt(field.getText().trim());
								}
								//update the shopping cart quantity
								shoppingCart.put(upc, newQuantity);
							}

							@Override
							public void focusLost(FocusEvent e) {
								// TODO Auto-generated method stub
								// TODO Auto-generated method stub
								JTextField field = (JTextField) e.getSource();
								int upc = quantityFieldUPC.get(field);
								int newQuantity = 0;
								if (field.getText().trim().length()!=0) {
									newQuantity = Integer.parseInt(field.getText().trim());
								}
								//update the shopping cart quantity
								shoppingCart.put(upc, newQuantity);
							}
						});
					} // end of for loop for adding the listener for quantity field
					
					
				} catch (SQLException ex) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					mainGui.updateStatusBar(ex.getMessage());
				} //
			} // end of loop
			// add the table to the gui
			dialogHelper.addOneTableToPanel(cartPane, columnLabels,
					quantityFieldList, rowList); 
			pack();
			
		}
		
		
		
		// when checkout button is pressed, insert the purchase into database
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			int receiptId = customerModel.getNextReceiptID();
			ArrayList<Integer> upcList = new ArrayList<Integer>();
			ArrayList<Integer> quantityList = new ArrayList<Integer>();
			for (int upc:shoppingCart.keySet()) {
				if (shoppingCart.get(upc)>0) {
					upcList.add(upc);
					quantityList.add(shoppingCart.get(upc));
				}
			}
			String dateString = getCurrentDate("yyyy-MM-dd") ;
			String cardNumber = cardNumberField.getText().trim();
			String expiryDate = expiryDateField.getText().trim();
			String expectedDateString = customerModel.getExpectedDeliveryDate();
			System.out.println("Date:" + expectedDateString); // for test
			String deliveredDateString = null;
			
			if (
				customerModel.processPurchase(receiptId, dateString, cid,
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
	

	
	/*
	 * This event handler gets called when the user selects a menu item
	 */ 
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		// you cannot use == for string comparisons
		if (actionCommand.equals(ShopGUI.REGISTRATION)) {
			//TODO
			mainGui.updateStatusBar("PROCESS A REGISTRATION .......");
			RegistrationDialog rDialog = new RegistrationDialog(mainGui);
			rDialog.pack();
			mainGui.centerWindow(rDialog);
			rDialog.setVisible(true);
			return;
		} else if (actionCommand.equals(ShopGUI.GO_SHOPPING)) {
			GoShoppingDialog goShoppingDialog = new GoShoppingDialog(mainGui);
			goShoppingDialog.pack();
			mainGui.centerWindow(goShoppingDialog);
			goShoppingDialog.setVisible(true);
			return; 
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