//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.business.IBOLookup;
import com.idega.event.IWFrameBusiness;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWPresentationLocation;
import com.idega.idegaweb.IWURL;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.ui.Window;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class FrameTable extends Window{

  private int alignment;
  //private int numberOfFrames=0;
  private boolean isInAWindow = false;

  private static final int ALIGNMENT_VERTICAL=1;
  private static final int ALIGNMENT_HORIZONTAL=2;
  private static final String COLS_PROPERTY="cols";

  private static final String SOURCE_PROPERTY="src";

  private static final String star = "*";
  private static final String PERCENTSIGN = "%";

//  private List containedFrameSets = new Vector();
  //private boolean _isInFrame = false;


  private int frameNameCounter = 0;


  public FrameTable(){
    setFrameBorder(0);
    setBorder(0);
    setFrameSpacing(0);
    setVertical();
    IWLocation loc = this.getLocation();
    loc.setApplicationClass(this.getClass());
//    loc.setTarget(null);
    loc.isInFrameSet(true);
  }

  public void add(String frameURL){
    setPage(frameURL);
  }

  public void add(Class pageClass){
    setPage(pageClass);
  }

  public void add(PresentationObject obj, String frameName){
    if(!(obj instanceof Frame) ){
      Frame frame = new Frame();

      IWLocation location = new IWPresentationLocation();
      location.isInFrameSet(true);
      location.setApplicationClass(this.getClass());
      location.setTarget(frameName);

      frame.setLocation(location);
      frame.setPresentationObject(obj);
      frame.setNameProperty(frameName);

      this.add(frame);
    } else {
      ((Frame)obj).setNameProperty(frameName);
      this.add(obj);
    }


  }

  public void add(PresentationObject obj){
    if(!(obj instanceof Frame)){
      Frame frame = new Frame();

      IWLocation location = new IWPresentationLocation();
      location.isInFrameSet(true);
      location.setApplicationClass(this.getClass());

      frame.setLocation(location);
      frame.setPresentationObject(obj);
      frame.setNameProperty(frameNameCounter++);
      // super.add() but does not set Location = this.location;
      super.add(obj);
      /*try {
        if (theObjects == null) {
          this.theObjects = new ArrayList();
        }
        if (obj != null) {
          obj.setParentObject(this);
          //modObject.setLocation(this.getLocation());
          this.theObjects.add(obj);
        }
      }
      catch(Exception ex) {
        //ExceptionWrapper exep = new ExceptionWrapper(ex,this);
      }*/
    }else{
    	super.add(obj);
      // super.add() but does not set Location = this.location;
      /*try {
        if (theObjects == null) {
          this.theObjects = new ArrayList();
        }
        if (obj != null) {
          obj.setParentObject(this);
          //modObject.setLocation(this.getLocation());
          this.theObjects.add(obj);
        }
      }
      catch(Exception ex) {
        //ExceptionWrapper exep = new ExceptionWrapper(ex,this);
      }*/
    }

  }

  public void add(int index, PresentationObject obj){
    if(!(obj instanceof Frame)){
      Frame frame = new Frame();
 
      IWLocation location = new IWPresentationLocation();
      location.isInFrameSet(true);
      location.setApplicationClass(this.getClass());

      frame.setLocation(location);
      frame.setPresentationObject(obj);
      frame.setNameProperty(frameNameCounter++);

      // super.add() but does not set Location = this.location;
      super.add(index,obj);
      /*try {
        if (theObjects == null) {
          this.theObjects = new ArrayList();
        }
        if (obj != null) {
          obj.setParentObject(this);
          //obj.setLocation(this.getLocation());
          this.theObjects.add(index,obj);
        }
      }
      catch(Exception ex) {
        //ExceptionWrapper exep = new ExceptionWrapper(ex,this);
      }*/
    }else{
      // super.add() but does not set Location = this.location;
    	super.add(index,obj);
      /*try {
        if (theObjects == null) {
          this.theObjects = new ArrayList();
        }
        if (obj != null) {
          obj.setParentObject(this);
          //obj.setLocation(this.getLocation());
          this.theObjects.add(index,obj);
        }
      }
      catch(Exception ex) {
        //ExceptionWrapper exep = new ExceptionWrapper(ex,this);
      }*/
    }
  }

  public void addAtBeginning(PresentationObject obj){
    if(!(obj instanceof Frame)){
      Frame frame = new Frame();

      IWLocation location = new IWPresentationLocation();
      location.isInFrameSet(true);
      location.setApplicationClass(this.getClass());

      frame.setLocation(location);
      frame.setPresentationObject(obj);
      frame.setNameProperty(frameNameCounter++);

      // super.addAtBeginning() but does not set Location = this.location;

      obj.setParentObject(this);
      obj.setLocation(this.getLocation());
      getChildren().add(0,obj);

    }else{

      // super.addAtBeginning() but does not set Location = this.location;

      obj.setParentObject(this);
      obj.setLocation(this.getLocation());
      getChildren().add(0,obj);

    }
  }

  public void add(Class pageClass, String frameName ){
//    numberOfFrames++;
//    setPage(numberOfFrames, pageClass);
//    this.setFrameName(numberOfFrames, frameName);
      Frame frame = new Frame();
      frame.setClassProperty(pageClass);
      frame.setNameProperty(frameName);
      this.add(frame);
  }

//  private void setPage(int frameIndex, Class pageClass){
//    this.getFramesMap().put(new Integer(frameIndex),new HashMap());
//    this.getFramesPropertyMap(frameIndex).put(CLASS_PROPERTY,pageClass);
//  }

  private void setPage(Class pageClass){
//    numberOfFrames++;
//    this.getFramesMap().put(new Integer(numberOfFrames),new HashMap());
//    this.getFramesPropertyMap(numberOfFrames).put(CLASS_PROPERTY,pageClass);
      Frame frame = new Frame();
      frame.setClassProperty(pageClass);
      frame.setNameProperty(frameNameCounter++);
      this.add(frame);
  }

//  private void setPage(int frameIndex, String url){
//    this.getFramesMap().put(new Integer(frameIndex),new HashMap());
//    setFrameSource(frameIndex,url);
//  }

  private void setPage(String url){
//    numberOfFrames++;
//    this.getFramesMap().put(new Integer(numberOfFrames),new HashMap());
//    setFrameSource(numberOfFrames,url);
      Frame frame = new Frame();
      frame.setUrlProperty(url);
      frame.setNameProperty(frameNameCounter++);
      this.add(frame);
  }

//  public Class getClass(int frameIndex){
    //return (Class)getFramesPropertyMap(frameIndex).get(CLASS_PROPERTY);
//    return ((ClassContainer)this.getAllContainingObjects().get(frameIndex-1)).getClassProperty();
//  }


//  /**
//   * Does nothing
//   */
//  protected void add(int index,PresentationObject modObject) {
//
//  }
//
//  /**
//   * Does nothing
//   */
//  public void add(PresentationObject modObject) {
//  }
//
//  /**
//   * Does nothing
//   */
//  public void add(Object presentationObject) {
//  }
//
//  /**
//   * Does nothing
//   */
//  public void addAtBeginning(PresentationObject modObject) {
//  }


  /* //
   //adds the Object to the First Page;
   //
  public void add(PresentationObject obj){
    if(obj instanceof Page){
      add((Page)obj);
    }
    else{
       getFirstFrame().add(obj);
    }
  }


  public void add(Page page){
    numberOfFrames += 1;
    super.add(numberOfFrames-1,page);
    setFrameName(numberOfFrames,page.getID());
    setAllMargins(numberOfFrames,0);
  }

  //
  // adds the Object to the First Page;
  //
  public void add(String string){
    getFirstFrame().add(string);
  }
*/

    /*private Map getClassesMap(){
      if(classesMap==null){
        classesMap = new Hashtable();
      }
      return classesMap;
    }*/

//    private Map getFramesMap(){
//      if(framesMap==null){
//        framesMap = new HashMap();
//      }
//      return framesMap;
//    }

    /*private Map getFramesPropertyMap(Class c){
      return (Map) getPagesMap().get(c);
    }*/

    private Map getFramesPropertyMap(int frameIndex){
      //Class c = (Class)this.getIndexesMap().get(Integer.toString(frameIndex));
      //return getFramesPropertyMap(c);
//      return (Map) getFramesMap().get(new Integer(frameIndex));
      return ((PresentationObject)this.getChildren().get(frameIndex-1)).getMarkupAttributes();
    }

    protected void setFrameSetProperty(String name,String value){
      setMarkupAttribute(name,value);
    }

    protected String getFrameSetPropertiesString(){
      return getMarkupAttributesString();
    }

    public List getAllContainedFrames(){
      List l = this.getChildren();
      List toReturn = new ArrayList();
      if(l != null){
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(item instanceof Frame){
            if(((Frame)item).getFrameType() == Frame.FRAMESET){
              toReturn.addAll(((FrameTable)((Frame)item).getPresentationObject()).getAllContainedFrames());
            } else {
              toReturn.add(item);
            }
          }
        }
      }
      return toReturn;
    }


    public void _main(IWContext iwc)throws Exception{
      try {


        IWFrameBusiness fb = (IWFrameBusiness)IBOLookup.getSessionInstance(iwc,IWFrameBusiness.class);
        fb.retainFrameSet(this);
        List l = this.getAllContainedFrames();
        if(l != null){
          Iterator iter = l.iterator();
          while (iter.hasNext()) {
            Frame frame = (Frame)iter.next();
            modifyFrameObject(iwc, fb, frame);
          }
        }

        //System.out.println("in _main()");
        isInAWindow = isChildOfOtherPage();
        if( isInAWindow ){
          this.getParentPage().setAddBody(false);
        }

        super._main(iwc);
        this.adaptFrames(iwc);

      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    private void adaptFrames(IWContext iwc){
      if(!(getChildren()!=null && getChildren().size() > 0)){
        add(com.idega.presentation.Page.class);
        setSpanPercent(1,100);
        setSpanAdaptive(2);
      }
      setSpanAttribute();

//      int i = 1;
//      while (i<=numberOfFrames) {
//
//        Class item = this.getClass(i);
//        if(item!=null){
//          setFrameSource(i,getFrameURI(item,iwc));
//        }
//        i++;
//      }


      int i = 1;
      List l = this.getChildren();
      if(l != null){
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(item instanceof Frame){
            Frame frame = (Frame)item;
            switch (frame.getFrameType()) {
              case Frame.CLASS:
                setFrameSource(i,getFrameURI(frame.getClassProperty(),iwc));
                break;
              case Frame.URL:
                setFrameSource(i,((Frame)item).getUrlProperty());
                break;
              case Frame.OBJ:
                try {
                  IWFrameBusiness fb = (IWFrameBusiness)IBOLookup.getSessionInstance(iwc,IWFrameBusiness.class);
                  setFrameSource(i,getFrameURI(fb.getFrameSetIdentifier(this),frame.getName(),iwc));
                }
                catch (RemoteException ex) {
                  ex.printStackTrace();
                  //throw new RuntimeException("RemoteException occured");
                  //getFrameURI("null","error",iwc);
                  /**
                   * @todo change
                   */
                  setFrameSource(i,"/errorpage1.jsp");
                }
                break;
              case Frame.FRAMESET:
                //No Source
                break;
              default:
                try {
                  IWFrameBusiness fb = (IWFrameBusiness)IBOLookup.getSessionInstance(iwc,IWFrameBusiness.class);
                  setFrameSource(i,getFrameURI(fb.getFrameSetIdentifier(this),frame.getName(),iwc));
                }
                catch (RemoteException ex) {
                  ex.printStackTrace();
                  //throw new RuntimeException("RemoteException occured");
                  //getFrameURI("null","error",iwc);
                  /**
                   * @todo change
                   */
                  setFrameSource(i,"/errorpage2.jsp");
                }
                break;
            }
          }else {
            setFrameSource(i,"/errorpage3.jsp");
            //Error
          }
          i++;
        }
      }

    }

    public void modifyFrameObject(IWContext iwc, IWFrameBusiness fb, Frame frame) throws RemoteException {
    }

    public void print(IWContext iwc) throws Exception{
      //goneThroughMain = false;
      //System.out.println("in print()");

      StringBuffer buf = new StringBuffer();
      boolean isInFrame = isInFrame();
      if( !isInAWindow && !isInFrame ){
		String characterEncoding = iwc.getApplicationSettings().getCharacterEncoding();
		String markup = iwc.getApplicationSettings().getProperty(Page.MARKUP_LANGUAGE, Page.HTML);
        buf.append(getStartTag(iwc.getCurrentLocale(), markup, characterEncoding));
        buf.append(getMetaInformation(markup, characterEncoding));
        buf.append("<title>"+getTitle()+"</title>");
      }



      buf.append("\n<frameset ");
      buf.append(getFrameSetPropertiesString());
      buf.append(" >\n");


      //int counter = 1;
      if(this.getChildren() != null){
        Iterator iter = this.getChildren().iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(item instanceof Frame){
            if(((Frame)item).getFrameType() == Frame.FRAMESET){
              //System.out.println(buf.toString());
              print(buf.toString());
              buf = new StringBuffer();
              ((Frame)item).getPresentationObject()._print(iwc);
            } else {
              buf.append("<frame ");
              buf.append(getFramePropertiesString((Frame)item));
              buf.append(" >\n");
            }
          }

          //counter++;
        }
      }


      buf.append("\n</frameset>\n");

      if( !isInAWindow && !isInFrame ){
        buf.append(getEndTag());
      }
      //System.out.println(buf.toString());
      print(buf.toString());
    }

    /*private String getFrameURI(Page page,IWContext iwc){
      String uri = iwc.getRequestURI()+"?"+this.IW_FRAME_STORAGE_PARMETER+"="+page.getID();
      return uri;
    }*/

    private static String getFrameURI(Class pageClass,IWContext iwc){
    		//String uri = iwc.getRequestURI()+"?"+IW_FRAME_CLASS_PARAMETER+"="+IWMainApplication.getEncryptedClassName(pageClass);
    		String uri = iwc.getIWMainApplication().getWindowOpenerURI(pageClass);
    		return uri;
    }

    private String getFrameURI(String framePath, String frameName, IWContext iwc){
      String uri = iwc.getRequestURI()+"?"+IW_FRAMESET_PAGE_PARAMETER+"="+framePath+"&"+IW_FRAME_NAME_PARAMETER+"="+frameName;
      return uri;
    }

    public static IWURL getFrameURL(Class pageClass,IWContext iwc){
      /*String uri = getFrameURI(pageClass,iwc);
      IWURL url = new IWURL(uri);
      return url;
      */
      return new IWURL(iwc.getIWMainApplication().getWindowOpenerURI(pageClass));
    }

    /*public static IWURL getFrameURL(Class pageClass){
      String baseURL = IWConstants.SERVLET_WINDOWOPENER_URL;
      IWURL url = new IWURL(baseURL);
      url.addPageClassParameter(pageClass);
      return url;
    }*/

    public void setFrameBorder(int width){
      setFrameSetProperty("frameborder",Integer.toString(width));
    }

    public void setBorder(int width){
      setFrameSetProperty("border",Integer.toString(width));
    }

    public void setFrameSpacing(int width){
      setFrameSetProperty("framespacing",Integer.toString(width));
    }

    public void setVertical(){
      alignment=ALIGNMENT_VERTICAL;
    };

    public void setHorizontal(){
      alignment=ALIGNMENT_HORIZONTAL;
    };

    /**
     * Sets the span (in percent) for each of the Frame Objects. frameIndex starts at 1.
     */
    public void setSpanPercent(int frameIndex,int percent){
      setFrameProperty(frameIndex,ROWS_PROPERTY,Integer.toString(percent)+PERCENTSIGN);
    }

    /**
     * Sets the span (in pixels) for each of the Frame Objects. frameIndex starts at 1.
     */
    public void setSpanPixels(int frameIndex,int pixels){
      setFrameProperty(frameIndex,ROWS_PROPERTY,Integer.toString(pixels));
    }

    /**
     * Sets the span (in pixels) for each of the Frame Objects. frameIndex starts at 1.
     */
    public void setSpanAdaptive(int frameIndex){
      setFrameProperty(frameIndex,ROWS_PROPERTY,this.star);
    }

    private String getSpan(int frameIndex){
      String frameProperty = getFrameProperty(frameIndex,ROWS_PROPERTY);
      if(frameProperty==null){
        frameProperty=star;
      }
      return frameProperty;
    }

    public void setNoresize(int frameIndex,boolean ifResize){
      if(ifResize){
        setFrameProperty(frameIndex,"noresize");
      }
    }

    public void setBorder(int frameIndex,int borderWidth){
        setFrameProperty(frameIndex,"border",Integer.toString(borderWidth));
    }

    public void setBorder(int frameIndex,boolean ifBorder){
      if(ifBorder){
        setFrameProperty(frameIndex,"border","yes");
      }
      else{
        setFrameProperty(frameIndex,"border","no");
      }
    }

    public void setScrollingAuto(int frameIndex){
        setFrameProperty(frameIndex,"scrolling","auto");
    }

    public void setScrolling(int frameIndex,boolean ifScrollBar){
      if(ifScrollBar){
        setFrameProperty(frameIndex,"scrolling","yes");
      }
      else{
        setFrameProperty(frameIndex,"scrolling","no");
      }
    }

    public void setMarginWidth(int frameIndex,int width) {
      setFrameProperty(frameIndex,"marginwidth",Integer.toString(width));
      //getPage(frameIndex).setMarginWidth(width);
    }

    public void setMarginHeight(int frameIndex,int height) {
      setFrameProperty(frameIndex,"marginheight",Integer.toString(height));
      //getPage(frameIndex).setMarginHeight(height);
    }

    public void setLeftMargin(int frameIndex,int leftmargin) {
      setFrameProperty(frameIndex,"leftmargin",Integer.toString(leftmargin));
      //getPage(frameIndex).setMarginWidth(leftmargin);
    }

    public void setTopMargin(int frameIndex,int topmargin) {
      setFrameProperty(frameIndex,"topmargin",Integer.toString(topmargin));
      //getPage(frameIndex).setTopMargin(topmargin);
    }

    public void setAllMargins(int frameIndex,int allMargins) {
      setMarginWidth(frameIndex,allMargins);
      setMarginHeight(frameIndex,allMargins);
      setLeftMargin(frameIndex,allMargins);
      setTopMargin(frameIndex,allMargins);
    }

    public void setFrameName(int frameIndex,String name){
      setFrameProperty(frameIndex,"name",name);
    }

    public void setFrameSource(int frameIndex,String URL){
      setFrameProperty(frameIndex,SOURCE_PROPERTY,URL);
    }

    public String getFrameSource(int frameIndex){
      return getFrameProperty(frameIndex,"src");
    }

    protected void setFrameProperty(int frameIndex,String propertyName,String propertyValue){
      //getPage(frameIndex).setFrameProperty(propertyName,propertyValue);
      //this.getFramesPropertyMap(frameIndex).put(propertyName,propertyValue);
      ((PresentationObject)this.getChildren().get(frameIndex-1)).setMarkupAttribute(propertyName,propertyValue);
    }

    protected void setFrameProperty(int frameIndex,String propertyName){
      //getPage(frameIndex).setFrameProperty(propertyName);
      //this.getFramesPropertyMap(frameIndex).put(propertyName,this.slash);
      ((PresentationObject)this.getChildren().get(frameIndex-1)).setMarkupAttribute(propertyName,this.slash);
    }

    protected String getFrameProperty(int frameIndex,String propertyName){
//      Map frameProperties = getFramesPropertyMap(frameIndex);
//      if(frameProperties == null){
//        return null;
//      }
//      return (String)frameProperties.get(propertyName);
      return ((PresentationObject)this.getChildren().get(frameIndex-1)).getMarkupAttribute(propertyName);
    }

    protected String getFramePropertiesString(Frame frame){
      Map frameProperties = frame.getMarkupAttributes();
      StringBuffer returnString = new StringBuffer();
      String Attribute ="";
      if (frameProperties != null) {
        Iterator e = frameProperties.keySet().iterator();
        while (e.hasNext()){
          Attribute = (String)e.next();
//          if(!(Attribute.equals(ROWS_PROPERTY)||Attribute.equals(CLASS_PROPERTY))){
          if(!(Attribute.equals(ROWS_PROPERTY))){
            returnString.append(" ");
            returnString.append(Attribute);

            String AttributeValue = (String)frameProperties.get(Attribute);
            if(!AttributeValue.equals(slash)){

              returnString.append("=\"");
              returnString.append(AttributeValue);
              returnString.append("\" ");

            }

          }
        }
      }
      return returnString.toString();
    }

    /*public Page getPage(int frameIndex){
      return (Page)this.getAllContainingObjects().get(frameIndex-1);
    }*/

    public void setSpanAttribute(){
      setSpan();

      String property = "";
      String comma = ",";
      int i;
      for (i = 1; i < this.getChildren().size(); i++) {
        property += getSpan(i);
        property += comma;
      }
      property += getSpan(i);
      String propertyName;
      if(isVertical()){
        propertyName = ROWS_PROPERTY;
      }
      else{
        propertyName = COLS_PROPERTY;
      }
      setFrameSetProperty(propertyName,property);
    }

    public boolean isVertical(){
      return (ALIGNMENT_VERTICAL==alignment);
    }

    public boolean isHorizontal(){
      return (ALIGNMENT_HORIZONTAL==alignment);
    }

    public void setSpan(){
      boolean nothingset=true;
      for (int i = 1; i <= this.getChildren().size() ; i++){

        String span= getSpan(i);
        if(!span.equals(star)){
          nothingset=false;
        }
      }
      if(nothingset){
        if(this.getChildren().size()!=0){
          int thePercent = (int)(100/this.getChildren().size());
          for (int i = 1; i <= this.getChildren().size() ; i++) {
              setSpanPercent(i,thePercent);
          }
        }
      }
    }

    public Page getFrame(String frameName, IWUserContext iwc, boolean askForPermission){// throws FrameNotFoundException {
      List l = this.getAllContainedFrames();
      if(l != null){
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          //if(item instanceof Frame){
            if(frameName.equals(((Frame)item).getName())){
              return ((Frame)item).getPage(iwc, askForPermission);
            }
          //}
        }
      }
      return null;
    }

    public Frame getFrame(String frameName){// throws FrameNotFoundException {
      List l = this.getAllContainedFrames();
      if(l != null){
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          //if(item instanceof Frame){
            if(frameName.equals(((Frame)item).getName())){
              return ((Frame)item);
            }
          //}
        }
      }
      return null;
    }

    public boolean isInFrame(){
      return (this.getParentObject() instanceof Frame);
    }



//    private class Frame extends PresentationObject{
//      private Class myClass = null;
//      private String _url = null;
//      private PresentationObject _obj = null;
//      public static final int NONE = 0;
//      public static final int CLASS = 1;
//      public static final int URL = 2;
//      public static final int OBJ = 3;
//      private int type = NONE;
//
//
//      public void setUrlProperty(String url){
//        type = URL;
//        _url = url;
//      }
//
//      public String getUrlProperty(){
//        return _url;
//      }
//
//      public void setClassProperty(Class pageClass){
//        type = CLASS;
//        myClass = pageClass;
//      }
//
//      public Class getClassProperty(){
//        return myClass;
//      }
//
//      public void setNameProperty(String frameName){
//        this.setName(frameName);
//        this.setAttribute("name", frameName);
//      }
//
//      public void setNameProperty(int name){
//        this.setNameProperty(Integer.toString(name));
//      }
//
//      public void setPresentationObject(PresentationObject obj){
//        type = OBJ;
//        _obj = obj;
//      }
//
//      public int getFrameType(){
//        return type;
//      }
//
//      public Page getPage(IWUserContext iwc, boolean askForPermission){
//
//        Page defaultPage = new Page();
//        defaultPage.setBackgroundColor("#336699");
//        switch (type) {
//          case CLASS:
//            try {
//              PresentationObject pObj = (PresentationObject)myClass.newInstance();
//              if(pObj instanceof Page){
//                return (Page)pObj._clone(iwc, askForPermission);
//              } else {
//                //Page page = new Page();
//                Page page = defaultPage;
//                page.add(pObj._clone(iwc, askForPermission));
//                return (Page)page;
//              }
//            }
//            catch (IllegalAccessException ex) {
//              ex.printStackTrace();
//            }
//            catch (InstantiationException ex) {
//              ex.printStackTrace();
//            }
//            return null;
//          case URL:
//            return null;
//          case OBJ:
//            if(_obj instanceof Page){
//              return (Page)_obj._clone(iwc, askForPermission);
//            } else {
//              //Page page = new Page();
//              Page page = defaultPage;
//              page.add(_obj._clone(iwc,askForPermission));
//              return (Page)page;
//            }
//          default:
//            return null;
//        }
//
//      }
//
//    }

//    private class ClassContainer extends PresentationObjectContainer{
//
//      //private static final String CLASS_PROPERTY="iw_frameset_class";
//
//      private Class myClass = null;
//
//      public void setClassProperty(Class pageClass){
//        myClass = pageClass;
//      }
//
//      public Class getClassProperty(){
//        return myClass;
//      }
//
//    }
//
//    private class UrlContainer extends PresentationObjectContainer{
//
//      //private static final String CLASS_PROPERTY="iw_frameset_class";
//
//      private String _url = null;
//
//      public void setUrlProperty(String url){
//        _url = url;
//      }
//
//      public String getUrlProperty(){
//        return _url;
//      }
//
//    }


}//End class
