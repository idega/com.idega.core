package com.idega.idegaweb.browser.presentation;

import com.idega.core.data.ICTreeNode;
import com.idega.event.IWPresentationEvent;
import com.idega.presentation.ui.AbstractTreeViewer;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class IWTreeControl<Node extends ICTreeNode<?>> extends AbstractTreeViewer<Node> implements IWBrowserView {

  private String _controlTarget = null;
  private IWPresentationEvent _contolEvent = null;

  public IWTreeControl() {
    getStateHandler().setPresentationStateClass(IWTreeControlPS.class);
  }


  @Override
public void setControlTarget(String controlTarget){
    this.setOpenCloseLinkTarget(controlTarget);
    this._controlTarget = controlTarget;
  }

  @Override
public void setControlEventModel(IWPresentationEvent model){
    this._contolEvent = model;
    this.addEventModel(model);
  }

  public IWPresentationEvent getControlEventModel(){
    return this._contolEvent;
  }

  public String getControlTarget(){
    return this._controlTarget;
  }

}