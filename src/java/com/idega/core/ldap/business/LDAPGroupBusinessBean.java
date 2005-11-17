/*
 * $Id: LDAPGroupBusinessBean.java,v 1.1 2005/11/17 15:50:45 tryggvil Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.business.IBOServiceBean;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.util.ListUtil;


/**
 * <p>
 * Business class for manipulating groups in LDAP
 * </p>
 *  Last modified: $Date: 2005/11/17 15:50:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class LDAPGroupBusinessBean extends IBOServiceBean implements IWLDAPConstants, LDAPGroupBusiness{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -5851403555280878882L;

	/**
	 * 
	 */
	public LDAPGroupBusinessBean() {
		super();
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.core.ldap.business.LDAPGroupBusiness#createOrUpdateGroup(com.idega.core.ldap.client.naming.DN, javax.naming.directory.Attributes, boolean, com.idega.user.data.Group)
	 */
	  public Group createOrUpdateGroup(DN distinguishedName,Attributes attributes, boolean createUnderRootDomainGroup, Group parentGroup)throws CreateException,NamingException,RemoteException{
	  	IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
	 	int homePageID,homeFolderID,aliasID;
	 	homePageID=homeFolderID=aliasID=-1;
	 	
	  	String name = ldapUtil.getNameOfGroupFromAttributes(attributes);
	  	String description = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_DESCRIPTION,attributes);
	  	String abbr = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_ABBREVIATION,attributes);
		String type = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_GROUP_TYPE,attributes);
		if(type==null){
			type = getGroupBusiness().getGroupTypeHome().getGeneralGroupTypeString();
		}
		//needs to be done, otherwise the create group will fail or at least the group is not displayed in the userapp
		getGroupBusiness().createVisibleGroupType(type);
		
		String uniqueID =  ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID,attributes);
		//String email = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_EMAIL,attributes);
		//String address = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_REGISTERED_ADDRESS,attributes);
		//String phone = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_TELEPHONE_NUMBER,attributes);
		
		//search for the group
		//try to find the group by its unique id or DN
	  	//if it is found this must mean an update
	  	Group group = getGroupByDNOrUniqueId(distinguishedName, uniqueID);
	  	
	  	if(group==null){
	  		System.out.println("GroupBusiness: Group not found by directoryString. Creating a new group...");
	  		group = getGroupBusiness().createGroup(name,description,type,homePageID,homeFolderID,aliasID,createUnderRootDomainGroup,parentGroup);
	  		if(uniqueID!=null){
	  			group.setUniqueId(uniqueID);
	  			group.store();
	  		}
	  	}
	  	
		//TODO update the group
		group.setName(name);
		group.setDescription(description);
		//don't overwrite the type
		//group.setGroupType(type);
		group.setAbbrevation(abbr);
		if(uniqueID!=null){
			group.setUniqueId(uniqueID);
		}
		
		//todoupdate emails,addresses,phone and email
		group.store();
	
		List parent = group.getParentGroups();
		if(parent.isEmpty() && parentGroup!=null){
			parentGroup.addGroup(group);
		}
	  	
	  	//set all the attributes as metadata also
	  	setMetaDataFromLDAPAttributes(group,distinguishedName,attributes);
	  	
	  	return group;
	  }
	
/* (non-Javadoc)
 * @see com.idega.core.ldap.business.LDAPGroupBusiness#getGroupByDNOrUniqueId(com.idega.core.ldap.client.naming.DN, java.lang.String)
 */
	public Group getGroupByDNOrUniqueId(DN distinguishedName, String uniqueID) throws RemoteException {
		Group group = null;
	  	IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
	  	if(uniqueID!=null){
		  	try {
				group = getGroupBusiness().getGroupByUniqueId(uniqueID);
			} catch (FinderException e) {
				//temp debug
				System.out.println("GroupBusiness: Group not found by unique id: "+uniqueID);
			}
	  	}
	  	
	  	if(group==null && distinguishedName!=null){
			group = getGroupByDirectoryString(ldapUtil.convertDNToDirectoryString(distinguishedName));
	  	}
		return group;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ldap.business.LDAPGroupBusiness#setMetaDataFromLDAPAttributes(com.idega.user.data.Group, com.idega.core.ldap.client.naming.DN, javax.naming.directory.Attributes)
	 */
		public void setMetaDataFromLDAPAttributes(Group group, DN distinguishedName, Attributes attributes) {
			IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
			
			//good to have for future lookup, should actually be multivalued!
			if(distinguishedName!=null){
				group.setMetaData(IWLDAPConstants.LDAP_META_DATA_KEY_DIRECTORY_STRING,distinguishedName.toString().toLowerCase());
			}
			
			if (attributes != null) {
				NamingEnumeration attrlist = attributes.getAll();
				try {
					while (attrlist.hasMore()) {
						Attribute att = (Attribute) attrlist.next();
						
						//only store true meta data keys and not ldap keys that have been handled already
						if(ldapUtil.isAttributeIWMetaDataKey(att)){
							String key = ldapUtil.getAttributeKeyWithoutMetaDataNamePrefix(att);
							String keyWithPrefix = att.getID();
							
							if(key.indexOf("binary")<0){
								String value;
								try {
									value = ldapUtil.getSingleValueOfAttributeByAttributeKey(keyWithPrefix, attributes);
									
									if(value.length()<=2000){
										if("".equals(value)){
											value = null;
										}
										group.setMetaData(key,value);
									}
									else{
										System.err.println("[GroupBusiness] attribute is too long: "+key);
									}
									//Todo if we want to copy all ldap attributes we could remove all attributes we have handled and just
									//save the rest as metdata
									//attributes.remove(key);
								}
								catch (Exception e) {
									System.err.println("[GroupBusiness] attribute has no value or an error occured: "+key);
								}
								
							}
							else{
								System.out.print("[GroupBusiness] binary attributes not supported yet: "+key);
							}
						}
					}
				}
				catch (NamingException e) {
					e.printStackTrace();
				}
			}
			
			group.store();
			
		}
		
		

	/* (non-Javadoc)
	 * @see com.idega.core.ldap.business.LDAPGroupBusiness#createOrUpdateGroup(com.idega.core.ldap.client.naming.DN, javax.naming.directory.Attributes)
	 */
	  public Group createOrUpdateGroup(DN distinguishedName,Attributes attributes)throws CreateException,NamingException,RemoteException{
	  	return createOrUpdateGroup(distinguishedName,attributes,true,null);
	  }
	  
	  /* (non-Javadoc)
	 * @see com.idega.core.ldap.business.LDAPGroupBusiness#createOrUpdateGroup(com.idega.core.ldap.client.naming.DN, javax.naming.directory.Attributes, com.idega.user.data.Group)
	 */
	  public Group createOrUpdateGroup(DN distinguishedName,Attributes attributes, Group parentGroup)throws CreateException,NamingException,RemoteException{
	  	return createOrUpdateGroup(distinguishedName,attributes,false,parentGroup);
	  }

		/* (non-Javadoc)
		 * @see com.idega.core.ldap.business.LDAPGroupBusiness#getGroupByDirectoryString(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString)
		 */
		public Group getGroupByDirectoryString(DirectoryString dn) throws RemoteException {
			//TODO use one of the DN helper classes to do this cleaner
			String identifier = dn.getDirectoryString().toLowerCase();
			String dnFromMetaDataKey = IWLDAPConstants.LDAP_META_DATA_KEY_DIRECTORY_STRING;
			Group group = null;
			
			//try and get by metadata first then by inaccurate method
			Collection groups = getGroupBusiness().getGroupsByMetaDataKeyAndValue(dnFromMetaDataKey,identifier);
			
			if(!groups.isEmpty() && groups.size()==1){
				//we found it!
				group = (Group)groups.iterator().next();
			}
			else{
				//LAST CHECK! 
				//Warning this is potentially inaccurate and slow.
				//1. We start with shrinking the DN to the groups parent DN
				//2. Then we look for that group by its metadata
				//3.	if we don't find it we return null because the groups parent does not exist in the database
				//	if we do find the parent we get its children and see if we find a group with the name we are looking for
				//4.We double check if the groups we find already have a DN meta data.
				//	if all of them do then we cannot say that they are the correct one and return null
				//	if exactly one does then we return that one
				//	else if more than one have no DN and have the same name we return null because we cannot deside.
				IWLDAPUtil util = IWLDAPUtil.getInstance();
				int firstComma = identifier.indexOf(",");
				int startOfGroupName = identifier.indexOf("=");
				String groupName = identifier.substring(startOfGroupName+1,firstComma);
				if(firstComma>0){
					//1.
					String parentDN = identifier.substring(firstComma+1,identifier.length());
					List candidates = new Vector();
					//2.
					Collection potentialParent = getGroupBusiness().getGroupsByMetaDataKeyAndValue(dnFromMetaDataKey,parentDN);
					if(!potentialParent.isEmpty() && potentialParent.size()==1){
						//we found it!
						Group parent = (Group)potentialParent.iterator().next();
						//3.
						List children = parent.getChildGroups();
						if(children!=null && !children.isEmpty()){
							Iterator iter = children.iterator();
							while (iter.hasNext()) {
								Group child = (Group) iter.next();
								//4.
								if(groupName.equals(child.getName()) && child.getMetaData(dnFromMetaDataKey)==null){
									candidates.add(child);
								}
							}
							
							if(!candidates.isEmpty() && candidates.size()==1){
								group = (Group)candidates.iterator().next();
							}
						}
					}else if(potentialParent.isEmpty()){
						
						Collection possibleGroups = getGroupBusiness().getGroupsByGroupName(groupName);
						
						if(!possibleGroups.isEmpty()){
							List dnParts = util.getListOfStringsFromDirectoryString(dn);
							String rootDN = IWLDAPUtil.getInstance().getRootDNString();
							boolean isTheWinner = false;
							//this used to be return (Group) possibleWinners.next() if the size of the collection was 1 but I think that not enough checking
							Iterator possibleWinners = possibleGroups.iterator();
							while(possibleWinners.hasNext() && group==null){
								Group child = (Group) possibleWinners.next();
								//a recursive method that checks all the parents names of this group against the names in the ldap dn string
								isTheWinner = isParentPathCorrectFromDN(child,1,dnParts,rootDN);
								if(isTheWinner){
									group = child;
								}
							}
							
						}
					}
					
				}
			}
			
			
			return group;
		}


		/**
		 * Checks RECURSIVELY if the name of the childs parent is the same as in the childs DN
		 * @param child
		 * @param i
		 * @param dnParts
		 * @param rootDN
		 * @return
		 */
		private boolean isParentPathCorrectFromDN(Group child, int indexOfChildsParentInDNParts, List dnParts, String rootDN) {
			IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
			boolean isPathCorrect = true;
			List parents = child.getParentGroups();

			//"there can be..only one!...parent"
			if(!parents.isEmpty()){
				String dnPart = (String)dnParts.get(indexOfChildsParentInDNParts);
				String parentName = dnPart.substring(dnPart.indexOf("=")+1);
				Group parent = (Group)parents.get(0);
				int size = dnParts.size();
				String name = ldapUtil.removeTrailingSpaces(parent.getName());
				
				if(name.equals(parentName) && indexOfChildsParentInDNParts<size){
					//keep on checking this parents parent...recursive
					isPathCorrect = isParentPathCorrectFromDN(parent,++indexOfChildsParentInDNParts,dnParts,rootDN);
				}
				else{
					isPathCorrect = false;
				}
			}
			else{
				//check if the remaining dn is the root dn
				List remaining = dnParts.subList(indexOfChildsParentInDNParts,dnParts.size());
				String restOfDn = ListUtil.convertListOfStringsToCommaseparatedString(remaining);
				if(!restOfDn.equals(rootDN)){
					isPathCorrect = false;
				}
				
			}
			
			return isPathCorrect;
		}

		protected GroupBusiness getGroupBusiness() throws RemoteException {
			return (GroupBusiness) getServiceInstance(GroupBusiness.class);
		}
		
}
