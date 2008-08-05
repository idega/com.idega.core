if (GroupsFilter == null) var GroupsFilter = {};

GroupsFilter.constants = {
	SEARCH_RESULT_PARAM: 'searchresult'
}

GroupsFilter.filterGroupsByNewInfo = function(params, selectedGroups) {
	var id = params[0];
	var message = params[1];
	var containerId = params[2];
	var selectedGroupName = params[3];
	
	showLoadingMessage(message);
	GroupsFilterEngine.getFilteredGroups(jQuery('#' + id).attr('value'), selectedGroupName, selectedGroups, {
		callback: function(html) {
			closeAllLoadingMessages();
			
			if (html == null) {
				return null;
			}
			
			var groupsBoxes = jQuery('div.filteredGroupsBoxStyle', jQuery('#' + containerId));
			if (groupsBoxes != null) {
				for (var i = 0; i < groupsBoxes.length; i++) {
					jQuery(groupsBoxes[i]).hide();
				}
			}
			
			IWCORE.insertHtml(html, document.getElementById(containerId));
		}
	});
}

GroupsFilter.clearSearchResults = function(params) {
	var containerId = params[0];
	var inputId = params[1];
	
	jQuery('#' + inputId).attr('value', '');
	
	var groupsBoxes = jQuery('div.filteredGroupsBoxStyle', jQuery('#' + containerId));
	if (groupsBoxes == null) {
		return false;
	}
	
	var box = null;
	var originalBox = null;
	for (var i = 0; i < groupsBoxes.length; i++) {
		box = jQuery(groupsBoxes[i]);
		if (box.attr(GroupsFilter.constants.SEARCH_RESULT_PARAM) == 'true') {
			box.remove();
		}
		else {
			originalBox = box;
		}
	}
	if (originalBox != null) {
		originalBox.show('fast');
	}
}