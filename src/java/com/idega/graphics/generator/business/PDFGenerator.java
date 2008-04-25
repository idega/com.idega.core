package com.idega.graphics.generator.business;

import javax.faces.component.UIComponent;

import com.idega.presentation.IWContext;

public interface PDFGenerator {
	
	public boolean generatePDF(IWContext iwc, UIComponent component, String fileName, String uploadPath, boolean replaceInputs);
	
//	public boolean generatePDF(IWContext iwc, Document doc, String fileName, String uploadPath);
	
	public boolean generatePDFFromComponent(String componentUUID, String fileName, String uploadPath, boolean replaceInputs);
	
	public boolean generatePDFFromPage(String pageUri, String fileName, String uploadPath, boolean replaceInputs);

}
