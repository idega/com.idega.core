//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 - Under development
*Class to serve as a service superclass for background services on an IdegaWeb Application
*/
public abstract class IWService{


  IWMainApplication application;



  public abstract void executeService();
  public abstract String getServiceName();


    public void startService(IWMainApplication superApplication){
        System.out.println("Starting service "+getServiceName());
        executeService();
    }

    public void endService(){
        System.out.println("Ending service "+getServiceName());
    }

    public IWMainApplication getApplication(){
      return application;
    }



}
