package com.idega.presentation.ui.contacts;

import java.util.Collection;

import javax.el.ValueExpression;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.idega.core.contact.data.PhoneType;
import com.idega.core.contact.data.PhoneTypeHome;
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


public class PhoneTypeSelector extends IWBaseComponent implements ValueHolder/*javax.faces.component.UIOutput*/{
	

	private String name = "default-pts-name";
	private Object value;
    private Converter converter;
    
    private DropdownMenu dropdownMenu;

	
	@Override
	public Object saveState(FacesContext facesContext) {
		Object[] values = new Object[4];
        values[0] = super.saveState(facesContext);
        values[1] = name;
        values[2] = value;
        values[3] = converter;
        return values; 
	}

	@Override
	public void restoreState(FacesContext facesContext, Object state) {
		// TODO Auto-generated method stub
		Object[] values = (Object[])state;
        super.restoreState(facesContext,values[0]);
        name = (java.lang.String) values[1];
        value = (java.lang.String) values[2];
        converter = (javax.faces.convert.Converter) values[3];
	}
	
	public PhoneTypeSelector(){}
	

	@Override
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		
		IWContext iwc = IWContext.getIWContext(context);
		IWBundle bundle = CoreUtil.getCoreBundle();
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		
		dropdownMenu = new DropdownMenu(name);
		add(dropdownMenu);
		
		dropdownMenu.addMenuElement("-1", iwrb.getLocalizedString("select_phone_type", "Select phone type"));
		PhoneTypeHome phoneTypeHome;
		Collection <PhoneType> phoneTypes = null;
		try {
			phoneTypeHome = (PhoneTypeHome) IDOLookup.getHome(PhoneType.class);
			phoneTypes = phoneTypeHome.getPhoneTypes(-1);
		} catch (Exception e) {
			
		}
		if(!ListUtil.isEmpty(phoneTypes)){
			for(PhoneType phoneType : phoneTypes){
				String label = iwrb.getLocalizedString(phoneType.getName(),phoneType.getName());
				String value = String.valueOf(phoneType.getPrimaryKey());
				dropdownMenu.addMenuElement(value, label);
			}
		}
		dropdownMenu.setSelectedElement(getPhoneTypeId());
	}

	@Override
	protected void updateComponent(FacesContext context) {
		super.updateComponent(context);
		dropdownMenu.setSelectedElement(getPhoneTypeId());
	}
	
	private String getPhoneTypeId(){
		Object value = getValue();
		if(value == null){
			return CoreConstants.UNDEFINED_VALUE;
		}
		if(value instanceof PhoneType){
			Object primaryKey = ((PhoneType)value).getPrimaryKey();
			String phoneTypeId = String.valueOf(primaryKey);
			if(StringUtil.isEmpty(phoneTypeId)){
				return CoreConstants.UNDEFINED_VALUE;
			}
			return phoneTypeId;
		}
		return String.valueOf(value);
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