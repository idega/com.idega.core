package com.idega.util.expression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import com.idega.business.SpringBeanName;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;

import bsh.Interpreter;

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
		if (ELUtil.me == null) {
			ELUtil.me = this;
		} else {
			LOGGER.warning("Tried to repeatedly create singleton instance");
		}
	}

	public <T>T getBean(String expression) throws BeanCreationException {
		String originalExpression = expression;
		if (expression.contains(CoreConstants.DOT)) {
			String springExpr = null;
			try {
				ApplicationContext appContext = getApplicationContext();
				BeanFactory beanFactory = appContext;

				StandardEvaluationContext context = new StandardEvaluationContext();
				context.setBeanResolver(new BeanFactoryResolver(beanFactory));
				context.addPropertyAccessor(new BeanExpressionContextAccessor());

				springExpr = expression.startsWith(CoreConstants.AT) ? expression : CoreConstants.AT.concat(expression);
				final ExpressionParser parser = new SpelExpressionParser();
				Expression exp = parser.parseExpression(springExpr);

				BeanExpressionContext rootObject = null;
				if (beanFactory instanceof ConfigurableBeanFactory) {
					rootObject = new BeanExpressionContext((ConfigurableBeanFactory) beanFactory, null);
				}
				Object o = rootObject == null ?
						exp.getValue(context) :
						exp.getValue(context, rootObject);
				if (o != null) {
					@SuppressWarnings("unchecked")
					T result = (T) o;
					return result;
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error evaluating expression " + springExpr + " with Spring");
			}

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
			} else {
				throw new UnsupportedOperationException("Method binding without faces context not supported yet");
			}
		}

		expression = cleanupExp(expression);
		if (StringUtil.isEmpty(expression) || "''".equals(expression.trim())) {
			LOGGER.info("Will use original expression '" + originalExpression + "' because cleaned expression '" + expression + "' is invalid");
			expression = originalExpression;
		}

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
		if (event != null) {
			ApplicationContext ac = getApplicationContext();
			ac.publishEvent(event);
		}
	}

	public <T>T getBean(Class<T> clazz) throws BeanCreationException {
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
	public Object evaluateExpression(String exp) throws Exception {
		String beanName = getBeanName(exp);
		if (StringUtil.isEmpty(beanName)) {
			return null;
		}
		String methodName = getMethodName(exp);
		if (StringUtil.isEmpty(methodName)) {
			return null;
		}

		List<String> argsList = getArgs(exp);

		Class<?>[] classParams = new Class[argsList.size()];

		Object[] strParams = new String[argsList.size()];
		for (int i = 0; i< argsList.size(); i++) {
			classParams[i] = String.class;
			strParams[i] = argsList.get(i);
		}

		Object obj = ELUtil.getInstance().getBean(beanName);
		if (obj == null) {
			LOGGER.warning("Failed to get object for bean '" + beanName + "'");
			return null;
		}

		Object returnedObj = null;
		try {
			Method method = obj.getClass().getMethod(methodName, classParams);
			returnedObj = method.invoke(obj, strParams);
		} catch (Exception e) {
			throw new Exception("Exeption occured while trying to invoke method " + methodName + " for object " + obj + (ArrayUtil.isEmpty(classParams) ? CoreConstants.EMPTY : ", parameters: " + Arrays.asList(classParams)), e);
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

	private String getBeanName(String exp) {
		if (StringUtil.isEmpty(exp) || exp.indexOf(CoreConstants.DOT) == -1) {
			return null;
		}

		String beanName = cleanupExp(exp);
		while (true) {
			beanName = beanName.substring(0, beanName.lastIndexOf(CoreConstants.DOT));
			if (beanName.matches("[a-zA-Z0-9.]+") || beanName.indexOf(CoreConstants.DOT) == -1) {
				break;
			}
		}
		return beanName;
	}

	private String getMethodName(String exp){
		String beanName = getBeanName(exp);
		if (StringUtil.isEmpty(beanName)) {
			return null;
		}

		String methodName = cleanupExp(exp);
		int index = methodName.indexOf(CoreConstants.BRACKET_LEFT);
		if (index >= 0) {
			methodName = methodName.substring(beanName.length() + 1, index);
		} else {
			methodName = methodName.substring(beanName.length() + 1);
			methodName = "get" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
		}
		return methodName;
	}

	public List<String> getArgs(String exp) {
		List<String> returnArray = new ArrayList<>();
		int pre = exp.indexOf(CoreConstants.BRACKET_LEFT);
		if (pre >= 0) {
			String argsList = exp.substring(pre + 1, exp.lastIndexOf(CoreConstants.BRACKET_RIGHT));
			if (StringUtil.isEmpty(argsList)) {
				return returnArray;
			}

			String[] args = argsList.split(CoreConstants.COMMA);
			for (String arg: args) {
				if (StringUtil.isEmpty(arg)) {
					returnArray.add(CoreConstants.EMPTY);
					continue;
				}

				arg = arg.trim();
				if (arg.startsWith(CoreConstants.QOUTE_SINGLE_MARK)) {
					arg = arg.replaceFirst(CoreConstants.QOUTE_SINGLE_MARK, CoreConstants.EMPTY);
				}
				if (arg.endsWith(CoreConstants.QOUTE_SINGLE_MARK)) {
					arg = arg.substring(0, arg.length() - 1);
				}

				returnArray.add(arg);
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

	public static MethodExpression createMethodExpression(FacesContext context, String expression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
		Application app = context.getApplication();
		ExpressionFactory exprFactory = app.getExpressionFactory();
		ELContext elContext = context.getELContext();
		return exprFactory.createMethodExpression(elContext, expression, expectedReturnType, expectedParamTypes);
	}

}