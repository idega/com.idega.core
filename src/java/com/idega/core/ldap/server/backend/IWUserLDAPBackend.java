/*
 * Created on May 2, 2004
 */
package com.idega.core.ldap.server.backend;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.FinderException;
import org.codehaus.plexus.ldapserver.ldapv3.Filter;
import org.codehaus.plexus.ldapserver.ldapv3.LDAPResultEnum;
import org.codehaus.plexus.ldapserver.ldapv3.SearchRequestEnum;
import org.codehaus.plexus.ldapserver.ldapv3.SubstringFilterSeqOfChoice;
import org.codehaus.plexus.ldapserver.server.Entry;
import org.codehaus.plexus.ldapserver.server.EntrySet;
import org.codehaus.plexus.ldapserver.server.backend.Backend;
import org.codehaus.plexus.ldapserver.server.backend.BaseBackend;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import org.codehaus.plexus.ldapserver.server.util.DirectoryException;
import org.codehaus.plexus.ldapserver.server.util.InvalidDNException;
import com.idega.business.IBOLookup;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.replication.business.LDAPReplicationConstants;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.core.ldap.server.util.GroupEntrySet;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * An LDAP backend implementation that exposes the idegaweb user/group/role
 * system as a LDAP directory
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson </a>
 *  
 */
public class IWUserLDAPBackend extends BaseBackend implements Backend, IWLDAPConstants, EmbeddedLDAPServerConstants,
LDAPReplicationConstants {
	
	private List exactIndexes = null;
	
	private  String baseDN = null;
	
	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;
	
	private LDAPReplicationBusiness ldapReplicationBiz;
	
	private IWLDAPUtil ldapUtil;
	
	
	
	public IWUserLDAPBackend() {
		super();
		ldapUtil = IWLDAPUtil.getInstance();
		// The list of supported attributes to search for
		exactIndexes = new ArrayList();
		exactIndexes.add(LDAP_ATTRIBUTE_COMMON_NAME);
		exactIndexes.add(LDAP_ATTRIBUTE_EMAIL);
		exactIndexes.add(LDAP_ATTRIBUTE_SURNAME);
		exactIndexes.add(LDAP_ATTRIBUTE_GIVEN_NAME);
		exactIndexes.add(LDAP_ATTRIBUTE_UID);
		exactIndexes.add(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID);
		exactIndexes.add(LDAP_ATTRIBUTE_ORGANIZATION_UNIT);
		exactIndexes.add(LDAP_ATTRIBUTE_ORGANIZATION);
		exactIndexes.add(LDAP_ATTRIBUTE_OBJECT_CLASS);
		exactIndexes.add(LDAP_ATTRIBUTE_DESCRIPTION);
		
		//lowercase
		exactIndexes.add(LDAP_ATTRIBUTE_COMMON_NAME.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_EMAIL.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_SURNAME.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_GIVEN_NAME.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_UID.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_ORGANIZATION_UNIT.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_ORGANIZATION.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_OBJECT_CLASS.toLowerCase());
		exactIndexes.add(LDAP_ATTRIBUTE_DESCRIPTION.toLowerCase());
		
		//uppercase
		exactIndexes.add(LDAP_ATTRIBUTE_COMMON_NAME.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_EMAIL.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_SURNAME.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_GIVEN_NAME.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_UID.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_ORGANIZATION_UNIT.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_ORGANIZATION.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_OBJECT_CLASS.toUpperCase());
		exactIndexes.add(LDAP_ATTRIBUTE_DESCRIPTION.toUpperCase());
		
		
		
		//exactIndexes.addElement( new DirectoryString( "seealso" ) );
		try {
			baseDN = getEmbeddedLDAPServerBusiness(IWMainApplication.getDefaultIWApplicationContext()).getBackendSettings().getProperty(
					PROPS_BACKEND_ZERO_ROOT);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#add(org.codehaus.plexus.ldapserver.server.Entry)
	 */
	public LDAPResultEnum add(Entry entryToAdd) {
		System.out.println(entryToAdd.getName().toString());
		return super.add(entryToAdd);
	}
	
	/**
	 * Not supported yet, dangerous so it really needs to check permissions
	 */
	public LDAPResultEnum delete(DirectoryString dn) {
		System.out.println(dn.toString());
		return super.delete(dn);
	}
	
	/**
	 *  Does the ldap search based on the filter, scope and base object. This method is recursive if the scope is SearchRequestEnum.WHOLESUBTREE
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#get(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString,
	 *      int, org.codehaus.plexus.ldapserver.ldapv3.Filter, boolean,
	 *      java.util.Vector)
	 */
	public EntrySet get(DirectoryString base, int scope, Filter filter, boolean typesOnly, List attributes) throws DirectoryException {
		EntrySet results = null;
		List entries = new ArrayList();
		String uniqueId = null;
		
		if (filter.choiceId == Filter.EQUALITYMATCH_CID) {
			DirectoryString matchType = new DirectoryString(filter.equalityMatch.attributeDesc);
			DirectoryString matchVal = new DirectoryString(filter.equalityMatch.assertionValue);
			String type = matchType.toString();
			String value = matchVal.toString();
			//TODO search for each type separately
			if (LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID.equals(type)) {
				uniqueId = value;
			}
		}
		
		if (scope == SearchRequestEnum.WHOLESUBTREE) {
		//TODO Eiki implement for all scopes
			
			//Same as singlelevel only recursive?
			//or just single search thingy
//			TODO finish implementing regular searches by filter.choiceId
			//Do a regular search
			if (filter.choiceId == Filter.OR_CID) {
				//any matching attribute
				Iterator filters = filter.or.values().iterator();
				while(filters.hasNext()){
					EntrySet matched = get(base, scope, (Filter) filters.next(), false, null);
					while (matched.hasMore()) {
						Entry entry = matched.getNext();
						if (!entries.contains(entry)) {
							entries.add(entry);
						}
					}
				}
			}
			else if (filter.choiceId == Filter.AND_CID) {
				boolean firstAnd = true;
				Iterator filters = filter.and.values().iterator();
				while(filters.hasNext()){
					EntrySet matched = get(base, scope, (Filter) filters.next(), false, null);
					if (firstAnd) {
						firstAnd = false;
						while (matched.hasMore()) {
							entries.add(matched.getNext());
						}
					}
					else {
						List inBoth = new ArrayList();
						while (matched.hasMore()) {
							Entry entry = matched.getNext();
							if (entries.contains(entry)) {
								inBoth.add(entry);
							}
						}
						entries = inBoth;
					}
				}
			}
			else if (filter.choiceId == Filter.SUBSTRINGS_CID) {
				//eiki@idega.is temp test to get the address book in osx lookup to work
				//addressbook searches with OR and substrings example: (|(givenName=Fred*)(sn=Fred*)(cn=Fred*)(mail=Fred*))
				List alreadyLoaded = new ArrayList();
				Iterator filters = filter.substrings.substrings.iterator();
				while(filters.hasNext()){
					SubstringFilterSeqOfChoice choice = (SubstringFilterSeqOfChoice) filters.next();
					String type = new String(filter.substrings.type);
					if(exactIndexes.contains(type)){
						byte[] bytes = choice.initial;
						if(bytes==null){
							bytes = choice.any;
							if(bytes==null){
								bytes = choice.final1;
							}
						}
						
						String searchWord = new String(bytes);
						System.out.println("type: " + type + " searchword:" + searchWord);
						try {
							doSubStringSearch(base,entries,alreadyLoaded, type, searchWord);
						}
						catch (RemoteException e) {
							e.printStackTrace();
						}
						catch (FinderException e) {
							// nothing to see here...move along now
						}
					}
				}
			}
			else if (filter.choiceId == Filter.EQUALITYMATCH_CID) {
				DirectoryString matchType = new DirectoryString(filter.equalityMatch.attributeDesc);
				DirectoryString matchVal = new DirectoryString(filter.equalityMatch.assertionValue);
				String type = matchType.toString();
				String value = matchVal.toString();
				//TODO search for each type separately
				if (LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID.equals(type)) {
					uniqueId = value;
				}
				else{
					List alreadyLoaded = new ArrayList();
					try {
						//todo make exact! using substring now
						doSubStringSearch(base,entries,alreadyLoaded,type,value);
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
					catch (FinderException e) {
						
					}
				}
				//			
				//			User user = getUserBusiness().getUser(searchWord);
				//			String identifier = getUserIdentifier(user, base);
				//			Entry userEntry = new Entry(new DirectoryString(identifier));
				//			fillUserEntry(user, userEntry);
				//			entries.add(userEntry);
			}
		}
		else if (scope == SearchRequestEnum.SINGLELEVEL) {
		
			try {
				if (base.getDirectoryString().equals(baseDN) && uniqueId == null) {
					addTopGroupsToEntries(base, entries);
				}
				else {
					//get children if a group
					if (ldapUtil.isGroup(base) || uniqueId != null) {
						Group group = null;
						if (uniqueId != null) {
							try {
								group = getGroupBusiness().getGroupHome().findGroupByUniqueId(uniqueId);
							}
							catch (FinderException e) {
								//	e.printStackTrace();
								System.err.println("[IWUserLDAPBackend] Could not find the group with the unique id:"
										+ uniqueId + " trying directory string");
							}
						}
						if (group == null) {
							group = getGroupBusiness().getGroupByDirectoryString(base);
						}
						//handle when group is not found specially, code to send??
						if (group != null) {
							Collection groups = group.getChildGroups();
							Collection users = getUserBusiness().getUsersInGroup(group);
							if (groups != null && !groups.isEmpty()) {
								Iterator groupIter = groups.iterator();
								while (groupIter.hasNext()) {
									Group childGroup = (Group) groupIter.next();
									Entry childEntry = getChildEntry(base, childGroup);
									entries.add(childEntry);
								}
							}
							if (users != null && !users.isEmpty()) {
								Iterator userIter = users.iterator();
								while (userIter.hasNext()) {
									User childUser = (User) userIter.next();
									Entry childEntry = getChildEntry(base, childUser);
									entries.add(childEntry);
								}
							}
						}
						else {
							//temp
							System.err.println("[IWUserLDAPBackend] No group found for unique id: " + uniqueId
									+ " OR DN : " + base.toString());
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (scope == SearchRequestEnum.BASEOBJECT) {
			//THIS is called when we want to get detailed info on a single ENTRY! find again from the DN and return it
			try {

				if (base.getDirectoryString().equals(baseDN)) {
					//addTopGroupsToEntries(base, entries);
					entries.add(new Entry(base));
				}
				else {
					Entry entry = getEntry(base, attributes, uniqueId);
					entries.add(entry);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		//here we return the search result that could be an empty set
		results = new GroupEntrySet(this, entries);
		return results;
	}
	
	private List doSubStringSearch(DirectoryString base, List entries, List alreadyLoaded, String type, String searchWord) throws FinderException, RemoteException, InvalidDNException {
			//we only allow substring searches for these attributes
			//(|(givenName=Fred*)(sn=Fred*)(cn=Fred*)(mail=Fred*))
		type = type.toLowerCase();
		Collection col = null;
		
		if(LDAP_ATTRIBUTE_COMMON_NAME.toLowerCase().equals(type) || LDAP_ATTRIBUTE_IDEGAWEB_PERSONAL_ID.toLowerCase().equals(type)){
			//look by the personal id
			col = getUserBusiness().getUserHome().findUsersByConditions(null,null,null,searchWord,null,null,-1,-1,-1,-1,null,null,false,false);
		}
		else if(LDAP_ATTRIBUTE_SURNAME.toLowerCase().equals(type) ){
			col = getUserBusiness().getUserHome().findUsersByConditions(null,null,searchWord,null,null,null,-1,-1,-1,-1,null,null,false,false);
		}
		else if(LDAP_ATTRIBUTE_GIVEN_NAME.toLowerCase().equals(type)){
			//what about middle name?
			col = getUserBusiness().getUserHome().findUsersByConditions(searchWord,null,null,null,null,null,-1,-1,-1,-1,null,null,false,false);
		}
		else if(LDAP_ATTRIBUTE_EMAIL.toLowerCase().equals(type)){
			User user = getUserBusiness().getUserHome().findUserFromEmail(searchWord);
			String identifier = getUserIdentifier(user, base);
			if(!alreadyLoaded.contains(identifier)){
				alreadyLoaded.add(identifier);
				Entry userEntry = new Entry(getDirectoryStringForIdentifier(identifier));
				fillUserEntry(user, userEntry);
				entries.add(userEntry);
			}
		}
		
		if(col!=null && !col.isEmpty()){
			Iterator users = col.iterator();
			while (users.hasNext()) {
				User user = (User) users.next();
				String identifier = getUserIdentifier(user, base);
				if(!alreadyLoaded.contains(identifier)){
					alreadyLoaded.add(identifier);
					Entry userEntry = new Entry(getDirectoryStringForIdentifier(identifier));
					fillUserEntry(user, userEntry);
					entries.add(userEntry);
				}
			}
		}
		
		
		return entries;
	}

	private DirectoryString getDirectoryStringForIdentifier(String identifier) {
		return new DirectoryString(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#getByDN(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString)
	 */
	public Entry getByDN(DirectoryString dn) throws DirectoryException {
		try {
			//this may seem strange but is needed so the DN is converted to the current encoding
			//DirectoryString converted = getDirectoryStringForIdentifier(dn.getDirectoryString());
			return getEntry(dn, null, null);
		}
		catch (Exception e) {
			throw new DirectoryException(e.getMessage());
		}
	}
	
	/**
	 * NOT USED, just returns an empty Entry (calls BaseBackend.getByID())
	 */
	public Entry getByID(Long id) {
		return super.getByID(id);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#modify(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString,
	 *      java.util.List)
	 */
	public void modify(DirectoryString base, List changeEntries) throws DirectoryException {
		if (ldapUtil.isUser(base)) {
			User user = null;
			try {
				user = getUser(base, null);
			}
			catch (RemoteException e) {
				e.printStackTrace();
				super.modify(base, changeEntries);
			}
			if (user == null) {
				System.err.println("[IWUserLDAPBackend] No user found for DN : " + base.toString());
				super.modify(base, changeEntries);
			}
			else {
				//TODO modify the record
			}
		}
		else if (ldapUtil.isGroup(base)) {
			Group group = null;
			try {
				group = getGroup(base, null);
			}
			catch (RemoteException e) {
				e.printStackTrace();
				super.modify(base, changeEntries);
			}
			if (group == null) {
				System.err.println("[IWUserLDAPBackend] No group found for DN : " + base.toString());
				super.modify(base, changeEntries);
			}
			else {
				//TODO modify the record
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#rename(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString,
	 *      org.codehaus.plexus.ldapserver.server.syntax.DirectoryString)
	 */
	public LDAPResultEnum rename(DirectoryString oldname, DirectoryString newname) {
		// TODO Auto-generated method stub
		//moving a group or a user is not supported
		return super.rename(oldname, newname);
	}
	
	public Entry getEntry(DirectoryString base, List attributes, String uniqueId) throws InvalidDNException,RemoteException {
		
		Entry entry = new Entry(base);
		
		if (ldapUtil.isUser(base)) {
			User user = getUser(base, uniqueId);
			if (user == null) {
				System.err.println("[IWUserLDAPBackend] No user found for DN : " + base.toString());
				throw new InvalidDNException(base.getDirectoryString());
			}
			else {
				fillUserEntry(user, entry);
			}
		}
		else if (ldapUtil.isGroup(base)) {
			Group group = getGroup(base, uniqueId);
			if (group == null) {
				System.err.println("[IWUserLDAPBackend] No group found for DN : " + base.toString());
				throw new InvalidDNException(base.getDirectoryString());
			}
			else {
				fillGroupEntry(group, entry);
			}
		}
		return entry;
	}
	
	private Group getGroup(DirectoryString base, String uniqueId) throws RemoteException {
		Group group = null;
		try {
			if (uniqueId != null) {
				group = getGroupBusiness().getGroupByUniqueId(uniqueId);
			}
		}
		catch (FinderException e) {
			System.err.println("[IWUserLDAPBackend] No group found for unique id: " + uniqueId + " trying DN");
		}
		if (group == null) {
			group = getGroupBusiness().getGroupByDirectoryString(base);
		}
		return group;
	}
	
	private User getUser(DirectoryString base, String uniqueId) throws RemoteException {
		User user = null;
		try {
			if (uniqueId != null) {
				user = getUserBusiness().getUserByUniqueId(uniqueId);
			}
		}
		catch (FinderException e) {
			System.err.println("[IWUserLDAPBackend] No user found for unique id: " + uniqueId + " trying DN");
		}
		if (user == null) {
			user = (User) getUserBusiness().getUserByDirectoryString(base.toString());
		}
		return user;
	}
	
	private Entry getChildEntry(DirectoryString base, Group group) throws InvalidDNException {
		Entry entry;
		if (group instanceof User) {
			User user = (User) group;
			String identifier = getUserIdentifier(user, base);
			DirectoryString childDN = getDirectoryStringForIdentifier(identifier);
			entry = new Entry(childDN);
			fillUserEntry(user, entry);
		}
		else {
			String identifier = getGroupIdentifier(group, base);
			DirectoryString childDN = getDirectoryStringForIdentifier(identifier);
			entry = new Entry(childDN);
			fillGroupEntry(group, entry);
		}
		return entry;
	}
	
	private String getUserIdentifier(User user, DirectoryString base) {
		String identifier = "cn=";
		String fullName = user.getName();
		String personalId = user.getPersonalID();
		identifier = identifier + fullName + LDAP_USER_DIRECTORY_STRING_SEPARATOR + personalId + "," + base.getDirectoryString();
		return IWLDAPUtil.getInstance().getEscapedLDAPString(identifier);
	}
	
	private String getGroupIdentifier(Group group, DirectoryString base) {
		String identifier = "ou=";
		String name = getGroupName(group);
		identifier = identifier + name + "," + base.getDirectoryString();
		return IWLDAPUtil.getInstance().getEscapedLDAPString(identifier);
	}
	
	private String getGroupName(Group group) {
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
	 * Fills the entry with user related info (objectClasses: person, inetOrgPerson)
	 * @param user
	 * @param entry
	 */
	private void fillUserEntry(User user, Entry entry) {
		//these are the displayable fields in the OS X AddressBook
		//		givenName
		//		sn
		//		cn
		//		mail
		//		telephoneNumber
		//		facsimile
		//		TelephoneNumber
		//		o
		//		title
		//		ou
		//		buildingName
		//		street
		//		l
		//		st
		//		postalCode
		//		c
		//		jpegPhoto
		//		mobile
		//		co
		//		pager
		//		destinationIndicator
		
		
		String personalId = user.getPersonalID();
		String lName = user.getLastName();
		String fName = user.getFirstName() + ( (user.getMiddleName()!=null)? " "+user.getMiddleName() : "");
		//should we add the unique id after the name or the pid like in the entry?
		String cn = user.getName();
		String uuid = user.getUniqueId();
		String description = user.getDescription();
		List name = getAttributeListForSingleEntry(cn);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_COMMON_NAME), name);
		List userPIN = getAttributeListForSingleEntry(personalId);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_IDEGAWEB_PERSONAL_ID), userPIN);
		//SHOULD WE ADD IT EMPTY TO REMOVE THE VALUE ON THE OTHER END?
		if (uuid != null) {
			List uniqueID = getAttributeListForSingleEntry(uuid);
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID), uniqueID);
		}
		if (description != null) {
			List descriptionV = getAttributeListForSingleEntry(description);
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_DESCRIPTION), descriptionV);
		}
		List firstName = getAttributeListForSingleEntry(fName);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_GIVEN_NAME), firstName);
		List lastName = getAttributeListForSingleEntry(lName);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_SURNAME), lastName);
		List uid = getAttributeListForSingleEntry(user.getPrimaryKey().toString());
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_UID), uid);
		
		//emails
		addEmailsToEntry(user, entry);
		//addresses
		addAddressesToEntry(user, entry);
		//phones
		addPhonesToEntry(user, entry);
		//gender
		addGenderToEntry(user, entry);
		//add the metadata
		addMetaDataFromGroup(user, entry);
		
		//add a jpeg photo
		//the ldap attribute is called "jpegPhoto"
		
		List objectClasses = new ArrayList();
		objectClasses.add(getDirectoryStringForIdentifier(LDAP_SCHEMA_PERSON));
		objectClasses.add(getDirectoryStringForIdentifier(LDAP_SCHEMA_INET_ORG_PERSON));
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_OBJECT_CLASS), objectClasses);
	}
	
	private void addGenderToEntry(User user, Entry entry) {
		//gender
		try {
			int genderId = user.getGenderID();
			boolean isMale;
			String gender = "male";
			isMale = getUserBusiness().isMale(genderId);
			if(!isMale){
				gender="female";
			}
			List genderAtt = getAttributeListForSingleEntry(gender);
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_IDEGAWEB_GENDER), genderAtt);
			
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
	}
	
	private void addPhonesToEntry(User user, Entry entry) {
		//phone stuff
		try {
			try {
				Phone fax;
				fax = getUserBusiness().getPhoneHome().findUsersFaxPhone(user);
				List faxNumber = getAttributeListForSingleEntry(fax.getNumber());
				entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_FAX_NUMBER), faxNumber);
			}
			catch (FinderException e1) {
				//e1.printStackTrace();
			}
			
			
			try {
				Phone home = getUserBusiness().getPhoneHome().findUsersHomePhone(user);
				List homeNumber = getAttributeListForSingleEntry(home.getNumber());
				entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_TELEPHONE_NUMBER), homeNumber);
			}
			catch (FinderException e1) {
				//e1.printStackTrace();
			}
			try {
				Phone mobile = getUserBusiness().getPhoneHome().findUsersMobilePhone(user);
				List mobileNumber = getAttributeListForSingleEntry(mobile.getNumber());
				entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_MOBILE_NUMBER), mobileNumber);	
			}
			catch (FinderException e1) {
				//e1.printStackTrace();
			}
		}
		catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}
	
	private void addAddressesToEntry(User user, Entry entry) {
		//addresses
		//TODO Eiki get all addresses and separate into types
		try {
			Address address;
			address = getUserBusiness().getUsersMainAddress(user);
			if (address != null) {
				addAddressToEntry(entry, address);
			}else{
				//just add the first address in the list
				Collection addresses = user.getAddresses();
				if(addresses!=null && !addresses.isEmpty()){
					addAddressToEntry(entry,(Address)addresses.iterator().next());
				}
				
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	private void addAddressToEntry(Entry entry, Address address) {
		String addressString;
		try {
			addressString = getAddressBusiness().getFullAddressString(address);
			List registeredAddress = getAttributeListForSingleEntry(addressString);
			//full address
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_REGISTERED_ADDRESS), registeredAddress);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		//partial
		String streetAndNumber = address.getStreetAddress();
		List street = getAttributeListForSingleEntry(streetAndNumber);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_ADDRESS_STREET_NAME_AND_NUMBER), street);
		
		String postalCode = address.getPostalAddress();
		List postal = getAttributeListForSingleEntry(postalCode);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_ADDRESS_POSTAL_CODE), postal);
	}
	
	private void addEmailsToEntry(User user, Entry entry) {
		//emails
		Collection emails = user.getEmails();
		if (emails != null && !emails.isEmpty()) {
			List emailValues = new ArrayList();
			Iterator iter = emails.iterator();
			while (iter.hasNext()) {
				Email email = (Email) iter.next();
				emailValues.add(getDirectoryStringForIdentifier(email.getEmailAddress()));
			}
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_EMAIL), emailValues);
		}
	}
	
	/**
	 * Fills the entry with group related info (objectClass: organizationalUnit)
	 * @param group
	 * @param entry
	 */
	private void fillGroupEntry(Group group, Entry entry) {
		//String name = IWLDAPUtil.getInstance().getEscapedLDAPString(getGroupName(group));
		String name = getGroupName(group);
		String desc = group.getDescription();
		String uuid = group.getUniqueId();
		//could need to escape all values??
		List names = getAttributeListForSingleEntry(name);
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_ORGANIZATION_UNIT), names);
		if (desc != null) {
			List description = getAttributeListForSingleEntry(desc);
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_DESCRIPTION), description);
		}
		if (uuid != null) {
			List uniqueID = getAttributeListForSingleEntry(group.getUniqueId());
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID), uniqueID);
		}
		List groupType = getAttributeListForSingleEntry(group.getGroupType());
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_IDEGAWEB_GROUP_TYPE), groupType);
		
		
		//emails
		addEmailsToEntry(group, entry);
		//address
		addAddressToEntry(group, entry);
		
		//phones
		addPhonesToEntry(group, entry);
		
		//add the metadata
		addMetaDataFromGroup(group, entry);
		
		List objectClasses = new ArrayList();
		objectClasses.add(getDirectoryStringForIdentifier(LDAP_SCHEMA_ORGANIZATIONAL_UNIT));
		entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_OBJECT_CLASS), objectClasses);
	}
	
	private void addEmailsToEntry(Group group, Entry entry) {
		//		emails
		Collection emails = group.getEmails();
		if (emails != null && !emails.isEmpty()) {
			List emailValues = new ArrayList();
			Iterator iter = emails.iterator();
			while (iter.hasNext()) {
				Email email = (Email) iter.next();
				emailValues.add(getDirectoryStringForIdentifier(email.getEmailAddress()));
			}
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_EMAIL), emailValues);
		}
	}
	
	private void addPhonesToEntry(Group group, Entry entry) {
		Collection phones = group.getPhones();
		//TODO separate into types like for user
		if (phones != null && !phones.isEmpty()) {
			List phoneValues = new ArrayList();
			Iterator iter = phones.iterator();
			while (iter.hasNext()) {
				Phone phone = (Phone) iter.next();
				phoneValues.add(getDirectoryStringForIdentifier(phone.getNumber()));
			}
			entry.put(getDirectoryStringForIdentifier(LDAP_ATTRIBUTE_TELEPHONE_NUMBER), phoneValues);
		}
	}
	
	private void addAddressToEntry(Group group, Entry entry) {
		//addresses
		//TODO Eiki get all addresses and separate into types
		Address address;
		try {
			address = getGroupBusiness().getGroupMainAddress(group);
			if (address != null) {
				addAddressToEntry(entry,address);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addMetaDataFromGroup(Group group, Entry entry) {
		//add the groups metadata
		Map metadata = group.getMetaDataAttributes();
		if (metadata != null && !metadata.isEmpty()) {
			Iterator iter = metadata.keySet().iterator();
			while (iter.hasNext()) {
				String metaKey = (String) iter.next();
				String metaValue = (String) metadata.get(metaKey);
				if (metaValue != null) {
					List metaDataValue = getAttributeListForSingleEntry(metaValue);
					entry.put(getDirectoryStringForIdentifier(ldapUtil.getAttributeKeyWithMetaDataNamePrefix(metaKey)),metaDataValue);
				}
			}
		}
	}
	
	private List getAttributeListForSingleEntry(String value) {
		List attributes = new ArrayList();
		if (value != null) {
			attributes.add(getDirectoryStringForIdentifier(value));
		}
		return attributes;
	}
	
	private void addTopGroupsToEntries(DirectoryString base, List entries) throws IDORelationshipException,
	RemoteException, FinderException, InvalidDNException {
		//String suffix = base.getDirectoryString();
		Collection topGroups = IWMainApplication.getDefaultIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
		Iterator iter = topGroups.iterator();
		while (iter.hasNext()) {
			Group group = (Group) iter.next();
			String identifier = getGroupIdentifier(group, base);
			DirectoryString dn = getDirectoryStringForIdentifier(identifier);
			
			Entry entry = new Entry(dn);
			fillGroupEntry(group, entry);
			entries.add(entry);
		}
	}
	
	private UserBusiness getUserBusiness() throws RemoteException {
		return (UserBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
				UserBusiness.class);
	}
	
	private GroupBusiness getGroupBusiness() throws RemoteException {
		return (GroupBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
				GroupBusiness.class);
	}
	
	public AddressBusiness getAddressBusiness() throws RemoteException {
		return (AddressBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
				AddressBusiness.class);
	}
	
	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness(IWApplicationContext iwc) {
		if (embeddedLDAPServerBiz == null) {
			try {
				embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) com.idega.business.IBOLookup.getServiceInstance(
						iwc, EmbeddedLDAPServerBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return embeddedLDAPServerBiz;
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness(IWApplicationContext iwc) {
		if (ldapReplicationBiz == null) {
			try {
				ldapReplicationBiz = (LDAPReplicationBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc,
						LDAPReplicationBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return ldapReplicationBiz;
	}
}