package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;

import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.User;

public interface UserCompanyBusiness {

	public static final String SPRING_BEAN_IDENTIFIER = "userCompanyBusinessBean";

	public Collection<Group> getUsersCompanies(IWContext iwc, User user) throws RemoteException;
	
	public void setPreferedCompanyForCurrentUser(String companyId) throws RemoteException;

	public Group getPreferedCompanyForUser(IWContext iwc, User user) throws RemoteException;
}
