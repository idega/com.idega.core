//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/

package com.idega.util;



import java.io.*;

import java.util.*;

import java.awt.*;

import com.idega.util.datastructures.InheritablePropertyStorer;





/**

* Class to handle inheritable properties (that are implemented as stored in a static Map in a superclass)

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class InheritableStaticPropertyHandler

{



  private static InheritableStaticPropertyHandler instance;



   // so that nobody can accidentally create an InheritableStaticPropertyHandler object

   private InheritableStaticPropertyHandler(){}





   public static InheritableStaticPropertyHandler getInstance(){

    if(instance==null){

      instance = new InheritableStaticPropertyHandler();

    }

    return instance;

   }





   public static void setProperty(String propertyName, Object storedPropertyObject,InheritablePropertyStorer storer){

      Map map = storer.getMap();

//      map.put();

   }



   public static Object getProperty(String propertyName,InheritablePropertyStorer storer){

      Map map = storer.getMap();

//      return map.get();

      return null;

   }

  /**

   * unimlpemented

   */

  public static void setProperty(){}



}

