package com.idega.idegaweb.browser.app;

import com.idega.event.*;
import com.idega.idegaweb.browser.presentation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeListener;
import com.idega.business.IBOLookup;
import com.idega.presentation.*;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.browser.event.IWBrowseEvent;
import java.rmi.RemoteException;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWBrowser extends FrameTable implements StatefullPresentation {


  private String _frameName[] = {"iwb_top","iwb_menu","iwb_middle","iwb_bottom",IWPresentationEvent.DEFAULT_IW_EVENT_TARGET,"iwb_main_left","iwb_main","iwb_main_right"};

  public static final int POS_TOP = 0;
  public static final int POS_MENU = 1;
  public static final int POS_MIDDLE = 2;
  public static final int POS_BOTTOM = 3;
  public static final int POS_EVENT = 4;

  public static final int POS_LEFTMAIN = 5;
  public static final int POS_MAIN = 6;
  public static final int POS_RIGHTMAIN = 7;

  private int _controlPosition = POS_TOP;

//  private IWBrowserFrame _topFrame = null;
//  private IWBrowserFrame _menuFrame = null;
//  private IWBrowserFrame _mainFrame = null;
//  private IWBrowserFrame _leftFrame = null;
//  private IWBrowserFrame _rightFrame = null;
  private IWBrowserFrame[] _browserFrames = new IWBrowserFrame[7];

  private FrameTable _middleFrameset;

  private boolean _showTopFrame = true;
  private boolean _showMenuFrame = true;
  private boolean _showBottomFrame = true;
  private boolean _showEventFrame = false;
  private boolean _showLeftMainFrame = true;
  private boolean _showRightMainFrame = false;

  private IWBrowserPresentationState _presentationState = null;

  private final static String IW_BUNDLE_IDENTIFIER = "com.idega.idegaweb.browser";

  public IWBrowser() {
    this.setResizable(true);
    this.setHeight(400);
    this.setWidth(600);
    initializeFrames();

  }

  protected void initializeFrames(){
    for (int i = 0; i < this._browserFrames.length; i++) {
//      System.out.println("IWBrowser.initializeFrames(): "+i);
      this._browserFrames[i] = new IWBrowserFrame();
      this._browserFrames[i].setNameProperty(this.getFrameName(i));
      this._browserFrames[i].getLocation().setApplicationClass(this.getClass());
      //_browserFrames[i].getLocation().isInFrameSet(true);
    }
    this._middleFrameset = new FrameTable();
    this._middleFrameset.setHorizontal();
  }

  public String getControlframeTarget(){
    return this.getFrameName(this._controlPosition);
  }

  public Frame getControlframe(){
    return this.getFrame(this.getFrameName(this._controlPosition));
  }

  public String getFrameName(int pos){
    return this._frameName[pos];
  }

  protected FrameTable getMiddleFrameset(){
    return this._middleFrameset;
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

  public void showEventFrame(boolean value){
    this._showEventFrame = value;
  }

  public boolean showEventFrame(){
    return this._showEventFrame;
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


  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void addToTop(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_TOP);
  }

  public void addToMenu(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_MENU);
  }

  public void addToMain(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_MAIN);
  }

  public void addToLeftMain(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_LEFTMAIN);
  }

  public void addToRightMain(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_RIGHTMAIN);
  }

  public void addToBottom(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_BOTTOM);
  }

  public void addToEvent(IWBrowserCompliant obj){
    this.addToFrame(obj,POS_EVENT);
  }

  public void setEventURL(String url){
    this.getEventFrame().setUrlProperty(url);
  }

  public void setSpanPixels(int pos, int pixels){
    this._browserFrames[pos].setSpanPixels(pixels);
  }

  protected IWBrowserFrame getTopFrame(){
    return this._browserFrames[POS_TOP];
  }

  protected IWBrowserFrame getMenuFrame(){
    return this._browserFrames[POS_MENU];
  }

  protected IWBrowserFrame getMiddleFrame(){
    return this._browserFrames[POS_MIDDLE];
  }

  protected IWBrowserFrame getMainFrame(){
    return this._browserFrames[POS_MAIN];
  }

  protected IWBrowserFrame getLeftMainFrame(){
    return this._browserFrames[POS_LEFTMAIN];
  }

  protected IWBrowserFrame getRightMainFrame(){
    return this._browserFrames[POS_RIGHTMAIN];
  }

  protected IWBrowserFrame getBottomFrame(){
    return this._browserFrames[POS_BOTTOM];
  }

  protected IWBrowserFrame getEventFrame(){
    return this._browserFrames[POS_EVENT];
  }


  public void addIWActionListener(int pos, IWActionListener l){
    this.getFrame(this.getFrameName(pos)).addIWActionListener(l);
  }

  /** this method is similar to the method addIWActionListener but uses
   * the method addActionListener
   * @param pos
   * @param l
   */
  public void addActionListener(int pos, IWActionListener l){
    this.getFrame(this.getFrameName(pos)).addActionListener(l);
  }  


  public void modifyFrameObject(IWContext iwc, IWFrameBusiness fb, Frame frame) throws RemoteException {
//      System.out.println("IWBrowser.modifyFrameObject");

    //if(frame.getFrameType() == Frame.OBJ || frame.getFrameType() == Frame.FRAMESET ){
      PresentationObject obj = frame.getPresentationObject();
//      System.out.println("frame.getPresentationObject() = "+obj);
      if(obj instanceof IWBrowserView){
        ((IWBrowserView)obj).setControlTarget(this.getControlframeTarget());

        IWBrowseEvent model = new IWBrowseEvent();
        model.setApplicationIdentifier(this,fb);
        model.setControlFrameTarget(getControlframeTarget());
        model.setSourceTarget(frame);
//        System.out.println("-----------------------------");
//        System.out.println("IWBrowser.frame.location: " +frame.getLocation().getLocationString());
        //model.setSource(frame.getLocation());
        String id = frame.getCompoundId();
        obj.setArtificialCompoundId(id,iwc);
        model.setSource(frame);

        ((IWBrowserView)obj).setControlEventModel(model);


//        Parameter appPrm = new Parameter(IW_FRAMESET_PAGE_PARAMETER,fb.getFrameSetIdentifier(this));
//        ((IWBrowserView)obj).setApplicationParameter(appPrm);
//        Parameter ctrlFP = new Parameter(IW_FRAME_NAME_PARAMETER,getControlframeTarget());
//        ((IWBrowserView)obj).setControlFrameParameter(ctrlFP);
//        Parameter src = new Parameter(PRM_IW_BROWSE_EVENT_SOURCE, frame.getName());
//        ((IWBrowserView)obj).setSourceParamenter(src);
      }
    //}
  }

  protected void addToFrame(Frame frame, IWBrowserCompliant obj){
    if (obj instanceof PresentationObject ){

      if(frame.getPresentationObject() != null){
          ((Page)frame.getPresentationObject()).add((PresentationObject)obj);
      } else {
        if(obj instanceof Page){
          frame.setPresentationObject((Page)obj);
        } else {
          /**
           * @todo
           */
          Page page = new Page();
          page.add(obj);
          frame.setPresentationObject(page);
        }
      }
    } else {
      // unexpected Object
      System.err.println("IWBrowser.addToFrame(Frame frame, SomeName obj) - unexpected Object type");
    }
  }

  protected void addToFrame(IWBrowserCompliant obj, int pos){
    IWBrowserFrame frame = null;
    switch (pos) {
      case POS_TOP:
        frame = this.getTopFrame();
        addToFrame(frame, obj);
        break;
      case POS_MENU:
        frame = this.getMenuFrame();
        addToFrame(frame, obj);
        break;
      case POS_BOTTOM:
        frame = this.getBottomFrame();
        addToFrame(frame, obj);
        break;
      case POS_EVENT:
        frame = this.getEventFrame();
        addToFrame(frame, obj);
        break;
      case POS_MAIN:
        frame = this.getMainFrame();
        addToFrame(frame, obj);
        break;
      case POS_LEFTMAIN:
        frame = this.getLeftMainFrame();
        addToFrame(frame, obj);
        break;
      case POS_RIGHTMAIN:
        frame = this.getRightMainFrame();
        addToFrame(frame, obj);
        break;
      default :
        // throw new Exception("Position not defined");
        break;
    }

//
//    switch (pos) {
//      case POS_TOP:
//
//        break;
//      case POS_MENU:
//
//        break;
//      case POS_MAIN:
//
//        break;
//      case POS_LEFTMAIN:
//
//        break;
//      case POS_RIGHTMAIN:
//
//        break;
//      default :
//        // throw new Exception("Position not defined");
//        break;
//    }
//
  }



  public Page getControlFramePresentation(IWUserContext iwc, boolean askForPermission){
    return getFrame(this.getFrameName(this._controlPosition), iwc, askForPermission);
  }


  public void _main(IWContext iwc) throws Exception {
    //System.out.println("in _main()");

    if(this._showTopFrame || this._showMenuFrame || this._showBottomFrame || this._showEventFrame){
      if(this._showTopFrame){
        this.add(this.getTopFrame());
      }

      if(this._showMenuFrame){
        this.add(this.getMenuFrame());
      }

      if(this._showLeftMainFrame || this._showRightMainFrame ){
        if(this._showLeftMainFrame){
          this._middleFrameset.add(this.getLeftMainFrame());
        }

        this._middleFrameset.add(this.getMainFrame());

        if(this._showRightMainFrame){
          this._middleFrameset.add(this.getRightMainFrame());
        }

        IWBrowserFrame bFrame = this.getMiddleFrame();
        bFrame.setPresentationObject(this._middleFrameset);
        this.add(bFrame);

      } else {
        this.add(this.getMainFrame());
      }
      
      if(this._showBottomFrame) {
      		this.add(this.getBottomFrame());
      }
      
      if(this._showEventFrame){
        this.add(this.getEventFrame());
      }

    } else if (this._showLeftMainFrame || this._showRightMainFrame ) {
      this.setMarkupAttributes(this._middleFrameset.getMarkupAttributes());

      if(this._showLeftMainFrame){
        this.add(this.getLeftMainFrame());
      }

      this.add(this.getMainFrame());

      if(this._showRightMainFrame){
        this.add(this.getRightMainFrame());
      }

    } else {
      this.add(this.getMainFrame());
    }
//  get all change listeners
    Collection changeListeners;
    try {
      IWStateMachine stateMachine = (IWStateMachine) IBOLookup.getSessionInstance(iwc, IWStateMachine.class);
      changeListeners = stateMachine.getAllChangeListeners();
    }
    catch (RemoteException e) {
      changeListeners = new ArrayList();
    }


//    System.out.println("IWBrowser: addChangeListener ...");
    Frame ctrlFrame = this.getControlframe();

    if(ctrlFrame != null){
      PresentationObject ctrlFrameObject = ctrlFrame.getPresentationObject();
//      System.out.println("IWBrowser: addChangeListener ...0");
      if(ctrlFrameObject instanceof IWBrowseControl){
//        System.out.println("IWBrowser: addChangeListener ...1");
        //ChangeListener ctrlFrameListener = ((IWBrowseControl)ctrlFrameObject).getChangeControler();
        List l = this.getAllContainedFrames();
        if(l != null){
//          System.out.println("IWBrowser: addChangeListener ...2");
          Iterator iter = l.iterator();
          while (iter.hasNext()) {
//            System.out.println("IWBrowser: addChangeListener ...3 while");
            Frame item = (Frame)iter.next();
  //          if(item != ctrlFrame){
              PresentationObject obj = item.getPresentationObject();
              if(obj instanceof StatefullPresentation){
//                System.out.println("IWBrowser: addChangeListener -> "+ctrlFrameListener);
                ///////////((StatefullPresentation)obj).getPresentationState(iwc).addChangeListener(ctrlFrameListener);
                Iterator iterator = changeListeners.iterator();
                while (iterator.hasNext())  {
                  ((StatefullPresentation)obj).getPresentationState(iwc).addChangeListener((ChangeListener) iterator.next());
                }
              }
  //          }
          }
        }
      }
    }


    super._main(iwc);

  }





  protected class IWBrowserFrame extends Frame { //implements IWBrowserCompliant{

//    private boolean _isControlFrame = false;

    public IWBrowserFrame(){

    }

//    public boolean isControlFrame(){
//      return _isControlFrame;
//    }

    public void setPresentationObject(PresentationObject obj){
      if(!(obj instanceof IWBrowserCompliant)){
        // Warning
      }
      super.setPresentationObject(obj);
    }

    public void setPresentationObject(IWBrowserCompliant obj){
      if(obj instanceof PresentationObject){
        super.setPresentationObject((PresentationObject)obj);
      } else {
        //Error
      }
    }


//    public IWEventListener getListener(){return null;}

  }




//  public class TopFrame extends Window {}
//  public class MenuFrame extends Window {}
//
//  public class MainFrame extends FrameSet {
//
//    public MainFrame(){
//      this.add(LeftMain.class, frameNameMainLeft);
//      this.add(RightMain.class, frameNameMainRight);
//      this.setHorizontal();
//    }
//
//  }
//
//  public class LeftMain extends Window {}
//  public class RightMain extends Window {}

//  public void setPresentationState( IWPresentationState state ){
//    IWStateMachine stateMachine = IBOLookup.getSessionInstance(this.getIWUserContext(),IWStateMachine.class);
//    if(state instanceof IWBrowserPresentationState){
//      _presentationState = (IWBrowserPresentationState)state;
//    } else {
//      System.err.println("PresentationState not instanceof IWBrowserPresentationState");
//    }
//
//  }

  public IWPresentationState getPresentationState(IWUserContext iwuc){
    if(this._presentationState == null){
      try {
        IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
        this._presentationState = (IWBrowserPresentationState)stateMachine.getStateFor(getCompoundId(),this.getPresentationStateClass());
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
    }
    return this._presentationState;
  }

  public Class getPresentationStateClass(){
    return IWBrowserPresentationState.class;
  }




}