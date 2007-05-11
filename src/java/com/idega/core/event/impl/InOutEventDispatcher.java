/*
 * $Id: InOutEventDispatcher.java,v 1.3 2007/05/11 13:32:57 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;


/**
 * 
 *  Last modified: $Date: 2007/05/11 13:32:57 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class InOutEventDispatcher implements Singleton{
	
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new InOutEventDispatcher();}};

	public static InOutEventDispatcher getInstance() {
		return (InOutEventDispatcher) SingletonRepository.getRepository().getInstance(InOutEventDispatcher.class,instantiator);
	}

	
	private InEventDispatcher inEventDispatcher = null;
	private OutEventDispatcher outEventDispatcher = null;
	
	protected InOutEventDispatcher() {
		// create an identifier for the application. Does not matter if the id changes when restarting
		String applicationId = UUIDGenerator.getInstance().generateUUID();
		outEventDispatcher = new OutEventDispatcher(applicationId);
		inEventDispatcher = new InEventDispatcher(outEventDispatcher, applicationId);
	}
	
	public MethodCallEventDispatcher getInEventDispatcher() {
		return inEventDispatcher;
	}
	
	public MethodCallEventDispatcher getOutEventDispatcher() {
		return outEventDispatcher;
	}

}
