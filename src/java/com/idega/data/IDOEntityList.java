package com.idega.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 2.0
 */

public class IDOEntityList implements List {

  private IDOPrimaryKeyList _pkLists;

  private IDOEntityList(){
  }

  public IDOEntityList(Collection idoPrimaryKeyList) {
    _pkLists = (IDOPrimaryKeyList)idoPrimaryKeyList;
  }


  public int size() {
    return _pkLists.size();
  }
  public boolean isEmpty() {
    return _pkLists.isEmpty();
  }
  public Iterator iterator() {
    return new IDOEntityIterator(this);
  }

  public void clear() {
    _pkLists.clear();
  }
  public boolean equals(Object o) {
    if(o instanceof IDOEntityList){
      return _pkLists.equals(((IDOEntityList)o)._pkLists);
    }
    return false;
  }

  public List subList(int fromIndex, int toIndex){
    return new IDOEntityList(_pkLists.subList(fromIndex,toIndex));
  }

  public Object get(int index) {
      return _pkLists.getIDOEntity(index);
  }

  public Object remove(int index) {
    return _pkLists.remove(index);
  }
  public ListIterator listIterator() {
    return new IDOEntityIterator(this);
  }
  public ListIterator listIterator(int index) {
    return new IDOEntityIterator(this);
  }

  public boolean contains(Object o) {
  	return _pkLists.contains(o);
  }

  public Object[] toArray() {
  	return _pkLists.toIDOEntityArray();
  }
  
  public Object[] toArray(Object[] a) {
  	return _pkLists.toIDOEntityArray(a);
  }
  
  public boolean add(Object o) {
  	if (o instanceof IDOEntity)
  		return _pkLists.add(((IDOEntity)o));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");

  }

  public boolean remove(Object o) {
  	return _pkLists.remove(((IDOEntity)o));
  }

  public boolean containsAll(Collection c) {
  	return _pkLists.containsAll(c);
  }

  public boolean addAll(Collection c) {
  	return _pkLists.addAll(c);
  }

  public boolean addAll(int index, Collection c) {
  	return _pkLists.addAll(index,c);
  }
  
  public boolean removeAll(Collection c) {
  	return _pkLists.removeAll(c);
  }
  
  public boolean retainAll(Collection c) {
  	return _pkLists.removeAll(c);
  }
  
  public Object set(int index, Object element) {
  	if (element instanceof IDOEntity)
  		return _pkLists.set(index, ((IDOEntity)element));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public void add(int index, Object element) {
  	if (element instanceof IDOEntity)
  		_pkLists.add(index, ((IDOEntity)element));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public int indexOf(Object o) {
	 	if (o instanceof IDOEntity)
	 		return _pkLists.indexOf(((IDOEntity)o));
	 	else
	 		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public int lastIndexOf(Object o) {
  	if (o instanceof IDOEntity)
  		return _pkLists.lastIndexOf(((IDOEntity)o));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }
  
  
  public static Collection merge(Collection c1, Collection c2) throws IDOFinderException {
  	if(c1 instanceof IDOEntityList && c2 instanceof IDOEntityList && IDOPrimaryKeyList.areMergeable(((IDOEntityList)c1)._pkLists,((IDOEntityList)c2)._pkLists)){
  		return new IDOEntityList(IDOPrimaryKeyList.merge(((IDOEntityList)c1)._pkLists,((IDOEntityList)c2)._pkLists));
  	} else {
  		ArrayList l = new ArrayList(c1.size()+c2.size());
  		l.addAll(c1);
  		l.addAll(c2);
  		return l;
  	}
  }
  
  
  
  public class IDOEntityIterator implements ListIterator {

    private IDOEntityList _list;
	private int _index=0;
	private Object lastObject;
	private boolean _hasPrevious=false;

    private IDOEntityIterator() {
    }

    public IDOEntityIterator( IDOEntityList entities) {
    	_list = entities;
    }

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return _list.size()>_index;
	}

	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return _hasPrevious;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		Object o =  _list.get(nextIndex());
		_index++;
		_hasPrevious=true;
		return o;
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return _index;
	}

	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		Object o = _list.get(previousIndex());
		_index=_index-1;
		if(_index==0){
			_hasPrevious=false;
		}
		return o;
	}

	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return _index-1;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		_list.remove(previousIndex());
	}

    public void set(Object o) {
    		if (o instanceof IDOEntity)
	 		_list.set(_index-1,((IDOEntity)o));
	 	else
	 		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
    }
    public void add(Object o) {
    		if (o instanceof IDOEntity)
	 		_list.add(_index,((IDOEntity)o));
	 	else
	 		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
    }

  }
  
}