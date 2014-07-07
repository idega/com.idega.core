package com.idega.business;

import java.util.Collection;

import com.idega.data.ExplicitlySynchronizedEntity;

public interface ExplicitSynchronizationBusiness {
	public static final String BEAN_NAME = "explicitSynchronizationBusiness";
	public void synchronize(ExplicitlySynchronizedEntity entity);
	public void activateSynchronization(Collection<ExplicitlySynchronizedEntity> entities);
	public void addSynchronizer(String key,ExplicitSynchronizer synchronizer);
}
