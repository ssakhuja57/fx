package com.peebeekay.fx.session;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.peebeekay.fx.utils.FileUtils;
import com.peebeekay.fx.utils.Logger;

public class SessionLoginUI extends JFrame{

	private SessionManager sm;
	private Credentials creds;
	
	JLabel loginNicknameLabel = new JLabel("Saved Preferences:");
	JLabel login_id_lbl = new JLabel("Login ID:");;
	JLabel passwd_lbl = new JLabel("Password:");
	JLabel account_type_lbl = new JLabel("Account Type:");
	JLabel account1_lbl = new JLabel("Account 1 ID:");
	JLabel account2_lbl = new JLabel("Account 2 ID (Optional):");;
	
	JComboBox<String> loginNickname;
	JTextField login_id;
	JTextField passwd;
	JComboBox<String> account_type;
	JTextField account1;
	JTextField account2;
	
	public SessionLoginUI(){
		
		super("FX Login");
		this.setSize(500, 250);
		this.setResizable(false);
		Container content = this.getContentPane();
		
		JPanel params = new JPanel(new GridLayout(0, 2, 10, 5));
		params.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		loginNickname = new JComboBox<String>();
			for (String name: FileUtils.PROPERTIES_MAP.keySet()){
				loginNickname.addItem(name);
			}
		login_id = new JTextField();
		passwd = new JPasswordField();
		account_type = new JComboBox<String>();
			account_type.addItem("Demo");
			account_type.addItem("Real");
		account1 = new JTextField();
		account2 = new JTextField();
		
		params.add(loginNicknameLabel);
		params.add(loginNickname);
		
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
		
		loginNickname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fillValues(FileUtils.PROPERTIES_MAP.get((String)loginNickname.getSelectedItem()));
			}
		});
		
		login.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!checkFields()){
					Logger.error("Need to specify all mandatory fields");
					return;
				}
				
				String account2_check = account2.getText().equals("") ? account1.getText() : account2.getText();
				creds = new Credentials(
						login_id.getText(), 
						passwd.getText(), 
						(String) account_type.getSelectedItem(), 
						new String[]{account1.getText(), account2_check}
						);
				sm = new SessionManager(
						creds,
						FileUtils.PROPERTIES_MAP.get((String)loginNickname.getSelectedItem())
						);
				dispose();
			}
		});
		
	}
	
	private boolean checkFields(){
		return !login_id.getText().equals("") && !passwd.getText().equals("") && !account_type.getSelectedItem().equals("") && !account1.getText().equals("");
	}
	
	private void fillValues(Properties props){
		login_id.setText(props.getProperty("login_id"));
		passwd.setText(props.getProperty("password"));
		account_type.setSelectedItem(props.getProperty("account_type"));
		account1.setText(props.getProperty("account1_ID"));
		account2.setText(props.getProperty("account2_ID"));
	}
	
	public SessionManager getSessionManager(){
		return sm;
	}
	
	public Credentials getCreds(){
		return creds;
	}
	
}
