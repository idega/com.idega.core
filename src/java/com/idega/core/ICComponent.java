//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.core;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @version 0.5 UNFINISHED -  UNDER DEVELOPMENT

*/



public interface ICComponent{

//    public int getID(ICSession session);
    public String getName();
    public String getBundleName();
    public ICBundle getBundle(ICApplication application);
    public ICComponent getInstance();
    public void setProperty(ICProperty property);
    public void setProperty(String propertyName,Object propertyValue);
    public Object getProperty(String propertyName);

}

