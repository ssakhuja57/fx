package strategies.spike;

import info.Pairs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import session.SessionLoginUI;

public class SpikeTraderUI extends JFrame{
	
	private SpikeTrader spikeTrader;
	
	private String currency; //this is temp
	
	//conifg UI
	SessionLoginUI login;
	JPanel config = new JPanel(new GridLayout(0,2));
	JComboBox<String> currencySelector;
	JTextField eventDate;
//	JSpinner eventDate;
	JTextField expireAfter;		String defExpireAfter = "180";
	JComboBox<Boolean> recalibrate;
	JTextField recalibratorFreq;	String defRecalibratorFreq = "1";	
	JTextField recalibrateUntil;	String defRecalibrateUntil = "30";
	
	
	//app UI
	JPanel info = new JPanel(new GridLayout(4,1));
	JLabel currencySelected;
	JLabel eventDateSelected;
	JButton currencySubscribe;
	JButton unsubscribeAll;
	
	JPanel data = new JPanel();
	JTable pairs;
	
	JPanel inputs = new JPanel(new GridLayout(0,4));
	HashMap<String,JTextField[]> orderInputs;
	JTextField defAmount;
	JTextField defSpikeBuffer;
	JTextField defStopBuffer;
	
	JPanel actions = new JPanel(new GridLayout(1,2));
	JButton saveParams;
	JButton placeOrders;
	JButton cancelOrders;

	
	
	
	public SpikeTraderUI(){
		
		super("Spike Trader");
		this.setSize(500, 225);
		this.setResizable(false);
		currencySelector = new JComboBox<String>();
		for (String currency: Pairs.currencies){
			currencySelector.addItem(currency);
		}
		eventDate = new JTextField(new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date()));
//		eventDate = new JSpinner( new SpinnerDateModel() );
//		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(eventDate, "HH:mm:ss");
//		eventDate.setEditor(timeEditor);
//		eventDate.setValue(new Date()); // will only show the current time
//		config.add(eventDate);
		expireAfter = new JTextField(defExpireAfter);
	
		recalibrate = new JComboBox<Boolean>();
			recalibrate.addItem(true);
			recalibrate.addItem(false);
		recalibratorFreq = new JTextField(defRecalibratorFreq);
		recalibrateUntil = new JTextField(defRecalibrateUntil);
		
		config.add(new JLabel("Currency:"));
		config.add(currencySelector);
		config.add(new JLabel("Event Date (YYYY-MM-dd HH:mm):"));
		config.add(eventDate);
		config.add(new JLabel("Expire orders X seconds after event date:"));
		config.add(expireAfter);
		config.add(new JLabel("Auto Recalibrate:"));
		config.add(recalibrate);
		config.add(new JLabel("Recalibrate every X seconds:"));
		config.add(recalibratorFreq);
		config.add(new JLabel("Recalibrate until X seconds before event:"));
		config.add(recalibrateUntil);
		
		JPanel submit_pnl = new JPanel(new FlowLayout());
		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currency = (String) currencySelector.getSelectedItem();
				spikeTrader = new SpikeTrader(
						login.getSessionManager(), 
						(String)currencySelector.getSelectedItem(), 
						eventDate.getText(),
						Integer.parseInt(expireAfter.getText()), 
						(Boolean)recalibrate.getSelectedItem(), 
						Integer.parseInt(recalibratorFreq.getText()),
						Integer.parseInt(recalibrateUntil.getText())
						);
				activate();
			}
		});
		
		submit_pnl.add(submit);
		
		this.add(config, BorderLayout.NORTH);
		this.add(submit_pnl, BorderLayout.SOUTH);
		this.setVisible(true);
		
		login = new SessionLoginUI();
		login.setVisible(true);


		this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                	try{
                		login.getSessionManager().close();
                		spikeTrader.close();
                	} catch(NullPointerException npe){
                		//
                	} catch(Exception ex){
                		ex.printStackTrace();
                	} finally{
                		dispose();
                	}
                }
            });


	}
	
	private void activate(){
		this.setVisible(false);
		this.getContentPane().removeAll();
		this.setSize(600, 300);
		this.setLayout(new GridBagLayout());
		
		currencySelected = new JLabel("Currency: " );//+ spikeTrader.getCurrency());
		eventDateSelected = new JLabel("Event Date: " );//+ spikeTrader.getEventDate());
		currencySubscribe = new JButton("Subscribe to " + currency + " Pairs");
		unsubscribeAll = new JButton("Unsubscribe all Pairs");
		currencySubscribe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spikeTrader.subscribeCurrency();
			}
		});
		unsubscribeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spikeTrader.unsubscribeAll();
			}
		});
		
		info.add(currencySelected);
		info.add(eventDateSelected);
		info.add(currencySubscribe);
		info.add(unsubscribeAll);
		addComponent(this, info, 1, 1);
		
		
		inputs.add(new JLabel("Pair"));
		inputs.add(new JLabel("Amount (1K Lots)"));
		inputs.add(new JLabel("Spike Buffer"));
		inputs.add(new JLabel("Stop Buffer"));
		orderInputs = new HashMap<String,JTextField[]>();
		for (String pair: Pairs.getRelatedPairs(currency)){
			inputs.add(new JLabel(pair + ":"));
			JTextField amount = new JTextField();
			JTextField spikeBuffer = new JTextField();
			JTextField stopBuffer = new JTextField();
			amount.getDocument().addDocumentListener(new InputValueChangeListener());
			spikeBuffer.getDocument().addDocumentListener(new InputValueChangeListener());
			stopBuffer.getDocument().addDocumentListener(new InputValueChangeListener());
			inputs.add(amount);
			inputs.add(spikeBuffer);
			inputs.add(stopBuffer);
			orderInputs.put(pair, new JTextField[]{amount, spikeBuffer, stopBuffer});
		}
		inputs.add(new JLabel("Defaults:"));
		defAmount = new JTextField();
		defSpikeBuffer = new JTextField();
		defStopBuffer = new JTextField();
		inputs.add(defAmount);
		inputs.add(defSpikeBuffer);
		inputs.add(defStopBuffer);
		addComponent(this, inputs, 2, 1);
		
		placeOrders = new JButton("Place Orders");
		cancelOrders = new JButton("Cancel Orders");
		saveParams = new JButton("Save Parameters");
		actions.add(saveParams);
		actions.add(placeOrders);
		actions.add(cancelOrders);
		addComponent(this, actions, 2, 2);
		
		
		placeOrders.setEnabled(false);
		cancelOrders.setEnabled(false);
		
		saveParams.addActionListener(new SetInputsListener());
		placeOrders.addActionListener(new StartListener());
		cancelOrders.addActionListener(new StopListener());
		
		this.setVisible(true);

	}
	
	private class StartListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			placeOrders.setEnabled(false);
			cancelOrders.setEnabled(true);
			//spikeTrader.start();
		}
	}
	
	private class StopListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			cancelOrders.setEnabled(false);
			saveParams.setEnabled(true);
			//spikeTrader.stop();
		}
	}
	
	private class SetInputsListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			saveParams.setEnabled(false);
			
			boolean valueErrors = false;
			for (String pair: Pairs.getRelatedPairs(currency)){
				
				int lots;
				int spikeBuffer;
				int stopBuffer;
				JTextField[] paramsSet = orderInputs.get(pair);
				
				try{
					lots = !paramsSet[0].getText().equals("") ? Integer.parseInt(paramsSet[0].getText()) : Integer.parseInt(defAmount.getText());
					spikeBuffer = !paramsSet[1].getText().equals("") ? Integer.parseInt(paramsSet[1].getText()) : Integer.parseInt(defSpikeBuffer.getText());
					stopBuffer = !paramsSet[2].getText().equals("") ? Integer.parseInt(paramsSet[2].getText()) : Integer.parseInt(defStopBuffer.getText());
				} catch (NumberFormatException nfe){
					System.out.println("value error for " + pair + " or default value not set");
					valueErrors = true;
				}
				
//				if(!spikeTrader.setParams(pair, lots, spikeBuffer, stopBuffer)){
//					return;
//				};
			}
			if(!valueErrors){
				if(!spikeTrader.getIsActive()){
					placeOrders.setEnabled(true);
				}
			}
			else{
				saveParams.setEnabled(true);
			}
		}
	}
	
	private class InputValueChangeListener implements DocumentListener{

		@Override
		public void changedUpdate(DocumentEvent e) {
			placeOrders.setEnabled(false);
			saveParams.setEnabled(true);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			placeOrders.setEnabled(false);
			saveParams.setEnabled(true);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			placeOrders.setEnabled(false);
			saveParams.setEnabled(true);
		}
		
	}
	
	private void addComponent(Container cont, Component comp, int gridx, int gridy){
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		cont.add(comp, c);
	}
	
	public static void main(String[] args){
		SpikeTraderUI ui = new SpikeTraderUI();
	}
	
	
	
	
}
