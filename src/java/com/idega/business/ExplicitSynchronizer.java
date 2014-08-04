package com.idega.business;

import java.util.Map;

import com.idega.data.ExplicitlySynchronizedEntity;

public interface ExplicitSynchronizer {
	public void addEntity(ExplicitlySynchronizedEntity entity);
	public void setProperties(Map<String, Object> properties);
	public void executeSynchronization();
}
