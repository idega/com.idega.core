/*
 * Created on 19.2.2004
 */
package com.idega.data;

import com.idega.core.localisation.data.ICLocale;

/**
 * Title:		IDOTranslationEntityHome
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public interface IDOTranslationEntityHome extends IDOEntity {
	public IDOTranslationEntity create() throws javax.ejb.CreateException;
	public IDOTranslationEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
	public IDOTranslationEntity findTranslation(IDOEntity translatedEntity, ICLocale locale)throws javax.ejb.FinderException;
	
}
