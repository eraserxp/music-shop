package gui_helper;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

// allow a checkbox to disable a text field when the check box is selected
// enable the text field when the check box is unselected
public class DisableTextField implements ItemListener{
	private JTextField fieldToBeDisabled;
	
	public DisableTextField(JTextField fieldToBeDisabled) {
		this.fieldToBeDisabled = fieldToBeDisabled;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		JCheckBox cb = (JCheckBox) e.getSource();
		if (cb.isSelected()) {
			fieldToBeDisabled.setEnabled(false);
		} else {
			fieldToBeDisabled.setEnabled(true);			
		}
	}

}
