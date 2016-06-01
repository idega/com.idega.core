/**
 *
 */
package com.idega.core.builder.data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;


/**
 * <p>
 * TODO tryggvil Describe Type ICPageHomeImpl
 * </p>
 *  Last modified: $Date: 2009/01/14 15:12:24 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */

public class ICPageHomeImpl extends com.idega.data.IDOFactory implements ICPageHome {

	private static final long serialVersionUID = -8153685941087501095L;

	@Override
 protected Class<ICPage> getEntityInterfaceClass(){
  return ICPage.class;
 }

 @Override
public ICPage create() throws javax.ejb.CreateException{
  return (ICPage) super.createIDO();
 }

 @Override
public ICPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 @Override
public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(id);
 }

 @Override
public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(pk);
 }

 @Override
public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


    @Override
	public Collection findByTemplate(Integer templateID) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindByTemplate(templateID);
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
    }

	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findByUri(java.lang.String, int)
	 */
	@Override
	public ICPage findByUri(String pageUri, int domainId) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((ICPageBMPBean)entity).ejbFindByPageUri(pageUri,domainId);
    		this.idoCheckInPooledEntity(entity);
    		return this.findByPrimaryKey(pk);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findByUri(java.lang.String, int)
	 */
	@Override
	public ICPage findExistingByUri(String pageUri, int domainId) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((ICPageBMPBean)entity).ejbFindExistingPageByPageUri(pageUri,domainId);
    		this.idoCheckInPooledEntity(entity);
    		return this.findByPrimaryKey(pk);
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAllPagesWithoutUri()
	 */
	@Override
	public Collection findAllPagesWithoutUri() throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllPagesWithoutUri();
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAllPagesWithoutUri()
	 */
	@Override
	public Collection findAllSimpleTemplates() throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllSimpleTemplates();
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findBySubType(String subType, boolean deleted) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindBySubType(subType, deleted);
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllByPhrase(String phrase, List<String> idsToAvoid) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllByPhrase(phrase, idsToAvoid);
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllByPrimaryKeys(List<String> primaryKeys) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((ICPageBMPBean)entity).ejbFindAllByPrimaryKeys(primaryKeys);
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllByName(String name, boolean findOnlyNotDeleted) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((ICPageBMPBean)entity).ejbFindAllByName(name, findOnlyNotDeleted);
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<ICPage> findAllPagesAndTemplates() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((ICPageBMPBean)entity).ejbFindAllPagesAndTemplates();
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public ICPage findByWebDavUri(String webDavUri) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((ICPageBMPBean)entity).ejbFindByWebDavUri(webDavUri);
    	this.idoCheckInPooledEntity(entity);
    	return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection<ICPage> findAllTemplatesWithWebDavUri() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((ICPageBMPBean)entity).ejbFindAllTemplatesWithWebDavUri();
    	this.idoCheckInPooledEntity(entity);
    	return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAll()
	 */
	@Override
	public Collection<ICPage> findAll() {
		ICPageBMPBean entity = (ICPageBMPBean) this.idoCheckOutPooledEntity();
        Collection<Integer> ids = entity.ejbFindAll();

    	try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get ICPages by primary keys: " + ids);
		}

    	return Collections.emptyList();
	}
}