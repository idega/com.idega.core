/*
 * Created on 13.7.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JRField;

/**
 * Title:		IDOReportableEntity
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public interface IDOReportableEntity {

	public Object getFieldValue(JRField arg0) throws JRException;

}
