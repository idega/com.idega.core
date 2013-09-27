/*
 * Created on 18.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.localisation.data.ICLocale;
import com.idega.core.version.data.ICItem;
import com.idega.core.version.data.ICItemHome;
import com.idega.core.version.data.ICVersion;
import com.idega.core.version.data.ICVersionableEntity;
import com.idega.core.version.data.ICVersionableHome;

/**
 * Title:		IDOEntityWrappers
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public abstract class IDOEntityWrapper implements IDOEntityBean {

	private static final long serialVersionUID = 1493736860412136280L;

	private ICLocale _locale = null;
	private Object _mainPrimaryKey = null;

	private Class _mainClass = null;
	private Class _translationClass = null;
	private Class _versionClass = null;

	private IDOEntity _mainEntity = null;
	private IDOEntity _translationEntity = null;
	private IDOEntity _currentOpenVersionEntity = null;
	private IDOEntity _versionInProgress = null;

	private IDOHome _mainEntityHome = null;
	private IDOHome _translationEntityHome = null;
	private IDOHome _versionEntityHome = null;

	private EJBLocalHome _ejbHome;

	private ICItem _mainEntityItem = null;

	public IDOEntityWrapper(Object primaryKey) throws IDOLookupException, FinderException {
		construct(primaryKey, null, null, null);
	}

	public IDOEntityWrapper(Object primaryKey, ICLocale locale) throws IDOLookupException, FinderException {
		construct(primaryKey, locale, null, null);
	}

	private void construct(Object primaryKey, ICLocale locale, ICVersion version, String versionName) throws IDOLookupException, FinderException {
		this._mainPrimaryKey = primaryKey;
		this._locale = locale;

		if(version != null){
		} else if(versionName != null){
		}

		initialize();
	}

	protected abstract <E extends IDOEntity> Class<E> getMainClass();

	protected abstract boolean useVersions();
	protected abstract boolean useTranslations();

	protected <T extends IDOEntity> Class<T> getTranslationClass() {
		return _translationClass;
	}
	protected <V extends IDOEntity> Class<V> getVersionClass() {
		return _versionClass;
	}

	protected void initialize() throws IDOLookupException, FinderException {
		this._mainClass = getMainClass();
		this._mainEntityHome = IDOLookup.getHome(getMainClass());
		if (this._mainPrimaryKey != null) {
			this._mainEntity = this._mainEntityHome.findByPrimaryKeyIDO(this._mainPrimaryKey);
		}

		if (useTranslations()) {
			this._translationClass = getTranslationClass();
			this._translationEntityHome = IDOLookup.getHome(getTranslationClass());
		}

		if (useVersions()) {
			if (!(this._mainEntityHome instanceof ICVersionableHome)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Home-Interface must extend " + ICVersionableHome.class);
			}
			if (!(this._mainEntity instanceof ICVersionableEntity)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Bean-Interface must extend " + ICVersionableEntity.class);
			}

			this._versionClass = getVersionClass();
			this._versionEntityHome = IDOLookup.getHome(getVersionClass());

			if (!(this._versionEntityHome instanceof ICVersionableHome)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Home-Interface must extend " + ICVersionableHome.class);
			}
		}

		updateWrapper();
	}

	public void updateWrapper() throws IDOLookupException, FinderException {
		if (useTranslations()) {
			// not yet implemented
		}

		if (useVersions()) {
			Object itemPK = ((ICVersionableEntity) this._mainEntity).getItemPrimaryKey();
			if (itemPK != null) {
				this._mainEntityItem = ((ICItemHome)IDOLookup.getHome(ICItem.class)).findByPrimaryKey(itemPK);
				try {
					this._currentOpenVersionEntity = ((ICVersionableHome)this._versionEntityHome).findEntityOfSpecificVersion(this._mainEntityItem.getCurrentOpenVersion());
					//TODO find currentVersionInProgress
					this._versionInProgress = this._currentOpenVersionEntity;
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}

			if (this._currentOpenVersionEntity == null) {
				System.out.println("IDOEntityWrapper: No version is available for this item. ");
				System.out.println("IDOEntityWrapper: MainClass: " + this.getMainClass() + " PK: " + this._mainEntity.getPrimaryKey());
				this._currentOpenVersionEntity = this._mainEntity;
				this._versionInProgress = this._currentOpenVersionEntity;
			}

			if (!(this._currentOpenVersionEntity instanceof ICVersionableEntity)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Bean-Interface must extend " + ICVersionableEntity.class);
			}
		}

	}

	public ICLocale getICLocale() {
		return this._locale;
	}

	public IDOHome getMainEntityHome() {
		return this._mainEntityHome;
	}

	public IDOHome getTranslationEntityHome() {
		return this._translationEntityHome;
	}

	protected IDOEntity getMainEntity() {
		return this._mainEntity;
	}

	protected IDOEntity getTranslationEntity() {
		return this._translationEntity;
	}

	protected IDOEntity getCurrentOpenVersionEntity() {
		return this._currentOpenVersionEntity;
	}

	protected IDOEntity getVersionInProgress() {
		return this._versionInProgress;
	}

	public Object getPrimaryKey() throws EJBException {
		return this._mainPrimaryKey;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#store()
	 */
	public void store() throws IDOStoreException {
		if (this._mainEntity != null) {
			this._mainEntity.store();
		}
		if (this._currentOpenVersionEntity != null) {
			this._currentOpenVersionEntity.store();
		}
		if (this._translationEntity != null) {
			this._translationEntity.store();
		}
	}

	//////IDOEntityBean	//////
	@Override
	public Object ejbCreate() throws CreateException {
		if (this._mainEntity != null) {
			if (useVersions()) {
				//TODO:Gummi create new version
			}
		} else {
			this._mainEntity = getMainEntityHome().createIDO();
			this._mainPrimaryKey = this._mainEntity.getPrimaryKey();
		}

		return this;
	}

	@Override
	public Object ejbFindByPrimaryKey(Object pk) throws FinderException {
		this._mainEntity = this._mainEntityHome.findByPrimaryKeyIDO(pk);
		this._mainPrimaryKey = pk;
		return this._mainPrimaryKey;
	}

	@Override
	public void setEJBLocalHome(javax.ejb.EJBLocalHome ejbHome) {
		this._ejbHome = ejbHome;
	}
	/**
	 * Meant to be overrided in subclasses, returns default Integer.class
	 */
	@Override
	public Class<Integer> getPrimaryKeyClass() {
		return Integer.class;
	}

	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#remove()
	 */
	public void remove() throws RemoveException, EJBException {
		if (this._currentOpenVersionEntity != null) {
			this._currentOpenVersionEntity.remove();
		}
		if (this._translationEntity != null) {
			this._translationEntity.remove();
		}
		if (this._mainEntity != null) {
			this._mainEntity.remove();
		}
	}

	@Override
	public Collection getAttributes() {
		//	TODO - implement
		throw new UnsupportedOperationException("Not yet implemented");
	}
	//////IDOEntityBean ends //////

	//////EntityBean //////
	@Override
	public void ejbActivate() throws EJBException, RemoteException {
	}

	@Override
	public void ejbLoad() throws EJBException, RemoteException {
		((IDOEntityBean)this._mainEntity).ejbLoad();

		if (this._translationEntity != null) {
			((IDOEntityBean)this._translationEntity).ejbLoad();
		}
		if (this._currentOpenVersionEntity != null) {
			((IDOEntityBean)this._currentOpenVersionEntity).ejbLoad();
		}
	}

	@Override
	public void ejbPassivate() throws EJBException, RemoteException {
		this._locale = null;

		this._mainClass = null;
		this._translationClass = null;

		this._mainPrimaryKey = null;

		this._mainEntity = null;
		this._currentOpenVersionEntity = null;
		this._translationEntity = null;

		this._mainEntityHome = null;
		this._translationEntityHome = null;

	}

	@Override
	public void ejbRemove() throws javax.ejb.RemoveException, EJBException, RemoteException {
		this.remove();
	}

	@Override
	public void ejbStore() throws EJBException, RemoteException {
		this.store();
	}

	@Override
	public void setEntityContext(javax.ejb.EntityContext ctx) throws EJBException, RemoteException {
	}
	@Override
	public void unsetEntityContext() throws EJBException, RemoteException {
	}

	//////EntityBean ends //////

	/**
	 * Default create method for IDO
	 **/
	public Object ejbCreateIDO() throws CreateException {
		return ejbCreate();
	}
	/**
	 * Default create method for IDO
	 **/
	public IDOEntity ejbHomeCreateIDO() throws CreateException {
		throw new UnsupportedOperationException("Not implemented");
		//return ejbCreate();
	}
	/**
	 * Default postcreate method for IDO
	 **/
	public void ejbPostCreateIDO() {
		//does nothing
	}

	/**
	 * Default findByPrimaryKey method for IDO
	 **/
	public Object ejbFindByPrimaryKeyIDO(Object pk) throws FinderException {
		return ejbFindByPrimaryKey(pk);
	}

	////////

	public EJBLocalHome getEJBLocalHome() {
		if (this._ejbHome == null) {
			try {
				this._ejbHome = IDOLookup.getHome(getMainClass());
			} catch (Exception e) {
				throw new EJBException("Lookup for home for: " + this.getClass().getName() + " failed. Error message was: " + e.getMessage());
			}
		}
		return this._ejbHome;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#getEntityDefinition()
	 */
	public IDOEntityDefinition getEntityDefinition() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#isIdentical(javax.ejb.EJBLocalObject)
	 */
	public boolean isIdentical(EJBLocalObject arg0) throws EJBException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
		 * @see com.idega.data.MetadataRelation#getMetaData(java.lang.String)
		 */
	public String getMetaData(String metaDataKey) {
		return ((MetaDataCapable)this.getCurrentOpenVersionEntity()).getMetaData(metaDataKey);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#setMetaDataAttributes(java.util.Hashtable)
	 */
	public void setMetaDataAttributes(java.util.Map metaDataAttribs) {
		((MetaDataCapable)this.getCurrentOpenVersionEntity()).setMetaDataAttributes(metaDataAttribs);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#setMetaData(java.lang.String, java.lang.String)
	 */
	public void setMetaData(String metaDataKey, String metaDataValue) {
		((MetaDataCapable)this.getCurrentOpenVersionEntity()).setMetaData(metaDataKey, metaDataValue);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#setMetaData(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setMetaData(String metaDataKey, String metaDataValue, String metaDataType) {
		((MetaDataCapable)this.getCurrentOpenVersionEntity()).setMetaData(metaDataKey, metaDataValue, metaDataType);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#removeMetaData(java.lang.String)
	 */
	public boolean removeMetaData(String metaDataKey) {
		return ((MetaDataCapable)this.getCurrentOpenVersionEntity()).removeMetaData(metaDataKey);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#getMetaDataAttributes()
	 */
	public java.util.Map getMetaDataAttributes() {
		return ((MetaDataCapable)this.getCurrentOpenVersionEntity()).getMetaDataAttributes();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#getMetaDataTypes()
	 */
	public java.util.Map getMetaDataTypes() {
		return ((MetaDataCapable)this.getCurrentOpenVersionEntity()).getMetaDataTypes();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.MetadataRelation#updateMetaData()
	 */
	public void updateMetaData() throws SQLException {
		((MetaDataCapable)this.getCurrentOpenVersionEntity()).updateMetaData();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityBean#setDatasource(java.lang.String)
	 */
	@Override
	public void setDatasource(String dataSource) {
		((IDOEntityBean)this.getCurrentOpenVersionEntity()).setDatasource(dataSource);
		((IDOEntityBean)this.getMainEntity()).setDatasource(dataSource);
		((IDOEntityBean)this.getTranslationEntity()).setDatasource(dataSource);
		((IDOEntityBean)this.getVersionInProgress()).setDatasource(dataSource);
	}

	/**
	* Decodes a String into a primaryKey Object.
	* Recognises strings of the same format as com.idega.data.GenericEntity#toString() returns.
	*  @see com.idega.data.GenericEntity#toString()
	**/
	public Object decode(String pkString){
		return Integer.decode(pkString);
	}

	/**
	* Decodes a String into a primaryKey Object.
	* Recognises strings of the same format as com.idega.data.GenericEntity#toString() returns.
	*  @see com.idega.data.GenericEntity#toString()
	**/
	public Collection decode(String[] primaryKeys){
		Collection c = new ArrayList();
		for (int i = 0; i < primaryKeys.length; i++) {
			c.add(decode(primaryKeys[i]));
		}
		return c;
	}

}
