/*
 * $Id: LDAPUserBusinessBean.java,v 1.1 2005/11/17 15:50:45 tryggvil Exp $
 * Created on 16.11.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.data.IDOStoreException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.text.Name;


/**
 * <p>
 * Service bean class for manipulating Users in LDAP
 * </p>
 *  Last modified: $Date: 2005/11/17 15:50:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class LDAPUserBusinessBean extends IBOServiceBean implements LDAPUserBusiness,IWLDAPConstants {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 6216449326254290911L;


	/**
	 * 
	 */
	public LDAPUserBusinessBean() {
		super();
	}
	
	/**
	 * Creates or updates a user from an LDAP DN and its attributes and adds it
	 * under the parentGroup supplied
	 * 
	 * @throws NamingException
	 * @throws CreateException
	 * @throws NamingException
	 * @throws RemoteException
	 * @see com.idega.user.business.UserBusiness#createOrUpdateUser(DN
	 *      distinguishedName,Attributes attributes,Group parentGroup)
	 */
	public User createOrUpdateUser(DN distinguishedName, Attributes attributes, Group parentGroup)
			throws RemoteException, CreateException, NamingException {
		User user = createOrUpdateUser(distinguishedName, attributes);
		parentGroup.addGroup(user);
		return user;
	}

	/**
	 * Adds all the ldap attributes as metadata-fields
	 * 
	 * @param group
	 * @param distinguishedName
	 * @param attributes
	 */
	public void setMetaDataFromLDAPAttributes(User user, DN distinguishedName, Attributes attributes) {
		getLDAPGroupBusiness().setMetaDataFromLDAPAttributes(user, distinguishedName, attributes);
	}

	/**
	 * Gets all the users that have this ldap metadata
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Collection getUsersByLDAPAttribute(String key, String value) {
		IWLDAPUtil util = IWLDAPUtil.getInstance();
		Collection users;
		try {
			users = getUserBusiness().getUserHome().findUsersByMetaData(util.getAttributeKeyWithMetaDataNamePrefix(key), value);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
		catch (RemoteException re) {
			throw new RuntimeException(re);
		}
		return users;
	}

	/**
	 * Looks for the user by his DN in his metadata
	 * 
	 * @param identifier
	 * @return
	 */
	public User getUserByDirectoryString(DirectoryString dn) {
		return getUserByDirectoryString(dn.toString());
	}

	/**
	 * Looks for the user by his DN in his metadata
	 * 
	 * @param identifier
	 * @return
	 */
	public User getUserByDirectoryString(String dn) {
		User user = null;
		dn = dn.toLowerCase(); 
		Collection users = getUsersByLDAPAttribute(IWLDAPConstants.LDAP_META_DATA_KEY_DIRECTORY_STRING, dn);
		if (!users.isEmpty() && users.size() == 1) {
			user = (User) users.iterator().next();
		}
		else {
			int index = dn.indexOf(IWLDAPConstants.LDAP_USER_DIRECTORY_STRING_SEPARATOR);
			int commaIndex = dn.indexOf(",");
			if (index > 0 && index < commaIndex) {
				String pid = dn.substring(index + 1, commaIndex);
				try {
					user = getUserBusiness().getUser(pid);
				}
				catch (FinderException e) {
				}
				catch (RemoteException re) {
					throw new RuntimeException(re);
				}				
			}
			else {
				//TODO find by his name
				//getUserHome().findUsersByConditions()
			}
		}
		return user;
	}
	
	/**
	 * Creates or updates a user from an LDAP DN and its attributes.
	 * 
	 * @throws NamingException,RemoteException
	 * @see com.idega.user.business.UserBusiness#createOrUpdateUser(DN
	 *      distinguishedName,Attributes attributes)
	 */
	public User createOrUpdateUser(DN distinguishedName, Attributes attributes) throws CreateException,
			NamingException, RemoteException {
		IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
		String fullName = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_COMMON_NAME, attributes);
		Name name = new Name(fullName);
		String firstName = name.getFirstName();
		String middleName = name.getMiddleName();
		String lastName = name.getLastName();
		String description = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_DESCRIPTION, attributes);
		String uniqueID = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID,attributes);
		String personalId = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_PERSONAL_ID,attributes);
		String dateOfBirth = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_DATE_OF_BIRTH,attributes);
		String userName = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_UID,attributes);
		if(userName==null || "".equals(userName)){
			//maybe its active directory!
			userName = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_UID_ACTIVE_DIRECTORY,attributes);	
		}
		
		String userPassword = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_USER_PASSWORD,attributes);
		String email = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_EMAIL, attributes);
		//String homePhone =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_TELEPHONE_NUMBER,attributes);
		//String fax =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_FAX_NUMBER,attributes);
		//String mobile =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_MOBILE_NUMBER,attributes);
		String fullAddressString =  ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_REGISTERED_ADDRESS,attributes);

		String gender = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_GENDER, attributes);
		int genderId = -1;
		if (gender != null) {
			try {
				genderId = getUserBusiness().getGenderId(gender).intValue();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Date birthDate = null;
		if(dateOfBirth!=null){
			try {
				birthDate = java.sql.Date.valueOf(dateOfBirth);
			}
			catch (IllegalArgumentException e) {
				System.err.println("UserBusiness: date of birth format is invalid.Should be yyyy-MM-dd : " + dateOfBirth);
			}
		}
		
		//find the user
		User user = null;
		if (uniqueID != null) {
			try {
				user = getUserBusiness().getUserByUniqueId(uniqueID);
			}
			catch (FinderException e) {
				System.out.println("UserBusiness: User not found by unique id:" + uniqueID);
			}
		}
		
		if (user == null && personalId != null) {
			try {
				user = getUserBusiness().getUser(personalId);
			}
			catch (FinderException e) {
				System.out.println("UserBusiness: User not found by personal id:" + personalId);
			}
		}
		
		if (user == null) {
			user = getUserByDirectoryString(distinguishedName.toString());
		}
		
//		if (user == null && firstName != null) {
//			try {
//				Collection users = getUserHome().findUsersByConditions(firstName, middleName, lastName, null, null,
//						null, genderId, -1, -1, -1, null, null, true, false);
//				if (users != null && !users.isEmpty() && users.size() == 1) {
//					//its the only one with this name must be our guy!
//					user = (User) users.iterator().next();
//				}
//			}
//			catch (FinderException e) {
//				System.out.println("UserBusiness: last try...user not found by firstname,middlename,lastname");
//			}
//		}
		
		//could not find the person create it
		if (user == null) {
			user = getUserBusiness().createUser(firstName, middleName, lastName, personalId);
			user.store();
		}
		
		//update stuff
		//the unique id
		if (uniqueID != null) {
			user.setUniqueId(uniqueID);
		}
		
		//the gender id
		if (genderId > 0) {
			user.setGender(genderId);
		}
		
		if(birthDate!=null && user.getDateOfBirth()==null){
			user.setDateOfBirth(new IWTimestamp(birthDate).getDate());
		}
		//the description
		user.setDescription(description);
		
		user.store();
		
		//the email
		getUserBusiness().updateUserMail(user, email);
		
		//the main address
		try {
			getUserBusiness().updateUsersMainAddressByFullAddressString(user,fullAddressString);
		}
		catch (CreateException e1) {
			e1.printStackTrace();
		}
		
		//the login
		if(userName!=null){
			try {
				int userId = ((Integer)user.getPrimaryKey()).intValue();
				LoginTable login = LoginDBHandler.getUserLogin(userId);
				if(login==null){
					//no login create one
					login = ((LoginTableHome) com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).createLegacy();
					login.setUserId(userId);	
				}
				
				login.setUserLogin(userName);
				if(userPassword!=null){
//					remove the encryption e.g. {md5} prefix
					userPassword = userPassword.substring(userPassword.indexOf("}")+1);
					login.setUserPasswordInClearText(userPassword);
				}
				login.setLastChanged(IWTimestamp.getTimestampRightNow());
				login.store();
			}
			catch (IDOStoreException e) {
				e.printStackTrace();
			}
			catch (EJBException e) {
				e.printStackTrace();
			}
		}
		
			
		//set all the attributes as metadata also
		setMetaDataFromLDAPAttributes(user, distinguishedName, attributes);
		
			
		
//			TODO Eiki make a method updatePhones(home,fax,mobile) DO in
			// update also
			//getPhoneHome().findUsersFaxPhone();
		//			String phone =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_TELEPHONE_NUMBER,attributes);  		
		
		return user;
	}

	
	protected UserBusiness getUserBusiness() {
		try {
			return (UserBusiness) getServiceInstance(UserBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected LDAPGroupBusiness getLDAPGroupBusiness() {
		try {
			return (LDAPGroupBusiness) getServiceInstance(LDAPGroupBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new RuntimeException(e);
		}
	}
	
}
