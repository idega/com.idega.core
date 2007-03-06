package com.idega.idegaweb.browser.presentation;

import com.idega.presentation.ui.TreeViewerPS;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class IWTreeControlPS extends TreeViewerPS {



  public IWTreeControlPS() {
  }


//
//
//  public void reset() {
//    /**@todo: Implement this com.idega.event.IWPresentationState method*/
//    throw new java.lang.UnsupportedOperationException("Method reset() not yet implemented.");
//  }
//
//  public List getOpenNodeList(){
//    /**@todo: Implement this com.idega.event.IWPresentationState method*/
//    throw new java.lang.UnsupportedOperationException("Method getOpenNodeList() not yet implemented.");
//
//  }
//
//  public Iterator getFirstlevelNodes(){
//    /**@todo: Implement this com.idega.event.IWPresentationState method*/
//    throw new java.lang.UnsupportedOperationException("Method getFirstlevelNodes() not yet implemented.");
//
//  }

  public Object clone() {
    IWTreeControlPS obj = null;
    try {
      obj = (IWTreeControlPS)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}