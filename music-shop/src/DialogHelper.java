import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/*
 * Since there are many dialog with very similar structure (label, field, OK button, 
 * Cancel button), we create this class to help with the creation of all sorts of 
 * dialogs. 
 * 
 * This class can create an JPane to hold all label and field components and OK and 
 * cancel button. It can add a label and filed components as a row to the input panel. 
 * It can also add the OK and cancel button as a row to the input panel. 
 * 
 */

public class DialogHelper {
	
	private GridBagLayout gb = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();

	public DialogHelper() {
		//empty
	}
	

	/* create an panel that will contain the text field labels, the text fields and 
	* the OK and cancel buttons
	*/
	public JPanel createInputPane(String title) {
		JPanel inputPane = new JPanel();
		inputPane.setBorder(BorderFactory.createCompoundBorder(
				new TitledBorder(new EtchedBorder(), title), 
				new EmptyBorder(5, 5, 5, 5)));
		return inputPane;
	}
	
	// add a label and its corresponding field as a row to the input panel
	public void addComponentToDialog(JPanel inputPanel, String label, JTextField field) {

		inputPanel.setLayout(gb);
		
		// create and place label component
		JLabel label1= new JLabel(label + ": ", SwingConstants.RIGHT);	    
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(0, 0, 0, 5);
		c.anchor = GridBagConstraints.EAST;
		gb.setConstraints(label1, c);
		inputPanel.add(label1);

		// place field component
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		gb.setConstraints(field, c);
		inputPanel.add(field);
	}
	
	// add the OK and Cancel buttons to the inputPane
	public void addOKandCancelButtons(JPanel inputPane, JButton OK, JButton Cancel) {
		// add the buttons to buttonPane
		inputPane.add(Box.createHorizontalGlue());
		inputPane.add(OK);
		inputPane.add(Box.createRigidArea(new Dimension(10,0)));
		inputPane.add(Cancel);
	}
}
