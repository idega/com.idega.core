package com.idega.core.file.util;

import java.net.URI;

public interface FileURIHandlerFactory {

	public abstract FileURIHandler getHandler(URI uri);

}