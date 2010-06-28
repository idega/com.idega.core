/*
 * $Id: ICTreeNodeComparator.java,v 1.2 2006/04/09 12:13:16 laddi Exp $
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
import com.idega.util.CoreConstants;


/**
 * A comparator to compare ICTreeNodes by their name.
 * 
 * Last modified: $Date: 2006/04/09 12:13:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class ICTreeNodeComparator implements Comparator<ICTreeNode> {

	private Collator collator;
	private Locale locale;
	
	public ICTreeNodeComparator() {
		this.collator = Collator.getInstance();
	}
	
	public ICTreeNodeComparator(Locale locale) {
		this.locale = locale;
		this.collator = Collator.getInstance(locale);
	}
	
	public int compare(ICTreeNode o1, ICTreeNode o2) {
		String name1 = getName(o1);
		String name2 = getName(o2);
		return collator.compare(name1, name2);
	}
	
	private String getName(ICTreeNode node) {
		if (node == null) {
			return CoreConstants.EMPTY;
		}
		
		String name = locale == null ? node.getNodeName() : node.getNodeName(locale);
		if (locale != null && name == null) {
			//	Failed to retrieve localized name
			name = node.getNodeName();
		}
		
		return name == null ? CoreConstants.EMPTY : name;
	}
}