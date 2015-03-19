package test;

import static org.junit.Assert.*;

import org.junit.Test;

import view.ViewController;
import view.ViewController.AddCouponsButtonActionListener;

/**
 * Tests the methods from the ViewController class
 * @author dillon
 *
 */
public class ViewControllerTest {
	
	@Test
	public void charArrayToStringTest() {
		ViewController VC = new ViewController();
		char[] charArray = {'s', 'o', 'm', 'e', 't','h', 'i', 'n', 'g'};
		assertTrue("something".equals(VC.charArrayToString(charArray)));
		assertFalse("another".equals(VC.charArrayToString(charArray)));
		assertFalse("Something".equals(VC.charArrayToString(charArray)));
	}
	
	@Test
	public void giveUsernameAndPasswordToModelTest() {
		ViewController VC = new ViewController();
		VC.usernameField.setText("username");
		VC.passwordField.setText("password");
		VC.giveModelUsernameAndPassword();
		assertTrue("username".equals(VC.model.getUsername()));
		assertFalse("not_username".equals(VC.model.getUsername()));
		assertTrue("password".equals(VC.model.getPassword()));
		assertFalse("not_password".equals(VC.model.getPassword()));
	}
}
