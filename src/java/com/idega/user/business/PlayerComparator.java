package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.user.data.Group;

/**
 * @author Sigtryggur
 *
 */
public class PlayerComparator implements Comparator {
		
	private Locale _locale;
	
	public PlayerComparator(Locale locale) {
		_locale = locale;	
	}
	
	public int compare(Object arg0, Object arg1) {
		int comp = 0;
		try {
			Collator collator = Collator.getInstance(_locale);
			Group group0 = (Group) arg0;
			Group group1 = (Group) arg1;
			String parentNode0 = group0.getParentNode().getNodeName();
			String parentNode1 = group1.getParentNode().getNodeName();
			comp = collator.compare(parentNode0, parentNode1);
							
			if(comp == 0) {
				String groupName0 = group0.getName();
				String groupName1 = group1.getName();
				comp = collator.compare(groupName0, groupName1);
			}
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		return comp;
	}
}