/*
 * Created on 23.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.version.data;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;

/**
 * Title:		ICVersionableHome
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public interface ICVersionableHome extends IDOHome {
	
	public IDOEntity findEntityOfSpecificVersion(ICVersion version) throws FinderException;
	
}
