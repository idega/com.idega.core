/**
 * Created on 17.1.2003
 *
 * This class does something very clever.
 */
package com.idega.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.core.data.GenericGroup;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GenericGroupComparator implements Comparator<Object> {

	protected IWContext _iwc;
	protected Locale locale = null;
	protected Collator collator = null;

	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	public GenericGroupComparator(IWContext iwc) {
		this._iwc = iwc;
		this.locale = iwc.getCurrentLocale();
		collator = Collator.getInstance(locale);
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		String c1 = null, c2 = null;
		if (o1 instanceof Group) {
			c1 = ((Group) o1).getName();
		} else if (o1 instanceof GenericGroup) {
			c1 = ((GenericGroup) o1).getName();
		}

		if (o2 instanceof Group) {
			c2 = ((Group) o2).getName();
		} else if (o2 instanceof GenericGroup) {
			c2 = ((GenericGroup) o2).getName();
		}

		return collator.compare(c1, c2);
	}

}