/*
 * $Id: PresentationObject.java,v 1.7 2001/10/22 14:22:00 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import java.sql.*;
import com.idega.util.database.*;
import com.idega.core.data.*;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import javax.swing.event.EventListenerList;
import com.idega.event.IWEvent;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWLinkListener;
import com.idega.event.IWSubmitListener;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.data.EntityFinder;
import com.idega.exception.ICObjectNotInstalledException;
import com.idega.presentation.ui.Form;


/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.2
 */
public class PresentationObject extends Object implements Cloneable {
  //private final static String IW_BUNDLE_IDENTIFIER="com.idega.idegaweb";
  public final static String IW_BUNDLE_IDENTIFIER="com.idega.core";

  protected static final String slash = "/";

  private HttpServletRequest Request;
  private HttpServletResponse Response;
  private PrintWriter out;
  private String interfaceStyle;
  private String language;
  public Hashtable attributes;
  private String name;
  protected PresentationObject parentObject;
  private boolean doPrint = true;
  private String errorMessage;
  protected boolean hasBeenAdded = false;
  protected String treeID;
  private boolean goneThroughMain = false;
  private int ic_object_instance_id;
  private int ic_object_id;
  private static String emptyString = "";
  public static String sessionEventStorageName = IWMainApplication.IWEventSessionAddressParameter;
  public EventListenerList listenerList = null;
  private Hashtable eventAttributes = null;
  private static long InstnceUniqueID;
  private String UniqueInstnceName;
  private boolean listenerAdded = false;
  public String eventLocationString = "";
  private IWContext eventIWContext = null;
  public static final PresentationObject NULL_CLONE_OBJECT = new PresentationObject();

  private boolean _useBuilderObjectControl = true;
  private boolean _belongsToParent = false;

  /**
   * Default constructor
   */
  public PresentationObject() {
  }

  /**
   * @return The parent (subclass of PresentationObjectContainer) of the current object
   */
  public PresentationObject getParentObject(){
    return parentObject;
  }

  public void setID() {
    int hashCode = hashCode();
    if (hashCode < 0) {
      hashCode = -hashCode;
    }
    setID("id"+hashCode);
  }

  public String getID() {
    String theReturn = getAttribute("id");
    if (theReturn == null || this.emptyString.equals(theReturn)) {
      setID();
      theReturn = getAttribute("id");
    }

    return theReturn;
  }

  public PresentationObject getRootParent() {
    PresentationObject tempobj = getParentObject();
    if (tempobj == null) {
      return null;
    }
    else {
      while (tempobj.getParentObject() != null ) {
        tempobj = tempobj.getParentObject();
      }
      return tempobj;
    }
  }

  public void setParentObject(PresentationObject modobj) {
    parentObject = modobj;
  }

  /**
   * Initializes variables contained in the IWContext object
   */
  public void initVariables(IWContext iwc) throws IOException {
    this.Request = iwc.getRequest();
    this.Response = iwc.getResponse();
    this.language = iwc.getLanguage();
    this.interfaceStyle = iwc.getInterfaceStyle();
    if (language == null) {
      language = "HTML";
    }
    if (interfaceStyle == null) {
      interfaceStyle = "default";
    };
    this.out = iwc.getWriter();
  }

  public void setDoPrint(boolean ifDoPrint) {
    this.doPrint = ifDoPrint;
  }

  public boolean doPrint(IWContext iwc) {
    if (this.doPrint) {
      PresentationObject parent = getParentObject();
      if (parent == null) {
        return this.doPrint;
      }
      else {
        return parent.doPrint(iwc);
      }
    }
    else {
      return false;
    }
  }

  protected void setAttribute(Hashtable attributes) {
    this.attributes = attributes;
  }

  public void setAttribute(String attributeName,String attributeValue) {
    if (this.attributes == null) {
      this.attributes = new Hashtable();
    }
    this.attributes.put((Object) attributeName,(Object) attributeValue);
  }

  public void setAttribute(String attributeName){
    setAttribute(attributeName,slash);
  }

  /** Copies all of the attribute mappings from the specified map to attributes.
   *  These mappings will replace attibutes that this map had for any of the
   *  keys currently in the specified map.
   */
  public void setAttributes(Map attributeMap){
    if (this.attributes == null) {
      this.attributes = new Hashtable();
    }
    attributes.putAll(attributeMap);
  }

  /**
   *
   */
  public static Map getAttributeMap(String attributeString){
    Hashtable map = new Hashtable();
    if(attributeString !=  null && attributeString.length() > 1){
      StringTokenizer tokens = new StringTokenizer(attributeString),tok;
      while(tokens.hasMoreTokens()){
        String s = tokens.nextToken();//.replace('"',' ');
        tok = new StringTokenizer(s,"=\"");
        if(tok.countTokens() == 2){
          map.put(tok.nextToken(),tok.nextToken());
        }
      }
    }
    return map;
  }

  public String getAttribute(String attributeName) {
    if (this.attributes != null){
      return (String)this.attributes.get((Object)attributeName);
    }
    else {
      return null;
    }
  }

  public static String getAttribute(String attributeName,Map map){
     if (map != null){
      return (String)map.get((Object)attributeName);
    }
    else {
      return null;
    }
  }

  public boolean isAttributeSet(String attributeName) {
    if (getAttribute(attributeName) == null) {
      return false;
    }
    else {
      return true;
    }
  }

  public Hashtable getAttributes() {
    return this.attributes;
  }

  public static String getAttributeString(Map map){
    StringBuffer returnString = new StringBuffer();
    String Attribute ="";
    String attributeValue = "";
    Map.Entry mapEntry;

    if (map != null) {
      Iterator i = map.entrySet().iterator();
      while (i.hasNext()) {
        mapEntry = (Map.Entry) i.next();
        Attribute = (String) mapEntry.getKey();
        returnString.append(" ");
        returnString.append(Attribute);
        attributeValue = (String) mapEntry.getValue();
        if(!attributeValue.equals(slash)){
          returnString.append("=\"");
          returnString.append(attributeValue);
          returnString.append("\"");
        }
        returnString.append("");
      }
    }
    return returnString.toString();
  }

  public String getAttributeString() {
/*
    StringBuffer returnString = new StringBuffer();
    String Attribute ="";

    if (this.attributes != null) {
      Enumeration e = attributes.keys();
      while (e.hasMoreElements()) {
        Attribute = (String)e.nextElement();
        returnString.append(" ");
        returnString.append(Attribute);
        String attributeValue=getAttribute(Attribute);
        if(!attributeValue.equals(slash)){
          returnString.append("=\"");
          returnString.append(attributeValue);
          returnString.append("\"");
        }
        returnString.append("");
      }
    }

    return returnString.toString();
*/
    return getAttributeString(this.attributes);
  }

  /**
   * Gets the name of this object
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of this object
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Flushes the buffer in the printwriter out
   */
  public void flush() {
    out.flush();
  }

  /**
   * Uses the default PrintWriter object to print out a string
   */
  public void print(String string) {
    out.print(string);
  }

  /**
   * Uses the default PrintWriter object to print out a string with the endline character
   */
  public void println(String string) {
    out.println(string);
  }

  public void _print(IWContext iwc) throws Exception {
    this.print(iwc);
  }

  /**
   * The default implementation for the print function
   */
  public void print(IWContext iwc) throws Exception {
    initVariables(iwc);
    if (iwc.getLanguage().equals("WML")) {
      getResponse().setContentType("text/vnd.wap.wml");
    }
  }

  /**
   * @deprecated Do not use this function, it is not safe
   * @resturn The Response object for the page
   */
  public HttpServletRequest getRequest() {
    return this.Request;
  }

  /**
   * @deprecated Do not use this function, it is not safe
   * @return The Request object for the page
   */
  public HttpServletResponse getResponse() {
    return this.Response;
  }

  /**
   * @return The "layout" language used and supplied by the IWContext
   */
  public String getLanguage() {
    return this.language;
  }

  public void setID(String ID) {
    setAttribute("id",ID);
  }

  public void setID(int ID) {
    setAttribute("id",Integer.toString(ID));
  }

  /**
   * @return The interface style supplied by the IWContext (optional)
   */
  public String getInterfaceStyle() {
    return this.interfaceStyle;
  }

  public PrintWriter getPrintWriter() {
    return out;
  }

  /**
   * @return The default DatabaseConnection
   */
  public Connection getConnection() {
    return ConnectionBroker.getConnection();
  }

  public void freeConnection(Connection conn) {
    ConnectionBroker.freeConnection(conn);
  }

  /**
   * @return The Class name of the Object
   */
  public String getClassName() {
    return this.getClass().getName();
  }

  /**
   * Encodes a string to call special request such as pop-up windows in HTML
   */
  public String encodeSpecialRequestString(String RequestType,String RequestName, IWContext iwc) {
    String theOutput = "";
    theOutput = iwc.getRequestURI();
    theOutput = theOutput + "?idegaspecialrequesttype=" + RequestType + "&idegaspecialrequestname=" + RequestName;
    return theOutput;
  }

  /**
   * Sets the associated (attached) script object to this object
   */
  public void setAssociatedScript(Script myScript) {
    if (getRootParent() != null) {
      getRootParent().setAssociatedScript(myScript);
    }
  }

  /**
   * @return The associated (attached) script or null if there is no Script associated
   */
  public Script getAssociatedScript() {
    if (getRootParent() != null) {
      return getRootParent().getAssociatedScript();
    }
    else {
      return null;
    }
  }

  /**
   * @return The enclosing Page object
   */
  public Page getParentPage() {
    Page returnPage = null;
    PresentationObject obj = getParentObject();
    while (obj != null) {
      if (obj instanceof Page) {
        returnPage = (Page)obj;
      }
      obj = obj.getParentObject();
    }

    if((returnPage == null) && (this instanceof Page)){
      return (Page)this;
    }

    return returnPage;
  }

  /**
   * @return The enclosing Form object
   */
  public Form getParentForm() {
    Form returnForm = null;
    PresentationObject obj = getParentObject();
    while (obj != null) {
      if (obj instanceof Form) {
        returnForm = (Form)obj;
      }
      obj = obj.getParentObject();
    }
    return returnForm;
  }

  public void main(IWContext iwc) throws Exception {
  }

  protected void prepareClone(PresentationObject newObjToCreate) {
  }

  public synchronized Object _clone(IWContext iwc, boolean askForPermission){
    if(askForPermission){
      if(iwc.hasViewPermission(this)){
        return this.clone(iwc,askForPermission);
      } else {
        return NULL_CLONE_OBJECT;
      }
    } else {
      return this.clone();
    }
  }

  public synchronized Object clone(IWContext iwc) {
    return this._clone(iwc,true);
  }

  public synchronized Object clone() {
    return this.clone(null, false);
  }

  /*
  public synchronized Object clone() {
    PresentationObject obj = null;
    try {
      //This is forbidden in clone i.e. "new":
      //obj = (PresentationObject)Class.forName(this.getClassName()).newInstance();
      obj = (PresentationObject)super.clone();
      if (this.attributes != null) {
        obj.setAttribute((Hashtable)this.attributes.clone());
      }
      obj.setName(this.name);
      //obj.setParentObject(this.parentObject);
      this.prepareClone(obj);
      Vector vector;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }
  */

  public synchronized Object clone(IWContext iwc, boolean askForPermission) {
    PresentationObject obj = null;
    try {
      //This is forbidden in clone i.e. "new":
      //obj = (PresentationObject)Class.forName(this.getClassName()).newInstance();
      obj = (PresentationObject)super.clone();
      if (this.attributes != null) {
        obj.setAttribute((Hashtable)this.attributes.clone());
      }
      obj.setName(this.name);
      //obj.setParentObject(this.parentObject);
      this.prepareClone(obj);
      Vector vector;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }
/*
  protected void initICObjectInstanceId(IWContext iwc){
    String sID = iwc.getParameter(_PARAMETER_IC_OBJECT_INSTANCE_ID);
    try {
      if(sID != null){
        System.err.println("sID: "+sID);
        this.setICObjectInstanceID(Integer.parseInt(sID));
        //this.ic_object_instance_id = Integer.parseInt(sID);
        System.err.println("Integer.parseInt(sID): "+Integer.parseInt(sID));
        System.err.println("getICObjectInstanceID: "+this.getICObjectInstanceID());
      }else{
        System.err.println("sID == null");
      }
    }
    catch (NumberFormatException ex) {
      System.err.println(this+": cannot init ic_object_instance_id");
    }

  }
*/
  /**
   * Function invoked before the print function
   */
  public void _main(IWContext iwc) throws Exception {
    /*if(this.ic_object_instance_id == 0){
      initICObjectInstanceId(iwc);
    }*/
    if (!goneThroughMain) {
      initVariables(iwc);
      main(iwc);
    }
    goneThroughMain = true;
  }


  protected void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  protected String getErrorMessage() {
    return this.errorMessage;
  }

  public void setAsPrinted(boolean printed) {
    doPrint = printed;
  }

  /*public void setTreeID(String treeID) {
    this.treeID = treeID;
  }

  public String getTreeID() {
    return treeID;
  }*/


  public void setICObjectInstanceID(int id) {
    this.ic_object_instance_id = id;
  }


  public void setICObjectInstance(ICObjectInstance instance) {
    this.ic_object_instance_id = instance.getID();
  }

  public void setICObjectID(int id) {
    this.ic_object_id = id;
  }

  public void setICObject(ICObject obj) {
    this.ic_object_id = obj.getID();
  }

  /**
   * owerwrite in module
   */

  public int getICObjectInstanceID(IWContext iwc) throws SQLException {
    return getICObjectInstanceID();
  }

  public int getICObjectInstanceID(){
    return this.ic_object_instance_id;
  }

  public ICObjectInstance getICObjectInstance(IWContext iwc) throws SQLException {
    return getICObjectInstance();
  }

  public ICObjectInstance getICObjectInstance()throws SQLException{
    if(getICObjectInstanceID() != 0){
      return new ICObjectInstance(getICObjectInstanceID());
    } else {
      return null;
    }
  }

  public int getICObjectID(IWContext iwc) throws SQLException {
    return ic_object_id;
    /*ICObjectInstance inst = this.getICObjectInstance();

    System.out.println("Getting ICObjectInstance");
    if(inst != null){

      ICObject ob = inst.getObject();
      if(ob != null){
        return ob.getID();
      }
    }
    return 0;*/
  }

  public int getICObjectID(){
    return ic_object_id;
  }

  public ICObject getICObject() throws SQLException {
    return this.getICObject(this.getClass());
  }

  protected ICObject getICObject(Class c) throws SQLException {
    List result = EntityFinder.findAllByColumn(ICObject.getStaticInstance(ICObject.class),"class_name",c.getName());
    if(result != null && result.size() > 0){
      return (ICObject)result.get(0);
    }else{
      throw new ICObjectNotInstalledException(this.getClass().getName());
    }
  }


  public ICObjectInstance getICInstance(IWContext iwc) throws IWException {
    try {
      return new ICObjectInstance(getICObjectInstanceID(iwc));
    }
    catch (Exception excep) {
      IWException exep = new IWException(excep.getMessage());
      throw (IWException) excep.fillInStackTrace();
    }
  }

  public void addIWLinkListener(IWLinkListener l,IWContext iwc) {
    //System.err.println(this.getClass().getName() + " : listener added of type -> " + l.getClass().getName());
    /**
     * temp
     */
    getEventListenerList().remove(IWLinkListener.class,l);

    getEventListenerList().add(IWLinkListener.class,l);
  }

  public IWLinkListener[] getIWLinkListeners() {
    return (IWLinkListener[])getEventListenerList().getListeners(IWLinkListener.class);
  }

  public void addIWSubmitListener(IWSubmitListener l,IWContext iwc){
    getEventListenerList().add(IWSubmitListener.class,l);
  }

    public IWSubmitListener[] getIWSubmitListeners(){
      if (listenerList == null){
        listenerList = new EventListenerList();
      }
      return (IWSubmitListener[])listenerList.getListeners(IWSubmitListener.class);
    }


    public void setEventAttribute(String attributeName,Object attributeValue){
    if (this.eventAttributes == null){
      this.eventAttributes = new Hashtable();
    }
    this.eventAttributes.put((Object) attributeName,(Object) attributeValue);
    }

    public Object getEventAttribute(String attributeName){
      if (this.eventAttributes != null){
        return this.eventAttributes.get((Object) attributeName);
      }
      else{
        return null;
      }
    }



     public void dispatchEvent(IWEvent e) {
          processEvent(e);
    }


      protected void processEvent(IWEvent e) {

          if (e instanceof IWLinkEvent) {
              processIWLinkEvent((IWLinkEvent)e);

          } else if (e instanceof IWSubmitEvent) {
              processIWSubmitEvent((IWSubmitEvent)e);
          } else{
              System.err.println("unable to prosess event: " + e);
          }
      }

      protected void processIWLinkEvent(IWLinkEvent e) {
        PresentationObject obj = (PresentationObject)e.getSource();
        // Guaranteed to return a non-null array
        IWLinkListener[] listeners = obj.getIWLinkListeners();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-1; i>=0; i--) {
          ((IWLinkListener)listeners[i]).actionPerformed(e);
        }
      }

      protected void processIWSubmitEvent(IWSubmitEvent e) {
        PresentationObject obj = (PresentationObject)e.getSource();
        // Guaranteed to return a non-null array
        IWSubmitListener[] listeners = obj.getIWSubmitListeners();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-1; i>=0; i--) {
          ((IWSubmitListener)listeners[i]).actionPerformed(e);
        }
      }




    /**
     * unimplemented
     */
    public void fireEvent(){
    }

    public void endEvent(IWContext iwc){
      iwc.removeSessionAttribute(eventLocationString);
    }

    public void listenerAdded(boolean added){
      listenerAdded = added;
    }

    public boolean listenerAdded(){
      return listenerAdded;
    }

  public void setIWContext(IWContext iwc){
  //  System.err.println(this.getClass().getName() + ": iwc set");
    eventIWContext = iwc;
  }

  public EventListenerList getEventListenerList(){
    if (listenerList == null){
        listenerList = new EventListenerList();
    }
    return listenerList;
  }

  /**
   * @deprecated Do not use this function, it is not safe
   */
  public IWContext getEventIWContext(){
    return eventIWContext;
  }

  public void _setIWContext(IWContext iwc){
    setIWContext(iwc);
  }

  public void setProperty(String key, String values[]){
  }

  /**
   * Needs to be overrided to get the right IWBundle identifier for the object
   */
  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public IWBundle getBundle(IWContext iwc){
    IWMainApplication iwma = iwc.getApplication();
    return iwma.getBundle(getBundleIdentifier());
  }

  public IWResourceBundle getResourceBundle(IWContext iwc){
    IWBundle bundle = getBundle(iwc);
    if(bundle!=null){
      return bundle.getResourceBundle(iwc.getCurrentLocale());
    }
    return null;
  }

  public String getLocalizedString(String key,IWContext iwc){
    IWResourceBundle bundle = getResourceBundle(iwc);
    if(bundle!=null){
      return bundle.getLocalizedString(key);
    }
    return null;
  }

  public void setUseBuilderObjectControl(boolean use) {
    _useBuilderObjectControl = use;
  }

  public boolean getUseBuilderObjectControl() {
    return(_useBuilderObjectControl);
  }

  public void setBelongsToParent(boolean belongs) {
    _belongsToParent = belongs;
  }

  public boolean getBelongsToParent() {
    return(_belongsToParent);
  }
}

