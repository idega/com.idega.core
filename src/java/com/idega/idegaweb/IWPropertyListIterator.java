package com.idega.idegaweb;

import java.util.Iterator;

/**
 * <p>
 * This class is used by IWPropertyList to iterate over properties or
 * key tags under a map tag.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2006/04/09 12:13:14 $ by $Author: laddi $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.4 $
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
    return this.innerIterator.hasNext();
  }

  public Object next() {
    return nextProperty();
  }

  public void remove(){
    if(this.currentKey!=null){
      this.list.removeProperty(this.currentKey);
    }
  }

  public IWProperty nextProperty(){
    String key = (String)this.innerIterator.next();
    this.currentKey=key;
    return this.list.getIWProperty(key);
  }

}
