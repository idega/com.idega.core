package com.idega.core.builder.business;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 29, 2004
 */
public interface ICDynamicPageTriggerInheritable {
	
	boolean copyICObjectInstance(String pageKey,int newInstanceID, ICDynamicPageTriggerCopySession copySession);
	
}
