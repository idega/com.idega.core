/*
 * $Id: SearchResult.java,v 1.2 2005/01/18 12:27:22 tryggvil Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;


/**
 * <p>
 * An instance of this class corresponds to a "search result" i.e. each row of a search result.
 * </p>
 * 
 *  Last modified: $Date: 2005/01/18 12:27:22 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public interface SearchResult {
	
	public String getSearchResultType();
	public String getSearchResultHeadline();
	public String getSearchResultDescription();
	public String getSearchResultAbstract();
	public String getSearchResultURL();
	
}
