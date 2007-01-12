package com.idega.util.reflect;
/**
 * A utility class to find methods by reflection.
 * Title:       idega Reflection utility classes
 * Description:
 * Copyright:    Copyright (c) 2001-2003
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1
 */
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.caching.CacheMap;
public class MethodFinder implements Singleton
{
	static final String separator = ":";
	static final String methodString = "method";
	static final int declaringClassIndex = 2;
	static final int returnedClassIndex = 3;
	static final int methodIndex = 4;
	static final int parameterClassStartIndex = 5;
	
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new MethodFinder();}};
	
	private Map methodCache;
	private Map classMethodCache;
	
	private MethodFinder()
	{
	}
	
	public static MethodFinder getInstance() {
		return (MethodFinder) SingletonRepository.getRepository().getInstance(MethodFinder.class, instantiator);
	}

	private Map getMethodCache()
	{
		if (this.methodCache == null)
		{
			this.methodCache = new CacheMap(1000);
		}
		return this.methodCache;
	}

	protected Method getMethodFromGlobalCache(String methodIdentifier)
	{
		return (Method) getMethodCache().get(methodIdentifier);
	}
	protected void putMethodInGlobalCache(String methodIdentifier, Method method)
	{
		getMethodCache().put(methodIdentifier, method);
	}
	
	
	
	private Map getClassMethodCache(Class declaringClass)
	{
		if (this.classMethodCache == null)
		{
			this.classMethodCache = new HashMap();
		}
		Map mapForClass = (Map) this.classMethodCache.get(declaringClass);
		if(mapForClass==null){
			mapForClass = new CacheMap(150);
			this.classMethodCache.put(declaringClass,mapForClass);
		}
		return mapForClass;
	}
	
	protected Method getMethodFromClassCache(Class declaringClass,String methodIdentifier)
	{
		return (Method) getClassMethodCache(declaringClass).get(methodIdentifier);
	}
	protected void putMethodInClassCache(Class declaringClass,String methodIdentifier, Method method)
	{
		getClassMethodCache(declaringClass).put(methodIdentifier, method);
	}
	
	public Method getMethod(String methodIdentifier)
	{
		try
		{
			Method m = getMethodFromGlobalCache(methodIdentifier);
			if (m == null)
			{
				String[] idArray = getIdentifierAsArray(methodIdentifier);
				Class declaringClass = getDeclaringClass(idArray);
				m = getMethod(declaringClass, idArray);
				putMethodInGlobalCache(methodIdentifier, m);
			}
			return m;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Method getMethod(String methodIdentifier, Class declaringClass)
	{
		Method m = getMethodFromClassCache(declaringClass,methodIdentifier);
		if(m==null){
			String[] idArray = getIdentifierAsArray(methodIdentifier);
			m = getMethod(declaringClass, idArray);
			putMethodInClassCache(declaringClass,methodIdentifier,m);
		}
		return m;
	}
	
	
	/**
	 * Does method finding and tries to correct any refactored class types
	 * @param methodIdentifier
	 * @param declaringClass
	 * @param idArray
	 * @return
	 */
	private Method getMethod(Class declaringClass, String[] idArray)
	{
		try{
			return findRealMethod(declaringClass,idArray);
		}
		catch(Throwable nsme){
			RefactorClassRegistry registry = RefactorClassRegistry.getInstance();
			Map refactoredClassNames = registry.getRefactoredClassNames();
			Iterator iterator = refactoredClassNames.keySet().iterator();
			while (iterator.hasNext())
			{
				String oldClassName = (String) iterator.next();
				String newClassName = (String)refactoredClassNames.get(oldClassName);
				String[] newIdArray = new String[idArray.length];
				for (int i = 0; i < idArray.length; i++)
				{
					String str = idArray[i];
					if(str.equals(oldClassName)){
						newIdArray[i]=newClassName;
					}
					else{
						newIdArray[i]=idArray[i];
					}
				}
				try{
					return findRealMethod(declaringClass,newIdArray);
				}
				catch(ClassNotFoundException cnfe){
					System.err.println("MethodFinder.getMethod() Error finding method with parameter of type : "+cnfe.getMessage());
				}
				catch(Throwable nsme2){
					System.err.println("MethodFinder.getMethod() Error finding method, message: "+nsme2.getMessage());
				}
				
			}
			throw new NoSuchMethodError(nsme.getMessage());
		}
	}
	
	
	/**
	 * Does the Real method finding
	 * @param methodIdentifier
	 * @param declaringClass
	 * @param idArray
	 * @return
	 */
	private Method findRealMethod(Class declaringClass, String[] idArray) throws ClassNotFoundException
	{
		//try
		//{
			//System.out.println("Declaring class: "+c.getName());
			Method[] methods = getMethods(declaringClass);
			for (int i = 0; i < methods.length; i++)
			{
				if (methods[i].getName().equals(getMethodName(idArray)))
				{
					//System.out.println("MethodName: "+methods[i].getName()+" returnType = "+methods[i].getReturnType().getName());
					if (methods[i].getReturnType().equals(getReturnTypeClass(idArray)))
					{
						Class[] classes = getArgumentClasses(idArray);
						Class[] methodClasses = methods[i].getParameterTypes();
						boolean check = true;
						if (methodClasses.length > 0)
						{
							if (classes.length != methodClasses.length)
							{
								check = false;
							} else
							{
								for (int j = 0;(j < classes.length && check); j++)
								{
									if (!methodClasses[j].equals(classes[j]))
									{
										//System.out.print(methodIdentifier);
										//System.out.println(methods[i].toString());
										//System.out.println("MethodClasses["+j+"]"+methodClasses[j].getName());
										check = false;
									}
								}
							}
						} else
						{
							//System.out.println("Methodfinder length "+classes.length);
							if (classes.length > 0)
							{
								if (!classes[0].equals(java.lang.Void.TYPE))
								{
									check = false;
								}
							}
						}
						if (check)
						{
							return methods[i];
						}
					}
				}
			}
		//} catch (ClassNotFoundException e)
		//{
		//	e.printStackTrace();
		//}
		return null;
	}
	public String getMethodIdentifierWithoutDeclaringClass(Method method)
	{
		return getMethodIdentifier(method, false);
	}
	public String getMethodIdentifier(Method method)
	{
		return getMethodIdentifier(method, true);
	}
	private String getMethodIdentifier(Method method, boolean includeDeclaringClass)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(separator);
		buf.append(methodString);
		buf.append(separator);
		buf.append(method.getModifiers());
		buf.append(separator);
		if (includeDeclaringClass)
		{
			buf.append(method.getDeclaringClass().getName());
		} else
		{
			buf.append("implied");
		}
		buf.append(separator);
		buf.append(method.getReturnType().getName());
		buf.append(separator);
		buf.append(method.getName());
		buf.append(separator);
		Class[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length > 0)
		{
			for (int i = 0; i < parameterTypes.length; i++)
			{
				buf.append(parameterTypes[i].getName());
				buf.append(separator);
			}
		} else
		{
			buf.append("void");
			buf.append(separator);
		}
		return buf.toString();
	}
	private String[] getIdentifierAsArray(String identifier)
	{
		StringTokenizer token = new StringTokenizer(identifier, separator);
		int size = token.countTokens();
		String array[] = new String[size];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = token.nextToken();
		}
		return array;
	}
	private static Class getClass(String className) throws ClassNotFoundException
	{
		if (className.equals("void"))
		{
			return java.lang.Void.TYPE;
		} else if (className.equals("int"))
		{
			return java.lang.Integer.TYPE;
		} else if (className.equals("boolean"))
		{
			return java.lang.Boolean.TYPE;
		} else if (className.equals("float"))
		{
			return java.lang.Float.TYPE;
		} else if (className.equals("double"))
		{
			return java.lang.Double.TYPE;
		} else if (className.equals("long"))
		{
			return java.lang.Long.TYPE;
		} else
		{
			return RefactorClassRegistry.forName(className);
		}
	}
	public String getDeclaringClassName(String[] identifierArray)
	{
		return identifierArray[declaringClassIndex];
	}
	public Class getDeclaringClass(String[] identifierArray) throws ClassNotFoundException
	{
		return RefactorClassRegistry.forName(getDeclaringClassName(identifierArray));
	}
	public String getReturnTypeClassName(String[] identifierArray)
	{
		return identifierArray[returnedClassIndex];
	}
	public Class getReturnTypeClass(String[] identifierArray) throws ClassNotFoundException
	{
		return getClass(getReturnTypeClassName(identifierArray));
	}
	public String getMethodName(String[] identifierArray)
	{
		return identifierArray[methodIndex];
	}
	public String[] getArgumentClassNames(String[] identifierArray)
	{
		int length = identifierArray.length - parameterClassStartIndex;
		String[] theReturn = new String[length];
		if (length != 0)
		{
			System.arraycopy(identifierArray, parameterClassStartIndex, theReturn, 0, theReturn.length);
		}
		return theReturn;
	}
	public Class[] getArgumentClasses(String methodIdentifier) throws ClassNotFoundException
	{
		return getArgumentClasses(getIdentifierAsArray(methodIdentifier));
	}
	public Class[] getArgumentClasses(String[] identifierArray) throws ClassNotFoundException
	{
		String[] strings = getArgumentClassNames(identifierArray);
		Class[] theReturn = new Class[strings.length];
		for (int i = 0; i < theReturn.length; i++)
		{
			theReturn[i] = getClass(strings[i]);
		}
		return theReturn;
	}
	public Method[] getMethods(Class c)
	{
		return c.getMethods();
	}
	public String[] getMethodIdentifiersWithoutDeclaringClass(Class c)
	{
		Method[] methods = getMethods(c);
		String[] theReturn = new String[methods.length];
		for (int i = 0; i < theReturn.length; i++)
		{
			theReturn[i] = getMethodIdentifierWithoutDeclaringClass(methods[i]);
		}
		return theReturn;
	}
	public String[] getMethodIdentifiers(Class c)
	{
		Method[] methods = getMethods(c);
		String[] theReturn = new String[methods.length];
		for (int i = 0; i < theReturn.length; i++)
		{
			theReturn[i] = getMethodIdentifier(methods[i]);
		}
		return theReturn;
	}
	/**
	* Gets only the method that is named "name" in class objectClass and takes in no arguments (parameters)
	* @throws NoSuchMethodException if no mathing method is found.
	**/
	public Method getMethodWithNameAndNoParameters(Class objectClass, String name) throws NoSuchMethodException
	{
		Method[] allMethods = objectClass.getMethods();
		for (int i = 0; i < allMethods.length; i++)
		{
			Method methodToCheck = allMethods[i];
			if (methodToCheck.getName().equals(name))
			{
				if (methodToCheck.getParameterTypes().length == 0)
				{
					return methodToCheck;
				}
			}
		}
		throw new NoSuchMethodException("Method " + name + "() not found in " + objectClass.getName());
	}
	/**
	* Gets only the method that is named "name" in class objectClass and takes in one parameter of the type parameterType
	* @throws NoSuchMethodException if no mathing method is found.
	**/
	public Method getMethodWithNameAndOneParameter(Class objectClass, String name,Class parameterType) throws NoSuchMethodException
	{
		Method[] allMethods = objectClass.getMethods();
		for (int i = 0; i < allMethods.length; i++)
		{
			Method methodToCheck = allMethods[i];
			if (methodToCheck.getName().equals(name))
			{
				if (methodToCheck.getParameterTypes().length == 1)
				{
					if(methodToCheck.getParameterTypes()[0].equals(parameterType)){
						return methodToCheck;
					}
				}
			}
		}
		throw new NoSuchMethodException("Method " + name + "() not found in " + objectClass.getName());
	}


	/**
	* Gets only the method that is named "name" in class objectClass and takes in parameters of the types exactly found in parameterTypes
	* @throws NoSuchMethodException if no mathing method is found.
	**/
	public Method getMethodWithNameAndParameters(Class objectClass, String name,Class[] parameterTypes) throws NoSuchMethodException
	{
		int numOfParameters = (parameterTypes!=null)? parameterTypes.length : 0;
		Method[] allMethods = getMethodsWithName(objectClass,name,numOfParameters);
		for (int i = 0; i < allMethods.length; i++)
		{
			Method methodToCheck = allMethods[i];
			Class[] methodParameters = methodToCheck.getParameterTypes();
			if (methodParameters.length == numOfParameters)
			{
				boolean check = true;
				for (int j = 0; j < methodParameters.length; j++) {
					Class methodParam = methodParameters[j];
					Class parameter = parameterTypes[j];
					if(!parameter.equals(methodParam)){
						check=false;
					}
				}
				if(check) {
					return methodToCheck;
				}
			}
		}
		throw new NoSuchMethodException("Method " + name + "() not found in " + objectClass.getName());
	}


	/**
	* Gets all the methods that are named "name" in class objectClass
	* @return An array with all mathing methods or an array with length 0 if no mathing methods are found.
	**/
	public Method[] getMethodsWithName(Class objectClass, String name)
	{
		return getMethodsWithName(objectClass, name, -1);
	}
	
	/**
	 * Gets all the methods that are named "name" in class objectClass
	 * @return An array with all mathing methods or an array with length 0 if no mathing methods are found.
	 * @param maxNumberOfParameters The maximum number of parameters the method can include or -1 of no maximum is specified.
	 **/
	public Method[] getMethodsWithName(Class objectClass, String name, int maxNumberOfParameters)
	{
		Method[] allMethods = objectClass.getMethods();
		//Method[] matchedMethods = new Method[returningSize];
		Vector matchedMethodsVector = new Vector();
		for (int i = 0; i < allMethods.length; i++)
		{
			Method methodToCheck = allMethods[i];
			if (methodToCheck.getName().equals(name))
			{
				if (maxNumberOfParameters < 0)
				{
					matchedMethodsVector.add(methodToCheck);
				} else
				{
					if (methodToCheck.getParameterTypes().length <= maxNumberOfParameters)
					{
						matchedMethodsVector.add(methodToCheck);
					}
				}
			}
		}
		//return matchedMethods;
		return (Method[]) matchedMethodsVector.toArray(new Method[0]);
	}
	
	private final static String openingParentheses = "(";
	private final static String closingParentheses = ")";
	private final static String comma = ",";
	
	/**
	 * Gets a string representation of a class method
	 *  "getMyValue(String arg)"
	 * @param method
	 * @return string representation of method
	 */
	public String getMethodNameWidthParameters(Method method){
		String methodToString = method.getName()+openingParentheses;
		Class[] arguments = method.getParameterTypes();
		for (int j = 0; j < arguments.length; j++) {
			if(j!=0){
				methodToString += comma;
			}
			methodToString += arguments[j].getName();
		}
		methodToString += closingParentheses;
	  	return methodToString;
	}
	
	/**
	 * Gets a map of class methods, keyed by getMethodNameWithParameter method
	 * lastSuperClassToReflect tells at wich superclass the reflection should stop
	 * if null then only the methods of the base class 
	 * @param methodClass
	 * @param lastSuperClassToReflect
	 * @param filters
	 * @return map of methods
	 */
	public Map getMapOfClassMethodsRecursive(Class methodClass, Class lastSuperClassToReflect,String[] filters){
		Map map = new HashMap();
		boolean noStopClass = lastSuperClassToReflect ==null;
		while(!methodClass.equals(lastSuperClassToReflect)){
			Method[] methods = methodClass.getMethods();
			for (int i = 0; i < filters.length; i++) {
				for (int j = 0; j < methods.length; j++) {
					String name = MethodFinder.getInstance().getMethodNameWidthParameters(methods[j]);
					if(name.startsWith(filters[i])){
						map.put(name,methods[j]);
						System.out.println("Putting method for "+name);
					}
				}
			}
			if(noStopClass) {
				break;
			}
			methodClass = methodClass.getSuperclass();
		}
		return map;
	}
	
	/** Returns an updated methodIdentifier.
	 * Checks if the declared class, the returned class or the parameter classes are refactored and 
	 * replaces them by the new ones.
	 * Returns the same string if no classes are refactored.
	 * 
	 * Use this method when handling with old method identifers (e.g. stored in old builder pages).
	 * See also MethodIdentifierCache.
	 * 
	 * @param methodIdentifier
	 * @return updated methodIdentifer
	 */
	public String getUpdatedMethodIdentifier(String methodIdentifier) {
		RefactorClassRegistry registry =  RefactorClassRegistry.getInstance();
		StringTokenizer firstTokenizer = new StringTokenizer(methodIdentifier, separator);
		StringBuffer buffer = new StringBuffer(separator);
		boolean somethingHasChanged = false;
		int counter = 0;
		while (firstTokenizer.hasMoreTokens()) {
			String token = firstTokenizer.nextToken();
			if ((counter == declaringClassIndex ||
				counter == returnedClassIndex ||
				counter >= parameterClassStartIndex) 
					&& registry.isClassRefactored(token)) {
				buffer.append(registry.getRefactoredClassName(token));
				somethingHasChanged = true;
			}
			else {
				buffer.append(token);
			}
			buffer.append(separator);
			counter++;
		}
		// avoid expensive creation of string
		if (somethingHasChanged) {
			return buffer.toString();
		}
		return methodIdentifier;
	}
	
	/**
	 * Test main method
	 **/
	public static void main(String[] args)
	{
		/*Method[] methods = 	getInstance().getMethodsWithName(MethodFinder.class,"getMethods");
		
		for (int i = 0; i < methods.length; i++) {
		System.out.println("Found method: "+methods[i].toString());	
		}*/
		/*try{
			Method method = getInstance().getMethodsWithNameAndNoParameters(MethodFinder.class,"getInstance");
			System.out.println("Found method: "+method.toString());	
		}
		catch(Exception e){
			e.printStackTrace();	
		}*/
	}
	
}
