package com.idega.data;

import java.sql.*;
import java.util.*;

import javax.ejb.FinderException;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IDOPrimaryKeyList implements List, Runnable {

	private IDOQuery _sqlQuery;
	private IDOQuery _countQuery;
//	private Statement _Stmt;
//	private ResultSet _RS;
	private GenericEntity _entity;
	private int _size;
	private int _cursor = 0;
	private Vector _PKs=null;
	private LoadTracker _tracker;
	private int fetchSize = 1;
	private int _prefetchSize=100;
	private boolean isSublist = false;
	private boolean _initialized = false;
	

	private IDOPrimaryKeyList() {
	}

	private IDOPrimaryKeyList(Vector sublist) {
		_PKs = sublist;
		isSublist = true;
	}

//	private void reloadResultSet(){
//
//	}

	private void debugPKs(){
	    System.out.println("[IDOPrimaryKeyList]: _PKs content ->");
		System.out.println("Index\tObject");
		System.out.println("-----\t------");
		ListIterator iter = _PKs.listIterator();
		int count=0;
		boolean showNulls = true;
		while (iter.hasNext()) {
			int index = iter.nextIndex();
			Object item = iter.next();
			if(item != null || showNulls){
				System.out.println(index+"\t"+item);
				if(item != null){
				    showNulls = true;
				} else {
				   showNulls = false;
				   count++;
				}
			} else {
			    count++;
			}
		}
		System.out.println("Number of null objects: "+count);
		System.out.println("[IDOPrimaryKeyList]: _PKs content ends");
	}

	public IDOPrimaryKeyList(IDOQuery sqlQuery,IDOQuery countQuery, GenericEntity entity, int prefetchSize) {
		_sqlQuery = sqlQuery;
		_countQuery = countQuery;
//		_Stmt = Stmt;
//		_RS = RS;
		_entity = entity;
		_prefetchSize = prefetchSize;
		_initialized=false;
    }
	
	public IDOPrimaryKeyList(IDOQuery sqlQuery, GenericEntity entity, int prefetchSize) {
		this(sqlQuery,((IDOQuery)sqlQuery.clone()).setToCount(),entity,prefetchSize);
    }
	
	
	List getListOfEntities() {
		return _PKs;
	}

	private void loadInBackground(){
	    Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void run() {
//		int loadIntervalSize = fetchSize;
//		if(_size < loadIntervalSize){
			try {
				if(!_initialized){
					System.err.println("["+this.getClass().getName()+"]: The size has not been initialized.  It might cause some trouble");
					loadSubset(0,100);
				} else {
					loadSubset(0,_size);
				}
			}
			catch (Exception ex) {
				System.err.println("["+this.getClass()+"]: Exeption: "+ex.getClass()+" occured while executing: "+_sqlQuery);
			}
//		} else {
//			try{
//				int numberOfSubsets = _size/loadIntervalSize + ((_size%loadIntervalSize > 0)?1:0);
//				for (int i = 0; i < loadIntervalSize; i++) {
//					loadSubset(i*loadIntervalSize, (i+1)*loadIntervalSize);
//				}
//			}
//			catch (Exception ex) {
//				System.err.println("["+this.getClass()+"]: Exeption: "+ex.getClass()+" occured while executing: "+_sqlQuery);
//			}
//		}

	}

	/**
	 * @param fromIndex low endpoint (inclusive).
	 * @param toIndex high endpoint (exclusive).
	 */
	private void loadSubset(int fromIndex, int toIndex) throws IDOFinderException
	{
		List setsToLoad;
		// check for changes in database, should not be necessary
//		if(_entity.idoGetNumberOfRecords(_countQuery) != _size){
//		    _PKs = new Vector(_size);
//		    _PKs.setSize(size);
//			_tracker = new LoadTracker(_size,fetchSize);
//		}

		if(_tracker == null){
			int [] interval = new int[2];
			interval[LoadTracker.FROM_INDEX_IN_ARRAY] = fromIndex;
			interval[LoadTracker.TO_INDEX_IN_ARRAY] = toIndex;
			setsToLoad = new Vector();
			setsToLoad.add(interval);
		} else {
			setsToLoad = _tracker.getNotLoadedSubsets(fromIndex, toIndex);
		}
		//assume that setsToLoad is sorted list (lower intervals to higher)

		if (_entity.isDebugActive())
		{
			_entity.debug("[IDOPrimaryKeyList]: Going to Datastore for SQL query: " + _sqlQuery);
			_entity.debug("[IDOPrimaryKeyList]: getting PKs from "+fromIndex+" to "+toIndex);
//			System.out.println("[IDOPrimaryKeyList]: setsToLoad ->");
//			Iterator iter = setsToLoad.iterator();
//			while (iter.hasNext()) {
//				int[] item = (int[])iter.next();
//				System.out.println("From: "+item[LoadTracker.FROM_INDEX_IN_ARRAY]+"\tTo:"+item[LoadTracker.TO_INDEX_IN_ARRAY]);
//			}

		}


		if(setsToLoad != null && setsToLoad.size() > 0)
		{
			Connection conn = null;
			Statement Stmt = null;
			try
			{
				
				conn = _entity.getConnection(_entity.getDatasource());
			
				DatastoreInterface iface = DatastoreInterface.getInstance(_entity);
				
				if(iface.isCabableOfRSScroll()){
//					JDBC 2.0
					Stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
				} else {
//					JDBC 1.0
					Stmt = conn.createStatement();
				}
				
				
								
				
//				JDBC 2.0
				//ResultSet RS = Stmt.executeQuery(_sqlQuery);
//				_size = RS.size(); // not possible yet

				ResultSet RS = null;
				int tmpSize=-1;
				if(iface.isCabableOfRSScroll()){
					//JDBC 1.0
					RS=Stmt.executeQuery(_sqlQuery.toString());
					RS.last();
					tmpSize=RS.getRow();
					RS.beforeFirst();
					
					if(_size!=tmpSize&&_initialized){
						System.err.println("[WARNING]: IDOPrimaryKey: data has changed since last partition was loaded");
					}
				} else{ 
					if(!_initialized){
						try {
							if (_entity.isDebugActive())
							{
								_entity.debug("[IDOPrimaryKeyList]: Going to Datastore for SQL count-query: " + _countQuery);
							}
							Object result =iface.executeQuery(_entity,_countQuery.toString());
							if(result != null && result instanceof Integer){
								tmpSize = ((Integer)result).intValue();
							}
						} catch (Exception e) {
							tmpSize=-1;
							e.printStackTrace();
						}
					}
					RS=Stmt.executeQuery(_sqlQuery.toString());
				}
					
				if(!_initialized){
					_size=tmpSize;
					_PKs = new Vector(_size);
					_PKs.setSize(_size);
					//FIXME What if someone adds to the list?? the size must be updated
					_tracker = new LoadTracker(_size,fetchSize);
					_initialized=true;
				}
				
				
				
				ListIterator iter = setsToLoad.listIterator();
				
				
				//System.out.println("EIKI DEBUG in idoprimarykeylist: "+_sqlQuery.toString());

				int RSpos = -1;
				while (iter.hasNext())
				{
					//int i = iter.nextIndex();
					int[] item = (int[])iter.next();
					int fIndex = item[LoadTracker.FROM_INDEX_IN_ARRAY];
//					int tIndex = Math.min(item[LoadTracker.TO_INDEX_IN_ARRAY],_size);
				    int tIndex = item[LoadTracker.TO_INDEX_IN_ARRAY];

					//JDBC 2.0
//					RS.absolute(item[LoadTracker.FROM_INDEX_IN_ARRAY]);

				    if (_entity.isDebugActive())
					{
						_entity.debug("[IDOPrimaryKeyList]: getting "+fIndex+" to "+tIndex);
					}


				    while((RSpos+1) < fromIndex) {
						if(!RS.next())
						{
						   RSpos =  fromIndex;
						   break;
						}
						RSpos++;  // RS.next()
					}
					//

					//while((RSpos+1) <= tIndex)
					while((RSpos+1) < tIndex)
					{
						if(!RS.next())
						{
							RSpos++;
						    break;
						}
						RSpos++;  // RS.next()
						Object pk = _entity.getPrimaryKeyFromResultSet(RS);
						if (pk != null)
						{
							//Integer pk = new Integer(id);
							
							try {
								_PKs.set(RSpos,_entity.prefetchBeanFromResultSet(pk, RS,_entity.getDatasource()));
							} catch (FinderException e) {
								e.printStackTrace();
							}
						}
					}
					_tracker.addLoadedSubSet(fIndex,tIndex);


				}
				//JDBC 1.0
				RS.close();
//				for (int i = 0; i < setsToLoad.length; i++)
//				{
//					int fIndex = setsToLoad[i][LoadTracker.FROM_INDEX_IN_ARRAY];
//
//					//JDBC 2.0
					//RS.absolute(setsToLoad[i][LoadTracker.FROM_INDEX_IN_ARRAY]);
//
//				    if (_entity.isDebugActive())
//					{
//						_entity.debug("[IDOPrimaryKeyList]: Going to Datastore for SQL query: " + _sqlQuery);
//						_entity.debug("[IDOPrimaryKeyList]: getting "+fIndex+" to "+tIndex);
//					}
//				    //JDBC 1.0
//				    ResultSet RS = Stmt.executeQuery(_sqlQuery);
//
//				    for (int k = 0; k < fromIndex; k++) {
//						RS.next();
//					}
//					//
//
//					for (int j = fromIndex; j < tIndex; j++)
//					{
//						if(!RS.next())
//						{
//						    break;
//						}
//						Object pk = _entity.getPrimaryKeyFromResultSet(RS);
//						if (pk != null)
//						{
//							//Integer pk = new Integer(id);
//							_entity.prefetchBeanFromResultSet(pk, RS);
//							_PKs.set(j,pk);
//						}
//					}
//					_tracker.addLoadedSubSet(setsToLoad[i][LoadTracker.FROM_INDEX_IN_ARRAY],tIndex);
//					//JDBC 1.0
//					RS.close();
//				}
				//JDBC 2.0
//				RS.close();
			}
			catch (SQLException sqle)
			{
				sqle.printStackTrace();
				throw new IDOFinderException(sqle);
			}
			finally
			{
				if (Stmt != null)
				{
					try
					{
						Stmt.close();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
				if (conn != null)
				{
					_entity.freeConnection(_entity.getDatasource(), conn);
				}
			}
		}
//
//		if(_entity.isDebugActive()){
//		    debugPKs();
//		}
	}

//    private void freeConnection(){
//		try {
//			if(_RS != null){
//				_RS.close();
//			}
//			if (_Stmt != null)
//			{
//				Connection conn = _Stmt.getConnection();
//				try
//				{
//					_Stmt.close();
//				}
//				catch (SQLException e)
//				{
//					e.printStackTrace();
//				}
//				if (conn != null)
//				{
//					_entity.freeConnection(_entity.getDatasource(), conn);
//				}
//			}
//		}
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	public void finalize() throws Throwable {
//		freeConnection();
//		super.finalize();
//	}

	public int size() {
		if(!_initialized){
			try {
				loadSubset(0,_prefetchSize);
			} catch (IDOFinderException e) {
				e.printStackTrace();
				return 0;
			}
		}
	    return _size;
	}
	public boolean isEmpty() {
		if(!_initialized){
			try {
				loadSubset(0,_prefetchSize);
			} catch (IDOFinderException e) {
				e.printStackTrace();
				return true;
			}
		}
	    return _size == 0;
	}
	public void clear() {
		_size = 0;
		_PKs.clear();
		_tracker = new LoadTracker(_size,fetchSize);
		//_initialized=false;
//		try {
//			_RS.close();
//		}
//		catch (SQLException ex) {
//			System.err.println("[Warning]: "+this.getClass()+".clear() - "+ResultSet.class+".close() failed");
//		}
	}

  public Iterator iterator() {
	/*try
	{
		this.loadSubset(0,_size);
	}
	catch (Exception ex)
	{
		ex.printStackTrace();
	}
	return _PKs.iterator();*/
		return listIterator();
  }

  public ListIterator listIterator() {
	/*try
	{
		this.loadSubset(0,_size);
	}
	catch (Exception ex)
	{
		ex.printStackTrace();
	}
	return _PKs.listIterator();*/
		return listIterator(0);
  }

  public ListIterator listIterator(int index) {
	/*try
	{
		this.loadSubset(index,_size);
	}
	catch (Exception ex)
	{
		ex.printStackTrace();
	}
	return _PKs.listIterator(index);*/
		return new IDOPrimaryKeyListIterator(this,index);
  }

	/**
	 * Returns a view of the portion of this List between fromIndex,
	 * inclusive, and toIndex, exclusive.  (If fromIndex and ToIndex are
	 * equal, the returned List is empty.)  The returned List is backed by this
	 * List, so changes in the returned List are reflected in this List, and
	 * vice-versa.
	 *
	 * @param fromIndex low endpoint (inclusive) of the subList.
	 * @param toIndex high endpoint (exclusive) of the subList.
	 * @return a view of the specified range within this List.
	 * @throws IndexOutOfBoundsException endpoint index value out of range
	 *         <code>(fromIndex &lt; 0 || toIndex &gt; size)</code>
	 * @throws IllegalArgumentException endpoint indices out of order
	 *	       <code>(fromIndex &gt; toIndex)</code>
	 */
	public List subList(int fromIndex, int toIndex) {
		try
		{
			this.loadSubset(fromIndex,toIndex);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return new IDOPrimaryKeyList((Vector)_PKs.subList(fromIndex, toIndex));
	}


  public boolean equals(Object o) {
    /**@todo: Implement this java.util.List method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }
  /**
   * fetches primary keys
   */
  public Object get(int index) {
    Object obj = _PKs.get(index);
	if(obj == null){
		try{
			loadSubset(index,index+_prefetchSize);
			return ((IDOEntity)_PKs.get(index)).getPrimaryKey();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		return null;//otherwise we get a loop
	}else {
		return ((IDOEntity)obj).getPrimaryKey();
	}
	
  }
  
  Object getIDOEntity(int index) {
    Object obj = _PKs.get(index);
	if(obj == null){
		try{
			loadSubset(index,index+_prefetchSize);
			return _PKs.get(index);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		return null;//otherwise we get a loop
	}
	else {
		return obj;
	}
  }
  
  public Object remove(int index) {
    /**@todo: Implement this java.util.List method*/
    throw new java.lang.UnsupportedOperationException("Method remove() not yet implemented.");
  }

  public boolean remove(Object o) {
    throw new java.lang.UnsupportedOperationException("Method remove() not yet implemented.");
  }
  public boolean contains(Object o) {
    throw new java.lang.UnsupportedOperationException("Method contains() not yet implemented.");
  }
  public Object[] toArray() {
    throw new java.lang.UnsupportedOperationException("Method toArray() not yet implemented.");
  }
  public Object[] toArray(Object[] a) {
    throw new java.lang.UnsupportedOperationException("Method toArray() not yet implemented.");
  }
  public boolean add(Object o) {
/*
TODO implement so that all the collection is loaded first if this method is called
  	boolean success = _PKs.add(o);
  	if(success) {
  		_size++;	
  	}
  	return success;
*/
    throw new java.lang.UnsupportedOperationException("Method add() not yet implemented.");
  }
  public boolean containsAll(Collection c) {
    throw new java.lang.UnsupportedOperationException("Method containsAll() not yet implemented.");
  }
  public boolean addAll(Collection c) {
    //throw new java.lang.UnsupportedOperationException("Method addAll() not yet implemented.");
   /* boolean success = _PKs.addAll(c);
	
  	if(success) {
  		_size+=c.size();	
  	}
  	
  	return success;
  */
  //todo see add(obj)
      throw new java.lang.UnsupportedOperationException("Method addAll() not yet implemented.");
  }
  public boolean addAll(int index, Collection c) {
    throw new java.lang.UnsupportedOperationException("Method addAll(index,collection) not implemented because you can only add to the end of the collection. use addAll(c) instead");
    //return _PKs.addAll(index,c);
  }
  public boolean removeAll(Collection c) {
    throw new java.lang.UnsupportedOperationException("Method removeAll() not yet implemented.");
  }
  public boolean retainAll(Collection c) {
    throw new java.lang.UnsupportedOperationException("Method retainAll() not yet implemented.");
  }
  public Object set(int index, Object element) {
    throw new java.lang.UnsupportedOperationException("Method set() not yet implemented.");
  }
  public void add(int index, Object element) {
    throw new java.lang.UnsupportedOperationException("Method add() not not implemented because you can only add to the end of the collection. use add(obj) instead (not implemented either yet)");
    //_PKs.add(index,element);
  }
  public int indexOf(Object o) {
    throw new java.lang.UnsupportedOperationException("Method indexOf() not yet implemented.");
  }
  public int lastIndexOf(Object o) {
    throw new java.lang.UnsupportedOperationException("Method lastIndexOf() not yet implemented.");
  }







	public class LoadTracker {
//		int initialCapacity;
		private int _subsetMinLength = 100;
		private int _capacityIncrement;
		private Vector _loadedSubSets =  new Vector();
//		private int _numberOfSubsets = 0;
		public static final int FROM_INDEX_IN_ARRAY = 0;
		public static final int TO_INDEX_IN_ARRAY = 1;
		private int _size;
		private IntervalComparator _comparator = new IntervalComparator();


		public LoadTracker(int size, int subsetMinLength){
		    this._size = size;
			this._subsetMinLength = subsetMinLength;
		}

		private void debugLoadedSubSets(){
	    System.out.println("[IDOPrimaryKeyList]: _loadedSubSets content ->");
		System.out.println("From\tTo");
		System.out.println("-----\t------");
		Iterator iter = _loadedSubSets.iterator();
		boolean showNulls = true;
		while (iter.hasNext()) {
			int[] item = (int[])iter.next();
			if(item != null || showNulls){
				System.out.println(item[0]+"\t"+item[1]);
			}
		}
		System.out.println("[IDOPrimaryKeyList]: _loadedSubSets content ends");
	}

		public float getLoadRatio(){
		    if(_size != 0){
				int NumberOfLoadedElements = 0;
				ListIterator iter = _loadedSubSets.listIterator();
				while (iter.hasNext()) {
					//int index = iter.nextIndex();
					int[] item = (int[])iter.next();
					NumberOfLoadedElements += item[TO_INDEX_IN_ARRAY] - item[FROM_INDEX_IN_ARRAY];
				}
				return NumberOfLoadedElements/_size;
			} else {
			    return 1;
			}
		}

//		private void increaseCapacity(){
//			int newCapacity = _loadedSubSets.length + _capacityIncrement;
//			int oldCapacity = _loadedSubSets.length;
//			int[][] newObject = new int[newCapacity][2];
//		    System.arraycopy(_loadedSubSets,0,newObject,0,oldCapacity);
//			_loadedSubSets = newObject;
//		}


		/**
		 * @param fromIndex low endpoint (inclusive).
		 * @param toIndex high endpoint (exclusive).
		 */
		private boolean isLoaded(int fromIndex, int toIndex){
//		    int fIndex = Math.min(fromIndex,toIndex);
		    int fIndex = fromIndex;
//			fIndex = Math.min(fIndex,_size);
		    if(fromIndex > _size){
			    return false;
			}
//			int tIndex = Math.max(fromIndex,toIndex);
		    int tIndex = toIndex;
			tIndex = Math.min(tIndex,_size);
			Iterator iter = _loadedSubSets.iterator();
			while (iter.hasNext()) {
				int[] item = (int[])iter.next();
				if(item[FROM_INDEX_IN_ARRAY] <= fIndex && item[TO_INDEX_IN_ARRAY] >= tIndex){
				    return true;
				}
				if(item[FROM_INDEX_IN_ARRAY] > tIndex+1 ){
				    break;
				}
			}
			return false;
		}

		/**
		 * @param fromIndex low endpoint (inclusive).
		 * @param toIndex high endpoint (exclusive).
		 */
		public List getNotLoadedSubsets(int fromIndex, int toIndex){
//			if (_entity.isDebugActive()){
//			    System.out.println("[IDOPrimaryKeyList]: _size = "+_size);
//				debugLoadedSubSets();
//			}
			List toReturn = new Vector();
			int fIndex = Math.min(fromIndex,toIndex);
			fIndex = Math.min(fIndex,_size);
			int tIndex = Math.max(fromIndex,toIndex);
//			if (_entity.isDebugActive()){
//				System.out.println("[IDOPrimaryKeyList]: 1");
//				System.out.println("[IDOPrimaryKeyList]: getNotLoadedSubsets("+fIndex+","+tIndex+")");
//				System.out.println("[IDOPrimaryKeyList]: isLoaded("+fromIndex+","+toIndex+") = "+isLoaded(fromIndex,toIndex));
//
//			}
			if(isLoaded(fromIndex,toIndex)){
			    return toReturn;
			}
			tIndex = Math.max(tIndex,fromIndex+_subsetMinLength);
			tIndex = Math.min(tIndex,_size);
			if(_loadedSubSets.size() == 0){
				int [] interval = new int[2];
				interval[FROM_INDEX_IN_ARRAY] = fIndex;
				interval[TO_INDEX_IN_ARRAY] = tIndex;
//				if (_entity.isDebugActive()){
//					System.out.println("[IDOPrimaryKeyList]: 2");
//					System.out.println("[IDOPrimaryKeyList]: getNotLoadedSubsets("+fIndex+","+tIndex+")");
//				}
				toReturn.add(interval);
				return toReturn;
			} else {
				ListIterator iter = _loadedSubSets.listIterator();
				while (iter.hasNext()) {
					int index = iter.nextIndex();
					int[] item = (int[])iter.next();


					if(( fIndex >= item[TO_INDEX_IN_ARRAY] && !iter.hasNext() )||(tIndex <= item[FROM_INDEX_IN_ARRAY] && (index != 0 && ((int[])_loadedSubSets.get(index-1))[TO_INDEX_IN_ARRAY] <= fIndex))){  // interval is not part of any other interval
//						if(index == 0){
							int [] interval = new int[2];
							interval[FROM_INDEX_IN_ARRAY] = fIndex;
							interval[TO_INDEX_IN_ARRAY] = Math.min(tIndex,_size);
//							if (_entity.isDebugActive()){
//								System.out.println("[IDOPrimaryKeyList]: 3");
//								System.out.println("[IDOPrimaryKeyList]: getNotLoadedSubsets("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//							}
							toReturn.add(interval);
//						}
					    break;
					}

					if(fIndex < item[FROM_INDEX_IN_ARRAY] && tIndex >= item[FROM_INDEX_IN_ARRAY]){  // interval begins before item's interval and covers at least some of it
					    int [] interval = new int[2];
						interval[FROM_INDEX_IN_ARRAY] = fIndex;
						int min = Math.min(tIndex,item[FROM_INDEX_IN_ARRAY]);
						interval[TO_INDEX_IN_ARRAY] = min;
//						if(_loadedSubSets.size() > index+1){
//						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])_loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
//						} else {
//						    interval[TO_INDEX_IN_ARRAY] = min;
//						}
//						if (_entity.isDebugActive()){
//							System.out.println("[IDOPrimaryKeyList]: 4");
//							System.out.println("[IDOPrimaryKeyList]: getNotLoadedSubsets("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						toReturn.add(interval);
					}

					if(fIndex < item[TO_INDEX_IN_ARRAY] && tIndex > item[TO_INDEX_IN_ARRAY] ){ // interval outreaches item's interval
						int [] interval = new int[2];
						interval[FROM_INDEX_IN_ARRAY] = item[TO_INDEX_IN_ARRAY];
						int min = Math.min(tIndex,_size);
						if(_loadedSubSets.size() > index+1){
						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])_loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
						} else {
						    interval[TO_INDEX_IN_ARRAY] = min;
						}
//						if (_entity.isDebugActive()){
//							System.out.println("[IDOPrimaryKeyList]: 5");
//							System.out.println("[IDOPrimaryKeyList]: getNotLoadedSubsets("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						toReturn.add(interval);
					}
				}

//				Collections.sort(toReturn,_comparator);
				return toReturn;
			}
		}



		/**
		 * @param fromIndex low endpoint (inclusive).
		 * @param toIndex high endpoint (exclusive).
		 */
		public void addLoadedSubSet(int fromIndex, int toIndex){
			if (_entity.isDebugActive()){
			    System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+fromIndex+","+toIndex+") -> begins");
				debugLoadedSubSets();
			}

			int fIndex = Math.min(fromIndex,toIndex);
			fIndex = Math.min(fIndex,_size);
			int tIndex = Math.max(fromIndex,toIndex);
//			if (_entity.isDebugActive()){
//				System.out.println("[IDOPrimaryKeyList]: 1");
//				System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+fIndex+","+tIndex+")");
//			}
//			if(isLoaded(fromIndex,toIndex)){
//			    return toReturn;
//			}
			tIndex = Math.max(tIndex,fromIndex+_subsetMinLength);
			tIndex = Math.min(tIndex,_size);
			if(_loadedSubSets.size() == 0){
				int [] interval = new int[2];
				interval[FROM_INDEX_IN_ARRAY] = fIndex;
				interval[TO_INDEX_IN_ARRAY] = tIndex;
//				if (_entity.isDebugActive()){
//					System.out.println("[IDOPrimaryKeyList]: 2");
//					System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+fIndex+","+tIndex+")");
//				}
				_loadedSubSets.add(interval);
			} else {
				ListIterator iter = _loadedSubSets.listIterator();
				while (iter.hasNext()) {
					int index = iter.nextIndex();
					int[] item = (int[])iter.next();


					if(( fIndex >= item[TO_INDEX_IN_ARRAY] && !iter.hasNext() )||(tIndex <= item[FROM_INDEX_IN_ARRAY] && (index != 0 && ((int[])_loadedSubSets.get(index-1))[TO_INDEX_IN_ARRAY] <= fIndex))){  // interval is not part of any other interval
//						if(index == 0){
							int [] interval = new int[2];
							interval[FROM_INDEX_IN_ARRAY] = fIndex;
							interval[TO_INDEX_IN_ARRAY] = Math.min(tIndex,_size);
//							if (_entity.isDebugActive()){
//								System.out.println("[IDOPrimaryKeyList]: 3");
//								System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//							}
							iter.add(interval);
//						}
					    break;
					}

					if(fIndex < item[FROM_INDEX_IN_ARRAY] && tIndex >= item[FROM_INDEX_IN_ARRAY]){  // interval begins before item's interval and covers at least some of it
					    int [] interval = new int[2];
						interval[FROM_INDEX_IN_ARRAY] = fIndex;
						int min = Math.min(tIndex,item[FROM_INDEX_IN_ARRAY]);
						interval[TO_INDEX_IN_ARRAY] = min;
//						if(_loadedSubSets.size() > index+1){
//						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])_loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
//						} else {
//						    interval[TO_INDEX_IN_ARRAY] = min;
//						}
//						if (_entity.isDebugActive()){
//							System.out.println("[IDOPrimaryKeyList]: 4");
//							System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						iter.add(interval);
					}

					if(fIndex < item[TO_INDEX_IN_ARRAY] && tIndex > item[TO_INDEX_IN_ARRAY] ){ // interval outreaches item's interval
						int [] interval = new int[2];
						interval[FROM_INDEX_IN_ARRAY] = item[TO_INDEX_IN_ARRAY];
						int min = Math.min(tIndex,_size);
						if(_loadedSubSets.size() > index+1){
						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])_loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
						} else {
						    interval[TO_INDEX_IN_ARRAY] = min;
						}
//						if (_entity.isDebugActive()){
//							System.out.println("[IDOPrimaryKeyList]: 5");
//							System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						iter.add(interval);
					}
				}
				Collections.sort(_loadedSubSets,_comparator);
			}
			if (_entity.isDebugActive()){
				System.out.println("And then");
				debugLoadedSubSets();
				System.out.println("[IDOPrimaryKeyList]: addLoadedSubSet("+fromIndex+","+toIndex+") -> ends");

			}
		}

//		/**
//		 * @param fromIndex low endpoint (inclusive).
//		 * @param toIndex high endpoint (exclusive).
//		 */
//		public void addLoadedSubSet(int fromIndex, int toIndex){
//			int fIndex = Math.min(fromIndex,toIndex);
//			int tIndex = Math.max(fromIndex,toIndex);
//			boolean done = false;
//			if(_loadedSubSets.size() != 0)
//			{
//				Iterator iter = _loadedSubSets.iterator();
//				while (iter.hasNext()) {
					//int index = iter.nextIndex();
//					int[] item = (int[])iter.next();
//					if(item[FROM_INDEX_IN_ARRAY] >= tIndex){
//					    break;
//					}
//					if(item[FROM_INDEX_IN_ARRAY] <= fIndex && item[TO_INDEX_IN_ARRAY] >= fIndex ){
//						// fromIndex is within this subset
//						done = true;
//						if(item[TO_INDEX_IN_ARRAY] < tIndex){
//							item[TO_INDEX_IN_ARRAY] = tIndex;
//							fIndex = item[FROM_INDEX_IN_ARRAY];
//							while (iter.hasNext()) {
								//index = iter.nextIndex();
//								int[] item2 = (int[])iter.next();
//								if(item2[FROM_INDEX_IN_ARRAY] <= item[TO_INDEX_IN_ARRAY]){
//								    if( item2[TO_INDEX_IN_ARRAY] >= item[TO_INDEX_IN_ARRAY]){
//										item[TO_INDEX_IN_ARRAY] = item2[TO_INDEX_IN_ARRAY];
//										iter.remove();
//										break;
//									} else {
//									    iter.remove();
//									}
//								}
//							}
//						} else {
//							break;
//						}
//					} else if(item[FROM_INDEX_IN_ARRAY] >= tIndex && item[TO_INDEX_IN_ARRAY] <= tIndex){
//						// toIndex is within this subset
//						done = true;
//						item[FROM_INDEX_IN_ARRAY] = fIndex;
//						break;
//					}
//				}
//			}
//			//if(_numberOfSubsets == 0 || !done)
//			if(!done)
//			{
//				int[] newInterval = new int[2];
//				newInterval[FROM_INDEX_IN_ARRAY] = fIndex;
//				newInterval[TO_INDEX_IN_ARRAY] = tIndex;
//				_loadedSubSets.add(newInterval);
//				Collections.sort(_loadedSubSets, _comparator);
//			}
//
//		}



		public class IntervalComparator implements Comparator {

			public IntervalComparator() {
			}

			public int compare(Object o1, Object o2) {
				int[] p1 = (int[]) o1;
				int[] p2 = (int[]) o2;
				int result = 0;

				if(!(p1[FROM_INDEX_IN_ARRAY] == p2[FROM_INDEX_IN_ARRAY])){
				    result = (p1[FROM_INDEX_IN_ARRAY] < p2[FROM_INDEX_IN_ARRAY])?-1:1;
				}

				return result;
			}


			public boolean equals(Object obj) {
				/**@todo: Implement this java.util.Comparator method*/
				throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
			}

		}

	}  //LoadTracker Ends




		public class IDOPrimaryKeyListIterator implements ListIterator{
			private int _index;
			private List _list;
			private Object lastObject;
			private boolean _hasPrevious=false;
			
			public IDOPrimaryKeyListIterator(IDOPrimaryKeyList list,int index){
				_list=list;
				_index=index;
			}
			
//					/**
//			 * @see java.util.ListIterator#add(java.lang.Object)
//			 */
//			public void add(Object o) {
//				_list.add(o);
//			}

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

//			/**
//			 * @see java.util.ListIterator#set(java.lang.Object)
//			 */
//			public void set(Object o) {
//				_list.set(previousIndex(),o);
//			}
			
		    public void set(Object o) {
		        throw new java.lang.UnsupportedOperationException("Method set() not yet implemented.");
		      }
		      public void add(Object o) {
		        throw new java.lang.UnsupportedOperationException("Method add() not yet implemented.");
		      }

}





}