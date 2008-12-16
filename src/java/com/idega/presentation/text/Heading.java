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
		
		try {
			getInlineEditable().makeInlineEditable(iwc, this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
