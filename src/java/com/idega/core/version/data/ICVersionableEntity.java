/*
 * Created on 23.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.version.data;

import com.idega.data.IDOEntity;

/**
 * Title:		ICVersionable
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */

public interface ICVersionableEntity extends IDOEntity {

	/**
	 * @return versionID, returns null if is not a version
	 * 
	 * @uml.property name="versionPrimaryKey"
	 */
	public Object getVersionPrimaryKey();

	/**
	 * @return itemID, returns null if is not an item
	 * 
	 * @uml.property name="itemPrimaryKey"
	 */
	public Object getItemPrimaryKey();

}
