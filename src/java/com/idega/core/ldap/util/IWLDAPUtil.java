/*
 * Created on Jun 30, 2004
 */
package com.idega.core.ldap.util;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.core.ldap.client.naming.DN;

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
	  	Attribute attr = attributes.get(attributeKey);
	  	
	  	
	  	if(attr!=null){
	  		try {
				Object obj = attr.get();
				return (String) obj;
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  		return null;
	  	}
	  	else return null;
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
}
