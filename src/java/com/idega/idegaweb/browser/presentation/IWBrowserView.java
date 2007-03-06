package com.idega.idegaweb.browser.presentation;
import com.idega.event.IWPresentationEvent;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public interface IWBrowserView extends IWBrowserCompliant{

  public void setControlTarget(String target);

  public void setControlEventModel(IWPresentationEvent model);



}