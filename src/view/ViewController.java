package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.WebCrawler;

/**
 * Controller class for the graphical view of this program. Contains the main
 * driver methods for the app
 * 
 * @author dillon
 *
 */
public class ViewController extends JFrame implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel startPanel;
	private JPanel mainPanel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextArea statusArea;

	private int viewWidth = 300;
	private int viewHeight = 110;
	private int startLocationX = 150;
	private int startLocationY = 150;
	public WebCrawler model;

	/**
	 * Constructor for the GUI class
	 * 
	 * @param makeGUI
	 *            True if we want the visual component constructed, false if we
	 *            want to test.
	 */
	public ViewController() {
		model = new WebCrawler(this); // send down self as observer
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(viewWidth, viewHeight);
		frame.pack();
		frame.setLocation(startLocationX, startLocationY);
		frame.setLayout(new BorderLayout());
		// frame.setResizable(false);
		frame.setTitle("Coupon Adder");

		statusArea = new JTextArea("Status");
		frame.add(statusArea, BorderLayout.PAGE_START);

		startPanel = addPanel(3, 1);
		mainPanel = addPanel(1, 2);

		// addTextArea(mainPanel, "Status Field");
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
		// frame.add(addCouponsButton, BorderLayout.PAGE_END);
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

	/**
	 * Transforms a character array to a string
	 * 
	 * @param charArray
	 *            the character array
	 * @return a string
	 */
	public String charArrayToString(char[] charArray) {
		String newString = "";
		for (char c : charArray) {
			newString += Character.toString(c);
		}
		return newString;
	}

	/**
	 * Sends the model the username and password from the gui fields.
	 */
	public void giveModelUsernameAndPassword() {
		model.setUsername(getUsernameField());
		model.setPassword(getPasswordField());
	}

	/**
	 * Sets the value of the username field. Used for testing to keep the fields
	 * private.
	 * 
	 * @param newUsername
	 *            The new username for the field.
	 */
	public void setUsernameField(String newUsername) {
		usernameField.setText(newUsername);
	}

	/**
	 * Used for testing to keep the field private.
	 * 
	 * @return The text value of the field.
	 */
	public String getUsernameField() {
		return usernameField.getText();
	}

	/**
	 * Used for testing to keep the field private.
	 * 
	 * @return The text value of the field.
	 */
	public String getPasswordField() {
		char[] charArray = passwordField.getPassword();
		return charArrayToString(charArray);
	}

	/**
	 * Sets the value of the password field. Used for testing to keep the fields
	 * private.
	 * 
	 * @param newPassword
	 */
	public void setPasswordField(String newPassword) {
		passwordField.setText(newPassword);
	}

	/**
	 * Listener for the add all coupons button.
	 * 
	 * @author dillon
	 *
	 */
	public class AddCouponsButtonActionListener implements ActionListener {

		/**
		 * The method that runs after the user presses 'add all coupons'. If one
		 * is not running, it starts running the model as a new thread.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				giveModelUsernameAndPassword();
				if (!model.getRunning()) {
					(new Thread(model)).start();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		statusArea.setText((String) arg1);
	}

}
