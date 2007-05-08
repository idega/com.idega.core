package com.idega.presentation.ui;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObjectContainer;

public class NewAbstractChooser extends PresentationObjectContainer {
	
	private static final String DISPLAYSTRING_PARAMETER = "iw_ch_d";
	
	private Image chooserButtonImage = null;
	
	private String chooserParameter = null;
	private String displayInputName = null;
	private String value = null;
	private String display = null;
	private String style = null;
	
	private boolean disabled = false;
	
	private IWBundle bundle = null;
	private IWResourceBundle iwrb = null;
	
	public void main(IWContext iwc) {
		try {
			super.main(iwc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		bundle = getBundle(iwc);
		iwrb = getResourceBundle(iwc);
		
		this.add(getSaveButton());
	}
	
	private GenericButton getSaveButton() {
		GenericButton save = new GenericButton("save", iwrb.getLocalizedString("save", "Save"));
		
		save.setOnClick("saveSelectedValues();");
		
		return save;
	}

	public void setChooserButtonImage(Image chooserButtonImage) {
		this.chooserButtonImage = chooserButtonImage;
	}
	
	public void setChooserParameter(String parameterName) {
		chooserParameter = parameterName;
		if (displayInputName.equals(DISPLAYSTRING_PARAMETER)) {
			displayInputName = new StringBuffer(parameterName).append("_displaystring").toString();
		}
	}
	
	protected void setChooserValue(String displayString, int valueInt) {
		setChooserValue(displayString, Integer.toString(valueInt));
	}
	
	public void setChooserValue(String displayString, String valueString) {
		value = valueString;
		display = displayString;
	}
	
	public void setInputStyle(String style) {
		this.style = style;
	}
	
	/**
	 * @param disabled - the new value for disabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
