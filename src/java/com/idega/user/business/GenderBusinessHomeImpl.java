/*
 * $Id: GenderBusinessHomeImpl.java,v 1.1 2005/05/19 11:15:54 laddi Exp $
 * Created on May 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHomeImpl;


/**
 * Last modified: $Date: 2005/05/19 11:15:54 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class GenderBusinessHomeImpl extends IBOHomeImpl implements GenderBusinessHome {

	protected Class getBeanInterfaceClass() {
		return GenderBusiness.class;
	}

	public GenderBusiness create() throws javax.ejb.CreateException {
		return (GenderBusiness) super.createIBO();
	}
}
