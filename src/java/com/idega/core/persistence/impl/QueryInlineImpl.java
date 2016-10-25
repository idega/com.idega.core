package com.idega.core.persistence.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.business.DefaultSpringBean;
import com.idega.core.persistence.DaoFunctions;
import com.idega.core.persistence.Param;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/16 08:36:53 $ by $Author: civilis $
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Service(QueryInlineImpl.beanIdentifier)
public class QueryInlineImpl extends DefaultSpringBean implements com.idega.core.persistence.Query {

	public static final String beanIdentifier = "QueryInlineImpl";

	@PersistenceContext
	private EntityManager entityManager;
	protected Query query;
	private String queryExpression;
	private String mappingName;
	private Class<?> expectedReturnType;
	private Integer maxResults;
	private Integer firstResult;

	@Autowired
	private DaoFunctions daoFunctions;

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getResultList(java.lang.Class, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, Param... params) {
		return getResultList(expectedReturnType, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getResultList(java.lang.Class, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, String cachedRegionName, Param... params) {
		setExpectedReturnType(expectedReturnType);

		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		try {
			return getDaoFunctions().getResultListByQuery(getQuery(), expectedReturnType, cachedRegionName, params);
		} finally {
			if (measure) {
				CoreUtil.doDebugSQL(start, System.currentTimeMillis(), getQueryExpression(), Arrays.asList(params));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getResultList(java.lang.Class, java.lang.String, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, String mappingName, String cachedRegionName, Param... params) {
		setMappingName(mappingName);
		setExpectedReturnType(expectedReturnType);

		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		try {
			return getDaoFunctions().getResultListByQuery(getQuery(), expectedReturnType, cachedRegionName, params);
		} catch (Exception e) {
			CoreUtil.sendExceptionNotification("Error executing query: " + getQueryExpression() + " with parameters: " + Arrays.asList(params), e);
			throw new RuntimeException(e);
		} finally {
			if (measure) {
				CoreUtil.doDebugSQL(start, System.currentTimeMillis(), getQueryExpression(), Arrays.asList(params));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getSingleResult(java.lang.Class, java.lang.String, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(Class<Expected> expectedReturnType, String mappingName, String cachedRegionName, Param... params) {
		setMappingName(mappingName);
		setExpectedReturnType(expectedReturnType);

		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		try {
			return getDaoFunctions().getSingleResultByQuery(getQuery(), expectedReturnType, cachedRegionName, params);
		} catch (NoResultException e) {
			return null;
		} finally {
			if (measure) {
				CoreUtil.doDebugSQL(start, System.currentTimeMillis(), getQueryExpression(), Arrays.asList(params));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getSingleResult(java.lang.Class)
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(Class<Expected> expectedReturnType) {
		return getSingleResult(expectedReturnType, CoreConstants.EMPTY);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getSingleResult(java.lang.Class, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(Class<Expected> expectedReturnType, Param... params) {
		return getSingleResult(expectedReturnType, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getSingleResult(java.lang.Class, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResult(Class<Expected> expectedReturnType, String cachedRegionName, Param... params) {
		setExpectedReturnType(expectedReturnType);

		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		try {
			return getDaoFunctions().getSingleResultByQuery(getQuery(), expectedReturnType, cachedRegionName, params);
		} catch (NoResultException e) {
			return null;
		} finally {
			if (measure) {
				CoreUtil.doDebugSQL(start, System.currentTimeMillis(), getQueryExpression(), Arrays.asList(params));
			}
		}
	}

	protected Query getQuery() {
		if (query == null) {
			if (getMappingName() != null) {
				getLogger().warning("Mapping name set for hql inline query. This can't be used, ignoring");
			}

			query = getEntityManager().createQuery(getQueryExpression());

			if (getMaxResults() != null)
				query.setMaxResults(getMaxResults());
			if (getFirstResult() != null)
				query.setFirstResult(getFirstResult());
		}

		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	protected Class<?> getExpectedReturnType() {
		return expectedReturnType;
	}

	protected String getMappingName() {
		return mappingName;
	}

	protected void setExpectedReturnType(Class<?> expectedReturnType) {
		this.expectedReturnType = expectedReturnType;
	}

	protected void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	protected String getQueryExpression() {
		return queryExpression;
	}

	@Override
	public void setQueryExpression(String queryExpression) {
		this.queryExpression = queryExpression;
	}

	protected DaoFunctions getDaoFunctions() {
		return daoFunctions;
	}

	protected Integer getMaxResults() {
		return maxResults;
	}

	protected Integer getFirstResult() {
		return firstResult;
	}

	@Override
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	@Override
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getResultList(java.lang.Class, java.util.Collection)
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, Collection<Param> params) {
		return getResultList(expectedReturnType, null, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.Query#getResultList(java.lang.Class, java.lang.String, java.util.Collection)
	 */
	@Override
	@Transactional(readOnly = true)
	public <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, String cachedRegionName, Collection<Param> params) {
		setExpectedReturnType(expectedReturnType);

		boolean measure = CoreUtil.isSQLMeasurementOn();
		long start = measure ? System.currentTimeMillis() : 0;
		try {
			return getDaoFunctions().getResultListByQuery(getQuery(), expectedReturnType, cachedRegionName, params);
		} finally {
			if (measure) {
				CoreUtil.doDebugSQL(start, System.currentTimeMillis(), getQueryExpression(), params);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void executeUpdate(Param... params) {
		Query query = getQuery();

		if (params != null)
			for (Param param : params) {
				query.setParameter(param.getParamName(), param.getParamValue());
			}

		query.executeUpdate();
	}
}