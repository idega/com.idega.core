package com.idega.user.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class UserBusinessHomeImpl extends IBOHomeImpl implements
		UserBusinessHome {
	public Class getBeanInterfaceClass() {
		return UserBusiness.class;
	}

	public UserBusiness create() throws CreateException {
		return (UserBusiness) super.createIBO();
	}
}