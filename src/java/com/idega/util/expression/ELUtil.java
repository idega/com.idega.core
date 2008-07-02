package com.idega.util.expression;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.SpringBeanName;
import com.idega.util.CoreConstants;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/07/02 19:26:37 $ by $Author: civilis $
 *
 */
@Scope("singleton")
@Service
public class ELUtil implements ApplicationContextAware {

	private ApplicationContext applicationcontext;
	private static ELUtil me;
	public static final String EXPRESSION_BEGIN	=	"#{";
	public static final String EXPRESSION_END	=	"}";
	
	public static ELUtil getInstance() {
		return me;
	}
	
	public ELUtil() {
		
//		should be created by spring, so no need to synchronize
		if(ELUtil.me == null)
			ELUtil.me = this;
		else
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Tried to repeatedly create singleton instance");
	}
	
	public <T>T getBean(String expression) {
		
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

		ApplicationContext ac = getApplicationContext();
		@SuppressWarnings("unchecked")
		T val = (T)ac.getBean(expression);
		return val;
	}
	
	public void autowire(Object obj) {

		ApplicationContext ac = getApplicationContext();
		ac.getAutowireCapableBeanFactory().autowireBeanProperties(obj, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT, false);
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
	
	public ApplicationContext getApplicationContext() {
		
		return applicationcontext;
	}
	
	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		this.applicationcontext = applicationcontext;
	}
	
	public void publishEvent(ApplicationEvent event) {
		
		ApplicationContext ac = getApplicationContext();
		ac.publishEvent(event);
	}
	
	public <T>T getBean(Class<T> clazz) {
		
		if(!clazz.isAnnotationPresent(SpringBeanName.class))
			throw new RuntimeException("Interface is not annotated with "+SpringBeanName.class.getName()+" annotation");
		
		SpringBeanName bname = (SpringBeanName)clazz.getAnnotation(SpringBeanName.class);

		@SuppressWarnings("unchecked")
		T bean = (T)getBean(bname.value());
		return bean;
	}
}