package com.idega.idegaweb.browser.app;

import com.idega.event.IWActionListener;
import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationStateImpl;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.browser.event.IWBrowseEvent;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
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
    this._showTopFrame = true;
    this._showMenuFrame = true;
    this._showBottomFrame = false;
    this._showLeftMainFrame = true;
    this._showRightMainFrame = false;
  }



  public void showTopFrame(boolean value){
    this._showTopFrame = value;
  }

  public void showMenuFrame(boolean value){
    this._showMenuFrame = value;
  }

  public void showRightMainFrame(boolean value){
    this._showRightMainFrame = value;
  }

  public void showLeftMainFrame(boolean value){
    this._showLeftMainFrame = value;
  }

  public void showBottomFrame(boolean value){
    this._showBottomFrame = value;
  }

  public boolean showBottomFrame(){
    return this._showBottomFrame;
  }

  public boolean showRightMainFrame(){
    return this._showRightMainFrame;
  }

  public boolean showLeftMainFrame(){
    return this._showLeftMainFrame;
  }

  public boolean showTopFrame(){
    return this._showTopFrame;
  }

  public boolean showMenuFrame(){
    return this._showMenuFrame;
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