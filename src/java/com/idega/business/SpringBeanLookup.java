package com.idega.business;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.springframework.context.ApplicationContext;

import com.idega.idegaweb.IWApplicationContext;

/**
 * <p>Glue code for legacy non-spring beans. Lookup spring bean by interface. Strive as much not to use this class.</p>
 * <p>This class should be mainly used for legacy code, which used to lookup bean by using IBOLookup.getSessionInstance</p>
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 *
 */
public class SpringBeanLookup {
	
	private static SpringBeanLookup me;
	
	public static synchronized SpringBeanLookup getInstance() {
		
		if (me == null)
			me = new SpringBeanLookup();
		
		return me;
	}
	
	protected SpringBeanLookup() { 	}
	
	/**
	 * 
	 * Additionaly retrieves ServletContext from HttpSession instance
	 * 
	 * @param session - current http session
	 * @param interface_class - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public Object getSpringBean(HttpSession session, Class interface_class) {
		
		return getSpringBean(session.getServletContext(), interface_class);
	}
	
	/**
	 * 
	 * @param ctx - current ServletContext
	 * @param interface_class - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public Object getSpringBean(ServletContext ctx, Class interface_class) {
		
		if(!interface_class.isAnnotationPresent(SpringBeanName.class))
			return null;
		
		SpringBeanName bname = (SpringBeanName)interface_class.getAnnotation(SpringBeanName.class);
		
		ApplicationContext ac = org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(
				ctx);
		return ac.getBean(bname.value());
	}
	
	/**
	 * Retrieves ServletContext from IWMainApplication instance
	 * 
	 * @param iwac - current IWApplicationContext
	 * @param interface_class - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public Object getSpringBean(IWApplicationContext iwac, Class interface_class) {
		
		return getSpringBean(iwac.getIWMainApplication().getServletContext(), interface_class);
	}
}