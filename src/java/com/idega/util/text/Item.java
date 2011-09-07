package com.idega.util.text;

import java.io.Serializable;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2008/11/26 08:48:11 $ by $Author: arunas $
 */
public class Item implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String itemLabel;
	private String itemValue;
	
	public Item() {
	}
	
	/**
	 * Single item with label and value of list selection
	 * 
	 * @param label
	 * @param value
	 *            <select> <option value = value>label</option> </select>
	 */
	public Item(String value, String label) {
		setItemValue(value);
		setItemLabel(label);
	}
	
	public String getItemLabel() {
		return itemLabel;
	}
	
	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}
	
	public String getItemValue() {
		return itemValue;
	}
	
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
	
	@Override
	public String toString() {
		return getItemValue() + " " + getItemLabel();
	}
}