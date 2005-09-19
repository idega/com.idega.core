/*
 * $Id: TableTest.java,v 1.2 2005/09/19 12:48:32 laddi Exp $
 * Created on Aug 6, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import com.idega.presentation.text.Text;


/**
 * Last modified: $Date: 2005/09/19 12:48:32 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class TableTest extends Block {
	
	public void main(IWContext iwc) throws Exception {
		//Detailed construction
		Table2 table = new Table2();
		table.setBorder(1);
		table.setRules(Table2.RULES_NONE);
		table.setID("mytable");
		table.setStyleClass("ruler");
		table.setCellpadding(5);
		table.setCellspacing(0);
		table.setWidth("300");
		
		TableColumnGroup group = table.createColumnGroup();
		TableColumn column = group.createColumn();
		column.setWidth("100");
		column = group.createColumn();
		column.setWidth("200");
		
		TableHeaderRowGroup rowGroup = table.createHeaderRowGroup();
		rowGroup.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		TableRow row = rowGroup.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.add(new Text("Name"));
		cell = row.createHeaderCell();
		cell.add(new Text("Personal ID"));
		
		TableBodyRowGroup bodyRowGroup = table.createBodyRowGroup();
		row = bodyRowGroup.createRow();
		cell = row.createCell();
		cell.add(new Text("Laddi"));
		cell = row.createCell();
		cell.add(new Text("020277-4919"));
		row = bodyRowGroup.createRow();
		cell = row.createCell();
		cell.add(new Text("Jonni"));
		cell = row.createCell();
		cell.add(new Text("090577-6119"));
		
		bodyRowGroup = table.createBodyRowGroup();
		row = bodyRowGroup.createRow();
		cell = row.createCell();
		cell.add(new Text("Palli"));
		cell = row.createCell();
		cell.add(new Text("061070-3899"));
		row = bodyRowGroup.createRow();
		cell = row.createCell();
		cell.add(new Text("Gimmi"));
		cell = row.createCell();
		cell.add(new Text("231177-2999"));
		
		TableFooterRowGroup footerRowGroup = table.createFooterRowGroup();
		row = footerRowGroup.createRow();
		cell = row.createCell();
		cell.setColumnSpan(2);
		cell.add(new Text("Last updated: Today"));
		
		table.createCaption("A little bit of info");
		
		add(table);
		
		//Very simple 1x1 table construction...
		Table2 table2 = new Table2();
		TableCell2 cell2 = table2.createRow().createCell();
		cell2.add(new Text("This is a test of a 1x1 table..."));
		add(table2);
	}
}