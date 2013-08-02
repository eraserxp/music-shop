


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


/**
* This the main GUI for the program
*/
public class ShopGUI extends JFrame {
	
	
	public static void main(String[] args) {
		ShopGUI mainGui = new ShopGUI();

		// we will not call pack() on the main frame 
		// because the size set by setSize() will be ignored
		mainGui.setVisible(true);

		// create the login window
		LoginWindow lw = new LoginWindow(mainGui);	      

		//lw.addWindowListener(new ControllerRegister(mvb));	

		// pack() has to be called before centerWindow() 
		// and setVisible()
		lw.pack();

		//mainGui.centerWindow(lw);

		lw.setVisible(true); 
	}
	
}