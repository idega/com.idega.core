package com.idega.data;

import java.util.Collection;

public interface ExplicitlySynchronizedEntity {

	public String getSynchronizerKey();

	public ExplicitlySynchronizedEntity getSimpleExplicitlySynchronizedEntityEntity();

	public String getPK();

	public Collection<String> getChanges();

	public void setSynchronizationEnabled(boolean enabled);

	public boolean isSynchronizationEnabled();

}