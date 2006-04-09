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



   private HashtableMultivalued tableForKeys;





   public HashtableDoubleKeyed(){

      this.table = new Hashtable();

      this.tableForKeys=new HashtableMultivalued();

   }



   public Object put(String key1,String key2,Object value){

      this.tableForKeys.put(key1,value);

      this.tableForKeys.put(key2,value);

      return this.table.put(StringHandler.concatAlphabetically(key1,key2),value);

   }



   public Object get(String key1,String key2){

      return this.table.get(StringHandler.concatAlphabetically(key1,key2));

  }



   public Object remove(String key1,String key2){

      return this.table.remove(StringHandler.concatAlphabetically(key1,key2));

  }



  public Enumeration keys(){

      return this.table.keys();

  }



  public Enumeration elements(){

      return this.table.elements();

  }



  /**

   * Returns a list of objects put for this key

   */

  public List get(String key){

      return this.tableForKeys.getList(key);

  }





}

