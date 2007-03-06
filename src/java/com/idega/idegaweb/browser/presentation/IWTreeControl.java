package com.idega.idegaweb.browser.presentation;

import com.idega.event.IWPresentationEvent;
import com.idega.presentation.ui.AbstractTreeViewer;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public abstract class IWTreeControl extends AbstractTreeViewer implements IWBrowserView {

  private String _controlTarget = null;
  private IWPresentationEvent _contolEvent = null;

  public IWTreeControl() {
    getStateHandler().setPresentationStateClass(IWTreeControlPS.class);
  }


  public void setControlTarget(String controlTarget){
    this.setOpenCloseLinkTarget(controlTarget);
    this._controlTarget = controlTarget;
  }

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



//  public PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode) {
//    /**@todo: implement this com.idega.presentation.ui.AbstractTreeViewer abstract method*/
//    return PresentationObject.NULL_CLONE_OBJECT;
//
//  }


}