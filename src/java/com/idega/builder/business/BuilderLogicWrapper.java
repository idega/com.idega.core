package com.idega.builder.business;

import com.idega.core.builder.business.BuilderService;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.CoreConstants;

/**
 * Wrapper bean of {@link BuilderLogic} instance. Bean id: {@link CoreConstants.SPRING_BEAN_NAME_BUILDER_LOGIC_WRAPPER}
 * @author valdas
 *
 */

public interface BuilderLogicWrapper {

	public static final String SPRING_BEAN_NAME_BUILDER_LOGIC_WRAPPER = "builderLogicWrapperBeanId";

	public BuilderService getBuilderService(IWApplicationContext iwac);
	
	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName);
}
