package com.idega.util.reflect;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.lang.reflect.Method;
import java.util.StringTokenizer;

public class MethodFinder {

  static final String separator = ":";
  static final String methodString = "method";
  private static MethodFinder instance;

  private MethodFinder(){
  }

  public static MethodFinder getInstance(){
    if(instance==null){
      instance = new MethodFinder();
    }
    return instance;
  }


  public Method getMethod(String methodIdentifier){
    try{
      String[] idArray = getIdentifierAsArray(methodIdentifier);
      Class c = getDeclaringClass(idArray);
      Method[] methods = getMethods(c);
      for (int i = 0; i < methods.length; i++) {
        if(methods[i].getName().equals(getMethodName(idArray))){
          if(methods[i].getReturnType().equals(getReturnTypeClass(idArray))){
            Class[] classes = getArgumentClasses(idArray);
            Class[] methodClasses = methods[i].getParameterTypes();
            boolean check=true;
            for (int j = 0;(j < classes.length && check); j++) {
              if(!methodClasses[j].equals(classes[j])){
                check=false;
              }
            }
            if(check){
              return methods[i];
            }
          }
        }
      }
    }
    catch(ClassNotFoundException e){
      e.printStackTrace();
    }
    return null;
  }

  public String getMethodIdentifier(Method method){
    StringBuffer buf = new StringBuffer();
    buf.append(separator);
    buf.append(methodString);
    buf.append(separator);
    buf.append(method.getModifiers());
    buf.append(separator);
    buf.append(method.getDeclaringClass().getName());
    buf.append(separator);
    buf.append(method.getReturnType().getName());
    buf.append(separator);
    buf.append(method.getName());
    buf.append(separator);
    Class[] parameterTypes = method.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++) {
        buf.append(parameterTypes[i].getName());
        buf.append(separator);
    }
    return buf.toString();
  }

  private String[] getIdentifierAsArray(String identifier){
    StringTokenizer token = new StringTokenizer(identifier,separator);
    int size = token.countTokens();
    String array[] = new String[size-1];
    for (int i = 0; i < array.length; i++) {
      array[i]=token.nextToken();
    }
    return array;
  }

  public String getDeclaringClassName(String[] identifierArray){
    return identifierArray[1];
  }

  public Class getDeclaringClass(String[] identifierArray)throws ClassNotFoundException{
    return Class.forName(getDeclaringClassName(identifierArray));
  }

  public String getReturnTypeClassName(String[] identifierArray){
    return identifierArray[2];
  }

  public Class getReturnTypeClass(String[] identifierArray)throws ClassNotFoundException{
    return Class.forName(getReturnTypeClassName(identifierArray));
  }

  public String getMethodName(String[] identifierArray){
    return identifierArray[3];
  }

  public String[] getArgumentClassNames(String[] identifierArray){
    String[] theReturn = new String[identifierArray.length-4];
    System.arraycopy(identifierArray,4,theReturn,0,theReturn.length);
    return theReturn;
  }

  public Class[] getArgumentClasses(String[] identifierArray)throws ClassNotFoundException{
    String[] strings =  getArgumentClassNames(identifierArray);
    Class[] theReturn = new Class[strings.length];
    for (int i = 0; i < theReturn.length; i++) {
        theReturn[i]=Class.forName(strings[i]);
    }
    return theReturn;
  }

  public Method[] getMethods(Class c){
    return c.getMethods();
  }

  public String[] getMethodIdentifiers(Class c){
    Method[] methods = getMethods(c);
    String[] theReturn = new String[methods.length];
    for (int i = 0; i < theReturn.length; i++) {
      theReturn[i]=getMethodIdentifier(methods[i]);
    }
    return theReturn;
  }

}