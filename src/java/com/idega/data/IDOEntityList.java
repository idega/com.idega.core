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
 * @version 1.0
 */

public class IDOEntityList implements List {

  private IDOPrimaryKeyList _entities;

  private IDOEntityList(){
  }

  public IDOEntityList(Collection idoPrimaryKeyList) {

    _entities = (IDOPrimaryKeyList)idoPrimaryKeyList;
  }


  public int size() {
    return _entities.size();
  }
  public boolean isEmpty() {
    return _entities.isEmpty();
  }
  public Iterator iterator() {
    return new IDOEntityIterator(_entities.getListOfEntities().listIterator());
  }

  public void clear() {
    _entities.clear();
  }
  public boolean equals(Object o) {
    if(o instanceof IDOEntityList){
      return _entities.equals(((IDOEntityList)o)._entities);
    }
    return false;
  }

  public List subList(int fromIndex, int toIndex){
    return new IDOEntityList(_entities.subList(fromIndex,toIndex));
  }

  public Object get(int index) {
      return _entities.get(index);
  }

  public Object remove(int index) {
    return _entities.remove(index);
  }
  public ListIterator listIterator() {
    return new IDOEntityIterator(_entities.getListOfEntities().listIterator());
  }
  public ListIterator listIterator(int index) {
    return new IDOEntityIterator(_entities.getListOfEntities().listIterator(index));
  }

  public boolean contains(Object o) {
  	if (o instanceof IDOEntity)
  		return _entities.contains(((IDOEntity)o));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public Object[] toArray() {
	IDOEntity[] entities = new IDOEntity[size()];
  	int i = 0;
  	for (Iterator iter = _entities.iterator(); iter.hasNext(); ) {
  		entities[i++] = (IDOEntity)iter.next();
  	}
  	return entities;
  }
  
  public Object[] toArray(Object[] a) {
	int i = 0;
	for (Iterator iter = _entities.iterator(); iter.hasNext(); ) {
		a[i++] =  (Object) iter.next();
	}
	return a;
  }
  
  public boolean add(Object o) {
  	if (o instanceof IDOEntity)
  		return _entities.add(((IDOEntity)o));
  	return false;
  }

  public boolean remove(Object o) {
  	if (o instanceof IDOEntity)
  		return _entities.remove(((IDOEntity)o));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public boolean containsAll(Collection c) {
  	Iterator iter = c.iterator();
		while (iter.hasNext()) {
			Object element = (Object) iter.next();
			if (!contains(element))
				return false;
		}
		return true;
  }

  public boolean addAll(Collection c) {
  	boolean changed = false;
  	Iterator iter = c.iterator();
  	while (iter.hasNext()) {
  		Object element = (Object) iter.next();
			add(element);
			changed = true;
  	}
  	return changed;
  }

  public boolean addAll(int index, Collection c) {
  	boolean changed = false;
  	Iterator iter = c.iterator();
  	while (iter.hasNext()) {
  		Object element = (Object) iter.next();
  		add(index, element);
  		changed = true;
  	}
  	return changed;
  }
  
  public boolean removeAll(Collection c) {
  	boolean changed = false;
  	Iterator iter = c.iterator();
  	while (iter.hasNext()) {
  		Object element = (Object) iter.next();
  		if (contains(element)) {
	  		remove(element);
	  		changed = true;
  		}
  	}
  	return changed;
  }
  
  public boolean retainAll(Collection c) {
  	List entities = new ArrayList(_entities);
  	int size = entities.size();
  	
  	Iterator iter = c.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof IDOEntity)
				entities.remove(((IDOEntity)element));
			else
				throw new RuntimeException(this.getClass()+": element is not IDOEntity");
		}
		_entities.removeAll(entities);
		
		return size == _entities.size();
  }
  
  public Object set(int index, Object element) {
  	if (element instanceof IDOEntity)
  		return _entities.set(index, ((IDOEntity)element));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public void add(int index, Object element) {
  	if (element instanceof IDOEntity)
  		_entities.add(index, ((IDOEntity)element));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public int indexOf(Object o) {
	 	if (o instanceof IDOEntity)
	 		return _entities.indexOf(((IDOEntity)o));
	 	else
	 		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }

  public int lastIndexOf(Object o) {
  	if (o instanceof IDOEntity)
  		return _entities.lastIndexOf(((IDOEntity)o));
  	else
  		throw new RuntimeException(this.getClass()+": element is not IDOEntity");
  }
}