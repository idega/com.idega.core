package com.idega.presentation;
import com.idega.idegaweb.IWUserContext;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

  public class Frame extends PresentationObject{
    private Class myClass = null;
    private String _url = null;
    private PresentationObject _obj = null;
    public static final int NONE = 0;
    public static final int CLASS = 1;
    public static final int URL = 2;
    public static final int OBJ = 3;
    public static final int FRAMESET = 4;
    private int type = NONE;

    private static final String star = "*";
    private static final String PERCENTSIGN = "%";
    protected final static String ROWS_PROPERTY = "ROWS";

    public void setUrlProperty(String url){
      type = URL;
      _url = url;
    }

    public String getUrlProperty(){
      return _url;
    }

    public void setClassProperty(Class pageClass){
      type = CLASS;
      myClass = pageClass;
    }

    public Class getClassProperty(){
      return myClass;
    }

    public void setNameProperty(String frameName){
      this.setName(frameName);
      this.setAttribute("name", frameName);
      if(_obj != null){
        _obj.setLocation(frameName);
      }
    }

    public void setNameProperty(int name){
      this.setNameProperty(Integer.toString(name));
    }

    public void setPresentationObject(PresentationObject obj){
      //type = (obj instanceof FrameTable)? FRAMESET:OBJ;
      if(obj instanceof FrameTable){
        type = FRAMESET;
        obj.setParentObject(this);
      } else {
        type = OBJ;
      }
      _obj = obj;

      if(this.getAttribute("name") != null){
        _obj.setLocation(this.getName());
      }
    }

    public PresentationObject getPresentationObject(){
      return _obj;
    }

    public int getFrameType(){
      return type;
    }

    public Page getPage(IWUserContext iwc, boolean askForPermission){

      Page defaultPage = new Page();
      //defaultPage.setBackgroundColor("#336699");
      switch (type) {
        case CLASS:
          try {
            PresentationObject pObj = (PresentationObject)myClass.newInstance();
            if(pObj instanceof Page){
              return (Page)pObj._clone(iwc, askForPermission);
            } else {
              //Page page = new Page();
              Page page = defaultPage;
              page.add(pObj._clone(iwc, askForPermission));
              return (Page)page;
            }
          }
          catch (IllegalAccessException ex) {
            ex.printStackTrace();
          }
          catch (InstantiationException ex) {
            ex.printStackTrace();
          }
          return null;
        case OBJ:
          if(_obj instanceof Page){
            return (Page)_obj._clone(iwc, askForPermission);
          } else {
            //Page page = new Page();
            Page page = defaultPage;
            page.add(_obj._clone(iwc,askForPermission));
            return (Page)page;
          }
        default:
          return null;
      }

    }

    public void setSpanPercent(int percent){
      this.setAttribute(ROWS_PROPERTY,Integer.toString(percent)+PERCENTSIGN);
    }

    /**
     * Sets the span (in pixels) for each of the Frame Objects. frameIndex starts at 1.
     */
    public void setSpanPixels(int pixels){
      this.setAttribute(ROWS_PROPERTY,Integer.toString(pixels));
    }

    public void setSpanAdaptive(){
      setAttribute(ROWS_PROPERTY,this.star);
    }

    private String getSpan(){
      String frameProperty = this.getAttribute(ROWS_PROPERTY);
      if(frameProperty==null){
        frameProperty=star;
      }
      return frameProperty;
    }

    public void setNoresize(boolean ifResize){
      if(ifResize){
        this.setAttribute("noresize");
      }
    }

    public void setBorder(int borderWidth){
        setAttribute("border",Integer.toString(borderWidth));
    }

    public void setBorder(boolean ifBorder){
      if(ifBorder){
        setAttribute("border","yes");
      }
      else{
        setAttribute("border","no");
      }
    }

    public void setScrollingAuto(){
        setAttribute("scrolling","auto");
    }

    public void setScrolling(boolean ifScrollBar){
      if(ifScrollBar){
        setAttribute("scrolling","yes");
      }
      else{
        setAttribute("scrolling","no");
      }
    }



//    public void setMarginWidth(int width) {
//      setAttribute("marginwidth",Integer.toString(width));
//      //getPage(frameIndex).setMarginWidth(width);
//    }
//
//    public void setMarginHeight(int height) {
//      setAttribute("marginheight",Integer.toString(height));
//      //getPage(frameIndex).setMarginHeight(height);
//    }
//
//    public void setLeftMargin(int leftmargin) {
//      setAttribute("leftmargin",Integer.toString(leftmargin));
//      //getPage(frameIndex).setMarginWidth(leftmargin);
//    }
//
//    public void setTopMargin(int topmargin) {
//      setAttribute("topmargin",Integer.toString(topmargin));
//      //getPage(frameIndex).setTopMargin(topmargin);
//    }


  public void _main(IWContext iwc) throws Exception{
    if(this.getFrameType()== Frame.FRAMESET){
      _obj._main(iwc);
    }
  }

  }