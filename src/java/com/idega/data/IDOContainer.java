/*
 * $Id: IDOContainer.java,v 1.27 2007/05/03 14:35:45 thomas Exp $
 * Created in 2002 by Tryggvi Larusson
 * 
 * Copyright (C) 2002-2006 Idega software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.datastructures.HashtableDoubleKeyed;

/**
 * <p>
 * IDOContainer is the central service for the IDO persistence Framework.<br/>
 * This class is a singleton for the application and is the "base center" for
 * getting access to other component of the persistence engine.
 * </p>
 * Last modified: $Date: 2007/05/03 14:35:45 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.27 $
 */
public class IDOContainer implements Singleton {

	// Static variables:
	private static Instantiator instantiator = new Instantiator() {
		public Object getInstance() {
			return new IDOContainer();
		}
	};
	// Instance variables:
	// this instance variable sets if beancaching is active by default for all
	// entities
	private boolean beanCachingActiveByDefault = false;
	private boolean queryCachingActive = false;
	private Map emptyBeanInstances;
	private Map beanCacheMap;
	private Map isBeanCacheActive;
	// These variables were moved from GenericEntity:
	private Map entityAttributes;
	private Map entityStaticInstances;
	private HashtableDoubleKeyed relationshipTables = new HashtableDoubleKeyed();

	protected IDOContainer() {
		// unload
	}

	public static IDOContainer getInstance() {
		return (IDOContainer) SingletonRepository.getRepository().getInstance(IDOContainer.class, instantiator);
	}

	protected Map getBeanMap() {
		if (this.emptyBeanInstances == null) {
			this.emptyBeanInstances = new HashMap();
		}
		return this.emptyBeanInstances;
	}

	/**
	 * <p>
	 * Map with all datasources and hashmaps for beancache for each datasource.<br/>
	 * Keys are datasourceNames and values are Maps for each datasource.
	 * </p>
	 * 
	 * @return
	 */
	protected Map getDatasourcesBeanCacheMaps() {
		if (this.beanCacheMap == null) {
			this.beanCacheMap = new HashMap();
		}
		return this.beanCacheMap;
	}

	/**
	 * <p>
	 * Gets a BeanCacheMap for each datasource, where the key is a
	 * entityInterfacesClass (Class) and value is a IDOBeanCache instance.
	 * </p>
	 * 
	 * @param dataSource
	 * @return
	 */
	protected Map getBeanCacheMap(String dataSource) {
		Map bCacheMap = getDatasourcesBeanCacheMaps();
		Map dataSourceMap = (Map) bCacheMap.get(dataSource);
		if (dataSourceMap == null) {
			dataSourceMap = new HashMap();
			bCacheMap.put(dataSource, dataSourceMap);
		}
		return dataSourceMap;
	}

	protected Map getIsBeanCachActiveMap() {
		if (this.isBeanCacheActive == null) {
			this.isBeanCacheActive = new HashMap();
		}
		return this.isBeanCacheActive;
	}

	protected IDOBeanCache getBeanCache(String datasource, Class entityInterfaceClass) {
		IDOBeanCache idobc = (IDOBeanCache) getBeanCacheMap(datasource).get(entityInterfaceClass);
		if (idobc == null) {
			idobc = new IDOBeanCache(entityInterfaceClass, datasource);
			getBeanCacheMap(datasource).put(entityInterfaceClass, idobc);
		}
		return idobc;
	}

	protected List getFreeBeansList(Class entityInterfaceClass) {
		List l = (List) getBeanMap().get(entityInterfaceClass);
		if (l == null) {
			l = new Vector();
		}
		getBeanMap().put(entityInterfaceClass, l);
		return l;
	}

	protected IDOEntity getFreeBeanInstance(Class entityInterfaceClass) throws Exception {
		IDOEntity entity = null;
		/*
		 * List l = getFreeBeansList(entityInterfaceClass); if(!l.isEmpty()){
		 * entity= (IDOEntity)l.get(0); }
		 */
		if (entity == null) {
			entity = this.instanciateBean(entityInterfaceClass);
		}
		return entity;
	}

	public IDOEntity createEntity(Class entityInterfaceClass) throws javax.ejb.CreateException {
		try {
			IDOEntity entity = null;
			try {
				entity = getFreeBeanInstance(entityInterfaceClass);
			}
			catch (Error e) {
				System.err.println("[idoContainer] : Error creating bean for " + entityInterfaceClass.getName());
				e.printStackTrace();
			}
			((EntityBean) entity).ejbActivate();
			((IDOEntityBean) entity).ejbCreate();
			return entity;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IDOCreateException(e);
		}
	}

	protected IDOEntity instanciateBean(Class entityInterfaceClass) throws Exception {
		Class beanClass = null;
		IDOEntity entity = null;
		try {
			beanClass = IDOLookup.getBeanClassFor(entityInterfaceClass);
		}
		catch (Error t) {
			System.err.println("Error looking up bean class for bean: " + entityInterfaceClass.getName());
			t.printStackTrace();
		}
		try {
			entity = (IDOEntity) beanClass.newInstance();
		}
		catch (Error t) {
			System.err.println("Error instanciating bean class for bean: " + entityInterfaceClass.getName());
			t.printStackTrace();
		}
		return entity;
	}

	/**
	 * To find the data by a primary key (cached if appropriate), usually called
	 * by HomeImpl classes
	 */
	public IDOEntity findByPrimaryKey(Class entityInterfaceClass, Object pk, IDOHome home)
			throws javax.ejb.FinderException {
		return findByPrimaryKey(entityInterfaceClass, pk, null, home);
	}

	/**
	 * To find the data by a primary key (cached if appropriate), usually called
	 * by HomeImpl classes
	 */
	IDOEntity findByPrimaryKey(Class entityInterfaceClass, Object pk, IDOHome home, String dataSourceName)
			throws javax.ejb.FinderException {
		return findByPrimaryKey(entityInterfaceClass, pk, null, home, dataSourceName);
	}

	/**
	 * Workaround to speed up finders where the ResultSet is already created
	 */
	IDOEntity findByPrimaryKey(Class entityInterfaceClass, Object pk, java.sql.ResultSet rs, IDOHome home)
			throws javax.ejb.FinderException {
		return findByPrimaryKey(entityInterfaceClass, pk, rs, home, null);
	}

	/**
	 * Workaround to speed up finders where the ResultSet is already created
	 */
	IDOEntity findByPrimaryKey(Class entityInterfaceClass, Object pk, java.sql.ResultSet rs, IDOHome home,
			String dataSourceName) throws javax.ejb.FinderException {
		try {
			IDOEntity entity = null;
			IDOBeanCache cache = null;
			// boolean useBeanCaching = (dataSourceName==null) &&
			// beanCachingActive(entityInterfaceClass);
			boolean useBeanCaching = beanCachingActive(entityInterfaceClass);
			if (useBeanCaching) {
				cache = this.getBeanCache(dataSourceName, entityInterfaceClass);
				entity = cache.getCachedEntity(pk);
			}
			if (entity == null) {
				entity = this.instanciateBean(entityInterfaceClass);
				if (dataSourceName != null) {
					try {
						((GenericEntity) entity).setDatasource(dataSourceName);
					}
					catch (ClassCastException ce) {
						ce.printStackTrace();
					}
				}
				/**
				 * @todo
				 */
				((IDOEntityBean) entity).ejbFindByPrimaryKey(pk);
				if (rs != null) {
					((GenericEntity) entity).preEjbLoad(rs);
				}
				else {
					((IDOEntityBean) entity).ejbLoad();
				}
				((IDOEntityBean) entity).setEJBLocalHome(home);
				if (useBeanCaching) {
					cache.putCachedEntity(pk, entity);
				}
			}
			return entity;
		}
		catch (Exception e) {
			// e.printStackTrace();
			throw new FinderException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Sets if the beanCaching is active by default for all entities
	 * </p>
	 * 
	 * @param onOrOff
	 */
	public synchronized void setBeanCachingActiveByDefault(boolean active) {
		if (!active) {
			this.flushAllBeanCache();
		}
		this.beanCachingActiveByDefault = active;
	}

	/**
	 * 
	 * @param entityInterfaceClass
	 * @return returns true if bean cashing is active for all entities or if it
	 *         is active for this entityInterfaceClass
	 */
	protected boolean beanCachingActive(Class entityInterfaceClass) {
		Boolean isActive = (Boolean) getIsBeanCachActiveMap().get(entityInterfaceClass);
		if (isActive == null) {
			try {
				IDOEntityDefinition def = IDOLookup.getEntityDefinitionForClass(entityInterfaceClass);
				isActive = def.isBeanCachingActive();
			}
			catch (IDOLookupException t) {
				System.err.println("Error looking up entity defitition for bean: " + entityInterfaceClass.getName());
				t.printStackTrace();
			}
			if (isActive == null) { // still null, use system-default
				isActive = ((this.beanCachingActiveByDefault) ? Boolean.TRUE : Boolean.FALSE);
			}
			getIsBeanCachActiveMap().put(entityInterfaceClass, isActive);
		}
		return isActive.booleanValue();
	}

	public synchronized void setQueryCaching(boolean onOrOff) {
		if (!onOrOff) {
			this.flushAllQueryCache();
		}
		this.isBeanCacheActive = null; // remove at least all elements useing
										// system-default (queryCachingActive)
		this.queryCachingActive = onOrOff;
	}

	protected boolean queryCachingActive(Class entityInterfaceClass) {
		return this.queryCachingActive;
	}

	IDOEntity getPooledInstance(Class entityInterfaceClass) {
		return null;
	}

	public synchronized void flushAllCache() {
		this.flushAllBeanCache();
		this.flushAllQueryCache();
	}

	public synchronized void flushAllBeanCache() {
		// if(this.beanCachingActive){
		Iterator dsIterator = getDatasourcesBeanCacheMaps().keySet().iterator();
		while (dsIterator.hasNext()) {
			String dataSource = (String) dsIterator.next();
			Iterator iter = getBeanCacheMap(dataSource).keySet().iterator();
			while (iter.hasNext()) {
				Class interfaceClass = (Class) iter.next();
				this.getBeanCache(dataSource, interfaceClass).flushAllBeanCache();
			}
		}
		System.out.println("[idoContainer] Flushed all Bean Cache");
		// }
	}

	public synchronized void flushAllQueryCache() {
		if (this.queryCachingActive) {
			Iterator dsIterator = getDatasourcesBeanCacheMaps().keySet().iterator();
			while (dsIterator.hasNext()) {
				String dataSource = (String) dsIterator.next();
				Iterator iter = getBeanCacheMap(dataSource).keySet().iterator();
				while (iter.hasNext()) {
					Class interfaceClass = (Class) iter.next();
					this.getBeanCache(dataSource, interfaceClass).flushAllQueryCache();
				}
			}
			System.out.println("[idoContainer] Flushed all Query Cache");
		}
	}

	/**
	 * Map Used by the IDO Framework and stores a static instance of a
	 * IDOEntityDefinition. This map has as a key a Class instance and a value a
	 * IDOEntityDefinition instance.
	 * 
	 * @return Returns the entityAttributes.
	 */
	Map getEntityDefinitions() {
		if (this.entityAttributes == null) {
			this.entityAttributes = new HashMap();
		}
		return this.entityAttributes;
	}

	/**
	 * Map Used by the IDO Framework and stores a static instance of a
	 * IDOEntity. This map has as a key a Class instance and a value a IDOEntity
	 * instance.
	 */
	Map getEntityStaticInstances() {
		if (this.entityStaticInstances == null) {
			this.entityStaticInstances = new HashMap();
		}
		return this.entityStaticInstances;
	}

	/**
	 * Map used to look up relationships (Many-to-many) between tables.<br>
	 * The keys here are two Strings (the EntityNames or TableNames for the
	 * Entity beans that have the relationship) and as a value an instance of
	 * EntityRelationship.
	 * 
	 * @return the relationship Map
	 */
	HashtableDoubleKeyed getRelationshipTableMap() {
		if (this.relationshipTables == null) {
			this.relationshipTables = new HashtableDoubleKeyed();
		}
		return this.relationshipTables;
	}

	private DatastoreInterfaceManager datastoreInterfaceManager;

	/**
	 * @return Returns the datastoreInterfaceManager.
	 */
	public DatastoreInterfaceManager getDatastoreInterfaceManager() {
		if (this.datastoreInterfaceManager == null) {
			this.datastoreInterfaceManager = new DatastoreInterfaceManager();
		}
		return this.datastoreInterfaceManager;
	}

	/**
	 * @param datastoreInterfaceManager
	 *            The datastoreInterfaceManager to set.
	 */
	public void setDatastoreInterfaceManager(DatastoreInterfaceManager datastoreInterfaceManager) {
		this.datastoreInterfaceManager = datastoreInterfaceManager;
	}
}
