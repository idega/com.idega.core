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
public abstract class IWServiceImpl implements IWService {


  private IWMainApplication application = null;

  protected abstract void executeService();
  public abstract String getServiceName();

  public void startService(IWMainApplication superApplication){
    if(application == null){
      setApplication(superApplication);
    }
    executeService();
  }

  public void endService(){
    System.out.println("Ending service "+getServiceName());
  }

  public IWMainApplication getApplication() throws IWServiceNotStartedException{
    if(application == null){
      throw new IWServiceNotStartedException(this);
    }
    return application;
  }

  public void setApplication(IWMainApplication iwma){
    application=iwma;
  }


}
