package com.idega.graphics.generator.business;

import java.io.InputStream;

import javax.faces.component.UIComponent;

import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

/**
 * Spring bean that generates PDF from UIComponent. Bean id: {@link CoreConstants.SPRING_BEAN_NAME_PDF_GENERATOR}
 * @author valdas
 *
 */
public interface PDFGenerator {
	
	public boolean generatePDF(IWContext iwc, UIComponent component, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags);
	
	public boolean generatePDFFromComponent(String componentUUID, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags);
	
	public boolean generatePDFFromPage(String pageUri, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags);
	
	public InputStream getStreamToGeneratedPDF(IWContext iwc, UIComponent component, boolean replaceInputs, boolean checkCustomTags);

}
