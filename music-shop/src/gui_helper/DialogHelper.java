package gui_helper;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	int rowCount1; // each row count represent a different gui setup
	int rowCount2;
	int rowCount3;
	int rowCount4;


	public DialogHelper() {
		//empty
		gb = new GridBagLayout();
		c = new GridBagConstraints();
		rowCount1 = 0;
		rowCount2 = 0;
		rowCount3 = 0;
		rowCount4 = 0;
	}
	

	/* create an panel and give the title for it
	*/
	public JPanel createInputPane(String title) {
		JPanel inputPane = new JPanel();
		inputPane.setBorder(BorderFactory.createCompoundBorder(
				new TitledBorder(new EtchedBorder(), title), 
				new EmptyBorder(5, 5, 5, 5)));
		return inputPane;
	}
	
	// add a label and its corresponding field as a row to the input panel
	// the next call will place components right below the last added ones
	// so that you can use it to add several rows of component pairs (one below the other)
	public void addComponentsToPanel(JPanel inputPanel, String label, JTextField field) {
		
		inputPanel.setLayout(gb);
		
		// create and place label component
		JLabel jLabel1= new JLabel(label + ": ", SwingConstants.RIGHT);	    
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = rowCount3;
		c.insets = new Insets(0, 0, 0, 5);
		c.anchor = GridBagConstraints.EAST;
		gb.setConstraints(jLabel1, c);
		inputPanel.add(jLabel1);

		// place field1 component
		c.gridx = 1;
		c.gridy = rowCount3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 30);
		gb.setConstraints(field, c);
		inputPanel.add(field);
		
		rowCount3++;
	}
	

	// add the item upc and quantity fields to the inputPanel 
	public void addComponentsToPanel(JPanel inputPanel, String itemUPC1, 
			  JTextField quantity1, String itemUPC2, JTextField quantity2,
			  JCheckBox removeCB) {

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
		//c.anchor = GridBagConstraints.WEST;
		gb.setConstraints(quantity2, c);
		inputPanel.add(quantity2);
		
		// place the checkbox component
		c.gridx = 4;
		c.gridy = rowCount1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		gb.setConstraints(removeCB, c);
		inputPanel.add(removeCB);
		
		rowCount1 += 1;
	}
	
	
	// you should call it after you have deleted one row from the inputPanel
//	public void decreaseRowCount1byOne() {
//		rowCount1 -= 1;
//	}

	
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
	
	// add a table of gui components to the inputPanel
	public void addOneTableToPanel(JPanel inputPanel, String[] columnLabels,
			                       ArrayList<JCheckBox> checkboxList,
			                       ArrayList<JTextField> fieldList, 
			                       ArrayList< ArrayList<String> > rowList) {
		int rowCount = 0;
		inputPanel.removeAll();
		inputPanel.setLayout(gb);
		// add the first row		
		for (int i=0; i<columnLabels.length; ++i) {
			JLabel fieldLabel= new JLabel(columnLabels[i],SwingConstants.RIGHT);
			c.gridx = i;
			c.gridy = rowCount;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 0, 0, 10);
			gb.setConstraints(fieldLabel, c);
			inputPanel.add(fieldLabel);
		}
		rowCount += 1;
		
		for (int row=0; row<checkboxList.size(); ++row) {
			// add the checkbox
			c.gridx = 0;
			c.gridy = rowCount;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 0, 0, 10);
			gb.setConstraints(checkboxList.get(row), c);
			inputPanel.add(checkboxList.get(row));
			// add the text field
			c.gridx = 1;
			c.gridy = rowCount;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 0, 0, 10);
			gb.setConstraints(fieldList.get(row), c);
			inputPanel.add(fieldList.get(row));
			// add one row from rowList
			for (int column = 0; column < columnLabels.length-2; ++column) {
				JLabel fieldLabel= new JLabel(rowList.get(row).get(column),SwingConstants.RIGHT);
				c.gridx = column + 2;
				c.gridy = rowCount;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.insets = new Insets(0, 0, 0, 10);
				gb.setConstraints(fieldLabel, c);
				inputPanel.add(fieldLabel);
			}
			rowCount += 1;
		} // end of outer loop
	} // end of addOneTableToPanel
	
	
}
