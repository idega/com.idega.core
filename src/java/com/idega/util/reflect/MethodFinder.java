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
      System.out.println("Declaring class: "+c.getName());
      Method[] methods = getMethods(c);
      for (int i = 0; i < methods.length; i++) {
        if(methods[i].getName().equals(getMethodName(idArray))){
          System.out.println("MethodName: "+methods[i].getName()+" returnType = "+methods[i].getReturnType().getName());
          if(methods[i].getReturnType().equals(getReturnTypeClass(idArray))){
            Class[] classes = getArgumentClasses(idArray);
            Class[] methodClasses = methods[i].getParameterTypes();
            boolean check=true;
            if(methodClasses.length>0){
              for (int j = 0;(j < methodClasses.length && check); j++) {
                if(!methodClasses[j].equals(classes[j])){
                  System.out.println("MethodClasses["+j+"]"+methodClasses[j].getName());
                  check=false;
                }
              }
            }
            else{
                System.out.println("length "+classes.length);
                if(classes.length>0){
                  if(!classes[0].equals(java.lang.Void.class)){
                    check=false;
                  }
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
    if(parameterTypes.length>0){
      for (int i = 0; i < parameterTypes.length; i++) {
          buf.append(parameterTypes[i].getName());
          buf.append(separator);
      }
    }
    else{
      buf.append("void");
      buf.append(separator);
    }
    return buf.toString();
  }

  private String[] getIdentifierAsArray(String identifier){
    StringTokenizer token = new StringTokenizer(identifier,separator);
    int size = token.countTokens();
    String array[] = new String[size];
    for (int i = 0; i < array.length; i++) {
      array[i]=token.nextToken();
    }
    return array;
  }

  private static Class getClass(String className)throws ClassNotFoundException{
    if(className.equals("void")){
      return java.lang.Void.TYPE;
    }
    else if(className.equals("int")){
      return java.lang.Integer.TYPE;
    }
    else if(className.equals("boolean")){
      return java.lang.Boolean.TYPE;
    }
    else if(className.equals("float")){
      return java.lang.Float.TYPE;
    }
    else if(className.equals("double")){
      return java.lang.Double.TYPE;
    }
    else if(className.equals("long")){
      return java.lang.Long.TYPE;
    }
    else{
      return Class.forName(className);
    }
  }

  public String getDeclaringClassName(String[] identifierArray){
    return identifierArray[2];
  }

  public Class getDeclaringClass(String[] identifierArray)throws ClassNotFoundException{
    return Class.forName(getDeclaringClassName(identifierArray));
  }

  public String getReturnTypeClassName(String[] identifierArray){
    return identifierArray[3];
  }

  public Class getReturnTypeClass(String[] identifierArray)throws ClassNotFoundException{
    return getClass(getReturnTypeClassName(identifierArray));
  }

  public String getMethodName(String[] identifierArray){
    return identifierArray[4];
  }

  public String[] getArgumentClassNames(String[] identifierArray){
    int length = identifierArray.length-5;
    String[] theReturn = new String[length];
    if(length!=0){
      System.arraycopy(identifierArray,5,theReturn,0,theReturn.length);
    }
    return theReturn;
  }

  public Class[] getArgumentClasses(String[] identifierArray)throws ClassNotFoundException{
    String[] strings =  getArgumentClassNames(identifierArray);
    Class[] theReturn = new Class[strings.length];
    for (int i = 0; i < theReturn.length; i++) {
        theReturn[i]=getClass(strings[i]);
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