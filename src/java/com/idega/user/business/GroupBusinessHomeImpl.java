/*
 * $Id: GroupBusinessHomeImpl.java,v 1.8 2004/11/16 14:53:32 eiki Exp $
 * Created on Nov 16, 2004
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
 *  Last modified: $Date: 2004/11/16 14:53:32 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.8 $
 */
public class GroupBusinessHomeImpl extends IBOHomeImpl implements GroupBusinessHome {

	protected Class getBeanInterfaceClass() {
		return GroupBusiness.class;
	}

	public GroupBusiness create() throws javax.ejb.CreateException {
		return (GroupBusiness) super.createIBO();
	}
}
