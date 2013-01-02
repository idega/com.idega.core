package com.idega.core.persistence;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/14 14:20:54 $ by $Author: civilis $
 */
public interface Query {

	@Transactional(readOnly = true)
	public abstract <Expected> List<Expected> getResultList(
	        Class<Expected> expectedReturnType, Param... params);

	@Transactional(readOnly = true)
	public abstract <Expected> List<Expected> getResultList(
	        Class<Expected> expectedReturnType,Collection <Param> params);

	@Transactional(readOnly = true)
	public abstract <Expected> List<Expected> getResultList(
	        Class<Expected> expectedReturnType, String mappingName,
	        Param... params);

	@Transactional(readOnly = true)
	public abstract <Expected> Expected getSingleResult(
	        Class<Expected> expectedReturnType, String mappingName,
	        Param... params);

	@Transactional(readOnly = true)
	public abstract <Expected> Expected getSingleResult(
	        Class<Expected> expectedReturnType, Param... params);

	public abstract void setMaxResults(Integer maxResults);

	public abstract void setFirstResult(Integer firstResult);

	/**
	 * method used in the factory
	 *
	 * @param queryExpression
	 */
	public abstract void setQueryExpression(String queryExpression);

	public void executeUpdate(Param... params);
}