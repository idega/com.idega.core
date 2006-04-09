/**
 * 
 */
package com.idega.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;
import junit.framework.TestCase;
import junit.framework.TestResult;
import com.idega.idegaweb.IWMainApplicationStarter;
import com.idega.util.FileUtil;


/**
 * <p>
 * Test case that set-ups an embedded IdegaWeb application and loads up all
 * necessary resources before running a test case.
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:14 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class IWApplicationTestCase extends TestCase {

	IWMainApplicationStarter starter;
	File baseDir;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#run()
	 */
	public TestResult run() {
		// TODO Auto-generated method stub
		return super.run();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		//super.setUp();
		
		this.baseDir = initializeBaseDir();
		
		initializeDBProperties();
		
		ServletContext context = new MockServletContext(this.baseDir);
		this.starter = new IWMainApplicationStarter(context);
		this.starter.startup();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initializeDbProperties
	 * </p>
	 */
	protected void initializeDBProperties() {
		
		File realPropsDir = new File(this.baseDir,"WEB-INF/idegaweb/properties");
		realPropsDir.mkdirs();
		File realPropsFile = new File(realPropsDir,"db.properties");
		
		String propertiesFileToCopy = getDBPropertiesFilePath();
		if(propertiesFileToCopy==null){
			try {
				realPropsFile.createNewFile();

				Properties properties = new Properties();
				properties.setProperty("drivers","org.hsqldb.jdbcDriver");

				//properties.setProperty("default.url","jdbc:hsqldb:file:/{iw_application_path}/WEB-INF/idegaweb/databases/iw1");
				properties.setProperty("default.url","jdbc:hsqldb:mem:test1");
				properties.setProperty("default.user","sa");
				properties.setProperty("default.password","");
				properties.setProperty("default.initconns","2");
				properties.setProperty("default.maxconns","20");
				
				properties.store(new FileOutputStream(realPropsFile),null);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			File toCopy = new File(propertiesFileToCopy);
			try {
				realPropsFile.createNewFile();
				FileUtil.copyFile(toCopy,realPropsFile);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getPropertiesFile
	 * </p>
	 * @return
	 */
	protected String getDBPropertiesFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initializeBaseDir
	 * </p>
	 * @return
	 */
	private File initializeBaseDir() {
		//try {
			String sTmpDir = System.getProperty("java.io.tmpdir");
			File tmpDir = new File(sTmpDir);
			
			String tmpName = "idegawebtemp1/";
			File file =  new File(tmpDir,tmpName);
			file.mkdirs();
			return file;
		/*}
		catch (IOException e) {
			throw new RuntimeException(e);
		}*/
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		//super.tearDown();
	
		this.starter.shutdown();
		FileUtil.deleteFileAndChildren(this.baseDir);
	}
	
	
	public static void main(String[] args){
		//TestRunner.run(new IWTestCase());
		
		IWApplicationTestCase tCase = new IWApplicationTestCase();
		try {
			tCase.setUp();
			tCase.tearDown();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#countTestCases()
	 */
	public int countTestCases() {
		// TODO Auto-generated method stub
		return super.countTestCases();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#createResult()
	 */
	protected TestResult createResult() {
		// TODO Auto-generated method stub
		return super.createResult();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#run(junit.framework.TestResult)
	 */
	public void run(TestResult arg0) {
		// TODO Auto-generated method stub
		super.run(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#runBare()
	 */
	public void runBare() throws Throwable {
		// TODO Auto-generated method stub
		super.runBare();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#runTest()
	 */
	protected void runTest() throws Throwable {
		// TODO Auto-generated method stub
		super.runTest();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setName(java.lang.String)
	 */
	public void setName(String arg0) {
		// TODO Auto-generated method stub
		super.setName(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
