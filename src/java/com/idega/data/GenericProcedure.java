/*
 * $Id: GenericProcedure.java,v 1.4 2006/02/22 20:52:47 laddi Exp $
 * Created on 31.8.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import com.idega.data.query.SelectQuery;
import com.idega.util.database.ConnectionBroker;


/**
 * 
 *  Last modified: $Date: 2006/02/22 20:52:47 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.4 $
 */
public abstract class GenericProcedure implements IDOProcedure {
	
	private String _dataSource = GenericEntity.DEFAULT_DATASOURCE;
	public GenericProcedure(){
		super();
	}
	
	public abstract Class getIDOEntityInterfaceClass();
	protected abstract String getCreateProcedureScript(DatastoreInterface i);
    protected abstract boolean isSupportedForDatabase(DatastoreInterface i);
	
	/**
	 * This method should be overwritten in subclasses that use the method
	 * <code>executeFindProsedure</code> to make sure that the entities are 
	 * loaded in the same order as the prosedure returns them
	 * @return
	 */
	protected SelectQuery getLoadQueryConstraints(){
		return null;
	}
	
	
	protected Collection idoExecuteFindProcedure(Object[] parameters) throws IDOFinderException{
		try {
			return DatastoreInterface.getInstance(this).executeFindProcedure(getDatasource(),this,parameters);
		}
		catch (SQLException e) {
			throw new IDOFinderException(e);
		}
	}
	
	protected Object idoExecuteGetProcedure(Object[] parameters) throws IDOException {
		try {
			return DatastoreInterface.getInstance(this).executeGetProcedure(getDatasource(),this,parameters);
		}
		catch (SQLException e) {
			throw new IDOException(e);
		}
	}
	
	protected Object executeProcedure(Object[] parameters,boolean returnColletion) throws IDOException {
		try {
			return DatastoreInterface.getInstance(this).executeProcedure(getDatasource(),this,parameters,returnColletion);
		}
		catch (SQLException e) {
			throw new IDOException(e);
		}
	}
	
	protected Collection executeFindProsedure(Object[] parameters)throws IDOFinderException {
		return loadEntityCollection(idoExecuteFindProcedure(parameters),getLoadQueryConstraints());
	}
	
	private Collection loadEntityCollection(Collection pks, SelectQuery queryConstarints) throws IDOFinderException {
		//TODO optimize: queryConstarints is not used at the moment, it needs to be implemented in IDOPrimaryKeyList.
		return new IDOEntityList(new IDOPrimaryKeyList(pks,getIDOEntityInterfaceClass(),DatastoreInterface.getInstance(this).getOptimalEJBLoadFetchSize()));
	}
	
	
	/**
	 * Should be overwritten in sub-classes in the way that it returns the same result as <code>executeProcedure(Object[] parameters)</code> but uses java code instead of stored procedure.
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	protected Object executeFallBackProcedure(Object[] parameters) throws Exception {
		throw new UnsupportedOperationException("The fallback procedure is not implemented yet.  Either you need to implement this method here or call isAvailable() before you try to use this procedure and handle it there.");
	}	
    protected abstract Object executeProcedure(Object[] parameters) throws Exception ;
	
	/**
	 * This method executes the appropriate prosedure according to the capabilities of the
	 * datasource.  This method could, in subclasses, be wrapped into a nicer method that returns the 
	 * appropriate classtype, e.g. Collection if the executeProsedure and executeFallBackProsedure are 
	 * implemented to return Collection.
	 * @param parameters
	 * @return
	 */
	public Object getResult(Object[] parameters) throws Exception {
		if(isAvailable()){
			return executeProcedure(parameters);
		} else {
			return executeFallBackProcedure(parameters);
		}
	}
	
	
	public String getDatasource() {
		return _dataSource;
	}
	
	/**
	 * Gets a databaseconnection identified by the datasourceName
	 */
	public Connection getConnection(String datasourceName) throws SQLException {
		return ConnectionBroker.getConnection(datasourceName);
	}
	/**
	 * Gets the default database connection
	 */
	public Connection getConnection() throws SQLException {
		return ConnectionBroker.getConnection(getDatasource());
	}
	/**
	 * Frees the connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(String datasourceName, Connection connection) {
		ConnectionBroker.freeConnection(datasourceName, connection);
	}
	/**
	 * Frees the default connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(Connection connection) {
		ConnectionBroker.freeConnection(getDatasource(), connection);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.data.IDOProcedure#doesExist()
	 */
	public boolean doesExistInDatabase(DatastoreInterface i) {
		try {
			return i.hasStoredProcedure(getName());
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
    public boolean isAvailable(){
    		return isAvailable(DatastoreInterface.getInstance(this));
    }
    
    public boolean isAvailable(DatastoreInterface i){
    		return (isSupportedForDatabase(i) && doesExistInDatabase(i));
    }
	
}
