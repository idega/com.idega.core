package com.idega.core.category.data;


public class ICInformationCategoryHomeImpl extends com.idega.data.IDOFactory implements ICInformationCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICInformationCategory.class;
 }


 public ICInformationCategory create() throws javax.ejb.CreateException{
  return (ICInformationCategory) super.createIDO();
 }


 public ICInformationCategory createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAvailableCategories(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICInformationCategoryBMPBean)entity).ejbFindAvailableCategories(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAvailableTopNodeCategories(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICInformationCategoryBMPBean)entity).ejbFindAvailableTopNodeCategories(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICInformationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICInformationCategory) super.findByPrimaryKeyIDO(pk);
 }


 public ICInformationCategory findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICInformationCategory) super.findByPrimaryKeyIDO(id);
 }


 public ICInformationCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


public void removeObjectInstanceRelation(com.idega.core.component.data.ICObjectInstance p0)throws com.idega.data.IDORemoveRelationshipException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	((ICInformationCategoryBMPBean)entity).ejbHomeRemoveObjectInstanceRelation(p0);
	this.idoCheckInPooledEntity(entity);
}


}