/*
 * $Id: ICTreeNodeComparator.java,v 1.1 2005/01/08 14:28:33 laddi Exp $
 * Created on 8.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.core.data.ICTreeNode;


/**
 * A comparator to compare ICTreeNodes by their name.
 * 
 * Last modified: $Date: 2005/01/08 14:28:33 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class ICTreeNodeComparator implements Comparator {

  private Collator collator;
	private Locale iLocale;
	
	public ICTreeNodeComparator() {
	}
	
	public ICTreeNodeComparator(Locale locale) {
		iLocale = locale;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
    ICTreeNode p1 = (ICTreeNode) o1;
    ICTreeNode p2 = (ICTreeNode) o2;
		
		if (iLocale != null) {
			collator = Collator.getInstance(iLocale);
	    return collator.compare(p1.getNodeName(iLocale), p2.getNodeName(iLocale));
		}
		else {
			collator = Collator.getInstance();
			return collator.compare(p1.getNodeName(), p2.getNodeName());
		}
	}
}