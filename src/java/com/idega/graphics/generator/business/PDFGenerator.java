package com.idega.graphics.generator.business;

import javax.faces.component.UIComponent;

import org.w3c.dom.Document;

import com.idega.business.SpringBeanName;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

@SpringBeanName(CoreConstants.SPRING_BEAN_NAME_PDF_GENERATOR)
public interface PDFGenerator {
	
	public boolean generatePDF(IWContext iwc, UIComponent component, String fileName, String uploadPath, boolean cleanHtml);
	
	public boolean generatePDF(IWContext iwc, Document doc, String fileName, String uploadPath);
	
	public boolean generatePDFFromComponent(String componentId, String fileName, String uploadPath, boolean cleanHtml);
	
	public boolean generatePDFFromPage(String pageUri, String fileName, String uploadPath, boolean cleanHtml);

}
