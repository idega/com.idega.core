package com.idega.data;

import java.util.ListIterator;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IDOEntityIterator implements ListIterator {

  private ListIterator _entities;

  private IDOEntityIterator() {
  }

  public IDOEntityIterator( ListIterator entities) {
    _entities = entities;
  }

  public boolean hasNext() {
    return _entities.hasNext();
  }

  public Object next() {
      return _entities.next();
  }

  public void remove() {
    _entities.remove();
  }

  public boolean hasPrevious() {
    return _entities.hasPrevious();
  }
  public Object previous() {
    return _entities.previous();
  }
  public int nextIndex() {
    return _entities.nextIndex();
  }
  public int previousIndex() {
    return _entities.previousIndex();
  }
  public void set(Object o) {
    throw new java.lang.UnsupportedOperationException("Method set() not yet implemented.");
  }
  public void add(Object o) {
    throw new java.lang.UnsupportedOperationException("Method add() not yet implemented.");
  }

}