package com.idega.core.view;

/**
 * 
 *  Last modified: $Date: 2007/08/23 13:22:20 $ by $Author: valdas $
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 */
public enum ViewNodeBase {
	
	UNSPECIFIED {public String extension() {throw new UnsupportedOperationException("Unspecified view node don't have an extension");}},
	JSP {public String extension() {return ".jsp";}},
	FACELET {public String extension() {return ".xhtml";}},
	ICEFACE {public String extension() {return ".iface";}},
	COMPONENT {public String extension() {throw new UnsupportedOperationException("Component based view node don't have an extension");}};
	
	public abstract String extension();
}