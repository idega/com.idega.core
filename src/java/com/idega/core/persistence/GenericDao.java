package com.idega.core.persistence;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Query;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.14 $ Last modified: $Date: 2009/04/16 13:23:09 $ by $Author: civilis $
 */
public interface GenericDao {

	public abstract void persist(Object product);

	public abstract <T> T merge(T product);

	public abstract <T> T getReference(Class<T> clazz, Object primaryKey);

	public abstract <T> T find(Class<T> clazz, Object primaryKey);

	public abstract Query createNamedQuery(String queryName);

	public abstract void remove(Object obj);

	public abstract boolean contains(Object obj);

	/**
	 * first merges and then removed detached entity. This can be used in non transactional method
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

	/**
	 * <p>Queries database for results.</p>
	 * @param query HQL query.
	 * @param expectedReturnType any type you expect to get, for example:
	 * {@link Long} should be fine for numbers, {@link String} for strings,
	 * fits any Hibernate/JPA {@link Entity}. Usually marked as @Entity.
	 * @param params to fill HQL query with and transform it to SQL query.
	 * @return {@link List} of ? extends {@link Entity} (Is JPA or
	 * Hibernate entity).
	 */
	public abstract <Expected> List<Expected> getResultListByInlineQuery(
	        String query, Class<Expected> expectedReturnType, Param... params);

	/**
	 * @param query
	 * @return native inline query by query provided
	 */
	public abstract com.idega.core.persistence.Query getQueryNativeInline(
	        String query);

	/**
	 * @param query Hibernate HQL type query.
	 * @return com.idega.core.persistence.Query query by query provided
	 */
	public abstract com.idega.core.persistence.Query getQueryInline(String query);

	/**
	 * @param queryName
	 * @return hql or native query by query name provided. Query can be defined e.g. using
	 * @javax.persistence.NamedQuery
	 */
	public abstract com.idega.core.persistence.Query getQueryNamed(
	        String queryName);
}