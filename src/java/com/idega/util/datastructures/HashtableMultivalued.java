//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.util.datastructures;


import java.util.*;


/**
*Class similar to Hashtable but with multiple values for each key
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.9
*/
public class HashtableMultivalued extends Hashtable{

   
   public Object put(Object key,Object value){
      List vector = getList(key);
      if(vector==null){
         vector = new Vector();
      }
      vector.add(value);
      return null; 
   }

   public Object put(Object key,Collection coll){
      List vector = getList(key);    
      if(vector==null){
         vector = new Vector();
      }
      vector.addAll(coll);
      return null; 
   }

   /**
   *Returns the first object with this key
   */
   public Object get(Object key){
      List vector = getList(key);
      if(vector==null){
         return null;
      }
      else{
         return vector.get(0);
      }
   }
   
   public Collection getCollection(Object key){
      return (Collection)super.get(key);
   }

	public List getList(Object key){
      return (List)super.get(key);
   }
}
