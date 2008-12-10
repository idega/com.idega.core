package com.idega.presentation.text;

import java.util.Arrays;

import javax.faces.component.UIComponent;

import com.idega.block.web2.business.JQueryUIType;
import com.idega.block.web2.business.Web2Business;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class Heading extends Text implements InlineEditableComponent {

	public Heading() {
		super();
	}
	
	public Heading(String text) {
		super(text);
	}

	public void makeInlineEditable(IWContext iwc, UIComponent component) {
		if (!iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			return;
		}
		
		String instanceId = getXmlId();
		if (StringUtil.isEmpty(instanceId)) {
			return;
		}
		
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.SPRING_BEAN_IDENTIFIER);
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
				web2.getBundleURIToJQueryLib(),
				web2.getBundleURIToJQueryUILib(JQueryUIType.UI_EDITABLE),
				iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("javascript/AdminCore.js")
		));
		
		setStyleClass(InlineEditableComponent.class.getSimpleName());
	}
	
	@Override
	public void print(IWContext iwc) throws Exception {
		makeInlineEditable(iwc, this);
		
		super.print(iwc);
	}
	
}
