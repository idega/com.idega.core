//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWURL;
import com.idega.presentation.ui.Window;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class FrameSet extends Window{

  private int alignment;
  private int numberOfFrames=0;
  private boolean isInAWindow = false;

  private static final int ALIGNMENT_VERTICAL=1;
  private static final int ALIGNMENT_HORIZONTAL=2;
  private static final String COLS_PROPERTY="cols";
  private static final String CLASS_PROPERTY="iw_frameset_class";
  private static final String SOURCE_PROPERTY="src";

  private static final String star = "*";
  private static final String PERCENTSIGN = "%";

  private Map framesMap;

  public FrameSet(){
    setFrameBorder(0);
    setBorder(0);
    setFrameSpacing(0);
    setVertical();
  }

  public void add(String frameURL){
    numberOfFrames++;
    setPage(numberOfFrames, frameURL);
  }

  public void add(Class pageClass){
    numberOfFrames++;
    setPage(numberOfFrames, pageClass);
  }

  public void add(Class pageClass, String frameName ){
    numberOfFrames++;
    setPage(numberOfFrames, pageClass);
    this.setFrameName(numberOfFrames, frameName);
  }

  private void setPage(int frameIndex, Class pageClass){
    this.getFramesMap().put(new Integer(frameIndex),new HashMap());
    this.getFramesPropertyMap(frameIndex).put(CLASS_PROPERTY,pageClass);
  }

  private void setPage(Class pageClass){
    numberOfFrames++;
    this.getFramesMap().put(new Integer(numberOfFrames),new HashMap());
    this.getFramesPropertyMap(numberOfFrames).put(CLASS_PROPERTY,pageClass);
  }

  private void setPage(int frameIndex, String url){
    this.getFramesMap().put(new Integer(frameIndex),new HashMap());
    setFrameSource(frameIndex,url);
  }

  private void setPage(String url){
    numberOfFrames++;
    this.getFramesMap().put(new Integer(numberOfFrames),new HashMap());
    setFrameSource(numberOfFrames,url);
  }

  public Class getClass(int frameIndex){
    return (Class)getFramesPropertyMap(frameIndex).get(CLASS_PROPERTY);
  }


  /**
   * Does nothing
   */
  protected void add(int index,PresentationObject modObject) {
  }

  /**
   * Does nothing
   */
  public void add(PresentationObject modObject) {
  }

  /**
   * Does nothing
   */
  public void add(Object presentationObject) {
  }

  /**
   * Does nothing
   */
  public void addAtBeginning(PresentationObject modObject) {
  }


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

    private Map getFramesMap(){
      if(framesMap==null){
        framesMap = new HashMap();
      }
      return framesMap;
    }

    /*private Map getFramesPropertyMap(Class c){
      return (Map) getPagesMap().get(c);
    }*/

    private Map getFramesPropertyMap(int frameIndex){
      //Class c = (Class)this.getIndexesMap().get(Integer.toString(frameIndex));
      //return getFramesPropertyMap(c);
      return (Map) getFramesMap().get(new Integer(frameIndex));
    }

    protected void setFrameSetProperty(String name,String value){
      setMarkupAttribute(name,value);
    }

    protected String getFrameSetPropertiesString(){
      return getMarkupAttributesString();
    }

    public void _main(IWContext iwc)throws Exception{
      isInAWindow = isChildOfOtherPage();
      if( isInAWindow ){
        this.getParentPage().setAddBody(false);
      }

      super._main(iwc);
      this.adaptFrames(iwc);
    }

    private void adaptFrames(IWContext iwc){
      if(numberOfFrames==1){
        add(com.idega.presentation.Page.class);
        setSpanPercent(1,100);
        setSpanAdaptive(2);
      }
      setSpanAttribute();

      int i = 1;
      while (i<=numberOfFrames) {

        Class item = this.getClass(i);
        if(item!=null){
          setFrameSource(i,getFrameURI(item,iwc));
        }
        i++;
      }

    }

    public void print(IWContext iwc) throws Exception{
      //goneThroughMain = false;

      StringBuffer buf = new StringBuffer();

      if( !isInAWindow ){
		String characterEncoding = iwc.getApplicationSettings().getCharacterEncoding();
		String markup = iwc.getApplicationSettings().getProperty(Page.MARKUP_LANGUAGE, Page.HTML);
        buf.append(getStartTag(iwc.getCurrentLocale(), markup, characterEncoding));
        buf.append(getMetaInformation(markup, characterEncoding));
        buf.append("<title>"+getTitle()+"</title>");
      }



      buf.append("\n<frameset ");
      buf.append(getFrameSetPropertiesString());
      buf.append(" >\n");


      int counter = 1;
      while(counter<=this.numberOfFrames){

        buf.append("<frame ");
        buf.append(getFramePropertiesString(counter));
        buf.append(" >\n");
        counter++;
      }


      buf.append("\n</frameset>\n");

      if( !isInAWindow ){
        buf.append(getEndTag());
      }

      //System.out.println("FrameSet - in print()\n"+ buf.toString());
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
      this.getFramesPropertyMap(frameIndex).put(propertyName,propertyValue);
    }

    protected void setFrameProperty(int frameIndex,String propertyName){
      //getPage(frameIndex).setFrameProperty(propertyName);
      this.getFramesPropertyMap(frameIndex).put(propertyName,this.slash);
    }

    protected String getFrameProperty(int frameIndex,String propertyName){
      Map frameProperties = getFramesPropertyMap(frameIndex);
      if(frameProperties == null){
        return null;
      }
      return (String)frameProperties.get(propertyName);
    }

    protected String getFramePropertiesString(int frameIndex){
      Map frameProperties = getFramesPropertyMap(frameIndex);
      StringBuffer returnString = new StringBuffer();
      String Attribute ="";
      if (frameProperties != null) {
        Iterator e = frameProperties.keySet().iterator();
        while (e.hasNext()){
          Attribute = (String)e.next();
          if(!(Attribute.equals(ROWS_PROPERTY)||Attribute.equals(CLASS_PROPERTY))){
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
      for (i = 1; i < numberOfFrames; i++) {
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
      for (int i = 1; i <= numberOfFrames ; i++){

        String span= getSpan(i);
        if(!span.equals(star)){
          nothingset=false;
        }
      }
      if(nothingset){
        if(numberOfFrames!=0){
          int thePercent = (int)(100/numberOfFrames);
          for (int i = 1; i <= numberOfFrames ; i++) {
              setSpanPercent(i,thePercent);
          }
        }
      }
    }

}//End class
