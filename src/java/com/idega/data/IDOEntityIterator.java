package com.idega.data;

import java.util.ListIterator;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import java.util.Iterator;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IDOEntityIterator implements ListIterator {

  private IDOFactory _factory;
  private ListIterator _PKs;

  private IDOEntityIterator() {
  }

  public IDOEntityIterator(IDOFactory factory, ListIterator PKs) {
    _factory = factory;
    _PKs = PKs;
  }

  public boolean hasNext() {
    return _PKs.hasNext();
  }

  public Object next() {
    try {
      Object pk = _PKs.next();
      IDOEntity entityObject = _factory.idoFindByPrimaryKey(pk);
      return entityObject;
    }
    catch (FinderException ex) {
	  ex.printStackTrace();
      throw new EJBException(ex);
    }
  }

  public void remove() {
    _PKs.remove();
  }

  public boolean hasPrevious() {
    return _PKs.hasPrevious();
  }
  public Object previous() {
    try {
      Object pk = _PKs.previous();
      IDOEntity entityObject = _factory.idoFindByPrimaryKey(pk);
      return entityObject;
    }
    catch (FinderException ex) {
      throw new EJBException(ex);
    }
  }
  public int nextIndex() {
    return _PKs.nextIndex();
  }
  public int previousIndex() {
    return _PKs.previousIndex();
  }
  public void set(Object o) {
    throw new java.lang.UnsupportedOperationException("Method set() not yet implemented.");
  }
  public void add(Object o) {
    throw new java.lang.UnsupportedOperationException("Method add() not yet implemented.");
  }

}