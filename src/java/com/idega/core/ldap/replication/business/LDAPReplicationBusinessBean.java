package com.idega.core.ldap.replication.business;

/**
 * This bean contains methods to replicate users and groups to the local IW User
 * system from other LDAP sources. <br>
 * 
 * @copyright Idega Software 2004
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson </a>
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.ejb.CreateException;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import com.idega.business.IBOServiceBean;
import com.idega.core.ldap.client.jndi.JNDIOps;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.util.Ldap;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.FileUtil;
import com.idega.util.timer.PastDateException;
import com.idega.util.timer.TimerEntry;
import com.idega.util.timer.TimerListener;
import com.idega.util.timer.TimerManager;

public class LDAPReplicationBusinessBean extends IBOServiceBean implements LDAPReplicationConstants,IWLDAPConstants,LDAPReplicationBusiness {

	private DirContext context;

	private Ldap ldap;

	private UserBusiness userBiz;

	private GroupBusiness groupBiz;

	private String thisServersLDAPBase;

	private IWApplicationContext iwac;

	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;

	private Properties repProps = null;

	private Map replicatorConnectionsMap;

	private Map replicatorTimerMap;

	private TimerManager scheduler;

	private static String STOP_REPLICATOR = "stop";

	private static String START_REPLICATOR = "start";
	
	private IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();

	public LDAPReplicationBusinessBean() {
	}

	/**
	 * Deletes the replicator that identified by the number supplied. Moves all
	 * values up one level and then delete the last replicator (its already been
	 * copied up) and STORES the settings
	 * 
	 * @param replicatorNumber
	 * @throws IOException
	 */
	public void deleteReplicator(int replicatorNumber) throws IOException {
		stopReplicator(replicatorNumber);
		//move all values up one level and then delete the last replicator (its
		// already been copied up)
		Properties props = getReplicationSettings();
		int num = getNumberOfReplicators();
		if (num != 1) {
			for (int i = replicatorNumber + 1; i <= num; i++) {
				int previous = i - 1;
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_BASE_RDN, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_HOST, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_PORT, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_REPLICATE_BASE_RDN, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_LAST_REPLICATED, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_NEXT_REPLICATED, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_INTERVAL_MINUTES, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_SCHEDULER_STRING, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_SEARCH_TIMEOUT_MS, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_AUTO_START, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_ROOT_USER, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_ROOT_PASSWORD, i, previous);
			}
		}
		removeAllPropertiesOfReplicator(num);
		setNumberOfReplicators(--num);
		storeReplicationProperties();
	}

	public void copyPropertyBetweenReplicators(String key, int copyFromReplicatorNumber, int copyToReplicatorNumber)
			throws IOException {
		Properties props = getReplicationSettings();
		props.setProperty(PROPS_REPLICATOR_PREFIX + copyToReplicatorNumber + key,
				props.getProperty(PROPS_REPLICATOR_PREFIX + copyFromReplicatorNumber + key));
	}

	public void removeAllPropertiesOfReplicator(int replicatorNumber) throws IOException {
		Properties props = getReplicationSettings();
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_RDN);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_UNIQUE_ID);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_HOST);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_PORT);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_REPLICATE_BASE_RDN);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_LAST_REPLICATED);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_NEXT_REPLICATED);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_INTERVAL_MINUTES);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SCHEDULER_STRING);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SEARCH_TIMEOUT_MS);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_AUTO_START);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_ROOT_USER);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_ROOT_PASSWORD);
	}

	/**
	 * @return the int value of PROPS_REPLICATION_NUM
	 * @throws IOException
	 */
	public int getNumberOfReplicators() throws IOException {
		String num = getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(getReplicationSettings(),
				PROPS_REPLICATION_NUM, "0");
		return Integer.parseInt(num);
	}

	/**
	 * Sets the int value of PROPS_REPLICATION_NUM
	 * 
	 * @throws IOException
	 */
	public void setNumberOfReplicators(int number) throws IOException {
		getReplicationSettings().setProperty(PROPS_REPLICATION_NUM, Integer.toString(number));
	}

	/**
	 * Starts and stores the replicator for the supplied replicatorNumber
	 */
	public boolean startReplicator(int replicatorNumber) throws IOException {
		return startOrStopReplicator(replicatorNumber, START_REPLICATOR);
	}

	/**
	 * Stops and removes the replicator for the supplied replicatorNumber
	 */
	public boolean stopReplicator(int replicatorNumber) throws IOException {
		return startOrStopReplicator(replicatorNumber, STOP_REPLICATOR);
	}

	/**
	 * Starts or stops a replicator
	 */
	private boolean startOrStopReplicator(final int replicatorNumber, String startOrStop) throws IOException {
		//initilize all variables
		final Integer repNum = new Integer(replicatorNumber);
		if (scheduler == null) {
			scheduler = new TimerManager();
		}
		
		replicatorConnectionsMap = getReplicatorConnectionsMap();
		replicatorTimerMap = getReplicatorTimerMap();
		
		Properties repProps = getReplicationSettings();
		final String host = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_HOST);
		final String port = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_PORT);
		final String userName = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_ROOT_USER);
		final String password = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_ROOT_PASSWORD);
		final String baseUniqueId = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber+PROPS_REPLICATOR_BASE_UNIQUE_ID);
		final String baseRDN = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_BASE_RDN);
		final String replicateBaseRDN = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_REPLICATE_BASE_RDN);
		final String intervalMinute = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_INTERVAL_MINUTES);
		final String schedulerCronString = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_SCHEDULER_STRING);
		final String searchEntryLimit = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT);
		final String searchTimeLimitInMs = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_SEARCH_TIMEOUT_MS);
		final String matchByUUID = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber
				+ PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID);
		
		//do stuff
		try {
			if (startOrStop.equals(START_REPLICATOR)) {
				//START REPLICATOR
				if (replicatorConnectionsMap.get(repNum) == null) {
					//todo add parentgroup
					return executeReplicator(replicatorNumber, repNum, host, port, userName, password, baseRDN,baseUniqueId, intervalMinute,schedulerCronString,true,null);
				}
				else {
					System.out.println("Replicator : " + repNum + " already started!");
				}
			}
			else if (startOrStop.equals(STOP_REPLICATOR)) {
				//STOP THE REPLICATOR
				JNDIOps connection = (JNDIOps) replicatorConnectionsMap.get(repNum);
				if(connection!=null){
					try {
						connection.close();
						replicatorConnectionsMap.remove(repNum);
						connection = null;
					}
					catch (NamingException e1) {
						e1.printStackTrace();
					}
				}
				TimerEntry timerEntry = (TimerEntry) replicatorTimerMap.get(repNum);
				if(timerEntry!=null){
					scheduler.removeTimer(timerEntry);
					replicatorTimerMap.remove(repNum);
					timerEntry = null;
				}
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (PastDateException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param replicatorNumber
	 * @param repNum
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param baseRDN
	 * @param intervalMinute
	 * @param schedulerString overrides intervalMinute
	 * @param repeat
	 * @return
	 * @throws PastDateException
	 */
	private boolean executeReplicator(final int replicatorNumber, final Integer repNum, final String host, final String port, final String userName, final String password, final String baseRDN, final String baseUniqueId, final String intervalMinute, final String schedulerString, final boolean repeat, final Group parentGroup) throws PastDateException {
		//don't run again if running
		if(!getReplicatorConnectionsMap().containsKey(repNum)){
			TimerEntry entry = null;
			if(schedulerString!=null && schedulerString.length()>=11){
				try {
					StringTokenizer tok = new StringTokenizer(schedulerString,",");
					
					String minute = tok.nextToken();
					String hour = tok.nextToken();
					String dayOfMonth = tok.nextToken();
					String month = tok.nextToken();
					String dayOfWeek = tok.nextToken();
					String year = tok.nextToken();
					//int minute, int hour, int dayOfMonth, int month,int dayOfWeek, int year
					
					entry = new TimerEntry(Integer.parseInt(minute),Integer.parseInt(hour),Integer.parseInt(dayOfMonth),Integer.parseInt(month),Integer.parseInt(dayOfWeek),Integer.parseInt(year), new TimerListener() {
						public void handleTimer(TimerEntry entry) {
							replicate(replicatorNumber, repNum, host, port, userName, password, baseRDN, baseUniqueId, parentGroup, entry);
						}
					});
				}
				catch (PastDateException e) {
					e.printStackTrace();
				}
			}
			else if(intervalMinute!=null){
			
				entry = new TimerEntry(Integer.parseInt(intervalMinute),repeat, new TimerListener() {
	
					public void handleTimer(TimerEntry entry) {
						replicate(replicatorNumber, repNum, host, port, userName, password, baseRDN,baseUniqueId, parentGroup, entry);
					}
				});
					
			}
			if(entry!=null){
				scheduler.addTimer(entry);
				replicatorTimerMap.put(repNum, entry);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			System.out.println("[LDAPReplication] " + new Date() + " - Tried to start replicator nr: "+ repNum+" again before finished");
			return false;//already running
		}
	}


	public void startAllReplicators() throws IOException {
		startOrStopAllReplicators(START_REPLICATOR);
	}

	public void startOrStopAllReplicators(String startOrStop) throws IOException {
		repProps = getReplicationSettings();
		String num = repProps.getProperty(PROPS_REPLICATION_NUM);
		int numberOfReplicators = Integer.parseInt(num);
		for (int i = 1; i <= numberOfReplicators; i++) {
			//only start autostarters
			if (startOrStop.equals(START_REPLICATOR)) {
				if (repProps.getProperty(PROPS_REPLICATOR_PREFIX + i + PROPS_REPLICATOR_AUTO_START).equals("true")) {
					startReplicator(i);
				}
			}
			else if (startOrStop.equals(STOP_REPLICATOR)) {
				stopReplicator(i);
			}
		}
	}

	public void stopAllReplicators() throws IOException {
		startOrStopAllReplicators(STOP_REPLICATOR);
	}

	public void replicateEntryAndChildrenRecursively(DN entryDN, JNDIOps jndiOps, Group parentGroup, String baseUniqueId) throws NamingException {
		//SearchControls searchControl = new SearchControls();
		//SearchControls.OBJECT_SCOPE; get info on the object itself
		//SearchControls.ONELEVEL_SCOPE; search children
		//SearchControls.SUBTREE_SCOPE; search all structure
		//searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// Set search controls to limit time to 120 seconds or 2 minutes
		// searchControl.setTimeLimit(120000);
		// NamingEnumeration searchResults =
		// context.search("","(dc=idega,dc=com)", searchControl);
		//NamingEnumeration searchResults
		// =jndiOps.searchOneLevel(thisServersLDAPBase,"objectClass=*",0,0);
		//		do the search
		
		//TODO copy the base object!
		NamingEnumeration searchResults = null;
		if(baseUniqueId==null || "".equals(baseUniqueId)){
			searchResults = jndiOps.searchOneLevel(entryDN.toString(), "objectClass=*", 0, 0);
		}
		else{
			searchResults = jndiOps.searchOneLevel(entryDN.toString(), LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID+"="+baseUniqueId, 0, 0);
		}
		
		//NamingEnumeration searchResults = basicOps.searchBaseObject(new
		// DN(thisServersLDAPBase),"*",0,0);
		if (searchResults != null) {
			while (searchResults.hasMore()) {
				Binding bd = (Binding) searchResults.next();
			
				String dn = getDNStringFromBinding(bd);
				//create a dn with the full path
				DN childDN = new DN(dn);
				childDN.addParentRDN(entryDN.toString());
				System.out.println(childDN.toString());
				//get the attributes for this DN
				Attributes childAttribs = jndiOps.read(childDN.toString());
				Group childGroup = null;
				try {
					if(ldapUtil.isUser(childDN)){
						if(parentGroup!=null){
							User user = getUserBusiness(iwac).createOrUpdateUser(childDN, childAttribs,parentGroup);
						}
						else{
							User user = getUserBusiness(iwac).createOrUpdateUser(childDN, childAttribs);
						}
					}
					else if(ldapUtil.isGroup(childDN)){
						if(parentGroup!=null){
							childGroup = getGroupBusiness(iwac).createOrUpdateGroup(childDN, childAttribs,parentGroup);
						}
						else{
							childGroup = getGroupBusiness(iwac).createOrUpdateGroup(childDN, childAttribs);	
						}
						
						
					}
					
//					if (childAttribs != null) {
//						NamingEnumeration attrlist = childAttribs.getAll();
//						while (attrlist.hasMore()) {
//							Attribute att = (Attribute) attrlist.next();
//							System.out.println("\t" + att.toString());
//						}
//					}
					
					if(childGroup!=null){
						String UUID = childGroup.getUniqueId();
						replicateEntryAndChildrenRecursively(childDN, jndiOps,childGroup,UUID);
					}
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				catch (CreateException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Removes the server part of the binding.getName() string so
	 * ldap://servername:port/ou=blah becomes ou=blah
	 * 
	 * @param binding
	 * @return the distinguished name from the full binding string
	 */
	private String getDNStringFromBinding(Binding binding) {
		String dn = binding.getName();
		int slashIndex = dn.lastIndexOf("/");
		if (slashIndex > 0) {
			dn = dn.substring(slashIndex + 1);
		}
		return dn;
	}

	/**
	 * Add a new replication server with the default values to the
	 * (replication.prop) settings
	 * 
	 * @throws IOException
	 */
	public void createNewReplicationSettings() throws IOException {
		try {
			Properties replicationProps = getReplicationSettings();
			int numberOfReplicators = getNumberOfReplicators();
			setNumberOfReplicators(++numberOfReplicators);
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_BASE_UNIQUE_ID, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_BASE_RDN, "dc=idega,dc=com");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_HOST, "localhost");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_PORT, "10389");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_REPLICATE_BASE_RDN, "false");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_LAST_REPLICATED, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_NEXT_REPLICATED, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_INTERVAL_MINUTES, "60");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_SCHEDULER_STRING, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_SEARCH_TIMEOUT_MS, "180000");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT, "0");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID, "true");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_AUTO_START, "true");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_ROOT_USER, "cn=Admin");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_ROOT_PASSWORD, "manager");
			storeReplicationProperties();
		}
		catch (NumberFormatException ex) {
			System.err.println("'" + PROPS_REPLICATION_NUM + "' must be an integer.");
		}
	}

	private GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}

	private UserBusiness getUserBusiness(IWApplicationContext iwc) {
		if (userBiz == null) {
			try {
				userBiz = (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return userBiz;
	}

	private EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness() {
		if (embeddedLDAPServerBiz == null) {
			try {
				embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) this.getServiceInstance(EmbeddedLDAPServerBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return embeddedLDAPServerBiz;
	}

	public Properties getReplicationSettings() throws IOException {
		if (repProps == null) {
			String pathToFile = getEmbeddedLDAPServerBusiness().getPathToLDAPConfigFiles()
					+ REPLICATION_PROPS_FILE_NAME;
			try {
				repProps = getEmbeddedLDAPServerBusiness().loadProperties(pathToFile);
			}
			catch (FileNotFoundException e) {
				FileUtil.createFile(pathToFile);//create the file if it does
				// not exist;
				System.out.println("LDAPManager: creating replication settings file : " + pathToFile);
				repProps = getEmbeddedLDAPServerBusiness().loadProperties(pathToFile);
			}
		}
		return repProps;
	}

	/**
	 * Stores the replication settings
	 * 
	 * @throws IOException
	 *  
	 */
	public void storeReplicationProperties() throws IOException {
		Properties props = getReplicationSettings();
		String pathToFile = getEmbeddedLDAPServerBusiness().getPathToLDAPConfigFiles() + REPLICATION_PROPS_FILE_NAME;
		getEmbeddedLDAPServerBusiness().storeProperties(props, pathToFile);
		repProps = null;//force reload
		getReplicationSettings();
	}
	
	private Map getReplicatorConnectionsMap(){
		if (replicatorConnectionsMap == null) {
			replicatorConnectionsMap = new HashMap();
		}
		
		return replicatorConnectionsMap;
	}
	
	private Map getReplicatorTimerMap(){
		if (replicatorTimerMap == null) {
			replicatorTimerMap = new HashMap();
		}
		
		return replicatorTimerMap;
	}

	private void replicate(final int replicatorNumber, final Integer repNum, final String host, final String port, final String userName, final String password, final String baseRDN, final String baseUniqueId, final Group parentGroup, TimerEntry entry) {
		//so it does not run again until we are done.
		entry.setCanRun(false);
		
		System.out.println("[LDAPReplication] " + new Date() + " - Starting replicator nr: "+ repNum);
		JNDIOps jndiOps;
		try {
			jndiOps = new JNDIOps("ldap://" + host + ":" + port, userName, password.toCharArray());
			replicatorConnectionsMap.put(repNum, jndiOps);
			replicateEntryAndChildrenRecursively(new DN(baseRDN), jndiOps, parentGroup,baseUniqueId);
			try {
				//finished stop the replicator
				stopReplicator(repNum.intValue());
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch (NamingException e) {
			e.printStackTrace();
			System.err.println("[LDAPReplication] " + new Date() + " - Replicator nr: " + repNum
					+ " failed. Stopping the replicator...");
			try {
				stopReplicator(replicatorNumber);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		//now we can run it again
		entry.setCanRun(true);
	}
}