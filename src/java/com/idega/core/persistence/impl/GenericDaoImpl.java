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
 * @version $Revision: 1.8 $
 *
 * Last modified: $Date: 2008/05/16 09:33:36 $ by $Author: civilis $
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
	
	protected <Expected>Expected getSingleResultByQuery(Query q, Class<Expected> expectedReturnType, Param... params) {
		
		for (Param param : params) {
			
			q.setParameter(param.getParamName(), param.getParamValue());
		}

		@SuppressWarnings("unchecked")
		Expected result = (Expected)q.getSingleResult();
		
		return result;
	}
	
	protected <Expected>List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, Param... params) {

		for (Param param : params) {
			
			q.setParameter(param.getParamName(), param.getParamValue());
		}
		
		@SuppressWarnings("unchecked")
		List<Expected> result = q.getResultList();
		
		return result;
	}
	
	
	public <Expected>Expected getSingleResultByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params) {
		
		Query q = getEntityManager().createQuery(query);
		return getSingleResultByQuery(q, expectedReturnType, params);
	}
	
	public <Expected>Expected getSingleResult(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createNamedQuery(namedQueryName);
		return getSingleResultByQuery(q, expectedReturnType, params);
	}
	
	public <Expected>List<Expected> getResultListByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createQuery(query);
		return getResultListByQuery(q, expectedReturnType, params);
	}
	
	public <Expected>List<Expected> getResultList(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createNamedQuery(namedQueryName);
		return getResultListByQuery(q, expectedReturnType, params);
	}
}