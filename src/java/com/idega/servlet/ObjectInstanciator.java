package com.idega.servlet;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.RefactorClassRegistry;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class ObjectInstanciator extends DynamicTemplateServlet {
	//TEMPORARY IMPLEMENTATION - See DynamicTemplateServlet
	public void main(IWContext iwc) throws Exception {
		String className =
			IWMainApplication.decryptClassName(iwc.getParameter(IWMainApplication.classToInstanciateParameter));
		PresentationObject obj = (PresentationObject) RefactorClassRegistry.forName(className).newInstance();
		if (obj instanceof Page) {
			this.setPage((Page) obj);
		}
		else {
			add(obj);
		}
	}
}
//-------------
//- End of file
//-------------
