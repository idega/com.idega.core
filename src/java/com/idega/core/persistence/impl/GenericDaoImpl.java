package com.idega.core.persistence.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.GenericDao;
import com.idega.core.persistence.Param;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.7 $
 *
 * Last modified: $Date: 2008/05/10 18:30:04 $ by $Author: civilis $
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
	
	@Transactional(readOnly=false)
	public void persist(Object product) {
		entityManager.persist(product);
	}
	
	@Transactional(readOnly=false)
	public Object merge(Object product) {
		
		return entityManager.merge(product);
	}
	
	@Transactional(readOnly=false)
	public <T>T merge(Object product, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		T merged = (T)entityManager.merge(product);
		return merged;
	}
	
	@Transactional(readOnly=true)
	public <T>T find(Class<T> clazz, Object primaryKey) {
		
		return entityManager.find(clazz, primaryKey);
	}
	
	@Transactional(readOnly=true)
	public Query createNamedQuery(String queryName) {
		return entityManager.createNamedQuery(queryName);
	}
	
	@Transactional(readOnly=false)
	public void remove(Object obj) {
		
		entityManager.remove(obj);
	}
	
	@Transactional(readOnly=false)
	public void flush() {
		entityManager.flush();
	}
	
	public <Expected>Expected getSingleResult(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createNamedQuery(namedQueryName);
		
		for (Param param : params) {
			
			q.setParameter(param.getParamName(), param.getParamValue());
		}

		@SuppressWarnings("unchecked")
		Expected result = (Expected)q.getSingleResult();
		
		return result;
	}
	
	public <Expected>List<Expected> getResultList(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {
		
		Query q = getEntityManager().createNamedQuery(namedQueryName);
		
		for (Param param : params) {
			
			q.setParameter(param.getParamName(), param.getParamValue());
		}
		
		@SuppressWarnings("unchecked")
		List<Expected> result = q.getResultList();
		
		return result;
	}
}