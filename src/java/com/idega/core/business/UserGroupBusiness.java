package com.idega.core.business;

import java.sql.SQLException;
import com.idega.core.data.*;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.core.data.GenericGroup;
import com.idega.data.EntityFinder;
import java.util.List;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import com.idega.data.GenericEntity;
import com.idega.core.user.data.User;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserGroupBusiness {

  public UserGroupBusiness() {
  }


  public static void deleteGroup(int groupId) throws SQLException {
    GenericGroup delGroup = new GenericGroup(groupId);

    delGroup.removeFrom((User)User.getStaticInstance(User.class));
    delGroup.removeGroup(GenericGroup.getStaticInstance());

    delGroup.delete();
  }


  public static List getAllGroupsRelated(int uGroupId)throws SQLException{
    try {
      return getAllGroupsRelated(new GenericGroup(uGroupId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUserGroupsDirectlyRelated(int uGroupId){
    try {
      return getUserGroupsDirectlyRelated(new GenericGroup(uGroupId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUserGroupsDirectlyRelated(GenericGroup group){
    try {
      List list = group.getListOfAllGroupsContainingThis();
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

  public static List getUserGroupsNotDirectlyRelated(int uGroupId){
    try {
      GenericGroup group = new GenericGroup(uGroupId);
      List isDirectlyRelated = getUserGroupsDirectlyRelated(group);
      List AllRelatedGroups = getAllGroupsRelated(group);

      if(AllRelatedGroups != null){
        if(isDirectlyRelated != null){
          Iterator iter = isDirectlyRelated.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            AllRelatedGroups.remove(item);
            //while(AllRelatedGroups.remove(item)){}
          }
        }
        AllRelatedGroups.remove(group);
        return AllRelatedGroups;
      }else {
        return null;
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getAllGroupsNotDirectlyRelated(int uGroupId){
    try {
      GenericGroup group = new GenericGroup(uGroupId);
      List isDirectlyRelated = getUserGroupsDirectlyRelated(group);
      List AllGroups = EntityFinder.findAll(GenericGroup.getStaticInstance());

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



  /**
   * @todo change implementation. Use EntityFinder not GenericEntity
   */
  public static List getAllGroupsRelated(GenericGroup group) throws SQLException{
      GenericGroup[] groups = group.getAllGroupsContainingThis();

      if (groups != null && groups.length > 0){
        Hashtable GroupsContained = new Hashtable();

        String key = "";
        for (int i = 0; i < groups.length; i++) {
          key = Integer.toString(groups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,groups[i]);
            putGroupsContaining( (GenericGroup)groups[i], GroupsContained );
          }
        }


        Vector  groupVector = new Vector();
        Enumeration e;
        int i = 0;
        for ( e = (Enumeration)GroupsContained.elements(); e.hasMoreElements();){
          GenericGroup tempObj = (GenericGroup)e.nextElement();
          if (!tempObj.getGroupType().equals(((UserGroupRepresentative)UserGroupRepresentative.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue()))
            groupVector.add(i++, tempObj);
        }

        //return (PermissionGroup[])groupVector.toArray((Object[])new PermissionGroup[0]);
        groupVector.remove(group);
        return groupVector;
      }else{
        return null;
      }
    }

  /**
   * @todo change implementation. Use EntityFinder not GenericEntity
   */
    private static void putGroupsContaining(GenericGroup group, Hashtable GroupsContained ) throws SQLException{
      GenericGroup[] pGroups = group.getAllGroupsContainingThis();
      if (pGroups != null){
        String key = "";
        for (int i = 0; i < pGroups.length; i++) {
          key = Integer.toString(pGroups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,pGroups[i]);
            putGroupsContaining((GenericGroup)pGroups[i], GroupsContained);
          }
        }
      }
    }






  public static List getAllGroupMembers(int groupId){
    return null;
  }

  public static List getAllUserMembers(int groupId){
    return null;
  }

  public static List getAllGroupMembersDirectlyRelated(int groupId){
    return null;
  }

  public static List getAllUserMembersDirectlyRelated(int groupId){
    return null;
  }

  public static List getAllGroupMembersNotDirectlyRelated(int groupId){
    return null;
  }

  public static List getAllUserMembersNotDirectlyRelated(int groupId){
    return null;
  }




  public static List getAllGroupMembers(GenericGroup group){
    return null;
  }

  public static List getAllUserMembers(GenericGroup group){
    return null;
  }

  public static List getAllGroupMembersDirectlyRelated(GenericGroup group){
    return null;
  }

  public static List getAllUserMembersDirectlyRelated(GenericGroup group){
    return null;
  }

  public static List getAllGroupMembersNotDirectlyRelated(GenericGroup group){
    return null;
  }

  public static List getAllUserMembersNotDirectlyRelated(GenericGroup group){
    return null;
  }


} // Class