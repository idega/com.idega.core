
package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.user.data.Group;


/**
 * @author Sigtryggur
 * Comparator created to sort the nodes in the group tree
 * First is ordered by groupType. If the typs is the same then the group name is used to order by.
 * If the group names start with digits the comparason is done between the numbers in the start, otherwise it is done alphabetically
 */
public class GroupTreeComparator implements Comparator {

	  private Locale _locale;
		
	  public GroupTreeComparator(Locale locale) {
	  	_locale = locale;
	  }

	  public int compare(Object o1, Object o2) {
		int returner = 0;
	  	Collator collator = Collator.getInstance(_locale);
		
		Group g1 = (Group) o1;
		Group g2 = (Group) o2;
	
		returner = collator.compare(g1.getGroupType(), g2.getGroupType());
		if (returner == 0) {
			Integer groupIntValue1 = getIntegerFromBeginnigOfString(g1.getName());
			Integer groupIntValue2 = getIntegerFromBeginnigOfString(g2.getName());

			if (groupIntValue1 != null && groupIntValue2 != null) {
				if (groupIntValue1.intValue() == groupIntValue2.intValue()) {
					returner = 0;
				}
				else if (groupIntValue1.intValue() < groupIntValue2.intValue()) {
					returner = -1;
				}
				else
					returner = 1;
				
				if (returner == 0)
					returner = collator.compare(g1.getName(), g2.getName());
			}
			else {
				returner = collator.compare(g1.getName(), g2.getName());
			}
		}
		return returner;
	}
	  
	  private Integer getIntegerFromBeginnigOfString(String original) {
        if (original == null) return null;

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < original.length(); i++) {
        	if (Character.isDigit(original.charAt(i))) {
            	result.append(original.charAt(i));
            }
            else {
            	break;
            }
        }
        if (result.length() > 0)
        	return Integer.valueOf(result.toString());

        return null;
    }
}
