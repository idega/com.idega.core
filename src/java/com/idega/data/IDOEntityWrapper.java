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
import javax.ejb.EntityContext;
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

	private EntityContext _entityContext;
	private EJBLocalHome _ejbHome;

	private ICItem _mainEntityItem = null;
	
	private ICVersion _selectedVersion = null;

	public IDOEntityWrapper(Object primaryKey) throws IDOLookupException, FinderException {
		construct(primaryKey, null, null, null);
	}

	public IDOEntityWrapper(Object primaryKey, ICLocale locale) throws IDOLookupException, FinderException {
		construct(primaryKey, locale, null, null);
	}

	private void construct(Object primaryKey, ICLocale locale, ICVersion version, String versionName) throws IDOLookupException, FinderException {
		_mainPrimaryKey = primaryKey;
		_locale = locale;
		
		if(version != null){
			_selectedVersion = version;
		} else if(versionName != null){
//			ICVersionHome versionHome = (ICVersionHome)IDOLookup.getHome(ICVersion.class);
//			_selectedVersion = versionHome.
		}
		
		initialize();
	}

	protected abstract Class getMainClass();

	protected abstract boolean useVersions();
	protected abstract boolean useTranslations();
	protected Class getTranslationClass() {
		return null;
	}
	protected Class getVersionClass() {
		return null;
	}

	protected void initialize() throws IDOLookupException, FinderException {
		_mainClass = getMainClass();
		_mainEntityHome = IDOLookup.getHome(_mainClass);
		if (_mainPrimaryKey != null) {
			_mainEntity = _mainEntityHome.findByPrimaryKeyIDO(_mainPrimaryKey);
		}

		if (useTranslations()) {
			_translationClass = getTranslationClass();
			_translationEntityHome = IDOLookup.getHome(_translationClass);
		}

		if (useVersions()) {
			if (!(_mainEntityHome instanceof ICVersionableHome)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Home-Interface must extend " + ICVersionableHome.class);
			}
			if (!(_mainEntity instanceof ICVersionableEntity)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Bean-Interface must extend " + ICVersionableEntity.class);
			}

			_versionClass = getVersionClass();
			_versionEntityHome = IDOLookup.getHome(_versionClass);

			if (!(_versionEntityHome instanceof ICVersionableHome)) {
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
			Object itemPK = ((ICVersionableEntity)_mainEntity).getItemPrimaryKey();
			if (itemPK != null) {
				_mainEntityItem = ((ICItemHome)IDOLookup.getHome(ICItemHome.class)).findByPrimaryKey(itemPK);
				try {
					_currentOpenVersionEntity = ((ICVersionableHome)_versionEntityHome).findEntityOfSpecificVersion(_mainEntityItem.getCurrentOpenVersion());
					//TODO find currentVersionInProgress
					_versionInProgress = _currentOpenVersionEntity;
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}

			if (_currentOpenVersionEntity == null) {
				System.out.println("IDOEntityWrapper: No version is available for this item. ");
				System.out.println("IDOEntityWrapper: MainClass: " + this.getMainClass() + " PK: " + _mainEntity.getPrimaryKey());
				_currentOpenVersionEntity = _mainEntity;
				_versionInProgress = _currentOpenVersionEntity;
			}

			if (!(_currentOpenVersionEntity instanceof ICVersionableEntity)) {
				throw new UnsupportedOperationException("if useVersions() is true, then the Bean-Interface must extend " + ICVersionableEntity.class);
			}
		}

	}

	public ICLocale getICLocale() {
		return _locale;
	}

	public IDOHome getMainEntityHome() {
		return _mainEntityHome;
	}

	public IDOHome getTranslationEntityHome() {
		return _translationEntityHome;
	}

	protected IDOEntity getMainEntity() {
		return _mainEntity;
	}

	protected IDOEntity getTranslationEntity() {
		return _translationEntity;
	}

	protected IDOEntity getCurrentOpenVersionEntity() {
		return _currentOpenVersionEntity;
	}

	protected IDOEntity getVersionInProgress() {
		return _versionInProgress;
	}

	public Object getPrimaryKey() throws EJBException {
		return _mainPrimaryKey;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#store()
	 */
	public void store() throws IDOStoreException {
		if (_mainEntity != null) {
			_mainEntity.store();
		}
		if (_currentOpenVersionEntity != null) {
			_currentOpenVersionEntity.store();
		}
		if (_translationEntity != null) {
			_translationEntity.store();
		}
	}

	//////IDOEntityBean	//////
	public Object ejbCreate() throws CreateException {
		if (_mainEntity != null) {
			if (useVersions()) {
				//TODO:Gummi create new version
			}
		} else {
			_mainEntity = getMainEntityHome().createIDO();
			_mainPrimaryKey = _mainEntity.getPrimaryKey();
		}

		return this;
	}

	public Object ejbFindByPrimaryKey(Object pk) throws FinderException {
		_mainEntity = _mainEntityHome.findByPrimaryKeyIDO(pk);
		_mainPrimaryKey = pk;
		return _mainPrimaryKey;
	}

	public void setEJBLocalHome(javax.ejb.EJBLocalHome ejbHome) {
		_ejbHome = ejbHome;
	}
	/**
	 * Meant to be overrided in subclasses, returns default Integer.class
	 */
	public Class getPrimaryKeyClass() {
		return Integer.class;
	}

	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#remove()
	 */
	public void remove() throws RemoveException, EJBException {
		if (_currentOpenVersionEntity != null) {
			_currentOpenVersionEntity.remove();
		}
		if (_translationEntity != null) {
			_translationEntity.remove();
		}
		if (_mainEntity != null) {
			_mainEntity.remove();
		}
	}

	public java.util.Collection getAttributes() {
		//	TODO - implement
		throw new UnsupportedOperationException("Not yet implemented");
	}
	//////IDOEntityBean ends //////

	//////EntityBean //////
	public void ejbActivate() throws EJBException, RemoteException {
	}

	public void ejbLoad() throws EJBException, RemoteException {
		((IDOEntityBean)_mainEntity).ejbLoad();

		if (_translationEntity != null) {
			((IDOEntityBean)_translationEntity).ejbLoad();
		}
		if (_currentOpenVersionEntity != null) {
			((IDOEntityBean)_currentOpenVersionEntity).ejbLoad();
		}
	}

	public void ejbPassivate() throws EJBException, RemoteException {
		_locale = null;

		_mainClass = null;
		_translationClass = null;

		_mainPrimaryKey = null;

		_mainEntity = null;
		_currentOpenVersionEntity = null;
		_translationEntity = null;

		_mainEntityHome = null;
		_translationEntityHome = null;

	}

	public void ejbRemove() throws javax.ejb.RemoveException, EJBException, RemoteException {
		this.remove();
	}

	public void ejbStore() throws EJBException, RemoteException {
		this.store();
	}

	public void setEntityContext(javax.ejb.EntityContext ctx) throws EJBException, RemoteException {
		this._entityContext = ctx;
	}
	public void unsetEntityContext() throws EJBException, RemoteException {
		this._entityContext = null;
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

	public javax.ejb.EJBLocalHome getEJBLocalHome() {
		if (_ejbHome == null) {
			try {
				_ejbHome = IDOLookup.getHome(this.getClass());
			} catch (Exception e) {
				throw new EJBException("Lookup for home for: " + this.getClass().getName() + " failed. Errormessage was: " + e.getMessage());
			}
		}
		return _ejbHome;
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
