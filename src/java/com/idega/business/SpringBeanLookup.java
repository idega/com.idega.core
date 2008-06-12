package com.idega.business;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import com.idega.idegaweb.IWApplicationContext;

/**
 * WARNING: should be used only if it's not possible to get beans in other ways (through jsf expression for example) 
 * 
 * <p>Glue code for legacy non-spring beans. Lookup spring bean by interface. Strive as much not to use this class.</p>
 * <p>This class should be mainly used for legacy code, which used to lookup bean by using IBOLookup.getSessionInstance</p>
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.15 $
 *
 * Last modified: $Date: 2008/06/12 18:24:50 $ by $Author: civilis $
 *
 */
public class SpringBeanLookup {
	
	private static SpringBeanLookup me;
	
	public static synchronized SpringBeanLookup getInstance() {
		
		if (me == null)
			me = new SpringBeanLookup();
		
		return me;
	}
	
	private ApplicationContext appContext;

	protected SpringBeanLookup() { 	}
	
	/**
	 * @deprecated use com.idega.util.expression.ELUtil getBean method
	 * Additionaly retrieves ServletContext from HttpSession instance
	 * 
	 * @param session - current http session
	 * @param clazz - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	@Deprecated
	public <T>T getSpringBean(HttpSession session, Class<T> clazz) {
		
		return getSpringBean(session.getServletContext(), clazz);
	}
	
	/**
	 * @deprecated use com.idega.util.expression.ELUtil getBean method
	 * @param ctx - current ServletContext
	 * @param clazz - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	@Deprecated
	public <T>T getSpringBean(ServletContext ctx, Class<T> clazz) {
		
		if(!clazz.isAnnotationPresent(SpringBeanName.class))
			throw new RuntimeException("Interface is not annotated with "+SpringBeanName.class.getName()+" annotation");
		
		SpringBeanName bname = (SpringBeanName)clazz.getAnnotation(SpringBeanName.class);

		@SuppressWarnings("unchecked")
		T bean = (T)getSpringBean(ctx, bname.value());
		return bean;
	}
	
	/**
	 * @deprecated use com.idega.util.expression.ELUtil getBean method
	 * @param ctx
	 * @param springBeanIdentifier
	 * @return
	 */
	@Deprecated
	public Object getSpringBean(ServletContext ctx, String springBeanIdentifier) {
		
		ApplicationContext ac = getAppContext(ctx);
		return ac.getBean(springBeanIdentifier);
	}
	
	

	public void setAppContext(ApplicationContext appContext, ServletContext context) {
		this.appContext = appContext;
	}
	
	protected ApplicationContext getAppContext(ServletContext ctx) {
		if(this.appContext==null){
			return org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
		}
		else{
			return this.appContext;
		}
	}
	
	/**
	 * @deprecated use com.idega.util.expression.ELUtil getBean method
	 * Retrieves ServletContext from IWMainApplication instance
	 * 
	 * @param iwac - current IWApplicationContext
	 * @param clazz - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	@Deprecated
	public <T>T getSpringBean(IWApplicationContext iwac, Class<T> clazz) {
		
		return getSpringBean(iwac.getIWMainApplication().getServletContext(), clazz);
	}
	
	public void publishEvent(ServletContext ctx, ApplicationEvent event) {
		
		ApplicationContext ac = getAppContext(ctx);
		ac.publishEvent(event);
	}
}