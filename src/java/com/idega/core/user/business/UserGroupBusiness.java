package com.idega.core.user.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.data.EntityFinder;
import com.idega.data.GenericEntity;
import com.idega.presentation.IWContext;
import com.idega.user.data.UserBMPBean;

/**
 * Title:        
 * Description: The main business class for Group management in the idegaWeb User system.
 * Copyright:    Idega Software Copyright (c) 2001
 * Company:      Idega Software
 * @author idega team <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 * @deprecated use com.idega.user.business.GroupBusiness
 */


public class UserGroupBusiness {

  public UserGroupBusiness() {
  }


  public static List getAllGroups(IWContext iwc) {
    try {
      //filter
      String[] groupsNotToReturn = new String[1];
      groupsNotToReturn[0] = ((UserGroupRepresentative)GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
      //filter end
      return UserGroupBusiness.getGroups(groupsNotToReturn,false,iwc);
      //return EntityFinder.findAll(com.idega.core.data.GenericGroupBMPBean.getStaticInstance());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getRegisteredGroups(IWContext iwc){
    try {
      //filter
      String[] groupsNotToReturn = new String[2];
      groupsNotToReturn[0] = ((GenericGroup)GenericEntity.getStaticInstance(GenericGroup.class)).getGroupTypeValue();
      groupsNotToReturn[0] = ((PermissionGroup)GenericEntity.getStaticInstance(PermissionGroup.class)).getGroupTypeValue();
      //filter end
      return UserGroupBusiness.getGroups(groupsNotToReturn,true,iwc);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes, IWContext iwc) throws Exception {
    List result = com.idega.core.data.GenericGroupBMPBean.getAllGroups(groupTypes,returnSepcifiedGroupTypes);
    if(result != null){
      result.removeAll(iwc.getAccessController().getStandardGroups());
    }
    return result;
  }

  public static void deleteGroup(int groupId) throws SQLException {
    GenericGroup delGroup = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId);
    deleteGroup(delGroup);
  }

  public static void deleteGroup(GenericGroup group)throws SQLException {
    group.removeGroup();
    group.delete();
  }


  public static List getGroupsContaining(int uGroupId)throws SQLException{
    try {
      return getGroupsContaining(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(uGroupId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getGroupsContainingDirectlyRelated(int uGroupId){
    try {
      return getGroupsContainingDirectlyRelated(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(uGroupId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getGroupsContainingDirectlyRelated(GenericGroup group){
    try {
      return group.getParentGroups();
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getAllGroupsNotDirectlyRelated(int uGroupId,IWContext iwc){
    try {
      GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(uGroupId);
      List isDirectlyRelated = getGroupsContainingDirectlyRelated(group);
      List AllGroups =  UserGroupBusiness.getAllGroups(iwc);// Filters out userrepresentative groups //  EntityFinder.findAll(com.idega.core.data.GenericGroupBMPBean.getStaticInstance());

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
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getRegisteredGroupsNotDirectlyRelated(int uGroupId,IWContext iwc){
    try {
      GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(uGroupId);
      List isDirectlyRelated = getGroupsContainingDirectlyRelated(group);
      List AllGroups =  UserGroupBusiness.getRegisteredGroups(iwc);// Filters out userrepresentative groups //  EntityFinder.findAll(com.idega.core.data.GenericGroupBMPBean.getStaticInstance());

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
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getGroupsContainingNotDirectlyRelated(int uGroupId){
    try {
      GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(uGroupId);
      List isDirectlyRelated = getGroupsContainingDirectlyRelated(group);
      List AllGroups =  UserGroupBusiness.getGroupsContaining(uGroupId);   //  EntityFinder.findAll(com.idega.core.data.GenericGroupBMPBean.getStaticInstance());



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
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getGroupsContaining(GenericGroup group) throws SQLException {
    //filter
    String[] groupsNotToReturn = new String[1];
    groupsNotToReturn[0] = ((UserGroupRepresentative)GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    //filter end
    return UserGroupBusiness.getGroupsContaining(group,groupsNotToReturn,false);
  }

  /**
   * @todo change implementation: create method getGroupsContaining(List groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) and use in this method
   */
  public static List getGroupsContaining(GenericGroup groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException{
    List groups = groupContained.getParentGroups();

    if (groups != null && groups.size() > 0){
      Hashtable GroupsContained = new Hashtable();

      String key = "";
      Iterator iter = groups.iterator();
      while (iter.hasNext()) {
        GenericGroup item = (GenericGroup)iter.next();
        key = Integer.toString(item.getID());
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
          GenericGroup tempObj = (GenericGroup)iter2.next();
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
          GenericGroup tempObj = (GenericGroup)iter2.next();
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

  private static void putGroupsContaining(GenericGroup group, Hashtable GroupsContained ) throws SQLException{
    List pGroups = group.getParentGroups();
    if (pGroups != null){
      String key = "";
      Iterator iter = pGroups.iterator();
      while (iter.hasNext()) {
        GenericGroup item = (GenericGroup)iter.next();
        key = Integer.toString(item.getID());
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
          putGroupsContaining(item, GroupsContained);
        }
      }
    }
  }












  public static List getGroupsContained(int groupId) throws SQLException{
    return UserGroupBusiness.getGroupsContained(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
  }

  public static List getUsersContained(int groupId) throws SQLException{
    return UserGroupBusiness.getUsersContained(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
  }

  public static List getGroupsContainedDirectlyRelated(int groupId) throws SQLException{
    return UserGroupBusiness.getGroupsContainedDirectlyRelated(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
  }

  public static List getUsersContainedDirectlyRelated(int groupId) throws SQLException{
    return UserGroupBusiness.getUsersContainedDirectlyRelated(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
  }

  public static List getGroupsContainedNotDirectlyRelated(int groupId) throws SQLException{
    return UserGroupBusiness.getGroupsContainedNotDirectlyRelated(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
  }

  public static List getUsersContainedNotDirectlyRelated(int groupId) throws SQLException{
    return UserGroupBusiness.getUsersContainedNotDirectlyRelated(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
  }


  public static List getGroupsContained(GenericGroup group) throws SQLException{
    //filter
    String[] groupsNotToReturn = new String[1];
    groupsNotToReturn[0] = ((UserGroupRepresentative)GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    //filter end

    return UserGroupBusiness.getGroupsContained(group,groupsNotToReturn,false);
  }


  /**
   * @todo change implementation: create method getGroupsContained(List groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) and use in this method
   */
  public static List getGroupsContained(GenericGroup groupContaining, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException{
    List groups = groupContaining.getChildGroups();

    if (groups != null && groups.size() > 0){
      Hashtable GroupsContained = new Hashtable();

      String key = "";
      Iterator iter = groups.iterator();
      while (iter.hasNext()) {
        GenericGroup item = (GenericGroup)iter.next();
        key = Integer.toString(item.getID());
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
          putGroupsContained( item, GroupsContained );
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
          GenericGroup tempObj = (GenericGroup)iter2.next();
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
          GenericGroup tempObj = (GenericGroup)iter2.next();
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


  public static List getUsersContained(GenericGroup group) throws SQLException{
    //filter
    String[] groupsNotToReturn = new String[1];
    groupsNotToReturn[0] = ((UserGroupRepresentative)GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    //filter end

    List list = UserGroupBusiness.getGroupsContained(group,groupsNotToReturn,true);
    return getUsersForUserRepresentativeGroups(list);
  }

  /**
   * @todo filter out UserGroupRepresentative groups
   */

  public static List getGroupsContainedDirectlyRelated(GenericGroup group){
    try {
      //filter
      String[] groupsNotToReturn = new String[1];
      groupsNotToReturn[0] = ((UserGroupRepresentative)GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
      //filter end

      List list = group.getChildGroups(groupsNotToReturn,false);
      if(list != null){
        list.remove(group);
      }
      return list;
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * @todo filter out UserGroupRepresentative groups ? time
   */
  public static List getUsersContainedDirectlyRelated(GenericGroup group) throws SQLException{
    List result = group.getChildGroups();
    return UserGroupBusiness.getUsersForUserRepresentativeGroups(result);
  }

  public static List getGroupsContainedNotDirectlyRelated(GenericGroup group) throws SQLException{
    try {
      List isDirectlyRelated = getGroupsContainedDirectlyRelated(group);
      List AllGroups = getGroupsContained(group);
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
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersContainedNotDirectlyRelated(GenericGroup group) throws SQLException{

    List DirectUsers = UserGroupBusiness.getUsersContainedDirectlyRelated(group);
    List notDirectUsers = UserGroupBusiness.getUsersContained(group);

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


  private static void putGroupsContained(GenericGroup group, Hashtable GroupsContained ) throws SQLException{
    List pGroups = group.getChildGroups();
    if (pGroups != null){
      String key = "";
      Iterator iter = pGroups.iterator();
      while (iter.hasNext()) {
        GenericGroup item = (GenericGroup)iter.next();
        key = Integer.toString(item.getID());
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,item);
          putGroupsContaining(item, GroupsContained);
        }
      }
    }
  }


  public static List getGroups(String[] groupIDs) throws SQLException {
    List toReturn = new ArrayList(0);

    String sGroupList = "";
    if (groupIDs != null && groupIDs.length > 0){
      for(int g = 0; g < groupIDs.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupIDs[g];
      }
    }
    if(!sGroupList.equals("")){
      GenericGroup group = com.idega.core.data.GenericGroupBMPBean.getStaticInstance();
      toReturn = EntityFinder.findAll(group,"SELECT * FROM " + group.getEntityName() + " WHERE " + group.getIDColumnName() + " in (" + sGroupList + ")");
    }
    return toReturn;
  }


  public static List getUsersForUserRepresentativeGroups(List groups)throws SQLException {
    if(groups != null && groups.size() > 0){
      String sGroupList = "";
      Iterator iter = groups.iterator();
      for (int g=0; iter.hasNext(); g++) {
        GenericGroup item = (GenericGroup)iter.next();
        if(g>0){ sGroupList += ", "; }
        sGroupList += item.getID();
      }
      if(!sGroupList.equals("")){
        User user = (User) GenericEntity.getStaticInstance(User.class);
        return EntityFinder.findAll(user,"Select * from "+user.getEntityName()+" where "+UserBMPBean._COLUMNNAME_USER_GROUP_ID+" in ("+sGroupList+")");
      }
    }
    return null;
  }


  public static void updateUsersInGroup( int groupId, String[] usrGroupIdsInGroup) throws SQLException {

    if(groupId != -1){
      GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId);
      //System.out.println("before");
      List lDirect = com.idega.core.user.business.UserGroupBusiness.getUsersContainedDirectlyRelated(groupId);
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



} // Class

/**
  * @todo move implementation from methodName(GenericGroup group) to methodName(int groupId)
  * @todo reimplement all methods returning list of users
  */
