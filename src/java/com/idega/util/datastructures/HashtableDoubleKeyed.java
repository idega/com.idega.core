//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/

package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.idega.util.ListUtil;
import com.idega.util.StringHandler;

/**

*Class similar to Hashtable but with two keys for each value

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 0.9

*/

public class HashtableDoubleKeyed<K, V> {

   private Hashtable<String, V> table;
   private HashtableMultivalued<String, Collection<V>, V> tableForKeys;

   public HashtableDoubleKeyed() {
      this.table = new Hashtable<String, V>();
      this.tableForKeys=new HashtableMultivalued<String, Collection<V>, V>();
   }

   public V put(String key1, String key2, V value){
      this.tableForKeys.putValue(key1, value);
      this.tableForKeys.putValue(key2, value);
      
      return this.table.put(StringHandler.concatAlphabetically(key1,key2),value);
   }

   public V get(String key1,String key2){
      return this.table.get(StringHandler.concatAlphabetically(key1,key2));
  }

   public V remove(String key1, String key2){
      return this.table.remove(StringHandler.concatAlphabetically(key1,key2));
  }

  public Enumeration<String> keys(){
      return this.table.keys();
  }

  public Enumeration<V> elements(){
      return this.table.elements();
  }

  /**
   * Returns a list of objects put for this key
   * @param key
   * @return
   */
  public List<V> get(String key){
	  Collection<V> values = this.tableForKeys.get(key);
	  return ListUtil.isEmpty(values) ? null : new ArrayList<V>(values);
  }
  
}