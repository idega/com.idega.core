/*
 * Created on Jul 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.presentation.ui;

import java.io.IOException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Table;

/**
 * The <code>SpinInput</code> is an integer input 
 * with adjustable maximum and minimum limits, 
 * and size of increment. In some frameworks this is called 
 * SpinEdit.  
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="mailto:aron@idega.is">Aron Birkir</a> 
 * @version 1.0
 */

public class SpinInput extends IntegerInput{

	private Integer maximum = null;
	private Integer minimum = null;
	private Integer increment = null;
	private Table table = null;
	private boolean tablePrinted = false;
	
	public SpinInput()
		{
			this("spin_untitled");
		}
	
	public SpinInput(String name)
	{
			super();
			setName(name);
			this.setValue(5);
			this.setSize(4);
			
			
	}
	/**
	 * Gets the current increment size
	 * @return increment size
	 */
	public Integer getIncrement() {
		return increment;
	}

	/**
	 * Gets the maximum limit the input can be set to
	 * @return maximum limit
	 */
	public Integer getMaximum() {
		return maximum;
	}

	/**
	 * Gets the minimum limit the input can be set to
	 * @return minimum limit
	 */
	public Integer getMinimum() {
		return minimum;
	}

	/**
	 * Sets the increment size, default value is 1
	 * @param increment
	 */
	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	/**
	 * Sets the maximum limit this input can be set to, default set to 100
	 * @param maximum
	 */
	public void setMaximum(int maximum) {
		this.maximum = new Integer(maximum);
	}

	/**
	 * Sets the minimum limit this input can be set to, default set to 0
	 * @param minimum
	 */
	public void setMinimum(int minimum) {
		this.minimum = new Integer(minimum);
	}
	
	/* (non-Javadoc)
	* @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	*/
	public void main(IWContext iwc){
	
		table = new Table(3,1);
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setParentObject(this.getParentObject());
		table.add(this, 1, 1);
		
			int inc = increment!=null?increment.intValue():1;
			int min = minimum!=null?minimum.intValue():0;
			int max = maximum!=null?maximum.intValue():100;
			//this.getParentPage().addsty
			//String buttonStyle = "color: black; background:white; font-size:7";
			GenericButton up = new GenericButton(this.getName() + "_up", " /\\ ");
			up.setOnClick("var spin = findObj('" + this.getName() + "'); if(spin){ var spinValue = parseInt(spin.value); if(spinValue +"+inc+" >= "+max+"){ spinValue="+max+" } else{spinValue+="+inc+" } spin.value =spinValue; }else window.status='no spin found'; return");
			GenericButton down = new GenericButton(this.getName() + "_down", " \\/ ");
			//up.setStyleAttribute(buttonStyle+";vertical-align:super");
			//down.setStyleAttribute(buttonStyle+";vertical-align:sub");
			down.setOnClick("var spin = findObj('" + this.getName() + "'); if(spin){ var spinValue = parseInt(spin.value); if(spinValue -"+inc+" <= "+min+"){ spinValue="+min+" } else{spinValue-="+inc+" } spin.value = spinValue; }else window.status='no spin found'; return");
			table.add(up, 2, 1);
			table.add(down, 2, 1);
			setOnChange("if (typeof this.value == 'string'){ this.value="+getValueAsString()+"}else{ var SpinValue = parseInt(this.value); if(SpinValue >"+max+") {this.value="+max+"; }else if(SpinValue < "+min+"){ this.value="+min+";  }  }");
			setStyleAttribute("font-size:10; background:white");
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#print(com.idega.presentation.IWContext)
	 */
	public void print(IWContext iwc) throws IOException {
		if(!tablePrinted){
			tablePrinted = true;
			try {
				table._print(iwc);
			}
			catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
		else{
			super.print(iwc);
		}
	}

}
