package com.idega.business.text;

import java.util.Arrays;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.JQueryPlugin;
import com.idega.builder.business.BuilderLogicWrapper;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(InlineEditableComponent.SPRING_BEAN_IDENTIFIER)
public class InlineEditableComponentImpl extends DefaultSpringBean implements InlineEditableComponent {

	private static final long serialVersionUID = -749409164128200690L;

	@Autowired
	private BuilderLogicWrapper builderLogic;

	@Autowired
	private JQuery jQuery;

	@Override
	public void makeInlineEditable(IWContext iwc, PresentationObject component) {
		try {
			if (component == null || iwc == null || !iwc.getApplicationSettings().getBoolean("inline_editable_ui_enabled", true)  || !iwc.isLoggedOn() || !iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
				return;
			}

			String instanceId = null;
			try {
				instanceId = getBuilderLogic().getBuilderService(iwc).getInstanceId(component);
			} catch(Exception e) {
				getLogger().log(Level.SEVERE, "Error getting unique ID for component: " + component, e);
			}
			if (StringUtil.isEmpty(instanceId) || !instanceId.startsWith(ICObjectBusiness.UUID_PREFIX)) {
				return;
			}

			PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
					jQuery.getBundleURIToJQueryLib(),
					jQuery.getBundleURIToJQueryPlugin(JQueryPlugin.EDITABLE),
					iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("javascript/AdminCore.js")
			));

			component.setStyleClass(InlineEditableComponent.class.getSimpleName());
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error making UI component " + component + " inline editable", e);
		}
	}

	public BuilderLogicWrapper getBuilderLogic() {
		return builderLogic;
	}

	public void setBuilderLogic(BuilderLogicWrapper builderLogic) {
		this.builderLogic = builderLogic;
	}

}