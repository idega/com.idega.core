/*
 * Created on Aug 15, 2004
 */
package com.idega.core.idgenerator.business;

import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.Timer;

/**
 * This beans contains methods to apply unique ids
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson </a>
 *  
 */
public class UUIDBusinessBean extends IBOServiceBean implements UUIDBusiness {

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
	public void generateUUIDsForAllUsersAndGroups() {
		try {
			generateUUIDsForAllUsers();
			generateUUIDsForAllGroups();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a UUID to the group or user or copies the uuid you pass into the
	 * method. Then stores the user/group
	 */
	public void addUniqueKeyIfNeeded(Group group, String uniqueIdToCopy) {
		if (group.getUniqueId() == null) {
			String uniqueId;
			if (uniqueIdToCopy == null) {
				uniqueId = uidGenerator.generateId();
			}
			else {
				uniqueId = uniqueIdToCopy;
			}
			group.setUniqueId(uniqueId);
			group.store();
		}
	}

	/**
	 * Removes all UUID from all users and groups.
	 */
	public void removeUniqueIDsForUsersAndGroups() {
		try {
			removeUUIDsFromAllUsers();
			removeUUIDsFromAllGroups();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateUUIDsForAllGroups() throws FinderException, IDOLookupException {
		int counter = 0;
		int reportAfter = 500;
		Timer timer = new Timer();
		timer.start();
		Collection groups = ((GroupHome) IDOLookup.getHome(Group.class)).findAll();
		timer.stop();
		System.out.println("Time for collecting groups : " + timer.getTime()/1000+"s");
		timer.reset();
		timer.start();
		if (!groups.isEmpty()) {
			Iterator iter = groups.iterator();
			while (iter.hasNext()) {
				counter++;
				Group group = (Group) iter.next();
				addUniqueKeyIfNeeded(group, null);
				if ((counter % reportAfter) == 0) {
					System.out.println("Time passed for creating UUIDs for " + counter + " groups : " + timer.getTime()/1000+"s");
				}
			}
			groups = null;
		}
		timer.stop();
		System.out.println("Time for groups : " + timer.getTime()/1000+"s");
	}

	public void generateUUIDsForAllUsers() throws FinderException, IDOLookupException {
		int counter = 0;
		int reportAfter = 500;
		Timer timer = new Timer();
		timer.start();
		Collection users = ((UserHome) IDOLookup.getHome(User.class)).findAllUsers();
		timer.stop();
		System.out.println("Done fetching users, time : " + timer.getTimeString());
		timer.reset();
		timer.start();
		if (!users.isEmpty()) {
			Iterator iter = users.iterator();
			while (iter.hasNext()) {
				counter++;
				User user = (User) iter.next();
				addUniqueKeyIfNeeded(user, null);
				if ((counter % reportAfter) == 0) {
					System.out.println("Time passed for creating UUIDs for " + counter + " users : " + timer.getTime()/1000+"s");
				}
				user = null;
			}
			users = null;
		}
		timer.stop();
		System.out.println("Time for users : " + timer.getTime()/1000+"s");
	}

	public void removeUUIDsFromAllGroups() throws FinderException, IDOLookupException {
		int counter = 0;
		int reportAfter = 100;
		Timer timer = new Timer();
		timer.start();
		Collection groups = ((GroupHome) IDOLookup.getHome(Group.class)).findAll();
		timer.stop();
		System.out.println("Time for collecting groups : " + timer.getTime()/1000+"s");
		timer.reset();
		timer.start();
		if (!groups.isEmpty()) {
			Iterator iter = groups.iterator();
			while (iter.hasNext()) {
				counter++;
				Group group = (Group) iter.next();
				removeUniqueIdIfPresent(group);
				if ((counter % reportAfter) == 0) {
					System.out.println("Time passed removing UUIDs for " + counter + " groups : " + timer.getTime()/1000+"s");
				}
			}
			groups = null;
		}
		timer.stop();
		System.out.println("Time for groups : " + timer.getTime()/1000+"s");
	}

	public void removeUUIDsFromAllUsers() throws FinderException, IDOLookupException {
		int counter = 0;
		int reportAfter = 100;
		Timer timer = new Timer();
		timer.start();
		Collection users = ((UserHome) IDOLookup.getHome(User.class)).findAllUsers();
		timer.stop();
		System.out.println("Done fetching users, time : " + timer.getTimeString());
		timer.reset();
		timer.start();
		if (!users.isEmpty()) {
			Iterator iter = users.iterator();
			while (iter.hasNext()) {
				counter++;
				User user = (User) iter.next();
				removeUniqueIdIfPresent(user);
				if ((counter % reportAfter) == 0) {
					System.out.println("Time passed removing UUIDs for " + counter + " users : " + timer.getTime()/1000+"s");
				}
				user = null;
			}
			users = null;
		}
		timer.stop();
		System.out.println("Time for users : " + timer.getTime()/1000+"s");
	}

	/**
	 * Removes the UUID from the user/group if it has been set and stores the
	 * user/group if needed
	 * 
	 * @param user
	 */
	public void removeUniqueIdIfPresent(Group group) {
		if (group.getUniqueId() != null) {
			group.setUniqueId(null);
			group.store();
		}
	}
}