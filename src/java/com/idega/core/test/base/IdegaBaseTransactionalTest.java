package com.idega.core.test.base;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

import com.idega.core.persistence.GenericDao;

/**
 * test extending this class requires hibarnate libs on the classpath
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/07/03 12:14:07 $ by $Author: civilis $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(transactionManager="idegaHibernateTestTransactionManager", defaultRollback=false)
@Transactional
public abstract class IdegaBaseTransactionalTest extends IdegaBaseTest {

	@Resource(name="genericDAO")
	private GenericDao genericDao;
	
	public GenericDao getGenericDao() {
		return genericDao;
	}
}