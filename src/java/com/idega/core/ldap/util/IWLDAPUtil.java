/*
 * Created on Jun 30, 2004
 */
package com.idega.core.ldap.util;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import org.codehaus.plexus.ldapserver.server.util.DNUtility;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.replication.business.LDAPReplicationConstants;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.text.TextSoap;

/**
 * TODO change into a service bean
 * A singleton helper class for all sorts of LDAP stuff.
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public class IWLDAPUtil implements IWLDAPConstants,EmbeddedLDAPServerConstants,LDAPReplicationConstants, Singleton{
	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;
	private LDAPReplicationBusiness ldapReplicationBiz;
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new IWLDAPUtil(); }};

	/**
	 * 
	 */
	private IWLDAPUtil() {
		super();
	}
	
	public static IWLDAPUtil getInstance(){
		return (IWLDAPUtil) SingletonRepository.getRepository().getInstance(IWLDAPUtil.class, instantiator);
	}
	
	/**
	 * Return the string value of the attribute requested, null if not found.
	 * @param attributeKey ou, givenName for example
	 * @param attributes 
	 * @return
	 */
	public String getSingleValueOfAttributeByAttributeKey(String attributeKey, Attributes attributes){
		try {
			Attribute attr = attributes.get(attributeKey);
			if(attr!=null){
				
				Object obj = attr.get();
				if(obj instanceof byte[]){
					return new String((byte[])obj);
				}
				
				return (String) obj;
			}
		} catch (Exception e) {
			//System.out.println(e.getClass() + ": " + e.getMessage());
			System.out.println("[IWLDAPUtil] "+attributeKey+" not defined in attributes");
		}
		
		return null;
		
	}
	
	/**
	 * @param dn
	 * @return true if the directory string refers to a group, e.g. starts with
	 *         either the o="idega" or ou="developers" attributes for groups
	 */
	public boolean isGroup(DirectoryString dn) {
		String directoryString = dn.getDirectoryString();
		//organization
		boolean isGroup = doesDNStartWithKey("o=",directoryString);
		
		//organizationalUnit
		if(!isGroup){
			isGroup = doesDNStartWithKey("ou=",directoryString);
		}
		//location
		if(!isGroup){
			isGroup = doesDNStartWithKey("l=",directoryString);
		}
		
		return isGroup;
	}
	
	/**
	 * @param dn
	 * @return true if the directory string refers to a user/person, e.g. starts
	 *         with the cn="name" attribute
	 */
	public boolean isUser(DirectoryString dn) {
		return doesDNStartWithKey("cn=",dn.getDirectoryString());
	}
	
	private boolean doesDNStartWithKey(String key,String ldapDirectoryString){
		int index = ldapDirectoryString.toLowerCase().indexOf(key);
		if(index==0){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * @param dn
	 * @return true if the directory string refers to a user/person, e.g. starts
	 *         with the cn="name" attribute
	 */
	public boolean isUser(DN dn) {
		return isUser(convertDNToDirectoryString(dn));
	}
	
	/**
	 * @param dn
	 * @return true if the directory string refers to a group, e.g. starts with
	 *         either the o="idega" or ou="developers" attributes for groups
	 */
	public boolean isGroup(DN dn) {
		return isGroup(convertDNToDirectoryString(dn));
	}
	
	public DirectoryString convertDNToDirectoryString(DN dn){
		return new DirectoryString(dn.toString());
	}

	public DN convertDirectoryStringToDN(DirectoryString directoryString){
		return new DN(directoryString.getDirectoryString());
	}
	
	/**
	 * Gets the (idegaweb)ldapmetadata prefix + thekey
	 * @param att
	 * @return
	 */
	public String getAttributeKeyWithMetaDataNamePrefix(Attribute att){
		return LDAP_META_DATA_KEY_PREFIX+att.getID();
	}
	
	/**
	 * Gets the attribute key - (idegaweb)ldapmetadata prefix
	 * @param att
	 * @return
	 */
	public String getAttributeKeyWithoutMetaDataNamePrefix(String key){
		int index = key.indexOf(LDAP_META_DATA_KEY_PREFIX);
		
		if(index>=0){
			return key.substring(LDAP_META_DATA_KEY_PREFIX.length(),key.length());
		}
		else {
			return key;
		}
	}
	
	/**
	 * Gets the attribute key - (idegaweb)ldapmetadata prefix
	 * @param att
	 * @return
	 */
	public String getAttributeKeyWithoutMetaDataNamePrefix(Attribute att){
		return getAttributeKeyWithoutMetaDataNamePrefix(att.getID());
	}
	
	/**
	 * Gets the key from the attribute
	 * @param att
	 * @return
	 */
	public String getAttributeKey(Attribute att){
		return att.getID();
	}
	
	
	/**
	 * Checks if the supplied attributes key is really a meta data key from a IW EmbeddedLDAPServer
	 * @param att
	 * @return
	 */
	public boolean isAttributeIWMetaDataKey(Attribute att){
		return isAttributeIWMetaDataKey(att.getID());
	}
	/**
	 * Checks if the supplied attributes key is really a meta data key from a IW EmbeddedLDAPServer
	 * @param att
	 * @return
	 */
	public boolean isAttributeIWMetaDataKey(String key){
		return key.startsWith(LDAP_META_DATA_KEY_PREFIX);
	}
		
	/**
	 * Gets the (idegaweb)ldapmetadata prefix + thekey
	 * @param thekey
	 * @return
	 */
	public String getAttributeKeyWithMetaDataNamePrefix(String theKey){
		if(isAttributeIWMetaDataKey(theKey)){
			//so we don't prefix again
			return theKey;
		}
		else{
			return LDAP_META_DATA_KEY_PREFIX+theKey;
		}	
	}
		

	/**
	 * @param attributes
	 * @return
	 */
	public String getNameOfGroupFromAttributes(Attributes attributes) {
		String name = getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_ORGANIZATION_UNIT,attributes);
	  	if(name==null){
	  		name = getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_ORGANIZATION,attributes);
	  		
	  		if(name==null){
	  			name = getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_LOCATION,attributes);
	  		}
	  	}
	  	
	  	return name;
	}
	
	/**
	 *  A naming convention, such as that for the LDAP or the file system, typically has meta characters. For example, in the LDAP, if one of the following characters appears in the name, then it must be preceded by the escape character, the backslash character ("\"):<br>
		 		<li>A space or "#" character occurring at the beginning of the string
		 		<li>A space character occurring at the end of the string
		 		<li>One of the characters ",", "+", """, "\", "<", ">" or ";"
	This method escapes those characters	 		
		 
	 * @param directoryString Example DN: ou=Group of people aged 13+,dc=idega,dc=com
	 * @return
	 */
	public String getEscapedLDAPString(String directoryString){
//		if(directoryString.startsWith(" ")){
//			directoryString.replaceFirst(" ","\\ ");
//		}
//		else if(directoryString.startsWith("#")){
//			directoryString.replaceFirst("#","\\#");
//		}
//		directoryString.replaceAll("\\+","\\+");
//		directoryString.replaceAll("\\\"","\\\"");
//		directoryString.replaceAll("\\>","\\>");
//		directoryString.replaceAll("\\<","\\<");
//		directoryString.replaceAll("\\;","\\;");
//		directoryString.replaceAll("\\\\","\\\\");
		
		
		directoryString = removeTrailingSpaces(directoryString);
		directoryString = TextSoap.findAndReplace(directoryString,"\\+","+");
		directoryString = TextSoap.findAndReplace(directoryString,"+","\\+");
		return directoryString;
	}
	
	
	public String removeTrailingSpaces(String directoryString) {
		while(directoryString.endsWith(" ")){
			directoryString = directoryString.substring(0,directoryString.length()-1);
		}
		
		return directoryString;
	}

	/**
	 * Creates a list of the individual parts of a DN separated by ","
	 * @param DN
	 * @return
	 */
	public List getListOfStringsFromDirectoryString(String DN){
		return getListOfStringsFromDirectoryString(new DirectoryString(DN));
	}
	/**
	 * Creates a list of the individual parts of a DN separated by ","
	 * @param DN
	 * @return
	 */
	public List getListOfStringsFromDirectoryString(DirectoryString DN){
		return DNUtility.getInstance().explodeDN(DN);
	}
	
	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness(
			IWApplicationContext iwac) {
		if (this.embeddedLDAPServerBiz == null) {
			try {
				this.embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwac,
								EmbeddedLDAPServerBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.embeddedLDAPServerBiz;
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness(IWApplicationContext iwca) {
		if (this.ldapReplicationBiz == null) {
			try {
				this.ldapReplicationBiz = (LDAPReplicationBusiness) com.idega.business.IBOLookup.getServiceInstance(iwca, LDAPReplicationBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.ldapReplicationBiz;
	}
	
	public DirectoryString getRootDN(){
		EmbeddedLDAPServerBusiness biz = getEmbeddedLDAPServerBusiness(IWMainApplication.getDefaultIWApplicationContext());
		try {
			DirectoryString dn = biz.getRootDN();
			return dn;
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Generated the RDN (directory string) from the groups name and its parents
	 * @param group
	 * @return
	 */
	public String getGeneratedRDNFromGroup(Group group) {
		String rdn = new String(getRootDNString());
		
		if(group!=null){
			Group parent = group;
			Collection parents;
			while( !(parents = parent.getParentGroups()).isEmpty() ){
				parent = (Group) parents.iterator().next();
				rdn = "ou="+getGroupName(parent)+","+rdn;
			}
		}
		rdn = "ou="+getGroupName(group)+","+rdn;
		return rdn;
	}
	
	public String getUserIdentifier(User user, DirectoryString base) {
		//ADD THE UNIQUE ID?
		String identifier = "cn=";
		String fullName = user.getName();
		String personalId = user.getPersonalID();
		identifier = identifier + fullName + LDAP_USER_DIRECTORY_STRING_SEPARATOR + personalId + "," + base.getDirectoryString();
		return getEscapedLDAPString(identifier);
	}
	
	public String getGroupIdentifier(Group group, DirectoryString base) {
		//ADD THE UNIQUE ID?
		String identifier = "ou=";
		String name = getGroupName(group);
		identifier = identifier + name + "," + base.getDirectoryString();
		return getEscapedLDAPString(identifier);
	}
	
	public String getGroupName(Group group) {
		String name = group.getName();//add abbreviation?;
		if (name == null && "".equals(name)) {
			name = group.getShortName();
			if (name == null && "".equals(name)) {
				name = group.getAbbrevation();
				if (name == null && "".equals(name)) {
					name = group.getPrimaryKey().toString();
				}
			}
		}
		return name;
	}
	
	/**
	 * Gets the base rdn (directory string) from the ldap server settings
	 */
	public String getRootDNString() {
		String baseDN="";
		try {
			DirectoryString dn = getRootDN();
			if(dn!=null){
				baseDN = dn.toString();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return baseDN;
	}
	
}
