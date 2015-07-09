package com.idega.core.persistence.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

	private static final Logger logger = Logger.getLogger(DaoFunctionsImpl.class.getName());

	private static final List<Class<?>> IMPLEMENTED_CONVERTERS = Collections.unmodifiableList(Arrays.asList(new Class<?>[] {
			Long.class,
			Integer.class,
			Float.class,
			Byte.class,
			Double.class,
			Short.class
	}));

	private void setParameters(Query q, Param... params) {
		if (ArrayUtil.isEmpty(params))
			return;

		setParameters(q, Arrays.asList(params));
	}

	private void setParameters(Query q, Collection<Param> params) {
		if (ListUtil.isEmpty(params))
			return;

		for (Param param : params) {
			String name = param.getParamName();
			Object value = param.getParamValue();
			if (value instanceof String && (CoreConstants.Y.equals(value) || CoreConstants.N.equals(value))) {
				logger.info("Changing type of parameter (name: " + name + ", value: '" + value + "'): from " + value.getClass().getName() + " to " + Character.class.getName());
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

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public <Expected> List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Param... params) {
		doPrepareForCaching(q, expectedReturnType, cachedRegionName);
		setParameters(q, params);

		final List<Expected> fresult;
		if (IMPLEMENTED_CONVERTERS.contains(expectedReturnType)) {
			fresult = getRealResults(q.getResultList(), expectedReturnType);
		} else {
			fresult = q.getResultList();
		}

		return fresult;
	}

	@SuppressWarnings("unchecked")
	private <Exptected> List<Exptected> getRealResults(List<Object> results, Class<Exptected> expectedReturnType) {
		if (ListUtil.isEmpty(results))
			return null;

		List<Exptected> realResults = new ArrayList<Exptected>();
		for (Object result : results) {
			if (expectedReturnType.isInstance(result)) {
				realResults.add((Exptected) result);
			} else if (result instanceof Number) {
				Number number = (Number) result;
				if (expectedReturnType.equals(Long.class)) {
					// Long
					realResults.add((Exptected) Long
					        .valueOf(number.longValue()));
				} else if (expectedReturnType.equals(Integer.class)) {
					// Integer
					realResults.add((Exptected) Integer.valueOf(number
					        .intValue()));
				} else if (expectedReturnType.equals(Float.class)) {
					// Float
					realResults.add((Exptected) Float.valueOf(number
					        .floatValue()));
				} else if (expectedReturnType.equals(Byte.class)) {
					// Byte
					realResults.add((Exptected) Byte
					        .valueOf(number.byteValue()));
				} else if (expectedReturnType.equals(Double.class)) {
					// Double
					realResults.add((Exptected) Double.valueOf(number
					        .doubleValue()));
				} else if (expectedReturnType.equals(Short.class)) {
					// Short
					realResults.add((Exptected) Short.valueOf(number
					        .shortValue()));
				}
			} else {
				String message = "Can not convert " + result + " (" + result.getClass() + ") to: " + expectedReturnType +
						": such converter is not implemented yet!";
				logger.warning(message);
				CoreUtil.sendExceptionNotification(message, null);
			}
		}

		return ListUtil.isEmpty(realResults) ? null : realResults;
	}

	@Override
	@Transactional(readOnly = true, noRollbackFor = NoResultException.class)
	public <Expected> Expected getSingleResultByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Param... params) {
		doPrepareForCaching(q, expectedReturnType, cachedRegionName);
		setParameters(q, params);

		@SuppressWarnings("unchecked")
		Expected result = (Expected) q.getSingleResult();

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public <Expected> List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Collection<Param> params) {
		doPrepareForCaching(q, expectedReturnType, cachedRegionName);
		setParameters(q, params);

		final List<Expected> fresult;
		if (IMPLEMENTED_CONVERTERS.contains(expectedReturnType)) {
			fresult = getRealResults(q.getResultList(), expectedReturnType);
		} else {
			fresult = q.getResultList();
		}

		return fresult;
	}
}