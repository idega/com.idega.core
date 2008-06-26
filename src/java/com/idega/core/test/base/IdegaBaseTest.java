package com.idega.core.test.base;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.GenericDao;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/26 14:14:11 $ by $Author: civilis $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/com/idega/core/test/base/IdegaBaseTest-applicationContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
@Transactional
public abstract class IdegaBaseTest extends TestCase implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	@Autowired
	private GenericDao genericDao;
	
	public void setApplicationContext(ApplicationContext actx)
			throws BeansException {
		this.applicationContext = actx;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public GenericDao getGenericDao() {
		return genericDao;
	}
}