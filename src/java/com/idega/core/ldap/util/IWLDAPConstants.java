/*
 * Created on Jun 30, 2004
 */
package com.idega.core.ldap.util;

/**
 * Contains a list of supported LDAP attributes and schemas by the IdegaWeb implementation.
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface IWLDAPConstants {
	
	public static final String LDAP_META_DATA_KEY_PREFIX = "idegaweb_ldap_";
	public static final String LDAP_META_DATA_KEY_DIRECTORY_STRING = "directory_string";
	//supported schemas
	public static final String LDAP_SCHEMA_ORGANIZATION = "organization";
	public static final String LDAP_SCHEMA_ORGANIZATIONAL_UNIT = "organizationalUnit";
	public static final String LDAP_SCHEMA_INET_ORG_PERSON = "inetOrgPerson";
	public static final String LDAP_SCHEMA_PERSON = "person";
	
	//supported attributes for searches
	/**
	 * cn, the Person identifier
	 */
	public static final String LDAP_ATTRIBUTE_COMMON_NAME = "cn";
	public static final String LDAP_ATTRIBUTE_GIVEN_NAME = "givenName";
	public static final String LDAP_ATTRIBUTE_SURNAME = "sn";
	/**
	 * not the same as uid but a generated 36 character unique identifier, see GenericEntity
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID= "idegawebUUID";
	
	/**
	 * The group type
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_GROUP_TYPE= "idegawebGroupType";
	
	/**
	 * The personal or social security number
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_PERSONAL_ID= "personalId";
	
	/**
	 * User id
	 */
	public static final String LDAP_ATTRIBUTE_UID= "uid";
	/**
	 * Used for group names
	 */
	public static final String LDAP_ATTRIBUTE_ORGANIZATION_UNIT = "ou";
	public static final String LDAP_ATTRIBUTE_ORGANIZATION = "o";
	public static final String LDAP_ATTRIBUTE_DOMAIN = "dc";
	public static final String LDAP_ATTRIBUTE_OBJECT_CLASS = "objectClass";
	public static final String LDAP_ATTRIBUTE_DESCRIPTION = "description";
	public static final String LDAP_ATTRIBUTE_TELEPHONE_NUMBER = "telephoneNumber";
	public static final String LDAP_ATTRIBUTE_REGISTERED_ADDRESS = "registeredAddress";
	public static final String LDAP_ATTRIBUTE_EMAIL = "mail";
	public static final String LDAP_ATTRIBUTE_LOCATION = "l";

}
