package com.idega.core.builder.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class ICDomainHomeImpl extends IDOFactory implements ICDomainHome {
	@Override
	public Class getEntityInterfaceClass() {
		return ICDomain.class;
	}

	public ICDomain create() throws CreateException {
		return (ICDomain) super.createIDO();
	}

	public ICDomain findByPrimaryKey(Object pk) throws FinderException {
		return (ICDomain) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllDomains() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ICDomainBMPBean) entity).ejbFindAllDomains();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllDomainsByServerName(String serverName) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ICDomainBMPBean) entity).ejbFindAllDomainsByServerName(serverName);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public ICDomain findFirstDomain() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICDomainBMPBean) entity).ejbFindFirstDomain();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public ICDomain findDefaultDomain() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICDomainBMPBean) entity).ejbFindDefaultDomain();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public ICDomain findDomainByServernameOrDefault(String serverName) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICDomainBMPBean) entity).ejbFindDomainByServernameOrDefault(serverName);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}