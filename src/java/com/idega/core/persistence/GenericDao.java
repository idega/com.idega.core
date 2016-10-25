package com.idega.core.persistence;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;

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

	/**
	 * 
	 * <p>Creates named query and executes {@link Query#getResultList()} with some problems fixed:
	 * <li>Creates {@link Query} if does not exist</li>
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @param namedQueryName is name of {@link NamedQuery} of the given entity, not <code>null</code>
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	<Expected> List<Expected> getResultList(String namedQueryName, Class<Expected> expectedReturnType, Param... params);

	/**
	 * 
	 * <p>Creates named query and executes {@link Query#getResultList()} with some problems fixed:
	 * <li>Creates {@link Query} if does not exist</li>
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @param firstResult is index of first line to fetch form data source, skipped if <code>null</code>
	 * @param maxResults is amount of entries to fetch from data source, skipped if <code>null</code>
	 * @param namedQueryName is name of {@link NamedQuery} of the given entity, not <code>null</code>
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	<Expected> List<Expected> getResultList(
			String namedQueryName, 
			Class<Expected> expectedReturnType, 
			Integer firstResult, 
			Integer maxResults, 
			String cachedRegionName, 
			Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Creates {@link Query} if does not exist</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @param namedQueryName is name of {@link NamedQuery} of the given entity, not <code>null</code>
	 * @return entity or <code>null</code> on failure;
	 */
	<Expected> Expected getSingleResult(String namedQueryName,  Class<Expected> expectedReturnType, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Creates {@link Query} if does not exist</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @param query is HQL query, not <code>null</code>
	 * @return entity or <code>null</code> on failure;
	 */
	<Expected> Expected getSingleResultByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params);

	/**
	 * 
	 * <p>Creates named query and executes {@link Query#getResultList()} with some problems fixed:
	 * <li>Creates {@link Query} if does not exist</li>
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @param query is HQL query, not <code>null</code>
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	<Expected> List<Expected> getResultListByInlineQuery(String query, Class<Expected> expectedReturnType, Param... params);
	
	/**
	 * 
	 * <p>Creates named query and executes {@link Query#getResultList()} with some problems fixed:
	 * <li>Creates {@link Query} if does not exist</li>
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @param firstResult is index of first line to fetch form data source, skipped if <code>null</code>
	 * @param maxResults is amount of entries to fetch from data source, skipped if <code>null</code>
	 * @param query is HQL query, not <code>null</code>
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	<Expected> List<Expected> getResultListByInlineQuery(
			String query, 
			Class<Expected> expectedReturnType, 
			Integer firstResult, 
			Integer maxResults, 
			String cachedRegionName, 
			Param... params);

	/**
	 * <p>Creates new Spring bean of {@link com.idega.core.persistence.Query} object of request scope</p>
	 * @param query HQL type data source query.
	 * @return com.idega.core.persistence.Query with queryExpression set.
	 */
	com.idega.core.persistence.Query getQueryNativeInline(String query);

	/**
	 * <p>Creates new Spring bean of {@link com.idega.core.persistence.Query} object of request scope</p>
	 * @param query HQL type data source query.
	 * @return com.idega.core.persistence.Query with queryExpression set.
	 */
	com.idega.core.persistence.Query getQueryInline(String query);

	/**
	 * <p>Creates new Spring bean of {@link com.idega.core.persistence.Query} object of request scope</p>
	 * @param queryName HQL type data source query.
	 * @return com.idega.core.persistence.Query with queryExpression set.
	 */
	com.idega.core.persistence.Query getQueryNamed(String queryName);

	/**
	 *
	 * @see EntityManager#getCriteriaBuilder()
	 */
	CriteriaBuilder getCriteriaBuilder();
}