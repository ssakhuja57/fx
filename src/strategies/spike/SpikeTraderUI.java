package strategies.spike;

import info.Pairs;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import session.LoginUI;

public class SpikeTraderUI extends JFrame{
	
	
	LoginUI login;
	SpikeTrader spikeTrader;
	
	JPanel controls;
	
	JComboBox<String> currencySelector;
	JButton unsubscribe;
	
	public SpikeTraderUI(){
		
		spikeTrader = new SpikeTrader();
		
		this.setLayout(new FlowLayout());
		
		currencySelector = new JComboBox<String>();
		for (String currency: Pairs.currencies){
			currencySelector.addItem(currency);
		}
		
		unsubscribe = new JButton("Unsubscribe All");
		
		controls = new JPanel(new GridLayout(2, 2, 5, 5));
		controls.add(currencySelector);
		controls.add(unsubscribe);
		this.add(controls, FlowLayout.LEFT);
		this.setVisible(true);
		login = new LoginUI(spikeTrader);
		login.setVisible(true);
	}
	
	public static void main(String[] args){
		SpikeTraderUI ui = new SpikeTraderUI();
	}
}
