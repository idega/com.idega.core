package com.idega.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class IWHttpSessionEventListener implements HttpSessionListener {

	@Autowired
	private IWHttpSessionsManager sessionsManager;

	@Autowired
	private ScriptCallerInterface scriptCaller;

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		HttpSession newSession = sessionEvent.getSession();
		getSessionsManager().addSession(newSession);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession destroyedSession = sessionEvent.getSession();

		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		if (settings.getBoolean("test_logout_stack", false)) {
			LoginSession loginSession = (LoginSession) destroyedSession.getAttribute("loginSession");
			//	Checking if user was logged in
			if (loginSession != null && loginSession.getUserEntity() != null) {
				String message = null;
				try {
					User user = loginSession.getUserEntity();
					message = "Session (ID: " + destroyedSession.getId() + ") destroyed, logged out user " + user + ", personal ID: " + user.getPersonalID();
					throw new RuntimeException(message);
				} catch (Exception e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING, StringUtil.isEmpty(message) ? "Session destroyed" : message, e);
				}
			}
		}

		//	Redirecting only if property set
		if (settings.getBoolean("redirect_when_session_timeout", Boolean.FALSE)) {
			LoginSession loginSession = (LoginSession) destroyedSession.getAttribute("loginSession");
			//	Checking if user was logged in
			if (loginSession != null && loginSession.getUserEntity() != null) {
				//	If session is time out redirect user to /pages
				if ((System.currentTimeMillis() - destroyedSession.getLastAccessedTime()) >= destroyedSession.getMaxInactiveInterval()) {
					//	Redirect
					ScriptCallerInterface scriptCaller = getScriptCaller();
					if (scriptCaller != null) {
						scriptCaller.setScript("window.location.pathname = '" + CoreConstants.PAGES_URI_PREFIX + "';");
						scriptCaller.setSessionId(destroyedSession.getId());
						scriptCaller.run();
					}
				}
			}
		}

		getSessionsManager().removeSession(destroyedSession.getId());
	}

	private ScriptCallerInterface getScriptCaller() {
		if (scriptCaller == null) {
			ELUtil.getInstance().autowire(this);
		}
		return scriptCaller;
	}

	public IWHttpSessionsManager getSessionsManager() {
		if (sessionsManager == null) {
			ELUtil.getInstance().autowire(this);
		}
		return sessionsManager;
	}
}