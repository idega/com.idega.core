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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.util.reflect.MethodFinder;
/**
 * IBOLookup is a class use to get instances of IBO (Service and Session) objects.<br><br>
 * <br>Instances of IBOService classes are stored in the IWApplicationContext and obtained by passing a reference to the application context and either a class representing a bean interface of implementation.
 * <br>Instances of IBOSession classes are stored in the IWUserContext and obtained by passing a reference to the user context and either a class representing a bean interface of implementation.
 * Copyright (c) 2002-2004 Idega Software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IBOLookup implements Singleton
{
	private static IBOLookup instance;
	protected static synchronized IBOLookup getInstance()
	{
		if (instance == null)
		{
			instance = new IBOLookup();
		}
		return instance;
	}
	
	/**
	 * Unload the previously loaded instance and all its resources
	 */
	public static void unload(){
		instance=null;
	}
	
	protected final String HOME_SUFFIX = "Home";
	protected final String FACTORY_SUFFIX = "HomeImpl";
	private final String BEAN_SUFFIX = "Bean";
	protected String getBeanSuffix()
	{
		return this.BEAN_SUFFIX;
	}
	private Map homes;
	private Map beanClasses;
	private Map interfaceClasses;
	private Map services;
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
		checkAnnotatedAsSpringBean(beanInterfaceClass);
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
	/**
	 * Cleans up after an an instance of a IBOSession bean has been used.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static void removeSessionInstance(HttpSession session, Class beanInterfaceClass)
		throws RemoteException, RemoveException
	{
		getInstance().removeSessionInstanceImpl(session, beanInterfaceClass);
	}
	private void removeSessionInstanceImpl(HttpSession session, Class beanInterfaceClass)
		throws RemoteException, RemoveException
	{
		IBOSession sessionBean = getSessionInstance(session, beanInterfaceClass);
		sessionBean.remove();
		session.removeAttribute(getSessionKeyForObject(beanInterfaceClass));
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
			session = (IBOService)defaultCreateMethod.invoke(home, null);
		}
		catch(InvocationTargetException ite){
			//ite.printStackTrace();
			Throwable e = ite.getTargetException();
			//e.printStackTrace();
			throw new CreateException("Exception invoking create method for: "+beanInterfaceClass.getName()+". Error was:"+e.getClass().getName()+" : "+e.getMessage());	
		}
		catch(Exception e){
			
			throw new CreateException("Exception invoking create method for: "+beanInterfaceClass.getName()+". Error was:"+e.getClass().getName()+" : "+e.getMessage());	
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
		if(this.createMethodsMap==null){
			this.createMethodsMap=new HashMap();	
		}
		return this.createMethodsMap;
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
			String homeClassName = baseClassName + this.HOME_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		}
		catch(ClassNotFoundException cnfe){
			//If that doesn't work then try to suffix the Home to the bean implementation class name - the bean suffix
			String beanClassName = getBeanClassForNonStatic(entityInterfaceClass).getName();
			String baseClassName = beanClassName.substring(0,beanClassName.indexOf(getBeanSuffix()));
			String homeClassName = baseClassName + this.HOME_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		}
		
	}
	
	protected Class getFactoryClassFor(Class entityInterfaceClass) throws Exception
	{
		try{
			//First try to suffix the FACTORY_SUFFIX to the bean implementation class name - the bean suffix
			String beanClassName = getBeanClassForNonStatic(entityInterfaceClass).getName();
			String baseClassName = beanClassName.substring(0,beanClassName.indexOf(getBeanSuffix()));
			String homeClassName = baseClassName + this.FACTORY_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		}
		catch(ClassNotFoundException cnfe){
			//If that doesn't work then try to suffix the FACTORY_SUFFIX to the interface class name
			String baseClassName = getInterfaceClassForNonStatic(entityInterfaceClass).getName();
			String homeClassName = baseClassName + this.FACTORY_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
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
		return getEJBHomeInstance(entityBeanOrInterfaceClass, null);
	}

	protected Object homesMapLookup(Class entityInterfaceClass, String parameter) {
		return getHomesMap().get(entityInterfaceClass+parameter);
	}
	
	/**
	 * Gets an instance of the implementation of the Home interface for the data bean.
	 * <br>The object retured can then needs to be casted to the specific home interface for the bean.
	 * @param entityInterfaceClass i the interface of the data bean.
	 * @param parameter is a parameter used to separete different instances of the same Home class
	 */
	protected Object getEJBHomeInstance(Class entityBeanOrInterfaceClass, String parameter){
		//Double check so it is not the bean class that is sent into the methods below
		Class entityInterfaceClass = getInterfaceClassForNonStatic(entityBeanOrInterfaceClass);
			
		//EJBHome home = (EJBHome) homes.get(entityInterfaceClass);
//		Object home = getHomesMap().get(entityInterfaceClass+parameter);
		Object home = homesMapLookup(entityInterfaceClass, parameter);
		
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
				getHomesMap().put(entityInterfaceClass+parameter, home);
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				throw new RuntimeException(
					"Error initializing Home for EJB Bean Interface class:"
						+ entityInterfaceClass.getName()
						+ " - Message: "
						+ e.getMessage(),e);
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
			Class beanClass = (Class) getBeanClassesMap().get(entityInterfaceClass);
			if (beanClass == null)
			{
				if (entityInterfaceClass.isInterface())
				{
					String className = entityInterfaceClass.getName();
					String beanClassName = className + getBeanSuffix();
					beanClass = RefactorClassRegistry.forName(beanClassName);
						
					getBeanClassesMap().put(entityInterfaceClass, beanClass);
					getInterfaceClassesMap().put(beanClass, entityInterfaceClass);
					
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
			//e.printStackTrace();
			throw new RuntimeException(e.getClass().getName() + " : " + e.getMessage());
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
			Class interfaceClass = (Class) getInterfaceClassesMap().get(entityBeanOrInterfaceClass);
			try
			{
				if (interfaceClass == null)
				{
					String className = entityBeanOrInterfaceClass.getName();
					int endIndex = className.indexOf(getBeanSuffix());
					if (endIndex != -1)
					{
						String interfaceClassName = className.substring(0, endIndex);
						interfaceClass = RefactorClassRegistry.forName(interfaceClassName);
						
						getBeanClassesMap().put(interfaceClass, entityBeanOrInterfaceClass);
						getInterfaceClassesMap().put(entityBeanOrInterfaceClass, interfaceClass);
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
		if (this.services == null)
		{
			this.services = new HashMap();
		}
		return this.services;
	}
	/**
	 * Clears all cached object instaces of looked up objects (Home instances etc.)
	 **/
	public static synchronized void clearAllCache()
	{
		getInstance().getHomesMap().clear();
		getInstance().getBeanClassesMap().clear();
		getInstance().getInterfaceClassesMap().clear();
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
  	if(this.jndiProperties==null){
  		this.jndiProperties=new Properties();
	  	try {
			this.jndiProperties.load(new FileInputStream("/idega/jndi.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
  	}
  	return new InitialContext(this.jndiProperties);
  }

  public Map getBeanClassesMap(){
  	if(this.beanClasses==null){
  		this.beanClasses = new HashMap();
  	}
  	return this.beanClasses;
  }
  
  public Map getInterfaceClassesMap(){
  	if(this.interfaceClasses==null){
  		this.interfaceClasses= new HashMap();
  	}
  	return this.interfaceClasses;
  }
  
  public Map getHomesMap(){
  	if(this.homes==null){
  		this.homes= new HashMap();
  	}
  	return this.homes;
  }

  /**
   * Registers an implementation for a bean.
   * 
   * @param interfaceClass
   * @param beanClass
   * @see #isImplementationRegistered(Class) 
   */
  public static void registerImplementationForBean(Class interfaceClass, Class beanClass) {
  	getInstance().getBeanClassesMap().put(interfaceClass,beanClass);
	getInstance().getInterfaceClassesMap().put(beanClass,interfaceClass);
  }
  
  /**
   * Checks if an implementation of the specified bean is registered.
   * This method should be called if the caller is not sure if an implementation exists.
   * If an implementation does not exist call of the getServiceInstance method 
   * returns a RuntimeException when tryng to get the bean.
   * 
   * Returns true if there is an entry else false
   * 
   * @param interfaceClass
   * @return true if an implementation is registered else false
   * @see  #registerImplementationForBean(Class, Class)
   */
  public static boolean isImplementationRegistered(Class interfaceClass) {
	  return getInstance().getBeanClassesMap().containsKey(interfaceClass);
  }
  
  /**
   * <p>
   * Checks if the sessionbean by class beanIntefaceClass has been initialized (and is alive in session).
   * </p>
   * @param request
   * @param beanInterfaceClass
   * @return
   */
  public static boolean isSessionBeanInitialized(HttpServletRequest request,Class beanInterfaceClass){
	  return isSessionBeanInitialized(request.getSession(),beanInterfaceClass);
  }
  
  /**
   * <p>
   * Checks if the sessionbean by class beanIntefaceClass has been initialized (and is alive in session).
   * </p>
   * @param request
   * @param beanInterfaceClass
   * @return
   */
  public static boolean isSessionBeanInitialized(HttpSession session,Class beanInterfaceClass){
	  String key = getInstance().getSessionKeyForObject(beanInterfaceClass);
	  Object bean = session.getAttribute(key);
	  if(bean!=null){
		  return true;
	  }
	  return false;
  }
  
  
  /**
   * <p>
   * Checks if the sessionbean by class beanIntefaceClass has been initialized (and is alive in session).
   * </p>
   * @param request
   * @param beanInterfaceClass
   * @return
   */
  public static boolean isSessionBeanInitialized(IWUserContext iwuc,Class beanInterfaceClass){
	  String key = getInstance().getSessionKeyForObject(beanInterfaceClass);
	  Object bean = iwuc.getSessionAttribute(key);
	  if(bean!=null){
		  return true;
	  }
	  return false;
  }
  
	/**
	 * Returns an instance of a IBOSession bean.
	 * The instance is stored in the session for t. After <b>this</b> method is called there should be a corresponding call to removeSessionAttribute() to clean up from the users context.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static IBOSession getSessionInstance(HttpServletRequest request, Class beanInterfaceClass) throws IBOLookupException
	{
		checkAnnotatedAsSpringBean(beanInterfaceClass);
		return getInstance().getSessionInstanceImpl(request.getSession(), beanInterfaceClass);
	}
	
	/**
	 * Returns an instance of a IBOSession bean.
	 * The instance is stored in the session for t. After <b>this</b> method is called there should be a corresponding call to removeSessionAttribute() to clean up from the users context.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static IBOSession getSessionInstance(HttpSession session, Class beanInterfaceClass) throws IBOLookupException
	{
		checkAnnotatedAsSpringBean(beanInterfaceClass);
		return getInstance().getSessionInstanceImpl(session, beanInterfaceClass);
	}
	
	private IBOSession getSessionInstanceImpl(HttpSession session, Class beanInterfaceClass) throws IBOLookupException
	{
		IBOSession sessionBean = (IBOSession) session.getAttribute(this.getSessionKeyForObject(beanInterfaceClass));
		if (sessionBean == null)
		{
			IWUserContext iwuc = new IWUserContextImpl(session,session.getServletContext());
			sessionBean = getSessionInstanceImpl(iwuc,beanInterfaceClass);
		}
		return sessionBean;

	}
	
	private static void checkAnnotatedAsSpringBean(Class interface_class) {
		
		if(interface_class.isAnnotationPresent(SpringBeanName.class)) {
			throw new UnsupportedOperationException(
					"Provided interface: "+interface_class.getName()+
					" is annotated as spring bean therefore it's not IBO bean anymore. " +
					"You need to retrieve this bean either by letting spring to inject it (use this one if possible)" +
					" or making a lookup from SpringBeanLookup -> getSpringBean(..)");
		}
	}
}