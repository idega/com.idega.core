package com.idega.core.builder.business;

import java.rmi.RemoteException;

import com.idega.business.IBOService;
import com.idega.core.builder.data.ICPage;
import com.idega.io.serialization.ObjectWriter;
import com.idega.presentation.IWContext;

public interface BuilderPageWriterService extends IBOService {

	public Object write(ICPage page, ObjectWriter writer, IWContext iwc) throws RemoteException;
}
