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
	public static final String LDAP_META_DATA_KEY_DIRECTORY_STRING = "ldap_rdn";
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
	 * The date of birth
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_DATE_OF_BIRTH= "dateOfBirth";
	
	/**
	 * The users gender, male/female
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_GENDER = "gender";
	
	/**
	 * The users status keys (only keys not localized)
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_STATUS = "idegawebUserStatus";
	
	/**
	 * Primary key
	 */
	public static final String LDAP_ATTRIBUTE_IDEGAWEB_PRIMARY_KEY= "idegawebPrimaryKey";
	
	public static String LDAP_ATTRIBUTE_IDEGAWEB_ABBREVIATION = "idegawebGroupAbbreviation";
	
	/**
	 * User login name
	 */
	public static final String LDAP_ATTRIBUTE_UID= "uid";
	
	/**
	 * User login name in AD
	 */
	public static final String LDAP_ATTRIBUTE_UID_ACTIVE_DIRECTORY= "sAMAccountName";
	
	/**
	 * User password. The value must be prefixed with LDAP_USER_PASSWORD_PREFIX( that is the string {md5} ) or similar<br>
	userPassword values MUST be represented by following syntax: 
        passwordvalue          = schemeprefix encryptedpassword
        schemeprefix           = "{" scheme "}"
        scheme                 = "crypt" / "md5" / "sha" / altscheme
        altscheme              = "x-" keystring
        encryptedpassword      = encrypted password

 The encrypted password contains of a plaintext key hashed using the algorithm scheme. 
 userPassword values which do not adhere to this syntax MUST NOT be used for authentication. The DUA MUST iterate through the values of the attribute until a value matching the above syntax is found. Only if encryptedpassword is an empty string does the user have no password. DUAs are not required to consider encryption schemes which the client will not recognize; in most cases, it may be sufficient to consider only "crypt". 
 Below is an example of a userPassword attribute: 
 	userPassword: {crypt}X5/DBrWPOQQaI
	 */
	public static final String LDAP_ATTRIBUTE_USER_PASSWORD= "userPassword";
	
	/**
	 * @see LDAP_ATTRIBUTE_USER_PASSWORD
	 */
	public static final String LDAP_USER_PASSWORD_PREFIX = "{md5}";
	
	/**
	 * Used to separate the users name and his personal id in a dn
	 */
	public static final String LDAP_USER_DIRECTORY_STRING_SEPARATOR = "#";
	
	/**
	 * Used for group names
	 */
	public static final String LDAP_ATTRIBUTE_ORGANIZATION_UNIT = "ou";
	public static final String LDAP_ATTRIBUTE_ORGANIZATION = "o";
	public static final String LDAP_ATTRIBUTE_DOMAIN = "dc";
	public static final String LDAP_ATTRIBUTE_OBJECT_CLASS = "objectClass";
	public static final String LDAP_ATTRIBUTE_DESCRIPTION = "description";
	public static final String LDAP_ATTRIBUTE_TELEPHONE_NUMBER = "telephoneNumber";
	public static final String LDAP_ATTRIBUTE_FAX_NUMBER = "facsimileTelephoneNumber";
	public static final String LDAP_ATTRIBUTE_MOBILE_NUMBER = "mobile";
	public static final String LDAP_ATTRIBUTE_REGISTERED_ADDRESS = "registeredAddress";
	public static final String LDAP_ATTRIBUTE_ADDRESS_STREET_NAME_AND_NUMBER = "street";
	public static final String LDAP_ATTRIBUTE_ADDRESS_POSTAL_CODE = "postalCode";
	public static final String LDAP_ATTRIBUTE_CO_ADDRESS = "co";
	public static final String LDAP_ATTRIBUTE_EMAIL = "mail";
	public static final String LDAP_ATTRIBUTE_LOCATION = "l";


}
