package com.idega.user.business;

import java.rmi.RemoteException;
import java.text.Collator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
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
  private boolean sortByParents = false;
  private int topmostParentLevel = -1;
  private Map cachedGroups = new HashMap();
  private Map cachedParents = new HashMap();
  private Map cachedCollectionsOfParentsRecursivelyPlusCurrentGroup = new HashMap();

	public GroupComparator(IWContext iwc) {
		super(iwc);
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
	    int comp = 0;
	    Group groupA = null;
		Group groupB = null;
		try {
			groupA = checkForCachedGroups(permissionCollectionA);
			groupB = checkForCachedGroups(permissionCollectionB);
			if (sortByParents) {
			    Collection parentsA = getParentGroupsRecursively(groupA);
			    Collection parentsB = getParentGroupsRecursively(groupB);
			    comp = compareRecursively(parentsA, parentsB);
			}
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
		if (comp == 0){
		    comp = super.compare(groupA,groupB);
		}
		return comp;
	}

	/**
     * @param groupA
     * @return
     * @throws RemoteException
     */
    private Collection getParentGroupsRecursively(Group group) throws RemoteException {
        Collection parentGroupsRecursive = null;
		if (cachedCollectionsOfParentsRecursivelyPlusCurrentGroup!=null) {
			if (cachedCollectionsOfParentsRecursivelyPlusCurrentGroup.containsKey(group.getPrimaryKey()))
			    parentGroupsRecursive = (Collection)cachedCollectionsOfParentsRecursivelyPlusCurrentGroup.get(group.getPrimaryKey());
			else
			{	
			    parentGroupsRecursive = groupBiz.getParentGroupsRecursive(group,cachedParents,cachedGroups);;
			    if (parentGroupsRecursive!=null) {
			        parentGroupsRecursive.add(group);
			    }
			    cachedCollectionsOfParentsRecursivelyPlusCurrentGroup.put(group.getPrimaryKey(), parentGroupsRecursive);
				if (parentGroupsRecursive == null) {
				    setTopmostParentLevel(0);
				}
				else {
			    	if (topmostParentLevel == -1 || topmostParentLevel > parentGroupsRecursive.size()) {
				        setTopmostParentLevel(parentGroupsRecursive.size());
				    }
				}	
			}
		}
		else {
		    parentGroupsRecursive = groupBiz.getParentGroupsRecursive(group,cachedParents,cachedGroups);
		    if (parentGroupsRecursive!=null) {
		        parentGroupsRecursive.add(group);
		    }
		}
        return parentGroupsRecursive;
    }

    /**
     * @param comp
     * @param groupA
     * @param groupB
     * @return
     */
    private int compareGroupTypes(Group groupA, Group groupB) {
        int comp = 0;
        Collator collator = Collator.getInstance(_iwc.getCurrentLocale());
        String groupType1 = _iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(_iwc.getCurrentLocale()).getLocalizedString(groupA.getGroupType());
        String groupType2 = _iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(_iwc.getCurrentLocale()).getLocalizedString(groupB.getGroupType());
        
        if (groupType1 != null && groupType2 == null) {
            comp = -1;
        } else if (groupType1 == null && groupType2 != null) {
            comp = 1;
        } else if (groupType1 != null && groupType2 != null) { 
            comp = collator.compare(groupType1, groupType2);
        }
        return comp;
    }

    /**
     * @param parentsA
     * @param parentsB
     */
    private int compareRecursively(Collection parentsA, Collection parentsB) {
        int comp = 0;
        if ((parentsA == null || parentsA.isEmpty()) && (parentsB != null && !parentsB.isEmpty())) {
            comp = -1;
        }
        else if ((parentsA != null && !parentsA.isEmpty()) && (parentsB == null || parentsB.isEmpty())) {
            comp = 1;
        }
        else if ((parentsA != null && !parentsA.isEmpty()) && (parentsB != null && !parentsB.isEmpty())){
	        Group parentA = null;
	        Group parentB = null;
	        Iterator parAIt = parentsA.iterator();
	        Iterator parBIt = parentsB.iterator();
	        while (comp == 0) {
	            if (parAIt.hasNext() && parBIt.hasNext()) {
	                parentA = (Group)parAIt.next();
		            parentB = (Group)parBIt.next();
		            comp = compareGroupTypes(parentA, parentB);
		            if (comp == 0){
		                comp = super.compare(parentA,parentB);
		            }
	            }
	            else if (!parAIt.hasNext() && parBIt.hasNext()) {
	                comp = -1;
	            }
	            else if (parAIt.hasNext() && !parBIt.hasNext()) {
	                comp = 1;
	            }
	            else {
	                break;
	            }
	        }
        }
        return comp;
    }

    /**
	 * The compatator checks if the two groups being compared have already been loaded
	 * and then gets them from a HashMap instead of loading them from the database 
	 * Optimization done by Sigtryggur 6.7.2004  
	 * @param permissionCollection
	 * @return group
	 */
	private Group checkForCachedGroups(Object permissionCollection) throws FinderException, RemoteException {
	    Group group = null;
	    String groupID = ((ICPermission) ((Collection) permissionCollection).iterator().next()).getContextValue();
		if (cachedGroups.containsKey(groupID)) {
			group = (Group)cachedGroups.get(groupID);
		}
		else
		{	
			group = (Group) groupBiz.getGroupByGroupID(Integer.parseInt(groupID));
			cachedGroups.put(groupID, group);
		}
		return group;
	}

	public String getIndentString(Integer groupID) {
	    int indent = 0;
	    String indentString = "";
	    Object obj = getCachedCollectionsOfParentsRecursivelyPlusCurrentGroup().get(groupID);
		if (obj != null) {
		    indent = ((Collection)obj).size();
		}
		indent = indent - getTopmostParentLevel();
		for (int i=0;i<indent;i++)
			indentString = "&nbsp;&nbsp;" + indentString;
		return indentString;
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

	/**
	 * 	@param sortByParents The sortByParents to set.
 	*/
	public void setSortByParents(boolean sortByParents) {
	    this.sortByParents = sortByParents;
	}

	public Map getCachedGroups() {
	    return cachedGroups;
	}
      
	public Map getCachedParents() {
	    return cachedParents;
	}

	public Map getCachedCollectionsOfParentsRecursivelyPlusCurrentGroup() {
	    return cachedCollectionsOfParentsRecursivelyPlusCurrentGroup;
	}
	
/**
 * @return Returns the topmostParentLevel.
 */
	public int getTopmostParentLevel() {
    	return topmostParentLevel;
	}
	/**
	 * @param topmostParentLevel The topmostParentLevel to set.
	 */
	public void setTopmostParentLevel(int topmostParentLevel) {
    	this.topmostParentLevel = topmostParentLevel;
	}
  

}
