/*
 * Created on Jan 11, 2004
 *
 */
package com.idega.core.component.business;

import com.idega.core.component.data.ICObject;
import com.idega.idegaweb.IWBundle;

/**
 * BundleObjectRegisterable allows custom registration of objects when they have been added to bundle and database. 
 * @author aron 
 * @version 1.0
 */
public interface BundleRegistrationListener {
	/**
	 *  Registers the object in it 
	 * @param bundle
	 * @param ico
	 * @throws RegisterException
	 */
	public void registerInBundle(IWBundle bundle,ICObject ico) throws RegisterException;
}
