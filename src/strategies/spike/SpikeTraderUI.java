package strategies.spike;

import info.Pairs;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class SpikeTraderUI extends JFrame{
	
	private JComboBox<String> currencySelector;
	private JButton unsubscribe;
	
	public SpikeTraderUI(){
		currencySelector = new JComboBox<String>();
		for (String currency: Pairs.currencies){
			currencySelector.addItem(currency);
		}
		
		unsubscribe = new JButton("Unsubsribe All");
		
		this.add(currencySelector);
		//this.add(unsubscribe);
	}
	
	public static void main(String[] args){
		SpikeTraderUI ui = new SpikeTraderUI();
		ui.setVisible(true);
	}
}
