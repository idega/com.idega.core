package com.idega.idegaweb.browser.event;

import com.idega.business.IWFrameBusiness;
import com.idega.event.IWPresentationEvent;
import com.idega.event.NoSuchEventException;
import com.idega.presentation.Frame;
import com.idega.presentation.FrameTable;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import java.rmi.RemoteException;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWBrowseEvent extends IWPresentationEvent {

  private final static String IW_FRAMESET_PAGE_PARAMETER = Page.IW_FRAMESET_PAGE_PARAMETER;
  private final static String IW_FRAME_NAME_PARAMETER = Page.IW_FRAME_NAME_PARAMETER;
  private final static String PRM_IW_BROWSE_EVENT_SOURCE = Page.PRM_IW_BROWSE_EVENT_SOURCE;

  private String _appId;
  private String _src;
  private String _ctrlTarget;

  public IWBrowseEvent() {
    super();
  }

  public IWBrowseEvent(IWContext iwc) throws NoSuchEventException{
    super(iwc);
  }

  public void setApplicationIdentifier(String value){
    this.addParameter(IW_FRAMESET_PAGE_PARAMETER,value);
  }

  public void setApplicationIdentifier(FrameTable table, IWFrameBusiness fb) throws RemoteException{
    this.addParameter(IW_FRAMESET_PAGE_PARAMETER,fb.getFrameSetIdentifier(table));
  }

  public void setSourceTarget(String value){
    this.addParameter(PRM_IW_BROWSE_EVENT_SOURCE,value);
  }

  public void setSourceTarget(Frame frame){
    this.addParameter(PRM_IW_BROWSE_EVENT_SOURCE,frame.getName());
  }

  public void setControlFrameTarget(String value){
    this.addParameter(IW_FRAME_NAME_PARAMETER,value);
  }



  public String getApplicationIdentifier(){
    return _appId;
  }
  public String getSourceTarget(){
    return _src;
  }
  public String getControlFrameTarget(){
    return _ctrlTarget;
  }



  public boolean initializeEvent(IWContext iwc){
    _appId = iwc.getParameter(IW_FRAMESET_PAGE_PARAMETER);
    _src = iwc.getParameter(PRM_IW_BROWSE_EVENT_SOURCE);
    _ctrlTarget = iwc.getParameter(IW_FRAME_NAME_PARAMETER);

    return (_appId != null && _src != null && _ctrlTarget != null);
  }


}