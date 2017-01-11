package com.idega.core.persistence.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Cacheable;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.DaoFunctions;
import com.idega.core.persistence.Param;
import com.idega.user.data.bean.Group;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.DBUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $ Last modified: $Date: 2009/05/22 04:55:13 $ by $Author: laddi $
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DaoFunctionsImpl implements DaoFunctions {

	private static final Logger LOGGER = Logger.getLogger(DaoFunctionsImpl.class.getName());

	private static final List<Class<?>> IMPLEMENTED_CONVERTERS = Collections.unmodifiableList(Arrays.asList(new Class<?>[] {
			Long.class,
			Integer.class,
			Float.class,
			Byte.class,
			Double.class,
			Short.class
	}));

	/**
	 *
	 * <p>For each parameter calls {@link Query#setParameter(String, Object)}, checks parameters before action</p>
	 * @param q is query for these parameters, not <code>null</code>
	 * @param params to set, skipped if <code>null</code>
	 */
	private void setParameters(Query q, Param... params) {
		if (ArrayUtil.isEmpty(params))
			return;

		setParameters(q, Arrays.asList(params));
	}

	/**
	 *
	 * <p>For each parameter calls {@link Query#setParameter(String, Object)}, checks parameters before action</p>
	 * @param q is query for these parameters, not <code>null</code>
	 * @param params to set, not <code>null</code>
	 */
	private void setParameters(Query q, Collection<Param> params) {
		if (ListUtil.isEmpty(params))
			return;

		for (Param param : params) {
			String name = param.getParamName();
			Object value = param.getParamValue();
			if (value instanceof String && (CoreConstants.Y.equals(value) || CoreConstants.N.equals(value))) {
				LOGGER.info("Changing type of parameter (name: " + name + ", value: '" + value + "'): from " + value.getClass().getName() + " to " + Character.class.getName());
				value = Character.valueOf(((String) value).charAt(0));
			}

			q.setParameter(name, value);
		}
	}

	private <Expected> void doPrepareForCaching(Query q, Class<Expected> expectedReturnType, String cachedRegionName) {
		if (StringUtil.isEmpty(cachedRegionName)) {
			Annotation cacheableDeclaration = expectedReturnType.getAnnotation(Cacheable.class);
			if (cacheableDeclaration != null) {
				cachedRegionName = expectedReturnType.getName() + "Cache";
			}
		}
		if (!StringUtil.isEmpty(cachedRegionName)) {
			DBUtil.getInstance().doInitializeCaching(q, cachedRegionName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.DaoFunctions#getResultListByQuery(javax.persistence.Query, java.lang.Class, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	public <Expected> List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Param... params) {
		return getResultListByQuery(new ArrayList<Expected>(), q, expectedReturnType, cachedRegionName, params);
	}

	private class QueryParams<Expected> {

		private Collection<Param> unusedParams = null, usableParams = null;

		private Param[] params;

		private QueryParams(Param... params) {
			this.params = params;
		}
		private QueryParams(Collection<Param> params) {
			this.params = ArrayUtil.convertListToArray(params);
		}

		private <V> void doPrepareParameters() {
			if (!ArrayUtil.isEmpty(params)) {
				unusedParams = new ArrayList<Param>();
				usableParams = new ArrayList<Param>();
				for (Param param: params) {
					Object value = param.getParamValue();
					if (value instanceof Collection) {
						@SuppressWarnings("unchecked")
						List<V> paramValue = new ArrayList<V>((Collection<V>) value);
						if (paramValue.size() > 5000) {
							usableParams.add(new Param(
									param.getParamName(),
									paramValue.subList(0, 1000)));
							unusedParams.add(new Param(
									param.getParamName(),
									paramValue.subList(1000, paramValue.size())));
						} else {
							usableParams.add(param);
						}

					} else {
						usableParams.add(param);
					}
				}
			}
		}

		public Collection<Param> getUnusedParams() {
			return unusedParams;
		}

		public Param[] getUsableParams() {
			return ArrayUtil.convertListToArray(usableParams);
		}

		public boolean isLoadInMultipleSteps() {
			return !ListUtil.isEmpty(unusedParams);
		}

	}

	/**
	 *
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li></p>
	 * @param results is {@link List} of existing results to append, skipped if <code>null</code>
	 * @param q is query to execute, not <code>null</code>;
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	private <Expected, V> List<Expected> getResultListByQuery(
			List<Expected> results,
			Query q,
			Class<Expected> expectedReturnType,
			String cachedRegionName,
			Param... params) {
		if (results == null) {
			results = new ArrayList<Expected>();
		}

		QueryParams<Expected> queryParams = new QueryParams<>(params);
		queryParams.doPrepareParameters();

		doPrepareForCaching(q, expectedReturnType, cachedRegionName);
		setParameters(q, queryParams.getUsableParams());

		List<Expected> tmpResults = null;
		if (IMPLEMENTED_CONVERTERS.contains(expectedReturnType)) {
			tmpResults = getRealResults(q.getResultList(), expectedReturnType);
		} else {
			tmpResults = q.getResultList();
		}

		if (ListUtil.isEmpty(tmpResults)) {
			return results;
		}

		results.addAll(tmpResults);
		if (queryParams.isLoadInMultipleSteps()) {
			return getResultListByQuery(
					results, q,
					expectedReturnType,
					cachedRegionName,
					ArrayUtil.convertListToArray(queryParams.getUnusedParams()));
		}

		return results;
	}

	/**
	 *
	 * @param results is {@link Collection} of objects to convert to more specified objects, not <code>null</code>
	 * @param expectedReturnType is {@link Class} to cast objects to, not <code>null</code>;
	 * @return results casted to expectedReturnType objects or <code>null</code> on failure;
	 */
	@SuppressWarnings("unchecked")
	private <Exptected> List<Exptected> getRealResults(List<Object> results, Class<Exptected> expectedReturnType) {
		if (ListUtil.isEmpty(results)) {
			return null;
		}

		Object tmp = null;
		List<Exptected> realResults = new ArrayList<Exptected>();
		try {
			for (Object result: results) {
				tmp = result;

				if (expectedReturnType.isInstance(result)) {
					realResults.add((Exptected) result);

				} else if (result instanceof Number) {
					Number number = (Number) result;
					if (expectedReturnType.equals(Long.class)) {
						// Long
						realResults.add((Exptected) Long.valueOf(number.longValue()));

					} else if (expectedReturnType.equals(Integer.class)) {
						// Integer
						realResults.add((Exptected) Integer.valueOf(number .intValue()));

					} else if (expectedReturnType.equals(Float.class)) {
						// Float
						realResults.add((Exptected) Float.valueOf(number.floatValue()));

					} else if (expectedReturnType.equals(Byte.class)) {
						// Byte
						realResults.add((Exptected) Byte.valueOf(number.byteValue()));

					} else if (expectedReturnType.equals(Double.class)) {
						// Double
						realResults.add((Exptected) Double.valueOf(number.doubleValue()));

					} else if (expectedReturnType.equals(Short.class)) {
						// Short
						realResults.add((Exptected) Short.valueOf(number.shortValue()));
					}

				} else if (result instanceof Group && expectedReturnType.equals(Integer.class)) {
					//	Group to Integer
					realResults.add((Exptected) ((Group) result).getID());

				} else {
					String message = null;
					if (result != null) {
						message = getClass().getName() + ": can not convert " + result + " (" + result.getClass() + ") to: " + expectedReturnType + ": such converter is not implemented yet!";
					} else {
						message = getClass().getName() + ": can not convert null to: " + expectedReturnType + ": such converter is not implemented yet!";
					}
					LOGGER.warning(message);
					CoreUtil.sendExceptionNotification(message, null);
				}
			}
		} catch (Exception e) {
			String message = null;
			if (tmp != null) {
				message = "Error converting " + tmp + " (" + tmp.getClass() + ") to: " + expectedReturnType + ": such converter is not implemented yet!";
			} else {
				message = "Error converting null to: " + expectedReturnType + ": such converter is not implemented yet!";
			}
			LOGGER.log(Level.WARNING, message, e);
			CoreUtil.sendExceptionNotification(message, e);
		}

		return ListUtil.isEmpty(realResults) ? null : realResults;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.DaoFunctions#getSingleResultByQuery(javax.persistence.Query, java.lang.Class, java.lang.String, com.idega.core.persistence.Param[])
	 */
	@Override
	@Transactional(readOnly = true, noRollbackFor = NoResultException.class)
	public <Expected> Expected getSingleResultByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Param... params) {
		doPrepareForCaching(q, expectedReturnType, cachedRegionName);
		setParameters(q, params);

		@SuppressWarnings("unchecked")
		Expected result = (Expected) q.getSingleResult();

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.persistence.DaoFunctions#getResultListByQuery(javax.persistence.Query, java.lang.Class, java.lang.String, java.util.Collection)
	 */
	@Override
	public <Expected> List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Collection<Param> params) {
		return getResultListByQuery(new ArrayList<Expected>(), q, expectedReturnType, cachedRegionName, params);
	}

	/**
	 *
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li></p>
	 * @param results is {@link List} of existing results to append, skipped if <code>null</code>
	 * @param q is query to execute, not <code>null</code>;
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	private <Expected> List<Expected> getResultListByQuery(
			List<Expected> results,
			Query q,
			Class<Expected> expectedReturnType,
			String cachedRegionName,
			Collection<Param> params) {
		if (results == null) {
			results = new ArrayList<Expected>();
		}

		QueryParams<Expected> queryParams = new QueryParams<>(params);
		queryParams.doPrepareParameters();

		doPrepareForCaching(q, expectedReturnType, cachedRegionName);
		setParameters(q, queryParams.getUsableParams());

		List<Expected> tmpResults = null;
		if (IMPLEMENTED_CONVERTERS.contains(expectedReturnType)) {
			tmpResults = getRealResults(q.getResultList(), expectedReturnType);
		} else {
			tmpResults = q.getResultList();
		}

		if (ListUtil.isEmpty(tmpResults)) {
			return results;
		}

		results.addAll(tmpResults);
		if (queryParams.isLoadInMultipleSteps()) {
			return getResultListByQuery(results, q, expectedReturnType, cachedRegionName, queryParams.getUnusedParams());
		}

		return results;
	}
}