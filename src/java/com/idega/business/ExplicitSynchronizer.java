package com.idega.business;

import java.util.Collection;
import java.util.Map;

import com.idega.data.ExplicitlySynchronizedEntity;

public interface ExplicitSynchronizer {
	public void executeSynchronization(Collection<ExplicitlySynchronizedEntity> entities,Map<String, Object> properties);
}
