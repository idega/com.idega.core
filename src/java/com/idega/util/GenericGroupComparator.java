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

	protected IWContext _iwc;
	protected final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	
	public GenericGroupComparator(IWContext iwc) {
		_iwc = iwc;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Collator collator = Collator.getInstance(_iwc.getCurrentLocale());
		
		GenericGroup g1 = (GenericGroup) o1;
		GenericGroup g2 = (GenericGroup) o2;
		
		return collator.compare(g1.getName(), g2.getName());
	}

}
