package com.idega.business.file;

import java.io.Serializable;
import java.util.Collection;

import com.idega.business.SpringBeanName;
import com.idega.user.data.User;

@SpringBeanName("fileStatisticsProvider")
public interface FileStatisticsProvider extends Serializable {

	public abstract Collection<User> getPotentialDownloaders(String fileHolderIdentifier);
	
}
