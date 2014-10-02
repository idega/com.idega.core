package com.idega.core.accesscontrol.business;

import com.idega.business.SpringBeanName;
import com.idega.idegaweb.IWApplicationContext;

@SpringBeanName("twoStepLoginVerificatorBean")
public interface TwoStepLoginVerificator {

	public boolean doSendSecondStepVerification(IWApplicationContext iwac, String userName, String sessionId);

	public boolean checkSecondStepVerification(String smsCode, String userName, String sessionId);

	public void invalidateSecondStepVerification(String userName, String sessionId, String smsCode);

	public void invalidateSecondStepVerification(String userName);
}