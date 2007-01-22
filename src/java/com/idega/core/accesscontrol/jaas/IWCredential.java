/*
 * $Id: IWCredential.java,v 1.1.2.1 2007/01/22 08:10:29 tryggvil Exp $
 * Created on May 10, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.jaas;

import javax.security.auth.Destroyable;


/**
 * 
 * Not fully implemented. Add more method if needed.
 * 
 * 
 *  Last modified: $Date: 2007/01/22 08:10:29 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.2.1 $
 */
public interface IWCredential extends Destroyable {
	
	Object getKey();
	
	String getName();
	
}