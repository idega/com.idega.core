package com.idega.data;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.util.ListUtil;
import java.util.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IDOEntityList implements List {

  private IDOFactory _factory;
  private List _PKs;
  private Collection collectionOfEntities = null;

  private IDOEntityList(){
  }

  public IDOEntityList(IDOFactory factory, Collection PKs) {
    _factory = factory;
    _PKs = ListUtil.convertCollectionToList(PKs);;
  }

  public IDOFactory getFactory(){
    return _factory;
  }

  public int size() {
    return _PKs.size();
  }
  public boolean isEmpty() {
    return _PKs.isEmpty();
  }
  public Iterator iterator() {
    return new IDOEntityIterator(_factory, _PKs.listIterator());
  }

  public void clear() {
    _PKs.clear();
  }
  public boolean equals(Object o) {
    if(o instanceof IDOEntityList){
      return _factory.equals( ((IDOEntityList)o)._factory ) && _PKs.equals(((IDOEntityList)o)._PKs);
    }
    return false;
  }

  public List subList(int fromIndex, int toIndex){
    return new IDOEntityList(_factory,_PKs.subList(fromIndex,toIndex));
  }

  public Object get(int index) {
    try {
      Object pk = _PKs.get(index);
      IDOEntity entityObject = _factory.idoFindByPrimaryKey(pk);
      return entityObject;
    }
    catch (FinderException ex) {
      throw new EJBException(ex);
    }
  }

  public Object remove(int index) {
    return _PKs.remove(index);
  }
  public ListIterator listIterator() {
    return new IDOEntityIterator(_factory, _PKs.listIterator());
  }
  public ListIterator listIterator(int index) {
    return new IDOEntityIterator(_factory, _PKs.listIterator(index));
  }


  public boolean contains(Object o) {
    throw new UnsupportedOperationException("Method contains(Object o) not yet implemented.");
  }
  public Object[] toArray() {
    throw new UnsupportedOperationException("Method toArray() not yet implemented.");
  }
  public Object[] toArray(Object[] a) {
    throw new UnsupportedOperationException("Method toArray(Object[] a) not yet implemented.");
  }
  public boolean add(Object o) {
    throw new UnsupportedOperationException("Method add(Object o) not yet implemented.");
  }
  public boolean remove(Object o) {
    throw new UnsupportedOperationException("Method remove(Object o) not yet implemented.");
  }
  public boolean containsAll(Collection c) {
    throw new UnsupportedOperationException("Method containsAll(Collection c) not yet implemented.");
  }
  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException("Method addAll(Collection c) not yet implemented.");
  }
  public boolean addAll(int index, Collection c) {
    throw new UnsupportedOperationException("Method addAll(int index, Collection c) not yet implemented.");
  }
  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException("Method removeAll(Collection c) not yet implemented.");
  }
  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException("Method retainAll(Collection c) not yet implemented.");
  }


  public Object set(int index, Object element) {
    throw new java.lang.UnsupportedOperationException("Method set() not yet implemented.");
  }
  public void add(int index, Object element) {
    throw new java.lang.UnsupportedOperationException("Method add() not yet implemented.");
  }

  public int indexOf(Object o) {
    throw new java.lang.UnsupportedOperationException("Method indexOf() not yet implemented.");
  }
  public int lastIndexOf(Object o) {
    throw new java.lang.UnsupportedOperationException("Method lastIndexOf() not yet implemented.");
  }

}