//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import com.idega.presentation.text.*;
import java.util.*;
import java.io.*;
import com.idega.jmodule.login.business.*;
import com.idega.idegaweb.IWCacheManager;
import com.idega.block.IWBlock;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class Block extends PresentationObjectContainer implements IWBlock{

  private static Map permissionKeyMap = new Hashtable();
  private String cacheKey = null;
  private boolean cacheable=false;
  private long cacheInterval;

  private boolean editPermission = false;

  private static final String concatter = "_";
  private static final String newline = "\n";

  public static boolean usingNewAcessControlSystem=false;

  public Block(){

  }

  public String getBundleIdentifier(){
    return IW_CORE_BUNDLE_IDENTIFIER;
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

  public boolean deleteBlock(int ICObjectInstanceId){
    System.err.print("method deleteBlock(int ICObjectInstanceId) not implemented in class "+this.getClass().getName());
    return true;
  }

  public boolean isAdministrator(IWContext iwc)throws Exception{
    if(usingNewAcessControlSystem){
      return iwc.hasEditPermission(this);
    }
    else{
      return AccessControl.isAdmin(iwc);
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
  protected void registerPermissionKeys(){
  }

  protected void registerPermissionKey(String permissionKey,String localizeableKey){
    Map m = getPermissionKeyClassMap();
    m.put(permissionKey,localizeableKey);
  }

  protected void registerPermissionKey(String permissionKey){
    registerPermissionKey(permissionKey,permissionKey);
  }

  private Map getPermissionKeyMap(){
    return permissionKeyMap;
  }

  private Map getPermissionKeyClassMap(){
    Map m = (Map)getPermissionKeyMap().get(this.getClass());
    if(m==null){
      m = new Hashtable();
      getPermissionKeyMap().put(this.getClass(),m);
    }
    return m;
  }

  String[] getPermissionKeys(Block obj){
    return getPermissionKeys(obj.getClass());
  }

  String[] getPermissionKeys(Class jPresentationObjectClass){
    Map m = (Map)getPermissionKeyMap().get(jPresentationObjectClass);
    if(m!=null){
      return (String[])m.keySet().toArray(new String[0]);
    }
    return null;
  }

  private static long twentyMinutes = 60*1000*20;

  public void setCacheable(String cacheKey){
    setCacheable(cacheKey,twentyMinutes);
  }

  public void setCacheable(String cacheKey,long millisecondsInterval){
    cacheable=true;
    this.cacheKey=cacheKey;
    this.cacheInterval=millisecondsInterval;
  }

  public void beginCacheing(IWContext iwc,StringBuffer buffer)throws Exception{
    PrintWriter servletWriter = iwc.getWriter();
    iwc.setCacheing(true);
    PrintWriter writer = new JModuleCacheWriter(servletWriter,buffer);
    iwc.setCacheWriter(writer);
  }

  public void endCacheing(IWContext iwc,StringBuffer buffer){
    iwc.setCacheing(false);
    IWCacheManager.getInstance(iwc.getApplication()).setObject(getCacheKey(iwc),buffer,cacheInterval);
  }

  public boolean hasEditPermission(){
    return editPermission;
  }

  public void _main(IWContext iwc)throws Exception{
    editPermission = iwc.hasEditPermission(this);

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

  public void print(IWContext iwc)throws Exception{
    if(this.isCacheable()){
      if(isCacheValid(iwc)){
        StringBuffer buffer = (StringBuffer)IWCacheManager.getInstance(iwc.getApplication()).getObject(getCacheKey(iwc));
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
    if(getCacheKey(iwc)!=null){
      return IWCacheManager.getInstance(iwc.getApplication()).isCacheValid(getCacheKey(iwc));
    }
    return false;
  }

  private String getCacheKey(IWContext iwc){
    return cacheKey;
  }

  private void setCacheKey(IWContext iwc){
    boolean loggedon = LoginBusiness.isLoggedOn(iwc);
    if(loggedon){
      String parameter = AccessControl.ACCESSCONTROL_GROUP_PARAMETER;
      String parametervalue = null;
      if(parameter.equals(AccessControl.CLUB_ADMIN_GROUP)){
        parametervalue = (String)iwc.getSessionAttribute(parameter);
      }
      else{
        parametervalue = (String)iwc.getSessionAttribute(parameter);
        parametervalue += concatter;
        parametervalue += AccessControl.CLUB_ADMIN_GOLF_UNION_ID_ATTRIBUTE;
        parametervalue += AccessControl.getGolfUnionOfClubAdmin(iwc);
      }
      cacheKey = cacheKey+concatter+parameter+concatter+parametervalue;
    }
    this.cacheKey += iwc.getCurrentLocale().toString();
  }

  protected boolean isCacheable(){
    return this.cacheable;
  }


  public class JModuleCacheWriter extends java.io.PrintWriter{

    private PrintWriter underlying;
    private StringBuffer buffer;


    public JModuleCacheWriter(PrintWriter underlying,StringBuffer buffer){
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



  public synchronized Object _clone(IWContext iwc, boolean askForPermission){
    if(askForPermission){
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
    Block obj = (Block)super.clone(null,false);

    obj.cacheable = this.cacheable;
    obj.cacheInterval = this.cacheInterval;

    if(this.cacheKey != null){
      obj.cacheKey = this.cacheKey;
    }

    return obj;
  }

}
