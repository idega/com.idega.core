package com.idega.core.persistence;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/14 14:20:54 $ by $Author: civilis $
 */
public interface DaoFunctions {

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li></p>
	 * @param q is query to execute, not <code>null</code>;
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	<Expected> List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Param... params);

	/**
	 * 
	 * <p>Executes {@link Query#getResultList()}, but with some problems fixed:
	 * <li>Fixes problem of loading more than 1000 entities at once</li>
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li></p>
	 * @param q is query to execute, not <code>null</code>;
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	<Expected> List<Expected> getResultListByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Collection<Param> params);

	/**
	 * 
	 * <p>Executes {@link Query#getSingleResult()}, but with some problems fixed:
	 * <li>Caches query</li>
	 * <li>Checks all parameters</li></p>
	 * @param q is query to execute, not <code>null</code>;
	 * @param expectedReturnType is {@link Class} of JPA entity to return, not <code>null</code>;
	 * @param cachedRegionName is name of existing cached data, skipped if <code>null</code>
	 * @param params to add to {@link Query}, skipped if <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	<Expected> Expected getSingleResultByQuery(Query q, Class<Expected> expectedReturnType, String cachedRegionName, Param... params);
}