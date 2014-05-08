package com.idega.core.security;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import org.owasp.csrfguard.CsrfGuard;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWMainApplicationStartedEvent;
import com.idega.util.IOUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SecurityController extends DefaultSpringBean implements ApplicationListener {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof IWMainApplicationStartedEvent) {
			if (!CsrfGuard.getInstance().isEnabled()) {
				Properties prop = new Properties();
				InputStream input = null;
				try {
					input = getClass().getClassLoader().getResourceAsStream("com/idega/core/Owasp.CsrfGuard.properties");
					prop.load(input);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error reading properties for CsrfGuard", e);
				} finally {
					IOUtil.close(input);
				}
				try {
					CsrfGuard.load(prop);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error loading properties for CsrfGuard", e);
				}
			}
		}
	}

}
