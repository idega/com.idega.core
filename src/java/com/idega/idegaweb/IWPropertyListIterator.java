package com.idega.idegaweb;

import java.util.Iterator;

/**
 * <p>
 * This class is used by IWPropertyList to iterate over properties or
 * key tags under a map tag.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2005/11/18 14:47:06 $ by $Author: tryggvil $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class IWPropertyListIterator implements Iterator {

  private Iterator innerIterator;
  private IWPropertyList list;
  private String currentKey;

  public IWPropertyListIterator(Iterator keyIterator,IWPropertyList list){
    this.innerIterator=keyIterator;
    this.list=list;
  }

  public boolean hasNext() {
    return innerIterator.hasNext();
  }

  public Object next() {
    return nextProperty();
  }

  public void remove(){
    if(currentKey!=null){
      list.removeProperty(currentKey);
    }
  }

  public IWProperty nextProperty(){
    String key = (String)innerIterator.next();
    currentKey=key;
    return list.getIWProperty(key);
  }

}
