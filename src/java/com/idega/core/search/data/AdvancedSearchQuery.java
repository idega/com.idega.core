/*
 * $Id: AdvancedSearchQuery.java,v 1.2 2005/03/20 11:22:37 eiki Exp $
 * Created on Jan 18, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.data;

import java.util.Map;

import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.presentation.Searcher;


/**
 *
 *  Last modified: $Date: 2005/03/20 11:22:37 $ by $Author: eiki $
 *
 * A basic implementor for an advanced query.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class AdvancedSearchQuery extends SimpleSearchQuery implements SearchQuery {

	/**
	 *
	 */
	public AdvancedSearchQuery() {
		super();
	}

	public AdvancedSearchQuery(Map<String, String> searchParameters) {
		super(searchParameters);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchQuery#isSimpleQuery()
	 */
	@Override
	public boolean isSimpleQuery() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchQuery#isAdvancedQuery()
	 */
	@Override
	public boolean isAdvancedQuery() {
		return true;
	}

	/**
	 * @return the first value in the search parameter map
	 */
	@Override
	public String getSimpleSearchQuery(){
		return getSearchParameters().get(Searcher.DEFAULT_SEARCH_PARAMETER_NAME);
	}

	@Override
	public String toString() {
		return "Advanced search query: " + getSearchParameters();
	}
}
