/*
 * $Id: ICLocale.java,v 1.2 2004/09/27 13:35:33 aron Exp $
 * Created on 25.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.localisation.data;

import java.util.Locale;


import com.idega.data.IDOEntity;

/**
 * 
 *  Last modified: $Date: 2004/09/27 13:35:33 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public interface ICLocale extends IDOEntity {
    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getName
     */
    public String getName();

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#setLocale
     */
    public void setLocale(String sLocaleName);

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getLocale
     */
    public String getLocale();

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#setLanguageId
     */
    public void setLanguageId(int iLanguageId);

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#setLanguageId
     */
    public void setLanguageId(Integer iLanguageId);

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getLanguageId
     */
    public int getLanguageId();

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#setCountryId
     */
    public void setCountryId(int iCountryId);

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#setCountryId
     */
    public void setCountryId(Integer iCountryId);

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getCountryId
     */
    public int getCountryId();

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#setInUse
     */
    public void setInUse(boolean inUse);

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getInUse
     */
    public boolean getInUse();

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getLocaleID
     */
    public int getLocaleID();

    /**
     * @see com.idega.core.localisation.data.ICLocaleBMPBean#getLocaleObject
     */
    public Locale getLocaleObject();

}
