/*
 * $Id: ICLocaleHome.java,v 1.2 2004/09/27 13:35:33 aron Exp $
 * Created on 25.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
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
 *  Last modified: $Date: 2004/09/27 13:35:33 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public interface ICLocaleHome extends IDOHome {
    public ICLocale create() throws javax.ejb.CreateException;

    public ICLocale findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException;

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

}
