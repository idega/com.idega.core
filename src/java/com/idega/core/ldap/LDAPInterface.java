/*
 * $Id: LDAPInterface.java,v 1.1 2006/03/21 12:31:45 tryggvil Exp $
 * Created on 21.3.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap;

import com.idega.user.data.Group;


/**
 * <p>
 * Class to interface with an embedded LDAP server if any.
 * </p>
 *  Last modified: $Date: 2006/03/21 12:31:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface LDAPInterface {
	/**
	 * Generated the RDN (directory string) from the groups name and its parents
	 * @param group
	 * @return
	 */
	public String getGeneratedRDNFromGroup(Group group);
	
}
