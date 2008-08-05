package com.idega.presentation.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.data.ICTreeNode;
import com.idega.group.bean.GroupFilterResult;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;

public class FilteredGroupsBox extends Block {
	
	private Collection<Group> groups;
	
	private List<GroupFilterResult> filteredGroups;
	private List<String> selectedGroups;
	
	private String selectedGroupParameterName = "selectedGroup";
	
	private boolean searchResult;
	
	private IWResourceBundle iwrb;
	
	@Override
	public void main(IWContext iwc) {
		iwrb = getResourceBundle(iwc);
		
		Layer container = new Layer();
		add(container);
		if (searchResult) {
			container.setMarkupAttribute("searchresult", Boolean.TRUE.toString());
		}
		container.setStyleClass("filteredGroupsBoxStyle");
		if (ListUtil.isEmpty(groups) && ListUtil.isEmpty(filteredGroups)) {
			container.add(new Heading3(iwrb.getLocalizedString("no_groups_available", "There are no groups available")));
			return;
		}
		
		if (ListUtil.isEmpty(groups)) {
			groups = new ArrayList<Group>();
			convertSearchResultsToGroups(iwc);
		}
		
		Lists groupsList = new Lists();
		container.add(groupsList);
		addGroups(groupsList, groups, iwc.getCurrentLocale());
	}
	
	private void convertSearchResultsToGroups(IWApplicationContext iwac) {
		int level = 0;
		Group group = null;
		Group parentGroup = null;
		GroupBusiness groupBusiness = null;
		try {
			groupBusiness = (GroupBusiness) IBOLookup.getServiceInstance(iwac, GroupBusiness.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		for (GroupFilterResult result: filteredGroups) {
			level = result.getLevel();
			group = result.getGroup();
			
			if (level == 0) {
				if (!groups.contains(group)) {
					groups.add(group);
				}
			}
			else {
				parentGroup = getGroup(groupBusiness, group.getParentNode());
				while (parentGroup != null && level > 0) {
					level--;
					group = parentGroup;
					parentGroup = getGroup(groupBusiness, group.getParentNode());
				}
				if (!groups.contains(group)) {
					groups.add(group);
				}
			}
		}
	}
	
	private Group getGroup(GroupBusiness groupBusiness, ICTreeNode groupNode) {
		if (groupBusiness == null || groupNode == null) {
			return null;
		}
		
		try {
			return groupBusiness.getGroupByGroupID(Integer.valueOf(groupNode.getId()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void addGroups(Lists container, Collection<Group> groups, Locale locale) {
		container.setStyleClass("filteredGroupsListStyle");
		
		ListItem item = null;
		Text groupName = null;
		Lists childrenGroups = null;
		CheckBox groupSelection = null;
		Collection<Group> children = null;
		String checkBoxStyleClass = "checkBoxForFilteredGroupSelectionStyle";
		for (Group group: groups) {
			item = new ListItem();
			container.add(item);
			item.setStyleClass("filteredGroupNodeStyle");
			
			groupSelection = new CheckBox(selectedGroupParameterName, group.getId());
			groupSelection.setStyleClass(checkBoxStyleClass);
			groupSelection.setToolTip(iwrb.getLocalizedString("select_or_deselect_group", "Select/deselect group"));
			item.add(groupSelection);
			if (!ListUtil.isEmpty(selectedGroups)) {
				if (selectedGroups.contains(group.getId())) {
					groupSelection.setChecked(true, true);
				}
			}
			
			groupName = new Text(group.getNodeName(locale));
			groupName.setStyleClass(getStyleAttributeForGroupName(group));
			item.add(groupName);
			
			children = group.getChildren();
			if (!ListUtil.isEmpty(children)) {
				childrenGroups = new Lists();
				item.add(childrenGroups);
				addGroups(childrenGroups, children, locale);
			}
		}
	}
	
	private String getStyleAttributeForGroupName(Group group) {
		StringBuilder styleClass = new StringBuilder("basicFilteredGroupNameElementStyle");
		if (ListUtil.isEmpty(filteredGroups)) {
			return styleClass.toString();
		}
		
		boolean directSearchResult = false;
		for (int i = 0; (i < filteredGroups.size() && !directSearchResult); i++) {
			if (group.equals(filteredGroups.get(i).getGroup())) {
				directSearchResult = true;
			}
		}
		
		if (directSearchResult) {
			return styleClass.append(" directSearchResultElementStyle").toString();
		}
		
		return styleClass.append(" inDirectSearchResultElementStyle").toString();
	}
	
	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public String getSelectedGroupParameterName() {
		return selectedGroupParameterName;
	}

	public void setSelectedGroupParameterName(String selectedGroupParameterName) {
		this.selectedGroupParameterName = selectedGroupParameterName;
	}

	public boolean isSearchResult() {
		return searchResult;
	}

	public void setSearchResult(boolean searchResult) {
		this.searchResult = searchResult;
	}

	public List<String> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<String> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	public List<GroupFilterResult> getFilteredGroups() {
		return filteredGroups;
	}

	public void setFilteredGroups(List<GroupFilterResult> filteredGroups) {
		this.filteredGroups = filteredGroups;
	}

}
