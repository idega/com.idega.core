//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.util.datastructures;


import java.util.*;
import com.idega.util.*;


/**
*Class similar to Hashtable but with two keys for each value
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.9
*/
public class HashtableDoubleKeyed{

   private Hashtable table;


   public HashtableDoubleKeyed(){
      table = new Hashtable();
   }

   public Object put(String key1,String key2,Object value){
      return table.put(StringHandler.concatAlphabetically(key1,key2),value);
   }


   public Object get(String key1,String key2){
      return table.get(StringHandler.concatAlphabetically(key1,key2));
  }


}
