/*
 * Created on Jan 24, 2005
 */
package com.idega.presentation.ui;

import java.util.Collection;

import com.idega.business.InputHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author Sigtryggur
 */
public class CheckBoxInputHandler extends CheckBox implements InputHandler {
    
    protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
    private static final String CHECKED = "checked";
    
	public CheckBoxInputHandler() {
		super();
	}
	
	public CheckBoxInputHandler(String name) {
		super(name);
	}
    
    public Object convertSingleResultingObjectToType(Object value, String className) {
        return value;
    }

    public String getDisplayForResultingObject(Object value, IWContext iwc) {
        if (value != null && value.equals(CHECKED)) {
            return getResourceBundle(iwc).getLocalizedString("CheckBoxInputHandler.yes","Yes");
        }
        return getResourceBundle(iwc).getLocalizedString("CheckBoxInputHandler.no","No");
    }

    public PresentationObject getHandlerObject(String name, Collection values, IWContext iwc) {
        return null;
    }

    public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
        this.setName(name);
		if (value != null && value.equals(CHECKED)) {
			this.setChecked(true);
		}
		return this;
    }

    public Object getResultingObject(String[] value, IWContext iwc) throws Exception {
        String ret = null;
        if (value != null && value.length != 0)
        	ret = CHECKED;
        return ret;
    }
    
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}