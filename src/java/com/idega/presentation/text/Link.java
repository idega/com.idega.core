/*
 * $Id: Link.java,v 1.65 2002/03/27 19:18:55 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.text;

import com.idega.block.media.business.MediaBusiness;
import java.net.URLDecoder;
import com.idega.util.text.TextSoap;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.ui.Window;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Parameter;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWConstants;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICObjectInstance;
import com.idega.presentation.Image;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.localisation.business.ICLocaleBusiness;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.2
 *@modified by  <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public class Link extends Text{

  private PresentationObject _obj;
  private Window _myWindow = null;
  private Form _formToSubmit;
  private Class _windowClass = null;
  private int icObjectInstanceIDForWindow=-1;

  private Map _ImageLocalizationMap;

  private StringBuffer _parameterString;
  //private String displayString;
  private String _objectType;
  private String windowOpenerJavascriptString=null;

  private static String _sessionStorageName = IWMainApplication.windowOpenerParameter;
  public static final String HASH = "#";
  public static final String JAVASCRIPT = "javascript:";
  public static final String TARGET_ATTRIBUTE = "target";
  public static final String HREF_ATTRIBUTE = "href";


  //private static final String OBJECT_TYPE_WINDOW = "Window";
  protected static final String OBJECT_TYPE_MODULEOBJECT="PresentationObject";
  protected static final String OBJECT_TYPE_TEXT = "Text";
  protected static final String OBJECT_TYPE_IMAGE = "Image";

  public static final String TARGET_NEW_WINDOW = "_new";
  public static final String TARGET_SELF_WINDOW = "_self";
  public static final String TARGET_BLANK_WINDOW = "_blank";
  public static final String TARGET_PARENT_WINDOW = "_parent";
  public static final String TARGET_TOP_WINDOW = "_top";

  private boolean isImageButton = false;
  private boolean isImageTab = false;
  private boolean useTextAsLocalizedTextKey = false;
  private boolean flip = true;

  private boolean hasClass = false;
  private boolean _maintainAllGlobalParameters = false;
  private boolean _maintainBuilderParameters = true;
  private boolean _addSessionId = true;

  private int _imageId;
  private int _onMouseOverImageId;
  private int _onClickImageId;
  private Image _onMouseOverImage = null;
  private Image _onClickImage = null;

  //If Link is constructed to open an instance of an object in a new page via ObjectInstanciator
  private Class classToInstanciate;
  private Class templatePageClass;
  private String templateForObjectInstanciation;

  private List listenerInstances = null;

  private boolean https = false;

  private int dptTemplateId = 0;
  private ICFile file = null;
  private int fileId = -1;



  private final static String DEFAULT_TEXT_STRING = "No text";

  public static boolean usingEventSystem = false;

  /**
   *
   */
  public Link() {
    this(DEFAULT_TEXT_STRING);
  }

  /**
   *
   */
  public Link(String text) {
    this( new Text(text) );
    //displayString = text;
  }

  /**
   *
   */
  public Link(PresentationObject mo, Window myWindow) {
    this.setWindow(myWindow);
    this.setPresentationObject(mo);
  }

  /**
   *
   */
  public Link(Window myWindow) {
    this(new Text(myWindow.getName()),myWindow);
  }

  /**
   *
   */
  public Link(PresentationObject mo) {
    this.setPresentationObject(mo);
  }

  /**
   *
   */
  public Link(Text text) {
    //text.setFontColor("");
    this.setText(text);

  }

  /**
   *
   */
  public Link(String text, String url) {
    this(new Text(text),url);
  }

  /**
   *
   */
  public Link(PresentationObject mo, String url) {
    /*_obj = mo;
    setURL(url);
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
    */
    this.setPresentationObject(mo);
    this.setURL(url);
  }

  /**
   *
   */
  public Link(Text text, String url) {
//    text.setFontColor("");
    this.text = text.getText();
    this.setText(text);
//    _obj = (PresentationObject)text;
//    System.err.println("setUrl"+url);
    setURL(url);
/*    System.err.println("getUrl"+this.getURL());
    if(this._parameterString != null){
      System.err.println("prm"+this._parameterString.toString());
    } else{
      System.err.println("noParameters");
    }
    */
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
  }

  /**
   * For files
   * @deprecated replaced with com.idega.presentation.ui.FilePresentation
   */
  public Link(int file_id) {
    /**
     * @todo: Temporary workaround before this constructor is removed
     */
    this(new Text("File"),IWContext.getInstance().getApplication().getMediaServletURI()+"?file_id="+file_id);
    //this(new Text("File"),com.idega.idegaweb.IWMainApplication.MEDIA_SERVLET_URL+"?file_id="+file_id);
  }

  /**
   * @deprecated replaced with com.idega.presentation.ui.FilePresentation
   */
  public Link(int file_id, String file_name) {
    /**
     * @todo: Temporary workaround before this constructor is removed
     */
    this(new Text(file_name),IWContext.getInstance().getApplication().getMediaServletURI()+"?file_id="+file_id);
    //this(new Text(file_name),com.idega.idegaweb.IWMainApplication.MEDIA_SERVLET_URL+"?file_id="+file_id);
  }

  /**
   * @deprecated replaced with com.idega.presentation.ui.FilePresentation
   */
  public Link(PresentationObject mo, int file_id) {
    super();
    _obj = mo;
    /**
     * @todo: Temporary workaround before this constructor is removed
     */
    setURL(IWContext.getInstance().getApplication().getMediaServletURI()+"?file_id="+file_id);
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_MODULEOBJECT;
  }

  /**
   * @deprecated replaced with com.idega.presentation.ui.FilePresentation
   */
  public Link(int file_id, Window myWindow) {
    setWindow(myWindow);
  }

  /**
   *
   */
  public Link(PresentationObject mo, Class classToInstanciate) {
    //this(mo,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
    this.setPresentationObject(mo);
    setClassToInstanciate(classToInstanciate);
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/

  }


  /**
   *
   */
  public Link(PresentationObject mo, Class classToInstanciate, Class templatePageClass) {
    //this(mo,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,templatePageClass));
    this.setPresentationObject(mo);
    this.setClassToInstanciate(classToInstanciate,templatePageClass);
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/
  }

  public Link(Class classToInstanciate) {
    //this(Link.DEFAULT_TEXT_STRING,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
    this(Link.DEFAULT_TEXT_STRING);
    this.setClassToInstanciate(classToInstanciate);
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/
  }


  /**
   *
   */
  public Link(PresentationObject mo, String classToInstanciate, String template) {
    //this(mo,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
    this.setPresentationObject(mo);
    try{
      this.setClassToInstanciate(Class.forName(classToInstanciate),template);
    }
    catch(Exception e){
      throw new RuntimeException(e.toString()+e.getMessage());
    }
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/
  }

  /**
   * Opens a new object of type classToInstanciate (has to be a PresentationObject)
   * in the same window.
   */
  public Link(String displayText, Class classToInstanciate) {
    //this(displayText,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
    this.setText(displayText);
    this.setClassToInstanciate(classToInstanciate);
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/
  }

  /**
   * Opens a new object of type classToInstanciate (has to be a PresentationObject)
   * in the window of target specified by "target"
   */
  public Link(String displayText, Class classToInstanciate, String target) {
    //this(displayText,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
    this.setText(displayText);
    this.setClassToInstanciate(classToInstanciate);
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/
    setTarget(target);
  }

  /**
   * Opens a new object of type classToInstanciate (has to be a PresentationObject)
   * in the same window with the template of name templateName
   */
  public Link(String displayText, String classToInstanciate, String templateName) {
    //this(displayText,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,templateName));
    this.setText(displayText);
    try{
      this.setClassToInstanciate(Class.forName(classToInstanciate),templateName);
    }
    catch(Exception e){
      throw new RuntimeException(e.toString()+e.getMessage());
    }
    /*if(_parameterString == null){
      _parameterString = new StringBuffer();
    }*/
  }

  /**
   *
   */
  public void setWindow(Window window) {
    _myWindow = window;
    //_objectType = OBJECT_TYPE_WINDOW;
    _myWindow.setParentObject(this);
    if(_obj==null){
      setText(_myWindow.getName());
    }
  }

  /**
   *
   */
  public void setPresentationObject(PresentationObject object) {

    if(object instanceof Image){
      setImage((Image)object);
    }
    else if(object instanceof Text){
      setText((Text)object);
    }
    else if(object instanceof Window){
      setWindow((Window)object);
    }
    else{
      _objectType = OBJECT_TYPE_MODULEOBJECT;
      _obj = object;
      object.setParentObject(this);
    }
  }


  /**
   *
   */
  public void main(IWContext iwc)throws Exception {
    if(fileId!=-1) setURL(MediaBusiness.getMediaURL(fileId,iwc.getApplication()));
    setURIToClassToInstanciate(iwc);
    setURIToWindowOpenerClass(iwc);
    //Builder edit mode
    if(iwc.isInEditMode()){
     addParameter("view","builder");/**@todo this doesn't update all the frames**/

    }


    //if (_objectType==(OBJECT_TYPE_WINDOW)) {
      if (_myWindow != null) {
    String windowOpenerURI = iwc.getApplication().getWindowOpenerURI();
	if (_myWindow.getURL(iwc).indexOf(windowOpenerURI) != -1) {
	  String sessionParameterName = com.idega.servlet.WindowOpener.storeWindow(iwc,_myWindow);
	  addParameter(_sessionStorageName,sessionParameterName);
	}
      }
    //}

    if(_obj != null){
      if (_obj instanceof Image) {
	if(_onMouseOverImage != null){
	  ((Image)_obj).setOverImage(_onMouseOverImage);
	}else if(_onMouseOverImageId > 0){
	  ((Image)_obj).setOverImage(new Image(_onMouseOverImageId));
	}
	if(_onClickImage != null){
	  ((Image)_obj).setOnClickImage(_onClickImage);
	}else if(_onClickImageId > 0){
	  ((Image)_obj).setOnClickImage(new Image(_onClickImageId));
	}
      }

    } else if (_imageId > 0){
      Image image = new Image(_imageId);
      if(_onMouseOverImage != null){
	image.setOverImage(_onMouseOverImage);
      }else if(_onMouseOverImageId > 0){
	image.setOverImage(new Image(_onMouseOverImageId));
      }
      if(_onClickImage != null){
	image.setOnClickImage(_onClickImage);
      }else if(_onClickImageId > 0){
	image.setOnClickImage(new Image(_onClickImageId));
      }
      setImage(image);
    }


    if( isImageButton ){//get a generated button gif image
      if(useTextAsLocalizedTextKey){//the text entered is a local key
        setPresentationObject(iwc.getApplication().getCoreBundle().getResourceBundle(iwc).getLocalizedImageButton(text,text));
      }
      else{
        setPresentationObject(iwc.getApplication().getCoreBundle().getImageButton(text));
      }
    }
    else if( isImageTab ){//get a generated button gif image
      if(useTextAsLocalizedTextKey){//the text entered is a local key
        setPresentationObject(iwc.getApplication().getCoreBundle().getResourceBundle(iwc).getLocalizedImageTab(text,text,flip));
      }
      else{
        setPresentationObject(iwc.getApplication().getCoreBundle().getImageTab(text,flip));
      }
    }

    if(isImageTab||isImageButton ){
      if( isSetToSubmitForm() ){
         ((Image)_obj).removeAttribute("onMouseDown");//so that it doesn't interfere with the link onclick event
      }
    }

    if (_obj != null) {
      _obj.main(iwc);
    }
  }

  /**
   *
   */
  public void setURL(String url) {
    StringTokenizer urlplusprm = new StringTokenizer(url,"?");
    String newUrl = urlplusprm.nextToken();
    if(urlplusprm.hasMoreTokens()){
      String prm = urlplusprm.nextToken();
      StringTokenizer param = new StringTokenizer(prm,"=&");
      while (param.hasMoreTokens()) {
	String p = param.nextToken();
	String v = null;
	if(param.hasMoreTokens()){
	  v = param.nextToken();
	}
	if(v != null){
	  this.addParameter(p,v);
	}
      }
    }
    setAttribute(HREF_ATTRIBUTE,newUrl);
  }

  /**
   *
   */
  public String getURL(IWContext iwc) {
    if (https) {
      return "https://"+iwc.getServerName()+ getAttribute(HREF_ATTRIBUTE);
    }else {
      return(getAttribute(HREF_ATTRIBUTE));
    }
  }

  public String getURL() {
    return(getAttribute(HREF_ATTRIBUTE));
  }

  /**
   *
   */
  public void addParameter(Parameter parameter) {
    addParameter(parameter.getName(),parameter.getValue());
  }

  /**
   *
   */
  public void addParameter(String parameterName,Class theClass) {
    addParameter(parameterName,IWMainApplication.getEncryptedClassName(theClass));
  }


  public boolean isParameterSet(String prmName){
    if(_parameterString != null){
      if(!(prmName != null && !prmName.equals(""))){
	return true;
      }
      String prmString = _parameterString.toString();
      if(prmString.length()>0){
	if((prmString.charAt(0) == '?') && (prmString.length()>1)){
	  prmString = prmString.substring(1,prmString.length());
	}
	if((prmString.charAt(0) == '&') && (prmString.length()>1)){
	  prmString = prmString.substring(1,prmString.length());
	}
	StringTokenizer token = new StringTokenizer(prmString,"&=",false);
	int index = 0;
	while (token.hasMoreTokens()) {
	  String st = token.nextToken();
					if(token.hasMoreTokens()){
	  String value = token.nextToken();
	  if(prmName.equals(st)){
	    return true;
	    //System.out.println("token "+index+" : "+st+" / true");
	    //System.err.println("token "+index+" : "+st+" / true");
	  }
	  //else{
	    //System.out.println("token "+index+" : "+st+" / false");
	    //System.err.println("token "+index+" : "+st+" / false");
	  //}
	  index++;
	}
	}
      }
      else{
	return false;
      }
    }
    return false; // false
  }

  /**
   *
   */
  public void addParameter(String parameterName, String parameterValue) {
    if ((parameterName != null) && (parameterValue != null)) {
      parameterName = java.net.URLEncoder.encode(parameterName);
      parameterValue = java.net.URLEncoder.encode(parameterValue);

      if (_parameterString == null) {
	_parameterString = new StringBuffer();
	_parameterString.append("?");
      }
      else  {
	_parameterString.append("&");
      }

      _parameterString.append(parameterName);
      _parameterString.append("=");
      _parameterString.append(parameterValue);
    }
    else if (parameterName != null) {
      parameterName = java.net.URLEncoder.encode(parameterName);
    }
    else if (parameterValue != null) {
      parameterValue = java.net.URLEncoder.encode(parameterValue);
    }
  }

  /**
   *
   */
  public void addParameter(String parameterName, int parameterValue) {
    addParameter(parameterName,Integer.toString(parameterValue));
  }

  /**
   *
   */
  public void maintainParameter(String parameterName, IWContext iwc) {
    String parameterValue = iwc.getParameter(parameterName);
    if (parameterValue != null) {
      addParameter(parameterName,parameterValue);
    }
  }

  /*
   *
   */
  private void setOnEvent(String eventType, String eventString) {
    setAttribute(eventType,eventString);
  }

  /**
   *
   */
  public void setOnFocus(String s) {
    setOnEvent("onfocus",s);
  }

  /**
   *
   */
  public void setOnBlur(String s) {
    setOnEvent("onblur",s);
  }

  /**
   *
   */
  public void setOnMouseOver(String s) {
    setOnEvent("onmouseover",s);
  }

  /**
   *
   */
  public void setOnMouseOut(String s) {
    setOnEvent("onmouseout",s);
  }

  /**
   *
   */
  public void setOnSelect(String s) {
    setOnEvent("onselect",s);
  }

  /**
   *
   */
  public void setOnChange(String s) {
    setOnEvent("onchange",s);
  }

  /**
   *
   */
  public void setOnClick(String s) {
    setOnEvent("onclick",s);
  }

  /**
   *
   */
  public String getOnFocus() {
    return getAttribute("onfocus");
  }

  /**
   *
   */
  public String getOnBlur() {
    return getAttribute("onblur");
  }

  /**
   *
   */
  public String getOnSelect() {
    return getAttribute("onselect");
  }

  /**
   *
   */
  public String getOnChange() {
    return getAttribute("onchange");
  }

  /**
   *
   */
  public String getOnClick() {
    return getAttribute("onclick");
  }

  /**
   *
   */
  public void setTarget(String target) {
    setAttribute(TARGET_ATTRIBUTE,target);
  }

  /**
   *
   */
  public String getTarget() {
    return getAttribute(TARGET_ATTRIBUTE);
  }

  /**
   *
   */
  public void setFontSize(String s) {
    if (isText()){
      ((Text)_obj).setFontSize(s);
    }
  }

  /**
   *
   */
  public void setFontSize(int i) {
    setFontSize(Integer.toString(i));
  }

  /**
   *
   */
  public void setFontFace(String s) {
    if (isText()){
      ((Text)_obj).setFontFace(s);
    }
  }

  /**
   *
   */
  public void setFontColor(String color) {
    if (isText()){
      ((Text)_obj).setFontColor(color);
    }
  }

  /**
   *
   */
  public void setFontStyle(String style) {
    if (isText()){
      ((Text)_obj).setFontStyle(style);
    }
  }

  /**
   *
   */
  public void setFontClass(String styleClass) {
    if (isText()){
      ((Text)_obj).setFontClass(styleClass);
    }
  }

  public void setStyle(String style) {
    super.setStyle(style);
    this.hasClass = true;
  }

  /**
   *
   */
  public void setSessionId(boolean addSessionId) {
    _addSessionId = addSessionId;
  }

  /**
   *
   */
  public void addBreak() {
    if (isText()){
      ((Text)_obj).addBreak();
    }
  }

  /**
   *
   */
  public void setTeleType() {
    if (isText()){
      ((Text)_obj).setTeleType();
    }
  }

  /**
   *
   */
  public void setBold() {
    if (isText()){
      ((Text)_obj).setBold();
    }
  }

  /**
   *
   */
  public void setItalic() {
    if (isText()){
      ((Text)_obj).setItalic();
    }
  }

  /**
   *
   */
  public void setUnderline() {
    if (isText()){
      ((Text)_obj).setUnderline();
    }
  }

  /**
   *
   */
  public void setText(String text) {
    if (isText()){
      ((Text)_obj).setText(text);
      this.text = text;
    }
    else{
      setText(new Text(text));
      this.text = text;
    }
  }

  public String getText(){
    String toReturn = this.text;
    if(toReturn == null && this._obj == null && this._obj instanceof Text && !(this._obj instanceof Link)){
      toReturn = ((Text)_obj).getText();
    }
    return toReturn;
  }

    /**
   *
   */
  public void setText(Text text) {
    this.text = text.getText();
    _obj = (PresentationObject)text;
    _obj.setParentObject(this);
    _objectType = OBJECT_TYPE_TEXT;
  }


  public void setLocalizedText(String localeString,String text){
      if (isText()){
	((Text)_obj).setLocalizedText(localeString,text);
      }
      else{
	super.setLocalizedText(localeString,text);
      }
  }


  /**
   *
   */
  public void setLocalizedText(int icLocaleID,String text) {
    if (isText()){
      ((Text)_obj).setLocalizedText(icLocaleID,text);
    }
    else{
      super.setLocalizedText(icLocaleID,text);
    }
  }

  /**
   *
   */
  public void setLocalizedText(Locale locale,String text){
    if (isText()){
      ((Text)_obj).setLocalizedText(locale,text);
    }
    else{
      super.setLocalizedText(locale,text);
    }
  }



  /**
   *
   */
  public void addToText(String text) {
    if (isText()){
      ((Text)_obj).addToText(text);
    }
  }

  /**
   *
   */
  public void setTextOnLink(String text) {
    setText(text);
  }

  /**
   *
   */
  public void setObject(PresentationObject object) {
    _obj = object;
    _objectType = OBJECT_TYPE_MODULEOBJECT;

	_obj.setParentObject(this);
  }

  /**
   *
   */
  public void setLocale(String languageString) {
    setEventListener(LocaleSwitcher.class.getName());
    addParameter(LocaleSwitcher.languageParameterString,languageString);
  }

  /**
   *
   */
  public void setLocale(Locale locale) {
    setLocale(locale.toString());
  }

  /**
   * method for adding an image to the link
   */
  public void setImage(Image image) {
    _obj = image;
    _objectType = OBJECT_TYPE_IMAGE;
    _obj.setParentObject(this);
  }

  /*
  public void setLocalizedImage(int icLocaleID,Image image){
      setLocalizedImage(ICLocaleBusiness.getLocale(icLocaleID),image);
  }

  public void setLocalizedImage(Locale locale,Image image){
      setLocalizedImage(locale,image);
  }
  */

  public void setLocalizedImage(String localeString,int imageID){
      setLocalizedImage(ICLocaleBusiness.getLocaleFromLocaleString(localeString),imageID);
  }

  public void setLocalizedImage(Locale locale,int imageID){
      this._objectType=OBJECT_TYPE_IMAGE;
      getImageLocalizationMap().put(locale,new Integer(imageID));
  }

  private Map getImageLocalizationMap(){
    if(_ImageLocalizationMap==null){
      _ImageLocalizationMap=new HashMap();
    }
    return _ImageLocalizationMap;
  }

  private boolean isImage(){
    if(this._objectType==OBJECT_TYPE_IMAGE){
      return true;
    }
    else{
      if(this._obj == null){
	if(this._ImageLocalizationMap != null){
	  return true;
	}
      }
      else{
	if(this._obj instanceof Image){
	  return true;
	}
      }
    }
    return false;
  }

  public boolean isSetToSubmitForm(){
    if( this._formToSubmit!= null ) return true;
    else return false;
  }

  /**
   * Returns the correct Image, localized or not depending on what has been set.
   */
  private Image getTheCorrectDefaultImage(IWContext iwc)throws Exception{
    Integer imageID = getTheCorrectDefaultImageID(iwc);
    if(imageID==null){
      return (Image)_obj;
    }
    else{
      return new Image(imageID.intValue());
    }
  }

  /**
   * Returns the correct ImageID, localized or not depending on what has been set.
   * Returns null if nothing localized shas been set
   */
  private Integer getTheCorrectDefaultImageID(IWContext iwc)throws Exception{
    if(this._ImageLocalizationMap!=null){
      Locale currLocale = iwc.getCurrentLocale();

      Integer localizedImageID = (Integer)this.getImageLocalizationMap().get(currLocale);
      if(localizedImageID!=null){
	return localizedImageID;
      }
      else{
	Integer defImageID = (Integer)this.getImageLocalizationMap().get(iwc.getApplication().getSettings().getDefaultLocale());
	if(defImageID!=null){
	  return defImageID;
	}
      }
    }
    return null;
  }

  public void setImageId(int imageId){
    _imageId = imageId;
  }

  public void setOnMouseOverImage(Image image){
    _onMouseOverImage = image;
  }

  public void setOnMouseOverImageId(int imageId){
    _onMouseOverImageId = imageId;
  }

  public void setOnClickImage(Image image){
    _onClickImage = image;
  }

  public void setOnClickImageId(int imageId){
    _onClickImageId = imageId;
  }

  /**
   *  set the target object instance
   */
  public void setTargetObjectInstance(ICObjectInstance instance){
    if( (instance!=null) && (instance.getID()!=-1) ){
      addParameter(TARGET_OBJ_INS,instance.getID());
    }
  }

  /**
  *  set the target object instance
  */
  public void setTargetObjectInstance(int instanceid){
    addParameter(TARGET_OBJ_INS,instanceid);
  }


  /**
   * method for adding a link to a page object
   */
  public void setPage(IBPage page) {
    if( (page!=null) && (page.getID()!=-1) ){
      /*StringBuffer url = new StringBuffer();
      url.append(IWMainApplication.BUILDER_SERVLET_URL);
      url.append('?');
      url.append(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
      url.append('=');
      url.append(page.getID());
      setURL(url.toString());*/
      String value = this.getParameterValue(BuilderLogic.IB_PAGE_PARAMETER);
      if (value != null) {
	removeParameter(BuilderLogic.IB_PAGE_PARAMETER);
      }

      addParameter(BuilderLogic.IB_PAGE_PARAMETER,page.getID());
    }
  }

  public void setPage(int pageID) {
    IBPage page = null;
    try {
      page = new IBPage(pageID);
    }
    catch (Exception e) {
      page = null;
    }
    setPage(page);
  }

  public int getPage(){
    String value = this.getParameterValue(BuilderLogic.IB_PAGE_PARAMETER);
    if(value != null && !value.equals("")){
      return Integer.parseInt(value);
    }else{
      return 0;
    }
  }


  public String getParameterValue(String prmName){
    if(_parameterString != null){
      if(!(prmName != null && prmName.endsWith(""))){
	return null;
      }
      String prmString = _parameterString.toString();
      if(prmString.length()>0){
	if((prmString.charAt(0) == '?') && (prmString.length()>1)){
	  prmString = prmString.substring(1,prmString.length());
	}
	if((prmString.charAt(0) == '&') && (prmString.length()>1)){
	  prmString = prmString.substring(1,prmString.length());
	}
	StringTokenizer token = new StringTokenizer(prmString,"&=",false);
	int index = 0;
	while (token.hasMoreTokens()) {
	  String st = token.nextToken();
	  if(token.hasMoreTokens()){
	    String value = token.nextToken();
	    if(prmName.equals(st)){
	      return value;
	    }
	    index++;
	  }
	}
      }
      else{
	return null;
      }
    }
    return null;
  }


  /**
   * method for adding a link to a file object
   * the url generation is done in the main method
   */
  public void setFile(ICFile file) {
    this.fileId = file.getID();
  }

  /**
   * @todo reimplement
   */
  public void setFile(int fileId) {
    this.fileId = fileId;
  }

  /**
   *
   */
  public PresentationObject getObject() {
    return(_obj);
  }

  /*
   *
   */
  private boolean isLinkOpeningOnSamePage() {
    return(!isAttributeSet(TARGET_ATTRIBUTE));
  }

  /**
   *
   */
  public Object clone() {
    Link linkObj = null;
    try {
      linkObj = (Link)super.clone();

      if (_obj != null) {
	linkObj._obj = (PresentationObject)_obj.clone();
      }
      if (_myWindow != null) {
	linkObj._myWindow = (Window)_myWindow.clone();
      }

      if (_formToSubmit != null) {
	linkObj._formToSubmit = (Form)_formToSubmit.clone();
      }

      if (_windowClass != null) {
	linkObj._windowClass = _windowClass;
      }

      linkObj._objectType = _objectType;
      linkObj._parameterString = _parameterString;
      linkObj._addSessionId = _addSessionId;
      linkObj._maintainAllGlobalParameters = _maintainAllGlobalParameters;
      linkObj._maintainBuilderParameters = _maintainBuilderParameters;
      linkObj.text = text;
      linkObj.isImageButton = isImageButton;
      linkObj.useTextAsLocalizedTextKey = useTextAsLocalizedTextKey;
      linkObj.isImageTab = isImageTab;
      linkObj.flip = flip;
      linkObj.classToInstanciate=this.classToInstanciate;
      linkObj.templateForObjectInstanciation=this.templateForObjectInstanciation;
      linkObj.templatePageClass=this.templatePageClass;

      if (_parameterString != null) {
	linkObj._parameterString = new StringBuffer(_parameterString.toString());
      }
      if(this._ImageLocalizationMap!=null){
	linkObj._ImageLocalizationMap=(Map)((HashMap)this._ImageLocalizationMap).clone();
      }

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return(linkObj);
  }

  /*
   *
   */
  private void addTheMaintainedParameters(IWContext iwc) {
    List list = com.idega.idegaweb.IWURL.getGloballyMaintainedParameters(iwc);
    if (list != null) {
      Iterator iter = list.iterator();
      while(iter.hasNext()) {
	String parameterName = (String)iter.next();
	String parameterValue = iwc.getParameter(parameterName);
	if (parameterValue != null) {
	  if(!this.isParameterSet(parameterName)){
	    addParameter(parameterName,parameterValue);
	  }
	}
      }
    }
  }


    /*
   *
   */
  private void addTheMaintainedBuilderParameters(IWContext iwc) {
    List list = com.idega.idegaweb.IWURL.getGloballyMaintainedBuilderParameters(iwc);
    //System.out.println("--------------------------------------");
    //System.out.println("builderPrm");
    if (list != null) {
      Iterator iter = list.iterator();
      while(iter.hasNext()) {
	String parameterName = (String)iter.next();
	String parameterValue = iwc.getParameter(parameterName);
	//System.out.print("parameterName = "+parameterName+" , parameterValue = "+parameterValue+" parameterSet = ");
	if (parameterValue != null) {
	  if(!this.isParameterSet(parameterName)){
	    //System.out.println("false");
	    addParameter(parameterName,parameterValue);
	  } else{
	    //System.out.println("true");
	  }
	}else{
	  //System.out.println("null");
	}
      }
    }
  }

  /**
   *
   */
  public void setToMaintainGlobalParameters() {
    _maintainAllGlobalParameters = true;
  }

  public void setToMaintainBuilderParameters(boolean value){
    _maintainBuilderParameters = value;
  }

  /**
   *
   */
  protected String getParameterString(IWContext iwc, String URL) {
    if(usingEventSystem){
    //if(!this.isParameterSet(BuilderLogic.PRM_HISTORY_ID)){
      this.removeParameter(BuilderLogic.PRM_HISTORY_ID);
      this.addParameter(BuilderLogic.PRM_HISTORY_ID,(String)iwc.getSessionAttribute(BuilderLogic.PRM_HISTORY_ID));
      //this.addParameter(BuilderLogic.PRM_HISTORY_ID,"1000");
    //}
    }


    if (_maintainBuilderParameters) {
      addTheMaintainedBuilderParameters(iwc);
    }
    else {
      if (isLinkOpeningOnSamePage()) {
	addTheMaintainedParameters(iwc);
      }
    }

    if (_maintainAllGlobalParameters) {
      addTheMaintainedParameters(iwc);
    }
    else {
      if (isLinkOpeningOnSamePage()) {
	addTheMaintainedParameters(iwc);
      }
    }
    List l = getIWPOListeners();
    if( l != null){
      int size = l.size();
      BuilderLogic logic = BuilderLogic.getInstance();
      if(size>1){
	int[] pages = new int[size];
	int[] inst = new int[size];
	ListIterator lIter = l.listIterator();
	while (lIter.hasNext()) {
	  int index = lIter.nextIndex();
	  Object lItem = lIter.next();
	  if(lItem instanceof String){
	    String str = (String)lItem;
	    int indexof_ = str.indexOf('_');
	    if(indexof_ != -1){
	      try{
		pages[index] = Integer.parseInt(str.substring(0,indexof_));
		inst[index] = Integer.parseInt(str.substring(indexof_+1,str.length()));
	      }catch(NumberFormatException e){
		System.err.println("Link: Listener coordenates not right");
	      }
	    }
	  } else if(lItem instanceof PresentationObject){
	    PresentationObject obj = (PresentationObject)lItem;
	    pages[index] = obj.getParentPageID();
	    inst[index] = obj.getParentObjectInstanceID();
	  }
	}
	logic.setICObjectInstanceListeners(this,pages,inst);
	logic.setICObjectInstanceEventSource(this,this.getParentPageID(),this.getParentObjectInstanceID());
      } else if(size==1){
	PresentationObject obj = (PresentationObject)l.get(0);
	if(obj != null){
	  logic.setICObjectInstanceListener(this,obj.getParentPageID(),obj.getParentObjectInstanceID());
	  logic.setICObjectInstanceEventSource(this,this.getParentPageID(),this.getParentObjectInstanceID());
	}
      }
    }

    if (URL == null) {
      URL = "";
    }
    if((!this.isParameterSet("idega_session_id"))){
      if (_parameterString == null) {
	_parameterString = new StringBuffer();
	if (_addSessionId && (!iwc.isSearchEngine())) {
	  if (URL.equals("#")) {
	    return("");
	  } else if (URL.indexOf("://") == -1) { //does not include ://
	    if (URL.indexOf("?") != -1) {
	      _parameterString.append("&idega_session_id=");
	      _parameterString.append(iwc.getSession().getId());
	      return(_parameterString.toString());
	    } else if ((URL.indexOf("//") != -1) && (URL.lastIndexOf("/") == URL.lastIndexOf("//") + 1 )) {
	      //the case where the URL is etc. http://www.idega.is
	      _parameterString.append("/?idega_session_id=");
	      _parameterString.append(iwc.getSession().getId());
	      return(_parameterString.toString());
	    } else {
	      if (URL.indexOf("/") != -1) {
		//If the URL ends with a "/"
		if (URL.lastIndexOf("/") == (URL.length()-1)) {
		  _parameterString.append("?idega_session_id=");
		  _parameterString.append(iwc.getSession().getId());
		  return(_parameterString.toString());
		}else {
		  //There is a dot after the last "/" interpreted as a file not a directory
		  if (URL.lastIndexOf(".") > URL.lastIndexOf("/")) {
		    _parameterString.append("?idega_session_id=");
		    _parameterString.append(iwc.getSession().getId());
		    return(_parameterString.toString());
		  }
		  else {
		    _parameterString.append("/?idega_session_id=");
		    _parameterString.append(iwc.getSession().getId());
		    return(_parameterString.toString());
		  }
		}
	      } else {
		_parameterString.append("?idega_session_id=");
		_parameterString.append(iwc.getSession().getId());
		return(_parameterString.toString());
	      }
	    }
	  } else {
	    /**
	     * @todo Temporary solution??? :// in link then no idega_session_id
	     */
	    return("");
	  }
	} else {
	  return("");
	}

      }else{
	/**
	* @todo Temporary solution??? :// in link then no idega_session_id
	*/
	if (URL.indexOf("?") == -1) {
	  if (_addSessionId && (!iwc.isSearchEngine())) {
	    if ( _parameterString.toString().indexOf("?") == -1) {
	      _parameterString.insert(0,'?');
	    }
	     _parameterString.append("&");
	    if (URL.indexOf("://") == -1) {
	      _parameterString.append("idega_session_id=");
	      _parameterString.append(iwc.getSession().getId());
	    }
	  }
	}
	else {
	  if (_addSessionId && (!iwc.isSearchEngine())) {
	    _parameterString.append("&");
	    if (URL.indexOf("://") == -1) {
	      _parameterString.append("idega_session_id=");
	      _parameterString.append(iwc.getSession().getId());
	    }
	  }
	}
	return(_parameterString.toString());
      }
    }
    if(_parameterString != null){
      return(_parameterString.toString());
    } else {
      return("");
    }
  }

  /**
   *
   */
  public void clearParameters() {
    _parameterString = null;
  }


  public String getWindowToOpenCallingScript(IWContext iwc){
    try {
      this._main(iwc);
    }
    catch (Exception ex) {

    }
    if (isOpeningInNewWindow()) {
    //if (_objectType==(OBJECT_TYPE_WINDOW)) {
      if (_windowClass == null) {
	return _myWindow.getCallingScriptString(iwc,_myWindow.getURL(iwc)+getParameterString(iwc,_myWindow.getURL(iwc)));
      }
      else {
	return Window.getCallingScriptString(_windowClass,getURL(iwc)+getParameterString(iwc,getURL(iwc)),true,iwc);
      }
    }
    return "";
  }

  protected void setFinalUrl(String url){
    setAttribute(HREF_ATTRIBUTE,url);
  }

  /**
   *
   */
  public void print(IWContext iwc) throws Exception {
    initVariables(iwc);
    boolean addParameters = true;
    String oldURL = getURL(iwc);
    /**
     * @todo: Is this the right solution? - If the user is not logged on then do not add a session id to the link
     */
    if(!com.idega.block.login.business.LoginBusiness.isLoggedOn(iwc)){
      setSessionId(false);
    }

    if (oldURL == null) {
      oldURL = iwc.getRequestURI();
      setFinalUrl(oldURL);
    } else if (oldURL.equals(com.idega.util.StringHandler.EMPTY_STRING)) {
      oldURL = iwc.getRequestURI();
      setFinalUrl(oldURL);
    }

    if (oldURL.equals(HASH)) {
      addParameters = false;
    } else if(oldURL.startsWith(JAVASCRIPT)) {
      addParameters = false;
    }

    if (getLanguage().equals("HTML")) {
      boolean openInNewWindow=isOpeningInNewWindow();
      boolean alignSet = isAttributeSet(HORIZONTAL_ALIGNMENT);

      if(alignSet){
        print("<div align=\""+getHorizontalAlignment()+"\">");
        removeAttribute(HORIZONTAL_ALIGNMENT);//does this slow things down?
      }

      if(openInNewWindow){
      //if (_objectType==(OBJECT_TYPE_WINDOW)) {
       // openInNewWindow=true;
	/*if (_windowClass == null) {
	  setFinalUrl("javascript:"+_myWindow.getCallingScriptString(iwc,_myWindow.getURL(iwc)+getParameterString(iwc,_myWindow.getURL(iwc))));
	} else {
	  setFinalUrl("javascript:"+Window.getCallingScriptString(_windowClass,getURL(iwc)+getParameterString(iwc,getURL(iwc)),true));
	}*/
        setFinalUrl(this.getWindowOpenerJavascriptString(iwc));
	//setFinalUrl(HASH);
      }
      else{
	//Should not happen when a new window is opened
	if (addParameters) {
	  setFinalUrl(oldURL+getParameterString(iwc,oldURL));
	}
      }//end if (_objectType==(OBJECT_TYPE_WINDOW))

	print("<a "+getAttributeString()+" >");
	/*
	if(openInNewWindow){
	//if (_obj == null) {
	  if(_obj!=null){
	    _obj.print(iwc);
	  }
	  else{
	    Text myText = new Text(_myWindow.getName());
	    myText.print(iwc);
	  }
	} else {
	*/
	  //if (_objectType==OBJECT_TYPE_TEXT) {
	  if (this.isText()) {
	    if ( hasClass ) {
	      /*if ( displayString != null ) {
		print(displayString);
	      }
	      else {*/
		if(_obj!=null){
		  String text = ((Text)_obj).getLocalizedText(iwc);
		  if ( text != null ) {
		    print(text);
		  }
		}
	      /*}*/
	    } else {
	      if(_obj!=null){
		_obj.print(iwc);
	      }
	    }
	  }
	  else if (this.isImage()){
	    Image image = this.getTheCorrectDefaultImage(iwc);
	    if(image!=null){
	      image.print(iwc);
	    }
	  }
	  else{
	    if(_obj !=null){
	      _obj.print(iwc);
	    }
	  }
	/*}*/
	print("</a>");

        if(alignSet){
          print("</div>");
        }
      /*} else {
	if (addParameters) {
	  setFinalUrl(oldURL+getParameterString(iwc,oldURL));
	}
	print("<a "+getAttributeString()+" >");
	//if (_objectType==OBJECT_TYPE_TEXT) {
	if (isText()) {
	  if ( hasClass ) {
	    if ( displayString != null ) {
	      print(displayString);
	    } else {
	      if ( ((Text)_obj).getText() != null ) {
		print(((Text)_obj).getText());
	      }
	    }
	  } else {
	  _obj.print(iwc);
	  }
	}
	else if (this.isImage()){
	  Image image = this.getTheCorrectDefaultImage(iwc);
	  image.print(iwc);
	} else {
	  _obj.print(iwc);
	}
	print("</a>");
      }*/
    } else if (getLanguage().equals("WML")) {
      if(_myWindow!=null){
      //if (_objectType.equals(OBJECT_TYPE_WINDOW)) {
	setFinalUrl(_myWindow.getURL(iwc)+getParameterString(iwc,oldURL));  // ????????????
	setFinalUrl(HASH);
	print("<a "+getAttributeString()+" >");
	print(_myWindow.getName());
	print("</a>");
      } else {
	if (addParameters) {
	  setFinalUrl(oldURL+getParameterString(iwc,oldURL));
	}
	print("<a "+getAttributeString()+" >");
	_obj.print(iwc);
	print("</a>");
      }
    }
    /**
    * @todo !!Find out why this is necessary:
    */
    //setURL(oldURL);
    setFinalUrl(oldURL);
  }

  /**
   *
   */
  public void addIWLinkListener(IWLinkListener l, IWContext iwc) {
    if (!listenerAdded()) {
      postIWLinkEvent(iwc);
    }
    super.addIWLinkListener(l,iwc);
  }


  /*
   *
   */
  private void postIWLinkEvent(IWContext iwc) {
    eventLocationString = getID();
    IWLinkEvent event = new IWLinkEvent(this,IWLinkEvent.LINK_ACTION_PERFORMED);
    if (_formToSubmit == null) {
      addParameter(sessionEventStorageName,eventLocationString);
    }
    iwc.setSessionAttribute(eventLocationString,event);
    listenerAdded(true);
  }

  /**
   *
   */
  public void setToFormSubmit(Form form) {
    setToFormSubmit(form,false);
  }

  /**
   *
   */
  public void setToFormSubmit(Form form, boolean useEvent) {
    _formToSubmit = form;
    setFinalUrl(HASH);
    if ((getIWLinkListeners() != null && getIWLinkListeners().length != 0) || useEvent) {
       //setOnClick("document."+form.getID()+"."+IWMainApplication.IWEventSessionAddressParameter+".value=this.id ;document."+form.getID()+".submit()");
      setOnClick("document.forms['"+form.getID()+"']."+IWMainApplication.IWEventSessionAddressParameter+".value='"+this.getID()+"';document.forms['"+form.getID()+"'].submit();");
    }
    else {
      setOnClick("document.forms['"+form.getID()+"'].submit()");
    }
  }

  /**
   *
   */
  public void setAsBackLink(int backUpHowManyPages) {
    setOnClick("history.go(-"+backUpHowManyPages+")");
    setFinalUrl(HASH);
  }

  /**
   *
   */
  public void setAsBackLink() {
    setAsBackLink(1);
  }

  public void setNoURL(){
    setFinalUrl(HASH);
  }

  /**
   *
   */
  public void setProperty(String key, String values[]) {
    if (key.equalsIgnoreCase("text")) {
      setText(values[0]);
    }
    else if (key.equalsIgnoreCase("url")) {
      setURL(values[0]);
    }
  }

  /**
   *
   */
  public void setEventListener(Class eventListenerClass) {
    setEventListener(eventListenerClass.getName());
  }

  /**
   *
   */
  public void setEventListener(String eventListenerClassName) {
    addParameter(IWMainApplication.IdegaEventListenerClassParameter,IWMainApplication.getEncryptedClassName(eventListenerClassName));
  }

  /**
   *
   */
  public void sendToControllerFrame() {
    setTarget(IWConstants.IW_CONTROLLER_FRAME_NAME);
  }

  /**
   *
   */
  public void setWindowToOpen(Class windowClass) {
    //_objectType=OBJECT_TYPE_WINDOW;
    _windowClass=windowClass;

    /**
     * @todo Temporary workaround - Find out why this is needed
     */
     this.setURIToWindowOpenerClass(IWContext.getInstance());

    //setURL(IWMainApplication.windowOpenerURL);
    //addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass);
  }

  public void setWindowToOpen(Class windowClass, int instanceId) {
    //_objectType=OBJECT_TYPE_WINDOW;
    setWindowToOpen(windowClass);
    //setURL(IWMainApplication.windowOpenerURL);
    //addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass);
    //this.addParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID,instanceId);
    icObjectInstanceIDForWindow=instanceId;
  }

  public void setNoTextObject(boolean noText) {
    if (isText())
      hasClass = noText;
  }

  private boolean isOpeningInNewWindow(){
    if(_myWindow!=null  || this._windowClass!=null || this.windowOpenerJavascriptString != null){
      return true;
    }
    return false;
  }

  private boolean isText(){
    if(this._objectType==this.OBJECT_TYPE_TEXT){
      if(_obj!=null){
	if(_obj instanceof Text){
	  return true;
	}
      }
      else{
	  //return (this._objectType==this.OBJECT_TYPE_TEXT);
	  return true;
      }
    }
    return false;
  }

  public void setAsImageButton(boolean isImageButton){
   this.isImageButton = isImageButton;
   this.isImageTab = false; //can't have both
  }

  public void setAsLocalizedImageButton( boolean useTextAsLocalizedTextKey ){
    this.useTextAsLocalizedTextKey = useTextAsLocalizedTextKey;
  }

  public void setAsImageButton(boolean isImageButton, boolean useTextAsLocalizedTextKey ){
    setAsImageButton(isImageButton);
    setAsLocalizedImageButton(useTextAsLocalizedTextKey);
  }

  public void setAsImageTab(boolean isImageTab, boolean flip){
   this.isImageTab = isImageTab;
   this.flip = flip;
   this.isImageButton = false; //can't have both

  }

  public void setAsLocalizedImageTab( boolean useTextAsLocalizedTextKey ){
    this.useTextAsLocalizedTextKey = useTextAsLocalizedTextKey;
  }

  public void setAsImageTab(boolean isImageTab, boolean useTextAsLocalizedTextKey, boolean flip){
    setAsImageTab(isImageTab,flip);
    setAsLocalizedImageTab(useTextAsLocalizedTextKey);
  }


  public void addIWPOListener(PresentationObject obj){
    if(listenerInstances == null){
      listenerInstances = new Vector();
    }
    if(!listenerInstances.contains(obj)){
      listenerInstances.add(obj);
    }
  }

  public void addIWPOListener(String page_instID){
    if(listenerInstances == null){
      listenerInstances = new Vector();
    }
    if(page_instID != null){
      StringTokenizer token = new StringTokenizer(page_instID,",",false);
      while(token.hasMoreTokens()){
	String pointer = token.nextToken();
	if(!listenerInstances.contains(pointer)){
	  listenerInstances.add(pointer);
	}
      }
    }
  }

  public List getIWPOListeners(){
    return listenerInstances;
  }

  public void setHttps(boolean useHttps) {
    this.https = useHttps;
  }

  public void setOnMouseOverImage(Image image, Image mouseOverImage) {
    image.setOverImage(mouseOverImage);

    setAttribute("onMouseOver","swapImage('"+image.getName()+"','','"+mouseOverImage.getMediaURL()+"',1)");
    setAttribute("onMouseOut","swapImgRestore()");
  }



  public void setDPTTemplateId(int id){
    dptTemplateId = id;
  }

  public int getDPTTemplateId(){
    return dptTemplateId;
  }

  public void setDPTTemplateId(IBPage page){
    dptTemplateId = page.getID();
  }

  public void removeParameter(String prmName){
    if(_parameterString != null){
      if (!(prmName != null && !prmName.equals(""))){
	return;
      }

      StringBuffer newBuffer = new StringBuffer();
      String prmString = _parameterString.toString();

      if (prmString.length() > 0) {
	if ((prmString.charAt(0) == '?') && (prmString.length() > 1)) {
	  prmString = prmString.substring(1,prmString.length());
	  newBuffer.append("?");
	}
	if ((prmString.charAt(0) == '&') && (prmString.length() > 1)) {
	  prmString = prmString.substring(1,prmString.length());
	  newBuffer.append("&");
	}
	StringTokenizer token = new StringTokenizer(prmString,"&",false);
	boolean firstToken = true;
	while (token.hasMoreTokens()) {
	  String st = token.nextToken();
	  StringTokenizer token2 = new StringTokenizer(st,"=",false);
	  if (token2.hasMoreTokens()) {
	    String name = token2.nextToken();
	    if (!name.equals(prmName)) {
	      if(!firstToken){
		newBuffer.append("&");
	      }
	      newBuffer.append(name);
              if(token2.hasMoreTokens()){
                String value = token2.nextToken();
                newBuffer.append("=");
                newBuffer.append(value);
              }
	    }
	  }
	  /*else {
	    newBuffer.append("&" + st);
	  }*/
	  firstToken = false;
	}
      }
      _parameterString = newBuffer;
      return;
    }
  }

  public void setWindowToOpenScript(String scriptString){
    this.windowOpenerJavascriptString=scriptString;
  }

  private String getWindowOpenerJavascriptString(IWContext iwc){
    if(windowOpenerJavascriptString==null){
	if (_windowClass == null) {
	   return ("javascript:"+_myWindow.getCallingScriptString(iwc,_myWindow.getURL(iwc)+getParameterString(iwc,_myWindow.getURL(iwc))));
	} else {
	  return ("javascript:"+Window.getCallingScriptString(_windowClass,getURL(iwc)+getParameterString(iwc,getURL(iwc)),true,iwc));
	}
    }
    else{

      windowOpenerJavascriptString = URLDecoder.decode(windowOpenerJavascriptString);
      List between = TextSoap.FindAllBetween(windowOpenerJavascriptString, "openwindow('", "'");
      String theUrl = "";
      if (between != null && between.size() > 0) {
        theUrl = (String) between.get(0);
      }

      String paramString = this.getParameterString(iwc, this.getURL(iwc));
      if (theUrl.indexOf("?") != -1) {
        paramString = TextSoap.findAndReplace(paramString, "?","&");
      }

      if (!paramString.equals("") || !theUrl.equals(""))
      windowOpenerJavascriptString = TextSoap.findAndInsertAfter(windowOpenerJavascriptString, theUrl, paramString);

      return "javascript:"+windowOpenerJavascriptString;
    }

  }


  public void setClassToInstanciate(Class presentationObjectClass){
    this.classToInstanciate=presentationObjectClass;
  }

  public void setClassToInstanciate(Class presentationObjectClass,Class pageTemplateClass){
    setClassToInstanciate(presentationObjectClass);
    this.templatePageClass=pageTemplateClass;
  }

  public void setClassToInstanciate(Class presentationObjectClass,String template){
    setClassToInstanciate(presentationObjectClass);
    this.templateForObjectInstanciation=template;
  }

  private void setURIToClassToInstanciate(IWContext iwc){
    if(this.classToInstanciate!=null){
      if(this.templatePageClass!=null){
        this.setURL(iwc.getApplication().getObjectInstanciatorURI(classToInstanciate,templatePageClass));
      }
      else if(this.templateForObjectInstanciation!=null){
        this.setURL(iwc.getApplication().getObjectInstanciatorURI(classToInstanciate,templateForObjectInstanciation));
      }
      else{
        this.setURL(iwc.getApplication().getObjectInstanciatorURI(classToInstanciate));
      }
    }
  }

  private void setURIToWindowOpenerClass(IWContext iwc){
    if(this._windowClass!=null){

      //setURL(iwc.getApplication().getWindowOpenerURI());
      //addParameter(Page.IW_FRAME_CLASS_PARAMETER,_windowClass);
      if(this.icObjectInstanceIDForWindow==-1){
          setURL(iwc.getApplication().getWindowOpenerURI(_windowClass));
      }
      else{
        setURL(iwc.getApplication().getWindowOpenerURI(_windowClass,icObjectInstanceIDForWindow));
        //this.addParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID,icObjectInstanceIDForWindow);
      }
    }
  }
}