//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import com.idega.presentation.PresentationObject;
import com.idega.presentation.Image;
import java.io.*;
import java.util.*;

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
