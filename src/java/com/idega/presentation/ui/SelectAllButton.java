/*
 * Created on Nov 11, 2004
 *
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.Script;


/**
 * @author birna
 *
 */
public class SelectAllButton extends GenericButton{
	/**
	 * Constructs a new <code>SelectAllButton</code> with the default display label
	 *
	 */
	public SelectAllButton() {
		this("Clear");
	}
	/**
	 * Construcs a new <code>SelectAllButton</code> with the display string specified
	 * @param displayString The string to display on the button.
	 */
	public SelectAllButton(String displayString) {
		setValue(displayString);
		setInputType(INPUT_TYPE_BUTTON);
	}
	/**
	 * Construcs a new <code>SelectAllButton</code> with the image specified
	 * @param buttonImage The image to use as the deselect button
	 */
	public SelectAllButton(Image buttonImage) {
		super();
		setButtonImage(buttonImage);
	}

	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		Page parentPage = getParentPage();
		Form parentForm = getParentForm();
		Script associatedScript = parentPage.getAssociatedScript();
		associatedScript.addFunction("selectall", "function selectall() {\n\t" +
				"var max = document."+parentForm.getName()+".length;\n\t " + 
				"for(var i=0; i<max; i++){\n\t\t " + 
				"document."+parentForm.getName()+".elements[i].checked=1;\n\t" +
				"}\n" + 
				"}");
		setOnClick("selectall()");
		
	}

}
