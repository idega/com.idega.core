//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;

import java.io.Serializable;

/**
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.9
* Unimplemented Class
*/
public interface IDOPrimaryKey extends Serializable {

	public IDOPrimaryKey getInstance();
	public Object getPrimaryKeyValue(String columnName);
	public void setPrimaryKeyValue(String columnName, Object PKValue);
	public boolean equals(Object object);
	public int hashCode();

}