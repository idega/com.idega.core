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

      this.stringsFile=new File(filePath);

      FileInputStream stream = new FileInputStream(this.stringsFile);

      this.properties = new Properties();

      this.properties.load(stream);

    }

    catch(Exception ex){

      ex.printStackTrace();

    }

   }





   public Enumeration getKeys(){

     return this.properties.keys();

   }



    public String getString(String key){

      return this.properties.getProperty(key);

    }





    public void setString(String key,String theString){

      this.properties.setProperty(key,theString);

      try{

        FileOutputStream outStream = new FileOutputStream(this.stringsFile);

        this.properties.store(outStream,"");

      }

      catch(FileNotFoundException e){

        e.printStackTrace();

      }

      catch(IOException e){

        e.printStackTrace();

      }



    }



    public Locale getLocale(){

      return this.locale;

    }



}

