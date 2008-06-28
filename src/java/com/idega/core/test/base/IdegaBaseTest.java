package com.idega.core.test.base;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/06/28 19:05:12 $ by $Author: civilis $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public abstract class IdegaBaseTest extends TestCase implements ApplicationContextAware {
	
	public static final String testSystemProp = "idega-test";

	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext actx)
			throws BeansException {
		this.applicationContext = actx;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}