package com.idega.user.business;

import com.idega.user.data.*;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.util.ListUtil;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public class UserGroupBusinessBean extends com.idega.business.IBOServiceBean implements UserGroupBusiness{

  private UserHome userHome;
  private GroupHome groupHome;
  private UserGroupRepresentativeHome userRepHome;
  private GroupHome permGroupHome;


  public UserGroupBusinessBean() {
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

  public Collection getAllGroups(IWContext iwc) {
    try {
      //filter
      String[] groupsNotToReturn = new String[1];
      groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
      //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
      //filter end
      return getGroups(groupsNotToReturn,false,iwc);
      //return EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getRegisteredGroups(IWContext iwc){
    try {
      //filter
      String[] groupsNotToReturn = new String[2];
      groupsNotToReturn[0] = this.getGroupHome().getGroupType();
      //groupsNotToReturn[0] = ((Group)com.idega.user.data.GroupBMPBean.getInstance(Group.class)).getGroupTypeValue();
      groupsNotToReturn[1] = this.getPermissionGroupHome().getGroupType();
      //groupsNotToReturn[0] = ((PermissionGroup)com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getInstance(PermissionGroup.class)).getGroupTypeValue();
      //filter end
      return getGroups(groupsNotToReturn,true,iwc);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes, IWContext iwc) throws Exception {
    Collection result = getGroupHome().findAllGroups(groupTypes,returnSepcifiedGroupTypes);

//    com.idega.user.data.GroupBMPBean.getAllGroups(groupTypes,returnSepcifiedGroupTypes);
    if(result != null){
      result.removeAll(iwc.getAccessController().getStandardGroups());
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


  public  Collection getGroupsContaining(int uGroupId)throws EJBException{
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      return getGroupsContaining(group);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getGroupsContainingDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      return getGroupsContainingDirectlyRelated(group);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getGroupsContainingDirectlyRelated(Group group){
    try {
      return group.getListOfAllGroupsContainingThis();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getAllGroupsNotDirectlyRelated(int uGroupId,IWContext iwc){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getGroupsContainingDirectlyRelated(group);
      Collection AllGroups =  getAllGroups(iwc);// Filters out userrepresentative groups //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

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

  public  Collection getRegisteredGroupsNotDirectlyRelated(int uGroupId,IWContext iwc){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getGroupsContainingDirectlyRelated(group);
      Collection AllGroups =  getRegisteredGroups(iwc);// Filters out userrepresentative groups //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

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

  public  Collection getGroupsContainingNotDirectlyRelated(int uGroupId){
    try {
      Group group = this.getGroupByGroupID(uGroupId);
      Collection isDirectlyRelated = getGroupsContainingDirectlyRelated(group);
      Collection AllGroups =  getGroupsContaining(uGroupId);   //  EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

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

  public  Collection getGroupsContaining(Group group) throws EJBException,RemoteException {
    //filter
    String[] groupsNotToReturn = new String[1];
    //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
    //filter end
    return getGroupsContaining(group,groupsNotToReturn,false);
  }

  /**
   * @todo change implementation: create method getGroupsContaining(List groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) and use in this method
   */
  public  Collection getGroupsContaining(Group groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws EJBException,RemoteException{
    Collection groups = groupContained.getListOfAllGroupsContainingThis();

    if (groups != null && groups.size() > 0){
      Hashtable GroupsContained = new Hashtable();

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

      List specifiedGroups = new Vector();
      List notSpecifiedGroups = new Vector();
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
        notSpecifiedGroups.remove(groupContained);
        specifiedGroups.remove(groupContained);
      } else {
        while (iter2.hasNext()) {
          Group tempObj = (Group)iter2.next();
          notSpecifiedGroups.add(j++, tempObj);
        }
        notSpecifiedGroups.remove(groupContained);
        returnSepcifiedGroupTypes = false;
      }

      return (returnSepcifiedGroupTypes) ? specifiedGroups : notSpecifiedGroups;
    }else{
      return null;
    }
  }

  private  void putGroupsContaining(Group group, Hashtable GroupsContained ) throws RemoteException{
    Collection pGroups = group.getListOfAllGroupsContainingThis();
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

  public  Collection getGroupsContained(int groupId) throws EJBException,FinderException,RemoteException{
    Group group = this.getGroupByGroupID(groupId);
    return getGroupsContained(group);
  }

  public  Collection getUsersContained(int groupId) throws EJBException,FinderException,RemoteException,RemoteException{
    Group group = this.getGroupByGroupID(groupId);
    return getUsersContained(group);
  }

  public  Collection getGroupsContainedDirectlyRelated(int groupId) throws EJBException,FinderException,RemoteException{
    Group group = this.getGroupByGroupID(groupId);
    return getGroupsContainedDirectlyRelated(group);
  }

  public  Collection getUsersContainedDirectlyRelated(int groupId) throws EJBException,FinderException,RemoteException{
    Group group = this.getGroupByGroupID(groupId);
    return getUsersContainedDirectlyRelated(group);
  }

  public  Collection getGroupsContainedNotDirectlyRelated(int groupId) throws EJBException,FinderException,RemoteException{
    Group group = this.getGroupByGroupID(groupId);
    return getGroupsContainedNotDirectlyRelated(group);
  }

  public  Collection getUsersContainedNotDirectlyRelated(int groupId) throws EJBException,FinderException,RemoteException{
    Group group = this.getGroupByGroupID(groupId);
    return getUsersContainedNotDirectlyRelated(group);
  }


  public  Collection getGroupsContained(Group group) throws EJBException,RemoteException{
    //filter
    String[] groupsNotToReturn = new String[1];
    groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
    //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    //filter end

    return getGroupsContained(group,groupsNotToReturn,false);
  }


  /**
   * @todo change implementation: create method getGroupsContained(List groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) and use in this method
   */
  public Collection getGroupsContained(Group groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws RemoteException{
    Collection groups = groupContaining.getListOfAllGroupsContained();

    if (groups != null && groups.size() > 0){
      Hashtable GroupsContained = new Hashtable();

      String key = "";
      Iterator iter = groups.iterator();
      while (iter.hasNext()) {
        Group item = (Group)iter.next();
        key = item.getPrimaryKey().toString();
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
          putGroupsContained( item, GroupsContained );
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
          for (int i = 0; i < groupTypes.length; i++) {
            if (tempObj.getGroupType().equals(groupTypes[i])){
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
        notSpecifiedGroups.remove(groupContaining);
        specifiedGroups.remove(groupContaining);
      } else {
        while (iter2.hasNext()) {
          Group tempObj = (Group)iter2.next();
          notSpecifiedGroups.add(j++, tempObj);
        }
        notSpecifiedGroups.remove(groupContaining);
        returnSepcifiedGroupTypes = false;
      }

      return (returnSepcifiedGroupTypes) ? specifiedGroups : notSpecifiedGroups;
    }else{
      return null;
    }
  }


  public Collection getUsersContained(Group group) throws FinderException,RemoteException{
    //filter
    String[] groupsNotToReturn = new String[1];
    groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
    //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    //filter end

    Collection list = getGroupsContained(group,groupsNotToReturn,true);
    if(list != null && !list.isEmpty()){
      return getUsersForUserRepresentativeGroups(list);
    } else {
      return ListUtil.getEmptyList();
    }
  }

  /**
   * @todo filter out UserGroupRepresentative groups
   */

  public  Collection getGroupsContainedDirectlyRelated(Group group){
    try {
      //filter
      String[] groupsNotToReturn = new String[1];
      groupsNotToReturn[0] = this.getUserGroupRepresentativeHome().getGroupType();
      //groupsNotToReturn[0] = ((UserGroupRepresentative)com.idega.user.data.UserGroupRepresentativeBMPBean.getInstance(UserGroupRepresentative.class)).getGroupTypeValue();
      //filter end

      Collection list = group.getGroupsContained(groupsNotToReturn,false);
      if(list != null){
        list.remove(group);
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
  public  Collection getUsersContainedDirectlyRelated(Group group) throws EJBException,RemoteException,FinderException{
    Collection result = group.getListOfAllGroupsContained();
    return getUsersForUserRepresentativeGroups(result);
  }

  public  Collection getGroupsContainedNotDirectlyRelated(Group group) throws EJBException{
    try {
      Collection isDirectlyRelated = getGroupsContainedDirectlyRelated(group);
      Collection AllGroups = getGroupsContained(group);
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

  public  Collection getUsersContainedNotDirectlyRelated(Group group) throws EJBException,RemoteException,FinderException{

    Collection DirectUsers = getUsersContainedDirectlyRelated(group);
    Collection notDirectUsers = getUsersContained(group);

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


  private  void putGroupsContained(Group group, Hashtable GroupsContained ) throws EJBException,RemoteException{
    Collection pGroups = group.getListOfAllGroupsContained();
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
      Collection lDirect = getUsersContainedDirectlyRelated(groupId);
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

} // Class

/**
  * @todo move implementation from methodName(Group group) to methodName(int groupId)
  * @todo reimplement all methods returning list of users
  */
