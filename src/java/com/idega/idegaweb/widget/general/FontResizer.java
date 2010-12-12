package com.idega.idegaweb.widget.general;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Span;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

public class FontResizer extends Widget {

	@Autowired
	private JQuery jQuery;
	
	public FontResizer() {
		super();
		setStyleClass("fontResize");
	}
	
	@Override
	protected PresentationObject getWidget(IWContext iwc) {
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getJQuery().getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getBundle().getVirtualPathWithFileNameString("javascript/fontResizer.js"));

		Lists list = new Lists();
		list.setStyleClass("fontResizer");
		
		ListItem smaller = new ListItem();
		smaller.add(new Span(new Text("A-")));
		smaller.setStyleClass("smaller");
		list.add(smaller);
		
		ListItem normal = new ListItem();
		normal.add(new Span(new Text("A")));
		normal.setStyleClass("normal");
		list.add(normal);
		
		ListItem larger = new ListItem();
		larger.add(new Span(new Text("A+")));
		larger.setStyleClass("larger");
		list.add(larger);
		
		return list;
	}

	private JQuery getJQuery() {
		if (jQuery == null) {
			ELUtil.getInstance().autowire(this);
		}
		
		return jQuery;
	}
}