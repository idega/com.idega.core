/*
 * Created on 23.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.version.util;

import javax.ejb.EJBException;

import com.idega.core.version.data.ICVersion;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOQuery;

/**
 * Title:		ICVersionQuery
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICVersionQuery extends IDOQuery {

	/**
	 * 
	 */
	public ICVersionQuery() {
		super();
	}

	/**
	 * @param length
	 */
	public ICVersionQuery(int length) {
		super(length);
	}

	/**
	 * @param str
	 */
	public ICVersionQuery(String str) {
		super(str);
	}
	
	public ICVersionQuery appendFindEntityOfSpecificVersionQuery(String middleTableName,ICVersion version) throws EJBException{
		this.appendSelectAllFrom(middleTableName);
		try {
			this.appendWhereEquals((version).getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName(),version.getPrimaryKey());
		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			throw new EJBException(e);
		}
		return this;
	}

}
