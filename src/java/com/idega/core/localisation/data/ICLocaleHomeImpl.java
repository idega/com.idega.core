/*
 * $Id: ICLocaleHomeImpl.java,v 1.2 2004/09/27 13:35:33 aron Exp $
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

import com.idega.data.IDOFactory;

/**
 * 
 *  Last modified: $Date: 2004/09/27 13:35:33 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class ICLocaleHomeImpl extends IDOFactory implements ICLocaleHome {
    protected Class getEntityInterfaceClass() {
        return ICLocale.class;
    }

    public ICLocale create() throws javax.ejb.CreateException {
        return (ICLocale) super.createIDO();
    }

    public ICLocale findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException {
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
        java.util.Collection ids = ((ICLocaleBMPBean) entity)
                .ejbFindByUsage(usage);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

}
