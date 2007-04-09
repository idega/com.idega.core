package com.idega.idegaweb;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;
import javax.servlet.ServletContext;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.appserver.AppServer;
import com.idega.core.cache.IWCacheManager2;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.graphics.generator.ImageFactory;


/**
 * <p>
 * IWMainApplication subclass for sub-domains.
 * </p>
 *  Last modified: $Date: 2007/04/09 22:17:59 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWSubApplication extends IWMainApplication {

	protected IWMainApplication superApplication;
	
	//Overrided instances:
	private IWApplicationContext iwac;
	private String domainName;
	

	public IWSubApplication(IWMainApplication superApp,String domainName) {
		setSuperApplication(superApp);
		setDomainName(domainName);
	}

	public IWMainApplication getSuperApplication() {
		return superApplication;
	}

	public void setSuperApplication(IWMainApplication superApplication) {
		this.superApplication = superApplication;
	}

	public void addApplicationEventListener(Class eventListenerClass) {
		getSuperApplication().addApplicationEventListener(eventListenerClass);
	}

	public void addComponent(String componentType, String componentClass) {
		getSuperApplication().addComponent(componentType, componentClass);
	}

	public void addConverter(Class targetClass, String converterClass) {
		getSuperApplication().addConverter(targetClass, converterClass);
	}

	public void addConverter(String converterId, String converterClass) {
		getSuperApplication().addConverter(converterId, converterClass);
	}

	public void addLocaleToRegisteredBundles(Locale locale) {
		getSuperApplication().addLocaleToRegisteredBundles(locale);
	}

	public void addValidator(String validatorId, String validatorClass) {
		getSuperApplication().addValidator(validatorId, validatorClass);
	}

	public UIComponent createComponent(String componentType) throws FacesException {
		return getSuperApplication().createComponent(componentType);
	}

	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType) throws FacesException {
		return getSuperApplication().createComponent(componentBinding, context, componentType);
	}

	public Converter createConverter(Class targetClass) {
		return getSuperApplication().createConverter(targetClass);
	}

	public Converter createConverter(String converterId) {
		return getSuperApplication().createConverter(converterId);
	}

	public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException {
		return getSuperApplication().createMethodBinding(ref, params);
	}

	public Validator createValidator(String validatorId) throws FacesException {
		return getSuperApplication().createValidator(validatorId);
	}

	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return getSuperApplication().createValueBinding(ref);
	}

	public boolean equals(Object obj) {
		return getSuperApplication().equals(obj);
	}

	public AccessController getAccessController() {
		return getSuperApplication().getAccessController();
	}

	public ActionListener getActionListener() {
		return getSuperApplication().getActionListener();
	}

	public String getApplicationContextURI() {
		return getSuperApplication().getApplicationContextURI();
	}

	public List getApplicationEventListeners() {
		return getSuperApplication().getApplicationEventListeners();
	}

	public String getApplicationPrivateRealPath() {
		return getSuperApplication().getApplicationPrivateRealPath();
	}

	public String getApplicationPrivateVirtualPath() {
		return getSuperApplication().getApplicationPrivateVirtualPath();
	}

	public String getApplicationRealPath() {
		return getSuperApplication().getApplicationRealPath();
	}

	public AppServer getApplicationServer() {
		return getSuperApplication().getApplicationServer();
	}

	public String getApplicationSpecialRealPath() {
		return getSuperApplication().getApplicationSpecialRealPath();
	}

	public String getApplicationSpecialVirtualPath() {
		return getSuperApplication().getApplicationSpecialVirtualPath();
	}

	public Object getAttribute(String parameterName, Object defaultObjectToReturnIfValueIsNull) {
		return getSuperApplication().getAttribute(parameterName, defaultObjectToReturnIfValueIsNull);
	}

	public Object getAttribute(String parameterName) {
		return getSuperApplication().getAttribute(parameterName);
	}

	public Enumeration getAttributeNames() {
		return getSuperApplication().getAttributeNames();
	}

	public List getAvailableLocales() {
		return getSuperApplication().getAvailableLocales();
	}

	public String getBuilderPagePrefixURI() {
		return getSuperApplication().getBuilderPagePrefixURI();
	}

	public String getBuildNumber() {
		return getSuperApplication().getBuildNumber();
	}

	public IWBundle getBundle(String bundleIdentifier, boolean autoCreate) throws IWBundleDoesNotExist {
		return getSuperApplication().getBundle(bundleIdentifier, autoCreate);
	}

	public IWBundle getBundle(String bundleIdentifier) throws IWBundleDoesNotExist {
		return getSuperApplication().getBundle(bundleIdentifier);
	}

	public String getBundlesRealPath() {
		return getSuperApplication().getBundlesRealPath();
	}

	public String getCacheDirectoryURI() {
		return getSuperApplication().getCacheDirectoryURI();
	}

	public Iterator getComponentTypes() {
		return getSuperApplication().getComponentTypes();
	}

	public String getContextURL() {
		return getSuperApplication().getContextURL();
	}

	public Iterator getConverterIds() {
		return getSuperApplication().getConverterIds();
	}

	public Iterator getConverterTypes() {
		return getSuperApplication().getConverterTypes();
	}

	public IWBundle getCoreBundle() {
		return getSuperApplication().getCoreBundle();
	}

	public Locale getCoreLocale() {
		return getSuperApplication().getCoreLocale();
	}

	public String getDefaultDarkInterfaceColor() {
		return getSuperApplication().getDefaultDarkInterfaceColor();
	}

	public String getDefaultLightInterfaceColor() {
		return getSuperApplication().getDefaultLightInterfaceColor();
	}

	public Locale getDefaultLocale() {
		return getSuperApplication().getDefaultLocale();
	}

	public String getDefaultRenderKitId() {
		return getSuperApplication().getDefaultRenderKitId();
	}

	public String getIdegaWebApplicationsURI() {
		return getSuperApplication().getIdegaWebApplicationsURI();
	}

	public String getIFrameContentURI() {
		return getSuperApplication().getIFrameContentURI();
	}

	public ImageFactory getImageFactory() {
		return getSuperApplication().getImageFactory();
	}

	public String getInitParameter(String p0) {
		return getSuperApplication().getInitParameter(p0);
	}

	public Enumeration getInitParameterNames() {
		return getSuperApplication().getInitParameterNames();
	}

	public ApplicationInstallationInfo getInstallationInfo() {
		return getSuperApplication().getInstallationInfo();
	}

	public IWApplicationContext getIWApplicationContext() {
		//return getSuperApplication().getIWApplicationContext();
	
		if(iwac==null){
			iwac = new IWSubApplicationContext(this);
		}
		return iwac;
	}

	public IWCacheManager getIWCacheManager() {
		return getSuperApplication().getIWCacheManager();
	}

	public IWCacheManager2 getIWCacheManager2() {
		return getSuperApplication().getIWCacheManager2();
	}

	public String getLoginURI() {
		return getSuperApplication().getLoginURI();
	}

	public int getMajorVersion() {
		return getSuperApplication().getMajorVersion();
	}

	public String getMediaServletURI() {
		return getSuperApplication().getMediaServletURI();
	}

	public String getMessageBundle() {
		return getSuperApplication().getMessageBundle();
	}

	public String getMimeType(String p0) {
		return getSuperApplication().getMimeType(p0);
	}

	public int getMinorVersion() {
		return getSuperApplication().getMinorVersion();
	}

	public NavigationHandler getNavigationHandler() {
		return getSuperApplication().getNavigationHandler();
	}

	public String getObjectInstanciatorURI() {
		return getSuperApplication().getObjectInstanciatorURI();
	}

	public String getObjectInstanciatorURI(Class className, String templateName) {
		return getSuperApplication().getObjectInstanciatorURI(className, templateName);
	}

	public String getObjectInstanciatorURI(Class classToInstanciate) {
		return getSuperApplication().getObjectInstanciatorURI(classToInstanciate);
	}

	public String getObjectInstanciatorURI(String className, String templateName) {
		return getSuperApplication().getObjectInstanciatorURI(className, templateName);
	}

	public String getObjectInstanciatorURI(String className) {
		return getSuperApplication().getObjectInstanciatorURI(className);
	}

	public ApplicationProductInfo getProductInfo() {
		return getSuperApplication().getProductInfo();
	}

	public String getPropertiesRealPath() {
		return getSuperApplication().getPropertiesRealPath();
	}

	public PropertyResolver getPropertyResolver() {
		return getSuperApplication().getPropertyResolver();
	}

	public String getPublicObjectInstanciatorURI() {
		return getSuperApplication().getPublicObjectInstanciatorURI();
	}

	public String getPublicObjectInstanciatorURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		return getSuperApplication().getPublicObjectInstanciatorURI(windowToOpen, ICObjectInstanceIDToOpen);
	}

	public String getPublicObjectInstanciatorURI(Class className, String templateName) {
		return getSuperApplication().getPublicObjectInstanciatorURI(className, templateName);
	}

	public String getPublicObjectInstanciatorURI(Class windowToOpen) {
		return getSuperApplication().getPublicObjectInstanciatorURI(windowToOpen);
	}

	public String getPublicWindowOpenerURI() {
		return getSuperApplication().getPublicWindowOpenerURI();
	}

	public String getPublicWindowOpenerURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		return getSuperApplication().getPublicWindowOpenerURI(windowToOpen, ICObjectInstanceIDToOpen);
	}

	public String getPublicWindowOpenerURI(Class windowToOpen) {
		return getSuperApplication().getPublicWindowOpenerURI(windowToOpen);
	}

	public String getRealPath(String requestURI) {
		return getSuperApplication().getRealPath(requestURI);
	}

	public List getRegisteredBundles() {
		return getSuperApplication().getRegisteredBundles();
	}

	public URL getResource(String p0) throws MalformedURLException {
		return getSuperApplication().getResource(p0);
	}

	public InputStream getResourceAsStream(String p0) {
		return getSuperApplication().getResourceAsStream(p0);
	}

	public Set getResourcePaths(String s) {
		return getSuperApplication().getResourcePaths(s);
	}

	public String getServerInfo() {
		return getSuperApplication().getServerInfo();
	}

	public ServletContext getServletContext() {
		return getSuperApplication().getServletContext();
	}

	public IWMainApplicationSettings getSettings() {
		return getSuperApplication().getSettings();
	}

	public StateManager getStateManager() {
		return getSuperApplication().getStateManager();
	}

	public Map getStaticWindowInstances() {
		return getSuperApplication().getStaticWindowInstances();
	}

	public Iterator getSupportedLocales() {
		return getSuperApplication().getSupportedLocales();
	}

	public IWSystemProperties getSystemProperties() {
		return getSuperApplication().getSystemProperties();
	}

	public String getTranslatedURIWithContext(String url) {
		return getSuperApplication().getTranslatedURIWithContext(url);
	}

	public String getURIFromURL(String URL) {
		return getSuperApplication().getURIFromURL(URL);
	}

	public Iterator getValidatorIds() {
		return getSuperApplication().getValidatorIds();
	}

	public VariableResolver getVariableResolver() {
		return getSuperApplication().getVariableResolver();
	}

	public String getVersion() {
		return getSuperApplication().getVersion();
	}

	public ViewHandler getViewHandler() {
		return getSuperApplication().getViewHandler();
	}

	public String getWindowOpenerURI() {
		return getSuperApplication().getWindowOpenerURI();
	}

	public String getWindowOpenerURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		return getSuperApplication().getWindowOpenerURI(windowToOpen, ICObjectInstanceIDToOpen);
	}

	public String getWindowOpenerURI(Class windowToOpen) {
		return getSuperApplication().getWindowOpenerURI(windowToOpen);
	}

	public String getWindowOpenerURIWithoutContextPath() {
		return getSuperApplication().getWindowOpenerURIWithoutContextPath();
	}

	public String getWindowOpenerURIWithoutContextPath(Class windowToOpen) {
		return getSuperApplication().getWindowOpenerURIWithoutContextPath(windowToOpen);
	}

	public String getWorkspaceURI() {
		return getSuperApplication().getWorkspaceURI();
	}

	public int hashCode() {
		return getSuperApplication().hashCode();
	}

	public boolean isBuilderApplicationRunning(IWUserContext iwuc) {
		return getSuperApplication().isBuilderApplicationRunning(iwuc);
	}

	public boolean isBundleLoaded(String bundleIdentifier) {
		return getSuperApplication().isBundleLoaded(bundleIdentifier);
	}

	public boolean isInDatabaseLessMode() {
		return getSuperApplication().isInDatabaseLessMode();
	}

	public boolean isInSetupMode() {
		return getSuperApplication().isInSetupMode();
	}

	public boolean isRunningUnderRootContext() {
		return getSuperApplication().isRunningUnderRootContext();
	}

	public void loadBundle(IWBundle bundle) {
		getSuperApplication().loadBundle(bundle);
	}

	public void loadBundles() {
		getSuperApplication().loadBundles();
	}

	public void loadViewManager() {
		getSuperApplication().loadViewManager();
	}

	public void log(String p0, Throwable p1) {
		getSuperApplication().log(p0, p1);
	}

	public void log(String p0) {
		getSuperApplication().log(p0);
	}

	public boolean registerBundle(String bundleIdentifier, boolean autoCreate) {
		return getSuperApplication().registerBundle(bundleIdentifier, autoCreate);
	}

	public boolean registerBundle(String bundleIdentifier, String bundlePath, boolean autoCreate) {
		return getSuperApplication().registerBundle(bundleIdentifier, bundlePath, autoCreate);
	}

	public boolean registerBundle(String bundleIdentifier, String bundlePath) {
		return getSuperApplication().registerBundle(bundleIdentifier, bundlePath);
	}

	public void removeAttribute(String parameterName) {
		getSuperApplication().removeAttribute(parameterName);
	}

	public boolean restartApplication() {
		return getSuperApplication().restartApplication();
	}

	public void setActionListener(ActionListener listener) {
		getSuperApplication().setActionListener(listener);
	}

	public void setApplicationContextURI(String uri) {
		getSuperApplication().setApplicationContextURI(uri);
	}

	public void setApplicationServer(AppServer appServer) {
		getSuperApplication().setApplicationServer(appServer);
	}

	public void setAttribute(String parameterName, Object objectToStore) {
		getSuperApplication().setAttribute(parameterName, objectToStore);
	}

	public void setDefaultDarkInterfaceColor(String color) {
		getSuperApplication().setDefaultDarkInterfaceColor(color);
	}

	public void setDefaultLightInterfaceColor(String color) {
		getSuperApplication().setDefaultLightInterfaceColor(color);
	}

	public void setDefaultLocale(Locale locale) {
		getSuperApplication().setDefaultLocale(locale);
	}

	public void setDefaultRenderKitId(String renderKitId) {
		getSuperApplication().setDefaultRenderKitId(renderKitId);
	}

	public void setInDatabaseLessMode(boolean inDatabaseLessMode) {
		getSuperApplication().setInDatabaseLessMode(inDatabaseLessMode);
	}

	public void setInSetupMode(boolean inSetupMode) {
		getSuperApplication().setInSetupMode(inSetupMode);
	}

	public void setMessageBundle(String bundle) {
		getSuperApplication().setMessageBundle(bundle);
	}

	public void setNavigationHandler(NavigationHandler handler) {
		getSuperApplication().setNavigationHandler(handler);
	}

	public void setPropertyResolver(PropertyResolver resolver) {
		getSuperApplication().setPropertyResolver(resolver);
	}

	public void setStateManager(StateManager manager) {
		getSuperApplication().setStateManager(manager);
	}

	public void setSupportedLocales(Collection locales) {
		getSuperApplication().setSupportedLocales(locales);
	}

	public void setVariableResolver(VariableResolver resolver) {
		getSuperApplication().setVariableResolver(resolver);
	}

	public void setViewHandler(ViewHandler handler) {
		getSuperApplication().setViewHandler(handler);
	}

	public void startAccessController() {
		getSuperApplication().startAccessController();
	}

	public void startFileSystem() {
		getSuperApplication().startFileSystem();
	}

	public void storeStatus() {
		getSuperApplication().storeStatus();
	}

	public String toString() {
		return getSuperApplication().toString();
	}

	public void unloadInstanceAndClass() {
		getSuperApplication().unloadInstanceAndClass();
	}


	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
}
