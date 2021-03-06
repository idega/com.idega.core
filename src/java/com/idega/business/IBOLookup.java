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
 * @param <SB>
 */
public class IBOLookup<Service, ServiceBean, Home, S extends Service, SB extends ServiceBean> implements Singleton {

	private static IBOLookup<IBOService, IBOServiceBean, IBOHome, IBOService, IBOServiceBean> instance;

	protected static synchronized IBOLookup<IBOService, IBOServiceBean, IBOHome, IBOService, IBOServiceBean> getInstance() {
		if (instance == null) {
			instance = new IBOLookup<IBOService, IBOServiceBean, IBOHome, IBOService, IBOServiceBean>();
		}
		return instance;
	}

	/**
	 * Unload the previously loaded instance and all its resources
	 */
	public static void unload() {
		instance=null;
	}

	protected final String HOME_SUFFIX = "Home";
	protected final String FACTORY_SUFFIX = "HomeImpl";
	private final String BEAN_SUFFIX = "Bean";
	protected String getBeanSuffix() {
		return this.BEAN_SUFFIX;
	}

	private Map<String, Home> homes;
	private Map<Class<S>, Class<SB>> beanClasses;
	private Map<Class<SB>, Class<S>> interfaceClasses;
	private Map<Class<S>, SB> services;
	private Properties jndiProperties;
	private Map<Class<Home>, Method> createMethodsMap;

	protected IBOLookup() {}

	protected static <H> H getHomeForClass(Class<?> beanInterfaceClass) throws IBOLookupException {
		H home = (H) getInstance().getEJBHomeInstance((Class<IBOService>) beanInterfaceClass);
		return home;
	}

	protected static <H> H getIBOHomeForClass(Class<?> beanInterfaceClass) throws IBOLookupException {
		return getHomeForClass(beanInterfaceClass);
	}

	/**
	 * Returns an instance of a IBOSession bean.
	 * The instance is stored in the session for the (current) user context. After <b>this</b> method is called there should be a corresponding call to removeSessionAttribute() to clean up from the users context.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static <S extends IBOSession> S getSessionInstance(IWUserContext iwuc, Class<S> beanInterfaceClass) throws IBOLookupException {
		checkAnnotatedAsSpringBean(beanInterfaceClass);
		return getInstance().getSessionInstanceImpl(iwuc, beanInterfaceClass);
	}

	private <Session extends IBOSession> Session getSessionInstanceImpl(IWUserContext iwuc, Class<Session> beanInterfaceClass) throws IBOLookupException {
		Session session = (Session) iwuc.getSessionAttribute(this.getSessionKeyForObject(beanInterfaceClass));
		if (session == null) {
			try {
				session = instanciateSessionBean(beanInterfaceClass);
			 	iwuc.setSessionAttribute(getSessionKeyForObject(beanInterfaceClass), session);
				session.setUserContext(iwuc);
			}
			catch (CreateException cre) {
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
	public static void removeSessionInstance(IWUserContext iwuc, Class<? extends IBOSession> beanInterfaceClass) throws RemoteException, RemoveException {
		getInstance().removeSessionInstanceImpl(iwuc, beanInterfaceClass);
	}

	private void removeSessionInstanceImpl(IWUserContext iwuc, Class<? extends IBOSession> beanInterfaceClass) throws RemoteException, RemoveException {
		IBOSession session = getSessionInstance(iwuc, beanInterfaceClass);
		session.remove();
		iwuc.removeSessionAttribute(getSessionKeyForObject(beanInterfaceClass));
	}

	/**
	 * Cleans up after an an instance of a IBOSession bean has been used.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static void removeSessionInstance(HttpSession session, Class<? extends IBOSession> beanInterfaceClass) throws RemoteException, RemoveException {
		getInstance().removeSessionInstanceImpl(session, beanInterfaceClass);
	}

	private void removeSessionInstanceImpl(HttpSession session, Class<? extends IBOSession> beanInterfaceClass) throws RemoteException, RemoveException {
		IBOSession sessionBean = getSessionInstance(session, beanInterfaceClass);
		sessionBean.remove();
		session.removeAttribute(getSessionKeyForObject(beanInterfaceClass));
	}

	private <Session extends IBOSession> Session instanciateSessionBean(Class<Session> beanInterfaceClass) throws IBOLookupException, CreateException {
		return instanciateServiceBean(beanInterfaceClass);
	}

	private <IService extends IBOService> IService instanciateServiceBean(Class<IService> beanInterfaceClass) throws IBOLookupException, CreateException {
		IService service = null;
		Home home = getIBOHomeForClass(beanInterfaceClass);
		try{
			Method defaultCreateMethod = getCreateMethod(home);
			service = (IService) defaultCreateMethod.invoke(home, (Object[]) null);
		} catch(InvocationTargetException ite){
			//ite.printStackTrace();
			Throwable e = ite.getTargetException();
			//e.printStackTrace();
			throw new CreateException("Exception invoking create method for: "+beanInterfaceClass.getName()+". Error was:"+e.getClass().getName()+" : "+e.getMessage());
		} catch(Exception e){
			throw new CreateException("Exception invoking create method for: "+beanInterfaceClass.getName()+". Error was:"+e.getClass().getName()+" : "+e.getMessage());
		}
		return service;
	}

	/**
	 * Method getCreateMethod.
	 * @param home
	 * @return Method
	 */
	private Method getCreateMethod(Home home) throws NoSuchMethodException{
		Map<Class<Home>, Method> map = getCreateMethodsMap();
		Class<Home> homeClass = (Class<Home>) home.getClass();
		Method m = map.get(homeClass);
		if(m==null){
			m = MethodFinder.getInstance().getMethodWithNameAndNoParameters(homeClass,"create");
			map.put(homeClass, m);
		}
		return m;
	}

	/**
	 * Method getCreateMethodsMap.
	 * @return Map
	 */
	private Map<Class<Home>, Method> getCreateMethodsMap() {
		if(this.createMethodsMap==null){
			this.createMethodsMap=new HashMap<Class<Home>, Method>();
		}
		return this.createMethodsMap;
	}

	/**
	 * Returns an instance of a IBOService bean.
	 * The instance is stored in the application context and is shared between all users.
	 * @param iwac A reference to the application (context) the bean should be looked up.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static <S extends IBOService> S getServiceInstance(IWApplicationContext iwac, Class<S> beanInterfaceClass) throws IBOLookupException {
		S service = (S) getInstance().getServiceInstanceImpl(iwac, (Class<IBOService>) beanInterfaceClass);
		return service;
	}

	private <IService extends IBOService> SB getServiceInstanceImpl(IWApplicationContext iwac, Class<IService> beanInterfaceClass) throws IBOLookupException {
		Map<Class<S>, SB> map = this.getServicesMap(iwac);
		SB service = map.get(beanInterfaceClass);
		if (service == null) {
			try {
				IService iboService = instanciateServiceBean(beanInterfaceClass);
				service = (SB) iboService;

				if (iwac != null) {
					((IBOServiceBean) service).setIWApplicationContext(iwac);
				}
				((IBOService) service).initializeBean();
				getServicesMap(iwac).put((Class<S>) beanInterfaceClass, service);
			}
			catch (CreateException cre) {
				throw new IBOLookupException("[IBOLookup] : CreateException : " + cre.getMessage());
			}
		}
		return service;
	}

	protected <H extends Home> Class<H> getHomeInterfaceClassFor(Class<S> entityInterfaceClass) throws Exception {
		try{
			//First try to suffix the Home to the interface class name
			String baseClassName = getInterfaceClassForNonStatic(entityInterfaceClass).getName();
			String homeClassName = baseClassName + this.HOME_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		} catch(ClassNotFoundException cnfe){
			//If that doesn't work then try to suffix the Home to the bean implementation class name - the bean suffix
			String beanClassName = getBeanClassForNonStatic(entityInterfaceClass).getName();
			String baseClassName = beanClassName.substring(0, beanClassName.indexOf(getBeanSuffix()));
			String homeClassName = baseClassName + this.HOME_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		}

	}

	protected Class<Home> getFactoryClassFor(Class<S> entityInterfaceClass) throws Exception {
		try{
			//First try to suffix the FACTORY_SUFFIX to the bean implementation class name - the bean suffix
			String beanClassName = getBeanClassForNonStatic(entityInterfaceClass).getName();
			String baseClassName = beanClassName.substring(0,beanClassName.indexOf(getBeanSuffix()));
			String homeClassName = baseClassName + this.FACTORY_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		} catch(ClassNotFoundException cnfe) {
			//If that doesn't work then try to suffix the FACTORY_SUFFIX to the interface class name
			String baseClassName = getInterfaceClassForNonStatic(entityInterfaceClass).getName();
			String homeClassName = baseClassName + this.FACTORY_SUFFIX;
			return RefactorClassRegistry.forName(homeClassName);
		}

	}

	private String getSessionKeyForObject(Class<? extends IBOService> interfaceClass) {
		return "IBO." + interfaceClass.getName();
	}

	/**
	 * Gets an instance of the implementation of the Home interface for the data bean.
	 * <br>The object returned can then needs to be casted to the specific home interface for the bean.
	 * @param entityInterfaceClass i the interface of the data bean.
	 */
	protected Home getEJBHomeInstance(Class<S> entityBeanOrInterfaceClass){
		return getEJBHomeInstance(entityBeanOrInterfaceClass, null);
	}

	protected Home homesMapLookup(Class<S> entityInterfaceClass, String parameter) {
		return getHomesMap().get(entityInterfaceClass+parameter);
	}

	/**
	 * Gets an instance of the implementation of the Home interface for the data bean.
	 * <br>The object retured can then needs to be casted to the specific home interface for the bean.
	 * @param entityInterfaceClass i the interface of the data bean.
	 * @param parameter is a parameter used to separete different instances of the same Home class
	 */
	protected Home getEJBHomeInstance(Class<S> entityBeanOrInterfaceClass, String parameter){
		//Double check so it is not the bean class that is sent into the methods below
		Class<S> entityInterfaceClass = getInterfaceClassForNonStatic(entityBeanOrInterfaceClass);

		Home home = homesMapLookup(entityInterfaceClass, parameter);
		if (home == null) {
			try {
				if (doLookupOverJNDI(entityInterfaceClass)) {
					home = getHomeThroughJNDI(entityInterfaceClass);
				} else{
					Class<Home> factoryClass = getFactoryClassFor(entityInterfaceClass);
					home = factoryClass.newInstance();
				}
				getHomesMap().put(entityInterfaceClass+parameter, home);
			} catch (Exception e) {
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
	static <S extends IBOService, SB extends IBOServiceBean> Class<SB> getIBOBeanClassFor(Class<S> entityInterfaceClass) {
		Class<SB> theClass = (Class<SB>) getInstance().getBeanClassForNonStatic((Class<IBOService>) entityInterfaceClass);
		return theClass;
	}

	/**
	 * Gets the Class object for the (BMP) bean class of a data bean.
	 * @param entityInterfaceClass i the (Remote) interface of the data bean.
	 */
	protected Class<SB> getBeanClassForNonStatic(Class<S> entityInterfaceClass) {
		try	{
			Map<Class<S>, Class<SB>> cache = getBeanClassesMap();
			Class<SB> beanClass = cache.get(entityInterfaceClass);
			if (beanClass == null) {
				if (entityInterfaceClass.isInterface()) {
					String className = entityInterfaceClass.getName();
					String beanClassName = className + getBeanSuffix();
					beanClass = RefactorClassRegistry.forName(beanClassName);

					cache.put(entityInterfaceClass, beanClass);
					getInterfaceClassesMap().put(beanClass, entityInterfaceClass);
				} else {
					beanClass = (Class<SB>) entityInterfaceClass;
				}
			}
			return beanClass;
		} catch (Exception e) {
			throw new RuntimeException(e.getClass().getName() + " : " + e.getMessage());
		}
	}

	/**
	* Gets the Class object for the (Remote) interface of a data bean.
	* @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
	*/
	protected Class<S> getInterfaceClassForNonStatic(Class<S> entityBeanOrInterfaceClass) {
		if (entityBeanOrInterfaceClass.isInterface()) {
			return entityBeanOrInterfaceClass;
		} else {
			Map<Class<SB>, Class<S>> cache2 = getInterfaceClassesMap();
			Class<S> interfaceClass = cache2.get(entityBeanOrInterfaceClass);
			try {
				if (interfaceClass == null) {
					String className = entityBeanOrInterfaceClass.getName();
					int endIndex = className.indexOf(getBeanSuffix());
					if (endIndex != -1)	{
						String interfaceClassName = className.substring(0, endIndex);
						interfaceClass = RefactorClassRegistry.forName(interfaceClassName);

						Map<Class<S>, Class<SB>> cache = getBeanClassesMap();
						cache.put(interfaceClass, (Class<SB>) entityBeanOrInterfaceClass);
						getInterfaceClassesMap().put((Class<SB>) entityBeanOrInterfaceClass, interfaceClass);
					} else {
						//For legacy beans
						interfaceClass = entityBeanOrInterfaceClass;
					}
				}
				return interfaceClass;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.getClass() + ": " + e.getMessage());
			}
		}
	}

	private Map<Class<S>, SB> getServicesMap(IWApplicationContext iwac) {
		return getServicesMap();
	}

	private Map<Class<S>, SB> getServicesMap() {
		if (this.services == null) {
			this.services = new HashMap<Class<S>, SB>();
		}
		return this.services;
	}
	/**
	 * Clears all cached object instances of looked up objects (Home instances etc.)
	 **/
	public static synchronized void clearAllCache() {
		getInstance().getHomesMap().clear();
		getInstance().getBeanClassesMap().clear();
		getInstance().getInterfaceClassesMap().clear();
	}

	protected Home getHomeThroughJNDI(Class<S> beanInterfaceClass)throws RemoteException{
		Home home = null;
		try{
	  		InitialContext jndiContext = getInitialContext();
			Home homeObj = (Home) jndiContext.lookup(getBeanClassForNonStatic(beanInterfaceClass).getName());
			//home = (ResponseHome) jndiContext.lookup("java:comp/env/"+ResponseBMPBean.class.getName());
			home = (Home) PortableRemoteObject.narrow(homeObj, getHomeInterfaceClassFor(beanInterfaceClass));
			return home;
		} catch(Exception e){
			throw new RemoteException("Error looking up home for "+beanInterfaceClass.getName()+". Errormessage was: "+e.getMessage());
		}
	}

	protected boolean doLookupOverJNDI(Class<S> beanInterfaceClass){
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

  /**
   * Interface -> Class
   *
   * @return
   */
  public Map<Class<S>, Class<SB>> getBeanClassesMap(){
  	if (this.beanClasses == null) {
  		this.beanClasses = new HashMap<Class<S>, Class<SB>>();
  	}
  	return this.beanClasses;
  }

  public Map<Class<SB>, Class<S>> getInterfaceClassesMap(){
  	if(this.interfaceClasses==null){
  		this.interfaceClasses= new HashMap<Class<SB>, Class<S>>();
  	}
  	return this.interfaceClasses;
  }

  public Map<String, Home> getHomesMap(){
  	if(this.homes==null){
  		this.homes= new HashMap<String, Home>();
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
  public static void registerImplementationForBean(Class<? extends IBOService> interfaceClass, Class<? extends IBOServiceBean> beanClass) {
	  Map<Class<IBOService>, Class<IBOServiceBean>> cache = getInstance().getBeanClassesMap();
	  cache.put((Class<IBOService>) interfaceClass, (Class<IBOServiceBean>) beanClass);

	  Map<Class<IBOServiceBean>, Class<IBOService>> cache2 = getInstance().getInterfaceClassesMap();
	  cache2.put((Class<IBOServiceBean>) beanClass, (Class<IBOService>) interfaceClass);
  }

  /**
   * Checks if an implementation of the specified bean is registered.
   * This method should be called if the caller is not sure if an implementation exists.
   * If an implementation does not exist call of the getServiceInstance method
   * returns a RuntimeException when trying to get the bean.
   *
   * Returns true if there is an entry else false
   *
   * @param interfaceClass
   * @return true if an implementation is registered else false
   * @see  #registerImplementationForBean(Class, Class)
   */
  public static <S extends IBOService> boolean isImplementationRegistered(Class<S> interfaceClass) {
	  return getInstance().getBeanClassesMap().containsKey(interfaceClass);
  }

  /**
   * <p>
   * Checks if the session bean by class beanIntefaceClass has been initialized (and is alive in session).
   * </p>
   * @param request
   * @param beanInterfaceClass
   * @return
   */
  public static boolean isSessionBeanInitialized(HttpServletRequest request, Class<? extends IBOSession> beanInterfaceClass){
	  return isSessionBeanInitialized(request.getSession(), beanInterfaceClass);
  }

  /**
   * <p>
   * Checks if the session bean by class beanIntefaceClass has been initialized (and is alive in session).
   * </p>
   * @param request
   * @param beanInterfaceClass
   * @return
   */
  public static boolean isSessionBeanInitialized(HttpSession session, Class<? extends IBOSession> beanInterfaceClass){
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
  public static boolean isSessionBeanInitialized(IWUserContext iwuc, Class<? extends IBOSession> beanInterfaceClass) {
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
	public static <Session extends IBOSession> Session getSessionInstance(HttpServletRequest request, Class<Session> beanInterfaceClass) throws IBOLookupException {
		checkAnnotatedAsSpringBean(beanInterfaceClass);
		return getInstance().getSessionInstanceImpl(request.getSession(), beanInterfaceClass);
	}

	/**
	 * Returns an instance of a IBOSession bean.
	 * The instance is stored in the session for t. After <b>this</b> method is called there should be a corresponding call to removeSessionAttribute() to clean up from the users context.
	 * @param iwuc A reference to the user (context) the bean instance is working under.
	 * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
	 */
	public static <Session extends IBOSession> Session getSessionInstance(HttpSession session, Class<Session> beanInterfaceClass) throws IBOLookupException {
		checkAnnotatedAsSpringBean(beanInterfaceClass);
		return getInstance().getSessionInstanceImpl(session, beanInterfaceClass);
	}

	private <Session extends IBOSession> Session getSessionInstanceImpl(HttpSession session, Class<Session> beanInterfaceClass) throws IBOLookupException {
		Session sessionBean = (Session) session.getAttribute(this.getSessionKeyForObject(beanInterfaceClass));
		if (sessionBean == null) {
			IWUserContext iwuc = new IWUserContextImpl(session,session.getServletContext());
			sessionBean = getSessionInstanceImpl(iwuc,beanInterfaceClass);
		}
		return sessionBean;
	}

	private static <S extends IBOService> void checkAnnotatedAsSpringBean(Class<S> interface_class) {
		if (interface_class.isAnnotationPresent(SpringBeanName.class)) {
			throw new UnsupportedOperationException(
					"Provided interface: "+interface_class.getName()+
					" is annotated as spring bean therefore it's not IBO bean anymore. " +
					"You need to retrieve this bean either by letting spring to inject it (use this one if possible)" +
					" or making a lookup from SpringBeanLookup -> getSpringBean(..)");
		}
	}
}