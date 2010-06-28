package com.idega.event;

import org.springframework.context.ApplicationEvent;
import org.w3c.dom.Document;

public class PDFGeneratedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 123778318523874047L;
	
	private Document pdfSource;
	
	public PDFGeneratedEvent(Object source, Document pdfSource) {
		super(source);
		
		this.pdfSource = pdfSource;
	}

	public Document getPdfSource() {
		return pdfSource;
	}

}