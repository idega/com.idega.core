package com.idega.reverse;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.component.bean.RenderedComponent;
import com.idega.presentation.IWContext;

/**
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.0 $
 *
 * Last modified: $Date: 2009.07.21 14:27:32 $ by: $Author: valdas $
 */
public interface ScriptDispatcher extends Serializable {

	public static final String BEAN_IDENTIFIER = "scriptDispathcerViaReverseAjax";
	
	/**
	 * Sends JavaScript action to ALL active pages with reverse Ajax enabled
	 * @param iwc
	 * @param javaScriptAction
	 * @return IDs of HTTP sessions ({@link HttpSession}) "served"
	 */
	public abstract Collection<String> dispatchScript(IWContext iwc, String javaScriptAction);
	
	/**
	 * Sends JavaScript action to active page(s) with reverse Ajax enabled
	 * @param iwc
	 * @param javaScriptAction
	 * @param invokeOriginalPage
	 * @return IDs of HTTP sessions ({@link HttpSession}) "served"
	 */
	public abstract Collection<String> dispatchScript(IWContext iwc, String javaScriptAction, boolean invokeOriginalPage);
	
	/**
	 * Renders component ({@link UIComponent}) to {@link RenderedComponent} and sends it to page(s) via reverse Ajax.
	 * @param iwc
	 * @param component
	 * @return IDs of HTTP sessions ({@link HttpSession}) "served"
	 */
	public abstract Collection<String> dispatchRenderedComponent(IWContext iwc, UIComponent component);
	
	/**
	 * Renders components ({@link UIComponent}) {@link RenderedComponent} and sends these to page(s) via reverse Ajax.
	 * @param iwc
	 * @param components
	 * @return IDs of HTTP sessions ({@link HttpSession}) "served"
	 */
	public abstract Collection<String> dispatchRenderedComponents(IWContext iwc, List<? extends UIComponent> components);
	
	/**
	 * Renders components ({@link UIComponent}) {@link RenderedComponent} and sends these to page(s) via reverse Ajax.
	 * @param iwc
	 * @param components
	 * @param options - for available options see JavaScript function 'IWCORE.insertRenderedComponent' in iw_core.js
	 * @return IDs of HTTP sessions ({@link HttpSession}) "served"
	 */
	public abstract Collection<String> dispatchRenderedComponents(IWContext iwc, List<? extends UIComponent> components, Collection<AdvancedProperty> options);
}
