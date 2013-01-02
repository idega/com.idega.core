package com.idega.core.component.data;


import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import com.idega.data.IDOException;
import com.idega.data.IDOHome;
import com.idega.data.IDORemoveRelationshipException;

public interface ICObjectInstanceHome extends IDOHome {

	public ICObjectInstance create() throws CreateException;

	public ICObjectInstance findByPrimaryKey(Object pk) throws FinderException;

	public ICObjectInstance createLegacy();

	public ICObjectInstance findByPrimaryKey(int id) throws FinderException;

	public ICObjectInstance findByPrimaryKeyLegacy(int id) throws SQLException;

	public void removeRelation(ICObjectInstance instance, Class relatedEntity) throws IDORemoveRelationshipException;

	public ICObjectInstance findByUniqueId(String uuid) throws FinderException;

	public Collection findByPageKey(String pageKey) throws FinderException;

	public int getCountByICObject(ICObject ico) throws IDOException;

	public Collection<ICObjectInstance> getByClassName(Class<? extends UIComponent> className) throws FinderException;
}