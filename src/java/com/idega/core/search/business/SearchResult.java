/*
 * $Id: SearchResult.java,v 1.3 2005/01/18 12:33:54 tryggvil Exp $
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
 *  Last modified: $Date: 2005/01/18 12:33:54 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public interface SearchResult {
	
	public String getSearchResultType();
	public String getSearchResultHeadline();
	public String getSearchResultDescription();
	public String getSearchResultAbstract();
	public String getSearchResultURL();
	/**
	 * Can return any arbitrary non-standard attributes with a Collection of attributeKey=attributeValue.
	 * @return
	 */
	public Map getSearchResultAttributes();
}
