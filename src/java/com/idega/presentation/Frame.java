package com.idega.presentation;
import com.idega.idegaweb.*;


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
    public static final String COMPOUND_ID_FRAME_NAME_KEY = ":frame:";    
    private int type = NONE;

    private static final String star = "*";
    private static final String PERCENTSIGN = "%";
    protected final static String ROWS_PROPERTY = "ROWS";

    public Frame(){
      this.getLocation().isInFrameSet(true);
    }


    /**
     *  Returns   the last occurence of a frame name in the specified compoundId
     * 	or null.
     * 	If	 the	 compoundId points to a frame it returns the corresponding
     * 	frame		name. if the compoundId does not point to a frame it returns the
     * 	name	 	of	 the frame the objects is contained.
     * 	@param	 compoundId
     * 	@return	 the name of a frame
 		*/
    public static String getFrameName(String compoundId) {
      int i = compoundId.lastIndexOf(Frame.COMPOUND_ID_FRAME_NAME_KEY);
      if (i < 0)
        return null;
      i += Frame.COMPOUND_ID_FRAME_NAME_KEY.length();
      String frameName = compoundId.substring(i);
      i = frameName.indexOf(PresentationObject.COMPOUNDID_COMPONENT_DELIMITER);
      // the compoundId points to a frame
      if (i < 0)
        return frameName;
      else
      // the compoundId does not point to a frame
        return frameName.substring(0, i);
    }


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
      this.setMarkupAttribute("name", frameName);

//      if(_obj != null){
//        IWLocation location = _obj.getLocation();
//        if(location == null){
//          if(this.getLocation() != null){
//            location = this.getLocation();
//            _obj.setLocation(location);
//          } else {
//            //Warning applicationClass Not set
//            location = new IWPresentationLocation();
//            location.isInFrameSet(true);
//            this.setLocation(location);
//          }
//        }
//        location.setTarget(frameName);
//      } else {
//        this.getLocation()
//      }

      this.getLocation().setTarget(frameName);

    }


  public void setLocation(IWLocation location){
    location.isInFrameSet(true);
    super.setLocation(location);
    if(_obj != null && !(_obj instanceof FrameTable)){
      _obj.setLocation(location);
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
        _obj = obj;
      } else {
        type = OBJ;

        _obj = obj;

//        System.out.println("Frame.setPresentationObject().this.location: "+this.getLocation()+" ->"+this.getLocation().getLocationString());
//        System.out.println("Frame.setPresentationObject()._obj.location: "+_obj.getLocation()+" ->"+_obj.getLocation().getLocationString());

        _obj.setLocation(this.getLocation());

//        System.out.println("And then");
//        System.out.println("Frame.setPresentationObject().this.location: "+this.getLocation()+" ->"+this.getLocation().getLocationString());
//        System.out.println("Frame.setPresentationObject()._obj.location: "+_obj.getLocation()+" ->"+_obj.getLocation().getLocationString());
//        System.out.println("");
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
              return (Page)pObj.clonePermissionChecked(iwc, askForPermission);
            } else {
              //Page page = new Page();
              Page page = defaultPage;
              page.setLocation(this.getLocation());
              page.add(pObj.clonePermissionChecked(iwc, askForPermission));
              return page;
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
            return (Page)_obj.clonePermissionChecked(iwc, askForPermission);
          } else {
            //Page page = new Page();
            Page page = defaultPage;
            page.setLocation(this.getLocation());
            page.add(_obj.clonePermissionChecked(iwc,askForPermission));
            return page;
          }
        default:
          return null;
      }

    }

    public void setSpanPercent(int percent){
      this.setMarkupAttribute(ROWS_PROPERTY,Integer.toString(percent)+PERCENTSIGN);
    }

    /**
     * Sets the span (in pixels) for each of the Frame Objects. frameIndex starts at 1.
     */
    public void setSpanPixels(int pixels){
      this.setMarkupAttribute(ROWS_PROPERTY,Integer.toString(pixels));
    }

    public void setSpanAdaptive(){
      setMarkupAttribute(ROWS_PROPERTY,this.star);
    }

    private String getSpan(){
      String frameProperty = this.getMarkupAttribute(ROWS_PROPERTY);
      if(frameProperty==null){
        frameProperty=star;
      }
      return frameProperty;
    }

    public void setNoresize(boolean ifResize){
      if(ifResize){
        this.setMarkupAttributeWithoutValue("noresize");
      }
    }

    public void setBorder(int borderWidth){
        setMarkupAttribute("border",Integer.toString(borderWidth));
    }

    public void setBorder(boolean ifBorder){
      if(ifBorder){
        setMarkupAttribute("border","yes");
      }
      else{
        setMarkupAttribute("border","no");
      }
    }

    public void setScrollingAuto(){
        setMarkupAttribute("scrolling","auto");
    }

    public void setScrolling(boolean ifScrollBar){
      if(ifScrollBar){
        setMarkupAttribute("scrolling","yes");
      }
      else{
        setMarkupAttribute("scrolling","no");
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
  
  /**
	 * @see com.idega.presentation.PresentationObject#getComponentId()
	 */
	public String getComponentId() {
		StringBuffer buffer = new StringBuffer(super.getComponentId());
    String frameName = this.getMarkupAttribute("name");
    buffer.append(Frame.COMPOUND_ID_FRAME_NAME_KEY).append(frameName);
    return buffer.toString();
    
	}


  }