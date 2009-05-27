/*
 * $Id: LoginState.java,v 1.2 2009/05/27 09:44:51 laddi Exp $ Created on 3.9.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.accesscontrol.business;

import java.io.Serializable;

/**
 * 
 * Last modified: $Date: 2009/05/27 09:44:51 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class LoginState implements Serializable {

	private static final long serialVersionUID = 2782211883576718465L;

	private static final int STATE_NO_STATE = 0;
	private static final int STATE_LOGGED_ON = 1;
	private static final int STATE_LOGGED_OUT = 2;
	private static final int STATE_NO_USER = 3;
	private static final int STATE_WRONG_PASSW = 4;
	private static final int STATE_LOGIN_EXPIRED = 5;
	private static final int STATE_LOGIN_FAILED = 6;
	private static final int STATE_LOGIN_FAILED_DISABLED_NEXT_TIME = 8;

	private int state = STATE_NO_STATE;

	private LoginState(int state) {
		this.state = state;
	}

	public static LoginState NoState = new LoginState(STATE_NO_STATE);
	public static LoginState LoggedOn = new LoginState(STATE_LOGGED_ON);
	public static LoginState LoggedOut = new LoginState(STATE_LOGGED_OUT);
	public static LoginState NoUser = new LoginState(STATE_NO_USER);
	public static LoginState WrongPassword = new LoginState(STATE_WRONG_PASSW);
	public static LoginState Expired = new LoginState(STATE_LOGIN_EXPIRED);
	public static LoginState Failed = new LoginState(STATE_LOGIN_FAILED);
	public static LoginState FailedDisabledNextTime = new LoginState(STATE_LOGIN_FAILED_DISABLED_NEXT_TIME);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(LoginState arg) {
		return this.state == arg.state;
	}

	public int getStateValue() {
		return this.state;
	}

}
