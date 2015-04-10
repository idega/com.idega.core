package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.idega.business.IBOLookup;
import com.idega.core.contact.data.Email;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;

public class UserEmailDropDownMenu extends DropDownMenuInputHandler {
	protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	public UserEmailDropDownMenu() {
		super();
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);

		User current = iwc.getCurrentUser();
		if (current == null) {
			addMenuElement(" ",iwrb.getLocalizedString("UserEmailDropDownMenu.no_user", "User not logged on"));
		} else {
			Collection<Email> emails = getUserBusiness(iwc).listOfUserEmails(((Integer)current.getPrimaryKey()).intValue());
			if (emails == null || emails.size() == 0) {
				addMenuElement(" ",iwrb.getLocalizedString("UserEmailDropDownMenu.no_email", "No email for user"));
			} else {
				Iterator<Email> it = emails.iterator();
				if (emails.size() == 1) {
					Email email = it.next();
					addMenuElement(email.getPrimaryKey().toString(), email.getEmailAddress());
					setSelectedElement(email.getPrimaryKey().toString());

				} else {
					addMenuElement(" ",iwrb.getLocalizedString("UserEmailDropDownMenu.select_email", "Select email"));
					while (it.hasNext()) {
						Email email = it.next();
						addMenuElement(email.getPrimaryKey().toString(), email.getEmailAddress());
					}
				}
			}

			String selectedElement = getSelectedElementValue();
			if (selectedElement == null || selectedElement.length() == 0) {
				setSelectedElement(" ");
			}
		}
	}

	@Override
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		if (value == null) {
			return "";
		}

		Integer key = new Integer((String) value);
		try {
			Email email = getUserBusiness(iwc).getEmailHome().findByPrimaryKey(key);
			return email.getEmailAddress();
		} catch (Exception e) {
		}

		return "";
	}

	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	private UserBusiness getUserBusiness(IWApplicationContext iwc) throws RemoteException{
		return IBOLookup.getServiceInstance(iwc, UserBusiness.class);
	}

}