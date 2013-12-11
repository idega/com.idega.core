package com.idega.core.component.data;


import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import com.idega.data.IDOEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;
import com.idega.data.IDORemoveRelationshipException;

public class ICObjectInstanceHomeImpl extends IDOFactory implements ICObjectInstanceHome {

	@Override
	public Class<ICObjectInstance> getEntityInterfaceClass() {
		return ICObjectInstance.class;
	}

	@Override
	public ICObjectInstance create() throws CreateException {
		return (ICObjectInstance) super.createIDO();
	}

	@Override
	public ICObjectInstance findByPrimaryKey(Object pk) throws FinderException {
		return (ICObjectInstance) super.findByPrimaryKeyIDO(pk);
	}

	@Override
	public ICObjectInstance createLegacy() {
		try {
			return create();
		}
		catch (CreateException ce) {
			throw new RuntimeException(ce.getMessage());
		}
	}

	@Override
	public ICObjectInstance findByPrimaryKey(int id) throws FinderException {
		return (ICObjectInstance) super.findByPrimaryKeyIDO(id);
	}

	@Override
	public ICObjectInstance findByPrimaryKeyLegacy(int id) throws SQLException {
		try {
			return findByPrimaryKey(id);
		}
		catch (FinderException fe) {
			throw new SQLException(fe.getMessage());
		}
	}

	@Override
	public void removeRelation(ICObjectInstance instance, Class relatedEntity) throws IDORemoveRelationshipException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		((ICObjectInstanceBMPBean) entity).ejbHomeRemoveRelation(instance, relatedEntity);
		this.idoCheckInPooledEntity(entity);
	}

	@Override
	public ICObjectInstance findByUniqueId(String uuid) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICObjectInstanceBMPBean) entity).ejbFindByUniqueId(uuid);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findByPageKey(String pageKey) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ICObjectInstanceBMPBean) entity).ejbFindByPageKey(pageKey);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public int getCountByICObject(ICObject ico) throws IDOException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((ICObjectInstanceBMPBean) entity).ejbHomeGetCountByICObject(ico);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Collection<ICObjectInstance> getByClassName(Class<? extends UIComponent> className) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<?> ids = ((ICObjectInstanceBMPBean) entity).ejbFindByClassName(className);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<ICObjectInstance> getByICObject(ICObject ico) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<?> ids = ((ICObjectInstanceBMPBean) entity).ejbFindByICObject(ico);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

}