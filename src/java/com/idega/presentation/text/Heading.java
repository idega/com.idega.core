package com.idega.presentation.text;

import com.idega.presentation.IWContext;

public class Heading extends Text {
	
	public Heading() {
		super();
	}
	
	public Heading(String text) {
		super(text);
	}
	
	@Override
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		
		getInlineEditable().makeInlineEditable(iwc, this);
	}
	
}
