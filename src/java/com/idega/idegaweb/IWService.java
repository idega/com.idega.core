package com.idega.idegaweb;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IWService{
  public String getServiceName();
  public void startService(IWMainApplication superApplication);
  public void endService();

}
