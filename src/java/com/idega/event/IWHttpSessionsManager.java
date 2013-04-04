package com.idega.event;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.util.expression.ELUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWHttpSessionsManager {

	private static final Logger LOGGER = Logger.getLogger(IWHttpSessionsManager.class.getName());

	@Autowired
	private ApplicationContext context;

	private Map<String, HttpSession> sessions;

	private IWHttpSessionsManager() {
		sessions = new HashMap<String, HttpSession>();
	}

	void addSession(HttpSession session) {
		String id = session.getId();
		synchronized (session) {
			sessions.put(id, session);
		}

		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("log_session_creation", Boolean.FALSE)) {
			String uri = "unknown";
			try {
				RequestResponseProvider requestProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
				uri = requestProvider.getRequest().getRequestURI();
			} catch (Exception e) {}
			LOGGER.info("********************************* HttpSession '" + id + "' created for request: " + uri);
		}
	}

	void removeSession(String id) {
		HttpSession session = null;
		synchronized (sessions) {
			session = sessions.remove(id);
		}

		long lastAccessedTime = session == null ? 0 : session.getLastAccessedTime();
		int maxInactiveInterval = session == null ? 0 : session.getMaxInactiveInterval();
		getContext().publishEvent(new HttpSessionDestroyed(this, id, lastAccessedTime, maxInactiveInterval));
	}

	public boolean isSessionValid(String id) {
		synchronized (sessions) {
			return sessions.containsKey(id);
		}
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
}