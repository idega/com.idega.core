package com.idega.event;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.owasp.csrfguard.CsrfGuard;
import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.IOUtil;
import com.idega.util.expression.ELUtil;

public class IWHttpSessionEventListener implements HttpSessionListener {

	@Autowired
	private IWHttpSessionsManager sessionsManager;

	@Autowired
	private ScriptCallerInterface scriptCaller;

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		if (!CsrfGuard.getInstance().isEnabled()) {
			Properties prop = new Properties();
			InputStream input = null;
			try {
				input = getClass().getClassLoader().getResourceAsStream("com/idega/core/Owasp.CsrfGuard.properties");
				prop.load(input);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtil.close(input);
			}
			try {
				CsrfGuard.load(prop);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HttpSession newSession = sessionEvent.getSession();
		getSessionsManager().addSession(newSession);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession destroyedSession = sessionEvent.getSession();

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