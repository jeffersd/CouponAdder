package test;

import static org.junit.Assert.*;

import org.junit.Test;

import view.ViewController;

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
		VC.setUsernameField("username");
		VC.setPasswordField("password");
		VC.giveModelUsernameAndPassword();
		assertTrue("username".equals(VC.model.getUsername()));
		assertFalse("not_username".equals(VC.model.getUsername()));
		assertTrue("password".equals(VC.model.getPassword()));
		assertFalse("not_password".equals(VC.model.getPassword()));
	}
	
	@Test
	public void usernameFieldMutatorsTest() {
		ViewController VC = new ViewController();
		VC.setUsernameField("username");
		assertTrue("username".equals(VC.getUsernameField()));
		assertFalse("another".equals(VC.getUsernameField()));
	}
	
	@Test
	public void passwordFieldMutatorsTest() {
		ViewController VC = new ViewController();
		VC.setPasswordField("password");
		assertTrue("password".equals(VC.getPasswordField()));
		assertFalse("another".equals(VC.getPasswordField()));
	}
	
	
}
