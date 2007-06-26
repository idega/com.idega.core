package com.idega.bean;

import java.util.Comparator;

public class GroupMemberDataBeanComparator implements Comparator<GroupMemberDataBean> {

	public int compare(GroupMemberDataBean o1, GroupMemberDataBean o2) {
		int result = 0;
		
		Integer statusOrder1 = o1.getStatusOrder();
		Integer statusOrder2 = o2.getStatusOrder();
		
		if (statusOrder1 == null && statusOrder2 == null) {
			result = 0;
		}
		else if (statusOrder2 == null) {
			result = 1;
		}
		else if (statusOrder1 == null) {
			result = -1;
		}
		else {
			result = statusOrder1.compareTo(statusOrder2);
		}
		
		return result;
	}

}
