/*
 * $Id: GroupBusinessHomeImpl.java,v 1.5 2004/10/11 17:23:43 eiki Exp $
 * Created on Oct 11, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/10/11 17:23:43 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public class GroupBusinessHomeImpl extends IBOHomeImpl implements GroupBusinessHome {

	protected Class getBeanInterfaceClass() {
		return GroupBusiness.class;
	}

	public GroupBusiness create() throws javax.ejb.CreateException {
		return (GroupBusiness) super.createIBO();
	}
}
