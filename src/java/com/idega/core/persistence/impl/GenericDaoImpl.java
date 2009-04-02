package com.idega.core.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.idega.util.ListUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.30 $
 * 
 *          Last modified: $Date: 2009/04/02 14:43:14 $ by $Author: valdas $
 */
@Repository("genericDAO")
public class GenericDaoImpl implements GenericDao {

	private EntityManager entityManager;
	private static final Logger logger = Logger.getLogger(GenericDaoImpl.class
			.getName());

	private static final List<Class<?>> IMPLENENTED_CONVERTERS = Collections.unmodifiableList(Arrays.asList(new Class<?>[] {
			Long.class,
			Integer.class,
			Float.class,
			Byte.class,
			Double.class,
			Short.class
	}));

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
		
		if (IMPLENENTED_CONVERTERS.contains(expectedReturnType)) {
			fresult = getRealResults(q.getResultList(), expectedReturnType);
		} else {
			fresult = q.getResultList();
		}

		return fresult;
	}
	
	@SuppressWarnings("unchecked")
	private <Exptected> List<Exptected> getRealResults(List<Object> results, Class<Exptected> expectedReturnType) {
		if (ListUtil.isEmpty(results)) {
			return null;
		}
		
		List<Exptected> realResults = new ArrayList<Exptected>();
		for (Object result: results) {
			if (expectedReturnType.isInstance(result)) {
				realResults.add((Exptected) result);
			} else if (result instanceof Number) {
				Number number = (Number) result;
				if (expectedReturnType.equals(Long.class)) {
					//	Long
					realResults.add((Exptected) Long.valueOf(number.longValue()));
				} else if (expectedReturnType.equals(Integer.class)) {
					//	Integer
					realResults.add((Exptected) Integer.valueOf(number.intValue()));
				} else if (expectedReturnType.equals(Float.class)) {
					//	Float
					realResults.add((Exptected) Float.valueOf(number.floatValue()));
				} else if (expectedReturnType.equals(Byte.class)) {
					//	Byte
					realResults.add((Exptected) Byte.valueOf(number.byteValue()));
				} else if (expectedReturnType.equals(Double.class)) {
					//	Double
					realResults.add((Exptected) Double.valueOf(number.doubleValue()));
				} else if (expectedReturnType.equals(Short.class)) {
					//	Short
					realResults.add((Exptected) Short.valueOf(number.shortValue()));
				}
			} else {
				logger.log(Level.SEVERE, "Can not convert " + result + " ("+result.getClass()+") to: " + expectedReturnType +
																												": such converter is not implemented yet!");
			}
		}
		
		return ListUtil.isEmpty(realResults) ? null : realResults;
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