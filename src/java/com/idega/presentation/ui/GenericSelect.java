package com.idega.presentation.ui;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	private boolean _allSelected = false;

	public GenericSelect() {
		this("undefined");	
	}
	
	public GenericSelect(String name) {
		setName(name);
		theElements = new Vector();
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

	public void addOption(SelectOption option) {
		theElements.add(option);	
	}
	
	public void addFirstOption(SelectOption option) {
		theElements.add(0, option);
	}
	
	public void addDisabledOption(SelectOption option) {
		option.setDisabled(true);
		theElements.add(option);	
	}
	
	public void addSeparator() {
		SelectOption option = new SelectOption("----------------------------", "separator");
		option.setDisabled(true);
		addOption(option);
	}

	protected void deselectOptions() {
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			SelectOption element = (SelectOption) iter.next();
			if (element.getSelected())
				element.setSelected(false);
		}	
	}
	
	public String getSelectedValue() {
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			SelectOption element = (SelectOption) iter.next();
			if (element.getSelected())
				return element.getValue();
		}
		return "";
	}
	
	public void setSelectedOption(String value) {
		if (!getMultiple())
			deselectOptions();
		SelectOption option = getOption(value);
		option.setSelected(true);
	}
	
	protected SelectOption getOption(String value) {
		SelectOption theReturn = new SelectOption();
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			SelectOption element = (SelectOption) iter.next();
			if (element.getValue().equals(value))
				return element;
		}
		return theReturn;
	}
	
	public void setOptionName(String value, String name) {
		SelectOption option = getOption(value);
		option.setName(name);	
	}

	protected void setMultiple(boolean multiple) {
		if (multiple)
			setAttribute("multiple");
		else
			removeAttribute("multiple");
	}
	
	protected boolean getMultiple() {
		if (isAttributeSet("multiple"))
			return true;
		return false;	
	}

	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
			
			Iterator iter = theElements.iterator();
			while (iter.hasNext()) {
				SelectOption option = (SelectOption) iter.next();
				if (_allSelected)
					option.setSelected(true);
				option._print(iwc);
			}
			
			println("</select>");
		}
		else if (getLanguage().equals("WML")) {
			println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
			
			Iterator iter = theElements.iterator();
			while (iter.hasNext()) {
				SelectOption option = (SelectOption) iter.next();
				if (_allSelected)
					option.setSelected(true);
				option._print(iwc);
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
					if (item instanceof SelectOption) {
						SelectOption option = (SelectOption) item;
						obj.theElements.add(index, ((SelectOption) item).clone());
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
	
	protected List getOptions() {
		return theElements;	
	}
	
	protected void setAllSelected(boolean allSelected) {
		_allSelected = allSelected;
	}
}
