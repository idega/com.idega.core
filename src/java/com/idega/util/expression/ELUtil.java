package com.idega.util.expression;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/17 12:17:31 $ by $Author: civilis $
 *
 */
public class ELUtil {
	
	private static ELUtil me = new ELUtil();
	public static final String EXPRESSION_BEGIN	=	"#{";
	public static final String EXPRESSION_END	=	"}";
	
	public static ELUtil getInstance() {
		return me;
	}
	
	protected ELUtil() { 	}
	
	public <T>T getBean(String expression, ServletContext sctx) {
		
		if(expression.contains(CoreConstants.DOT)) {
			
			FacesContext fctx = FacesContext.getCurrentInstance();
			
			if(fctx != null) {
			
				if(!expression.startsWith(EXPRESSION_BEGIN)) {
					expression = EXPRESSION_BEGIN + expression;
				}
				
				if(!expression.endsWith(EXPRESSION_END)) {
					expression += EXPRESSION_END;
				}
				
				ValueBinding vb = fctx.getApplication().createValueBinding(expression);
				
				@SuppressWarnings("unchecked")
				T bean = (T)vb.getValue(fctx);
				return bean;
				
			} else 
				throw new UnsupportedOperationException("Method binding without faces context not supported yet");
		}
			
		expression = cleanupExp(expression);

		if(sctx == null) {
			sctx = IWMainApplication.getDefaultIWMainApplication().getServletContext();
		}
		
		ApplicationContext ac = getAppContext(sctx);
		@SuppressWarnings("unchecked")
		T val = (T)ac.getBean(expression);
		return val;
	}
	
	public void autowire(Object obj) {

		autowire(obj, FacesContext.getCurrentInstance());
	}
	
	public void autowire(Object obj, FacesContext fctx) {
		
		final ServletContext sctx = getServletContext(fctx);
		autowire(obj, sctx);
	}
	
	public void autowire(Object obj, ServletContext sctx) {

		ApplicationContext ac = getAppContext(sctx);
		ac.getAutowireCapableBeanFactory().autowireBeanProperties(obj, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT, false);
	}
	
	public <T>T getBean(String expression) {

		@SuppressWarnings("unchecked")
		T bean = (T)getBean(expression, FacesContext.getCurrentInstance());
		return bean;
	}
	
	public <T>T getBean(String expression, FacesContext fctx) {

		final ServletContext sctx = getServletContext(fctx);
		
		@SuppressWarnings("unchecked")
		T bean = (T)getBean(expression, sctx);
		return bean;
	}
	
	private ServletContext getServletContext(FacesContext fctx) {
		
		final ServletContext sctx;
		
		if(fctx != null) {
			
			sctx = (ServletContext)fctx.getExternalContext().getContext();
		} else
			sctx = null;
		
		return sctx;
	}
	
	private static String cleanupExp(String exp) {
		
		if(exp.startsWith(EXPRESSION_BEGIN)) {
			exp = exp.substring(EXPRESSION_BEGIN.length());
		}
		
		if(exp.endsWith(EXPRESSION_END)) {
			exp = exp.substring(0, exp.length()-EXPRESSION_END.length());
		}
		
		return exp;
	}
	
	protected ApplicationContext getAppContext(ServletContext ctx) {
		return org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
	}
}