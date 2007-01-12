/*
 * Created on Dec 15, 2004
  */
package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;

import com.idega.presentation.IWContext;
import com.idega.user.data.GroupType;

/**
 * @author Sigtryggur
 * 
 */
public class GroupTypeComparator implements Comparator {

    private IWContext _iwc;
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";	

    public GroupTypeComparator(IWContext iwc) {
	  	this._iwc = iwc;
	}

    public int compare(Object o1, Object o2) {
        Collator collator = Collator.getInstance(this._iwc.getCurrentLocale());
        
        GroupType groupType1 = (GroupType)o1;
        GroupType groupType2 = (GroupType)o2;
        
        String localizedGroupType1 = this._iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(this._iwc.getCurrentLocale()).getLocalizedString(groupType1.getType());
        String localizedGroupType2 = this._iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(this._iwc.getCurrentLocale()).getLocalizedString(groupType2.getType());
        return  collator.compare(localizedGroupType1, localizedGroupType2);
    }
}