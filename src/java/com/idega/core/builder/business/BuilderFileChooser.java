package com.idega.core.builder.business;

import com.idega.presentation.PresentationObjectType;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 15, 2004
 */
public interface BuilderFileChooser extends PresentationObjectType {

	 void setChooserParameter(String parameterName);
	 
	 void setValue(Object file);
}
