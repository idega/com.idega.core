package com.idega.core.data;


public class ICCategoryHomeImpl extends com.idega.data.IDOFactory implements ICCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICCategory.class;
 }

 public ICCategory create() throws javax.ejb.CreateException{
  return (ICCategory) super.idoCreate();
 }

 public ICCategory createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICCategory findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICCategory) super.idoFindByPrimaryKey(id);
 }

 public ICCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICCategory) super.idoFindByPrimaryKey(pk);
 }

 public ICCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }

 public int getOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ICCategoryBMPBean)entity).ejbHomeGetOrderNumber(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

 public boolean setOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1,int p2)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	boolean theReturn = ((ICCategoryBMPBean)entity).ejbHomeSetOrderNumber(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}