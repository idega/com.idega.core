package com.idega.idegaweb.browser.app;

import com.idega.event.*;
import com.idega.idegaweb.IWException;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.browser.event.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWBrowserPresentationState implements IWPresentationState, IWEventListener {

  private boolean _showTopFrame;
  private boolean _showMenuFrame;
  private boolean _showBottomFrame;
  private boolean _showLeftMainFrame;
  private boolean _showRightMainFrame;

  public IWBrowserPresentationState() {
    reset();
  }


  public void reset() {
    _showTopFrame = true;
    _showMenuFrame = true;
    _showBottomFrame = false;
    _showLeftMainFrame = true;
    _showRightMainFrame = false;
  }


  public void setStateValue(String stateName, Object value) {
    /**@todo: Implement this com.idega.event.IWPresentationState method*/
    throw new java.lang.UnsupportedOperationException("Method setStateValue() not yet implemented.");
  }
  public Object getStateValue(String stateName) {
    /**@todo: Implement this com.idega.event.IWPresentationState method*/
    throw new java.lang.UnsupportedOperationException("Method getStateValue() not yet implemented.");
  }



  public void showTopFrame(boolean value){
    _showTopFrame = value;
  }

  public void showMenuFrame(boolean value){
    _showMenuFrame = value;
  }

  public void showRightMainFrame(boolean value){
    _showRightMainFrame = value;
  }

  public void showLeftMainFrame(boolean value){
    _showLeftMainFrame = value;
  }

  public void showBottomFrame(boolean value){
    _showBottomFrame = value;
  }

  public boolean showBottomFrame(){
    return _showBottomFrame;
  }

  public boolean showRightMainFrame(){
    return _showRightMainFrame;
  }

  public boolean showLeftMainFrame(){
    return _showLeftMainFrame;
  }

  public boolean showTopFrame(){
    return _showTopFrame;
  }

  public boolean showMenuFrame(){
    return _showMenuFrame;
  }

  public boolean actionPerformed(IWPresentationEvent e)throws IWException{
    boolean refresh = false;
    if(e instanceof IWBrowseEvent){


      refresh = true;
    }


    return refresh;
  }

}