package com.idega.presentation.ui.contacts;

import java.util.Collection;

import javax.el.ValueExpression;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.idega.core.contact.data.ContactPurpose;
import com.idega.core.contact.data.ContactPurposeHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class ContactPurposeSelector   extends IWBaseComponent implements ValueHolder{
	
	private String name = "default-cps-name";
	private Object value;
    private Converter converter;
    private DropdownMenu dropdownMenu = null;

	@Override
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		
		IWContext iwc = IWContext.getIWContext(context);
		IWBundle bundle = CoreUtil.getCoreBundle();
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		
		
		dropdownMenu = new DropdownMenu(name);
		add(dropdownMenu);
		
		dropdownMenu.addMenuElement(CoreConstants.UNDEFINED_VALUE, iwrb.getLocalizedString("select_purpose", "Select_purpose"));
		ContactPurposeHome contactPurposeHome;
		Collection <ContactPurpose> contactPurposes = null;
		try {
			contactPurposeHome = (ContactPurposeHome) IDOLookup.getHome(ContactPurpose.class);
			contactPurposes = contactPurposeHome.getContactPurposes(-1);
		} catch (Exception e) {
			
		}
		if(!ListUtil.isEmpty(contactPurposes)){
			for(ContactPurpose contactPurpose : contactPurposes){
				String label = iwrb.getLocalizedString(contactPurpose.getName(),contactPurpose.getName());
				String value = String.valueOf(contactPurpose.getPrimaryKey());
				dropdownMenu.addMenuElement(value, label);
			}
		}
		dropdownMenu.setSelectedElement(getContactPurposeId());
	}




	@Override
	protected void updateComponent(FacesContext context) {
		super.updateComponent(context);
		dropdownMenu.setSelectedElement(getContactPurposeId());
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public Object getLocalValue() {
		return value;
	}

	public Object getValue() {
		if (value != null){
            return value;
        }
        ValueExpression expression = getValueExpression("value");
        if (expression != null){
            return expression.getValue(getFacesContext().getELContext());
        }
        return null;
	}
	
	private String getContactPurposeId(){
		Object value = getValue();
		if(value == null){
			return CoreConstants.UNDEFINED_VALUE;
		}
		if(value instanceof ContactPurpose){
			Object primaryKey = ((ContactPurpose)value).getPrimaryKey();
			String contactPurpose = String.valueOf(primaryKey);
			if(StringUtil.isEmpty(contactPurpose)){
				return CoreConstants.UNDEFINED_VALUE;
			}
			return contactPurpose;
		}
		return String.valueOf(value);
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Converter getConverter() {
		if (converter != null) {
            return converter;
        }
        ValueExpression expression = getValueExpression("converter");
        if (expression != null){
            return (Converter) expression.getValue(getFacesContext().getELContext());
        }
        return null;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}
}