package com.idega.io.export;

import java.rmi.RemoteException;

import com.idega.builder.data.IBExportImportData;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.data.ICFile;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.util.xml.XMLData;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 31, 2004
 */
public interface ObjectReader {
	
	public Object read(XMLData xmlData, IWContext iwc) throws RemoteException;
	
	public Object read(ICFile file, IWContext iwc) throws RemoteException;
	
	public Object read(IBExportImportData metadata, IWContext iwc) throws RemoteException;
	
	public Object read(ICPage page, IWContext iwc) throws RemoteException;
	
	public Object read(UploadFile file, IWContext iwc) throws RemoteException;

	
}
