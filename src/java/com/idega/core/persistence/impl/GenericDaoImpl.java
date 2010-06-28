package com.idega.core.persistence.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.DaoFunctions;
import com.idega.core.persistence.GenericDao;
import com.idega.core.persistence.Param;
import com.idega.util.CoreUtil;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.33 $ Last modified: $Date: 2009/04/16 13:23:09 $ by $Author: civilis $
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
		
		return getQueryInline(query)
		        .getSingleResult(expectedReturnType, params);
	}
	
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(String namedQueryName,
	        Class<Expected> expectedReturnType, Param... params) {
		
		return getQueryNamed(namedQueryName).getSingleResult(
		    expectedReturnType, params);
	}
	
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params) {
		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		
		try {
			return getQueryInline(query).getResultList(expectedReturnType, params);
		} finally {
			if (measure) {
				long end = System.currentTimeMillis();
				Logger.getLogger(this.getClass().getName()).info("Query '" + query + "' executed in " + (end - start) + " ms");
			}
		}
	}
	
	public com.idega.core.persistence.Query getQueryNativeInline(String query) {
		
		return createNewQueryNativeInline(query);
	}
	
	public com.idega.core.persistence.Query getQueryInline(String query) {
		
		return createNewQueryInline(query);
	}
	
	public com.idega.core.persistence.Query getQueryNamed(String queryName) {
		
		return createNewQueryNamed(queryName);
	}
	
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {
		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		
		try {
			return getQueryNamed(namedQueryName).getResultList(expectedReturnType, params);
		} finally {
			if (measure) {
				long end = System.currentTimeMillis();
				Logger.getLogger(this.getClass().getName()).info("Query '" + namedQueryName + "' executed in " + (end - start) + " ms");
			}
		}
	}
	
	protected com.idega.core.persistence.Query createNewQueryNativeInline(
	        String queryExpression) {
		
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(
		    QueryNativeInlineImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}
	
	protected com.idega.core.persistence.Query createNewQueryInline(
	        String queryExpression) {
		
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(
		    QueryInlineImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}
	
	protected com.idega.core.persistence.Query createNewQueryNamed(
	        String queryExpression) {
		
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(
		    QueryNamedImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}
	
	protected DaoFunctions getDaoFunctions() {
		return daoFunctions;
	}
}