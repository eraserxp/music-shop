package gui_helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

// allow a textfield to unselect a checkbox
public class TextFieldUnSelectCheckBox implements ActionListener{
	JCheckBox checkbox;
	
	public TextFieldUnSelectCheckBox(JCheckBox checkbox) {
		this.checkbox = checkbox;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		checkbox.setSelected(false);
	}

}
