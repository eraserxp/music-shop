package controller;

import gui_helper.DialogHelper;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*; 

import controller.ClerkController.ProcessPurchaseDialog;

import subject_observer.ExceptionEvent;
import subject_observer.ExceptionListener;
import view.ShopGUI;

import model.ManagerModel;


import java.sql.*;
import java.util.ArrayList;


/*
 * ManagerController is a control class that handles action events on the Manager menu.
 * It updates the GUI based on which menu item the Manager selected and also use methods
 * in ManagerModel to update the tables in database
 */

public class ManagerController implements ActionListener, ExceptionListener {

	private ShopGUI mainGui = null;
	private ManagerModel managerModel = null;
	private DialogHelper dialogHelper = null;
	private DialogHelper dialogHelper2 = null;
	
	public ManagerController(ShopGUI sg) {
		mainGui = sg;
		managerModel = new ManagerModel();
		// add this to the listener list of the manager model
		managerModel.addExceptionListener(this);
		dialogHelper = new DialogHelper();
		dialogHelper2 = new DialogHelper();
	}
	
	
	/**
	 * generate a dialog to allow the manager to add items to store
	 *
	 */
	class AddItemToStoreDialog extends JDialog implements ActionListener {
		JTextField upcField = new JTextField(4);
		JTextField titleField = new JTextField(20);
		String[] allowedTypes = {"CD", "DVD"};
		JComboBox<String> typeComboBox = new JComboBox<String>(allowedTypes);
		String[] allowedCategories = {"rock", "pop", "rap", "country", "classical", "new age", "instrumental"};
		JComboBox<String> CategoryComboBox = new JComboBox<String>(allowedCategories);
		JTextField companyField = new JTextField(10);
		JTextField yearField = new JTextField(4);
		JTextField priceField = new JTextField(4);
		JTextField stockField = new JTextField(4);
		JTextField singerField =  new JTextField(10);
		ArrayList<JTextField> singerFieldList = new ArrayList<JTextField>();
		JTextField songField =  new JTextField(10);
		ArrayList<JTextField> songFieldList = new ArrayList<JTextField>();
		
		public AddItemToStoreDialog(ShopGUI shopGUI) {
			super(shopGUI, "Add item to store", true);
			//setResizable(false);			
			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);			
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			// input pane to hold item pane, singer pane and song pane
			JPanel inputPane = new JPanel(new BorderLayout());
			
			
			JPanel itemPane = dialogHelper.createInputPane("Item");
			dialogHelper.addComponentsToPanel2(itemPane, "Enter upc", upcField);
			dialogHelper.addComponentsToPanel2(itemPane, "Enter title", titleField);
			dialogHelper.addComponentsToPanel2(itemPane, "Choose type", typeComboBox);
			dialogHelper.addComponentsToPanel2(itemPane, "Choose category", CategoryComboBox);
			dialogHelper.addComponentsToPanel2(itemPane, "Enter company", companyField);
			dialogHelper.addComponentsToPanel2(itemPane, "Enter year", yearField);
			dialogHelper.addComponentsToPanel2(itemPane, "Enter price", priceField);
			dialogHelper.addComponentsToPanel2(itemPane, "Enter quantity", stockField);
			
			inputPane.add(itemPane, BorderLayout.NORTH);
			
			//
			JPanel singerPane = new JPanel(new BorderLayout());
			JPanel singerPaneTop = new JPanel(new BorderLayout());
			JButton addSingerButton = new JButton("Add more singer");
			singerPaneTop.add(addSingerButton, BorderLayout.WEST);
			final JPanel singerPaneBottom = dialogHelper.createInputPane("");
			dialogHelper.addComponentsToPanel(singerPaneBottom, "Singer name", singerField);
			singerFieldList.add(singerField);
			
			singerPane.add(singerPaneTop,BorderLayout.NORTH);
			singerPane.add(singerPaneBottom,BorderLayout.SOUTH);
			

			final JPanel songPane = new JPanel(new BorderLayout());
			JPanel songPaneTop = new JPanel(new BorderLayout());
			JButton addSongButton = new JButton("Add more song");
			songPaneTop.add(addSongButton, BorderLayout.WEST);
			final JPanel songPaneBottom = dialogHelper2.createInputPane("");
			dialogHelper2.addComponentsToPanel(songPaneBottom, "Song title", songField);
			songFieldList.add(songField);
			
			songPane.add(songPaneTop,BorderLayout.NORTH);
			songPane.add(songPaneBottom,BorderLayout.SOUTH);
			
			inputPane.add(singerPane, BorderLayout.CENTER);
			inputPane.add(songPane, BorderLayout.SOUTH);
			
			contentPane.add(inputPane, BorderLayout.CENTER);
			

			// pane to hold the register and cancel buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));
			final JButton addButton = new JButton("Add to store");			
			JButton cancelButton = new JButton("Cancel");			
			dialogHelper.addComponentsToPanel(buttonPanel, addButton, cancelButton); 
			
			contentPane.add(buttonPanel, BorderLayout.SOUTH);
			
			
			// add listeners
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					mainGui.updateStatusBar("CANCEL THIS OPERATION");
					dispose();
				}
			});
			
			addSingerButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTextField singerField = new JTextField(10);
					dialogHelper.addComponentsToPanel(singerPaneBottom, "Singer name", singerField);
					singerFieldList.add(singerField);
					pack();
				}
			});
			
			addSongButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTextField songField = new JTextField(10);
					dialogHelper2.addComponentsToPanel(songPaneBottom, "Song title", songField);
					songFieldList.add(songField);
					pack();
				}
			});
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * generate a dialog to allow the manager to process the delivery of an order
	 * (it just sets the delivery date)
	 *
	 */
	class ProcessDeliveryDialog extends JDialog implements ActionListener {

		public ProcessDeliveryDialog(ShopGUI shopGUI) {
			// TODO
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the customer to generate a daily sales report
	 *
	 */
	class GenerateDailyReportDialog extends JDialog implements ActionListener {

		public GenerateDailyReportDialog(ShopGUI shopGUI) {
			// TODO
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * generate a dialog to allow the manager to find out the top selling items
	 *
	 */
	class ShowTopSellingItemsDialog extends JDialog implements ActionListener {

		public ShowTopSellingItemsDialog(ShopGUI shopGUI) {
			// TODO
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
		if (actionCommand.equals(ShopGUI.ADD_ITEM_TO_STORE)) {
			mainGui.updateStatusBar("PROCESS A PURCHASE .......");
			AddItemToStoreDialog dialog = new AddItemToStoreDialog(mainGui);
			dialog.pack();
			mainGui.centerWindow(dialog);
			dialog.setVisible(true);
			return;
		} else if (actionCommand.equals(ShopGUI.PROCESS_DELIVERY)) {
			
			return; 
		} else if (actionCommand.equals(ShopGUI.GENERATE_DAILY_REPORT)) {
			// TODO
			return;
		} else if (actionCommand.equals(ShopGUI.SHOW_TOP_SELLING_ITEMS)) {
			// TODO
			return;
		}
	}

	
}