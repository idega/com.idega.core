/*
 * Created on Jun 30, 2004
 */
package com.idega.core.ldap.util;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.core.ldap.client.naming.DN;
import com.idega.util.text.TextSoap;

/**
 * A singleton helper class for all sorts of LDAP stuff.
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public class IWLDAPUtil implements IWLDAPConstants{
	
	private static IWLDAPUtil instance;

	/**
	 * 
	 */
	private IWLDAPUtil() {
		super();
	}
	
	public static IWLDAPUtil getInstance(){
		if(instance==null){
			instance = new IWLDAPUtil();
		}
		
		return instance;
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
				return (String) obj;
			}
		} catch (Exception e) {
			System.out.println("[IWLDAPUtil] No UUID defined in attributes");
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
	 * Gets the (idegaweb)ldapmetadata prefix + thekey
	 * @param thekey
	 * @return
	 */
	public String getAttributeKeyWithMetaDataNamePrefix(String theKey){
		return LDAP_META_DATA_KEY_PREFIX+theKey;
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
		 
	 * @param directoryString Exmaple DN: ou=Group of people aged 13+,dc=idega,dc=com
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
		
		directoryString = TextSoap.findAndReplace(directoryString,"+","\\+");
		return directoryString;
	}
}
