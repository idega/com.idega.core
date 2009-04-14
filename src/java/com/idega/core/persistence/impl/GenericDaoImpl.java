package com.idega.core.persistence.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.DaoFunctions;
import com.idega.core.persistence.GenericDao;
import com.idega.core.persistence.Param;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.31 $ Last modified: $Date: 2009/04/14 14:20:25 $ by $Author: civilis $
 */
@Repository("genericDAO")
public class GenericDaoImpl implements GenericDao {
	
	private EntityManager entityManager;
	
	@Autowired
	private DaoFunctions daoFunctions;
	
	protected EntityManager getEntityManager() {
		
		return entityManager;
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Transactional(readOnly = false)
	public void persist(Object product) {
		entityManager.persist(product);
	}
	
	@Transactional(readOnly = false)
	public <T> T merge(T product) {
		
		return entityManager.merge(product);
	}
	
	@Transactional(readOnly = true)
	public void refresh(Object product) {
		entityManager.refresh(product);
	}
	
	@Transactional(readOnly = true)
	public <T> T find(Class<T> clazz, Object primaryKey) {
		
		return entityManager.find(clazz, primaryKey);
	}
	
	@Transactional(readOnly = true)
	public Query createNamedQuery(String queryName) {
		return entityManager.createNamedQuery(queryName);
	}
	
	@Transactional(readOnly = false)
	public void remove(Object obj) {
		
		entityManager.remove(obj);
	}
	
	@Transactional(readOnly = false)
	public void mergeRemove(Object obj) {
		
		entityManager.remove(entityManager.merge(obj));
	}
	
	public boolean contains(Object obj) {
		
		return getEntityManager().contains(obj);
	}
	
	@Transactional(readOnly = false)
	public void flush() {
		entityManager.flush();
	}
	
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByInlineQuery(String query,
	        Class<Expected> expectedReturnType, Param... params) {
		
		try {
			Query q = getEntityManager().createQuery(query);
			return getDaoFunctions().getSingleResultByQuery(q,
			    expectedReturnType, params);
			
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(String namedQueryName,
	        Class<Expected> expectedReturnType, Param... params) {
		
		try {
			Query q = getEntityManager().createNamedQuery(namedQueryName);
			return getDaoFunctions().getSingleResultByQuery(q,
			    expectedReturnType, params);
			
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineQuery(String query,
	        Class<Expected> expectedReturnType, Param... params) {
		
		Query q = getEntityManager().createQuery(query);
		return getDaoFunctions().getResultListByQuery(q, expectedReturnType,
		    params);
	}
	
	@Transactional(readOnly = true)
	public com.idega.core.persistence.Query getNativeInlineQuery(String query) {
		
		return createNewNativeInlineQuery(query);
	}
	
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineNativeQuery(
	        String query, Class<Expected> expectedReturnType, Param... params) {
		
		return getNativeInlineQuery(query).getResultList(expectedReturnType,
		    params);
	}
	
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineNativeQuery(
	        String query, Class<Expected> expectedReturnType,
	        String mappingName, Param... params) {
		
		return getNativeInlineQuery(query).getResultList(expectedReturnType,
		    mappingName, params);
	}
	
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByInlineNativeQuery(String query,
	        Class<Expected> expectedReturnType, String mappingName,
	        Param... params) {
		
		return getNativeInlineQuery(query).getSingleResult(expectedReturnType,
		    mappingName, params);
	}
	
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByInlineNativeQuery(String query,
	        Class<Expected> expectedReturnType, Param... params) {
		
		return getNativeInlineQuery(query).getSingleResult(expectedReturnType,
		    params);
	}
	
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(String namedQueryName,
	        Class<Expected> expectedReturnType, Param... params) {
		
		Query q = getEntityManager().createNamedQuery(namedQueryName);
		return getDaoFunctions().getResultListByQuery(q, expectedReturnType,
		    params);
	}
	
	protected com.idega.core.persistence.Query createNewNativeInlineQuery(
	        String queryExpression) {
		
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(
		    NativeQueryInlineImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}
	
	protected DaoFunctions getDaoFunctions() {
		return daoFunctions;
	}
}