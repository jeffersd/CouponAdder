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
	
	
}
