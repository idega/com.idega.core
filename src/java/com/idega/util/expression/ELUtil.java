package com.idega.util.expression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import bsh.Interpreter;

import com.idega.business.SpringBeanName;
import com.idega.util.CoreConstants;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.9 $
 *
 * Last modified: $Date: 2009/05/15 07:22:30 $ by $Author: valdas $
 *
 */

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ELUtil implements ApplicationContextAware {

	private ApplicationContext applicationcontext;

	private static ELUtil me;
	private static Logger LOGGER = Logger.getLogger(ELUtil.class.getName());

	public static final String	EXPRESSION_BEGIN	=	"#{",
								EXPRESSION_END	=	"}";

	private Interpreter interpreter;

	public static ELUtil getInstance() {
		return me;
	}

	public ELUtil() {
		//should be created by spring, so no need to synchronize
		if(ELUtil.me == null) {
			ELUtil.me = this;
		}
		else {
			LOGGER.warning("Tried to repeatedly create singleton instance");
		}
	}

	public <T>T getBean(String expression) {
		if (expression.contains(CoreConstants.DOT)) {
			FacesContext fctx = FacesContext.getCurrentInstance();
			if (fctx != null) {
				if (!expression.startsWith(EXPRESSION_BEGIN)) {
					expression = EXPRESSION_BEGIN + expression;
				}
				if (!expression.endsWith(EXPRESSION_END)) {
					expression += EXPRESSION_END;
				}

				ValueExpression ve = fctx.getApplication().getExpressionFactory().createValueExpression(fctx.getELContext(), expression, Object.class);

				@SuppressWarnings("unchecked")
				T bean = (T) ve.getValue(fctx.getELContext());
				return bean;
			} else
				throw new UnsupportedOperationException("Method binding without faces context not supported yet");
		}

		expression = cleanupExp(expression);

		ApplicationContext ac = getApplicationContext();
		@SuppressWarnings("unchecked")
		T val = (T) ac.getBean(expression);
		return val;
	}

	public void autowire(Object obj) {
		ApplicationContext ac = getApplicationContext();
		ac.getAutowireCapableBeanFactory().autowireBean(obj);
	}

	public static String cleanupExp(String exp) {
		if (exp.startsWith(EXPRESSION_BEGIN)) {
			exp = exp.substring(EXPRESSION_BEGIN.length());
		}

		if (exp.endsWith(EXPRESSION_END)) {
			exp = exp.substring(0, exp.length()-EXPRESSION_END.length());
		}

		return exp;
	}

	public ApplicationContext getApplicationContext() {
		return applicationcontext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
		this.applicationcontext = applicationcontext;
	}

	public void publishEvent(ApplicationEvent event) {
		ApplicationContext ac = getApplicationContext();
		ac.publishEvent(event);
	}

	public <T>T getBean(Class<T> clazz) {
		if(!clazz.isAnnotationPresent(SpringBeanName.class)) {
			throw new RuntimeException("Interface is not annotated with "+SpringBeanName.class.getName()+" annotation");
		}

		SpringBeanName bname = clazz.getAnnotation(SpringBeanName.class);

		@SuppressWarnings("unchecked")
		T bean = (T) getBean(bname.value());
		return bean;
	}

	/**
	 * Evaluates expression
	 *
	 * @param exp
	 * @return
	 * @throws Exception
	 */
	public Object evaluateExpression(String exp) throws Exception{
		//TODO: use unified EL resolvers???

		String beanName = getBeanName(exp);
		String methodName = getMethodName(exp);
		List<String> argsList = getArgs(exp);

		Class<?>[] classParams = new Class[argsList.size()];

		Object[] strParams = new String[argsList.size()];
		for(int i = 0;i<argsList.size();i++){
			classParams[i] = String.class;
			strParams[i] = argsList.get(i);

		}

		Object obj = ELUtil.getInstance().getBean(beanName);
		Object returnedObj = null;
		try {
			Method method = obj.getClass().getMethod(methodName, classParams);
			returnedObj = method.invoke(obj, strParams);
		} catch (Exception e) {
			throw new Exception("Exeption accured while trying to invoke method " + methodName, e);
		}

		returnedObj = getPostConditionValue(exp, returnedObj, "==");
		returnedObj = getPostConditionValue(exp, returnedObj, "!=");

		return returnedObj;
	}

	private Object getPostConditionValue(String exp, Object returnedObj, String condition) {
		if (exp == null || returnedObj == null) {
			return returnedObj;
		}

		int conditionIndex = exp.indexOf(condition);
		if (conditionIndex == -1) {
			return returnedObj;
		}

		String conditionParameter = exp.substring(conditionIndex + condition.length());
		conditionParameter = cleanupExp(conditionParameter);

		String expression = null;
		if (returnedObj instanceof Boolean || returnedObj instanceof Number) {
			expression = String.valueOf(returnedObj).concat(condition).concat(conditionParameter);
		}	//	More expressions can be added here

		if (expression == null) {
			return returnedObj;
		}

		try {
			returnedObj = getInterpreter().eval(expression);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error evaluating expression: " + expression, e);
			return returnedObj;
		}

		return returnedObj;
	}

	private String getBeanName(String exp){
		String beanName = cleanupExp(exp);
		while(true){
			beanName = beanName.substring(0, beanName.lastIndexOf(CoreConstants.DOT));
			if (beanName.matches("[a-zA-Z0-9.]+") || beanName.indexOf(CoreConstants.DOT) == -1) {
				break;
			}
		}
		return beanName;
	}

	private String getMethodName(String exp){
		String beanName = getBeanName(exp);
		String methodName = cleanupExp(exp);
		int index = methodName.indexOf("(");
		if(index >= 0){
			methodName = methodName.substring(beanName.length()+1, index);
		} else {
			methodName = methodName.substring(beanName.length()+1);
			methodName = "get"+Character.toUpperCase(methodName.charAt(0))+methodName.substring(1);
		}
		return methodName;
	}

	private List<String> getArgs(String exp){
		List<String> returnArray = new ArrayList<String>();
		int pre = exp.indexOf("(");
		if(pre >= 0){
			String argsList = exp.substring( pre+1, exp.lastIndexOf(")"));
			String removedApo = argsList.replaceAll("'", "");

			StringTokenizer tokenizer = new  StringTokenizer(removedApo,",");
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				returnArray.add(token.trim());
			}
		}
		return returnArray;
	}

	private Interpreter getInterpreter() {
		if (interpreter == null) {
			interpreter = new Interpreter();
		}
		return interpreter;
	}


	public static boolean isExpression(String expression) {
		if(expression == null){
			return false;
		}
		int open = expression.indexOf("#{");
		if(open >= 0){
			int close = expression.indexOf("}",open);
			return (close >= 0);
		}
		return false;
	}


	public static ValueExpression createValueExpression(FacesContext fContext, String expression, Class<?> expectedReturnType) {
		// get application from faces context
		Application app = fContext.getApplication();
		ExpressionFactory exprFactory = app.getExpressionFactory();
		// getting the ELContext from faces context
		ELContext elContext = fContext.getELContext();
		// creating value expression with the help of the expression factory and the ELContext
		return exprFactory.createValueExpression(elContext, expression, expectedReturnType);
	}

	public static MethodExpression createMethodExpression(FacesContext context, String expression, Class<?> expectedReturnType,
			Class<?>[] expectedParamTypes) {
		Application app = context.getApplication();
		ExpressionFactory exprFactory = app.getExpressionFactory();
		ELContext elContext = context.getELContext();
		return exprFactory.createMethodExpression(elContext, expression, expectedReturnType, expectedParamTypes);
	}
}