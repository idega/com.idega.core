/**
 * 
 */
package com.idega.core.builder.data;

import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;


/**
 * <p>
 * TODO tryggvil Describe Type ICPageHomeImpl
 * </p>
 *  Last modified: $Date: 2009/01/14 15:12:24 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */

public class ICPageHomeImpl extends com.idega.data.IDOFactory implements ICPageHome
{
 protected Class getEntityInterfaceClass(){
  return ICPage.class;
 }

 public ICPage create() throws javax.ejb.CreateException{
  return (ICPage) super.idoCreate();
 }

 public ICPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(id);
 }

 public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(pk);
 }

 public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


    public Collection findByTemplate(Integer templateID) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindByTemplate(templateID);
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
    }

	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findByUri(java.lang.String, int)
	 */
	public ICPage findByUri(String pageUri, int domainId) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((ICPageBMPBean)entity).ejbFindByPageUri(pageUri,domainId);
    		this.idoCheckInPooledEntity(entity);
    		return this.findByPrimaryKey(pk);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findByUri(java.lang.String, int)
	 */
	public ICPage findExistingByUri(String pageUri, int domainId) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((ICPageBMPBean)entity).ejbFindExistingPageByPageUri(pageUri,domainId);
    		this.idoCheckInPooledEntity(entity);
    		return this.findByPrimaryKey(pk);
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAllPagesWithoutUri()
	 */
	public Collection findAllPagesWithoutUri() throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllPagesWithoutUri();
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAllPagesWithoutUri()
	 */
	public Collection findAllSimpleTemplates() throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllSimpleTemplates();
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection findBySubType(String subType, boolean deleted) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindBySubType(subType, deleted);
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection findAllByPhrase(String phrase, List<String> idsToAvoid) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllByPhrase(phrase, idsToAvoid);
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection findAllByPrimaryKeys(List<String> primaryKeys) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllByPrimaryKeys(primaryKeys);
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByName(String name, boolean findOnlyNotDeleted) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((ICPageBMPBean)entity).ejbFindAllByName(name, findOnlyNotDeleted);
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection<ICPage> findAllPagesAndTemplates() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((ICPageBMPBean)entity).ejbFindAllPagesAndTemplates();
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
}