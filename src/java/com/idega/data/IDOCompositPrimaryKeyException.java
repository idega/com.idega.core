/*
 * Created on 27.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

/**
 * Title:		IDOCompositPrimaryKeyException
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class IDOCompositPrimaryKeyException extends IllegalAccessException {

	/**
	 * 
	 */
	public IDOCompositPrimaryKeyException() {
		super("");
	}

	/**
	 * @param s
	 */
	public IDOCompositPrimaryKeyException(String s) {
		super("IDOCompositPrimaryKeyException: PrimaryKey is composit\n"+s);
	}

}
