package com.idega.core.persistence;

import java.util.List;

import javax.persistence.Query;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.9 $
 * 
 *          Last modified: $Date: 2009/02/02 13:34:53 $ by $Author: donatas $
 */
public interface GenericDao {

	public abstract void persist(Object product);

	public abstract <T> T merge(T product);

	public abstract <T> T find(Class<T> clazz, Object primaryKey);

	public abstract Query createNamedQuery(String queryName);

	public abstract void remove(Object obj);

	public abstract boolean contains(Object obj);

	/**
	 * first merges and then removed detached entity. This can be used in non
	 * transactional method
	 * 
	 * @param obj
	 */
	public abstract void mergeRemove(Object obj);

	public abstract void flush();

	public abstract void refresh(Object product);

	public abstract <Expected> List<Expected> getResultList(
			String namedQueryName, Class<Expected> expectedReturnType,
			Param... params);

	public abstract <Expected> Expected getSingleResult(String namedQueryName,
			Class<Expected> expectedReturnType, Param... params);

	public abstract <Expected> Expected getSingleResultByInlineQuery(
			String query, Class<Expected> expectedReturnType, Param... params);

	public abstract <Expected> List<Expected> getResultListByInlineQuery(
			String query, Class<Expected> expectedReturnType, Param... params);

	public abstract <Expected> List<Expected> getResultListByInlineNativeQuery(
			String query, Class<Expected> expectedReturnType, Param... params);
	
	public abstract <Expected> List<Expected> getResultListByInlineNativeQuery(
			String query, Class<Expected> expectedReturnType, String mappingName, Param... params);
}