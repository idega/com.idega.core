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
  private static String seperator = FileUtil.getFileSeparator();

  public static String getCachedUrl( GenericEntity entity, ModuleInfo modinfo, String blobColumnName){
    //check if the cachingtable exists
    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )modinfo.getApplicationAttribute(applicationObject);
    if ( cache == null ) {
     cache = new HashtableDoubleKeyed();
     modinfo.setApplicationAttribute(applicationObject,cache);
    }

    //check if this blob has already been cached
    String url = (String) cache.get(entity.getEntityName(), Integer.toString(entity.getID()) );
    if( url == null ) {//if null cache it for next time
     cacheToFile(entity,modinfo.getApplication().getApplicationRealPath(), cache, blobColumnName);
     return null;
    }
    else return url;

  }



  private static void cacheToFile(GenericEntity entity, String applicationURL, HashtableDoubleKeyed cache, String blobColumnName ){
    InputStream input;
    try{
      input = entity.getInputStreamColumnValue(blobColumnName);
    }
    catch( Exception e ){
     e.printStackTrace(System.err);
     System.err.println("BlobCacher : error getting stream from blob");
     return;
    }

    String realPath = applicationURL+seperator+rootCachingPath;
    String virtualPath = "/"+rootCachingPath+"/";
    String fileName = entity.getName();

    try{
      FileUtil.streamToFile(input,realPath,entity.getID()+"_"+fileName);
    }
    catch( Exception e ){
     e.printStackTrace(System.err);
     System.err.println("BlobCacher : error streaming to file");
     return;
    }

    cache.put(entity.getEntityName(),Integer.toString(entity.getID()),virtualPath+entity.getID()+"_"+fileName);

  }

  public static void deleteCache(IWMainApplication app){
    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )app.getAttribute(applicationObject);
    if ( cache != null ) {
      String realPath = app.getApplicationRealPath();
      Enumeration enum = cache.elements();
      while( enum.hasMoreElements() ){
        String pathAndFile = realPath + TextSoap.findAndReplace( (String)enum.nextElement() ,"/",FileUtil.getFileSeparator());
        System.out.println(pathAndFile);
        FileUtil.delete( pathAndFile );
      }
    }
  }
  // not done yet
  private static void deleteCachedBlobFile(String pathAndFileName){
     // File file = new File( (String) enum.nextElement());
     // file.delete();
  }


}//end of class

