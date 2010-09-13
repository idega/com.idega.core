//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;

import com.idega.idegaweb.IWURL;
import com.idega.presentation.ui.Window;
import com.idega.util.StringUtil;

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

  private Map<Integer, Map<String, String>> framesMap;

  public FrameSet(){
    setFrameBorder(0);
    setBorder(0);
    setFrameSpacing(0);
    setVertical();
  }

  @Override
public void add(String frameURL){
    this.numberOfFrames++;
    setPage(this.numberOfFrames, frameURL);
  }

  public void add(Class<? extends UIComponent> pageClass){
    this.numberOfFrames++;
    setPage(this.numberOfFrames, pageClass);
  }

  public void add(Class<? extends UIComponent> pageClass, String frameName ){
    this.numberOfFrames++;
    setPage(this.numberOfFrames, pageClass);
    this.setFrameName(this.numberOfFrames, frameName);
  }

  private void setPage(int frameIndex, Class<? extends UIComponent> pageClass){
    this.getFramesMap().put(new Integer(frameIndex),new HashMap<String, String>());
    this.getFramesPropertyMap(frameIndex).put(CLASS_PROPERTY, pageClass.getName());
  }

  private void setPage(int frameIndex, String url){
    this.getFramesMap().put(new Integer(frameIndex), new HashMap<String, String>());
    setFrameSource(frameIndex,url);
  }

  @SuppressWarnings("unchecked")
public Class<? extends UIComponent> getClass(int frameIndex){
    String className = getFramesPropertyMap(frameIndex).get(CLASS_PROPERTY);
    if (StringUtil.isEmpty(className)) {
    	return null;
    }
    
    Class<?> theClass = null;
    try {
		theClass = Class.forName(className);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return (Class<? extends UIComponent>) theClass;
  }

  /**
   * Does nothing
   */
  @Override
protected void add(int index,PresentationObject modObject) {
  }

  /**
   * Does nothing
   */
  @Override
public void add(PresentationObject modObject) {
  }

  /**
   * Does nothing
   */
  @Override
public void add(Object presentationObject) {
  }

  /**
   * Does nothing
   */
  @Override
public void addAtBeginning(PresentationObject modObject) {
  }

    private Map<Integer, Map<String, String>> getFramesMap(){
      if(this.framesMap==null){
        this.framesMap = new HashMap<Integer, Map<String, String>>();
      }
      return this.framesMap;
    }

    private Map<String, String> getFramesPropertyMap(int frameIndex){
    	Map<String, String> props = getFramesMap().get(frameIndex);
    	if (props == null) {
    		props = new HashMap<String, String>();
    		getFramesMap().put(frameIndex, props);
    	}
    	return props;
    }

    protected void setFrameSetProperty(String name,String value){
      setMarkupAttribute(name,value);
    }

    protected String getFrameSetPropertiesString(){
      return getMarkupAttributesString();
    }

    @Override
	public void _main(IWContext iwc)throws Exception{
      this.isInAWindow = isChildOfOtherPage();
      if( this.isInAWindow ){
        this.getParentPage().setAddBody(false);
      }

      super._main(iwc);
      this.adaptFrames(iwc);
    }

    private void adaptFrames(IWContext iwc){
      if(this.numberOfFrames==1){
        add(com.idega.presentation.Page.class);
        setSpanPercent(1,100);
        setSpanAdaptive(2);
      }
      setSpanAttribute();

      int i = 1;
      while (i<=this.numberOfFrames) {

        Class<? extends UIComponent> item = this.getClass(i);
        if(item!=null){
          setFrameSource(i,getFrameURI(item,iwc));
        }
        i++;
      }

    }

    @Override
	public void print(IWContext iwc) throws Exception{
      //goneThroughMain = false;

    		printBegin(iwc);
   
    		printEnd(iwc);
    }
    
    
    @Override
	public void printBegin(IWContext iwc){
        StringBuffer buf = new StringBuffer();

        if( !this.isInAWindow ){
  		String characterEncoding = iwc.getApplicationSettings().getCharacterEncoding();
		String markup = getMarkupLanguageForPage();
		String docType = getDocType();
          buf.append(getStartTag(iwc.getCurrentLocale(), docType, characterEncoding));
          buf.append(getMetaInformation(markup, characterEncoding));
          buf.append("<title>"+getTitle()+"</title>");
        }

        buf.append("\n<frameset ");
        buf.append(getFrameSetPropertiesString());
        buf.append(" >\n");
        
        print(buf.toString());
    }

    @Override
	public void printEnd(IWContext iwc){
    		StringBuffer buf = new StringBuffer();

		int counter = 1;
		while(counter<=this.numberOfFrames){
		
		  buf.append("<frame ");
		  buf.append(getFramePropertiesString(counter));
		  buf.append(" >\n");
		  counter++;
		}        
    		
        buf.append("\n</frameset>\n");

        if( !this.isInAWindow ){
          buf.append(getEndTag());
        }

        print(buf.toString());
    }
    
    private static String getFrameURI(Class<? extends UIComponent> pageClass,IWContext iwc){
    		String uri = iwc.getIWMainApplication().getWindowOpenerURI(pageClass);
    		return uri;
    }

    public static IWURL getFrameURL(Class<? extends UIComponent> pageClass,IWContext iwc){
      return new IWURL(iwc.getIWMainApplication().getWindowOpenerURI(pageClass));
    }

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
      this.alignment=ALIGNMENT_VERTICAL;
    }

    public void setHorizontal(){
      this.alignment=ALIGNMENT_HORIZONTAL;
    }

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
      setFrameProperty(frameIndex,ROWS_PROPERTY,star);
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
      this.getFramesPropertyMap(frameIndex).put(propertyName,slash);
    }

    protected String getFrameProperty(int frameIndex,String propertyName){
      Map<String, String> frameProperties = getFramesPropertyMap(frameIndex);
      if(frameProperties == null){
        return null;
      }
      return frameProperties.get(propertyName);
    }

    protected String getFramePropertiesString(int frameIndex){
      Map<String, String> frameProperties = getFramesPropertyMap(frameIndex);
      StringBuffer returnString = new StringBuffer();
      String attribute ="";
      if (frameProperties != null) {
        for (Iterator<String> e = frameProperties.keySet().iterator(); e.hasNext();) {
          attribute = e.next();
          if(!(attribute.equals(ROWS_PROPERTY)||attribute.equals(CLASS_PROPERTY))){
            returnString.append(" ");
            returnString.append(attribute);

            String AttributeValue = frameProperties.get(attribute);
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
      for (i = 1; i < this.numberOfFrames; i++) {
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
      return (ALIGNMENT_VERTICAL==this.alignment);
    }

    public boolean isHorizontal(){
      return (ALIGNMENT_HORIZONTAL==this.alignment);
    }

    public void setSpan(){
      boolean nothingset=true;
      for (int i = 1; i <= this.numberOfFrames ; i++){

        String span= getSpan(i);
        if(!span.equals(star)){
          nothingset=false;
        }
      }
      if(nothingset){
        if(this.numberOfFrames!=0){
          int thePercent = (100/this.numberOfFrames);
          for (int i = 1; i <= this.numberOfFrames ; i++) {
              setSpanPercent(i,thePercent);
          }
        }
      }
    }
}