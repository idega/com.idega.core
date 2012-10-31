package com.idega.core.persistence.impl;

import javax.persistence.Query;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/04/16 08:36:45 $ by $Author: civilis $
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Service(QueryNativeInlineImpl.beanIdentifier)
public class QueryNativeInlineImpl extends QueryInlineImpl {

	public static final String beanIdentifier = "QueryNativeInlineImpl";

	@Override
	protected Query getQuery() {
		if (query == null) {
			if (getMappingName() != null) {
				query = getEntityManager().createNativeQuery(getQueryExpression(), getMappingName());
			} else {
				query = getEntityManager().createNativeQuery(getQueryExpression(), getExpectedReturnType());
			}

			if (getMaxResults() != null)
				query.setMaxResults(getMaxResults());
			if (getFirstResult() != null)
				query.setFirstResult(getFirstResult());
		}

		return query;
	}
}