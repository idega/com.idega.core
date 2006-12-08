import junit.textui.TestRunner;
import com.idega.idegaweb.IWMainApplication;
import com.idega.test.IWApplicationTestCase;

/**
 * <p>
 * Simple test that starts up an idegaWeb application.
 * </p>
 *  Last modified: $Date: 2006/12/08 10:32:11 $ by $Author: gediminas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SimpleApplicationTest extends IWApplicationTestCase{

	public void testSimpleApp() throws Throwable {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		
		String testObj = "teststring1";
		iwma.setAttribute("testattr",testObj);
		Object gotObj = iwma.getAttribute("testattr");
		
		assertEquals(testObj,gotObj);		
	}
	
	public static void main(String[] args){
		TestRunner.run(new SimpleApplicationTest());
	}
}