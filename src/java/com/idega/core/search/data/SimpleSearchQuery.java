/*
 * $Id: SimpleSearchQuery.java,v 1.1 2005/01/19 01:48:30 eiki Exp $
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


/**
 * 
 *  Last modified: $Date: 2005/01/19 01:48:30 $ by $Author: eiki $
 * 
 * A basic implementor for an simple query.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1 $
 */
public class SimpleSearchQuery implements SearchQuery {

	private Map searchParameters;
	
	/**
	 * 
	 */
	public SimpleSearchQuery() {
		super();
	}
	
	public SimpleSearchQuery(Map searchParameters) {
		this();
		setSearchParameters(searchParameters);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchQuery#isSimpleQuery()
	 */
	public boolean isSimpleQuery() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchQuery#isAdvancedQuery()
	 */
	public boolean isAdvancedQuery() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchQuery#getSearchParameters()
	 */
	public Map getSearchParameters() {
		return searchParameters;
	}
	
	/**
	 * @param searchParameters The searchParameters to set.
	 */
	public void setSearchParameters(Map searchParameters) {
		this.searchParameters = searchParameters;
	}
	
	/**
	 * @return the first value in the search parameter map
	 */
	public String getSimpleSearchQuery(){
		if(searchParameters!=null && !searchParameters.isEmpty()){
			return (String)searchParameters.values().iterator().next();
		}
		return null;
	}
}
