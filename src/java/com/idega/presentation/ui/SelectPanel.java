package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SelectPanel extends GenericSelect {
	
	public SelectPanel() {
		this("untitled");
	}
	
	public SelectPanel(String name) {
		super(name);
		setMultiple(true);	
	}
	
	public void setMultiple(boolean multiple) {
		super.setMultiple(multiple);
	}
	
	public boolean getMultiple() {
		return super.getMultiple();	
	}

	public void setSize(int size) {
		setMarkupAttribute("size",String.valueOf(size));
	}
	
	public int getSize() {
		if (isMarkupAttributeSet("size"))
			return Integer.parseInt(getMarkupAttribute("size"));
		return 1;	
	}
	
	public void print(IWContext iwc) throws Exception {
		if (!getMultiple() && getSize() <= 1)
			setSize(3);
		super.print(iwc);	
	}

	public void setSelectedElements(int[] values)	{
		if(values !=null){
			for (int i = 0; i < values.length; i++)
			{
				int value = values[i];
				setSelectedOption(String.valueOf(value));
			}
		}
	}
	
	public void setSelectedElements(String[] values) {
		if(values !=null){
			for (int i = 0; i < values.length; i++)
			{
				String value = values[i];
				setSelectedOption(value);
			}
		}
	}
}
