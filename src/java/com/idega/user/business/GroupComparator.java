package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.data.GenericGroup;
import com.idega.util.GenericGroupComparator;

/**
 * Description: A comparator for all "sorts" of groups that implement GenericGroup. Mainly used to order groups by name and locale.<br>
 * It can handle can handle comparing (groupA,groupB) and also (collectionOfGroupA,CollectionOfGroupB);
 * Copyright:    Copyright (c) 2003
 * Company:      Idega Software
 * @author       <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 */

public class GroupComparator extends GenericGroupComparator{

  private GroupBusiness groupBiz;
  private boolean areICPermissions = false;
  private Map cachedGroups = new HashMap();

	public GroupComparator(Locale locale) {
		super(locale);
	}

  public int compare(Object groupAOrCollectionOfGroupA, Object groupBOrCollectionOfGroupB) {
    if( areICPermissions){
    	return compareICPermissionCollections(groupAOrCollectionOfGroupA,groupBOrCollectionOfGroupB);
    }
    else if( groupAOrCollectionOfGroupA instanceof Collection ){
    	((Collection)groupAOrCollectionOfGroupA).iterator().next() ;
    	
    	return super.compare(((Collection)groupAOrCollectionOfGroupA).iterator().next(),((Collection)groupBOrCollectionOfGroupB).iterator().next());
    }
    else{
    	return super.compare(groupAOrCollectionOfGroupA,groupBOrCollectionOfGroupB);
    }
  }
  
  
	/**
	 * Compares a collection of ICPermissions. It gets the group from GroupBusiness.getGroupByGroupId( ICPermission.getContextValue())
	 * @param groupAOrCollectionOfGroupA
	 * @param groupBOrCollectionOfGroupB
	 * @return
	 */
	private int compareICPermissionCollections(Object permissionCollectionA, Object permissionCollectionB) {
		GenericGroup groupA = null;
		GenericGroup groupB = null;
		try {
			groupA = checkForCachedGroups(permissionCollectionA);
			groupB = checkForCachedGroups(permissionCollectionB);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		return super.compare(groupA,groupB);
	}

	/**
	 * The compatator checks if the two groups being compared have already been loaded
	 * and then gets them from a HashMap instead of loading them from the database 
	 * Optimization done by Sigtryggur 6.7.2004  
	 * @param permissionCollection
	 * @return group
	 */
	private GenericGroup checkForCachedGroups(Object permissionCollection) throws FinderException, RemoteException {
		GenericGroup group = null;
		String groupID = ((ICPermission) ((Collection) permissionCollection).iterator().next()).getContextValue();
		if (cachedGroups.containsKey(groupID)) {
			group = (GenericGroup)cachedGroups.get(groupID);
		}
		else
		{	
			group = (GenericGroup) groupBiz.getGroupByGroupID(Integer.parseInt(groupID));
			cachedGroups.put(groupID, group);
		}
		return group;
	}

	/**
	 * Tells the comparator to check inside the collections and get the group from the ICPermission bean. 
	 * It is mandatory to set the GroupBusiness also if this is set to true.
	 * @param arePermission
	 */
	public void setObjectsAreICPermissions(boolean arePermission){
		this.areICPermissions = arePermission;
	}
	
	/**
	 * Is mandatory only if setObjectsAreICPermissions(true).
	 */
	public void setGroupBusiness(GroupBusiness groupBiz){
		this.groupBiz = groupBiz;
	}
      
  

}
