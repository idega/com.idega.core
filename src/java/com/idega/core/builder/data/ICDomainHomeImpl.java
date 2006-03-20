package com.idega.core.builder.data;

import javax.ejb.FinderException;


public class ICDomainHomeImpl extends com.idega.data.IDOFactory implements ICDomainHome
{
 protected Class getEntityInterfaceClass(){
  return ICDomain.class;
 }


 public ICDomain create() throws javax.ejb.CreateException{
  return (ICDomain) super.createIDO();
 }


 public ICDomain createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAllDomains()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICDomainBMPBean)entity).ejbFindAllDomains();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllDomainsByServerName(String serverName)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICDomainBMPBean)entity).ejbFindAllDomainsByServerName(serverName);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICDomain findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICDomain) super.findByPrimaryKeyIDO(pk);
 }


 public ICDomain findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICDomain) super.findByPrimaryKeyIDO(id);
 }


 public ICDomain findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


/* (non-Javadoc)
 * @see com.idega.core.builder.data.ICDomainHome#findFirstDomain()
 */
public ICDomain findFirstDomain() throws FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICDomainBMPBean)entity).ejbFindFirstDomain();
	this.idoCheckInPooledEntity(entity);
	return findByPrimaryKey(pk);
}



}