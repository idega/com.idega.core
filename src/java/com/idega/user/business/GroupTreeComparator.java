
package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.presentation.IWContext;


/**
 * @author Sigtryggur
 * Comparator created to sort the nodes in the group tree
 * First is ordered by groupType. If the typs is the same then the group name is used to order by.
 * If the group names start with digits the comparason is done between the numbers in the start, otherwise it is done alphabetically
 */
public class GroupTreeComparator implements Comparator {

	  private Locale _locale;
	  private final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
		
	  public GroupTreeComparator(Locale locale) {
	  	_locale = locale;
	  }

	  public int compare(Object o1, Object o2) {
		int returner = 0;
	  	Collator collator = Collator.getInstance(_locale);
		System.out.println(o1.getClass()+" "+o2.getClass());
	  	if (o1 instanceof GroupTreeNode &&  o2 instanceof GroupTreeNode) {
			GroupTreeNode g1 = (GroupTreeNode) o1;
			GroupTreeNode g2 = (GroupTreeNode) o2;
		
			IWContext iwc = IWContext.getInstance();
			String groupType1 = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(_locale).getLocalizedString(g1.getGroupType());
			String groupType2 = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(_locale).getLocalizedString(g2.getGroupType());
			
			if (groupType1 != null && groupType2 == null) {
			    returner = -1;
			} else if (groupType1 == null && groupType2 != null) {
			    returner = 1;
			} else if (groupType1 != null && groupType2 != null) { 
				returner = collator.compare(groupType1, groupType2);
				if (returner == 0) {
					Integer groupIntValue1 = getIntegerFromBeginnigOfString(g1.getNodeName());
					Integer groupIntValue2 = getIntegerFromBeginnigOfString(g2.getNodeName());
		
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
							returner = collator.compare(g1.getNodeName(), g2.getNodeName());
					}
					else {
						returner = collator.compare(g1.getNodeName(), g2.getNodeName());
					}
				}
			} else {
				returner = collator.compare(g1.getNodeName(), g2.getNodeName());
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
