package com.idega.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.Iterator;
import java.util.Collection;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ListUtil {

  private static final ArrayList emptyVector = new EmptyList();

  private ListUtil() {
  }
/**
 * Gets an instance of a list that is empty.
 * @return An immutable unsynchronized List with no values
 **/
  public static List getEmptyList(){
    return getEmptyVector();
  }

  private static ArrayList getEmptyVector(){
    return emptyVector;
  }

/**
 * Converts an instance of List to an instance of Collection.
 * @param coll An input Collection
 * @return The input value coll if it is an instance of List. Else it will construct a list with the same values and return it.
 **/
  public static List convertCollectionToList(Collection coll){
    if(coll instanceof List){
      return (List)coll;
    }
    else{
      List theReturn = new ArrayList(coll);
      return theReturn;
    }
  }

  public static List reverseList(List list){
    List theReturn = new ArrayList();
    int size = list.size();
    for (int i = size-1 ; i >= 0 ; i--) {
      Object item = list.get(i);
      if(item!=null){
        theReturn.add(item);
      }
    }
    return theReturn;
  }
  
  /**
   * Return a list of the strings or a null if the string array is empty
   * @param stringArray
   * @return
   */
  public static List convertStringArrayToList(String[] stringArray){
  	
  	if(stringArray!=null && stringArray.length>0){
  		List returnList = new ArrayList();
  		
  		for (int i = 0; i < stringArray.length; i++) {
				String string = stringArray[i];
				returnList.add(i,string);
			}
  		
  		return returnList;
  	}
  	else return null;
		
  }
  
	/**
	 * @param list A list of Strings
	 *
	 * @returns a String with comma separated values
	 */
	public static String convertListOfStringsToCommaseparatedString(List list) {
		StringBuffer sList = new StringBuffer();
		if (list != null && !list.isEmpty()) {
			Iterator iter = list.iterator();
			for (int g = 0; iter.hasNext(); g++) {
				String item = (String) iter.next();
				if (g > 0) {
					sList.append(",");
				}
				
				if(item!=null) {
					sList.append(item);
				}
				
			}
		}
		return sList.toString();
	}

  private static class EmptyList extends ArrayList{


    public boolean add(Object o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    public void add(int index,Object o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    public boolean addAll(Collection o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    public boolean addAll(int index, Collection o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    public Object set(int index,Object o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

  }

}
