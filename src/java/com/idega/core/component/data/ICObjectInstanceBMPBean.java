// idega 2001 - Tryggvi Larusson
/*
 *
 * Copyright 2001 idega.is All Rights Reserved.
 *
 */
package com.idega.core.component.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import com.idega.core.component.business.ICObjectBusiness;
import com.idega.data.IDOException;
import com.idega.data.IDOFinderException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.query.CountColumn;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class ICObjectInstanceBMPBean extends com.idega.data.GenericEntity implements ICObjectInstance {

	private static final long serialVersionUID = -1522439895980168899L;

	public ICObjectInstanceBMPBean() {
		super();
	}

	public ICObjectInstanceBMPBean(int id) throws Exception {
		super(id);
	}

	public static final String COLUMN_OBJECT_ID = "IC_OBJECT_ID";
	public static final String IBPAGEID = "IB_PAGE_ID";
	public static final String COLUMNNAME_PARENTID = "DPT_PARENT_ID";

	@Override
	public void initializeAttributes() {
		// par1: column name, par2: visible column name, par3-par4:
		// editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addManyToOneRelationship(COLUMN_OBJECT_ID, "Object", ICObject.class);
		addManyToOneRelationship(IBPAGEID, "Page", com.idega.core.builder.data.ICPage.class);
		addManyToOneRelationship(COLUMNNAME_PARENTID, "ParentId", ICObjectInstance.class);
		addUniqueIDColumn();

		addIndex(getUniqueIdColumnName());

		getEntityDefinition().setBeanCachingActiveByDefault(true,10000);
		getEntityDefinition().setUseFinderCollectionPrefetch(true);
	}

	@Override
	public String getEntityName() {
		return "IC_OBJECT_INSTANCE";
	}

	@Override
	public void setDefaultValues() {
		// setColumn("image_id",1);
	}

	@Override
	public String getName() {
		return getObject().getName() + " nr. " + this.getID();
	}

	@Override
	public void setICObjectID(int id) {
		this.setColumn(COLUMN_OBJECT_ID, id);
	}

	@Override
	public void setICObject(ICObject object) {
		this.setColumn(COLUMN_OBJECT_ID, object);
	}

	@Override
	public int getIBPageID() {
		return getIntColumnValue(IBPAGEID);
	}

	@Override
	public void setIBPageID(int id) {
		this.setColumn(IBPAGEID, id);
	}

	@Override
	public int getParentInstanceID() {
		return getIntColumnValue(COLUMNNAME_PARENTID);
	}

	@Override
	public void setParentInstanceID(int id) {
		this.setColumn(COLUMNNAME_PARENTID, id);
	}

	@Override
	public ICObject getObject() {
		int icObjectID = this.getIntColumnValue(COLUMN_OBJECT_ID);
		if (icObjectID < 0) {
			return null;
		}
		return ICObjectBusiness.getInstance().getICObject(icObjectID);
	}

	@Override
	public PresentationObject getNewInstance() throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		return getObject().getNewInstance();
	}

	@Override
	public void setIBPageByKey(String pageKey) {
		try {
			int id = Integer.parseInt(pageKey);
			this.setIBPageID(id);
		}
		catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		}
	}

	public void ejbHomeRemoveRelation(ICObjectInstance instance, Class relatedEntity)
			throws IDORemoveRelationshipException {
		try {
			instance.removeFrom(relatedEntity);
		}
		catch (SQLException e) {
			throw new IDORemoveRelationshipException(e.getMessage());
		}
	}

	@Override
	public String getUniqueId() {
		return super.getUniqueId();
	}

	@Override
	public void setUniqueId(String uniqueId) {
		super.setUniqueId(uniqueId);
	}

	@Override
	public int getID(){
		return super.getID();
	}

	public Integer ejbFindByUniqueId(String uuid) throws FinderException {
		Collection cachedList = getCachedEntities();
		for (Iterator iter = cachedList.iterator(); iter.hasNext();) {
			ICObjectInstance instance = (ICObjectInstance) iter.next();
			if(instance != null && instance.getUniqueId() != null && uuid != null && instance.getUniqueId().equals(uuid)){
				return (Integer) instance.getPrimaryKey();
			}
		}
		//if not found in cache try to find in database
		SelectQuery sql = idoSelectPKQuery();
		Table thisTable = new Table(getEntityDefinition().getSQLTableName());
		sql.addCriteria(new MatchCriteria(thisTable, getUniqueIdColumnName(), MatchCriteria.EQUALS, uuid));
		return (Integer) idoFindOnePKByQuery(sql);
	}

	public Collection ejbFindByPageKey(String pageKey) throws FinderException{
		try{
			int pageId = Integer.parseInt(pageKey);
			SelectQuery query = idoSelectQuery();
			Table thisTable = new Table(getEntityDefinition().getSQLTableName());
			query.addCriteria(new MatchCriteria(thisTable, IBPAGEID, MatchCriteria.EQUALS, pageId));
			Collection list = idoFindPKsByQuery(query);
			return list;
		}
		catch(NumberFormatException nfe){
			throw new IDOFinderException(nfe);
		}

	}

	public int ejbHomeGetCountByICObject(ICObject ico) throws IDOException {
		Table thisTable = new Table(this);

		SelectQuery query = new SelectQuery(thisTable);
		query.addColumn(new CountColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(thisTable, COLUMN_OBJECT_ID, MatchCriteria.EQUALS, ico));

		return idoGetNumberOfRecords(query);
	}

	public Collection<?> ejbFindByClassName(Class<? extends UIComponent> className) throws FinderException {
		SelectQuery query = idoSelectQuery();
		Table thisTable = new Table(getEntityName());
		Table icObjectTable = new Table(ICObject.class);
		query.addJoin(thisTable, COLUMN_OBJECT_ID, icObjectTable, COLUMN_OBJECT_ID);
		query.addCriteria(new MatchCriteria(icObjectTable, ICObjectBMPBean.class_name_column_name, MatchCriteria.EQUALS, className.getName()));
		return idoFindPKsByQuery(query);
	}
}