package com.idega.util;

import java.util.Collection;
import java.util.List;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;

import com.idega.presentation.IWContext;

public class PresentationUtil {
	
	public static boolean addJavaScriptSourceLineToHeader(IWContext iwc, String scriptUri) {
		if (iwc == null || scriptUri == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		return addJavaScriptSourceLineToHeader(iwc, adder, scriptUri);
	}
	
	public static boolean addJavaScriptSourcesLinesToHeader(IWContext iwc, List<String> scriptsUris) {
		if (iwc == null || scriptsUris == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		for (int i = 0; i < scriptsUris.size(); i++) {
			addJavaScriptSourceLineToHeader(iwc, adder, scriptsUris.get(i));
		}
		
		return true;
	}
	
	private static boolean addJavaScriptSourceLineToHeader(IWContext iwc, AddResource adder, String scriptUri) {
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, scriptUri);
		return true;
	}
	
	public static boolean addJavaScriptActionToBody(IWContext iwc, String action) {
		if (iwc == null || action == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		return addJavaScriptActionToBody(iwc, adder, action);
	}
	
	public static boolean addJavaScriptActionsToBody(IWContext iwc, List<String> actions) {
		if (iwc == null || actions == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		for (int i = 0; i < actions.size(); i++) {
			addJavaScriptActionToBody(iwc, adder, actions.get(i));
		}
		
		return true;
	}
	
	private static boolean addJavaScriptActionToBody(IWContext iwc, AddResource adder, String action) {
		adder.addInlineScriptAtPosition(iwc, AddResource.BODY_END, action);
		return true;
	}
	
	public static String getJavaScriptSourceLine(String scriptUri) {
		
		return getJavaScriptSourceLine(scriptUri, false);
	}
	
	public static String getJavaScriptSourceLine(String scriptUri, boolean includeOnce) {
		
		if (scriptUri == null) {
			return null;
		}
		
		StringBuffer script;
		
		if(includeOnce) {
			script = new StringBuffer("<script type=\"text/javascript\"> IWCORE.includeScript('")
			.append(scriptUri).append("');</script>\n");
			//IWCORE.includeScript(pathtojsfiles+modules[i].incfile);
			
		} else {
			
			script = new StringBuffer("<script type=\"text/javascript\" src=\"").append(scriptUri).append("\"></script>\n");
		}
		
		return script.toString();
	}
	
	public static String getJavaScriptSourceLines(List<String> scriptsUris) {
		if (scriptsUris == null) {
			return null;
		}
		
		StringBuffer scripts = new StringBuffer();
		for (int i = 0; i < scriptsUris.size(); i++) {
			scripts.append(getJavaScriptSourceLine(scriptsUris.get(i)));
		}
		
		return scripts.toString();
	}
	
	public static String getJavaScriptSourceLinesIncludeOnce(Collection<String> scriptsUris) {
		
		if (scriptsUris == null) {
			return null;
		}
		
		StringBuffer scripts = new StringBuffer();
		
		for (String uri : scriptsUris) {
			
			scripts.append(getJavaScriptSourceLine(uri, true));
		}
		
		return scripts.toString();
	}
	
	public static String getJavaScriptAction(String action) {
		if (action == null) {
			return null;
		}
		
		return new StringBuffer("<script type=\"text/javascript\">").append(action).append("</script>\n").toString();
	}
	
	public static String getJavaScriptActions(List<String> actions) {
		if (actions == null) {
			return null;
		}
		
		StringBuffer allActions = new StringBuffer();
		for (int i = 0; i < actions.size(); i++) {
			allActions.append(getJavaScriptAction(actions.get(i)));
		}
		
		return allActions.toString();
	}
	
	public static boolean addStyleSheetToHeader(IWContext iwc, String styleSheetUri) {
		if (iwc == null || styleSheetUri == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		return addStyleSheetToHeader(iwc, adder, styleSheetUri);
	}
	
	public static boolean addStyleSheetsToHeader(IWContext iwc, List<String> styleSheetsUris) {
		if (iwc == null || styleSheetsUris == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		for (int i = 0; i < styleSheetsUris.size(); i++) {
			addStyleSheetToHeader(iwc, adder, styleSheetsUris.get(i));
		}
		
		return true;
	}
	
	private static boolean addStyleSheetToHeader(IWContext iwc, AddResource adder, String styleSheetUri) {
		adder.addStyleSheet(iwc, AddResource.HEADER_BEGIN, styleSheetUri);
		return true;
	}
	
	public static String getStyleSheetSourceLine(String styleSheetUri) {
		if (styleSheetUri == null) {
			return null;
		}
		
		StringBuffer style = new StringBuffer("<link type=\"text/css\" href=\"").append(styleSheetUri).append("\" rel=\"stylesheet\" media=\"screen\"/>\n");
		return style.toString();
	}
	
	public static String getCssLine(String cssUri, boolean includeOnce) {
		
		if (cssUri == null) {
			return null;
		}
		
		StringBuffer css;
		
		if(includeOnce) {
			css = new StringBuffer("<script type=\"text/javascript\">IWCORE.includeCss('")
			.append(cssUri).append("');</script>\n");
			//IWCORE.includeScript(pathtojsfiles+modules[i].incfile);
			
		} else {
			
			css = new StringBuffer("<link type=\"text/css\" href=\"").append(cssUri).append("\" rel=\"stylesheet\" media=\"screen\"/>\n");
		}
		
		return css.toString();
	}
	
	public static String getStyleSheetsSourceLines(List<String> styleSheetsUris) {
		if (styleSheetsUris == null) {
			return null;
		}
		
		StringBuffer styles = new StringBuffer();
		for (int i = 0; i < styleSheetsUris.size(); i++) {
			styles.append(getStyleSheetSourceLine(styleSheetsUris.get(i)));
		}
		
		return styles.toString();
	}
	
	public static String getStyleSheetsSourceLinesIncludeOnce(Collection<String> styleSheetsUris) {
		
		if (styleSheetsUris == null) {
			return null;
		}
		
		StringBuffer styles = new StringBuffer();
		
		for (String uri : styleSheetsUris) {
			styles.append(getCssLine(uri, true));
		}
		
		return styles.toString();
	}
}
