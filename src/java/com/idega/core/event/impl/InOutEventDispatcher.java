/*
 * $Id: InOutEventDispatcher.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class InOutEventDispatcher implements Singleton{
	
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new InOutEventDispatcher();}};

	public static InOutEventDispatcher getInstance() {
		return (InOutEventDispatcher) SingletonRepository.getRepository().getInstance(InOutEventDispatcher.class,instantiator);
	}

	
	private InEventDispatcher inEventDispatcher = null;
	private OutEventDispatcher outEventDispatcher = null;
	
	protected InOutEventDispatcher() {
		outEventDispatcher = new OutEventDispatcher();
		inEventDispatcher = new InEventDispatcher(outEventDispatcher);
	}
	
	public MethodCallEventDispatcher getInEventDispatcher() {
		return inEventDispatcher;
	}
	
	public MethodCallEventDispatcher getOutEventDispatcher() {
		return outEventDispatcher;
	}

}
