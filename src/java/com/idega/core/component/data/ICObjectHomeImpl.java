/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.core.component.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO thomas Describe Type ICObjectHomeImpl
 * </p>
 *  Last modified: $Date: 2005/06/03 15:18:29 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class ICObjectHomeImpl extends IDOFactory implements ICObjectHome {
	
	
	 public ICObject createLegacy(){
		try{
			return create();
		}
		catch(javax.ejb.CreateException ce){
			throw new RuntimeException("CreateException:"+ce.getMessage());
		}

	 }
	 
	 public ICObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
		try{
			return findByPrimaryKey(id);
		}
		catch(javax.ejb.FinderException fe){
			throw new java.sql.SQLException(fe.getMessage());
		}

	 }
	 
	 public ICObject findByPrimaryKey(int id) throws javax.ejb.FinderException{
	 	  return (ICObject) super.findByPrimaryKeyIDO(id);
	 	 }

	protected Class getEntityInterfaceClass() {
		return ICObject.class;
	}

	public ICObject create() throws javax.ejb.CreateException {
		return (ICObject) super.createIDO();
	}

	public ICObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (ICObject) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByObjectType(String type) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllByObjectType(type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByObjectTypeAndBundle(String type, String bundle) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllByObjectTypeAndBundle(type, bundle);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByBundle(String bundle) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllByBundle(bundle);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public ICObject findByClassName(String className) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICObjectBMPBean) entity).ejbFindByClassName(className);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllBlocksByBundle(String bundle) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllBlocksByBundle(bundle);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBlocks() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllBlocks();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllElementsByBundle(String bundle) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllElementsByBundle(bundle);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllElements() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICObjectBMPBean) entity).ejbFindAllElements();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
