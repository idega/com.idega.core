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
//  private Collection collectionOfEntities = null;

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
  	if (o instanceof IDOEntity)
  		return _PKs.contains(((IDOEntity)o).getPrimaryKey());
  	else
  		return _PKs.contains(o);
  }

  public Object[] toArray() {
  	try {
  		IDOEntity[] entities = new IDOEntity[size()];
	  	int i = 0;
	  	for (Iterator iter = _PKs.iterator(); iter.hasNext(); ) {
				Object pk = (Object) iter.next();
				entities[i++] = _factory.idoFindByPrimaryKey(pk);
	  	}
	  	return entities;
		}
  	catch (FinderException ex) {
  		throw new EJBException(ex);
  	}
  }
  
  public Object[] toArray(Object[] a) {
  	try {
  		int i = 0;
  		for (Iterator iter = _PKs.iterator(); iter.hasNext(); ) {
  			Object pk = (Object) iter.next();
  			a[i++] = _factory.idoFindByPrimaryKey(pk);
  		}
  		return a;
  	}
  	catch (FinderException ex) {
  		throw new EJBException(ex);
  	}
  }
  
  public boolean add(Object o) {
  	if (o instanceof IDOEntity)
  		return _PKs.add(((IDOEntity)o).getPrimaryKey());
  	return false;
  }

  public boolean remove(Object o) {
  	if (o instanceof IDOEntity)
  		return _PKs.remove(((IDOEntity)o).getPrimaryKey());
  	else
  		return _PKs.remove(o);
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
  	List PKs = new ArrayList(_PKs);
  	int size = PKs.size();
  	
  	Iterator iter = c.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof IDOEntity)
				PKs.remove(((IDOEntity)element).getPrimaryKey());
			else
				PKs.remove(element);
		}
		_PKs.removeAll(PKs);
		
		return size == _PKs.size();
  }
  
  public Object set(int index, Object element) {
  	if (element instanceof IDOEntity)
  		return _PKs.set(index, ((IDOEntity)element).getPrimaryKey());
  	else
  		return _PKs.set(index, element);
  }

  public void add(int index, Object element) {
  	if (element instanceof IDOEntity)
  		_PKs.add(index, ((IDOEntity)element).getPrimaryKey());
  	else
  		_PKs.add(index, element);
  }

  public int indexOf(Object o) {
	 	if (o instanceof IDOEntity)
	 		return _PKs.indexOf(((IDOEntity)o).getPrimaryKey());
	 	else
	 		return _PKs.indexOf(o);
  }

  public int lastIndexOf(Object o) {
  	if (o instanceof IDOEntity)
  		return _PKs.lastIndexOf(((IDOEntity)o).getPrimaryKey());
  	else
  		return _PKs.lastIndexOf(o);
  }
}