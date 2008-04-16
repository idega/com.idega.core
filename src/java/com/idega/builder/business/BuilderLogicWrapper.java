package com.idega.builder.business;

import com.idega.business.SpringBeanName;
import com.idega.core.builder.business.BuilderService;
import com.idega.idegaweb.IWApplicationContext;

@SpringBeanName(BuilderLogicWrapper.BUILDER_LOGIC_WRAPPER_BEAN_ID)
public interface BuilderLogicWrapper {
	
	public static final String BUILDER_LOGIC_WRAPPER_BEAN_ID = "builderLogicWrapperBeanId";

	public BuilderService getBuilderService(IWApplicationContext iwac);
	
	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName);
}
