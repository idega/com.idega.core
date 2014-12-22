package com.idega.business;

import java.util.Collection;
import java.util.Map;

import com.idega.data.ExplicitlySynchronizedEntity;

public interface ExplicitSynchronizationBusiness {

	public static final String BEAN_NAME = "explicitSynchronizationBusiness";

	public void synchronize(ExplicitlySynchronizedEntity entity);

	public void activateSynchronization(Collection<ExplicitlySynchronizedEntity> entities);

	public void activateSynchronization(Collection<ExplicitlySynchronizedEntity> entities,Map<String, Object> properties);

	public void addSynchronizer(String key,ExplicitSynchronizer synchronizer);

}