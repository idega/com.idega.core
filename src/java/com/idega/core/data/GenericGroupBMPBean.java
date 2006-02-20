package com.idega.core.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.idega.core.net.data.ICNetwork;
import com.idega.core.net.data.ICProtocol;
import com.idega.core.user.data.User;
import com.idega.data.EntityFinder;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDORuntimeException;
import com.idega.data.SimpleQuerier;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.ListUtil;
import com.idega.util.database.ConnectionBroker;
/**
 * 
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @deprecated Class replaced with com.idega.user.data.GroupBMPBean
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */
public class GenericGroupBMPBean extends com.idega.data.GenericEntity implements com.idega.core.data.GenericGroup
{
	private static final String ENTITY_NAME = "IC_GROUP";
	public GenericGroupBMPBean()
	{
		super();
	}
	public GenericGroupBMPBean(int id) throws SQLException
	{
		super(id);
	}
	public void initializeAttributes()
	{
		addAttribute(getIDColumnName());
		addAttribute(getNameColumnName(), "Hópnafn", true, true, "java.lang.String");
		addAttribute(getGroupTypeColumnName(), "Hópgerð", true, true, "java.lang.String");
		addAttribute(getGroupDescriptionColumnName(), "Lýsing", true, true, "java.lang.String");
		addAttribute(getExtraInfoColumnName(), "Auka upplýsingar", true, true, "java.lang.String");
		this.addTreeRelationShip();
		this.addManyToManyRelationShip(ICNetwork.class, "ic_group_network");
		this.addManyToManyRelationShip(ICProtocol.class, "ic_group_protocol");
	}
	public String getEntityName()
	{
		return ENTITY_NAME;
	}
	public void setDefaultValues()
	{
		setGroupType(getGroupTypeValue());
	}
	/**
	
	 * overwrite in extended classes
	
	 */
	public String getGroupTypeValue()
	{
		return "GENERAL";
	}
	/*  ColumNames begin   */
	public static String getColumnNameGroupID()
	{
		return "IC_GROUP_ID";
	}
	public static String getNameColumnName()
	{
		return "NAME";
	}
	public static String getGroupTypeColumnName()
	{
		return "GROUP_TYPE";
	}
	public static String getGroupDescriptionColumnName()
	{
		return "DESCRIPTION";
	}
	public static String getExtraInfoColumnName()
	{
		return "EXTRA_INFO";
	}
	/*  ColumNames end   */
	/*  functions begin   */
	public String getName()
	{
		return (String) getColumnValue(getNameColumnName());
	}
	public void setName(String name)
	{
		setColumn(getNameColumnName(), name);
	}
	public String getGroupType()
	{
		return (String) getColumnValue(getGroupTypeColumnName());
	}
	public void setGroupType(String groupType)
	{
		setColumn(getGroupTypeColumnName(), groupType);
	}
	public String getDescription()
	{
		return (String) getColumnValue(getGroupDescriptionColumnName());
	}
	public void setDescription(String description)
	{
		setColumn(getGroupDescriptionColumnName(), description);
	}
	public String getExtraInfo()
	{
		return (String) getColumnValue(getExtraInfoColumnName());
	}
	public void setExtraInfo(String extraInfo)
	{
		setColumn(getExtraInfoColumnName(), extraInfo);
	}
	public static GenericGroup getStaticInstance()
	{
		return (GenericGroup) getStaticInstance(GenericGroup.class);
	}
	//??
	public GenericGroup[] getAllGroupsContainingThis() throws SQLException
	{
		List vector = this.getParentGroups();
		if (vector != null)
		{
			return (GenericGroup[]) vector.toArray((Object[]) java.lang.reflect.Array.newInstance(this.getClass(), 0));
		}
		else
		{
			return new GenericGroup[0];
		}
	}
	///**
	// * Gets the groups that are direct parents of this group.
	// * @deprecated Replaced with getParentGroups
	// */
	//public List getListOfAllGroupsContainingThis() throws SQLException{
	//	try{
	//		return ListUtil.convertCollectionToList(getParentGroups());	
	//	}
	//	catch(Exception e){
	//		throw new SQLException(e.getMessage());	
	//	}
	//}
	/**
	 * Gets the groups that are direct parents of this group
	 **/
	public List getParentGroups()
	{
		//return getParentGroupsLegacy();
		return ListUtil.convertCollectionToList(getParentGroupsLegacy());
	}
	/**
	 * Gets the groups that are direct parents of this group
	 * The old implementation. Uses IC_GROUP_TREE to find parent groups.
	 **/
	protected Collection getParentGroupsLegacy()
	{
		try
		{
			return this.getListOfAllGroupsContaining(this.getID());
		}
		catch (SQLException e)
		{
			throw new IDORuntimeException(e, this);
		}
	}
	/**
	 * @deprecated Old implementation. Uses IC_GROUP_TREE to find parent groups.
	 **/
	protected List getListOfAllGroupsContaining(int group_id) throws SQLException
	{
		String tableToSelectFrom = "IC_GROUP_TREE";
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from ");
		buffer.append(tableToSelectFrom);
		buffer.append(" where ");
		buffer.append("CHILD_IC_GROUP_ID");
		buffer.append("=");
		buffer.append(group_id);
		String SQLString = buffer.toString();
		Connection conn = null;
		Statement Stmt = null;
		ResultSet RS = null;
		Vector vector = new Vector();
		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			RS = Stmt.executeQuery(SQLString);
			while (RS.next())
			{
				IDOLegacyEntity tempobj = null;
				try
				{
					tempobj = (IDOLegacyEntity) RefactorClassRegistry.forName(this.getClass().getName()).newInstance();
					tempobj.findByPrimaryKey(RS.getInt(this.getIDColumnName()));
				}
				catch (Exception ex)
				{
					System.err.println(
						"There was an error in " + this.getClass().getName() + ".getAllGroupsContainingThis(): " + ex.getMessage());
				}
				vector.addElement(tempobj);
			}
		}
	    finally {
	    	// do not hide an existing exception
	    	try { 
	    		if (RS != null) {
	    			RS.close();
		      	}
	    	}
		    catch (SQLException resultCloseEx) {
		    	System.err.println("[GenericGroup] result set could not be closed");
		     	resultCloseEx.printStackTrace(System.err);
		    }
		    // do not hide an existing exception
		    try {
		    	if (Stmt != null)  {
		    		Stmt.close();
					if (conn != null)
						ConnectionBroker.freeConnection(getDatasource(), conn);
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[GenericGroup] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }

		if (vector != null)
		{
			vector.trimToSize();
			return vector;
			//return (GenericGroup[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
		}
		else
		{
			return null;
		}
	}
	//??
	public GenericGroup[] getAllGroupsContained() throws SQLException
	{
		List vector = this.getChildGroups();
		if (vector != null)
		{
			return (GenericGroup[]) vector.toArray((Object[]) java.lang.reflect.Array.newInstance(this.getClass(), 0));
		}
		else
		{
			return new GenericGroup[0];
		}
	}
	/**
	 * @deprecated Replaced with getChildGroups
	 */
	public List getListOfAllGroupsContained() throws SQLException
	{
		try
		{
			return ListUtil.convertCollectionToList(getChildGroups());
		}
		catch (Exception e)
		{
			throw new SQLException(e.getMessage());
		}
	}
	/**
	 * Gets the groups that are direct children of this group
	 **/
	public List getChildGroups()
	{
		try
		{
			//return getChildGroupsLegacy();
			return ListUtil.convertCollectionToList(getChildGroupsLegacy());
		}
		catch (SQLException e)
		{
			throw new IDORuntimeException(e, this);
		}
	}
	/**
	 * @deprecated The implementation belonging to the old user system.
	 * Gets the groups that are direct children of this group. Uses the IC_GROUP_TREE table to find children.
	 **/
	protected Collection getChildGroupsLegacy() throws SQLException
	{
		String tableToSelectFrom = "IC_GROUP_TREE";
		StringBuffer buffer = new StringBuffer();
		buffer.append("select CHILD_IC_GROUP_ID from ");
		buffer.append(tableToSelectFrom);
		buffer.append(" where ");
		buffer.append("IC_GROUP_ID");
		buffer.append("=");
		buffer.append(this.getID());
		String SQLString = buffer.toString();
		Connection conn = null;
		Statement Stmt = null;
		ResultSet RS = null;
		Vector vector = new Vector();
		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			RS = Stmt.executeQuery(SQLString);
			while (RS.next())
			{
				IDOLegacyEntity tempobj = null;
				try
				{
					tempobj = (IDOLegacyEntity) this.getClass().newInstance();
					tempobj.findByPrimaryKey(RS.getInt("CHILD_IC_GROUP_ID"));
				}
				catch (Exception ex)
				{
					System.err.println(
						"There was an error in " + this.getClass().getName() + ".getAllGroupsContainingThis(): " + ex.getMessage());
				}
				vector.addElement(tempobj);
			}
		}
	    finally {
	    	// do not hide an existing exception
	    	try { 
	    		if (RS != null) {
	    			RS.close();
		      	}
	    	}
		    catch (SQLException resultCloseEx) {
		    	System.err.println("[GenericGroup] result set could not be closed");
		     	resultCloseEx.printStackTrace(System.err);
		    }
		    // do not hide an existing exception
		    try {
		    	if (Stmt != null)  {
		    		Stmt.close();
					if (conn != null)
						ConnectionBroker.freeConnection(getDatasource(), conn);
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[GenericGroup] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }
		if (vector != null)
		{
			vector.trimToSize();
			return vector;
			//return (GenericGroup[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
		}
		else
		{
			return null;
		}
		//return (Group[])this.findReverseRelated(this);
	}
	/**
	
	 * @todo change implementation: let the database handle the filtering
	
	 */
	public List getChildGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException
	{
		List list = this.getChildGroups();
		List specifiedGroups = new Vector();
		List notSpecifiedGroups = new Vector();
		int j = 0;
		int k = 0;
		Iterator iter2 = list.iterator();
		if (groupTypes != null && groupTypes.length > 0)
		{
			boolean specified = false;
			while (iter2.hasNext())
			{
				GenericGroup tempObj = (GenericGroup) iter2.next();
				for (int i = 0; i < groupTypes.length; i++)
				{
					if (tempObj.getGroupType().equals(groupTypes[i]))
					{
						specifiedGroups.add(j++, tempObj);
						specified = true;
					}
				}
				if (!specified)
				{
					notSpecifiedGroups.add(k++, tempObj);
				}
				else
				{
					specified = false;
				}
			}
			notSpecifiedGroups.remove(this);
			specifiedGroups.remove(this);
		}
		else
		{
			while (iter2.hasNext())
			{
				GenericGroup tempObj = (GenericGroup) iter2.next();
				notSpecifiedGroups.add(j++, tempObj);
			}
			notSpecifiedGroups.remove(this);
			returnSepcifiedGroupTypes = false;
		}
		return (returnSepcifiedGroupTypes) ? specifiedGroups : notSpecifiedGroups;
	}
	public List getAllGroupsContainingUser(User user) throws SQLException
	{
		return this.getListOfAllGroupsContaining(user.getGroupID());
	}
	public void addGroup(GenericGroup groupToAdd) throws SQLException
	{
		this.addGroup(groupToAdd.getID());
	}
	public void addGroup(int groupId) throws SQLException
	{
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "insert into IC_GROUP_TREE (" + getIDColumnName() + ", CHILD_IC_GROUP_ID) values(" + getID() + "," + groupId + ")";
			//System.err.println(sql);
			Stmt.executeUpdate(sql);
			//System.err.println(sql);
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.out);
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public void removeGroup(GenericGroup entityToRemoveFrom) throws SQLException
	{
		if ((entityToRemoveFrom.getID() == -1) || (entityToRemoveFrom.getID() == 0)) //removing all in middle table
			this.removeGroup(entityToRemoveFrom.getID(), true);
		else // just removing this particular one
			this.removeGroup(entityToRemoveFrom.getID(), false);
	}
	public void removeGroup() throws SQLException
	{
		this.removeGroup(-1, true);
	}
	public void removeGroup(int groupId, boolean AllEntries) throws SQLException
	{
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String qry;
			if (AllEntries) //removing all in middle table
				qry =
					"delete from IC_GROUP_TREE where "
						+ this.getIDColumnName()
						+ "='"
						+ this.getID()
						+ "' OR CHILD_IC_GROUP_ID ='"
						+ this.getID()
						+ "'";
			else // just removing this particular one
				qry =
					"delete from IC_GROUP_TREE where "
						+ this.getIDColumnName()
						+ "='"
						+ this.getID()
						+ "' AND CHILD_IC_GROUP_ID ='"
						+ groupId
						+ "'";
			Stmt.executeUpdate(qry);
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.out);
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				freeConnection(getDatasource(), conn);
			}
		}
	}
	public static void addUser(int groupId, User user) throws SQLException
	{
		((com.idega.core.data.GenericGroupHome) com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class))
			.findByPrimaryKeyLegacy(groupId)
			.addGroup(user.getGroupID());
	}
	public void addUser(User user) throws SQLException
	{
		this.addGroup(user.getGroupID());
	}
	public void removeUser(User user) throws SQLException
	{
		this.removeGroup(user.getGroupID(), false);
	}
	public GenericGroup findGroup(String groupName) throws SQLException
	{
		List group =
			EntityFinder.findAllByColumn(
				(IDOLegacyEntity) com.idega.data.GenericEntity.getStaticInstance(this.getClass().getName()),
				getNameColumnName(),
				groupName,
				getGroupTypeColumnName(),
				this.getGroupTypeValue());
		if (group != null)
		{
			return (GenericGroup) group.get(0);
		}
		else
		{
			return null;
		}
	}
	public static List getAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException
	{
		String typeList = "";
		if (groupTypes != null && groupTypes.length > 0)
		{
			for (int g = 0; g < groupTypes.length; g++)
			{
				if (g > 0)
				{
					typeList += ", ";
				}
				typeList += "'" + groupTypes[g] + "'";
			}
			GenericGroup gr = com.idega.core.data.GenericGroupBMPBean.getStaticInstance();
			return EntityFinder.findAll(
				gr,
				"select * from "
					+ gr.getEntityName()
					+ " where "
					+ com.idega.core.data.GenericGroupBMPBean.getGroupTypeColumnName()
					+ ((returnSepcifiedGroupTypes) ? " in (" : " not in (")
					+ typeList
					+ ") order by "
					+ com.idega.core.data.GenericGroupBMPBean.getNameColumnName());
		}
		return EntityFinder.findAllOrdered(
			com.idega.core.data.GenericGroupBMPBean.getStaticInstance(),
			com.idega.core.data.GenericGroupBMPBean.getNameColumnName());
	}
	protected boolean identicalGroupExistsInDatabase() throws Exception
	{
		return SimpleQuerier.executeStringQuery(
			"select * from "
				+ this.getEntityName()
				+ " where "
				+ GenericGroupBMPBean.getGroupTypeColumnName()
				+ " = '"
				+ this.getGroupType()
				+ "' and "
				+ GenericGroupBMPBean.getNameColumnName()
				+ " = '"
				+ this.getName()
				+ "'",
			this.getDatasource()).length
			> 0;
	}
	public void insert() throws SQLException
	{
		try
		{
			//            if(!this.getName().equals("")){
			if (identicalGroupExistsInDatabase())
			{
				throw new SQLException("group with same name and type already in database");
			}
			//            }
			super.insert();
		}
		catch (Exception ex)
		{
			if (ex instanceof SQLException)
			{
				throw (SQLException) ex;
			}
			else
			{
				//System.err.println(ex.getMessage());
				//ex.printStackTrace();
				throw new SQLException(ex.getMessage());
			}
		}
	}
	public boolean equals(IDOLegacyEntity entity)
	{
		if (entity != null)
		{
			if (entity instanceof GenericGroup)
			{
				return this.equals((GenericGroup) entity);
			}
			else
			{
				return super.equals(entity);
			}
		}
		return false;
	}
	public boolean equals(GenericGroup group)
	{
		if (group != null)
		{
			if (group.getID() == this.getID())
			{
				return true;
			}
			return false;
		}
		return false;
	}
} // Class Group
