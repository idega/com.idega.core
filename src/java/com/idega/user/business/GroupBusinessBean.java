package com.idega.user.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneHome;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.data.Group;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupDomainRelationTypeHome;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.data.ParentGroupsRecursiveProcedure;
import com.idega.user.data.User;
import com.idega.user.data.UserGroupPlugIn;
import com.idega.user.data.UserGroupPlugInHome;
import com.idega.user.data.UserGroupRepresentative;
import com.idega.user.data.UserGroupRepresentativeHome;
import com.idega.user.data.UserHome;
import com.idega.util.ListUtil;
import com.idega.util.datastructures.NestedSetsContainer;

 /**
  * <p>Title: GroupBusinessBean</p>
  * <p>Description:  A collection of methods to create, remove, lookup and manipulate a Group.</p>
  * <p>Copyright: Copyright (c) 2002</p>
  * <p>Company: idega Software</p>
  * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a href="eiki@idega.is">Eirikur S. Hrafnsson</a>
  * @version 1.2
  */


public class GroupBusinessBean extends com.idega.business.IBOServiceBean implements GroupBusiness,IWLDAPConstants {

  private GroupRelationHome groupRelationHome;
  private UserHome userHome;
  private GroupHome groupHome;
  private UserGroupRepresentativeHome userRepHome; 
  private GroupHome permGroupHome;
  private AddressHome addressHome;
  private EmailHome emailHome;
  private PhoneHome phoneHome;
  private ICFileHome fileHome;
  private String[] userRepresentativeType;
  private static final String GROUP_HOME_FOLDER_LOCALIZATION_PREFIX = "ic_group.home_folder.";
  
  private NestedSetsContainer groupTreeSnapShot = null;


  public GroupBusinessBean() {
  }

  public UserHome getUserHome(){
    if(userHome==null){
      try{
        userHome = (UserHome)IDOLookup.getHome(User.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return userHome;
  }

  public UserGroupRepresentativeHome getUserGroupRepresentativeHome(){
    if(userRepHome==null){
      try{
        userRepHome = (UserGroupRepresentativeHome)IDOLookup.getHome(UserGroupRepresentative.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return userRepHome;
  }

  public GroupHome getGroupHome(){
    if(groupHome==null){
      try{
        groupHome = (GroupHome)IDOLookup.getHome(Group.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return groupHome;
  }


  public GroupHome getPermissionGroupHome(){
    if(permGroupHome==null){
      try{
        permGroupHome = (GroupHome)IDOLookup.getHome(PermissionGroup.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return permGroupHome;
  }

  /** 
   * Get all groups in the system that are not UserRepresentative groups   * @return Collection With all grops in the system that are not UserRepresentative groups   */
  public Collection getAllGroups() {
    try {
      return getGroups(getUserRepresentativeGroupTypeStringArray(),false);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


/**
 * Returns all groups that are not permission or general groups */
  public  Collection getAllNonPermissionOrGeneralGroups(){
    try {
      //filter
      String[] groupsNotToReturn = new String[2];
      groupsNotToReturn[0] = this.getGroupHome().getGroupType();
      //groupsNotToReturn[0] = ((Group)com.idega.user.data.GroupBMPBean.getInstance(Group.class)).getGroupTypeValue();
      groupsNotToReturn[1] = this.getPermissionGroupHome().getGroupType();
      //groupsNotToReturn[0] = ((PermissionGroup)com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getInstance(PermissionGroup.class)).getGroupTypeValue();
      //filter end
      return getGroups(groupsNotToReturn,true);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

/**
 * Returns all groups filtered by the grouptypes array.
 * @param groupTypes the Groups a String array of group types to be filtered with
 * @param returnSpecifiedGroupTypes if true it returns the Collection with all the groups that are of the types specified in  groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[])
 * @return Collection of Groups
 * @throws Exception If an error occured
 */
  public  Collection getGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws Exception {
    Collection result = getGroupHome().findAllGroups(groupTypes,returnSpecifiedGroupTypes);
    if(result != null){ //TODO move from business level to data level by using 'NOT IN (_list_of_standard_group_ids_)' 
      result.removeAll(getAccessController().getStandardGroups());
    }
    return result;
  }


/**
 * Returns all the groups that are a direct parent of the group with id uGroupId
 * @return Collection of direct parent groups
 */
  public  Collection getParentGroups(int uGroupId)throws EJBException,FinderException{
  //public  Collection getGroupsContainingDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      return getParentGroups(group);
    }
    catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }

/**
 * Returns all the groups that are a direct parent of the group group
 * @return Collection of direct parent groups
 */
  public  Collection getParentGroups(Group group){
  //public  Collection getGroupsContainingDirectlyRelated(Group group){
    try {
      return group.getParentGroups();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
	/**
	 * Optimized version of getParentGroups(Group group) by Gummi 25.08.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	public  Collection getParentGroups(Group group,Map cachedParents, Map cachedGroups){
	//public  Collection getGroupsContainingDirectlyRelated(Group group){
	  try {
	    return group.getParentGroups(cachedParents,cachedGroups);
	  }
	  catch (Exception ex) {
	    ex.printStackTrace();
	    return null;
	  }
	}



/**
 * Returns all the groups that are not a direct parent of the Group with id uGroupId. That is both groups that are indirect parents of the group or not at all parents of the group. * @see com.idega.user.business.GroupBusiness#getNonParentGroups(int)
 * @return Collection of non direct parent groups */
  public  Collection getNonParentGroups(int uGroupId){
//  public  Collection getAllGroupsNotDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getParentGroups(group);
      Collection AllGroups =  getAllGroups();// Filters out userrepresentative groups //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

      if(AllGroups != null){
        if(isDirectlyRelated != null){
						AllGroups.remove(isDirectlyRelated);
        }
        AllGroups.remove(group);
        return AllGroups;
      }else{
        return null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  

/**
 * Returns all the groups that are not a direct parent of the group with id uGroupId which are "Registered" i.e. non system groups such as not of the type user-representative and permission  * @param uGroupId the ID of the group * @return Collection */
public  Collection getNonParentGroupsNonPermissionNonGeneral(int uGroupId){
	//public  Collection getRegisteredGroupsNotDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getParentGroups(group);
      Collection AllGroups =  getAllNonPermissionOrGeneralGroups();// Filters out userrepresentative/permission groups //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

      if(AllGroups != null){
        if(isDirectlyRelated != null){
            AllGroups.remove(isDirectlyRelated);
        }
        AllGroups.remove(group);
        return AllGroups;
      }else{
        return null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

	/**
	 * Gets all the groups that are indirect parents of the group by id uGroupId recursively up the group tree.	 * @param uGroupId	 * @return Collection of indirect parent (grandparents etc.) Groups	 */
	public  Collection getParentGroupsInDirect(int uGroupId){
	//  public  Collection getGroupsContainingNotDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getParentGroups(group);
      Collection AllGroups =  getParentGroupsRecursive(uGroupId);   //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

      if(AllGroups != null){
        if(isDirectlyRelated != null){
          Iterator iter = isDirectlyRelated.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            AllGroups.remove(item);
            //while(AllGroups.remove(item)){}
          }
        }
        AllGroups.remove(group);
        return AllGroups;
      }else{
        return null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
/**
 * Returns recursively up the group tree parents of group aGroup
 * @param uGroupId an id of the Group to be found parents recursively for.
 * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured
 */
  public Collection getParentGroupsRecursive(int uGroupId)throws EJBException{
  //public  Collection getGroupsContaining(int uGroupId)throws EJBException{
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      return getParentGroupsRecursive(group);
    }
    catch (Exception ex) {
      throw new IBORuntimeException(ex);
    }
  }

  
  
/**
 * Returns recursively up the group tree parents of group aGroup * @param aGroup The Group to be found parents recursively for. * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured */
	public  Collection getParentGroupsRecursive(Group aGroup) throws EJBException {
  		return getParentGroupsRecursive(aGroup, null, null);
	}
	
	/**
	 * Optimized version of getParentGroupsRecursive(Group) by Sigtryggur 22.06.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	public  Collection getParentGroupsRecursive(Group aGroup, Map cachedParents, Map cachedGroups  ) throws EJBException {
		return getParentGroupsRecursive(aGroup,getUserRepresentativeGroupTypeStringArray(),false, cachedParents, cachedGroups);
	}

	public String[] getUserRepresentativeGroupTypeStringArray(){
		if(userRepresentativeType == null){
			userRepresentativeType = new String[1];
			userRepresentativeType[0] = this.getUserGroupRepresentativeHome().getGroupType();
		}
		return userRepresentativeType;
	}



/**
 * Returns recursively up the group tree parents of group aGroup with filtered out with specified groupTypes * @param aGroup a Group to find parents for * @param groupTypes the Groups a String array of group types to be filtered with * @param returnSpecifiedGroupTypes if true it returns the Collection with all the groups that are of the types specified in  groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[]) * @return Collection of Groups found recursively up the tree * @throws EJBException If an error occured */
  public  Collection getParentGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException{
  	return getParentGroupsRecursive(aGroup, groupTypes, returnSpecifiedGroupTypes, null, null);
  }

  

  private  Collection getParentGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes, Map cachedParents, Map cachedGroups) throws EJBException{  	
  	if(useStoredProsedureGettingParentGroupsRecursive()){
  		return getParentGroupsRecursiveUsingStoredProcedure(aGroup,groupTypes,returnSpecifiedGroupTypes);
  	} else {
  		return getParentGroupsRecursiveNotUsingStoredProcedure(aGroup,groupTypes,returnSpecifiedGroupTypes,cachedParents,cachedGroups);
  	}
  }
  
  
  
  
/**
 * Optimized version of getParentGroupsRecursive(Group,String[],boolean) by Sigtryggur 22.06.2004
 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
 */
  private  Collection getParentGroupsRecursiveNotUsingStoredProcedure(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes, Map cachedParents, Map cachedGroups) throws EJBException{  	
  //public  Collection getGroupsContaining(Group groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws EJBException,RemoteException{

	Collection groups = aGroup.getParentGroups(cachedParents, cachedGroups);
	
	if (groups != null && groups.size() > 0){
	  Map GroupsContained = new LinkedHashMap();
	
	  String key = "";
	  Iterator iter = groups.iterator();
	  while (iter.hasNext()) {
	    Group item = (Group)iter.next();
	    if(item!=null){
	    	key = item.getPrimaryKey().toString();
	   		if(!GroupsContained.containsKey(key)){
	      		GroupsContained.put(key,item);
	      		putGroupsContaining( item, GroupsContained,groupTypes, returnSpecifiedGroupTypes, cachedParents, cachedGroups );
	    	}
	   	}
	  }
	  
		List specifiedGroups = new ArrayList();
		List notSpecifiedGroups = new ArrayList();
		int j = 0;
		int k = 0;
		Iterator iter2 = GroupsContained.values().iterator();
		if(groupTypes != null && groupTypes.length > 0){
			boolean specified = false;
			while (iter2.hasNext()) {
				Group tempObj = (Group)iter2.next();
				for (int i = 0; i < groupTypes.length; i++) {
					if (tempObj.getGroupType().equals(groupTypes[i])){
						specifiedGroups.add(j++, tempObj);
						specified = true;
					}
				}
				if(!specified){
					notSpecifiedGroups.add(k++, tempObj);
				}else{
					specified = false;
				}
			}
			notSpecifiedGroups.remove(aGroup);
			specifiedGroups.remove(aGroup);
		} else {
			while (iter2.hasNext()) {
				Group tempObj = (Group)iter2.next();
				notSpecifiedGroups.add(j++, tempObj);
			}
			notSpecifiedGroups.remove(aGroup);
			returnSpecifiedGroupTypes = false;
		}
		
		return (returnSpecifiedGroupTypes) ? specifiedGroups : notSpecifiedGroups;
		
		/////REMOVE AFTER IMPLEMENTING PUTGROUPSCONTAINED BETTER
	  
	}else{
	  return null;
	}
  }

  private  void putGroupsContaining(Group group, Map GroupsContained , String[] groupTypes, boolean returnGroupTypes ) {
  	putGroupsContaining(group, GroupsContained, groupTypes, returnGroupTypes, null, null);
  }
  
/**
 * Optimized version of putGroupsContaining(Group, Map, String[], boolean) by Sigtryggur 22.06.2004
 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
 */
  private  void putGroupsContaining(Group group, Map GroupsContained , String[] groupTypes, boolean returnGroupTypes, Map cachedParents, Map cachedGroups ) {
  	Collection pGroups = null;
  	if (cachedParents == null)
  		pGroups = group.getParentGroups();//TODO EIKI FINISH THIS groupTypes,returnGroupTypes);
  	else 
  		pGroups = group.getParentGroups(cachedParents, cachedGroups);
		if (pGroups != null ){
		  String key = "";
		  Iterator iter = pGroups.iterator();
		  while (iter.hasNext()) {
		    Group item = (Group)iter.next();
		    if(item!=null){
		      key = item.getPrimaryKey().toString();
		      
		      if(!GroupsContained.containsKey(key)){
		        GroupsContained.put(key,item);
		        putGroupsContaining(item, GroupsContained,groupTypes,returnGroupTypes, cachedParents, cachedGroups);
		      }
		    }
		  }
		}
  }



  public  Collection getUsers(int groupId) throws EJBException,FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getUsers(group);
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
  	}
  }



  public  Collection getUsersDirectlyRelated(int groupId) throws EJBException,FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getUsersDirectlyRelated(group);
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
  	}
  }



  public  Collection getUsersNotDirectlyRelated(int groupId) throws EJBException,FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getUsersNotDirectlyRelated(group);
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
  	}
  }

/**
 * Returns recursively down the group tree children of group with id groupId
 * @param groupId an id of a Group to find parents for
 * @return Collection of Groups found recursively down the tree
 * @throws EJBException If an error occured
 */
  public  Collection getChildGroupsRecursive(int groupId) throws EJBException,FinderException{
  //public  Collection getGroupsContained(int groupId) throws EJBException,FinderException,RemoteException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getChildGroupsRecursive(group);
  	}
  	catch(IOException e){
  		throw new IBORuntimeException(e,this);
  	}
  }

/**
 * Returns recursively down the group tree children of group aGroup with filtered out with specified groupTypes
 * @param aGroup a Group to find children for
 * @return Collection of Groups found recursively down the tree
 * @throws EJBException If an error occured
 */
   public  Collection getChildGroupsRecursive(Group aGroup) throws EJBException{
		return getChildGroupsRecursive(aGroup,getUserRepresentativeGroupTypeStringArray(),false);
  }



/**
 * Returns recursively down the group tree children of group aGroup with filtered out with specified groupTypes.
 * WHEN IT FINDS A GROUP THAT IS NOT OF A DESIRED GROUP TYPE IT STOPS FOR THAT GROUP. If you want to also recurse under those groups
 * use the method Collection getChildGroupsRecursiveResultFiltered(int groupId, Collection groupTypesAsString, boolean complementSetWanted).
 * @param aGroup a Group to find children for
 * @param groupTypes the Groups a String array of group types to be filtered with
 * @param returnSpecifiedGroupTypes if true it returns the Collection with all the groups that are of the types specified in  groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[])
 * @return Collection of Groups found recursively down the tree
 * @throws EJBException If an error occured
 */
  public Collection getChildGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException{
  //public Collection getGroupsContained(Group groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws RemoteException{
  try{

			Map GroupsContained = new HashMap();//to avoid duplicates
	    
	    Collection groups = aGroup.getChildGroups(groupTypes,returnSpecifiedGroupTypes);
	    
			//int j = 0;
			
	    if (groups != null && !groups.isEmpty() ){
	
	      String key = "";
	      Iterator iter = groups.iterator();
	      while (iter.hasNext()) {
	        Group item = (Group)iter.next();
	        if(item!=null){
		        key = item.getPrimaryKey().toString();     
		        if(!GroupsContained.containsKey(key)){
		          GroupsContained.put(key,item);
		          putGroupsContained( item, GroupsContained,groupTypes ,returnSpecifiedGroupTypes);
		        }
	        }
	      }

	      return new ArrayList(GroupsContained.values());
	    }else{
	      return null;
	    }
  	}
  	catch(IOException e){
  		throw new IBORuntimeException(e,this);
  	}
  
  }
  

/**
 * Return all the user directly under(related to) this group.
 * 
 * @see com.idega.user.business.GroupBusiness#getUsersContained(Group)
 */
  public Collection getUsers(Group group) throws FinderException{
    //filter
    User groupTypeProxy = (User)IDOLookup.instanciateEntity(User.class);

    return group.getChildGroups(groupTypeProxy);
  }
  
  /**
 * Return all the user under(related to) this group and any contained group recursively!
 * 
 * @see com.idega.user.business.GroupBusiness#getUsersContainedRecursive(Group)
 */
  public Collection getUsersRecursive(Group group) throws FinderException{
	try{
	    Collection list = getChildGroupsRecursive(group,getUserRepresentativeGroupTypeStringArray(),true);
	    if(list != null && !list.isEmpty()){
	      return getUsersForUserRepresentativeGroups(list);
	    } else {
	      return ListUtil.getEmptyList();
	    }
	}
	catch(RemoteException e){
		throw new IBORuntimeException(e,this);	
	}
  }
  
    /**
 * Return all the user under(related to) this group and any contained group recursively!
 * 
 * @see com.idega.user.business.GroupBusiness#getUsersContainedRecursive(Group)
 */
  public Collection getUsersRecursive(int groupId) throws FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getUsersRecursive(group);
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
  	}
  }	
		
 
/**
 * Returns all the groups that are direct children groups of group with id groupId.
 * @param groupId an id of a Group to find children groups for
 * @return Collection of Groups that are Direct children of group aGroup
 */
  public  Collection getChildGroups(int groupId) throws EJBException,FinderException{
  //public  Collection getGroupsContainedDirectlyRelated(int groupId) throws EJBException,FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getChildGroups(group);
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
  	}
  }

  /**
   * @see getChildGroupsRecursiveResultFiltered(Group group, Collection groupTypesAsString, boolean onlyReturnTypesInCollection)
   * @param groupId 
   * @param groupTypesAsString - a collection of strings representing group types, empty or null for any type
   * @param onlyReturnTypesInCollection - should be set to true if you want to fetch all the groups that have group types
   * that are contained in the collection groupTypesAsString else false to exclude those group types
   * @return a collection of groups
   */
  public Collection getChildGroupsRecursiveResultFiltered(int groupId, Collection groupTypesAsString, boolean onlyReturnTypesInCollection) {
    Group group = null;
    try{
      group = this.getGroupByGroupID(groupId);
    }
    catch (FinderException findEx)  {
      System.err.println(
        "[GroupBusiness]: Can't retrieve group. Message is: "
          + findEx.getMessage());
      findEx.printStackTrace(System.err);
      return new ArrayList();
    }
    catch (RemoteException ex) {
      System.err.println(
        "[GroupBusiness]: Can't retrieve group. Message is: "
          + ex.getMessage());
      ex.printStackTrace(System.err);
      throw new RuntimeException("[GroupBusiness]: Can't retrieve group.");
    }
    return getChildGroupsRecursiveResultFiltered(group, groupTypesAsString, onlyReturnTypesInCollection);
  }
    
    
  /** Returns all the groups that are direct and indirect children of the specified group.
   *  If the grouptype collection is not null and none empty the returned groups are filtered to only include or exclude those grouptypes
   * in the returning collection depending on whether the boolean is set to true or false.
   *  The method does not stop recursing a group even if that group is not specified in the desired grouptype collection.
   * Its children are always checked also that is the most important difference to the method getChildGroupsRecursive.
   * @param group
   * @param groupTypesAsString - a collection of strings representing group types, empty or null for any type
   * @param onlyReturnTypesInCollection - should be set to true if you want to fetch all the groups that have group types
   * that are contained in the collection groupTypesAsString else false to exclude those group types
   * @return a collection of groups
   */
  public Collection getChildGroupsRecursiveResultFiltered(Group group, Collection groupTypesAsString, boolean onlyReturnTypesInCollection) {
    // author: Thomas
    Collection alreadyCheckedGroups = new ArrayList();
    Collection result = new ArrayList();
    getChildGroupsRecursive(group, alreadyCheckedGroups, result, groupTypesAsString, onlyReturnTypesInCollection);
    return result;
  }
  
  public Collection getUsersFromGroupRecursive(Group group)  {
    return getUsersFromGroupRecursive(group, null, false);
  }

  public Collection getUsersFromGroupRecursive(Group group, Collection groupTypesAsString, boolean onlyReturnTypesInCollection)  {
    // author: Thomas
    Collection users = new ArrayList();
    Collection groups = getChildGroupsRecursiveResultFiltered(group, groupTypesAsString , onlyReturnTypesInCollection);
    Iterator iterator = groups.iterator();
    while (iterator.hasNext())  {
      Group tempGroup = (Group) iterator.next();
      try {
        users.addAll(getUsers(tempGroup));
      }
      catch (Exception ex)  {};
    }
    return users;
  }

  private void getChildGroupsRecursive
    ( Group currentGroup, 
      Collection alreadyCheckedGroups, 
      Collection result, 
      Collection groupTypesAsString,
      boolean onlyReturnTypesInCollection)  {
  	
    Integer currentPrimaryKey = (Integer) currentGroup.getPrimaryKey();
    if (alreadyCheckedGroups.contains(currentPrimaryKey)) {
      // already checked, avoid looping 
      return;
    }
    alreadyCheckedGroups.add(currentPrimaryKey);
    String currentGroupType = currentGroup.getGroupType();
    // does the current group belong to the result set?
    //if both are true or false then it belongs, otherwise not. (using XOR)
    if(groupTypesAsString==null || groupTypesAsString.isEmpty()){
    		//no specific type, add all
    		result.add(currentGroup);
    }
    else if (!(groupTypesAsString.contains(currentGroupType) ^ ( onlyReturnTypesInCollection) ) )  {
      result.add(currentGroup);
    }
    // go further
    Collection children = currentGroup.getChildGroups();
    Iterator childrenIterator = children.iterator();
    while (childrenIterator.hasNext())  {
      Group child = (Group) childrenIterator.next();
      getChildGroupsRecursive(child, alreadyCheckedGroups, result, groupTypesAsString, onlyReturnTypesInCollection);
    }
  }

/**
 * Returns all the groups that are direct children groups of group aGroup. * @param aGroup a group to find children groups for * @return Collection of Groups that are Direct children of group aGroup */
  public  Collection getChildGroups(Group aGroup){
    return getChildGroups(aGroup, getUserRepresentativeGroupTypeStringArray(),false);
  }

  public Collection getChildGroups(Group aGroup, Collection groupTypes, boolean returnSpecifiedGroupTypes) {
  		return getChildGroups(aGroup, ((String[]) groupTypes.toArray(new String[groupTypes.size()])), returnSpecifiedGroupTypes);
  }
  
  public Collection getChildGroups(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) {
    try {
      Collection list = aGroup.getChildGroups(groupTypes,returnSpecifiedGroupTypes);
      if(list != null){
        list.remove(aGroup);
      }
      return list;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public  Collection getUsersDirectlyRelated(Group group) throws EJBException,RemoteException,FinderException{
  	//TODO GET USERS DIRECTLY
    Collection result = group.getChildGroups(this.getUserRepresentativeGroupTypeStringArray(),true);
    return getUsersForUserRepresentativeGroups(result);
  }

/** * @param groupId a group to find Groups under * @return Collection A Collection of Groups that are indirect children (grandchildren etc.)  of the specified group recursively down the group tree * @throws FinderException if there was an error finding the group by id groupId * @throws EJBException if other errors occur.
 */ 
public  Collection getChildGroupsInDirect(int groupId) throws EJBException,FinderException{ 
  //public  Collection getGroupsContainedNotDirectlyRelated(int groupId) throws EJBException,FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getChildGroupsInDirect(group);
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
  	}
  }
  
/**
 * @param group a group to find Groups under
 * @return Collection A Collection of Groups that are indirect children (grandchildren etc.)  of the specified group recursively down the group tree
 * @throws EJBException if an error occurs.
 */ 
  public  Collection getChildGroupsInDirect(Group group) throws EJBException{
  //public  Collection getGroupsContainedNotDirectlyRelated(Group group) throws EJBException{
    try {
      Collection isDirectlyRelated = getChildGroups(group);
      Collection AllGroups = getChildGroupsRecursive(group);

      if(AllGroups != null){
        if(isDirectlyRelated != null){
        	AllGroups.removeAll(isDirectlyRelated);
        }
        AllGroups.remove(group);
        return AllGroups;
      }else{
        return null;
      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUsersNotDirectlyRelated(Group group) throws EJBException,RemoteException,FinderException{

    Collection DirectUsers = getUsersDirectlyRelated(group);
    Collection notDirectUsers = getUsers(group);

    if(notDirectUsers != null){
      if(DirectUsers != null){
        Iterator iter = DirectUsers.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          notDirectUsers.remove(item);
        }
      }
      return notDirectUsers;
    }else{
      return null;
    }
    /*
    if(notDirectUsers != null){
      notDirectUsers.removeAll(DirectUsers);
    }
    return notDirectUsers;
    */
  }


  private  void putGroupsContained(Group group,Map GroupsContained, String[] groupTypes, boolean returnGroupTypes ) throws RemoteException{
    Collection childGroups = group.getChildGroups(groupTypes,returnGroupTypes);
    if (childGroups != null && !childGroups.isEmpty() ){
      String key = "";
      Iterator iter = childGroups.iterator();
      while (iter.hasNext()) {
        Group item = (Group)iter.next();
        key = item.getPrimaryKey().toString();
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
					putGroupsContained(item, GroupsContained, groupTypes, returnGroupTypes);
        }
      }
    }
    
  }
 


/**
 * @param groupIDs a string array of IDs to be found.
 * @return A Collection of groups with the specified ids. * @see com.idega.user.business.GroupBusiness#getGroups(String[]) */
  public  Collection getGroups(String[] groupIDs) throws FinderException,RemoteException {
    return this.getGroupHome().findGroups(groupIDs);
  }


  public Collection getUsersForUserRepresentativeGroups(Collection groups)throws FinderException,RemoteException{
    try {
      return this.getUserHome().findUsersForUserRepresentativeGroups(groups);
    }
    catch (FinderException ex) {
      System.err.println(ex.getMessage());
      return new Vector(0);
    }
  }


  public  void updateUsersInGroup( int groupId, String[] usrGroupIdsInGroup, User currentUser) throws RemoteException,FinderException {

    if(groupId != -1){
      Group group = this.getGroupByGroupID(groupId);
      //System.out.println("before");
      Collection lDirect = getUsersDirectlyRelated(groupId);
      Set direct = new HashSet();
      if(lDirect != null){
        Iterator iter = lDirect.iterator();
        while (iter.hasNext()) {
          User item = (User)iter.next();
          direct.add(Integer.toString(item.getGroupID()));
          //System.out.println("id: "+ item.getGroupID());
        }
      }

      //System.out.println("after");
      Set toRemove = (Set)((HashSet)direct).clone();
      Set toAdd = new HashSet();

      if(usrGroupIdsInGroup != null){
        for (int i = 0; i < usrGroupIdsInGroup.length; i++) {

          if(direct.contains(usrGroupIdsInGroup[i])){
            toRemove.remove(usrGroupIdsInGroup[i]);
          } else {
            toAdd.add(usrGroupIdsInGroup[i]);
          }

          //System.out.println("id: "+ usrGroupIdsInGroup[i]);
        }
      }

      //System.out.println("toRemove");
      Iterator iter2 = toRemove.iterator();
      while (iter2.hasNext()) {
        String item = (String)iter2.next();
        //System.out.println("id: "+ item);
        group.removeGroup(Integer.parseInt(item), currentUser, false);
      }

      //System.out.println("toAdd");
      Iterator iter3 = toAdd.iterator();
      while (iter3.hasNext()) {
        String item = (String)iter3.next();
        //System.out.println("id: "+ item);
        group.addGroup(Integer.parseInt(item));
      }

    }else{
      //System.out.println("groupId = "+ groupId + ", usrGroupIdsInGroup = "+ usrGroupIdsInGroup);
    }


  }


  public Group getGroupByGroupID(int id)throws FinderException,RemoteException{
    return this.getGroupHome().findByPrimaryKey(new Integer(id));
  }
  
 
	public Collection getGroupsByGroupName(String name)throws RemoteException{
		try {
			return this.getGroupHome().findGroupsByName(name);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}
	
	/**
	 * Gets a collection of groups of the supplied group type and which names start with the supplied name string ('name%')
	 * @param type the group
	 * @param name
	 * @return
	 * @throws RemoteException
	 */
	public Collection getGroupsByGroupTypeAndFirstPartOfName(String groupType, String groupNameStartsWith)throws RemoteException{
		try {
			if(!groupNameStartsWith.endsWith("%")){
				groupNameStartsWith = groupNameStartsWith + "%";
			}
			return this.getGroupHome().findGroupsByGroupTypeAndLikeName(groupType,groupNameStartsWith);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}
	
	public Collection getGroupsByAbbreviation(String abbreviation)throws RemoteException{
		try {
			return this.getGroupHome().findGroupsByAbbreviation(abbreviation);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

  public User getUserByID(int id)throws FinderException,RemoteException{
    return this.getUserHome().findByPrimaryKey(new Integer(id));
  }

  public void addUser(int groupId, User user)throws EJBException,RemoteException{
    try{
      this.getGroupByGroupID(groupId).addGroup(user);
    }
    catch(FinderException fe){
      throw new EJBException(fe.getMessage());
    }
  }




  /**
   * Not yet implemented
   */
  public GroupHome getGroupHome(String groupType){
    if(groupHome==null){
      try{
        /**
         * @todo: implement
         */
        groupHome = (GroupHome)IDOLookup.getHome(Group.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return groupHome;
  }


	public GroupRelationHome getGroupRelationHome(){
		 if(groupRelationHome==null){
			 try{
				groupRelationHome = (GroupRelationHome)IDOLookup.getHome(GroupRelation.class);
			 }
			 catch(RemoteException rme){
				 throw new RuntimeException(rme.getMessage());
			 }
		 }
		 return groupRelationHome;
	 }
	

	/**
	   * Creates or updated a group from an LDAP DN and its attributes and adds it under the root (directly under in the group tree) of the default Domain (ICDomain) and/or the supplied parent group
	 * @throws NamingException,RemoteException,CreateException
	 * @see com.idega.user.business.GroupBusiness#createOrUpdateGroup(DN distinguishedName,Attributes attributes, boolean createUnderRootGroup, Group parentGroup)
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
			type = getGroupTypeHome().getGeneralGroupTypeString();
		}
		//needs to be done, otherwise the create group will fail or at least the group is not displayed in the userapp
		createVisibleGroupType(type);
		
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
	  		group = createGroup(name,description,type,homePageID,homeFolderID,aliasID,createUnderRootDomainGroup,parentGroup);
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
	
/**
 * Tries to to get the group by its unique id and then tries to find it by the distinguished name (LDAP) if that fails.
 * Both values do NOT need to be set.
	 * @param distinguishedName
	 * @param uniqueID
	 * @param ldapUtil
	 * @return The group if found but otherwise null
	 * @throws RemoteException
	 */
	public Group getGroupByDNOrUniqueId(DN distinguishedName, String uniqueID) throws RemoteException {
		Group group = null;
	  	IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
	  	if(uniqueID!=null){
		  	try {
				group = getGroupByUniqueId(uniqueID);
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

public Group getGroupByUniqueId(String uniqueID) throws FinderException {
		Group group;
		group = getGroupHome().findGroupByUniqueId(uniqueID);
		return group;
	}

/**
 * Adds or changes the groups current metadata with metadatafields contained in the attributes.
 * It does not currently remove any existing metadata unless the value in the attributes is an empty string ("")
	 * @param group
	 * @param distinguishedName
	 * @param attributes
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
	
	

/**
   * Creates or updated a group from an LDAP DN and its attributes and adds it under the root (directly under in the group tree) of the default Domain (ICDomain)
 * @throws NamingException,RemoteException,CreateException
 * @see com.idega.user.business.GroupBusiness#createOrUpdateGroup(DN distinguishedName,Attributes attributes)
 */
  public Group createOrUpdateGroup(DN distinguishedName,Attributes attributes)throws CreateException,NamingException,RemoteException{
  	return createOrUpdateGroup(distinguishedName,attributes,true,null);
  }
  
  /**
   * Creates a group from an LDAP DN and its attributes and adds it under the parentGroup supplied
 * @see com.idega.user.business.GroupBusiness#createOrUpdateGroup(DN distinguishedName,Attributes attributes,Group parentGroup)
 */
  public Group createOrUpdateGroup(DN distinguishedName,Attributes attributes, Group parentGroup)throws CreateException,NamingException,RemoteException{
  	return createOrUpdateGroup(distinguishedName,attributes,false,parentGroup);
  }
	 
/**
   * Creates a general group and adds it under the root (directly under in the group tree) of the default Domain (ICDomain)
 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
 */
  public Group createGroup(String name)throws CreateException,RemoteException{
  	String description = "";
  	return createGroup(name,description);
  }

/**
   * Creates a general group and adds it under the root (directly under in the group tree) of the default Domain (ICDomain)
 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
 */
  public Group createGroup(String name,String description)throws CreateException,RemoteException{
  	String generaltype = getGroupHome().getGroupType();
  	return createGroup(name,description,generaltype);
  }


/**
   * Creates a group and adds it under the root (directly under in the group tree) of the default Domain (ICDomain) * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String) */
  public Group createGroup(String name,String description,String type)throws CreateException,RemoteException{
  	return createGroup(name,description,type,-1);
  }
  
  /**
	 * Creates a group and adds it under the default Domain (ICDomain)<br>
	 * If createUnderDomainRoot is true it is added under the root (directly under in the group tree) of the domain.
   * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
   */
	public Group createGroup(String name,String description,String type,boolean createUnderDomainRoot)throws CreateException,RemoteException{
	  return createGroup(name,description,type,-1,-1,createUnderDomainRoot,null);
	}
	
	/**
	   * Creates a group and adds it under the default Domain (IBDomain) and under the group parentGroup.
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
	 */
	  public Group createGroupUnder(String name,String description,String type,Group parentGroup)throws CreateException,RemoteException{
		return createGroup(name,description,type,-1,-1,false,parentGroup);
	  }
	  
	/**
	   * Creates a general group and adds it under the default Domain (IBDomain) and under the group parentGroup.
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
	 */
	  public Group createGroupUnder(String name,String description,Group parentGroup)throws CreateException,RemoteException{
		String generaltype = getGroupHome().getGroupType();
	  	return createGroup(name,description,generaltype,-1,-1,false,parentGroup);
	  }
  
  /**
   * Creates a group and adds it under the root (directly under in the group tree) of the default Domain (ICDomain)   * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String, int)   */
  public Group createGroup(String name,String description,String type,int homePageID)throws CreateException,RemoteException{
		return createGroup(name,description,type,-1,-1);
  }
  /**
   * Creates a group and adds it under the root (directly under in the group tree) of the default Domain (ICDomain)
   * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String, int)
   */
  public Group createGroup(String name,String description,String type,int homePageID,int aliasID)throws CreateException,RemoteException{
  	return createGroup(name,description,type,homePageID,aliasID,true,null);
  }
  
  /**
   * Creates a group and adds it under the the default Domain (ICDomain) and under the group parentGroup.
   * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String, int)
   */
  public Group createGroupUnder(String name,String description,String type,int homePageID,int aliasID,Group parentGroup)throws CreateException,RemoteException{
	return createGroup(name,description,type,homePageID,aliasID,false,parentGroup);
  }  
  
  protected Group createGroup(String name,String description,String type,int homePageID,int aliasID,boolean createUnderDomainRoot,Group parentGroup)throws CreateException,RemoteException{
	return createGroup(name,description,type,homePageID,-1,aliasID,createUnderDomainRoot,parentGroup);
  } 
  
  protected Group createGroup(String name,String description,String type,int homePageID,int homeFolderID,int aliasID,boolean createUnderDomainRoot,Group parentGroup)throws CreateException,RemoteException{
		Group newGroup;
		newGroup = getGroupHome().create();
		newGroup.setName(name);
		newGroup.setDescription(description);
		
		newGroup.setGroupType(type);
		if ( homePageID != -1 ) {
			newGroup.setHomePageID(homePageID);
		}
		if (aliasID != -1) {
			newGroup.setAliasID(aliasID);
		}
		
		if ( homeFolderID != -1 ) {
			newGroup.setHomeFolderID(homeFolderID);
		}
		
		newGroup.store(); 
		
		if(homeFolderID == -1 ) {
			createGroupHomeFolder(newGroup);
		}
		
		if(createUnderDomainRoot){
			addGroupUnderDomainRoot(this.getIWApplicationContext().getDomain(),newGroup);
		}
		else{
			addGroupUnderDomain(this.getIWApplicationContext().getDomain(),newGroup,null);
		}
		if(parentGroup!=null){
			parentGroup.addGroup(newGroup);
		}
		
		return newGroup;
  }
  
	public ICFileHome getICFileHome(){
		if(fileHome==null){
			try{
				fileHome = (ICFileHome)IDOLookup.getHome(ICFile.class);
			}
			catch(RemoteException rme){
				throw new RuntimeException(rme.getMessage());
			}
		}
		return fileHome;
	}

	public ICFile createGroupHomeFolder(Group group) throws CreateException {
		ICFile file = (ICFile)getICFileHome().create();
		file.setName(group.getName());
		file.setLocalizationKey(GROUP_HOME_FOLDER_LOCALIZATION_PREFIX+group.getGroupType());
		file.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		file.setDescription("This is a home folder for a group");
		file.store();
		
		group.setHomeFolder(file);
		group.store();
		
		return file;
	}

	public Collection getAllAllowedGroupTypesForChildren(int groupId, IWUserContext iwuc)  {
    // try to get the group
    Group group;
    try {
      group = (groupId > -1) ? getGroupByGroupID(groupId) : null;
    }
    catch (Exception ex)  {
      throw new RuntimeException(ex.getMessage());
    }  
    return getAllAllowedGroupTypesForChildren(group, iwuc);
  }  
        
  /**
   * It is allowed and makes sense if the parameter group is null: 
   * In this case alias and general group type is returned.
   */      
	public Collection getAllAllowedGroupTypesForChildren(Group group, IWUserContext iwuc) {
		GroupTypeHome groupTypeHome;
		GroupType groupType;
		String groupTypeString;
		try {
			groupTypeHome = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			// super admin: return all group types
			if (iwuc.isSuperAdmin()) {
				try {
					if (groupTypeHome.getNumberOfVisibleGroupTypes() <= 0)
						((com.idega.data.GenericEntity) com.idega.data.IDOLookup.instanciateEntity(GroupType.class)).insertStartData();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return groupTypeHome.findVisibleGroupTypes();
			}
			// try to get the corresponding group type
			if (group != null) {
				groupTypeString = group.getGroupType();
				groupType = groupTypeHome.findByPrimaryKey(groupTypeString);
			}
			else {
				// okay, group is null, but we need an instance
				// to get the alias and general group type
				groupTypeString = "";
				groupType = GroupTypeBMPBean.getStaticInstance();
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
		
		// get general and alias group type
		GroupType generalType = findOrCreateGeneralGroupType(groupType, groupTypeHome);
		GroupType aliasType = findOrCreateAliasGroupType(groupType, groupTypeHome);
		
		ArrayList groupTypes = new ArrayList();
		if (group == null) {
			// first case: group is null
			groupTypes.add(generalType);
			groupTypes.add(aliasType);
		}
		else {
			
			if(!groupType.getOnlySupportsSameTypeChildGroups()){
				// add same type
				if(groupType.getSupportsSameTypeChildGroups()){
					groupTypes.add(groupType);
				}
				

				int typeSize = groupTypes.size();
				//add children of type of selected group
				addGroupTypeChildren(groupTypes, groupType);
				
				//we only add general and alias types if there are no child types defined
				if(groupTypes.size()<=typeSize){
					if (!generalType.getType().equals(groupTypeString) && !groupTypes.contains(groupType)) {
						groupTypes.add(generalType);
					}
					
					if (!aliasType.getType().equals(groupTypeString) && !groupTypes.contains(groupType)) {
						groupTypes.add(aliasType);
					}
				}
				
			}
			else{
				//so we can define a type that cannot have children
				if(groupType.getSupportsSameTypeChildGroups()){
					groupTypes.add(groupType);
				}
			}
		}
		return groupTypes;
	}

  public void addGroupTypeChildren(List list, GroupType groupType)  {
    Iterator iterator = groupType.getChildrenIterator();
    while (iterator != null && iterator.hasNext())  {
      GroupType child = (GroupType) iterator.next();
      if(!list.contains(child)){
      	list.add(child);
      }
      addGroupTypeChildren(list, child);
    }
  }
        
    
  
  public String getGroupType(Class groupClass)throws RemoteException{
    return ((GroupHome)IDOLookup.getHome(groupClass)).getGroupType();
  }
  
  public GroupType getGroupTypeFromString(String type) throws RemoteException, FinderException{
  	return getGroupTypeHome().findGroupTypeByGroupTypeString(type);
  }
  
  /**
   * Returns a collection of UserGroupPluginBusiness beans or an empty list
   * @param plugins
   * @return a collection of UserGroupPluginBusiness implementing classes
   */
  protected Collection getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(Collection plugins) {

  	if(plugins == null || plugins.isEmpty()) {
  	  	return ListUtil.getEmptyList();
  	}
  	ArrayList list = new ArrayList();
	Iterator iter = plugins.iterator();
	while (iter.hasNext()) {
		UserGroupPlugIn element = (UserGroupPlugIn) iter.next();
		UserGroupPlugInBusiness pluginBiz;
		try {
			pluginBiz = (UserGroupPlugInBusiness) getServiceInstance(Class.forName(element.getBusinessICObject().getClassName()) );
			list.add(pluginBiz);
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	return list;
  }

  
/**
 * Gets a collection of UserGroupPluginBusiness beans that can operate on the supplied group type
 * @param groupType
 * @return Collection of plugins
 * @throws RemoteException
 */
  public Collection getUserGroupPluginsForGroupType(String groupType) throws RemoteException{

	try {
		return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findRegisteredPlugInsForGroupType(groupType));
	}
	catch (FinderException e) {
		//no big deal, there are no plugins registered. Return an empty list
	}
	catch (IDORelationshipException e) {
//		no big deal, there are no plugins registered. Return an empty list
	}

	return ListUtil.getEmptyList();
	
  }
  
  /**
   * Gets a collection of UserGroupPluginBusiness beans that can operate on the supplied group
 * @param group
 * @return Collection of plugins
 * @throws RemoteException
   */
  public Collection getUserGroupPluginsForGroup(Group group) throws RemoteException {
  	try {
		return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findRegisteredPlugInsForGroup(group));
	}
	catch (FinderException e) {
		//no big deal, there are no plugins registered. Return an empty list
	}

	return ListUtil.getEmptyList();
  }
    
  /**
 * Gets a collection of UserGroupPluginBusiness beans that can operate on the supplied user
 * @param user
 * @return Collection of plugins
 * @throws RemoteException
 */
  public Collection getUserGroupPluginsForUser(User user) throws RemoteException{
  	try {
  		//TODO finna allar gruppur tengdar thessum user og gera find fall sem tekur inn i sig collection a groups 
		//THIS METHOD IS NOT FINISHED AND THE FIND METHOD ONLY GETS ALL PLUGINS
		return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findRegisteredPlugInsForUser(user));
	}
	catch (FinderException e) {
		//no big deal, there are no plugins registered. Return an empty list
	}
	
	return ListUtil.getEmptyList();
  }
  
  
  /**
   * Gets a collection of all registered UserGroupPluginBusiness beans
   * @return Collection of plugins
   * @throws RemoteException
   */
    public Collection getUserGroupPlugins() throws RemoteException{
	    	try {
	  		return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findAllPlugIns());
	  	}
	  	catch (FinderException e) {
	  		//no big deal, there are no plugins registered. Return an empty list
	  	}
	  	
	  	return ListUtil.getEmptyList();
    }
   
	public void callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(Group group) {
		// get plugins and call the method
		Collection pluginsForGroup;
		try {
			pluginsForGroup = getUserGroupPluginsForGroup(group);
			Iterator plugs = pluginsForGroup.iterator();
			while (plugs.hasNext()) {
				UserGroupPlugInBusiness plugBiz = (UserGroupPlugInBusiness) plugs.next();
				plugBiz.afterGroupCreateOrUpdate(group);
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
	}

	public void callAllUserGroupPluginBeforeGroupRemoveMethod(Group group) {
		// get plugins and call the method
		Collection pluginsForGroup;
		try {
			pluginsForGroup = getUserGroupPluginsForGroup(group);
			Iterator plugs = pluginsForGroup.iterator();
			while (plugs.hasNext()) {
				UserGroupPlugInBusiness plugBiz = (UserGroupPlugInBusiness) plugs.next();
				plugBiz.beforeGroupRemove(group);
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (RemoveException e) {
			e.printStackTrace();
		}
	}
    
  public GroupTypeHome getGroupTypeHome() throws RemoteException{
  	return  (GroupTypeHome) this.getIDOHome(GroupType.class);
  }
  	
  protected UserGroupPlugInHome getUserGroupPlugInHome() throws RemoteException{
  	return  (UserGroupPlugInHome) this.getIDOHome(UserGroupPlugIn.class);
  }

  /**
   * Adds a group direcly under the domain (right in top under the domain in the group tree).
   * This adds the group with GroupRelationType Top to the domain.
   * @param domain
   * @param group
   * @throws CreateException
   * @throws RemoteException
   */
  public void addGroupUnderDomainRoot(ICDomain domain, Group group) throws CreateException,RemoteException{
	GroupDomainRelationTypeHome gdrHome = (GroupDomainRelationTypeHome)getIDOHome(GroupDomainRelationType.class);
	GroupDomainRelationType domRelType;
	try {
		domRelType = gdrHome.getTopNodeRelationType();
		addGroupUnderDomain(domain,group,domRelType);
	}
	catch (FinderException e) {
		logWarning("Error finding GroupRelationType=TOP when adding group under domain");
		log(e);
	}
  }

  public void addGroupUnderDomain(ICDomain domain, Group group, GroupDomainRelationType type) throws CreateException,RemoteException{
    GroupDomainRelation relation = (GroupDomainRelation)IDOLookup.create(GroupDomainRelation.class);
    relation.setDomain(domain);
    relation.setRelatedGroup(group);

    if(type != null){
      relation.setRelationship(type);
    }

    relation.store();
  }


  /**
   * Method updateUsersMainAddressOrCreateIfDoesNotExist. This method can both be used to update the user main address or to create one<br>
   * if one does not exist. Only userId and StreetName(AndNumber) are required to be not null others are optional.
   * @param userId
   * @param streetNameAndNumber
   * @param postalCodeId
   * @param countryName
   * @param city
   * @param province
   * @param poBox
   * @return Address the address that was created or updated
   * @throws CreateException
   * @throws RemoteException
   */
  public Address updateGroupMainAddressOrCreateIfDoesNotExist(Integer groupId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws CreateException,RemoteException {
    Address address = null;
      if( streetNameAndNumber!=null && groupId!=null ){
        try{
          AddressBusiness addressBiz = getAddressBusiness();
          String streetName = addressBiz.getStreetNameFromAddressString(streetNameAndNumber);
          String streetNumber = addressBiz.getStreetNumberFromAddressString(streetNameAndNumber);
          
          Group group = getGroupByGroupID(groupId.intValue());
          address = getGroupMainAddress(group);
          
          Country country = null;
          
          if( countryName!=null ){
            country = ((CountryHome)getIDOHome(Country.class)).findByCountryName(countryName);
          }
          
          PostalCode code = null;
          if( postalCodeId!=null){
            code = ((PostalCodeHome)getIDOHome(PostalCode.class)).findByPrimaryKey(postalCodeId);
          }
                  
          
          boolean addAddress = false;/**@todo is this necessary?**/
  
          if( address == null ){
            AddressHome addressHome = addressBiz.getAddressHome();
            address = addressHome.create();
            AddressType mainAddressType = addressHome.getAddressType1();
            address.setAddressType(mainAddressType);
            addAddress = true;
          }
  
          if( country!=null ) address.setCountry(country);
          if( code!=null ) address.setPostalCode(code);
          if( province!=null ) address.setProvince(province);
          if( city!=null ) address.setCity(city);
          if( poBox!=null) address.setPOBox(poBox);
          
          address.setStreetName(streetName);
          if( streetNumber!=null ) address.setStreetNumber(streetNumber);
  
          address.store();
  
          if(addAddress){
            group.addAddress(address);
          }
        }
        catch(Exception e){
          e.printStackTrace();
          System.err.println("Failed to update or create address for groupid : "+ groupId.toString()); 
        }
          
      }
        else throw new CreateException("No streetname or userId is null!");
        
        return address;
  }

  public AddressBusiness getAddressBusiness() throws RemoteException{
    return (AddressBusiness) getServiceInstance(AddressBusiness.class);
  }
  
  /**
   * Gets the users main address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getGroupMainAddress(Group group) throws RemoteException, IDOLookupException, IDOCompositePrimaryKeyException, IDORelationshipException{
 		AddressType type = getAddressHome().getAddressType1();
  	Collection coll = group.getAddresses(type);
  	if (coll == null || coll.isEmpty()) {
  	  	return null;
  	}
  	// return the first element (there is only on element)
  	return (Address) coll.iterator().next();
  }  

  public AddressHome getAddressHome(){
    if(addressHome==null){
      try{
        addressHome = (AddressHome)IDOLookup.getHome(Address.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return addressHome;
  }
  
  public  Phone[] getGroupPhones(Group group)throws RemoteException{
    try {
      Collection phones = group.getPhones();
//    if(phones != null){
        return (Phone[])phones.toArray(new Phone[phones.size()]);
//    }
      //return (Phone[]) ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
    }
    catch (EJBException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Email getGroupEmail(Group group) {
    try {
      Collection L = group.getEmails();
      if(L != null){
        if ( ! L.isEmpty() )
          return (Email)L.iterator().next();
      }
      return null;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
  public void updateGroupMail(Group group, String email) throws CreateException,RemoteException {
    Email mail = getGroupEmail(group);
    boolean insert = false;
    if ( mail == null ) {
      mail = this.getEmailHome().create();
      insert = true;
    }

    if ( email != null ) {
      mail.setEmailAddress(email);
    }
    mail.store();
    if(insert){
      //((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(mail);
      try{
       group.addEmail(mail);
      }
      catch(Exception e){
        throw new RemoteException(e.getMessage());
      }
    }

  }

  public EmailHome getEmailHome(){
    if(emailHome==null){
      try{
        emailHome = (EmailHome)IDOLookup.getHome(Email.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return emailHome;
  }

  public void updateGroupPhone(Group group, int phoneTypeId, String phoneNumber) throws EJBException {
    try{
    Phone phone = getGroupPhone(group,phoneTypeId);
    boolean insert = false;
    if ( phone == null ) {
      phone = this.getPhoneHome().create();
      phone.setPhoneTypeId(phoneTypeId);
      insert = true;
    }

    if ( phoneNumber != null ) {
      phone.setNumber(phoneNumber);
    }

    phone.store();
    if(insert){
      //((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
      group.addPhone(phone);
    }

    }
    catch(Exception e){
      e.printStackTrace();
      throw new EJBException(e.getMessage());
    }


  }
  
  public  Phone getGroupPhone(Group group, int phoneTypeId)throws RemoteException{
    try {
      Phone[] result = this.getGroupPhones(group);
      //IDOLegacyEntity[] result = ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
      if(result != null){
        for (int i = 0; i < result.length; i++) {
          if(((Phone)result[i]).getPhoneTypeId() == phoneTypeId){
            return (Phone)result[i];
          }
        }
      }
      return null;
    }
    catch (EJBException ex) {
      ex.printStackTrace();
      return null;
    }
  }  

  public PhoneHome getPhoneHome(){
    if(phoneHome==null){
      try{
        phoneHome = (PhoneHome)IDOLookup.getHome(Phone.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return phoneHome;
  }
 
 
  /** Group is removeable if the group is either an alias or 
   *  has not any children.
   *  Childrens are other groups or users.
   * @param group
   * @return boolean 
   */
  public boolean isGroupRemovable(Group group)  { 
    try {
      return ( (group.getGroupType().equals("alias"))
          // childCount checks only groups as children
          || (group.getChildCount() <= 0 && 
             ( getUserBusiness().getUsersInGroup(group).isEmpty())));
    }
    catch (java.rmi.RemoteException rme) {
      throw new RuntimeException(rme.getMessage());
    } 
  }
 
  public String getNameOfGroupWithParentName(Group group, Collection parentGroups) {
    StringBuffer buffer = new StringBuffer();    
    Collection parents = parentGroups;
    buffer.append(group.getName()).append(" ");
		if(parents!=null && !parents.isEmpty()) {
		  Iterator par = parents.iterator();
		  Group parent = (Group) par.next();
		  buffer.append("(").append(parent.getName()).append(") ");
		}
		
		return buffer.toString();
  }
  
  public String getNameOfGroupWithParentName(Group group) {  
    return getNameOfGroupWithParentName(group,getParentGroups(group));
  }
  
  
/**
 * Optimized version of getNameOfGroupWithParentName(Group group) by Gummi 25.08.2004
 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
 */
  public String getNameOfGroupWithParentName(Group group,Map cachedParents, Map cachedGroups) {  
    return getNameOfGroupWithParentName(group,getParentGroups(group,cachedParents,cachedGroups));
  }

 
  private UserBusiness getUserBusiness() {
    IWApplicationContext context = getIWApplicationContext();
    try {
      return (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(context, UserBusiness.class);
    }
    catch (java.rmi.RemoteException rme) {
      throw new RuntimeException(rme.getMessage());
    }
  } 
  
  /**
   * Creates a visible group type from the supplied group type string if it does not already exist,
   * if it exists it will update the group types visibilty to true.
   * @param groupType
   * @return a GroupType bean
   * @throws RemoteException
   */
  public GroupType createVisibleGroupType(String groupType) throws RemoteException{
  	return createGroupTypeOrUpdate(groupType,true);
  }
  /**
   * Creates a group type that has the visibility supplied if the type does not already exist.
   * If it exist this method will update its visibility.
   * @param groupType
   * @param visible
   * @return a GroupType bean
   * @throws RemoteException
   */
  public GroupType createGroupTypeOrUpdate(String groupType, boolean visible) throws RemoteException{
  	GroupTypeHome home = getGroupTypeHome();
  	 try {
        GroupType type = home.findByPrimaryKey(groupType);
        type.setVisibility(visible);
        return type;
      }
      catch (FinderException findEx)  {
        try {
	        GroupType type = home.create();
	        type.setType(groupType);
	        type.setVisibility(visible);
	        type.store();
	        return type;
        }
        catch (CreateException createEx)  {
          throw new RuntimeException(createEx.getMessage());
        }
      }
  }
  
  private GroupType findOrCreateAliasGroupType(GroupType aGroupType, GroupTypeHome home) {  
    try {
      GroupType type = home.findByPrimaryKey(home.getAliasGroupTypeString());
      return type;
    }
    catch (FinderException findEx)  {
      try {
	      GroupType type = home.create();
	      type.setGroupTypeAsAliasGroup();
	      type.setVisibility(true);
	      type.store();
	      return type;
      }
      catch (CreateException createEx)  {
        throw new RuntimeException(createEx.getMessage());
      }
    }
  }
    

  private GroupType findOrCreateGeneralGroupType(GroupType aGroupType, GroupTypeHome home) {  
    try {
      GroupType type = home.findByPrimaryKey(home.getGeneralGroupTypeString());
      return type;
    }
    catch (FinderException findEx)  {
      try {
	      GroupType type = home.create();
	      type.setGroupTypeAsGeneralGroup();
	      type.setVisibility(true);
	      type.store();
	      return type;
      }
      catch (CreateException createEx)  {
        throw new RuntimeException(createEx.getMessage());
      }
    }
  }  
  
  /**
   * Gives all parent groups owners' primary groups, permit permission to this group.
   * The permission to give others permissions to this group.
   */
  public void applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups(Group group) throws RemoteException {
      UserBusiness userBiz = getUserBusiness();
      String groupId = group.getPrimaryKey().toString();
	  AccessController access = getAccessController();
		
      Collection col = getParentGroupsRecursive(group);
      if (col != null && !col.isEmpty()) {
          Iterator iter = col.iterator();
          while (iter.hasNext()) {
              Group parent = (Group) iter.next();
              Collection owners = AccessControl.getAllOwnerGroupPermissionsReverseForGroup(parent);

              if (owners != null && !owners.isEmpty()) {
                  Iterator iter2 = owners.iterator();
                  while (iter2.hasNext()) {

                      ICPermission perm = (ICPermission) iter2.next();
                      User user = userBiz.getUser(perm.getGroupID());
                      Group primary = user.getPrimaryGroup();
                      if(primary!=null) {
	                      String primaryGroupId = primary.getPrimaryKey().toString();
	                      try {
	                          //the owners primary group
	                          access.setPermission(
	                                  AccessController.CATEGORY_GROUP_ID, this.getIWApplicationContext(),
	                                  primaryGroupId, groupId,
	                                  access.PERMISSION_KEY_PERMIT, Boolean.TRUE);
	                      } catch (Exception e) {
	                          e.printStackTrace();
	                      }
                      }
                  }
              }
          }
      }
  }
  
  /**
   * Sets the user as an owner of the group.
   * @param group
   * @param user
   */
  public void applyUserAsGroupsOwner(Group group, User user) {
	  AccessController access = getAccessController();
	
      try {
          access.setAsOwner(group, ((Integer) user.getPrimaryKey())
                  .intValue(), this.getIWApplicationContext());
      } catch (Exception ex) {
          ex.printStackTrace();

      }
  }
  
  public void applyCurrentUserAsOwnerOfGroup(IWUserContext iwc, Group group) {
      User user = iwc.getCurrentUser();
      applyUserAsGroupsOwner(group, user);
  }
  

  /**
   * Give the current users primary group all permission except for owner
   * 
   */
      public void applyAllGroupPermissionsForGroupToCurrentUsersPrimaryGroup(IWUserContext iwuc, Group group) {
          
          User user = iwuc.getCurrentUser();
          
          Group groupToGetPermissions = user.getPrimaryGroup();
          applyAllGroupPermissionsForGroupToGroup(group, groupToGetPermissions);
      }
      
  /**
   * Give the users primary group all permission except for owner
   * 
   */
  public void applyAllGroupPermissionsForGroupToUsersPrimaryGroup(Group group, User user) {
      
      Group groupToGetPermissions = user.getPrimaryGroup();
      applyAllGroupPermissionsForGroupToGroup(group, groupToGetPermissions);
  }      

  /**
   * This methods gives the second group specified all permissions to the other groups except for owner permission (set to users not groups).
   * The permissions include: view,edit,create,remove users, and the permission to give others permissions to it.
   * @param iwac
   * @param groupToSetPermissionTo The group the permission apply to.
   * @param groupToGetPermissions The group that will own the permissions e.g. get the rights to do the stuff.
   */
  public void applyAllGroupPermissionsForGroupToGroup(Group groupToSetPermissionTo, Group groupToGetPermissions) {
	  AccessController access = getAccessController();
      try {
		  IWApplicationContext iwac = this.getIWApplicationContext();
          String groupId = groupToGetPermissions.getPrimaryKey()
                  .toString();
          String theGroupIDToSetPermissionTo = groupToSetPermissionTo.getPrimaryKey()
                  .toString();

          //create permission
          access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac,
                  groupId, theGroupIDToSetPermissionTo,
                  access.PERMISSION_KEY_CREATE, Boolean.TRUE);
          //edit permission
          access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac,
                  groupId, theGroupIDToSetPermissionTo,
                  access.PERMISSION_KEY_EDIT, Boolean.TRUE);
          //delete permission
          access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac,
                  groupId, theGroupIDToSetPermissionTo,
                  access.PERMISSION_KEY_DELETE, Boolean.TRUE);
          //view permission
          access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac,
                  groupId, theGroupIDToSetPermissionTo,
                  access.PERMISSION_KEY_VIEW, Boolean.TRUE);
          //permission to give other permission
          access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac,
                  groupId, theGroupIDToSetPermissionTo,
                  access.PERMISSION_KEY_PERMIT, Boolean.TRUE);
      } catch (Exception ex) {
          ex.printStackTrace();
      }
  }
  
  /**
   * If the groupToGetInheritanceFrom has inherited permission it is copied to the other group.
   * 
   * @param groupToGetInheritanceFrom
   * @param groupToInheritPermissions
   */
  public void applyPermissionControllingFromGroupToGroup(Group groupToGetInheritanceFrom, Group groupToInheritPermissions) {

      if (groupToGetInheritanceFrom != null) {
          //is controller
          if (groupToGetInheritanceFrom.isPermissionControllingGroup()) {
              groupToInheritPermissions.setPermissionControllingGroup(groupToGetInheritanceFrom);
              groupToInheritPermissions.store();
          }
          //is being controlled
          if (groupToGetInheritanceFrom.getPermissionControllingGroupID() > 0) {
              groupToInheritPermissions.setPermissionControllingGroup(groupToGetInheritanceFrom
                      .getPermissionControllingGroup());
              groupToInheritPermissions.store();
          }

      }

  }
  
  /**
   * This method should only be called once for a newly created group if it was done in code. This method is
   * automatically called if the group is created in the user application.
   * Sets the user as the owner of the group and gives his primary group all group permissions to the group. 
   * Also gives all owners' primary groups of the groups parent groups permission to give others permission 
   * to this group. Finally checks the groups parent if any for inherited permissions and sets them.
   * @param newlyCreatedGroup
   * @param user
   * @throws RemoteException
   */
  public void applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(Group newlyCreatedGroup, User user) throws RemoteException {

      //set user as owner of group
      applyUserAsGroupsOwner(newlyCreatedGroup, user);

      //give the users primary group all permission except for owner
      applyAllGroupPermissionsForGroupToUsersPrimaryGroup(newlyCreatedGroup, user);

      //owners should get the permission to give permission for this group
      applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups(newlyCreatedGroup);

      //check if to parent group is a permissions controlling group or has a reference to a permission controlling group
      Collection parentGroups = newlyCreatedGroup.getParentGroups();
      
      if(parentGroups!=null && !parentGroups.isEmpty()) {
          applyPermissionControllingFromGroupToGroup((Group)parentGroups.iterator().next(), newlyCreatedGroup);
      }
      
      //apply permissions that have been marked to be inherited to this group from its parents
      applyInheritedPermissionsToGroup(newlyCreatedGroup);
      
  }
  
  /**
	 * Applies permissions that have been marked to be inherited to this group
	 * from its parents
	 * 
	 * @param newlyCreatedGroup
	 * @throws RemoteException
	 */
	public void applyInheritedPermissionsToGroup(Group newlyCreatedGroup) throws RemoteException {
		AccessController access = getAccessController();
		Collection recursiveParents = getParentGroupsRecursive(newlyCreatedGroup);
		if (recursiveParents != null && !recursiveParents.isEmpty()) {
			try {
				Collection permissions = AccessControl.getPermissionHome().findAllGroupPermissionsToInheritByGroupCollection(
						recursiveParents);
				Iterator iter = permissions.iterator();
				while (iter.hasNext()) {
					ICPermission perm = (ICPermission) iter.next();
					try {
						access.setPermission(AccessController.CATEGORY_GROUP_ID, this.getIWApplicationContext(),
								Integer.toString(perm.getGroupID()), newlyCreatedGroup.getPrimaryKey().toString(),
								perm.getPermissionString(), Boolean.TRUE);
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			catch (FinderException e) {
				e.printStackTrace();// no parents, might happen not really an
									// error
			}
		}
	}

/**
 * Returns a collection (list) of User objects that have owner permission to
 * this group
 * 
 * @param group
 *            to get owners for
 * @return
 * @throws RemoteException
 */
public Collection getOwnerUsersForGroup(Group group) throws RemoteException {
      Collection permissions = AccessControl.getAllOwnerGroupPermissionsReverseForGroup(group);
      ArrayList listOfOwnerUsers = new ArrayList();
      UserBusiness userBiz = getUserBusiness();
      
	    //we only want active ones
	    Iterator permissionsIter = permissions.iterator();
	    while (permissionsIter.hasNext()) {
	        ICPermission perm = (ICPermission) permissionsIter.next();
		    if(perm.getPermissionValue()){
		        listOfOwnerUsers.add(userBiz.getUser(perm.getGroupID()));
			}
	    }
    
	    return listOfOwnerUsers;
  }
 

	/**
	 * Finds the correct group from the database using the directory strings
	 * group structure
	 * 
	 * @param dn
	 * @return a Group data bean
	 */
	public Group getGroupByDirectoryString(DirectoryString dn) throws RemoteException {
		//TODO use one of the DN helper classes to do this cleaner
		String identifier = dn.getDirectoryString().toLowerCase();
		String dnFromMetaDataKey = IWLDAPConstants.LDAP_META_DATA_KEY_DIRECTORY_STRING;
		Group group = null;
		
		//try and get by metadata first then by inaccurate method
		Collection groups = getGroupsByMetaDataKeyAndValue(dnFromMetaDataKey,identifier);
		
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
				Collection potentialParent = getGroupsByMetaDataKeyAndValue(dnFromMetaDataKey,parentDN);
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
					
					Collection possibleGroups = getGroupsByGroupName(groupName);
					
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

	/**
	 * Gets all the groups that have this metadata key and value
	 * @param key
	 * @param value
	 * @return a collection of Groups or an empty list
	 */
	public Collection getGroupsByMetaDataKeyAndValue(String key, String value){
		Collection groups;
		try {
			groups = getGroupHome().findGroupsByMetaData(key,value);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
		
		return groups;
	}
	
    private  Collection getParentGroupsRecursiveUsingStoredProcedure(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException{  	
    		return ParentGroupsRecursiveProcedure.getInstance().findParentGroupsRecursive(aGroup,groupTypes,returnSpecifiedGroupTypes);
    }
    
    private boolean useStoredProsedureGettingParentGroupsRecursive(){
    		return false; //ParentGroupsRecursiveProcedure.getInstance().isAvailable();
    }
	
	
	public NestedSetsContainer getLastGroupTreeSnapShot() throws EJBException {
		if(groupTreeSnapShot==null){
			refreshGroupTreeSnapShot();
		}
		return groupTreeSnapShot;
	}
	
	public void refreshGroupTreeSnapShotInANewThread(){
		try {
			GroupTreeRefreshThread thread = new GroupTreeRefreshThread();
			thread.start();
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshGroupTreeSnapShot() throws EJBException {
		try {
			Collection domainTopNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
			NestedSetsContainer nsc = new NestedSetsContainer();
			Iterator iter = domainTopNodes.iterator();
			while(iter.hasNext()){
				nsc.add(GroupTreeImageProcedure.getInstance().getGroupTree((Group)iter.next()));
			}
			groupTreeSnapShot=nsc;
		}
		catch (Exception e) {
			throw new EJBException(e);
		}
	}
	
	public boolean userGroupTreeImageProcedureTopNodeSearch(){
		return GroupTreeImageProcedure.getInstance().isAvailable();
	}
	
	/**
	 * 
	 *  Last modified: $Date: 2005/05/31 11:40:02 $ by $Author: eiki $
	 * 
	 * @author <a href="mailto:gummi@idega.com">gummi</a>
	 * @version $Revision: 1.94 $
	 */
	public class GroupTreeRefreshThread extends Thread {
		
		private int randID;
		/**
		 * 
		 */
		public GroupTreeRefreshThread() {
			this("GroupTreeRefreshThread-",(int)(Math.random()*1000));
		}
		
		private GroupTreeRefreshThread(String name, int rand){
			super(name+rand);
			randID=rand;
		}
		
		public void run() {
			try {
				log("[GroupBusiness]: fetch grouptree, new thread started 'randID:"+randID+"'");
				refreshGroupTreeSnapShot();
				log("[GroupBusiness]: fetch grouptree, thread done 'randID:"+randID+"'");
			}
			catch (EJBException e) {
				e.printStackTrace();
			}
		}
		
		

	}
	
	
} // Class





/**
  * @todo move implementation from methodName(Group group) to methodName(int groupId)
  * @todo reimplement all methods returning list of users
  */
