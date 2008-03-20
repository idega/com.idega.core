package com.idega.util;

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
	
	public static boolean addJavaScriptSourcesLinesToHeader(IWContext iwc, List scriptsUris) {
		if (iwc == null || scriptsUris == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		for (int i = 0; i < scriptsUris.size(); i++) {
			addJavaScriptSourceLineToHeader(iwc, adder, scriptsUris.get(i).toString());
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
	
	public static boolean addJavaScriptActionsToBody(IWContext iwc, List actions) {
		if (iwc == null || actions == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		for (int i = 0; i < actions.size(); i++) {
			addJavaScriptActionToBody(iwc, adder, actions.get(i).toString());
		}
		
		return true;
	}
	
	private static boolean addJavaScriptActionToBody(IWContext iwc, AddResource adder, String action) {
		adder.addInlineScriptAtPosition(iwc, AddResource.BODY_END, action);
		return true;
	}
	
	public static String getJavaScriptSourceLine(String scriptUri) {
		if (scriptUri == null) {
			return null;
		}
		
		StringBuffer script = new StringBuffer("<script type=\"text/javascript\" src=\"").append(scriptUri).append("\"></script>\n");
		return script.toString();
	}
	
	public static String getJavaScriptSourceLines(List scriptsUris) {
		if (scriptsUris == null) {
			return null;
		}
		
		StringBuffer scripts = new StringBuffer();
		for (int i = 0; i < scriptsUris.size(); i++) {
			scripts.append(getJavaScriptSourceLine(scriptsUris.get(i).toString()));
		}
		
		return scripts.toString();
	}
	
	public static String getJavaScriptAction(String action) {
		if (action == null) {
			return null;
		}
		
		return new StringBuffer("<script type=\"text/javascript\">").append(action).append("</script>\n").toString();
	}
	
	public static String getJavaScriptActions(List actions) {
		if (actions == null) {
			return null;
		}
		
		StringBuffer allActions = new StringBuffer();
		for (int i = 0; i < actions.size(); i++) {
			allActions.append(getJavaScriptAction(actions.get(i).toString()));
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
	
	public static boolean addStyleSheetsToHeader(IWContext iwc, List styleSheetsUris) {
		if (iwc == null || styleSheetsUris == null) {
			return false;
		}
		
		AddResource adder = AddResourceFactory.getInstance(iwc);
		for (int i = 0; i < styleSheetsUris.size(); i++) {
			addStyleSheetToHeader(iwc, adder, styleSheetsUris.get(i).toString());
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
	
	public static String getStyleSheetsSourceLines(List styleSheetsUris) {
		if (styleSheetsUris == null) {
			return null;
		}
		
		StringBuffer styles = new StringBuffer();
		for (int i = 0; i < styleSheetsUris.size(); i++) {
			styles.append(getStyleSheetSourceLine(styleSheetsUris.get(i).toString()));
		}
		
		return styles.toString();
	}
}
