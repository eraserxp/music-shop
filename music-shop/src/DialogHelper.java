import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

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
 * It can also add the OK and cancel button as a row to the button panel. 
 * 
 */

public class DialogHelper {
	private GridBagLayout gb = null;
	private GridBagConstraints c = null;
	int rowCount1; 
	int rowCount2;


	public DialogHelper() {
		//empty
		gb = new GridBagLayout();
		c = new GridBagConstraints();
		rowCount1 = 0;
		rowCount2 = 0;
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
	public void addComponentsToPanel(JPanel inputPanel, String label, JTextField field) {
		
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
	

	// add the item upc and quantity fields to the inputPanel 
	public void addComponentsToPanel(JPanel inputPanel, String itemUPC1, 
			  JTextField quantity1, String itemUPC2, JTextField quantity2) {
		inputPanel.setLayout(gb);
		
		// create and place label component
		JLabel jLabel1= new JLabel(itemUPC1 + ": ", SwingConstants.RIGHT);	    
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = rowCount1;
		c.insets = new Insets(0, 0, 0, 5);
		c.anchor = GridBagConstraints.EAST;
		gb.setConstraints(jLabel1, c);
		inputPanel.add(jLabel1);

		// place field1 component
		c.gridx = 1;
		c.gridy = rowCount1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 30);
		gb.setConstraints(quantity1, c);
		inputPanel.add(quantity1);
		
		// create and place label2 component
		JLabel jLabel2= new JLabel(itemUPC2 + ": ", SwingConstants.RIGHT);	    
		c.gridx = 2;
		c.gridy = rowCount1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		gb.setConstraints(jLabel2, c);
		inputPanel.add(jLabel2);

		// place field2 component
		c.gridx = 3;
		c.gridy = rowCount1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		gb.setConstraints(quantity2, c);
		inputPanel.add(quantity2);
		
		rowCount1 += 1;
	}
	


	
	// add the OK and Cancel buttons to the inputPane
	public void addComponentsToPanel(JPanel inputPane, JButton button1, JButton button2) {
		// add the buttons to buttonPane
		inputPane.add(Box.createHorizontalGlue());
		inputPane.add(button1);
		inputPane.add(Box.createRigidArea(new Dimension(10,0)));
		inputPane.add(button2);
	}
	
	// add a row of fields to inputPanel
	public void addOneRowToPanel(JPanel inputPanel, String[] stringColumns) {
		inputPanel.setLayout(gb);
		for (int i=0; i<stringColumns.length; ++i) {
			JLabel fieldLabel= new JLabel(stringColumns[i],SwingConstants.RIGHT);
			c.gridx = i;
			c.gridy = rowCount2;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 0, 0, 10);
			gb.setConstraints(fieldLabel, c);
			inputPanel.add(fieldLabel);
		}
		rowCount2 += 1;
	}
}
