package com.idega.presentation.ui;

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;

/**
 * @author laddi
 */
public class SelectOption extends InterfaceObject {
	
	private Class windowClass;
	private Map parameterMap;
	private String target;
	private int fileID = -1;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[8];
		values[0] = super.saveState(ctx);
		values[1] = windowClass;
		values[2] = parameterMap;
		values[3] = target;
		values[4] = new Integer(fileID);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		windowClass = (Class) values[1];
		parameterMap = (Map) values[2];
		target = (String) values[3];
		fileID = ((Integer) values[4]).intValue();
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
		if (selected)
			setMarkupAttribute("selected", "selected");
		else
			this.removeMarkupAttribute("selected");
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
		if (isMarkupAttributeSet("selected"))
			return true;
		return false;	
	}
	
	public void main(IWContext iwc) throws Exception {
		if (windowClass != null) {
			String URL = Window.getWindowURLWithParameters(windowClass, iwc, parameterMap);
			String arguments = Window.getWindowArgumentCallingScript(windowClass);
			setValue(URL + "$" + arguments + "$" + target);
			
			getParentSelect().addSelectScript(true);
		}
		if (fileID != -1) {
			String URL = getICFileSystem(iwc).getFileURI(fileID);
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
		return xhtmlEncode(super.getName());
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
}