//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.core;



import java.util.List;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @version 0.5 UNFINISHED -  UNDER DEVELOPMENT

*/



public interface ICApplication extends ICComponent{



      public List getBundlesRegistered();



      public ICBundle getBundle(String bundleName);



}

