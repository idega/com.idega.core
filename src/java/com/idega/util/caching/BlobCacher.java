package com.idega.util.caching;



import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import com.idega.data.IDOLegacyEntity;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.FileUtil;
import com.idega.util.datastructures.HashtableDoubleKeyed;
import com.idega.util.text.TextSoap;



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



  private static String rootCachingPath = "cache";

  private static String applicationObject = "blobcacher";

  private static String separator = FileUtil.getFileSeparator();



  public static String getCachedUrl( String entityClassString, int id, IWMainApplication app, String blobColumnName){

     String url = null;

    //check for cachetable in memory

    if( cacheTableExists( entityClassString, id, app) ){

      //check if this blob has already been cached

      HashtableDoubleKeyed cache = getCacheTable(app);

      url = (String) cache.get(com.idega.data.GenericEntity.getStaticInstance(entityClassString).getEntityName(), Integer.toString(id) );

      if( url == null ) {//if null cache it for next time

       cacheToFile(entityClassString,id,app.getApplicationRealPath(), cache, blobColumnName,app);

      }

    }



    return url;

  }





  private static void cacheToFile(String entityClassString, int id, String applicationURL, HashtableDoubleKeyed cache, String blobColumnName , IWMainApplication app){

    InputStream input = null;

    IDOLegacyEntity entity;

    try{

      entity = com.idega.data.GenericEntity.getEntityInstance(RefactorClassRegistry.forName(entityClassString),id);

      // setja inn entitie app.setAttribute();



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

       if (input != null ) {
		input.close();
	}

      }

      catch(IOException e){

        e.printStackTrace(System.err);

        System.err.println("BlobCacher : error closing stream");

      }

    }



  }



 public static boolean isCached(String entityClassString, int id, IWMainApplication app){

    if(!cacheTableExists( entityClassString, id, app)) {
		return false;
	}



    //check if this blob has already been cached

    String url = (String) getCacheTable(app).get(com.idega.data.GenericEntity.getStaticInstance(entityClassString).getEntityName(), Integer.toString(id) );

    if( url == null ) {//if null cache it for next time

     return false;

    }
	else {
		return true;
	}



  }



  private static boolean cacheTableExists(String entityClassString, int id, IWMainApplication app){

    //check if the cachingtable exists if not create it

    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )app.getAttribute(applicationObject);

    if ( cache == null ) {

     cache = new HashtableDoubleKeyed();

     app.setAttribute(applicationObject,cache);

     return false;

    }
	else {
		return true;
	}

  }



  private static HashtableDoubleKeyed getCacheTable(IWMainApplication app){

    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )app.getAttribute(applicationObject);

    return cache;

  }



  public static void deleteCache(IWMainApplication app){

    HashtableDoubleKeyed cache = ( HashtableDoubleKeyed )app.getAttribute(applicationObject);

    if ( cache != null ) {

      String realPath = app.getApplicationRealPath();

      Enumeration enumer = cache.elements();

      while( enumer.hasMoreElements() ){

        String pathAndFile = realPath + java.net.URLDecoder.decode(TextSoap.findAndReplace( (String)enumer.nextElement() ,"/",FileUtil.getFileSeparator()) );

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



