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
      return group.getListOfAllGroupsContainingThis();
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
   * @todo change implementation. Use List not Array
   */
  public static List getAllGroupsRelated(GenericGroup group) throws SQLException{
      GenericGroup[] groups = group.getAllGroupsContainingThis();

      if (groups != null && groups.length > 0){
        Hashtable GroupsContaining = new Hashtable();

        String key = "";
        for (int i = 0; i < groups.length; i++) {
          key = Integer.toString(groups[i].getID());
          if(!GroupsContaining.containsKey(key)){
            GroupsContaining.put(key,groups[i]);
            putGroupsContaining( (GenericGroup)groups[i], GroupsContaining );
          }
        }


        Vector  groupVector = new Vector();
        Enumeration e;
        int i = 0;
        for ( e = (Enumeration)GroupsContaining.elements(); e.hasMoreElements();){
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
   * @todo change implementation. Use List not Array
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




  public static List getAllGroupMembers(GenericGroup group) throws SQLException{
    GenericGroup[] groups = group.getAllGroupsContained();

    if (groups != null && groups.length > 0){
      Hashtable GroupsContained = new Hashtable();

      String key = "";
      for (int i = 0; i < groups.length; i++) {
        key = Integer.toString(groups[i].getID());
        if(!GroupsContained.containsKey(key)){
          GroupsContained.put(key,groups[i]);
          putGroupsContained( (GenericGroup)groups[i], GroupsContained );
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

  public static List getAllUserMembers(GenericGroup group) throws SQLException{
    Vector vector = new Vector();

    List list = getAllGroupMembers(group);

    list.add(group);

    if(list != null){
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        List userList = getAllUserMembersDirectlyRelated((GenericGroup)item);
        if(userList != null){

          Iterator userIter = userList.iterator();
          while (userIter.hasNext()) {
            User userItem = (User)userIter.next();
            if(!vector.contains(userItem)){
              vector.add(userItem);
            }
          }
        }
      }
    }

    return vector;
  }

  public static List getAllGroupMembersDirectlyRelated(GenericGroup group){
    try {
      List list = group.getListOfAllGroupsContained();
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

  public static List getAllUserMembersDirectlyRelated(GenericGroup group) throws SQLException{
    return EntityFinder.findRelated(group,(User)User.getStaticInstance(User.class));
  }

  public static List getAllGroupMembersNotDirectlyRelated(GenericGroup group) throws SQLException{
    try {
      List isDirectlyRelated = getAllGroupMembersDirectlyRelated(group);
      List AllGroups = getAllGroupMembers(group);

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

  public static List getAllUserMembersNotDirectlyRelated(GenericGroup group) throws SQLException{
    Vector vector = new Vector();

    List list = getAllGroupMembers(group);

    if(list != null){
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        List userList =  getAllUserMembersDirectlyRelated((GenericGroup)item);
        if(userList != null){

          Iterator userIter = userList.iterator();
          while (userIter.hasNext()) {
            User userItem = (User)userIter.next();
            if(!vector.contains(userItem)){
              vector.add(userItem);
            }
          }
        }
      }
    }

    return vector;
  }


  /**
   * @todo change implementation. Use List not Array
   */
  private static void putGroupsContained(GenericGroup group, Hashtable GroupsContained ) throws SQLException{
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


  public static List getGroups(String[] groupIDs) throws SQLException {
    List toReturn = new Vector(0);

    String sGroupList = "";
    if (groupIDs != null && groupIDs.length > 0){
      for(int g = 0; g < groupIDs.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupIDs[g];
      }
    }
    if(!sGroupList.equals("")){
      GenericGroup group = GenericGroup.getStaticInstance();
      toReturn = EntityFinder.findAll(group,"SELECT * FROM " + group.getEntityName() + " WHERE " + group.getIDColumnName() + " in (" + sGroupList + ")");
    }
    System.err.println("groupIDs : "+groupIDs);
    System.err.println("sGroupList : "+sGroupList);
    return toReturn;
  }




} // Class