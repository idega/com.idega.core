package com.idega.core.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.GenericDao;
import com.idega.core.persistence.Param;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.26 $
 * 
 *          Last modified: $Date: 2009/02/17 21:47:26 $ by $Author: civilis $
 */
@Repository("genericDAO")
public class GenericDaoImpl implements GenericDao {

	private EntityManager entityManager;
	private static final Logger logger = Logger.getLogger(GenericDaoImpl.class
			.getName());

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

	protected <Expected> Expected getSingleResultByQuery(Query q,
			Class<Expected> expectedReturnType, Param... params) {

		for (Param param : params) {

			q.setParameter(param.getParamName(), param.getParamValue());
		}

		@SuppressWarnings("unchecked")
		Expected result = (Expected) q.getSingleResult();

		return result;
	}

	@SuppressWarnings("unchecked")
	protected <Expected> List<Expected> getResultListByQuery(Query q,
			Class<Expected> expectedReturnType, Param... params) {

		if (params != null)
			for (Param param : params) {

				q.setParameter(param.getParamName(), param.getParamValue());
			}

		final List<Expected> fresult;

		if (Long.class.equals(expectedReturnType)) {

			List<Object> result = q.getResultList();

			if (result != null) {

				List<Long> longsResult = new ArrayList<Long>(result.size());

				for (Object item : result) {

					if (item instanceof Long) {
						longsResult.add((Long) item);

					} else if (item instanceof Number) {
						longsResult.add(((Number) item).longValue());

					} else {

						logger.log(Level.SEVERE,
								"Not a Number returned, when expected Long data type, returned = "
										+ item.getClass().getName() + ", item="
										+ item);
					}
				}

				fresult = (List<Expected>) longsResult;
			} else {
				fresult = null;
			}

		} else {
			fresult = q.getResultList();
		}

		return fresult;
	}

	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByInlineQuery(String query,
			Class<Expected> expectedReturnType, Param... params) {

		try {
			Query q = getEntityManager().createQuery(query);
			return getSingleResultByQuery(q, expectedReturnType, params);

		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(String namedQueryName,
			Class<Expected> expectedReturnType, Param... params) {

		try {
			Query q = getEntityManager().createNamedQuery(namedQueryName);
			return getSingleResultByQuery(q, expectedReturnType, params);

		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineQuery(String query,
			Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createQuery(query);
		return getResultListByQuery(q, expectedReturnType, params);
	}

	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineNativeQuery(
			String query, Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createNativeQuery(query);
		return getResultListByQuery(q, expectedReturnType, params);
	}

	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineNativeQuery(
			String query, Class<Expected> expectedReturnType,
			String mappingName, Param... params) {

		Query q = getEntityManager().createNativeQuery(query, mappingName);
		return getResultListByQuery(q, expectedReturnType, params);
	}

	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByInlineNativeQuery(String query,
			Class<Expected> expectedReturnType, String mappingName,
			Param... params) {

		try {
			Query q = getEntityManager().createNativeQuery(query, mappingName);
			return getSingleResultByQuery(q, expectedReturnType, params);

		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(String namedQueryName,
			Class<Expected> expectedReturnType, Param... params) {

		Query q = getEntityManager().createNamedQuery(namedQueryName);
		return getResultListByQuery(q, expectedReturnType, params);
	}
}