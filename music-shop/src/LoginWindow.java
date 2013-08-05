// This code is based on the branch.java code given in JDBC tutorial 1

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/*
 * The login window
 */ 
public class LoginWindow extends JDialog implements ActionListener
{
	// MyOracleConnection represents a connection to an Oracle database
	private MyOracleConnection moc = MyOracleConnection.getInstance();

	// record the login attempts (maximum is 3) 
	private int loginAttempts = 0;

	// components of the login window
	private JTextField usernameField = new JTextField(15);
	private JPasswordField passwordField = new JPasswordField(15);	   
	private JLabel usernameLabel = new JLabel("Username:  ");
	private JLabel passwordLabel = new JLabel("Password:  ");
	private JButton loginButton = new JButton("Log In");


	/*
	 * constructor for LogInWindow
	 */
	public LoginWindow(JFrame parent)
	{
		// set up the title for the login window
		super(parent, "User Login", true);
		// don't allow the user to resize this window
		setResizable(false);
		
		usernameField.setText("ora_w2u8");
		// every character entered in the password field is echoed by '*'
		passwordField.setEchoChar('*');
		

		// content pane for the login window
		// everything is contained in this loginPane
		JPanel loginPane = new JPanel();
		setContentPane(loginPane);


		/*
		 * layout components using the GridBag layout manager
		 */ 

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		loginPane.setLayout(gb);
		loginPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

		// place the username label 
		
		/* gridwidth --- number of columns in its display area
		 * 
		 *GridBagConstraints.RELATIVE --- place this component just to the right
		 *                                or to the below of the last component added
		 */ 
		c.gridwidth = GridBagConstraints.RELATIVE; 
		//set up the space between the component and the edges of its display area
		c.insets = new Insets(10, 10, 5, 0); 
		gb.setConstraints(usernameLabel, c);
		loginPane.add(usernameLabel);

		
		// place the text field for the username 
		/*
		 * GridBagConstraints.REMAINDER --- to specify that this component be the last
		 *                                  one in its row (for gridwidth) or column 
		 *                                  (for gridheight)
		 */
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 5, 10);
		gb.setConstraints(usernameField, c);
		loginPane.add(usernameField);

		// place password label
		/*
		 * place the passwordLabel just below the the usernameLabel
		 */
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(0, 10, 10, 0);
		gb.setConstraints(passwordLabel, c);
		loginPane.add(passwordLabel);

		// place the password field 
		/*
		 * place the passwordField to the right of passwordLabel
		 */
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 10, 10);
		gb.setConstraints(passwordField, c);
		loginPane.add(passwordField);

		// place the login button
		//this component is the last one in its row
		c.gridwidth = GridBagConstraints.REMAINDER; 
		c.insets = new Insets(10, 10, 5, 10);
		c.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(loginButton, c);
		loginPane.add(loginButton);

		// end of layout

		// Register password field and OK button with action event handler.
		// An action event is generated when the return key is pressed while 
		// the cursor is in the password field or when the OK button is pressed.
		passwordField.addActionListener(this);
		loginButton.addActionListener(this);

		// anonymous inner class for closing the window
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				System.exit(0); 
			}
		});

		// initially, place the cursor in the username text field
		//usernameField.requestFocus();
		passwordField.requestFocus();
	}


	/*
	 * event handler for password field and OK button
	 */ 	    
	public void actionPerformed(ActionEvent e) {
		if (moc.connect(usernameField.getText(), 
				String.valueOf(passwordField.getPassword()))) {
			// if the username and password are valid, 
			// get rid of the login window
			dispose();     
		} else {
			loginAttempts++;

			if (loginAttempts >= 3) {
				dispose();
				System.exit(0);
			} else {
				// clear the password
				passwordField.setText("");
			}
		}  
	}  
	
	
}

