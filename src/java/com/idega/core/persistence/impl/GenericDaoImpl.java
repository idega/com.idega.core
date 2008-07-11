package com.idega.core.persistence.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.GenericDao;
import com.idega.core.persistence.Param;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.11 $
 *
 * Last modified: $Date: 2008/07/11 07:50:09 $ by $Author: civilis $
 */
@Repository("genericDAO")
@Transactional(readOnly=true)
public class GenericDaoImpl implements GenericDao {

	private EntityManager entityManager;
	private static final Logger logger = Logger.getLogger(GenericDaoImpl.class.getName());
	
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
	public <T>T merge(T product) {
		
		return entityManager.merge(product);
	}
	
	public void refresh(Object product) {
		entityManager.refresh(product);
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
	
	@SuppressWarnings("unchecked")
	protected <Expected>List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, Param... params) {

		for (Param param : params) {
			
			q.setParameter(param.getParamName(), param.getParamValue());
		}
		
		final List<Expected> fresult;
		
		if(Long.class.equals(expectedReturnType)) {

			@SuppressWarnings("unchecked")
			List<Object> result = q.getResultList();
			
			if(result != null) {
			
				List<Long> longsResult = new ArrayList<Long>(result.size());
				
				for (Object item : result) {
					
					if (item instanceof Long) {
						longsResult.add((Long)item);
						
					} else if (item instanceof BigInteger) {
						
						logger.log(Level.INFO, "Converting BigInteger: " + item + " to Long");
						longsResult.add(Long.valueOf(((BigInteger) item).longValue()));
					} else if (item instanceof BigDecimal) {
						
						logger.log(Level.INFO, "Converting BigDecimal: " + item + " to Long");
						longsResult.add(Long.valueOf(((BigDecimal) item).longValue()));
					} else {
						logger.log(Level.WARNING, "Unsupported -expected long- type="+item.getClass().getName()+", item="+item);
					}
				}
				
				fresult = (List<Expected>)longsResult;
			} else {
				fresult = null;
			}
			
		} else {
			fresult = q.getResultList();
		}
		
		return fresult;
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