//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.idegaweb;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 0.5 - Under development

*UNIMPLEMENTED

*/

public class IWLocalizedStringHandler{



  private Locale locale;

  private IWBundle bundle;

  private Properties properties;

  private File stringsFile;



   protected IWLocalizedStringHandler(IWBundle bundle,String filePath,Locale locale){

    try{

      this.bundle=bundle;

      this.locale=locale;

      stringsFile=new File(filePath);

      FileInputStream stream = new FileInputStream(stringsFile);

      properties = new Properties();

      properties.load(stream);

    }

    catch(Exception ex){

      ex.printStackTrace();

    }

   }





   public Enumeration getKeys(){

     return properties.keys();

   }



    public String getString(String key){

      return properties.getProperty(key);

    }





    public void setString(String key,String theString){

      properties.setProperty(key,theString);

      try{

        FileOutputStream outStream = new FileOutputStream(stringsFile);

        properties.store(outStream,"");

      }

      catch(FileNotFoundException e){

        e.printStackTrace();

      }

      catch(IOException e){

        e.printStackTrace();

      }



    }



    public Locale getLocale(){

      return locale;

    }



}

