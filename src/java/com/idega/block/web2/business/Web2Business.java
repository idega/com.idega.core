package com.idega.block.web2.business;


import java.rmi.RemoteException;
import java.util.List;

import com.idega.business.IBOService;
import com.idega.business.SpringBeanName;
import com.idega.presentation.IWContext;

@SpringBeanName(Web2Business.SPRING_BEAN_IDENTIFIER)
public interface Web2Business extends IBOService {
	
	public static final String SPRING_BEAN_IDENTIFIER = "web2bean";
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#addTablesorterScriptFilesToPage
	 */
	public void addTablesorterScriptFilesToPage(IWContext iwc, String className, String theme);
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#addWebAppFilesToPage
	 */
	public void addWebAppFilesToPage(IWContext iwc);
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToBehaviourLib
	 */
	public String getBundleURIToBehaviourLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToSoundManager2Lib
	 */
	public String getBundleURIToSoundManager2Lib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToSoundManager2FlashFile
	 */
	public String getBundleURIToSoundManager2FlashFile() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToSoundManager2TestSoundFile
	 */
	public String getBundleURIToSoundManager2TestSoundFile();

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToReflectionLib
	 */
	public String getBundleURIToReflectionLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToPrototypeLib
	 */
	public String getBundleURIToPrototypeLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToPrototypeLib
	 */
	public String getBundleURIToPrototypeLib(String scriptaculousLibraryVersion) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToDojoLib
	 */
	public String getBundleURIToDojoLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToScriptaculousLib
	 */
	public String getBundleURIToScriptaculousLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToScriptaculousLib
	 */
	public String getBundleURIToScriptaculousLib(String scriptaculousLibraryVersion) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToScriptaculousLibRootFolder
	 */
	public String getBundleURIToScriptaculousLibRootFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToScriptaculousLibRootFolder
	 */
	public String getBundleURIToScriptaculousLibRootFolder(String scriptaculousLibraryVersion) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMootoolsLib
	 */
	public String getBundleURIToMootoolsLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMootoolsLib
	 */
	public String getBundleURIToMootoolsLib(String mootoolsLibraryVersion) throws RemoteException;
	public List<String> getBundleURIsToMooToolsLib(String version, boolean needCompressedFiles, boolean needMooToolsMore, boolean addBridgingScript);
	public List<String> getBundleURIsToMooToolsLib(boolean needCompressedFiles, boolean needMooToolsMore, boolean addBridgingScript);
	public List<String> getBundleURIsToMooToolsLib();
	
	/**
	 * @deprecated use getJQuery().getBundleURIToJQueryLib
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToJQueryLib
	 */
	@Deprecated
	public String getBundleURIToJQueryLib();

	/**
	 * @deprecated use getJQuery().getBundleURIToJQueryLib
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToJQueryLib
	 */
	@Deprecated
	public String getBundleURIToJQueryLib(String jqueryLibraryVersion) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMootoolsStyleFile
	 */
	public String getBundleURIToMootoolsStyleFile() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToScriptsFolder
	 */
	public String getBundleURIToScriptsFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToLibsFolder
	 */
	public String getBundleURIToLibsFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIWithinLibsFolder
	 */
	public String getBundleURIWithinLibsFolder(String uriExtension) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIWithinScriptsFolder
	 */
	public String getBundleURIWithinScriptsFolder(String uriExtension) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleIdentifier
	 */
	public String getBundleIdentifier() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToRicoLib
	 */
	public String getBundleURIToRicoLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToRico
	 */
	public String getBundleURIToRico() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToJMakiWidgetsFolder
	 */
	public String getBundleURIToJMakiWidgetsFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToJMakiLib
	 */
	public String getBundleURIToJMakiLib() throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleUriToInlineEditScript
	 */
	public String getBundleUriToInlineEditScript() throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleUriToSmoothboxScript
	 */
	public String getBundleUriToSmoothboxScript() throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleUriToSmoothboxStylesheet
	 */
	public String getBundleUriToSmoothboxStylesheet() throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleUriToInlineEditScript
	 */
	public String getBundleUriToInlineEditScript(String version) throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getInlineEditScriptPath
	 */
	public String getInlineEditScriptPath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToLightboxLibRootFolder
	 */
	public String getBundleURIToLightboxLibRootFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToLightboxLibRootFolder
	 */
	public String getBundleURIToLightboxLibRootFolder(String versionNumber) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getLightboxImagesPath
	 */
	public String getLightboxImagesPath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getLightboxScriptPath
	 */
	public String getLightboxScriptPath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getLightboxStylePath
	 */
	public String getLightboxStylePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getLightboxScriptFilePath
	 */
	public String getLightboxScriptFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getLightboxStyleFilePath
	 */
	public String getLightboxStyleFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToThickboxLibRootFolder
	 */
	public String getBundleURIToThickboxLibRootFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToThickboxLibRootFolder
	 */
	public String getBundleURIToThickboxLibRootFolder(String versionNumber) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getThickboxScriptPath
	 */
	public String getThickboxScriptPath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getThickboxStylePath
	 */
	public String getThickboxStylePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getThickboxScriptFilePath
	 */
	public String getThickboxScriptFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getThickboxStyleFilePath
	 */
	public String getThickboxStyleFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getPrototypeScriptFilePath
	 */
	public String getPrototypeScriptFilePath(String version) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToControlModalLib
	 */
	public String getBundleURIToControlModalLib() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getNiftyCubeScriptFilePath
	 */
	public String getNiftyCubeScriptFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getMootoolsBasedBehaviourScriptFilePath
	 */
	public String getMootoolsBasedBehaviourScriptFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getMoodalboxScriptFilePath
	 */
	public String getMoodalboxScriptFilePath(boolean needFullScript) throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getMoodalboxStyleFilePath
	 */
	public String getMoodalboxStyleFilePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getMoodalboxStylePath
	 */
	public String getMoodalboxStylePath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getMoodalboxScriptPath
	 */
	public String getMoodalboxScriptPath() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMoodalboxLibRootFolder
	 */
	public String getBundleURIToMoodalboxLibRootFolder() throws RemoteException;

	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMoodalboxLibRootFolder
	 */
	public String getBundleURIToMoodalboxLibRootFolder(String versionNumber) throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getTranscornersScriptFilePath
	 */
	public String getTranscornersScriptFilePath();
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getReflectionForMootoolsScriptFilePath
	 */
	public String getReflectionForMootoolsScriptFilePath();
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getCodePressScriptFilePath
	 */
	public String getCodePressScriptFilePath();
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMootoolsLib
	 */
	public String getBundleURIToMootoolsLib(boolean needFullScript) throws RemoteException;
	
	/**
	 * @see com.idega.block.web2.business.Web2BusinessBean#getBundleURIToMootoolsLib
	 */
	public String getBundleURIToMootoolsLib(String mootoolsLibraryVersion, boolean needFullScript) throws RemoteException;
	
	public String getBundleURIToYUIScript(String version, boolean needFullScript);
	
	public String getBundleURIToYUIScript(boolean needFullScript);
	
	public String getBundleURIToYUIScript();
	
	public String getBundleUriToMootabsScript(String version);
	
	public String getBundleUriToMootabsScript();
	
	public String getBundleUriToMootabsStyle(String version);
	
	public String getBundleUriToMootabsStyle();
	
	public String getBundleUriToMooRainbowScript(String version);
	
	public String getBundleUriToMooRainbowScript();
	
	public String getBundleUriToMooRainbowStyle(String version);
	
	public String getBundleUriToMooRainbowStyle();
	
	public String getSlimboxScriptFilePath();
	public String getSlimboxStyleFilePath();
	
	/**
	 * @deprecated use getJQuery().getBundleURIToJQueryUILib
	 * @param type
	 * @return
	 */
	@Deprecated
	public String getBundleURIToJQueryUILib(JQueryUIType type);
	
	/**
	 * @deprecated use getJQuery().getBundleURIToJQueryUILib
	 * @param type
	 * @return
	 */
	@Deprecated
	public String getBundleURIToJQueryUILib(String jqueryUILibraryVersion, String fileName);
	
	public String getBundleURIToJQGrid();
	
	public String getBundleURIToJQGridStyles();
	
	//	ContextMenu
	public String getBundleUriToContextMenuScript(String version, boolean compressedFile);
	public String getBundleUriToContextMenuScript(boolean compressedFile);
	public String getBundleUriToContextMenuScript();
	
	//	Humanized messages
	public String getBundleUriToHumanizedMessagesScript();
	public String getBundleUriToHumanizedMessagesStyleSheet();
	
	//	GreyBox
	public String getBundleUtiToGreyBoxScript();
	public String getBundleUtiToGreyBoxStyleSheet();

	/**
	 * @deprecated use getJQuery().getBundleURIToJQueryPlugin
	 * @param plugin
	 * @return
	 */
	@Deprecated
	public String getBundleURIToJQueryPlugin(JQueryPlugin plugin);
	
	//	jsTree (based on jQuery)
	public List<String> getBundleURIsToJSTreeScriptFiles();
	public List<String> getBundleURIsToJSTreeScriptFiles(boolean usesXmlDataTypes, boolean usesMetadataRules, boolean usesCookies);
	public String getBundleURIToJSTreeStyleFile();
	
	//	SexyLightBox (based on MooTools)
	public List<String> getBundleURIsToSexyLightBoxScriptFiles();
	public List<String> getBundleURIsToSexyLightBoxScriptFiles(boolean useCompressedScript);
	public String getBundleURIToSexyLightBoxStyleFile();
	public String getSexyLightBoxInitAction(IWContext iwc, String variableName);
	public void insertSexyLightBoxIntoPage(IWContext iwc);
	public String getSexyLightBoxVariableName();
	
	//	http://browserplus.yahoo.com/
	public String getBrowserPlusScriptFile();
	
	//	FancyBox
	public List<String> getBundleURIsToFancyBoxScriptFiles();
	public String getBundleURIToFancyBoxStyleFile();
	public String getBundleURIToFancyBoxStyleFile(String version);
	public List<String> getBundleURIsToFancyBoxScriptFiles(String version);

	//	CodeMirror
	public String getBundleURIToCodeMirrorScriptFile();
	public String getBundleURIToCodeMirrorScriptFile(String scriptFile);
	public String getBundleURIToCodeMirrorStyleFile(String styleFile);
	public String getBundleURIToCodeMirrorFolder();
	
	public String getBundleUriToLinkLinksWithFilesScriptFile();
	public String getActionToLinkLinksWithFiles(String containerId, boolean executeOnLoad, boolean addStyleForNonFileLinks);
	public String getBundleUriToLinkLinksWithFilesStyleFile();
	
	public abstract JQuery getJQuery();
	
	//	Gritter
	public String getBundleUriToGritterStyleSheet();
	public String getBundleUriToGritterScriptFile();
	
	//	TinyMCE
	public List<String> getScriptsForTinyMCE();
	public List<String> getScriptsForTinyMCE(String version);
	
	//	JCaptcha
	public boolean validateJCaptcha(String sessionId, String userCaptchaResponse);
	public String getJCaptchaImageURL();
	
	//	SWF Upload
	public String getSWFUploadObjectScript();
	public String getSWFUploadScript();
	public String getSWFUploadPlugin();
}