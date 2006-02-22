/*
 * $Id: IdegaTransactionManager.java,v 1.17 2006/02/22 20:52:49 laddi Exp $ Created
 * in 2001 by Tryggvi Larusson
 * 
 * Copyright (C) 2001-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import com.idega.data.GenericEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.ThreadContext;
import com.idega.util.database.ConnectionBroker;

/**
 * <p>
 * idegaWeb Implementation of the JTA (javax.transaction) TransactionManager
 * interface.<br>
 * This class works together with ConnectionBroker to mark a Connection for a
 * transaction and associate it with the current Thread.
 * </p>
 * Last modified: $Date: 2006/02/22 20:52:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.17 $
 */
public class IdegaTransactionManager implements javax.transaction.TransactionManager, Singleton {

	static String transaction_attribute_name = "idega_transaction";
	private static Instantiator instantiator = new Instantiator() {
		
		public Object getInstance(Object datasource) {
			return new IdegaTransactionManager((String) datasource);
		}
	};
	private String datasource = com.idega.util.database.ConnectionBroker.DEFAULT_POOL;

	/**
	 * 
	 * Only this class can construct itself
	 * 
	 */
	public String getDatasource() {
		return datasource;
	}

	protected IdegaTransactionManager(String datasource) {
		if (datasource != null) {
			this.datasource = datasource;
		}
	}

	/**
	 * @deprecated use getInstance(String datasource) instead
	 */
	public static TransactionManager getInstance() {
		return getInstance(ConnectionBroker.DEFAULT_POOL);
	}

	/**
	 * 
	 * <p>
	 * The only way to get an instance of the TransactionManager
	 * </p>
	 * @param datasource
	 * @return
	 */
	public static TransactionManager getInstance(String datasource) {
		// uses datasource as identifier, that is there is a singleton for each
		// datasource!
		return (IdegaTransactionManager) SingletonRepository.getRepository().getInstanceUsingIdentifier(
				IdegaTransactionManager.class, instantiator, datasource, datasource);
	}

	/**
	 * Looks up the datasource for the returing entity, and calls
	 * getInstance(datasource)
	 * 
	 * @param returningEntityInterfaceClass
	 * @return
	 */
	public static TransactionManager getInstance(Class returningEntityInterfaceClass) {
		try {
			IDOHome home = IDOLookup.getHome(returningEntityInterfaceClass);
			return getInstance(home.getDatasource());
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
			return getInstance();
		}
	}

	/**
	 * 
	 * Start a transaction, constructs a new Transaction and associates it with
	 * the current thread.
	 * 
	 */
	public void begin() throws NotSupportedException, SystemException {
		Transaction trans = null;
		try {
			trans = getTransaction();
		}
		catch (Exception ex) {
		}
		/*
		 * if(transactionAlreadyBegun){ throw new
		 * NotSupportedException("Transaction already begun, nested transactions
		 * not currently supported"); }
		 */
		if (trans == null) {
			trans = new IdegaTransaction(this.datasource);
		}
		begin(trans);
		// trans.registerSynchronization(new IdegaTransactionSynchronization());
		// ThreadContext.getInstance().setAttribute(Thread.currentThread(),transaction_attribute_name,trans);
	}

	public void begin(Transaction trans) throws NotSupportedException, SystemException {
		/*
		 * boolean transactionAlreadyBegun=false; boolean
		 * startingValidUnderTransaction=true; Transaction trans2=null; try{
		 * trans2 = getTransaction(); if(trans2!=null){
		 * transactionAlreadyBegun=true; } } catch(Exception ex){ }
		 */
		/*
		 * if(transactionAlreadyBegun){ if(trans2.equals(trans)){
		 * ((IdegaTransaction)trans2).beginSubTransaction(); } else{ throw new
		 * NotSupportedException("Nested transaction is invalid (does not equal
		 * to the supertransaction)"); } //throw new
		 * NotSupportedException("Transaction already begun, nested transactions
		 * not currently supported"); } else{ //Transaction trans = new
		 * IdegaTransaction(this.datasource);
		 * //trans.registerSynchronization(new
		 * IdegaTransactionSynchronization());
		 *  }
		 */
		((UserTransaction) trans).begin();
	}

	/**
	 * 
	 * Commits the current transaction and deassociates it with the current
	 * thread.
	 * 
	 */
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			java.lang.SecurityException, java.lang.IllegalStateException, SystemException {
		Transaction transaction = getTransaction();
		transaction.commit();
	}

	public int getStatus() throws SystemException {
		return getTransaction().getStatus();
	}

	/**
	 * 
	 * Returns the current Transaction, If no transaction has been begun, it
	 * creates a new (unassigned) Transaction object
	 */
	public Transaction getTransaction() throws SystemException {
		Transaction trans = (Transaction) ThreadContext.getInstance().getAttribute(Thread.currentThread(),
				getTransactionAttributeName());
		if (trans == null) {
			/**
			 * Changed -- The transactionManager now creates a new (empty)
			 * transaction
			 */
			// throw new SystemException("Transaction not set");
			trans = new IdegaTransaction(this.datasource);
		}
		return trans;
	}

	/**
	 * 
	 * UNIMPLEMENTED
	 * 
	 */
	public void resume(Transaction tobj) throws InvalidTransactionException, java.lang.IllegalStateException,
			SystemException {
		// Transaction trans = getTransaction();
	}

	/**
	 * 
	 * Rollbacks the current transaction and deassociates it with the current
	 * thread.
	 * 
	 */
	public void rollback() throws java.lang.IllegalStateException, java.lang.SecurityException, SystemException {
		Transaction transaction = getTransaction();
		transaction.rollback();
	}

	public void setRollbackOnly() throws java.lang.IllegalStateException, SystemException {
		Transaction trans = getTransaction();
		trans.setRollbackOnly();
	}

	/**
	 * 
	 * UNIMPLEMENTED
	 * 
	 */
	public void setTransactionTimeout(int seconds) throws SystemException {
		// Transaction trans = getTransaction();
	}

	/**
	 * 
	 * UNIMPLEMENTED
	 * 
	 */
	public Transaction suspend() throws SystemException {
		Transaction trans = getTransaction();
		return trans;
	}

	/**
	 * 
	 * Returns true if the TransactionManager has bound a Transaction Object to
	 * the current Thread
	 * 
	 */
	public boolean hasCurrentThreadBoundTransaction() {
		/*
		 * try{ Transaction trans = getTransaction(); } catch(SystemException
		 * ex){ return false; } return true;
		 */
		Transaction obj = null;
		try {
			obj = (Transaction) ThreadContext.getInstance().getAttribute(Thread.currentThread(),
					getTransactionAttributeName());
			if (obj == null) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (Exception ex) {
			return false;
		}
	}

	public void setEntity(GenericEntity entity) {
		this.datasource = entity.getDatasource();
	}

	private String getTransactionAttributeName() {
		return transaction_attribute_name + "_" + this.datasource;
	}
}
