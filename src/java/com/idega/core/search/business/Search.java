/*
 * $Id: Search.java,v 1.1 2005/01/18 11:53:25 tryggvil Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;

import java.util.Collection;
import java.util.Map;


/**
 * <p>
 * An instance of this class corresponds to an individual search performed by a user.
 * </p>
 *  Last modified: $Date: 2005/01/18 11:53:25 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface Search {
	
	public String getSearchType();
	public String getSearchName();
	/**
	 * The parameter Map used to create the search.
	 * @return
	 */
	public Map getSearchParameters();
	/**
	 * Returns a collection of SearchResult objects.
	 * @return
	 */
	public Collection getSearchResults();
	
	public long getNumberOfResults();
}
