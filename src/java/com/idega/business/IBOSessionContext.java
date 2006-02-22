package com.idega.business;
import java.io.Serializable;
import java.security.Identity;
import java.security.Principal;
import javax.ejb.*;
import java.util.Properties;
import javax.transaction.UserTransaction;
import javax.transaction.SystemException;
import com.idega.transaction.IdegaTransactionManager;
/**
 * Title:        idega Business Objects
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IBOSessionContext implements SessionContext, Serializable
{
	private EJBObject ejbo;
	private EJBHome ejbHome;
	private boolean roolbackOnly = false;
	IBOSessionContext()
	{
	}
	public EJBObject getEJBObject() throws java.lang.IllegalStateException
	{
		return ejbo;
	}
	void setEJBObject(EJBObject ejbo)
	{
		this.ejbo = ejbo;
	}
	public Identity getCallerIdentity()
	{
		/**@todo: Implement this javax.ejb.EJBContext method*/
		throw new java.lang.UnsupportedOperationException("Method getCallerIdentity() not yet implemented.");
	}
	public Principal getCallerPrincipal()
	{
		/**@todo: Implement this javax.ejb.EJBContext method*/
		throw new java.lang.UnsupportedOperationException("Method getCallerPrincipal() not yet implemented.");
	}
	public EJBHome getEJBHome()
	{
		return ejbHome;
	}
	void setEJBHome(EJBHome ejbHome)
	{
		this.ejbHome = ejbHome;
	}
	public Properties getEnvironment()
	{
		/**@todo: Implement this javax.ejb.EJBContext method*/
		throw new java.lang.UnsupportedOperationException("Method getEnvironment() not yet implemented.");
	}
	public boolean getRollbackOnly() throws java.lang.IllegalStateException
	{
		return this.roolbackOnly;
	}
	/**
	 * Returns the current UserTransaction associated with the current Thread , or a new
	 * UserTransaction if there is no UserTransaction associated with the current Thread.
	 */
	public UserTransaction getUserTransaction() throws java.lang.IllegalStateException
	{
		try
		{
			return (UserTransaction) IdegaTransactionManager.getInstance().getTransaction();
		}
		catch (SystemException se)
		{
			throw new IllegalStateException("SystemException : " + se.getMessage());
		}
	}
	public boolean isCallerInRole(String parm1)
	{
		/**@todo: Implement this javax.ejb.EJBContext method*/
		throw new java.lang.UnsupportedOperationException("Method isCallerInRole() not yet implemented.");
	}
	public boolean isCallerInRole(Identity parm1)
	{
		/**@todo: Implement this javax.ejb.EJBContext method*/
		throw new java.lang.UnsupportedOperationException("Method isCallerInRole() not yet implemented.");
	}
	public void setRollbackOnly() throws java.lang.IllegalStateException
	{
		this.roolbackOnly = true;
	}
	public EJBLocalObject getEJBLocalObject()
	{
		return (EJBLocalObject) this.getEJBObject();
	}
	public EJBLocalHome getEJBLocalHome()
	{
		return (EJBLocalHome) this.getEJBHome();
	}
}