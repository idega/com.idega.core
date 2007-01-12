/*
 * $Id: ParentGroupsRecursiveProcedure.java,v 1.1.2.1 2007/01/12 19:32:13 idegaweb Exp $
 * Created on 1.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.data.DatastoreInterface;
import com.idega.data.GenericProcedure;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.MSSQLServerDatastoreInterface;
import com.idega.util.ListUtil;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:13 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">gummi</a>
 * @version $Revision: 1.1.2.1 $
 */
public class ParentGroupsRecursiveProcedure extends GenericProcedure {

	private static Class[] parameterType = new Class[] {Integer.class};
	private String[] _groupTypes = null;
	private boolean _returnSpecifiedGroupTypes = true;
	private Group _gr = null;
	//private boolean _usedFallBackProcedure = false;
	
	
	/**
	 * 
	 */
	private ParentGroupsRecursiveProcedure() {
		super();
	}
	
	public static ParentGroupsRecursiveProcedure getInstance(){
		return new ParentGroupsRecursiveProcedure();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#getName()
	 */
	public String getName() {
		return "get_ic_parent_groups_recursive";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#getReturnType()
	 */
	public Class getReturnType() {
		return Collection.class;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#getParameterTypes()
	 */
	public Class[] getParameterTypes() {
		return parameterType;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#getIDOEntityInterfaceClass()
	 */
	public Class getIDOEntityInterfaceClass() {
		return Group.class;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#executeProcedure()
	 */
	protected Object executeProcedure(Object[] parameters) throws Exception {
		return executeFindProsedure(parameters);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#executeFallBackProcedure()
	 */
	protected Object executeFallBackProcedure(Object[] parameters) throws Exception {
		//_usedFallBackProcedure=true;
		return getParentGroupsRecursive(parameters[0],this._groupTypes,this._returnSpecifiedGroupTypes,new HashMap(),new HashMap()); //Using temporary local variables set in the method findParentGroupsRecursive and there again 
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#getCreateProcedureScript(com.idega.data.DatastoreInterface)
	 */
	protected String getCreateProcedureScript(DatastoreInterface i) {
//		CREATE PROCEDURE get_ic_parent_groups_recursive (@current Integer) as  --This is a non-recursive preorder traversal.
//		 SET NOCOUNT ON
//		 DECLARE @lvl int
//		 
//		 CREATE TABLE #stack (item Integer, lvl int)   --Create a tempory stack.
//		 CREATE TABLE #result (parent_ic_group_id int)			--Create a result stack.
//		 INSERT INTO #stack VALUES (@current, 1)        --Insert current node to the stack.
//		 SELECT @lvl = 1                                
//		 WHILE @lvl > 0                                 --From the top level going down.
//		        BEGIN
//		            IF EXISTS (SELECT * FROM #stack WHERE lvl = @lvl)
//		                BEGIN
//		                    SELECT @current = item      --Find the first node that matches current node's name.
//		                    FROM #stack
//		                    WHERE lvl = @lvl
//
//							IF NOT EXISTS (SELECT * FROM #result where parent_ic_group_id = @current)
//								BEGIN
//		                    
//		                    		insert #result values (@current)		--Insert current into result
//
//		                    		DELETE FROM #stack
//		                    		WHERE lvl = @lvl
//		                        		AND item = @current     --Remove the current node from the stack.
//
//		                    		INSERT #stack               --Insert the childnodes of the current node into the stack.
//		                        		SELECT IC_GROUP_ID, @lvl + 1 
//		                        		FROM IC_GROUP_RELATION 
//		                        		WHERE RELATED_IC_GROUP_ID=@current 
//		                        		AND (RELATIONSHIP_TYPE='GROUP_PARENT' OR RELATIONSHIP_TYPE IS NULL) 
//		                        		AND ( GROUP_RELATION_STATUS='ST_ACTIVE' OR GROUP_RELATION_STATUS='PASS_PEND' ) 
//
//		                    		IF @@ROWCOUNT > 0           --If the previous statement added one or more nodes, go down for its first child.
//		                        		SELECT @lvl = @lvl + 1  --If no nodes are added, check its brother nodes.
//		                		END
//		                	ELSE
//		                		SELECT @lvl = @lvl - 1          --Back to the level immediately above.
//		                END
//		            ELSE
//		                SELECT @lvl = @lvl - 1          --Back to the level immediately above.
//		        
//		END                                             --While
//		SELECT * FROM #result
//		DROP TABLE #stack
//		DROP TABLE #result
//		SET NOCOUNT OFF
		return null;
	}
	
	public synchronized Collection findParentGroupsRecursive(Group gr, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		this._groupTypes = groupTypes;
		this._returnSpecifiedGroupTypes = returnSpecifiedGroupTypes;
		this._gr = gr;
		
		Collection c;
		try {
			c = (Collection) getResult(new Object[] {gr.getPrimaryKey()});
		}
		catch (Exception e) {
			throw new EJBException(e);
		}
		List specifiedGroups = new ArrayList();
		List notSpecifiedGroups = new ArrayList();
		int j = 0;
		int k = 0;
		Iterator iter2 = c.iterator();
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
			notSpecifiedGroups.remove(gr);
			specifiedGroups.remove(gr);
		} else {
			while (iter2.hasNext()) {
				Group tempObj = (Group)iter2.next();
				notSpecifiedGroups.add(j++, tempObj);
			}
			notSpecifiedGroups.remove(gr);
			returnSpecifiedGroupTypes = false;
		}
		
		this._groupTypes=null;
		this._returnSpecifiedGroupTypes=true;
		this._gr=null;
		//_usedFallBackProcedure = false;
		
		return (returnSpecifiedGroupTypes) ? specifiedGroups : notSpecifiedGroups;
		
		
	}

	
	
	/**
	 * Optimized version of getParentGroupsRecursive(Group,String[],boolean) by Sigtryggur 22.06.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	  private  Collection getParentGroupsRecursive(Object grPK, String[] groupTypes, boolean returnSpecifiedGroupTypes, Map cachedParents, Map cachedGroups) throws EJBException{  	
	  //public  Collection getGroupsContaining(Group groupContained, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws EJBException,RemoteException{

	  	Group aGroup;
		try {
			aGroup = (Group)IDOLookup.findByPrimaryKey(Group.class,(Integer)grPK);
		}
		catch (IDOLookupException e) {
			throw new EJBException(e);
		}
		catch (FinderException e) {
			throw new EJBException(e);
		}
		Collection groups = aGroup.getParentGroups(cachedParents, cachedGroups);
		
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
		      		putGroupsContaining( item, GroupsContained,groupTypes, returnSpecifiedGroupTypes, cachedParents, cachedGroups );
		    	}
		   	}
		  }
		  
		 return GroupsContained.values();
			
			/////REMOVE AFTER IMPLEMENTING PUTGROUPSCONTAINED BETTER
		  
		}else{
		  return ListUtil.getEmptyList();
		}
	  }
	  
	/**
	 * Optimized version of putGroupsContaining(Group, Map, String[], boolean) by Sigtryggur 22.06.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	  private void putGroupsContaining(Group group, Map GroupsContained , String[] groupTypes, boolean returnGroupTypes, Map cachedParents, Map cachedGroups ) {
	  	Collection pGroups = null;
	  	if (cachedParents == null) {
			pGroups = group.getParentGroups();//TODO EIKI FINISH THIS groupTypes,returnGroupTypes);
		}
		else {
			pGroups = group.getParentGroups(cachedParents, cachedGroups);
		}
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

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#processResultSet(java.sql.ResultSet)
	 */
	public Object processResultSet(ResultSet rs) {
		Collection c = new ArrayList();
		if (rs != null ){
			try {
				while(rs.next()) {
					c.add(rs.getObject(1));
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#useStoredProcedure(com.idega.data.DatastoreInterface)
	 */
	protected boolean isSupportedForDatabase(DatastoreInterface i) {
		return (i instanceof MSSQLServerDatastoreInterface);
	}

	  
}
