package com.idega.user.business;

import java.rmi.RemoteException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.presentation.IWContext;
import com.idega.user.data.CachedGroup;
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
  private static String CACHE_PARENTS_APPLICATION_ATTRIBUTE = "CACHE_PARENTS";
  private static String CACHE_GROUPS_APPLICATION_ATTRIBUTE = "CACHE_GROUPS";
  private Map applicationCachedGroups = null;
  private Map applicationCachedParents = null;
  private Map cachedGroups = new HashMap();
  private Map cachedParentsRecursiveAndCurrentGroup = new HashMap();

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
	    CachedGroup cachedGroupA = null;
	    CachedGroup cachedGroupB = null;
		try {
		    applicationCachedGroups = (Map)_iwc.getApplicationContext().getApplicationAttribute(CACHE_GROUPS_APPLICATION_ATTRIBUTE);
		    if(applicationCachedGroups == null){
		        applicationCachedGroups = new HashMap();
		        _iwc.getApplicationContext().setApplicationAttribute(CACHE_GROUPS_APPLICATION_ATTRIBUTE, applicationCachedGroups);
		    }

		    cachedGroupA = checkForCachedGroups(permissionCollectionA);
			cachedGroupB = checkForCachedGroups(permissionCollectionB);
			if (sortByParents) {
			     applicationCachedParents= (Map)_iwc.getApplicationContext().getApplicationAttribute(CACHE_PARENTS_APPLICATION_ATTRIBUTE);
			    if(applicationCachedParents == null){
			        applicationCachedParents = new HashMap();
			        _iwc.getApplicationContext().setApplicationAttribute(CACHE_PARENTS_APPLICATION_ATTRIBUTE, applicationCachedParents);
			    }
			    
			    Collection parentsA = getParentGroupsRecursive(cachedGroupA);
			    Collection parentsB = getParentGroupsRecursive(cachedGroupB);
			    comp = compareRecursive(parentsA, parentsB);
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
		    Collator collator = Collator.getInstance(_iwc.getCurrentLocale());
		    comp = collator.compare(cachedGroupA.getName(), cachedGroupB.getName()); 
		}
		return comp;
	}

	/**
     * @param cachedGroup
     * @return
     * @throws RemoteException
     */
    private Collection getParentGroupsRecursive(CachedGroup cachedGroup) throws RemoteException, FinderException {
        Collection parentGroupsRecursive = null;
		if (cachedParentsRecursiveAndCurrentGroup!=null) {
		    String key = cachedGroup.getPrimaryKey().toString();
			if (cachedParentsRecursiveAndCurrentGroup.containsKey(key))
			    parentGroupsRecursive = (Collection)cachedParentsRecursiveAndCurrentGroup.get(key);
			else
			{	
			    parentGroupsRecursive = getParentsRecursiveAndCurrentGroup(cachedGroup);
			    cachedParentsRecursiveAndCurrentGroup.put(key, parentGroupsRecursive);
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
		    parentGroupsRecursive = getParentsRecursiveAndCurrentGroup(cachedGroup);
		}
        return parentGroupsRecursive;
    }

    /**
     * @param cachedGroup
     * @return
     * @throws RemoteException
     */
    private Collection getParentsRecursiveAndCurrentGroup(CachedGroup cachedGroup) throws RemoteException, FinderException {
        Collection parentGroupsRecursive = getParentGroupsRecursive(cachedGroup.getPrimaryKey());
        
        if (parentGroupsRecursive!=null) {
            int collSize = parentGroupsRecursive.size();
            List tempCollection = new ArrayList();
            tempCollection.addAll(parentGroupsRecursive);
            parentGroupsRecursive.clear();
            for (int i = collSize-1; i >=0 ;i--)
                parentGroupsRecursive.add(tempCollection.get(i));
        	
        }
        else {
            parentGroupsRecursive = new ArrayList();
        }
        parentGroupsRecursive.add(cachedGroup);
        return parentGroupsRecursive;
    }

    private Collection getParentGroupsRecursive(Integer groupId) throws RemoteException, FinderException {
        Collection parentGroupsRecursive = new ArrayList();
        getParentGroupsRecursive(parentGroupsRecursive, groupId);
        return parentGroupsRecursive;
    }

    private void getParentGroupsRecursive(Collection parentsRecursive, Integer groupId) throws RemoteException, FinderException {
        Group group = null;
        Group parent = null;
        if (applicationCachedParents.containsKey(groupId.toString())) {
            Collection col = (Collection)applicationCachedParents.get(groupId.toString());
            Iterator it = col.iterator();
            Integer parentId = null;
            CachedGroup cachedParentGroup = null;
            
            if (it.hasNext()) {
                 parentId = (Integer)it.next();
                 String key = parentId.toString();
                 if (applicationCachedGroups.containsKey(key)) {
                     cachedParentGroup = (CachedGroup)applicationCachedGroups.get(key); 
                 }
                 else if (cachedGroups.containsKey(key)) {
                     parent = (Group)cachedGroups.get(key);
                     cachedParentGroup = new CachedGroup(parent);
                     applicationCachedGroups.put(key, cachedParentGroup);
                 }
                 else {
                     parent = (Group) groupBiz.getGroupByGroupID(parentId.intValue());
	                 cachedParentGroup = new CachedGroup(parent);
	                 cachedGroups.put(key,parent);
	                 applicationCachedGroups.put(key, cachedParentGroup);
                 }
                 parentsRecursive.add(cachedParentGroup);
                 getParentGroupsRecursive(parentsRecursive, cachedParentGroup.getPrimaryKey());
            }       
        }
        else {
            String key = groupId.toString();
            if (cachedGroups.containsKey(key)) {
                group = (Group)cachedGroups.get(key);
                CachedGroup cachedGroup = new CachedGroup(group);
	            applicationCachedGroups.put(key, cachedGroup);
            }
            else {
	            group = (Group) groupBiz.getGroupByGroupID(groupId.intValue());
	            CachedGroup cachedGroup = new CachedGroup(group);
	            cachedGroups.put(key, group);
	            applicationCachedGroups.put(key, cachedGroup);
            }
            Collection parents = group.getParentGroups(applicationCachedParents, cachedGroups);        
            Iterator parIt = parents.iterator();
	        if (parIt.hasNext()) {
	            parent = (Group)parIt.next();
	            if (parent!= null) {
	                CachedGroup cachedParentGroup = new CachedGroup(parent);
		            parentsRecursive.add(cachedParentGroup);
		            getParentGroupsRecursive(parentsRecursive, cachedParentGroup.getPrimaryKey());
	            }
	        }
        }
    }

    /**
     * @param comp
     * @param cachedGroupA
     * @param cachedGroupB
     * @return
     */
    private int compareGroupTypes(CachedGroup cachedGroupA, CachedGroup cachedGroupB) {
        int comp = 0;
        Collator collator = Collator.getInstance(_iwc.getCurrentLocale());
        String groupType1 = _iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(_iwc.getCurrentLocale()).getLocalizedString(cachedGroupA.getGroupType());
        String groupType2 = _iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(_iwc.getCurrentLocale()).getLocalizedString(cachedGroupA.getGroupType());
        
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
    private int compareRecursive(Collection parentsA, Collection parentsB) {
        int comp = 0;
        if ((parentsA == null || parentsA.isEmpty()) && (parentsB != null && !parentsB.isEmpty())) {
            comp = -1;
        }
        else if ((parentsA != null && !parentsA.isEmpty()) && (parentsB == null || parentsB.isEmpty())) {
            comp = 1;
        }
        else if ((parentsA != null && !parentsA.isEmpty()) && (parentsB != null && !parentsB.isEmpty())){
	        CachedGroup cachedParentGroupA = null;
	        CachedGroup cachedParentGroupB = null;
	        Iterator parAIt = parentsA.iterator();
	        Iterator parBIt = parentsB.iterator();
	        while (comp == 0) {
	            if (parAIt.hasNext() && parBIt.hasNext()) {
	                cachedParentGroupA = (CachedGroup)parAIt.next();
	                cachedParentGroupB = (CachedGroup)parBIt.next();
		            comp = compareGroupTypes(cachedParentGroupA, cachedParentGroupB);
		            if (comp == 0){
		                Collator collator = Collator.getInstance(_iwc.getCurrentLocale());
		                comp = collator.compare(cachedParentGroupA.getName(), cachedParentGroupB.getName());
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
	private CachedGroup checkForCachedGroups(Object permissionCollection) throws FinderException, RemoteException {
	    CachedGroup cachedGroup = null;
	    Group group = null;
	    String groupId = ((ICPermission) ((Collection) permissionCollection).iterator().next()).getContextValue();
		String key = groupId;
	    if (applicationCachedGroups.containsKey(key)) {
		    cachedGroup = (CachedGroup)applicationCachedGroups.get(key);
		}
		else if (cachedGroups.containsKey(key)) {
		    group = (Group)cachedGroups.get(groupId);
            cachedGroup = new CachedGroup(group);
            applicationCachedGroups.put(key, cachedGroup);
        }
		else
		{	
			group = groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
			cachedGroup = new CachedGroup(group);
			cachedGroups.put(key, group);
			applicationCachedGroups.put(key, cachedGroup);
		}
		return cachedGroup;
	}

	public String getIndentedGroupName(CachedGroup cachedGroup) {
	    int indent = 0;
	    String indentString = "";
	    Object obj = getCachedParentsRecursiveAndCurrentGroup().get(cachedGroup.getPrimaryKey().toString());
		if (obj != null) {
		    indent = ((Collection)obj).size();
		}
		indent = indent - getTopmostParentLevel();
		for (int i=0;i<indent;i++)
			indentString = "&middot;&nbsp;" + indentString;
		indentString = indentString + cachedGroup.getName();
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

	public Map getApplicationCachedGroups() {
	    return applicationCachedGroups;
	}

	public Map getCachedParentsRecursiveAndCurrentGroup() {
	    return cachedParentsRecursiveAndCurrentGroup;
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
