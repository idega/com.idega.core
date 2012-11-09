package com.idega.event;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.IWTimestamp;
import com.idega.util.expression.ELUtil;

public class IWHttpSessionEventListener implements HttpSessionListener {

	@Autowired
	private IWHttpSessionsManager sessionsManager;

	@Autowired
	private ScriptCallerInterface scriptCaller;

	public void sessionCreated(HttpSessionEvent sessionEvent) {
		HttpSession newSession = sessionEvent.getSession();
		getSessionsManager().addSession(newSession);
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession destroyedSession = sessionEvent.getSession();
		Logger.getLogger(getClass().getName()).info("*********** destroyed: " + destroyedSession.getId() + ". Created " + new IWTimestamp(destroyedSession.getCreationTime()));
		
		//	Redirecting only if property set
		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("redirect_when_session_timeout", Boolean.FALSE)) {
			LoginSession loginSession = (LoginSession) destroyedSession.getAttribute("loginSession");
			//	Checking if user were logged in
			if (loginSession != null && loginSession.getUser() != null) {
				//	If session is time out redirect user to /pages
				if ((System.currentTimeMillis() - destroyedSession.getLastAccessedTime()) >= destroyedSession.getMaxInactiveInterval()) {
					//	Redirect
					ScriptCallerInterface scriptCaller = getScriptCaller();
					if (scriptCaller != null) {
						scriptCaller.setScript("window.location.pathname = '/pages/';");
						scriptCaller.setSessionId(destroyedSession.getId());
						scriptCaller.run();
					}
				}
			}
		}
		
		getSessionsManager().removeSession(destroyedSession.getId());
	}
	
	private ScriptCallerInterface getScriptCaller() {
		if (scriptCaller == null)
			ELUtil.getInstance().autowire(this);
		return scriptCaller;
	}

	public IWHttpSessionsManager getSessionsManager() {
		if (sessionsManager == null) {
			ELUtil.getInstance().autowire(this);
		}
		return sessionsManager;
	}
}