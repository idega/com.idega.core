/*
 * $Id: CommuneBusinessHomeImpl.java,v 1.2 2004/09/14 15:04:06 joakim Exp $
 * Created on 14.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/09/14 15:04:06 $ by $Author: joakim $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim</a>
 * @version $Revision: 1.2 $
 */
public class CommuneBusinessHomeImpl extends IBOHomeImpl implements CommuneBusinessHome {

	protected Class getBeanInterfaceClass() {
		return CommuneBusiness.class;
	}

	public CommuneBusiness create() throws javax.ejb.CreateException {
		return (CommuneBusiness) super.createIBO();
	}
}
