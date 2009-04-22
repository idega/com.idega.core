//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/

package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import com.idega.util.ListUtil;


/**

*Class similar to Hashtable but with multiple values for each key

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 0.9
 * @param <K>
 * @param <V>
*/

public class HashtableMultivalued<K, T extends Collection<V>, V> extends Hashtable<K, Collection<V>> {

	private static final long serialVersionUID = -7189090701326564598L;

	public V putValue(K key, V value) {
      Collection<V> values = getList(key);
      values.add(value);

      super.put(key, values);

      return value;
   }

   @Override
   public Collection<V> put(K key, Collection<V> coll){
      Collection<V> values = getList(key);
      values.addAll(coll);
      return super.put(key, values);
   }

   @Override
   public Collection<V> get(Object key){
      return super.get(key);
   }

   public V getFirstValue(K key) {
	   Collection<V> values = getList(key, false);
	   
	   if (ListUtil.isEmpty(values)) {
		   return null;
	   }
	   
	   return values.iterator().next();
   }
   
   /**
    * Returns the first object with this key and removes it from the Hashtable
    * @param key
    * @return
    */
   public V getAndRemove(K key) {
      V obj = getFirstValue(key);
     
      if (obj == null) {
    	  return null;
      }
      
      getList(key, false).remove(obj);

      return obj;
   }
   
   public Collection<V> getList(Object key) {
	   return getList(key, true);
   }

   private Collection<V> getList(Object key, boolean initialize) {
	   Collection<V> values = super.get(key);
	   if (values == null && initialize) {
		   values = new ArrayList<V>();
	   }
	   return values;
   }

  public int getNumberOfValues(K key){
      Collection<V> values = getList(key, false);
      return ListUtil.isEmpty(values) ? 0 : values.size();
  }

}

