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

public class IDOEntityList<T> implements List<T> {

  private IDOPrimaryKeyList _pkLists;

  @SuppressWarnings("unused")
	private IDOEntityList(){
  }

  public IDOEntityList(Collection<?> idoPrimaryKeyList) {
    this._pkLists = (IDOPrimaryKeyList)idoPrimaryKeyList;
  }

  @Override
  public int size() {
    return this._pkLists.size();
  }
  @Override
  public boolean isEmpty() {
    return this._pkLists.isEmpty();
  }
  @Override
  public Iterator<T> iterator() {
    return new IDOEntityIterator(this);
  }

  @Override
  public void clear() {
    this._pkLists.clear();
  }

  @Override
	public boolean equals(Object o) {
    if(o instanceof IDOEntityList){
      return this._pkLists.equals(((IDOEntityList) o)._pkLists);
    }
    return false;
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex){
    return new IDOEntityList<T>(this._pkLists.subList(fromIndex,toIndex));
  }

  @Override
  public T get(int index) {
      Object o = this._pkLists.getIDOEntity(index);
      return (T) o;
  }

  @Override
  public T remove(int index) {
    Object o = this._pkLists.remove(index);
    return (T) o;
  }

  @Override
  public ListIterator<T> listIterator() {
    return new IDOEntityIterator(this);
  }
  @Override
public ListIterator<T> listIterator(int index) {
    return new IDOEntityIterator(this);
  }

  @Override
public boolean contains(Object o) {
  	return this._pkLists.containsIDOEntity(o);
  }

  @Override
public Object[] toArray() {
  	return this._pkLists.toIDOEntityArray();
  }

  @Override
  public Object[] toArray(Object[] a) {
  	Object[] array = this._pkLists.toIDOEntityArray(a);
  	return array;
  }

  @Override
public boolean add(Object o) {
  	if (o instanceof IDOEntity) {
			return this._pkLists.add(o);
		}
		else {
			throw new RuntimeException(this.getClass()+": element is not IDOEntity");
		}

  }

  @Override
public boolean remove(Object o) {
  	return this._pkLists.remove(o);
  }

  @Override
public boolean containsAll(Collection c) {
  	return this._pkLists.containsAll(c);
  }

  @Override
public boolean addAll(Collection c) {
  	return this._pkLists.addAll(c);
  }

  @Override
public boolean addAll(int index, Collection c) {
  	return this._pkLists.addAll(index,c);
  }

  @Override
public boolean removeAll(Collection c) {
  	return this._pkLists.removeAll(c);
  }

  @Override
public boolean retainAll(Collection c) {
  	return this._pkLists.retainAll(c);
  }

  @Override
public T set(int index, Object element) {
  	if (element instanceof IDOEntity) {
			Object o = this._pkLists.set(index, element);
			return (T) o;
		}
		else {
			throw new RuntimeException(this.getClass()+": element is not IDOEntity");
		}
  }

  @Override
public void add(int index, Object element) {
  	if (element instanceof IDOEntity) {
			this._pkLists.add(index, element);
		}
		else {
			throw new RuntimeException(this.getClass()+": element is not IDOEntity");
		}
  }

  @Override
public int indexOf(Object o) {
	 	if (o instanceof IDOEntity) {
			return this._pkLists.indexOf(o);
		}
		else {
			throw new RuntimeException(this.getClass()+": element is not IDOEntity");
		}
  }

  @Override
public int lastIndexOf(Object o) {
  	if (o instanceof IDOEntity) {
			return this._pkLists.lastIndexOf(o);
		}
		else {
			throw new RuntimeException(this.getClass()+": element is not IDOEntity");
		}
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

  public class IDOEntityIterator implements ListIterator<T> {

    private IDOEntityList<T> _list;
	private int _index=0;
	private boolean _hasPrevious=false;

    @SuppressWarnings("unused")
		private IDOEntityIterator() {
    }

    public IDOEntityIterator(IDOEntityList<T> entities) {
    	this._list = entities;
    }

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this._list.size()>this._index;
	}

	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious() {
		return this._hasPrevious;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		T o = this._list.get(nextIndex());
		this._index++;
		this._hasPrevious=true;
		return o;
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex() {
		return this._index;
	}

	/**
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public T previous() {
		T o = this._list.get(previousIndex());
		this._index=this._index-1;
		if(this._index==0){
			this._hasPrevious=false;
		}
		return o;
	}

	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex() {
		return this._index-1;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		this._list.remove(previousIndex());
	}

    @Override
	public void set(Object o) {
    		if (o instanceof IDOEntity) {
					this._list.set(this._index-1,o);
				}
				else {
					throw new RuntimeException(this.getClass()+": element is not IDOEntity");
				}
    }
    @Override
	public void add(Object o) {
    		if (o instanceof IDOEntity) {
					this._list.add(this._index,o);
				}
				else {
					throw new RuntimeException(this.getClass()+": element is not IDOEntity");
				}
    }

  }

}