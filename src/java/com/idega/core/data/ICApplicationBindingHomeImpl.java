/*
 * $Id: ICApplicationBindingHomeImpl.java,v 1.2 2005/12/15 16:57:12 thomas Exp $
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
import com.idega.data.IDOFactory;


/**
 * 
 *  Last modified: $Date: 2005/12/15 16:57:12 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class ICApplicationBindingHomeImpl extends IDOFactory implements ICApplicationBindingHome {

	protected Class getEntityInterfaceClass() {
		return ICApplicationBinding.class;
	}

	public ICApplicationBinding create() throws javax.ejb.CreateException {
		return (ICApplicationBinding) super.createIDO();
	}

	public ICApplicationBinding findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (ICApplicationBinding) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findByBindingType(String type) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICApplicationBindingBMPBean) entity).ejbFindByBindingType(type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICApplicationBindingBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
