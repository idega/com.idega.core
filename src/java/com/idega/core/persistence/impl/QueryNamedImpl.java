package com.idega.core.persistence.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/16 08:36:53 $ by $Author: civilis $
 */
@Service(QueryNamedImpl.beanIdentifier)
@Scope("prototype")
public class QueryNamedImpl extends QueryInlineImpl {
	
	public static final String beanIdentifier = "QueryNamedImpl";
	
	protected Query getQuery() {
		
		if (query == null) {
			
			if (getMappingName() != null) {
				Logger
				        .getLogger(getClass().getName())
				        .log(Level.WARNING,
				            "Mapping name set for hql named query. This can't be used, ignoring");
			}
			
			query = getEntityManager().createNamedQuery(getQueryExpression());
			
			if (getMaxResults() != null)
				query.setMaxResults(getMaxResults());
			if (getFirstResult() != null)
				query.setFirstResult(getFirstResult());
		}
		
		return query;
	}
}