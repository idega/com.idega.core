package com.idega.idegaweb.browser.presentation;
import javax.swing.event.ChangeListener;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface IWBrowseControl extends IWBrowserCompliant {

  //public void dispatchEvent(IWContext iwc);
//  public void setOnLoad(String action);
//  public void setOnUnLoad(String action);

  public ChangeListener getChangeControler();

}