package com.idega.data;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.util.Collection;
import java.util.Iterator;

import java.rmi.RemoteException;


public class IDOUtil {

  private static IDOUtil instance;

  private IDOUtil() {
  }

  public static IDOUtil getInstance(){
    if(instance==null){
      instance = new IDOUtil();
    }
    return instance;
  }


  /**
   * @param list A list of IDOEntity objects
   *
   * @returns a String with comma separated list of primary keys for the IDOEntities
   */
  public String convertListToCommaseparatedString(Collection list){
      String sList = "";
      if(list != null && list.size() > 0){
        //String sGroupList = "";
        Iterator iter = list.iterator();
        for (int g=0; iter.hasNext(); g++) {
          IDOEntity item = (IDOEntity)iter.next();
          if(g>0){ sList += ", "; }
          try{
            sList += item.getPrimaryKey();
          }
          catch(RemoteException rme){
            rme.printStackTrace();
          }
        }
      }
      return sList;
  }


  /**
   * @param sArray An array of primary keys
   *
   * @returns a String with comma separated list of primary keys for the IDOEntities
   */
  public String convertArrayToCommaseparatedString(String[] sArray){
    String sList = "";
    if (sArray != null && sArray.length > 0){
      for(int g = 0; g < sArray.length; g++){
        if(g>0){ sList += ", "; }
        sList += sArray[g];
      }
    }
    return sList;
  }




}