/*
 * Created on 23.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.version.data;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.user.data.User;

/**
 * Title:		ICVersion
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICVersionBMPBean extends GenericEntity implements ICVersion {

	private final static String COLUMNNAME_PARENT_VERSION_ID = "parent_version_id";
	private final static String COLUMNNAME_PARENT_HISTORY_ID = "parent_history_id";
	private final static String COLUMNNAME_NAME = "name";
	private final static String COLUMNNAME_NUMBER = "version_number";
	private final static String COLUMNNAME_DESCRIPTION = "description";
	private final static String COLUMNNAME_CREATED_TIMESTAMP = "created_timestamp";
	private final static String COLUMNNAME_CREATED_BY_USER = "created_by_user";
	private final static String COLUMNNAME_IC_ITEM_ID = "ic_item_id";

	/**
	 *
	 */
	public ICVersionBMPBean() {
		super();
	}

	/**
	 * @param id
	 * @throws SQLException
	 */
	public ICVersionBMPBean(int id) throws SQLException {
		super(id);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return "ic_version";
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMNNAME_NAME, "Name", true, true, String.class);
		addAttribute(COLUMNNAME_NUMBER, "Number", true, true, String.class);
		addAttribute(COLUMNNAME_DESCRIPTION, "Description", true, true, String.class);
		addAttribute(COLUMNNAME_CREATED_TIMESTAMP, "Created Timestamp", true, true, String.class);

		addManyToOneRelationship(COLUMNNAME_CREATED_BY_USER, User.class);
		addManyToOneRelationship(COLUMNNAME_PARENT_VERSION_ID, ICVersion.class);
		addManyToOneRelationship(COLUMNNAME_PARENT_HISTORY_ID,ICVersion.class);
		addManyToOneRelationship(COLUMNNAME_IC_ITEM_ID,ICItem.class);

	}

	/**
	 * @return
	 */
	@Override
	public int getCreatedByUserID() {
		return this.getIntColumnValue(COLUMNNAME_CREATED_BY_USER);
	}

	/**
	 * @return
	 */
	@Override
	public User getCreatedByUser() {
		return (User)this.getColumnValue(COLUMNNAME_CREATED_BY_USER);
	}

	/**
	 * @return
	 */
	@Override
	public Timestamp getCreatedTimestamp() {
		return (Timestamp)this.getColumnValue(COLUMNNAME_CREATED_TIMESTAMP);
	}

	/**
	 * @return
	 */
	@Override
	public String getDescription() {
		return this.getStringColumnValue(COLUMNNAME_DESCRIPTION);
	}

	/**
	 * @return
	 */
	@Override
	public String getName() {
		return this.getStringColumnValue(COLUMNNAME_NAME);
	}

	/**
	 * @return
	 */
	@Override
	public String getNumber() {
		return this.getStringColumnValue(COLUMNNAME_NUMBER);
	}

	/**
	 * @return
	 */
	@Override
	public int getParentVersionID() {
		return this.getIntColumnValue(COLUMNNAME_PARENT_VERSION_ID);
	}

	/**
	 * @return
	 */
	@Override
	public ICVersion getParentVersion() {
		return (ICVersion)this.getColumnValue(COLUMNNAME_PARENT_VERSION_ID);
	}

	/**
	 * @param userID
	 */
	@Override
	public void setCreatedByUser(int userID) {
		this.setColumn(COLUMNNAME_CREATED_BY_USER, userID);
	}

	/**
	 * @param user
	 */
	@Override
	public void setCreatedByUser(User user) {
		this.setColumn(COLUMNNAME_CREATED_BY_USER, user);
	}

	/**
	 * @param time
	 */
	@Override
	public void setCreatedTimestamp(Timestamp time) {
		this.setColumn(COLUMNNAME_CREATED_TIMESTAMP, time);
	}

	/**
	 * @param desc
	 */
	@Override
	public void setDescription(String desc) {
		this.setColumn(COLUMNNAME_DESCRIPTION, desc);
	}

	/**
	 * @param name
	 */
	@Override
	public void setName(String name) {
		this.setColumn(COLUMNNAME_NAME, name);
	}

	/**
	 * @param number
	 */
	@Override
	public void setNumber(String number) {
		this.setColumn(COLUMNNAME_NUMBER, number);
	}

	/**
	 * @param versionID
	 */
	@Override
	public void setParentVersionID(int versionID) {
		this.setColumn(COLUMNNAME_PARENT_VERSION_ID, versionID);
	}

	/**
	 * @param version
	 */
	@Override
	public void setParentVersion(ICVersion version) {
		this.setColumn(COLUMNNAME_PARENT_VERSION_ID, version);
	}



	//Home begins
	public Collection ejbFindChildrens(ICVersion version) throws FinderException{
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(version);
		query.appendWhereEquals(COLUMNNAME_PARENT_VERSION_ID,version.getPrimaryKey());

		return this.idoFindPKsByQuery(query);
	}


	public int ejbHomeGetChildrenCount(ICVersion version) throws IDOException{
		IDOQuery query = idoQuery();
		query.appendSelectCountFrom(version);
		query.appendWhereEquals(COLUMNNAME_PARENT_VERSION_ID,version.getPrimaryKey());

		return this.idoGetNumberOfRecords(query);
	}

	public Object ejbFindVersionByNumber(ICItem item, String Number) throws FinderException{
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(IDOLookup.instanciateEntity(ICVersion.class));
		query.appendWhereEquals(COLUMNNAME_NUMBER,Number);

		return this.idoFindOnePKByQuery(query);
	}

	//Home ends




	//ICTreeNode methods begin

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildrenIterator()
	 */
	@Override
	public Iterator<ICVersion> getChildrenIterator() {
	    Iterator<ICVersion> it = null;
	    Collection<ICVersion> children = getChildren();
	    if (children != null) {
				it = children.iterator();
			}
	    return it;
	}
	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildren()
	 */
	@Override
	public Collection<ICVersion> getChildren() {
		Collection<ICVersion> coll = null;
		try {
			ICVersionHome versionHome = (ICVersionHome)IDOLookup.getHome(ICVersion.class);
			coll = versionHome.findChildrens(this);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return coll;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICVersion#getChildAtIndex(int)
	 */
	@Override
	public ICVersion getChildAtIndex(int childIndex) {
		ICVersion node = null;
		try {
			ICVersionHome versionHome = (ICVersionHome)IDOLookup.getHome(ICVersion.class);
			Collection<ICVersion> coll = versionHome.findChildrens(this);
			try{
				List<ICVersion> list = (List<ICVersion>)coll;
				if(list.size()>childIndex){
					node = list.get(childIndex);
				}
			} catch(ClassCastException ex){
				System.out.println("ICVersion#getChildAtIndex(int):(List)(Collection)ICVersionHome#findChildrens(ICVersion)");
				System.out.println("ICVersion#getChildAtIndex(int):Not java.util.List -> cannot implement getChildAtIndex()");
				ex.printStackTrace();
				throw new UnsupportedOperationException("ICVersion#getChildAtIndex(int):Not java.util.List -> cannot implement getChildAtIndex()");
			}


		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return node;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		try {
			ICVersionHome versionHome = (ICVersionHome)IDOLookup.getHome(ICVersion.class);
			return versionHome.getChildrenCount(this);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (IDOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getIndex(com.idega.core.ICTreeNode)
	 */
	@Override
	public int getIndex(ICVersion node) {
		try {
			ICVersionHome versionHome = (ICVersionHome)IDOLookup.getHome(ICVersion.class);
			Collection<ICVersion> coll = versionHome.findChildrens(this);

			try{
				List<ICVersion> list = (List<ICVersion>)coll;
				return list.indexOf(node);
			} catch(ClassCastException ex){
				System.out.println("ICVersion#getIndex(int):(List)(Collection)ICVersionHome#getIndex(ICTreeNode)");
				System.out.println("ICVersion#getIndex(int):Not java.util.List -> cannot implement getIndex()");
				ex.printStackTrace();
				throw new UnsupportedOperationException("ICVersion#getIndex(int):Not java.util.List -> cannot implement getIndex()");
			}


		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getParentNode()
	 */
	@Override
	public ICVersion getParentNode() {
		return this.getParentVersion();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICVersion#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return !(this.getChildCount() > 0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return this.getName();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName(Locale locale)
	 */
	@Override
	public String getNodeName(Locale locale) {
		return this.getNodeName();
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac){
		return this.getNodeName(locale);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeID()
	 */
	@Override
	public int getNodeID() {
		return this.getID();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getSiblingCount()
	 */
	@Override
	public int getSiblingCount() {
		return (this.getParentVersion().getChildCount() - 1);
	}

	/**
	 * @return the number of siblings this node has
	 */
	@Override
	public String getId(){
		return getPrimaryKey().toString();
	}
	//ICTreeNode methods end

}
