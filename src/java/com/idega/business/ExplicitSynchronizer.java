package com.idega.business;

import com.idega.data.ExplicitlySynchronizedEntity;

public interface ExplicitSynchronizer {
	public void addEntity(ExplicitlySynchronizedEntity entity);
	public void executeSynchronization();
}
