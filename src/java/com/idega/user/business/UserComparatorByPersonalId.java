package com.idega.user.business;

import java.util.Comparator;

import com.idega.user.data.User;

public class UserComparatorByPersonalId implements Comparator {

	public int compare(Object arg0, Object arg1) {
		User user1 = (User) arg0;
		User user2 = (User) arg0;
		int result = 0;
		
		Integer id1 = null;
		try {
			id1 = Integer.valueOf(user1.getPersonalID());
		} catch (Exception e) {}
		Integer id2 = null;
		try {
			id2 = Integer.valueOf(user2.getPersonalID());
		} catch (Exception e) {}
		
		if (id1 == null && id2 == null) {
			result = 0;
		}
		else if (id1 == null) {
			result = 1;
		}
		else if (id2 == null) {
			result = -1;
		}
		else {
			result = id1.compareTo(id2);
		}
		
		return result;
	}

}
