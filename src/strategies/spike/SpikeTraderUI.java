package strategies.spike;

import info.Pairs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;

import session.SessionLoginUI;

public class SpikeTraderUI extends JFrame{
	

	private SpikeTrader spikeTrader;
	
	//private String currency; //this is temp
	
	//conifg UI
	SessionLoginUI login;
	JPanel config = new JPanel(new GridLayout(0,2));
	//JComboBox<String> currencySelector;
	JList<String> currencySelector;
	JTextField eventDate;
//	JSpinner eventDate;
	JTextField accountUtilization; String defAccountUtilization = "80.0";
	JComboBox<Boolean> autoStart;
	JTextField autoStartBefore; String defAutoStartBefore = "600";
	JTextField expireAfter;		String defExpireAfter = "90";
	JComboBox<Boolean> recalibrate;
	JTextField recalibratorFreq;	String defRecalibratorFreq = "1";	
	JTextField recalibrateUntil;	String defRecalibrateUntil = "20";
	
	
	//app UI
	JPanel info = new JPanel(new GridLayout(0,1));
	JLabel currencySelected;
	JLabel eventDateSelected;
	JLabel accountUtilizationSelected;
	JLabel autoStartDateSelected;
	JLabel expirationDateSelected;
	JLabel recalibrateSelected;
	
	JPanel setup = new JPanel(new GridLayout(0,1));
	JButton currencySubscribe;
	JButton unsubscribeAll;
	JButton updateCalculated;
	JButton recalibrateOrders;
	
	JPanel data = new JPanel();
	TableModel pairsDataModel;
	JTable pairsData;
	
	JPanel inputs = new JPanel(new GridLayout(0,5));
	HashMap<String,JTextField[]> orderInputs = new HashMap<String,JTextField[]>();
	HashMap<String, JCheckBox> recalibrateParamOptions = new HashMap<String, JCheckBox>();
	JTextField defAmount;
	JTextField defSpikeBuffer;
	JTextField defStopBuffer;
	
	JPanel actions = new JPanel(new GridLayout(1,2));
	JButton saveParams;
	JButton placeOrders;
	JButton cancelOrders;

	
	
	
	public SpikeTraderUI(){
		
		super("Spike Trader");
		this.setSize(500, 750);
		this.setResizable(false);
		currencySelector = new JList<String>(Pairs.currencies.toArray(new String[Pairs.currencies.size()]));
			currencySelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			currencySelector.setSelectedIndex(0);
			JScrollPane currencyScroll = new JScrollPane(currencySelector);
			currencyScroll.setPreferredSize(new Dimension(250, 70));
		eventDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
//		eventDate = new JSpinner( new SpinnerDateModel() );
//		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(eventDate, "HH:mm:ss");
//		eventDate.setEditor(timeEditor);
//		eventDate.setValue(new Date()); // will only show the current time
//		config.add(eventDate);
		accountUtilization = new JTextField(defAccountUtilization);
		autoStart = new JComboBox<Boolean>();
			autoStart.addItem(false);
			autoStart.addItem(true);
		autoStartBefore = new JTextField(defAutoStartBefore);
		expireAfter = new JTextField(defExpireAfter);
		recalibrate = new JComboBox<Boolean>();
			recalibrate.addItem(true);
			recalibrate.addItem(false);
		recalibratorFreq = new JTextField(defRecalibratorFreq);
		recalibrateUntil = new JTextField(defRecalibrateUntil);
		
		config.add(new JLabel("Currency:"));
		config.add(currencyScroll);
		config.add(new JLabel("Event Date (YYYY-MM-dd HH:mm):"));
		config.add(eventDate);
		config.add(new JLabel("Account Utilization (%):"));
		config.add(accountUtilization);
		config.add(new JLabel("Automatically place orders:"));
		config.add(autoStart);
		config.add(new JLabel("Place orders X seconds before event:"));
		config.add(autoStartBefore);
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
				spikeTrader = new SpikeTrader(
						login.getSessionManager(), 
						currencySelector.getSelectedValuesList().toArray(new String[currencySelector.getSelectedValuesList().size()]), 
						eventDate.getText(),
						Double.parseDouble(accountUtilization.getText())/100,
						(Boolean)autoStart.getSelectedItem(),
						Integer.parseInt(autoStartBefore.getText()),
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
		this.setSize(800, 200 + 25*spikeTrader.getPairs().size());
		this.setLayout(new GridBagLayout());
		
		currencySelected = new JLabel("Currency: " + Arrays.toString(spikeTrader.getCurrencies()));
		eventDateSelected = new JLabel("Event Date: " + spikeTrader.getEventDate());
		accountUtilizationSelected = new JLabel("Account Utilization: " + spikeTrader.getAccountUtilization()*100 + "%");
		autoStartDateSelected = new JLabel("Auto Start Time: " + (spikeTrader.autoStartEnabled() ? spikeTrader.getAutoStartDate() : "N/A"));
		expirationDateSelected = new JLabel("Expire Time: " + spikeTrader.getExpirationDate());
		recalibrateSelected = new JLabel("Recalibrate: " + (spikeTrader.recalibratorEnabled() ? "Until " + spikeTrader.getRecalibrateUntil() : "N/A"));
		info.add(currencySelected);
		info.add(eventDateSelected);
		info.add(accountUtilizationSelected);
		info.add(autoStartDateSelected);
		info.add(expirationDateSelected);
		info.add(recalibrateSelected);
		addComponent(this, info, 1, 1);
		
		
		currencySubscribe = new JButton("Subscribe to " + Arrays.toString(spikeTrader.getCurrencies()) + " Pairs");
		unsubscribeAll = new JButton("Unsubscribe all Pairs");
		updateCalculated = new JButton("Update Calculated Values");
		recalibrateOrders = new JButton("Recalibrate Orders");
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
		updateCalculated.addActionListener(new UpdateCalculated());
		recalibrateOrders.addActionListener((new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				spikeTrader.recalibrateAllOrders();
			}
		}));
		
		setup.add(currencySubscribe);
		setup.add(unsubscribeAll);
		setup.add(updateCalculated);
		setup.add(recalibrateOrders);
		addComponent(this, setup, 1, 3);
		
		
//		pairsDataModel = new DefaultTableModel(new String[]{"Pair" ,"Buy Min", "Buy Max", "Buy Diff", 
//					"Sell Min", "Sell Max", "Sell Diff", "Slope", "Std Dev"},
//					spikeTrader.getPairs().size());
//		pairsData = new JTable(pairsDataModel);
//		pairsData.setValueAt("test", 0, 0);
//		data.add(pairsData);
//		addComponent(this, data, 1, 2);
		
		
		inputs.add(new JLabel("Pair"));
		inputs.add(new JLabel("Amount (1K Lots)"));
		inputs.add(new JLabel("Spike Buffer"));
		inputs.add(new JLabel("Stop Buffer"));
		inputs.add(new JLabel("Recal Params"));

		for (String pair: spikeTrader.getPairs()){
			inputs.add(new JLabel(pair + ":"));
			//Integer[] calculated = spikeTrader.getParams().get(pair);
			JTextField amount = new JTextField();
			JTextField spikeBuffer = new JTextField();
			JTextField stopBuffer = new JTextField();
			JCheckBox recalibrateParams = new JCheckBox("", true);
			
			amount.getDocument().addDocumentListener(new InputValueChangeListener());
			spikeBuffer.getDocument().addDocumentListener(new InputValueChangeListener());
			stopBuffer.getDocument().addDocumentListener(new InputValueChangeListener());
			
			inputs.add(amount);
			inputs.add(spikeBuffer);
			inputs.add(stopBuffer);
			inputs.add(recalibrateParams);
			
			orderInputs.put(pair, new JTextField[]{amount, spikeBuffer, stopBuffer});
			recalibrateParamOptions.put(pair, recalibrateParams);
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
		//cancelOrders.setEnabled(false);
		
		saveParams.addActionListener(new SaveInputsListener());
		placeOrders.addActionListener(new StartListener());
		cancelOrders.addActionListener(new StopListener());
		
		this.setVisible(true);

	}
	
	private class StartListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			placeOrders.setEnabled(false);
			//cancelOrders.setEnabled(true);
			spikeTrader.start();
			setInputsEnabled(false);
		}
	}
	
	private class StopListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			spikeTrader.stop();
			//cancelOrders.setEnabled(false);
			saveParams.setEnabled(true);
			setInputsEnabled(true);
		}
	}
	
	private class SaveInputsListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			saveParams.setEnabled(false);
			if(spikeTrader.isActive()){
				spikeTrader.printIsRunning();
			}
			else{	
				boolean valueErrors = false;
				for (String pair: spikeTrader.getPairs()){
					
					int lots = 0;
					int spikeBuffer = 0;
					int stopBuffer = 0;
					JTextField[] paramsSet = orderInputs.get(pair);
					
					try{
						lots = !paramsSet[0].getText().equals("") ? Integer.parseInt(paramsSet[0].getText()) : Integer.parseInt(defAmount.getText());
						spikeBuffer = !paramsSet[1].getText().equals("") ? Integer.parseInt(paramsSet[1].getText()) : Integer.parseInt(defSpikeBuffer.getText());
						stopBuffer = !paramsSet[2].getText().equals("") ? Integer.parseInt(paramsSet[2].getText()) : Integer.parseInt(defStopBuffer.getText());
					} catch (NumberFormatException nfe){
						System.out.println("value error for " + pair + " or default value not set");
						valueErrors = true;
					}
					
					boolean recalibrateOption = recalibrateParamOptions.get(pair).isSelected();
					
					spikeTrader.setParams(pair, lots, spikeBuffer, stopBuffer);
					spikeTrader.setRecalibrationOptions(pair, recalibrateOption);

				}
				if(!valueErrors){
					placeOrders.setEnabled(true);
				}
				else{
					saveParams.setEnabled(true);
				}
			}
		}
	}
	
	private class InputValueChangeListener implements DocumentListener{
		
		private void update(){
			placeOrders.setEnabled(false);
			saveParams.setEnabled(true);
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			update();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			update();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			update();
		}
	}
	
	private class UpdateCalculated implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			spikeTrader.recalculateParams();
			for (String pair: spikeTrader.getPairs()){
				Integer[] calculated = spikeTrader.getParams().get(pair);
				Integer lots = calculated[0]/1000;
				Integer spikeBuffer = calculated[1];
				Integer stopBuffer = calculated[2];
				orderInputs.get(pair)[0].setText(lots.toString());
				orderInputs.get(pair)[1].setText(spikeBuffer.toString());
				orderInputs.get(pair)[2].setText(stopBuffer.toString());
			}
		}
	}
	
	private void setInputsEnabled(boolean enabled){
		updateCalculated.setEnabled(enabled);
		for (JTextField[] jtfArr: orderInputs.values()){
			for (JTextField jtf: jtfArr){
				jtf.setEnabled(enabled);
			}
		}
		defAmount.setEnabled(enabled);
		defSpikeBuffer.setEnabled(enabled);
		defStopBuffer.setEnabled(enabled);
	}
	
	private void addComponent(Container cont, Component comp, int gridx, int gridy){
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		cont.add(comp, c);
	}
	
//	private class UpdatePairsData extends TimerTask{
//		public void run(){
//			pairsDataModel.
//		}
//	}
	
//	public static void main(String[] args){
//		SpikeTraderUI ui = new SpikeTraderUI();
//	}
	
	public static void main(String[] args){
		SpikeTraderUI ui = new SpikeTraderUI();
		ui.activate();
	}
	
	
	
	
}
