// package com.wrox.connectionpool;
package com.idega.util.database;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.util.text.TextSoap;

/**
 *
 * @author <a href="http://www.wrox.com">wrox</a> modified by <a
 *         href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3 Class to deliver database connections through a connectionpool
 */
public class PoolManager implements Singleton {

	private static final Logger log = Logger.getLogger(PoolManager.class.getName());

	private static final String IW_APPLICATION_PATH_PLACE_HOLDER = "{iw_application_path}";// a
	private static final String IW_BUNDLES_PATH_PLACE_HOLDER = "{iw_bundles_path}";// a

	private static String DEFAULT_DSN = "default";

	static private PoolManager instance;
	//static private int clients;

	private static boolean isUnlocked = true;

	private List<Driver> drivers = new ArrayList<Driver>();
	private Map<String, ConnectionPool> pools = new HashMap<String, ConnectionPool>();
	private IWMainApplication iwma;

	private PoolManager() {
		init(System.getProperty("file.separator") + "db.properties");
	}

	private PoolManager(String propertiesFileLocation) {
		init(propertiesFileLocation);
	}

	private PoolManager(String propertiesFileLocation, IWMainApplication iwMainApplication) {
		this.iwma = iwMainApplication;

		init(propertiesFileLocation);
	}

	public static void lock() {
		isUnlocked = false;
	}

	public static void unlock() {
		isUnlocked = true;
	}

	static synchronized public PoolManager getInstance() {
		if (instance == null && isUnlocked) {
			instance = new PoolManager();
		}
		//clients++;
		return instance;
	}

	static synchronized public PoolManager getInstance(String propertiesFileLocation) {
		if (instance == null && isUnlocked) {
			instance = new PoolManager(propertiesFileLocation);
		}
		//clients++;
		return instance;
	}

	static synchronized public PoolManager getInstance(String propertiesFileLocation, IWMainApplication mainApplication) {
		if (instance == null && isUnlocked) {
			instance = new PoolManager(propertiesFileLocation, mainApplication);
		}
		//clients++;
		return instance;
	}

	private void init(String propertiesFile) {
		// InputStream is = getClass().getResourceAsStream(propertiesFile);
		InputStream is;
		Properties dbProps = new Properties();
		try {
			is = new FileInputStream(propertiesFile);
			dbProps.load(is);
		}
		catch (Exception e) {
			log.warning("Can't read the properties file from the specified location");
			return;
		}
		loadDrivers(dbProps);
		createPools(dbProps);
	}

	private void loadDrivers(Properties props) {
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			try {
				Driver driver = (Driver) RefactorClassRegistry.forName(driverClassName).newInstance();
				DriverManager.registerDriver(driver);
				this.drivers.add(driver);
				log.info( "Registered JDBC driver " + driverClassName);
			}
			catch (Exception e) {
				log.log(Level.WARNING, "Can't register JDBC driver: " + driverClassName, e);
			}
		}
	}

	private void createPools(Properties props) {
		Enumeration<?> propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");

				if (url == null) {
					log.warning("No URL specified for " + poolName);
					continue;
				}

				if (this.pools.containsKey(poolName)) {
					log.warning("Pool '" + poolName + "'already exists");
					continue;
				}
				// replace the {iw_application_path} variable with the real path to the
				// applications we folder
				if (this.iwma != null) {
					String applicationRealPath = this.iwma.getApplicationRealPath();
					String bundlesRealPath = this.iwma.getBundlesRealPath();
					// does not work because the string must be an expression
					// url.replaceAll(IW_APPLICATION_PATH_PLACE_HOLDER,
					// applicationRealPath);
					if (url.indexOf(IW_APPLICATION_PATH_PLACE_HOLDER) != -1) {
						url = TextSoap.findAndReplace(url, IW_APPLICATION_PATH_PLACE_HOLDER, applicationRealPath);
					}
					else if (url.indexOf(IW_BUNDLES_PATH_PLACE_HOLDER) != -1) {
						url = TextSoap.findAndReplace(url, IW_BUNDLES_PATH_PLACE_HOLDER, bundlesRealPath);
					}
				}
				String user = props.getProperty(poolName + ".user");
				String password = props.getProperty(poolName + ".password");
				String maxConns = props.getProperty(poolName + ".maxconns", "0");
				String sRefreshIntervalMinutes = props.getProperty(poolName + ".refreshminutes", "20");
				long lRefreshIntervalMillis;
				int max;
				try {
					max = Integer.valueOf(maxConns).intValue();
				}
				catch (NumberFormatException e) {
					log.warning("Invalid maxconns value " + maxConns + " for " + poolName);
					max = 0;
				}
				String initConns = props.getProperty(poolName + ".initconns", "0");
				int init;
				try {
					init = Integer.valueOf(initConns).intValue();
				}
				catch (NumberFormatException e) {
					log.warning("Invalid initconns value " + initConns + " for " + poolName);
					init = 0;
				}
				String loginTimeOut = props.getProperty(poolName + ".logintimeout", "5");
				int timeOut;
				try {
					timeOut = Integer.valueOf(loginTimeOut).intValue();
				}
				catch (NumberFormatException e) {
					log.warning("Invalid logintimeout value " + loginTimeOut + " for " + poolName);
					timeOut = 5;
				}
				try {
					int iRefreshIntervalMinutes = Long.valueOf(sRefreshIntervalMinutes).intValue();
					lRefreshIntervalMillis = iRefreshIntervalMinutes * 60 * 1000;
				}
				catch (NumberFormatException e) {
					lRefreshIntervalMillis = 20 * 1000 * 60;
					log.warning("Invalid refreshminutes value " + sRefreshIntervalMinutes + " for " + poolName);
					max = 0;
				}
				ConnectionPool pool = new ConnectionPool(poolName, url, user, password, max, init, timeOut, lRefreshIntervalMillis);
				this.pools.put(poolName, pool);
			}
		}
	}

	public Connection getConnection() {
		return getConnection(DEFAULT_DSN);
	}

	public Connection getConnection(String dataSourceName) {
		Connection conn = null;
		ConnectionPool pool = this.pools.get(dataSourceName);
		if (pool != null) {
			try {
				conn = pool.getConnection();
			}
			catch (SQLException e) {
				log.log(Level.WARNING, "Exception getting connection from " + dataSourceName, e);
			}
		}
		return conn;
	}

	public void freeConnection(String datasourceName, Connection con) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			pool.freeConnection(con);
		}
	}

	public void freeConnection(Connection connection) {
		freeConnection(DEFAULT_DSN, connection);
	}

	public synchronized void release() {
		// prevent creation
		PoolManager.lock();

		Collection<ConnectionPool> allPools = this.pools.values();
		for (ConnectionPool pool: allPools) {
			pool.release();
		}
		for (Driver driver: drivers) {
			try {
				DriverManager.deregisterDriver(driver);
				log.info("Deregistered JDBC driver " + driver.getClass().getName());
			}
			catch (SQLException e) {
				log.log(Level.WARNING, "Couldn't deregister JDBC driver: " + driver.getClass().getName(), e);
			}
		}
		instance = null;
	}

	// debug
	public String getStats(String name) {
		ConnectionPool tempPool = this.pools.get(name);
		return tempPool.getStats();
	}

	// Status of all pools:
	public String getStats() {
		String returnString = "";
		for (String name: this.pools.keySet()) {
			ConnectionPool tempPool = this.pools.get(name);
			returnString = returnString + "\nStatus of datasource " + name + " is: " + tempPool.getStats() + " ";
		}
		return returnString;
	}

	public Map<String, String> getStatsHashtable() {
		Map<String, String> table = new HashMap<String, String>();
		for (String name: this.pools.keySet()) {
			ConnectionPool tempPool = this.pools.get(name);
			table.put(name, "Status of datasource " + name + " is: " + tempPool.getStats() + " ");
		}
		return table;
	}

	public String[] getDatasources() {
		Collection<String> sources = new ArrayList<String>();
		for (String name: this.pools.keySet()) {
			sources.add(name);
		}
		return sources.toArray(new String[0]);
	}

	public boolean hasDatasource(String datasource) {
		return this.pools.containsKey(datasource);
	}

	public String getPasswordForPool(String datasourceName) {
		String password = null;
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			password = pool.getPassword();
		}
		return password;
	}

	public String getPasswordForPool() {
		return getPasswordForPool(DEFAULT_DSN);
	}

	public String getUserNameForPool(String datasourceName) {
		String userName = null;
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			userName = pool.getUserName();
		}
		return userName;
	}

	public String getUserNameForPool() {
		return getUserNameForPool(DEFAULT_DSN);
	}

	public String getURLForPool(String datasourceName) {
		String URL = null;
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			URL = pool.getURL();
		}
		return URL;
	}

	public String getURLForPool() {
		return getURLForPool(DEFAULT_DSN);
	}

	/**
	 *
	 * This only works if there is one driver definition in the db.properties file
	 *
	 * TODO: Fix this limitation
	 *
	 *
	 *
	 * public String getDriverClassForPool(String datasourceName){
	 *
	 * String driverClass = null;
	 *
	 * ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
	 *
	 * if (pool != null)
	 *  {
	 *
	 *
	 *
	 * driverClass = drivers.elementAt(0);
	 *
	 *
	 *  }
	 *
	 * return driverClass;
	 *  }
	 */
	public String getDriverClassForPool() {
		String driverClass = null;
		// ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
		if (this.drivers != null) {
			driverClass = this.drivers.get(0).getClass().getName();
		}
		return driverClass;
		// return getDriverClassForPool(DEFAULT_DSN);
	}

	public Connection recycleConnection(Connection conn) {
		return recycleConnection(conn, DEFAULT_DSN);
	}

	public Connection recycleConnection(Connection conn, String dataSourceName) {
		ConnectionPool pool = this.pools.get(dataSourceName);
		return pool.recycleConnection(conn);
	}

	/**
	 *
	 * Trims the pool so that it has only size connections
	 *
	 */
	public void trimTo(String datasourceName, int size) {
		trimTo(datasourceName, size, size, size);
	}

	/**
	 *
	 * Trims the pool so that it has only size,minSize,maxSize connections
	 *
	 */
	public void trimTo(String datasourceName, int size, int minSize, int maxSize) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			pool.trimTo(size, minSize, maxSize);
		}
	}

	/**
	 *
	 * Enlarges the pool so it has size number of connections
	 *
	 */
	public void enlargeTo(String datasourceName, int size) {
		this.enlargeTo(datasourceName, size, size, size);
	}

	/**
	 *
	 * Enlarges the pool so it has size number of connections
	 *
	 */
	public void enlargeTo(String datasourceName, int size, int minSize, int maxSize) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			pool.enlargeTo(size, minSize, maxSize);
		}
	}

	public int getCurrentConnectionCount(String datasourceName) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			return pool.getCurrentConnectionCount();
		}
		return 0;
	}

	public int getMaximumConnectionCount(String datasourceName) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			return pool.getMaximumConnectionCount();
		}
		return 0;
	}

	public int getMaximumConnectionCount() {
		return getMaximumConnectionCount(DEFAULT_DSN);
	}

	public int getMinimumConnectionCount(String datasourceName) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			return pool.getMinimumConnectionCount();
		}
		return 0;
	}

	public int getMinimumConnectionCount() {
		return getMinimumConnectionCount(DEFAULT_DSN);
	}

	public int getTimeOut(String datasourceName) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			return pool.getTimeOut();
		}
		return 0;
	}

	public void setTimeOut(String datasourceName, int timeout) {
		ConnectionPool pool = this.pools.get(datasourceName);
		if (pool != null) {
			pool.setTimeOut(timeout);
		}
	}
}
