/*
 * Created on 16.9.2003 by  tryggvil in project com.project
 */
package com.idega.presentation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * CheckBoxGroup This class presents a group of checkboxes
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CheckBoxGroup extends InterfaceObjectContainer
{
	private String parameterName;
	private Table boxTable;
	private Map keyValueMapping;
	
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
		return parameterName;
	}

	/**
	 * Sets the parameter name defined for this group
	 * @param string
	 */
	public void setParameterName(String string)
	{
		parameterName = string;
	}


	protected Table getBoxTable(){
		if(boxTable==null){
			boxTable=new Table();
		}
		return boxTable;
	}

	protected Map getMapping(){
		if(keyValueMapping==null){
			keyValueMapping=new HashMap();
		}
		return keyValueMapping;
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
	}


	public void main(IWContext iwc){
		Table t = getBoxTable();
		add(t);
		Iterator keyIter = getMapping().keySet().iterator();
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

}
