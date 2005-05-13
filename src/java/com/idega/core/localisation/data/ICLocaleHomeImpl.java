/*
 * $Id: ICLocaleHomeImpl.java,v 1.3 2005/05/13 04:21:49 gimmi Exp $
 * Created on 13.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.localisation.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * 
 *  Last modified: $Date: 2005/05/13 04:21:49 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public class ICLocaleHomeImpl extends IDOFactory implements ICLocaleHome {

	protected Class getEntityInterfaceClass() {
		return ICLocale.class;
	}

	public ICLocale create() throws javax.ejb.CreateException {
		return (ICLocale) super.createIDO();
	}

	public ICLocale findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (ICLocale) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICLocaleBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllInUse() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICLocaleBMPBean) entity).ejbFindAllInUse();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByUsage(boolean usage) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ICLocaleBMPBean) entity).ejbFindByUsage(usage);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public ICLocale findByLocaleName(String locale) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICLocaleBMPBean) entity).ejbFindByLocaleName(locale);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}
