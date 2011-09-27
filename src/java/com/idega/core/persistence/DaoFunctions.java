package com.idega.core.persistence;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/14 14:20:54 $ by $Author: civilis $
 */
public interface DaoFunctions {

	public abstract <Expected> List<Expected> getResultListByQuery(Query q,
	        Class<Expected> expectedReturnType, Param... params);

	public abstract <Expected> List<Expected> getResultListByQuery(Query q,
	        Class<Expected> expectedReturnType,  Collection<Param> params);

	public abstract <Expected> Expected getSingleResultByQuery(Query q,
	        Class<Expected> expectedReturnType, Param... params);
}