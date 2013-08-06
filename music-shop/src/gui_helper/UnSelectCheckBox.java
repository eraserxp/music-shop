package gui_helper;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;

//create a focus listener to unselect the input checkbox
public class UnSelectCheckBox implements FocusListener {
	JCheckBox checkbox; 
	public UnSelectCheckBox(JCheckBox cb) {
		super();
		checkbox = cb;
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		//checkbox.setSelected(false);						
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		checkbox.setSelected(false);						
	}				
}; // end of class UnSelectCheckBox
