package com.idega.idegaweb.browser.presentation;

import com.idega.event.IWEventModel;
import com.idega.presentation.ui.AbstractTreeViewer;
import com.idega.presentation.ui.Parameter;
import com.idega.business.IWEventListener;
import com.idega.presentation.PresentationObject;
import com.idega.core.ICTreeNode;
import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWTreeControl extends AbstractTreeViewer implements IWBrowserView {

  private String _controlTarget = null;
  private IWEventModel _contolEvent = null;

  public IWTreeControl() {
  }


  public void setControlTarget(String controlTarget){
    _controlTarget = controlTarget;
  }

  public void setControlEventModel(IWEventModel model){
    _contolEvent = model;
  }



  public PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode) {
    /**@todo: implement this com.idega.presentation.ui.AbstractTreeViewer abstract method*/
    return PresentationObject.NULL_CLONE_OBJECT;

  }

  public IWEventListener getListener(){return null;}

}