package session;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class SessionLoginUI extends JFrame{

	private SessionManager sm;
	
	JLabel login_id_lbl;
	JLabel passwd_lbl;
	JLabel account_type_lbl;
	JLabel account1_lbl;
	JLabel account2_lbl;
	
	JTextField login_id;
	JTextField passwd;
	JComboBox<String> account_type;
	JTextField account1;
	JTextField account2;
	
	public SessionLoginUI(){
		
		super("FX Login");
		this.setSize(500, 200);
		this.setResizable(false);
		Container content = this.getContentPane();
		
		JPanel params = new JPanel(new GridLayout(5, 2, 10, 5));
		params.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		login_id_lbl = new JLabel("Login ID:");
		passwd_lbl = new JLabel("Password:");
		account_type_lbl = new JLabel("Account Type:");
		account1_lbl = new JLabel("Account 1 ID:");
		account2_lbl = new JLabel("Account 2 ID (Optional):");
		
		login_id = new JTextField();
		passwd = new JPasswordField();
		account_type = new JComboBox<String>();
			account_type.addItem("Demo");
			account_type.addItem("Real");
		account1 = new JTextField();
		account2 = new JTextField();
		
		params.add(login_id_lbl);
		params.add(login_id);
		
		params.add(passwd_lbl);
		params.add(passwd);
		
		params.add(account_type_lbl);
		params.add(account_type);
		
		params.add(account1_lbl);
		params.add(account1);
		
		params.add(account2_lbl);
		params.add(account2);
		
		content.add(params, BorderLayout.CENTER);
		
		JPanel login_pnl = new JPanel(new FlowLayout());
		JButton login = new JButton("Login");
		login_pnl.add(login);
		content.add(login_pnl, BorderLayout.SOUTH);
		
		login.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!checkFields()){
					System.out.println("Need to specify all mandatory fields");
					return;
				}
				String account2_check = account2.getText().equals("") ? account1.getText() : account2.getText();
				sm = new SessionManager(login_id.getText(), passwd.getText(), (String) account_type.getSelectedItem(), account1.getText(), account2_check);
				dispose();
			}
		});
		
	}
	
	private boolean checkFields(){
		return !login_id.getText().equals("") && !passwd.getText().equals("") && !account_type.getSelectedItem().equals("") && !account1.getText().equals("");
	}

	
	public SessionManager getSessionManager(){
		return this.sm;
	}
}
