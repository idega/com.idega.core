/*
 * Created on Aug 15, 2004
 */
package com.idega.core.idgenerator.business;

import java.util.Collection;
import java.util.Iterator;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.Timer;

/**
 * This beans contains methods to apply unique ids
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public class UUIDBusinessBean extends IBOServiceBean implements UUIDBusiness{

	private IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
	/**
	 * 
	 */
	public UUIDBusinessBean() {
		super();
	}
	
	/**
	 * Adds a UUID to all users and groups
	 */
	public void createUniqueIDsForUsersAndGroups() {
		try {
			Timer timer = new Timer();
			timer.start();
			Collection users = ((UserHome) IDOLookup.getHome(User.class)).findAllUsers();
			timer.stop();
			System.out.println("Done fetching users, time : "+ timer.getTimeString());
			timer.reset();

			timer.start();
			if (!users.isEmpty()) {
				Iterator iter = users.iterator();
				while (iter.hasNext()) {
					User user = (User) iter.next();
					addUniqueKeyIfNeeded(user, null);
					//Group groupForUser = user.getUserGroup();
					//redundant but might be useful, (TO SLOW)
					//addUniqueKeyIfNeeded(groupForUser,user.getUniqueId());
					user = null;
				}
				users = null;
			}
			timer.stop();
			System.out.println("Time for users : " + timer.getTime());
			timer.reset();

			timer.start();
			Collection groups = ((GroupHome) IDOLookup.getHome(Group.class)).findAll();

			timer.stop();
			System.out.println("Time for collecting groups : "+ timer.getTime());
			timer.reset();

			timer.start();
			if (!groups.isEmpty()) {
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					Group group = (Group) iter.next();
					addUniqueKeyIfNeeded(group, null);
					group = null;
				}
				groups = null;
			}
			timer.stop();
			System.out.println("Time for groups : " + timer.getTime());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addUniqueKeyIfNeeded(Group group, String uniqueIdToCopy) {
		if (group.getUniqueId() == null) {
			String uniqueId;
			if (uniqueIdToCopy == null) {
				uniqueId = uidGenerator.generateId();
			} else {
				uniqueId = uniqueIdToCopy;
			}

			group.setUniqueId(uniqueId);
			group.store();
		}
	}


}
