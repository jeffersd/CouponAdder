package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.WebCrawler;

/**
 * Controller class for the graphical view of this program. Contains the main driver methods for the app
 * @author dillon
 *
 */
public class ViewController extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel startPanel;
	private JPanel mainPanel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	
	private int viewWidth = 300;
	private int viewHeight = 100;
	private int startLocationX = 150;
	private int startLocationY = 150;
	private WebCrawler model;

	public ViewController() {
		model = new WebCrawler();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(viewWidth, viewHeight);
		frame.pack();
		frame.setLocation(startLocationX, startLocationY);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setTitle("Coupon Adder");
		
		startPanel = addPanel(3, 1);
		mainPanel = addPanel(1, 2);
		
		addTextArea(mainPanel, "Status Field");
		addTextArea(mainPanel, "Coupons");
		
		addLabel(startPanel, "Username: ");
		usernameField = addTextField(startPanel, "");
		addLabel(startPanel, "Password: ");
		passwordField = new JPasswordField();
		startPanel.add(passwordField);
		passwordField.setVisible(true);
		
		JButton addCouponsButton = new JButton("Add Coupons");
		AddCouponsButtonActionListener AL = new AddCouponsButtonActionListener();
		addCouponsButton.addActionListener(AL);
		//frame.add(addCouponsButton, BorderLayout.PAGE_END);
		startPanel.add(addCouponsButton);
		
		frame.add(mainPanel);
		frame.add(startPanel);
		startPanel.setVisible(true);
		frame.setVisible(true);
	}
	
	public JPanel addPanel(int rows, int cols) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(rows, cols));
		panel.setVisible(false);
		return panel;
	}
	
	public void addTextArea(JPanel panel, String name) {
		JTextArea field = new JTextArea(name);
		field.setEditable(false);
		panel.add(field);
		field.setVisible(true);
	}
	
	public JTextField addTextField(JPanel panel, String name) {
		JTextField field = new JTextField(name);
		field.setEditable(true);
		panel.add(field);
		field.setVisible(true);
		return field;
	}
	
	public void addLabel(JPanel panel, String labelText) {
		JLabel label = new JLabel(labelText);
		panel.add(label);
	}
	
	private class AddCouponsButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				startPanel.setVisible(false);
				mainPanel.setVisible(true);
				int coupons = 0;
				model.setUsername(usernameField.getText());
				//char[] passwordAsCharArray = passwordField.getPassword();
				model.setPassword(passwordField.getText());
				coupons = model.addAllCoupons();
				if (coupons == -1) {
					startPanel.setVisible(true);
					mainPanel.setVisible(false);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	
}
