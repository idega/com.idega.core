package com.idega.idegaweb.browser.presentation;

import com.idega.event.IWPresentationEvent;
import com.idega.presentation.Block;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWBrowseListView extends Block implements IWBrowserView {

  private String _controlTarget = null;
  private IWPresentationEvent _contolEvent = null;

  public IWBrowseListView() {
  }

  public void setControlTarget(String controlTarget){
    _controlTarget = controlTarget;
  }

  public void setControlEventModel(IWPresentationEvent model){
    _contolEvent = model;
  }



}