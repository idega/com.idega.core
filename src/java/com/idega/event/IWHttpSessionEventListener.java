package com.idega.event;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.util.expression.ELUtil;

public class IWHttpSessionEventListener implements HttpSessionListener {

	@Autowired
	private IWHttpSessionsManager sessionsManager;
	
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		HttpSession newSession = sessionEvent.getSession();
		getSessionsManager().addSession(newSession);
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession destroyedSession = sessionEvent.getSession();
		getSessionsManager().removeSession(destroyedSession.getId());
	}

	public IWHttpSessionsManager getSessionsManager() {
		if (sessionsManager == null) {
			ELUtil.getInstance().autowire(this);
		}
		return sessionsManager;
	}
}