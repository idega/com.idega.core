package com.idega.core.user.presentation;

import java.util.Iterator;
import java.util.List;

import javax.transaction.TransactionManager;

import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.data.GenericEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;
import com.idega.transaction.IdegaTransactionManager;
import com.idega.util.IWTimestamp;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class CreateUser extends Window {

	private Text firstNameText;
	private Text middleNameText;
	private Text lastNameText;
	private Text userLoginText;
	private Text passwordText;
	private Text confirmPasswordText;

	private Text generateLoginText;
	private Text generatePasswordText;
	private Text mustChangePasswordText;
	private Text cannotChangePasswordText;
	private Text passwordNeverExpiresText;
	private Text disableAccountText;
	private Text goToPropertiesText;
	private Text primaryGroupText;

	private TextInput firstNameField;
	private TextInput middleNameField;
	private TextInput lastNameField;
	private TextInput userLoginField;
	private PasswordInput passwordField;
	private PasswordInput confirmPasswordField;

	private CheckBox generateLoginField;
	private CheckBox generatePasswordField;
	private CheckBox mustChangePasswordField;
	private CheckBox cannotChangePasswordField;
	private CheckBox passwordNeverExpiresField;
	private CheckBox disableAccountField;
	private CheckBox goToPropertiesField;

	private DropdownMenu primaryGroupField;

	private SubmitButton okButton;
	private SubmitButton cancelButton;

	private Form myForm;

	public static String okButtonParameterValue = "ok";
	public static String cancelButtonParameterValue = "cancel";
	public static String submitButtonParameterName = "submit";

	public static String firstNameFieldParameterName = "firstName";
	public static String middleNameFieldParameterName = "middleName";
	public static String lastNameFieldParameterName = "lastName";
	public static String userLoginFieldParameterName = "login";
	public static String passwordFieldParameterName = "password";
	public static String confirmPasswordFieldParameterName = "confirmPassword";

	public static String generateLoginFieldParameterName = "generateLogin";
	public static String generatePasswordFieldParameterName = "generatePassword";
	public static String mustChangePasswordFieldParameterName = "mustChange";
	public static String cannotChangePasswordFieldParameterName = "cannotChange";
	public static String passwordNeverExpiresFieldParameterName = "neverExpires";
	public static String disableAccountFieldParameterName = "disableAccount";
	public static String goToPropertiesFieldParameterName = "gotoProperties";
	public static String primaryGroupFieldParameterName = "primarygroup";

	private UserBusiness business;

	private String rowHeight = "37";

	public CreateUser() {
		super();
		this.setName("idegaWeb Builder - Stofna f�laga");
		this.setHeight(460);
		this.setWidth(390);
		this.setBackgroundColor("#d4d0c8");
		this.setScrollbar(false);
		this.myForm = new Form();
		this.add(this.myForm);
		this.business = new UserBusiness();
		initializeTexts();
		initializeFields();
		lineUpElements();
	}

	protected void initializeTexts() {

		this.firstNameText = new Text("First name");
		this.middleNameText = new Text("Middle name");
		this.lastNameText = new Text("Last name");
		this.userLoginText = new Text("User login");
		this.passwordText = new Text("Password");
		this.confirmPasswordText = new Text("Confirm password");

		this.generateLoginText = new Text("generate");
		this.generatePasswordText = new Text("generate");
		this.mustChangePasswordText = new Text("User must change password at next login");
		this.cannotChangePasswordText = new Text("User cannot change password");
		this.passwordNeverExpiresText = new Text("Password never expires");
		this.disableAccountText = new Text("Account is disabled");
		this.goToPropertiesText = new Text("go to properties");

		this.primaryGroupText = new Text("Primarygroup");
	}

	protected void initializeFields() {
		this.firstNameField = new TextInput(firstNameFieldParameterName);
		this.firstNameField.setLength(12);
		this.middleNameField = new TextInput(middleNameFieldParameterName);
		this.middleNameField.setLength(12);
		this.lastNameField = new TextInput(lastNameFieldParameterName);
		this.lastNameField.setLength(12);
		this.userLoginField = new TextInput(userLoginFieldParameterName);
		this.userLoginField.setLength(12);
		this.passwordField = new PasswordInput(passwordFieldParameterName);
		this.passwordField.setLength(12);
		this.confirmPasswordField = new PasswordInput(confirmPasswordFieldParameterName);
		this.confirmPasswordField.setLength(12);

		this.generateLoginField = new CheckBox(generateLoginFieldParameterName);
		this.generatePasswordField = new CheckBox(generatePasswordFieldParameterName);
		this.mustChangePasswordField = new CheckBox(mustChangePasswordFieldParameterName);
		this.cannotChangePasswordField = new CheckBox(cannotChangePasswordFieldParameterName);
		this.passwordNeverExpiresField = new CheckBox(passwordNeverExpiresFieldParameterName);
		this.passwordNeverExpiresField.setChecked(true);
		this.disableAccountField = new CheckBox(disableAccountFieldParameterName);
		this.goToPropertiesField = new CheckBox(goToPropertiesFieldParameterName);
		this.goToPropertiesField.setChecked(true);

		this.primaryGroupField = new DropdownMenu(CreateUser.primaryGroupFieldParameterName);
		//primaryGroupField.addMenuElement("","a�alh�pur");
		this.primaryGroupField.addSeparator();

		try {
			String[] gr = new String[1];
			gr[0] = ((UserGroupRepresentative) GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
			List groups = com.idega.core.data.GenericGroupBMPBean.getAllGroups(gr, false);
			if (groups != null) {
				/**
				 * @todo filter standardGroups
				 */
				//groups.removeAll(AccessController.getStandardGroups());
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					GenericGroup item = (GenericGroup) iter.next();
					this.primaryGroupField.addMenuElement(item.getID(), item.getName());
				}
			}
		}
		catch (Exception ex) {

		}

		this.okButton = new SubmitButton("     OK     ", submitButtonParameterName, okButtonParameterValue);
		this.cancelButton = new SubmitButton(" Cancel ", submitButtonParameterName, cancelButtonParameterValue);

	}

	public void lineUpElements() {

		Table frameTable = new Table(1, 6);
		frameTable.setCellpadding(0);
		frameTable.setCellspacing(0);

		// nameTable begin
		Table nameTable = new Table(4, 2);
		nameTable.setCellpadding(0);
		nameTable.setCellspacing(0);
		nameTable.setHeight(1, this.rowHeight);
		nameTable.setHeight(2, this.rowHeight);

		nameTable.add(this.firstNameText, 1, 1);
		nameTable.add(this.firstNameField, 2, 1);
		nameTable.add(this.middleNameText, 3, 1);
		nameTable.add(this.middleNameField, 4, 1);
		nameTable.add(this.lastNameText, 1, 2);
		nameTable.add(this.lastNameField, 2, 2);
		// nameTable end

		// loginTable begin
		Table loginTable = new Table(4, 3);
		loginTable.setCellpadding(0);
		loginTable.setCellspacing(0);
		loginTable.setHeight(1, this.rowHeight);
		loginTable.setHeight(2, this.rowHeight);
		loginTable.setHeight(3, this.rowHeight);
		loginTable.setWidth(1, "110");

		loginTable.add(this.userLoginText, 1, 1);
		loginTable.add(this.userLoginField, 2, 1);
		loginTable.add(this.generateLoginField, 3, 1);
		loginTable.add(this.generateLoginText, 4, 1);
		loginTable.add(this.passwordText, 1, 2);
		loginTable.add(this.passwordField, 2, 2);
		loginTable.add(this.generatePasswordField, 3, 2);
		loginTable.add(this.generatePasswordText, 4, 2);
		loginTable.add(this.confirmPasswordText, 1, 3);
		loginTable.add(this.confirmPasswordField, 2, 3);
		// loginTable end

		// groupTable begin
		Table groupTable = new Table(2, 1);
		groupTable.setCellpadding(0);
		groupTable.setCellspacing(0);
		groupTable.setHeight(1, this.rowHeight);
		groupTable.setWidth(1, "110");

		groupTable.add(this.primaryGroupText, 1, 1);
		groupTable.add(this.primaryGroupField, 2, 1);
		// groupTable end

		// AccountPropertyTable begin
		Table AccountPropertyTable = new Table(2, 4);
		AccountPropertyTable.setCellpadding(0);
		AccountPropertyTable.setCellspacing(0);
		AccountPropertyTable.setHeight(1, this.rowHeight);
		AccountPropertyTable.setHeight(2, this.rowHeight);
		AccountPropertyTable.setHeight(3, this.rowHeight);
		AccountPropertyTable.setHeight(4, this.rowHeight);

		AccountPropertyTable.add(this.mustChangePasswordField, 1, 1);
		AccountPropertyTable.add(this.mustChangePasswordText, 2, 1);
		AccountPropertyTable.add(this.cannotChangePasswordField, 1, 2);
		AccountPropertyTable.add(this.cannotChangePasswordText, 2, 2);
		AccountPropertyTable.add(this.passwordNeverExpiresField, 1, 3);
		AccountPropertyTable.add(this.passwordNeverExpiresText, 2, 3);
		AccountPropertyTable.add(this.disableAccountField, 1, 4);
		AccountPropertyTable.add(this.disableAccountText, 2, 4);
		// AccountPropertyTable end

		// propertyTable begin
		Table propertyTable = new Table(2, 1);
		propertyTable.setCellpadding(0);
		propertyTable.setCellspacing(0);
		propertyTable.setHeight(1, this.rowHeight);

		propertyTable.add(this.goToPropertiesText, 1, 1);
		propertyTable.add(this.goToPropertiesField, 2, 1);
		// propertyTable end

		// buttonTable begin
		Table buttonTable = new Table(3, 1);
		buttonTable.setCellpadding(0);
		buttonTable.setCellspacing(0);
		buttonTable.setHeight(1, this.rowHeight);
		buttonTable.setWidth(2, "5");

		buttonTable.add(this.okButton, 1, 1);
		buttonTable.add(this.cancelButton, 3, 1);
		// buttonTable end

		frameTable.add(nameTable, 1, 1);
		frameTable.add(loginTable, 1, 2);
		frameTable.add(groupTable, 1, 3);
		frameTable.add(AccountPropertyTable, 1, 4);
		frameTable.add(propertyTable, 1, 5);
		frameTable.setAlignment(1, 5, "right");
		frameTable.add(buttonTable, 1, 6);
		frameTable.setAlignment(1, 6, "right");

		this.myForm.add(frameTable);

	}

	public void commitCreation(IWContext iwc) throws Exception {

		User newUser;
		boolean createLogin = false;

		String login = iwc.getParameter(CreateUser.userLoginFieldParameterName);
		String passw = iwc.getParameter(CreateUser.passwordFieldParameterName);
		String cfPassw = iwc.getParameter(CreateUser.confirmPasswordFieldParameterName);
		String password = null;

		String mustChage = iwc.getParameter(CreateUser.mustChangePasswordFieldParameterName);
		String cannotchangePassw = iwc.getParameter(CreateUser.cannotChangePasswordFieldParameterName);
		String passwNeverExpires = iwc.getParameter(CreateUser.passwordNeverExpiresFieldParameterName);
		String disabledAccount = iwc.getParameter(CreateUser.disableAccountFieldParameterName);
		String primaryGroup = iwc.getParameter(CreateUser.primaryGroupFieldParameterName);

		Boolean bMustChage;
		Boolean bAllowedToChangePassw;
		Boolean bPasswNeverExpires;
		Boolean bEnabledAccount;

		Integer primaryGroupId = null;

		if (primaryGroup != null && !primaryGroup.equals("")) {
			primaryGroupId = new Integer(primaryGroup);
		}

		if (mustChage != null && !"".equals(mustChage)) {
			bMustChage = Boolean.TRUE;
		}
		else {
			bMustChage = Boolean.FALSE;
		}

		if (cannotchangePassw != null && !"".equals(cannotchangePassw)) {
			bAllowedToChangePassw = Boolean.FALSE;
		}
		else {
			bAllowedToChangePassw = Boolean.TRUE;
		}

		if (passwNeverExpires != null && !"".equals(passwNeverExpires)) {
			bPasswNeverExpires = Boolean.TRUE;
		}
		else {
			bPasswNeverExpires = Boolean.FALSE;
		}

		if (disabledAccount != null && !"".equals(disabledAccount)) {
			bEnabledAccount = Boolean.FALSE;
		}
		else {
			bEnabledAccount = Boolean.TRUE;
		}

		if (passw != null && cfPassw != null && passw.equals(cfPassw)) {
			password = passw;
		}
		else if (passw != null && cfPassw != null && !passw.equals(cfPassw)) {
			throw new Exception("password and confirmed password not the same");
		}

		if (password != null && password.length() > 0 && login != null && login.length() > 0) {
			createLogin = true;
		}

		TransactionManager transaction = IdegaTransactionManager.getInstance();
		try {
			transaction.begin();
			newUser =
				this.business.insertUser(
					iwc.getParameter(firstNameFieldParameterName),
					iwc.getParameter(middleNameFieldParameterName),
					iwc.getParameter(lastNameFieldParameterName),
					null,
					null,
					null,
					null,
					primaryGroupId);

			if (createLogin) {
				LoginDBHandler.createLogin(newUser.getID(), login, password, bEnabledAccount, IWTimestamp.RightNow(), 5000, bPasswNeverExpires, bAllowedToChangePassw, bMustChage, null);
			}

			transaction.commit();
		}
		catch (Exception e) {
			transaction.rollback();
			throw new Exception(e.getMessage() + " : User entry was removed");
		}

		if (iwc.getParameter(goToPropertiesFieldParameterName) != null) {
			Link gotoLink = new Link();
			gotoLink.setWindowToOpen(UserPropertyWindow.class);
			gotoLink.addParameter(UserPropertyWindow.PARAMETERSTRING_USER_ID, newUser.getID());
			this.setWindowToOpenOnLoad(gotoLink, iwc);
		}

	}

	public void main(IWContext iwc) throws Exception {
		String submit = iwc.getParameter("submit");
		if (submit != null) {
			if (submit.equals("ok")) {
				this.commitCreation(iwc);
				this.close();
				this.setParentToReload();
			}
			else if (submit.equals("cancel")) {
				this.close();
			}
		}
	}

}
