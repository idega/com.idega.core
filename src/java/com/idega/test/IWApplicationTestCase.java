/**
 * 
 */
package com.idega.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.faces.FactoryFinder;
import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.idega.core.test.base.IdegaBaseTest;
import com.idega.idegaweb.IWMainApplicationStarter;
import com.idega.util.FileUtil;


/**
 * <p>
 * Test case that set-ups an embedded IdegaWeb application and loads up all
 * necessary resources before running a test case.
 * </p>
 *  Last modified: $Date: 2008/11/06 22:00:24 $ by $Author: civilis $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class IWApplicationTestCase extends IdegaBaseTest {

	IWMainApplicationStarter starter;
	File baseDir;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		//super.setUp();
		
		this.baseDir = initializeBaseDir();
		
		initializeDBProperties();
		
		//Mock the ServletContext:
		ServletContext context = new MockServletContext(this.baseDir);
		
		//Mock the JSF ApplicationFactory object
		FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
		
		//Mock the Spring ApplicationContext and environment
		DefaultListableBeanFactory beanFactory= new DefaultListableBeanFactory();
		ApplicationContext appContext = new GenericWebApplicationContext(beanFactory);
//		TODO: this context should be aware of all spring testing framework environment
		((AbstractApplicationContext) appContext).refresh();
		
//		FIXME: use ELUtil, which is spring bean. Should be created by webapp, as it needs to have context
//		now it's created by spring test framework, and doesn't have iw application context
		//SpringBeanLookup.getInstance().setAppContext(appContext, context);
		
		//Starts the IWMainApplication:
		this.starter = new IWMainApplicationStarter(context);
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

}
