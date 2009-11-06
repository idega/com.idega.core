package com.idega.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.servlet.filter.RequestProvider;
import com.idega.util.expression.ELUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWHttpSessionsManager {
	
	private static final Logger LOGGER = Logger.getLogger(IWHttpSessionsManager.class.getName());
	
	private Map<String, HttpSession> sessions;
	
	private IWHttpSessionsManager() {
		sessions = new HashMap<String, HttpSession>();
	}
	
	void addSession(HttpSession session) {
		String id = session.getId();
		sessions.put(id, session);
		
		String uri = "unknown";
		try {
			RequestProvider requestProvider = ELUtil.getInstance().getBean(RequestProvider.class);
			uri = requestProvider.getRequest().getRequestURI();
		} catch (Exception e) {}
		LOGGER.info("HttpSession '" + id + "' created for request: " + uri);
	}
	
	void removeSession(String id) {
		sessions.remove(id);
	}
	
	@SuppressWarnings("deprecation")
	int removeUselessSessions() {
		if (sessions.isEmpty()) {
			return 0;
		}
		
		int count = 0;
		List<String> sessionsToRemove = new ArrayList<String>();
		long currentTime = System.currentTimeMillis();
		for (HttpSession session: sessions.values()) {
			long idleTime = currentTime - session.getLastAccessedTime();
			if (idleTime >= 300000) {
				//	Session "was" idle for 5 minutes or more
				Object principal = session.getValue("org.apache.slide.webdav.method.principal");
				//	Checking if session was created by Slide's root user
				if (principal instanceof String && "root".equals(principal)) {
					sessionsToRemove.add(session.getId());
					count++;
				}
			}
		}
		
		for (String sessionId: sessionsToRemove) {
			removeSession(sessionId);
		}
		
		return count;
	}
}