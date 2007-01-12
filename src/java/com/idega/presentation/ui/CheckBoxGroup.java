/*
 * Created on 16.9.2003 by  tryggvil in project com.project
 */
package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * CheckBoxGroup This class presents a group of checkboxes
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CheckBoxGroup extends InterfaceObject
{
	private String parameterName;
	private Table boxTable;
	private Map keyValueMapping;
	private List valueOrderingList;
	
	/**
	 * 
	 */
	public CheckBoxGroup()
	{
		this("unspecified");
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public CheckBoxGroup(String parameterName)
	{
		this.setParameterName(parameterName);
	}
	/**
	 * @return
	 */
	public String getParameterName()
	{
		return this.parameterName;
	}

	/**
	 * Sets the parameter name defined for this group
	 * @param string
	 */
	public void setParameterName(String string)
	{
		this.parameterName = string;
	}


	protected Table getBoxTable(){
		if(this.boxTable==null){
			this.boxTable=new Table();
		}
		return this.boxTable;
	}

	protected Map getMapping(){
		if(this.keyValueMapping==null){
			this.keyValueMapping=new HashMap();
		}
		return this.keyValueMapping;
	}
	
	protected List getOrdering(){
		if(this.valueOrderingList==null){
			this.valueOrderingList=new ArrayList();
		}
		return this.valueOrderingList;
	}
	

	/**
	 * Adds an option (a checkbox) to this group with the parametervalue checkValue and descriptiontext (after the checkbox)
	 * @param checkValue the value of the parameter if the checkbox is checked
	 * @param descriptionText the description text that is set after the checkbox
	 */
	public void addOption(String checkValue,String descriptionText){
		addOption(checkValue,descriptionText,false);
	}

	/**
	 * Adds an option (a checkbox) to this group with the parametervalue checkValue and descriptiontext (after the checkbox)
	 * @param checkValue the value of the parameter if the checkbox is checked
	 * @param descriptionText the description text that is set after the checkbox
	 * @param isChecked if true the checkbox is set checked else it is not checked
	 */
	public void addOption(String checkValue,String descriptionText,boolean isChecked){
		getMapping().put(checkValue,new CheckHolder(checkValue,descriptionText,isChecked));
		getOrdering().add(checkValue);
	}


	public void main(IWContext iwc){
		Table t = getBoxTable();
		add(t);
		Iterator keyIter = getOrdering().iterator();
		int ypos = 1;
		while (keyIter.hasNext())
		{
			String key = (String) keyIter.next();
			CheckHolder value = (CheckHolder)getMapping().get(key);
			String displayString = value.checkDescription;
			Text displayText = getDisplayText(displayString);
			t.add(getCheckBox(value),1,ypos);
			t.add(displayText,2,ypos);
			ypos++;
		}
	}

	
	public void print(IWContext iwc) throws Exception {
		if(IWConstants.MARKUP_LANGUAGE_WML.equals(iwc.getMarkupLanguage())) {
			print("<select multiple=\"true\">");
			super.print(iwc);
			print("</select>");
		} else {
			super.print(iwc);
		}
	}
	

	/**
	 * @param displayString
	 */
	protected Text getDisplayText(String displayString)
	{
		return new Text(displayString);
	}
	
	/**
	 * @param displayString
	 */
	protected CheckBox getCheckBox(CheckHolder check)
	{
		CheckBox box = new CheckBox(this.getParameterName());
		if(check.isChecked){
			box.setChecked(check.isChecked);
		}
		box.setValue(check.checkValue);
		return box;
	}

	protected class CheckHolder{
		private boolean isChecked=false;
		private String checkValue;
		private String checkDescription;
		private CheckHolder(String checkValue,String descriptionText,boolean isChecked){
			this.checkValue=checkValue;
			this.checkDescription=descriptionText;
			this.isChecked=isChecked;
		}
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
