package controller;

import gui_helper.DialogHelper;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 

import model.ClerkModel;
import model.CustomerModel;

import org.omg.CORBA.portable.CustomValue;

import controller.ClerkController.ProcessPurchaseDialog;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;
import view.ShopGUI;

import java.sql.*;
import java.text.NumberFormat;


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
		String cid = null; // to record the username
		
		
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
			JPanel cartPane = dialogHelper.createInputPane("Shopping cart");
			
			// the pane to allow the user to check out
			JPanel confirmPane = new JPanel();
			confirmPane.setLayout(new BoxLayout(confirmPane, BoxLayout.X_AXIS));
			confirmPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton checkOutButton = new JButton("Check out");
			checkOutButton.setEnabled(false);
			JButton cancelButton = new JButton("Cancel");			
			dialogHelper.addComponentsToPanel(confirmPane, checkOutButton, cancelButton);
			
			contentPane.add(loginPane, BorderLayout.NORTH);
			JPanel centerPane = new JPanel(new BorderLayout());
			centerPane.add(searchPane, BorderLayout.NORTH);
			centerPane.add(cartPane, BorderLayout.CENTER);
			contentPane.add(centerPane, BorderLayout.CENTER);
			contentPane.add(confirmPane, BorderLayout.SOUTH);
			
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
					}
				}
			});
			
		}

		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
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