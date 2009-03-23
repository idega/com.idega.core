package com.idega.transaction;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/03/23 10:48:18 $ by $Author: civilis $
 */
@Service
@Scope("singleton")
public class TransactionContextImpl implements TransactionContext {
	
	@Transactional
	public <T> T executeInTransaction(TransactionalCallback callback) {
		
		return callback.execute();
	}
	
	@Transactional(readOnly = true)
	public <T> T executeInReadonlyTransaction(TransactionalCallback callback) {
		
		return callback.execute();
	}
}