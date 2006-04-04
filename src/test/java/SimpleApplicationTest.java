import junit.framework.TestResult;
import junit.textui.TestRunner;
import com.idega.idegaweb.IWMainApplication;
import com.idega.test.IWApplicationTestCase;

/**
 * <p>
 * Simple test that starts up an idegaWeb application.
 * </p>
 *  Last modified: $Date: 2006/04/04 11:53:02 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class SimpleApplicationTest extends IWApplicationTestCase{

	/* (non-Javadoc)
	 * @see com.idega.test.IWApplicationTestCase#runBare()
	 */
	public void testSimpleApp() throws Throwable {
		// TODO Auto-generated method stub
		
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		
		String testObj = "teststring1";
		iwma.setAttribute("testattr",testObj);
		Object gotObj = iwma.getAttribute("testattr");
		
		assertEquals(testObj,gotObj);
		
	}

	/* (non-Javadoc)
	 * @see com.idega.test.IWApplicationTestCase#getPropertiesFilePath()
	 */
	protected String getDBPropertiesFilePath() {
		// TODO Auto-generated method stub
		return super.getDBPropertiesFilePath();
	}


	/* (non-Javadoc)
	 * @see com.idega.test.IWApplicationTestCase#run()
	 */
	public TestResult run() {
		// TODO Auto-generated method stub
		return super.run();
	}

	/* (non-Javadoc)
	 * @see com.idega.test.IWApplicationTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see com.idega.test.IWApplicationTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public static void main(String[] args){
		TestRunner.run(new SimpleApplicationTest());
	}
}