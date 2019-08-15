package com.idega.core.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */
@Deprecated
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
	@Override
	public void initializeAttributes()
	{
		addAttribute(getIDColumnName());
		addAttribute(getNameColumnName(), "H�pnafn", true, true, "java.lang.String");
		addAttribute(getGroupTypeColumnName(), "H�pger�", true, true, "java.lang.String");
		addAttribute(getGroupDescriptionColumnName(), "L�sing", true, true, "java.lang.String");
		addAttribute(getExtraInfoColumnName(), "Auka uppl�singar", true, true, "java.lang.String");
		this.addTreeRelationShip();
		this.addManyToManyRelationShip(ICNetwork.class, "ic_group_network");
		this.addManyToManyRelationShip(ICProtocol.class, "ic_group_protocol");
	}
	@Override
	public String getEntityName()
	{
		return ENTITY_NAME;
	}
	@Override
	public void setDefaultValues()
	{
		setGroupType(getGroupTypeValue());
	}
	/**

	 * overwrite in extended classes

	 */
	@Override
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
	@Override
	public String getName()
	{
		return (String) getColumnValue(getNameColumnName());
	}
	@Override
	public void setName(String name)
	{
		setColumn(getNameColumnName(), name);
	}
	@Override
	public String getGroupType()
	{
		return (String) getColumnValue(getGroupTypeColumnName());
	}
	@Override
	public void setGroupType(String groupType)
	{
		setColumn(getGroupTypeColumnName(), groupType);
	}
	@Override
	public String getDescription()
	{
		return (String) getColumnValue(getGroupDescriptionColumnName());
	}
	@Override
	public void setDescription(String description)
	{
		setColumn(getGroupDescriptionColumnName(), description);
	}
	@Override
	public String getExtraInfo()
	{
		return (String) getColumnValue(getExtraInfoColumnName());
	}
	@Override
	public void setExtraInfo(String extraInfo)
	{
		setColumn(getExtraInfoColumnName(), extraInfo);
	}
	public static GenericGroup getStaticInstance()
	{
		return (GenericGroup) getStaticInstance(GenericGroup.class);
	}
	//??
	@Override
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
	@Override
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
	@Deprecated
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
					if (conn != null) {
						ConnectionBroker.freeConnection(getDatasource(), conn);
					}
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[GenericGroup] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }

		vector.trimToSize();
		return vector;
	}
	//??
	@Override
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
	@Deprecated
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
	@Override
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
	@Deprecated
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
					tempobj = this.getClass().newInstance();
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
					if (conn != null) {
						ConnectionBroker.freeConnection(getDatasource(), conn);
					}
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[GenericGroup] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }

		vector.trimToSize();
		return vector;
	}
	/**

	 * @todo change implementation: let the database handle the filtering

	 */
	@Override
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
	@Override
	public List getAllGroupsContainingUser(User user) throws SQLException
	{
		return this.getListOfAllGroupsContaining(user.getGroupID());
	}
	@Override
	public void addGroup(GenericGroup groupToAdd) throws SQLException
	{
		this.addGroup(groupToAdd.getID());
	}
	@Override
	public void addGroup(int groupId) throws SQLException {
		addGroupToTree(groupId);
	}

	private void addGroupToTree(int groupId) throws SQLException {
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
	@Override
	public void removeGroup(GenericGroup entityToRemoveFrom) throws SQLException
	{
		if ((entityToRemoveFrom.getID() == -1) || (entityToRemoveFrom.getID() == 0)) {
			this.removeGroup(entityToRemoveFrom.getID(), true);
		}
		else {
			this.removeGroup(entityToRemoveFrom.getID(), false);
		}
	}
	@Override
	public void removeGroup() throws SQLException
	{
		this.removeGroup(-1, true);
	}
	@Override
	public void removeGroup(int groupId, boolean AllEntries) throws SQLException
	{
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String qry;
			if (AllEntries) {
				qry =
					"delete from IC_GROUP_TREE where "
						+ this.getIDColumnName()
						+ "='"
						+ this.getID()
						+ "' OR CHILD_IC_GROUP_ID ='"
						+ this.getID()
						+ "'";
			}
			else {
				qry =
					"delete from IC_GROUP_TREE where "
						+ this.getIDColumnName()
						+ "='"
						+ this.getID()
						+ "' AND CHILD_IC_GROUP_ID ='"
						+ groupId
						+ "'";
			}
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
	@Override
	public void addUser(User user) throws SQLException
	{
		this.addGroup(user.getGroupID());
	}
	@Override
	public void removeUser(User user) throws SQLException
	{
		this.removeGroup(user.getGroupID(), false);
	}
	@Override
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
	@Override
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
	@Override
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
	@Override
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

	@Override
	public void updateChildGroupOrder(int groupId, int order) throws SQLException
	{
		if (getChildGroupOrder(groupId) <= 0) {
			addGroupToTree(groupId);
		}

		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "UPDATE ic_group_tree SET ordering = " + order + " WHERE " + getIDColumnName() + " = " + getID() + " AND child_ic_group_id = " + groupId;
			Stmt.executeUpdate(sql);
		}
		catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
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


	@Override
	public int getChildGroupOrder(int groupId) throws SQLException
	{
		Connection conn = null;
		Statement Stmt = null;
		int order = 0;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "SELECT ordering FROM ic_group_tree WHERE " + getIDColumnName() + " = " + getID() + " AND child_ic_group_id = " + groupId;
			ResultSet resultSet = Stmt.executeQuery(sql);
			if (resultSet != null && resultSet.next()) {
				order = resultSet.getInt("ordering");
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				freeConnection(getDatasource(), conn);
			}
		}
		return order;
	}

	@Override
	public List<Integer> getChildGroupIds() throws SQLException {
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
		List<Integer> result = new ArrayList<Integer>();
		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			RS = Stmt.executeQuery(SQLString);
			while (RS.next()) {
				result.add(RS.getInt("CHILD_IC_GROUP_ID"));
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
					if (conn != null) {
						ConnectionBroker.freeConnection(getDatasource(), conn);
					}
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[GenericGroup] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }

		return result;
	}


} // Class Group
