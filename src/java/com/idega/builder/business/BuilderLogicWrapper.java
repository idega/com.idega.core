package com.idega.builder.business;

import com.idega.core.builder.business.BuilderService;
import com.idega.idegaweb.IWApplicationContext;

public interface BuilderLogicWrapper {

	public BuilderService getBuilderService(IWApplicationContext iwac);
	
	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName);
}
