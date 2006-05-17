/*
 * $Id: EmailType.java,v 1.3 2006/05/17 16:40:00 thomas Exp $
 * Created on May 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.data;

import javax.ejb.CreateException;
import com.idega.core.data.GenericType;
import com.idega.data.IDOException;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2006/05/17 16:40:00 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public interface EmailType extends GenericType {
	
	public boolean ejbHomeUpdateStartData() throws IDOException, IDOLookupException, CreateException;
}
