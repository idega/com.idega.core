package com.idega.user.business;

import java.util.Comparator;

import com.idega.user.data.User;

public class UserComparatorByName implements Comparator {

	public int compare(Object arg0, Object arg1) {
		User user1 = (User) arg0;
		User user2 = (User) arg0;
		int result = 0;
		
		String name1 = user1.getName();
		String name2 = user2.getName();
		
		if (name1 == null && name2 == null) {
			result = 0;
		}
		else if (name2 == null) {
			result = 1;
		}
		else if (name1 == null) {
			result = -1;
		}
		else {
			result = name1.compareTo(name2);
		}
		
		return result;
	}

}
