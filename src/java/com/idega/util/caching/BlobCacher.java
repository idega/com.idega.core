package com.idega.util.caching;

import java.io.*;
import java.util.*;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.util.datastructures.HashtableDoubleKeyed;
import com.idega.data.GenericEntity;
import com.idega.util.FileUtil;
import com.idega.util.text.TextSoap;
import com.idega.idegaweb.IWMainApplication;

/**
 * Title: BlobCacher
 * Description: A caching utility for storing blob to files to minimize database access
 * Copyright: Idega software Copyright (c) 2001
 * Company: idega
 * @author <a href = "mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 *
 */


public class BlobCacher  {

  private static String rootCachingPath = "blobcache";
  private static String applicationObject = "blobcacher";
  private static String separator = FileUtil.getFileSeparator();

  public static String getCachedUrl( String entityClassString, int id, ModuleInfo modinfo, String blobColumnName){
     String url = null;
    //check for cachetable in memory
    if( cacheTableExists( entityClassString, id, modinfo) ){
      //check if this blob has already been cached
      HashtableDoubleKeyed cache = getCacheTable(modinfo);
      url = (String) cache.get(GenericEntity.getStaticInstance(entityClassString).getEntityName(), Integer.toString(id) );
      if( url == null ) {//if null cache it for next time
       cacheToFile(entityClassString,id,modinfo.getApplication().getApplicationRealPath(), cache, blobColumnName);
      }
    }

    return url;
  }


  private static void cacheToFile(String entityClassString, int id, String applicationURL, HashtableDoubleKeyed cache, String blobColumnName ){
    InputStream input = null;
    GenericEntity entity;
    try{
      entity = GenericEntity.getEntityInstance(Class.forName(entityClassString),id);
      input = entity.getInputStreamColumnValue(blobColumnName);
      String realPath = applicationURL+separator+rootCachingPath;
      String virtualPath = "/"+rootCachingPath+"/";
      String fileName = entity.getName();

      if(input != null ){
        FileUtil.streamToFile(input,realPath,entity.getID()+"_"+fileName);
        cache.put(entity.getEntityName(), Integer.toString(entity.getID()),virtualPath+java.net.URLEncoder.encode(entity.getID()+"_"+fileName));
      }
    }
    catch( Exception e ){
     e.printStackTrace(System.err);
     System.err.println("BlobCacher : error getting stream from blob");
    }
    finally{
      try{
       if (input != null ) input.close();
      }
      catch(IOException e){
        e.printStackTrace(System.err);
        System.err.println("BlobCacher : error closing stream");
      }
    }

  }

 public static boolean isCached(String entityClassString, int id, ModuleInfo modinfo){
    if(!cacheTableExists( entityClassString, id, modinfo)) return false;

    //check if this blob has already been cached
    String url = (String) getCacheTable(modinfo).get(GenericEntity.getStaticInstance(entityClassString).getEntityName(), Integer.toString(id) );
    if( url == null ) {//if null cache it for next time
     return false;
    }
    else return true;

  }

  private static boolean cacheTableExists(String entityClassString, int id, ModuleInfo modinfo){
    //check if the cachingtable exists if not create it
    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )modinfo.getApplicationAttribute(applicationObject);
    if ( cache == null ) {
     cache = new HashtableDoubleKeyed();
     modinfo.setApplicationAttribute(applicationObject,cache);
     return false;
    }
    else return true;
  }

  private static HashtableDoubleKeyed getCacheTable(ModuleInfo modinfo){
    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )modinfo.getApplicationAttribute(applicationObject);
    return cache;
  }

  public static void deleteCache(IWMainApplication app){
    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )app.getAttribute(applicationObject);
    if ( cache != null ) {
      String realPath = app.getApplicationRealPath();
      Enumeration enum = cache.elements();
      while( enum.hasMoreElements() ){
        String pathAndFile = realPath + java.net.URLDecoder.decode(TextSoap.findAndReplace( (String)enum.nextElement() ,"/",FileUtil.getFileSeparator()) );
        System.out.println(pathAndFile);
        FileUtil.delete( pathAndFile );
      }
    }
  }
  // not done yet
  private static void deleteCachedBlobFile(String pathAndFileName){
     FileUtil.delete(pathAndFileName);
  }


}//end of class

