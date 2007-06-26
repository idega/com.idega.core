package com.idega.bean;

import java.util.Comparator;

public class GroupMemberDataBeanComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		GroupMemberDataBean bean1 = (GroupMemberDataBean)arg0;
		GroupMemberDataBean bean2 = (GroupMemberDataBean)arg1;
		int result = 0;
		
		Integer statusOrder1 = bean1.getStatusOrder();
		Integer statusOrder2 = bean2.getStatusOrder();
		
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
