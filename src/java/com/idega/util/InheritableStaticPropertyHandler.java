//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/

package com.idega.util;



//import java.util.Map;

import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.datastructures.InheritablePropertyStorer;





/**

* Class to handle inheritable properties (that are implemented as stored in a static Map in a superclass)

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class InheritableStaticPropertyHandler implements Singleton

{



  private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new InheritableStaticPropertyHandler();}};



   // so that nobody can accidentally create an InheritableStaticPropertyHandler object

   private InheritableStaticPropertyHandler(){}





   public static InheritableStaticPropertyHandler getInstance(){
   		return (InheritableStaticPropertyHandler) SingletonRepository.getRepository().getInstance(InheritableStaticPropertyHandler.class, instantiator);
   }





   public static void setProperty(String propertyName, Object storedPropertyObject,InheritablePropertyStorer storer){

      //Map map = storer.getMap();

//      map.put();

   }



   public static Object getProperty(String propertyName,InheritablePropertyStorer storer){

      //Map map = storer.getMap();

//      return map.get();

      return null;

   }

  /**

   * unimlpemented

   */

  public static void setProperty(){}



}

