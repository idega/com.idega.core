/*
 * $Id: SearchQuery.java,v 1.1 2005/01/18 12:43:12 tryggvil Exp $
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
 * 
 *  Last modified: $Date: 2005/01/18 12:43:12 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface SearchQuery {

	public boolean isSimpleQuery();
	public boolean isAdvancedQuery();
	public Map getSearchParameters();
}
