package com.idega.business;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.reflect.MethodFinder;
/**
 * Title:        idega Business Objects
 * Description:  IBOLookup is a class use to get instances of IBO (Service and Session) objects.<br><br>
 * <br>Instances of IBOService classes are stored in the IWApplicationContext and obtained by passing a reference to the application context and either a class representing a bean interface of implementation.
 * <br>Instances of IBOSession classes are stored in the IWUserContext and obtained by passing a reference to the user context and either a class representing a bean interface of implementation.
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IBOLookup
{
	private static IBOLookup instance;
	private static IBOLookup getInstance()
	{
		if (instance == null)
		{
			instance = new IBOLookup();
		}
		return instance;
	}
	
	protected final String HOME_SUFFIX = "Home";
	protected final String FACTORY_SUFFIX = "HomeImpl";
	private final String BEAN_SUFFIX = "Bean";
	protected String getBeanSuffix()
	{
		return BEAN_SUFFIX;
	}
	protected static Map homes = new HashMap();
	protected static Map beanClasses = new HashMap();
	protected static Map interfaceClasses = new HashMap();
	protected Map services;
	private Properties jndiProperties;
	private Map createMethodsMap;
	
	protected IBOLookup()
	{}
	protected static Object getHomeForClass(Class beanInterfaceClass) throws IBOLookupException
	{
		return getInstance().getEJBHomeInstance(beanInterfaceClass);
	}
	protected static IBOHome getIBOHomeForClass(Class beanInterfaceClass) throws IBOLookupException
	{
		return (IBOHome) getHomeForClass(beanInterfaceClass);
	}
	/**
	 * Returns an instance of a IBOSession bean.
	 * The instance is stored in the session for the (current) user context. After <b>this</b> method is called there should be a corresponding call to removeSessionAttribute() to clean up from the users context.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static IBOSession getSessionInstance(IWUserContext iwuc, Class beanInterfaceClass) throws IBOLookupException
	{
		return getInstance().getSessionInstanceImpl(iwuc, beanInterfaceClass);
	}
	private IBOSession getSessionInstanceImpl(IWUserContext iwuc, Class beanInterfaceClass) throws IBOLookupException
	{
		IBOSession session = (IBOSession) iwuc.getSessionAttribute(this.getSessionKeyForObject(beanInterfaceClass));
		if (session == null)
		{
			try
			{
				session = instanciateSessionBean(beanInterfaceClass);
				iwuc.setSessionAttribute(getSessionKeyForObject(beanInterfaceClass), session);
				session.setUserContext(iwuc);
			}
			catch (CreateException cre)
			{
				throw new IBOLookupException("[IBOLookup] : CreateException : " + cre.getMessage());
			}
			catch (RemoteException cre)
			{
				throw new IBOLookupException("[IBOLookup] : RemoteException : " + cre.getMessage());
			}
		}
		return session;
	}
	/**
	 * Cleans up after an an instance of a IBOSession bean has been used.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static void removeSessionInstance(IWUserContext iwuc, Class beanInterfaceClass)
		throws RemoteException, RemoveException
	{
		getInstance().removeSessionInstanceImpl(iwuc, beanInterfaceClass);
	}
	private void removeSessionInstanceImpl(IWUserContext iwuc, Class beanInterfaceClass)
		throws RemoteException, RemoveException
	{
		IBOSession session = getSessionInstance(iwuc, beanInterfaceClass);
		session.remove();
		iwuc.removeSessionAttribute(getSessionKeyForObject(beanInterfaceClass));
	}
	private IBOSession instanciateSessionBean(Class beanInterfaceClass) throws IBOLookupException, CreateException
	{
		return (IBOSession) instanciateServiceBean(beanInterfaceClass);
	}
	private IBOService instanciateServiceBean(Class beanInterfaceClass) throws IBOLookupException, CreateException
	{
		IBOService session = null;
		IBOHome home = getIBOHomeForClass(beanInterfaceClass);
		try{
			Method defaultCreateMethod = getCreateMethod(home);
			session = (IBOService)defaultCreateMethod.invoke(home,null);
		}
		catch(InvocationTargetException ite){
			//ite.printStackTrace();
			Throwable e = ite.getTargetException();
			e.printStackTrace();
			throw new CreateException("Exception invoking create method for: "+beanInterfaceClass.getName()+". Error was:"+e.getClass().getName()+":"+e.getMessage());	
		}
		catch(Exception e){
			e.printStackTrace();
			throw new CreateException("Exception invoking create method for: "+beanInterfaceClass.getName()+". Error was:"+e.getMessage());	
		}
		//session = home.createIBO();
		return session;
	}

	/**
	 * Method getCreateMethod.
	 * @param home
	 * @return Method
	 */
	private Method getCreateMethod(IBOHome home) throws NoSuchMethodException{
		Map map = getCreateMethodsMap();
		Class homeClass = home.getClass();
		Method m = (Method)map.get(homeClass);
		if(m==null){
			m = MethodFinder.getInstance().getMethodWithNameAndNoParameters(homeClass,"create");
			map.put(homeClass,m);
		}
		return m;
	}

	/**
	 * Method getCreateMethodsMap.
	 * @return Map
	 */
	private Map getCreateMethodsMap() {
		if(createMethodsMap==null){
			createMethodsMap=new HashMap();	
		}
		return createMethodsMap;
	}


	/**
	 * Returns an instance of a IBOService bean.
	 * The instance is stored in the application context and is shared between all users.
	 * @param iwac A reference to the application (context) the bean should be looked up.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static IBOService getServiceInstance(IWApplicationContext iwac, Class beanInterfaceClass)
		throws IBOLookupException
	{
		return getInstance().getServiceInstanceImpl(iwac, beanInterfaceClass);
	}
	private IBOService getServiceInstanceImpl(IWApplicationContext iwac, Class beanInterfaceClass)
		throws IBOLookupException
	{
		IBOService service = (IBOService) this.getServicesMap(iwac).get(beanInterfaceClass);
		if (service == null)
		{
			try
			{
				service = this.instanciateServiceBean(beanInterfaceClass);
				if(iwac!=null){
					((IBOServiceBean) service).setIWApplicationContext(iwac);
				}
				service.initializeBean();
				getServicesMap(iwac).put(beanInterfaceClass, service);
			}
			catch (CreateException cre)
			{
				throw new IBOLookupException("[IBOLookup] : CreateException : " + cre.getMessage());
			}
		}
		return service;
	}
	protected Class getHomeInterfaceClassFor(Class entityInterfaceClass) throws Exception
	{
		try{
			//First try to suffix the Home to the interface class name
			String baseClassName = getInterfaceClassForNonStatic(entityInterfaceClass).getName();
			String homeClassName = baseClassName + HOME_SUFFIX;
			return Class.forName(homeClassName);
		}
		catch(ClassNotFoundException cnfe){
			//If that doesn't work then try to suffix the Home to the bean implementation class name - the bean suffix
			String beanClassName = getBeanClassForNonStatic(entityInterfaceClass).getName();
			String baseClassName = beanClassName.substring(0,beanClassName.indexOf(getBeanSuffix()));
			String homeClassName = baseClassName + HOME_SUFFIX;
			return Class.forName(homeClassName);
		}
		
	}
	protected Class getFactoryClassFor(Class entityInterfaceClass) throws Exception
	{
		try{
			//First try to suffix the FACTORY_SUFFIX to the bean implementation class name - the bean suffix
			String beanClassName = getBeanClassForNonStatic(entityInterfaceClass).getName();
			String baseClassName = beanClassName.substring(0,beanClassName.indexOf(getBeanSuffix()));
			String homeClassName = baseClassName + FACTORY_SUFFIX;
			return Class.forName(homeClassName);
		}
		catch(ClassNotFoundException cnfe){
			//If that doesn't work then try to suffix the FACTORY_SUFFIX to the interface class name
			String baseClassName = getInterfaceClassForNonStatic(entityInterfaceClass).getName();
			String homeClassName = baseClassName + FACTORY_SUFFIX;
			return Class.forName(homeClassName);
		}

	}
	private String getSessionKeyForObject(Class interfaceClass)
	{
		return "IBO." + interfaceClass.getName();
	}
	/**
	 * Gets an instance of the implementation of the Home interface for the data bean.
	 * <br>The object retured can then needs to be casted to the specific home interface for the bean.
	 * @param entityInterfaceClass i the interface of the data bean.
	 */
	protected Object getEJBHomeInstance(Class entityBeanOrInterfaceClass){
		//Double check so it is not the bean class that is sent into the methods below
		Class entityInterfaceClass = getInterfaceClassForNonStatic(entityBeanOrInterfaceClass);
			
		//EJBHome home = (EJBHome) homes.get(entityInterfaceClass);
		Object home = homes.get(entityInterfaceClass);
		
		if (home == null)
		{
			try
			{
				if(doLookupOverJNDI(entityInterfaceClass)){
					//home = (EJBHome) getHomeThroughJNDI(getInterfaceClassForNonStatic(entityInterfaceClass));
					
					home = getHomeThroughJNDI(entityInterfaceClass);
				}
				else{
					Class factoryClass = getFactoryClassFor(entityInterfaceClass);
					home = factoryClass.newInstance();
				}
				homes.put(entityInterfaceClass, home);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(
					"Error initializing Home for EJB Bean Interface class:"
						+ entityInterfaceClass.getName()
						+ " - Message: "
						+ e.getMessage());
			}
		}
		return home;
	}
	/**
	* Gets the Class object for the (BMP) bean class of a data bean.
	* @param entityInterfaceClass i the (Remote) interface of the data bean.
	*/
	static Class getIBOBeanClassFor(Class entityInterfaceClass)
	{
		return getInstance().getBeanClassForNonStatic(entityInterfaceClass);
	}
	/**
	 * Gets the Class object for the (BMP) bean class of a data bean.
	 * @param entityInterfaceClass i the (Remote) interface of the data bean.
	 */
	protected Class getBeanClassForNonStatic(Class entityInterfaceClass)
	{
		try
		{
			Class beanClass = (Class) beanClasses.get(entityInterfaceClass);
			if (beanClass == null)
			{
				if (entityInterfaceClass.isInterface())
				{
					String className = entityInterfaceClass.getName();
					String beanClassName = className + getBeanSuffix();
					beanClass = Class.forName(beanClassName);
						
					beanClasses.put(entityInterfaceClass, beanClass);
					interfaceClasses.put(beanClass, entityInterfaceClass);
					
				}
				else
				{
					beanClass = entityInterfaceClass;
				}
			}
			return beanClass;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage());
		}
		//return null;
	}
	/**
	* Gets the Class object for the (Remote) interface of a data bean.
	* @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
	*/
	protected Class getInterfaceClassForNonStatic(Class entityBeanOrInterfaceClass)
	{
		if (entityBeanOrInterfaceClass.isInterface())
		{
			return entityBeanOrInterfaceClass;
		}
		else
		{
			Class interfaceClass = (Class) interfaceClasses.get(entityBeanOrInterfaceClass);
			try
			{
				if (interfaceClass == null)
				{
					String className = entityBeanOrInterfaceClass.getName();
					int endIndex = className.indexOf(getBeanSuffix());
					if (endIndex != -1)
					{
						String interfaceClassName = className.substring(0, endIndex);
						interfaceClass = Class.forName(interfaceClassName);
						
						beanClasses.put(interfaceClass, entityBeanOrInterfaceClass);
						interfaceClasses.put(entityBeanOrInterfaceClass, interfaceClass);
					}
					else
					{
						//For legacy beans
						interfaceClass = entityBeanOrInterfaceClass;
					}
				}
				return interfaceClass;
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e.getClass() + ": " + e.getMessage());
			}
		}
	}
	private Map getServicesMap(IWApplicationContext iwac)
	{
		/**
		 * @todo: implement
		 */
		return getServicesMap();
	}
	private Map getServicesMap()
	{
		if (services == null)
		{
			services = new HashMap();
		}
		return services;
	}
	/**
	 * Clears all cached object instaces of looked up objects (Home instances etc.)
	 **/
	public static synchronized void clearAllCache()
	{
		homes.clear();
		beanClasses.clear();
		interfaceClasses.clear();
	}
	
	protected Object getHomeThroughJNDI(Class beanInterfaceClass)throws RemoteException{
		Object home = null;
		try{
	  		InitialContext jndiContext = getInitialContext();
			Object homeObj = jndiContext.lookup(getBeanClassForNonStatic(beanInterfaceClass).getName());
			//home = (ResponseHome) jndiContext.lookup("java:comp/env/"+ResponseBMPBean.class.getName());
			home = PortableRemoteObject.narrow(homeObj, getHomeInterfaceClassFor(beanInterfaceClass));
			return home;
		}
		catch(Exception e){
			throw new RemoteException("Error looking up home for "+beanInterfaceClass.getName()+". Errormessage was: "+e.getMessage());	
		}
	}
	
	protected boolean doLookupOverJNDI(Class beanInterfaceClass){
		/**
		 *@todo: implement
		 **/
		return false;
	}

  private InitialContext getInitialContext() throws NamingException{
  	if(jndiProperties==null){
  		jndiProperties=new Properties();
	  	try {
			jndiProperties.load(new FileInputStream("/idega/jndi.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
  	}
  	return new InitialContext(jndiProperties);
  }



  public static void registerImplementationForBean(Class interfaceClass, Class beanClass) {
  	beanClasses.put(interfaceClass,beanClass);
	interfaceClasses.put(beanClass,interfaceClass);
  }
}