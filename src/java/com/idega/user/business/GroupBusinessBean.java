package com.idega.user.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.builder.data.IBDomain;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.data.User;
import com.idega.user.data.UserGroupPlugIn;
import com.idega.user.data.UserGroupPlugInHome;
import com.idega.user.data.UserGroupRepresentative;
import com.idega.user.data.UserGroupRepresentativeHome;
import com.idega.user.data.UserHome;
import com.idega.util.ListUtil;

 /**
  * <p>Title: idegaWeb User</p>
  * <p>Description: </p>
  * <p>Copyright: Copyright (c) 2002</p>
  * <p>Company: idega Software</p>
  * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
  * @version 1.2
  */


public class GroupBusinessBean extends com.idega.business.IBOServiceBean implements GroupBusiness{

  private UserHome userHome;
  private GroupHome groupHome;
  private UserGroupRepresentativeHome userRepHome;
  private GroupHome permGroupHome;


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
      //filter
      String[] groupsNotToReturn = new String[1];
      groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
      //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
      //filter end
      return getGroups(groupsNotToReturn,false);
      //return EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());
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
  public  Collection getGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws Exception {
    Collection result = getGroupHome().findAllGroups(groupTypes,returnSepcifiedGroupTypes);
//    com.idega.user.data.GroupBMPBean.getAllGroups(groupTypes,returnSepcifiedGroupTypes);
    if(result != null){
      result.removeAll(getAccessController().getStandardGroups());
    }
    return result;
  }

  public  void deleteGroup(int groupId) throws RemoveException,FinderException,RemoteException {
    Group delGroup = this.getGroupByGroupID(groupId);
    //Group delGroup = ((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(groupId);
    deleteGroup(delGroup);
  }

  public  void deleteGroup(Group group)throws RemoveException,RemoteException {
    group.removeGroup();
    group.remove();
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
 * Returns all the groups that are not a direct parent of the Group with id uGroupId. That is both groups that are indirect parents of the group or not at all parents of the group. * @see com.idega.user.business.GroupBusiness#getAllGroupsNotDirectlyRelated(int)
 * @return Collection of non direct parent groups */
  public  Collection getNonParentGroups(int uGroupId){
//  public  Collection getAllGroupsNotDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getParentGroups(group);
      Collection AllGroups =  getAllGroups();// Filters out userrepresentative groups //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

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
 * Returns all the groups that are not a direct parent of the group with id uGroupId which are "Registered" i.e. non system groups such as not of the type user-representative and permission  * @param uGroupId the ID of the group * @return Collection */
public  Collection getNonParentGroupsNonPermissionNonGeneral(int uGroupId){
	//public  Collection getRegisteredGroupsNotDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getParentGroups(group);
      Collection AllGroups =  getAllNonPermissionOrGeneralGroups();// Filters out userrepresentative/permission groups //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

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
		try{
		  //public  Collection getGroupsContaining(Group group) throws EJBException,RemoteException {
		    //filter
		    String[] groupsNotToReturn = new String[1];
		    //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
		    groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
		    //filter end
		    return getParentGroupsRecursive(aGroup,groupsNotToReturn,false);
		}
		catch(IOException ex){
      		throw new IBORuntimeException(ex);
		}
  }


/**
 * Returns recursively up the group tree parents of group aGroup with filtered out with specified groupTypes
 * @param aGroup a Group to find parents for
 * @param groupTypes the Groups a String array of group types of which the Groups that are returned must be.
 * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured
 */
  public  Collection getParentGroupsRecursive(Group aGroup, String[] groupTypes) throws EJBException{
  		return getParentGroupsRecursive(aGroup,groupTypes,true);
  }


/**
 * Returns recursively up the group tree parents of group aGroup with filtered out with specified groupTypes * @param aGroup a Group to find parents for * @param groupTypes the Groups a String array of group types to be filtered with * @param returnSpecifiedGroupTypes if true it returns the Collection with all the groups that are of the types specified in  groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[]) * @return Collection of Groups found recursively up the tree * @throws EJBException If an error occured */
  public  Collection getParentGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException{
  //public  Collection getGroupsContaining(Group groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws EJBException,RemoteException{
	/**
   * @todo change implementation: create method getGroupsContaining(List groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) and use in this method
   */
	try{
    Collection groups = aGroup.getParentGroups();

    if (groups != null && groups.size() > 0){
      Map GroupsContained = new Hashtable();

      String key = "";
      Iterator iter = groups.iterator();
      while (iter.hasNext()) {
        Group item = (Group)iter.next();
        key = item.getPrimaryKey().toString();
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
          putGroupsContaining( item, GroupsContained );
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
    }else{
      return null;
    }
	}
	catch(IOException io){
		throw new IBORuntimeException(io,this);
	}
  }

  private  void putGroupsContaining(Group group, Map GroupsContained ) {
  	try{
	    Collection pGroups = group.getParentGroups();
	    if (pGroups != null){
	      String key = "";
	      Iterator iter = pGroups.iterator();
	      while (iter.hasNext()) {
	        Group item = (Group)iter.next();
	        key = item.getPrimaryKey().toString();
	        if(!GroupsContained.containsKey(key)){
	          GroupsContained.put(key,item);
	          putGroupsContaining(item, GroupsContained);
	        }
	      }
	    }
  	}
  	catch(RemoteException e){
  		throw new IBORuntimeException(e,this);
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
 * Returns recursively down the group tree children of group whith id groupId with filtered out with specified groupTypes
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
  //public  Collection getGroupsContained(Group group) throws EJBException,RemoteException{
  	try{
	    //filter
	    String[] groupsNotToReturn = new String[1];
	    groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
	    //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
	    //filter end
	    return getChildGroupsRecursive(aGroup,groupsNotToReturn,false);
  	}
  	catch(IOException e){
  		throw new IBORuntimeException(e,this);
  	}
  }



/**
 * Returns recursively down the group tree children of group aGroup with filtered with specified groupTypes
 * @param aGroup a Group to find children for
 * @param groupTypes the Groups a String array of group types of which the returned Groups must by.
 * @return Collection of Groups found recursively down the tree
 * @throws EJBException If an error occured
 */
  public Collection getChildGroupsRecursive(Group aGroup, String[] groupTypes) throws EJBException{
  	return getChildGroupsRecursive(aGroup,groupTypes,true);
  }

/**
 * Returns recursively down the group tree children of group aGroup with filtered out with specified groupTypes
 * @param aGroup a Group to find children for
 * @param groupTypes the Groups a String array of group types to be filtered with
 * @param returnSpecifiedGroupTypes if true it returns the Collection with all the groups that are of the types specified in  groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[])
 * @return Collection of Groups found recursively down the tree
 * @throws EJBException If an error occured
 */
  public Collection getChildGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException{
  //public Collection getGroupsContained(Group groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws RemoteException{
  try{
	  /**
	   * @todo change implementation: create method getGroupsContained(List groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) and use in this method
	   */
	    Collection groups = aGroup.getChildGroups();
	
	    if (groups != null && groups.size() > 0){
	      Map GroupsContained = new Hashtable();
	
	      String key = "";
	      Iterator iter = groups.iterator();
	      while (iter.hasNext()) {
	        Group item = (Group)iter.next();
	        if(item!=null){
		        key = item.getPrimaryKey().toString();
		        if(!GroupsContained.containsKey(key)){
		          GroupsContained.put(key,item);
		          putGroupsContained( item, GroupsContained );
		        }
	        }
	      }
	
	      List specifiedGroups = new Vector();
	      List notSpecifiedGroups = new Vector();
	      int j = 0;
	      int k = 0;
	      Iterator iter2 = GroupsContained.values().iterator();
	      if(groupTypes != null && groupTypes.length > 0){
	        boolean specified = false;
	        while (iter2.hasNext()) {
	          Group tempObj = (Group)iter2.next();
	          try {
	
	            String tempObjGroupType = tempObj.getGroupType();
	
	            for (int i = 0; i < groupTypes.length; i++) {
	              if (groupTypes[i].equals(tempObjGroupType)){
	                specifiedGroups.add(j++, tempObj);
	                specified = true;
	              }
	            }
	            if(!specified) {
	              notSpecifiedGroups.add(k++, tempObj);
	            } else {
	              specified = false;
	            }
	          }
	          catch (Exception ex) {
	            ex.printStackTrace();
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
	try{
	    //filter
	    String[] groupTypeToReturn = new String[1];
	    groupTypeToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
	
	    Collection list = group.getChildGroups(groupTypeToReturn,true);
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
  public Collection getUsersContainedRecursive(Group group) throws FinderException{
	try{
	    //filter
	    String[] groupTypeToReturn = new String[1];
	    groupTypeToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
	
	    Collection list = getChildGroupsRecursive(group,groupTypeToReturn,true);
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
  public Collection getUsersContainedRecursive(int groupId) throws FinderException{
  	try{
	    Group group = this.getGroupByGroupID(groupId);
	    return getUsersContainedRecursive(group);
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
 * Returns all the groups that are direct children groups of group aGroup. * @param aGroup a group to find children groups for * @return Collection of Groups that are Direct children of group aGroup */
  public  Collection getChildGroups(Group aGroup){
  //public  Collection getGroupsContainedDirectlyRelated(Group group){
   /**
   * @todo filter out UserGroupRepresentative groups
   */
    try {
      //filter
      String[] groupsNotToReturn = new String[1];
      groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
      //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
      //filter end
      Collection list = aGroup.getChildGroups(groupsNotToReturn,false);
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

  /**
   * @todo filter out UserGroupRepresentative groups ? time
   */
  public  Collection getUsersDirectlyRelated(Group group) throws EJBException,RemoteException,FinderException{
    Collection result = group.getChildGroups();
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
/*
      if(AllGroups != null){
        AllGroups.removeAll(isDirectlyRelated);
        AllGroups.remove(group);
      }
      return AllGroups;
*/

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


  private  void putGroupsContained(Group group,Map GroupsContained ) throws EJBException,RemoteException{
    Collection pGroups = group.getChildGroups();
    if (pGroups != null){
      String key = "";
      Iterator iter = pGroups.iterator();
      while (iter.hasNext()) {
        Group item = (Group)iter.next();
        key = item.getPrimaryKey().toString();
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
          putGroupsContaining(item, GroupsContained);
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


  public  void updateUsersInGroup( int groupId, String[] usrGroupIdsInGroup) throws RemoteException,FinderException {

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
        group.removeGroup(Integer.parseInt(item), false);
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

  public User getUserByID(int id)throws FinderException,RemoteException{
    return this.getUserHome().findByPrimaryKey(new Integer(id));
  }

  public void addUser(int groupId, User user)throws EJBException,RemoteException{
    try{
      //((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(groupId).addGroup(user.getGroupID());
      this.getGroupByGroupID(groupId).addGroup(user.getGroup());
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


/**
   * Creates a group with the general grouptype and adds it under the default Domain (IBDomain)
 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
 */
  public Group createGroup(String name)throws CreateException,RemoteException{
  	String description = "";
  	return createGroup(name,description);
  }

/**
   * Creates a group with the general grouptype and adds it under the default Domain (IBDomain)
 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
 */
  public Group createGroup(String name,String description)throws CreateException,RemoteException{
  	String generaltype = getGroupHome().getGroupType();
  	return createGroup(name,description,generaltype);
  }


/**
   * Creates a group and adds it under the default Domain (IBDomain) * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String) */
  public Group createGroup(String name,String description,String type)throws CreateException,RemoteException{
  	return createGroup(name,description,type,-1);
  }
  
  /**
   * Creates a group and adds it under the default Domain (IBDomain)   * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String, int)   */
  public Group createGroup(String name,String description,String type,int homePageID)throws CreateException,RemoteException{
    Group newGroup;
    newGroup = getGroupHome().create();
    newGroup.setName(name);
    newGroup.setDescription(description);
    newGroup.setGroupType(type);
    if ( homePageID != -1 ) {
	    newGroup.setHomePageID(homePageID);
    }
    newGroup.store();

    addGroupUnderDomain(this.getIWApplicationContext().getDomain(),newGroup,(GroupDomainRelationType)null);
    return newGroup;
  }
  
  public String getGroupType(Class groupClass)throws RemoteException{
    return ((GroupHome)IDOLookup.getHome(groupClass)).getGroupType();
  }
  
  public GroupType getGroupTypeFromString(String type) throws RemoteException, FinderException{
  	return getGroupTypeHome().findByPrimaryKey(type);
  }
  
/**
 * Method getUserGroupPluginsForGroupType.
 * @param groupType
 * @return Collection of plugins or null if no found or error occured
 */
  public Collection getUserGroupPluginsForGroupTypeString(String groupType){
  	try {
		return getUserGroupPlugInHome().findRegisteredPlugInsForGroupType(groupType);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
  }

/**
 * Method getUserGroupPluginsForGroupType.
 * @param groupType
 * @return Collection of plugins or null if no found or error occured
 */
  public Collection getUserGroupPluginsForGroupType(GroupType groupType){
  	try {
		return getUserGroupPlugInHome().findRegisteredPlugInsForGroupType(groupType);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
  }
  
  
  /**
 * Method getUserGroupPluginsForUser.
 * @param groupType
 * @return Collection of plugins or null if no found or error occured
 */
  public Collection getUserGroupPluginsForUser(User user){
  	try {
  		//finna allar gruppur tengdar thessum user og gera find fall sem tekur inn i sig collection a groups 
		return getUserGroupPlugInHome().findAllPlugIns();
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
  }
  
  public GroupTypeHome getGroupTypeHome() throws RemoteException{
  	return  (GroupTypeHome) this.getIDOHome(GroupType.class);
  }
  	
  public UserGroupPlugInHome getUserGroupPlugInHome() throws RemoteException{
  	return  (UserGroupPlugInHome) this.getIDOHome(UserGroupPlugIn.class);
  }


  public void addGroupUnderDomain(IBDomain domain, Group group, GroupDomainRelationType type) throws CreateException,RemoteException{
    GroupDomainRelation relation = (GroupDomainRelation)IDOLookup.create(GroupDomainRelation.class);
    relation.setDomain(domain);
    relation.setRelatedGroup(group);

    if(type != null){
      relation.setRelationship(type);
    }

    relation.store();
  }





} // Class

/**
  * @todo move implementation from methodName(Group group) to methodName(int groupId)
  * @todo reimplement all methods returning list of users
  */
