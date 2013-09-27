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

	@Override
	public void addApplicationEventListener(Class eventListenerClass) {
		getSuperApplication().addApplicationEventListener(eventListenerClass);
	}

	@Override
	public void addComponent(String componentType, String componentClass) {
		getSuperApplication().addComponent(componentType, componentClass);
	}

	@Override
	public void addConverter(Class targetClass, String converterClass) {
		getSuperApplication().addConverter(targetClass, converterClass);
	}

	@Override
	public void addConverter(String converterId, String converterClass) {
		getSuperApplication().addConverter(converterId, converterClass);
	}

	@Override
	public void addLocaleToRegisteredBundles(Locale locale) {
		getSuperApplication().addLocaleToRegisteredBundles(locale);
	}

	@Override
	public void addValidator(String validatorId, String validatorClass) {
		getSuperApplication().addValidator(validatorId, validatorClass);
	}

	@Override
	public UIComponent createComponent(String componentType) throws FacesException {
		return getSuperApplication().createComponent(componentType);
	}

	@Override
	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType) throws FacesException {
		return getSuperApplication().createComponent(componentBinding, context, componentType);
	}

	@Override
	public Converter createConverter(Class targetClass) {
		return getSuperApplication().createConverter(targetClass);
	}

	@Override
	public Converter createConverter(String converterId) {
		return getSuperApplication().createConverter(converterId);
	}

	@Override
	public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException {
		return getSuperApplication().createMethodBinding(ref, params);
	}

	@Override
	public Validator createValidator(String validatorId) throws FacesException {
		return getSuperApplication().createValidator(validatorId);
	}

	@Override
	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return getSuperApplication().createValueBinding(ref);
	}

	@Override
	public boolean equals(Object obj) {
		return getSuperApplication().equals(obj);
	}

	@Override
	public AccessController getAccessController() {
		return getSuperApplication().getAccessController();
	}

	@Override
	public ActionListener getActionListener() {
		return getSuperApplication().getActionListener();
	}

	@Override
	public String getApplicationContextURI() {
		return getSuperApplication().getApplicationContextURI();
	}

	@Override
	public List getApplicationEventListeners() {
		return getSuperApplication().getApplicationEventListeners();
	}

	@Override
	public String getApplicationPrivateRealPath() {
		return getSuperApplication().getApplicationPrivateRealPath();
	}

	@Override
	public String getApplicationPrivateVirtualPath() {
		return getSuperApplication().getApplicationPrivateVirtualPath();
	}

	@Override
	public String getApplicationRealPath() {
		return getSuperApplication().getApplicationRealPath();
	}

	@Override
	public AppServer getApplicationServer() {
		return getSuperApplication().getApplicationServer();
	}

	@Override
	public String getApplicationSpecialRealPath() {
		return getSuperApplication().getApplicationSpecialRealPath();
	}

	@Override
	public String getApplicationSpecialVirtualPath() {
		return getSuperApplication().getApplicationSpecialVirtualPath();
	}

	@Override
	public Object getAttribute(String parameterName, Object defaultObjectToReturnIfValueIsNull) {
		return getSuperApplication().getAttribute(parameterName, defaultObjectToReturnIfValueIsNull);
	}

	@Override
	public Object getAttribute(String parameterName) {
		return getSuperApplication().getAttribute(parameterName);
	}

	@Override
	public Enumeration getAttributeNames() {
		return getSuperApplication().getAttributeNames();
	}

	@Override
	public List getAvailableLocales() {
		return getSuperApplication().getAvailableLocales();
	}

	@Override
	public String getBuilderPagePrefixURI() {
		return getSuperApplication().getBuilderPagePrefixURI();
	}

	@Override
	public String getBuildNumber() {
		return getSuperApplication().getBuildNumber();
	}

	@Override
	public IWBundle getBundle(String bundleIdentifier, boolean autoCreate) throws IWBundleDoesNotExist {
		return getSuperApplication().getBundle(bundleIdentifier, autoCreate);
	}

	@Override
	public IWBundle getBundle(String bundleIdentifier) throws IWBundleDoesNotExist {
		return getSuperApplication().getBundle(bundleIdentifier);
	}

	@Override
	public String getBundlesRealPath() {
		return getSuperApplication().getBundlesRealPath();
	}

	@Override
	public String getCacheDirectoryURI() {
		return getSuperApplication().getCacheDirectoryURI();
	}

	@Override
	public Iterator getComponentTypes() {
		return getSuperApplication().getComponentTypes();
	}

	@Override
	public String getContextURL() {
		return getSuperApplication().getContextURL();
	}

	@Override
	public Iterator getConverterIds() {
		return getSuperApplication().getConverterIds();
	}

	@Override
	public Iterator getConverterTypes() {
		return getSuperApplication().getConverterTypes();
	}

	@Override
	public IWBundle getCoreBundle() {
		return getSuperApplication().getCoreBundle();
	}

	@Override
	public Locale getCoreLocale() {
		return getSuperApplication().getCoreLocale();
	}

	@Override
	public String getDefaultDarkInterfaceColor() {
		return getSuperApplication().getDefaultDarkInterfaceColor();
	}

	@Override
	public String getDefaultLightInterfaceColor() {
		return getSuperApplication().getDefaultLightInterfaceColor();
	}

	@Override
	public Locale getDefaultLocale() {
		return getSuperApplication().getDefaultLocale();
	}

	@Override
	public String getDefaultRenderKitId() {
		return getSuperApplication().getDefaultRenderKitId();
	}

	@Override
	public String getIdegaWebApplicationsURI() {
		return getSuperApplication().getIdegaWebApplicationsURI();
	}

	@Override
	public String getIFrameContentURI() {
		return getSuperApplication().getIFrameContentURI();
	}

	@Override
	public ImageFactory getImageFactory() {
		return getSuperApplication().getImageFactory();
	}

	@Override
	public String getInitParameter(String p0) {
		return getSuperApplication().getInitParameter(p0);
	}

	@Override
	public Enumeration getInitParameterNames() {
		return getSuperApplication().getInitParameterNames();
	}

	@Override
	public ApplicationInstallationInfo getInstallationInfo() {
		return getSuperApplication().getInstallationInfo();
	}

	@Override
	public IWApplicationContext getIWApplicationContext() {
		//return getSuperApplication().getIWApplicationContext();

		if(iwac==null){
			iwac = new IWSubApplicationContext(this);
		}
		return iwac;
	}

	@Override
	public IWCacheManager getIWCacheManager() {
		return getSuperApplication().getIWCacheManager();
	}

	@Override
	public IWCacheManager2 getIWCacheManager2() {
		return getSuperApplication().getIWCacheManager2();
	}

	@Override
	public String getLoginURI() {
		return getSuperApplication().getLoginURI();
	}

	@Override
	public int getMajorVersion() {
		return getSuperApplication().getMajorVersion();
	}

	@Override
	public String getMediaServletURI() {
		return getSuperApplication().getMediaServletURI();
	}

	@Override
	public String getMessageBundle() {
		return getSuperApplication().getMessageBundle();
	}

	@Override
	public String getMimeType(String p0) {
		return getSuperApplication().getMimeType(p0);
	}

	@Override
	public int getMinorVersion() {
		return getSuperApplication().getMinorVersion();
	}

	@Override
	public NavigationHandler getNavigationHandler() {
		return getSuperApplication().getNavigationHandler();
	}

	@Override
	public String getObjectInstanciatorURI() {
		return getSuperApplication().getObjectInstanciatorURI();
	}

	@Override
	public String getObjectInstanciatorURI(Class className, String templateName) {
		return getSuperApplication().getObjectInstanciatorURI(className, templateName);
	}

	@Override
	public String getObjectInstanciatorURI(Class classToInstanciate) {
		return getSuperApplication().getObjectInstanciatorURI(classToInstanciate);
	}

	@Override
	public String getObjectInstanciatorURI(String className, String templateName) {
		return getSuperApplication().getObjectInstanciatorURI(className, templateName);
	}

	@Override
	public String getObjectInstanciatorURI(String className) {
		return getSuperApplication().getObjectInstanciatorURI(className);
	}

	@Override
	public ApplicationProductInfo getProductInfo() {
		return getSuperApplication().getProductInfo();
	}

	@Override
	public String getPropertiesRealPath() {
		return getSuperApplication().getPropertiesRealPath();
	}

	@Override
	public PropertyResolver getPropertyResolver() {
		return getSuperApplication().getPropertyResolver();
	}

	@Override
	public String getPublicObjectInstanciatorURI() {
		return getSuperApplication().getPublicObjectInstanciatorURI();
	}

	@Override
	public String getPublicObjectInstanciatorURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		return getSuperApplication().getPublicObjectInstanciatorURI(windowToOpen, ICObjectInstanceIDToOpen);
	}

	@Override
	public String getPublicObjectInstanciatorURI(Class className, String templateName) {
		return getSuperApplication().getPublicObjectInstanciatorURI(className, templateName);
	}

	@Override
	public String getPublicObjectInstanciatorURI(Class windowToOpen) {
		return getSuperApplication().getPublicObjectInstanciatorURI(windowToOpen);
	}

	@Override
	public String getPublicWindowOpenerURI() {
		return getSuperApplication().getPublicWindowOpenerURI();
	}

	@Override
	public String getPublicWindowOpenerURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		return getSuperApplication().getPublicWindowOpenerURI(windowToOpen, ICObjectInstanceIDToOpen);
	}

	@Override
	public String getPublicWindowOpenerURI(Class windowToOpen) {
		return getSuperApplication().getPublicWindowOpenerURI(windowToOpen);
	}

	@Override
	public String getRealPath(String requestURI) {
		return getSuperApplication().getRealPath(requestURI);
	}

	@Override
	public List getRegisteredBundles() {
		return getSuperApplication().getRegisteredBundles();
	}

	@Override
	public URL getResource(String p0) throws MalformedURLException {
		return getSuperApplication().getResource(p0);
	}

	@Override
	public InputStream getResourceAsStream(String p0) {
		return getSuperApplication().getResourceAsStream(p0);
	}

	@Override
	public Set getResourcePaths(String s) {
		return getSuperApplication().getResourcePaths(s);
	}

	@Override
	public String getServerInfo() {
		return getSuperApplication().getServerInfo();
	}

	@Override
	public ServletContext getServletContext() {
		return getSuperApplication().getServletContext();
	}

	@Override
	public IWMainApplicationSettings getSettings() {
		return getSuperApplication().getSettings();
	}

	@Override
	public StateManager getStateManager() {
		return getSuperApplication().getStateManager();
	}

	@Override
	public Map getStaticWindowInstances() {
		return getSuperApplication().getStaticWindowInstances();
	}

	@Override
	public Iterator getSupportedLocales() {
		return getSuperApplication().getSupportedLocales();
	}

	@Override
	public IWSystemProperties getSystemProperties() {
		return getSuperApplication().getSystemProperties();
	}

	@Override
	public String getTranslatedURIWithContext(String url) {
		return getSuperApplication().getTranslatedURIWithContext(url);
	}

	@Override
	public String getURIFromURL(String URL) {
		return getSuperApplication().getURIFromURL(URL);
	}

	@Override
	public Iterator getValidatorIds() {
		return getSuperApplication().getValidatorIds();
	}

	@Override
	public VariableResolver getVariableResolver() {
		return getSuperApplication().getVariableResolver();
	}

	@Override
	public String getVersion() {
		return getSuperApplication().getVersion();
	}

	@Override
	public ViewHandler getViewHandler() {
		return getSuperApplication().getViewHandler();
	}

	@Override
	public String getWindowOpenerURI() {
		return getSuperApplication().getWindowOpenerURI();
	}

	@Override
	public String getWindowOpenerURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		return getSuperApplication().getWindowOpenerURI(windowToOpen, ICObjectInstanceIDToOpen);
	}

	@Override
	public String getWindowOpenerURI(Class windowToOpen) {
		return getSuperApplication().getWindowOpenerURI(windowToOpen);
	}

	@Override
	public String getWindowOpenerURIWithoutContextPath() {
		return getSuperApplication().getWindowOpenerURIWithoutContextPath();
	}

	@Override
	public String getWindowOpenerURIWithoutContextPath(Class windowToOpen) {
		return getSuperApplication().getWindowOpenerURIWithoutContextPath(windowToOpen);
	}

	@Override
	public String getWorkspaceURI() {
		return getSuperApplication().getWorkspaceURI();
	}

	@Override
	public int hashCode() {
		return getSuperApplication().hashCode();
	}

	@Override
	public boolean isBuilderApplicationRunning(IWUserContext iwuc) {
		return getSuperApplication().isBuilderApplicationRunning(iwuc);
	}

	@Override
	public boolean isBundleLoaded(String bundleIdentifier) {
		return getSuperApplication().isBundleLoaded(bundleIdentifier);
	}

	@Override
	public boolean isInDatabaseLessMode() {
		return getSuperApplication().isInDatabaseLessMode();
	}

	@Override
	public boolean isInSetupMode() {
		return getSuperApplication().isInSetupMode();
	}

	@Override
	public boolean isRunningUnderRootContext() {
		return getSuperApplication().isRunningUnderRootContext();
	}

	@Override
	public void loadBundle(IWBundle bundle) {
		getSuperApplication().loadBundle(bundle);
	}

	@Override
	public void loadBundles() {
		getSuperApplication().loadBundles();
	}

	@Override
	public void loadViewManager() {
		getSuperApplication().loadViewManager();
	}

	@Override
	public void log(String p0, Throwable p1) {
		getSuperApplication().log(p0, p1);
	}

	@Override
	public void log(String p0) {
		getSuperApplication().log(p0);
	}

	@Override
	public boolean registerBundle(String bundleIdentifier, boolean autoCreate) {
		return getSuperApplication().registerBundle(bundleIdentifier, autoCreate);
	}

	@Override
	public boolean registerBundle(String bundleIdentifier, String bundlePath, boolean autoCreate) {
		return getSuperApplication().registerBundle(bundleIdentifier, bundlePath, autoCreate);
	}

	@Override
	public boolean registerBundle(String bundleIdentifier, String bundlePath) {
		return getSuperApplication().registerBundle(bundleIdentifier, bundlePath);
	}

	@Override
	public void removeAttribute(String parameterName) {
		getSuperApplication().removeAttribute(parameterName);
	}

	@Override
	public boolean restartApplication() {
		return getSuperApplication().restartApplication();
	}

	@Override
	public void setActionListener(ActionListener listener) {
		getSuperApplication().setActionListener(listener);
	}

	@Override
	public void setApplicationContextURI(String uri) {
		getSuperApplication().setApplicationContextURI(uri);
	}

	@Override
	public void setApplicationServer(AppServer appServer) {
		getSuperApplication().setApplicationServer(appServer);
	}

	@Override
	public void setAttribute(String parameterName, Object objectToStore) {
		getSuperApplication().setAttribute(parameterName, objectToStore);
	}

	@Override
	public void setDefaultDarkInterfaceColor(String color) {
		getSuperApplication().setDefaultDarkInterfaceColor(color);
	}

	@Override
	public void setDefaultLightInterfaceColor(String color) {
		getSuperApplication().setDefaultLightInterfaceColor(color);
	}

	@Override
	public void setDefaultLocale(Locale locale) {
		getSuperApplication().setDefaultLocale(locale);
	}

	@Override
	public void setDefaultRenderKitId(String renderKitId) {
		getSuperApplication().setDefaultRenderKitId(renderKitId);
	}

	@Override
	public void setInDatabaseLessMode(boolean inDatabaseLessMode) {
		getSuperApplication().setInDatabaseLessMode(inDatabaseLessMode);
	}

	@Override
	public void setInSetupMode(boolean inSetupMode) {
		getSuperApplication().setInSetupMode(inSetupMode);
	}

	@Override
	public void setMessageBundle(String bundle) {
		getSuperApplication().setMessageBundle(bundle);
	}

	@Override
	public void setNavigationHandler(NavigationHandler handler) {
		getSuperApplication().setNavigationHandler(handler);
	}

	@Override
	public void setPropertyResolver(PropertyResolver resolver) {
		getSuperApplication().setPropertyResolver(resolver);
	}

	@Override
	public void setStateManager(StateManager manager) {
		getSuperApplication().setStateManager(manager);
	}

	@Override
	public void setSupportedLocales(Collection locales) {
		getSuperApplication().setSupportedLocales(locales);
	}

	@Override
	public void setVariableResolver(VariableResolver resolver) {
		getSuperApplication().setVariableResolver(resolver);
	}

	@Override
	public void setViewHandler(ViewHandler handler) {
		getSuperApplication().setViewHandler(handler);
	}

	@Override
	public void startAccessController() {
		getSuperApplication().startAccessController();
	}

	public void startFileSystem() {
		getSuperApplication().startFileSystem(true);
	}

	@Override
	public void storeStatus() {
		getSuperApplication().storeStatus();
	}

	@Override
	public String toString() {
		return getSuperApplication().toString();
	}

	@Override
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
