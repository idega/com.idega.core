package com.idega.util;

/**
 * Title:        FrameStorageInfo
 * Description:  Class to store Page Objects for cache-ing in the idegaWeb Framework
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */
import com.idega.presentation.Page;
import com.idega.repository.data.RefactorClassRegistry;

  public class FrameStorageInfo{

    public static final FrameStorageInfo EMPTY_FRAME=new FrameStorageInfo();

    private Class myClass;
    private String storageKey;
    private long creationTime;

    private FrameStorageInfo(){
      Page page = new Page("Empty Page");
      setFrameClass(page.getClass());
      setStorageKey(page.getID());
      setCreationTime();
    }

    public FrameStorageInfo(String storageKey, Class theClass){
      setFrameClass(theClass);
      setStorageKey(storageKey);
      setCreationTime();
    }


    public Class getFrameClass(){
      return myClass;
    }

    public void setFrameClass(Class myClass){
      this.myClass=myClass;
    }

    public void setFrameClass(String myClassName){
      try{
        this.myClass=RefactorClassRegistry.forName(myClassName);
      }
      catch(ClassNotFoundException e){
        e.printStackTrace();
      }
    }

    public String getStorageKey(){
      return storageKey;
    }

    public void setStorageKey(String key){
      this.storageKey=key;
    }

    private void setCreationTime(){
      this.creationTime=System.currentTimeMillis();
    }


  }
