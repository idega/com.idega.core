package com.idega.idegaweb.browser.presentation;
import com.idega.presentation.ui.Parameter;
import com.idega.business.IWEventListener;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface IWBrowserView extends IWBrowserCompliant{

  public IWEventListener getListener();
  public void setControlTarget(String target);
  public void setApplicationParameter(Parameter prm);
  public void setSourceParamenter(Parameter prm);
  public void setControlFrameParameter(Parameter prm);

}