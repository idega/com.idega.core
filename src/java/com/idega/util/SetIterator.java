package com.idega.util;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.HiddenInput;
/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 *
 *	This	 list iterator can be used to step through a list by getting subsets
 *	of	 the list.
 *	There	 are	 also some methods to store and to retrieve the state of 	an
 *	instance	 into and from the session.
 *
 *
 */
public class SetIterator implements ListIterator  {

  public final static String SET_ITERATOR_STATE_KEY = "set_iterator_state_key";
  
  public final static String SET_ITERATOR_LIST_ID_KEY = "set_iterator_list_id_key";
  
  private final static String STATE_STRING_DELIMITER = ":";
  
  private List list = null; 

	private int increment = 1;
  
  private int quantity = 1;
  
  private int lastIndex = -1;
  
  private int firstIndex = -1;
  
  // start index of the current set
  private int firstIndexOfCurrentSet;
  
  // index of the current element
  private int indexOfCurrentElement;
  
  public SetIterator()  {
  }
  
  public SetIterator(List list) {
    this( 0 , (list.size() - 1));
    this.list = list;
  }
  
  public SetIterator(int firstIndex, int lastIndex)  {
    this.firstIndex = firstIndex;
    this.lastIndex = lastIndex;
    this.firstIndexOfCurrentSet = firstIndex;
    this.indexOfCurrentElement = firstIndex - 1;
  }
    
    
    
	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
    return (indexOfCurrentElement < lastIndex);
	}
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
    if (! hasNext())
      throw new NoSuchElementException();
    indexOfCurrentElement++;
    return getElement(indexOfCurrentElement);
	}
  
	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return (indexOfCurrentElement > firstIndex);
	}
	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
    if (! hasPrevious())
      throw new NoSuchElementException();
    indexOfCurrentElement--;
    return getElement(indexOfCurrentElement);
  }
    
	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
    return indexOfCurrentElement + 1;
	}
	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
    return indexOfCurrentElement - 1;
	}
  
  public int currentIndex() {
    return indexOfCurrentElement;
  }
  
  public int currentFirstIndexSet() {
    return ( firstIndexOfCurrentSet < firstIndex) ? firstIndex : firstIndexOfCurrentSet;
  }
  
  public int currentLastIndexSet()  {
    int realLastIndexSet = currentFirstIndexSet() + quantity - 1; 
    return (realLastIndexSet > lastIndex) ? lastIndex : realLastIndexSet;
  }
  
  public void setIncrement(int increment) {
    this.increment = (increment > 0) ? increment : 1;
    // increment has changed..
    adjustFirstIndexOfCurrentSet();
  }
  
  public int getIncrement() {
    return increment;
  }
  
  public void setQuantity(int quantity) {
    this.quantity = (quantity > 0) ? quantity : 1;
  }
  
  public int getQuantity()  {
    return quantity;
  }
    
    
  public void nextSet() {
    if (! hasNextSet())   
      throw new NoSuchElementException("There is not such a subset");
    firstIndexOfCurrentSet += increment;
    currentSet();
    
  }
        
  public void previousSet() {
    if (! hasPreviousSet())
      throw new NoSuchElementException("There is not such a subset");  
    firstIndexOfCurrentSet -= increment;    
    currentSet();
  }

  public void currentSet()  {
    indexOfCurrentElement = (firstIndexOfCurrentSet < firstIndex) ? 
      firstIndex - 1 : firstIndexOfCurrentSet - 1;
  }

  public int size()  {
    return lastIndex - firstIndex + 1;
  }
  
  public int sizeSet()  {
    return currentLastIndexSet() - currentFirstIndexSet() + 1;
  }
    
  
  
  /**
   * Returns true if there is a next set containing at least
   * one element else false
   */
  public boolean hasNextSet() {
    return (firstIndexOfCurrentSet + increment <= lastIndex);
  }
  
  
  /** Returns true if there is a previous set containing at least
   * one element else false
   */
  public boolean hasPreviousSet() {        
    return (firstIndexOfCurrentSet > firstIndex);
  }
    
  /** Returns true if the next element is within the current set 
   *  else false 
   */
  public boolean hasNextInSet() {
    return (hasNext() &&
           (indexOfCurrentElement + 1 < firstIndexOfCurrentSet + quantity) &&
           (indexOfCurrentElement + 1 >= firstIndexOfCurrentSet));    
  }

  /** Returns true if the previous element is within the current set
   * else false
   */  
  public boolean hasPreviousInSet() {
    return (hasPrevious() &&
           (indexOfCurrentElement - 1 >= firstIndexOfCurrentSet)  &&
           (indexOfCurrentElement - 1 < firstIndexOfCurrentSet + quantity));
  }
   
  
  /** Gets current state as string */
  public String getStateAsString()  {
    return storeStateIntoString();
  }  
  
  
  /** Gets current state as hidden input  
   */
  public HiddenInput getStateAsHiddenInput(int id) {
    return new HiddenInput(SET_ITERATOR_STATE_KEY + Integer.toString(id), storeStateIntoString());
  }
  
  public void storeStateInSession(IWUserContext iwuc, String listId, String id) {
    iwuc.setSessionAttribute(SET_ITERATOR_STATE_KEY + id, storeStateIntoString());
    iwuc.setSessionAttribute(SET_ITERATOR_LIST_ID_KEY + id, listId);
  }
  
  
  public boolean retrieveStateFromRequest(IWContext iwc, int id) {
    String key = SET_ITERATOR_STATE_KEY + Integer.toString(id);
    String stateString;
      if (iwc.isParameterSet(key)) 
      stateString = iwc.getParameter(key);
    else
      return false;
    return retrieveStateFromString(stateString);  
  }
 
  
  public boolean retrieveStateFromSession(IWUserContext iwuc, String listId, String id)  {
    String stateString = (String) iwuc.getSessionAttribute(SET_ITERATOR_STATE_KEY + id);
    String listIdString = (String) iwuc.getSessionAttribute(SET_ITERATOR_LIST_ID_KEY + id);
    if (stateString == null || listIdString == null || (! listIdString.equals(listId))) {
      if (stateString != null)
        iwuc.removeSessionAttribute(SET_ITERATOR_STATE_KEY + id);
      if (listIdString != null)
        iwuc.removeSessionAttribute(SET_ITERATOR_LIST_ID_KEY + id);
      return false;  
    }
    return retrieveStateFromString(stateString);             
  }

  
   
	/**
   * Not supported.
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
    throw new UnsupportedOperationException();
    }

	/**
   * Not supported.
	 * @see java.util.ListIterator#set(Object)
	 */
	public void set(Object arg0) {
    throw new UnsupportedOperationException();
  }
	
  /**
   * Not supported.
	 * @see java.util.ListIterator#add(Object)
	 */
	public void add(Object arg0) {
    throw new UnsupportedOperationException();
	}
  
  private Object getElement(int index) {
    return (list == null) ? new Integer(index) :  list.get(index);
  }   
    

  
  private String storeStateIntoString() {
    StringBuffer stateString = new StringBuffer();
    // if you change this order or add or remove something do the same with the method
    // retrieveStateFromString
    stateString
      .append(indexOfCurrentElement)
      .append(STATE_STRING_DELIMITER)
      .append(firstIndex)
      .append(STATE_STRING_DELIMITER)
      .append(lastIndex)
      .append(STATE_STRING_DELIMITER)
      .append(firstIndexOfCurrentSet)
      .append(STATE_STRING_DELIMITER)    
      .append(increment)
      .append(STATE_STRING_DELIMITER)
      .append(quantity);
    return stateString.toString();  
  }
  
  private boolean retrieveStateFromString(String stateString)  {
    StringTokenizer tokenizer = new StringTokenizer(stateString, STATE_STRING_DELIMITER);
    if (tokenizer.countTokens() != 6)
      return false;
    // if you change this order or add or remove something do the same with the method
    // storeStateIntoString  
    int indexOfCurrentElement = Integer.parseInt(tokenizer.nextToken());  
    int firstIndex = Integer.parseInt(tokenizer.nextToken());
    int lastIndex = Integer.parseInt(tokenizer.nextToken());
    int firstIndexOfCurrentSet = Integer.parseInt(tokenizer.nextToken());
    int increment = Integer.parseInt(tokenizer.nextToken());
    int quantity = Integer.parseInt(tokenizer.nextToken());
    if (increment > 0)
      this.increment = increment;
    if (quantity > 0)
      this.quantity = quantity;
      
    // adjust values (list has changed)
    if (this.firstIndex == firstIndex && this.lastIndex == lastIndex) {
      // list has not changed its size
      this.indexOfCurrentElement = indexOfCurrentElement;
      this.firstIndexOfCurrentSet = firstIndexOfCurrentSet;
      return true;
    }
    
    // list has changed, repair it....
    int diff = firstIndex - this.firstIndex;
    firstIndexOfCurrentSet += diff;
    // is the new value within the range?
    if (firstIndexOfCurrentSet > this.lastIndex)  {
      this.firstIndexOfCurrentSet = lastIndex;
    }
    else  {
      this.firstIndexOfCurrentSet = firstIndexOfCurrentSet;
    }
    // now adjust the current set
    adjustFirstIndexOfCurrentSet();
    return true;  
  }
  
  private void adjustFirstIndexOfCurrentSet()  {
    int size = size();
    
    if (size == 0)  {
      // list is empty (compare constructor)
      firstIndexOfCurrentSet = firstIndex;
      indexOfCurrentElement = firstIndex - 1;
      return;
    }
          
    int position = firstIndexOfCurrentSet - firstIndex + 1;

    // positionFromZero is now at least 1
    int division = position / increment;
    int mod = position % increment;
    if (mod == 0)
      division -= 1;
    firstIndexOfCurrentSet = division * increment;
    currentSet();
    return;  
  }
}