package com.idega.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
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
		synchronized (sessions) {
			sessions.remove(id);
		}
		
		getContext().publishEvent(new HttpSessionDestroyed(this, id));
	}
	
	public boolean isSessionValid(String id) {
		synchronized (sessions) {
			return sessions.containsKey(id);
		}
	}
	
	@SuppressWarnings("deprecation")
	String removeUselessSessions() {
		if (sessions.isEmpty()) {
			return CoreConstants.EMPTY;
		}
		
		List<String> keys = new ArrayList<String>(sessions.keySet());
		
		List<String> sessionsToRemove = new ArrayList<String>();
		long currentTime = System.currentTimeMillis();
		for (String key: keys) {
			HttpSession session = sessions.get(key);
			if (session == null) {
				continue;
			}
			
			long idleTime = currentTime - session.getLastAccessedTime();
			if (idleTime >= 600000) {
				//	Session "was" idle for 10 minutes or more
				
				Object chibaManager = session.getAttribute("chiba.session.manager");
				if (chibaManager != null) {
					continue;
				}
				
				Object principal = session.getValue("org.apache.slide.webdav.method.principal");
				//	Checking if session was created by Slide's root user
				if (principal instanceof String && "root".equals(principal)) {
					sessionsToRemove.add(session.getId());
				}
			}
		}
		
		for (String sessionId: sessionsToRemove) {
			removeSession(sessionId);
		}
		
		return ListUtil.isEmpty(sessionsToRemove) ? CoreConstants.EMPTY : sessionsToRemove.toString();
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
}