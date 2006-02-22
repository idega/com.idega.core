// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.presentation.ui;

import java.io.IOException;

import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;
import com.idega.util.LocaleUtil;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class GenericList extends InterfaceObject {

	private Table theTable;
	private IDOLegacyEntity[] entity;
	private String pageLink;
	private String headerColor;
	private String headerFontColor;
	private String listColor1;
	private String listColor2;

	public GenericList() {
		super();
	}

	public GenericList(IDOLegacyEntity entity) {
		super();

		IDOLegacyEntity[] entityArr = (IDOLegacyEntity[]) java.lang.reflect.Array.newInstance(entity.getClass(), 1);
		entityArr[0] = entity;
		setEntity(entityArr);
		setDefaults();
	}

	public GenericList(IDOLegacyEntity[] entity) {
		super();
		setEntity(entity);
		setDefaults();
	}

	private void setDefaults() {
		setHeaderFontColor("#FFFFFF");
		setHeaderColor("#707070");
		setColor2("#E0E0E0");
		setColor1("#C0C0C0");
	}

	public IDOLegacyEntity[] getEntity() {
		return entity;
	}

	public void setEntity(IDOLegacyEntity[] entity) {
		this.entity = entity;
	}

	public void setHeaderFontColor(String color) {
		headerFontColor = color;
	}

	public void setHeaderColor(String color) {
		headerColor = color;
	}

	public void setColor1(String color1) {
		listColor1 = color1;
	}

	public void setColor2(String color2) {
		listColor2 = color2;
	}

	public void setLink(String pageToLinkTo) {
		pageLink = pageToLinkTo;
	}

	public void setList(IDOLegacyEntity[] entity) {
		if (entity != null) {
			if (entity.length > 0) {
				initializeTable(entity[0].getVisibleColumnNames().length, entity.length + 1);
				for (int y = 0; y <= entity.length; y++) {
					if (y == 0) {
						for (int x = 0; x < entity[0].getVisibleColumnNames().length; x++) {
							Text text = new Text(getEntity()[0].getLongName(entity[0].getVisibleColumnNames()[x]));
							text.setFontColor(headerFontColor);
							theTable.add(text, x + 1, y + 1);
						}
					}
					else {
						for (int x = 0; x < entity[0].getVisibleColumnNames().length; x++) {
							String stringToDisplay;
							String columnName = entity[0].getVisibleColumnNames()[x];
							if (entity[0].getStorageClassName(columnName).equals("java.sql.Timestamp") || entity[0].getStorageClassName(columnName).equals("java.sql.Date")) {
								Object value = entity[y - 1].getColumnValue(columnName);
								IWTimestamp stamp = null;
								if (value instanceof java.sql.Timestamp) {
									stamp = new IWTimestamp((java.sql.Timestamp) value);
								}
								else if (value instanceof java.sql.Date) {
									stamp = new IWTimestamp((java.sql.Date) value);
								}
								if (stamp == null) {
									stringToDisplay = "";
								}
								else {
									stringToDisplay = stamp.getLocaleDate(LocaleUtil.getIcelandicLocale());
								}
							}
							else if (entity[0].getRelationShipClass(columnName) == null) {
								if (entity[y - 1].isNull(columnName)) {
									stringToDisplay = "";
								}
								else {
									stringToDisplay = entity[y - 1].getStringColumnValue(columnName);
								}
							}
							else {
								if (entity[y - 1].isNull(columnName)) {
									stringToDisplay = "";
								}
								else {
									stringToDisplay = ((IDOLegacyEntity) entity[y - 1].getColumnValue(columnName)).getName();
								}
							}
							if (x == 0) {
								if (pageLink != null) {
									Link link = new Link(stringToDisplay, pageLink);
									link.addParameter(entity[0].getIDColumnName(), Integer.toString(entity[y - 1].getID()));
									theTable.add(link, x + 1, y + 1);
								}
								else {
									theTable.add(new Text(stringToDisplay), x + 1, y + 1);
								}
							}
							else {
								theTable.add(new Text(stringToDisplay), x + 1, y + 1);
							}
						}
					}
				}
			}
		}
	}

	private void initializeTable(int columns, int rows) {
		theTable = new Table(columns, rows);
		add(theTable);
		theTable.setBorder(0);
		theTable.setCellpadding(3);
	}

	public void beforePrint(IWContext iwc) throws IOException {
		setList(entity);
	}

	public void print(IWContext iwc) throws Exception {
		beforePrint(iwc);
		if (theTable != null) {
			theTable.setHorizontalZebraColored(listColor1, listColor2);
			theTable.setRowColor(1, headerColor);
		}
		super.print(iwc);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}