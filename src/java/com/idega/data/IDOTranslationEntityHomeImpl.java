/*
 * Created on 19.2.2004
 */
package com.idega.data;

import com.idega.core.localisation.data.ICLocale;

/**
 * Title:		IDOTranslationEntityHomeImpl
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class IDOTranslationEntityHomeImpl extends IDOFactory {

	protected Class getEntityInterfaceClass(){
		return IDOTranslationEntity.class;
	}


	public IDOTranslationEntity create() throws javax.ejb.CreateException{
		return (IDOTranslationEntity) super.createIDO();
	}


	public IDOTranslationEntity findTranslation(IDOEntity translatedEntity, ICLocale locale)throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((IDOTranslationEntityBMPBean)entity).ejbFindTranslation(translatedEntity, locale);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public IDOTranslationEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
		return (IDOTranslationEntity) super.findByPrimaryKeyIDO(pk);
	}
	
	
}
