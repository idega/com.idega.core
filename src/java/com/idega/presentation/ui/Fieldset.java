/*
 * Created on 3.1.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * Title:		Fieldset 
 * Description: The fieldset element draws a box around its containing elements.
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class Fieldset extends InterfaceObjectContainer {
	
	private String legendAlignment = "left";

	/**
	 * 
	 */
	public Fieldset() {
		super();
	}
	
	/**
	 *  Sets the the legend text , aligned by default to the left
	 * @param legend
	 */
	public void setLegend(String legend){
		this.setName(legend);
	}
	
	/**
	 *  Sets the the legend text showing aligned with the provided alignment value ("left","center","right")
	 * @param legend
	 * @param alignment
	 */
	public void setLegend(String legend,String alignment){
		setName(legend);
		setLegendAlignment(alignment);
	}
	
	/**
	 * Sets the alignment of the legend ("left","center","right")
	 * @param align
	 */
	public void setLegendAlignment(String align){
		this.legendAlignment =align;
	}
	
	/**
	 * Gets the legend to be shown
	 * @return
	 */
	public String getLegend(){
		return getName();
	}
	
	/**
	 * Gets the aligment value for the legend
	 * @return
	 */
	public String getLegendAlignment(){
		return this.legendAlignment;
	}
	
	public void print(IWContext iwc) throws Exception {
		
		//<fieldset dir="ltl"><legend> "name" </legend> "content" </fieldset>
		
		if (getLanguage().equals("HTML")) {
			println("<fieldset " +getMarkupAttributesString()+ " >");
			if(getName()!=null){
				String align = "align=\""+this.legendAlignment+"\"";
				println("<legend "+align+" >"+getName()+"</legend>");
			}
			//		Catch all exceptions that are thrown in print functions of objects stored inside
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<h1>Villa var&eth;!</h1>");
				println("IW Error");
				println("<pre>");
				println(ex.getMessage());
				ex.printStackTrace(System.err);
				println("</pre>");
			}
			println("</fieldset>");
		}
		else if (getLanguage().equals("WML")) {
			//println("<fieldset " +getMarkupAttributesString()+ " ><legend>"+getName()+"</legend>");
			//		Catch all exceptions that are thrown in print functions of objects stored inside
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<h1>Villa var&eth;!</h1>");
				println("IW Error");
				println("<pre>");
				println(ex.getMessage());
				ex.printStackTrace(System.err);
				println("</pre>");
			}
			//println("</fieldset>");
		}
	}

}
