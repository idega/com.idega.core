package com.idega.core.builder.data;

import java.util.List;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.ImplementorPlaceholder;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 21, 2004
 */
public interface ICDynamicPageTrigger extends Cloneable, ImplementorPlaceholder {
	
	void setRootPage(String rootPageId);
	
	int getRootPage();
	
	Boolean hasRelationTo(PresentationObject presentationObject, List[] permissionGroupLists, IWUserContext iwuc);
	
	Object clone();
	
	
}
