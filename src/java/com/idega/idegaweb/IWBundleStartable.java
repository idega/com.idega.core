/*
 * $Id: IWBundleStartable.java,v 1.5 2004/11/02 18:46:41 tryggvil Exp $
 * 
 * Created in 2001 by Tryggvi Larusson
 *
 * Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;


/**
 * This is a class to hook into the start and stop mechanism of each bundle.<br>
 * The simplest way to do this is to create a class named IWBundleStarter that implements
 * this interface and is in the package named the same as the bundle identifier, then
 * the DefaultIWBundle class sees to it that the class is instanciated and the methods start
 * and stop called on the startup and shutdown of the application.
 * 
 * Last modified: $Date: 2004/11/02 18:46:41 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
public interface IWBundleStartable{
	
	String DEFAULT_STARTER_CLASS = "IWBundleStarter";
	
	void start(IWBundle starterBundle);
	void stop(IWBundle starterBundle);

}
