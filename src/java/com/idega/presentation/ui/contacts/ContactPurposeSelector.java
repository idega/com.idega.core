package com.idega.presentation.ui.contacts;

import java.util.Collection;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.core.contact.data.ContactPurpose;
import com.idega.core.contact.data.ContactPurposeHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;

public class ContactPurposeSelector   extends IWBaseComponent{
	
	private String name = "default-cps-name";
	private ContactPurpose contactPurpose;

	
	@Override
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		
		IWContext iwc = IWContext.getIWContext(context);
		IWBundle bundle = CoreUtil.getCoreBundle();
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		
		DropdownMenu dropdownMenu = new DropdownMenu(name);
		add(dropdownMenu);
		
		dropdownMenu.addMenuElement("-1", iwrb.getLocalizedString("select_purpose", "Select_purpose"));
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
		ContactPurpose contactPurpose = getContactPurpose();
		if((contactPurpose == null) || (contactPurpose.getPrimaryKey() == null)){
			dropdownMenu.setSelectedElement("-1");
		}
		else{
			dropdownMenu.setSelectedElement(String.valueOf(contactPurpose.getPrimaryKey()));
		}
	}


	public ContactPurpose getContactPurpose() {
		if(contactPurpose != null){
			return contactPurpose;
		}
		ValueExpression valueExpression = getValueExpression("contactPurpose");
		if(valueExpression == null){
			return null;
		}
		contactPurpose = (ContactPurpose)valueExpression.getValue(getFacesContext().getELContext());
		return contactPurpose;
	}


	public void setContactPurpose(ContactPurpose contactPurpose) {
		this.contactPurpose = contactPurpose;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	
	
}