import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 


import java.sql.*;


/*
 * ClerkController is a control class that handles action events on the Clerk menu.
 * It updates the GUI based on which menu item the clerk selected and also use methods
 * in ClerkModel to update the tables in database
 */

public class ClerkController implements ActionListener, ExceptionListener {

	private ShopGUI shopGUI = null;
	private ClerkModel clerkModel = null;
	private DialogHelper dialogHelper = null;
	
	public ClerkController(ShopGUI sg) {
		shopGUI = sg;
		clerkModel = new ClerkModel();
		// add this to the listener list of the clerk model
		clerkModel.addExceptionListener(this);
		
		dialogHelper = new DialogHelper();
	}
	
	/**
	 * generate a dialog to process a purchase. It calls processPurchase method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessPurchaseDialog extends JDialog implements ActionListener {

		private JTextField branchID = new JTextField(4);
		private JTextField branchName = new JTextField(10);		
		
		public ProcessPurchaseDialog(ShopGUI shopGUI) {
			//TODO
			super(shopGUI, "Process purchase", true);
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JPanel inputPane = dialogHelper.createInputPane("Purchase fields");
			
			dialogHelper.addComponentToDialog(inputPane, "Branch ID", branchID);
			dialogHelper.addComponentToDialog(inputPane, "Branch name", branchName);
			
			JButton OKButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancel");
			OKButton.addActionListener(this);
			OKButton.setActionCommand("OK");
			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			});
			
			dialogHelper.addOKandCancelButtons(inputPane, OKButton, cancelButton);
			
			contentPane.add(inputPane, BorderLayout.CENTER);

			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e)
				{
					dispose();
				}
			});
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * generate a dialog to process a return. It calls processReturn method in
	 * ClerkModel to update the database
	 *
	 */
	class ProcessReturnDialog extends JDialog implements ActionListener {

		public ProcessReturnDialog(ShopGUI shopGUI) {
			//TODO
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	@Override
	public void exceptionGenerated(ExceptionEvent ex) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		// you cannot use == for string comparisons
		if (actionCommand.equals(ShopGUI.PROCESS_PURCHASE)) {
			//TODO
			ProcessPurchaseDialog ppDialog = new ProcessPurchaseDialog(shopGUI);
			ppDialog.pack();
			//mvb.centerWindow(iDialog);
			ppDialog.setVisible(true);
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_RETURN)) {
			//TODO
			return; 
		}
	}
	
}