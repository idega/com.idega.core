package com.idega.idegaweb;

import java.util.Iterator;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
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
