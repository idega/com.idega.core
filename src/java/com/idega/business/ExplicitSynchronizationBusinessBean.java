package com.idega.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.data.ExplicitlySynchronizedEntity;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

@Service(ExplicitSynchronizationBusiness.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ExplicitSynchronizationBusinessBean implements ExplicitSynchronizationBusiness{
	private Map<String, Collection<SynchronizerWraper>> synchronizersEntityMap;
	
	@Override
	public void synchronize(ExplicitlySynchronizedEntity entity){
		try{
			RequestSynchronizer rs = ELUtil.getInstance().getBean(RequestSynchronizer.BEAN_NAME);
			rs.addEntityToSyncronize(entity);
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Non request synchronization is not implemented yet", e);
		}
	}
	private Logger getLogger(){
		return Logger.getLogger(ExplicitSynchronizationBusinessBean.class.getName());
	}
	@Override
	public void activateSynchronization(Collection<ExplicitlySynchronizedEntity> entities) {
		activateSynchronization(entities, new HashMap<String, Object>());
	}
	@Override
	public void activateSynchronization(Collection<ExplicitlySynchronizedEntity> entities,Map<String, Object> properties) {
		if(ListUtil.isEmpty(entities)){
			return;
		}
		if(synchronizersEntityMap == null){
			return;
		}
		Collection<SynchronizerWraper> synchronizersToExecute = new HashSet<SynchronizerWraper>();
		for(ExplicitlySynchronizedEntity entity : entities){
			Collection<SynchronizerWraper> synchronizers = synchronizersEntityMap.get(entity.getSynchronizerKey());
			if(ListUtil.isEmpty(synchronizers)){
				continue;
			}
			for(SynchronizerWraper synchronizer : synchronizers){
				synchronizer.synchronizer.addEntity(entity);
				synchronizersToExecute.add(synchronizer);
			}
		}
		for(SynchronizerWraper wraper : synchronizersToExecute){
			try{
				wraper.synchronizer.setProperties(properties);
				wraper.synchronizer.executeSynchronization();
			}catch (Exception e) {
				getLogger().log(Level.WARNING, "Synchronizer " + wraper.synchronizer +" failed!", e);
			}
		}
	}
	private Map<String, Collection<SynchronizerWraper>> getSynchronizersEntityMap() {
		if(synchronizersEntityMap != null){
			return synchronizersEntityMap;
		}
		synchronizersEntityMap = new HashMap<String, Collection<SynchronizerWraper>>();
		return synchronizersEntityMap;
	}
	@Override
	public void addSynchronizer(String key,ExplicitSynchronizer synchronizer){
		Map<String, Collection<SynchronizerWraper>> synchronizersEntityMap = getSynchronizersEntityMap();
		Collection<SynchronizerWraper> synchronizers = synchronizersEntityMap.get(key);
		if(synchronizers == null){
			synchronizers = new HashSet<SynchronizerWraper>();
		}
		synchronizers.add(new SynchronizerWraper(synchronizer));
		synchronizersEntityMap.put(key, synchronizers);
	}
	private class SynchronizerWraper {
		public ExplicitSynchronizer synchronizer;
		public Object equalKey;
		public SynchronizerWraper(ExplicitSynchronizer synchronizer){
			this.synchronizer = synchronizer;
			equalKey = synchronizer.getClass();
		}
		
		public boolean equals(SynchronizerWraper synchronizerWraper) {
			return synchronizerWraper.equalKey.equals(equalKey);
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof SynchronizerWraper){
				return equals((SynchronizerWraper)obj);
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return equalKey.hashCode();
		}
		
		
	}
}
