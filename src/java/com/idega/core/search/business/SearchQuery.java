/*
 * $Id: SearchQuery.java,v 1.2 2005/03/20 11:22:37 eiki Exp $
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
 *  Last modified: $Date: 2005/03/20 11:22:37 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public interface SearchQuery {

	public boolean isSimpleQuery();
	public boolean isAdvancedQuery();
	public Map getSearchParameters();
	public void setSearchParameters(Map searchParameters);
}
