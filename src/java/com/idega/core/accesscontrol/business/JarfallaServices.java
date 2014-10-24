package com.idega.core.accesscontrol.business;

import com.idega.business.SpringBeanName;
import com.idega.user.data.User;

@SpringBeanName("jarfallaServices")
public interface JarfallaServices {

	/**
	 * Getting User from KIR system according given peronal id
	 * @param personalId User personal id
	 * @return User from KIR
	 */
	public User getUserFromKIR(String personalId);

	/**
	 * Send SMS message to the given phone number with the given message
	 * @param toPhone Phone number send SMS message to
	 * @param message SMS message
	 * @return true/false if SMS message was successfully/unsuccessfully sent
	 */
	public boolean sendSMSMessage(String toPhone, String message);
}
