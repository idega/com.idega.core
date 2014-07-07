package com.idega.business;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.data.ExplicitlySynchronizedEntity;

@Service(RequestSynchronizer.BEAN_NAME)
@Scope("request")
public class RequestSynchronizer {
	public static final String BEAN_NAME = "requestSynchronizer";
	
	private Collection<ExplicitlySynchronizedEntity> synchronizationEntities;
	
	@Autowired
	private ExplicitSynchronizationBusiness explicitSynchronizationBusiness;

	public Collection<ExplicitlySynchronizedEntity> getSynchronizerKeys() {
		if(synchronizationEntities != null){
			return synchronizationEntities;
		}
		synchronizationEntities = new ArrayList<ExplicitlySynchronizedEntity>();
		return synchronizationEntities;
	}

	public void addEntityToSyncronize(ExplicitlySynchronizedEntity entity){
		getSynchronizerKeys().add(entity);
	}
	
	@SuppressWarnings("unused")
	@PreDestroy
	private void activateSynchronization(){
		explicitSynchronizationBusiness.activateSynchronization(synchronizationEntities);
		synchronizationEntities = null;
	}
	
}
