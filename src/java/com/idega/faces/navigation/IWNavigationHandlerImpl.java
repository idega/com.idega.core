/*
 * $Id: IWNavigationHandlerImpl.java,v 1.5 2006/05/11 17:06:38 eiki Exp $
 * Created on Nov 8, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.faces.navigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.application.NavigationHandlerImpl;
import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.element.NavigationCase;
import org.apache.myfaces.config.element.NavigationRule;
import org.apache.myfaces.portlet.PortletUtil;
import org.apache.myfaces.shared_impl.util.HashMapUtils;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;

public class IWNavigationHandlerImpl extends NavigationHandler {

	private static final Log log = LogFactory.getLog(NavigationHandlerImpl.class);

	private static final String ASTERISK = "*";

	private Map _navigationCases = null;
	private List _wildcardKeys = new ArrayList();

	public IWNavigationHandlerImpl() {
		if (log.isTraceEnabled()) {
			log.trace("New NavigationHandler instance created");
		}
	}

	public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
		if (outcome == null) {
			// stay on current ViewRoot
			return;
		}

		// switch to page uri if the view id represents a builder page stored as jsp
		String viewId = facesContext.getViewRoot().getViewId();

		ViewManager viewManager = ViewManager.getInstance(facesContext);
		ViewNode viewNode = viewManager.getViewNodeForContext(facesContext);
		if (viewNode != null) {
			// if viewNode is not null we have a request for an URI in the ViewNode
			// structure
			String pageUri = viewManager.getRequestUriWithoutContext(facesContext);
			if (pageUri != null) {
				// something like "/pages/test/hello/" or "/workspace/builder/"
				viewId = pageUri;
			}

		}

		Map casesMap = getNavigationCases(facesContext);
		NavigationCase navigationCase = null;

		List casesList = (List) casesMap.get(viewId);
		if (casesList != null) {
			// Exact match?
			navigationCase = calcMatchingNavigationCase(casesList, fromAction, outcome);
		}

		if (navigationCase == null) {
			// Wildcard match?
			List keys = getSortedWildcardKeys();
			for (int i = 0, size = keys.size(); i < size; i++) {
				String fromViewId = (String) keys.get(i);
				if (fromViewId.length() > 2) {
					String prefix = fromViewId.substring(0, fromViewId.length() - 1);
					if (viewId != null && viewId.startsWith(prefix)) {
						casesList = (List) casesMap.get(fromViewId);
						if (casesList != null) {
							navigationCase = calcMatchingNavigationCase(casesList, fromAction, outcome);
							if (navigationCase != null) {
								break;
							}
						}
					}
				}
				else {
					casesList = (List) casesMap.get(fromViewId);
					if (casesList != null) {
						navigationCase = calcMatchingNavigationCase(casesList, fromAction, outcome);
						if (navigationCase != null) {
							break;
						}
					}
				}
			}
		}

		if (navigationCase != null) {
			if (log.isTraceEnabled()) {
				log.trace("handleNavigation fromAction=" + fromAction + " outcome=" + outcome + " toViewId =" + navigationCase.getToViewId() + " redirect=" + navigationCase.isRedirect());
			}
			if (navigationCase.isRedirect() && (!PortletUtil.isPortletRequest(facesContext))) { // Spec
																																													// section
																																													// 7.4.2
																																													// says
																																													// "redirects
																																													// not
																																													// possible"
																																													// in
																																													// this
																																													// case
																																													// for
																																													// portlets
				ExternalContext externalContext = facesContext.getExternalContext();
				ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
				String redirectPath = viewHandler.getActionURL(facesContext, navigationCase.getToViewId());

				try {
					externalContext.redirect(externalContext.encodeActionURL(redirectPath));
				}
				catch (IOException e) {
					throw new FacesException(e.getMessage(), e);
				}
				facesContext.responseComplete();
			}
			else {
				ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
				// create new view
				String newViewId = navigationCase.getToViewId();
				UIViewRoot viewRoot = viewHandler.createView(facesContext, newViewId);
				viewRoot.setViewId(newViewId);
				facesContext.setViewRoot(viewRoot);
				facesContext.renderResponse();
			}
		}
		else {
			// no navigationcase found, stay on current ViewRoot
			if (log.isTraceEnabled()) {
				log.trace("handleNavigation fromAction=" + fromAction + " outcome=" + outcome + " no matching navigation-case found, staying on current ViewRoot");
			}
		}
	}

	private NavigationCase calcMatchingNavigationCase(List casesList, String actionRef, String outcome) {
		for (int i = 0, size = casesList.size(); i < size; i++) {
			NavigationCase caze = (NavigationCase) casesList.get(i);
			String cazeOutcome = caze.getFromOutcome();
			String cazeActionRef = caze.getFromAction();
			if ((cazeOutcome == null || cazeOutcome.equals(outcome)) && (cazeActionRef == null || cazeActionRef.equals(actionRef))) {
				return caze;
			}
		}
		return null;
	}

	private List getSortedWildcardKeys() {
		return this._wildcardKeys;
	}

	private Map getNavigationCases(FacesContext facesContext) {
		ExternalContext externalContext = facesContext.getExternalContext();
		RuntimeConfig runtimeConfig = RuntimeConfig.getCurrentInstance(externalContext);

		if (this._navigationCases == null || runtimeConfig.isNavigationRulesChanged()) {
			synchronized (this) {
				if (this._navigationCases == null || runtimeConfig.isNavigationRulesChanged()) {
					Collection rules = runtimeConfig.getNavigationRules();
					int rulesSize = rules.size();
					Map cases = new HashMap(HashMapUtils.calcCapacity(rulesSize));
					List wildcardKeys = new ArrayList();

					for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
						NavigationRule rule = (NavigationRule) iterator.next();
						String fromViewId = rule.getFromViewId();

						// specification 7.4.2 footnote 4 - missing fromViewId is allowed:
						if (fromViewId == null) {
							fromViewId = ASTERISK;
						}
						else {
							fromViewId = fromViewId.trim();
						}

						List list = (List) cases.get(fromViewId);
						if (list == null) {
							list = new ArrayList(rule.getNavigationCases());
							cases.put(fromViewId, list);
							if (fromViewId.endsWith(ASTERISK)) {
								wildcardKeys.add(fromViewId);
							}
						}
						else {
							list.addAll(rule.getNavigationCases());
						}

					}
					Collections.sort(wildcardKeys, new KeyComparator());

					synchronized (cases) {
						// We do not really need this sychronization at all, but this
						// gives us the peace of mind that some good optimizing compiler
						// will not rearrange the execution of the assignment to an
						// earlier time, before all init code completes
						this._navigationCases = cases;
						this._wildcardKeys = wildcardKeys;

						runtimeConfig.setNavigationRulesChanged(false);
					}
				}
			}
		}
		return this._navigationCases;
	}

	protected static final class KeyComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			return -(((String) o1).compareTo((String) o2));
		}
	}
}
