package com.idega.business;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.ejb.FinderException;
import javax.ejb.Handle;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.user.data.UserHome;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.logging.LoggingHelper;

/**
 * Title:        idega Business Objects
 * Description:  A class to be a base implementation for IBO Service (Stateless EJB Session) beans
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IBOServiceBean implements IBOService, SessionBean {

	private static final long serialVersionUID = 2234823785602301801L;

	@Autowired
	private RepositoryService repositoryService;

  private SessionContext ejbSessionContext;
  private IWApplicationContext iwac;
  private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

  public IBOServiceBean() {
  }

  /**
   * The default implementation
   */
  public void ejbCreate()throws CreateException{
  }
  public void ejbPostCreate(){
  }

  @Override
public void initializeBean() {
  }

  public void ejbHomeIboCreate() throws CreateException{
  		ejbCreate();
  }

  public void ejbCreateIBO() throws CreateException{

  }


  public void ejbPostCreateIBO(){
  }


  public IBOService ejbHomeCreateIBO() throws CreateException{
  	throw new UnsupportedOperationException("Not implemented");
  }
  @Override
public String getServiceDescription() {
    /**@todo: Implement this com.idega.business.IBOService method*/
    throw new java.lang.UnsupportedOperationException("Method getServiceDescription() not yet implemented.");
  }
  @Override
public String getLocalizedServiceDescription(Locale locale) {
    /**@todo: Implement this com.idega.business.IBOService method*/
    throw new java.lang.UnsupportedOperationException("Method getLocalizedServiceDescription() not yet implemented.");
  }
  @Override
public EJBHome getEJBHome() throws java.rmi.RemoteException {
    return this.getSessionContext().getEJBHome();
  }

  protected EJBObject getEJBObject() throws java.rmi.RemoteException {
    return this.getSessionContext().getEJBObject();
  }

  @Override
public Handle getHandle() throws java.rmi.RemoteException {
    throw new java.lang.UnsupportedOperationException("Method getHandle() not yet implemented.");
  }
  @Override
public Object getPrimaryKey() throws EJBException {
    throw new EJBException("Method getPrimaryKey() not available for a session bean");
  }
  @Override
public boolean isIdentical(EJBObject parm1) throws java.rmi.RemoteException {
    return this.getEJBObject().equals(parm1);
  }
  @Override
public void remove() throws java.rmi.RemoteException, javax.ejb.RemoveException {
    this.ejbRemove();
  }
  @Override
public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
  }
  @Override
public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
  }
  @Override
public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    this.ejbSessionContext=null;
  }
  @Override
public void setSessionContext(SessionContext parm1) throws javax.ejb.EJBException, java.rmi.RemoteException {
    this.ejbSessionContext=parm1;
  }
  protected SessionContext getSessionContext() {
    return this.ejbSessionContext;
  }

  public void setIWApplicationContext(IWApplicationContext iwac){
  	if(iwac instanceof IWContext){
  		IWContext iwc = (IWContext)iwac;
  		this.iwac=iwc.getIWMainApplication().getIWApplicationContext();
  	}
  	else{
  		this.iwac=iwac;
  	}
  }

  @Override
public IWApplicationContext getIWApplicationContext(){
	if(this.iwac==null){
		return IWMainApplication.getDefaultIWApplicationContext();
	}
    return this.iwac;
  }

  /**
   * Gets the current IWMainApplication
   * @return
   */
  protected IWMainApplication getIWMainApplication(){
  	return getIWApplicationContext().getIWMainApplication();
  }

  /**
   * Gets the bundle for this component. Can be overridden.
   * @return
   */
  protected IWBundle getBundle(){
  	return getIWMainApplication().getBundle(getBundleIdentifier());
  }
  /**
   * Gets the bundle identifier for this component.
   * This method is used by default by the getBundle() method and should be overridden.<br>
   * By default it returns the identifier for the core bundle.
   * @return
   */
  protected String getBundleIdentifier(){
  	return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
  }


  /**
   * Get an instance of the service bean specified by serviceClass
   */
  @SuppressWarnings("unchecked")
protected <T extends IBOService> T getServiceInstance(Class<? extends IBOService> serviceClass) throws IBOLookupException {
    return (T) IBOLookup.getServiceInstance(this.getIWApplicationContext(), serviceClass);
  }

  /**
   * Get an instance of the session bean specified by serviceClass
   */
  @SuppressWarnings("unchecked")
protected <T extends IBOSession> T getSessionInstance(IWUserContext iwuc, Class<? extends IBOSession> sessionClass) throws IBOLookupException {
    return (T) IBOLookup.getSessionInstance(iwuc, sessionClass);
  }

  /**
   * Get an instance of the home interface for the IDO bean specified identified by beanClass
   */
    protected IDOHome getIDOHome(Class<?> beanClass)throws RemoteException{
    	return IDOLookup.getHome(beanClass);
  }

  	protected AccessController getAccessController() {
		return this.getIWApplicationContext().getIWMainApplication().getAccessController();
	}


  	//STANDARD LOGGING METHODS:

	/**
	 * Logs out to the default log level (which is by default INFO)
	 * @param msg The message to log out
	 */
	public void log(String msg) {
		getLogger().log(getDefaultLogLevel(),msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the default Logger
	 * @param e The Exception to log out
	 */
	protected void log(Exception e) {
		LoggingHelper.logException(e,this,getLogger(),getErrorLogLevel());
	}

	/**
	 * Logs out to the specified log level to the default Logger
	 * @param level The log level
	 * @param msg The message to log out
	 */
	protected void log(Level level,String msg) {
		//System.out.println(msg);
		getLogger().log(level,msg);
	}

	protected void log(Level level, String msg, Throwable exception) {
		getLogger().log(level, msg, exception);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the default Logger
	 * @param msg The message to log out
	 */
	protected void logError(String msg) {
		//System.err.println(msg);
		getLogger().log(getErrorLogLevel(),msg);
	}

	/**
	 * Logs out to the debug log level (which is by default FINER) to the default Logger
	 * @param msg The message to log out
	 */
	protected void logDebug(String msg) {
		//System.err.println(msg);
		getLogger().log(getDebugLogLevel(),msg);
	}

	/**
	 * Logs out to the SEVERE log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logSevere(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.SEVERE,msg);
	}


	/**
	 * Logs out to the WARNING log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logWarning(String msg) {
		getLogger().warning(msg);
	}

	/**
	 * Logs out to the CONFIG log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logConfig(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.CONFIG,msg);
	}

	/**
	 * Logs out to the debug log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void debug(String msg) {
		logDebug(msg);
	}

	/**
	 * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	 * This behaviour can be overridden in subclasses.
	 * @return the default Logger
	 */
	protected Logger getLogger(){
		return Logger.getLogger(getClass().getName());
	}

	/**
	 * Gets the log level which messages are sent to when no log level is given.
	 * @return the Level
	 */
	protected Level getDefaultLogLevel(){
		return Level.INFO;
	}
	/**
	 * Gets the log level which debug messages are sent to.
	 * @return the Level
	 */
	protected Level getDebugLogLevel(){
		return Level.FINER;
	}
	/**
	 * Gets the log level which error messages are sent to.
	 * @return the Level
	 */
	protected Level getErrorLogLevel(){
		return Level.WARNING;
	}

	@Override
	public void addActionListener(ActionListener listener) {
		if (!this.actionListeners.contains(listener)) {
			this.actionListeners.add(listener);
		}
	}

	public void triggerActionEvent(String command) {
		triggerActionEvent(command, 0);
	}

	public void triggerActionEvent(String command, int id) {
		ActionEvent e = new ActionEvent(this, id, command);
		for (ActionListener listener: actionListeners) {
			listener.actionPerformed(e);
		}
	}

	protected User getCurrentUser() {
		try {
			LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			return loginSession.getUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected com.idega.user.data.User getOldUser(User newUser) throws RemoteException {
		if (newUser == null) {
			return null;
		}

		UserHome userHome = (UserHome) getIDOHome(com.idega.user.data.User.class);
		try {
			return userHome.findByPrimaryKey(newUser.getId());
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RepositoryService getRepositoryService() {
		if (repositoryService == null) {
			ELUtil.getInstance().autowire(this);
		}
		return repositoryService;
	}
}