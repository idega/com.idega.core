package com.idega.core.persistence;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/14 14:20:54 $ by $Author: civilis $
 */
public interface Query {

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	public abstract <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	public abstract <Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, String cachedRegionName, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	<Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, Collection<Param> params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	<Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, String cachedRegionName, Collection<Param> params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	@Transactional(readOnly = true)
	<Expected> List<Expected> getResultList(Class<Expected> expectedReturnType, String mappingName, String cachedRegionName, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	@Transactional(readOnly = true)
	<Expected> Expected getSingleResult(Class<Expected> expectedReturnType, String mappingName, String cachedRegionName, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	@Transactional(readOnly = true)
	public abstract <Expected> Expected getSingleResult(Class<Expected> expectedReturnType);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	@Transactional(readOnly = true)
	<Expected> Expected getSingleResult(Class<Expected> expectedReturnType, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li>
	 * <li>Sets expected {@link Class} for this Spring request bean</li>
	 * <li>Measures execution time</li></p>
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	@Transactional(readOnly = true)
	<Expected> Expected getSingleResult(Class<Expected> expectedReturnType, String cachedRegionName, Param... params);

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