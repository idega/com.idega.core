package com.idega.io;

import java.io.File;
import java.rmi.RemoteException;

import com.idega.io.serialization.*;
import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UploadFile extends File implements Storable {

  private String _sName,_sMimeType,_sRealPath, _sWebPath;
  private long lSize;
  int _fileDBId = -1;

  public UploadFile(String name, String realpath, String webpath, String mimetype, long size) {
    super(realpath);
    this.setRealPath(realpath);
    this.setWebPath(webpath);
    this.setMimeType(mimetype);
    this.setSize(size);
    this.setName(name);
  }

  public String getFileName(){
    return super.getName();
  }

  public String getName(){
    return _sName;
  }

  public void setName(String name){
    _sName = name;
  }

  public String getMimeType(){
    return _sMimeType;
  }

  public void setMimeType(String mimetype){
    _sMimeType = mimetype;
  }

  public String getRealPath(){
    return _sRealPath;
  }

  public void setRealPath(String realPath){
    _sRealPath = realPath;
  }

  public String getWebPath(){
    return _sWebPath;
  }

  public void setWebPath(String webPath){
    _sWebPath = webPath;
  }

  public long getSize(){
    return lSize;
  }

  public void setSize(long size){
    lSize = size;
  }

  public void setId(int fileId){
    _fileDBId = fileId;
  }

  public int getId(){
    return _fileDBId;
  }

	/* (non-Javadoc)
	 * @see com.idega.io.IBStorable#write(com.idega.io.ObjectWriter)
	 */
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
		return writer.write(this, iwc);
	}

	/* (non-Javadoc)
	 * @see com.idega.io.IBStorable#read(com.idega.io.IBObjectReader)
	 */
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
		return reader.read(this, iwc);
	}




}
