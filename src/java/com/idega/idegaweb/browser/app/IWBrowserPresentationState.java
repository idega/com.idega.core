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

public class IWBrowserPresentationState extends IWPresentationStateImpl implements IWActionListener {

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

  public void actionPerformed(IWPresentationEvent e)throws IWException{
    if(e instanceof IWBrowseEvent){

      fireStateChanged();
    }


  }

  public Object clone() {
    IWBrowserPresentationState obj = null;
    try {
      obj = (IWBrowserPresentationState)super.clone();
      obj._showTopFrame = this._showTopFrame;
      obj._showMenuFrame = this._showMenuFrame;
      obj._showBottomFrame = this._showBottomFrame;
      obj._showLeftMainFrame = this._showLeftMainFrame;
      obj._showRightMainFrame = this._showRightMainFrame;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}