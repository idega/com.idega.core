package com.idega.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.data.query.InCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.util.Timer;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 2.0
 */

public class IDOPrimaryKeyList extends Vector implements List, Runnable {

	private SelectQuery _sqlQuery;
	private GenericEntity _entity;
	private Class allowedPrimaryKeyClass; // varaable set in the initialize method where it is fetched ones from the variable _entity for optimizing reasons
	private String pkColumnName;
	private Table sqlQueryTable;
	
	private int _cursor = 0;
	//This Vector contains the loaded IDOEntities but the super list of this object contains list of all primary keys
	private Vector _entities=null;
	private LoadTracker _tracker;
	private int fetchSize = 1;
	private int _prefetchSize=100;
	private boolean isSublist = false;

	

	private IDOPrimaryKeyList() {
	}

//	private void reloadResultSet(){
//
//	}

	private void debugPKs(){
	    System.out.println("[IDOPrimaryKeyList]: _PKs content ->");
		System.out.println("Index\tObject");
		System.out.println("-----\t------");
		ListIterator iter = _entities.listIterator();
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

	public IDOPrimaryKeyList(SelectQuery sqlQuery, GenericEntity entity, int prefetchSize) throws IDOFinderException {
		_sqlQuery = sqlQuery;
//		_Stmt = Stmt;
//		_RS = RS;
		_entity = entity;
		_prefetchSize = prefetchSize;
		initialize();
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
				loadSubset(0,size());
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
	
	
	private void initialize() throws IDOFinderException {
		Timer timer = new Timer();
		timer.start();
		IDOPrimaryKeyDefinition pkDefinition = _entity.getEntityDefinition().getPrimaryKeyDefinition();
		IDOEntityField[] pkFields = pkDefinition.getFields();
		if(pkFields==null || pkFields.length>1){
			throw new UnsupportedOperationException("IDOPrimaryKeyList is capable of handling entities whith one primary key only, this entity: "+_entity.getClass().getName()+" has "+ ((pkFields==null)?"none":String.valueOf(pkFields.length)));
		}
		allowedPrimaryKeyClass = pkDefinition.getPrimaryKeyClass();
		pkColumnName = pkFields[0].getSQLFieldName();
		
		sqlQueryTable = new Table(_entity);
		
		SelectQuery initialQuery = (SelectQuery)_sqlQuery.clone();
		initialQuery.addOrder(sqlQueryTable,pkColumnName,true);
		initialQuery.removeAllColumns();
		initialQuery.addColumn(sqlQueryTable,pkColumnName);
		
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = _entity.getConnection(_entity.getDatasource());
			Stmt = conn.createStatement();
			
			
			System.out.println("[IDOPrimaryKeyList - Initialize - orginal query]: "+ _sqlQuery.toString());
				
			if (_entity.isDebugActive())
			{
				System.out.println("[IDOPrimaryKeyList - Initialize - modified query]: "+ initialQuery.toString());
			}
			ResultSet RS=Stmt.executeQuery(initialQuery.toString());
			_entities = new Vector();
				
//			int fetchIndex = 0;
			Object pk = null;
			while(RS.next()){
				pk = _entity.getPrimaryKeyFromResultSet(allowedPrimaryKeyClass,pkFields,RS);
				super.add(pk);
//				if(fetchIndex++<=_prefetchSize){
//					_entities.add(_entity.prefetchBeanFromResultSet(pk, RS,_entity.getDatasource()));
//				}
			}
			RS.close();
			
			
			
			_entities.setSize(size());
			_tracker = new LoadTracker(size(),fetchSize);
//			_tracker.setSubsetAsLoaded(0,_prefetchSize);
		}
		catch (SQLException sqle)
		{
			System.err.println("[IDOPrimaryKeyList]: Went to database for SQL query: " + _sqlQuery);
			sqle.printStackTrace();
			throw new IDOFinderException(sqle);
		} 
//		catch (FinderException e) {
//			System.err.println("[IDOPrimaryKeyList]: Went to database for SQL query: " + _sqlQuery);
//			e.printStackTrace();
//			throw new IDOFinderException(e);
//		}
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
		timer.stop();
		System.out.println("[PrimaryKeyList]: method initialize() "+timer.getTime()+"ms");
	}


	/**
	 * @param fromIndex low endpoint (inclusive).
	 * @param toIndex high endpoint (exclusive).
	 */
	private void loadSubset(int fromIndex, int toIndex) throws IDOFinderException
	{
		Timer timer = new Timer();
		timer.start();
		List setsToLoad = _tracker.getNotLoadedSubsets(fromIndex,toIndex);
		if (_entity.isDebugActive())
		{
			System.out.println("############################[PrimaryKeyList]: method loadSubset ##################################");
			System.out.println("[PrimaryKeyList]: method loadSubset("+fromIndex+","+toIndex+") before");
		}
		
		if(setsToLoad != null && setsToLoad.size() > 0)
		{
			DatastoreInterface iFace = DatastoreInterface.getInstance(_entity);
			for (Iterator iter = setsToLoad.iterator(); iter.hasNext();) {
				int[] item = (int[]) iter.next();
				int fIndex = item[LoadTracker.FROM_INDEX_IN_ARRAY];
			    int tIndex = item[LoadTracker.TO_INDEX_IN_ARRAY];
			    
			    int partitionSize = iFace.getOptimalEJBLoadFetchSize();
			    int partitions = (tIndex-fIndex)/partitionSize;
			    if(partitions==0 || (tIndex-fIndex)%partitions>0){
			    		partitions++;
			    }
				for(int i = 0; i < partitions; i++){
					int f = fIndex+(i*partitionSize);
					int t =Math.min(tIndex,f+partitionSize);
					if(f<=t){
						if (_entity.isDebugActive())
						{
							System.out.println("[PrimaryKeyList]: method loadList("+f+","+t+") before");
						}
				  		_tracker.printLoadInformations();
						loadSubset(subList(f,t), f);
				  		_tracker.setSubsetAsLoaded(f,t);
				  		if (_entity.isDebugActive())
						{
					  		System.out.println("[PrimaryKeyList]: after");
					  		_tracker.printLoadInformations();
						}
					} else {
						System.err.println("[PrimaryKeyList]: method loadList(...) from > to, "+f+" > "+t);
					}
				}
			}
			
			
		}
		timer.stop();
		System.out.println("[PrimaryKeyList]: method loadSubset("+fromIndex+","+toIndex+") "+timer.getTime()+"ms");
	}
	
	private void loadSubset(List listOfPrimaryKeys, int firstIndex) throws IDOFinderException {
		Timer timer = new Timer();
		timer.start();
		SelectQuery subsetQuery = (SelectQuery)_sqlQuery.clone();
		subsetQuery.removeAllCriteria();
		subsetQuery.addCriteria(new InCriteria(sqlQueryTable,pkColumnName,listOfPrimaryKeys));
		subsetQuery.addOrder(sqlQueryTable,pkColumnName,true);
		
		
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = _entity.getConnection(_entity.getDatasource());
			Stmt = conn.createStatement();
			
			if (_entity.isDebugActive())
			{
				System.out.println("[IDOPrimaryKeyList - Load index "+firstIndex+" to "+(firstIndex+listOfPrimaryKeys.size())+"]: "+ subsetQuery);
			}
		    ResultSet RS=Stmt.executeQuery(subsetQuery.toString());
		    
		    int total = 0;
		    int no = 0;
		    
			for(int i = firstIndex; RS.next(); i++)
			{
				Object pk = _entity.getPrimaryKeyFromResultSet(RS);
				if (pk != null)
				{
					try {
						_entities.set(i,_entity.prefetchBeanFromResultSet(pk, RS,_entity.getDatasource()));
						if(!pk.equals(this.get(i))){
//							System.err.println("[IDOPrimaryKeyList - WARNING]: "+ subsetQuery);
							no++;
							System.err.println("[IDOPrimaryKeyList]: At index "+i+" loadSubset set entity with primary key "+pk+" but the primaryKeyList contains primary key "+this.get(i)+" at that index");
							System.err.println("[IDOPrimaryKeyList]: The right index would have been "+indexOf(pk));
						}
					} catch (FinderException e) {
						//The row must have been deleted from database
//						this.remove(pk);
						e.printStackTrace();
					}
				}
				total++;
			}
			if (_entity.isDebugActive())
			{
				System.err.println("[IDOPrimaryKeyList]:  "+no+" of "+total+ " where not loaded right");
			}
			RS.close();
		} catch (SQLException sqle) {
			System.err.println("[IDOPrimaryKeyList]: Went to database for SQL query: " + subsetQuery);
			sqle.printStackTrace();
			throw new IDOFinderException(sqle);
		} finally {
			if (Stmt != null) {
				try {
					Stmt.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				_entity.freeConnection(_entity.getDatasource(), conn);
			}
		}
		timer.stop();
		System.out.println("[PrimaryKeyList]: method loadList() from "+firstIndex+" to "+(firstIndex+listOfPrimaryKeys.size())+" "+timer.getTime()+"ms");
	}

	public Object validatePrimaryKeyObject(Object o){
		if(!allowedPrimaryKeyClass.equals(o.getClass())){
			throw new UnsupportedOperationException("Object of type "+o.getClass().getName()+" is not allowed as primary key in this IDOPrimaryKeyList, it needs to be "+allowedPrimaryKeyClass.getName());
		}
		return o;
	}
	
	public IDOEntity validateIDOEntityObject(IDOEntity o){
		if(!_entity.getInterfaceClass().equals(o.getEntityDefinition().getInterfaceClass())){
			throw new UnsupportedOperationException("Object of type "+o.getEntityDefinition().getInterfaceClass()+" is not allowed as entity in this IDOPrimaryKeyList/IDOEntityList, it needs to be "+_entity.getInterfaceClass().getName());
		}
		return o;
	}

	public void clear() {
		super.clear();
		_entities.clear();
		_tracker = new LoadTracker(0,fetchSize);
		//_initialized=false;
	}

//  public Iterator iterator() {
//		return listIterator();
//  }
//
//  public ListIterator listIterator() {
//		return listIterator(0);
//  }
//
//  public ListIterator listIterator(int index) {
//		return new IDOPrimaryKeyListIterator(this,index);
//  }
  
  

//	/**
//	 * Returns a view of the portion of this List between fromIndex,
//	 * inclusive, and toIndex, exclusive.  (If fromIndex and ToIndex are
//	 * equal, the returned List is empty.)  The returned List is not backed by this
//	 * List, so changes in the returned List will not affect this List.
//	 *
//	 * @param fromIndex low endpoint (inclusive) of the subList.
//	 * @param toIndex high endpoint (exclusive) of the subList.
//	 * @return a view of the specified range within this List.
//	 * @throws IndexOutOfBoundsException endpoint index value out of range
//	 *         <code>(fromIndex &lt; 0 || toIndex &gt; size)</code>
//	 * @throws IllegalArgumentException endpoint indices out of order
//	 *	       <code>(fromIndex &gt; toIndex)</code>
//	 */
//	List subIDOEntityList(int fromIndex, int toIndex) {
//		try
//		{
//			this.loadSubset(fromIndex,toIndex);
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		return (List)((Vector)_entities.subList(fromIndex, toIndex)).clone();
//	}


  public boolean equals(Object o) {
  	if(!super.equals(o))
		return false;
	
	IDOPrimaryKeyList obj = (IDOPrimaryKeyList)o;
	if(!_sqlQuery.equals(obj._sqlQuery))
		return false;
	if(!_entity.equals(obj._entity))
		return false;
	if(isSublist !=  obj.isSublist)
		return false;
	
	return true;
  }

  
  Object getIDOEntity(int index) {
    Object obj = _entities.get(index);
	if(obj == null){
		try{
			loadSubset(index,index+_prefetchSize);
			obj = _entities.get(index);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	return obj;
  }

  
  public Object remove(int index) {
  	super.remove(index);
  	System.out.println("[PrimaryKeyList]: method remove("+index+") before");
	_tracker.printLoadInformations();
	_tracker.remove(index);
	System.out.println("[PrimaryKeyList]: after");
	_tracker.printLoadInformations();
    return _entities.remove(index);
  }

  public boolean remove(Object o) {
  	if(o instanceof IDOEntity){
  		return removeIDOEntity((IDOEntity)o);
  	}
  	int index = super.indexOf(o);
  	if(index==-1){
  		return false;
  	}
  	remove(index);
  	return true;
  }
  
  boolean removeIDOEntity(IDOEntity o){
  	int index = _entities.indexOf(o);
  	if(index==-1){
  		return false;
  	}
  	remove(index);
  	return true;  	
  }
  
  public boolean contains(Object o) {
  	if(o instanceof IDOEntity){
  		if(_tracker.getLoadRatio() != 1){
	  		try {
				loadSubset(0,size());
			} catch (IDOFinderException e) {
				System.err.println("[WARNING!!!][IDOPrimaryKeyList]: failed to load entities and can therefore return incorrect result");
				e.printStackTrace();
			}
  		}
  		return _entities.contains(o);
  	} else {
  		return super.contains(o);
  	}
  }
  
  Object[] toIDOEntityArray() {
  	try {
		loadSubset(0,size());
	} catch (IDOFinderException e) {
		System.err.println("[WARNING!!!][IDOPrimaryKeyList]: failed to load entities and will therefore return incorrect result");
		e.printStackTrace();
	}
    return _entities.toArray();
  }
  Object[] toIDOEntityArray(Object[] a) {
  	try {
		loadSubset(0,size());
	} catch (IDOFinderException e) {
		System.err.println("[WARNING!!!][IDOPrimaryKeyList]: failed to load entities and will therefore return incorrect result");
		e.printStackTrace();
	}
    return _entities.toArray(a);
  }
  
  public boolean containsAll(Collection c) {  //Implementation copied from java.util.AbstractCollection
  	Iterator e = c.iterator();
	while (e.hasNext())
	    if(!contains(e.next()))
		return false;

	return true;
  }
  
  public boolean add(Object o) {
  	boolean toReturn;
  	if(o instanceof IDOEntity){
  		toReturn = _entities.add(validateIDOEntityObject((IDOEntity)o)) && super.add(((IDOEntity)o).getPrimaryKey());
  		System.out.println("[PrimaryKeyList]: method add("+o+") before");
  		_tracker.printLoadInformations();
  		_tracker.addLoadedSubset(1);
  		System.out.println("[PrimaryKeyList]: after");
  		_tracker.printLoadInformations();
  	} else {
  		Object pk = o;
  		if(pk instanceof String){
  			pk = _entity.decode((String)pk);
  		} else {
  			validatePrimaryKeyObject(pk);
  		}
  		toReturn = super.add(pk);
  		System.out.println("[PrimaryKeyList]: method add("+o+") before");
  		_tracker.printLoadInformations();
  		_tracker.addUnloadedSubset(1);
  		System.out.println("[PrimaryKeyList]: after");
  		_tracker.printLoadInformations();
  	}
  	return toReturn;
  }
  
  public void add(int index, Object element) {
  	if(element instanceof IDOEntity){
  		_entities.add(index,validateIDOEntityObject((IDOEntity)element));
  		super.add(index,((IDOEntity)element).getPrimaryKey());
  		System.out.println("[PrimaryKeyList]: method add("+index+","+element+") before");
  		_tracker.printLoadInformations();
  		_tracker.addLoadedSubset(index,1);
  		System.out.println("[PrimaryKeyList]: after");
  		_tracker.printLoadInformations();
  	} else {
  		Object pk = element;
  		if(pk instanceof String){
  			pk = _entity.decode((String)pk);
  		} else {
  			validatePrimaryKeyObject(pk);
  		}
  		super.add(index,pk);
  		System.out.println("[PrimaryKeyList]: method add("+index+","+element+") before");
  		_tracker.printLoadInformations();
  		_tracker.addUnloadedSubset(index,1);
  		System.out.println("[PrimaryKeyList]: after");
  		_tracker.printLoadInformations();
  	}
  }
  
  public boolean addAll(Collection c) {
  	boolean toReturn=true;
	for (Iterator iter = c.iterator(); iter.hasNext();) {
		Object element = (Object) iter.next();
		if(!add(element)){
			toReturn = false;
		}
	}
  	return toReturn;
  }
  public boolean addAll(int index, Collection c) {
  	int i = index;
  	for (Iterator iter = c.iterator(); iter.hasNext();i++) {
		Object element = (Object) iter.next();
		add(i,element);
	}
  	
    return c.size() != 0; // this return implementation is copied from java.util.Vector
  }
  
  public boolean removeAll(Collection c) {  //Implementation copied from java.util.AbstractCollection
	boolean modified = false;
	Iterator e = iterator();
	while (e.hasNext()) {
	    if(c.contains(e.next())) {
		e.remove();
		modified = true;
	    }
	}
	return modified;
  }
  
  public boolean retainAll(Collection c) {  //Implementation copied from java.util.AbstractCollection
	boolean modified = false;
	Iterator e = iterator();
	while (e.hasNext()) {
	    if(!c.contains(e.next())) {
		e.remove();
		modified = true;
	    }
	}
	return modified;
  }
  
  public Object set(int index, Object element) {
  	Object toReturn = null;
  	if(element instanceof IDOEntity){
  		toReturn=_entities.set(index,validateIDOEntityObject((IDOEntity)element));
  		super.set(index,((IDOEntity)element).getPrimaryKey());
  		System.out.println("[PrimaryKeyList]: method set("+index+","+element+") before");
  		_tracker.printLoadInformations();
  		_tracker.setAsLoaded(index);
  		System.out.println("[PrimaryKeyList]: after");
  		_tracker.printLoadInformations();
  	} else {
  		Object pk = element;
  		if(pk instanceof String){
  			pk = _entity.decode((String)pk);
  		} else {
  			validatePrimaryKeyObject(pk);
  		}
  		toReturn=super.set(index,pk);
  		_entities.add(index,null);
  		System.out.println("[PrimaryKeyList]: method set("+index+","+element+") before");
  		_tracker.printLoadInformations();
  		_tracker.setAsNotLoaded(index);
  		System.out.println("[PrimaryKeyList]: after");
  		_tracker.printLoadInformations();
  	}
  	return toReturn;
  }
  
  public int indexOf(Object o) {
  	if(o instanceof IDOEntity){
  		return _entities.indexOf(o);
  	} else {
  		return super.indexOf(o);
  	}
  }
  public int lastIndexOf(Object o) {
  	if(o instanceof IDOEntity){
  		return _entities.lastIndexOf(o);
  	} else {
  		return super.lastIndexOf(o);
  	}
  }


	public class LoadTracker {
//		int initialCapacity;
		private int _subsetMinLength = 100;
		private int _capacityIncrement;
		private IntervalComparator _comparator = new IntervalComparator();
		/**
		 * int this list there are added int[] of length 2 wherre the int in index 0 is first loaded object in subset but the int in index 1 is the index after the last loaded object in that subset 
		 */
		private Vector _loadedSubSets = new Vector();
//		private int _numberOfSubsets = 0;
		public static final int FROM_INDEX_IN_ARRAY = 0;
		public static final int TO_INDEX_IN_ARRAY = 1;
		private int _size;
		


		public LoadTracker(int size, int subsetMinLength){
		    this._size = size;
			this._subsetMinLength = subsetMinLength;
		}

		private void debugLoadedSubSets(){
		    System.out.println("[IDOPrimaryKeyList.LoadTracker]: not loaded subsets ->");
			System.out.println("From\tTo");
			System.out.println("-----\t------");
			List notLoaded = getNotLoadedSubsets(0,_size);
			if(notLoaded != null){
				Iterator iter = notLoaded.iterator();
				boolean showNulls = true;
				while (iter.hasNext()) {
					int[] item = (int[])iter.next();
					if(item != null || showNulls){
						System.out.println(item[0]+"\t"+item[1]);
					}
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
				return (float)((float)NumberOfLoadedElements)/((float)_size);
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
		    int tIndex = Math.min(toIndex,_size);
			Iterator iter = _loadedSubSets.iterator();
			while (iter.hasNext()) {
				int[] item = (int[])iter.next();
				if(item[FROM_INDEX_IN_ARRAY]<= fIndex){
					 if(item[TO_INDEX_IN_ARRAY] >= tIndex){
					 	return true;
					 } else {
					 	int joint = item[TO_INDEX_IN_ARRAY]; 
					 	while(iter.hasNext()){
					 		int[] anotherItem = (int[])iter.next();
					 		if(joint == anotherItem[FROM_INDEX_IN_ARRAY]){
					 			if(anotherItem[TO_INDEX_IN_ARRAY] >= tIndex){
					 				return true;
					 			} else {
					 				joint = anotherItem[TO_INDEX_IN_ARRAY];
					 			}
					 		} else {  // that is if (joint < anotherItem[FROM_INDEX_IN_ARRAY])
					 			return false;
					 		}
					 	}
					}
				}
				if(item[FROM_INDEX_IN_ARRAY] > tIndex ){
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
							if(interval[FROM_INDEX_IN_ARRAY]<interval[TO_INDEX_IN_ARRAY]){
								fIndex = interval[TO_INDEX_IN_ARRAY];
								toReturn.add(interval);
							}
//						}
					    break;
					}

					if(fIndex < item[FROM_INDEX_IN_ARRAY] && tIndex >= item[FROM_INDEX_IN_ARRAY] && tIndex <= item[TO_INDEX_IN_ARRAY]){  // interval begins before item's interval and covers at least some of it or is right beside it
					    int [] interval = new int[2];
					    
					    	interval[FROM_INDEX_IN_ARRAY] = fIndex;
					    	if(!toReturn.isEmpty()){
					    		interval[FROM_INDEX_IN_ARRAY] = Math.max(interval[FROM_INDEX_IN_ARRAY],((int[])_loadedSubSets.get(toReturn.size()-1))[TO_INDEX_IN_ARRAY]);
					    	}
					    	if(_loadedSubSets.size() > index+1){
							    interval[FROM_INDEX_IN_ARRAY] = Math.max(interval[FROM_INDEX_IN_ARRAY],((int[])_loadedSubSets.get(index+1))[TO_INDEX_IN_ARRAY]);
						}
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
						if(interval[FROM_INDEX_IN_ARRAY]<interval[TO_INDEX_IN_ARRAY]){
							fIndex = interval[TO_INDEX_IN_ARRAY];
							toReturn.add(interval);
						}
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
						if(interval[FROM_INDEX_IN_ARRAY]<interval[TO_INDEX_IN_ARRAY]){
							fIndex = interval[TO_INDEX_IN_ARRAY];
							toReturn.add(interval);
						}
					}
				}

//				Collections.sort(toReturn,_comparator);
				return toReturn;
			}
		}
		
		public void printLoadInformations(){
//			System.out.println("-------[IDOPrimaryKeyList.LoadTracker]: begin -------");
//			System.out.println("[IDOPrimaryKeyList.LoadTracker]: Load ratio "+getLoadRatio());
//			System.out.println("[IDOPrimaryKeyList.LoadTracker]: Size "+_size);
////			debugLoadedSubSets();
//			debugLoadedSubSets();
//////			for (Iterator iter = _loadedSubSets.iterator(); iter.hasNext();) {
//////				int[] loadedSets = (int[]) iter.next();
//////				System.out.println("[IDOPrimaryKeyList.LoadTracker]: Loaded from "+loadedSets[FROM_INDEX_IN_ARRAY]+" to "+loadedSets[TO_INDEX_IN_ARRAY]);
//////			}
//			System.out.println("-------[IDOPrimaryKeyList.LoadTracker]: end -------");
		}
		
		public void removeSubset(int index, int size){
			_size -= size;
			
			int toIndex = index+size;
			for (Iterator iter = _loadedSubSets.iterator(); iter.hasNext();) {
				int[] subset = (int[]) iter.next();
				int f = subset[FROM_INDEX_IN_ARRAY];
				int t = subset[TO_INDEX_IN_ARRAY];
				if(f >= index && t > toIndex){ // cut left and lower index
					subset[FROM_INDEX_IN_ARRAY] = index+1;
					subset[TO_INDEX_IN_ARRAY] = t+size;
				}
				if(f < index && t >= index && t <= toIndex){ // cut right
					subset[TO_INDEX_IN_ARRAY] = index;
				}
				
				if( f <= index && t > toIndex){ // if set is from within a loaded subset
					if(t == toIndex && f == index){ // if set is a loaded subset
						iter.remove();
					} else {
						subset[TO_INDEX_IN_ARRAY] = t-size;
					}
				}
				if(f>toIndex){ // all loaded subsets that start after the removed subset
					subset[FROM_INDEX_IN_ARRAY] = f-size;
					subset[TO_INDEX_IN_ARRAY] = t-size;
				}
			}
		}
		
		public void remove(int index){
			removeSubset(index,1);
		}

		public void addUnloadedSubset(int index, int size){
			_size += size;
			int[] setToAdd = null;
			for (Iterator iter = _loadedSubSets.iterator(); iter.hasNext();) {
				int[] subset = (int[]) iter.next();
				int f = subset[FROM_INDEX_IN_ARRAY];
				int t = subset[TO_INDEX_IN_ARRAY];
				if(t >= index && f <= index){  // if set is added into a loaded subset or right after it
					subset[TO_INDEX_IN_ARRAY] = index;
					setToAdd = new int[2];
					setToAdd[FROM_INDEX_IN_ARRAY] = index+size;
					setToAdd[TO_INDEX_IN_ARRAY] = t+size;
				}
				if(f>(index)){  // all loaded subsets that start after where the current subset is added
					subset[FROM_INDEX_IN_ARRAY] = f+size;
					subset[TO_INDEX_IN_ARRAY] = t+size;
				}
			}
			if(setToAdd !=null){
				_loadedSubSets.add(setToAdd);
				Collections.sort(_loadedSubSets,_comparator);
			}

		}
		
		public void addLoadedSubset(int index, int size){
			_size += size;
			boolean isLoaded = false;
			for (Iterator iter = _loadedSubSets.iterator(); iter.hasNext();) {
				int[] subset = (int[]) iter.next();
				int f = subset[FROM_INDEX_IN_ARRAY];
				int t = subset[TO_INDEX_IN_ARRAY];
				if(t >= index && f <= index){ // if set is added into a loaded subset
					subset[TO_INDEX_IN_ARRAY] = t+size;
					isLoaded = true;
				}
				if(f>index){ // all loaded subsets that start after where the current subset is added
					subset[FROM_INDEX_IN_ARRAY] = f+size;
					subset[TO_INDEX_IN_ARRAY] = t+size;
				}
			}
			if(!isLoaded){
				int [] interval = new int[2];
				interval[FROM_INDEX_IN_ARRAY] = index;
				interval[TO_INDEX_IN_ARRAY] = index+size;
				_loadedSubSets.add(interval);
				Collections.sort(_loadedSubSets,_comparator);
			}
		}
		
		public void addUnloadedSubset(int size){
			addUnloadedSubset(_size,size);
		}
		
		public void addLoadedSubset(int size){
			addLoadedSubset(_size,size);
		}
		
		public void setAsNotLoaded(int index){
			setSubsetAsNotLoaded(index,index+1);
		}
		
		public void setAsLoaded(int index){
			setSubsetAsLoaded(index,index+1);
		}
		
		/**
		 * @param fromIndex low endpoint (inclusive).
		 * @param toIndex high endpoint (exclusive).
		 */
		public void setSubsetAsNotLoaded(int fromIndex, int toIndex){
			int[] setToAdd = null;
			for (Iterator iter = _loadedSubSets.iterator(); iter.hasNext();) {
				int[] subset = (int[]) iter.next();
				int f = subset[FROM_INDEX_IN_ARRAY];
				int t = subset[TO_INDEX_IN_ARRAY];
				if(t >= fromIndex && f <= fromIndex){  // if set is subset of a loaded subset
					subset[TO_INDEX_IN_ARRAY] = fromIndex;
					setToAdd = new int[2];
					setToAdd[FROM_INDEX_IN_ARRAY] = toIndex;
					setToAdd[TO_INDEX_IN_ARRAY] = t;
					break;
				}
				if(f>(toIndex)){  // cut left
					subset[FROM_INDEX_IN_ARRAY] = toIndex;
				}
				if(t>(fromIndex)){  // cut right
					subset[TO_INDEX_IN_ARRAY] = fromIndex;
				}
			}
			if(setToAdd !=null){
				_loadedSubSets.add(setToAdd);
				Collections.sort(_loadedSubSets,_comparator);
			}
		}
		
		/**
		 * @param fromIndex low endpoint (inclusive).
		 * @param toIndex high endpoint (exclusive).
		 */
		public void setSubsetAsLoaded(int fromIndex, int toIndex){
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
			 	_list.set(_index-1,((IDOEntity)o));
		    }
		    public void add(Object o) {
			 	_list.add(_index,((IDOEntity)o));
		    }

}

}