package com.idega.servlet;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.DefaultErrorHandlingUriWindow;
import com.idega.presentation.IWContext;
/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.0
 * @deprecated Replaced with FacesServlet
 */
public class ObjectInstanciator extends DynamicTemplateServlet {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8974280767665490773L;

	//TEMPORARY IMPLEMENTATION - See DynamicTemplateServlet
	public void main(IWContext iwc) throws Exception {
		String className = IWMainApplication.decryptClassName(iwc.getParameter(IWMainApplication.classToInstanciateParameter));
		className = null;
		
		if (className == null) {
			add(new DefaultErrorHandlingUriWindow());
			log("no class found to instanciate");
			return;
		}
			
		/*try {
			PresentationObject obj = (PresentationObject) RefactorClassRegistry.forName(className).newInstance();
			if (obj instanceof Page) {
				this.setPage((Page) obj);
			}
			else {
				add(obj);
			}
		} catch (ClassNotFoundException e) {
			System.err.println("[ObjectInstanciator] ClassNotFound : "+className+", referer = "+iwc.getReferer());
			add(new DefaultErrorHandlingUriWindow());
		}*/
	}
}
//-------------
//- End of file
//-------------
