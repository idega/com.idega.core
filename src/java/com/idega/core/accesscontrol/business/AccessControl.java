package com.idega.core.accesscontrol.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.accesscontrol.data.ICPermissionHome;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.accesscontrol.data.PermissionGroupHome;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.data.GenericGroup;
import com.idega.core.file.data.ICFile;
import com.idega.core.user.business.UserGroupBusiness;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.data.EntityFinder;
import com.idega.data.IDOLookup;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWServiceImpl;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.util.EncryptionType;
import com.idega.util.IWTimestamp;
import com.idega.util.reflect.FieldAccessor;




/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 Idega Software All Rights Reserved
 * Company:      Idega Software
 * @author       <a href="mailto:gummi@idega.is">Gudmundur Agust
 * Saemundsson</a>, Eirikur Hrafnsson
 * @version 1.0
 */

public class AccessControl extends IWServiceImpl implements AccessController {
/**
 * @todo change next 4 variables to applicationAddesses
 */
  private PermissionGroup AdministratorPermissionGroup = null;
  private PermissionGroup PermissionGroupEveryOne = null;
  private PermissionGroup PermissionGroupUsers = null;
  private List standardGroups = null;

  private static final String _APPADDRESS_ADMINISTRATOR_USER = "ic_super_admin";
  private static final String _ADMINISTRATOR_NAME = "Administrator";


  //private static final int _GROUP_ID_EVERYONE = -7913;
  //private static final int _GROUP_ID_USERS = -1906;

  private static final int _GROUP_ID_EVERYONE = com.idega.user.data.GroupBMPBean.GROUP_ID_EVERYONE;
  private static final int _GROUP_ID_USERS = com.idega.user.data.GroupBMPBean.GROUP_ID_USERS;


  private static final int _notBuilderPageID = 0;

  //temp
  private static ICObject staticPageICObject = null;
  private static ICObject staticFileICObject = null;


  private void initAdministratorPermissionGroup() throws Exception {
    PermissionGroup permission = getPermissionGroupHome().create();
    permission.setName(AccessControl.getAdministratorGroupName());
    permission.setDescription("Administrator permission");
    permission.store();
    AdministratorPermissionGroup = permission;
  }

  private void initPermissionGroupEveryone() throws Exception {
    PermissionGroup permission = getPermissionGroupHome().create();
    permission.setID(_GROUP_ID_EVERYONE);
    permission.setName("Everyone");
    permission.setDescription("Permission if not logged on");
    permission.store();
    PermissionGroupEveryOne = permission;
  }

  private void initPermissionGroupUsers() throws Exception {
    PermissionGroup permission = getPermissionGroupHome().create();
    permission.setID(_GROUP_ID_USERS);
    permission.setName("Users");
    permission.setDescription("Permission if logged on");
    permission.insert();
    PermissionGroupUsers = permission;
  }

  public PermissionGroup getPermissionGroupEveryOne() throws Exception {
    if(PermissionGroupEveryOne == null){
      initPermissionGroupEveryone();
    }
    return PermissionGroupEveryOne;
  }

  public PermissionGroup getPermissionGroupUsers() throws Exception {
    if(PermissionGroupUsers == null){
      initPermissionGroupUsers();
    }
    return PermissionGroupUsers;
  }

  public PermissionGroup getPermissionGroupAdministrator() throws Exception {
    if(AdministratorPermissionGroup == null){
      initAdministratorPermissionGroup();
    }
    return AdministratorPermissionGroup;
  }

  public boolean isAdmin(IWUserContext iwc)throws Exception{
  	//TODO Eiki review bullshit here, there is only one super user!
    try {
      Object ob = LoginBusinessBean.getLoginAttribute(getAdministratorGroupName(), iwc);
      if(ob != null){
        return ((Boolean)ob).booleanValue();
      }else{
        if(getAdministratorUser().equals(LoginBusinessBean.getUser(iwc))){
          LoginBusinessBean.setLoginAttribute(getAdministratorGroupName(),Boolean.TRUE,iwc);
          return true;
        }
        List groups = LoginBusinessBean.getPermissionGroups(iwc);
        if (groups != null){
          Iterator iter = groups.iterator();
          while (iter.hasNext()) {
            GenericGroup item = (GenericGroup)iter.next();
            if (getAdministratorGroupName().equals(item.getName())){
              LoginBusinessBean.setLoginAttribute(getAdministratorGroupName(),Boolean.TRUE,iwc);
              return true;
            }
          }
        }
      }
      LoginBusinessBean.setLoginAttribute(getAdministratorGroupName(),Boolean.FALSE,iwc);
      return false;
    }
    catch (NotLoggedOnException ex) {
      return false;
    }
  }

  /**
   * @todo page ownership
   */
  public boolean isOwner(Object obj , IWUserContext iwc) throws Exception { 	
    Boolean returnVal = Boolean.FALSE;
    User user = iwc.getUser();
    if(user != null){
      List[] permissionOrder = new Vector[2];
      permissionOrder[0] = new Vector();
      permissionOrder[0].add( Integer.toString(user.getGroupID()) );
      permissionOrder[1] = new Vector();
      permissionOrder[1].add( Integer.toString(user.getPrimaryGroupID()) );

      returnVal = checkForPermission(permissionOrder,obj,AccessControl.PERMISSION_KEY_OWNER,iwc);
    }

    if(returnVal != null){
      return returnVal.booleanValue();
    } else {
      return false;
    }

  }

  public boolean isOwner(int category, String identifier,IWUserContext iwc) throws Exception {
    Boolean returnVal = Boolean.FALSE;
    User user = iwc.getUser();
    if(user != null){
      List[] permissionOrder = new Vector[2];
      permissionOrder[0] = new Vector();
      permissionOrder[0].add( Integer.toString(user.getGroupID()) );
      permissionOrder[1] = new Vector();
      permissionOrder[1].add( Integer.toString(user.getPrimaryGroupID()) );

      returnVal = checkForPermission(permissionOrder,category, identifier,AccessControl.PERMISSION_KEY_OWNER,iwc);
    }

    if(returnVal != null){
      return returnVal.booleanValue();
    } else {
      return false;
    }
  }

  public boolean isOwner(List groupIds, Object obj,IWUserContext iwc) throws Exception {
    Boolean returnVal = Boolean.FALSE;
    List[] permissionOrder = new Vector[1];
    permissionOrder[0] = groupIds;
    returnVal = checkForPermission(permissionOrder, obj, AccessControl.PERMISSION_KEY_OWNER,iwc);

    if(returnVal != null){
      return returnVal.booleanValue();
    } else {
      return false;
    }
  }

  public boolean isOwner(ICFile file, IWUserContext iwc)throws Exception{
    return isOwner(AccessController.CATEGORY_FILE_ID, file.getPrimaryKey().toString(),iwc);
  }
  
	public boolean isOwner(Group group, IWUserContext iwc)throws Exception{
		return isOwner(AccessController.CATEGORY_GROUP_ID, group.getPrimaryKey().toString(),iwc);
	}
	
	public boolean isGroupOwnerRecursively(Group group, IWUserContext iwc)throws Exception{
		boolean value = isOwner(group,iwc);
		
		if(!value){//check parents to see if user is an owner of them
			
			Collection parents = getGroupBusiness(iwc).getParentGroups(group);//little at at time not all groups recursive
			
			if(parents!=null && !parents.isEmpty()){
				
				Iterator parentIter = parents.iterator();
				while (parentIter.hasNext() && !value) {
					Group parent = (Group) parentIter.next();
					value = isGroupOwnerRecursively(parent,iwc);
				}
				
				return value;
				
			}
			else{
				return false;//ran out of parents to check
			}
			
		}
		else {
			return true;
		}
		
	}
	
	
	

  public boolean isOwner(ICPage page, IWUserContext iwc)throws Exception{
    return isOwner(AccessController.CATEGORY_PAGE_INSTANCE, Integer.toString(page.getID()),iwc);
  }

  /**
   * @todo implement isOwner(ICObject obj, int entityRecordId, IWUserContext iwc)throws Exception
   */
  public boolean isOwner(ICObject obj, int entityRecordId, IWUserContext iwc)throws Exception{
    return false;
  }

  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getAdministratorGroupName(){
    return "administrator";
  }
  
  public GroupBusiness getGroupBusiness(IWUserContext iwc) {
	  try {
		  return (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc.getApplicationContext(), GroupBusiness.class);
	  }
	  catch (RemoteException e) {
		  e.printStackTrace();
	  }

	  return null;
  }

  public boolean hasPermission(String permissionKey, int category, String identifier, IWUserContext iwc) throws Exception{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

    if (isAdmin(iwc)){
      return true;
    }

    User user = LoginBusinessBean.getUser(iwc);

    Collection groups = null;
    List[] permissionOrder = null; // Everyone, users, user, primaryGroup, otherGroups

    if (user == null){
    	permissionOrder = new List[1];
    	permissionOrder[0] = new ArrayList();
    	permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
    } else {
    	
    	
    	String recurseParents = iwc.getApplicationContext().getApplicationSettings().getProperty("TEMP_ACCESS_CONTROL_DO_NOT_RECURSE_PARENTS");
    	if(recurseParents==null){//old crap
//			TODO Eiki remove this old crap, one should not recurse the parents! Done in more places
    		groups = LoginBusinessBean.getPermissionGroups(iwc);
    	}
    	else{//the correct version
    		groups = getParentGroupsAndPermissionControllingParentGroups(permissionKey,iwc);
    		
    	}
    	
    	GenericGroup primaryGroup = LoginBusinessBean.getPrimaryGroup(iwc);
    	
    	if (groups != null && !groups.isEmpty()){
    		if(primaryGroup != null){
    			groups.remove(primaryGroup);
    		}
    		List groupIds = new ArrayList();
    		Iterator iter = groups.iterator();
    		while (iter.hasNext()) {
    			groupIds.add(Integer.toString(((GenericGroup)iter.next()).getID()));
    		}
    		permissionOrder = new List[5];
    		permissionOrder[4] = groupIds;
    	} else {
    		permissionOrder = new List[4];
    	}
    	permissionOrder[0] = new ArrayList();
    	permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
    	permissionOrder[1] = new ArrayList();
    	permissionOrder[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
    	permissionOrder[2] = new ArrayList();
    	permissionOrder[2].add( Integer.toString(user.getGroupID()) );
    	permissionOrder[3] = new ArrayList();
    	permissionOrder[3].add( Integer.toString(user.getPrimaryGroupID()) );
    	// Everyone, user, primaryGroup, otherGroups
    }
    myPermission = checkForPermission(permissionOrder, category, identifier, permissionKey, iwc);
    if(myPermission != null){
    	return myPermission.booleanValue();
    }
    
    
    if(permissionKey.equals(AccessControl.PERMISSION_KEY_EDIT) || permissionKey.equals(AccessControl.PERMISSION_KEY_VIEW)){
    	return isOwner(category, identifier, iwc);
    } else {
    	return false;
    }

  }


  private static Boolean checkForPermission(List[] permissionGroupLists, int category, String identifier, String permissionKey, IWUserContext iwc ) throws Exception {
    Boolean myPermission = null;
    if(permissionGroupLists != null){
      int arrayLength = permissionGroupLists.length;
      switch (category) {
        case AccessController.CATEGORY_OBJECT_INSTANCE:
        case AccessController.CATEGORY_OBJECT:
        case AccessController.CATEGORY_BUNDLE :
        case AccessController.CATEGORY_PAGE_INSTANCE:
        case AccessController.CATEGORY_PAGE:
          //PageInstance
          if(category == AccessController.CATEGORY_PAGE_INSTANCE &&  !identifier.equals(Integer.toString(_notBuilderPageID)) ){
            for (int i = 0; i < arrayLength; i++) {
              myPermission = PermissionCacher.hasPermissionForPage(identifier,iwc,permissionKey,permissionGroupLists[i]);
              if(myPermission != null){
                return myPermission;
              }
            }

            if(!permissionKey.equals(AccessControl.PERMISSION_KEY_OWNER)){
              // Global - (Page)
              if(!PermissionCacher.anyInstancePermissionsDefinedForPage(identifier,iwc,permissionKey)){
                ICObject page = getStaticPageICObject();
                if(page != null){
                  for (int i = 0; i < arrayLength; i++) {
                    myPermission = PermissionCacher.hasPermission(page,iwc,permissionKey,permissionGroupLists[i]);
                    if(myPermission != null){
                      return myPermission;
                    }
                  }
                }
              }
              // Global - (Page)
            }


            return myPermission;
          }else{
            //instance
            for (int i = 0; i < arrayLength; i++) {
              myPermission = PermissionCacher.hasPermissionForObjectInstance(identifier,iwc,permissionKey,permissionGroupLists[i]);
              if(myPermission != null){
                return myPermission;
              }
            }
            //instance

            if(!permissionKey.equals(AccessControl.PERMISSION_KEY_OWNER)){
              // Global - (object)
              if(!PermissionCacher.anyInstancePermissionsDefinedForObject(identifier,iwc,permissionKey)){
                for (int i = 0; i < arrayLength; i++) {
                  myPermission = PermissionCacher.hasPermissionForObject(identifier,iwc,permissionKey,permissionGroupLists[i]);
                  if(myPermission != null){
                    return myPermission;
                  }
                }
              }
              // Global - (object)
            }

            return myPermission;
          }
        case AccessController.CATEGORY_JSP_PAGE:
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForJSPPage(identifier,iwc,permissionKey,permissionGroupLists[i]);
            if(myPermission != null){
              return myPermission;
            }
          }

          return myPermission;
        case AccessController.CATEGORY_FILE_ID:
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForFile(identifier,iwc,permissionKey,permissionGroupLists[i]);
            if(myPermission != null){
              return myPermission;
            }
          }

          if(!permissionKey.equals(AccessControl.PERMISSION_KEY_OWNER)){
            // Global - (File)
            if(!PermissionCacher.anyInstancePermissionsDefinedForFile(identifier,iwc,permissionKey)){
              ICObject file = getStaticFileICObject();
              if(file != null){
                for (int i = 0; i < arrayLength; i++) {
                  myPermission = PermissionCacher.hasPermission(file,iwc,permissionKey,permissionGroupLists[i]);
                  if(myPermission != null){
                    return myPermission;
                  }
                }
              }
            }
            // Global - (File)
          }

          return myPermission;
          
			 case AccessController.CATEGORY_GROUP_ID:
			 		for (int i = 0; i < arrayLength; i++) {
						 myPermission = PermissionCacher.hasPermissionForGroup(identifier,iwc,permissionKey,permissionGroupLists[i]);
						 if(myPermission != null){
							 return myPermission;
						 }
					 }
      }//switch ends
    }
    return myPermission;
  }

/**
 * The main hasPermission method all hasXYZPesmission methodscall this method.
 * @see com.idega.core.accesscontrol.business.AccessController#hasPermission(String, PresentationObject, IWUserContext)
 */
  public boolean hasPermission(String permissionKey, Object obj,IWUserContext iwc) throws Exception{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

    if (isAdmin(iwc)){//this is almost a security hole - eiki
      return true;
    }

    User user = LoginBusinessBean.getUser(iwc);

    Collection groups = null;
//The order that is checked for : Everyone Group, Logged on users group, user, primaryGroup, otherGroups
//This is an ordered list to check against the permissions set in the database
    List[] usersGroupsToCheckAgainstPermissions = null; 
//
    
    if (user == null){//everyone group check
      usersGroupsToCheckAgainstPermissions = new List[1];
      usersGroupsToCheckAgainstPermissions[0] = new ArrayList();
      usersGroupsToCheckAgainstPermissions[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
    } 
    else {//user check


	String recurseParents = iwc.getApplicationContext().getApplicationSettings().getProperty("TEMP_ACCESS_CONTROL_DO_NOT_RECURSE_PARENTS");
	  if(recurseParents==null){//old crap
	  		//TODO Eiki remove this old crap, one should not recurse the parents! Done in more places
		  groups = LoginBusinessBean.getPermissionGroups(iwc);
	  }
	  else{//the correct version
	  	groups = getParentGroupsAndPermissionControllingParentGroups(permissionKey, iwc);
	  }
      
      
      GenericGroup primaryGroup = LoginBusinessBean.getPrimaryGroup(iwc);

      if (groups != null && !groups.isEmpty() ){
        if(primaryGroup != null){
          groups.remove(primaryGroup);
        }
        List groupIds = new Vector();
        Iterator iter = groups.iterator();
        while (iter.hasNext()) {
          groupIds.add(Integer.toString(((GenericGroup)iter.next()).getID()));
        }
        
        
        usersGroupsToCheckAgainstPermissions = new List[5];
        usersGroupsToCheckAgainstPermissions[4] = groupIds;
      } else {
        usersGroupsToCheckAgainstPermissions = new List[4];
      }
        usersGroupsToCheckAgainstPermissions[0] = new ArrayList();
        usersGroupsToCheckAgainstPermissions[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
        usersGroupsToCheckAgainstPermissions[1] = new ArrayList();
        usersGroupsToCheckAgainstPermissions[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
        usersGroupsToCheckAgainstPermissions[2] = new ArrayList();
        usersGroupsToCheckAgainstPermissions[2].add( Integer.toString(user.getGroupID()) );
        usersGroupsToCheckAgainstPermissions[3] = new ArrayList();
        usersGroupsToCheckAgainstPermissions[3].add( Integer.toString(user.getPrimaryGroupID()) );
        // Everyone, user, primaryGroup, otherGroups
    }
    
    
    myPermission = checkForPermission(usersGroupsToCheckAgainstPermissions, obj, permissionKey, iwc);
    
    
    
    boolean hasPermission = false;
	if(myPermission != null ){
		hasPermission = myPermission.booleanValue();
		if(hasPermission){
			return true;
		}
    }
    
	
    //if the user is an owner these rights are given. double checking really
    if(permissionKey.equals(AccessControl.PERMISSION_KEY_EDIT) || permissionKey.equals(AccessControl.PERMISSION_KEY_VIEW)){
    	if(obj instanceof Group){
    		return isGroupOwnerRecursively((Group)obj,iwc);//because owners parents groups always get read/write access
    	}
    	else{
    		return isOwner(obj,iwc);
    	}
    }
    else {
      return false;
    }

  } // method hasPermission



private Collection getParentGroupsAndPermissionControllingParentGroups(String permissionKey, IWUserContext iwc) throws RemoteException {
	Collection groups;
	//must be slow optimize
	  groups = getGroupBusiness(iwc).getParentGroups(iwc.getCurrentUser());//com.idega.user.data.User
	
	  Vector groupsToCheckForPermissions = new Vector();
	  Iterator iter = groups.iterator();
	  while (iter.hasNext()) {
		  Group parent = (Group) iter.next();
		  if( !AccessControl.PERMISSION_KEY_OWNER.equals(permissionKey) && parent.getPermissionControllingGroupID()>0){
			  groupsToCheckForPermissions.add(parent.getPermissionControllingGroup());
		  }
	  }
	
	  groups.addAll(groupsToCheckForPermissions);
	return groups;
}

/**
 * Assembles the grouplist for this user to check agains the permission maps in
 * memory.
 * @see com.idega.core.accesscontrol.business.AccessController#hasPermission(List, String, PresentationObject, IWUserContext)
 */
  public boolean hasPermission(List groupIds,String permissionKey, Object obj,IWUserContext iwc) throws Exception{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked
//TODO Eiki make one universal haspermission method
    List[] permissionOrder = null; // Everyone, users, (primaryGroup), otherGroups

    if(groupIds != null){
      if (groupIds.contains(Integer.toString(getPermissionGroupAdministrator().getID()))){
        return true;
      } else {
        if(groupIds.size() == 1){
          if(groupIds.get(0).equals(Integer.toString(_GROUP_ID_EVERYONE))){
            permissionOrder = new List[1];
            permissionOrder[0] = new Vector();
            permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
          }else {
            if(groupIds.get(0).equals(Integer.toString(_GROUP_ID_USERS))){
              permissionOrder = new List[2];
            } else{
              permissionOrder = new List[3];
              permissionOrder[2] = groupIds;
            }
              permissionOrder[0] = new Vector();
              permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
              permissionOrder[1] = new Vector();
              permissionOrder[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
          }
        } else if (groupIds.size() > 1){
            permissionOrder = new List[3];
            permissionOrder[0] = new Vector();
            permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
            permissionOrder[1] = new Vector();
            permissionOrder[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
            permissionOrder[2] = groupIds;
            // Everyone, users, (primaryGroup), otherGroups
        } else {
          return false;
        }
      }
    } else{
        return false;
    }
    myPermission = checkForPermission(permissionOrder, obj, permissionKey, iwc);
    
    
	boolean hasPermission = false;
	if(myPermission != null ){
		hasPermission = myPermission.booleanValue();
		if(hasPermission){
			return true;
		}
	}



    if(permissionKey.equals(AccessControl.PERMISSION_KEY_EDIT) || permissionKey.equals(AccessControl.PERMISSION_KEY_VIEW)){
		if(obj instanceof Group){
		   return isGroupOwnerRecursively((Group)obj,iwc);//because owners parents groups always get read/write access
	   }
	   else{
		   return isOwner(obj,iwc);
	   }
    } else {
      return false;
    }

  } // method hasPermission




  private static Boolean checkForPermission(List[] permissionGroupLists, Object obj, String permissionKey, IWUserContext iwc ) throws Exception {
    Boolean myPermission = Boolean.FALSE;
    if(permissionGroupLists != null){
      int arrayLength = permissionGroupLists.length;
       
			//JSP PAGE
      if (obj == null){ 
        for (int i = 0; i < arrayLength; i++) {
          myPermission = PermissionCacher.hasPermissionForJSPPage(iwc,permissionKey,permissionGroupLists[i]);
					if(Boolean.TRUE.equals(myPermission)){
						return myPermission;
					}
        }

        return myPermission;
      } 
			//JSP PAGE ENDS
      else { // if (obj != null)

				//PAGE
        if(obj instanceof Page && ((Page)obj).getPageID() != _notBuilderPageID ){
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForPage((Page)obj,iwc,permissionKey,permissionGroupLists[i]);
						if(Boolean.TRUE.equals(myPermission)){
							return myPermission;
						}
          }

          if(!permissionKey.equals(AccessControl.PERMISSION_KEY_OWNER)){
          	
            // Global - (Page)
            if(!PermissionCacher.anyInstancePermissionsDefinedForPage((Page)obj,iwc,permissionKey)){
              ICObject page = getStaticPageICObject();
              if(page != null){
                for (int i = 0; i < arrayLength; i++) {
                  myPermission = PermissionCacher.hasPermission(page,iwc,permissionKey,permissionGroupLists[i]);                  

                  if(Boolean.TRUE.equals(myPermission)){
                    return myPermission;
                  }

                }
              }
            }
            // Global - (Page)
          }


          return myPermission;
          
          
        }//PAGE ENDS
        else if( obj instanceof Group){// Group checking
        	
			for (int i = 0; i < arrayLength; i++) {
				
				myPermission = PermissionCacher.hasPermissionForGroup((Group)obj,iwc,permissionKey,permissionGroupLists[i]);
				
				if(Boolean.TRUE.equals(myPermission)){
					return myPermission;
				}
			}
		
    	
        }
        else{
          //Object instance
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForObjectInstance((PresentationObject)obj,iwc,permissionKey,permissionGroupLists[i]);
						if(Boolean.TRUE.equals(myPermission)){
							return myPermission;
						}
          }

          //instance
/*
          //page permission inheritance
          if(obj.allowPagePermissionInheritance()){
            Page p = obj.getParentPage();
            if(p != null && p.getPageID() != _notBuilderPageID ){
              myPermission = checkForPermission(permissionGroupLists,p,permissionType,iwc);
              if(myPermission != null){
                return myPermission;
              }
            }
          }
          //page permission inheritance
*/
          if(!permissionKey.equals(AccessControl.PERMISSION_KEY_OWNER)){
            // Global - (object)
            if(!PermissionCacher.anyInstancePermissionsDefinedForObject((PresentationObject) obj,iwc,permissionKey)){
              for (int i = 0; i < arrayLength; i++) {
                myPermission = PermissionCacher.hasPermissionForObject((PresentationObject) obj,iwc,permissionKey,permissionGroupLists[i]);
								if(Boolean.TRUE.equals(myPermission)){
									return myPermission;
								}
              }
            }
            // Global - (object)
          }

          return myPermission;
        }
      }
    }
    return myPermission;
  }


  //temp
  private static ICObject getStaticPageICObject(){
    if(staticPageICObject == null){
      try {
        staticPageICObject = (ICObject)EntityFinder.findAllByColumn((ICObject)com.idega.core.component.data.ICObjectBMPBean.getStaticInstance(ICObject.class),com.idega.core.component.data.ICObjectBMPBean.getClassNameColumnName(),Page.class.getName()).get(0);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return staticPageICObject;
  }

  //temp
  private static ICObject getStaticFileICObject(){
    if(staticFileICObject == null){
      try {
				staticFileICObject = (ICObject)EntityFinder.findAllByColumn((ICObject)com.idega.core.component.data.ICObjectBMPBean.getStaticInstance(ICObject.class),com.idega.core.component.data.ICObjectBMPBean.getClassNameColumnName(),ICFile.class.getName()).get(0);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return staticFileICObject;
  }
  

//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getObjectInstanceIdString(){
//    return "ic_object_instance_id";
//  }
//
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getObjectIdString(){
//    return "ic_object_id";
//  }
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getBundleIdentifierString(){
//    return "iw_bundle_identifier";
//  }
//
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getPageIdString(){
//    return "page_id";
//  }
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getPageString(){
//    return "page";
//  }
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getJSPPageString(){
//    return "jsp_page";
//  }
//


  public boolean hasEditPermission(PresentationObject obj,IWUserContext iwc)throws Exception{
    return hasPermission( PERMISSION_KEY_EDIT , obj, iwc);
  }


  public boolean hasViewPermission(PresentationObject obj,IWUserContext iwc){
    try {
      /*boolean permission = hasPermission( _PERMISSIONKEY_VIEW, obj, iwc);
      System.err.println(obj.getClass().getName()+" has permission: " + permission);
      return permission;
      */
      return hasPermission( PERMISSION_KEY_VIEW, obj, iwc);
    }
    catch (Exception ex) {
      return false;
    }
  }

  public boolean hasViewPermission(List groupIds, PresentationObject obj,IWUserContext iwc){
    try {
      /*boolean permission = hasPermission( _PERMISSIONKEY_VIEW, obj, iwc);
      System.err.println(obj.getClass().getName()+" has permission: " + permission);
      return permission;
      */
      return hasPermission(groupIds, PERMISSION_KEY_VIEW, obj, iwc);
    }
    catch (Exception ex) {
      return false;
    }
  }

  public void setJSPPagePermission(IWUserContext iwc, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_JSP_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + PageContextValue + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = getPermissionHome().create();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.CATEGORY_STRING_JSP_PAGE);
      // use 'ICJspHandler.getJspPageInstanceID(iwc)' on the current page and send in as PageContextValue
      permission.setContextValue(PageContextValue);
      permission.setGroupID(new Integer(group.getID()));
      permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateJSPPagePermissions(PageContextValue,permissionType,iwc);
  }

  public void setObjectPermission(IWUserContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_IC_OBJECT_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + obj.getICObjectID() + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = getPermissionHome().create();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.CATEGORY_STRING_IC_OBJECT_ID);
      permission.setContextValue(Integer.toString(obj.getICObjectID()));
      permission.setGroupID(new Integer(group.getID()));
      permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateObjectPermissions(Integer.toString(obj.getICObjectID()),permissionType,iwc);
  }


  public void setBundlePermission(IWUserContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_BUNDLE_IDENTIFIER + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + obj.getBundleIdentifier() + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = getPermissionHome().create();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.CATEGORY_STRING_BUNDLE_IDENTIFIER);
      permission.setContextValue(obj.getBundleIdentifier());
      permission.setGroupID(new Integer(group.getID()));
      permission.setPermissionString(permissionType);
  //        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateBundlePermissions(obj.getBundleIdentifier(),permissionType,iwc);
  }



  public void setObjectInstacePermission(IWUserContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception{
    setObjectInstacePermission(iwc,Integer.toString(group.getID()),Integer.toString(obj.getICObjectInstance().getID()),permissionType,permissionValue);
  }

  public static boolean removeICObjectInstancePermissionRecords(IWUserContext iwc, String ObjectInstanceId, String permissionKey, String[] groupsToRemove){
    String sGroupList = "";
    if (groupsToRemove != null && groupsToRemove.length > 0){
      for(int g = 0; g < groupsToRemove.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupsToRemove[g];
      }
    }
    if(!sGroupList.equals("")){
      ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
      try {
        boolean done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_OBJECT_INSTANCE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
        if(done){
          PermissionCacher.updateObjectInstancePermissions(ObjectInstanceId,permissionKey,iwc);
        }
        return done;
      }
      catch (Exception ex) {
        return false;
      }
    } else {
      return true;
    }

  }


  public static boolean removePermissionRecords(int permissionCategory, IWUserContext iwc, String identifier, String permissionKey, String[] groupsToRemove){
    String sGroupList = "";
    if (groupsToRemove != null && groupsToRemove.length > 0){
      for(int g = 0; g < groupsToRemove.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupsToRemove[g];
      }
    }
    if(!sGroupList.equals("")){
      ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
      try {

        switch (permissionCategory) {
          case AccessControl.CATEGORY_OBJECT_INSTANCE :
            SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_OBJECT_INSTANCE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl.CATEGORY_OBJECT :
            SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_IC_OBJECT_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl.CATEGORY_BUNDLE :
            SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_BUNDLE_IDENTIFIER + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl.CATEGORY_PAGE_INSTANCE :
            SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_PAGE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl.CATEGORY_PAGE :
            SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl.CATEGORY_JSP_PAGE :
            SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_JSP_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
        }

        PermissionCacher.updatePermissions(permissionCategory,identifier,permissionKey,iwc);

        return true;
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    } else {
      return true;
    }

  }



  public void setPermission(int permissionCategory, IWUserContext iwc, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws Exception{
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    boolean update = true;
    try {
      switch (permissionCategory) {
        case AccessControl.CATEGORY_OBJECT_INSTANCE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_OBJECT_INSTANCE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl.CATEGORY_OBJECT :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_IC_OBJECT_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl.CATEGORY_BUNDLE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_BUNDLE_IDENTIFIER + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl.CATEGORY_PAGE_INSTANCE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_PAGE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl.CATEGORY_PAGE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl.CATEGORY_JSP_PAGE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_JSP_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl.CATEGORY_FILE_ID :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_FILE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
				case AccessControl.CATEGORY_GROUP_ID :
					permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_GROUP_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
					break;
									

      }

    }
    catch (Exception ex) {
      permission = getPermissionHome().create();
      update = false;
    }

    if(!update){

      switch (permissionCategory) {
        case AccessControl.CATEGORY_OBJECT_INSTANCE :
          permission.setContextType(AccessControl.CATEGORY_STRING_OBJECT_INSTANCE_ID);
          break;
        case AccessControl.CATEGORY_OBJECT :
          permission.setContextType(AccessControl.CATEGORY_STRING_IC_OBJECT_ID);
          break;
        case AccessControl.CATEGORY_BUNDLE :
          permission.setContextType(AccessControl.CATEGORY_STRING_BUNDLE_IDENTIFIER);
          break;
        case AccessControl.CATEGORY_PAGE_INSTANCE :
          permission.setContextType(AccessControl.CATEGORY_STRING_PAGE_ID);
          break;
        case AccessControl.CATEGORY_PAGE :
          permission.setContextType(AccessControl.CATEGORY_STRING_PAGE);
          break;
        case AccessControl.CATEGORY_JSP_PAGE :
          permission.setContextType(AccessControl.CATEGORY_STRING_JSP_PAGE);
          break;
        case AccessControl.CATEGORY_FILE_ID :
          permission.setContextType(AccessControl.CATEGORY_STRING_FILE_ID);
          break;
				case AccessControl.CATEGORY_GROUP_ID :
					permission.setContextType(AccessControl.CATEGORY_STRING_GROUP_ID);
					break;
          
          
        
      }

      permission.setContextValue(identifier);
      permission.setGroupID(new Integer(permissionGroupId));
      permission.setPermissionString(permissionKey);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updatePermissions(permissionCategory, identifier, permissionKey, iwc);
  }


  public void setObjectInstacePermission(IWUserContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_OBJECT_INSTANCE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + permissionGroupId))[0];
    }
    catch (Exception ex) {
      permission = getPermissionHome().create();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.CATEGORY_STRING_OBJECT_INSTANCE_ID);
      permission.setContextValue(ObjectInstanceId);
      permission.setGroupID(new Integer(permissionGroupId));
      permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateObjectInstancePermissions(ObjectInstanceId,permissionType,iwc);
  }





  public int createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs)throws Exception{
    PermissionGroup newGroup = getPermissionGroupHome().create();

    if(GroupName != null)
      newGroup.setName(GroupName);

    if(Description != null)
      newGroup.setDescription(Description);

    if(ExtraInfo != null)
      newGroup.setExtraInfo(ExtraInfo);

    newGroup.insert();

    int newGroupID = newGroup.getID();

    if(userIDs != null){
      for (int i = 0; i < userIDs.length; i++) {
        addUserToPermissionGroup(newGroup, userIDs[i]);
      }
    }
    if (groupIDs != null){
      for (int j = 0; j < groupIDs.length; j++) {
        addGroupToPermissionGroup(newGroup, groupIDs[j]);
      }
    }

    return newGroupID;

  }

  public static void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws Exception{
    User userToAdd = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userIDtoAdd);
    group.addUser(userToAdd);
  }


  public static void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws Exception{
    GenericGroup groupToAdd = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupIDtoAdd);
    group.addGroup(groupToAdd);
  }


  /**
   * @todo implement filter to get grouptypes from property file
   */
  private static String[] getPermissionGroupFilter(){
    //filter begin
    String[] groupsToReturn = new String[2];
    groupsToReturn[0] = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
    //TODO: This is a hack, implement better
	try
	{
		//groupsToReturn[1] = com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupBMPBean.getStaticGroupInstance().getGroupTypeValue();
		groupsToReturn[1] = FieldAccessor.getInstance().getStaticStringFieldValue(Class.forName("com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupBMPBean"),"GROUP_TYPE");
	}
	catch (IllegalArgumentException e)
	{
		e.printStackTrace();
	}
	catch (IllegalAccessException e)
	{
		e.printStackTrace();
	}
	catch (NoSuchFieldException e)
	{
		e.printStackTrace();
	}
	catch (ClassNotFoundException e)
	{
		e.printStackTrace();
	}
    /*
    String[] groupsToReturn = new String[1];
    groupsToReturn[0] = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
*/
    //filter end
    return groupsToReturn;
  }

  public static List getPermissionGroups(User user) throws Exception{
    //temp - ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).createLegacy()
    int groupId = user.getGroupID();
    if(groupId != -1){
      return getPermissionGroups(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
    }else{
      return null;
    }
  }

  public static List getPermissionGroups(GenericGroup group) throws Exception{
    List permissionGroups = UserGroupBusiness.getGroupsContaining(group,getPermissionGroupFilter(),true);

    if(permissionGroups != null){
      return permissionGroups;
    }else {
      return null;
    }
  }

  public List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception {
    List toReturn = new Vector(0);
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    List permissions = null;

    switch (permissionCategory) {
      case AccessControl.CATEGORY_OBJECT_INSTANCE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_OBJECT_INSTANCE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl.CATEGORY_OBJECT :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_IC_OBJECT_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl.CATEGORY_BUNDLE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_BUNDLE_IDENTIFIER + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl.CATEGORY_PAGE_INSTANCE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_PAGE_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl.CATEGORY_PAGE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl.CATEGORY_JSP_PAGE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + CATEGORY_STRING_JSP_PAGE + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + identifier + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionValueColumnName() +" = 'Y'");
        break;
    }

    if (permissions != null){
      Iterator iter = permissions.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        try {
          toReturn.add(((com.idega.core.accesscontrol.data.PermissionGroupHome)com.idega.data.IDOLookup.getHomeLegacy(PermissionGroup.class)).findByPrimaryKeyLegacy(((ICPermission)item).getGroupID()));
        }
        catch (Exception ex) {
          System.err.println("Accesscontrol.getAllowedGroups(): Group not created for id "+((ICPermission)item).getGroupID());
        }

      }
    }
    toReturn.remove(AdministratorPermissionGroup);
    return toReturn;
  }


  public List getAllPermissionGroups()throws Exception {

    List permissionGroups = com.idega.core.data.GenericGroupBMPBean.getAllGroups(getPermissionGroupFilter(),true);
    if(permissionGroups != null){
      permissionGroups.remove(getPermissionGroupAdministrator());
    }

    return permissionGroups;
  }


  public List getStandardGroups() throws Exception {
    if(standardGroups == null){
      initStandardGroups();
    }
    return standardGroups;
  }

  private void initStandardGroups() throws Exception {
    standardGroups = new Vector();
    //standardGroups.add(AccessControl.getPermissionGroupAdministrator());
    standardGroups.add(this.getPermissionGroupEveryOne());
    standardGroups.add(this.getPermissionGroupUsers());
  }


  public User getAdministratorUser() throws Exception {
    Object ob = getApplication().getAttribute(_APPADDRESS_ADMINISTRATOR_USER);
    if(ob == null){
      try {
        initAdministratorUser();
        return (User)getApplication().getAttribute(_APPADDRESS_ADMINISTRATOR_USER);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return null;
      }


    }else{
      return (User)ob;
    }
  }

  private User createAdministratorUser()throws Exception{
    User adminUser = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).createLegacy();
    adminUser.setColumn(com.idega.core.user.data.UserBMPBean.getColumnNameFirstName(),_ADMINISTRATOR_NAME);
    adminUser.insert();
    
    int adminUserID = adminUser.getID();

    UserGroupRepresentative ugr = ((com.idega.core.user.data.UserGroupRepresentativeHome)com.idega.data.IDOLookup.getHomeLegacy(UserGroupRepresentative.class)).createLegacy();
    ugr.setName("admin");
    ugr.insert();

    adminUser.setGroupID(ugr.getID());
    adminUser.setPrimaryGroupID(this.getPermissionGroupAdministrator().getID());
    adminUser.update();

	//System.out.println("Creating login for user with id="+adminUserID);
    LoginDBHandler.createLogin(adminUserID,"Administrator","idega",Boolean.TRUE,IWTimestamp.RightNow(),-1,Boolean.FALSE,Boolean.TRUE,Boolean.TRUE,EncryptionType.MD5);
    return adminUser;
  }

  private void initAdministratorUser() throws Exception{
    List list = EntityFinder.findAllByColumn(com.idega.core.user.data.UserBMPBean.getStaticInstance(),com.idega.core.user.data.UserBMPBean.getColumnNameFirstName(),_ADMINISTRATOR_NAME);
    User adminUser = null;
    if(list == null || list.size() < 1){
      adminUser = createAdministratorUser();
    } else {
      adminUser = (User)list.get(0);
    }
    getApplication().setAttribute(_APPADDRESS_ADMINISTRATOR_USER,adminUser);
  }

  public void executeService(){

    try {
      PermissionGroup permission = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance();
      List groups = EntityFinder.findAllByColumn(permission,com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getGroupTypeColumnName(),permission.getGroupTypeValue());
      if(groups != null){
        Iterator iter = groups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(getAdministratorGroupName().equals (((GenericGroup)item).getName())){
            AdministratorPermissionGroup = (PermissionGroup)item;
          }
        }
      }
      if(AdministratorPermissionGroup == null){
        initAdministratorPermissionGroup();
      }
    }
    catch (Exception ex) {
      System.err.println("AccessControl: PermissionGroup administrator not initialized");
      ex.printStackTrace();
    }

    try {
      PermissionGroupEveryOne = ((com.idega.core.accesscontrol.data.PermissionGroupHome)com.idega.data.IDOLookup.getHomeLegacy(PermissionGroup.class)).findByPrimaryKeyLegacy(_GROUP_ID_EVERYONE);
    }
    catch (Exception e) {
      try {
        initPermissionGroupEveryone();
      }
      catch (Exception ex) {
        System.err.println("AccessControl: PermissionGroup Everyone not initialized");
      }
    }

    try {
      PermissionGroupUsers = ((com.idega.core.accesscontrol.data.PermissionGroupHome)com.idega.data.IDOLookup.getHomeLegacy(PermissionGroup.class)).findByPrimaryKeyLegacy(_GROUP_ID_USERS);
    }
    catch (Exception e) {
      try {
        initPermissionGroupUsers();
      }
      catch (Exception ex) {
        System.err.println("AccessControl: PermissionGroup Users not initialized");
      }
    }

    try {
      initAdministratorUser();
    }
    catch (Exception ex) {
      System.err.println("AccessControl: User Administrator not initialized");
      ex.printStackTrace();
    }

  }

  public String getServiceName(){
    return "AccessControl";
  }

  public static boolean isValidUsersFirstName(String name){
    return !_ADMINISTRATOR_NAME.equals(name);
  }



  public String[] getICObjectPermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = PERMISSION_KEY_VIEW;
    keys[1] = PERMISSION_KEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }


  public String[] getBundlePermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = PERMISSION_KEY_VIEW;
    keys[1] = PERMISSION_KEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }

  public String[] getBundlePermissionKeys(String BundleIdentifier){
    String[] keys = new String[2];

    keys[0] = PERMISSION_KEY_VIEW;
    keys[1] = PERMISSION_KEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }

  public String[] getPagePermissionKeys(){
    String[] keys = new String[2];

    keys[0] = PERMISSION_KEY_VIEW;
    keys[1] = PERMISSION_KEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }






  public static void initICObjectPermissions(ICObject obj) throws Exception{

    ICPermission permission = ((com.idega.core.accesscontrol.data.ICPermissionHome)com.idega.data.IDOLookup.getHomeLegacy(ICPermission.class)).createLegacy();
    /*
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_ID + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " = '" + obj.getICObjectID(iwc) + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = ((com.idega.core.accesscontrol.data.ICPermissionHome)com.idega.data.IDOLookup.getHomeLegacy(ICPermission.class)).createLegacy();
      update = false;
    }*/

    permission.setContextType(AccessControl.CATEGORY_STRING_IC_OBJECT_ID);
    permission.setContextValue(Integer.toString(obj.getID()));
    permission.setGroupID(new Integer(AccessControl._GROUP_ID_EVERYONE));
    permission.setPermissionString(AccessControl.PERMISSION_KEY_VIEW);
//        permission.setPermissionStringValue();
    permission.setPermissionValue(Boolean.TRUE);
    permission.insert();

    //PermissionCacher.updateObjectPermissions(Integer.toString(obj.getICObjectID(iwc)),permissionType,iwc);




  }


  /**
   * @todo implement hasFilePermission(String permissionKey, int id, IWUserContext iwc)throws Exception
   */
  public boolean hasFilePermission(String permissionKey, int id, IWUserContext iwc)throws Exception{
    return true;
  }

  /**
   * @todo implement hasDataPermission(String permissionKey, ICObject obj, int entityRecordId, IWUserContext iwc)
   */
  public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId, IWUserContext iwc) throws Exception{
    return true;
  }



/*
  public boolean hasPermission(Class someClass, int id, IWUserContext iwc) throws Exception{
    if(someClass.equals(ICFile.class)){
      return true;
    }else if(someClass.equals(ICObject.class)){
      return true;
    }else {
      return true;
    }
  }
*/



  public void setCurrentUserAsOwner(ICPage page, IWUserContext iwc)throws Exception {
    User user = iwc.getUser();
//    System.out.println("User = "+ user);
    if(user != null){
      int groupId = -1;
      groupId = user.getPrimaryGroupID();
      if(groupId == -1){
        groupId = user.getGroupID();
      }
//      System.out.println("Group = "+ groupId);
      if(groupId != -1){
          setAsOwner(page,groupId,iwc);
//        setPermission(AccessController._CATEGORY_PAGE,iwc,Integer.toString(groupId),Integer.toString(page.getID()),AccessControl._PERMISSIONKEY_EDIT,Boolean.TRUE);
//        setPermission(AccessController._CATEGORY_PAGE,iwc,Integer.toString(groupId),Integer.toString(page.getID()),AccessControl._PERMISSIONKEY_VIEW,Boolean.TRUE);
      } else {
        // return false;
      }
    } else {
      // return false;
    }
  }

  /**
   * @todo implement setAsOwner(ICFile file, IWUserContext iwc)throws Exception
   */
  public void setAsOwner(ICPage page, int groupId, IWUserContext iwc)throws Exception {
    setPermission(AccessController.CATEGORY_PAGE_INSTANCE,iwc,Integer.toString(groupId),Integer.toString(page.getID()),AccessControl.PERMISSION_KEY_OWNER,Boolean.TRUE);
  }


  /**
   * @todo implement setAsOwner(PresentationObject obj , IWUserContext iwc) throws Exception
   */
  public void setAsOwner(PresentationObject obj, int groupId, IWUserContext iwc) throws Exception {}

  /**
   * @todo implement setAsOwner(ICFile file, IWUserContext iwc)throws Exception
   */
  public void setAsOwner(ICFile file, int groupId, IWUserContext iwc)throws Exception {
    setPermission(AccessController.CATEGORY_FILE_ID,iwc,Integer.toString(groupId),file.getPrimaryKey().toString(),AccessControl.PERMISSION_KEY_OWNER,Boolean.TRUE);
  }
  
	public void setAsOwner(Group group, int groupId, IWUserContext iwc)throws Exception {
		setPermission(AccessController.CATEGORY_GROUP_ID,iwc,Integer.toString(groupId),group.getPrimaryKey().toString(),AccessControl.PERMISSION_KEY_OWNER,Boolean.TRUE);
	}

  /**
   * @todo implement setAsOwner(ICObject obj, int entityRecordId, IWUserContext iwc)throws Exception
   */
  public void setAsOwner(ICObject obj, int entityRecordId, int groupId, IWUserContext iwc)throws Exception {
    throw new Exception(this.getClass().getName()+".setAsOwner(...) : not implemented");
  }




  public static void copyObjectInstancePermissions( String idToCopyFrom, String idToCopyTo) throws SQLException{
    copyPermissions(AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID,idToCopyFrom,idToCopyTo);
  }

  public static void copyPagePermissions( String idToCopyFrom, String idToCopyTo) throws SQLException{
    copyPermissions(AccessController.CATEGORY_STRING_PAGE_ID,idToCopyFrom,idToCopyTo);
  }

  public static List getGroupsPermissions(String category, GenericGroup group, Set identifiers) throws SQLException{
    ICPermission permission = com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance();
    List permissions = null;
    String instanceIds = "";
    if(identifiers != null){
      Iterator iter = identifiers.iterator();
      boolean first = true;
      while (iter.hasNext()) {
        if(!first){
          instanceIds += ",";
        }
        instanceIds += "'"+(String)iter.next()+"'";
        first = false;
      }
    }
    String SQLString = null;
    if(!instanceIds.equals("")){
      StringBuffer buffer = new StringBuffer();
      buffer.append("SELECT * FROM ");
      buffer.append(permission.getEntityName());
      buffer.append(" WHERE ");
      buffer.append(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName());
      buffer.append(" = '");
      buffer.append(category);
      buffer.append("' AND ");
      buffer.append(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName());
      if(identifiers.size() > 1){
        buffer.append(" in(");
        buffer.append(instanceIds);
        buffer.append(")");
      } else {
        buffer.append(" = '");
        buffer.append(instanceIds);
        buffer.append("'");
      }
      buffer.append(" AND ");
      buffer.append(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName());
      buffer.append(" = ");
      buffer.append(group.getID());


      SQLString = buffer.toString();

      if(SQLString != null){
        permissions = EntityFinder.findAll(permission,SQLString);
      }

      //permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName() + " = '" + category + "' AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName() + " in(" + instanceIds + ") AND " + com.idega.core.accesscontrol.data.ICPermissionBMPBean.getGroupIDColumnName() + " = " + group.getID());

    }
    //System.err.println(SQLString);
    //System.err.println(" = " + permissions);
    return permissions;
  }

  public static List getGroupsPermissionsForInstances(GenericGroup group, Set instances) throws SQLException{
    return getGroupsPermissions(AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID,group, instances);
  }

  public static List getGroupsPermissionsForPages(GenericGroup group, Set instances) throws SQLException{
    return getGroupsPermissions(AccessController.CATEGORY_STRING_PAGE_ID,group, instances);
  }

  public static boolean replicatePermissionForNewGroup(ICPermission permission, GenericGroup group){
    try {
      ICPermission p = ((com.idega.core.accesscontrol.data.ICPermissionHome)com.idega.data.IDOLookup.getHome(ICPermission.class)).create();

      String s = permission.getContextType();
      if(s != null){
        p.setContextType(s);
      }

      String s2 = permission.getContextValue();
      if(s2 != null){
        p.setContextValue(s2);
      }

      String s3 = permission.getPermissionString();
      if(s3 != null){
        p.setPermissionString(s3);
      }

     /* String s4 = permission.getPermissionStringValue();
      if(s4 != null){
        p.setPermissionStringValue(s4);
      }
			*/
			
      p.setPermissionValue(permission.getPermissionValue());

      // groupID changes
      p.setGroupID(group.getID());

      p.store();

      //PermissionCacher.updatePermissions(,p.getContextValue(),permissionType,iwc);
      return true;
    }
    catch (Exception ex) {
    	ex.printStackTrace();
      System.err.println("AccessControl.replicatePermissionForNewGroup(..) did not succeed");
      return false;
    }

  }

  public static void copyPermissions( String contextType, String identifierToCopyFrom, String identifierToCopyTo) throws SQLException{
    List permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),contextType,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifierToCopyFrom);
    if(permissions != null){
      Iterator iter = permissions.iterator();
      while (iter.hasNext()) {
        ICPermission item = (ICPermission)iter.next();
        ICPermission perm = (ICPermission) ((ICPermissionHome)com.idega.data.IDOLookup.getHomeLegacy(ICPermission.class)).createLegacy();
        perm.setContextType(contextType);
        perm.setContextValue(identifierToCopyTo);
        perm.setGroupID(item.getGroupID());
        String str = item.getPermissionString();
        if(str != null){
          perm.setPermissionString(str);
        }

        /*String str2 = item.getPermissionStringValue();
        if(str2 != null){
          perm.setPermissionStringValue(str2);
        }*/
        
        
        perm.setPermissionValue(item.getPermissionValue());

        perm.store();
      }
    }
  }

  public boolean hasEditPermissionFor(Group group,IWUserContext iwuc){
		try{
    
			return this.hasPermission(AccessController.PERMISSION_KEY_EDIT, group, iwuc);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
    
  }

  public boolean hasViewPermissionFor(Group group,IWUserContext iwuc){
    try{
    
    	return this.hasPermission(AccessController.PERMISSION_KEY_VIEW, group, iwuc);
    }
    catch(Exception e){
    	e.printStackTrace();
    	return false;
    }
    
  }
  
	public boolean hasCreatePermissionFor(Group group,IWUserContext iwuc){
		try{
    
			return this.hasPermission(AccessController.PERMISSION_KEY_CREATE, group, iwuc);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
    
	}
	
	public boolean hasDeletePermissionFor(Group group,IWUserContext iwuc){
		try{
    
			return this.hasPermission(AccessController.PERMISSION_KEY_DELETE, group, iwuc);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
    
	}


  public void addEditPermissionFor(Group group,IWUserContext iwuc){
    /**
     * @todo: Implement
     */
  }

  public void revokeEditPermissionFor(Group group,IWUserContext iwuc){
    /**
     * @todo: Implement
     */
  }


  public void addViewPermissionFor(Group group,IWUserContext iwuc){
    /**
     * @todo: Implement
     */
  }

  public void revokeViewPermissionFor(Group group,IWUserContext iwuc){
    /**
     * @todo: Implement
     */
  }

  private static PermissionGroupHome getPermissionGroupHome()throws RemoteException{
    return (PermissionGroupHome)IDOLookup.getHome(PermissionGroup.class);
  }

  private static ICPermissionHome getPermissionHome()throws RemoteException{
    return (ICPermissionHome)IDOLookup.getHome(ICPermission.class);
  }


//Methods added after big changes by Eiki
	public static Collection getAllPermissions(Group group, String contextType){
		Collection returnCol = new Vector();//empty
		try{
			returnCol = getPermissionHome().findAllPermissionsByTypeAndPermissionGroupOrderedByContextValue(contextType,group);
		}
		catch(FinderException ex){
			ex.printStackTrace();
		}
		catch(RemoteException x){
			x.printStackTrace();
		}
		
		return returnCol;
		
	}

	public static Collection getAllGroupPermissionsForGroup(Group group){
		return getAllPermissions(group,AccessControl.CATEGORY_STRING_GROUP_ID);
	}
	
	public static Collection getAllPermissionsOwnedByGroup(Group group, String contextType){
		Collection returnCol =  new Vector();//empty
		try{
			returnCol = getPermissionHome().findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(group,AccessControl.PERMISSION_KEY_OWNER,contextType);
		}
		catch(FinderException ex){
			ex.printStackTrace();
		}
		catch(RemoteException x){
			x.printStackTrace();
		}
		
		return returnCol;
		
	}

	public static Collection getAllPermissionsForContextTypeAndContextValue(String contextType,String contextValue){
		Collection returnCol = new Vector();//empty
		try{
			returnCol = getPermissionHome().findAllPermissionsByTypeAndContextValue(contextType,contextValue);
		}
		catch(FinderException ex){
			ex.printStackTrace();
		}
		catch(RemoteException x){
			x.printStackTrace();
		}
		
		return returnCol;
	}
	
	public static Collection getAllGroupPermissionsReverseForGroup(Group group){
		return getAllPermissionsForContextTypeAndContextValue( AccessControl.CATEGORY_STRING_GROUP_ID, group.getPrimaryKey().toString());
	}
	
	
	/**
	 * Gets a list of all permissions associtated with the supplied group and permission string (type e.g. view,edit,owner etc)
	 * @param group the group that you want to see what permissions have been set to. 
	 * @param permissionString the type
	 * @return
	 */
	public static Collection getAllGroupPermissionsReverseForGroupAndPermissionString(Group group, String permissionString){
		Collection returnCol = new Vector();//empty
		try{
			returnCol = getPermissionHome().findAllPermissionsByTypeAndContextValueAndPermissionString(AccessControl.CATEGORY_STRING_GROUP_ID,group.getPrimaryKey().toString(),permissionString);
		}
		catch(FinderException ex){
			ex.printStackTrace();
		}
		catch(RemoteException x){
			x.printStackTrace();
		}
		
		return returnCol;
	}
	
	public static Collection getAllOwnerGroupPermissionsReverseForGroup(Group group){
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group,AccessControl.PERMISSION_KEY_OWNER);
	}
	
	public static Collection getAllEditGroupPermissionsReverseForGroup(Group group){
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group,AccessControl.PERMISSION_KEY_EDIT);
	}
	
	public static Collection getAllViewGroupPermissionsReverseForGroup(Group group){
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group,AccessControl.PERMISSION_KEY_VIEW);
	}
	
	public static Collection getAllDeleteGroupPermissionsReverseForGroup(Group group){
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group,AccessControl.PERMISSION_KEY_DELETE);
	}
	
	public static Collection getAllCreateGroupPermissionsReverseForGroup(Group group){
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group,AccessControl.PERMISSION_KEY_CREATE);
	}
	
	
	public static Collection getAllGroupPermissionsOwnedByGroup(Group group){
		Collection returnCol =  new Vector();//empty
		try{
			returnCol = getPermissionHome().findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(group,AccessControl.PERMISSION_KEY_OWNER,AccessControl.CATEGORY_STRING_GROUP_ID);
		}
		catch(FinderException ex){
			ex.printStackTrace();
		}
		catch(RemoteException x){
			x.printStackTrace();
		}
		
		return returnCol;
		 
	}
	

	public static Collection getAllGroupViewPermissions(Group group){
			Collection returnCol = null;
			try{
				returnCol = getPermissionHome().findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(group,AccessControl.PERMISSION_KEY_VIEW,AccessControl.CATEGORY_STRING_GROUP_ID);
			}
			catch(FinderException ex){
				returnCol =  new Vector();//empty
			}
			catch(RemoteException x){
				x.printStackTrace();
				returnCol =  new Vector();//empty
			}
		
			return returnCol;
		 
		}
		
		public static Collection getAllGroupDeletePermissions(Group group){
				Collection returnCol = null;
				try{
					returnCol = getPermissionHome().findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(group,AccessControl.PERMISSION_KEY_DELETE,AccessControl.CATEGORY_STRING_GROUP_ID);
				}
				catch(FinderException ex){
					returnCol =  new Vector();//empty
				}
				catch(RemoteException x){
					x.printStackTrace();
					returnCol =  new Vector();//empty
				}
		
				return returnCol;
		 
			}
	
    public static Collection getAllGroupEditPermissions(Group group){
        Collection returnCol = null;
        try{
          returnCol = getPermissionHome().findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(group,AccessControl.PERMISSION_KEY_EDIT,AccessControl.CATEGORY_STRING_GROUP_ID);
        }
        catch(FinderException ex){
          returnCol =  new Vector();//empty
        }
        catch(RemoteException x){
          x.printStackTrace();
          returnCol =  new Vector();//empty
        }
    
        return returnCol;
     
      }
      
		public static Collection getAllGroupCreatePermissions(Group group){
				Collection returnCol = null;
				try{
					returnCol = getPermissionHome().findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(group,AccessControl.PERMISSION_KEY_CREATE,AccessControl.CATEGORY_STRING_GROUP_ID);
				}
				catch(FinderException ex){
					returnCol =  new Vector();//empty
				}
				catch(RemoteException x){
					x.printStackTrace();
					returnCol =  new Vector();//empty
				}
    
				return returnCol;
     
			}

		/**
		 * @param groups
		 * @return all ICPermissions owned by these groups
		 */
		public static Collection getAllGroupEditPermissions(Collection groups) {
			Collection returnCol = null;
			 try{
				 returnCol = getPermissionHome().findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndTypeOrderedByContextValue(groups,AccessControl.PERMISSION_KEY_EDIT,AccessControl.CATEGORY_STRING_GROUP_ID);
			 }
			 catch(FinderException ex){
				 returnCol =  new Vector();//empty
			 }
			 catch(RemoteException x){
				 x.printStackTrace();
				 returnCol =  new Vector();//empty
			 }
			 return returnCol;
		}
		
		/**
		 * @param groups
		 * @return all ICPermissions owned by these groups
		 */
		public static Collection getAllGroupViewPermissions(Collection groups) {
			Collection returnCol = null;
			 try{
				 returnCol = getPermissionHome().findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndTypeOrderedByContextValue(groups,AccessControl.PERMISSION_KEY_VIEW,AccessControl.CATEGORY_STRING_GROUP_ID);
			 }
			 catch(FinderException ex){
				 returnCol =  new Vector();//empty
			 }
			 catch(RemoteException x){
				 x.printStackTrace();
				 returnCol =  new Vector();//empty
			 }
			 return returnCol;
		}
		
		/**
		 * @param groups
		 * @return all ICPermissions owned by these groups
		 */
		public static Collection getAllGroupCreatePermissions(Collection groups) {
			Collection returnCol = null;
			 try{
				 returnCol = getPermissionHome().findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndTypeOrderedByContextValue(groups,AccessControl.PERMISSION_KEY_CREATE,AccessControl.CATEGORY_STRING_GROUP_ID);
			 }
			 catch(FinderException ex){
				 returnCol =  new Vector();//empty
			 }
			 catch(RemoteException x){
				 x.printStackTrace();
				 returnCol =  new Vector();//empty
			 }
			 return returnCol;
		}
		
		/**
		 * @param groups
		 * @return all ICPermissions owned by these groups
		 */
		public static Collection getAllGroupDeletePermissions(Collection groups) {
			Collection returnCol = null;
			 try{
				 returnCol = getPermissionHome().findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndTypeOrderedByContextValue(groups,AccessControl.PERMISSION_KEY_DELETE,AccessControl.CATEGORY_STRING_GROUP_ID);
			 }
			 catch(FinderException ex){
				 returnCol =  new Vector();//empty
			 }
			 catch(RemoteException x){
				 x.printStackTrace();
				 returnCol =  new Vector();//empty
			 }
			 return returnCol;
		}

} // Class AccessControl
