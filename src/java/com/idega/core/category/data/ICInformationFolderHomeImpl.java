package com.idega.core.category.data;


public class ICInformationFolderHomeImpl extends com.idega.data.IDOFactory implements ICInformationFolderHome
{
 protected Class getEntityInterfaceClass(){
  return ICInformationFolder.class;
 }


 public ICInformationFolder create() throws javax.ejb.CreateException{
  return (ICInformationFolder) super.createIDO();
 }


 public ICInformationFolder createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


 public ICInformationFolder findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICInformationFolder) super.findByPrimaryKeyIDO(pk);
 }


 public ICInformationFolder findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICInformationFolder) super.findByPrimaryKeyIDO(id);
 }


 public ICInformationFolder findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


public void removeObjectInstanceRelation(com.idega.core.component.data.ICObjectInstance p0)throws com.idega.data.IDORemoveRelationshipException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	((ICInformationFolderBMPBean)entity).ejbHomeRemoveObjectInstanceRelation(p0);
	this.idoCheckInPooledEntity(entity);
}


}