/*
 * Created on Dec 15, 2004
  */
package com.idega.user.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.data.GroupType;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;

/**
 * @author Sigtryggur
 *
 */
public class GroupTypeComparator implements Comparator {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	private Collator collator = null;
	private IWResourceBundle iwrb = null;

    public GroupTypeComparator(IWContext iwc) {
	  	Locale locale = iwc == null ? Locale.ENGLISH : iwc.getCurrentLocale();
	  	locale = locale == null ? Locale.ENGLISH : locale;
	  	collator = Collator.getInstance(locale);
	  	iwrb = IWMainApplication.getDefaultIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(locale);
	}

	public int compare(Object o1, Object o2) {
		GroupType groupType1 = (GroupType) o1;
		GroupType groupType2 = (GroupType) o2;
		
        String localizedGroupType1 = iwrb.getLocalizedString(groupType1.getType());
        String localizedGroupType2 = iwrb.getLocalizedString(groupType2.getType());

        localizedGroupType1 = StringUtil.isEmpty(localizedGroupType1) ? groupType1.getType() : localizedGroupType1;
        localizedGroupType1 = StringUtil.isEmpty(localizedGroupType1) ? CoreConstants.EMPTY : localizedGroupType1;

        localizedGroupType2 = StringUtil.isEmpty(localizedGroupType2) ? groupType2.getType() : localizedGroupType2;
        localizedGroupType2 = StringUtil.isEmpty(localizedGroupType2) ? CoreConstants.EMPTY : localizedGroupType2;

        return collator.compare(localizedGroupType1, localizedGroupType2);
    }
	
}