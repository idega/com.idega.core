/*
 * Created on 7.8.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import java.util.Locale;

/**
 * Title:		IDOReportableField
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public interface IDOReportableField{

	/**
	 *
	 */
	public String getName();
		
	/**
	 *
	 */
	public String getDescription();
		
	/**
	 *
	 */
	public void setDescription(String description);
		
	/**
	 *
	 */
	public Class getValueClass();
	
	public String getLocalizedName(Locale locale);
	public void setLocalizedName(String name, Locale locale);

}
