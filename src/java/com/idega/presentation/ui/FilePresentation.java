package com.idega.presentation.ui;

import com.idega.presentation.text.Link;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.idegaweb.IWMainApplication;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class FilePresentation extends Link {

  public FilePresentation() {
  }

  public void setFileID(int file_id){
    this.setURL(getFileURL(file_id));
  }

public static String getFileURL(int file_id){
  return IWMainApplication.FILE_SERVLET_URL+"?file"+file_id+"&file_id="+file_id;
}

//for files
public FilePresentation(int file_id){
	//this(new Text("File"),"/servlet/FileModule?file_id="+file_id);
            super(new Text("File"),getFileURL(file_id));
}

public FilePresentation(int file_id,String fileName){
        //this(new Text(file_name),"/servlet/FileModule?file_id="+file_id);
        super(new Text(fileName),getFileURL(file_id));
}

public FilePresentation(PresentationObject mo,int file_id){
	super();
	setPresentationObject(mo);
	//setURL("/servlet/FileModule?file_id="+file_id);
	setFileID(file_id);

}

public FilePresentation(int file_id, Window myWindow){
	//super();
            setFileID(file_id);
            this.setWindow(myWindow);
	//this.myWindow = myWindow;

}



}