package com.idega.idegaweb.browser.presentation;

import com.idega.event.IWPresentationStateImpl;
import java.util.Iterator;
import java.util.List;
import com.idega.event.IWPresentationState;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWTreeControlPresentationState extends IWPresentationStateImpl{



  public IWTreeControlPresentationState() {
  }




  public void reset() {
    /**@todo: Implement this com.idega.event.IWPresentationState method*/
    throw new java.lang.UnsupportedOperationException("Method reset() not yet implemented.");
  }

  public List getOpenNodeList(){
    /**@todo: Implement this com.idega.event.IWPresentationState method*/
    throw new java.lang.UnsupportedOperationException("Method getOpenNodeList() not yet implemented.");

  }

  public Iterator getFirstlevelNodes(){
    /**@todo: Implement this com.idega.event.IWPresentationState method*/
    throw new java.lang.UnsupportedOperationException("Method getFirstlevelNodes() not yet implemented.");

  }

  public Object clone() {
    IWTreeControlPresentationState obj = null;
    try {
      obj = (IWTreeControlPresentationState)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}