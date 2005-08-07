/*
 * $Id: TableHeaderCell.java,v 1.1 2005/08/07 16:07:32 laddi Exp $
 * Created on Aug 6, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;


/**
 * Last modified: $Date: 2005/08/07 16:07:32 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class TableHeaderCell extends TableCell2 {

  protected String getTag() {
		return "th";
	}
}