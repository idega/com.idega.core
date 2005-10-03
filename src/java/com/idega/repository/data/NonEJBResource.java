/*
 * $Id: NonEJBResource.java,v 1.1 2005/10/03 18:24:19 thomas Exp $
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
 *  Last modified: $Date: 2005/10/03 18:24:19 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 * 
 * @see com.idega.repository.Resource
 */

public interface NonEJBResource extends Resource {
	
	ResourceDescription getResourceDescription();
}
