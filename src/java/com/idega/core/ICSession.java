//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.core;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED -  UNDER DEVELOPMENT
*/

public interface ICSession{

    public ICApplication getICApplication();

    //public ICUser getCurrentUser();

    public void setSessionAttribute(String attribute,Object value);

    public Object getSessionAttribute(String attributeName);

    public void removeSessionAttribute(String attributeName);

}
