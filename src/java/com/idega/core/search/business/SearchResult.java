/*
 * $Id: SearchResult.java,v 1.6 2005/01/20 14:16:08 laddi Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;

import java.util.Map;


/**
 * <p>
 * An instance of this class corresponds to a "search result" i.e. each row of a search result.
 * </p>
 * 
 *  Last modified: $Date: 2005/01/20 14:16:08 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public interface SearchResult {
	
	public String getSearchResultType();
	/**
	 * Used as the name of the link in the default impl.
	 * @return
	 */
	public String getSearchResultName();
	
	/**
	 * Used as extra info for the search result item e.g. the modification date of the resource, size etc.
	 * @return
	 */
	public String getSearchResultExtraInformation();
	
	/**
	 * Used as a "teaser" or abstract for the search result item
	 * @return
	 */
	public String getSearchResultAbstract();
	/**
	 * The URI to the resource for the search result.
	 * @return
	 */
	public String getSearchResultURI();
	/**
	 * Can return any arbitrary non-standard attributes with a Collection of attributeKey=attributeValue.
	 * @return
	 */
	public Map<String, Object> getSearchResultAttributes();
}