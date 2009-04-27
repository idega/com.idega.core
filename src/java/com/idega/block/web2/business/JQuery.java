package com.idega.block.web2.business;

import java.util.List;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $ Last modified: $Date: 2009/04/27 12:50:50 $ by $Author: valdas $
 */
public interface JQuery {
	
	public abstract String getBundleURIToJQueryLib();
	
	public abstract String getBundleURIToJQueryLib(String jqueryLibraryVersion);
	
	public abstract String getBundleURIToJQueryUILib(JQueryUIType type);
	
	public abstract String getBundleURIToJQueryUILib(
	        String jqueryUILibraryVersion, String fileName);
	
	public abstract String getBundleURIToJQueryPlugin(JQueryPlugin plugin);
	
	public abstract List<String> getBundleURISToValidation();
	public abstract List<String> getBundleURISToValidation(boolean addAdditionalMethods);
}