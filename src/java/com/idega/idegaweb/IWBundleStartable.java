package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IWBundleStartable{
	
	String DEFAULT_STARTER_CLASS = "IWBundleStarter";
	
	void start(IWBundle starterBundle);
	void stop(IWBundle starterBundle);

}
