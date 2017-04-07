/**
 * Created on 17.1.2003
 *
 * This class does something very clever.
 */
package com.idega.util;

import java.text.Collator;
import java.util.Comparator;

import com.idega.core.data.GenericGroup;
import com.idega.presentation.IWContext;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GenericGroupComparator implements Comparator {

	protected final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	protected IWContext _iwc = null;
	private Collator collator = null;

	public GenericGroupComparator(IWContext iwc) {
		this._iwc = iwc;
		collator = Collator.getInstance(iwc.getCurrentLocale());
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof GenericGroup) || !(o2 instanceof GenericGroup)) {
			return 0;
		}

		GenericGroup g1 = (GenericGroup) o1;
		GenericGroup g2 = (GenericGroup) o2;

		if (g1 == null || g2 == null) {
			return 0;
		}

		String name1 = g1.getName();
		String name2 = g2.getName();
		if (name1 == null || name2 == null) {
			return 0;
		}

		return collator.compare(name1, name2);
	}

}
