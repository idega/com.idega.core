package com.idega.event;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWMainApplication;
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

		LoginSession loginSession = (LoginSession) destroyedSession.getAttribute("loginSession");

		//redirecting only if property set, and user is logged on
		if(IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("redirect_when_session_timeout", Boolean.FALSE) &&
				(loginSession != null) && (loginSession.getUser() != null)){

			//if session is time out redirect user to /pages
			if ((System.currentTimeMillis() - destroyedSession.getLastAccessedTime()) >= destroyedSession.getMaxInactiveInterval()) {
				//redirect
				this.scriptCaller.setScript("window.location.pathname = \"/pages\"");
				this.scriptCaller.setSessionId(destroyedSession.getId());
				this.scriptCaller.run();
			}

		}

		getSessionsManager().removeSession(destroyedSession.getId());
	}

	public IWHttpSessionsManager getSessionsManager() {
		if (sessionsManager == null) {
			ELUtil.getInstance().autowire(this);
		}
		return sessionsManager;
	}
}