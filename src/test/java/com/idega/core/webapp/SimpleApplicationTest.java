package com.idega.core.webapp;
import junit.textui.TestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.idega.idegaweb.IWMainApplication;
import com.idega.test.IWApplicationTestCase;

/**
 * <p>
 * Simple test that starts up an idegaWeb application.
 * </p>
 *  Last modified: $Date: 2008/07/02 19:27:58 $ by $Author: civilis $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleApplicationTest extends IWApplicationTestCase {
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testSimpleApp() throws Throwable {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		
		String testObj = "teststring1";
		iwma.setAttribute("testattr",testObj);
		Object gotObj = iwma.getAttribute("testattr");
		
		assertEquals(testObj,gotObj);		
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static void main(String[] args){
		TestRunner.run(new SimpleApplicationTest());
	}
}