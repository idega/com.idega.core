/*
 * $Id: GroupBusinessHomeImpl.java,v 1.7 2004/11/16 12:36:21 laddi Exp $
 * Created on 16.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;




import com.idega.business.IBOHomeImpl;


/**
 * Last modified: 16.11.2004 13:37:28 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public class GroupBusinessHomeImpl extends IBOHomeImpl implements GroupBusinessHome {

	protected Class getBeanInterfaceClass() {
		return GroupBusiness.class;
	}

	public GroupBusiness create() throws javax.ejb.CreateException {
		return (GroupBusiness) super.createIBO();
	}

}
