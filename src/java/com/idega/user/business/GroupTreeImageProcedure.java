/*
 * $Id: GroupTreeImageProcedure.java,v 1.1 2004/09/07 12:50:19 gummi Exp $
 * Created on 5.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.ejb.EJBException;
import com.idega.data.DatastoreInterface;
import com.idega.data.GenericProcedure;
import com.idega.data.IDOProcedure;
import com.idega.data.MSSQLServerDatastoreInterface;
import com.idega.user.data.Group;
import com.idega.util.datastructures.NestedSetNode;
import com.idega.util.datastructures.NestedSetsContainer;


/**
 * 
 *  Last modified: $Date: 2004/09/07 12:50:19 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">gummi</a>
 * @version $Revision: 1.1 $
 */
public class GroupTreeImageProcedure extends GenericProcedure implements IDOProcedure {

	
	private static Class[] parameterType = new Class[] {Integer.class};
	
	/**
	 * 
	 */
	private GroupTreeImageProcedure() {
		super();
	}
	
	public static GroupTreeImageProcedure getInstance(){
		return new GroupTreeImageProcedure();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#getIDOEntityInterfaceClass()
	 */
	public Class getIDOEntityInterfaceClass() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#getCreateProcedureScript(com.idega.data.DatastoreInterface)
	 */
	protected String getCreateProcedureScript(DatastoreInterface i) {
//		"CREATE PROCEDURE get_group_tree (@topNode Integer) as  --This is a non-recursive preorder traversal.
//		 SET NOCOUNT ON
//		 DECLARE @lvl int, @current int, @counter int, @updateitem Integer, @updateitem_index int
//		  
//		 CREATE TABLE #stack (item Integer, lvl int)   --Create a tempory stack.
//		 CREATE TABLE #result (ic_group_id Integer, lft int, rgt int)			--Create a result table.
//		 CREATE TABLE #y_stack(item Integer, stack_order int)
//		 SELECT @lvl = 1
//		 SELECT @current = @topNode
//		 SELECT @counter = 0
//		 SELECT @updateitem_index = 0
//		 INSERT INTO #stack VALUES (@current, 1)        --Insert current node to the stack.
//		                                 
//		 WHILE @lvl > 0                                 --From the top level going down.
//		        BEGIN
//		            IF EXISTS (SELECT * FROM #stack WHERE lvl = @lvl)
//		                BEGIN
//		                    SELECT @current = item      --Find the first node that matches current node's name.
//		                    	FROM #stack
//		                    	WHERE lvl = @lvl
//		
//		                    IF NOT EXISTS (SELECT * FROM #result where ic_group_id = @current)
//					             BEGIN
//		                            SELECT @counter = @counter + 1
//		                    		insert #result values (@current,@counter,-1)		--Insert current into result
//		                            
//		                    		DELETE FROM #stack
//		                                WHERE lvl = @lvl
//		                                AND item = @current     --Remove the current node from the stack.
//		
//		                    		INSERT #stack               --Insert the childnodes of the current node into the stack.
//		                        		SELECT r.RELATED_IC_GROUP_ID, @lvl + 1 
//		                        		FROM IC_GROUP_RELATION r, ic_group g
//		                        		WHERE r.IC_GROUP_ID=@current 
//		                        		AND (r.RELATIONSHIP_TYPE='GROUP_PARENT' OR r.RELATIONSHIP_TYPE IS NULL) 
//		                        		AND (r.GROUP_RELATION_STATUS='ST_ACTIVE' OR r.GROUP_RELATION_STATUS='PASS_PEND' )
//		                        		AND (g.IC_GROUP_ID=r.RELATED_IC_GROUP_ID)
//		                        		AND g.group_type != 'ic_user_representative' 
//		                        		--ORDER BY g.name
//		
//		                    		IF @@ROWCOUNT > 0           --If the previous statement added one or more nodes, go down for its first child.
//		                                BEGIN
//		                                    SELECT @lvl = @lvl + 1  --If no nodes are added, check its brother nodes.
//		                                    SELECT @updateitem_index = @updateitem_index + 1
//		                                    INSERT #y_stack values(@current, @updateitem_index )
//		                                END
//		                            ELSE
//		                                BEGIN
//		                                    SELECT @counter = @counter + 1
//		                                    update #result set rgt =@counter where ic_group_id =  @current and lft = (@counter -1)
//		                                END
//		                		END
//		                	ELSE
//		                		BEGIN       			-- Don't follow if already in stack, to prevent endless loop
//		                			--SELECT @lvl = @lvl - 1          --Back to the level immediately above.
//		                			
//		                            SELECT @counter = @counter + 2
//		                            insert #result values (@current,(@counter-1),@counter)		--Insert current into result
//		                            
//		                            DELETE FROM #stack
//		                                WHERE lvl = @lvl
//		                                AND item = @current     --Remove the current node from the stack.
//		                			
//		                			--SELECT @counter = @counter + 1
//		                            --update #result set rgt =@counter where ic_group_id =  @current
//		                		END
//		                END
//		            ELSE
//		            	BEGIN
//		                	SELECT @lvl = @lvl - 1          --Back to the level immediately above.
//		                    SELECT @counter = @counter + 1
//		                    IF (@updateitem_index > 0)
//		                        BEGIN
//		                            SELECT @updateitem = item FROM #y_stack WHERE stack_order = @updateitem_index
//		                            UPDATE #result set rgt=@counter where ic_group_id =  @updateitem
//		                            DELETE FROM #y_stack WHERE stack_order = @updateitem_index AND item = @updateitem
//		                            SELECT @updateitem_index = @updateitem_index - 1
//		                        END
//		                END
//			   END                                             --While
//		 SELECT * FROM #result order by lft
//		 DROP TABLE #stack
//		 DROP TABLE #result
//		 DROP TABLE #y_stack
//		 SET NOCOUNT OFF"

		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#executeProcedure(java.lang.Object[])
	 */
	protected Object executeProcedure(Object[] parameters) throws Exception {
		return executeProcedure(parameters,true);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#getName()
	 */
	public String getName() {
		return "get_group_tree";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#getParameterTypes()
	 */
	public Class[] getParameterTypes() {
		return parameterType;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#processResultSet(java.sql.ResultSet)
	 */
	public Object processResultSet(ResultSet rs) {
		//SortedSet set = new TreeSet(new NestedSetNodeComparator());
		Vector set = new Vector();
		NestedSetNode parent = null;
		try {
			while(rs.next()){
				NestedSetNode node = new NestedSetNode(String.valueOf(rs.getInt("ic_group_id")),parent,rs.getInt("lft"),rs.getInt("rgt"));
				set.add(node);
//				parent = node;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return new NestedSetsContainer(set);
	}
	
	
	public NestedSetsContainer getGroupTree(Group group) throws EJBException, Exception{
		return (NestedSetsContainer) getResult(new Object[] {group.getPrimaryKey()});
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericProcedure#useStoredProcedure(com.idega.data.DatastoreInterface)
	 */
	protected boolean isSupportedForDatabase(DatastoreInterface i) {
		// TODO Auto-generated method stub
		return (i instanceof MSSQLServerDatastoreInterface);
	}
	
}
