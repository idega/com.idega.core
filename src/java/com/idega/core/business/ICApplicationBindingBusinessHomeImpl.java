/*
 * $Id: ICApplicationBindingBusinessHomeImpl.java,v 1.4 2005/12/15 17:07:03 thomas Exp $
 * Created on Dec 15, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2005/12/15 17:07:03 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
 */
public class ICApplicationBindingBusinessHomeImpl extends IBOHomeImpl implements ICApplicationBindingBusinessHome {

	protected Class getBeanInterfaceClass() {
		return ICApplicationBindingBusiness.class;
	}

	public ICApplicationBindingBusiness create() throws javax.ejb.CreateException {
		return (ICApplicationBindingBusiness) super.createIBO();
	}
}
