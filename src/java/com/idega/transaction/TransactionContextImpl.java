package com.idega.transaction;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $ Last modified: $Date: 2009/03/23 14:14:37 $ by $Author: civilis $
 */
@Service
@Scope("singleton")
public class TransactionContextImpl implements TransactionContext {
	
	@SuppressWarnings("unchecked")
	@Transactional
	public <T> T executeInTransaction(TransactionalCallback callback) {
		
		Object result = callback.execute();
		return (T) result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public <T> T executeInReadonlyTransaction(TransactionalCallback callback) {
		
		Object result = callback.execute();
		return (T) result;
	}
}