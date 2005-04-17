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
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import com.idega.business.IBOServiceBean;
import com.idega.core.ldap.client.jndi.JNDIOps;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.util.Ldap;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;
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

	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;

	private Properties repProps = null;

	private Map replicatorConnectionsMap;

	private Map replicatorTimerMap;

	private TimerManager scheduler;

	private static String STOP_REPLICATOR = "stop";

	private static String START_REPLICATOR = "start";
	
	private IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();

	private Collection pluginsForGroup = null;
	private Collection pluginsForUser = null;
	
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
		getReplicationSettings();
		int num = getNumberOfReplicators();
		if (num != 1) {
			for (int i = replicatorNumber + 1; i <= num; i++) {
				int previous = i - 1;
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_BASE_RDN, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_BASE_UNIQUE_ID, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_BASE_GROUP_ID, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_PARENT_GROUP_ID, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_HOST, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_PORT, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_REPLICATE_BASE_RDN, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_LAST_REPLICATED, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_NEXT_REPLICATED, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_INTERVAL_MINUTES, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_SCHEDULER_STRING, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_REPEAT, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_SEARCH_TIMEOUT_MS, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID, i, previous);
				copyPropertyBetweenReplicators(PROPS_REPLICATOR_ACTIVE, i, previous);
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
	
	public void setReplicationProperty(String key, int replicatorNumber, String value)throws IOException {
		Properties props = getReplicationSettings();
		props.setProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + key,value);
		storeReplicationProperties();
	}
	
	public String getReplicationProperty(String key, int replicatorNumber)throws IOException {
		Properties props = getReplicationSettings();
		return props.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + key);
	}

	public void removeAllPropertiesOfReplicator(int replicatorNumber) throws IOException {
		Properties props = getReplicationSettings();
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_RDN);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_UNIQUE_ID);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_GROUP_ID);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_PARENT_GROUP_ID);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_HOST);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_PORT);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_REPLICATE_BASE_RDN);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_LAST_REPLICATED);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_NEXT_REPLICATED);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_INTERVAL_MINUTES);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SCHEDULER_STRING);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_REPEAT);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SEARCH_TIMEOUT_MS);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID);
		props.remove(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_ACTIVE);
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
		final String userName = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_ROOT_USER);
		final String password = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_ROOT_PASSWORD);
		final String baseUniqueId = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber+PROPS_REPLICATOR_BASE_UNIQUE_ID);
		final String baseRDN = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_RDN);
		final String baseGroupToOverwriteId = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_BASE_GROUP_ID);
		final String parentGroupToWriteUnder = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_PARENT_GROUP_ID);
		final String replicateBaseRDN = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_REPLICATE_BASE_RDN);
		final String interval = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_INTERVAL_MINUTES);
		final String schedulerCronString = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SCHEDULER_STRING);
		final String repeatReplication = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber+ PROPS_REPLICATOR_REPEAT);
		final String searchEntryLimit = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT);
		final String searchTimeLimitInMs = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_SEARCH_TIMEOUT_MS);
		//this will force lookup by uniqueid
		final String matchByUUID = repProps.getProperty(PROPS_REPLICATOR_PREFIX + replicatorNumber + PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID);
		
		
		final int maxEntrylimit = (searchEntryLimit!=null && !"".equals(searchEntryLimit))? Integer.parseInt(searchEntryLimit) : 0;
		final int searchTimeLimit = (searchTimeLimitInMs!=null && !"".equals(searchTimeLimitInMs))? Integer.parseInt(searchTimeLimitInMs) : 0;
		final int intervalMinute = (interval!=null && !"".equals(interval))? Integer.parseInt(interval) : 0;

		final boolean replicateBase = (replicateBaseRDN!=null && ("Y".equalsIgnoreCase(replicateBaseRDN) || "true".equalsIgnoreCase(replicateBaseRDN)));
		final boolean repeat = (repeatReplication!=null && ("Y".equalsIgnoreCase(repeatReplication) || "true".equalsIgnoreCase(repeatReplication)));
		final boolean forceUUID = (matchByUUID!=null && ("Y".equalsIgnoreCase(matchByUUID) || "true".equalsIgnoreCase(matchByUUID)));
		
		
		//do stuff
		try {
			if (startOrStop.equals(START_REPLICATOR)) {
				//START REPLICATOR
				if (replicatorConnectionsMap.get(repNum) == null) {
					//todo add parentgroup
					Group parentGroup = null;
					Group baseGroup = null;
					if(parentGroupToWriteUnder!=null && !"".equals(parentGroupToWriteUnder)){
						try {
							parentGroup = getGroupBusiness().getGroupByGroupID(Integer.parseInt(parentGroupToWriteUnder));
						}
						catch (FinderException e1) {
							e1.printStackTrace();
						}
					}
					
					if(baseGroupToOverwriteId!=null && !"".equals(baseGroupToOverwriteId)){
						try {
							baseGroup = getGroupBusiness().getGroupByGroupID(Integer.parseInt(baseGroupToOverwriteId));
						}
						catch (FinderException e1) {
							e1.printStackTrace();
						}
					}
					
					return executeReplicator(replicatorNumber, repNum, host, port, userName, password, baseRDN, replicateBase, baseUniqueId, intervalMinute,schedulerCronString,repeat,parentGroup,baseGroup,maxEntrylimit,searchTimeLimit,forceUUID);
				}
				else {
					log("Replicator : " + repNum + " already started!");
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
				return true;
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
	 * @param parentGroup
	 * @param baseGroupToOverwrite
	 * @param maxEntrylimit
	 * @param searchTimeLimit
	 * @return true if the replicator worked
	 * @throws PastDateException
	 */
	protected boolean executeReplicator(final int replicatorNumber, final Integer repNum, final String host, final String port, final String userName, final String password, final String baseRDN, final boolean replicateBaseRDN, final String baseUniqueId, final int intervalMinute, final String schedulerString, final boolean repeatReplication, final Group parentGroup, final Group baseGroupToOverwrite, final int maxEntrylimit,final int searchTimeLimit, final boolean onlyUseUUID) throws PastDateException {
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
							log("[LDAPReplication] " + new Date() + " - Starting replicator nr: "+ repNum+" "+host+" "+baseRDN+ " "+baseUniqueId);
							
							try {
								setReplicationProperty(PROPS_REPLICATOR_LAST_REPLICATED,replicatorNumber,IWTimestamp.getTimestampRightNow().toString());
							}
							catch (IOException e1) {
								e1.printStackTrace();
							}
							replicate(replicatorNumber, repNum, host, port, userName, password, baseRDN,replicateBaseRDN, baseUniqueId, parentGroup, baseGroupToOverwrite, maxEntrylimit, searchTimeLimit, onlyUseUUID, entry);
							
							log("[LDAPReplication] " + new Date() + " - Stopping replicator nr: "+ repNum+" "+host+" "+baseRDN+ " "+baseUniqueId);
							
							
							//todo figure out the next replication date
							//probably from the entry.get...
//							try {
//								IWTimestamp stamp = new IWTimestamp(getReplicationProperty(PROPS_REPLICATOR_LAST_REPLICATED,replicatorNumber));
//								stamp.addMinutes(interval);
//								setReplicationProperty(PROPS_REPLICATOR_NEXT_REPLICATED,replicatorNumber,stamp.toString());
//							}
//							catch (IOException e1) {
//								e1.printStackTrace();
//							}
							
						}
					});
				}
				catch (PastDateException e) {
					e.printStackTrace();
				}
			}
			else if(intervalMinute>0){
				entry = new TimerEntry(intervalMinute,repeatReplication, new TimerListener() {
	
					public void handleTimer(TimerEntry entry) {
						log("[LDAPReplication] " + new Date() + " - Starting replicator nr: "+ repNum+" "+host+" "+baseRDN+ " "+baseUniqueId);
						try {
							setReplicationProperty(PROPS_REPLICATOR_LAST_REPLICATED,replicatorNumber,IWTimestamp.getTimestampRightNow().toString());
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
						
						replicate(replicatorNumber, repNum, host, port, userName, password, baseRDN, replicateBaseRDN, baseUniqueId, parentGroup, baseGroupToOverwrite, maxEntrylimit, searchTimeLimit, onlyUseUUID, entry);
						
						try {
							IWTimestamp stamp = new IWTimestamp(getReplicationProperty(PROPS_REPLICATOR_LAST_REPLICATED,replicatorNumber));
							stamp.addMinutes(intervalMinute);
							setReplicationProperty(PROPS_REPLICATOR_NEXT_REPLICATED,replicatorNumber,stamp.toString());
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
						log("[LDAPReplication] " + new Date() + " - Stopping replicator nr: "+ repNum+" "+host+" "+baseRDN+ " "+baseUniqueId);
						
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
			log("[LDAPReplication] " + new Date() + " - Tried to start replicator nr: "+ repNum+" again before finished");
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
				String auto = repProps.getProperty(PROPS_REPLICATOR_PREFIX + i + PROPS_REPLICATOR_ACTIVE);
				boolean autostart = (auto!=null && ("Y".equalsIgnoreCase(auto) || "true".equalsIgnoreCase(auto)));
				if (autostart) {
					startReplicator(i);
				}
			}
			else if (startOrStop.equals(STOP_REPLICATOR)) {
				stopReplicator(i);
			}
		}
		
		if(startOrStop.equals(STOP_REPLICATOR)){
			if(scheduler!=null){
				scheduler.removeAllTimers();
				scheduler = null;
			}
		}
	}

	public void stopAllReplicators() throws IOException {
		startOrStopAllReplicators(STOP_REPLICATOR);
	}
	
	protected Group replicateOneGroupEntry(DN entryDN, JNDIOps jndiOps, Group parentGroup, String baseUniqueId, Group baseGroupToOverwrite, int maxEntrylimit, int searchTimeLimit, boolean onlyUseUUID) throws RemoteException, CreateException, NamingException {
		Group group = null;
		NamingEnumeration searchResults = null;
		String baseDNString = entryDN.toString();
		
		
		if( (baseUniqueId!=null && !"".equals(baseUniqueId)) && onlyUseUUID){
			searchResults = jndiOps.searchBaseEntry(baseDNString, "(&("+LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID+"="+baseUniqueId+")("+LDAP_ATTRIBUTE_OBJECT_CLASS+"="+LDAP_SCHEMA_ORGANIZATIONAL_UNIT+"))", maxEntrylimit, searchTimeLimit,null);
		}
		else{
			searchResults = jndiOps.searchBaseEntry(baseDNString, LDAP_ATTRIBUTE_OBJECT_CLASS+"=*", maxEntrylimit, searchTimeLimit,null);
		}
		
		if (searchResults != null) {
			while (searchResults.hasMore()) {
				SearchResult result = (SearchResult) searchResults.next();
				
				//get the attributes for this DN
				Attributes attribs = result.getAttributes();
				
				logDebug(entryDN.toString());
				
				if(attribs==null){
					//load the attributes
					attribs= jndiOps.read(entryDN.toString());
				}
				//TODO if baseGroupToOverwrite != null update that group
				//just set the unique id and entry dn to that group if no other group has it already
				
				
				if(ldapUtil.isGroup(entryDN)){
					group = createOrUpdateGroup(parentGroup,baseGroupToOverwrite,attribs,entryDN,baseUniqueId);
				}
			}
		}
		
		return group;
		
	}
	
	
	protected void replicateChildEntriesRecursively(DN entryDN, JNDIOps jndiOps, Group parentGroup, String baseUniqueId, int maxEntrylimit, int searchTimeLimit, boolean onlyUseUUID) throws RemoteException, CreateException, NamingException {
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
		//System.out.println("Current encoding: "+System.getProperty("file.encoding"));
		
		NamingEnumeration searchResults = null;
		String baseDNString = entryDN.toString();
		if( (baseUniqueId!=null && !"".equals(baseUniqueId)) && onlyUseUUID){
			searchResults = jndiOps.searchOneLevel(baseDNString,LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID+"="+baseUniqueId, maxEntrylimit, searchTimeLimit,null);
		}
		else{
			searchResults = jndiOps.searchOneLevel(baseDNString,LDAP_ATTRIBUTE_OBJECT_CLASS+"=*", maxEntrylimit, searchTimeLimit,null);
		}
		
		//NamingEnumeration searchResults = basicOps.searchBaseObject(new
		// DN(thisServersLDAPBase),"*",0,0);
		if (searchResults != null) {
			while (searchResults.hasMore()) {
				SearchResult result = (SearchResult) searchResults.next();
				
//				get the attributes for this DN
				Attributes childAttribs = result.getAttributes();
				//get the dn for the entry
				String dn = getDNStringFromSearchResult(result,childAttribs);
				//create the child dn with the full parent dn path
				DN childDN = new DN(dn);
				childDN.addParentRDN(entryDN.toString());
				logDebug(childDN.toString());
				
				
				if(childAttribs==null){
					//load the attributes
					childAttribs= jndiOps.read(childDN.toString());
				}
				
				Group childGroup = null;
					if(ldapUtil.isUser(childDN)){
						createOrUpdateUser(parentGroup, childAttribs, childDN);
					}
					else if(ldapUtil.isGroup(childDN)){
						childGroup = createOrUpdateGroup(parentGroup, null, childAttribs, childDN, baseUniqueId);
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
						replicateChildEntriesRecursively(childDN, jndiOps, childGroup, UUID, maxEntrylimit, searchTimeLimit,onlyUseUUID);
					}
			}
		}
	}

	/**
	 * Creates or updates a group from the attributes and distinguished name.
	 * Also calls methods in registered UserGroupPlugins
	 * @param parentGroup
	 * @param childAttribs
	 * @param childDN
	 * @param uniqueId
	 * @return the group
	 * @throws CreateException
	 * @throws NamingException
	 * @throws RemoteException
	 */
	protected Group createOrUpdateGroup(Group parentGroup, Group baseGroupToOverwrite, Attributes entryAttribs, DN entryDN, String entryUniqueId) throws CreateException, NamingException, RemoteException {
		Group group;
		
		//if we will overwrite a group then we do not use the parent group because we it must already have a parent group
		//since we chose it in the group chooser
		if(baseGroupToOverwrite!=null){
			parentGroup = null;
			if(entryUniqueId!=null && !"".equals(entryUniqueId)){
				baseGroupToOverwrite.setUniqueId(entryUniqueId);
			}
			if(entryDN!=null){
				baseGroupToOverwrite.setMetaData(IWLDAPConstants.LDAP_META_DATA_KEY_DIRECTORY_STRING,entryDN.toString().toLowerCase());
			}	
			baseGroupToOverwrite.store();
		}
		
		if(parentGroup!=null){
			group = getGroupBusiness().createOrUpdateGroup(entryDN, entryAttribs,parentGroup);
		}
		else{
			group = getGroupBusiness().createOrUpdateGroup(entryDN, entryAttribs);	
		}
		
		getGroupBusiness().callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(group);
		
		return group;
	}

	/**
	 * Creates or updates a user from the attributes and distinguished name.
	 * Also calls methods in registered UserGroupPlugins
	 * @param parentGroup
	 * @param childAttribs
	 * @param childDN
	 * @return the user
	 * @throws RemoteException
	 * @throws CreateException
	 * @throws NamingException
	 */
	protected User createOrUpdateUser(Group parentGroup, Attributes childAttribs, DN childDN) throws RemoteException, CreateException, NamingException {
		User user = null;
		if(parentGroup!=null){
			user = getUserBusiness().createOrUpdateUser(childDN, childAttribs,parentGroup);
		}
		else{
			user = getUserBusiness().createOrUpdateUser(childDN, childAttribs);
		}
		
		getUserBusiness().callAllUserGroupPluginAfterUserCreateOrUpdateMethod(user);
		
		return user;
	}

	/**
	 * Returns a cleaned DN, escaped and with the servername removed if needed
	 * @param result
	 * @return the distinguished name from the full binding string
	 */
	private String getDNStringFromSearchResult(SearchResult result, Attributes attr) {
		//TODO remove server crap, it never happens any more?
		String dn = result.getName();
		dn = TextSoap.findAndReplace(dn,"\"","");
		
		try {
			//TODO just testing remove if does nothing
			dn = new String(dn.getBytes(),"UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
//		
//			int startindex = dn.indexOf("://");
//			int slashIndex = dn.indexOf("/",startindex+3);
//			if (slashIndex > 0) {
//				dn = dn.substring(slashIndex + 1);
//				return dn;
//			}
//		}
//		else{
//			dn = dn.replaceAll("\"","");
//			
//			
//		}
		
		
		
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
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_BASE_GROUP_ID, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_PARENT_GROUP_ID, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_HOST, "localhost");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_PORT, "10389");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_REPLICATE_BASE_RDN, "true");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_LAST_REPLICATED, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_NEXT_REPLICATED, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_INTERVAL_MINUTES, "60");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_SCHEDULER_STRING, "");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_REPEAT, "false");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_SEARCH_TIMEOUT_MS, "180000");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT, "0");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID, "true");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_ACTIVE, "false");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_ROOT_USER, "cn=Admin");
			getEmbeddedLDAPServerBusiness().getPropertyAndCreateIfDoesNotExist(replicationProps,
					PROPS_REPLICATOR_PREFIX + numberOfReplicators + PROPS_REPLICATOR_ROOT_PASSWORD, "manager");		
			storeReplicationProperties();
		}
		catch (NumberFormatException ex) {
			logError("'" + PROPS_REPLICATION_NUM + "' must be an integer.");
		}
	}

	private GroupBusiness getGroupBusiness() {
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) this.getServiceInstance(GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}

	private UserBusiness getUserBusiness() {
		if (userBiz == null) {
			try {
				userBiz = (UserBusiness)  this.getServiceInstance(UserBusiness.class);
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
				logConfig("LDAPManager: creating replication settings file : " + pathToFile);
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

	private void replicate(final int replicatorNumber, final Integer repNum, final String host, final String port, final String userName, final String password, final String baseRDN, final boolean replicateBaseRDN, final String baseUniqueId, final Group parentGroup, final Group baseGroupToOverwrite, final int maxEntrylimit, final int searchTimeLimit, final boolean onlyUseUUID, TimerEntry entry) {
		//so it does not run again until we are done.
		entry.setCanRun(false);
		
		logConfig("[LDAPReplication] " + new Date() + " - Starting replicator nr: "+ repNum+" host:"+host+ " base rdn:"+baseRDN);
		JNDIOps jndiOps;
		try {
			jndiOps = new JNDIOps("ldap://" + host + ":" + port, userName, password.toCharArray());
			replicatorConnectionsMap.put(repNum, jndiOps);
			DN baseDN = new DN(baseRDN);
			if(replicateBaseRDN){
				Group updatedParent = replicateOneGroupEntry((DN)baseDN.clone(), jndiOps, parentGroup, baseUniqueId, baseGroupToOverwrite, maxEntrylimit, searchTimeLimit, onlyUseUUID);
				if(updatedParent!=null){
					if(baseUniqueId==null || "".equals(baseUniqueId)){
						try {
							setReplicationProperty(PROPS_REPLICATOR_BASE_UNIQUE_ID, repNum.intValue(), updatedParent.getUniqueId());
						}
						catch (IOException e2) {
							e2.printStackTrace();
						}
						
						replicateChildEntriesRecursively((DN)baseDN.clone(), jndiOps,updatedParent,updatedParent.getUniqueId(), maxEntrylimit, searchTimeLimit, onlyUseUUID);
					}
					else{
						replicateChildEntriesRecursively(baseDN, jndiOps,updatedParent,baseUniqueId, maxEntrylimit, searchTimeLimit, onlyUseUUID);
					}
				}				
			}
			else{
				replicateChildEntriesRecursively(baseDN, jndiOps, parentGroup,baseUniqueId, maxEntrylimit, searchTimeLimit, onlyUseUUID);
			}
			
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
			logError("[LDAPReplication] " + new Date() + " - Replicator nr: " + repNum+" host:"+host+ " base rdn:"+baseRDN+ " failed. Stopping the replicator...");
			try {
				stopReplicator(replicatorNumber);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
			logError("[LDAPReplication] " + new Date() + " - Replicator nr: " + repNum+" host:"+host+ " base rdn:"+baseRDN+ " failed. Stopping the replicator...");
			try {
				stopReplicator(replicatorNumber);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch (CreateException e) {
			e.printStackTrace();
			logError("[LDAPReplication] " + new Date() + " - Replicator nr: " + repNum+" host:"+host+ " base rdn:"+baseRDN+ " failed. Stopping the replicator...");
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