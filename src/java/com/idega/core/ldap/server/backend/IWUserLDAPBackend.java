/*
 * Created on May 2, 2004
 */
package com.idega.core.ldap.server.backend;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
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
public class IWUserLDAPBackend extends BaseBackend implements Backend,IWLDAPConstants,EmbeddedLDAPServerConstants,LDAPReplicationConstants {

	Vector exactIndexes = null;
	String baseDN = null;
	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;
	private LDAPReplicationBusiness ldapReplicationBiz;
	private IWLDAPUtil ldapUtil;
	
	/**
	 *  
	 */
	public IWUserLDAPBackend() {
		super();
		
		ldapUtil = IWLDAPUtil.getInstance();
		// The list of supported attributes to search for
		exactIndexes = new Vector();
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_COMMON_NAME ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_SURNAME ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_GIVEN_NAME ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_UID ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_ORGANIZATION_UNIT ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_ORGANIZATION ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_OBJECT_CLASS ) );
		exactIndexes.addElement( new DirectoryString( LDAP_ATTRIBUTE_DESCRIPTION) );
		//exactIndexes.addElement( new DirectoryString( "seealso" ) );
		
		
		try {
			baseDN = getEmbeddedLDAPServerBusiness(IWMainApplication.getDefaultIWApplicationContext()).getBackendSettings().getProperty(PROPS_BACKEND_ZERO_ROOT);
		} catch (Exception e) {
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
	public EntrySet get(DirectoryString base, int scope, Filter filter, boolean typesOnly, Vector attributes) throws DirectoryException {
		EntrySet results = null;
		//System.out.println("Current encoding: "+System.getProperty("file.encoding"));
		Vector entries = new Vector();
			
		//String filtStr = constructFilter(filter);
		String uniqueId = null;
		//switch (currentFilter.choiceId) {
		if(filter.choiceId==Filter.EQUALITYMATCH_CID){
			DirectoryString matchType = new DirectoryString(filter.equalityMatch.attributeDesc);
			DirectoryString matchVal = new DirectoryString(filter.equalityMatch.assertionValue);
			if(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID.equals(matchType.toString())){
				uniqueId = matchVal.toString();
			}
		}
		
		if (scope == SearchRequestEnum.WHOLESUBTREE) {
			//Same as singlelevel only recursive?
			//or just single search thingy
			//NOT IMPLEMENTED YET
			
		} else if (scope == SearchRequestEnum.SINGLELEVEL) {
			
			try {
				if(base.getDirectoryString().equals(baseDN) && uniqueId==null) {
					addTopGroupsToEntries(base, entries);
				}
				else {
					//get children if a group
					if(ldapUtil.isGroup(base) || uniqueId!=null) {
						
						Group group = null;
						
						try{
							group = getGroupBusiness().getGroupHome().findGroupByUniqueId(uniqueId);
						}
						catch(FinderException e){
						//	e.printStackTrace();
							System.err.println("[IWUserLDAPBackend] Could not find the group with the unique id:"+uniqueId+" trying directory string");
						}
						
						if(group==null){
							group = getGroupBusiness().getGroupByDirectoryString(base);
						}
						//handle when group is not found specially, code to send??
						if(group!=null){
							Collection groups = group.getChildGroups();
							Collection users = getUserBusiness().getUsersInGroup(group);
							
							if(groups!=null && !groups.isEmpty()) {
								Iterator groupIter = groups.iterator();
								while (groupIter.hasNext()) {
									Group childGroup = (Group) groupIter.next();
									Entry childEntry = getChildEntry(base,childGroup);
									entries.add(childEntry);
								}
							}
							
							if(users!=null && !users.isEmpty()) {
								Iterator userIter = users.iterator();
								while (userIter.hasNext()) {
									User childUser = (User) userIter.next();
									Entry childEntry = getChildEntry(base,childUser);
									entries.add(childEntry);
								}
							}
					}
					else{
						//temp
						System.err.println("[IWUserLDAPBackend] No group found for unique id: "+uniqueId+" OR DN : "+base.toString());
						
					}
						
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(scope == SearchRequestEnum.BASEOBJECT){
			//THIS is called when we want to get detailed info on a single ENTRY! find again from the DN and return it
			try {
				
				if(base.getDirectoryString().equals(baseDN)) {
					
					//addTopGroupsToEntries(base, entries);
					entries.add(new Entry(base));
				}
				else {
					Entry entry = getEntry(base,attributes,uniqueId);
					entries.add(entry);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		//here we return the search result that could be an empty set
		results = new GroupEntrySet(this, entries);
		
		return results;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#getByDN(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString)
	 */
	public Entry getByDN(DirectoryString dn) throws DirectoryException {
		try {
			return getEntry(dn,null,null);
		} catch (Exception e) {
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
	 *      java.util.Vector)
	 */
	public void modify(DirectoryString dn, Vector changeEntries) throws DirectoryException {
		// TODO find user or group we are looking for from dn. cn=user o=group
		// and ou=group
		//iterate through changes and apply them and store the object
		//boolean isUser = ldapUtil.isUser(dn);
		//boolean isGroup = ldapUtil.isGroup(dn);
		/*
		 * Entry entry = (Entry) datastore.get( name );
		 * 
		 * if ( entry != null ) { Entry current = (Entry) entry.clone();
		 * Enumeration changeEnum = changeEntries.elements(); while (
		 * changeEnum.hasMoreElements() ) { oneChange( current, (EntryChange)
		 * changeEnum.nextElement() ); } SchemaChecker.getInstance().checkEntry(
		 * current ); index( entry, changeEntries ); datastore.put( name,
		 * current ); }
		 */
		super.modify(dn, changeEntries);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.plexus.ldapserver.server.backend.Backend#rename(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString,
	 *      org.codehaus.plexus.ldapserver.server.syntax.DirectoryString)
	 */
	public LDAPResultEnum rename(DirectoryString oldname, DirectoryString newname) {
		// TODO Auto-generated method stub
		return super.rename(oldname, newname);
	}
	
	
	private Entry getEntry(DirectoryString base, Vector attributes, String uniqueId) throws InvalidDNException, RemoteException {
		Entry entry = new Entry(base);
		try {
			if(ldapUtil.isUser(base)) {
				User user = (User)getGroupBusiness().getGroupByDirectoryString(base);
				fillUserEntry(user,entry);
			}
			else if(ldapUtil.isGroup(base)){
				Group group = getGroupBusiness().getGroupByDirectoryString(base);
				fillGroupEntry(group,entry);
			}
		} 
		catch (FinderException e) {
			e.printStackTrace();
			throw new InvalidDNException(base.getDirectoryString());
		}
		
		return entry;
	}
	
	private Entry getChildEntry(DirectoryString base,Group group) throws InvalidDNException {
		Entry entry;
		
		if(group instanceof User) {
//			mail: eiki@idega.is
//			initials: ESH
//			title: manager, product development
//			uid: eiki
//			telephoneNumber: +1 408 555 1862
//			facsimileTelephoneNumber: +1 408 555 1992
//			mobile: +1 408 555 1941
			User user = (User)group;
			String identifier = getUserIdentifier(user,base);
//			
//			DirectoryString childDN = new DirectoryString(identifier+","+dn);
			DirectoryString childDN = new DirectoryString(identifier);
			entry = new Entry(childDN);
			
			fillUserEntry(user,entry);
			
		}
		else {
			String identifier = getGroupIdentifier(group,base);
			
			DirectoryString childDN = new DirectoryString(identifier);
			entry = new Entry(childDN);
			fillGroupEntry(group, entry);
			
		}
		
		
		
		return entry;
	}
	
	
	
	private String getUserIdentifier(User user,DirectoryString base) {
		String identifier = "cn="; 
		String  fullName = user.getName();
		String personalId = user.getPersonalID();
		identifier = identifier+fullName+"-"+personalId+","+base.toString();
		
		
		return IWLDAPUtil.getInstance().getEscapedLDAPString(identifier);
	}
	
	private String getGroupIdentifier(Group group,DirectoryString base) {
		String identifier = "ou=";
		String name = getGroupName(group);
		
		identifier = identifier+name+","+base.toString();
		
		return IWLDAPUtil.getInstance().getEscapedLDAPString(identifier);
	}
	
	private String getGroupName(Group group) {
		String name = group.getName();//add abbreviation?;
		if(name==null && "".equals(name)) {
			name = group.getShortName();
			if(name==null && "".equals(name)) {
				name = group.getAbbrevation();
				if(name==null && "".equals(name)) {
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
		String personalId = user.getPersonalID();
		
		String lName = user.getLastName() ;
		String fName = user.getFirstName();
		
		//should we add the unique id after the name or the pid like in the entry?
		String cn = user.getName();
		String uuid = user.getUniqueId();
		String description = user.getDescription();
		
		
		Vector name = getAttributeVectorForSingleEntry(cn);
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_COMMON_NAME),name);
		
		Vector userPIN = getAttributeVectorForSingleEntry(personalId);
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_IDEGAWEB_PERSONAL_ID),userPIN);
		
		//SHOULD WE ADD IT EMPTY TO REMOVE THE VALUE ON THE OTHER END?
		if(uuid!=null){
			Vector uniqueID = getAttributeVectorForSingleEntry(uuid);
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID),uniqueID);
		}
		
		if(description!=null){
			Vector descriptionV = getAttributeVectorForSingleEntry(description);
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_DESCRIPTION),descriptionV);
		}
		
		
		Vector firstName = getAttributeVectorForSingleEntry(fName);
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_GIVEN_NAME),firstName);
		
		Vector lastName = getAttributeVectorForSingleEntry(lName);
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_SURNAME),lastName);
		
		Vector uid = getAttributeVectorForSingleEntry(user.getPrimaryKey().toString());
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_UID),uid);
		
		//emails
		Collection emails = user.getEmails();
		if(emails!=null && !emails.isEmpty()) {
			Vector emailValues = new Vector();
			Iterator iter = emails.iterator();
			while (iter.hasNext()) {
				Email email = (Email) iter.next();
				emailValues.add(new DirectoryString(email.getEmailAddress()));
			}
			
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_EMAIL),emailValues);
		}
		
		//addresses
		//TODO separate into types
		Collection addresses = user.getAddresses();
		if(addresses!=null && !addresses.isEmpty()) {
			Vector addressValues = new Vector();
			Iterator iter = addresses.iterator();
			while (iter.hasNext()) {
				Address address = (Address) iter.next();
				String addressString;
				try {
					addressString = getAddressBusiness().getFullAddressString(address);
					addressValues.add(new DirectoryString(addressString));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_REGISTERED_ADDRESS),addressValues);
		}
		
		Collection phones = user.getPhones();
		//TODO separate into types
		if(phones!=null && !phones.isEmpty()) {
			Vector phoneValues = new Vector();
			Iterator iter = phones.iterator();
			while (iter.hasNext()) {
				Phone phone = (Phone) iter.next();
				phoneValues.add(new DirectoryString(phone.getNumber()));
			}
			
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_TELEPHONE_NUMBER),phoneValues);
		}
		
		
		
		Vector objectClasses = new Vector();
		objectClasses.add(new DirectoryString(LDAP_SCHEMA_PERSON));
		objectClasses.add(new DirectoryString(LDAP_SCHEMA_INET_ORG_PERSON));
		
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_OBJECT_CLASS),objectClasses);
	}
	
	/**
	 * Fills the entry with group related info (objectClass: organizationalUnit)
	 * @param group
	 * @param entry
	 */
	private void fillGroupEntry(Group group, Entry entry) {
		String name = IWLDAPUtil.getInstance().getEscapedLDAPString(getGroupName(group));
		String desc = group.getDescription();
		String uuid = group.getUniqueId();
		
		//could need to escape all values??
		Vector names = getAttributeVectorForSingleEntry(name);
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_ORGANIZATION_UNIT),names);
		
		if(desc!=null) {
			Vector description = getAttributeVectorForSingleEntry(desc);
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_DESCRIPTION),description);
		}
		
		if(uuid!=null){
			Vector uniqueID = getAttributeVectorForSingleEntry(group.getUniqueId());
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID),uniqueID);
		}
		
		Vector groupType = getAttributeVectorForSingleEntry(group.getGroupType());
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_IDEGAWEB_GROUP_TYPE),groupType);
		
		
		
		
//		emails
		Collection emails = group.getEmails();
		if(emails!=null && !emails.isEmpty()) {
			Vector emailValues = new Vector();
			Iterator iter = emails.iterator();
			while (iter.hasNext()) {
				Email email = (Email) iter.next();
				emailValues.add(new DirectoryString(email.getEmailAddress()));
			}
			
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_EMAIL),emailValues);
		}
		
		//address
		Address address;
		try {
			address = getGroupBusiness().getGroupMainAddress(group);
			if(address!=null) {
				String addressString = getAddressBusiness().getFullAddressString(address);
				Vector registeredAddress = getAttributeVectorForSingleEntry(addressString);
				entry.put(new DirectoryString(LDAP_ATTRIBUTE_EMAIL),registeredAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		Collection phones = group.getPhones();
		//TODO separate into types
		if(phones!=null && !phones.isEmpty()) {
			Vector phoneValues = new Vector();
			Iterator iter = phones.iterator();
			while (iter.hasNext()) {
				Phone phone = (Phone) iter.next();
				phoneValues.add(new DirectoryString(phone.getNumber()));
			}
			
			entry.put(new DirectoryString(LDAP_ATTRIBUTE_TELEPHONE_NUMBER),phoneValues);
		}
		
		Vector objectClasses = new Vector();
		objectClasses.add(new DirectoryString(LDAP_SCHEMA_ORGANIZATIONAL_UNIT));
		entry.put(new DirectoryString(LDAP_ATTRIBUTE_OBJECT_CLASS),objectClasses);
	}
	
	private Vector getAttributeVectorForSingleEntry(String value) {
		Vector attributes = new Vector();
		if(value!=null) {
			attributes.add(new DirectoryString(value));
		}
		return attributes;
	}
	
	private void addTopGroupsToEntries(DirectoryString base, Vector entries) throws IDORelationshipException, RemoteException, FinderException, InvalidDNException {
		//String suffix = base.getDirectoryString();
		Collection topGroups = IWMainApplication.getDefaultIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
		
		Iterator iter = topGroups.iterator();
		while (iter.hasNext()) {
			Group group = (Group) iter.next();	
			String identifier = getGroupIdentifier(group,base);
			DirectoryString dn = new DirectoryString(identifier);
			Entry entry = new Entry(dn);
			
			fillGroupEntry(group,entry);
			entries.add(entry);
			
		}
	}
	private String constructFilter(Filter currentFilter) {
		
		//EIKI NOTHING USED HERE BUT SHOULD BE USED FOR BETTER SEARCHES
		switch (currentFilter.choiceId) {
			case Filter.EQUALITYMATCH_CID :
				DirectoryString matchType = new DirectoryString(currentFilter.equalityMatch.attributeDesc);
			//DirectoryString matchVal = new DirectoryString(currentFilter.equalityMatch.assertionValue);
			
			//only search for allowed attributes
			
			//IF DOES NOT WORK!
			if (exactIndexes.contains(matchType)) {
				//todo do an exact search for a user or a group
				
			}
			break;
			case Filter.PRESENT_CID :
				matchType = new DirectoryString(currentFilter.present);
			if (exactIndexes.contains(matchType)) {
				//todo search for all of this type
				return new String("SELECT " + matchType + ".entryid FROM " + matchType);
				
				
			}
			break;
			case Filter.SUBSTRINGS_CID :
				matchType = new DirectoryString(currentFilter.substrings.type);
			String subfilter = new String();
			//todo do a substring search for users or groups
			
			for (Enumeration substrEnum = currentFilter.substrings.substrings.elements(); substrEnum.hasMoreElements();) {
				SubstringFilterSeqOfChoice oneSubFilter = (SubstringFilterSeqOfChoice) substrEnum.nextElement();
				if (oneSubFilter.choiceId == oneSubFilter.INITIAL_CID) {
					subfilter = subfilter.concat(new String(oneSubFilter.initial) + "%");
				} else if (oneSubFilter.choiceId == oneSubFilter.ANY_CID) {
					if (subfilter.length() == 0) {
						subfilter = subfilter.concat("%");
					}
					subfilter = subfilter.concat(new String(oneSubFilter.any) + "%");
				} else if (oneSubFilter.choiceId == oneSubFilter.FINAL1_CID) {
					if (subfilter.length() == 0) {
						subfilter = subfilter.concat("%");
					}
					subfilter = subfilter.concat(new String(oneSubFilter.final1));
				}
			}
			if (exactIndexes.contains(matchType)) {
				// return new String("SELECT entryid FROM " + matchType + "
				// WHERE UPPER(value) LIKE UPPER('" + subfilter + "')");
				return new String("SELECT " + matchType + ".entryid FROM " + matchType + " WHERE " + matchType + ".value LIKE '"
						+ new DirectoryString(subfilter).normalize() + "'");
			}
			break;
			case Filter.AND_CID :
				String strFilt = new String();
			for (Enumeration andEnum = currentFilter.and.elements(); andEnum.hasMoreElements();) {
				//strFilt = strFilt.concat("(" +
				// constructFilter((Filter)andEnum.nextElement()) + ")");
				strFilt = strFilt.concat(constructFilter((Filter) andEnum.nextElement()));
				if (andEnum.hasMoreElements()) {
					strFilt = strFilt.concat(" INTERSECT ");
				}
			}
			return strFilt;
			case Filter.OR_CID :
				strFilt = new String();
			for (Enumeration orEnum = currentFilter.or.elements(); orEnum.hasMoreElements();) {
				//strFilt = strFilt.concat("(" +
				// constructFilter((Filter)orEnum.nextElement()) + ")");
				strFilt = strFilt.concat(constructFilter((Filter) orEnum.nextElement()));
				if (orEnum.hasMoreElements()) {
					strFilt = strFilt.concat(" UNION ");
				}
			}
			return strFilt;
			case Filter.NOT_CID :
				// Need to fix this...Not a correct implementation
				//Vector matched =
				// evaluateFilter(currentFilter.not,base,scope);
				//matchThisFilter.removeAll(matched);
				break;
		}
		return new String();
	}
	
	private UserBusiness getUserBusiness() throws RemoteException {
		return (UserBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),UserBusiness.class);
	}
	
	private GroupBusiness getGroupBusiness() throws RemoteException {
		return (GroupBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),GroupBusiness.class);
	}
	
  public AddressBusiness getAddressBusiness() throws RemoteException{
    return (AddressBusiness)  IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),AddressBusiness.class);
  }
  
	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness(IWApplicationContext iwc) {
		if (embeddedLDAPServerBiz == null) {
			try {
				embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, EmbeddedLDAPServerBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return embeddedLDAPServerBiz;
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness(IWApplicationContext iwc) {
		if (ldapReplicationBiz == null) {
			try {
				ldapReplicationBiz = (LDAPReplicationBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, LDAPReplicationBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return ldapReplicationBiz;
	}
	
}