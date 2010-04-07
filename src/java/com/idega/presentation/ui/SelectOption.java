package com.idega.presentation.ui;

import java.io.IOException;
import java.text.Collator;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;

/**
 * @author laddi
 */
public class SelectOption extends InterfaceObject implements Comparable<SelectOption> {
	
	private Class windowClass;
	private Map parameterMap;
	private String target;
	private int fileID = -1;
	
	private static final String SELECTED_PROPERTY = "selected";

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[8];
		values[0] = super.saveState(ctx);
		values[1] = this.windowClass;
		values[2] = this.parameterMap;
		values[3] = this.target;
		values[4] = new Integer(this.fileID);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.windowClass = (Class) values[1];
		this.parameterMap = (Map) values[2];
		this.target = (String) values[3];
		this.fileID = ((Integer) values[4]).intValue();
	}
	
	public SelectOption() {
		this("untitled");
	}

	public SelectOption(String value) {
		this(value, value);
	}
	
	public SelectOption(String name, int value) {
		this(name, String.valueOf(value));	
	}
	
	public SelectOption(String name, char value) {
		this(name, String.valueOf(value));	
	}

	public SelectOption(String name, String value) {
		super();
		setTransient(false);
		setName(name);
		setValue(value);
		setSelected(false);
		setDisabled(false);
	}

	/**
	 * Sets whether the <code>SelectOption</code> is selected or not.
	 * @param selected	The status to set.
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			setMarkupAttribute("selected", "selected");
		}
		else {
			this.removeMarkupAttribute("selected");
		}
	}
	
	/**
	 * Sets the label for the <code>SelectOption</code>.
	 * @param label	The label to set.
	 */
	public void setLabel(String label) {
		setMarkupAttribute("label", label);
	}
	
	/**
	 * Returns the selected status of the <code>SelectOption</code>.
	 * @return boolean	True if <code>SelectOption</code> is selected, false otherwise.
	 */
	public boolean getSelected() {
		if (isMarkupAttributeSet("selected")) {
			return true;
		}
		return false;	
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
    	ValueExpression ve = getValueExpression(SELECTED_PROPERTY);
    	if (ve != null) {
	    	boolean selected = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setSelected(selected);
    	}
    	
    	super.encodeBegin(context);
	}
	
	public void main(IWContext iwc) throws Exception {
		if (this.windowClass != null) {
			String URL = Window.getWindowURLWithParameters(this.windowClass, iwc, this.parameterMap);
			String arguments = Window.getWindowArgumentCallingScript(this.windowClass);
			setValue(URL + "$" + arguments + "$" + this.target);
			
			getParentSelect().addSelectScript(true);
		}
		if (this.fileID != -1) {
			String URL = getICFileSystem(iwc).getFileURI(this.fileID);
			String arguments = Window.getWindowArgumentCallingScript(false, false, false, false, false, true, true, true, false, 640, 480, null, null);
			setValue(URL + "$" + arguments + "$" + "_blank");
		}
	}
	
	protected GenericSelect getParentSelect() {
		UIComponent parent = this.getParent();
		if (parent != null && parent instanceof GenericSelect) {
			return (GenericSelect) parent;
		}
		return null;
	}

	public String getName(){
		return getName(true);
	}
	
	public String getName(boolean xhtmlEncode){
		if (xhtmlEncode) {
			return xhtmlEncode(super.getName());
		}
		else {
			return super.getName();
		}
	}

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<option " + getMarkupAttributesString() + " >");
			print(getName());
			println("</option>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			print("<option value=\"" + getValueAsString() + "\" >");
			print(getName());
			println("</option>");
		}
	}

	public void setWindowToOpenOnSelect(Class windowClass, Map parameterMap) {
		this.windowClass = windowClass;
		this.parameterMap = parameterMap;
		this.target = "undefined";
	}

	public void setWindowToOpenOnSelect(Class windowClass, Map parameterMap, String target) {
		this.windowClass = windowClass;
		this.parameterMap = parameterMap;
		this.target = target;
	}
	
	public void setFileToOpenOnSelect(int fileID) {
		this.fileID = fileID;
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
	
	
	/**
	 * This method is overrided from the PresentationObjectContainer superclass here 
	 * to call clone(iwc,false) and sets the askForPermission value always to false
	 */
	public Object clonePermissionChecked(IWUserContext iwc, boolean askForPermission)
	{
		//This method is overridden is because the SelectOption instances do not have a direct ICObjectInstanceId (in the Builder)
		// - this is because the Dropdownmenu is inserted in the Builder, not a SelectOption
		return this.clone(iwc,false);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof SelectOption) {
			SelectOption option = (SelectOption) obj;
			if (option.getValueAsString().equals(this.getValueAsString())) {
				return true;
			}
		}
		
		return false;
	}

	public int compareTo(SelectOption o) {
		String value = this.getValueAsString();
		String otherValue = this.getValueAsString();
		if (value.length() == 0) {
			return -1;
		}
		else if (otherValue.length() == 0) {
			return 1;
		}
		
		String name = this.getName();
		String otherName = o.getName();
		
		return Collator.getInstance(IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings().getDefaultLocale()).compare(name, otherName);
	}
}