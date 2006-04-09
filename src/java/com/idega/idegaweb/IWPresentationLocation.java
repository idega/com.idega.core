package com.idega.idegaweb;
import java.util.StringTokenizer;
import com.idega.repository.data.RefactorClassRegistry;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWPresentationLocation implements IWLocation {

  private final static String SEPARATOR = "|";

  private static final String DEFAULT_TARGET = "_top";
  protected String _target = DEFAULT_TARGET;
  protected boolean _topPage = false;
  protected boolean _inFrameset = false;
  protected boolean _inPopUpWindow = false;
  protected String _applicationClass = null;
  protected int _id = 0;
  protected int _subID = 0;

  public IWPresentationLocation() {
  }

  public IWPresentationLocation(String target, boolean inFrameSet, Class applicationClass, boolean topPage, boolean inPopUpWindow){
    this._target = target;
    this._topPage = topPage;
    if(applicationClass != null){
      this._applicationClass = applicationClass.getName();
    }
    this._inFrameset = inFrameSet;
    this._inPopUpWindow = inPopUpWindow;
  }

  public String toString(){
    String toReturn = super.toString() + " -> " + this.getLocationString();
    return toReturn;
  }



  public void setTarget(String target) {
    this._target = target;
//    try {
//      throw new Exception("IWPresentationLocation._target: "+target);
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
  }
  public void isInFrameSet(boolean inFrameset) {
    this._inFrameset = inFrameset;
  }

  public void setApplicationClass(Class applicationClass){
    if(applicationClass != null){
      this._applicationClass = applicationClass.getName();
    }
  }

  public void isTopPage(boolean topPage) {
    this._topPage = topPage;
  }
  public void isInPopUpWindow(boolean inPopUpWindow) {
    this._inPopUpWindow = inPopUpWindow;
  }

  public int getICObjectInstanceID(){
    return this._id;
  }



  public void setICObjectInstanceID(int id){
    this._id = id;
  }

  public int getSubID(){
    return this._subID;
  }

  public void setSubID(int id){
    this._subID = id;
  }



  public String getTarget() {
    return this._target;
  }
  public boolean isInFrameSet() {
    return this._inFrameset;
  }

  public String getApplicationClassName(){
    return this._applicationClass;
  }

  public Class getApplicationClass(){
    try {
      if(this._applicationClass != null){
        return RefactorClassRegistry.forName(this._applicationClass);
      } else {
        return null;
      }
    }
    catch (ClassNotFoundException ex) {
      System.err.println(ex.getMessage());
      return null;
    }

  }

  public boolean isTopPage() {
    return this._topPage;
  }
  public boolean isInPopUpWindow() {
    return this._inPopUpWindow;
  }

  public boolean equals(Object obj){
//    System.out.println("IWPresentationLocation: "+this+".equals("+obj+")");
    if(obj instanceof IWLocation){
//      boolean r = this.equals((IWLocation)obj);
//      System.out.println("type = IWLocation");
//      System.out.println("equals = "+r);
//      return r;
      return this.equals((IWLocation)obj);
    } else if(obj instanceof String){
//      boolean r = this.equals((String)obj);
//      System.out.println("type = String");
//      System.out.println("equals = "+r);
//      return r;
      return this.equals((String)obj);
    } else {
//      boolean r = super.equals(obj);
//      System.out.println("type = none");
//      System.out.println("equals = "+r);
//      return r;
      return super.equals(obj);
    }
  }

  public int hashCode(){
    return this.getLocationString().hashCode();
  }

  public boolean equals(IWLocation location){
//      System.out.println("type = IWLocation");
//      System.out.println("equals = "+r);
//      return r;
    return this.getLocationString().equals(location.getLocationString());
//    return ( this.getTarget().equals(location.getTarget()) && this.isInFrameSet() == location.isInFrameSet() && this.getApplicationClassName() == location.getApplicationClassName() && this.isInPopUpWindow() == location.isInPopUpWindow() && this.isTopPage() == location.isTopPage());
  }

  public boolean equals(String location){
//     boolean r = this.getLocationString().equals(location);
//      System.out.println("type = IWLocation");
//      System.out.println("equals = "+r);
//      return r;
    return this.getLocationString().equals(location);

//     try {
//      StringTokenizer tokenizer = new StringTokenizer(location,SEPARATOR);
//      String applicationClassName = tokenizer.nextToken();
//      String target = tokenizer.nextToken();
//      String topPage = tokenizer.nextToken();
//      String inFrameset = tokenizer.nextToken();
//      String inPopUpWindow = tokenizer.nextToken();
//
//      if(!(target.equals(_target) || (target.equals("null") && _target==null))){
//        return false;
//      }
//
//      if(!((applicationClassName.equals("null") && applicationClassName==null) || ( applicationClassName != null || applicationClassName.equals(IWMainApplication.getEncryptedClassName(applicationClassName))) )){
//        return false;
//      }
//
//      if( _topPage != ((topPage.equals("1"))?true:false)){
//        return false;
//      }
//
//      if( _inFrameset != ((inFrameset.equals("1"))?true:false)){
//        return false;
//      }
//
//      if( _inPopUpWindow != ((inPopUpWindow.equals("1"))?true:false)){
//        return false;
//      }
//
//      return true;
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//      return false;
//    }
  }

  public Object clone(){
    IWPresentationLocation obj = null;
    try {
      obj = (IWPresentationLocation)super.clone();
      obj._target = this._target;
      obj._topPage = this._topPage;
      obj._inFrameset = this._inFrameset;
      obj._inPopUpWindow = this._inPopUpWindow;
      obj._applicationClass = this._applicationClass;
      obj._id = this._id;
      obj._subID = this._subID;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public String getLocationString(){
    //String toReturn = IWMainApplication.getEncryptedClassName(this._applicationClass) + SEPARATOR + _target + SEPARATOR + ((_topPage)?1:0) + SEPARATOR + ((_inFrameset)?1:0) + SEPARATOR + ((_inPopUpWindow)?1:0);

    StringBuffer buffer = new StringBuffer();
    if(this._applicationClass != null){
      buffer.append(IWMainApplication.getEncryptedClassName(this._applicationClass));
    } else {
      buffer.append("null");
    }
    buffer.append(SEPARATOR);
    buffer.append(this._target);
    buffer.append(SEPARATOR);
    buffer.append(((this._topPage)?1:0));
    buffer.append(SEPARATOR);
    buffer.append(((this._inFrameset)?1:0));
    buffer.append(SEPARATOR);
    buffer.append(((this._inPopUpWindow)?1:0));
    buffer.append(SEPARATOR);
    buffer.append(this._subID);

//    try {
//      throw new Exception(buffer.toString());
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }


    return buffer.toString();
  }

  public static IWLocation getLocationObject(String str){


    try {
//      System.err.println("IWPresentationLocation.getLocationObject("+str+")");
      if(str != null){
        StringTokenizer tokenizer = new StringTokenizer(str,SEPARATOR);
        String applicationClassName = tokenizer.nextToken();
        String target = tokenizer.nextToken();
        String topPage = tokenizer.nextToken();
        String inFrameset = tokenizer.nextToken();
        String inPopUpWindow = tokenizer.nextToken();
        String subID = tokenizer.nextToken();

        if(target.equals("null")){
          target = DEFAULT_TARGET;
        }

        IWPresentationLocation location = new IWPresentationLocation(target,((inFrameset.equals("1"))?true:false),null,((topPage.equals("1"))?true:false),((inPopUpWindow.equals("1"))?true:false));
        if(!applicationClassName.equals("null")){
          location._applicationClass = IWMainApplication.decryptClassName(applicationClassName);
        }
        location._subID = Integer.parseInt(subID);

//        System.err.println("IWPresentationLocation.location: "+location.getLocationString());
        return location;
      } else {
        return null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }



  }


}