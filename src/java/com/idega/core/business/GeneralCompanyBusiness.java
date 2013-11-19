package com.idega.core.business;

import java.util.Collection;

import com.idega.core.company.bean.GeneralCompany;
import com.idega.user.data.User;

public interface GeneralCompanyBusiness{
	public static final String BEAN_NAME = "jBPMCompanyBusinessImpl";
	public Collection<GeneralCompany> getJBPMCompaniesForUser(User user);
}
