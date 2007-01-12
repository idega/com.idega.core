
package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;

import com.idega.presentation.IWContext;


/**
 * @author Sigtryggur
 * Comparator created to sort the nodes in the group tree
 * First is ordered by groupType. If the typs is the same then the group name is used to order by.
 * If the group names start with digits the comparason is done between the numbers in the start, otherwise it is done alphabetically
 */
public class GroupTreeComparator implements Comparator {

	  private IWContext _iwc;
	  private final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
		
	  public GroupTreeComparator(IWContext iwc) {
	  	this._iwc = iwc;
	  }

	  public int compare(Object o1, Object o2) {
		int returner = 0;
	  	Collator collator = Collator.getInstance(this._iwc.getCurrentLocale());

	  	if (o1 instanceof GroupTreeNode &&  o2 instanceof GroupTreeNode) {
			GroupTreeNode g1 = (GroupTreeNode) o1;
			GroupTreeNode g2 = (GroupTreeNode) o2;
		
			String groupType1 = this._iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(this._iwc.getCurrentLocale()).getLocalizedString(g1.getGroupType());
			String groupType2 = this._iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(this._iwc.getCurrentLocale()).getLocalizedString(g2.getGroupType());
			
			if (groupType1 != null && groupType2 == null) {
			    returner = -1;
			} else if (groupType1 == null && groupType2 != null) {
			    returner = 1;
			} else if (groupType1 != null && groupType2 != null) { 
				returner = collator.compare(groupType1, groupType2);
				if (returner == 0) {
					Long groupLongValue1 = getLongFromBeginnigOfString(g1.getNodeName());
					Long groupLongValue2 = getLongFromBeginnigOfString(g2.getNodeName());
		
					if (groupLongValue1 != null && groupLongValue2 != null) {
						if (groupLongValue1.longValue() == groupLongValue2.longValue()) {
							returner = 0;
						}
						else if (groupLongValue1.longValue() < groupLongValue2.longValue()) {
							returner = -1;
						}
						else {
							returner = 1;
						}
						
						if (returner == 0) {
							returner = compareNodes(collator, g1, g2);
						}
					}
					else {
					    returner = compareNodes(collator, g1, g2);
					}
				}
			} else {
			    returner = compareNodes(collator, g1, g2);
			}
		}
		return returner;
	}
	  
	  /**
     * @param returner
     * @param collator
     * @param g1
     * @param g2
     * @return
     */
    private int compareNodes(Collator collator, GroupTreeNode g1, GroupTreeNode g2) {
        int returner = 0;
        if (g1.getNodeName() != null && g2.getNodeName() == null) {
            returner = -1;
        } else if (g1.getNodeName() == null && g2.getNodeName() != null) {
            returner = 1;
        } else if (g1.getNodeName() != null && g2.getNodeName() != null) { 
        	returner = collator.compare(g1.getNodeName(), g2.getNodeName());
        }
        return returner;
    }

    private Long getLongFromBeginnigOfString(String original) {
        if (original == null) {
			return null;
		}

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < original.length(); i++) {
        	if (Character.isDigit(original.charAt(i))) {
            	result.append(original.charAt(i));
            }
            else {
            	break;
            }
        }
        if (result.length() > 0) {
			return Long.valueOf(result.toString());
		}

        return null;
    }
}
