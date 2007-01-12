package com.idega.core.ldap.server.business;

/**
 * This bean has methods for starting and stopping the embedded ldap server and
 * getting its status <br>
 * 
 * @copyright Idega Software 2004
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson </a>
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.codehaus.plexus.ldapserver.server.EmbeddedLDAPServer;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;
import com.idega.util.SortedProperties;

public class EmbeddedLDAPServerBusinessBean extends IBOServiceBean implements EmbeddedLDAPServerBusiness,EmbeddedLDAPServerConstants {
	private EmbeddedLDAPServer server;
	private static final String LDAP_CONFIG_DIRECTORY_NAME = "ldap";
	private Properties ldapProps = null;
	private Properties backendProps = null;
	private String pathToConfigFiles = null;
	private static final String JAVA_LDAP_PROPS_FILE_NAME = "javaldap.prop";
	private static final String BACKENDS_PROPS_FILE_NAME = "backends.prop";

	public EmbeddedLDAPServerBusinessBean() {

	}

	/**
	 * Gets the absolut path to the IdegaWeb embedded ldap settings folder (in
	 * the core bundle)
	 * 
	 * @return
	 */
	public String getPathToLDAPConfigFiles() {
		if (this.pathToConfigFiles == null) {
			this.pathToConfigFiles = IWMainApplication.getDefaultIWMainApplication()
					.getCoreBundle().getPropertiesRealPath()
					+ FileUtil.getFileSeparator()
					+ LDAP_CONFIG_DIRECTORY_NAME
					+ FileUtil.getFileSeparator();
		}

		return this.pathToConfigFiles;
	}

	/**
	 * Starts the ldap server
	 */
	public boolean startEmbeddedLDAPServer() {
		if (this.server == null) {
			String pathToConfigFiles = getPathToLDAPConfigFiles();
			
			this.server = new EmbeddedLDAPServer(pathToConfigFiles);

			try {
				this.server.start();

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.err.println("EmbeddedLDAPServer already starter!");
		}

		return true;
	}

	/**
	 * Stops the ldap server (10 seconds wait for shutdown)
	 */
	public boolean stopEmbeddedLDAPServer() {
		if (this.server != null) {
			this.server.sendStopSignal();
			this.server.waitForShutdown(10000);
		}

		this.server = null;
		return true;
	}

	/**
	 * 
	 * @return Returnes true if the server is running, otherwise false
	 */
	public boolean isServerStarted() {
		if (this.server != null) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized void storeProperties(Properties props,
			String pathToSettingsFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(pathToSettingsFile);
		props.store(fos, null);
		fos.close();
	}

	public synchronized Properties loadProperties(String pathToSettingsFile)
			throws IOException {
		Properties properties = new SortedProperties();
		properties.load(new FileInputStream(pathToSettingsFile));
		return properties;
	}


	public Properties getLDAPSettings() throws IOException {

		if (this.ldapProps == null) {

			String pathToFile = getPathToLDAPConfigFiles()
					+ JAVA_LDAP_PROPS_FILE_NAME;
			this.ldapProps = loadProperties(pathToFile);

		}

		return this.ldapProps;
	}

	public Properties getBackendSettings() throws IOException {

		if (this.backendProps == null) {

			String pathToFile = getPathToLDAPConfigFiles()
					+ BACKENDS_PROPS_FILE_NAME;
			this.backendProps = loadProperties(pathToFile);

		}

		return this.backendProps;
	}

	/**
	 * Stores the java ldap settings
	 * @throws IOException
	 * 
	 */
	public void storeLDAPProperties() throws IOException {
		Properties props = getLDAPSettings();
		String pathToFile = this.pathToConfigFiles+JAVA_LDAP_PROPS_FILE_NAME;
		storeProperties(props,pathToFile);	
	}
	
	/**
	 * Stores the backend settings
	 * @throws IOException
	 * 
	 */
	public void storeBackendProperties() throws IOException {
		Properties props = getBackendSettings();
		String pathToFile = this.pathToConfigFiles+BACKENDS_PROPS_FILE_NAME;
		storeProperties(props,pathToFile);	
	}
	
	
	
	/**
	 * Creates the property with supplied default value if it does not exist
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getPropertyAndCreateIfDoesNotExist(Properties properties, String key, String defaultValue) {
		String value = properties.getProperty(key);
		if(value==null){
			properties.setProperty(key,defaultValue);
			value = defaultValue;
		}
		return value;
	}
	
	
	public DirectoryString getRootDN(){
		try {
			return new DirectoryString(getBackendSettings().getProperty(EmbeddedLDAPServerConstants.PROPS_BACKEND_ZERO_ROOT));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}