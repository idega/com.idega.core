//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWStyleManager;
import com.idega.presentation.text.*;
import java.util.*;
import java.io.*;
//import com.idega.jmodule.login.business.*;
//import com.idega.block.login.business.LoginBusiness;
//import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.data.ICObjectInstance;
import com.idega.idegaweb.IWCacheManager;
import com.idega.block.IWBlock;
import com.idega.idegaweb.IWUserContext;

/**
 *
 * A base class for idegaWeb Blocks
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class Block extends PresentationObjectContainer implements IWBlock{

  private static Map permissionKeyMap = new Hashtable();
  private String cacheKey = null;
  private String derivedCacheKey = null;
  private boolean cacheable=false;
  private long cacheInterval;
  private int targetObjInst = -1;
  private int targetObjInstset = -2;

  private boolean editPermission = false;

  private boolean debugParameters = false;

  private static final String concatter = "_";
  private static final String newline = "\n";

  public final static String IW_BLOCK_CACHE_KEY="iw_not_cached";


  public static boolean usingNewAcessControlSystem=false;

  public Block(){

  }

  public String getBundleIdentifier(){
    return IW_CORE_BUNDLE_IDENTIFIER;
  }
  
  /**
   * Override to add styles (names) to stylesheet.  Add name (String) as key and style (String) as value.
   */
  public Map getStyleNames() {
  	//return IWConstants.getDefaultStyles();
  	return null;
  }

  public String getCacheKey(){
    return IW_BLOCK_CACHE_KEY;
  }

  public String getLocalizedNameKey(){
    return "block";
  }

  public String getLocalizedNameValue(){
    return "Block";
  }

  public String getLocalizedName(IWContext iwc){
    return getLocalizedString(getLocalizedNameKey(),getLocalizedNameValue(),iwc);
  }

  /////// target features ///////
  /////// target code begins ////////////

  public void setTargetObjectInstance(ICObjectInstance instance){
    if(instance !=null)
      this.targetObjInst = instance.getID();
  }

  public int getTargetObjectInstance(){
    if(targetObjInst >0)
      return targetObjInst;
    else
      return getICObjectInstanceID();
  }

  public void setAsObjectInstanceTarget(Link link){
    if(targetObjInst > 0)
      link.setTargetObjectInstance(getTargetObjectInstance());
  }

  public boolean isTarget(){
    return this.targetObjInstset==this.targetObjInst;
  }
  /////// target code ends ////////////

  ////// debugging parameters /////////

  public void setToDebugParameters(boolean debugPrm){
    this.debugParameters = debugPrm;
  }


  public boolean deleteBlock(int ICObjectInstanceId){
    System.err.println("method deleteBlock(int ICObjectInstanceId) not implemented in class "+this.getClass().getName());
    return true;
  }

  public boolean copyBlock(int newInstanceID){
    System.err.println("method copyBlock(int newInstanceID) not implemented in class "+this.getClass().getName());
    return true;
  }

  public boolean isAdministrator(IWContext iwc)throws Exception{
    if(usingNewAcessControlSystem){
      return iwc.hasEditPermission(this);
    }
    else{
      return false;
      //return AccessControl.isAdmin(iwc);
    }
  }

  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isDeveloper(IWContext iwc)throws Exception{
    return false;
  }

  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isUser(IWContext iwc)throws Exception{
    return false;
  }


  /**
   * <H2>Unimplemented</H2>
   */
  public boolean isMemberOf(IWContext iwc,String groupName)throws Exception{
    return false;
  }

  /**
   * <H2>Unimplemented</H2>
   */
  public boolean hasPermission(String permissionType, PresentationObject obj, IWContext iwc)throws Exception{
    return iwc.getAccessController().hasPermission(permissionType,obj,iwc);
  }

/* public boolean hasPermission(String permissionType,IWContext iwc)throws Exception{
    return hasPermission(permissionType,iwc,this);
  }*/


  public String getModuleName(){
    return this.getClass().getName();
  }

  /**
   * Implement in subclasses:
   */
  public void registerPermissionKeys(){
  }

  /*
  protected void registerPermissionKey(String permissionKey,String localizeableKey){
    Map m = getPermissionKeyClassMap();
    m.put(permissionKey,localizeableKey);
  }
*/
  protected void registerPermissionKey(String permissionKey){
    Map m = getPermissionKeyMap();
    if( m.containsKey(getClassName() ) ){
      List l = (List) m.get(getClassName());
      l.add(permissionKey);
    }
    else{
      List l = new ArrayList();
      l.add(permissionKey);
      m.put(getClassName(),l);
    }
  }

  private Map getPermissionKeyMap(){
    return permissionKeyMap;
  }
/*
  private Map getPermissionKeyClassMap(){
    Map m = (Map)getPermissionKeyMap().get(this.getClass());
    if(m==null){
      m = new Hashtable();
      getPermissionKeyMap().put(this.getClass(),m);
    }
    return m;
  }
*/
  public String[] getPermissionKeys(){
    return getPermissionKeys(getClass());
  }

  public String[] getPermissionKeys(Block obj){
    return getPermissionKeys(obj.getClass());
  }

  public String[] getPermissionKeys(Class PresentationObjectClass){
    List l = (List)getPermissionKeyMap().get(PresentationObjectClass.getName());
    if(l!=null){
      return (String[])l.toArray(new String[0]);
    }
    return null;
  }

  private static long twentyMinutes = 60*1000*20;

  public void setCacheable(String cacheKey){
    setCacheable(cacheKey,twentyMinutes);
  }
/**@ todo how do this.cacheKey and setCacheKey work together??**/
  public void setCacheable(String cacheKey,long millisecondsInterval){
    cacheable=true;
    this.cacheKey=cacheKey;
    this.cacheInterval=millisecondsInterval;
  }

  public void beginCacheing(IWContext iwc,StringBuffer buffer)throws Exception{
    PrintWriter servletWriter = iwc.getWriter();
    iwc.setCacheing(true);
    PrintWriter writer = new BlockCacheWriter(servletWriter,buffer);
    iwc.setCacheWriter(writer);
  }

  public void endCacheing(IWContext iwc,StringBuffer buffer){
    iwc.setCacheing(false);
    IWCacheManager.getInstance(iwc.getApplication()).setObject(getOriginalCacheKey(),getDerivedCacheKey(),buffer,cacheInterval);
  }

  public boolean hasEditPermission(){
    return editPermission;
  }

  public void _main(IWContext iwc)throws Exception{
    editPermission = iwc.hasEditPermission(this);

    if(debugParameters){
      debugParameters(iwc);
    }

    if(iwc.isParameterSet(TARGET_OBJ_INS))
      targetObjInstset = Integer.parseInt(iwc.getParameter(TARGET_OBJ_INS));

    if(targetObjInst <=0)
      targetObjInst = getParentObjectInstanceID();
      
    if(getStyleNames() != null){
    	Map styles = getStyleNames();
    	IWStyleManager manager = new IWStyleManager();
    	Iterator iter = styles.keySet().iterator();
    	while ( iter.hasNext() ) {
    		String style = (String) iter.next();
    		if ( !manager.isStyleSet(style) )
    			manager.setStyle(style, (String) styles.get(style));
    	}
    }

    if(this.isCacheable()){
      setCacheKey(iwc);
      if(isCacheValid(iwc)){
      }
      else{
	//beginCacheing(iwc);
	super._main(iwc);
	//endCacheing(iwc);
      }
    }
    else{
      super._main(iwc);
    }
  }

  /**
   * The default implementation for the print function for Blocks. This implementation is final and therefore can not be overrided.
   */
  public final void print(IWContext iwc)throws Exception{
    if(this.isCacheable()){
      if(isCacheValid(iwc)){
	StringBuffer buffer = (StringBuffer)IWCacheManager.getInstance(iwc.getApplication()).getObject(getDerivedCacheKey());
	iwc.getWriter().print(buffer.toString());
      }
      else{
	StringBuffer buffer = new StringBuffer();
	beginCacheing(iwc,buffer);
	super.print(iwc);
	endCacheing(iwc,buffer);
      }
    }
    else{
      super.print(iwc);
    }
  }


  private boolean isCacheValid(IWContext iwc){
    boolean valid = false;
    if( cacheable ){
      if(getDerivedCacheKey()!=null){
	valid = IWCacheManager.getInstance(iwc.getApplication()).isCacheValid(getDerivedCacheKey());
      }
    }

    return valid;
  }

  protected String getDerivedCacheKey(){
    return derivedCacheKey;
  }


  private String getOriginalCacheKey(){
    return cacheKey;
  }

  /** cache specifically for view right and for edit rights**/
  private void setCacheKey(IWContext iwc){
    derivedCacheKey= cacheKey + getCacheState(iwc,getCachePrefixString(iwc));
//    cacheKey += getCacheState(iwc,getCachePrefixString(iwc));
    /**@todo remove debug**/
    //debug("cachKey = "+cacheKey);
  }

  /**
   * Default string is currentlocale+hasEditPermission
   */
  protected String getCachePrefixString(IWContext iwc){
    int instanceID = this.getICObjectInstanceID();
    boolean edit = hasEditPermission();
    String locale = iwc.getCurrentLocale().toString();
    boolean isSecure = iwc.getRequest().isSecure();

    return (instanceID+locale+edit+isSecure);
  }

  protected boolean isCacheable(){
    return this.cacheable;
  }

  public void setCacheable(boolean cacheable){
    this.cacheable = cacheable;
  }

  /**
   * Override this method for correct caching. The cacheStateprefix will always be <br>
   * of the form CurrentLocal+edit(boolean) unless you override getCachePrefixString(iwc) <br>
   * It is better to return a string with that string prefixed to it unless the block output <br>
   * is the same for every local and edit/view rights.
   * @return cacheStatePrefix
   */
  protected String getCacheState(IWContext iwc, String cacheStatePrefix){
    return cacheStatePrefix;
  }

  /**
   * Override this method to invalidate something other than the current state.
   * Default: iwc.getApplication().getIWCacheManager().invalidateCache(cacheKey);
   */
  public void invalidateCache(IWContext iwc){
    invalidateCache(iwc.getApplication());
    //debug("INVALIDATING : "+getCacheKey(iwc));
  }

  public void invalidateCache(IWMainApplication iwma){
     if( getDerivedCacheKey()!=null ) iwma.getIWCacheManager().invalidateCache(getDerivedCacheKey());
  }

  /**
   * Default: iwc.getApplication().getIWCacheManager().invalidateCache(cacheKey+suffix);
   */
  public void invalidateCache(IWContext iwc, String suffix){
    if( getOriginalCacheKey()!=null ) iwc.getApplication().getIWCacheManager().invalidateCache(getOriginalCacheKey()+suffix);
    //debug("INVALIDATING : "+getCacheKey(iwc)+suffix);
  }

  public class BlockCacheWriter extends java.io.PrintWriter{

    private PrintWriter underlying;
    private StringBuffer buffer;


    public BlockCacheWriter(PrintWriter underlying,StringBuffer buffer){
      super(underlying);
      this.underlying = underlying;
      this.buffer=buffer;
    }

    public boolean checkError(){
      return underlying.checkError();
    }

    public void close(){
      underlying.close();
    }

    public void flush(){
      underlying.flush();
    }

    public void print(boolean b){
      print(String.valueOf(b));
    }

    public void print(char c){
      print(String.valueOf(c));
    }

    public void print(char[] s){
      print(String.valueOf(s));
    }

    public void print(double d){
      print(String.valueOf(d));
    }

    public void print(float f){
      print(String.valueOf(f));
    }

    public void print(int i){
      print(String.valueOf(i));
    }

    public void print(long l){
      print(String.valueOf(l));
    }

    public void print(Object o){
      print(String.valueOf(o));
    }

    public void print(String s){
      underlying.print(s);
      buffer.append(s);
    }

    public void println(){
      underlying.println();
      buffer.append(newline);
    }


    public void println(boolean b){
      println(String.valueOf(b));
    }

    public void println(char c){
      println(String.valueOf(c));
    }

    public void println(char[] s){
      println(String.valueOf(s));
    }

    public void println(double d){
      println(String.valueOf(d));
    }

    public void println(float f){
      println(String.valueOf(f));
    }

    public void println(int i){
      println(String.valueOf(i));
    }

    public void println(long l){
      println(String.valueOf(l));;
    }

    public void println(Object o){
      println(String.valueOf(o));
    }

    public void println(String s){
      print(s);
      println();
    }

    public void setError(){
      super.setError();
    }

    public void write(char[] buf){
      print(buf);
    }

    public void write(char[] buf,int off, int len){
      char[] newarray = new char[len];
      System.arraycopy(buf,off,newarray,0,len);
      write(newarray);
    }

    public void write(int c){
      print(c);
    }

    public void write(String s){
      print(s);
    }

    public void write(String s, int off, int len){
      write(s.substring(off,off+len));
    }


  }

  public static Block getCacheableObject(PresentationObject objectToCache, String cacheKey, long millisecondsInterval) {
      Block obj = new Block();
	obj.add(objectToCache);
	obj.setCacheable(cacheKey, millisecondsInterval);
      return obj;
  }



  public synchronized Object _clone(IWUserContext iwc, boolean askForPermission){
    if ( iwc != null ) {
      this.setIWApplicationContext(iwc.getApplicationContext());
      this.setIWUserContext(iwc);
    }
    if(askForPermission||iwc!=null){
      if(iwc.hasViewPermission(this)){
	return this.clone();
      } else {
	return NULL_CLONE_OBJECT;
      }
    } else {
      return this.clone();
    }
  }


  public synchronized Object clone(){
    Block obj = (Block)super.clone();

    obj.cacheable = this.cacheable;
    obj.cacheInterval = this.cacheInterval;
    obj.targetObjInst = this.targetObjInst;
    if(this.cacheKey != null){
      obj.cacheKey = this.cacheKey;
    }
    if(this.derivedCacheKey != null){
      obj.derivedCacheKey = this.derivedCacheKey;
    }
    return obj;
  }


}
