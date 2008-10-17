package com.idega.presentation.web2;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import com.idega.block.web2.business.Web2Business;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

public class DynamicTree extends Block {

	private boolean addStylesManager;
	private boolean usesXmlDataTypes;
	private boolean usesMetadataRules;
	private boolean usesCookies;
	
	private UIComponent rootNode;
	private List<UIComponent> treeNodes;
	
	@Override
	public void main(IWContext iwc) {
		Layer container = new Layer();
		add(container);
		
		Layer treeContainer = new Layer();
		container.add(treeContainer);
		String treeContainerId = treeContainer.getId();
		
		String treeVariable = new StringBuilder("jsTree").append(treeContainerId).toString();
		StringBuilder initAction = new StringBuilder("var ").append(treeVariable).append(" = new tree_component();\n");
		initAction.append(treeVariable).append(".init(jQuery('#").append(treeContainerId).append("'), ").append(getOptions()).append(");");
		if (!CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			initAction = new StringBuilder("jQuery(window).load(function() {\n").append(initAction.toString()).append("\n});");
		}
		PresentationUtil.addJavaScriptActionToBody(iwc, initAction.toString());
		
		addRequiredLibraries(iwc);
		
		renderTree(iwc, treeContainer);
	}
	
	private void renderTree(IWContext iwc, Layer container) {
		if (rootNode == null) {
			return;
		}
		
		Lists list = new Lists();
		container.add(list);
		
		ListItem root = new ListItem();
		list.add(root);
		root.add(rootNode);
		
		if (ListUtil.isEmpty(treeNodes)) {
			return;
		}
		
		Lists childNodes = new Lists();
		root.add(childNodes);
		ListItem childNode = null;
		for (UIComponent treeNode: treeNodes) {
			childNode = new ListItem();
			childNodes.add(childNode);
			
			childNode.add(treeNode);
		}
	}
	
	private String getOptions() {
		StringBuilder options = new StringBuilder("[{}]");
		
		//	TODO
		
		return options.toString();
	}
	
	public void addTreeNode(UIComponent component) {
		if (component == null) {
			return;
		}
		
		if (treeNodes == null) {
			treeNodes = new ArrayList<UIComponent>();
		}
		
		treeNodes.add(component);
	}
	
	private void addRequiredLibraries(IWContext iwc) {
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.SPRING_BEAN_IDENTIFIER);
		PresentationUtil.addStyleSheetToHeader(iwc, web2.getBundleURIToJSTreeStyleFile());
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, web2.getBundleURIsToJSTreeScriptFiles(isAddStylesManager(), isUsesXmlDataTypes(),
				isUsesMetadataRules(), isUsesCookies()));
	}

	public boolean isAddStylesManager() {
		return addStylesManager;
	}

	public void setAddStylesManager(boolean addStylesManager) {
		this.addStylesManager = addStylesManager;
	}

	public boolean isUsesXmlDataTypes() {
		return usesXmlDataTypes;
	}

	public void setUsesXmlDataTypes(boolean usesXmlDataTypes) {
		this.usesXmlDataTypes = usesXmlDataTypes;
	}

	public boolean isUsesMetadataRules() {
		return usesMetadataRules;
	}

	public void setUsesMetadataRules(boolean usesMetadataRules) {
		this.usesMetadataRules = usesMetadataRules;
	}

	public boolean isUsesCookies() {
		return usesCookies;
	}

	public void setUsesCookies(boolean usesCookies) {
		this.usesCookies = usesCookies;
	}

	public UIComponent getRootNode() {
		return rootNode;
	}

	public void setRootNode(UIComponent rootNode) {
		this.rootNode = rootNode;
	}

	public List<UIComponent> getTreeNodes() {
		return treeNodes;
	}

	public void setTreeNodes(List<UIComponent> treeNodes) {
		this.treeNodes = treeNodes;
	}
	
}
