package com.idega.core.persistence.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.GenericDao;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/01/06 16:57:38 $ by $Author: civilis $
 */
@Repository
@Transactional
public class GenericDaoImpl implements GenericDao {

	private EntityManager entityManager;
	
	protected EntityManager getEntityManager() {
		
		return entityManager;
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.persistence.impl.GenericDao#persist(java.lang.Object)
	 */
	public void persist(Object product) {
		entityManager.persist(product);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.persistence.impl.GenericDao#merge(java.lang.Object)
	 */
	public Object merge(Object product) {
		
		return entityManager.merge(product);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.persistence.impl.GenericDao#merge(java.lang.Object, java.lang.Class)
	 */
	public <T>T merge(Object product, Class<T> clazz) {

		
		@SuppressWarnings("unchecked")
		T merged = (T)entityManager.merge(product);
		return merged;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.persistence.impl.GenericDao#find(java.lang.Class, java.lang.Object)
	 */
	public <T>T find(Class<T> clazz, Object primaryKey) {
		
		return entityManager.find(clazz, primaryKey);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.persistence.impl.GenericDao#createNamedQuery(java.lang.String)
	 */
	public Query createNamedQuery(String queryName) {
		return entityManager.createNamedQuery(queryName);
	}
	
	public void remove(Object obj) {
		
		entityManager.remove(obj);
	}
	
	public void flush() {
		entityManager.flush();
	}
}