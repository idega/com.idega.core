/*
 * $Id: UUIDBusinessHomeImpl.java,v 1.2 2004/09/09 16:24:14 eiki Exp $
 * Created on Sep 9, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.idgenerator.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/09/09 16:24:14 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class UUIDBusinessHomeImpl extends IBOHomeImpl implements UUIDBusinessHome {

	protected Class getBeanInterfaceClass() {
		return UUIDBusiness.class;
	}

	public UUIDBusiness create() throws javax.ejb.CreateException {
		return (UUIDBusiness) super.createIBO();
	}
}
