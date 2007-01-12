/*
 * $Id: UserGroupPluginFormCollector.java,v 1.2.2.1 2007/01/12 19:33:02 idegaweb Exp $ Created on
 * Apr 13, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.user.util;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.GenericFormCollector;

/**
 * A simple extend of GenericFormCollector that calls afterCreateOrUpdateUser in
 * all UserGroupPlugins
 * 
 * Last modified: $Date: 2007/01/12 19:33:02 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2.2.1 $
 */
public class UserGroupPluginFormCollector extends GenericFormCollector {

	private User user = null;
	private Group group = null;

	public UserGroupPluginFormCollector(User user) {
		super();
		this.user = user;
	}

	public UserGroupPluginFormCollector(Group group) {
		super();
		this.group = group;
	}

	public boolean storeAll(IWContext iwc) {
		if (super.storeAll(iwc)) {
			try {
				if (this.user != null) {
					//MUST BE SURE WE HAVE THE LATEST DATA
					this.user = getUserBusiness(iwc).getUser((Integer)this.user.getPrimaryKey());
					getUserBusiness(iwc).callAllUserGroupPluginAfterUserCreateOrUpdateMethod(this.user);
				}
				if (this.group != null) {
					//MUST BE SURE WE HAVE THE LATEST DATA
					this.group = getGroupBusiness(iwc).getGroupByGroupID(((Integer)this.group.getPrimaryKey()).intValue());
					getGroupBusiness(iwc).callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(this.group);
				}
			}
			catch (IBOLookupException e) {
				e.printStackTrace();
				return false;
			}
			catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
			catch (EJBException e) {
				e.printStackTrace();
				return false;
			}
			catch (FinderException e) {
				e.printStackTrace();
				return false;
			}
			catch (CreateException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}

	public UserBusiness getUserBusiness(IWApplicationContext iwac) throws IBOLookupException {
		return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
	}

	public GroupBusiness getGroupBusiness(IWApplicationContext iwac) throws IBOLookupException {
		return (GroupBusiness) IBOLookup.getServiceInstance(iwac, GroupBusiness.class);
	}

	/**
	 * @return Returns the group.
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group
	 *            The group to set.
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @return Returns the user.
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 *            The user to set.
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
