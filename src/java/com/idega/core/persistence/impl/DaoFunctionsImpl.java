package com.idega.core.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.DaoFunctions;
import com.idega.core.persistence.Param;
import com.idega.util.ListUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/14 14:20:54 $ by $Author: civilis $
 */
@Service
@Scope("singleton")
public class DaoFunctionsImpl implements DaoFunctions {
	
	private static final Logger logger = Logger
	        .getLogger(DaoFunctionsImpl.class.getName());
	
	private static final List<Class<?>> IMPLENENTED_CONVERTERS = Collections
	        .unmodifiableList(Arrays.asList(new Class<?>[] { Long.class,
	                Integer.class, Float.class, Byte.class, Double.class,
	                Short.class }));
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public <Expected> List<Expected> getResultListByQuery(Query q,
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
	private <Exptected> List<Exptected> getRealResults(List<Object> results,
	        Class<Exptected> expectedReturnType) {
		if (ListUtil.isEmpty(results)) {
			return null;
		}
		
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
				logger.log(Level.SEVERE, "Can not convert " + result + " ("
				        + result.getClass() + ") to: " + expectedReturnType
				        + ": such converter is not implemented yet!");
			}
		}
		
		return ListUtil.isEmpty(realResults) ? null : realResults;
	}
	
	@Transactional(readOnly = true)
	public <Expected> Expected getSingleResultByQuery(Query q,
	        Class<Expected> expectedReturnType, Param... params) {
		
		for (Param param : params) {
			
			q.setParameter(param.getParamName(), param.getParamValue());
		}
		
		@SuppressWarnings("unchecked")
		Expected result = (Expected) q.getSingleResult();
		
		return result;
	}
}