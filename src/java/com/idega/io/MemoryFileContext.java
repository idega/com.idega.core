package com.idega.io;



import com.idega.idegaweb.IWMainApplication;

import javax.servlet.http.HttpServletRequest;

import com.idega.presentation.IWContext;



/**

 * Title:        idegaclasses

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega

 * @author <a href="aron@idega.is">Aron Birkir</a>

 * @version 1.0

 */



public class MemoryFileContext {



	public final static String MEMORY_BUFFER_PARAMETERNAME = "mem_buffer_name";



	public static MemoryFileBuffer getFile(){

		return new MemoryFileBuffer();

	}



	public static MemoryFileBuffer getDataBaseFile(int iFileId,IWMainApplication iwma){

		return new MemoryFileBuffer();

	}



	public static MemoryFileBuffer getMemoryFile(HttpServletRequest request){

	 return new MemoryFileBuffer();





	}



	public static void saveMemoryFileBuffer(IWContext iwc){



	}

}



