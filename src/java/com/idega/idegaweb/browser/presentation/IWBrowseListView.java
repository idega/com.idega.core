package com.idega.idegaweb.browser.presentation;

import com.idega.event.IWEventModel;
import com.idega.presentation.ui.Parameter;
import com.idega.business.IWEventListener;
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
  private IWEventModel _contolEvent = null;

  public IWBrowseListView() {
  }

  public IWEventListener getListener(){return null;}

  public void setControlTarget(String controlTarget){
    _controlTarget = controlTarget;
  }

  public void setControlEventModel(IWEventModel model){
    _contolEvent = model;
  }



}