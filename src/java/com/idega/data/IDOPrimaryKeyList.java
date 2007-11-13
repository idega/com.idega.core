package com.idega.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.JoinCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.Order;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.util.logging.LoggingHelper;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 2.0
 */

public class IDOPrimaryKeyList extends Vector implements List, Runnable {

	//TODO gummi: replace GenericEntity with IDOEntity and IDOContainer
	
	private String _dataSource = GenericEntity.DEFAULT_DATASOURCE;
	
	private SelectQuery _sqlQuery;
	private SelectQuery _loadQueryBase;
	GenericEntity _entity;
	private Class allowedPrimaryKeyClass; // varaable set in the initialize method where it is fetched ones from the variable _entity for optimizing reasons
	private String pkColumnName;
	private Table sqlQueryTable;
	private GenericEntity _returnProxy;
	private String _returnProxyPkColumnName;
	private Table _returnProxySqlQueryTable; 
	private SelectQuery _returnProxyQueryConstraints;
	
	//This Vector contains the loaded IDOEntities but the super list of this object contains list of all primary keys
	private Vector _entities=null;
	private LoadTracker _tracker;
	private int fetchSize = 1;
	private int _prefetchSize=100;
	private boolean isSublist = false;

	

	@SuppressWarnings("unused")
	private IDOPrimaryKeyList() {
	}

//	private void reloadResultSet(){
//
//	}

	public IDOPrimaryKeyList(SelectQuery sqlQuery, GenericEntity entity, int prefetchSize) throws IDOFinderException {
		this(sqlQuery,entity,null,null,prefetchSize);
    }
	
	public IDOPrimaryKeyList(SelectQuery sqlQuery, GenericEntity entity, GenericEntity returnProxy, SelectQuery proxyQueryConstraints, int prefetchSize) throws IDOFinderException {
		this._sqlQuery = sqlQuery;
//		_Stmt = Stmt;
//		_RS = RS;
		this._entity = entity;
		if (entity != null) {
			this._dataSource = entity.getDatasource();
		}
		this._prefetchSize = prefetchSize;
		this._returnProxy = returnProxy;
		this._returnProxyQueryConstraints = proxyQueryConstraints;
		
		initialize(null);
    }
	
	public IDOPrimaryKeyList(Collection primaryKeyList, GenericEntity entity, int prefetchSize) throws IDOFinderException {
		this._sqlQuery = null;
//		_Stmt = Stmt;
//		_RS = RS;
		this._entity = entity;
		if (entity != null) {
			this._dataSource = entity.getDatasource();
		}
		this._prefetchSize = prefetchSize;
		initialize(primaryKeyList);
    }
	
	public IDOPrimaryKeyList(Collection primaryKeyList, Class entityInterfaceClass, int prefetchSize) throws IDOFinderException {
		this._sqlQuery = null;
//		_Stmt = Stmt;
//		_RS = RS;
		this._entity = (GenericEntity)IDOLookup.instanciateEntity(entityInterfaceClass);
		if (this._entity != null) {
			this._dataSource = this._entity.getDatasource();
		}
		this._prefetchSize = prefetchSize;
		initialize(primaryKeyList);
    }

	public void run() {
//		int loadIntervalSize = fetchSize;
//		if(_size < loadIntervalSize){
			try {
				loadSubset(0,size());
			}
			catch (Exception ex) {
				logError("["+this.getClass()+"]: Exeption: "+ex.getClass()+" occured while executing: "+this._sqlQuery);
			}
//		} else {
//			try{
//				int numberOfSubsets = _size/loadIntervalSize + ((_size%loadIntervalSize > 0)?1:0);
//				for (int i = 0; i < loadIntervalSize; i++) {
//					loadSubset(i*loadIntervalSize, (i+1)*loadIntervalSize);
//				}
//			}
//			catch (Exception ex) {
//				logError("["+this.getClass()+"]: Exeption: "+ex.getClass()+" occured while executing: "+_sqlQuery);
//			}
//		}

	}
	
	
	private void initialize(Collection primaryKeyCollection) throws IDOFinderException {
		IDOPrimaryKeyDefinition pkDefinition = this._entity.getEntityDefinition().getPrimaryKeyDefinition();
		IDOEntityField[] pkFields = pkDefinition.getFields();
		if(pkFields==null || pkFields.length>1){
			throw new UnsupportedOperationException("IDOPrimaryKeyList is capable of handling entities whith one primary key only, this entity: "+this._entity.getClass().getName()+" has "+ ((pkFields==null)?"none":String.valueOf(pkFields.length)));
		}
		this.allowedPrimaryKeyClass = pkDefinition.getPrimaryKeyClass();
		this.pkColumnName = pkFields[0].getSQLFieldName();
		
		if (this._sqlQuery != null && this._sqlQuery.getBaseTable() != null) {
		    this.sqlQueryTable = this._sqlQuery.getBaseTable();
		} else {
		    this.sqlQueryTable = new Table(this._entity);
		}
		
		if(this._returnProxy!=null){
			IDOPrimaryKeyDefinition proxyPkDefinition = this._returnProxy.getEntityDefinition().getPrimaryKeyDefinition();
			IDOEntityField[] proxyPkFields = proxyPkDefinition.getFields();
			if(pkFields==null || pkFields.length>1){
				throw new UnsupportedOperationException("IDOPrimaryKeyList is capable of handling entities whith one primary key only, this entity: "+this._returnProxy.getClass().getName()+" has "+ ((pkFields==null)?"none":String.valueOf(pkFields.length)));
			}
			this._returnProxyPkColumnName = proxyPkFields[0].getSQLFieldName();	
			this._returnProxySqlQueryTable = new Table(this._returnProxy);
		}
		
		if(primaryKeyCollection != null){
			debug("[IDOPrimaryKeyList - Initialize - primary key list added]: length "+ primaryKeyCollection.size());
			super.addAll(primaryKeyCollection);
			this._entities = new Vector();
			this._entities.setSize(size());
			this._tracker = new LoadTracker(size(),this.fetchSize);
		} else {
			SelectQuery initialQuery = (SelectQuery)this._sqlQuery.clone();
			this._loadQueryBase = (SelectQuery)this._sqlQuery.clone();
			initialQuery.removeAllColumns();
			initialQuery.addColumn(this.sqlQueryTable,this.pkColumnName);
			
			if(this._returnProxy!=null && this._returnProxyQueryConstraints!=null){
				boolean join = false;
				
				Collection criteria = this._returnProxyQueryConstraints.getCriteria();
				if(criteria!=null && !criteria.isEmpty()){
					for (Iterator iter = criteria.iterator(); iter.hasNext();) {
						Criteria cr = (Criteria) iter.next();
						initialQuery.addCriteria(cr);
					}
					join = true;
				}
				
				Collection order = this._returnProxyQueryConstraints.getOrder();
				if(order!=null && !order.isEmpty()){
					for (Iterator iter = order.iterator(); iter.hasNext();) {
						Order o = (Order) iter.next();
						initialQuery.addOrder(o);
						this._loadQueryBase.addOrder(o);
					}
					join = true;
				}
				
				if(join){
					initialQuery.addJoin(this.sqlQueryTable,this.pkColumnName,this._returnProxySqlQueryTable,this._returnProxyPkColumnName);
				}
			}
			
			initialQuery.addOrder(this.sqlQueryTable,this.pkColumnName,true);
			this._loadQueryBase.addOrder(this.sqlQueryTable,this.pkColumnName,true);
			
			Connection conn = null;
			Statement Stmt = null;
			ResultSet RS=null;
			try
			{
				conn = this._entity.getConnection(this._entity.getDatasource());
				Stmt = null;//conn.createStatement();
					
				if (this._entity.isDebugActive())
				{
					debug("[IDOPrimaryKeyList - Initialize - orginal query]: "+ this._sqlQuery.toString());
					debug("[IDOPrimaryKeyList - Initialize - modified query]: "+ initialQuery.toString());
					debug("[IDOPrimaryKeyList - Initialize - load query base]: "+ this._loadQueryBase.toString());
				}
				List placeHolderValues = initialQuery.getValues();
			    if(placeHolderValues==null || placeHolderValues.isEmpty()){
				    Stmt = conn.createStatement();
				    RS = Stmt.executeQuery(initialQuery.toString());
				}
				// use PreparedStatement
				else{
				    Stmt = conn.prepareStatement(initialQuery.toString(true));
				    DatastoreInterface dsi = DatastoreInterface.getInstance(this._entity);
				    dsi.insertIntoPreparedStatement(placeHolderValues,(PreparedStatement)Stmt,1);
				    	RS = ((PreparedStatement)Stmt).executeQuery();
				}
				//ResultSet RS=Stmt.executeQuery(initialQuery.toString());
				this._entities = new Vector();
					
	//			int fetchIndex = 0;
				Object pk = null;
				while(RS.next()){
					pk = this._entity.getPrimaryKeyFromResultSet(this.allowedPrimaryKeyClass,pkFields,RS);
					super.add(pk);
	//				if(fetchIndex++<=_prefetchSize){
	//					_entities.add(_entity.prefetchBeanFromResultSet(pk, RS,_entity.getDatasource()));
	//				}
				}
				RS.close();
				
				this._entities.setSize(size());
				this._tracker = new LoadTracker(size(),this.fetchSize);
	//			_tracker.setSubsetAsLoaded(0,_prefetchSize);
			}
			catch (SQLException sqle)
			{
				logError("[IDOPrimaryKeyList]: Went to database for SQL query: " + initialQuery);
				throw new IDOFinderException(sqle);
			} 
	//		catch (FinderException e) {
	//			logError("[IDOPrimaryKeyList]: Went to database for SQL query: " + _sqlQuery);
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
				
//				if(RS!=null){
//					try {
//						RS.close();
//					}
//					catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
				if (conn != null)
				{
					this._entity.freeConnection(this._entity.getDatasource(), conn);
				}
			}
		}
	}


	/**
	 * @param fromIndex low endpoint (inclusive).
	 * @param toIndex high endpoint (exclusive).
	 */
	private void loadSubset(int fromIndex, int toIndex) throws IDOFinderException
	{
		List setsToLoad = this._tracker.getNotLoadedSubsets(fromIndex,toIndex);
		if (this._entity.isDebugActive())
		{
			debug("############################[PrimaryKeyList]: method loadSubset ##################################");
			debug("[PrimaryKeyList]: method loadSubset("+fromIndex+","+toIndex+") before");
		}
		
		if(setsToLoad != null && !setsToLoad.isEmpty())
		{
			DatastoreInterface iFace = DatastoreInterface.getInstance(this._entity);
			for (Iterator iter = setsToLoad.iterator(); iter.hasNext();) {
				int[] item = (int[]) iter.next();
				int fIndex = item[LoadTracker.FROM_INDEX_IN_ARRAY];
			    int tIndex = item[LoadTracker.TO_INDEX_IN_ARRAY];
			    
			    int partitionSize = iFace.getOptimalEJBLoadFetchSize();
			    int partitions = (tIndex-fIndex)/partitionSize;
			    if((tIndex-fIndex)%partitionSize>0){
			    		partitions++;
			    }
				for(int i = 0; i < partitions; i++){
					int f = fIndex+(i*partitionSize);
					int t =Math.min(tIndex,f+partitionSize);
					if(f<=t){
						if (this._entity.isDebugActive())
						{
							debug("[PrimaryKeyList]: method loadList("+f+","+t+") before");
						}
				  		this._tracker.printLoadInformations();
						loadSubset(subList(f,t), f);
				  		if (this._entity.isDebugActive())
						{
					  		debug("[PrimaryKeyList]: after");
					  		this._tracker.printLoadInformations();
						}
					} else {
						logError("[PrimaryKeyList]: method loadList(...) from > to, "+f+" > "+t);
					}
				}
			}
			
			
		}
	}
	
	private void loadSubset(List listOfPrimaryKeys, int firstIndex) throws IDOFinderException {
		SelectQuery subsetQuery = null;
		GenericEntity proxyEntity = this._entity;
		if(this._loadQueryBase==null){
			subsetQuery = new SelectQuery(this.sqlQueryTable);
			subsetQuery.addColumn(new WildCardColumn(this.sqlQueryTable));
			if(listOfPrimaryKeys.size()==1){
				Object pk = listOfPrimaryKeys.get(0);
				if(pk instanceof String){
					subsetQuery.addCriteria(new MatchCriteria(this.sqlQueryTable,this.pkColumnName,MatchCriteria.EQUALS,(String)pk,true));
				} else {
					subsetQuery.addCriteria(new MatchCriteria(this.sqlQueryTable,this.pkColumnName,MatchCriteria.EQUALS,pk));
				}
			} else {
				subsetQuery.addCriteria(new InCriteria(this.sqlQueryTable,this.pkColumnName,listOfPrimaryKeys));
			}
		} else {
			subsetQuery = (SelectQuery)this._loadQueryBase.clone();
			subsetQuery.removeAllCriteria();
			subsetQuery.clearLeftJoins();
			if(listOfPrimaryKeys.size()==1){
				Object pk = listOfPrimaryKeys.get(0);
				if(pk instanceof String){
					subsetQuery.addCriteria(new MatchCriteria(this.sqlQueryTable,this.pkColumnName,MatchCriteria.EQUALS,(String)pk,true));
				} else {
					subsetQuery.addCriteria(new MatchCriteria(this.sqlQueryTable,this.pkColumnName,MatchCriteria.EQUALS,pk));
				}
			} else {
				subsetQuery.addCriteria(new InCriteria(this.sqlQueryTable,this.pkColumnName,listOfPrimaryKeys));
			}
			
			if(this._returnProxy!=null){
				subsetQuery.removeAllColumns();
				subsetQuery.addColumn(new WildCardColumn(this._returnProxySqlQueryTable));
				subsetQuery.addCriteria(new JoinCriteria(this.sqlQueryTable.getColumn(this.pkColumnName),this._returnProxySqlQueryTable.getColumn(this._returnProxyPkColumnName)));
				proxyEntity = this._returnProxy;
			}
		}
		
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = proxyEntity.getConnection(this._dataSource);
			Stmt = null;
			//conn.createStatement();
			
			if (this._entity.isDebugActive())
			{
				debug("[IDOPrimaryKeyList - Load index "+firstIndex+" to "+(firstIndex+listOfPrimaryKeys.size())+"]: "+ subsetQuery);
			}
		    //ResultSet RS=Stmt.executeQuery(subsetQuery.toString());
			ResultSet RS = null;
		    List placeHolderValues = subsetQuery.getValues();
		    if(placeHolderValues==null || placeHolderValues.isEmpty()){
			    Stmt = conn.createStatement();
			    RS = Stmt.executeQuery(subsetQuery.toString());
			}
			// use PreparedStatement
			else{
			    Stmt = conn.prepareStatement(subsetQuery.toString(true));
			    DatastoreInterface dsi = DatastoreInterface.getInstance(this._entity);
			    dsi.insertIntoPreparedStatement(placeHolderValues,(PreparedStatement)Stmt,1);
			    	RS = ((PreparedStatement)Stmt).executeQuery();
			}
		    
		    int total = 0;
		    int no = 0;
		    IDOHome home = (IDOHome)proxyEntity.getEJBLocalHome();
			Class interfaceClass = proxyEntity.getInterfaceClass();
		    if(this._loadQueryBase==null){ // if there is no SQL query the primaryKeys must be added by searching  the pk list for the right index
		    		HashMap mapOfEntities = new HashMap();
		    		while(RS.next())
				{
					Object pk = proxyEntity.getPrimaryKeyFromResultSet(RS);
					if (pk != null)
					{
						try {
							IDOEntity bean = IDOContainer.getInstance().findByPrimaryKey(interfaceClass, pk, RS, home,this._dataSource);
							//IDOEntity bean = proxyEntity.prefetchBeanFromResultSet(pk, RS,);
							mapOfEntities.put(pk,bean);
						} catch (FinderException e) {
							//The row must have been deleted from database
	//						this.remove(pk);
							e.printStackTrace();
						}
					}
					total++;
				}
	    		Iterator iter = listOfPrimaryKeys.iterator();
	    		for (int i = firstIndex; iter.hasNext();i++) {
					Object pk = iter.next();
					Object entity = mapOfEntities.get(pk);
					this._entities.set(i,entity);
//					_tracker.setAsLoaded(firstIndex+i);
					if(entity == null) {
						System.out.println("An entity was not found, id=" + pk);
					} else if(!this.get(i).equals(((IDOEntity)this._entities.get(i)).getPrimaryKey())){
						no++;
						logError("[IDOPrimaryKeyList]: At index "+(i)+" loadSubset set entity with primary key "+pk+" but the primaryKeyList contains primary key "+this.get(i)+" at that index");
						logError("[IDOPrimaryKeyList]: The right index would have been "+indexOf(pk));
					} 
				}
		    		this._tracker.setSubsetAsLoaded(firstIndex,firstIndex+listOfPrimaryKeys.size());
		    } else {
			    for(int i = firstIndex; RS.next(); i++)
			    {
					Object pk = proxyEntity.getPrimaryKeyFromResultSet(RS);
					if (pk != null)
					{
						try {
							IDOEntity bean = IDOContainer.getInstance().findByPrimaryKey(interfaceClass, pk, RS, home,this._dataSource);
							this._entities.set(i,bean);
							if(!pk.equals(this.get(i))){
	//							logError("[IDOPrimaryKeyList - WARNING]: "+ subsetQuery);
								no++;
								logError("[IDOPrimaryKeyList]: At index "+i+" loadSubset set entity with primary key "+pk+" but the primaryKeyList contains primary key "+this.get(i)+" at that index");
								logError("[IDOPrimaryKeyList]: The right index would have been "+indexOf(pk));
							}
							
							//Althoug the same object is more than ones in the primary key list it is only ones
							//in the resultset.  Since the primary key list is orderd by id we can assume that the 
							//duplicated primary keys lie side by side.
							int numberOfEqualObjectsInSequence = 1;
							while(this.size()>(i+numberOfEqualObjectsInSequence) && this.get(i).equals(this.get(i+numberOfEqualObjectsInSequence))){
							    this._entities.set(i+numberOfEqualObjectsInSequence,bean);
							    numberOfEqualObjectsInSequence++;
							}
							i+=numberOfEqualObjectsInSequence-1;
							total+=numberOfEqualObjectsInSequence-1;
						} catch (FinderException e) {
							//The row must have been deleted from database
	//						this.remove(pk);
							e.printStackTrace();
						}
					}
					total++;
				}
			    	this._tracker.setSubsetAsLoaded(firstIndex,firstIndex+listOfPrimaryKeys.size());
		    }
			if (this._entity.isDebugActive())
			{
				logError("[IDOPrimaryKeyList]:  "+no+" of "+total+ " where not loaded right");
			}
			RS.close();
		} catch (SQLException sqle) {
			logError("[IDOPrimaryKeyList]: Went to database for SQL query: " + subsetQuery);
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
				proxyEntity.freeConnection(proxyEntity.getDatasource(), conn);
			}
		}
	}

	public Object validatePrimaryKeyObject(Object o){
		if(!this.allowedPrimaryKeyClass.equals(o.getClass())){
			throw new UnsupportedOperationException("Object of type "+o.getClass().getName()+" is not allowed as primary key in this IDOPrimaryKeyList, it needs to be "+this.allowedPrimaryKeyClass.getName());
		}
		return o;
	}
	
	public IDOEntity validateIDOEntityObject(IDOEntity o){
		if(!this._entity.getInterfaceClass().equals(o.getEntityDefinition().getInterfaceClass())){
			throw new UnsupportedOperationException("Object of type "+o.getEntityDefinition().getInterfaceClass()+" is not allowed as entity in this IDOPrimaryKeyList/IDOEntityList, it needs to be "+this._entity.getInterfaceClass().getName());
		}
		return o;
	}

	@Override
	public void clear() {
		super.clear();
		this._entities.clear();
		this._tracker = new LoadTracker(0,this.fetchSize);
		//_initialized=false;
	}
	
	/**
	 * Logs out to the default log level (which is by default INFO)
	 * @param msg The message to log out
	 */
	protected void log(String msg) {
		//System.out.println(string);
		getLogger().log(getDefaultLogLevel(),msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the default Logger
	 * @param e The Exception to log out
	 */
	protected void log(Exception e) {
		LoggingHelper.logException(e,this,getLogger(),getErrorLogLevel());
	}
	
	/**
	 * Logs out to the specified log level to the default Logger
	 * @param level The log level
	 * @param msg The message to log out
	 */
	protected void log(Level level,String msg) {
		//System.out.println(msg);
		getLogger().log(level,msg);
	}
	
	/**
	 * Logs out to the error log level (which is by default WARNING) to the default Logger
	 * @param msg The message to log out
	 */
	protected void logError(String msg) {
		//logError(msg);
		getLogger().log(getErrorLogLevel(),msg);
	}

	/**
	 * Logs out to the debug log level (which is by default FINER) to the default Logger
	 * @param msg The message to log out
	 */
	protected void logDebug(String msg) {
		System.err.println(msg);
		getLogger().log(getDebugLogLevel(),msg);
	}
	
	/**
	 * Logs out to the SEVERE log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logSevere(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.SEVERE,msg);
	}	
	
	
	/**
	 * Logs out to the WARNING log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logWarning(String msg) {
		System.err.println(msg);
		getLogger().log(Level.WARNING,msg);
	}
	
	/**
	 * Logs out to the CONFIG log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logConfig(String msg) {
		System.err.println(msg);
		getLogger().log(Level.CONFIG,msg);
	}	
	
	/**
	 * Logs out to the debug log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void debug(String msg) {
		logDebug(msg);
	}	
	
	/**
	 * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	 * This behaviour can be overridden in subclasses.
	 * @return the default Logger
	 */
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass().getName());
	}
	
	/**
	 * Gets the log level which messages are sent to when no log level is given.
	 * @return the Level
	 */
	protected Level getDefaultLogLevel(){
		return Level.INFO;
	}
	/**
	 * Gets the log level which debug messages are sent to.
	 * @return the Level
	 */
	protected Level getDebugLogLevel(){
		return Level.FINER;
	}
	/**
	 * Gets the log level which error messages are sent to.
	 * @return the Level
	 */
	protected Level getErrorLogLevel(){
		return Level.WARNING;
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


  @Override
	public boolean equals(Object o) {
  	if(!super.equals(o)) {
			return false;
		}
	
	IDOPrimaryKeyList obj = (IDOPrimaryKeyList)o;
	if(!this._sqlQuery.equals(obj._sqlQuery)) {
		return false;
	}
	if(!this._entity.equals(obj._entity)) {
		return false;
	}
	if(this.isSublist !=  obj.isSublist) {
		return false;
	}
	
	return true;
  }

  
  Object getIDOEntity(int index) {
    Object obj = this._entities.get(index);
	if(obj == null){
		try{
			loadSubset(index,index+this._prefetchSize);
			obj = this._entities.get(index);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	return obj;
  }

  
  @Override
	public Object remove(int index) {
  	super.remove(index);
  	debug("[PrimaryKeyList]: method remove("+index+") before");
	this._tracker.printLoadInformations();
	this._tracker.remove(index);
	debug("[PrimaryKeyList]: after");
	this._tracker.printLoadInformations();
    return this._entities.remove(index);
  }

  @Override
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
  	int index = this._entities.indexOf(o);
  	if(index==-1){
  		return false;
  	}
  	remove(index);
  	return true;  	
  }
  
  
  boolean containsIDOEntity(Object o) {
	if(this._tracker.getLoadRatio() != 1){
  		try {
			loadSubset(0,size());
		} catch (IDOFinderException e) {
			logError("[WARNING!!!][IDOPrimaryKeyList]: failed to load entities and can therefore return incorrect result");
			e.printStackTrace();
		}
	}
	return this._entities.contains(o);
  }
  
  Object[] toIDOEntityArray() {
  	try {
		loadSubset(0,size());
	} catch (IDOFinderException e) {
		logError("[WARNING!!!][IDOPrimaryKeyList]: failed to load entities and will therefore return incorrect result");
		e.printStackTrace();
	}
    return this._entities.toArray();
  }
  Object[] toIDOEntityArray(Object[] a) {
  	try {
		loadSubset(0,size());
	} catch (IDOFinderException e) {
		logError("[WARNING!!!][IDOPrimaryKeyList]: failed to load entities and will therefore return incorrect result");
		e.printStackTrace();
	}
    return this._entities.toArray(a);
  }
  
  @Override
	public boolean containsAll(Collection c) {  //Implementation copied from java.util.AbstractCollection
  	Iterator e = c.iterator();
	while (e.hasNext()) {
		if(!contains(e.next())) {
			return false;
		}
	}

	return true;
  }
  
  @Override
	public boolean add(Object o) {
  	boolean toReturn;
  	if(o instanceof IDOEntity){
  		toReturn = this._entities.add(validateIDOEntityObject((IDOEntity)o)) && super.add(((IDOEntity)o).getPrimaryKey());
  		debug("[PrimaryKeyList]: method add("+o+") before");
  		this._tracker.printLoadInformations();
  		this._tracker.addLoadedSubset(1);
  		debug("[PrimaryKeyList]: after");
  		this._tracker.printLoadInformations();
  	} else {
  		Object pk = o;
  		if(pk instanceof String){
  			pk = this._entity.decode((String)pk);
  		} else {
  			validatePrimaryKeyObject(pk);
  		}
  		toReturn = super.add(pk);
  		debug("[PrimaryKeyList]: method add("+o+") before");
  		this._tracker.printLoadInformations();
  		this._tracker.addUnloadedSubset(1);
  		debug("[PrimaryKeyList]: after");
  		this._tracker.printLoadInformations();
  	}
  	return toReturn;
  }
  
  @Override
	public void add(int index, Object element) {
  	if(element instanceof IDOEntity){
  		this._entities.add(index,validateIDOEntityObject((IDOEntity)element));
  		super.add(index,((IDOEntity)element).getPrimaryKey());
  		debug("[PrimaryKeyList]: method add("+index+","+element+") before");
  		this._tracker.printLoadInformations();
  		this._tracker.addLoadedSubset(index,1);
  		debug("[PrimaryKeyList]: after");
  		this._tracker.printLoadInformations();
  	} else {
  		Object pk = element;
  		if(pk instanceof String){
  			pk = this._entity.decode((String)pk);
  		} else {
  			validatePrimaryKeyObject(pk);
  		}
  		super.add(index,pk);
  		debug("[PrimaryKeyList]: method add("+index+","+element+") before");
  		this._tracker.printLoadInformations();
  		this._tracker.addUnloadedSubset(index,1);
  		debug("[PrimaryKeyList]: after");
  		this._tracker.printLoadInformations();
  	}
  }
  
  @Override
	public boolean addAll(Collection c) {
  	boolean toReturn=true;
	for (Iterator iter = c.iterator(); iter.hasNext();) {
		Object element = iter.next();
		if(!add(element)){
			toReturn = false;
		}
	}
  	return toReturn;
  }
  @Override
	public boolean addAll(int index, Collection c) {
  	int i = index;
  	for (Iterator iter = c.iterator(); iter.hasNext();i++) {
		Object element = iter.next();
		add(i,element);
	}
  	
    return c.size() != 0; // this return implementation is copied from java.util.Vector
  }
  
  @Override
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
  
  @Override
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
  
  @Override
	public Object set(int index, Object element) {
  	Object toReturn = null;
  	if(element instanceof IDOEntity){
  		toReturn=this._entities.set(index,validateIDOEntityObject((IDOEntity)element));
  		super.set(index,((IDOEntity)element).getPrimaryKey());
  		debug("[PrimaryKeyList]: method set("+index+","+element+") before");
  		this._tracker.printLoadInformations();
  		this._tracker.setAsLoaded(index);
  		debug("[PrimaryKeyList]: after");
  		this._tracker.printLoadInformations();
  	} else {
  		Object pk = element;
  		if(pk instanceof String){
  			pk = this._entity.decode((String)pk);
  		} else {
  			validatePrimaryKeyObject(pk);
  		}
  		toReturn=super.set(index,pk);
  		this._entities.add(index,null);
  		debug("[PrimaryKeyList]: method set("+index+","+element+") before");
  		this._tracker.printLoadInformations();
  		this._tracker.setAsNotLoaded(index);
  		debug("[PrimaryKeyList]: after");
  		this._tracker.printLoadInformations();
  	}
  	return toReturn;
  }
  
  @Override
	public int indexOf(Object o) {
  	if(o instanceof IDOEntity){
  		return this._entities.indexOf(o);
  	} else {
  		return super.indexOf(o);
  	}
  }
  @Override
	public int lastIndexOf(Object o) {
  	if(o instanceof IDOEntity){
  		return this._entities.lastIndexOf(o);
  	} else {
  		return super.lastIndexOf(o);
  	}
  }
  
  
  
  	private boolean merge(IDOPrimaryKeyList l){
  		debug("[IDOPrimaryKeyList - Merge - primary key list added]: length"+ l.size());
  		int sizeBefore = this.size();
  		super.addAll(l);
  		this._entities.setSize(this.size());
		
		IDOPrimaryKeyList.copy(this._entities,l._entities,sizeBefore);
		this._tracker.add(l._tracker);
		return true;
  	}
  	
	static boolean areMergeable(IDOPrimaryKeyList l1, IDOPrimaryKeyList l2){
		return l1._entity.getClass() == l2._entity.getClass();
	}
	
	static IDOPrimaryKeyList merge(IDOPrimaryKeyList l1, IDOPrimaryKeyList l2) throws IDOFinderException{
		IDOPrimaryKeyList l = new IDOPrimaryKeyList(l1,l1._entity,((l1._prefetchSize+l2._prefetchSize)/2));
		Collections.copy(l._entities,l1._entities);
		l._tracker = (LoadTracker)l1._tracker.clone();
		l.merge(l2);
		
		return l;
	}
	
	   /**
     * Copies all of the elements from one list into another.  After the
     * operation, the index of each copied element in the destination list
     * will be identical to its index in the source list.  The destination
     * list must be at least as long as the source list.  If it is longer, the
     * remaining elements in the destination list are unaffected. <p>
     *
     * This method runs in linear time.
     *
     * @param  dest The destination list.
     * @param  src The source list.
     * @throws IndexOutOfBoundsException if the destination list is too small
     *         to contain the entire source List.
     * @throws UnsupportedOperationException if the destination list's
     *         list-iterator does not support the <tt>set</tt> operation.
     */
    private static void copy(List dest, List src, int startIndexInSrc) {
    		//implementation copied from java.util.Collections but modified
        int srcSize = src.size();
        if (srcSize > dest.size()) {
					throw new IndexOutOfBoundsException("Source does not fit in dest");
				}
        int COPY_THRESHOLD = 10;
        if (srcSize < COPY_THRESHOLD ||
            (src instanceof RandomAccess && dest instanceof RandomAccess)) {
            for (int i=0; i<srcSize; i++) {
							dest.set(i+startIndexInSrc, src.get(i));
						}
        } else {
            ListIterator di=dest.listIterator(startIndexInSrc), si=src.listIterator();
            for (int i=0; i<srcSize; i++) {
                di.next();
                di.set(si.next());
            }
        }
    }


	public class LoadTracker implements Cloneable {
//		int initialCapacity;
		private int _subsetMinLength = 100;
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
		
		boolean add(LoadTracker tracker){
			for (Iterator iter = tracker._loadedSubSets.iterator(); iter.hasNext();) {
				int[] sub = (int[]) iter.next();
				sub[FROM_INDEX_IN_ARRAY] += this._size;
				sub[TO_INDEX_IN_ARRAY] += this._size;
				this._loadedSubSets.add(sub);
			}
			this._size += tracker._size;
			return true;
		}
		
		@Override
		public Object clone(){
			LoadTracker obj = null;
				
			try {
				obj= (LoadTracker)super.clone();
				obj._loadedSubSets = (Vector)this._loadedSubSets.clone();
				obj._comparator = this._comparator;
				obj._subsetMinLength = this._subsetMinLength;
				obj._size = this._size;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return obj;
		}

		private void debugLoadedSubSets(){
		    debug("[IDOPrimaryKeyList.LoadTracker]: not loaded subsets ->");
			debug("From\tTo");
			debug("-----\t------");
			List notLoaded = getNotLoadedSubsets(0,this._size);
			if(notLoaded != null){
				Iterator iter = notLoaded.iterator();
				boolean showNulls = true;
				while (iter.hasNext()) {
					int[] item = (int[])iter.next();
					if(item != null || showNulls){
						debug(item[0]+"\t"+item[1]);
					}
				}
			}
			debug("[IDOPrimaryKeyList]: _loadedSubSets content ends");
		}

		public float getLoadRatio(){
		    if(this._size != 0){
				int NumberOfLoadedElements = 0;
				ListIterator iter = this._loadedSubSets.listIterator();
				while (iter.hasNext()) {
					//int index = iter.nextIndex();
					int[] item = (int[])iter.next();
					NumberOfLoadedElements += item[TO_INDEX_IN_ARRAY] - item[FROM_INDEX_IN_ARRAY];
				}
				return ((float)NumberOfLoadedElements)/((float)this._size);
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
		    if(fromIndex > this._size){
			    return false;
			}
//			int tIndex = Math.max(fromIndex,toIndex);
		    int tIndex = Math.min(toIndex,this._size);
			Iterator iter = this._loadedSubSets.iterator();
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
//			    debug("[IDOPrimaryKeyList]: _size = "+_size);
//				debugLoadedSubSets();
//			}
			List toReturn = new ArrayList();
			int fIndex = Math.min(fromIndex,toIndex);
			fIndex = Math.min(fIndex,this._size);
			int tIndex = Math.max(fromIndex,toIndex);
//			if (_entity.isDebugActive()){
//				debug("[IDOPrimaryKeyList]: 1");
//				debug("[IDOPrimaryKeyList]: getNotLoadedSubsets("+fIndex+","+tIndex+")");
//				debug("[IDOPrimaryKeyList]: isLoaded("+fromIndex+","+toIndex+") = "+isLoaded(fromIndex,toIndex));
//
//			}
			if(this._size == 0 || isLoaded(fromIndex,toIndex)){
			    return toReturn;
			}
			tIndex = Math.max(tIndex,fromIndex+this._subsetMinLength);
			tIndex = Math.min(tIndex,this._size);
			if(this._loadedSubSets.size() == 0){
				int [] interval = new int[2];
				interval[FROM_INDEX_IN_ARRAY] = fIndex;
				interval[TO_INDEX_IN_ARRAY] = tIndex;
//				if (_entity.isDebugActive()){
//					debug("[IDOPrimaryKeyList]: 2");
//					debug("[IDOPrimaryKeyList]: getNotLoadedSubsets("+fIndex+","+tIndex+")");
//				}
				toReturn.add(interval);
				return toReturn;
			} else {
				ListIterator iter = this._loadedSubSets.listIterator();
				while (iter.hasNext()) {
					int index = iter.nextIndex();
					int[] item = (int[])iter.next();


					if(( fIndex >= item[TO_INDEX_IN_ARRAY] && !iter.hasNext() )||(tIndex <= item[FROM_INDEX_IN_ARRAY] && (index != 0 && ((int[])this._loadedSubSets.get(index-1))[TO_INDEX_IN_ARRAY] <= fIndex))){  // interval is not part of any other interval
//						if(index == 0){
							int [] interval = new int[2];
							interval[FROM_INDEX_IN_ARRAY] = fIndex;
							interval[TO_INDEX_IN_ARRAY] = Math.min(tIndex,this._size);
//							if (_entity.isDebugActive()){
//								debug("[IDOPrimaryKeyList]: 3");
//								debug("[IDOPrimaryKeyList]: getNotLoadedSubsets("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
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
					    		interval[FROM_INDEX_IN_ARRAY] = Math.max(interval[FROM_INDEX_IN_ARRAY],((int[])this._loadedSubSets.get(toReturn.size()-1))[TO_INDEX_IN_ARRAY]);
					    	}
					    	if(this._loadedSubSets.size() > index+1){
							    interval[FROM_INDEX_IN_ARRAY] = Math.max(interval[FROM_INDEX_IN_ARRAY],((int[])this._loadedSubSets.get(index+1))[TO_INDEX_IN_ARRAY]);
						}
						int min = Math.min(tIndex,item[FROM_INDEX_IN_ARRAY]);
						interval[TO_INDEX_IN_ARRAY] = min;
//						if(_loadedSubSets.size() > index+1){
//						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])_loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
//						} else {
//						    interval[TO_INDEX_IN_ARRAY] = min;
//						}
//						if (_entity.isDebugActive()){
//							debug("[IDOPrimaryKeyList]: 4");
//							debug("[IDOPrimaryKeyList]: getNotLoadedSubsets("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						if(interval[FROM_INDEX_IN_ARRAY]<interval[TO_INDEX_IN_ARRAY]){
							fIndex = interval[TO_INDEX_IN_ARRAY];
							toReturn.add(interval);
						}
					}

					if(fIndex < item[TO_INDEX_IN_ARRAY] && tIndex > item[TO_INDEX_IN_ARRAY] ){ // interval outreaches item's interval
						int [] interval = new int[2];
						interval[FROM_INDEX_IN_ARRAY] = item[TO_INDEX_IN_ARRAY];
						int min = Math.min(tIndex,this._size);
						if(this._loadedSubSets.size() > index+1){
						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])this._loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
						} else {
						    interval[TO_INDEX_IN_ARRAY] = min;
						}
//						if (_entity.isDebugActive()){
//							debug("[IDOPrimaryKeyList]: 5");
//							debug("[IDOPrimaryKeyList]: getNotLoadedSubsets("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
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
//			debug("-------[IDOPrimaryKeyList.LoadTracker]: begin -------");
//			debug("[IDOPrimaryKeyList.LoadTracker]: Load ratio "+getLoadRatio());
//			debug("[IDOPrimaryKeyList.LoadTracker]: Size "+_size);
////			debugLoadedSubSets();
//			debugLoadedSubSets();
//////			for (Iterator iter = _loadedSubSets.iterator(); iter.hasNext();) {
//////				int[] loadedSets = (int[]) iter.next();
//////				debug("[IDOPrimaryKeyList.LoadTracker]: Loaded from "+loadedSets[FROM_INDEX_IN_ARRAY]+" to "+loadedSets[TO_INDEX_IN_ARRAY]);
//////			}
//			debug("-------[IDOPrimaryKeyList.LoadTracker]: end -------");
		}
		
		public void removeSubset(int index, int size){
			this._size -= size;
			
			int toIndex = index+size;
			for (Iterator iter = this._loadedSubSets.iterator(); iter.hasNext();) {
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
			this._size += size;
			int[] setToAdd = null;
			for (Iterator iter = this._loadedSubSets.iterator(); iter.hasNext();) {
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
				this._loadedSubSets.add(setToAdd);
				Collections.sort(this._loadedSubSets,this._comparator);
			}

		}
		
		public void addLoadedSubset(int index, int size){
			this._size += size;
			boolean isLoaded = false;
			for (Iterator iter = this._loadedSubSets.iterator(); iter.hasNext();) {
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
				this._loadedSubSets.add(interval);
				Collections.sort(this._loadedSubSets,this._comparator);
			}
		}
		
		public void addUnloadedSubset(int size){
			addUnloadedSubset(this._size,size);
		}
		
		public void addLoadedSubset(int size){
			addLoadedSubset(this._size,size);
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
			for (Iterator iter = this._loadedSubSets.iterator(); iter.hasNext();) {
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
				this._loadedSubSets.add(setToAdd);
				Collections.sort(this._loadedSubSets,this._comparator);
			}
		}
		
		/**
		 * @param fromIndex low endpoint (inclusive).
		 * @param toIndex high endpoint (exclusive).
		 */
		public void setSubsetAsLoaded(int fromIndex, int toIndex){
			if (IDOPrimaryKeyList.this._entity.isDebugActive()){
			    debug("[IDOPrimaryKeyList]: addLoadedSubSet("+fromIndex+","+toIndex+") -> begins");
				debugLoadedSubSets();
			}

			int fIndex = Math.min(fromIndex,toIndex);
			fIndex = Math.min(fIndex,this._size);
			int tIndex = Math.max(fromIndex,toIndex);
//			if (_entity.isDebugActive()){
//				debug("[IDOPrimaryKeyList]: 1");
//				debug("[IDOPrimaryKeyList]: addLoadedSubSet("+fIndex+","+tIndex+")");
//			}
//			if(isLoaded(fromIndex,toIndex)){
//			    return toReturn;
//			}
			tIndex = Math.max(tIndex,fromIndex+this._subsetMinLength);
			tIndex = Math.min(tIndex,this._size);
			if(this._loadedSubSets.size() == 0){
				int [] interval = new int[2];
				interval[FROM_INDEX_IN_ARRAY] = fIndex;
				interval[TO_INDEX_IN_ARRAY] = tIndex;
//				if (_entity.isDebugActive()){
//					debug("[IDOPrimaryKeyList]: 2");
//					debug("[IDOPrimaryKeyList]: addLoadedSubSet("+fIndex+","+tIndex+")");
//				}
				this._loadedSubSets.add(interval);
			} else {
				ListIterator iter = this._loadedSubSets.listIterator();
				while (iter.hasNext()) {
					int index = iter.nextIndex();
					int[] item = (int[])iter.next();


					if(( fIndex >= item[TO_INDEX_IN_ARRAY] && !iter.hasNext() )||(tIndex <= item[FROM_INDEX_IN_ARRAY] && (index != 0 && ((int[])this._loadedSubSets.get(index-1))[TO_INDEX_IN_ARRAY] <= fIndex))){  // interval is not part of any other interval
//						if(index == 0){
							int [] interval = new int[2];
							interval[FROM_INDEX_IN_ARRAY] = fIndex;
							interval[TO_INDEX_IN_ARRAY] = Math.min(tIndex,this._size);
//							if (_entity.isDebugActive()){
//								debug("[IDOPrimaryKeyList]: 3");
//								debug("[IDOPrimaryKeyList]: addLoadedSubSet("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
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
//							debug("[IDOPrimaryKeyList]: 4");
//							debug("[IDOPrimaryKeyList]: addLoadedSubSet("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						iter.add(interval);
					}

					if(fIndex < item[TO_INDEX_IN_ARRAY] && tIndex > item[TO_INDEX_IN_ARRAY] ){ // interval outreaches item's interval
						int [] interval = new int[2];
						interval[FROM_INDEX_IN_ARRAY] = item[TO_INDEX_IN_ARRAY];
						int min = Math.min(tIndex,this._size);
						if(this._loadedSubSets.size() > index+1){
						    interval[TO_INDEX_IN_ARRAY] = Math.min(min,((int[])this._loadedSubSets.get(index+1))[FROM_INDEX_IN_ARRAY]);
						} else {
						    interval[TO_INDEX_IN_ARRAY] = min;
						}
//						if (_entity.isDebugActive()){
//							debug("[IDOPrimaryKeyList]: 5");
//							debug("[IDOPrimaryKeyList]: addLoadedSubSet("+interval[FROM_INDEX_IN_ARRAY]+","+interval[TO_INDEX_IN_ARRAY]+")");
//						}
						iter.add(interval);
					}
				}
				Collections.sort(this._loadedSubSets,this._comparator);
			}
			if (IDOPrimaryKeyList.this._entity.isDebugActive()){
				debug("And then");
				debugLoadedSubSets();
				debug("[IDOPrimaryKeyList]: addLoadedSubSet("+fromIndex+","+toIndex+") -> ends");

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


			@Override
			public boolean equals(Object obj) {
				/**@todo: Implement this java.util.Comparator method*/
				throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
			}

		}

	}  //LoadTracker Ends

		public class IDOPrimaryKeyListIterator implements ListIterator{
			private int _index;
			private List _list;
			private boolean _hasPrevious=false;
			
			public IDOPrimaryKeyListIterator(IDOPrimaryKeyList list,int index){
				this._list=list;
				this._index=index;
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
				return this._list.size()>this._index;
			}

			/**
			 * @see java.util.ListIterator#hasPrevious()
			 */
			public boolean hasPrevious() {
				return this._hasPrevious;
			}

			/**
			 * @see java.util.Iterator#next()
			 */
			public Object next() {
				Object o =  this._list.get(nextIndex());
				this._index++;
				this._hasPrevious=true;
				return o;
			}

			/**
			 * @see java.util.ListIterator#nextIndex()
			 */
			public int nextIndex() {
				return this._index;
			}

			/**
			 * @see java.util.ListIterator#previous()
			 */
			public Object previous() {
				Object o = this._list.get(previousIndex());
				this._index=this._index-1;
				if(this._index==0){
					this._hasPrevious=false;
				}
				return o;
			}

			/**
			 * @see java.util.ListIterator#previousIndex()
			 */
			public int previousIndex() {
				return this._index-1;
			}

			/**
			 * @see java.util.Iterator#remove()
			 */
			public void remove() {
				this._list.remove(previousIndex());
			}

//			/**
//			 * @see java.util.ListIterator#set(java.lang.Object)
//			 */
//			public void set(Object o) {
//				_list.set(previousIndex(),o);
//			}
			
		    public void set(Object o) {
			 	this._list.set(this._index-1,o);
		    }
		    public void add(Object o) {
			 	this._list.add(this._index,o);
		    }

}

}