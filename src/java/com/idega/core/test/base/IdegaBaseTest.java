package com.idega.core.test.base;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/08/07 17:27:17 $ by $Author: civilis $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public abstract class IdegaBaseTest extends TestCase implements ApplicationContextAware {
	
	public static final String testSystemProp = "idega-test";
	public static final String trueVal = "true";

	private ApplicationContext applicationContext;
	
	@Before
	public void before() {
		
		System.setProperty(IdegaBaseTest.testSystemProp, trueVal);
	}
	
	public void setApplicationContext(ApplicationContext actx)
			throws BeansException {
		this.applicationContext = actx;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}