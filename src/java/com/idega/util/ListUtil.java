package com.idega.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ListUtil {

  private static final ArrayList<Object> emptyVector = new EmptyList<Object>();

  private ListUtil() {
  }

  /**
   * <p>Copies first level of given {@link Collection}. It means, that every
   * element in original {@link Collection} was inserted into new one.</p>
   * @param <T> {@link Collection} type.
   * @return Copied {@link Collection} by first level.
   * <code>null</code> on failure.
   * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
   */
  public static <T> Collection<T> getDeepCopy(Collection<T> original) {
	  if (ListUtil.isEmpty(original))
		  return Collections.emptyList();

	  Collection<T> copy = new ArrayList<T>();
	  for (T instance: original)
		  copy.add(instance);

	  return copy;
  }

/**
 * Gets an instance of a list that is empty.
 * @return An immutable unsynchronized List with no values
 **/
  public static <T> ArrayList<T> getEmptyList(){
    return getEmptyVector();
  }

  @SuppressWarnings("unchecked")
private static <T> ArrayList<T> getEmptyVector(){
    return (ArrayList<T>) emptyVector;
  }

/**
 * Converts an instance of List to an instance of Collection.
 * @param <T>
 * @param coll An input Collection
 * @return The input value coll if it is an instance of List. Else it will construct a list with the same values and return it.
 **/
  public static <T> List<T> convertCollectionToList(Collection<T> coll){
    if (coll instanceof List) {
    	return (List<T>) coll;
    } else {
    	return new ArrayList<T>(coll);
    }
  }

  public static <T> List<T> reverseList(List<T> list){
    List<T> theReturn = new ArrayList<T>();
    int size = list.size();
    for (int i = size-1 ; i >= 0 ; i--) {
      T item = list.get(i);
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
  public static List<String> convertStringArrayToList(String[] stringArray) {
  	if (stringArray != null && stringArray.length > 0) {
  		List<String> returnList = new ArrayList<String>();

  		for (int i = 0; i < stringArray.length; i++) {
			returnList.add(stringArray[i]);
		}

  		return returnList;
  	}

  	return null;
  }

	/**
	 * @param list A list of Strings
	 *
	 * @returns a String with comma separated values
	 */
	public static String convertListOfStringsToCommaseparatedString(List<String> list) {
		return convertListToCommaseparatedString(list);
	}

	public static String convertListToCommaseparatedString(List<?> list) {
		StringBuffer sList = new StringBuffer();
		if (list != null && !list.isEmpty()) {
			Iterator<?> iter = list.iterator();
			for (int g = 0; iter.hasNext(); g++) {
				Object item = iter.next();
				if (g > 0) {
					sList.append(CoreConstants.COMMA);
				}

				if (item != null) {
					sList.append(item.toString());
				}

			}
		}
		return sList.toString();
	}

	/**
	 * Converts a comma separated string to a list of strings
	 * @param commaSeparatedString A comma separated string e.g. "value1,value2,value3,..."
	 * @returns a List of Strings or an empty list if no values where found
	 */
	public static List<String> convertCommaSeparatedStringToList(String commaSeparatedString) {
		return ListUtil.convertTokenSeparatedStringToList(commaSeparatedString, CoreConstants.COMMA);
	}

	/**
	 * Converts a [token] separated string to a list of strings
	 * @param tokenSeparatedString A [token] separated string e.g. "value1[token]value2[token]value3[token]..." like "value1,value2,value3,..." for example
	 * @param tokenSeparator The separator such as a "," or ";" for example
	 * @returns a List of Strings or an empty list if no values where found
	 */
	public static List<String> convertTokenSeparatedStringToList(String tokenSeparatedString, String tokenSeparator) {
		List<String> list = new ArrayList<String>();

		if(tokenSeparatedString!=null && tokenSeparator!=null){
			StringTokenizer tokens = new StringTokenizer(tokenSeparatedString,tokenSeparator);
			while (tokens.hasMoreTokens()) {
				list.add(tokens.nextToken());
			}
		}
		else{
			Logger.getLogger(ListUtil.class.getName()).warning("String or token is null!");
		}

		return list;
	}

	public static final boolean isEmpty(Collection<?> collection) {
		if (collection == null || collection.isEmpty() || collection.size() <= 0) {
			return true;
		}

		return false;
	}

	public static <E>List<E> getFilteredList(Collection<E> collection) {
		List<E> filtered = new ArrayList<E>();
		for (E object: collection) {
			if (object != null && !StringUtil.isEmpty(object.toString())) {
				filtered.add(object);
			}
		}

		return filtered;
	}

	public static <T> List<T> getConcatinatedLists(List<List<T>> lists) {
		if (isEmpty(lists)) {
			return Collections.emptyList();
		}

		List<T> results = new ArrayList<T>();
		lists.forEach(list -> {
			results.addAll(list);
		});
		return results;
	}

  protected static class EmptyList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 4998333443520433621L;

	@Override
	public boolean add(T o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    @Override
	public void add(int index, T o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    @Override
	public boolean addAll(@SuppressWarnings("rawtypes") Collection o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    @Override
	public boolean addAll(int index, @SuppressWarnings("rawtypes") Collection o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

    @Override
	public T set(int index, T o){
      throw new RuntimeException("This empty list is final and cannot be modified");
    }

  }

}