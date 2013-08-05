


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;


/**
* This the main GUI for the program
*/
public class ShopGUI extends JFrame {
	// action command for clerk
	public static String PROCESS_PURCHASE = "Process purchase"; 
	public static String PROCESS_RETURN = "Process return"; 
	// action command for customer
	public static String REGISTRATION = "Registration";
	public static String SEARCH_ITEM = "Search item";
	public static String ADD_ITEM_TO_CART = "Add item to cart";
	public static String REMOVE_ITEM = "Remove item from cart";
	public static String CHECK_OUT = "Check out";
	// action command for manager
	public static String ADD_ITEM_TO_STORE = "Add item to store";
	public static String PROCESS_DELIVERY = "Process delivery";
	public static String GENERATE_DAILY_REPORT = "Generate daily report";
	public static String SHOW_TOP_SELLING_ITEMS = "Show top selling items";
	
	
	// the text field for displaying error messages
	private JTextArea statusField = new JTextArea(5,0);
	
	// the scrollpane to hold the table
	private JScrollPane tableScrPane = new JScrollPane();
	
	// the menus for clerk, customer, and manager
	private JMenu clerkMenu;
	private JMenu customerMenu;
	private JMenu managerMenu;
	
	public ShopGUI() {
		
		super("AMS music shop");
		setSize(1500,650); //set up the size for the frame
		
		// the content pane;
		// components will be spaced vertically 10 pixels apart
		JPanel contentPane = new JPanel(new BorderLayout(0, 10));
		setContentPane(contentPane);
		
		// leave some space around the content pane
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// setup the menubar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// indent first menu
		menuBar.add(Box.createRigidArea(new Dimension(10,0)));
		
		setUpClerkMenu(menuBar);
		setUpCustomerMenu(menuBar);
		setUpManagerMenu(menuBar);


	}
	
	// create an menu item and add it to menu
	// set the action command for the menu item
	private void createMenuItem(JMenu menu, String label, String actionCommand) {
		JMenuItem menuItem = new JMenuItem(label);

		if (actionCommand.length() > 0) {
			menuItem.setActionCommand(actionCommand);
		}

		menu.add(menuItem);
	}
	
	private void setUpClerkMenu(JMenuBar menuBar) {
		clerkMenu = new JMenu("Clerk");
		createMenuItem(clerkMenu, PROCESS_PURCHASE, PROCESS_PURCHASE);
		createMenuItem(clerkMenu, PROCESS_RETURN, PROCESS_RETURN);
		//add the menu to the menu bar
		menuBar.add(clerkMenu);
	}
	
	private void setUpCustomerMenu(JMenuBar menuBar) {
		customerMenu = new JMenu("Customer");
		
		createMenuItem(customerMenu, REGISTRATION, REGISTRATION);
		createMenuItem(customerMenu, SEARCH_ITEM, SEARCH_ITEM);
		createMenuItem(customerMenu, ADD_ITEM_TO_CART, ADD_ITEM_TO_CART);
		createMenuItem(customerMenu, REMOVE_ITEM, REMOVE_ITEM);
		createMenuItem(customerMenu, CHECK_OUT, CHECK_OUT);
		//add the menu to the menu bar
		menuBar.add(customerMenu);
	}	
	
	private void setUpManagerMenu(JMenuBar menuBar) {
		managerMenu = new JMenu("Manager");
		
		createMenuItem(managerMenu, ADD_ITEM_TO_STORE, ADD_ITEM_TO_STORE);
		createMenuItem(managerMenu, PROCESS_DELIVERY, PROCESS_DELIVERY);
		createMenuItem(managerMenu, GENERATE_DAILY_REPORT, GENERATE_DAILY_REPORT);
		createMenuItem(managerMenu, SHOW_TOP_SELLING_ITEMS, SHOW_TOP_SELLING_ITEMS);
		//add the menu to the menu bar
		menuBar.add(managerMenu);
	}
	
	/*
	 * This method registers the controllers for all items in each menu. 
	 */ 
	public void registerControllers() {
		JMenuItem menuItem; 

		// for clerk
		ClerkController clerkController = new ClerkController(this);

		for (int i = 0; i < clerkMenu.getItemCount(); i++) {
			menuItem = clerkMenu.getItem(i);
			menuItem.addActionListener(clerkController);
		}
		
		// for customer
		CustomerController customerController = new CustomerController(this);

		for (int i = 0; i < customerMenu.getItemCount(); i++) {
			menuItem = customerMenu.getItem(i);
			menuItem.addActionListener(customerController);
		}
		
		// for manager
		ManagerController managerController = new ManagerController(this);

		for (int i = 0; i < managerMenu.getItemCount(); i++) {
			menuItem = managerMenu.getItem(i);
			menuItem.addActionListener(managerController);
		}
	}
	
	
	
	public static void main(String[] args) {
		ShopGUI mainGui = new ShopGUI();

		// we will not call pack() on the main frame 
		// because the size set by setSize() will be ignored
		mainGui.setVisible(true);

		// create the login window
		LoginWindow lw = new LoginWindow(mainGui);	      

		// only after the login window disappeared, the controllers are created
		// otherwise, the controller will not be associated with the successful
		// connection of the database
		lw.addWindowListener(new ControllerRegister(mainGui));	

		//mainGui.registerControllers();
		
		// pack() has to be called before centerWindow() 
		// and setVisible()
		lw.pack();

		//mainGui.centerWindow(lw);

		lw.setVisible(true); 
	}
	
}


/*
 * Event handler for login window. After the user logs in (after login
 * window closes), the controllers that handle events on the menu items
 * are created. The controllers cannot be created before the user logs 
 * in because the database connection is not valid at that time. The 
 * models that are created by the controllers require a valid database 
 * connection.
 */ 
class ControllerRegister extends WindowAdapter {
	private ShopGUI shopGUI; 

	public ControllerRegister(ShopGUI shopGUI)
	{
		this.shopGUI = shopGUI;
	}

	public void windowClosed(WindowEvent e)
	{	
		shopGUI.registerControllers();
	}
} 
