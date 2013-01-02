//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.util.logging.Logger;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 - Under development
*Class to serve as a service superclass for background services on an IdegaWeb Application
*/
public abstract class IWServiceImpl implements IWService {

  private IWMainApplication application = null;

  protected abstract void executeService();

  @Override
  public abstract String getServiceName();

  @Override
  public void startService(IWMainApplication superApplication){
    if (this.application == null)
      setApplication(superApplication);

    executeService();
  }

  @Override
  public void endService(){
    Logger.getLogger(getClass().getName()).info("Ending service " + getServiceName());
  }

  public IWMainApplication getApplication() throws IWServiceNotStartedException{
    if (this.application == null)
      throw new IWServiceNotStartedException(this);

    return this.application;
  }

  public void setApplication(IWMainApplication iwma){
    this.application=iwma;
  }

}
