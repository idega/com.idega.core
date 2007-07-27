package com.idega.core.view;

/**
 * 
 *  Last modified: $Date: 2007/07/27 15:44:02 $ by $Author: civilis $
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 */
public enum ViewNodeBase {
	
	UNSPECIFIED {public String extension() {throw new UnsupportedOperationException("Unspecified view node don't have an extension");}},
	JSP {public String extension() {return ".jsp";}},
	FACELET {public String extension() {return ".xhtml";}},
	COMPONENT {public String extension() {throw new UnsupportedOperationException("Component based view node don't have an extension");}};
	
	public abstract String extension();
}