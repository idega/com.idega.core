package com.idega.util.reflect;

import java.lang.reflect.*;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class MethodInvoker {

  private static MethodInvoker instance;

  private MethodInvoker(){
  }

  private static MethodInvoker getInstance(){
    if(instance==null){
      instance = new MethodInvoker();
    }
    return instance;
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