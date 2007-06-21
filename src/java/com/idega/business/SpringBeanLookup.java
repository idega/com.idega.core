package com.idega.business;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.springframework.context.ApplicationContext;

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
	
	public static SpringBeanLookup getInstance() {
		
		if (me == null) {
			
			synchronized (SpringBeanLookup.class) {
				if (me == null) {
					me = new SpringBeanLookup();
				}
			}
		}
		
		return me;
	}
	
	protected SpringBeanLookup() { 	}
	
	/**
	 * 
	 * 
	 * 
	 * @param session - current http session
	 * @param interface_class - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public Object getSpringBean(HttpSession session, Class interface_class) {
		
		if(!interface_class.isAnnotationPresent(SpringBeanName.class))
			return null;
		
		SpringBeanName bname = (SpringBeanName)interface_class.getAnnotation(SpringBeanName.class);
		
		ServletContext servlet_ctx = session != null ? session.getServletContext() :
			(ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
		
		 ApplicationContext ac = org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(
				 servlet_ctx);
		 return ac.getBean(bname.value());
	}
	
	/**
	 * Retrieves ServletContext from FacesContext current instance
	 * 
	 * 
	 * @param session - current http session
	 * @param interface_class - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public Object getSpringBean(Class interface_class) {
		
		return getSpringBean(null, interface_class);
	}
	
}