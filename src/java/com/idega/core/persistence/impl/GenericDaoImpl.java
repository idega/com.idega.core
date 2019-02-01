package com.idega.core.persistence.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.DaoFunctions;
import com.idega.core.persistence.GenericDao;
import com.idega.core.persistence.Param;
import com.idega.util.ListUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.33 $ Last modified: $Date: 2009/04/16 13:23:09 $ by $Author: civilis $
 */
@Repository("genericDAO")
public class GenericDaoImpl implements GenericDao {

	private static Logger LOGGER;

	protected Logger getLogger() {
		if (LOGGER == null)
			LOGGER = Logger.getLogger(getClass().getName());
		return LOGGER;
	}

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

	@Override
	@Transactional(readOnly = false)
	public void persist(Object product) {
		entityManager.persist(product);
	}

	@Override
	@Transactional(readOnly = false)
	public <T> T merge(T product) {
		return entityManager.merge(product);
	}

	@Override
	@Transactional(readOnly = true)
	public void refresh(Object product) {
		entityManager.refresh(product);
	}

	@Override
	@Transactional(readOnly = true)
	public <T> T getReference(Class<T> clazz, Object primaryKey) {
		return entityManager.getReference(clazz, primaryKey);
	}

	@Override
	@Transactional(readOnly = true)
	public <T> T find(Class<T> clazz, Object primaryKey) {
		return entityManager.find(clazz, primaryKey);
	}

	@Override
	@Transactional(readOnly = true)
	public Query createNamedQuery(String queryName) {
		return entityManager.createNamedQuery(queryName);
	}

	@Override
	@Transactional(readOnly = false)
	public void remove(Object obj) {
		entityManager.remove(obj);
	}

	@Override
	@Transactional(readOnly = false)
	public void mergeRemove(Object obj) {
		entityManager.remove(entityManager.merge(obj));
	}

	@Override
	public boolean contains(Object obj) {
		return getEntityManager().contains(obj);
	}

	@Override
	@Transactional(readOnly = false)
	public void flush() {
		entityManager.flush();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		if (getEntityManager() != null) {
			return getEntityManager().getCriteriaBuilder();
		}

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params) {
		return getQueryInline(query).getSingleResult(expectedReturnType, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getSingleResult(java.lang.String, java.lang.Class, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {
		return getQueryNamed(namedQueryName).getSingleResult(expectedReturnType, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getResultListByInlineQuery(java.lang.String, java.lang.Class, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params) {
		return getResultListByInlineQuery(query, expectedReturnType, null, null, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getResultListByInlineQuery(java.lang.String, java.lang.Class, java.lang.Integer, java.lang.Integer, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultListByInlineQuery(String query, Class<Expected> expectedReturnType, Integer firstResult, Integer maxResults, String cachedRegionName, Param... params) {
		com.idega.core.persistence.Query hqlQuery = getQueryInline(query);
		if (firstResult != null) {
			hqlQuery.setFirstResult(firstResult);
		}

		if (maxResults != null) {
			hqlQuery.setMaxResults(maxResults);
		}

		return hqlQuery.getResultList(expectedReturnType, cachedRegionName, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getQueryNativeInline(java.lang.String)
	 */
	@Override
	public com.idega.core.persistence.Query getQueryNativeInline(String query) {
		return createNewQueryNativeInline(query);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getQueryInline(java.lang.String)
	 */
	@Override
	public com.idega.core.persistence.Query getQueryInline(String query) {
		return createNewQueryInline(query);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getQueryNamed(java.lang.String)
	 */
	@Override
	public com.idega.core.persistence.Query getQueryNamed(String queryName) {
		return createNewQueryNamed(queryName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getResultList(java.lang.String, java.lang.Class, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(String namedQueryName, Class<Expected> expectedReturnType, Param... params) {
		return getResultList(namedQueryName, expectedReturnType, null, null, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getResultList(java.lang.String, java.lang.Class, java.lang.Integer, java.lang.Integer, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(
			String namedQueryName,
			Class<Expected> expectedReturnType,
			Integer firstResult,
			Integer maxResults,
			String cachedRegionName,
			Param... params
	) {
		com.idega.core.persistence.Query query = getQueryNamed(namedQueryName);
		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query.getResultList(expectedReturnType, cachedRegionName, params);
	}

	/**
	 * <p>Creates new Spring bean of {@link com.idega.core.persistence.Query} object of request scope</p>
	 * @param queryExpression HQL type data source query.
	 * @return com.idega.core.persistence.Query with queryExpression set.
	 */
	protected com.idega.core.persistence.Query createNewQueryNativeInline(String queryExpression) {
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(QueryNativeInlineImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}

	/**
	 * <p>Creates new Spring bean of {@link com.idega.core.persistence.Query} object of request scope</p>
	 * @param queryExpression HQL type data source query.
	 * @return com.idega.core.persistence.Query with queryExpression set.
	 */
	protected com.idega.core.persistence.Query createNewQueryInline(String queryExpression) {
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(QueryInlineImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}

	/**
	 * <p>Creates new Spring bean of {@link com.idega.core.persistence.Query} object of request scope</p>
	 * @param queryExpression HQL type data source query.
	 * @return com.idega.core.persistence.Query with queryExpression set.
	 */
	protected com.idega.core.persistence.Query createNewQueryNamed(String queryExpression) {
		com.idega.core.persistence.Query q = ELUtil.getInstance().getBean(QueryNamedImpl.beanIdentifier);
		q.setQueryExpression(queryExpression);
		return q;
	}

	protected DaoFunctions getDaoFunctions() {
		return daoFunctions;
	}

	protected void initialize(Object object) {
		new HibernateTemplate().initialize(object);
	}

	/**
	 * 
	 * @param root of {@link Entity} to query, created by {@link CriteriaQuery#from(Class)}, not <code>null</code>
	 * @param arguments is {@link Map} of POJO field name and {@link Collection} of values to be matched, skipped <code>null</code>
	 * @return array of parameters suitable for {@link CriteriaQuery#where(Predicate...)} query or empty array on failure
	 */
	private <T> ArrayList<Predicate> getPredicates(
			Root<T> root,
			Map<String, Collection<? extends Serializable>> arguments) {
		ArrayList<Predicate> predicates = new ArrayList<Predicate>();
		if (!MapUtil.isEmpty(arguments) && root != null) {
			for (Entry<String, Collection<? extends Serializable>> entry : arguments.entrySet()) {
				Path<String> attribute = root.get(entry.getKey());
				if (attribute != null && !ListUtil.isEmpty(entry.getValue())) {
					Predicate predicate = attribute.in(entry.getValue());
					if (predicate != null) {
						predicates.add(predicate);
					}
				}
			}
		}

		return predicates;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getQuery(java.lang.Class, java.util.Map, java.util.Collection)
	 */
	@Override
	public <T> CriteriaQuery<T> getQuery(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments,
			Collection<Predicate> additional) {
		CriteriaQuery<T> criteriaQuery = getCriteriaBuilder().createQuery(type);

		/*
		 * Table to select from
		 */
		Root<T> entityRoot = criteriaQuery.from(type);

		/*
		 * Appending arguments
		 */
		ArrayList<Predicate> predicates = getPredicates(entityRoot, arguments);
		if (!ListUtil.isEmpty(predicates)) {
			if (!ListUtil.isEmpty(additional)) {
				predicates.addAll(additional);
			}

			criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
		}

		return criteriaQuery;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getQuery(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> CriteriaQuery<T> getQuery(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments) {
		return getQuery(type, arguments, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getAmountQuery(java.lang.Class, java.util.Map, java.util.Collection)
	 */
	@Override
	public <T> CriteriaQuery<Long> getAmountQuery(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments,
			Collection<Predicate> additional) {
		CriteriaQuery<Long> criteriaQuery = getCriteriaBuilder().createQuery(Long.class);

		/*
		 * Table to select from
		 */
		Root<T> entityRoot = criteriaQuery.from(type);

		/*
		 * Count expression
		 */
		Expression<Long> expression = getCriteriaBuilder().count(entityRoot);

		/*
		 * Select count 
		 */
		criteriaQuery = criteriaQuery.select(expression);

		/*
		 * Appending arguments
		 */
		ArrayList<Predicate> predicates = getPredicates(entityRoot, arguments);
		if (!ListUtil.isEmpty(predicates)) {
			if (!ListUtil.isEmpty(additional)) {
				predicates.addAll(additional);
			}

			criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
		}

		return criteriaQuery;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getAmountQuery(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> CriteriaQuery<Long> getAmountQuery(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments) {
		return getAmountQuery(type, arguments, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#findAll(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> List<T> findAll(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments,
			Collection<Predicate> additional) {
		return findAll(type, arguments, additional, null, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#findAll(java.lang.Class, java.util.Map, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public <T> List<T> findAll(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments,
			Collection<Predicate> additional,
			Integer index,
			Integer amount) {
		if (getEntityManager() != null && type != null) {
			TypedQuery<T> query = getEntityManager().createQuery(getQuery(type, arguments));
			if (index != null) {
				query.setFirstResult(index);
			}

			if (amount != null) {
				query.setMaxResults(amount);
			}

			return query.getResultList();
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#findAll(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> List<T> findAll(
			Class<T> type, 
			Map<String, Collection<? extends Serializable>> arguments) {
		return findAll(type, arguments, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getAmount(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> Long getAmount(
			Class<T> type,
			Map<String, Collection<? extends Serializable>> arguments,
			Collection<Predicate> additional) {
		if (getEntityManager() != null && type != null) {
			CriteriaQuery<Long> criteriaQuery = getAmountQuery(type, arguments, additional);

			TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);

			return query.getSingleResult();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.GenericDao#getAmount(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> Long getAmount(
			Class<T> type, 
			Map<String, Collection<? extends Serializable>> arguments) {
		return getAmount(type, arguments, null);
	}
}