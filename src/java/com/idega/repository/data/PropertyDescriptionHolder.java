/*
 * $Id: PropertyDescriptionHolder.java,v 1.1 2005/10/05 17:45:13 thomas Exp $
 * Created on Oct 5, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.repository.data;

import java.util.List;

/**
 * A flag for a class that has some PropertyDescriptions.
 * 
 *  Last modified: $Date: 2005/10/05 17:45:13 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */

public interface PropertyDescriptionHolder {
	
	List getPropertyDescriptions();
	
	
}

