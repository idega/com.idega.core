package com.idega.presentation.groups;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJBException;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

public class GroupsFilter extends Block {
	
	private String selectedGroupParameterName = "selectedGroup";
	
	private List<String> selectedGroups;
	
	@Override
	public void main(IWContext iwc) {
		IWBundle bundle = getBundle(iwc);
		List<String> files = new ArrayList<String>(4);
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.class);
		files.add(web2.getBundleURIToJQueryLib());
		files.add(CoreConstants.DWR_ENGINE_SCRIPT);
		files.add("/dwr/interface/GroupsFilterEngine.js");
		files.add(bundle.getVirtualPathWithFileNameString("javascript/GroupsFilter.js"));
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, files);
		
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		
		Layer container = new Layer();
		add(container);
		container.setStyleClass("groupsChooserBoxStyle");
		
		Layer header = new Layer();
		header.setStyleClass("groupsChooserBoxHeaderStyle");
		container.add(header);
		
		Layer clearLayer = new Layer();
		clearLayer.setStyleClass("Clear");
		container.add(clearLayer);
		
		Layer body = new Layer();
		container.add(body);
		body.setStyleClass("groupsChooserBoxBodyStyle");
		
		TextInput filterInput = new TextInput();
		filterInput.setToolTip(iwrb.getLocalizedString("enter_group_name", "Enter group's name"));
		
		boolean selectedAnything = !ListUtil.isEmpty(selectedGroups);
		StringBuilder selectedGroupsExpression = selectedAnything ? new StringBuilder("[") : new StringBuilder("null");
		if (selectedAnything) {
			for (int i = 0; i < selectedGroups.size(); i++) {
				selectedGroupsExpression.append("'").append(selectedGroups.get(i)).append("'");
				
				if ((i + 1) < selectedGroups.size()) {
					selectedGroupsExpression.append(", ");
				}
			}
			selectedGroupsExpression.append("]");
		}
		
		String filterAction = new StringBuilder("GroupsFilter.filterGroupsByNewInfo(['").append(filterInput.getId()).append("', '")
												.append(iwrb.getLocalizedString("searching", "Searching...")).append("', '").append(body.getId())
												.append("', '").append(selectedGroupParameterName).append("'], ").append(selectedGroupsExpression.toString())
												.append(");").toString();
		filterInput.setOnKeyPress(new StringBuilder("if (isEnterEvent(event)) {").append(filterAction).append(" return false;}").toString());
		Label filterInputLabel = new Label(iwrb.getLocalizedString("groups_filter", "Groups filter") + ":", filterInput);
		header.add(filterInputLabel);
		header.add(filterInput);
		
		GenericButton searchButton = new GenericButton(iwrb.getLocalizedString("search", "Search"));
		searchButton.setStyleClass("applicationButton");
		searchButton.setToolTip(iwrb.getLocalizedString("search_for_groups", "Search for groups"));
		searchButton.setOnClick(filterAction);
		header.add(searchButton);
		GenericButton clearResults = new GenericButton(iwrb.getLocalizedString("clear", "Clear"));
		clearResults.setStyleClass("applicationButton");
		clearResults.setToolTip(iwrb.getLocalizedString("clear_search_results", "Clear search results"));
		header.add(clearResults);
		clearResults.setOnClick(new StringBuilder("GroupsFilter.clearSearchResults(['").append(body.getId()).append("', '").append(filterInput.getId())
													.append("']);").toString());
		
		body.add(getFilteredGroupsBox(iwc));
	}
	
	private FilteredGroupsBox getFilteredGroupsBox(IWContext iwc) {
		FilteredGroupsBox filteredGroups = new FilteredGroupsBox();
		filteredGroups.setGroups(getUserGroups(iwc));
		
		String[] selectedInForm = iwc.getParameterValues(selectedGroupParameterName);
		if (selectedInForm != null) {
			selectedGroups = Arrays.asList(selectedInForm);
		}
		
		filteredGroups.setSelectedGroups(selectedGroups);
		filteredGroups.setSelectedGroupParameterName(selectedGroupParameterName);
		return filteredGroups;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Group> getUserGroups(IWContext iwc) {
		UserBusiness userBusiness = null;
		try {
			userBusiness = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		try {
			return userBusiness.getUsersTopGroupNodesByViewAndOwnerPermissions(iwc.getCurrentUser(), iwc);
		} catch (EJBException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

	public List<String> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<String> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	public String getSelectedGroupParameterName() {
		return selectedGroupParameterName;
	}

	public void setSelectedGroupParameterName(String selectedGroupParameterName) {
		this.selectedGroupParameterName = selectedGroupParameterName;
	}

}
