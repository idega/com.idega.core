package com.idega.user.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class UserInfoColumnsBusinessHomeImpl extends IBOHomeImpl implements UserInfoColumnsBusinessHome {

	public Class getBeanInterfaceClass() {
		return UserInfoColumnsBusiness.class;
	}

	public UserInfoColumnsBusiness create() throws CreateException {
		return (UserInfoColumnsBusiness) super.createIBO();
	}
}