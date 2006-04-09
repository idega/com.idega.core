package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.core.data.ICTreeNode;
import com.idega.user.data.Group;

/**
 * @author Sigtryggur
 * Comparator to handle sorting of groups within divisions
 */
public class PlayerComparator implements Comparator {
		
	private Locale _locale;
	
	public PlayerComparator(Locale locale) {
		this._locale = locale;	
	}
	
	public int compare(Object arg0, Object arg1) {
		int comp = 0;
		try {
			Collator collator = Collator.getInstance(this._locale);
			Group group0 = (Group) arg0;
			Group group1 = (Group) arg1;
			ICTreeNode parent0 = group0.getParentNode();
			ICTreeNode parent1 = group0.getParentNode();
			if (parent0 != null && parent1 == null) {
			    comp = -1;
			} else if (parent0 == null && parent1 != null) {
			    comp = 1;
			} else if (parent0 != null && parent1 != null) { 
			    String parentName0 = parent0.getNodeName();
				String parentName1 = parent1.getNodeName();
				System.out.println(parentName0);
				System.out.println(parentName1);
				comp = collator.compare(parentName0, parentName1);
			}

			if(comp == 0) {
				String groupName0 = group0.getName();
				String groupName1 = group1.getName();
				System.out.println(groupName0);
				System.out.println(groupName1);
				comp = collator.compare(groupName0, groupName1);
			}
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(comp);
		return comp;
	}
}