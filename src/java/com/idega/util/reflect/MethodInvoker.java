package com.idega.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * A utility class to invoke methods by reflection.
 * Title:       idega Reflection utility classes
 * Description:
 * Copyright:    Copyright (c) 2001-2003
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1
 */

public class MethodInvoker implements Singleton {

  private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new MethodInvoker();}};

  private MethodInvoker(){
  }
 /**
  * The default way of getting an instance of this class.
  * @return the singleton instance of this class
  */
  public static MethodInvoker getInstance(){
  	return (MethodInvoker) SingletonRepository.getRepository().getInstance(MethodInvoker.class, instantiator);
  }


  /**
   * Invoke a method of object instance of name methodName, where that method does not take any arguments
   * @param instance The instance of the object to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public  Object invokeMethodWithNoParameters(Object instance,String methodName)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	return invokeMethod(instance,MethodFinder.getInstance().getMethodWithNameAndNoParameters(instance.getClass(),methodName),null);
  }
  /**
   * Invoke the static method of class objectClass of name methodName, where that method does not take any arguments
   * @param objectClass The class to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public Object invokeStaticMethodWithNoParameters(Class objectClass,String methodName)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	Method method = MethodFinder.getInstance().getMethodWithNameAndNoParameters(objectClass,methodName);
	return this.invokeStaticMethod(method,null);
  }
  /**
   * Invoke the static method of class of name objectClassName of name methodName, where that method does not take any arguments
   * @param objectClassName The fully qualified class name to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   */
  public Object invokeStaticMethodWithNoParameters(String objectClassName,String methodName)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException{
	Class objectClass = Class.forName(objectClassName);
	return invokeStaticMethodWithNoParameters(objectClass,methodName);
  }
  /**
   * Invoke the static method of class of name objectClassName of name methodName, where that method does only take in one parameter that may not be null
   * @param objectClassName The fully qualified class name to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   */
  public Object invokeStaticMethodWithOneParameter(String objectClassName,String methodName,Object argument)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException{
	Class objectClass = Class.forName(objectClassName);
	Method method = MethodFinder.getInstance().getMethodWithNameAndOneParameter(objectClass,methodName,argument.getClass());
	Object[] params = {argument};
	return this.invokeStaticMethod(method,params);
  }
  
  /**
   * Invoke the static method of class of name objectClassName of name methodName, where that method does only take in one parameter that may not be null
   * @param objectClassName The fully qualified class name to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   */
  public Object invokeStaticMethodWithOneIntParameter(String objectClassName,String methodName,int argument)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException{
	return invokeStaticMethodWithOneParameter(objectClassName,methodName,new Integer(argument));
  }
  
  /**
   * Invoke a method of object instance of name methodName, where that method does take in one argument of type int
   * @param instance The instance of the object to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @param arg0 The int argument to be sent into the metod.
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public  Object invokeMethodWithIntParameter(Object instance,String methodName,int arg0)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	Integer[] argarray = new Integer[]{new Integer(arg0)};
	return invokeMethod(instance,MethodFinder.getInstance().getMethodWithNameAndOneParameter(instance.getClass(),methodName,Integer.TYPE),argarray);
  }
  /**
   * Invoke a method of object instance of name methodName, where that method does take in one argument of type String
   * @param instance The instance of the object to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @param arg0 The String argument to be sent into the metod.
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public  Object invokeMethodWithStringParameter(Object instance,String methodName,String arg0)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	String[] argarray = new String[]{arg0};
	return invokeMethod(instance,MethodFinder.getInstance().getMethodWithNameAndOneParameter(instance.getClass(),methodName,String.class),argarray);
  }
  /**
   * Invoke a method of object instance of name methodName, where that method does take in one argument of type boolean
   * @param instance The instance of the object to invoke a method in.
   * @param methodName the name of the method to invoke (without parentheses)
   * @param arg0 The boolean argument to be sent into the metod.
   * @return the return of the method invokation
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public  Object invokeMethodWithBooleanParameter(Object instance,String methodName,boolean arg0)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	Boolean[] argarray = new Boolean[]{new Boolean(arg0)};
	return invokeMethod(instance,MethodFinder.getInstance().getMethodWithNameAndOneParameter(instance.getClass(),methodName,Boolean.TYPE),argarray);
  }


  public  Object invokeMethod(Object instance,String methodIdentifier,Object[] arguments)throws IllegalAccessException,InvocationTargetException{
	return invokeMethod(instance,MethodFinder.getInstance().getMethod(methodIdentifier),arguments);
  }

  public  Object invokeStaticMethod(String methodIdentifier,Object[] arguments)throws IllegalAccessException,InvocationTargetException{
    return invokeStaticMethod(MethodFinder.getInstance().getMethod(methodIdentifier),arguments);
  }

  private  Object invokeMethod(Object instance,Method method,Object[] arguments)throws IllegalAccessException,InvocationTargetException{
    return method.invoke(instance,arguments);
  }

  private  Object invokeStaticMethod(Method method,Object[] arguments)throws IllegalAccessException,InvocationTargetException{
    return method.invoke(null,arguments);
  }
}
