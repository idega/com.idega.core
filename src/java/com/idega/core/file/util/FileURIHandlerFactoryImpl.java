package com.idega.core.file.util;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/28 19:03:51 $ by $Author: civilis $
 *
 */
@Service
@Scope("singleton")
public class FileURIHandlerFactoryImpl implements FileURIHandlerFactory {
	
	@Autowired(required=false)
	private List<FileURIHandler> handlers;
	
	public FileURIHandler getHandler(URI uri) {
		
		if(handlers != null) {
			
			String scheme = uri.getScheme();
			
			for (FileURIHandler handler : handlers) {
				
				if(scheme.equals(handler.getSupportedScheme()))
					return handler;
			}
		}
		
		return null;
	}

}