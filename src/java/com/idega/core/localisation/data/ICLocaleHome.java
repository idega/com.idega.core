/*
 * $Id: ICLocaleHome.java,v 1.3 2005/05/13 04:21:49 gimmi Exp $
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
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2005/05/13 04:21:49 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public interface ICLocaleHome extends IDOHome {

	public ICLocale create() throws javax.ejb.CreateException;

	public ICLocale findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.localisation.data.ICLocaleBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.core.localisation.data.ICLocaleBMPBean#ejbFindAllInUse
	 */
	public Collection findAllInUse() throws FinderException;

	/**
	 * @see com.idega.core.localisation.data.ICLocaleBMPBean#ejbFindByUsage
	 */
	public Collection findByUsage(boolean usage) throws FinderException;

	/**
	 * @see com.idega.core.localisation.data.ICLocaleBMPBean#ejbFindByLocaleName
	 */
	public ICLocale findByLocaleName(String locale) throws FinderException;
}
