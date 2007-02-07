package com.idega.core.component.data;


import com.idega.data.IDOException;
import java.util.Collection;
import javax.ejb.CreateException;
import java.sql.SQLException;
import javax.ejb.FinderException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class ICObjectInstanceHomeImpl extends IDOFactory implements ICObjectInstanceHome {

	public Class getEntityInterfaceClass() {
		return ICObjectInstance.class;
	}

	public ICObjectInstance create() throws CreateException {
		return (ICObjectInstance) super.createIDO();
	}

	public ICObjectInstance findByPrimaryKey(Object pk) throws FinderException {
		return (ICObjectInstance) super.findByPrimaryKeyIDO(pk);
	}

	public ICObjectInstance createLegacy() {
		try {
			return create();
		}
		catch (CreateException ce) {
			throw new RuntimeException(ce.getMessage());
		}
	}

	public ICObjectInstance findByPrimaryKey(int id) throws FinderException {
		return (ICObjectInstance) super.findByPrimaryKeyIDO(id);
	}

	public ICObjectInstance findByPrimaryKeyLegacy(int id) throws SQLException {
		try {
			return findByPrimaryKey(id);
		}
		catch (FinderException fe) {
			throw new SQLException(fe.getMessage());
		}
	}

	public void removeRelation(ICObjectInstance instance, Class relatedEntity) throws IDORemoveRelationshipException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		((ICObjectInstanceBMPBean) entity).ejbHomeRemoveRelation(instance, relatedEntity);
		this.idoCheckInPooledEntity(entity);
	}

	public ICObjectInstance findByUniqueId(String uuid) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICObjectInstanceBMPBean) entity).ejbFindByUniqueId(uuid);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findByPageKey(String pageKey) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ICObjectInstanceBMPBean) entity).ejbFindByPageKey(pageKey);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getCountByICObject(ICObject ico) throws IDOException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((ICObjectInstanceBMPBean) entity).ejbHomeGetCountByICObject(ico);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}
}