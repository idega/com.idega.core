//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.core;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @version 0.5 UNFINISHED -  UNDER DEVELOPMENT

*/



public interface ICUser{


    public String getName();
    public int getID();

    //public boolean verifyUser(ICUser user);
    public boolean hasPermissionFor(ICComponent component);

}

