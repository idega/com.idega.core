package com.idega.presentation.ui;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.idega.presentation.IWContext;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GenericSelect extends InterfaceObject {

	private Vector theElements;

	public GenericSelect() {
		this("undefined");	
	}
	
	public GenericSelect(String name) {
		setName(name);
		theElements = new Vector();
	}
	
	public void setMultiple(boolean multiple) {
		if (multiple)
			setAttribute("multiple");
		else
			removeAttribute("multiple");
	}
	
	public boolean getMultiple() {
		if (isAttributeSet("multiple"))
			return true;
		return false;	
	}

	public void removeElements() {
		theElements.clear();
	}

	/**
	 * Sets the select to submit automatically.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit() {
		setAttribute("onChange","this.form.submit()");
	}

	public void addOption(Option option) {
		theElements.add(option);	
	}
	
	public void addOption(String value, String name) {
		Option option = new Option(name, value);
		addOption(option);
	}
	
	public void addOption(int value, String name) {
		Option option = new Option(name, String.valueOf(value));
		addOption(option);
	}
	
	public void addOption(String value) {
		Option option = new Option(value, value);
		addOption(option);
	}
	
	public void addFirstOption(Option option) {
		theElements.add(0, option);
	}
	
	public void addFirstOption(String value, String name) {
		Option option = new Option(name, value);
		addFirstOption(option);
	}
	
	public void addDisabledOption(Option option) {
		option.setDisabled(true);
		theElements.add(option);	
	}
	
	public void addDisabledOption(String value, String name) {
		Option option = new Option(name, value);
		option.setDisabled(true);
		addDisabledOption(option);
	}
	
	public void addSeparator() {
		Option option = new Option("----------------------------", "separator");
		option.setDisabled(true);
		addOption(option);
	}

	protected void deselectOptions() {
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			Option element = (Option) iter.next();
			if (element.getSelected())
				element.setSelected(false);
		}	
	}
	
	public String getSelectedValue() {
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			Option element = (Option) iter.next();
			if (element.getSelected())
				return element.getValue();
		}
		return "";
	}
	
	public void setSelectedOption(String value) {
		if(!getMultiple())
			deselectOptions();
		
		Option option = getOption(value);
		option.setSelected(true);
	}
	
	protected Option getOption(String value) {
		Option theReturn = new Option();
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			Option element = (Option) iter.next();
			if (element.getValue().equals(value))
				return element;
		}
		return theReturn;
	}
	
	public void setOptionName(String value, String name) {
		Option option = getOption(value);
		option.setName(name);	
	}

	public void setSize(int size) {
		setAttribute("size",String.valueOf(size));
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
			
			Iterator iter = theElements.iterator();
			while (iter.hasNext()) {
				Option tempobj = (Option) iter.next();
				tempobj._print(iwc);
			}
			
			println("</select>");
		}
		else if (getLanguage().equals("WML")) {
			println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
			
			Iterator iter = theElements.iterator();
			while (iter.hasNext()) {
				Option tempobj = (Option) iter.next();
				tempobj._print(iwc);
			}
			
			println("</select>");
		}
	}
	
	public Object clone() {
		GenericSelect obj = null;
		try {
			obj = (GenericSelect) super.clone();
			if (this.theElements != null) {
				obj.theElements = (Vector) this.theElements.clone();
				ListIterator iter = obj.theElements.listIterator();
				while (iter.hasNext()) {
					int index = iter.nextIndex();
					Object item = iter.next();
					if (item instanceof Option) {
						Option option = (Option) item;
						obj.theElements.add(index, ((Option) item).clone());
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.isParameterSet(getName())) {
			String[] values = iwc.getParameterValues(getName());
			if (values != null) {
				for (int a = 0; a < values.length; a++) {
					setSelectedOption(values[a]);
				}
			}
		}
	}
}
