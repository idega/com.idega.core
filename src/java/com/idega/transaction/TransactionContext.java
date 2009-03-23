package com.idega.transaction;

import org.springframework.transaction.annotation.Transactional;

/**
 * Adds possiblity to run transactional routines where designating transactional boundaries is not
 * possible, or inconvenient.
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/03/23 10:48:18 $ by $Author: civilis $
 */
public interface TransactionContext {
	
	@Transactional
	public abstract <T> T executeInTransaction(TransactionalCallback callback);
	
	@Transactional(readOnly = true)
	public abstract <T> T executeInReadonlyTransaction(
	        TransactionalCallback callback);
}