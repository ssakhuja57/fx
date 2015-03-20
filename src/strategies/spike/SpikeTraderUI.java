package strategies.spike;

import info.Pairs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import session.SessionLoginUI;

public class SpikeTraderUI extends JFrame{
	
	SpikeTrader spikeTrader;
	
	//conifg UI
	SessionLoginUI login;
	JPanel config = new JPanel(new GridLayout(2,2));
	JComboBox<String> currencySelector;
	JTextField eventDate;
//	JSpinner eventDate;
	
	//app UI
	JPanel info = new JPanel(new GridLayout(3,1));
	JLabel currency;
	JLabel eventDateSelected;
	JButton currencySubscribe;
	JButton unsubscribeAll;
	
	JPanel data = new JPanel();
	JTable pairs;
	
	JPanel params = new JPanel(new GridLayout(2,2));
	JTextField pipBuffer;
	JTextField stopBuffer;
	
	JPanel actions = new JPanel(new GridLayout(1,2));
	JButton placeOrders;
	JButton cancelOrders;

	
	
	
	public SpikeTraderUI(){
		
		super("Spike Trader");
		this.setSize(400, 120);
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
		config.add(new JLabel("Currency:"));
		config.add(currencySelector);
		config.add(new JLabel("News Date (YYYY-MM-dd HH:mm):"));
		config.add(eventDate);
		
		JPanel submit_pnl = new JPanel(new FlowLayout());
		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spikeTrader = new SpikeTrader(login.getSessionManager(), (String)currencySelector.getSelectedItem(), eventDate.getText());
				activate();
			}
		});
		
		submit_pnl.add(submit);
		
		this.add(submit_pnl, BorderLayout.SOUTH);
		this.add(config, BorderLayout.NORTH);
		this.setVisible(true);
		
		login = new SessionLoginUI();
		login.setVisible(true);


		this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                	try{
                		login.getSessionManager().close();
                		spikeTrader.close();
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
		this.setSize(1000, 1000);
		
		currency = new JLabel("Currency: " + spikeTrader.getCurrency());
		eventDateSelected = new JLabel("Event Date: " + spikeTrader.getEventDate());
		currencySubscribe = new JButton("Subscribe to Currency Pairs");
		unsubscribeAll = new JButton("Unsubscribe all Pairs");
		info.add(currency);
		info.add(eventDateSelected);
		info.add(currencySubscribe);
		info.add(unsubscribeAll);
		this.add(info, BorderLayout.NORTH);
		
		this.setVisible(true);

	}
	
	
	
	
}
