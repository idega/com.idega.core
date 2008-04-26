package com.idega.core.persistence;

import java.util.List;

import javax.persistence.Query;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/04/26 02:29:01 $ by $Author: civilis $
 */
public interface GenericDao {

	public abstract void persist(Object product);

	public abstract Object merge(Object product);

	public abstract <T> T merge(Object product, Class<T> clazz);

	public abstract <T> T find(Class<T> clazz, Object primaryKey);

	public abstract Query createNamedQuery(String queryName);

	public abstract void remove(Object obj);
	
	public abstract void flush();
	
	public abstract <Expected>List<Expected> getResultList(String namedQueryName, Class<Expected> expectedReturnType, Param... params);
	
	public abstract <Expected>Expected getSingleResult(String namedQueryName, Class<Expected> expectedReturnType, Param... params);
}