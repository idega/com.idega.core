package com.idega.business.text;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.web2.business.JQueryPlugin;
import com.idega.block.web2.business.Web2Business;
import com.idega.builder.business.BuilderLogicWrapper;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Scope("singleton")
@Service(InlineEditableComponent.SPRING_BEAN_IDENTIFIER)
public class InlineEditableComponentImpl implements InlineEditableComponent {

	private static final long serialVersionUID = -749409164128200690L;
	private static final Logger LOGGER = Logger.getLogger(InlineEditableComponentImpl.class.getName());
	
	@Autowired
	private BuilderLogicWrapper builderLogic;
	
	public void makeInlineEditable(IWContext iwc, PresentationObject component) {
		if (!iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			return;
		}
		
		String instanceId = null;
		try {
			instanceId = getBuilderLogic().getBuilderService(iwc).getInstanceId(component);
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting unique ID for component: " + component, e);
		}
		if (StringUtil.isEmpty(instanceId) || !instanceId.startsWith(ICObjectBusiness.UUID_PREFIX)) {
			return;
		}
		
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.SPRING_BEAN_IDENTIFIER);
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
				web2.getBundleURIToJQueryLib(),
				web2.getBundleURIToJQueryPlugin(JQueryPlugin.EDITABLE),
				iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("javascript/AdminCore.js")
		));
		
		component.setStyleClass(InlineEditableComponent.class.getSimpleName());
	}

	public BuilderLogicWrapper getBuilderLogic() {
		return builderLogic;
	}

	public void setBuilderLogic(BuilderLogicWrapper builderLogic) {
		this.builderLogic = builderLogic;
	}

}
