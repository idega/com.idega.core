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
 * Can be used with checkboxes and radiobuttons to deselect all selected items
 * @author birna
 *
 */
public class DeselectAllButton extends GenericButton {
	/**
	 * Constructs a new <code>DeselectAllButton</code> with the default display label
	 *
	 */
	public DeselectAllButton() {
		this("Clear");
	}
	/**
	 * Construcs a new <code>DeselectAllButton</code> with the display string specified
	 * @param displayString The string to display on the button.
	 */
	public DeselectAllButton(String displayString) {
		setValue(displayString);
		setInputType(INPUT_TYPE_BUTTON);
	}
	/**
	 * Construcs a new <code>DeselectAllButton</code> with the image specified
	 * @param buttonImage The image to use as the deselect button
	 */
	public DeselectAllButton(Image buttonImage) {
		super();
		setButtonImage(buttonImage);
	}

	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		Page parentPage = getParentPage();
		Form parentForm = getParentForm();
		Script associatedScript = parentPage.getAssociatedScript();
		associatedScript.addFunction("clearform", "function clearform() {\n\t" +
				"var max = document."+parentForm.getName()+".length;\n\t " + 
				"for(var i=0; i<max; i++){\n\t\t " + 
				"document."+parentForm.getName()+".elements[i].checked=0;\n\t" +
				"}\n" + 
				"}");
		setOnClick("clearform()");
		
	}
}
