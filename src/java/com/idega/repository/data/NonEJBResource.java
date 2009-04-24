/*
 * $Id: NonEJBResource.java,v 1.2 2009/04/24 08:39:08 valdas Exp $
 * Created on Sep 30, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 * 
 
 */
package com.idega.repository.data;

/**
 * A flag for a class that is a resource but not an EJB. 
 * <p/>
 * Non EJB resources should provide metadata (ResourceDescription) 
 * for creating and finding such a resource. 
 * 
 * <p/>
 *  Last modified: $Date: 2009/04/24 08:39:08 $ by $Author: valdas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 * 
 * @see com.idega.presentation.bean.Resource
 */

public interface NonEJBResource extends Resource {
	
	ResourceDescription getResourceDescription();
}
