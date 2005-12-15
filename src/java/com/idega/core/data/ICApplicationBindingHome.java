/*
 * $Id: ICApplicationBindingHome.java,v 1.2 2005/12/15 16:57:12 thomas Exp $
 * Created on Dec 14, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2005/12/15 16:57:12 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public interface ICApplicationBindingHome extends IDOHome {

	public ICApplicationBinding create() throws javax.ejb.CreateException;

	public ICApplicationBinding findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#ejbFindByBindingType
	 */
	public Collection findByBindingType(String type) throws FinderException;

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;
}
