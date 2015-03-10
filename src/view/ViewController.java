package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JFrame;
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
	
	private int viewWidth = 500;
	private int viewHeight = 300;
	private int startLocationX = 100;
	private int startLocationY = 100;
	//private WebCrawler model;

	public ViewController() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(viewWidth, viewHeight);
		frame.pack();
		frame.setLocation(startLocationX, startLocationY);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		
		//model = new WebCrawler();
		
		frame.setTitle("Coupon Adder");
		
		JButton addCouponsButton = new JButton("Add Coupons");
		frame.add(addCouponsButton, BorderLayout.PAGE_END);
		
//		JTextArea statusField = new JTextArea("Status Field");
//		statusField.setEditable(false);
//		frame.add(statusField, BorderLayout.PAGE_START);
//		statusField.setVisible(true);
		addTextComponent(frame, "Status Field", BorderLayout.PAGE_START);
		
//		JTextArea couponsAddedField = new JTextArea("Coupons");
//		couponsAddedField.setEditable(false);
//		frame.add(couponsAddedField, BorderLayout.WEST);
//		couponsAddedField.setVisible(true);
		addTextComponent(frame, "Coupons", BorderLayout.CENTER);
		addTextComponent(frame, "Username", BorderLayout.EAST);
		addTextComponent(frame, "Password", BorderLayout.EAST);
		
		frame.setVisible(true);
	}
	
	public void addTextComponent(JFrame frame, String name, String location) {
		JTextArea field = new JTextArea(name);
		field.setEditable(false);
		frame.add(field, location);
		field.setVisible(true);
	}
}
