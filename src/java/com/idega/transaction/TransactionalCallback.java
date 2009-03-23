package com.idega.transaction;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/03/23 10:48:18 $ by $Author: civilis $
 */
public interface TransactionalCallback {
	
	public <T> T execute();
}